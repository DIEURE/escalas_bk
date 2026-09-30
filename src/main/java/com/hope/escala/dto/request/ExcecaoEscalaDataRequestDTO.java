package com.hope.escala.dto.request;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

public record ExcecaoEscalaDataRequestDTO(
        @NotNull(message = "A data da exceção é obrigatória")
        LocalDate dataExcecao,

        @NotNull(message = "O departamento é obrigatório")
        Long departamentoId,

        @NotNull(message = "O instrumento é obrigatório")
        Long instrumentoId,

        Integer limiteVagas,
        Boolean bloqueado,
        String observacao
) {}
