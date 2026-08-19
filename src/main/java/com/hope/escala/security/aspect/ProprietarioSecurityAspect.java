package com.hope.escala.security.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.PodeSerProprietario;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ProprietarioSecurityAspect {

    private final SecurityUtils securityUtils;

    public ProprietarioSecurityAspect(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Before("@annotation(podeSerProprietario)")
    public void validarProprietario(PodeSerProprietario podeSerProprietario) {
        
        // ADMIN acessa tudo
        if (securityUtils.isAdmin()) {
            return;
        }

        // Extrai o ID da URL
        Long idDaUrl = extrairIdDaUrl();

        // Obtém o ID do usuário logado
        Long usuarioLogadoId = securityUtils.usuarioId();  // ← CORRIGIDO

        // Verifica se é o mesmo usuário
        if (!idDaUrl.equals(usuarioLogadoId)) {
            throw new AccessDeniedException(
                "Você só pode acessar seus próprios dados."
            );
        }
    }

    private Long extrairIdDaUrl() {
        ServletRequestAttributes attrs = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            throw new RuntimeException("Request attributes não encontrados");
        }

        HttpServletRequest request = attrs.getRequest();
        String requestURI = request.getRequestURI();
        String[] parts = requestURI.split("/");

        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                try {
                    return Long.parseLong(part);
                } catch (NumberFormatException e) {
                    // Continua procurando
                }
            }
        }

        throw new IllegalArgumentException("ID não encontrado na URL");
    }
}