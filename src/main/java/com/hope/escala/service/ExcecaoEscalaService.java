package com.hope.escala.service;

import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.ExcecaoEscalaData;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.ExcecaoEscalaDataRepository;
import com.hope.escala.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        // Certifique-se de ter o método correspondente no repository filtrando por empresaId
        return repository.findByDepartamentoIdAndDataExcecaoAndEmpresaId(departamentoId, data, empresaIdLogada);
    }

    public List<ExcecaoEscalaData> listarPorPeriodo(Long departamentoId, LocalDate inicio, LocalDate fim) {
        Long empresaId = securityUtils.empresaId(); // 🟢 Pega a empresa do tenant logado
        
        if (departamentoId != null) {
            return repository.findByEmpresaIdAndDepartamentoIdAndDataExcecaoBetween(empresaId, departamentoId, inicio, fim);
        } else {
            // Se não passar o departamento, traz todas as exceções do mês daquela empresa/igreja
            return repository.findByEmpresaIdAndDataExcecaoBetween(empresaId, inicio, fim);
        }
    }

    public List<ExcecaoEscalaData> listarPorEmpresaLogada() {
        Long empresaId = securityUtils.empresaId(); // ou a sua forma de pegar a empresa logada
        return repository.findByEmpresaId(empresaId);
    }


    public List<ExcecaoEscalaData> listarTodasPorEmpresa() {
        return repository.findByEmpresaId(securityUtils.empresaId());
    }


    public ExcecaoEscalaData salvarOuAtualizar(ExcecaoEscalaData excecao) {
        Long empresaIdLogada = securityUtils.empresaId();

        // 🟢 Associa a empresa logada de forma segura
        Empresa empresa = new Empresa();
        empresa.setId(empresaIdLogada);
        excecao.setEmpresa(empresa);

        // Verifica se já existe exceção para a mesma data, departamento, instrumento e empresa
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
        // Certifique-se de ter o método no repository, ex: findByEmpresaId(empresaId)
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
