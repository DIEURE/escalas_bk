package com.hope.escala.dto.request;

import java.util.List;

public record CriarPautaItemDTO(
        Integer ordem,
        String titulo,
        String descricao,
        Boolean requerVotacao,
        List<String> opcoes
) {}