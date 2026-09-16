package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hope.escala.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Lista apenas as categorias ativas, útil para preencher selects no frontend
    List<Categoria> findByAtivoTrue();
    List<Categoria> findByEmpresaId(Long empresaId);
}
