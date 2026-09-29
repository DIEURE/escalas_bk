package com.hope.escala.dto.response;

import com.hope.escala.entity.Departamento;
import org.hibernate.Hibernate;

public record DepartamentoResponseDTO(
    Long id,
    String nome,
    Boolean ativo,
    Long empresaId,
    String empresaNome
) {
    public DepartamentoResponseDTO(Departamento dep) {
        this(
            dep.getId(),
            dep.getNome(),
            dep.getAtivo(),
            // 🟢 Evita acessar métodos do proxy se ele não foi carregado
            (dep.getEmpresa() != null ? dep.getEmpresa().getId() : null),
            (dep.getEmpresa() != null && Hibernate.isInitialized(dep.getEmpresa()) 
                ? dep.getEmpresa().getNome() 
                : null)
        );
    }
}
