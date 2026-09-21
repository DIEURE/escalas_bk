package com.hope.escala.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.ExcecaoEscalaData;

@Repository
public interface ExcecaoEscalaDataRepository extends JpaRepository<ExcecaoEscalaData, Long> {

// 🟢 Métodos antigos (mantidos caso precise em outros contextos)
	List<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecao(Long departamentoId, LocalDate dataExcecao);

	List<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoBetween(Long departamentoId, LocalDate inicio,
			LocalDate fim);

	Optional<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoAndInstrumentoId(Long departamentoId,
			LocalDate dataExcecao, Long instrumentoId);

// 🟢 Novos métodos com suporte total ao Multi-Tenant (Empresa)
	List<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoAndEmpresaId(Long departamentoId, LocalDate dataExcecao,
			Long empresaId);

	List<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoBetweenAndEmpresaId(Long departamentoId, LocalDate inicio,
			LocalDate fim, Long empresaId);

	Optional<ExcecaoEscalaData> findByDepartamentoIdAndDataExcecaoAndInstrumentoIdAndEmpresaId(Long departamentoId,
			LocalDate dataExcecao, Long instrumentoId, Long empresaId);

	List<ExcecaoEscalaData> findByEmpresaId(Long empresaId);

	// 🟢 Busca por empresa e período usando o nome correto do atributo (dataExcecao)
    List<ExcecaoEscalaData> findByEmpresaIdAndDataExcecaoBetween(Long empresaId, LocalDate inicio, LocalDate fim);

    // 🟢 Busca por empresa, departamento e período
    List<ExcecaoEscalaData> findByEmpresaIdAndDepartamentoIdAndDataExcecaoBetween(Long empresaId, Long departamentoId, LocalDate inicio, LocalDate fim);
}
