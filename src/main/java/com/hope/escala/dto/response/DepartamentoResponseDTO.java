package com.hope.escala.dto.response;

import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;

public class DepartamentoResponseDTO {

    private Long id;

    private String nome;

    private Boolean ativo;

    public DepartamentoResponseDTO() {
    }     

    public DepartamentoResponseDTO(Departamento departamento) {
		 
		this.id = departamento.getId();
		this.nome = departamento.getNome();
		this.ativo = departamento.getAtivo();
	}  


	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}