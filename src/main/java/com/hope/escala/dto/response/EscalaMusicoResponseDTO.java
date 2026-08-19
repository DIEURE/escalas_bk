package com.hope.escala.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public class EscalaMusicoResponseDTO {

    private Long id;

    private Long escalaId;
    private String culto;

    private Long usuarioId;
    private String nomeUsuario;

    private String instrumento;

    private Boolean confirmado;

    private String observacao;
    
   private LocalDate dataEscala; 
    
    private LocalTime horario;       
    
    public LocalDate getDataEscala() {
		return dataEscala;
	}

	public void setDataEscala(LocalDate dataEscala) {
		this.dataEscala = dataEscala;
	}

	public LocalTime getHorario() {
		return horario;
	}

	public void setHorario(LocalTime localTime) {
		this.horario = localTime;
	}

	private Boolean substituido;

    private Long usuarioSubstitutoId;
    
    private String nomeSubstituto;

    private String motivoSubstituicao;

    public EscalaMusicoResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEscalaId() {
        return escalaId;
    }

    public void setEscalaId(Long escalaId) {
        this.escalaId = escalaId;
    }

    public String getCulto() {
        return culto;
    }

    public void setCulto(String culto) {
        this.culto = culto;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
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

	public Long getUsuarioSubstitutoId() {
		return usuarioSubstitutoId;
	}

	public void setUsuarioSubstitutoId(Long usuarioSubstitutoId) {
		this.usuarioSubstitutoId = usuarioSubstitutoId;
	}

	public String getNomeSubstituto() {
		return nomeSubstituto;
	}

	public void setNomeSubstituto(String nomeSubstituto) {
		this.nomeSubstituto = nomeSubstituto;
	}

	public String getMotivoSubstituicao() {
		return motivoSubstituicao;
	}

	public void setMotivoSubstituicao(String motivoSubstituicao) {
		this.motivoSubstituicao = motivoSubstituicao;
	}
    
    
}