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
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.departamentos d JOIN u.instrumentos i WHERE u.ativo = true AND u.disponibilidade = true AND i.id = :instrumentoId AND d.id = :departamentoId")
    List<Usuario> buscarMusicosDisponiveis(@Param("instrumentoId") Long instrumentoId, @Param("departamentoId") Long departamentoId);

    // Novo método que você precisará para o modal de Escala Manual (listar músicos por depto)
    @Query("SELECT u FROM Usuario u JOIN u.departamentos d WHERE d.id = :departamentoId AND u.ativo = true")
    List<Usuario> findByDepartamentoIdAndAtivoTrue(@Param("departamentoId") Long departamentoId);
  
   
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.instrumentos i WHERE i.id = :instrumentoId AND u.ativo = true")
    List<Usuario> findByInstrumentoIdAndAtivoTrue(@Param("instrumentoId") Long instrumentoId);

    // Se preferir buscar pelo NOME do instrumento (caso sua escala guarde a string do instrumento, ex: "TECLADO"):
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.instrumentos i WHERE LOWER(i.nome) = LOWER(:nomeInstrumento) AND u.ativo = true")
    List<Usuario> findByNomeInstrumentoAndAtivoTrue(@Param("nomeInstrumento") String nomeInstrumento);
    
    
}