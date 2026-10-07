package com.hope.escala.entity;

import java.util.ArrayList;
import java.util.List;

import com.hope.escala.enums.StatusVotacaoPauta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "pautas_reuniao")
public class PautaReuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ata_id", nullable = false)
    private AtaReuniao ata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private Integer ordem = 1;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "requer_votacao", nullable = false)
    private Boolean requerVotacao = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_votacao", nullable = false, length = 30)
    private StatusVotacaoPauta statusVotacao = StatusVotacaoPauta.NAO_INICIADA;

    @OneToMany(mappedBy = "pauta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<PautaOpcao> opcoes = new ArrayList<>();

    @OneToMany(mappedBy = "pauta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotoPauta> votos = new ArrayList<>();

    public PautaReuniao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtaReuniao getAta() {
        return ata;
    }

    public void setAta(AtaReuniao ata) {
        this.ata = ata;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getRequerVotacao() {
        return requerVotacao;
    }

    public void setRequerVotacao(Boolean requerVotacao) {
        this.requerVotacao = requerVotacao;
    }

    public StatusVotacaoPauta getStatusVotacao() {
        return statusVotacao;
    }

    public void setStatusVotacao(StatusVotacaoPauta statusVotacao) {
        this.statusVotacao = statusVotacao;
    }

    public List<PautaOpcao> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<PautaOpcao> opcoes) {
        this.opcoes = opcoes;
    }

    public List<VotoPauta> getVotos() {
        return votos;
    }

    public void setVotos(List<VotoPauta> votos) {
        this.votos = votos;
    }
}