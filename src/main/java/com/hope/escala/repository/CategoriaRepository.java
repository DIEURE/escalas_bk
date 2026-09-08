package com.hope.escala.repository;

import com.hope.escala.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Lista apenas as categorias ativas, útil para preencher selects no frontend
    List<Categoria> findByAtivoTrue();
}
