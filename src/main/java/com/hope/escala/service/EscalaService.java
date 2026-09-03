package com.hope.escala.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hope.escala.dto.request.EscalaRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.EscalaDetalhesResponseDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.dto.response.EscalaMusicoResponseDTO;
import com.hope.escala.dto.response.EscalaResponseDTO;
import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Escala;
import com.hope.escala.entity.EscalaMusica;
import com.hope.escala.entity.EscalaMusico;
import com.hope.escala.entity.Instrumento;
import com.hope.escala.entity.Musica;
import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.StatusAgendaMensal;
import com.hope.escala.enums.StatusEscala;
import com.hope.escala.enums.TipoEscala;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.AgendaMensalRepository;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.EscalaMusicaRepository;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.repository.MusicaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

// Adicione o campo (junto com os outros repositórios)

import jakarta.transaction.Transactional;

@Service
public class EscalaService {

	private final EscalaRepository escalaRepository;
	private final AgendaMensalRepository agendaMensalRepository;
	private final EscalaMusicoRepository escalaMusicoRepository;
	private final EscalaMusicaRepository escalaMusicaRepository;
	private final DepartamentoRepository departamentoRepository;
	private final EscalaAutomaticaService escalaAutomaticaService;
	private final InstrumentoRepository instrumentoRepository;
	private final UsuarioRepository usuarioRepository;
	private final SecurityUtils securityUtils;
	private final MusicaRepository musicaRepository;

	public EscalaService(EscalaRepository escalaRepository, AgendaMensalRepository agendaMensalRepository,
			EscalaMusicoRepository escalaMusicoRepository, EscalaMusicaRepository escalaMusicaRepository,
			DepartamentoRepository departamentoRepository, EscalaAutomaticaService escalaAutomaticaService,
			InstrumentoRepository instrumentoRepository, SecurityUtils securityUtils,
			UsuarioRepository usuarioRepository, MusicaRepository musicaRepository) { // <-- ADICIONADO
		this.escalaRepository = escalaRepository;
		this.agendaMensalRepository = agendaMensalRepository;
		this.escalaMusicoRepository = escalaMusicoRepository;
		this.escalaMusicaRepository = escalaMusicaRepository;
		this.departamentoRepository = departamentoRepository;
		this.escalaAutomaticaService = escalaAutomaticaService;
		this.instrumentoRepository = instrumentoRepository;
		this.usuarioRepository = usuarioRepository;
		this.securityUtils = securityUtils;
		this.musicaRepository = musicaRepository; // <-- ADICIONADO
	}

	@Transactional
	public EscalaResponseDTO salvar(EscalaRequestDTO dto) {
		AgendaMensal agendaMensal = agendaMensalRepository.findById(dto.getAgendaMensalId())
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		Escala escala = new Escala();
		escala.setDataEscala(dto.getDataEscala());
		escala.setNomeCultoNoite(dto.getNomeCultoNoite());
		escala.setHorarioNoite(dto.getHorarioNoite());
		escala.setHorarioNoiteFim(dto.getHorarioNoiteFim());

		// Manhã opcional
		if (dto.getNomeCultoManha() != null && !dto.getNomeCultoManha().isBlank() && dto.getHorarioManha() != null) {
			escala.setNomeCultoManha(dto.getNomeCultoManha());
			escala.setHorarioManha(dto.getHorarioManha());
			escala.setHorarioManhaFim(dto.getHorarioManhaFim());
		}

		escala.setObservacao(dto.getObservacao());
		escala.setTipoEscala(dto.getTipoEscala());
		escala.setAgendaMensal(agendaMensal);
		escala.setDepartamento(departamento);
		escala.setStatus(StatusEscala.ABERTA);
		escala.setAtiva(true);

		Escala salva = escalaRepository.save(escala);

		if (dto.getTipoEscala() == TipoEscala.AUTOMATICA) {
			gerarMusicosAutomaticamente(salva);
			salva.setStatus(StatusEscala.AGUARDANDO_CONFIRMACAO);
			escalaRepository.save(salva);
		}

		return converterParaDTO(salva);
	}

	public List<EscalaResponseDTO> listar() {
		return escalaRepository.findByAtivaTrue().stream().map(this::converterParaDTO).collect(Collectors.toList());
	}

