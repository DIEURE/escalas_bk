package com.hope.escala.dto.response;

public record InstrumentoResponse(

		Long id, String nome, String tipo, String descricao, Integer quantidade_escala, Boolean ativo) {
}