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

import com.hope.escala.dto.request.AgendaMensalRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.AgendaMensalResponseDTO;
import com.hope.escala.security.annotation.PodeGerenciarDepartamento;
import com.hope.escala.service.AgendaMensalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/agenda-mensal")
public class AgendaMensalController {

	private final AgendaMensalService service;

	public AgendaMensalController(AgendaMensalService service) {
		this.service = service;
	}

	@PostMapping
	// ❌ REMOVER: @PodeGerenciarDepartamento(parametro = "departamentoId")
	// A validação será feita no service
	public ResponseEntity<AgendaMensalResponseDTO> salvar(@Valid @RequestBody AgendaMensalRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
	}

	@GetMapping
	public ResponseEntity<List<AgendaMensalResponseDTO>> listar() {
		return ResponseEntity.ok(service.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AgendaMensalResponseDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PutMapping("/{id}")
	@PodeGerenciarDepartamento
	public ResponseEntity<AgendaMensalResponseDTO> atualizar(@PathVariable Long id,
			@Valid @RequestBody AgendaMensalRequestDTO dto) {
		return ResponseEntity.ok(service.atualizar(id, dto));
	}

	@DeleteMapping("/{id}")
	@PodeGerenciarDepartamento
	public ResponseEntity<String> inativar(@PathVariable Long id) {
		service.inativar(id);
		return ResponseEntity.ok("Agenda mensal inativada");
	}

	@PostMapping("/{id}/gerar-escalas")
	
	public ResponseEntity<String> gerarEscalasMes(@PathVariable Long id,
			@Valid @RequestBody GerarEscalasMesRequestDTO dto) {
		service.gerarEscalasMes(id, dto);
		return ResponseEntity.ok("Escalas do mês geradas com sucesso");
	}
}
