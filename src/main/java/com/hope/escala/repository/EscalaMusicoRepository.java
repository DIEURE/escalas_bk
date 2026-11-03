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

    @Query("SELECT em FROM EscalaMusico em " +
           "LEFT JOIN FETCH em.usuario u " +
           "WHERE em.escala.id = :escalaId " +
           "ORDER BY em.id ASC")
    List<EscalaMusico> findByEscalaId(@Param("escalaId") Long escalaId);

    List<EscalaMusico> findByUsuarioId(Long usuarioId);

    @Query("SELECT em FROM EscalaMusico em " +
           "JOIN FETCH em.escala e " +
           "JOIN FETCH em.usuario u " +
           "WHERE em.usuario.id = :usuarioId " +
           "AND em.empresa.id = :empresaId " +
           "AND e.ativa = true " +
           "AND e.dataEscala >= :hoje " +
           "ORDER BY e.dataEscala ASC")
    List<EscalaMusico> findMinhasEscalasPorUsuarioId(
            @Param("usuarioId") Long usuarioId,
            @Param("empresaId") Long empresaId,
            @Param("hoje") LocalDate hoje);

    @Query("SELECT em FROM EscalaMusico em WHERE em.empresa.id = :empresaId")
    List<EscalaMusico> findByEmpresaId(@Param("empresaId") Long empresaId);

    void deleteByEscalaId(Long escalaId);

    // 🟢 IDs dos usuários já escalados na escala (qualquer instrumento)
    @Query("SELECT em.usuario.id FROM EscalaMusico em " +
           "WHERE em.escala.id = :escalaId " +
           "AND em.usuario IS NOT NULL")
    List<Long> buscarUsuariosJaEscalados(@Param("escalaId") Long escalaId);

    // 🟢 IDs dos usuários já escalados NAQUELE INSTRUMENTO na escala
    @Query("SELECT em.usuario.id FROM EscalaMusico em " +
           "WHERE em.escala.id = :escalaId " +
           "AND em.instrumento = :instrumento " +
           "AND em.usuario IS NOT NULL")
    List<Long> buscarUsuariosJaEscaladosNoInstrumento(
            @Param("escalaId") Long escalaId,
            @Param("instrumento") String instrumento);

    // 🟢 Verifica se o usuário já está escalado naquele instrumento na escala
    @Query("SELECT COUNT(em) > 0 FROM EscalaMusico em " +
           "WHERE em.escala.id = :escalaId " +
           "AND em.usuario.id = :usuarioId " +
           "AND em.instrumento = :instrumento")
    boolean existsByEscalaIdAndUsuarioIdAndInstrumento(
            @Param("escalaId") Long escalaId,
            @Param("usuarioId") Long usuarioId,
            @Param("instrumento") String instrumento);

    @Query("SELECT MAX(em.escala.dataEscala) FROM EscalaMusico em WHERE em.usuario.id = :usuarioId")
    LocalDate buscarUltimaEscalaDoMusico(@Param("usuarioId") Long usuarioId);
}