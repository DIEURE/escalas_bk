package com.hope.escala.dto.request;
 

import jakarta.validation.constraints.NotNull;

public class UsuarioDisponibilidadeDTO {

    @NotNull(message = "Disponibilidade é obrigatória")
	public Boolean disponibilidade;

    public UsuarioDisponibilidadeDTO() {
    }

    public Boolean getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Boolean disponibilidade) {
        this.disponibilidade = disponibilidade;
    }
 }

