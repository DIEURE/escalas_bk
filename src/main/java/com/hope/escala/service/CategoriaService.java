package com.hope.escala.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hope.escala.entity.Categoria;
import com.hope.escala.entity.Empresa;
import com.hope.escala.repository.CategoriaRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class CategoriaService {

	@Autowired
	private final CategoriaRepository categoriaRepository;
	@Autowired
	private final EmpresaRepository empresaRepository;
	@Autowired
	private final SecurityUtils securityUtils;

	public CategoriaService(CategoriaRepository categoriaRepository, EmpresaRepository empresaRepository,
			SecurityUtils securityUtils) {
		this.categoriaRepository = categoriaRepository;
		this.empresaRepository = empresaRepository;
		this.securityUtils = securityUtils;
	}

	public List<Categoria> listarPorEmpresa() {
		return categoriaRepository.findByEmpresaId(securityUtils.empresaId());
	}

	public List<Categoria> listarTodas() {
		return categoriaRepository.findAll();
	}

	public List<Categoria> listarAtivas() {
		return categoriaRepository.findByAtivoTrue();
	}

	public Optional<Categoria> buscarPorId(Long id) {
		return categoriaRepository.findById(id);
	}

	public Categoria salvar(Categoria categoria) {
		Empresa empresa = empresaRepository.findById(securityUtils.empresaId())
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

		if (categoria.getAtivo() == null) {
			categoria.setAtivo(true);
		}
		categoria.setEmpresa(empresa);
		return categoriaRepository.save(categoria);
	}

	public Categoria atualizar(Long id, Categoria categoriaAtualizada) {
		return categoriaRepository.findById(id).map(categoria -> {
			categoria.setNome(categoriaAtualizada.getNome());
			if (categoriaAtualizada.getAtivo() != null) {
				categoria.setAtivo(categoriaAtualizada.getAtivo());
			}
			return categoriaRepository.save(categoria);
		}).orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + id));
	}

	public void deletar(Long id) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + id));

		// Exclusão lógica (soft delete) recomendada para manter integridade com músicas
		categoria.setAtivo(false);
		categoriaRepository.save(categoria);

		// Ou se preferir exclusão física:
		// categoriaRepository.deleteById(id);
	}
}
