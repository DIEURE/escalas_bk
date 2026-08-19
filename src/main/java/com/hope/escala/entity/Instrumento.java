
package com.hope.escala.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "instrumentos")
public class Instrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    /*
     * Quantidade desejada por escala
     */
    private Integer quantidadeEscala = 1;

    private Boolean ativo = true;
    
    private String tipo;
    private String descricao;

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

	public Instrumento() {
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
}

