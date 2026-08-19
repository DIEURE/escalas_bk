package com.hope.escala.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AgendaMensalRequestDTO {

    private Integer mes;

    private Integer ano;

    private String descricao;
    
    @NotNull(message = "Departamento é obrigatório")
    @Positive(message = "Departamento ID inválido")
    private Long departamentoId;

    public Long getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Long departamentoId) {
		this.departamentoId = departamentoId;
	}

	public AgendaMensalRequestDTO() {
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}