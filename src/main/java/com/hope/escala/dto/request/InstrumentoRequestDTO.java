package com.hope.escala.dto.request;

 

import jakarta.validation.constraints.NotBlank;

public record InstrumentoRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Tipo é obrigatório")
        String tipo,

        String descricao,
        
        Integer quantidade_escala,
        
        Boolean ativo
) {
}