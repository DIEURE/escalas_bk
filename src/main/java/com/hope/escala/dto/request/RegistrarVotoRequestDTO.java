package com.hope.escala.dto.request;

public record RegistrarVotoRequestDTO(
        Long opcaoId,
        String justificativa
) {}