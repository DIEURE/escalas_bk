package com.hope.escala.dto.response;
 
import java.time.LocalDate;
import java.util.List;

public record EscalaMesDTO(
    Long id,
    LocalDate data,
    String observacao,
    List<VoluntarioEscalaDTO> voluntarios
) {
    public record VoluntarioEscalaDTO(
        Long usuarioId,
        String nome,
        String instrumento // Função ou Instrumento (ex: Bateria, Ministro, Vocal)
    ) {}
}
