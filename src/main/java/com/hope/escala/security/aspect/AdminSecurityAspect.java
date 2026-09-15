package com.hope.escala.security.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.PodeSerAdmin;

@Aspect
@Component
public class AdminSecurityAspect {

    private final SecurityUtils securityUtils;

    public AdminSecurityAspect(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Before("@annotation(podeSerAdmin)")
    public void validarAdmin(PodeSerAdmin podeSerAdmin) {
        if (!securityUtils.isAdmin()) {
            throw new AccessDeniedException(
                "Apenas administradores podem acessar este recurso."
            );
        }
    }
}