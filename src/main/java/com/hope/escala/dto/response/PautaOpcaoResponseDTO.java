package com.hope.escala.dto.response;

public record PautaOpcaoResponseDTO(
        Long id,
        String texto,
        Integer ordem,
        long totalVotos,
        double porcentagem
) {}