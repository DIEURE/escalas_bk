package com.hope.escala.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.hope.escala.enums.StatusAta;

public record AtaDetalheResponseDTO(
        Long id,
        String titulo,
        LocalDate dataReuniao,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        String localReuniao,
        String conteudoAta,
        StatusAta status,
        Long departamentoId,
        String nomeDepartamento,
        String nomeCriador,
        List<PautaDetalheResponseDTO> pautas
) {}
