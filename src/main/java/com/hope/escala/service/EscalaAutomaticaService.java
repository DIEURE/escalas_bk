package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class EscalaAutomaticaService {

	private final UsuarioRepository usuarioRepository;
	private final EscalaMusicoRepository escalaMusicoRepository;
	private final SecurityUtils securityUtils;
	private final EmpresaRepository empresaRepository;	

	public EscalaAutomaticaService(
			UsuarioRepository usuarioRepository,
			EscalaMusicoRepository escalaMusicoRepository,
			SecurityUtils securityUtils,
			EmpresaRepository empresaRepository
			) {

		this.usuarioRepository = usuarioRepository;
		this.escalaMusicoRepository = escalaMusicoRepository;
		this.securityUtils = securityUtils;
		this.empresaRepository = empresaRepository;
	}
	
	public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId) {

		// 🟢 1. Obtém a empresa logada com segurança pelo Token JWT
		Long empresaIdLogada = securityUtils.empresaId();

		// 🟢 2. Busca os músicos disponíveis filtrando também pela empresa (Multi-Tenant)
		// Nota: Certifique-se de que o seu método no repository aceite o empresaId: buscarMusicosDisponiveis(instrumentoId, departamentoId, empresaIdId)
		List<Usuario> usuarios = usuarioRepository.buscarMusicosDisponiveisPorEmpresa(instrumentoId, departamentoId, empresaIdLogada);

		List<Long> usuariosJaEscalados = escalaMusicoRepository.buscarUsuariosJaEscalados(escalaId);

		usuarios = usuarios.stream().filter(usuario -> !usuariosJaEscalados.contains(usuario.getId())).toList();

		if (usuarios.isEmpty()) {
			return null;
		}

		/*
		 * Apenas 1 músico
		 */
		if (usuarios.size() == 1) {
			return usuarios.get(0);
		}

		Usuario escolhido = null;
		LocalDate dataMaisAntiga = null;

		for (Usuario usuario : usuarios) {

			LocalDate ultimaEscala = escalaMusicoRepository.buscarUltimaEscalaDoMusico(usuario.getId());

			/*
			 * Nunca tocou
			 */
			if (ultimaEscala == null) {
				return usuario;
			}

			/*
			 * Está há mais tempo sem tocar
			 */
			if (dataMaisAntiga == null || ultimaEscala.isBefore(dataMaisAntiga)) {
				dataMaisAntiga = ultimaEscala;
				escolhido = usuario;
			}
		}

		return escolhido;
	}
}
