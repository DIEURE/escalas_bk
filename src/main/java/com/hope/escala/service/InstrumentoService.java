package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.InstrumentoRequestDTO;
import com.hope.escala.dto.response.InstrumentoResponse;
import com.hope.escala.entity.Empresa;
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
		
		// APENAS ADMIN pode criar instrumentos
	    if (!securityUtils.isAdmin()) {
	        throw new RuntimeException("Apenas administradores podem criar instrumentos.");
	    }

		Long empresaIdLogada = securityUtils.empresaId();

		Instrumento instrumento = new Instrumento();
		instrumento.setNome(request.nome());
		instrumento.setTipo(request.tipo());
		instrumento.setDescricao(request.descricao());
		instrumento.setQuantidadeEscala(request.quantidade_escala());
		instrumento.setAtivo(request.ativo());
		
		// 🟢 Associa a empresa logada ao instrumento (Multi-Tenant)
		Empresa empresa = new Empresa();
		empresa.setId(empresaIdLogada);
		instrumento.setEmpresa(empresa);
		
		Instrumento salvo = repository.save(instrumento);

		return converterResponse(salvo);
	}

	public List<InstrumentoResponse> listar() {
		Long empresaIdLogada = securityUtils.empresaId();
		// Certifique-se de ter o método findByEmpresaId no InstrumentoRepository
		return repository.findByEmpresaId(empresaIdLogada).stream().map(this::converterResponse).toList();
	}

	public InstrumentoResponse buscarPorId(Long id) {
		Long empresaIdLogada = securityUtils.empresaId();

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		// 🟢 Valida se o instrumento pertence à instituição logada
		if (!instrumento.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Instrumento não pertence à sua instituição");
		}

		return converterResponse(instrumento);
	}

	public InstrumentoResponse atualizar(Long id, InstrumentoRequestDTO request) {
		
        if (!securityUtils.isAdmin()) {
            throw new RuntimeException("Apenas administradores podem atualizar instrumentos");
        }

		Long empresaIdLogada = securityUtils.empresaId();

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		if (!instrumento.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Instrumento não pertence à sua instituição");
		}

		instrumento.setNome(request.nome());
		instrumento.setTipo(request.tipo());
		instrumento.setDescricao(request.descricao());
		instrumento.setQuantidadeEscala(request.quantidade_escala());
		instrumento.setAtivo(request.ativo());
		
		Instrumento atualizado = repository.save(instrumento);

		return converterResponse(atualizado);
	}

	public void deletar(Long id) {
        if (!securityUtils.isAdmin()) {
            throw new RuntimeException("Apenas administradores podem deletar instrumentos");
        }

		Long empresaIdLogada = securityUtils.empresaId();

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		if (!instrumento.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Instrumento não pertence à sua instituição");
		}

		repository.delete(instrumento);
	}

	private InstrumentoResponse converterResponse(Instrumento instrumento) {
		return new InstrumentoResponse(instrumento.getId(), instrumento.getNome(), instrumento.getTipo(),
				instrumento.getDescricao(), instrumento.getQuantidadeEscala(), instrumento.getAtivo());
	}
}
