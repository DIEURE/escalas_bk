package com.hope.escala.security.annotation;
 

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PodeGerenciarDepartamento {
    
    /**
     * Nome do parâmetro da URL (@PathVariable)
     * Exemplo: "id" para extrair de /{id}
     */
    String parametro() default "id";
    
    /**
     * Tipo de recurso a validar
     * Exemplo: "agendaMensal", "escala", "departamento"
     */
    String tipo() default "agendaMensal";
}
