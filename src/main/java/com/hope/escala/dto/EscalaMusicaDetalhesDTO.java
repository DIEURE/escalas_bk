package com.hope.escala.dto;

 

public class EscalaMusicaDetalhesDTO {

    private Long id;
    private Long musicaId;
    private String titulo;
    private String tom;
    private Integer ordem;
    private Boolean foiSubstituida;
    private MusicaResumoDTO substituidaPor;

    public EscalaMusicaDetalhesDTO() {
    }

    public EscalaMusicaDetalhesDTO(
            Long id,
            Long musicaId,
            String titulo,
            String tom,
            Integer ordem,
            Boolean foiSubstituida,
            MusicaResumoDTO substituidaPor
    ) {
        this.id = id;
        this.musicaId = musicaId;
        this.titulo = titulo;
        this.tom = tom;
        this.ordem = ordem;
        this.foiSubstituida = foiSubstituida;
        this.substituidaPor = substituidaPor;
    }

    public Long getId() {
        return id;
    }

    public Long getMusicaId() {
        return musicaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTom() {
        return tom;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public Boolean getFoiSubstituida() {
        return foiSubstituida;
    }

    public MusicaResumoDTO getSubstituidaPor() {
        return substituidaPor;
    }
}