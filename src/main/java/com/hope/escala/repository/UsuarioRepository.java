package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // IMPORTANTE
import com.hope.escala.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    List<Usuario> findByAtivoTrue();
    List<Usuario> findByAtivoFalse();
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Método que estava causando erro
    @Query("SELECT u FROM Usuario u JOIN u.departamentos d WHERE u.ativo = true AND u.disponibilidade = true AND u.instrumento.id = :instrumentoId AND d.id = :departamentoId")
    List<Usuario> buscarMusicosDisponiveis(@Param("instrumentoId") Long instrumentoId, @Param("departamentoId") Long departamentoId);

    // Novo método que você precisará para o modal de Escala Manual (listar músicos por depto)
    @Query("SELECT u FROM Usuario u JOIN u.departamentos d WHERE d.id = :departamentoId AND u.ativo = true")
    List<Usuario> findByDepartamentoIdAndAtivoTrue(@Param("departamentoId") Long departamentoId);
}