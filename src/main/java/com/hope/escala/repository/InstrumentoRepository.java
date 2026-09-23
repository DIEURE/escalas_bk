package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hope.escala.entity.Instrumento;

public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
	List<Instrumento> findByEmpresaId(Long empresaId);
	// Se o seu repositório se chama InstrumentoRepository:
	@Query("SELECT COUNT(i) > 0 FROM Instrumento i WHERE LOWER(TRIM(i.nome)) = LOWER(TRIM(:nome)) AND i.empresa.id = :empresaId")
	boolean existsByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);

	
}