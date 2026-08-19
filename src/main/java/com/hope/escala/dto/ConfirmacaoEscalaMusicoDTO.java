package com.hope.escala.dto;
 
import jakarta.validation.constraints.NotNull;

public class ConfirmacaoEscalaMusicoDTO {

    @NotNull(message = "Confirmação é obrigatória")
    private Boolean confirmado;

    public ConfirmacaoEscalaMusicoDTO() {
    }

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
    }
}