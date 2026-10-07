package com.hope.escala.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pautas_opcoes")
public class PautaOpcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false)
    private PautaReuniao pauta;

    @Column(nullable = false, length = 150)
    private String texto;

    @Column(nullable = false)
    private Integer ordem = 1;

    public PautaOpcao() {
    }

    public PautaOpcao(PautaReuniao pauta, String texto, Integer ordem) {
        this.pauta = pauta;
        this.texto = texto;
        this.ordem = ordem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PautaReuniao getPauta() {
        return pauta;
    }

    public void setPauta(PautaReuniao pauta) {
        this.pauta = pauta;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}