package com.hope.escala.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CriarAtaRequestDTO(
        String titulo,
        LocalDate dataReuniao,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        String localReuniao,
        String conteudoAta,
        Long departamentoId,
        List<CriarPautaItemDTO> pautas
) {}
