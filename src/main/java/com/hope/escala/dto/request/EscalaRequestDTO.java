package com.hope.escala.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.hope.escala.enums.TipoEscala;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EscalaRequestDTO {

	@NotNull(message = "Data da escala é obrigatória")
	private LocalDate dataEscala;

	private String nomeCultoManha;
	
	 
	private String nomeCultoNoite; 
	
	private LocalTime horarioManha;
	private LocalTime horarioManhaFim;

	 
	private LocalTime horarioNoite;
	private LocalTime horarioNoiteFim;

	private String observacao;

	private Long agendaMensalId;

	private TipoEscala tipoEscala;

	private Long departamentoId;

	private List<Long> musicosIds; // Vamos enviar apenas a lista de IDs dos músicos

	public List<Long> getMusicosIds() {
		return musicosIds;
	}

	public void setMusicosIds(List<Long> musicosIds) {
		this.musicosIds = musicosIds;
	}

	public Long getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Long departamentoId) {
		this.departamentoId = departamentoId;
	}

	public EscalaRequestDTO() {
	}

	public LocalDate getDataEscala() {
		return dataEscala;
	}

	public void setDataEscala(LocalDate dataEscala) {
		this.dataEscala = dataEscala;
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

	public LocalTime getHorarioManha() {
		return horarioManha;
	}

	public void setHorarioManha(LocalTime horarioManha) {
		this.horarioManha = horarioManha;
	}

	public LocalTime getHorarioManhaFim() {
		return horarioManhaFim;
	}

	public void setHorarioManhaFim(LocalTime horarioManhaFim) {
		this.horarioManhaFim = horarioManhaFim;
	}

	public LocalTime getHorarioNoite() {
		return horarioNoite;
	}

	public void setHorarioNoite(LocalTime horarioNoite) {
		this.horarioNoite = horarioNoite;
	}

	public LocalTime getHorarioNoiteFim() {
		return horarioNoiteFim;
	}

	public void setHorarioNoiteFim(LocalTime horarioNoiteFim) {
		this.horarioNoiteFim = horarioNoiteFim;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Long getAgendaMensalId() {
		return agendaMensalId;
	}

	public void setAgendaMensalId(Long agendaMensalId) {
		this.agendaMensalId = agendaMensalId;
	}

	public TipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(TipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

}