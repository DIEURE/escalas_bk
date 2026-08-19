package com.hope.escala.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;

import com.hope.escala.entity.Instrumento;

public interface InstrumentoRepository
        extends JpaRepository<Instrumento, Long> {
}