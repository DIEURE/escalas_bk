package com.hope.escala.entity;

import java.time.LocalDateTime;

import com.hope.escala.enums.TipoVoto;

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
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "opcao_voto", nullable = false, length = 20)
    private TipoVoto opcaoVoto;

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

    public TipoVoto getOpcaoVoto() {
        return opcaoVoto;
    }

    public void setOpcaoVoto(TipoVoto opcaoVoto) {
        this.opcaoVoto = opcaoVoto;
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
