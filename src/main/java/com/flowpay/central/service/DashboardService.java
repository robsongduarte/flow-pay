package com.flowpay.central.service;

import com.flowpay.central.dto.dashboard.DashboardResponse;
import com.flowpay.central.dto.dashboard.DashboardTimeResponse;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.enums.TimeAtendimento;
import com.flowpay.central.repository.AtendimentoRepository;
import com.flowpay.central.repository.projection.AtendenteCargaProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final long LIMITE_ATENDIMENTOS_POR_ATENDENTE = 3L;

    private final AtendimentoRepository atendimentoRepository;

    public DashboardResponse getDashboard() {
        long totalAguardando = atendimentoRepository.countByStatus(StatusAtendimento.AGUARDANDO);
        long totalEmAtendimento = atendimentoRepository.countByStatus(StatusAtendimento.EM_ATENDIMENTO);
        long totalFinalizados = atendimentoRepository.countByStatus(StatusAtendimento.FINALIZADO);

        List<DashboardTimeResponse> porTime = List.of(
                buildPorTime(TimeAtendimento.CARTOES),
                buildPorTime(TimeAtendimento.EMPRESTIMOS),
                buildPorTime(TimeAtendimento.OUTROS_ASSUNTOS)
        );

        return DashboardResponse.builder()
                .totalAguardando(totalAguardando)
                .totalEmAtendimento(totalEmAtendimento)
                .totalFinalizados(totalFinalizados)
                .porTime(porTime)
                .build();
    }

    private DashboardTimeResponse buildPorTime(TimeAtendimento time) {
        long aguardando = atendimentoRepository.countByStatusAndTimeAtendimento(StatusAtendimento.AGUARDANDO, time);
        long emAtendimento = atendimentoRepository.countByStatusAndTimeAtendimento(StatusAtendimento.EM_ATENDIMENTO, time);
        long finalizados = atendimentoRepository.countByStatusAndTimeAtendimento(StatusAtendimento.FINALIZADO, time);

        List<AtendenteCargaProjection> cargas = atendimentoRepository.findCargaAtivaPorTime(time);
        long capacidadeUtilizada = 0L;
        long atendentesDisponiveis = 0L;
        long atendentesOcupados = 0L;

        for (AtendenteCargaProjection carga : cargas) {
            long cargaAtual = carga.getTotalAtivos();
            capacidadeUtilizada += cargaAtual;
            if (cargaAtual >= LIMITE_ATENDIMENTOS_POR_ATENDENTE) {
                atendentesOcupados++;
            } else {
                atendentesDisponiveis++;
            }
        }

        long capacidadeTotal = cargas.size() * LIMITE_ATENDIMENTOS_POR_ATENDENTE;

        return DashboardTimeResponse.builder()
                .time(time)
                .aguardando(aguardando)
                .emAtendimento(emAtendimento)
                .finalizados(finalizados)
                .atendentesDisponiveis(atendentesDisponiveis)
                .atendentesOcupados(atendentesOcupados)
                .capacidadeTotal(capacidadeTotal)
                .capacidadeUtilizada(capacidadeUtilizada)
                .build();
    }
}
