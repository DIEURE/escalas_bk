package com.hope.escala.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.dto.request.AgendaMensalRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.AgendaMensalResponseDTO;
import com.hope.escala.dto.response.DataCultoResponseDTO;
import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Escala;
import com.hope.escala.enums.StatusAgendaMensal;
import com.hope.escala.exception.ObjectnotFoundException;
import com.hope.escala.repository.AgendaMensalRepository;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class AgendaMensalService {

	private final AgendaMensalRepository agendaMensalRepository;
	private final DepartamentoRepository departamentoRepository;
	private final EscalaService escalaService;
	private final EscalaRepository escalaRepository;
	private final SecurityUtils securityUtils;
	private final EmpresaRepository empresaRepository;

	public AgendaMensalService(AgendaMensalRepository agendaMensalRepository,
			DepartamentoRepository departamentoRepository, 
			EscalaService escalaService, 
			EscalaRepository escalaRepository,
			SecurityUtils securityUtils,
			EmpresaRepository empresaRepository) {

		this.departamentoRepository = departamentoRepository;
		this.escalaService = escalaService;
		this.escalaRepository = escalaRepository;
		this.securityUtils = securityUtils;
		this.empresaRepository = empresaRepository;
		this.agendaMensalRepository = agendaMensalRepository;
	}

	@Transactional(readOnly = true)
	public List<DataCultoResponseDTO> buscarDatasPorMesEAno(int mes, int ano) {
		Long empresaId = securityUtils.empresaId();
		if (empresaId == null) {
			throw new RuntimeException("Empresa não identificada na sessão.");
		}

		LocalDate inicio = LocalDate.of(ano, mes, 1);
		LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

		// 1. Busca as escalas já cadastradas para a congregação no período
		List<Escala> escalas = escalaRepository.findByEmpresaIdAndDataEscalaBetweenOrderByDataEscalaAsc(empresaId, inicio, fim);

		if (!escalas.isEmpty()) {
			List<DataCultoResponseDTO> listaCultos = new ArrayList<>();

			for (Escala e : escalas) {
				// 🟢 Culto da Manhã: só entra SE foi marcado na geração da escala
				if (e.getHorarioManha() != null || (e.getNomeCultoManha() != null && !e.getNomeCultoManha().isBlank())) {
					String nomeManha = (e.getNomeCultoManha() != null && !e.getNomeCultoManha().isBlank()) 
							? e.getNomeCultoManha() 
							: "Culto da Manhã";
					String horaManha = e.getHorarioManha() != null ? e.getHorarioManha().toString() : "09:00";
					listaCultos.add(new DataCultoResponseDTO(e.getDataEscala(), nomeManha, horaManha));
				}

				// Culto da Noite (padrão principal)
				String nomeNoite = (e.getNomeCultoNoite() != null && !e.getNomeCultoNoite().isBlank()) 
						? e.getNomeCultoNoite() 
						: "Culto de Celebração";
				String horaNoite = e.getHorarioNoite() != null ? e.getHorarioNoite().toString() : "18:00";
				listaCultos.add(new DataCultoResponseDTO(e.getDataEscala(), nomeNoite, horaNoite));
			}

			return listaCultos;
		}

		// 2. Fallback (antes de gerar as escalas): apenas domingos à noite
		List<DataCultoResponseDTO> domingosPadrao = new ArrayList<>();
		LocalDate dataCorrente = inicio;

		while (!dataCorrente.isAfter(fim)) {
			// Apenas Domingos (DayOfWeek = 7)
			if (dataCorrente.getDayOfWeek().getValue() == 7) {
				domingosPadrao.add(new DataCultoResponseDTO(dataCorrente, "Culto de Celebração", "19:00"));
			}
			dataCorrente = dataCorrente.plusDays(1);
		}

		return domingosPadrao;
	}


	public List<AgendaMensal> listarPorEmpresa() {
		return agendaMensalRepository.findByEmpresaId(securityUtils.empresaId());
	}

	public AgendaMensalResponseDTO salvar(AgendaMensalRequestDTO dto) {

		  Empresa empresa = empresaRepository.findById(securityUtils.empresaId())
	                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
	        
		// ← Validar permissão de segurança
		if (!securityUtils.isAdmin() && !securityUtils.pertenceAoDepartamento(dto.getDepartamentoId())) {
			throw new RuntimeException("Você não possui acesso a este departamento.");
		}

		// Validar departamento
		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new ObjectnotFoundException("Departamento não encontrado"));

		// Verificar duplicata
		boolean existe = agendaMensalRepository.existsByMesAndAnoAndDepartamento(dto.getMes(), dto.getAno(), departamento);

		if (existe) {
			throw new RuntimeException("Agenda mensal já cadastrada para este mês/ano/departamento");
		}

		AgendaMensal agenda = new AgendaMensal();
		agenda.setMes(dto.getMes());
		agenda.setAno(dto.getAno());
		agenda.setDescricao(dto.getDescricao());
		agenda.setDepartamento(departamento);
		agenda.setStatus(StatusAgendaMensal.EM_MONTAGEM);
		agenda.setAtiva(true);
		agenda.setEmpresa(empresa);

		AgendaMensal agendaSalva = agendaMensalRepository.save(agenda);
		return converterParaDTO(agendaSalva);
	}

	public List<AgendaMensalResponseDTO> listar() {
		return agendaMensalRepository.findByAtivaTrue().stream().map(this::converterParaDTO).collect(Collectors.toList());
	}

	public List<AgendaMensalResponseDTO> listarPorDepartamento(Long departamentoId) {
		Departamento departamento = departamentoRepository.findById(departamentoId)
				.orElseThrow(() -> new ObjectnotFoundException("Departamento não encontrado"));

		return agendaMensalRepository.findByDepartamentoAndAtivaTrue(departamento).stream().map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public AgendaMensalResponseDTO buscarPorId(Long id) {
		AgendaMensal agenda = agendaMensalRepository.findById(id)
				.orElseThrow(() -> new ObjectnotFoundException("Agenda não encontrada"));
		return converterParaDTO(agenda);
	}

	public AgendaMensalResponseDTO atualizar(Long id, AgendaMensalRequestDTO dto) {
		AgendaMensal agenda = agendaMensalRepository.findById(id)
				.orElseThrow(() -> new ObjectnotFoundException("Agenda não encontrada"));

		Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
				.orElseThrow(() -> new ObjectnotFoundException("Departamento não encontrado"));

		agenda.setMes(dto.getMes());
		agenda.setAno(dto.getAno());
		agenda.setDescricao(dto.getDescricao());
		agenda.setDepartamento(departamento);

		AgendaMensal atualizada = agendaMensalRepository.save(agenda);
		return converterParaDTO(atualizada);
	}

	public void inativar(Long id) {
		AgendaMensal agenda = agendaMensalRepository.findById(id)
				.orElseThrow(() -> new ObjectnotFoundException("Agenda não encontrada"));
		agenda.setAtiva(false);
		agendaMensalRepository.save(agenda);
	}

	@Transactional
	public void gerarEscalasMes(Long agendaMensalId, GerarEscalasMesRequestDTO dto) {

		AgendaMensal agenda = agendaMensalRepository.findById(agendaMensalId).orElseThrow(
				() -> new ObjectnotFoundException("Agenda mensal não encontrada com ID: " + agendaMensalId));

		if (!securityUtils.isAdmin() && !securityUtils.pertenceAoDepartamento(dto.getDepartamentoId())) {
			throw new RuntimeException("Você não possui acesso a este departamento.");
		}

		if (!agenda.getDepartamento().getId().equals(dto.getDepartamentoId())) {
			throw new RuntimeException("O departamento informado não corresponde ao da agenda mensal");
		}

		escalaService.gerarEscalasMes(agendaMensalId, dto);

		agenda.setStatus(StatusAgendaMensal.FINALIZADA);
		agendaMensalRepository.save(agenda);
	}

	private AgendaMensalResponseDTO converterParaDTO(AgendaMensal agenda) {
		AgendaMensalResponseDTO dto = new AgendaMensalResponseDTO();
		dto.setId(agenda.getId());
		dto.setMes(agenda.getMes());
		dto.setAno(agenda.getAno());
		dto.setDescricao(agenda.getDescricao());
		dto.setAtiva(agenda.getAtiva());
		dto.setDepartamentoId(agenda.getDepartamento().getId());
		dto.setDepartamentoNome(agenda.getDepartamento().getNome());
		dto.setStatus(agenda.getStatus());
		
		return dto;
	}
}
