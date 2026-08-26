package com.hope.escala.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.hope.escala.enums.StatusEscala;
import com.hope.escala.enums.TipoEscala;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "escalas")
public class Escala {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate dataEscala;

	private LocalTime horario;
	private LocalTime horarioFim;

	private String culto;

	@Column(columnDefinition = "TEXT")
	private String observacao;

	private Boolean ativa = true;

	private LocalDateTime dataCadastro;

	private String youtubePlaylistId;

	private String youtubePlaylistUrl;

	@Enumerated(EnumType.STRING)
	private StatusEscala status;

	public StatusEscala getStatus() {
		return status;
	}

	public void setStatus(StatusEscala status) {
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name = "agenda_mensal_id")
	private AgendaMensal agendaMensal;

	@ManyToOne
	@JoinColumn(name = "departamento_id")
	private Departamento departamento;

	@Enumerated(EnumType.STRING)
	private TipoEscala tipoEscala;

	public Escala() {
	}

	@PrePersist
	public void prePersist() {
		this.dataCadastro = LocalDateTime.now();
	}

	public Long getId() {
		return id;
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

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public String getYoutubePlaylistId() {
		return youtubePlaylistId;
	}

	public void setYoutubePlaylistId(String youtubePlaylistId) {
		this.youtubePlaylistId = youtubePlaylistId;
	}

	public String getYoutubePlaylistUrl() {
		return youtubePlaylistUrl;
	}

	public void setYoutubePlaylistUrl(String youtubePlaylistUrl) {
		this.youtubePlaylistUrl = youtubePlaylistUrl;
	}

	public AgendaMensal getAgendaMensal() {
		return agendaMensal;
	}

	public void setAgendaMensal(AgendaMensal agendaMensal) {
		this.agendaMensal = agendaMensal;
	}

	public TipoEscala getTipoEscala() {
		return tipoEscala;
	}
	
	public LocalTime getHorarioFim() { return horarioFim; }
	public void setHorarioFim(LocalTime horarioFim) { this.horarioFim = horarioFim; }

	public void setTipoEscala(TipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}
}