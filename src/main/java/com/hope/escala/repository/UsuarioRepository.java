package com.hope.escala.repository;

import java.time.LocalDateTime;
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

    // 🟢 SEMPRE trazer a empresa no findByEmail padrão para evitar proxy Lazy em qualquer controller/service:
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.email = :email")
    Optional<Usuario> findByEmailComEmpresa(@Param("email") String email);

    boolean existsByEmail(String email);

    // 🟢 SUPER ADMIN: Carrega os usuários trazendo a congregação
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa ORDER BY u.nome ASC")
    List<Usuario> findAllByOrderByNomeAsc();

    // 🟢 Músicos aptos para rodízio
    @Query("SELECT DISTINCT u FROM Usuario u " +
           "JOIN u.instrumentos i " +
           "JOIN u.departamentos d " +
           "LEFT JOIN FETCH u.empresa " +
           "WHERE i.id = :instrumentoId " +
           "AND d.id = :departamentoId " +
           "AND u.empresa.id = :empresaId " +
           "AND u.ativo = true " +
           "AND u.disponibilidade = true")
    List<Usuario> buscarMusicosDisponiveisPorEmpresa(
            @Param("instrumentoId") Long instrumentoId,
            @Param("departamentoId") Long departamentoId,
            @Param("empresaId") Long empresaId);

    // Listagem por congregação/empresa
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.empresa.id = :empresaId ORDER BY u.nome ASC")
    List<Usuario> findByEmpresaIdOrderByNomeAsc(@Param("empresaId") Long empresaId);

    // Listagem apenas de ativos por empresa
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.empresa.id = :empresaId AND u.ativo = true ORDER BY u.nome ASC")
    List<Usuario> findByEmpresaIdAndAtivoTrue(@Param("empresaId") Long empresaId);

    // Busca por ID garantindo a empresa
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.id = :id AND u.empresa.id = :empresaId")
    Optional<Usuario> findByIdAndEmpresaId(@Param("id") Long id, @Param("empresaId") Long empresaId);

    // Pendentes/aprovação
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.ativo = false AND u.empresa.id = :empresaId ORDER BY u.nome ASC")
    List<Usuario> findByAtivoFalseAndEmpresaIdOrderByNomeAsc(@Param("empresaId") Long empresaId);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE u.ativo = false ORDER BY u.nome ASC")
    List<Usuario> findByAtivoFalseOrderByNomeAsc();

    // Busca por departamento, empresa e ativo
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.departamentos d LEFT JOIN FETCH u.empresa WHERE d.id = :departamentoId AND u.empresa.id = :empresaId AND u.ativo = true ORDER BY u.nome ASC")
    List<Usuario> findByDepartamentosIdAndEmpresaIdAndAtivoTrue(@Param("departamentoId") Long departamentoId, @Param("empresaId") Long empresaId);

    // Busca por instrumento, empresa e ativo
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.instrumentos i LEFT JOIN FETCH u.empresa WHERE i.id = :instrumentoId AND u.empresa.id = :empresaId AND u.ativo = true ORDER BY u.nome ASC")
    List<Usuario> findByInstrumentosIdAndEmpresaIdAndAtivoTrue(@Param("instrumentoId") Long instrumentoId, @Param("empresaId") Long empresaId);

    // 🟢 Data Table com filtros: adicionado LEFT JOIN FETCH u.empresa e countQuery explícita
    @Query(value = "SELECT u FROM Usuario u LEFT JOIN FETCH u.empresa WHERE (:empresaId IS NULL OR u.empresa.id = :empresaId) " +
                   "AND (:busca IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
                   "AND (:perfil IS NULL OR u.perfil = :perfil) " +
                   "AND (:ativo IS NULL OR u.ativo = :ativo)",
           countQuery = "SELECT COUNT(u) FROM Usuario u WHERE (:empresaId IS NULL OR u.empresa.id = :empresaId) " +
                        "AND (:busca IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
                        "AND (:perfil IS NULL OR u.perfil = :perfil) " +
                        "AND (:ativo IS NULL OR u.ativo = :ativo)")
    Page<Usuario> listarComFiltros(@Param("empresaId") Long empresaId,
                                  @Param("busca") String busca,
                                  @Param("perfil") PerfilUsuario perfil,
                                  @Param("ativo") Boolean ativo,
                                  Pageable pageable);

    long countByAtivoTrue();

    long countByAtivoFalse();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.empresa.id = :empresaId AND u.ativo = true")
    long countByEmpresaIdAndAtivoTrue(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.empresa.id = :empresaId AND u.ativo = false")
    long countByEmpresaIdAndAtivoFalse(@Param("empresaId") Long empresaId);

    long countByDataCadastroBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT u.perfil, COUNT(u) FROM Usuario u GROUP BY u.perfil")
    List<Object[]> contarUsuariosPorPerfilRaw();
}
