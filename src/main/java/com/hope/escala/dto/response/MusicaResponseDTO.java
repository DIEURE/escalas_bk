package com.hope.escala.dto.response;

import com.hope.escala.entity.Musica;

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
    private Integer vezesEscalada;
    private Long categoriaId;
    private String categoriaNome;

    public MusicaResponseDTO() {
    }

    public MusicaResponseDTO(Musica musica) {
        this.id = musica.getId();
        this.nome = musica.getNome();
        this.cantor = musica.getCantor();
        this.tom = musica.getTom();
        this.bpm = musica.getBpm();
        this.linkPlaylistManual = musica.getLinkPlaylistManual();
        this.youtubeVideoId = musica.getYoutubeVideoId();
        this.cifra = musica.getCifra();
        this.cifraUrl = musica.getCifraUrl();
        this.ativa = musica.getAtiva();
        this.dataCadastro = musica.getDataCadastro();
        this.vezesEscalada = musica.getVezesEscalada();
        this.categoriaId = musica.getCategoria() != null ? musica.getCategoria().getId() : null;
        this.categoriaNome = musica.getCategoria() != null ? musica.getCategoria().getNome() : null;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCantor() {
        return cantor;
    }

    public String getTom() {
        return tom;
    }

    public Integer getBpm() {
        return bpm;
    }

    public String getLinkPlaylistManual() {
        return linkPlaylistManual;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public String getCifra() {
        return cifra;
    }

    public String getCifraUrl() {
        return cifraUrl;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public Integer getVezesEscalada() {
        return vezesEscalada;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNome() {
        return categoriaNome;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCantor(String cantor) {
        this.cantor = cantor;
    }

    public void setTom(String tom) {
        this.tom = tom;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public void setLinkPlaylistManual(String linkPlaylistManual) {
        this.linkPlaylistManual = linkPlaylistManual;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }

    public void setCifra(String cifra) {
        this.cifra = cifra;
    }

    public void setCifraUrl(String cifraUrl) {
        this.cifraUrl = cifraUrl;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setVezesEscalada(Integer vezesEscalada) {
        this.vezesEscalada = vezesEscalada;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }
}
