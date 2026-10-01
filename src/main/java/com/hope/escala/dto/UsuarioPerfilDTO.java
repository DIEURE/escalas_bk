package com.hope.escala.dto;

public record UsuarioPerfilDTO(
	    Long id,
	    String nome,
	    String email,
	    String telefone,
	    String perfil,
	    String empresaNome
	) {}