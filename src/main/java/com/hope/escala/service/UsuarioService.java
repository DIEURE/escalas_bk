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
import com.hope.escala.entity.Instrumento;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.annotation.PodeSerAdmin;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	private final InstrumentoRepository instrumentoRepository;

	private final DepartamentoRepository departamentoRepository;
	
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, InstrumentoRepository instrumentoRepository,
			DepartamentoRepository departamentoRepository, PasswordEncoder passwordEncoder) {

		this.usuarioRepository = usuarioRepository;

		this.instrumentoRepository = instrumentoRepository;

		this.departamentoRepository = departamentoRepository;
		
		this.passwordEncoder = passwordEncoder;
	}

	@PodeSerAdmin
	public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {

		if (usuarioRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já existe");
		}

		// Busca múltiplos instrumentos pelos IDs enviados
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
		usuario.setAtivo(true);

		Usuario salvo = usuarioRepository.save(usuario);

		return converterParaDTO(salvo);
	}

	// 🟢 1. Lista os usuários pendentes de aprovação (ativo = false)
	public List<UsuarioResponseDTO> listarPendentes() {
		return usuarioRepository.findByAtivoFalse()
				.stream()
				.map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	// 🟢 2. Aprova o usuário pendente, ativando o cadastro e definindo perfil/instrumentos/departamentos
	@PodeSerAdmin // Garante que apenas Admin ou Líder (conforme sua regra) possa aprovar
	public UsuarioResponseDTO aprovar(Long id, UsuarioRequestDTO dto) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário pendente não encontrado"));

		// Ativa o acesso do usuário no sistema
		usuario.setAtivo(true);

		// Atualiza o perfil definido pelo Admin (ex: MUSICO, VOLUNTARIO, LIDER)
		if (dto.getPerfil() != null) {
			usuario.setPerfil(dto.getPerfil());
		}

		// Associa os instrumentos selecionados pelo Admin na aprovação
		Set<Instrumento> instrumentosEncontrados = new HashSet<>();
		if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
			instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
		}
		usuario.setInstrumentos(instrumentosEncontrados);

		// Associa os departamentos selecionados
		if (dto.getDepartamentoIds() != null && !dto.getDepartamentoIds().isEmpty()) {
			Set<Departamento> departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));
			usuario.setDepartamentos(departamentos);
		}

		Usuario aprovado = usuarioRepository.save(usuario);

		return converterParaDTO(aprovado);
	}

	
	public List<UsuarioResponseDTO> listar() {
		return usuarioRepository.findByAtivoTrue().stream().map(this::converterParaDTO).collect(Collectors.toList());
	}

	public UsuarioResponseDTO buscarPorId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		return converterParaDTO(usuario);
	}
	
	public List<UsuarioResponseDTO> buscarPorDepartamento(Long departamentoId) {
	    return usuarioRepository.findByDepartamentoIdAndAtivoTrue(departamentoId)
	            .stream()
	            .map(this::converterParaDTO)
	            .collect(Collectors.toList());
	}

	public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email já existe");
		}

		// Busca múltiplos instrumentos atualizados
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
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

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

		// Mapeia os múltiplos instrumentos para o DTO (ajuste o DTO de resposta se necessário para aceitar Set<String> ou List<String>)
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
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		usuario.setDisponibilidade(disponibilidade);

		Usuario atualizado = usuarioRepository.save(usuario);

		return converterParaDTO(atualizado);
	}
}