	public EscalaResponseDTO buscarPorId(Long id) {
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));
		return converterParaDTO(escala);
	}

	@Transactional
	public EscalaResponseDTO atualizar(Long id, EscalaRequestDTO dto) {
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		AgendaMensal agendaMensal = agendaMensalRepository.findById(dto.getAgendaMensalId())
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		escala.setDataEscala(dto.getDataEscala());
		escala.setHorarioManha(dto.getHorarioManha());
		escala.setHorarioManhaFim(dto.getHorarioManhaFim());
		escala.setHorarioNoite(dto.getHorarioNoite());
		escala.setHorarioNoiteFim(dto.getHorarioNoiteFim());
		escala.setNomeCultoManha(dto.getNomeCultoManha());
		escala.setNomeCultoNoite(dto.getNomeCultoNoite());
		escala.setObservacao(dto.getObservacao());
		escala.setTipoEscala(dto.getTipoEscala());
		escala.setAgendaMensal(agendaMensal);
		escala.setDepartamento(departamento);

		// Atualizar Músicos
		if (dto.getMusicosIds() != null) {
			List<EscalaMusico> musicosAtuais = escala.getMusicos();
			List<Long> novosIds = dto.getMusicosIds();

			musicosAtuais.removeIf(m -> !novosIds.contains(m.getUsuario().getId()));

			for (Long idUsuario : novosIds) {
				boolean jaExiste = musicosAtuais.stream().anyMatch(m -> m.getUsuario().getId().equals(idUsuario));
				if (!jaExiste) {
					Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();
					EscalaMusico novo = new EscalaMusico();
					novo.setEscala(escala);
					novo.setUsuario(usuario);
					escala.getMusicos().add(novo);
				}
			}
		}

		Escala atualizada = escalaRepository.save(escala);
		escalaRepository.flush();
		return converterParaDTO(atualizada);
	}

	public void inativar(Long id) {
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));
		escala.setAtiva(false);
		escalaRepository.save(escala);
	}

	public List<EscalaResponseDTO> listarPorAgendaMensal(Long agendaMensalId) {
		return escalaRepository.findByAgendaMensalIdAndAtivaTrue(agendaMensalId).stream().map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public List<EscalaResponseDTO> gerarEscalasMes(Long agendaMensalId, GerarEscalasMesRequestDTO dto) {

		if (!Boolean.TRUE.equals(dto.getGerarDomingos())) {
			return new ArrayList<>();
		}

		AgendaMensal agendaMensal = agendaMensalRepository.findById(agendaMensalId)
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		List<Escala> escalasCriadas = new ArrayList<>();
		YearMonth yearMonth = YearMonth.of(agendaMensal.getAno(), agendaMensal.getMes());
		LocalDate data = yearMonth.atDay(1);

		// Verifica se o usuário QUER a manhã
		boolean temManha = dto.getNomeCultoManha() != null && !dto.getNomeCultoManha().isBlank()
				&& dto.getHorarioManha() != null;

		while (data.getMonthValue() == yearMonth.getMonthValue()) {

			if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {

				Escala escala = new Escala();
				escala.setAgendaMensal(agendaMensal);
				escala.setDepartamento(departamento);
				escala.setDataEscala(data);
				escala.setTipoEscala(dto.getTipoEscala());
				escala.setAtiva(true);
				escala.setStatus(StatusEscala.ABERTA);

				// Preenche a NOITE (sempre)
				escala.setNomeCultoNoite(dto.getNomeCultoNoite());
				escala.setHorarioNoite(dto.getHorarioNoite());
				escala.setHorarioNoiteFim(dto.getHorarioNoiteFim());

				// Preenche a MANHÃ (somente se solicitado)
				if (temManha) {
					escala.setNomeCultoManha(dto.getNomeCultoManha());
					escala.setHorarioManha(dto.getHorarioManha());
					escala.setHorarioManhaFim(dto.getHorarioManhaFim());
				}

				Escala salva = escalaRepository.save(escala);

				// Gera músicos automaticamente para a escala
				if (dto.getTipoEscala() == TipoEscala.AUTOMATICA) {
					gerarMusicosAutomaticamente(salva);
					salva.setStatus(StatusEscala.AGUARDANDO_CONFIRMACAO);
					escalaRepository.save(salva);
				}

				escalasCriadas.add(salva);
			}

			data = data.plusDays(1);
		}

		return escalasCriadas.stream().map(this::converterParaDTO).toList();
	}

	private void gerarMusicosAutomaticamente(Escala escala) {
		List<Instrumento> instrumentos = instrumentoRepository.findAll();

		for (Instrumento instrumento : instrumentos) {
			Integer quantidade = instrumento.getQuantidadeEscala();

			for (int i = 0; i < quantidade; i++) {
				Usuario usuario = escalaAutomaticaService.escolherMusicoRodizio(instrumento.getId(),
						escala.getDepartamento().getId(), escala.getId());

				if (usuario == null) {
					continue;
				}

				EscalaMusico escalaMusico = new EscalaMusico();
				escalaMusico.setEscala(escala);
				escalaMusico.setUsuario(usuario);
				escalaMusico.setConfirmado(false);
				escalaMusicoRepository.save(escalaMusico);
			}
		}
	}

	public EscalaDetalhesResponseDTO buscarDetalhesEscala(Long escalaId) {
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		List<EscalaMusicoResponseDTO> musicos = escalaMusicoRepository.findByEscalaId(escalaId).stream()
				.map(this::converterMusicoDTO).collect(Collectors.toList());

		List<EscalaMusicaResponseDTO> musicas = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId).stream()
				.map(this::converterMusicaDTO).collect(Collectors.toList());

		EscalaDetalhesResponseDTO dto = new EscalaDetalhesResponseDTO();
		dto.setEscala(converterParaDTO(escala));
		dto.setMusicos(musicos);
		dto.setMusicas(musicas);
		return dto;
	}

	private EscalaResponseDTO converterParaDTO(Escala escala) {
		EscalaResponseDTO dto = new EscalaResponseDTO();
		dto.setId(escala.getId());
		dto.setDataEscala(escala.getDataEscala());
		dto.setHorarioManha(escala.getHorarioManha());
		dto.setHorarioManhaFim(escala.getHorarioManhaFim());
		dto.setHorarioNoite(escala.getHorarioNoite());
		dto.setHorarioNoiteFim(escala.getHorarioNoiteFim());
		dto.setNomeCultoManha(escala.getNomeCultoManha());
		dto.setNomeCultoNoite(escala.getNomeCultoNoite());
		dto.setObservacao(escala.getObservacao());
		dto.setAtiva(escala.getAtiva());
		dto.setYoutubePlaylistUrl(escala.getLinkPlaylistManual());
		dto.setTipoEscala(escala.getTipoEscala());
		dto.setStatus(escala.getStatus());

		if (escala.getAgendaMensal() != null) {
			dto.setAgendaMensalId(escala.getAgendaMensal().getId());
			dto.setDescricaoAgendaMensal(escala.getAgendaMensal().getDescricao());
		}

		if (escala.getDepartamento() != null) {
			dto.setDepartamentoId(escala.getDepartamento().getId());
			dto.setNomeDepartamento(escala.getDepartamento().getNome());
		}

		return dto;
	}

	private EscalaMusicoResponseDTO converterMusicoDTO(EscalaMusico escalaMusico) {
		EscalaMusicoResponseDTO dto = new EscalaMusicoResponseDTO();
		dto.setId(escalaMusico.getId());
		dto.setEscalaId(escalaMusico.getEscala().getId());
		dto.setNomeCultoManha(escalaMusico.getEscala().getNomeCultoManha());
		dto.setNomeCultoNoite(escalaMusico.getEscala().getNomeCultoNoite());

		// ADICIONE ESTAS LINHAS (data e horários):
		dto.setDataEscala(escalaMusico.getEscala().getDataEscala());
		dto.setHorarioManha(escalaMusico.getEscala().getHorarioManha());
		dto.setHorarioNoite(escalaMusico.getEscala().getHorarioNoite());

		dto.setUsuarioId(escalaMusico.getUsuario().getId());
		dto.setNomeUsuario(escalaMusico.getUsuario().getNome());
		dto.setInstrumento(escalaMusico.getUsuario().getInstrumento().getNome());
		dto.setConfirmado(escalaMusico.getConfirmado());
		dto.setObservacao(escalaMusico.getObservacao());
		return dto;
	}

	private EscalaMusicaResponseDTO converterMusicaDTO(EscalaMusica escalaMusica) {
		EscalaMusicaResponseDTO dto = new EscalaMusicaResponseDTO();
		dto.setId(escalaMusica.getId());
		dto.setEscalaId(escalaMusica.getEscala().getId());

		dto.setNomeCultoManha(escalaMusica.getEscala().getNomeCultoManha());
		dto.setNomeCultoNoite(escalaMusica.getEscala().getNomeCultoNoite());
		dto.setCifraUrl(escalaMusica.getMusica().getCifraUrl());
		dto.setMusicaId(escalaMusica.getMusica().getId());
		dto.setNomeMusica(escalaMusica.getMusica().getNome());
		dto.setCantor(escalaMusica.getMusica().getCantor());
		dto.setTom(escalaMusica.getMusica().getTom());
		dto.setCifra(escalaMusica.getMusica().getCifra());
		dto.setBpm(escalaMusica.getMusica().getBpm());
		dto.setOrdem(escalaMusica.getOrdem());
		dto.setObservacao(escalaMusica.getObservacao());
		return dto;
	}

	public void atualizarStatusAgendaMensal(Long agendaMensalId) {
		List<Escala> escalas = escalaRepository.findByAgendaMensalIdAndAtivaTrue(agendaMensalId);
		AgendaMensal agenda = agendaMensalRepository.findById(agendaMensalId)
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		if (escalas.isEmpty()) {
			agenda.setStatus(StatusAgendaMensal.EM_MONTAGEM);
		} else {
			boolean todasFechadas = escalas.stream().allMatch(e -> e.getStatus() == StatusEscala.FECHADA);
			boolean existeFechada = escalas.stream().anyMatch(e -> e.getStatus() == StatusEscala.FECHADA);

			if (todasFechadas) {
				agenda.setStatus(StatusAgendaMensal.COMPLETA);
			} else if (existeFechada) {
				agenda.setStatus(StatusAgendaMensal.PARCIAL);
			} else {
				agenda.setStatus(StatusAgendaMensal.EM_MONTAGEM);
			}
		}
		agendaMensalRepository.save(agenda);
	}

	public void fecharEscala(Long escalaId) {
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));

		if (escala.getStatus() == StatusEscala.FECHADA) {
			return;
		}

		escala.setStatus(StatusEscala.FECHADA);
		escalaRepository.save(escala);
		atualizarStatusAgendaMensal(escala.getAgendaMensal().getId());
	}

	@Transactional
	public void adicionarMusicos(Long escalaId, List<Long> musicosIds) {
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		List<EscalaMusico> antigos = escalaMusicoRepository.findByEscalaId(escalaId);
		escalaMusicoRepository.deleteAll(antigos);

		for (Long usuarioId : musicosIds) {
			Usuario usuario = usuarioRepository.findById(usuarioId)
					.orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

			EscalaMusico novo = new EscalaMusico();
			novo.setEscala(escala);
			novo.setUsuario(usuario);
			novo.setConfirmado(false);
			novo.setSubstituido(false);
			escalaMusicoRepository.save(novo);
		}
	}

	@Transactional
	public EscalaResponseDTO alterarStatus(Long id, StatusEscala novoStatus) {
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));
		escala.setStatus(novoStatus);
		Escala salva = escalaRepository.save(escala);
		return converterParaDTO(salva);
	}

	// No seu EscalaService.java
	@Transactional
	public String salvarPlaylistManual(Long escalaId, List<Long> musicasIds) {
	    Escala escala = escalaRepository.findById(escalaId)
	            .orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada com ID: " + escalaId));

	    List<EscalaMusica> antigas = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId);
	    escalaMusicaRepository.deleteAll(antigas);
	    escalaMusicaRepository.flush();

	    List<EscalaMusica> novas = new ArrayList<>();
	    int ordem = 1;
	    for (Long musicaId : musicasIds) {
	        Musica musica = musicaRepository.findById(musicaId)
	                .orElseThrow(() -> new ResourceNotFoundException("Música não encontrada: " + musicaId));

	        EscalaMusica em = new EscalaMusica();
	        em.setEscala(escala);
	        em.setMusica(musica);
	        em.setOrdem(ordem++);
	        escalaMusicaRepository.save(em);
	        novas.add(em);
	    }

	    // 💡 1. Monta o título descritivo inteligente da escala
	    String nomeCulto = escala.getNomeCultoNoite() != null ? escala.getNomeCultoNoite() 
	                     : (escala.getNomeCultoManha() != null ? escala.getNomeCultoManha() : "Culto");
	    String tituloPlaylist =  nomeCulto + "-" + escala.getDataEscala();

	    // 💡 2. Monta a URL limpa do YouTube
	    String urlPlaylist = montarUrlPlaylistYoutube(novas);
	    
	    // 💡 3. Salva a URL e o Título na Escala
	    escala.setLinkPlaylistManual(urlPlaylist);
	    escala.setTituloPlaylistManual(tituloPlaylist); // Salvando o título na base!
	    escalaRepository.save(escala);

	    return urlPlaylist;
	}

	private String montarUrlPlaylistYoutube(List<EscalaMusica> musicas) {
	    String ids = musicas.stream()
	            .map(em -> em.getMusica().getYoutubeVideoId())
	            .filter(id -> id != null && !id.isBlank())
	            .collect(Collectors.joining(","));

	    if (ids.isBlank()) {
	        return null;
	    }

	    return "https://www.youtube.com/watch_videos?video_ids=" + ids;
	}
	
	public List<EscalaMusicaResponseDTO> listarMusicasDaPlaylistManual(Long escalaId) {
	    List<EscalaMusica> lista = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId);
	    return lista.stream().map(this::converterMusicaDTO).collect(Collectors.toList());
	}


}
