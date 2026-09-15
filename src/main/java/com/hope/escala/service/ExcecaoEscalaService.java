package com.hope.escala.service;

import com.hope.escala.entity.ExcecaoEscalaData;
import com.hope.escala.repository.ExcecaoEscalaDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExcecaoEscalaService {

    private final ExcecaoEscalaDataRepository repository;

    public ExcecaoEscalaService(ExcecaoEscalaDataRepository repository) {
        this.repository = repository;
    }

    public List<ExcecaoEscalaData> listarPorDepartamentoEData(Long departamentoId, LocalDate data) {
        return repository.findByDepartamentoIdAndDataExcecao(departamentoId, data);
    }

    public List<ExcecaoEscalaData> listarPorPeriodo(Long departamentoId, LocalDate inicio, LocalDate fim) {
        return repository.findByDepartamentoIdAndDataExcecaoBetween(departamentoId, inicio, fim);
    }

    public ExcecaoEscalaData salvarOuAtualizar(ExcecaoEscalaData excecao) {
        // Verifica se já existe exceção para a mesma data, departamento e instrumento
        Optional<ExcecaoEscalaData> existente = repository
                .findByDepartamentoIdAndDataExcecaoAndInstrumentoId(
                        excecao.getDepartamentoId(),
                        excecao.getDataExcecao(),
                        excecao.getInstrumentoId()
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
        return repository.findAll();
    }
    
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
