package com.hope.escala.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.SubstituicaoEscalaMusicaDTO;
import com.hope.escala.dto.request.EscalaMusicaRequestDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Escala;
import com.hope.escala.entity.EscalaMusica;
import com.hope.escala.entity.Musica;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.EscalaMusicaRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.repository.MusicaRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class EscalaMusicaService {

	private final EscalaMusicaRepository escalaMusicaRepository;
	private final EscalaRepository escalaRepository;
	private final MusicaRepository musicaRepository;
	private final SecurityUtils securityUtils;
	private final EmpresaRepository empresaRepository;

	public EscalaMusicaService(EscalaMusicaRepository escalaMusicaRepository, EscalaRepository escalaRepository,
			MusicaRepository musicaRepository,
			SecurityUtils securityUtils,
			EmpresaRepository empresaRepository) {
		this.escalaMusicaRepository = escalaMusicaRepository;
		this.escalaRepository = escalaRepository;
		this.musicaRepository = musicaRepository;
		this.securityUtils = securityUtils;
		this.empresaRepository = empresaRepository;
	}

	public EscalaMusicaResponseDTO salvar(EscalaMusicaRequestDTO dto) {

		// 🟢 1. Obtém o ID da empresa de forma segura pelo Token JWT (SecurityUtils)
		Long empresaIdLogada = securityUtils.empresaId();
		
		Empresa empresa = empresaRepository.findById(empresaIdLogada)
				.orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

		// 🟢 2. Valida se a escala pertence à empresa logada (Segurança Multi-Tenant)
		Escala escala = escalaRepository.findById(dto.getEscalaId())
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));
		
		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		// 🟢 3. Valida se a música pertence à empresa logada
		Musica musica = musicaRepository.findById(dto.getMusicaId())
				.orElseThrow(() -> new ResourceNotFoundException("Música não encontrada"));
				
		if (!musica.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Música não pertence à sua instituição");
		}

		EscalaMusica escalaMusica = new EscalaMusica();

		escalaMusica.setEscala(escala);
		escalaMusica.setMusica(musica);
		escalaMusica.setOrdem(dto.getOrdem());
		escalaMusica.setObservacao(dto.getObservacao());
		
		// 🟢 4. Associa a empresa na entidade EscalaMusica (caso ela possua o campo empresa_id)
		escalaMusica.setEmpresa(empresa);

		EscalaMusica escalaMusicaSalva = escalaMusicaRepository.save(escalaMusica);

		return converterParaDTO(escalaMusicaSalva);
	}

	public List<EscalaMusicaResponseDTO> listarPorEscala(Long escalaId) {
		// Dica: você também pode garantir que a escala solicitada pertence à empresa logada aqui se achar necessário
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
