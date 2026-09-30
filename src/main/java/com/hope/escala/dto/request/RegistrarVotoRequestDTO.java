package com.hope.escala.dto.request;

import com.hope.escala.enums.TipoVoto;

public record RegistrarVotoRequestDTO(
        TipoVoto opcaoVoto,
        String justificativa
) {}
