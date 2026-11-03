package com.hope.escala.dto.response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.hope.escala.enums.PerfilUsuario;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Boolean disponibilidade;
    private Boolean ativo;
    private PerfilUsuario perfil;

    private Set<Long> instrumentoIds = new HashSet<>();
    private String nomeInstrumento;
    private List<DepartamentoResumoDTO> departamentos;
    private Integer quantidade_Escala;
    
    private Long instrumentoPrincipalId;

    // 🟢 Multi-tenant: ID e Nome da congregação
    private Long empresaId;
    private String empresaNome;
    
    private Boolean elegivelEscalaAutomatica;

   

    public UsuarioResponseDTO() {
    }

   

    public UsuarioResponseDTO(Long id, String nome, String email, String telefone, Boolean disponibilidade,
			Boolean ativo, PerfilUsuario perfil, Set<Long> instrumentoIds, String nomeInstrumento,
			List<DepartamentoResumoDTO> departamentos, Integer quantidade_Escala, Long instrumentoPrincipalId,
			Long empresaId, String empresaNome, Boolean elegivelEscalaAutomatica) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.telefone = telefone;
		this.disponibilidade = disponibilidade;
		this.ativo = ativo;
		this.perfil = perfil;
		this.instrumentoIds = instrumentoIds;
		this.nomeInstrumento = nomeInstrumento;
		this.departamentos = departamentos;
		this.quantidade_Escala = quantidade_Escala;
		this.instrumentoPrincipalId = instrumentoPrincipalId;
		this.empresaId = empresaId;
		this.empresaNome = empresaNome;
		this.elegivelEscalaAutomatica = elegivelEscalaAutomatica;
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

    public void setDisponibilidade(Boolean disponibilidade) {
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

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
    
    public Long getInstrumentoPrincipalId() {
        return instrumentoPrincipalId;
    }

    public void setInstrumentoPrincipalId(Long instrumentoPrincipalId) {
        this.instrumentoPrincipalId = instrumentoPrincipalId;
    }

    public Set<Long> getInstrumentoIds() {
        return instrumentoIds;
    }

    public void setInstrumentoIds(Set<Long> instrumentoIds) {
        this.instrumentoIds = instrumentoIds;
    }

    public String getNomeInstrumento() {
        return nomeInstrumento;
    }

    public void setNomeInstrumento(String nomeInstrumento) {
        this.nomeInstrumento = nomeInstrumento;
    }

    

    public List<DepartamentoResumoDTO> getDepartamentos() {
		return departamentos;
	}



	public void setDepartamentos(List<DepartamentoResumoDTO> departamentos) {
		this.departamentos = departamentos;
	}



	public Integer getQuantidade_Escala() {
        return quantidade_Escala;
    }

    public void setQuantidade_Escala(Integer quantidade_Escala) {
        this.quantidade_Escala = quantidade_Escala;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }
    
    public Boolean getElegivelEscalaAutomatica() {
        return elegivelEscalaAutomatica;
    }

    public void setElegivelEscalaAutomatica(Boolean elegivelEscalaAutomatica) {
        this.elegivelEscalaAutomatica = elegivelEscalaAutomatica;
    }
}
