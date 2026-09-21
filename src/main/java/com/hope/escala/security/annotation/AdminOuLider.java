package com.hope.escala.security.annotation;

import java.lang.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('ADMIN', 'LIDER')")
public @interface AdminOuLider {

}
