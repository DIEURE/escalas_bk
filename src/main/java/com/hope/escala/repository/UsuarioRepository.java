package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hope.escala.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	List<Usuario> findByAtivoTrue();

	List<Usuario> findByAtivoFalse();

	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

	List<Usuario> findByInstrumentoIdAndAtivoTrue(Long instrumentoId);

	@Query("""
			    SELECT u
			    FROM Usuario u
			    JOIN u.departamentos d
			    WHERE u.ativo = true
			    AND u.disponibilidade = true
			    AND u.instrumento.id = :instrumentoId
			    AND d.id = :departamentoId
			""")
	List<Usuario> buscarMusicosDisponiveis(Long instrumentoId, Long departamentoId);

}
