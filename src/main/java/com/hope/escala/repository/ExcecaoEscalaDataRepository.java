package com.hope.escala.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.ExcecaoEscalaData;

@Repository
public interface ExcecaoEscalaDataRepository extends JpaRepository<ExcecaoEscalaData, Long> {

	List<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecao(Long departamentoId, LocalDate dataExcecao);

	Optional<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoAndInstrumentoId(Long departamentoId,
			LocalDate dataExcecao, Long instrumentoId);

	List<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoBetween(Long departamentoId, LocalDate inicio,
			LocalDate fim);

	List<ExcecaoEscalaData> findByEmpresaId(Long empresaId);
}
