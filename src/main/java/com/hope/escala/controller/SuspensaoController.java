package com.hope.escala.controller;
 

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.SuspensaoRequestDTO;
import com.hope.escala.dto.response.SuspensaoResponseDTO;
import com.hope.escala.service.SuspensaoService;

@RestController
@RequestMapping("/suspensoes-voluntarios")
public class SuspensaoController {

    private final SuspensaoService service;

    public SuspensaoController(SuspensaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SuspensaoResponseDTO> aplicar(
            @RequestBody SuspensaoRequestDTO dto,
            @RequestAttribute("empresaId") Long empresaId,
            Authentication authentication) {
        String criadoPor = authentication != null ? authentication.getName() : "Admin";
        SuspensaoResponseDTO resposta = service.aplicarSuspensao(dto, empresaId, criadoPor);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<List<SuspensaoResponseDTO>> listar(
            @RequestParam("departamentoId") Long departamentoId,
            @RequestParam("mes") Integer mes,
            @RequestParam("ano") Integer ano,
            @RequestAttribute("empresaId") Long empresaId) {
        List<SuspensaoResponseDTO> lista = service.listarPorPeriodo(departamentoId, mes, ano, empresaId);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revogar(
            @PathVariable("id") Long id,
            @RequestAttribute("empresaId") Long empresaId) {
        service.revogarSuspensao(id, empresaId);
        return ResponseEntity.noContent().build();
    }
}
