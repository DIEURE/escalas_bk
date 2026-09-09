package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.MusicaRequestDTO;
import com.hope.escala.dto.response.MusicaResponseDTO;
import com.hope.escala.security.annotation.PodeSerAdmin;
import com.hope.escala.service.MusicaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaService musicaService;

    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }

    @PostMapping
    @PodeSerAdmin  // ← Apenas ADMIN pode criar músicas
    public ResponseEntity<MusicaResponseDTO> salvar(
            @Valid @RequestBody MusicaRequestDTO dto) {
        MusicaResponseDTO musicaSalva = musicaService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(musicaSalva);
    }

    @PutMapping("/{id}")
    @PodeSerAdmin  // ← Apenas ADMIN pode atualizar músicas
    public ResponseEntity<MusicaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MusicaRequestDTO dto) {
        MusicaResponseDTO musicaAtualizada = musicaService.atualizar(id, dto);
        return ResponseEntity.ok(musicaAtualizada);
    }

    @GetMapping
    // ✅ Público - todos podem listar músicas
    public ResponseEntity<List<MusicaResponseDTO>> listar() {
        List<MusicaResponseDTO> musicas = musicaService.listarTodas();
        return ResponseEntity.ok(musicas);
    }

    @GetMapping("/{id}")
    // ✅ Público - todos podem ver detalhes de uma música
    public ResponseEntity<MusicaResponseDTO> buscarPorId(@PathVariable Long id) {
        MusicaResponseDTO musica = musicaService.buscarPorId(id);
        return ResponseEntity.ok(musica);
    }

    @DeleteMapping("/{id}")
    @PodeSerAdmin  // ← Apenas ADMIN pode deletar músicas
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        musicaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}