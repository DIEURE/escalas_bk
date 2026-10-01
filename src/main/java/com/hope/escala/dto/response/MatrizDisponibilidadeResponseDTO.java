package com.hope.escala.dto.response;
 
 
import java.time.LocalDate;
import java.util.List;

public record MatrizDisponibilidadeResponseDTO(
    int mes,
    int ano,
    List<LocalDate> domingos,
    List<MusicoMatrizDTO> musicos
) {
    public record MusicoMatrizDTO(
        Long usuarioId,
        String nome,
        String instrumentoPrincipal,
        List<StatusDataDTO> disponibilidades
    ) {}

    public record StatusDataDTO(
        LocalDate data,
        String status // "DISPONIVEL", "INDISPONIVEL", "PENDENTE"
    ) {}
}
