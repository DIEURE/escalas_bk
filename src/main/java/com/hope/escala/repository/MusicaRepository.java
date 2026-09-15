package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.Musica;


public interface MusicaRepository extends JpaRepository<Musica, Long>{
	List<Musica> findByAtivaTrue();

}
