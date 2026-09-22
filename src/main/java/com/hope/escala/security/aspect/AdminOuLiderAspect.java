package com.hope.escala.security.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.AdminOuLider;

@Aspect
@Component
public class AdminOuLiderAspect {

    private final SecurityUtils securityUtils;

    public AdminOuLiderAspect(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Before("@annotation(adminOuLider)")
    public void validarAdminOuLider(AdminOuLider adminOuLider) {
        // 🟢 Se o método de checagem estiver no SecurityUtils:
        if (!securityUtils.isAdmin()) {
            throw new RuntimeException("Acesso permitido somente para administradores ou líderes");
        }
    }
}
