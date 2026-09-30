package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.ExcecaoEscalaData;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.ExcecaoEscalaDataRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class ExcecaoEscalaService {

    private final ExcecaoEscalaDataRepository repository;
    private final SecurityUtils securityUtils;

    public ExcecaoEscalaService(ExcecaoEscalaDataRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    public List<ExcecaoEscalaData> listarPorDepartamentoEData(Long departamentoId, LocalDate data) {
        Long empresaIdLogada = securityUtils.empresaId();
        return repository.findByDepartamentoIdAndDataExcecaoAndEmpresaId(departamentoId, data, empresaIdLogada);
    }

    public List<ExcecaoEscalaData> listarPorPeriodo(Long departamentoId, LocalDate inicio, LocalDate fim) {
        Long empresaId = securityUtils.empresaId();
        
        if (departamentoId != null) {
            return repository.findByEmpresaIdAndDepartamentoIdAndDataExcecaoBetween(empresaId, departamentoId, inicio, fim);
        } else {
            return repository.findByEmpresaIdAndDataExcecaoBetween(empresaId, inicio, fim);
        }
    }

    @Transactional(readOnly = true)
    public ExcecaoEscalaData buscarPorId(Long id) {
        Long empresaId = securityUtils.empresaId();
        ExcecaoEscalaData excecao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exceção não encontrada com ID: " + id));

        if (!excecao.getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Exceção não pertence à sua congregação");
        }

        return excecao;
    }

    @Transactional
    public ExcecaoEscalaData atualizar(Long id, ExcecaoEscalaData dadosAtualizados) {
        Long empresaId = securityUtils.empresaId();

        ExcecaoEscalaData excecao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exceção não encontrada com ID: " + id));

        if (!excecao.getEmpresa().getId().equals(empresaId)) {
            throw new ResourceNotFoundException("Exceção não pertence à sua congregação");
        }

        // Atualiza os campos reais da sua entidade ExcecaoEscalaData
        excecao.setDataExcecao(dadosAtualizados.getDataExcecao());
        excecao.setDepartamentoId(dadosAtualizados.getDepartamentoId());
        excecao.setInstrumentoId(dadosAtualizados.getInstrumentoId());
        excecao.setLimiteVagas(dadosAtualizados.getLimiteVagas());
        excecao.setBloqueado(dadosAtualizados.getBloqueado() != null ? dadosAtualizados.getBloqueado() : false);
        excecao.setObservacao(dadosAtualizados.getObservacao());

        return repository.save(excecao);
    }

    public List<ExcecaoEscalaData> listarPorEmpresaLogada() {
        Long empresaId = securityUtils.empresaId();
        return repository.findByEmpresaId(empresaId);
    }

    public List<ExcecaoEscalaData> listarTodasPorEmpresa() {
        return repository.findByEmpresaId(securityUtils.empresaId());
    }

    @Transactional
    public ExcecaoEscalaData salvarOuAtualizar(ExcecaoEscalaData excecao) {
        Long empresaIdLogada = securityUtils.empresaId();

        Empresa empresa = new Empresa();
        empresa.setId(empresaIdLogada);
        excecao.setEmpresa(empresa);

        Optional<ExcecaoEscalaData> existente = repository
                .findByDepartamentoIdAndDataExcecaoAndInstrumentoIdAndEmpresaId(
                        excecao.getDepartamentoId(),
                        excecao.getDataExcecao(),
                        excecao.getInstrumentoId(),
                        empresaIdLogada
                );

        if (existente.isPresent()) {
            ExcecaoEscalaData reg = existente.get();
            reg.setLimiteVagas(excecao.getLimiteVagas());
            reg.setBloqueado(excecao.getBloqueado());
            reg.setObservacao(excecao.getObservacao());
            return repository.save(reg);
        }

        return repository.save(excecao);
    }

    public List<ExcecaoEscalaData> listarTodas() {
        Long empresaIdLogada = securityUtils.empresaId();
        return repository.findByEmpresaId(empresaIdLogada);
    }
    
    public void deletar(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();
        ExcecaoEscalaData excecao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exceção não encontrada"));

        if (!excecao.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new ResourceNotFoundException("Exceção não pertence à sua instituição");
        }

        repository.delete(excecao);
    }
}
