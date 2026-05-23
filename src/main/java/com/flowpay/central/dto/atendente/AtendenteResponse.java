package com.flowpay.central.dto.atendente;

import com.flowpay.central.enums.TimeAtendimento;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AtendenteResponse(
        Long id,
        String nome,
        TimeAtendimento timeAtendimento,
        boolean ativo,
        LocalDateTime criadoEm,
        long atendimentosAtivos
) {
}
