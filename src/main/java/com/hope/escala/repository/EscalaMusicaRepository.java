package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hope.escala.entity.EscalaMusica;

public interface EscalaMusicaRepository extends JpaRepository<EscalaMusica, Long> {
	@Query("""
	        SELECT em FROM EscalaMusica em
	        LEFT JOIN FETCH em.musica m
	        LEFT JOIN FETCH em.musicaSubstituta ms
	        WHERE em.escala.id = :escalaId
	        ORDER BY em.ordem ASC
	    """)
	    List<EscalaMusica> findByEscalaIdOrderByOrdemAsc(@Param("escalaId") Long escalaId);

		    @Query("""
		        SELECT em FROM EscalaMusica em
		        LEFT JOIN FETCH em.musica m
		        WHERE em.escala.id IN :escalaIds
		        ORDER BY em.ordem ASC
		    """)
		    List<EscalaMusica> findByEscalaIdIn(@Param("escalaIds") List<Long> escalaIds);
	List<EscalaMusica> findByEmpresaId(Long empresaId);
	// EscalaMusicaRepository.java
	List<EscalaMusica> findByEscalaIdAndEscalaEmpresaIdOrderByOrdemAsc(Long escalaId, Long empresaId);

}
