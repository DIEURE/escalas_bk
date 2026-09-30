package com.hope.escala.dto.response;

import java.time.LocalDate;

public record DataCultoResponseDTO(
        LocalDate data,
        String nome,
        String horario
) {}
