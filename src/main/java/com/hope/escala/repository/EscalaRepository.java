package com.hope.escala.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.Escala;

public interface EscalaRepository extends JpaRepository<Escala, Long> {
	List<Escala> findByAtivaTrue();

	List<Escala> findByAgendaMensalIdAndAtivaTrue(Long agendaMensalId);

	boolean existsByDataEscalaAndCultoAndAtivaTrue(LocalDate dataEscala, String culto);
	
	boolean existsByAgendaMensalIdAndDepartamentoIdAndDataEscalaAndHorario( Long agendaMensalId, Long departamentoId, LocalDate dataEscala, LocalTime horario );
}
