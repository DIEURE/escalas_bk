package com.hope.escala.security.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.exception.AcessoNegadoException;

@Aspect
@Component
public class AuthorizationAspect {

    private final SecurityUtils securityUtils;

    public AuthorizationAspect(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Before("@annotation(com.hope.escala.security.annotation.PodeSerAdmin)")
    public void validarSomenteAdmin() {
        // Super Admin herda as permissões de Admin
        if (!securityUtils.isAdmin() && !securityUtils.isSuperAdmin()) {
            throw new AcessoNegadoException(
                    "Acesso permitido somente para administradores"
            );
        }
    }

    @Before("@annotation(com.hope.escala.security.annotation.AdminOuLider)")
    public void validarAdminOuLider() {
        // Super Admin tem acesso total a recursos de Admin e Líder
        if (!securityUtils.isAdmin() && !securityUtils.isLider() && !securityUtils.isSuperAdmin()) {
            throw new AcessoNegadoException(
                    "Acesso permitido somente para administradores ou líderes"
            );
        }
    }
}
