package com.hope.escala.dto;
 

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarPerfilDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    String telefone,

    // Campos opcionais para alteração de senha:
    String senhaAtual,

    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres")
    String novaSenha
) {}
