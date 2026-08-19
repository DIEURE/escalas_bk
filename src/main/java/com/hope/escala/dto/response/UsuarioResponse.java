package com.hope.escala.dto.response;

import java.util.Set;

import com.hope.escala.enums.PerfilUsuario;

public record UsuarioResponse(

        Long id,

        String nome,

        String email,

        String telefone,

        Boolean disponibilidade,

        Set<PerfilUsuario> perfis

) {
}