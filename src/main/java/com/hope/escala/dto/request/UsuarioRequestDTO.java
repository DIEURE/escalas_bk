package com.hope.escala.dto.request;

import java.util.HashSet;
import java.util.Set;

import com.hope.escala.enums.PerfilUsuario;

public class UsuarioRequestDTO {

    private String nome;

    private String email;

    private String telefone;

    private String senha;

    private Boolean disponibilidade;

    private String observacao;

    private PerfilUsuario perfil;

    /**
     * Todos os instrumentos que o usuário toca
     * (principal + curingas). Mantém compatibilidade com o front atual.
     */
    private Set<Long> instrumentoIds = new HashSet<>();

    /**
     * Instrumento PRINCIPAL — único — usado pelo motor de escala automática.
     * Se não vier, o service assume o primeiro de instrumentoIds (backfill).
     */
    private Long instrumentoPrincipalId;

    private Set<Long> departamentoIds;

    private Long empresaId;

    public UsuarioRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Boolean disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Set<Long> getInstrumentoIds() {
        return instrumentoIds;
    }

    public void setInstrumentoIds(Set<Long> instrumentoIds) {
        this.instrumentoIds = instrumentoIds;
    }

    public Long getInstrumentoPrincipalId() {
        return instrumentoPrincipalId;
    }

    public void setInstrumentoPrincipalId(Long instrumentoPrincipalId) {
        this.instrumentoPrincipalId = instrumentoPrincipalId;
    }

    public Set<Long> getDepartamentoIds() {
        return departamentoIds;
    }

    public void setDepartamentoIds(Set<Long> departamentoIds) {
        this.departamentoIds = departamentoIds;
    }
}
