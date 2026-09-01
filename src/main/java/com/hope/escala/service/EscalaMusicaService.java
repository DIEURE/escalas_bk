package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.SubstituicaoEscalaMusicaDTO;
import com.hope.escala.dto.request.EscalaMusicaRequestDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.entity.Escala;
import com.hope.escala.entity.EscalaMusica;
import com.hope.escala.entity.Musica;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.EscalaMusicaRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.repository.MusicaRepository;

@Service
public class EscalaMusicaService {

	private final EscalaMusicaRepository escalaMusicaRepository;
	private final EscalaRepository escalaRepository;
	private final MusicaRepository musicaRepository;

	public EscalaMusicaService(EscalaMusicaRepository escalaMusicaRepository, EscalaRepository escalaRepository,
			MusicaRepository musicaRepository) {
		this.escalaMusicaRepository = escalaMusicaRepository;
		this.escalaRepository = escalaRepository;
		this.musicaRepository = musicaRepository;
	}

	public EscalaMusicaResponseDTO salvar(EscalaMusicaRequestDTO dto) {

		Escala escala = escalaRepository.findById(dto.getEscalaId())
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));

		Musica musica = musicaRepository.findById(dto.getMusicaId())
				.orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

		EscalaMusica escalaMusica = new EscalaMusica();

		escalaMusica.setEscala(escala);
		escalaMusica.setMusica(musica);
		escalaMusica.setOrdem(dto.getOrdem());
		escalaMusica.setObservacao(dto.getObservacao());

		EscalaMusica escalaMusicaSalva = escalaMusicaRepository.save(escalaMusica);

		return converterParaDTO(escalaMusicaSalva);
	}

	public List<EscalaMusicaResponseDTO> listarPorEscala(Long escalaId) {

		List<EscalaMusica> escalaMusicas = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId);

		return escalaMusicas.stream().map(this::converterParaDTO).toList();
	}

	public EscalaMusicaResponseDTO substituirMusica(Long id, SubstituicaoEscalaMusicaDTO dto) {

		EscalaMusica escalaMusica = escalaMusicaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Escala música não encontrada"));

		Musica musicaSubstituta = musicaRepository.findById(dto.getMusicaSubstitutaId())
				.orElseThrow(() -> new ResourceNotFoundException("Música substituta não encontrada"));

		escalaMusica.setSubstituida(true);

		escalaMusica.setMusicaSubstituta(musicaSubstituta);

		escalaMusica.setMotivoSubstituicao(dto.getMotivoSubstituicao());

		EscalaMusica escalaMusicaAtualizada = escalaMusicaRepository.save(escalaMusica);

		return converterParaDTO(escalaMusicaAtualizada);
	}

	private EscalaMusicaResponseDTO converterParaDTO(EscalaMusica escalaMusica) {

		EscalaMusicaResponseDTO dto = new EscalaMusicaResponseDTO();

		dto.setId(escalaMusica.getId());

		dto.setEscalaId(escalaMusica.getEscala().getId());

		dto.setNomeCultoManha(escalaMusica.getEscala().getNomeCultoManha());
		
		dto.setNomeCultoNoite(escalaMusica.getEscala().getNomeCultoNoite());

		dto.setMusicaId(escalaMusica.getMusica().getId());

		dto.setNomeMusica(escalaMusica.getMusica().getNome());

		dto.setCantor(escalaMusica.getMusica().getCantor());

		dto.setYoutubeVideoId(escalaMusica.getMusica().getYoutubeVideoId());

		dto.setTom(escalaMusica.getMusica().getTom());

		dto.setOrdem(escalaMusica.getOrdem());

		dto.setObservacao(escalaMusica.getObservacao());

		dto.setSubstituida(Boolean.TRUE.equals(escalaMusica.getSubstituida()));
		
		dto.setCifraUrl(escalaMusica.getMusica().getCifraUrl());

		if (escalaMusica.getMusicaSubstituta() != null) {

			dto.setMusicaSubstitutaId(escalaMusica.getMusicaSubstituta().getId());

			dto.setNomeMusicaSubstituta(escalaMusica.getMusicaSubstituta().getNome());
		}

		dto.setMotivoSubstituicao(escalaMusica.getMotivoSubstituicao());

		return dto;
	}
}