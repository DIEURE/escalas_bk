package com.hope.escala.dto;

 

import jakarta.validation.constraints.NotNull;

public class SubstituicaoEscalaMusicaDTO {

    @NotNull(message = "Música substituta é obrigatória")
    private Long musicaSubstitutaId;

    private String motivoSubstituicao;

    public SubstituicaoEscalaMusicaDTO() {
    }

    public Long getMusicaSubstitutaId() {
        return musicaSubstitutaId;
    }

    public void setMusicaSubstitutaId(Long musicaSubstitutaId) {
        this.musicaSubstitutaId = musicaSubstitutaId;
    }

    public String getMotivoSubstituicao() {
        return motivoSubstituicao;
    }

    public void setMotivoSubstituicao(String motivoSubstituicao) {
        this.motivoSubstituicao = motivoSubstituicao;
    }
}