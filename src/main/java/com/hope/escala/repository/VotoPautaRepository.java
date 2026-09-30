package com.hope.escala.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.VotoPauta;
import com.hope.escala.enums.TipoVoto;

@Repository
public interface VotoPautaRepository extends JpaRepository<VotoPauta, Long> {

    Optional<VotoPauta> findByPautaIdAndUsuarioId(Long pautaId, Long usuarioId);

    @Query("SELECT COUNT(v) FROM VotoPauta v WHERE v.pauta.id = :pautaId AND v.opcaoVoto = :tipo")
    long countPorTipo(@Param("pautaId") Long pautaId, @Param("tipo") TipoVoto tipo);
}
