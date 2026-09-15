package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.InstrumentoRequestDTO;
import com.hope.escala.dto.response.InstrumentoResponse;
import com.hope.escala.entity.Instrumento;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class InstrumentoService {

	private final InstrumentoRepository repository;
	private final SecurityUtils securityUtils;
	
	public InstrumentoService(InstrumentoRepository repository, SecurityUtils securityUtils) {
		this.repository = repository;
		this.securityUtils = securityUtils;
	}

	public InstrumentoResponse salvar(InstrumentoRequestDTO request) {
		
		// ← APENAS ADMIN pode criar departamentos
	    if (!securityUtils.isAdmin()) {
	        throw new RuntimeException("Apenas administradores podem criar departamentos.");
	    }

		Instrumento instrumento = new Instrumento();

		instrumento.setNome(request.nome());
		instrumento.setTipo(request.tipo());
		instrumento.setDescricao(request.descricao());
		instrumento.setQuantidadeEscala(request.quantidade_escala());
		instrumento.setAtivo(request.ativo());
		
		Instrumento salvo = repository.save(instrumento);

		return converterResponse(salvo);
	}

	public List<InstrumentoResponse> listar() {

		return repository.findAll().stream().map(this::converterResponse).toList();
	}

	public InstrumentoResponse buscarPorId(Long id) {

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		return converterResponse(instrumento);
	}

	public InstrumentoResponse atualizar(Long id, InstrumentoRequestDTO request) {
		
		// ← Validação de segurança
        if (!securityUtils.isAdmin()) {
            throw new RuntimeException("Apenas administradores podem atualizar instrumentos");
        }

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		instrumento.setNome(request.nome());
		instrumento.setTipo(request.tipo());
		instrumento.setDescricao(request.descricao());
		instrumento.setQuantidadeEscala(request.quantidade_escala());
		instrumento.setAtivo(request.ativo());
		
		Instrumento atualizado = repository.save(instrumento);

		return converterResponse(atualizado);
	}

	public void deletar(Long id) {
		
		  // ← Validação de segurança
        if (!securityUtils.isAdmin()) {
            throw new RuntimeException("Apenas administradores podem deletar instrumentos");
        }

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		repository.delete(instrumento);
	}

	private InstrumentoResponse converterResponse(Instrumento instrumento) {

		return new InstrumentoResponse(instrumento.getId(), instrumento.getNome(), instrumento.getTipo(),
				instrumento.getDescricao(), instrumento.getQuantidadeEscala(), instrumento.getAtivo());
	}
}