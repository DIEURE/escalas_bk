package com.hope.escala.dto.response;

import com.hope.escala.entity.Instrumento;

public record InstrumentoResponse(
        Long id,
        String nome,
        String tipo,
        String descricao,
        Integer quantidade_escala,
        Boolean ativo,
        Long empresaId,
        String empresaNome
) {
    // Construtor auxiliar compacto a partir da Entidade
    public InstrumentoResponse(Instrumento inst) {
        this(
            inst.getId(),
            inst.getNome(),
            inst.getTipo(),
            inst.getDescricao(),
            inst.getQuantidadeEscala(), 
            inst.getAtivo(),
            inst.getEmpresa() != null ? inst.getEmpresa().getId() : null,
            inst.getEmpresa() != null ? inst.getEmpresa().getNome() : null
        );
    }
}
