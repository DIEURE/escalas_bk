package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hope.escala.dto.response.DepartamentoResponseDTO;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.annotation.AdminOuLider;
import com.hope.escala.service.DepartamentoService;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;
    private final UsuarioRepository usuarioRepository;

    public DepartamentoController(DepartamentoService departamentoService,UsuarioRepository usuarioRepository) {
        this.departamentoService = departamentoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @AdminOuLider
    public ResponseEntity<List<DepartamentoResponseDTO>> listar(
            Authentication authentication,
            @RequestParam(name = "empresaId", required = false) Long empresaId) {

        String email = authentication.getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado"));

        List<Departamento> departamentos = departamentoService.listarPorUsuarioLogado(usuarioLogado, empresaId);

        List<DepartamentoResponseDTO> response = departamentos.stream()
                .map(DepartamentoResponseDTO::new)
                .toList();

        return ResponseEntity.ok(response);
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
