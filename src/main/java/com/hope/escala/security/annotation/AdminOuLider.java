package com.hope.escala.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('ADMIN', 'ROLE_ADMIN', 'LIDER', 'ROLE_LIDER', 'SUPER_ADMIN', 'ROLE_SUPER_ADMIN')")
public @interface AdminOuLider {
}
