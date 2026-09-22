package com.hope.escala.controller;

import com.hope.escala.entity.Departamento;
import com.hope.escala.security.annotation.AdminOuLider;
import com.hope.escala.service.DepartamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> listar() {
        return ResponseEntity.ok(departamentoService.listarPorEmpresaLogada());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Departamento>> listarAtivos() {
        return ResponseEntity.ok(departamentoService.listarAtivosPorEmpresaLogada());
    }

    @PostMapping
    @AdminOuLider
    public ResponseEntity<Departamento> criar(@RequestBody Departamento dep) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departamentoService.salvar(dep));
    }

    @PutMapping("/{id}")
    @AdminOuLider
    public ResponseEntity<Departamento> atualizar(@PathVariable Long id, @RequestBody Departamento dep) {
        return ResponseEntity.ok(departamentoService.atualizar(id, dep));
    }

    @PatchMapping("/{id}/status")
    @AdminOuLider
    public ResponseEntity<Void> alternarStatus(@PathVariable Long id) {
        departamentoService.alternarStatus(id);
        return ResponseEntity.noContent().build();
    }
}
