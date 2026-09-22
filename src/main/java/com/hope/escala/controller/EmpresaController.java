package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hope.escala.dto.response.EmpresaResponseDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.security.annotation.AdminOuLider;
import com.hope.escala.security.annotation.SomenteSuperAdmin;
import com.hope.escala.service.EmpresaService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

 
    @GetMapping
    @SomenteSuperAdmin
    public ResponseEntity<List<Empresa>> listarTodas() {
        List<Empresa> empresas = empresaService.listarTodas(); // ou empresaRepository.findAll()
        return ResponseEntity.ok(empresas);
    }

    
    @PostMapping
    @SomenteSuperAdmin
    public ResponseEntity<Empresa> criar(@RequestBody Empresa empresa) {
        Empresa novaEmpresa = empresaService.criarEmpresa(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaEmpresa);
    }

  
    @PutMapping("/minha-empresa")
    @AdminOuLider
    public ResponseEntity<EmpresaResponseDTO> atualizarMinhaEmpresa(@RequestBody Empresa dados) {
        EmpresaResponseDTO dto = empresaService.atualizarEmpresaLogada(dados);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @SomenteSuperAdmin
    public ResponseEntity<Empresa> atualizar(@PathVariable Long id, @RequestBody Empresa empresaDados) {
        Empresa empresaAtualizada = empresaService.atualizarEmpresa(id, empresaDados);
        return ResponseEntity.ok(empresaAtualizada);
    }
     
    @GetMapping("/empresas-publicas")
    public ResponseEntity<List<Empresa>> listarEmpresasPublicas() {
        List<Empresa> empresas = empresaService.listarEmpresasParaCadastro(); 
        return ResponseEntity.ok(empresas);
    }

     
    @GetMapping("/minha-empresa")
    @AdminOuLider
    public ResponseEntity<EmpresaResponseDTO> buscarMinhaEmpresa() {
        EmpresaResponseDTO dto = empresaService.buscarEmpresaLogadaDTO();
        return ResponseEntity.ok(dto);
    }

     
    @GetMapping("/{id}")
    @SomenteSuperAdmin
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        Empresa empresa = empresaService.buscarPorId(id);
        return ResponseEntity.ok(empresa);
    }

   

}
