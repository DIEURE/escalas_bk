package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.MusicaRequestDTO;
import com.hope.escala.dto.response.MusicaResponseDTO;
import com.hope.escala.entity.Musica;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.MusicaRepository;

@Service
public class MusicaService {

	private final MusicaRepository musicaRepository;

	public MusicaService(MusicaRepository musicaRepository) {
		this.musicaRepository = musicaRepository;
	}

	public MusicaResponseDTO salvar(MusicaRequestDTO dto) {

		Musica musica = new Musica();

		musica.setNome(dto.getNome());
		musica.setCantor(dto.getCantor());
		musica.setTom(dto.getTom());
		musica.setBpm(dto.getBpm());
		musica.setLinkPlaylistManual(dto.getLinkPlaylistManual());
		musica.setYoutubeVideoId(dto.getYoutubeVideoId());
		musica.setCifra(dto.getCifra());
		musica.setCifraUrl(dto.getCifraUrl());
		
		musica.setAtiva(musica.getAtiva());
		musica.setDataCadastro(musica.getDataCadastro());
		musica.setVezesEscalada(musica.getVezesEscalada());

		 if (musica.getCategoria() != null) {
		        dto.setCategoriaId(musica.getCategoria().getId());
		    }
		
		Musica musicaSalva = musicaRepository.save(musica);

		return converterParaDTO(musicaSalva);
	}

	private MusicaResponseDTO converterParaDTO(Musica musica) {

		MusicaResponseDTO dto = new MusicaResponseDTO();

		dto.setId(musica.getId());
		dto.setNome(musica.getNome());
		dto.setCantor(musica.getCantor());
		dto.setTom(musica.getTom());
		dto.setBpm(musica.getBpm());
		dto.setLinkPlaylistManual(musica.getLinkPlaylistManual());
		dto.setYoutubeVideoId(musica.getYoutubeVideoId());
		dto.setCifra(musica.getCifra());
		dto.setCifraUrl(musica.getCifraUrl());
		musica.setAtiva(musica.getAtiva());
		musica.setDataCadastro(musica.getDataCadastro());
		musica.setVezesEscalada(musica.getVezesEscalada());

		 if (musica.getCategoria() != null) {
		        dto.setCategoriaId(musica.getCategoria().getId());
		    }
		return dto;
	}

	public MusicaResponseDTO atualizar(Long id, MusicaRequestDTO dto) {

		Musica musica = musicaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

		musica.setNome(dto.getNome());
		musica.setCantor(dto.getCantor());
		musica.setTom(dto.getTom());
		musica.setBpm(dto.getBpm());
		musica.setLinkPlaylistManual(dto.getLinkPlaylistManual());
		musica.setYoutubeVideoId(dto.getYoutubeVideoId());
		musica.setCifra(dto.getCifra());
		musica.setCifraUrl(dto.getCifraUrl());

		Musica musicaAtualizada = musicaRepository.save(musica);

		return converterParaDTO(musicaAtualizada);
	}

	public List<MusicaResponseDTO> listar() {

		List<Musica> musicas = musicaRepository.findByAtivaTrue();

		return musicas.stream().map(this::converterParaDTO).toList();
	}

	public MusicaResponseDTO buscarPorId(Long id) {

		Musica musica = musicaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

		return converterParaDTO(musica);
	}

	public void desativar(Long id) {

		Musica musica = musicaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));

		musica.setAtiva(false);

		musicaRepository.save(musica);
	}
}