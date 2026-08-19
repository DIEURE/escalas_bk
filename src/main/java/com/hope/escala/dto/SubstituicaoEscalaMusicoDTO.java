package com.hope.escala.dto;

 
import jakarta.validation.constraints.NotNull;

public class SubstituicaoEscalaMusicoDTO {

    @NotNull(message = "Usuário substituto é obrigatório")
    private Long usuarioSubstitutoId;

    private String motivoSubstituicao;

    public SubstituicaoEscalaMusicoDTO() {
    }

    public Long getUsuarioSubstitutoId() {
        return usuarioSubstitutoId;
    }

    public void setUsuarioSubstitutoId(Long usuarioSubstitutoId) {
        this.usuarioSubstitutoId = usuarioSubstitutoId;
    }

    public String getMotivoSubstituicao() {
        return motivoSubstituicao;
    }

    public void setMotivoSubstituicao(String motivoSubstituicao) {
        this.motivoSubstituicao = motivoSubstituicao;
    }
}
