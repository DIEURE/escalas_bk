package com.hope.escala.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "escala_musicos")
public class EscalaMusico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id", nullable = false)
    private Escala escala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true) // Permite vaga aberta/desocupada
    private Usuario usuario;
    
    // Armazena o instrumento/posto da escala (ex: "TECLADO", "VIOLÃO", "MINISTRO")
    @Column(name = "instrumento")
    private String instrumento;

    private Boolean substituido = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_substituto_id")
    private Usuario usuarioSubstituto;

    private String motivoSubstituicao;

    private Boolean confirmado = false;

    @Column(name = "justificativa_recusa")
    private String justificativaRecusa;

    private String observacao;
    
    @Column(name = "data_escala")
    private LocalDate dataEscala; 
    
    @Column(name = "horario_manha")
    private LocalTime horarioManha;

    @Column(name = "horario_noite")
    private LocalTime horarioNoite;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnore
    private Empresa empresa;

    public EscalaMusico() {
    }

    // 🟢 Sincroniza automaticamente data, horários e empresa da Escala pai antes de salvar/atualizar
    @PrePersist
    @PreUpdate
    public void sincronizarDadosComEscala() {
        if (this.escala != null) {
            if (this.dataEscala == null) {
                this.dataEscala = this.escala.getDataEscala();
            }
            if (this.horarioManha == null) {
                this.horarioManha = this.escala.getHorarioManha();
            }
            if (this.horarioNoite == null) {
                this.horarioNoite = this.escala.getHorarioNoite();
            }
            if (this.empresa == null && this.escala.getEmpresa() != null) {
                this.empresa = this.escala.getEmpresa();
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Escala getEscala() {
        return escala;
    }

    public void setEscala(Escala escala) {
        this.escala = escala;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
    }

    public String getJustificativaRecusa() {
        return justificativaRecusa;
    }

    public void setJustificativaRecusa(String justificativaRecusa) {
        this.justificativaRecusa = justificativaRecusa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getSubstituido() {
        return substituido;
    }

    public void setSubstituido(Boolean substituido) {
        this.substituido = substituido;
    }

    public Usuario getUsuarioSubstituto() {
        return usuarioSubstituto;
    }

    public void setUsuarioSubstituto(Usuario usuarioSubstituto) {
        this.usuarioSubstituto = usuarioSubstituto;
    }

    public String getMotivoSubstituicao() {
        return motivoSubstituicao;
    }

    public void setMotivoSubstituicao(String motivoSubstituicao) {
        this.motivoSubstituicao = motivoSubstituicao;
    }

    public LocalDate getDataEscala() {
        return dataEscala;
    }

    public void setDataEscala(LocalDate dataEscala) {
        this.dataEscala = dataEscala;
    }

    public LocalTime getHorarioManha() {
        return horarioManha;
    }

    public void setHorarioManha(LocalTime horarioManha) {
        this.horarioManha = horarioManha;
    }

    public LocalTime getHorarioNoite() {
        return horarioNoite;
    }

    public void setHorarioNoite(LocalTime horarioNoite) {
        this.horarioNoite = horarioNoite;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
