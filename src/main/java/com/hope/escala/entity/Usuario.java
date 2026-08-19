
package com.hope.escala.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hope.escala.enums.PerfilUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String nome;

	@Column(unique = true)
	@Email
	private String email;

	private String telefone;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String senha;
	
	@ManyToMany
	@JoinTable(
	        name = "usuario_departamentos",

	        joinColumns = @JoinColumn(name = "usuario_id"),

	        inverseJoinColumns = @JoinColumn(name = "departamento_id")
	)

	private Set<Departamento> departamentos =
	        new HashSet<>();

	private Boolean disponibilidade;

	private Boolean ativo = true;

	private String observacao;

	private LocalDateTime dataCadastro;

	private LocalDateTime dataAtualizacao;

	private LocalDateTime dataInativacao;

	private LocalDateTime ultimoLogin;

 

	@ManyToOne
	@JoinColumn(name = "instrumento_id")
	private Instrumento instrumento;

	public PerfilUsuario getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilUsuario perfil) {
		this.perfil = perfil;
	}

	@Enumerated(EnumType.STRING) private PerfilUsuario perfil;

	public Usuario() {
	}

	@PrePersist
	public void prePersist() {
		this.dataCadastro = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.dataAtualizacao = LocalDateTime.now();
	}

	public Long getId() {
		return id;
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

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public LocalDateTime getDataInativacao() {
		return dataInativacao;
	}

	public void setDataInativacao(LocalDateTime dataInativacao) {
		this.dataInativacao = dataInativacao;
	}

	public LocalDateTime getUltimoLogin() {
		return ultimoLogin;
	}

	public void setUltimoLogin(LocalDateTime ultimoLogin) {
		this.ultimoLogin = ultimoLogin;
	}

 

	public Instrumento getInstrumento() {
		return instrumento;
	}

	public void setInstrumento(Instrumento instrumento) {
		this.instrumento = instrumento;
	}
	
	public Set<Departamento> getDepartamentos() {
	    return departamentos;
	}

	public void setDepartamentos(
	        Set<Departamento> departamentos
	) {
	    this.departamentos = departamentos;
	}
}
