package com.hope.escala.dto.response;

import java.time.LocalDateTime;

public class MusicaResponseDTO {

    private Long id;
    private String nome;
    private String cantor;
    private String tom;
    private Integer bpm;
    private String linkPlaylistManual;
    private String youtubeVideoId;
    private String cifra;
    private String cifraUrl;
    private Boolean ativa;
    private LocalDateTime dataCadastro;
    
    // Dados da categoria aninhados ou simplificados
    private Long categoriaId;
    private String categoriaNome;
    
    // Contador recap
    private Integer vezesEscalada;

    public MusicaResponseDTO() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Boolean getAtiva() {
		return ativa;
	}

	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}
	
	public String getYoutubeVideoId() {
	    return youtubeVideoId;
	}

	public void setYoutubeVideoId(String youtubeVideoId) {
	    this.youtubeVideoId = youtubeVideoId;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDateTime dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
		this.categoriaId = categoriaId;
	}

	public String getCategoriaNome() {
		return categoriaNome;
	}

	public void setCategoriaNome(String categoriaNome) {
		this.categoriaNome = categoriaNome;
	}

	public Integer getVezesEscalada() {
		return vezesEscalada;
	}

	public void setVezesEscalada(Integer vezesEscalada) {
		this.vezesEscalada = vezesEscalada;
	}

    // getters e setters
    
    
}
