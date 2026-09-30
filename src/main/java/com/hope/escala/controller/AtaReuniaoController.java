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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.CriarAtaRequestDTO;
import com.hope.escala.dto.response.AtaDetalheResponseDTO;
import com.hope.escala.service.AtaReuniaoService;

@RestController
@RequestMapping("/atas")
public class AtaReuniaoController {

    private final AtaReuniaoService ataService;

    public AtaReuniaoController(AtaReuniaoService ataService) {
        this.ataService = ataService;
    }

    @PostMapping
    public ResponseEntity<AtaDetalheResponseDTO> criarAta(@RequestBody CriarAtaRequestDTO dto) {
        AtaDetalheResponseDTO resposta = ataService.criarAta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtaDetalheResponseDTO> buscarPorId(@PathVariable Long id) {
        AtaDetalheResponseDTO resposta = ataService.buscarPorIdComDetalhes(id);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<List<AtaDetalheResponseDTO>> listarAtas(
            @RequestParam(required = false) Long departamentoId) {
        List<AtaDetalheResponseDTO> atas = ataService.listarAtas(departamentoId);
        return ResponseEntity.ok(atas);
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizarAta(@PathVariable Long id) {
        ataService.finalizarAta(id);
        return ResponseEntity.noContent().build();
    }
}
