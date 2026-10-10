package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.VotoPauta;

@Repository
public interface VotoPautaRepository extends JpaRepository<VotoPauta, Long> {

    Optional<VotoPauta> findByPautaIdAndUsuarioId(Long pautaId, Long usuarioId);

    @Query("SELECT COUNT(v) FROM VotoPauta v WHERE v.opcao.id = :opcaoId")
    long countPorOpcaoId(@Param("opcaoId") Long opcaoId);

    @Query("SELECT COUNT(v) FROM VotoPauta v WHERE v.pauta.id = :pautaId")
    long countTotalVotosPauta(@Param("pautaId") Long pautaId);
    
      

        @Query("SELECT v FROM VotoPauta v " +
               "JOIN FETCH v.usuario u " +
               "JOIN FETCH v.opcao o " +
               "WHERE v.pauta.id = :pautaId")
        List<VotoPauta> findByPautaId(@Param("pautaId") Long pautaId);
    
}