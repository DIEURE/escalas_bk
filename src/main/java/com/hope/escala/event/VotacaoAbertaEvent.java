package com.hope.escala.event;

public record VotacaoAbertaEvent(
        Long pautaId,
        String tituloPauta,
        Long ataId,
        Long empresaId
) {
}