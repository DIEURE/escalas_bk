
package com.hope.escala.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hope.escala.entity.EscalaMusico;

public interface EscalaMusicoRepository
        extends JpaRepository<EscalaMusico, Long> {

    List<EscalaMusico> findByEscalaId(
            Long escalaId
    );

    @Query("""
        SELECT MAX(em.escala.dataEscala)
        FROM EscalaMusico em
        WHERE em.usuario.id = :usuarioId
    """)
    LocalDate buscarUltimaEscalaDoMusico(
            Long usuarioId
    );

    @Query("""
        SELECT em.usuario.id
        FROM EscalaMusico em
        WHERE em.escala.id = :escalaId
    """)
    List<Long> buscarUsuariosJaEscalados(
            Long escalaId
    );
    
    List<EscalaMusico> findByUsuarioId(Long usuarioId);
}

