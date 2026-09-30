package com.hope.escala.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.SuspensaoVoluntario;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuspensaoRepository extends JpaRepository<SuspensaoVoluntario, Long> {

    @Query("SELECT s.usuarioId FROM SuspensaoVoluntario s " +
           "WHERE s.departamentoId = :departamentoId " +
           "  AND s.mesBloqueio = :mes " +
           "  AND s.anoBloqueio = :ano " +
           "  AND s.empresaId = :empresaId " +
           "  AND s.ativo = true")
    List<Long> buscarIdsSuspensosNoMes(
            @Param("departamentoId") Long departamentoId,
            @Param("mes") Integer mes,
            @Param("ano") Integer ano,
            @Param("empresaId") Long empresaId);

    List<SuspensaoVoluntario> findByDepartamentoIdAndMesBloqueioAndAnoBloqueioAndEmpresaId(
            Long departamentoId, Integer mesBloqueio, Integer anoBloqueio, Long empresaId);

    List<SuspensaoVoluntario> findByEmpresaId(Long empresaId);

    Optional<SuspensaoVoluntario> findByUsuarioIdAndDepartamentoIdAndMesBloqueioAndAnoBloqueioAndEmpresaId(
            Long usuarioId, Long departamentoId, Integer mesBloqueio, Integer anoBloqueio, Long empresaId);
}
