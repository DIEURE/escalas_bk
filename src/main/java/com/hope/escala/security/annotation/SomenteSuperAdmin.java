package com.hope.escala.security.annotation;

 
import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permite acesso estrito ao administrador global do SaaS (Hope Escala Pro).
 * Usa hasAuthority('SUPER_ADMIN') mantendo o padrão sem prefixo ROLE_.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ROLE_SUPER_ADMIN')")
public @interface SomenteSuperAdmin {
}
