package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.AtaReuniao;

@Repository
public interface AtaReuniaoRepository extends JpaRepository<AtaReuniao, Long> {

    @Query("SELECT DISTINCT a FROM AtaReuniao a LEFT JOIN FETCH a.pautas WHERE a.id = :id AND a.empresa.id = :empresaId")
    Optional<AtaReuniao> findByIdAndEmpresaIdWithPautas(@Param("id") Long id, @Param("empresaId") Long empresaId);

    Optional<AtaReuniao> findByIdAndEmpresaId(Long id, Long empresaId);

    List<AtaReuniao> findByEmpresaIdOrderByDataReuniaoDesc(Long empresaId);

    List<AtaReuniao> findByEmpresaIdAndDepartamentoIdOrderByDataReuniaoDesc(Long empresaId, Long departamentoId);
}
