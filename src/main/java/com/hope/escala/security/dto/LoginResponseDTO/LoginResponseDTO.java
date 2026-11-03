package com.hope.escala.security.dto.LoginResponseDTO;

import java.util.ArrayList;
import java.util.List;

import com.hope.escala.dto.response.DepartamentoResumoDTO;

public class LoginResponseDTO {

	private Long id; // <-- necessário pro front
	private String token;
	private String nome;
	private String email;
	private String perfil;
	private Long empresaId;
	private String nomeEmpresa;
	private List<DepartamentoResumoDTO> departamentos = new ArrayList<>();

	public LoginResponseDTO() {
	}

	public LoginResponseDTO(Long id, String token, String nome, String email, String perfil,
			Long empresaId, String nomeEmpresa, List<DepartamentoResumoDTO> departamentos) {
		this.id = id;
		this.token = token;
		this.nome = nome;
		this.email = email;
		this.perfil = perfil;
		this.empresaId = empresaId;
		this.nomeEmpresa = nomeEmpresa;
		this.departamentos = departamentos != null ? departamentos : new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
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

	public List<DepartamentoResumoDTO> getDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(List<DepartamentoResumoDTO> departamentos) {
		this.departamentos = departamentos != null ? departamentos : new ArrayList<>();
	}
}