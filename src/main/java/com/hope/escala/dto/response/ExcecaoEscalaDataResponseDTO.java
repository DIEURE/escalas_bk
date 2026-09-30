package com.hope.escala.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExcecaoEscalaDataResponseDTO(
        Long id,
        LocalDate dataExcecao,
        Long departamentoId,
        String departamentoNome,
        Long instrumentoId,
        String instrumentoNome,
        Integer limiteVagas,
        Boolean bloqueado,
        String observacao,
        LocalDateTime dataCriacao,
        Long empresaId,
        String empresaNome
) {}
