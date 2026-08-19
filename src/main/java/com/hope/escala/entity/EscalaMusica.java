package com.hope.escala.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "escala_musicas")
public class EscalaMusica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "escala_id")
	private Escala escala;

	@ManyToOne
	@JoinColumn(name = "musica_id")
	private Musica musica;
	
	
	private Boolean substituida = false;

	@ManyToOne
	@JoinColumn(name = "musica_substituta_id")
	private Musica musicaSubstituta;

	private String motivoSubstituicao;
	

	private Integer ordem;

	private String observacao;

	public EscalaMusica() {
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

	public Musica getMusica() {
		return musica;
	}

	public void setMusica(Musica musica) {
		this.musica = musica;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Boolean getSubstituida() {
		return substituida;
	}

	public void setSubstituida(Boolean substituida) {
		this.substituida = substituida;
	}

	public Musica getMusicaSubstituta() {
		return musicaSubstituta;
	}

	public void setMusicaSubstituta(Musica musicaSubstituta) {
		this.musicaSubstituta = musicaSubstituta;
	}

	public String getMotivoSubstituicao() {
		return motivoSubstituicao;
	}

	public void setMotivoSubstituicao(String motivoSubstituicao) {
		this.motivoSubstituicao = motivoSubstituicao;
	}
	
	
}