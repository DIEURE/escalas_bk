package com.hope.escala.dto.response;

import com.hope.escala.enums.StatusVotacaoPauta;
import com.hope.escala.enums.TipoVoto;

public record PautaDetalheResponseDTO(
        Long id,
        Integer ordem,
        String titulo,
        String descricao,
        Boolean requerVotacao,
        StatusVotacaoPauta statusVotacao,
        long totalVotosFavor,
        long totalVotosContra,
        long totalAbstencoes,
        TipoVoto meuVoto // Voto registrado pelo usuário logado (se houver)
) {}
