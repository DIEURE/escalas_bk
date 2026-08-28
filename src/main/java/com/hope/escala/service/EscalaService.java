package com.hope.escala.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

	public EscalaService(EscalaRepository escalaRepository, AgendaMensalRepository agendaMensalRepository,
			EscalaMusicoRepository escalaMusicoRepository, EscalaMusicaRepository escalaMusicaRepository,
			DepartamentoRepository departamentoRepository, EscalaAutomaticaService escalaAutomaticaService,
			InstrumentoRepository instrumentoRepository, SecurityUtils securityUtils,
			UsuarioRepository usuarioRepository) {

		this.escalaRepository = escalaRepository;

		this.agendaMensalRepository = agendaMensalRepository;

		this.escalaMusicoRepository = escalaMusicoRepository;

		this.escalaMusicaRepository = escalaMusicaRepository;

		this.departamentoRepository = departamentoRepository;

		this.escalaAutomaticaService = escalaAutomaticaService;

		this.instrumentoRepository = instrumentoRepository;

		this.usuarioRepository = usuarioRepository;

		this.securityUtils = securityUtils;
	}

	@Transactional
	public EscalaResponseDTO salvar(EscalaRequestDTO dto) {

		// Validar se ja existe alguma escala no mesmo horario e dia
		 boolean existeConflito = escalaRepository.existsByDataEscalaAndHorarioAndDepartamentoId(
		            dto.getDataEscala(), 
		            dto.getHorario(), 
		            dto.getDepartamentoId()
		    );

		    if (existeConflito) {
		        throw new RuntimeException("Já existe uma escala agendada para este departamento neste mesmo horário!");
		    }
		
		AgendaMensal agendaMensal = agendaMensalRepository.findById(dto.getAgendaMensalId())
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));
		
	 

		Escala escala = new Escala();

		escala.setDataEscala(dto.getDataEscala());

		escala.setHorario(dto.getHorario());

		escala.setHorarioFim(dto.getHorarioFim());

		escala.setCulto(dto.getCulto());

		escala.setObservacao(dto.getObservacao());

		escala.setTipoEscala(dto.getTipoEscala());

		escala.setAgendaMensal(agendaMensal);

		escala.setDepartamento(departamento);
		
		escala.setStatus(StatusEscala.ABERTA);

		escala.setAtiva(true);

		Escala salva = escalaRepository.save(escala);

		if (dto.getTipoEscala() == TipoEscala.AUTOMATICA) {

			gerarMusicosAutomaticamente(salva);
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

		// Adicione este log para garantir que o ID não está nulo antes de salvar
		System.out.println("DEBUG: Atualizando escala com ID: " + escala.getId()); 
		
		AgendaMensal agendaMensal = agendaMensalRepository.findById(dto.getAgendaMensalId())
				.orElseThrow(() -> new RuntimeException("Agenda mensal não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new RuntimeException("Departamento não encontrado"));
		
		System.out.println("DEBUG: Escala ID " + id + " recebida com musicos: " + dto.getMusicosIds());

		
		escala.setDataEscala(dto.getDataEscala());

		escala.setHorario(dto.getHorario());

		escala.setHorarioFim(dto.getHorarioFim());

		escala.setCulto(dto.getCulto());

		escala.setObservacao(dto.getObservacao());

		escala.setTipoEscala(dto.getTipoEscala());

		escala.setAgendaMensal(agendaMensal);

		escala.setDepartamento(departamento);

		  
		// 2. Lógica para ATUALIZAR MÚSICOS
		if (dto.getMusicosIds() != null) {
	        List<EscalaMusico> musicosAtuais = escala.getMusicos();
	        List<Long> novosIds = dto.getMusicosIds();

	        // A. Remover quem não está mais na lista
	        musicosAtuais.removeIf(m -> !novosIds.contains(m.getUsuario().getId()));

	        // B. Adicionar quem é novo
	        for (Long idUsuario : novosIds) {
	            boolean jaExiste = musicosAtuais.stream()
	                .anyMatch(m -> m.getUsuario().getId().equals(idUsuario));
	            
	            if (!jaExiste) {
	                Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow();
	                EscalaMusico novo = new EscalaMusico();
	                novo.setEscala(escala);
	                novo.setUsuario(usuario);
	                escala.getMusicos().add(novo); // O CascadeType.ALL salvará este novo
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

		while (data.getMonthValue() == yearMonth.getMonthValue()) {

			if (data.getDayOfWeek() == DayOfWeek.SUNDAY) {

				Escala manha = criarEscala(agendaMensal, departamento, data, dto.getHorarioManha(),
						dto.getNomeCultoManha(), dto.getTipoEscala(), true);

				escalasCriadas.add(manha);

				Boolean gerarAutomaticamenteNoite = !Boolean.TRUE.equals(dto.getRepetirMesmaEquipe());

				Escala noite = criarEscala(agendaMensal, departamento, data, dto.getHorarioNoite(),
						dto.getNomeCultoNoite(), dto.getTipoEscala(), gerarAutomaticamenteNoite);

				if (Boolean.TRUE.equals(dto.getRepetirMesmaEquipe())) {

					copiarMusicos(manha, noite);
				}

				escalasCriadas.add(noite);
			}

			data = data.plusDays(1);
		}

		return escalasCriadas.stream().map(this::converterParaDTO).toList();
	}

	private Escala criarEscala(AgendaMensal agendaMensal, Departamento departamento, LocalDate data, LocalTime horario,
			  String culto, TipoEscala tipoEscala, Boolean gerarAutomaticamente) {

		Escala escala = new Escala();

		escala.setAgendaMensal(agendaMensal);

		escala.setDepartamento(departamento);

		escala.setDataEscala(data);

		escala.setHorario(horario);	 

		escala.setCulto(culto);

		escala.setTipoEscala(tipoEscala);

		escala.setAtiva(true);

		escala.setStatus(StatusEscala.ABERTA);

		boolean existe = escalaRepository.existsByAgendaMensalIdAndDepartamentoIdAndDataEscalaAndHorario(
				agendaMensal.getId(), departamento.getId(), data, horario);
		if (existe) {
			throw new RuntimeException("Já existe escala para " + data + " às " + horario);
		}

		Escala escalaSalva = escalaRepository.save(escala);

		if (tipoEscala == TipoEscala.AUTOMATICA && Boolean.TRUE.equals(gerarAutomaticamente)) {
			gerarMusicosAutomaticamente(escalaSalva);
			escalaSalva.setStatus(StatusEscala.AGUARDANDO_CONFIRMACAO);
			escalaRepository.save(escalaSalva);
		}

		return escalaSalva;
	}

	private void copiarMusicos(Escala origem, Escala destino) {

		List<EscalaMusico> musicosOrigem = escalaMusicoRepository.findByEscalaId(origem.getId());

		for (EscalaMusico item : musicosOrigem) {

			EscalaMusico novo = new EscalaMusico();

			novo.setEscala(destino);

			novo.setUsuario(item.getUsuario());

			novo.setConfirmado(false);

			novo.setObservacao(item.getObservacao());

			novo.setSubstituido(false);

			escalaMusicoRepository.save(novo);
		}
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

		dto.setHorario(escala.getHorario());
		
		dto.setHorarioFim(escala.getHorarioFim());

		dto.setCulto(escala.getCulto());

		dto.setObservacao(escala.getObservacao());

		dto.setAtiva(escala.getAtiva());

		dto.setYoutubePlaylistUrl(escala.getYoutubePlaylistUrl());

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

		dto.setCulto(escalaMusico.getEscala().getCulto());

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

		dto.setCulto(escalaMusica.getEscala().getCulto());

		dto.setMusicaId(escalaMusica.getMusica().getId());

		dto.setNomeMusica(escalaMusica.getMusica().getNome());

		dto.setCantor(escalaMusica.getMusica().getCantor());

		dto.setTom(escalaMusica.getMusica().getTom());

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

	private Escala buscarEscala(Long id) {

		return escalaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Escala não encontrada"));
	}

	@Transactional
	public void adicionarMusicos(Long escalaId, List<Long> musicosIds) {
		// 1. Busca a escala
		Escala escala = escalaRepository.findById(escalaId)
				.orElseThrow(() -> new RuntimeException("Escala não encontrada"));

		// 2. Remove músicos antigos (se for substituir tudo)
		List<EscalaMusico> antigos = escalaMusicoRepository.findByEscalaId(escalaId);
		escalaMusicoRepository.deleteAll(antigos);

		// 3. Adiciona os novos músicos
		for (Long usuarioId : musicosIds) {
			Usuario usuario = usuarioRepository.findById(usuarioId)
					.orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

			EscalaMusico novo = new EscalaMusico();
			novo.setEscala(escala);
			novo.setUsuario(usuario);
			novo.setConfirmado(false); // Default
			novo.setSubstituido(false);
			escalaMusicoRepository.save(novo);
		}
	}
	
	@Transactional
	public EscalaResponseDTO alterarStatus(Long id, StatusEscala novoStatus) {
	    Escala escala = escalaRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Escala não encontrada"));
	    
	     
	    escala.setStatus(novoStatus);
	    
	    Escala salva = escalaRepository.save(escala);
	    return converterParaDTO(salva);
	}

}
