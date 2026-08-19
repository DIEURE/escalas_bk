package com.hope.escala.service;
 

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.DepartamentoRequestDTO;
import com.hope.escala.dto.response.DepartamentoResponseDTO;
import com.hope.escala.entity.Departamento;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class DepartamentoService {

	  
	     
	    private final SecurityUtils securityUtils;
		private DepartamentoRepository repository;

	public DepartamentoService(DepartamentoRepository repository,SecurityUtils securityUtils) {
		 this.securityUtils = securityUtils;
		this.repository = repository;
	}

	public DepartamentoResponseDTO salvar(DepartamentoRequestDTO dto) {
	    
	    // ← APENAS ADMIN pode criar departamentos
	    if (!securityUtils.isAdmin()) {
	        throw new RuntimeException("Apenas administradores podem criar departamentos.");
	    }

	    Departamento departamento = new Departamento();
	    departamento.setNome(dto.getNome());
	    departamento.setAtivo(true);  // ← Ativo por padrão
	    
	    Departamento salvo = repository.save(departamento);
	    
	    return converterParaDTO(salvo);
	}

	public List<DepartamentoResponseDTO> listar() {

		return repository.findByAtivoTrue().stream().map(this::converterParaDTO).collect(Collectors.toList());
	}

	public DepartamentoResponseDTO buscarPorId(Long id) {

		Departamento departamento = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		return converterParaDTO(departamento);
	}

	public DepartamentoResponseDTO atualizar(Long id, DepartamentoRequestDTO dto) {

		Departamento departamento = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		departamento.setNome(dto.getNome());

		Departamento atualizado = repository.save(departamento);

		return converterParaDTO(atualizado);
	}

	public void inativar(Long id) {

		Departamento departamento = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		departamento.setAtivo(false);

		repository.save(departamento);
	}

	public List<DepartamentoResponseDTO> listarInativos() {
	    return repository.findByAtivoFalse()
	            .stream()
	            .map(this::converterParaDTO)
	            .collect(Collectors.toList());
	}

	public DepartamentoResponseDTO ativar(Long id) {
	    if (!securityUtils.isAdmin()) {
	        throw new RuntimeException(
	            "Apenas administradores podem ativar departamentos."
	        );
	    }

	    Departamento departamento = repository.findById(id)
	            .orElseThrow(() ->
	                new RuntimeException("Departamento não encontrado")
	            );

	    departamento.setAtivo(true);

	    Departamento atualizado = repository.save(departamento);

	    return converterParaDTO(atualizado);
	}
	
	private DepartamentoResponseDTO converterParaDTO(Departamento departamento) {

		DepartamentoResponseDTO dto = new DepartamentoResponseDTO();

		dto.setId(departamento.getId());

		dto.setNome(departamento.getNome());

		dto.setAtivo(departamento.getAtivo());

		return dto;
	}
}