package com.hope.escala.dto.response;
 

import java.util.List;

public class EscalaDashboardResponseDTO {

    private EscalaResponseDTO escala;

    private List<EscalaMusicoResponseDTO> musicos;

    private List<EscalaMusicaResponseDTO> repertorio;

    private Integer totalMusicos;

    private Integer totalConfirmados;

    private Integer totalPendentes;

    private Integer totalMusicas;

    public EscalaDashboardResponseDTO() {
    }

    public EscalaResponseDTO getEscala() {
        return escala;
    }

    public void setEscala(EscalaResponseDTO escala) {
        this.escala = escala;
    }

    public List<EscalaMusicoResponseDTO> getMusicos() {
        return musicos;
    }

    public void setMusicos(
            List<EscalaMusicoResponseDTO> musicos
    ) {
        this.musicos = musicos;
    }

    public List<EscalaMusicaResponseDTO> getRepertorio() {
        return repertorio;
    }

    public void setRepertorio(
            List<EscalaMusicaResponseDTO> repertorio
    ) {
        this.repertorio = repertorio;
    }

    public Integer getTotalMusicos() {
        return totalMusicos;
    }

    public void setTotalMusicos(Integer totalMusicos) {
        this.totalMusicos = totalMusicos;
    }

    public Integer getTotalConfirmados() {
        return totalConfirmados;
    }

    public void setTotalConfirmados(
            Integer totalConfirmados
    ) {
        this.totalConfirmados = totalConfirmados;
    }

    public Integer getTotalPendentes() {
        return totalPendentes;
    }

    public void setTotalPendentes(
            Integer totalPendentes
    ) {
        this.totalPendentes = totalPendentes;
    }

    public Integer getTotalMusicas() {
        return totalMusicas;
    }

    public void setTotalMusicas(Integer totalMusicas) {
        this.totalMusicas = totalMusicas;
    }
}