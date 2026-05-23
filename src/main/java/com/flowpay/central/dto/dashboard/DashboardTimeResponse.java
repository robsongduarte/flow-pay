package com.flowpay.central.dto.dashboard;

import com.flowpay.central.enums.TimeAtendimento;
import lombok.Builder;

@Builder
public record DashboardTimeResponse(
        TimeAtendimento time,
        long aguardando,
        long emAtendimento,
        long finalizados,
        long atendentesDisponiveis,
        long atendentesOcupados,
        long capacidadeTotal,
        long capacidadeUtilizada
) {
}
