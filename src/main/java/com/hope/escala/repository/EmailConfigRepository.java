package com.hope.escala.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hope.escala.entity.EmailConfig;

public interface EmailConfigRepository extends JpaRepository<EmailConfig, Long> {
    
   
    Optional<EmailConfig> findByEmpresa_Id(Long empresaId);
}
