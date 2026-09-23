package com.hope.escala.dto.response;

import java.util.HashSet;
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
    private Set<String> departamentos;
    private Integer quantidade_Escala;

    // 🟢 Multi-tenant: ID e Nome da congregação
    private Long empresaId;
    private String empresaNome;

    public UsuarioResponseDTO() {
    }

    // 🟢 Construtor a partir da entidade Usuario
    public UsuarioResponseDTO(com.hope.escala.entity.Usuario usuario) {
        if (usuario != null) {
            this.id = usuario.getId();
            this.nome = usuario.getNome();
            this.email = usuario.getEmail();
            this.telefone = usuario.getTelefone();
            this.disponibilidade = usuario.getDisponibilidade();
            this.ativo = usuario.getAtivo();
            this.perfil = usuario.getPerfil();

            // Vínculo da congregação/empresa
            if (usuario.getEmpresa() != null) {
                this.empresaId = usuario.getEmpresa().getId();
                this.empresaNome = usuario.getEmpresa().getNome();
            }

            // Mapeia os IDs dos instrumentos
            if (usuario.getInstrumentos() != null) {
                this.instrumentoIds = usuario.getInstrumentos().stream()
                        .map(inst -> inst.getId())
                        .collect(Collectors.toSet());
            }

            // Mapeia os nomes dos departamentos
            if (usuario.getDepartamentos() != null) {
                this.departamentos = usuario.getDepartamentos().stream()
                        .map(dep -> dep.getNome())
                        .collect(Collectors.toSet());
            }
        }
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

    public Set<String> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(Set<String> departamentos) {
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
}
