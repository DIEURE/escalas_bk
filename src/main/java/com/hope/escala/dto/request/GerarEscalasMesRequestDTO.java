package com.hope.escala.dto.request;

import java.time.LocalTime;

import com.hope.escala.enums.TipoEscala;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GerarEscalasMesRequestDTO {

    @NotNull(message = "Departamento ID é obrigatório")
    @Positive(message = "Departamento ID deve ser positivo")
    private Long departamentoId;

    @NotNull(message = "Tipo de escala é obrigatório")
    private TipoEscala tipoEscala;

    @NotNull(message = "Horário da manhã é obrigatório")
    private LocalTime horarioManha;

    @NotNull(message = "Horário da noite é obrigatório")
    private LocalTime horarioNoite;

    @NotNull(message = "Nome do culto da manhã é obrigatório")
    private String nomeCultoManha;

    @NotNull(message = "Nome do culto da noite é obrigatório")
    private String nomeCultoNoite;

    @NotNull(message = "Gerar domingos é obrigatório")
    private Boolean gerarDomingos;

    @NotNull(message = "Repetir mesma equipe é obrigatório")
    private Boolean repetirMesmaEquipe;

    // Construtores
    public GerarEscalasMesRequestDTO() {
    }

    public GerarEscalasMesRequestDTO(
            Long departamentoId,
            TipoEscala tipoEscala,
            LocalTime horarioManha,
            LocalTime horarioNoite,
            String nomeCultoManha,
            String nomeCultoNoite,
            Boolean gerarDomingos,
            Boolean repetirMesmaEquipe) {
        this.departamentoId = departamentoId;
        this.tipoEscala = tipoEscala;
        this.horarioManha = horarioManha;
        this.horarioNoite = horarioNoite;
        this.nomeCultoManha = nomeCultoManha;
        this.nomeCultoNoite = nomeCultoNoite;
        this.gerarDomingos = gerarDomingos;
        this.repetirMesmaEquipe = repetirMesmaEquipe;
    }

    // Getters e Setters
    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public TipoEscala getTipoEscala() {
        return tipoEscala;
    }

    public void setTipoEscala(TipoEscala tipoEscala) {
        this.tipoEscala = tipoEscala;
    }

    public LocalTime getHorarioManha() {
        return horarioManha;
    }

    public void setHorarioManha(LocalTime horarioManha) {
        this.horarioManha = horarioManha;
    }

    public LocalTime getHorarioNoite() {
        return horarioNoite;
    }

    public void setHorarioNoite(LocalTime horarioNoite) {
        this.horarioNoite = horarioNoite;
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

    public Boolean getGerarDomingos() {
        return gerarDomingos;
    }

    public void setGerarDomingos(Boolean gerarDomingos) {
        this.gerarDomingos = gerarDomingos;
    }

    public Boolean getRepetirMesmaEquipe() {
        return repetirMesmaEquipe;
    }

    public void setRepetirMesmaEquipe(Boolean repetirMesmaEquipe) {
        this.repetirMesmaEquipe = repetirMesmaEquipe;
    }
}