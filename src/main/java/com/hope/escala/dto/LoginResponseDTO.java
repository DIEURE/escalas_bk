package com.hope.escala.dto;

public record LoginResponseDTO(
	    String token,
	    String tipo,
	    Long id,
	    String nome,
	    String email,
	    String perfil,
	    Long empresaId,
	    String empresaNome
	) {}