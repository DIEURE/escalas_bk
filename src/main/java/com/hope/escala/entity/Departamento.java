package com.hope.escala.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_departamentos")
    @SequenceGenerator(name = "seq_departamentos", sequenceName = "seq_departamentos", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "ativo")
    private Boolean ativo = true;

    // 🟢 Evita lazy loading proxies no JSON sem bloquear a leitura do ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "departamentos", "usuarios"})
    private Empresa empresa;

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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    // 🟢 Permite ao Jackson serializar o ID da congregação no JSON de resposta
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getEmpresaId() {
        return this.empresa != null ? this.empresa.getId() : null;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getEmpresaNome() {
        return this.empresa != null ? this.empresa.getNome() : null;
    }
}
