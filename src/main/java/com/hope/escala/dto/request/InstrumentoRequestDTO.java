package com.hope.escala.dto.request;

public record InstrumentoRequestDTO(
        String nome,
        String tipo,
        String descricao,
        Integer quantidade_escala,
        Boolean ativo,
        Long empresaId // opcional, para o Super Admin vincular
) {
}
