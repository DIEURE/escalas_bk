package com.hope.escala.dto.request;

 

import jakarta.validation.constraints.NotBlank;

public class MusicaRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String cantor;

    private String tom;

    private Integer bpm;

    private String linkPlaylistManual;
    
    private String youtubeVideoId;

    private String cifra;
    
    private String cifraUrl;
    
    private Boolean ativa;
    
    private Long categoriaId; // ID da categoria selecionada no frontend

    public MusicaRequestDTO() {
    }
    
    
    // getters e setters

	public String getNome() {
		return nome;
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
	
	public String getCifraUrl() {
	    return cifraUrl;
	}

	public void setCifraUrl(String cifraUrl) {
	    this.cifraUrl = cifraUrl;
	}

	public String getYoutubeVideoId() {
	    return youtubeVideoId;
	}

	public void setYoutubeVideoId(String youtubeVideoId) {
	    this.youtubeVideoId = youtubeVideoId;
	}


	public Boolean getAtiva() {
		return ativa;
	}


	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}


	public Long getCategoriaId() {
		return categoriaId;
	}


	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}
    
    
    
    
}