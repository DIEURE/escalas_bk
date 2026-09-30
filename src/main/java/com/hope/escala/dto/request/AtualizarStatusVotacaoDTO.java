package com.hope.escala.dto.request;

import com.hope.escala.enums.StatusVotacaoPauta;

public record AtualizarStatusVotacaoDTO(
        StatusVotacaoPauta status
) {}
