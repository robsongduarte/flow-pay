package com.flowpay.central.dto.atendimento;

import com.flowpay.central.enums.AssuntoAtendimento;
import com.flowpay.central.enums.StatusAtendimento;
import com.flowpay.central.enums.TimeAtendimento;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AtendimentoResponse(
        Long id,
        String nomeCliente,
        String documentoCliente,
        AssuntoAtendimento assunto,
        TimeAtendimento timeAtendimento,
        StatusAtendimento status,
        Long atendenteId,
        String atendenteNome,
        LocalDateTime criadoEm,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm
) {
}
