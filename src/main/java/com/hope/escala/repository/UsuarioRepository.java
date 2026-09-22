package com.hope.escala.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByAtivoTrue();

    List<Usuario> findByAtivoFalse();

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Usuario> findByIdAndEmpresaId(Long id, Long empresaId);

    // Músicos disponíveis gerais
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.departamentos d JOIN u.instrumentos i " +
           "WHERE u.ativo = true AND u.disponibilidade = true AND i.id = :instrumentoId AND d.id = :departamentoId")
    List<Usuario> buscarMusicosDisponiveis(@Param("instrumentoId") Long instrumentoId,
                                          @Param("departamentoId") Long departamentoId);

    // Músicos por departamento
    @Query("SELECT u FROM Usuario u JOIN u.departamentos d WHERE d.id = :departamentoId AND u.ativo = true")
    List<Usuario> findByDepartamentoIdAndAtivoTrue(@Param("departamentoId") Long departamentoId);

    // Músicos por instrumento ID
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.instrumentos i WHERE i.id = :instrumentoId AND u.ativo = true")
    List<Usuario> findByInstrumentoIdAndAtivoTrue(@Param("instrumentoId") Long instrumentoId);

    // Músicos por nome do instrumento
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.instrumentos i WHERE LOWER(i.nome) = LOWER(:nomeInstrumento) AND u.ativo = true")
    List<Usuario> findByNomeInstrumentoAndAtivoTrue(@Param("nomeInstrumento") String nomeInstrumento);

    // 🟢 Músicos disponíveis com isolamento multi-tenant (Empresa) corrigido
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.departamentos d JOIN u.instrumentos i " +
           "WHERE u.empresa.id = :empresaId AND u.ativo = true AND u.disponibilidade = true " +
           "AND i.id = :instrumentoId AND d.id = :departamentoId")
    List<Usuario> buscarMusicosDisponiveisPorEmpresa(@Param("instrumentoId") Long instrumentoId,
                                                    @Param("departamentoId") Long departamentoId,
                                                    @Param("empresaId") Long empresaId);

    // Multi-tenant: pendentes e ativos
    List<Usuario> findByEmpresaIdAndAtivoFalse(Long empresaId);

    List<Usuario> findByEmpresaIdAndAtivoTrue(Long empresaId);

    List<Usuario> findByDepartamentosIdAndEmpresaIdAndAtivoTrue(Long departamentoId, Long empresaId);

    // 🟢 Consulta paginada para a Data Table com filtros (Nome/Email, Perfil e Ativo)
    @Query("""
        SELECT DISTINCT u FROM Usuario u 
        LEFT JOIN FETCH u.instrumentos
        WHERE u.empresa.id = :empresaId
          AND (:busca IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%')))
          AND (:perfil IS NULL OR u.perfil = :perfil)
          AND (:ativo IS NULL OR u.ativo = :ativo)
    """)
    Page<Usuario> listarComFiltros(
        @Param("empresaId") Long empresaId,
        @Param("busca") String busca,
        @Param("perfil") PerfilUsuario perfil,
        @Param("ativo") Boolean ativo,
        Pageable pageable
    );
}
