package com.hope.escala.dto.response;

import java.util.List;
import com.hope.escala.enums.StatusVotacaoPauta;

public record PautaDetalheResponseDTO(
        Long id,
        Integer ordem,
        String titulo,
        String descricao,
        Boolean requerVotacao,
        StatusVotacaoPauta statusVotacao,
        long totalVotosGeral,
        Long minhaOpcaoEscolhidaId,
        List<PautaOpcaoResponseDTO> opcoes
) {}