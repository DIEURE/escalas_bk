package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Departamento;

@Repository
public interface AgendaMensalRepository extends JpaRepository<AgendaMensal, Long> {

    List<AgendaMensal> findByAtivaTrue();

    List<AgendaMensal> findByDepartamentoAndAtivaTrue(Departamento departamento);

    List<AgendaMensal> findByEmpresaId(Long empresaId);

    boolean existsByMesAndAno(Integer mes, Integer ano);

    boolean existsByMesAndAnoAndDepartamento(Integer mes, Integer ano, Departamento departamento);

    Optional<AgendaMensal> findByMesAndAnoAndDepartamento(Integer mes, Integer ano, Departamento departamento);

    // 🟢 Pode retornar 0, 1 ou N (uma por departamento)
    @Query("SELECT a FROM AgendaMensal a WHERE a.mes = :mes AND a.ano = :ano AND a.empresa.id = :empresaId")
    List<AgendaMensal> findByMesEAnoEEmpresaId(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("empresaId") Long empresaId);

    // 🟢 Busca a agenda da empresa + mês + ano + departamento (1 registro esperado)
    @Query("SELECT a FROM AgendaMensal a WHERE a.mes = :mes AND a.ano = :ano " +
           "AND a.empresa.id = :empresaId AND a.departamento.id = :departamentoId")
    Optional<AgendaMensal> findByMesEAnoEEmpresaIdAndDepartamentoId(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("empresaId") Long empresaId,
            @Param("departamentoId") Long departamentoId);
}