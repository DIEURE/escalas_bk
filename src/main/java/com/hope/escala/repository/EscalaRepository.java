package com.hope.escala.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hope.escala.entity.Escala;

public interface EscalaRepository extends JpaRepository<Escala, Long> {

    List<Escala> findByAtivaTrue();

    List<Escala> findByAgendaMensalIdAndAtivaTrue(Long agendaMensalId);

    // Query customizada para verificar conflito de horário (manhã OU noite)
    @Query("SELECT COUNT(e) > 0 FROM Escala e WHERE e.dataEscala = :data " +
           "AND e.departamento.id = :deptId " +
           "AND (e.horarioManha = :horaManha OR e.horarioNoite = :horaNoite)")
    boolean existeConflitoHorario(
        @Param("data") LocalDate data,
        @Param("horaManha") LocalTime horaManha,
        @Param("horaNoite") LocalTime horaNoite,
        @Param("deptId") Long deptId);
}