package com.hope.escala.entity;

 

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suspensao_voluntario")
public class SuspensaoVoluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "departamento_id", nullable = false)
    private Long departamentoId;

    @Column(name = "mes_bloqueio", nullable = false)
    private Integer mesBloqueio;

    @Column(name = "ano_bloqueio", nullable = false)
    private Integer anoBloqueio;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "criado_por", length = 100)
    private String criadoPor;

    @Column(nullable = false)
    private Boolean ativo = true;

    public SuspensaoVoluntario() {
        this.criadoEm = LocalDateTime.now();
        this.ativo = true;
    }

    public SuspensaoVoluntario(Long id, Long usuarioId, Long empresaId, Long departamentoId,
                               Integer mesBloqueio, Integer anoBloqueio, String motivo,
                               LocalDateTime criadoEm, String criadoPor, Boolean ativo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.empresaId = empresaId;
        this.departamentoId = departamentoId;
        this.mesBloqueio = mesBloqueio;
        this.anoBloqueio = anoBloqueio;
        this.motivo = motivo;
        this.criadoEm = criadoEm != null ? criadoEm : LocalDateTime.now();
        this.criadoPor = criadoPor;
        this.ativo = ativo != null ? ativo : true;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public Integer getMesBloqueio() {
        return mesBloqueio;
    }

    public void setMesBloqueio(Integer mesBloqueio) {
        this.mesBloqueio = mesBloqueio;
    }

    public Integer getAnoBloqueio() {
        return anoBloqueio;
    }

    public void setAnoBloqueio(Integer anoBloqueio) {
        this.anoBloqueio = anoBloqueio;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
