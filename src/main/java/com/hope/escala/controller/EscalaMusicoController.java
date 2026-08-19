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

import com.hope.escala.dto.ConfirmacaoEscalaMusicoDTO;
import com.hope.escala.dto.SubstituicaoEscalaMusicoDTO;
import com.hope.escala.dto.request.EscalaMusicoRequestDTO;
import com.hope.escala.dto.response.EscalaMusicoResponseDTO;
import com.hope.escala.service.EscalaMusicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/escala-musicos")
public class EscalaMusicoController {

    private final EscalaMusicoService escalaMusicoService;

    public EscalaMusicoController(EscalaMusicoService escalaMusicoService) {
        this.escalaMusicoService = escalaMusicoService;
    }

    @PostMapping
     
    public ResponseEntity<EscalaMusicoResponseDTO> salvar(
            @Valid @RequestBody EscalaMusicoRequestDTO dto) {
        EscalaMusicoResponseDTO escalaMusicoSalvo = escalaMusicoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(escalaMusicoSalvo);
    }

    @GetMapping("/escala/{escalaId}")
     
    public ResponseEntity<List<EscalaMusicoResponseDTO>> listarPorEscala(
            @PathVariable Long escalaId) {
        List<EscalaMusicoResponseDTO> escalaMusicos = 
            escalaMusicoService.listarPorEscala(escalaId);
        return ResponseEntity.ok(escalaMusicos);
    }
    
    @GetMapping("/minhas-escalas")
    public ResponseEntity<List<EscalaMusicoResponseDTO>> listarMinhasEscalas() {
        return ResponseEntity.ok(escalaMusicoService.listarMinhasEscalas());
    }
    
    @PatchMapping("/minhas-escalas/{escalaId}/confirmacao")
    public ResponseEntity<EscalaMusicoResponseDTO> confirmarMinhaEscala(
            @PathVariable Long escalaId,
            @RequestBody ConfirmacaoEscalaMusicoDTO dto) {
        // Você precisará criar o método confirmarMinhaEscala no seu EscalaMusicoService
        return ResponseEntity.ok(escalaMusicoService.confirmarMinhaEscala(escalaId, dto.getConfirmado()));
    }
    

    @PatchMapping("/{id}/confirmacao")
    // ✅ Qualquer usuário pode confirmar sua própria participação
    public ResponseEntity<EscalaMusicoResponseDTO> atualizarConfirmacao(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmacaoEscalaMusicoDTO dto) {
        EscalaMusicoResponseDTO escalaMusicoAtualizado = 
            escalaMusicoService.atualizarConfirmacao(id, dto);
        return ResponseEntity.ok(escalaMusicoAtualizado);
    }

    
    @PatchMapping("/{id}/substituir")    
    public ResponseEntity<EscalaMusicoResponseDTO> substituirMusico(
            @PathVariable Long id,
            @Valid @RequestBody SubstituicaoEscalaMusicoDTO dto) {
        EscalaMusicoResponseDTO escalaMusicoAtualizado = 
            escalaMusicoService.substituirMusico(id, dto);
        return ResponseEntity.ok(escalaMusicoAtualizado);
    }
}