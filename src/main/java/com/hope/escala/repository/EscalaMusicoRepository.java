package com.hope.escala.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.EscalaMusico;

@Repository
public interface EscalaMusicoRepository extends JpaRepository<EscalaMusico, Long> {

    List<EscalaMusico> findByEscalaId(Long escalaId);

    List<EscalaMusico> findByUsuarioId(Long usuarioId);

    // 🟢 Busca os registros pela congregação
    @Query("SELECT em FROM EscalaMusico em WHERE em.empresa.id = :empresaId")
    List<EscalaMusico> findByEmpresaId(@Param("empresaId") Long empresaId);

    void deleteByEscalaId(Long escalaId);

    // 🟢 Busca os IDs dos músicos já escalados para o evento/escala
    @Query("SELECT em.usuario.id FROM EscalaMusico em WHERE em.escala.id = :escalaId")
    List<Long> buscarUsuariosJaEscalados(@Param("escalaId") Long escalaId);

    // 🟢 Busca a data da última escala em que o músico tocou (usando dataEscala)
    @Query("SELECT MAX(em.escala.dataEscala) FROM EscalaMusico em WHERE em.usuario.id = :usuarioId")
    LocalDate buscarUltimaEscalaDoMusico(@Param("usuarioId") Long usuarioId);
}
