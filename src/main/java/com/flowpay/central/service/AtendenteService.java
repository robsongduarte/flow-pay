package com.flowpay.central.service;

import com.flowpay.central.dto.atendente.AtendenteCreateRequest;
import com.flowpay.central.dto.atendente.AtendenteResponse;
import com.flowpay.central.entity.Atendente;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.exception.ResourceNotFoundException;
import com.flowpay.central.repository.AtendenteRepository;
import com.flowpay.central.repository.AtendimentoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AtendenteService {

    private final AtendenteRepository atendenteRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final DashboardStreamService dashboardStreamService;

    @Transactional
    public AtendenteResponse criar(AtendenteCreateRequest request) {
        Atendente atendente = Atendente.builder()
                .nome(request.nome().trim())
                .timeAtendimento(request.timeAtendimento())
                .ativo(request.ativo() == null || request.ativo())
                .build();

        Atendente salvo = atendenteRepository.save(atendente);
        dashboardStreamService.publishDashboardUpdate();
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<AtendenteResponse> listar() {
        return atendenteRepository.findAllByOrderByCriadoEmAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AtendenteResponse ativar(Long id) {
        return atualizarStatusAtivo(id, true);
    }

    @Transactional
    public AtendenteResponse desativar(Long id) {
        return atualizarStatusAtivo(id, false);
    }

    private AtendenteResponse atualizarStatusAtivo(Long id, boolean ativo) {
        Atendente atendente = atendenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atendente nao encontrado: id=" + id));
        atendente.setAtivo(ativo);
        Atendente salvo = atendenteRepository.save(atendente);
        dashboardStreamService.publishDashboardUpdate();
        return toResponse(salvo);
    }

    private AtendenteResponse toResponse(Atendente atendente) {
        long atendimentosAtivos = atendimentoRepository.countByAtendenteIdAndStatus(
                atendente.getId(),
                StatusAtendimento.EM_ATENDIMENTO
        );
        return AtendenteResponse.builder()
                .id(atendente.getId())
                .nome(atendente.getNome())
                .timeAtendimento(atendente.getTimeAtendimento())
                .ativo(Boolean.TRUE.equals(atendente.getAtivo()))
                .criadoEm(atendente.getCriadoEm())
                .atendimentosAtivos(atendimentosAtivos)
                .build();
    }
}
