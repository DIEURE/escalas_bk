package com.hope.escala.dto.response;

public class SubstitutoDisponivelDTO {

    private Long usuarioId;
    private String nome;
    private boolean principal;   // true = tem como principal, false = é curinga

    public SubstitutoDisponivelDTO() {
    }

    public SubstitutoDisponivelDTO(Long usuarioId, String nome, boolean principal) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.principal = principal;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
    
}
