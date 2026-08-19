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

import com.hope.escala.dto.request.InstrumentoRequestDTO;
import com.hope.escala.dto.response.InstrumentoResponse;
 
import com.hope.escala.service.InstrumentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/instrumentos")
public class InstrumentoController {

    private final InstrumentoService service;

    public InstrumentoController(InstrumentoService service) {
        this.service = service;
    }

    @PostMapping
    
    public ResponseEntity<InstrumentoResponse> criar(
            @RequestBody @Valid InstrumentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(request));
    }

    @GetMapping
    // ✅ Público - todos podem listar
    public ResponseEntity<List<InstrumentoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    // ✅ Público - todos podem ver detalhe
    public ResponseEntity<InstrumentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    
    public ResponseEntity<InstrumentoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid InstrumentoRequestDTO request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
   
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}