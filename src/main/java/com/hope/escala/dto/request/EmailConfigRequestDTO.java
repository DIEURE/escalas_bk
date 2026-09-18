package com.hope.escala.dto.request;

public class EmailConfigRequestDTO {
    private String host;
    private Integer porta;
    private String usuario;
    private String senha;
    private String remetenteNome;
    private Boolean usarTls;

    // Getters e Setters
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
