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
import org.springframework.web.bind.annotation.PatchMapping;

import com.hope.escala.dto.request.DepartamentoRequestDTO;
import com.hope.escala.dto.response.DepartamentoResponseDTO;
 
import com.hope.escala.service.DepartamentoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService service;

    public DepartamentoController(DepartamentoService service) {
        this.service = service;
    }

    @PostMapping     
    public ResponseEntity<DepartamentoResponseDTO> salvar(
            @Valid @RequestBody DepartamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    
    public ResponseEntity<DepartamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DepartamentoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
     
    public ResponseEntity<String> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.ok("Departamento inativado");
    }
    
    @GetMapping("/inativos")
    public ResponseEntity<List<DepartamentoResponseDTO>> listarInativos() {
        return ResponseEntity.ok(service.listarInativos());
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<DepartamentoResponseDTO> ativar(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.ativar(id));
    }
}