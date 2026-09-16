package com.hope.escala.security.dto.LoginResponseDTO;

public class LoginResponseDTO {

	private String token;
	private String nome;
	private String email;
	private String perfil;
	private Long empresaId; // <-- Novo campo
	private String nomeEmpresa; // <-- Novo campo para mostrar na interface

	public LoginResponseDTO() {
	}

	 

	public LoginResponseDTO(String token, String nome, String email, String perfil, Long empresaId,
			String nomeEmpresa) {
		super();
		this.token = token;
		this.nome = nome;
		this.email = email;
		this.perfil = perfil;
		this.empresaId = empresaId;
		this.nomeEmpresa = nomeEmpresa;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
}
