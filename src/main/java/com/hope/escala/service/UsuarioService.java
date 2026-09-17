package com.hope.escala.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.UsuarioRequestDTO;
import com.hope.escala.dto.response.UsuarioResponseDTO;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Instrumento;
import com.hope.escala.entity.Usuario;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.PodeSerAdmin;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final InstrumentoRepository instrumentoRepository;
	private final DepartamentoRepository departamentoRepository;
	private final PasswordEncoder passwordEncoder;
	private final SecurityUtils securityUtils;

	public UsuarioService(UsuarioRepository usuarioRepository, InstrumentoRepository instrumentoRepository,
			DepartamentoRepository departamentoRepository, PasswordEncoder passwordEncoder,
			SecurityUtils securityUtils) {
		this.usuarioRepository = usuarioRepository;
		this.instrumentoRepository = instrumentoRepository;
		this.departamentoRepository = departamentoRepository;
		this.passwordEncoder = passwordEncoder;
		this.securityUtils = securityUtils;
	}

	@PodeSerAdmin
	public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

		if (usuarioRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já existe");
		}

		Set<Instrumento> instrumentosEncontrados = new HashSet<>();
		if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
		    instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
		}

		Set<Departamento> departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));

		Usuario usuario = new Usuario();
		usuario.setNome(dto.getNome());
		usuario.setEmail(dto.getEmail());
		usuario.setTelefone(dto.getTelefone());
		usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
		usuario.setPerfil(dto.getPerfil());
		usuario.setDisponibilidade(dto.getDisponibilidade());
		usuario.setObservacao(dto.getObservacao());

		usuario.setInstrumentos(instrumentosEncontrados);
		usuario.setDepartamentos(departamentos);
		usuario.setAtivo(true); // Se cadastrado por admin, nasce ativo (ou ajuste conforme regra de negócio)

		// 🟢 Associa a empresa logada (Multi-Tenant)
		Empresa empresa = new Empresa();
		empresa.setId(empresaIdLogada);
		usuario.setEmpresa(empresa);

		Usuario salvo = usuarioRepository.save(usuario);

		return converterParaDTO(salvo);
	}

	// 🟢 1. Lista os usuários pendentes de aprovação filtrados por empresa
	public List<UsuarioResponseDTO> listarPendentes() {
		Long empresaIdLogada = securityUtils.empresaId();
		return usuarioRepository.findByEmpresaIdAndAtivoFalse(empresaIdLogada)
				.stream()
				.map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	// 🟢 2. Aprova o usuário pendente da mesma empresa
	@PodeSerAdmin 
	public UsuarioResponseDTO aprovar(Long id, UsuarioRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário pendente não encontrado"));

		if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
		}

		usuario.setAtivo(true);

		if (dto.getPerfil() != null) {
			usuario.setPerfil(dto.getPerfil());
		}

		Set<Instrumento> instrumentosEncontrados = new HashSet<>();
		if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
			instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
		}
		usuario.setInstrumentos(instrumentosEncontrados);

		if (dto.getDepartamentoIds() != null && !dto.getDepartamentoIds().isEmpty()) {
			Set<Departamento> departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));
			usuario.setDepartamentos(departamentos);
		}

		Usuario aprovado = usuarioRepository.save(usuario);

		return converterParaDTO(aprovado);
	}

	public List<UsuarioResponseDTO> listar() {
		Long empresaIdLogada = securityUtils.empresaId();
		return usuarioRepository.findByEmpresaIdAndAtivoTrue(empresaIdLogada)
				.stream()
				.map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public UsuarioResponseDTO buscarPorId(Long id) {
		Long empresaIdLogada = securityUtils.empresaId();

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

		if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
		}

		return converterParaDTO(usuario);
	}
	
	public List<UsuarioResponseDTO> buscarPorDepartamento(Long departamentoId) {
	    Long empresaIdLogada = securityUtils.empresaId();
	    return usuarioRepository.findByDepartamentosIdAndEmpresaIdAndAtivoTrue(departamentoId, empresaIdLogada)
	            .stream()
	            .map(this::converterParaDTO)
	            .collect(Collectors.toList());
	}


	public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

		if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
		}

		if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já existe");
		}

		Set<Instrumento> instrumentosEncontrados = new HashSet<>();
		if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
		    instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
		}

		Set<Departamento> departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));

		usuario.setNome(dto.getNome());
		usuario.setEmail(dto.getEmail());
		usuario.setTelefone(dto.getTelefone());
		usuario.setPerfil(dto.getPerfil());
		usuario.setDisponibilidade(dto.getDisponibilidade());
		usuario.setObservacao(dto.getObservacao());

		usuario.setInstrumentos(instrumentosEncontrados);
		usuario.setDepartamentos(departamentos);

		if (dto.getSenha() != null && !dto.getSenha().isBlank()) {  
		    usuario.setSenha(passwordEncoder.encode(dto.getSenha())); 
		}

		Usuario atualizado = usuarioRepository.save(usuario);

		return converterParaDTO(atualizado);
	}

	@PodeSerAdmin
	public void inativar(Long id) {
		Long empresaIdLogada = securityUtils.empresaId();

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

		if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
		}

		usuario.setAtivo(false);
		usuario.setDataInativacao(LocalDateTime.now());

		usuarioRepository.save(usuario);
	}

	private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
		UsuarioResponseDTO dto = new UsuarioResponseDTO();

		dto.setId(usuario.getId());
		dto.setNome(usuario.getNome());
		dto.setEmail(usuario.getEmail());
		dto.setTelefone(usuario.getTelefone());
		dto.setDisponibilidade(usuario.getDisponibilidade());
		dto.setPerfil(usuario.getPerfil());
		dto.setAtivo(usuario.getAtivo());

		if (usuario.getInstrumentos() != null) {
			dto.setInstrumentoIds(
				usuario.getInstrumentos().stream().map(Instrumento::getId).collect(Collectors.toSet())
			);
		}

		dto.setDepartamentos(
				usuario.getDepartamentos().stream().map(Departamento::getNome).collect(Collectors.toSet()));

		return dto;
	}

	public UsuarioResponseDTO atualizarDisponibilidade(Long id, Boolean disponibilidade) {
		Long empresaIdLogada = securityUtils.empresaId();

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

		if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
		}

		usuario.setDisponibilidade(disponibilidade);

		Usuario atualizado = usuarioRepository.save(usuario);

		return converterParaDTO(atualizado);
	}
}
