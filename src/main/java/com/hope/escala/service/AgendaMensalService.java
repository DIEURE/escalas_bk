package com.hope.escala.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.dto.request.AgendaMensalRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.AgendaMensalResponseDTO;
import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Departamento;
import com.hope.escala.enums.StatusAgendaMensal;
import com.hope.escala.exception.ObjectnotFoundException;
import com.hope.escala.repository.AgendaMensalRepository;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class AgendaMensalService {

    private final AgendaMensalRepository repository;
    private final DepartamentoRepository departamentoRepository;
    private final EscalaService escalaService;
    private final SecurityUtils securityUtils;

    public AgendaMensalService(
            AgendaMensalRepository repository,
            DepartamentoRepository departamentoRepository,
            EscalaService escalaService,
            SecurityUtils securityUtils) {
        this.repository = repository;
        this.departamentoRepository = departamentoRepository;
        this.escalaService = escalaService;
        this.securityUtils = securityUtils;
    }

    public AgendaMensalResponseDTO salvar(AgendaMensalRequestDTO dto) {

        // ← NOVO: Validar permissão de segurança
        // Verifica se o usuário é ADMIN ou se pertence ao departamento
        if (!securityUtils.isAdmin() && !securityUtils.pertenceAoDepartamento(dto.getDepartamentoId())) {
            throw new RuntimeException(
                "Você não possui acesso a este departamento."
            );
        }

        // Validar departamento
        Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
            .orElseThrow(() -> new ObjectnotFoundException("Departamento não encontrado"));

        // Verificar duplicata
        boolean existe = repository.existsByMesAndAnoAndDepartamento(
            dto.getMes(),
            dto.getAno(),
            departamento
        );

        if (existe) {
            throw new RuntimeException(
                "Agenda mensal já cadastrada para este mês/ano/departamento"
            );
        }

        AgendaMensal agenda = new AgendaMensal();
        agenda.setMes(dto.getMes());
        agenda.setAno(dto.getAno());
        agenda.setDescricao(dto.getDescricao());
        agenda.setDepartamento(departamento);
        agenda.setStatus(StatusAgendaMensal.EM_MONTAGEM);
        agenda.setAtiva(true);

        AgendaMensal agendaSalva = repository.save(agenda);
        return converterParaDTO(agendaSalva);
    }

    public List<AgendaMensalResponseDTO> listar() {
        return repository.findByAtivaTrue()
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    public List<AgendaMensalResponseDTO> listarPorDepartamento(Long departamentoId) {
        Departamento departamento = departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new ObjectnotFoundException("Departamento não encontrado"));

        return repository.findByDepartamentoAndAtivaTrue(departamento)
            .stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }

    public AgendaMensalResponseDTO buscarPorId(Long id) {
        AgendaMensal agenda = repository.findById(id)
            .orElseThrow(() -> new ObjectnotFoundException("Agenda não encontrada"));
        return converterParaDTO(agenda);
    }

    public AgendaMensalResponseDTO atualizar(Long id, AgendaMensalRequestDTO dto) {
        AgendaMensal agenda = repository.findById(id)
            .orElseThrow(() -> new ObjectnotFoundException("Agenda não encontrada"));

        Departamento departamento = departamentoRepository.findById(dto.getDepartamentoId())
            .orElseThrow(() -> new ObjectnotFoundException("Departamento não encontrado"));

        agenda.setMes(dto.getMes());
        agenda.setAno(dto.getAno());
        agenda.setDescricao(dto.getDescricao());
        agenda.setDepartamento(departamento);

        AgendaMensal atualizada = repository.save(agenda);
        return converterParaDTO(atualizada);
    }

    public void inativar(Long id) {
        AgendaMensal agenda = repository.findById(id)
            .orElseThrow(() -> new ObjectnotFoundException("Agenda não encontrada"));
        agenda.setAtiva(false);
        repository.save(agenda);
    }

    @Transactional
    public void gerarEscalasMes(Long agendaMensalId, GerarEscalasMesRequestDTO dto) {

        AgendaMensal agenda = repository.findById(agendaMensalId)
            .orElseThrow(() -> new ObjectnotFoundException(
                "Agenda mensal não encontrada com ID: " + agendaMensalId
            ));

        // ← NOVO: Validação de segurança PRIMEIRO
        if (!securityUtils.isAdmin() && !securityUtils.pertenceAoDepartamento(dto.getDepartamentoId())) {
            throw new RuntimeException(
                "Você não possui acesso a este departamento."
            );
        }

        // Depois valida se o departamento da agenda corresponde
        if (!agenda.getDepartamento().getId().equals(dto.getDepartamentoId())) {
            throw new RuntimeException(
                "O departamento informado não corresponde ao da agenda mensal"
            );
        }

        escalaService.gerarEscalasMes(agendaMensalId, dto);

        agenda.setStatus(StatusAgendaMensal.FINALIZADA);
        repository.save(agenda);
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