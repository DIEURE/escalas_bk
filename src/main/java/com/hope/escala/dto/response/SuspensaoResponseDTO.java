package com.hope.escala.dto.response;

 
import java.time.LocalDateTime;

public record SuspensaoResponseDTO(
        Long id,
        Long usuarioId,
        String nomeUsuario,
        Long departamentoId,
        Integer mesBloqueio,
        Integer anoBloqueio,
        String motivo,
        LocalDateTime criadoEm,
        String criadoPor,
        Boolean ativo
) {}
