package com.hope.escala.dto.request;

  
public record SuspensaoRequestDTO(
        Long usuarioId,
        Long departamentoId,
        Integer mesBloqueio,
        Integer anoBloqueio,
        String motivo
) {}
