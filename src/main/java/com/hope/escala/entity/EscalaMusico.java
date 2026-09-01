package com.hope.escala.entity;

 

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "escala_musicos")
public class EscalaMusico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "escala_id")
    private Escala escala;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    private Boolean substituido = false;

    @ManyToOne
    @JoinColumn(name = "usuario_substituto_id")
    private Usuario usuarioSubstituto;

    private String motivoSubstituicao;

    private Boolean confirmado = false;

    private String observacao;
    
    private LocalDate dataEscala; 
    
    private LocalTime horarioManha;
    private LocalTime horarioNoite;

    
    public EscalaMusico() {
    }

    public Long getId() {
        return id;
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

    public Boolean getConfirmado() {
        return confirmado;
    }

    public void setConfirmado(Boolean confirmado) {
        this.confirmado = confirmado;
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

 
 
}