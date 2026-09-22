package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.UsuarioDisponibilidadeDTO;
import com.hope.escala.dto.request.UsuarioRequestDTO;
import com.hope.escala.dto.response.UsuarioResponseDTO;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.AdminOuLider;
import com.hope.escala.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;
    public UsuarioController(UsuarioService service,SecurityUtils securityUtils, UsuarioRepository usuarioRepository) {
        this.service = service;
        this.securityUtils = securityUtils;
        this.usuarioRepository = usuarioRepository;
    }

    @AdminOuLider // 🟢 Garante que apenas Admin ou Líder possa criar usuários diretamente
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> salvar(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @AdminOuLider
    @GetMapping    
    public ResponseEntity<List<UsuarioResponseDTO>> listar(
            @RequestParam(name = "empresaId", required = false) Long empresaId) {
        return ResponseEntity.ok(service.listar(empresaId));
    }


    @GetMapping("/{id}")     
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @AdminOuLider // 🟢 Apenas Admin ou Líder pode atualizar dados de usuários
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    // 🟢 Endpoint para listar usuários pendentes de aprovação (Admin/Líder)
 // 🟢 Endpoint para listar usuários pendentes de aprovação (Admin/Líder)
    @GetMapping("/pendentes")
    @AdminOuLider
    public ResponseEntity<List<UsuarioResponseDTO>> listarPendentes(
            @RequestParam(name = "empresaId", required = false) Long empresaId) {

        return ResponseEntity.ok(service.listarPendentes(empresaId));
    }

    // 🟢 Endpoint para aprovar e ativar o usuário (Admin/Líder)
    @AdminOuLider
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<UsuarioResponseDTO> aprovar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.aprovar(id, dto));
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<UsuarioResponseDTO> atualizarDisponibilidade(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDisponibilidadeDTO dto) {
        return ResponseEntity.ok(service.atualizarDisponibilidade(id, dto.disponibilidade));
    }
    
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorDepartamento(@PathVariable Long departamentoId) {
        return ResponseEntity.ok(service.buscarPorDepartamento(departamentoId));
    }

    @AdminOuLider // 🟢 Apenas Admin ou Líder pode inativar usuários
    @DeleteMapping("/{id}")
    public ResponseEntity<String> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.ok("Usuário inativado");
    }
    
    @GetMapping("/meu-perfil")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuPerfil() {
        Long usuarioIdLogado = securityUtils.usuarioId(); // ou extraído do token
        return ResponseEntity.ok(service.buscarPorId(usuarioIdLogado));
    }

    @PutMapping("/meu-perfil")
    public ResponseEntity<UsuarioResponseDTO> atualizarMeuPerfil(@Valid @RequestBody UsuarioRequestDTO dto) {
        Long usuarioIdLogado = securityUtils.usuarioId();
        return ResponseEntity.ok(service.atualizar(usuarioIdLogado, dto));
    }

}
