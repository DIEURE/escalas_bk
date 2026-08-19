package com.hope.escala.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;
import com.hope.escala.repository.UsuarioRepository;

@Component
public class SecurityUtils {

	private final UsuarioRepository usuarioRepository;

	public SecurityUtils(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario usuarioLogado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("Usuário não autenticado");
		}

		String email = authentication.getName();
		return usuarioRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
	}

	public Long usuarioId() {
		return usuarioLogado().getId();
	}

	public String email() {
		return usuarioLogado().getEmail();
	}

	public PerfilUsuario perfil() {
		return usuarioLogado().getPerfil();
	}

	public boolean isAdmin() {
		return perfil() == PerfilUsuario.ADMIN;
	}

	public boolean isLider() {
		return perfil() == PerfilUsuario.LIDER;
	}

	public boolean isMusico() {
		return perfil() == PerfilUsuario.MUSICO;
	}

	public boolean isBasico() {
		return perfil() == PerfilUsuario.BASICO;
	}

	public Set<Long> departamentos() {
		Set<Long> depts = usuarioLogado().getDepartamentos()
			.stream()
			.map(d -> d.getId())
			.collect(Collectors.toSet());
		
		// ← NOVO: Log de debug
		System.out.println("DEBUG SecurityUtils.departamentos():");
		System.out.println("  Email: " + email());
		System.out.println("  Departamentos: " + depts);
		
		return depts;
	}

	public boolean pertenceAoDepartamento(Long departamentoId) {
		boolean pertence = departamentos().contains(departamentoId);
		
		// ← NOVO: Log de debug
		System.out.println("DEBUG SecurityUtils.pertenceAoDepartamento():");
		System.out.println("  Departamento solicitado: " + departamentoId);
		System.out.println("  Resultado: " + pertence);
		
		return pertence;
	}
}