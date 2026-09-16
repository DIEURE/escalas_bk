package com.hope.escala.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.DepartamentoRequestDTO;
import com.hope.escala.dto.response.DepartamentoResponseDTO;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class DepartamentoService {

	private final DepartamentoRepository departamentoRepository;
	private final EmpresaRepository empresaRepository;
	private final SecurityUtils securityUtils;

	public DepartamentoService(DepartamentoRepository departamentoRepository, EmpresaRepository empresaRepository,
			SecurityUtils securityUtils) {
		this.departamentoRepository = departamentoRepository;
		this.empresaRepository = empresaRepository;
		this.securityUtils = securityUtils;
	}

	public DepartamentoResponseDTO salvar(DepartamentoRequestDTO dto) {

		// ← APENAS ADMIN pode criar departamentos
		if (!securityUtils.isAdmin()) {
			throw new RuntimeException("Apenas administradores podem criar departamentos.");
		}

		Empresa empresa = empresaRepository.findById(securityUtils.empresaId())
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

		Departamento departamento = new Departamento();

		departamento.setNome(dto.getNome());
		departamento.setAtivo(true); // ← Ativo por padrão
		departamento.setEmpresa(empresa);

		Departamento salvo = departamentoRepository.save(departamento);

		return converterParaDTO(salvo);
	}

	public List<DepartamentoResponseDTO> listar() {

		return departamentoRepository.findByAtivoTrue().stream().map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public List<Departamento> listarPorEmpresa() {
		return departamentoRepository.findByEmpresaId(securityUtils.empresaId());
	}

	public DepartamentoResponseDTO buscarPorId(Long id) {

		Departamento departamento = departamentoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		return converterParaDTO(departamento);
	}

	public DepartamentoResponseDTO atualizar(Long id, DepartamentoRequestDTO dto) {

		Departamento departamento = departamentoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		departamento.setNome(dto.getNome());

		Departamento atualizado = departamentoRepository.save(departamento);

		return converterParaDTO(atualizado);
	}

	public void inativar(Long id) {

		Departamento departamento = departamentoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		departamento.setAtivo(false);

		departamentoRepository.save(departamento);
	}

	public List<DepartamentoResponseDTO> listarInativos() {
		return departamentoRepository.findByAtivoFalse().stream().map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public DepartamentoResponseDTO ativar(Long id) {
		if (!securityUtils.isAdmin()) {
			throw new RuntimeException("Apenas administradores podem ativar departamentos.");
		}

		Departamento departamento = departamentoRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		departamento.setAtivo(true);

		Departamento atualizado = departamentoRepository.save(departamento);

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