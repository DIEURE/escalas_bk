package com.hope.escala.security;

import java.util.Collections;
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

	// 🟢 NOVO: Método explícito para identificar Super Admin
	public boolean isSuperAdmin() {
		return perfil() == PerfilUsuario.SUPER_ADMIN;
	}

	// 🟢 AJUSTADO: Super Admin herda os privilégios de Admin
	public boolean isAdmin() {
		return perfil() == PerfilUsuario.ADMIN || isSuperAdmin();
	}

	public boolean isLider() {
		return perfil() == PerfilUsuario.LIDER;
	}

	// 🟢 NOVO: Utilitário direto para Admin, Líder ou Super Admin
	public boolean isAdminOuLider() {
		return isAdmin() || isLider();
	}

	public boolean isMusico() {
		return perfil() == PerfilUsuario.MUSICO;
	}

	public boolean isBasico() {
		return perfil() == PerfilUsuario.VOLUNTARIO;
	}
	
	// 🟢 AJUSTADO: Evita NullPointerException se o Super Admin não tiver empresa vinculada
	public Long empresaId() {
		Usuario usuario = usuarioLogado();
		return usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null;
	}

	public Set<Long> departamentos() {
		Usuario usuario = usuarioLogado();
		
		// Se não tiver departamentos (ex: Super Admin global), retorna conjunto vazio seguro
		if (usuario.getDepartamentos() == null) {
			return Collections.emptySet();
		}

		Set<Long> depts = usuario.getDepartamentos()
			.stream()
			.map(d -> d.getId())
			.collect(Collectors.toSet());
		
		System.out.println("DEBUG SecurityUtils.departamentos():");
		System.out.println("  Email: " + email());
		System.out.println("  Departamentos: " + depts);
		
		return depts;
	}

	public boolean pertenceAoDepartamento(Long departamentoId) {
		// 🟢 Super Admin tem acesso a qualquer departamento globalmente
		if (isSuperAdmin()) {
			return true;
		}

		boolean pertence = departamentos().contains(departamentoId);
		
		System.out.println("DEBUG SecurityUtils.pertenceAoDepartamento():");
		System.out.println("  Departamento solicitado: " + departamentoId);
		System.out.println("  Resultado: " + pertence);
		
		return pertence;
	}
}
