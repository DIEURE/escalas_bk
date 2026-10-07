package com.hope.escala.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "votos_pauta",
    uniqueConstraints = @UniqueConstraint(name = "uk_voto_pauta_usuario", columnNames = {"pauta_id", "usuario_id"})
)
public class VotoPauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false)
    private PautaReuniao pauta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcao_id", nullable = false)
    private PautaOpcao opcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(length = 300)
    private String justificativa;

    @Column(name = "data_voto", nullable = false)
    private LocalDateTime dataVoto;

    @PrePersist
    public void prePersist() {
        if (dataVoto == null) {
            dataVoto = LocalDateTime.now();
        }
    }

    public VotoPauta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PautaReuniao getPauta() {
        return pauta;
    }

    public void setPauta(PautaReuniao pauta) {
        this.pauta = pauta;
    }

    public PautaOpcao getOpcao() {
        return opcao;
    }

    public void setOpcao(PautaOpcao opcao) {
        this.opcao = opcao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public LocalDateTime getDataVoto() {
        return dataVoto;
    }

    public void setDataVoto(LocalDateTime dataVoto) {
        this.dataVoto = dataVoto;
    }
}