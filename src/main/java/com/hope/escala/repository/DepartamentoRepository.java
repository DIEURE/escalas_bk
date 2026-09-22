package com.hope.escala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hope.escala.entity.Departamento;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    
    // NOVO: Usado pelo SUPER_ADMIN para listar tudo sem filtro
    List<Departamento> findAllByOrderByNomeAsc();

    // Lista todos os departamentos da empresa logada
    List<Departamento> findByEmpresaIdOrderByNomeAsc(Long empresaId);
    
    // Lista apenas ativos da empresa (usado para selects/escalas)
    List<Departamento> findByEmpresaIdAndAtivoTrueOrderByNomeAsc(Long empresaId);
    
    // Busca por ID garantindo que pertence à empresa
    Optional<Departamento> findByIdAndEmpresaId(Long id, Long empresaId);
    
    // Validação para evitar nomes duplicados na mesma empresa
    boolean existsByNomeIgnoreCaseAndEmpresaId(String nome, Long empresaId);
    boolean existsByNomeIgnoreCaseAndEmpresaIdAndIdNot(String nome, Long empresaId, Long id);
}
