package com.hope.escala.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hope.escala.entity.Escala;

public interface EscalaRepository extends JpaRepository<Escala, Long> {

    List<Escala> findByAtivaTrue();

    List<Escala> findByAgendaMensalIdAndAtivaTrue(Long agendaMensalId);
    
    List<Escala> findByEmpresaId(Long empresaId);
    
    // 🟢 Método padrão para listagem geral filtrada por empresa e ativas
    List<Escala> findByEmpresaIdAndAtivaTrue(Long empresaId);

    // 🟢 Método que estava faltando no repositório para a agenda mensal com multi-tenant
    List<Escala> findByAgendaMensalIdAndEmpresaIdAndAtivaTrue(Long agendaMensalId, Long empresaId);
    
    List<Escala> findByEmpresaIdAndDataEscalaBetweenOrderByDataEscalaAsc(Long empresaId, LocalDate inicio, LocalDate fim);


    // Query customizada para verificar conflito de horário (manhã OU noite)
    @Query("SELECT COUNT(e) > 0 FROM Escala e WHERE e.dataEscala = :data " +
            "AND e.departamento.id = :deptId " +
            "AND e.empresa.id = :empresaId " +
            "AND (e.horarioManha = :horaManha OR e.horarioNoite = :horaNoite)")
     boolean existeConflitoHorario(
         @Param("data") LocalDate data,
         @Param("horaManha") LocalTime horaManha,
         @Param("horaNoite") LocalTime horaNoite,
         @Param("deptId") Long deptId,
         @Param("empresaId") Long empresaId);

    
    @Query("SELECT DISTINCT e FROM Escala e " +
            "LEFT JOIN FETCH e.musicos m " +
            "LEFT JOIN FETCH m.usuario u " +
            "WHERE e.departamento.id = :departamentoId " +
            "AND e.empresa.id = :empresaId " +
            "AND (e.ativa = true OR e.ativa IS NULL) " +
            "AND e.dataEscala BETWEEN :inicio AND :fim " +
            "ORDER BY e.dataEscala ASC")
     List<Escala> buscarPorPeriodoEDepartamento(
             @Param("departamentoId") Long departamentoId,
             @Param("empresaId") Long empresaId,
             @Param("inicio") LocalDate inicio,
             @Param("fim") LocalDate fim);





 // EscalaRepository.java
    Optional<Escala> findByIdAndEmpresaId(Long id, Long empresaId);


}