package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hope.escala.dto.response.EmpresaResponseDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.service.EmpresaService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }
    
    
 // Endpoint para criar uma nova empresa/igreja (usado no ModalEmpresaForm)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empresa> criar(@RequestBody Empresa empresa) {
        Empresa novaEmpresa = empresaService.criarEmpresa(empresa);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(novaEmpresa);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Empresa>> listarTodas() {
        List<Empresa> empresas = empresaService.listar(); // ou o método correspondente no seu service
        return ResponseEntity.ok(empresas);
    }
    
    @GetMapping("/empresas-publicas")
    public ResponseEntity<List<Empresa>> listarEmpresas() {
    	  List<Empresa> empresas = empresaService.listarEmpresasParaCadastro(); 
    	return ResponseEntity.ok(empresas);
		 
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
