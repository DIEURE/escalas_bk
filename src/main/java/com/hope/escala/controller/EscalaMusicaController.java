package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.SubstituicaoEscalaMusicaDTO;
import com.hope.escala.dto.request.EscalaMusicaRequestDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.security.annotation.PodeGerenciarDepartamento;
import com.hope.escala.service.EscalaMusicaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/escala-musicas")
public class EscalaMusicaController {

    private final EscalaMusicaService escalaMusicaService;

    public EscalaMusicaController(EscalaMusicaService escalaMusicaService) {
        this.escalaMusicaService = escalaMusicaService;
    }

    @PostMapping
    @PodeGerenciarDepartamento(parametro = "escalaId", tipo = "escala")
    public ResponseEntity<EscalaMusicaResponseDTO> salvar(
            @Valid @RequestBody EscalaMusicaRequestDTO dto) {
        EscalaMusicaResponseDTO escalaMusicaSalva = escalaMusicaService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(escalaMusicaSalva);
    }

    @GetMapping("/escala/{escalaId}")
    @PodeGerenciarDepartamento(parametro = "escalaId", tipo = "escala")
    public ResponseEntity<List<EscalaMusicaResponseDTO>> listarPorEscala(
            @PathVariable Long escalaId) {
        List<EscalaMusicaResponseDTO> repertorio = 
            escalaMusicaService.listarPorEscala(escalaId);
        return ResponseEntity.ok(repertorio);
    }

    @PatchMapping("/{id}/substituir")
    @PodeGerenciarDepartamento  // ← Protegido
    public ResponseEntity<EscalaMusicaResponseDTO> substituirMusica(
            @PathVariable Long id,
            @Valid @RequestBody SubstituicaoEscalaMusicaDTO dto) {
        EscalaMusicaResponseDTO escalaMusicaAtualizada = 
            escalaMusicaService.substituirMusica(id, dto);
        return ResponseEntity.ok(escalaMusicaAtualizada);
    }
}