
package com.hope.escala.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(
	    name = "instrumentos", // Certifique-se de que é o mesmo nome da sua tabela
	    uniqueConstraints = {
	        @UniqueConstraint(
	            name = "uk_instrumento_nome_empresa", 
	            columnNames = {"nome", "empresa_id"} // Nomes exatos das colunas no banco
	        )
	    }
	)
public class Instrumento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome; // Ex: Teclado, Ministro, Back-Vocal
	private Integer quantidadeEscala = 1;
	private Boolean ativo = true;
	private String tipo;
	private String descricao;

	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
	 
    private Empresa empresa;

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Instrumento() {
	}

	// Getters e Setters normais (sem getUsuario/setUsuario)
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
	// ... demais getters e setters

	public Integer getQuantidadeEscala() {
		return quantidadeEscala;
	}

	public void setQuantidadeEscala(Integer quantidadeEscala) {
		this.quantidadeEscala = quantidadeEscala;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
