package com.hope.escala.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "excecao_escala_data")
public class ExcecaoEscalaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_excecao", nullable = false)
    private LocalDate dataExcecao;

    @Column(name = "departamento_id", nullable = false)
    private Long departamentoId;

    // Relacionamento com a tabela de instrumentos (baseado no seu ID de instrumento: 1, 2, 3, etc.)
    @Column(name = "instrumento_id", nullable = false)
    private Long instrumentoId;

    @Column(name = "limite_vagas")
    private Integer limiteVagas; // Ex: Limitar a 1 violão nesta data

    @Column(name = "bloqueado")
    private Boolean bloqueado = false; // Ex: Bloquear bateria neste culto específico

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public ExcecaoEscalaData() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataExcecao() {
        return dataExcecao;
    }

    public void setDataExcecao(LocalDate dataExcecao) {
        this.dataExcecao = dataExcecao;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public Long getInstrumentoId() {
        return instrumentoId;
    }

    public void setInstrumentoId(Long instrumentoId) {
        this.instrumentoId = instrumentoId;
    }

    public Integer getLimiteVagas() {
        return limiteVagas;
    }

    public void setLimiteVagas(Integer limiteVagas) {
        this.limiteVagas = limiteVagas;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    
}
