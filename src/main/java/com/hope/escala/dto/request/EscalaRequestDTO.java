package com.hope.escala.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.hope.escala.enums.TipoEscala;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EscalaRequestDTO {

	@NotNull(message = "Data da escala é obrigatória")
	private LocalDate dataEscala;

	@NotNull(message = "Horário é obrigatório")
	private LocalTime horario;

	private LocalTime horarioFim;

	@NotBlank(message = "Culto é obrigatório")
	private String culto;

	private String observacao;

	private Long agendaMensalId;

	private TipoEscala tipoEscala;

	private Long departamentoId;

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