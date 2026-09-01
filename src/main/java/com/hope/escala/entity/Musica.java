package com.hope.escala.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "musicas")
public class Musica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String cantor;

	private String tom;

	private Integer bpm;

	private String linkPlaylistManual;

	private String youtubeVideoId;

	@Column(columnDefinition = "TEXT")
	private String cifra;

	private Boolean ativa = true;

	private LocalDateTime dataCadastro;

	private String cifraUrl;

	public Musica() {
	}

	@PrePersist
	public void prePersist() {
		this.dataCadastro = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCifraUrl() {
		return cifraUrl;
	}

	public void setCifraUrl(String cifraUrl) {
		this.cifraUrl = cifraUrl;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCantor() {
		return cantor;
	}

	public void setCantor(String cantor) {
		this.cantor = cantor;
	}

	public String getTom() {
		return tom;
	}

	public void setTom(String tom) {
		this.tom = tom;
	}

	public Integer getBpm() {
		return bpm;
	}

	public void setBpm(Integer bpm) {
		this.bpm = bpm;
	}

	public String getLinkPlaylistManual() {
		return linkPlaylistManual;
	}

	public void setLinkPlaylistManual(String linkPlaylistManual) {
		this.linkPlaylistManual = linkPlaylistManual;
	}

	public String getCifra() {
		return cifra;
	}

	public void setCifra(String cifra) {
		this.cifra = cifra;
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

	public String getYoutubeVideoId() {
		return youtubeVideoId;
	}

	public void setYoutubeVideoId(String youtubeVideoId) {
		this.youtubeVideoId = youtubeVideoId;
	}

}