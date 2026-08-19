package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Departamento;

@Repository
public interface AgendaMensalRepository extends JpaRepository<AgendaMensal, Long> {

    List<AgendaMensal> findByAtivaTrue();

    List<AgendaMensal> findByDepartamentoAndAtivaTrue(Departamento departamento);

    boolean existsByMesAndAno(Integer mes, Integer ano);

    // ✅ NOVO - Verificar se existe agenda para mes/ano/departamento específico
    boolean existsByMesAndAnoAndDepartamento(Integer mes, Integer ano, Departamento departamento);

    Optional<AgendaMensal> findByMesAndAnoAndDepartamento(Integer mes, Integer ano, Departamento departamento);
}