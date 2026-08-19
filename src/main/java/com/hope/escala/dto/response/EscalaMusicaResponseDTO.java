package com.hope.escala.dto.response;

public class EscalaMusicaResponseDTO {

	private Long id;

	private Long escalaId;

	private String culto;

	private Long musicaId;

	private String youtubeVideoId;

	private String nomeMusica;

	private String cantor;

	private String tom;

	private Integer ordem;
	
	

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

	public String getCulto() {
		return culto;
	}

	public void setCulto(String culto) {
		this.culto = culto;
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

	public void setCifraUrl(String cifraUrl) {
		this.cifraUrl = cifraUrl;
	}

}