package com.hope.escala.dto.request;

 

import jakarta.validation.constraints.NotNull;

public class EscalaMusicaRequestDTO {

    @NotNull(message = "Escala é obrigatória")
    private Long escalaId;

    @NotNull(message = "Música é obrigatória")
    private Long musicaId;

    @NotNull(message = "Ordem é obrigatória")
    private Integer ordem;

    private String observacao;

    public EscalaMusicaRequestDTO() {
    }

    public Long getEscalaId() {
        return escalaId;
    }

    public void setEscalaId(Long escalaId) {
        this.escalaId = escalaId;
    }

    public Long getMusicaId() {
        return musicaId;
    }

    public void setMusicaId(Long musicaId) {
        this.musicaId = musicaId;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}