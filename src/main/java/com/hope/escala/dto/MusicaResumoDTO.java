package com.hope.escala.dto;
 

public class MusicaResumoDTO {

    private Long id;
    private String titulo;
    private String tom;

    public MusicaResumoDTO() {
    }

    public MusicaResumoDTO(Long id, String titulo, String tom) {
        this.id = id;
        this.titulo = titulo;
        this.tom = tom;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTom() {
        return tom;
    }
}
