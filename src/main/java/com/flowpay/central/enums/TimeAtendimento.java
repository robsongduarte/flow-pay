package com.flowpay.central.enums;

import com.flowpay.central.exception.BusinessException;

public enum TimeAtendimento {
    CARTOES,
    EMPRESTIMOS,
    OUTROS_ASSUNTOS;

    public static TimeAtendimento fromAssunto(AssuntoAtendimento assunto) {
        if (assunto == null) {
            throw new BusinessException("Assunto do atendimento deve ser informado.");
        }
        return switch (assunto) {
            case PROBLEMA_CARTAO -> CARTOES;
            case CONTRATACAO_EMPRESTIMO -> EMPRESTIMOS;
            case OUTROS -> OUTROS_ASSUNTOS;
        };
    }
}
