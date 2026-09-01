package com.hope.escala.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public class EscalaMusicoResponseDTO {

	private Long id;

	private Long escalaId;

	private String nomeCultoManha;

	private String nomeCultoNoite;

	private Long usuarioId;

	private String nomeUsuario;

	private String instrumento;

	private Boolean confirmado;

	private String observacao;

	private LocalDate dataEscala;

	private LocalTime horarioManha;

	private LocalTime horarioNoite;

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

	public String getNomeCultoManha() {
		return nomeCultoManha;
	}

	public void setNomeCultoManha(String nomeCultoManha) {
		this.nomeCultoManha = nomeCultoManha;
	}

	public String getNomeCultoNoite() {
		return nomeCultoNoite;
	}

	public void setNomeCultoNoite(String nomeCultoNoite) {
		this.nomeCultoNoite = nomeCultoNoite;
	}

}