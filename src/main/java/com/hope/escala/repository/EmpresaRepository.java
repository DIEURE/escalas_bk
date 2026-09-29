package com.hope.escala.repository;
 

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.hope.escala.entity.Empresa;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
	@Query("SELECT e FROM Empresa e WHERE e.ativa = true AND e.id <> 1 ORDER BY e.nome ASC")
    List<Empresa> findEmpresasParaCadastroPublico();
     
}
