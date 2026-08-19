package com.hope.escala.dto.response;

import com.hope.escala.enums.StatusAgendaMensal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AgendaMensalResponseDTO {

    private Long id;

    @NotNull(message = "Mês é obrigatório")
    @Positive(message = "Mês deve ser entre 1 e 12")
    private Integer mes;

    @NotNull(message = "Ano é obrigatório")
    @Positive(message = "Ano deve ser positivo")
    private Integer ano;
    
    private String descricao;
    
    @NotNull(message = "Departamento é obrigatório")
    @Positive(message = "Departamento ID inválido")
    private Long departamentoId;

    private String departamentoNome;

    private StatusAgendaMensal status;


    public String getDepartamentoNome() {
		return departamentoNome;
	}

	public void setDepartamentoNome(String departamentoNome) {
		this.departamentoNome = departamentoNome;
	}

	public StatusAgendaMensal getStatus() {
		return status;
	}

	public void setStatus(StatusAgendaMensal status) {
		this.status = status;
	}

	public Long getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Long departamentoId) {
		this.departamentoId = departamentoId;
	}

	private Boolean ativa;

    public AgendaMensalResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}