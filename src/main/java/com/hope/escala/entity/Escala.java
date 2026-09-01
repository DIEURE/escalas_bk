package com.hope.escala.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.hope.escala.enums.StatusEscala;
import com.hope.escala.enums.TipoEscala;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "escalas")
public class Escala {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate dataEscala;

	private String nomeCultoManha;

	private String nomeCultoNoite;

	private LocalTime horarioManha;
	private LocalTime horarioManhaFim;

	private LocalTime horarioNoite;
	private LocalTime horarioNoiteFim;

	@Column(columnDefinition = "TEXT")
	private String observacao;

	private Boolean ativa = true;

	private LocalDateTime dataCadastro;

	// Na sua Entity Escala (ex: com.hope.escala.entity.Escala)
	private String linkPlaylistManual;

	// Getters e Setters
	public String getLinkPlaylistManual() {
		return linkPlaylistManual;
	}

	public void setLinkPlaylistManual(String linkPlaylistManual) {
		this.linkPlaylistManual = linkPlaylistManual;
	}

	@Enumerated(EnumType.STRING)
	private StatusEscala status;

	public StatusEscala getStatus() {
		return status;
	}

	@OneToMany(mappedBy = "escala", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EscalaMusico> musicos = new ArrayList<>();

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

	public void setId(Long id) {
		this.id = id;
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

	public Boolean getAtiva() {
		return ativa;
	}

	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDateTime dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public List<EscalaMusico> getMusicos() {
		return musicos;
	}

	public void setMusicos(List<EscalaMusico> musicos) {
		this.musicos = musicos;
	}

	public AgendaMensal getAgendaMensal() {
		return agendaMensal;
	}

	public void setAgendaMensal(AgendaMensal agendaMensal) {
		this.agendaMensal = agendaMensal;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public TipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(TipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	public void setStatus(StatusEscala status) {
		this.status = status;
	}

}