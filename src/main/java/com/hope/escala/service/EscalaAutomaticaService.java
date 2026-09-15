
package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.UsuarioRepository;

@Service
public class EscalaAutomaticaService {

	private final UsuarioRepository usuarioRepository;

	private final EscalaMusicoRepository escalaMusicoRepository;

	public EscalaAutomaticaService(UsuarioRepository usuarioRepository, EscalaMusicoRepository escalaMusicoRepository) {

		this.usuarioRepository = usuarioRepository;

		this.escalaMusicoRepository = escalaMusicoRepository;
	}

	public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId) {

		List<Usuario> usuarios = usuarioRepository.buscarMusicosDisponiveis(instrumentoId, departamentoId);

		/*
		 * Remove músicos já escalados
		 */
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
