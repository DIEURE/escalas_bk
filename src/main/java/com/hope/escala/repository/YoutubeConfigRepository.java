package com.hope.escala.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.YoutubeConfig;

public interface YoutubeConfigRepository extends JpaRepository<YoutubeConfig, Long> {

	YoutubeConfig findFirstByOrderByIdAsc();
}