package com.hope.escala.dto.response;

public record UsuarioVotoResponseDTO(
        Long id,
        String nome,
        
        Long opcaoEscolhidaId,
        String opcaoEscolhidaTexto
) {}