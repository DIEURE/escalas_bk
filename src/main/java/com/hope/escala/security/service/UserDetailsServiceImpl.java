package com.hope.escala.security.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {

		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
		
		 System.out.println("Usuário encontrado: " + usuario.getNome());

		return new User(usuario.getEmail(), usuario.getSenha(),
				List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())));
	}
}
