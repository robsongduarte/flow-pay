package com.flowpay.central.dto.atendimento;

import com.flowpay.central.enums.AssuntoAtendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtendimentoCreateRequest(
        @NotBlank(message = "Nome do cliente e obrigatorio.")
        @Size(max = 120, message = "Nome do cliente deve ter no maximo 120 caracteres.")
        String nomeCliente,
        @NotBlank(message = "Documento do cliente e obrigatorio.")
        @Pattern(regexp = "^[0-9A-Za-z.-]{5,30}$", message = "Documento do cliente invalido.")
        String documentoCliente,
        @NotNull(message = "Assunto do atendimento e obrigatorio.")
        AssuntoAtendimento assunto
) {
}
