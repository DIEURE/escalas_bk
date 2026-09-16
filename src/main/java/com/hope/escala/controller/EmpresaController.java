package com.hope.escala.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hope.escala.entity.Empresa;
import com.hope.escala.service.EmpresaService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Endpoint para buscar os dados da empresa do usuário logado (usado na EmpresaPage)
    @GetMapping("/minha-empresa")
    public ResponseEntity<Empresa> buscarMinhaEmpresa() {
        Empresa empresa = empresaService.buscarEmpresaLogada();
        return ResponseEntity.ok(empresa);
    }

    // Endpoint para buscar por ID (usado no ModalEditarEmpresa)
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        Empresa empresa = empresaService.buscarPorId(id);
        return ResponseEntity.ok(empresa);
    }

    // Endpoint para atualizar os dados da empresa
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @RequestBody Empresa empresaDados) {
        Empresa empresaAtualizada = empresaService.atualizarEmpresa(id, empresaDados);
        return ResponseEntity.ok(empresaAtualizada);
    }
}
