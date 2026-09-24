package com.hope.escala.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hope.escala.dto.request.EscalaMusicoRequestDTO;
import com.hope.escala.dto.request.EscalaRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.EscalaDetalhesResponseDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.dto.response.EscalaMusicoResponseDTO;
import com.hope.escala.dto.response.EscalaResponseDTO;
import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
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
import com.hope.escala.repository.ExcecaoEscalaDataRepository;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.repository.MusicaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

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
	private final ExcecaoEscalaDataRepository excecaoEscalaDataRepository;

	@Autowired
	private YoutubeService youtubeService;

	public EscalaService(EscalaRepository escalaRepository, AgendaMensalRepository agendaMensalRepository,
			EscalaMusicoRepository escalaMusicoRepository, EscalaMusicaRepository escalaMusicaRepository,
			DepartamentoRepository departamentoRepository, EscalaAutomaticaService escalaAutomaticaService,
			InstrumentoRepository instrumentoRepository, SecurityUtils securityUtils,
			UsuarioRepository usuarioRepository, MusicaRepository musicaRepository,
			ExcecaoEscalaDataRepository excecaoEscalaDataRepository) {
		this.escalaRepository = escalaRepository;
		this.agendaMensalRepository = agendaMensalRepository;
		this.escalaMusicoRepository = escalaMusicoRepository;
		this.escalaMusicaRepository = escalaMusicaRepository;
		this.departamentoRepository = departamentoRepository;
		this.escalaAutomaticaService = escalaAutomaticaService;
		this.instrumentoRepository = instrumentoRepository;
		this.usuarioRepository = usuarioRepository;
		this.securityUtils = securityUtils;
		this.musicaRepository = musicaRepository;
		this.excecaoEscalaDataRepository = excecaoEscalaDataRepository;
	}

	@Transactional
	public EscalaResponseDTO salvar(EscalaRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

		AgendaMensal agendaMensal = agendaMensalRepository.findById(dto.getAgendaMensalId())
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

		Escala escala = new Escala();
		escala.setDataEscala(dto.getDataEscala());
		escala.setNomeCultoNoite(dto.getNomeCultoNoite());
		escala.setHorarioNoite(dto.getHorarioNoite());
		escala.setHorarioNoiteFim(dto.getHorarioNoiteFim());

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

		Empresa empresa = new Empresa();
		empresa.setId(empresaIdLogada);
		escala.setEmpresa(empresa);

		Escala salva = escalaRepository.save(escala);

		if (dto.getTipoEscala() == TipoEscala.AUTOMATICA) {
			gerarMusicosAutomaticamente(salva);
			salva.setStatus(StatusEscala.AGUARDANDO_CONFIRMACAO);
			escalaRepository.save(salva);
		}

		return converterParaDTO(salva);
	}

	public List<EscalaResponseDTO> listar() {
		Long empresaIdLogada = securityUtils.empresaId();
		return escalaRepository.findByEmpresaIdAndAtivaTrue(empresaIdLogada).stream().map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public List<EscalaResponseDTO> listarPorAgendaMensal(Long agendaMensalId) {
		Long empresaIdLogada = securityUtils.empresaId();
		return escalaRepository.findByAgendaMensalIdAndEmpresaIdAndAtivaTrue(agendaMensalId, empresaIdLogada).stream()
				.map(this::converterParaDTO).collect(Collectors.toList());
	}

	public EscalaResponseDTO buscarPorId(Long id) {
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		return converterParaDTO(escala);
	}

	@Transactional
	public EscalaResponseDTO atualizar(Long id, EscalaRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

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

		escala.getMusicos().clear();
		escalaMusicoRepository.deleteByEscalaId(escala.getId());
		escalaRepository.flush();

		if (dto.getMusicos() != null && !dto.getMusicos().isEmpty()) {
			for (EscalaMusicoRequestDTO mDto : dto.getMusicos()) {
				if (mDto.getUsuarioId() != null) {
					Usuario usuario = usuarioRepository.findById(mDto.getUsuarioId())
							.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

					EscalaMusico novo = new EscalaMusico();
					novo.setEscala(escala);
					novo.setUsuario(usuario);
					novo.setInstrumento(
							mDto.getInstrumento() != null && !mDto.getInstrumento().isBlank() ? mDto.getInstrumento()
									: "Geral");
					novo.setConfirmado(false);
					novo.setSubstituido(false);
					novo.setEmpresa(escala.getEmpresa()); // 🟢 multi-tenant
					escala.getMusicos().add(novo);
					escalaMusicoRepository.save(novo);
				}
			}
		} else if (dto.getMusicosIds() != null && !dto.getMusicosIds().isEmpty()) {
			for (Long usuarioId : dto.getMusicosIds()) {
				Usuario usuario = usuarioRepository.findById(usuarioId)
						.orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

				EscalaMusico novo = new EscalaMusico();
				novo.setEscala(escala);
				novo.setUsuario(usuario);

				String nomeInstrumento = (usuario.getInstrumentos() != null && !usuario.getInstrumentos().isEmpty())
						? usuario.getInstrumentos().iterator().next().getNome()
						: "Geral";
				novo.setInstrumento(nomeInstrumento);

				novo.setConfirmado(false);
				novo.setSubstituido(false);
				novo.setEmpresa(escala.getEmpresa()); // 🟢 CORRIGIDO: Adicionado o empresa_id aqui!

				escala.getMusicos().add(novo);
				escalaMusicoRepository.save(novo);
			}
		}

		Escala atualizada = escalaRepository.save(escala);
		escalaRepository.flush();
		return converterParaDTO(atualizada);
	}

	public void inativar(Long id) {
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		escala.setAtiva(false);
		escalaRepository.save(escala);
	}

	public List<EscalaResponseDTO> gerarEscalasMes(Long agendaMensalId, GerarEscalasMesRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

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

				Empresa empresa = new Empresa();
				empresa.setId(empresaIdLogada);
				escala.setEmpresa(empresa);

				escala.setNomeCultoNoite(dto.getNomeCultoNoite());
				escala.setHorarioNoite(dto.getHorarioNoite());
				escala.setHorarioNoiteFim(dto.getHorarioNoiteFim());

				if (temManha) {
					escala.setNomeCultoManha(dto.getNomeCultoManha());
					escala.setHorarioManha(dto.getHorarioManha());
					escala.setHorarioManhaFim(dto.getHorarioManhaFim());
				}

				Escala salva = escalaRepository.save(escala);

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
		// 🟢 Dica: se tiver o método no repository, filtre os instrumentos da empresa
		List<Instrumento> instrumentos = instrumentoRepository.findByEmpresaIdAndAtivoTrue(escala.getEmpresa().getId());

		Long departamentoId = escala.getDepartamento().getId();
		LocalDate dataEscala = escala.getDataEscala();
		Long empresaId = escala.getEmpresa().getId();

		for (Instrumento instrumento : instrumentos) {
			int quantidadeFinal = instrumento.getQuantidadeEscala();

			java.util.Optional<com.hope.escala.entity.ExcecaoEscalaData> excecaoOpt = excecaoEscalaDataRepository
					.findByDepartamentoIdAndDataExcecaoAndInstrumentoId(departamentoId, dataEscala,
							instrumento.getId());

			if (excecaoOpt.isPresent()) {
				com.hope.escala.entity.ExcecaoEscalaData excecao = excecaoOpt.get();

				if (Boolean.TRUE.equals(excecao.getBloqueado())) {
					quantidadeFinal = 0;
				} else if (excecao.getLimiteVagas() != null) {
					quantidadeFinal = excecao.getLimiteVagas();
				}
			}

			for (int i = 0; i < quantidadeFinal; i++) {
				Usuario usuario = escalaAutomaticaService.escolherMusicoRodizio(instrumento.getId(), departamentoId,
						escala.getId(), empresaId);

				if (usuario == null) {
					continue;
				}

				EscalaMusico escalaMusico = new EscalaMusico();
				escalaMusico.setEscala(escala);
				escalaMusico.setUsuario(usuario);
				escalaMusico.setInstrumento(instrumento.getNome());
				escalaMusico.setConfirmado(false);
				// 🟢 CORREÇÃO DO ERRO AQUI:
				escalaMusico.setEmpresa(escala.getEmpresa());

				escalaMusicoRepository.save(escalaMusico);
			}
		}
	}

	public EscalaDetalhesResponseDTO buscarDetalhesEscala(Long escalaId) {
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

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

		dto.setDataEscala(escalaMusico.getEscala().getDataEscala());
		dto.setHorarioManha(escalaMusico.getEscala().getHorarioManha());
		dto.setHorarioNoite(escalaMusico.getEscala().getHorarioNoite());

		dto.setUsuarioId(escalaMusico.getUsuario().getId());
		dto.setNomeUsuario(escalaMusico.getUsuario().getNome());
		dto.setInstrumento(escalaMusico.getInstrumento() != null ? escalaMusico.getInstrumento() : "Sem Instrumento");

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
		dto.setYoutubeVideoId(escalaMusica.getMusica().getYoutubeVideoId());
		return dto;
	}

	public void atualizarStatusAgendaMensal(Long agendaMensalId) {
		Long empresaIdLogada = securityUtils.empresaId();
		List<Escala> escalas = escalaRepository.findByAgendaMensalIdAndEmpresaIdAndAtivaTrue(agendaMensalId,
				empresaIdLogada);
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
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		if (escala.getStatus() == StatusEscala.FECHADA) {
			return;
		}

		escala.setStatus(StatusEscala.FECHADA);
		escalaRepository.save(escala);
		atualizarStatusAgendaMensal(escala.getAgendaMensal().getId());
	}

	@Transactional
	public void adicionarMusicos(Long escalaId, List<Long> musicosIds) {
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		List<EscalaMusico> antigos = escalaMusicoRepository.findByEscalaId(escalaId);
		escalaMusicoRepository.deleteAll(antigos);

		for (Long usuarioId : musicosIds) {
			Usuario usuario = usuarioRepository.findById(usuarioId)
					.orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

			EscalaMusico novo = new EscalaMusico();
			novo.setEscala(escala);
			novo.setUsuario(usuario);

			String nomeInstrumento = (usuario.getInstrumentos() != null && !usuario.getInstrumentos().isEmpty())
					? usuario.getInstrumentos().iterator().next().getNome()
					: "Geral";
			novo.setInstrumento(nomeInstrumento); // 🟢 Garante instrumento preenchido

			novo.setConfirmado(false);
			novo.setSubstituido(false);
			novo.setEmpresa(escala.getEmpresa()); // 🟢 Garante empresa preenchida
			
			escalaMusicoRepository.save(novo);
		}
	}


	@Transactional
	public EscalaResponseDTO alterarStatus(Long id, StatusEscala novoStatus) {
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		escala.setStatus(novoStatus);
		Escala salva = escalaRepository.save(escala);
		return converterParaDTO(salva);
	}

	@Transactional
	public String salvarPlaylistManual(Long escalaId, String tituloPersonalizado, List<Long> musicasIds) {
		Long empresaIdLogada = securityUtils.empresaId();

		if (!youtubeService.isConfiguradoEConectado()) {
			throw new RuntimeException(
					"A conta do YouTube não está configurada ou conectada. Vá em Configurações para autenticar.");
		}

		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada com ID: " + escalaId));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		List<EscalaMusica> antigas = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId);
		for (EscalaMusica antiga : antigas) {
			Musica musicaAntiga = antiga.getMusica();
			if (musicaAntiga.getVezesEscalada() != null && musicaAntiga.getVezesEscalada() > 0) {
				musicaAntiga.setVezesEscalada(musicaAntiga.getVezesEscalada() - 1);
			} else {
				musicaAntiga.setVezesEscalada(0);
			}
			musicaRepository.save(musicaAntiga);
		}

		escalaMusicaRepository.deleteAll(antigas);
		escalaMusicaRepository.flush();

		List<EscalaMusica> novas = new ArrayList<>();
		int ordem = 1;
		for (Long musicaId : musicasIds) {
			Musica musica = musicaRepository.findById(musicaId)
					.orElseThrow(() -> new ResourceNotFoundException("Música não encontrada: " + musicaId));

			int vezesAtual = musica.getVezesEscalada() != null ? musica.getVezesEscalada() : 0;
			musica.setVezesEscalada(vezesAtual + 1);
			musicaRepository.save(musica);

			EscalaMusica em = new EscalaMusica();
			em.setEscala(escala);
			em.setMusica(musica);
			em.setOrdem(ordem++);
			escalaMusicaRepository.save(em);
			novas.add(em);
		}

		String tituloFinal = (tituloPersonalizado != null && !tituloPersonalizado.isBlank()) ? tituloPersonalizado
				: ("" + escala.getDataEscala());

		String urlPlaylist = "";

		try {
			String accessToken = youtubeService.obterAccessToken();
			String youtubePlaylistId = youtubeService.criarPlaylistNoYoutube(accessToken, tituloFinal);

			if (youtubePlaylistId == null || youtubePlaylistId.isBlank()) {
				throw new RuntimeException("O YouTube não retornou o ID da playlist criada.");
			}

			for (EscalaMusica em : novas) {
				String videoId = em.getMusica().getYoutubeVideoId();
				if (videoId != null && !videoId.isBlank()) {
					youtubeService.adicionarVideoNaPlaylist(accessToken, youtubePlaylistId, videoId);
				}
			}

			urlPlaylist = "https://www.youtube.com/playlist?list=" + youtubePlaylistId;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao criar playlist oficial no YouTube: " + e.getMessage(), e);
		}

		escala.setLinkPlaylistManual(urlPlaylist);
		escala.setTituloPlaylistManual(tituloFinal);
		escalaRepository.save(escala);

		return urlPlaylist;
	}

	public List<EscalaMusicaResponseDTO> listarMusicasDaPlaylistManual(Long escalaId) {
		List<EscalaMusica> lista = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId);
		return lista.stream().map(this::converterMusicaDTO).collect(Collectors.toList());
	}

	@Transactional
	public void desvincularPlaylist(Long escalaId) {
		Long empresaIdLogada = securityUtils.empresaId();
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada com ID: " + escalaId));

		if (!escala.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Escala não pertence à sua instituição");
		}

		escala.setLinkPlaylistManual(null);
		escala.setTituloPlaylistManual(null);

		List<EscalaMusica> musicasEscala = escalaMusicaRepository.findByEscalaIdOrderByOrdemAsc(escalaId);

		for (EscalaMusica em : musicasEscala) {
			Musica musica = em.getMusica();
			if (musica.getVezesEscalada() != null && musica.getVezesEscalada() > 0) {
				musica.setVezesEscalada(musica.getVezesEscalada() - 1);
				musicaRepository.save(musica);
			}
		}

		escalaMusicaRepository.deleteAll(musicasEscala);
		escalaRepository.save(escala);
	}
}
