package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.EscalaMusica;

public interface EscalaMusicaRepository extends JpaRepository<EscalaMusica, Long> {
	List<EscalaMusica> findByEscalaIdOrderByOrdemAsc(Long escalaId);
	List<EscalaMusica> findByEmpresaId(Long empresaId);
	// EscalaMusicaRepository.java
	List<EscalaMusica> findByEscalaIdAndEscalaEmpresaIdOrderByOrdemAsc(Long escalaId, Long empresaId);

}
