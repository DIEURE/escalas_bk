package com.hope.escala.dto;

 

import java.time.LocalDate;
import java.time.LocalTime;

public class EscalaResumoDTO {

    private Long id;
    private String titulo;
    private LocalDate data;
    private LocalTime horario;
    private String tipo;
    private String status;
    private String observacoes;

    public EscalaResumoDTO() {
    }

    public EscalaResumoDTO(Long id, String titulo, LocalDate data, LocalTime horario, String tipo, String status, String observacoes) {
        this.id = id;
        this.titulo = titulo;
        this.data = data;
        this.horario = horario;
        this.tipo = tipo;
        this.status = status;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }
}