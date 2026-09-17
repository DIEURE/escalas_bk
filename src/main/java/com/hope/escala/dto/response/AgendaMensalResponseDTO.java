package com.hope.escala.dto.response;

import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.enums.StatusAgendaMensal;

public class AgendaMensalResponseDTO {

    private Long id;
    private Integer mes;
    private Integer ano;
    private String descricao;
    private Long departamentoId;
    private String departamentoNome;
    private StatusAgendaMensal status;
    private Boolean ativa;
    
    // 🟢 No DTO, expomos apenas os dados necessários da empresa, não a Entidade JPA
    private Long empresaId;
    private String nomeEmpresa;

    public AgendaMensalResponseDTO() {
    }

    // 🟢 Construtor útil para converter a Entidade direto em DTO com facilidade
    public AgendaMensalResponseDTO(AgendaMensal agenda) {
        this.id = agenda.getId();
        this.mes = agenda.getMes();
        this.ano = agenda.getAno();
        this.descricao = agenda.getDescricao();
        this.status = agenda.getStatus();
        this.ativa = agenda.getAtiva();
        
        if (agenda.getDepartamento() != null) {
            this.departamentoId = agenda.getDepartamento().getId();
            this.departamentoNome = agenda.getDepartamento().getNome();
        }

        if (agenda.getEmpresa() != null) {
            this.empresaId = agenda.getEmpresa().getId();
            this.nomeEmpresa = agenda.getEmpresa().getNome();
        }
    }

    // Getters e Setters
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

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

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

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }
}
