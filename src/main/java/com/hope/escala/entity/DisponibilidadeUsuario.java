package com.hope.escala.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_disponibilidade_usuario",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "data_disponivel", "empresa_id"}))
public class DisponibilidadeUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_disponivel", nullable = false)
    private LocalDate dataDisponivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_mensal_id", nullable = false)
    private AgendaMensal agendaMensal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public DisponibilidadeUsuario() {}

    // Getters e Setters manuais (Java 21 sem Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDate getDataDisponivel() { return dataDisponivel; }
    public void setDataDisponivel(LocalDate dataDisponivel) { this.dataDisponivel = dataDisponivel; }
    public AgendaMensal getAgendaMensal() { return agendaMensal; }
    public void setAgendaMensal(AgendaMensal agendaMensal) { this.agendaMensal = agendaMensal; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
