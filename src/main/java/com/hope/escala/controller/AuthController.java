package com.hope.escala.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.dto.LoginRequestDTO.LoginRequestDTO;
import com.hope.escala.security.dto.LoginResponseDTO.LoginResponseDTO;
import com.hope.escala.security.jwt.JwtService.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	private final UsuarioRepository usuarioRepository;

	private final PasswordEncoder passwordEncoder;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {

		this.authenticationManager = authenticationManager;

		this.jwtService = jwtService;

		this.usuarioRepository = usuarioRepository;

		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {

		System.out.println("Email recebido: " + dto.getEmail());
		System.out.println("Senha recebida: " + dto.getSenha());
		authenticationManager.authenticate(

				new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha()));

		Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		System.out.println("Usuário encontrado: " + usuario.getNome());
		System.out.println("Senha no banco: " + usuario.getSenha());
		
		String token = jwtService.gerarToken(usuario);

		return new LoginResponseDTO(token, usuario.getNome(), usuario.getPerfil().name());
	}
}
