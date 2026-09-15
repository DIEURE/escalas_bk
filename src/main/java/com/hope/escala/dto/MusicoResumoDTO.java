package com.hope.escala.dto;
 
public class MusicoResumoDTO {

    private Long id;
    private String nome;

    public MusicoResumoDTO() {
    }

    public MusicoResumoDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}