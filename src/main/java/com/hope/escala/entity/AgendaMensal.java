package com.hope.escala.entity;

import com.hope.escala.enums.StatusAgendaMensal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agenda_mensal")
public class AgendaMensal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer mes;

	private Integer ano;

	private String descricao;

	private Boolean ativa = true;
	
	@ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

	@Enumerated(EnumType.STRING)
	private StatusAgendaMensal status;

	public AgendaMensal() {
	}

	public Long getId() {
		return id;
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

	public StatusAgendaMensal getStatus() {
		return status;
	}

	public void setStatus(StatusAgendaMensal status) {
		this.status = status;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
  
     
}