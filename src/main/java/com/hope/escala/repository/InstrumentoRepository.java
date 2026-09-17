package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.Instrumento;

public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
	List<Instrumento> findByEmpresaId(Long empresaId);
	
}