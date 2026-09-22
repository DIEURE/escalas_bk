package com.hope.escala.security.annotation;

import java.lang.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ROLE_SUPER_ADMIN','ADMIN', 'ROLE_ADMIN', 'LIDER', 'ROLE_LIDER')")
public @interface PodeSerAdmin {
}
