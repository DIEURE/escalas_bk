package com.hope.escala.dto.response;

import com.hope.escala.entity.Departamento;
 
 
public class DepartamentoResponseDTO {

    private Long id;
    private String nome;
    private Boolean ativo;
    private Long empresaId;
    private String empresaNome;

    public DepartamentoResponseDTO() {
    }

    public DepartamentoResponseDTO(Long id, String nome, Boolean ativo, Long empresaId, String empresaNome) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
    }

    // Construtor a partir da Entidade
    public DepartamentoResponseDTO(Departamento dep) {
        this.id = dep.getId();
        this.nome = dep.getNome();
        this.ativo = dep.getAtivo();
        if (dep.getEmpresa() != null) {
            this.empresaId = dep.getEmpresa().getId();
            this.empresaNome = dep.getEmpresa().getNome();
        }
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

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }
}
