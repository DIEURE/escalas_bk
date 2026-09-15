package com.hope.escala.dto;
 

public class EscalaMusicoDetalhesDTO {

    private Long id;
    private Long musicoId;
    private String nome;
    private String funcao;
    private String statusConfirmacao;
    private Boolean foiSubstituido;
    private MusicoResumoDTO substituidoPor;

    public EscalaMusicoDetalhesDTO() {
    }

    public EscalaMusicoDetalhesDTO(
            Long id,
            Long musicoId,
            String nome,
            String funcao,
            String statusConfirmacao,
            Boolean foiSubstituido,
            MusicoResumoDTO substituidoPor
    ) {
        this.id = id;
        this.musicoId = musicoId;
        this.nome = nome;
        this.funcao = funcao;
        this.statusConfirmacao = statusConfirmacao;
        this.foiSubstituido = foiSubstituido;
        this.substituidoPor = substituidoPor;
    }

    public Long getId() {
        return id;
    }

    public Long getMusicoId() {
        return musicoId;
    }

    public String getNome() {
        return nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public String getStatusConfirmacao() {
        return statusConfirmacao;
    }

    public Boolean getFoiSubstituido() {
        return foiSubstituido;
    }

    public MusicoResumoDTO getSubstituidoPor() {
        return substituidoPor;
    }
}