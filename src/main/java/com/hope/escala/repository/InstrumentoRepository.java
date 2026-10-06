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
	List<Instrumento> findByEmpresaIdAndAtivoTrue(Long empresaId);

	// 🟢 SUPER ADMIN: Lista todos trazendo a congregação sem lazy proxy
    @Query("SELECT i FROM Instrumento i LEFT JOIN FETCH i.empresa ORDER BY i.nome ASC")
    List<Instrumento> findAllByOrderByNomeAsc();

    // 🟢 ADMIN / LÍDER: Lista por congregação trazendo a congregação no mesmo SELECT
    @Query("SELECT i FROM Instrumento i LEFT JOIN FETCH i.empresa WHERE i.empresa.id = :empresaId ORDER BY i.nome ASC")
    List<Instrumento> findByEmpresaIdOrderByNomeAsc(@Param("empresaId") Long empresaId);

    // 🟢 Lista apenas ativos trazendo a congregação
    @Query("SELECT i FROM Instrumento i LEFT JOIN FETCH i.empresa WHERE i.empresa.id = :empresaId AND i.ativo = true ORDER BY i.nome ASC")
    List<Instrumento> findByEmpresaIdAndAtivoTrueOrderByNomeAsc(@Param("empresaId") Long empresaId);
    
    List<Instrumento> findByEmpresaIdAndDepartamentoIdAndAtivoTrue(Long empresaId, Long departamentoId);

}