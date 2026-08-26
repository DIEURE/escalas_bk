package com.hope.escala.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.hope.escala.enums.StatusEscala;
import com.hope.escala.enums.TipoEscala;

public class EscalaResponseDTO {

	private Long id;
	private LocalDate dataEscala;
	private LocalTime horario;
	private LocalTime horarioFim;
	private String culto;
	private String observacao;
	private Boolean ativa;
	private String youtubePlaylistUrl;
	private Long agendaMensalId;
	private String descricaoAgendaMensal;
	private TipoEscala tipoEscala;
	private String nomeDepartamento;
	private Long departamentoId;
	private StatusEscala status;

	public StatusEscala getStatus() {
		return status;
	}

	public void setStatus(StatusEscala status) {
		this.status = status;
	}

	public Long getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Long departamentoId) {
		this.departamentoId = departamentoId;
	}

	public EscalaResponseDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeDepartamento() {
		return nomeDepartamento;
	}

	public void setNomeDepartamento(String nomeDepartamento) {
		this.nomeDepartamento = nomeDepartamento;
	}

	public LocalDate getDataEscala() {
		return dataEscala;
	}

	public void setDataEscala(LocalDate dataEscala) {
		this.dataEscala = dataEscala;
	}

	public LocalTime getHorario() {
		return horario;
	}

	public void setHorario(LocalTime horario) {
		this.horario = horario;
	}

	public LocalTime getHorarioFim() {
		return horarioFim;
	}

	public void setHorarioFim(LocalTime horarioFim) {
		this.horarioFim = horarioFim;
	}

	public String getCulto() {
		return culto;
	}

	public void setCulto(String culto) {
		this.culto = culto;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Boolean getAtiva() {
		return ativa;
	}

	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}

	public String getYoutubePlaylistUrl() {
		return youtubePlaylistUrl;
	}

	public void setYoutubePlaylistUrl(String youtubePlaylistUrl) {
		this.youtubePlaylistUrl = youtubePlaylistUrl;
	}

	public Long getAgendaMensalId() {
		return agendaMensalId;
	}

	public void setAgendaMensalId(Long agendaMensalId) {
		this.agendaMensalId = agendaMensalId;
	}

	public String getDescricaoAgendaMensal() {
		return descricaoAgendaMensal;
	}

	public void setDescricaoAgendaMensal(String descricaoAgendaMensal) {
		this.descricaoAgendaMensal = descricaoAgendaMensal;
	}

	public TipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(TipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}
}