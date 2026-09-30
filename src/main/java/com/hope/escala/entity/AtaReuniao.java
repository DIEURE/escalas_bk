package com.hope.escala.entity;

 
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.hope.escala.enums.StatusAta;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "atas_reuniao")
public class AtaReuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_id", nullable = false)
    private Usuario criador;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(name = "data_reuniao", nullable = false)
    private LocalDate dataReuniao;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Column(name = "local_reuniao", length = 150)
    private String localReuniao;

    @Column(name = "conteudo_ata", columnDefinition = "TEXT")
    private String conteudoAta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAta status = StatusAta.RASCUNHO;

    @Column(nullable = false)
    private Boolean ativa = true;

    @OneToMany(mappedBy = "ata", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<PautaReuniao> pautas = new ArrayList<>();

    public AtaReuniao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getDataReuniao() {
        return dataReuniao;
    }

    public void setDataReuniao(LocalDate dataReuniao) {
        this.dataReuniao = dataReuniao;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public String getLocalReuniao() {
        return localReuniao;
    }

    public void setLocalReuniao(String localReuniao) {
        this.localReuniao = localReuniao;
    }

    public String getConteudoAta() {
        return conteudoAta;
    }

    public void setConteudoAta(String conteudoAta) {
        this.conteudoAta = conteudoAta;
    }

    public StatusAta getStatus() {
        return status;
    }

    public void setStatus(StatusAta status) {
        this.status = status;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public List<PautaReuniao> getPautas() {
        return pautas;
    }

    public void setPautas(List<PautaReuniao> pautas) {
        this.pautas = pautas;
    }
}
