package com.hope.escala.dto.request;

public record CriarPautaItemDTO(
        Integer ordem,
        String titulo,
        String descricao,
        Boolean requerVotacao
) {}
