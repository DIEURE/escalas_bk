package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.InstrumentoRequestDTO;
import com.hope.escala.dto.response.InstrumentoResponse;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Instrumento;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class InstrumentoService {

	private final InstrumentoRepository repository;
	private final EmpresaRepository empresaRepository;
	private final SecurityUtils securityUtils;

	public InstrumentoService(InstrumentoRepository repository, EmpresaRepository empresaRepository, SecurityUtils securityUtils) {
		this.repository = repository;
		this.empresaRepository = empresaRepository;
		this.securityUtils = securityUtils;
	}

	public InstrumentoResponse salvar(InstrumentoRequestDTO request) {
		boolean isSuperAdmin = securityUtils.isSuperAdmin();
		boolean isAdmin = securityUtils.isAdmin();

		// Apenas ADMIN ou SUPER_ADMIN podem criar instrumentos
		if (!isSuperAdmin && !isAdmin) {
			throw new RuntimeException("Apenas administradores podem criar instrumentos.");
		}

		Long empresaDestinoId;

		if (isSuperAdmin && request.empresaId() != null) {
			empresaDestinoId = request.empresaId();
		} else {
			empresaDestinoId = securityUtils.empresaId();
		}

		if (empresaDestinoId == null) {
			throw new RuntimeException("A congregação é obrigatória para cadastrar um instrumento.");
		}

		// 🟢 VALIDAÇÃO PREVENTIVA: Impede duplicidade antes de bater no banco
		String nomeLimpo = request.nome() != null ? request.nome().trim() : "";
		if (repository.existsByNomeIgnoreCaseAndEmpresaId(nomeLimpo, empresaDestinoId)) {
			throw new RuntimeException("Já existe um instrumento cadastrado com este nome nesta congregação.");
		}

		Empresa empresa = empresaRepository.findById(empresaDestinoId)
				.orElseThrow(() -> new ResourceNotFoundException("Congregação não encontrada com ID: " + empresaDestinoId));

		Instrumento instrumento = new Instrumento();
		instrumento.setNome(nomeLimpo);
		instrumento.setTipo(request.tipo());
		instrumento.setDescricao(request.descricao());
		instrumento.setQuantidadeEscala(request.quantidade_escala());
		instrumento.setAtivo(request.ativo());
		instrumento.setEmpresa(empresa);

		Instrumento salvo = repository.save(instrumento);

		return converterResponse(salvo);
	}


	public List<InstrumentoResponse> listar(Long empresaId) {
		boolean isSuperAdmin = securityUtils.isSuperAdmin();

		List<Instrumento> lista;

		if (isSuperAdmin) {
			if (empresaId != null) {
				lista = repository.findByEmpresaId(empresaId);
			} else {
				lista = repository.findAll();
			}
		} else {
			Long empresaIdLogada = securityUtils.empresaId();
			lista = repository.findByEmpresaId(empresaIdLogada);
		}

		return lista.stream().map(this::converterResponse).toList();
	}

	public InstrumentoResponse buscarPorId(Long id) {
		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		validarAcessoEmpresa(instrumento);

		return converterResponse(instrumento);
	}

	public InstrumentoResponse atualizar(Long id, InstrumentoRequestDTO request) {
		boolean isSuperAdmin = securityUtils.isSuperAdmin();
		boolean isAdmin = securityUtils.isAdmin();

		if (!isSuperAdmin && !isAdmin) {
			throw new RuntimeException("Apenas administradores podem atualizar instrumentos.");
		}

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		validarAcessoEmpresa(instrumento);

		instrumento.setNome(request.nome());
		instrumento.setTipo(request.tipo());
		instrumento.setDescricao(request.descricao());
		instrumento.setQuantidadeEscala(request.quantidade_escala());
		instrumento.setAtivo(request.ativo());

		// Se Super Admin informou outra empresa na edição
		if (isSuperAdmin && request.empresaId() != null && !request.empresaId().equals(instrumento.getEmpresa().getId())) {
			Empresa novaEmpresa = empresaRepository.findById(request.empresaId())
					.orElseThrow(() -> new ResourceNotFoundException("Congregação não encontrada"));
			instrumento.setEmpresa(novaEmpresa);
		}

		Instrumento atualizado = repository.save(instrumento);

		return converterResponse(atualizado);
	}

	public void deletar(Long id) {
		boolean isSuperAdmin = securityUtils.isSuperAdmin();
		boolean isAdmin = securityUtils.isAdmin();

		if (!isSuperAdmin && !isAdmin) {
			throw new RuntimeException("Apenas administradores podem deletar instrumentos.");
		}

		Instrumento instrumento = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));

		validarAcessoEmpresa(instrumento);

		repository.delete(instrumento);
	}

	private void validarAcessoEmpresa(Instrumento instrumento) {
		if (!securityUtils.isSuperAdmin()) {
			Long empresaIdLogada = securityUtils.empresaId();
			if (instrumento.getEmpresa() == null || !instrumento.getEmpresa().getId().equals(empresaIdLogada)) {
				throw new ResourceNotFoundException("Instrumento não pertence à sua instituição");
			}
		}
	}

	private InstrumentoResponse converterResponse(Instrumento instrumento) {
		Long empresaId = null;
		String empresaNome = null;

		if (instrumento.getEmpresa() != null) {
			empresaId = instrumento.getEmpresa().getId();
			empresaNome = instrumento.getEmpresa().getNome();
		}

		return new InstrumentoResponse(
				instrumento.getId(),
				instrumento.getNome(),
				instrumento.getTipo(),
				instrumento.getDescricao(),
				instrumento.getQuantidadeEscala(),
				instrumento.getAtivo(),
				empresaId,
				empresaNome
		);
	}
}
