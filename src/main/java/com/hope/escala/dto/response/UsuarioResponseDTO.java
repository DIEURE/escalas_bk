
package com.hope.escala.dto.response;

import java.util.Set;

import com.hope.escala.enums.PerfilUsuario;

public class UsuarioResponseDTO {

    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private Boolean disponibilidade;

    private Boolean ativo;

    private PerfilUsuario perfil;

    private Long instrumentoId;

    private String nomeInstrumento;

    private Set<String> departamentos;

    public UsuarioResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(
            Boolean disponibilidade
    ) {
        this.disponibilidade = disponibilidade;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(
            PerfilUsuario perfil
    ) {
        this.perfil = perfil;
    }

    public Long getInstrumentoId() {
        return instrumentoId;
    }

    public void setInstrumentoId(
            Long instrumentoId
    ) {
        this.instrumentoId = instrumentoId;
    }

    public String getNomeInstrumento() {
        return nomeInstrumento;
    }

    public void setNomeInstrumento(
            String nomeInstrumento
    ) {
        this.nomeInstrumento = nomeInstrumento;
    }

    public Set<String> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(
            Set<String> departamentos
    ) {
        this.departamentos = departamentos;
    }
}

