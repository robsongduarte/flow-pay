package com.flowpay.central.dto.atendente;

import com.flowpay.central.enums.TimeAtendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtendenteCreateRequest(
        @NotBlank(message = "Nome do atendente e obrigatorio.")
        @Size(max = 120, message = "Nome do atendente deve ter no maximo 120 caracteres.")
        String nome,
        @NotNull(message = "Time de atendimento e obrigatorio.")
        TimeAtendimento timeAtendimento,
        Boolean ativo
) {
}
