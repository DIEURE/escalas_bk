package com.hope.escala.repository;

 
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.YoutubeConfig;

public interface YoutubeConfigRepository extends JpaRepository<YoutubeConfig, Long> {

	YoutubeConfig findFirstByOrderByIdAsc(); 
	// 🟢 Retorna Optional em vez de List
    Optional<YoutubeConfig> findByEmpresaId(Long empresaId);
}