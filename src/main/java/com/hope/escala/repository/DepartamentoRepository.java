package com.hope.escala.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
	
	List<Departamento> findByAtivoTrue();
	List<Departamento> findByAtivoFalse();

}
