
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

 // Substitua o antigo Long instrumentoId por:
    private Set<Long> instrumentoIds = new HashSet<>();
    // ou List<Long> instrumentoIds = new ArrayList<>();


    private Set<Long> departamentoIds;

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

    public void setPerfil(
            PerfilUsuario perfil
    ) {
        this.perfil = perfil;
    }

  

    public Set<Long> getInstrumentoIds() {
		return instrumentoIds;
	}

	public void setInstrumentoIds(Set<Long> instrumentoIds) {
		this.instrumentoIds = instrumentoIds;
	}

	public Set<Long> getDepartamentoIds() {
        return departamentoIds;
    }

    public void setDepartamentoIds(
            Set<Long> departamentoIds
    ) {
        this.departamentoIds = departamentoIds;
    }
}

