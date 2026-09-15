package com.hope.escala.security.dto.LoginResponseDTO;

public class LoginResponseDTO {

	private String token;

	private String nome;

	private String perfil;

	public LoginResponseDTO() {
	}

	public LoginResponseDTO(String token, String nome, String perfil) {

		this.token = token;
		this.nome = nome;
		this.perfil = perfil;
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
