package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    
    // 🟢 SUPER_ADMIN: Lista tudo com a Empresa já carregada
    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa ORDER BY d.nome ASC")
    List<Departamento> findAllByOrderByNomeAsc();

    // 🟢 ADMIN / LÍDER: Lista da empresa trazendo a Empresa no mesmo SELECT
    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa WHERE d.empresa.id = :empresaId ORDER BY d.nome ASC")
    List<Departamento> findByEmpresaIdOrderByNomeAsc(@Param("empresaId") Long empresaId);
    
    // 🟢 Lista ativos trazendo a Empresa
    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa WHERE d.empresa.id = :empresaId AND d.ativo = true ORDER BY d.nome ASC")
    List<Departamento> findByEmpresaIdAndAtivoTrueOrderByNomeAsc(@Param("empresaId") Long empresaId);
    
    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa WHERE d.id = :id AND d.empresa.id = :empresaId")
    Optional<Departamento> findByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);
    
    @Query("SELECT COUNT(d) > 0 FROM Departamento d WHERE LOWER(d.nome) = LOWER(:nome) AND d.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(d) > 0 FROM Departamento d WHERE LOWER(d.nome) = LOWER(:nome) AND d.empresa.id = :empresaId AND d.id <> :id")
    boolean existsByNomeIgnoreCaseAndEmpresaIdAndIdNot(@Param("nome") String nome, @Param("empresaId") Long empresaId, @Param("id") Long id);

    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa WHERE d.empresa.id = :empresaId")
    List<Departamento> findByEmpresaIdComEmpresa(@Param("empresaId") Long empresaId);

    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa")
    List<Departamento> findAllComEmpresa();

    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.empresa WHERE d.id = :id")
    Optional<Departamento> findByIdComEmpresa(@Param("id") Long id);
}
