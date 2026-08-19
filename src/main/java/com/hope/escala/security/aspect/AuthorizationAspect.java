package com.hope.escala.security.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.AdminOuLider;
import com.hope.escala.security.annotation.PodeSerAdmin;
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


		if (!securityUtils.isAdmin()) {

			throw new AcessoNegadoException(
					"Acesso permitido somente para administradores"
			);
		}
	}




	@Before("@annotation(com.hope.escala.security.annotation.AdminOuLider)")
	public void validarAdminOuLider() {


		if (!securityUtils.isAdmin()
				&& !securityUtils.isLider()) {


			throw new AcessoNegadoException(
					"Acesso permitido somente para administradores ou líderes"
			);
		}
	}
}