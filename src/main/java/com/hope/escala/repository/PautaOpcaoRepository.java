package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.PautaOpcao;

@Repository
public interface PautaOpcaoRepository extends JpaRepository<PautaOpcao, Long> {
    Optional<PautaOpcao> findByIdAndPautaId(Long id, Long pautaId);
    List<PautaOpcao> findByPautaIdOrderByOrdemAsc(Long pautaId);
}