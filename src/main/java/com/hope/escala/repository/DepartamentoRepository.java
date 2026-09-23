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
    
    // NOVO: Usado pelo SUPER_ADMIN para listar tudo sem filtro
    List<Departamento> findAllByOrderByNomeAsc();

    // Lista todos os departamentos da empresa
    @Query("SELECT d FROM Departamento d WHERE d.empresa.id = :empresaId ORDER BY d.nome ASC")
    List<Departamento> findByEmpresaIdOrderByNomeAsc(@Param("empresaId") Long empresaId);
    
    // Lista apenas ativos da empresa (usado para selects/escalas)
    @Query("SELECT d FROM Departamento d WHERE d.empresa.id = :empresaId AND d.ativo = true ORDER BY d.nome ASC")
    List<Departamento> findByEmpresaIdAndAtivoTrueOrderByNomeAsc(@Param("empresaId") Long empresaId);
    
    // Busca por ID garantindo que pertence à empresa
    @Query("SELECT d FROM Departamento d WHERE d.id = :id AND d.empresa.id = :empresaId")
    Optional<Departamento> findByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);
    
    // Validação para evitar nomes duplicados na mesma empresa
    @Query("SELECT COUNT(d) > 0 FROM Departamento d WHERE LOWER(d.nome) = LOWER(:nome) AND d.empresa.id = :empresaId")
    boolean existsByNomeIgnoreCaseAndEmpresaId(@Param("nome") String nome, @Param("empresaId") Long empresaId);

    // Validação para edição (ignora o próprio ID)
    @Query("SELECT COUNT(d) > 0 FROM Departamento d WHERE LOWER(d.nome) = LOWER(:nome) AND d.empresa.id = :empresaId AND d.id <> :id")
    boolean existsByNomeIgnoreCaseAndEmpresaIdAndIdNot(@Param("nome") String nome, @Param("empresaId") Long empresaId, @Param("id") Long id);
}
