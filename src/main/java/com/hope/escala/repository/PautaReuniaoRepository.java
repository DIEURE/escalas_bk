package com.hope.escala.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.PautaReuniao;

@Repository
public interface PautaReuniaoRepository extends JpaRepository<PautaReuniao, Long> {
    Optional<PautaReuniao> findByIdAndEmpresaId(Long id, Long empresaId);
}
