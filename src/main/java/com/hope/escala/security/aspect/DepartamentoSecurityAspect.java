package com.hope.escala.security.aspect;

import java.lang.reflect.Field;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.repository.AgendaMensalRepository;
import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.PodeGerenciarDepartamento;

@Aspect
@Component
public class DepartamentoSecurityAspect {

    private final SecurityUtils securityUtils;
    private final AgendaMensalRepository agendaMensalRepository;

    public DepartamentoSecurityAspect(
            SecurityUtils securityUtils,
            AgendaMensalRepository agendaMensalRepository) {
        this.securityUtils = securityUtils;
        this.agendaMensalRepository = agendaMensalRepository;
    }

    @Before("@annotation(podeGerenciarDepartamento)")
    public void validarAcesso(
            JoinPoint joinPoint,
            PodeGerenciarDepartamento podeGerenciarDepartamento) {

        // ADMIN possui acesso a qualquer departamento
        if (securityUtils.isAdmin()) {
            return;
        }

        String nomeParametro = podeGerenciarDepartamento.parametro();

        Long departamentoId = encontrarDepartamentoId(
            joinPoint,
            nomeParametro,
            podeGerenciarDepartamento.tipo()
        );

        if (departamentoId == null) {
            throw new AccessDeniedException(
                "Departamento não informado."
            );
        }

        if (!securityUtils.pertenceAoDepartamento(departamentoId)) {
            throw new AccessDeniedException(
                "Você não possui acesso a este departamento."
            );
        }
    }

    private Long encontrarDepartamentoId(
            JoinPoint joinPoint,
            String nomeParametro,
            String tipoRecurso) {

        // ← NOVO: Log para debug
        System.out.println("=== ASPECT DEBUG ===");
        System.out.println("Método: " + joinPoint.getSignature());
        System.out.println("Tipo de recurso: " + tipoRecurso);

        // Primeiro: Tenta encontrar nos argumentos do método
        Long departamentoIdDoArgumento = encontrarNosArgumentos(
            joinPoint,
            nomeParametro
        );

        if (departamentoIdDoArgumento != null) {
            System.out.println("Encontrado nos argumentos: " + departamentoIdDoArgumento);
            return departamentoIdDoArgumento;
        }

        // Segundo: Se não encontrou, tenta resolver pelo tipo de recurso
        if ("agendaMensal".equals(tipoRecurso)) {
            Long agendaMensalId = extrairPrimeiroIdDosArgumentos(joinPoint);
            if (agendaMensalId != null) {
                Long deptId = encontrarDepartamentoDaAgendaMensal(agendaMensalId);
                System.out.println("Encontrado via agendaMensal: " + deptId);
                return deptId;
            }
        }

        System.out.println("Nenhum departamento encontrado!");
        return null;
    }

    private Long encontrarNosArgumentos(JoinPoint joinPoint, String nomeParametro) {
        for (Object argumento : joinPoint.getArgs()) {
            if (argumento == null) {
                continue;
            }

            if (argumento instanceof Long valor) {
                System.out.println("Encontrado Long simples: " + valor);
                return valor;
            }

            try {
                Field field = encontrarCampo(
                    argumento.getClass(),
                    nomeParametro
                );
                if (field == null) {
                    continue;
                }

                field.setAccessible(true);
                Object valor = field.get(argumento);
                if (valor instanceof Long) {
                    System.out.println("Encontrado campo " + nomeParametro + ": " + valor);
                    return (Long) valor;
                }
            } catch (IllegalAccessException e) {
                throw new AccessDeniedException(
                    "Não foi possível identificar o departamento."
                );
            }
        }

        return null;
    }

    private Long extrairPrimeiroIdDosArgumentos(JoinPoint joinPoint) {
        for (Object argumento : joinPoint.getArgs()) {
            if (argumento instanceof Long) {
                return (Long) argumento;
            }
        }
        return null;
    }

    private Long encontrarDepartamentoDaAgendaMensal(Long agendaMensalId) {
        try {
            AgendaMensal agenda = agendaMensalRepository.findById(agendaMensalId)
                .orElse(null);

            if (agenda != null && agenda.getDepartamento() != null) {
                return agenda.getDepartamento().getId();
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar departamento da agenda: " + e.getMessage());
        }

        return null;
    }

    private Field encontrarCampo(Class<?> classe, String nomeCampo) {
        Class<?> classeAtual = classe;
        while (classeAtual != null) {
            try {
                return classeAtual.getDeclaredField(nomeCampo);
            } catch (NoSuchFieldException e) {
                classeAtual = classeAtual.getSuperclass();
            }
        }

        return null;
    }
}