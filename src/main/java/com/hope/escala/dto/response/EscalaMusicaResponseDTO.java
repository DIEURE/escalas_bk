package com.hope.escala.dto.response;

import jakarta.persistence.Column;

public class EscalaMusicaResponseDTO {

	private Long id;

	private Long escalaId;

	private String nomeCultoManha;
	
	private String nomeCultoNoite;

	private Long musicaId;

	private String youtubeVideoId;

	private String nomeMusica;

	private String cantor;

	private String tom;
	
	private Integer bpm;
	
	@Column(columnDefinition = "TEXT")
	private String cifra;

	private Integer ordem;
	
	private String tituloPlaylistManual;

	private String observacao;

	private Boolean substituida;

	private Long musicaSubstitutaId;

	private String nomeMusicaSubstituta;

	private String motivoSubstituicao;

	private String cifraUrl;

	public EscalaMusicaResponseDTO() {
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

	 

	public String getNomeCultoManha() {
		return nomeCultoManha;
	}

	public void setNomeCultoManha(String nomeCultoManha) {
		this.nomeCultoManha = nomeCultoManha;
	}

	public String getNomeCultoNoite() {
		return nomeCultoNoite;
	}

	public String getTituloPlaylistManual() {
		return tituloPlaylistManual;
	}

	public void setTituloPlaylistManual(String tituloPlaylistManual) {
		this.tituloPlaylistManual = tituloPlaylistManual;
	}

	public void setNomeCultoNoite(String nomeCultoNoite) {
		this.nomeCultoNoite = nomeCultoNoite;
	}

	public Long getMusicaId() {
		return musicaId;
	}

	public void setMusicaId(Long musicaId) {
		this.musicaId = musicaId;
	}

	public String getNomeMusica() {
		return nomeMusica;
	}

	public void setNomeMusica(String nomeMusica) {
		this.nomeMusica = nomeMusica;
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

	public Long getMusicaSubstitutaId() {
		return musicaSubstitutaId;
	}

	public void setMusicaSubstitutaId(Long musicaSubstitutaId) {
		this.musicaSubstitutaId = musicaSubstitutaId;
	}

	public String getNomeMusicaSubstituta() {
		return nomeMusicaSubstituta;
	}

	public void setNomeMusicaSubstituta(String nomeMusicaSubstituta) {
		this.nomeMusicaSubstituta = nomeMusicaSubstituta;
	}

	public String getMotivoSubstituicao() {
		return motivoSubstituicao;
	}

	public void setMotivoSubstituicao(String motivoSubstituicao) {
		this.motivoSubstituicao = motivoSubstituicao;
	}

	public String getYoutubeVideoId() {
		return youtubeVideoId;
	}

	public void setYoutubeVideoId(String youtubeVideoId) {
		this.youtubeVideoId = youtubeVideoId;
	}

	public String getCifraUrl() {
		return cifraUrl;
	}

	public Integer getBpm() {
		return bpm;
	}

	public void setBpm(Integer integer) {
		this.bpm = integer;
	}

	public void setCifraUrl(String cifraUrl) {
		this.cifraUrl = cifraUrl;
	}

	public String getCifra() {
		return cifra;
	}

	public void setCifra(String cifra) {
		this.cifra = cifra;
	}

}