package com.flowpay.central.service;

import com.flowpay.central.dto.atendimento.AtendimentoCreateRequest;
import com.flowpay.central.dto.atendimento.AtendimentoResponse;
import com.flowpay.central.entity.Atendente;
import com.flowpay.central.entity.Atendimento;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.enums.TimeAtendimento;
import com.flowpay.central.exception.BusinessException;
import com.flowpay.central.exception.ResourceNotFoundException;
import com.flowpay.central.repository.AtendenteRepository;
import com.flowpay.central.repository.AtendimentoRepository;
import com.flowpay.central.repository.projection.AtendenteCargaProjection;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private static final long LIMITE_ATENDIMENTOS_POR_ATENDENTE = 3L;

    private final AtendimentoRepository atendimentoRepository;
    private final AtendenteRepository atendenteRepository;
    private final DashboardStreamService dashboardStreamService;

    @Transactional
    public AtendimentoResponse criar(AtendimentoCreateRequest request) {
        TimeAtendimento timeAtendimento = TimeAtendimento.fromAssunto(request.assunto());
        Atendente atendenteSelecionado = escolherAtendenteDisponivel(timeAtendimento);

        Atendimento atendimento = Atendimento.builder()
                .nomeCliente(request.nomeCliente().trim())
                .documentoCliente(request.documentoCliente().trim())
                .assunto(request.assunto())
                .timeAtendimento(timeAtendimento)
                .build();

        if (atendenteSelecionado != null) {
            atendimento.setAtendente(atendenteSelecionado);
            atendimento.setStatus(StatusAtendimento.EM_ATENDIMENTO);
            atendimento.setIniciadoEm(LocalDateTime.now());
        } else {
            atendimento.setStatus(StatusAtendimento.AGUARDANDO);
        }

        Atendimento salvo = atendimentoRepository.save(atendimento);
        dashboardStreamService.publishDashboardUpdate();
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoResponse> listar(StatusAtendimento status, TimeAtendimento timeAtendimento) {
        return buscarAtendimentos(status, timeAtendimento)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AtendimentoResponse finalizar(Long id) {
        Atendimento atendimento = atendimentoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendimento nao encontrado: id=" + id));

        if (atendimento.getStatus() == StatusAtendimento.FINALIZADO) {
            throw new BusinessException("Atendimento ja foi finalizado.");
        }
        if (atendimento.getStatus() == StatusAtendimento.AGUARDANDO) {
            throw new BusinessException("Nao e possivel finalizar um atendimento que ainda esta aguardando.");
        }

        atendimento.setStatus(StatusAtendimento.FINALIZADO);
        atendimento.setFinalizadoEm(LocalDateTime.now());
        atendimentoRepository.save(atendimento);

        Atendente atendenteLivre = atendimento.getAtendente();
        if (atendenteLivre != null) {
            Atendente atendenteBloqueado = atendenteRepository.findByIdForUpdate(atendenteLivre.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Atendente associado nao encontrado: id=" + atendenteLivre.getId()));
            atribuirProximoDaFilaSePossivel(atendenteBloqueado, atendimento.getTimeAtendimento());
        }

        dashboardStreamService.publishDashboardUpdate();
        return toResponse(atendimento);
    }

    private Atendente escolherAtendenteDisponivel(TimeAtendimento timeAtendimento) {
        List<Atendente> atendentesAtivos = atendenteRepository.findAtivosByTimeForUpdate(timeAtendimento);
        if (atendentesAtivos.isEmpty()) {
            return null;
        }

        List<Long> ids = atendentesAtivos.stream()
                .map(Atendente::getId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Long> cargaPorAtendente = atendimentoRepository
                .countAtivosByAtendenteIdsAndStatus(ids, StatusAtendimento.EM_ATENDIMENTO)
                .stream()
                .collect(Collectors.toMap(AtendenteCargaProjection::getAtendenteId, AtendenteCargaProjection::getTotalAtivos));

        return atendentesAtivos.stream()
                .filter(atendente -> cargaPorAtendente.getOrDefault(atendente.getId(), 0L) < LIMITE_ATENDIMENTOS_POR_ATENDENTE)
                .min(Comparator
                        .comparingLong((Atendente a) -> cargaPorAtendente.getOrDefault(a.getId(), 0L))
                        .thenComparingLong(Atendente::getId))
                .orElse(null);
    }

    private void atribuirProximoDaFilaSePossivel(Atendente atendente, TimeAtendimento timeAtendimento) {
        if (!Boolean.TRUE.equals(atendente.getAtivo())) {
            return;
        }

        long atendimentosAtivos = atendimentoRepository.countByAtendenteIdAndStatus(
                atendente.getId(),
                StatusAtendimento.EM_ATENDIMENTO
        );
        if (atendimentosAtivos >= LIMITE_ATENDIMENTOS_POR_ATENDENTE) {
            return;
        }

        List<Atendimento> fila = atendimentoRepository
                .findByTimeAtendimentoAndStatusAndAtendenteIsNullOrderByCriadoEmAscIdAsc(
                        timeAtendimento,
                        StatusAtendimento.AGUARDANDO,
                        PageRequest.of(0, 1)
                );

        if (fila.isEmpty()) {
            return;
        }

        Atendimento proximo = fila.get(0);
        proximo.setAtendente(atendente);
        proximo.setStatus(StatusAtendimento.EM_ATENDIMENTO);
        proximo.setIniciadoEm(LocalDateTime.now());
        atendimentoRepository.save(proximo);
    }

    private AtendimentoResponse toResponse(Atendimento atendimento) {
        Atendente atendente = atendimento.getAtendente();
        return AtendimentoResponse.builder()
                .id(atendimento.getId())
                .nomeCliente(atendimento.getNomeCliente())
                .documentoCliente(atendimento.getDocumentoCliente())
                .assunto(atendimento.getAssunto())
                .timeAtendimento(atendimento.getTimeAtendimento())
                .status(atendimento.getStatus())
                .atendenteId(atendente != null ? atendente.getId() : null)
                .atendenteNome(atendente != null ? atendente.getNome() : null)
                .criadoEm(atendimento.getCriadoEm())
                .iniciadoEm(atendimento.getIniciadoEm())
                .finalizadoEm(atendimento.getFinalizadoEm())
                .build();
    }

    private List<Atendimento> buscarAtendimentos(StatusAtendimento status, TimeAtendimento timeAtendimento) {
        if (status != null && timeAtendimento != null) {
            return atendimentoRepository.findByStatusAndTimeAtendimentoOrderByCriadoEmDesc(status, timeAtendimento);
        }
        if (status != null) {
            return atendimentoRepository.findByStatusOrderByCriadoEmDesc(status);
        }
        if (timeAtendimento != null) {
            return atendimentoRepository.findByTimeAtendimentoOrderByCriadoEmDesc(timeAtendimento);
        }
        return atendimentoRepository.findAllByOrderByCriadoEmDesc();
    }
}
