package com.hope.escala.dto.response;
 
import java.time.LocalDate;
import java.util.List;

public record EscalaMesDTO(
    Long id,
    LocalDate data,
    String observacao,
    String nomeDepartamento,
     Long departamentoId,
    List<VoluntarioEscalaDTO> voluntarios
) {
    public record VoluntarioEscalaDTO(
        Long usuarioId, 
        Long departamentoId,
        String nome,
        String instrumento,
        String nomeDepartamento
    ) {}
}
