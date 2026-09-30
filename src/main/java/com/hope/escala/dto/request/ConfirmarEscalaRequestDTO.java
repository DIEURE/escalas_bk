package com.hope.escala.dto.request;

public record ConfirmarEscalaRequestDTO(
        Boolean confirmado,
        String justificativa
) {}