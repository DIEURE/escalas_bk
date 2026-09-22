package com.hope.escala.security.annotation;

import java.lang.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')") // 🟢 usa hasAnyAuthority
public @interface PodeSerAdmin {
}
