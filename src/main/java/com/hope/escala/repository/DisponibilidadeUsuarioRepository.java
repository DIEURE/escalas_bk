package com.hope.escala.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.DisponibilidadeUsuario;

@Repository
public interface DisponibilidadeUsuarioRepository extends JpaRepository<DisponibilidadeUsuario, Long> {

    @Query("SELECT d.usuario.id FROM DisponibilidadeUsuario d " +
           "WHERE d.dataDisponivel = :data " +
           "  AND d.empresa.id = :empresaId")
    List<Long> buscarIdsUsuariosDisponiveisNaData(
            @Param("data") LocalDate data, 
            @Param("empresaId") Long empresaId);

    @Query("SELECT d.dataDisponivel FROM DisponibilidadeUsuario d " +
           "WHERE d.usuario.id = :usuarioId " +
           "  AND d.empresa.id = :empresaId " +
           "  AND d.dataDisponivel BETWEEN :inicio AND :fim " +
           "ORDER BY d.dataDisponivel ASC")
    List<LocalDate> buscarDatasMarcadasPorPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    @Modifying
    @Query("DELETE FROM DisponibilidadeUsuario d " +
           "WHERE d.usuario.id = :usuarioId " +
           "  AND d.empresa.id = :empresaId " +
           "  AND d.dataDisponivel BETWEEN :inicio AND :fim")
    void removerPorUsuarioPeriodoEEmpresa(
            @Param("usuarioId") Long usuarioId,
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}
