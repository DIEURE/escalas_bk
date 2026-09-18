package com.hope.escala.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "email_config")
public class EmailConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private Integer porta;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String senha;

    @Column(name = "remetente_nome")
    private String remetenteNome;

    @Column(name = "usar_tls")
    private Boolean usarTls = true;

    public EmailConfig() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Integer getPorta() { return porta; }
    public void setPorta(Integer porta) { this.porta = porta; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getRemetenteNome() { return remetenteNome; }
    public void setRemetenteNome(String remetenteNome) { this.remetenteNome = remetenteNome; }

    public Boolean getUsarTls() { return usarTls; }
    public void setUsarTls(Boolean usarTls) { this.usarTls = usarTls; }
}
