package com.hope.escala.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.UsuarioRequestDTO; // 🟢 Usar o DTO para receber os dados
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;
import com.hope.escala.repository.EmpresaRepository; // 🟢 Import necessário
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.dto.LoginRequestDTO.LoginRequestDTO;
import com.hope.escala.security.dto.LoginResponseDTO.LoginResponseDTO;
import com.hope.escala.security.jwt.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UsuarioRepository usuarioRepository;
	private final EmpresaRepository empresaRepository; // 🟢 Injetado aqui
	private final PasswordEncoder passwordEncoder;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
			UsuarioRepository usuarioRepository, EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.usuarioRepository = usuarioRepository;
		this.empresaRepository = empresaRepository; // 🟢 Inicializado aqui
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {
		System.out.println("Email recebido: " + dto.getEmail());
		System.out.println("Senha recebida: " + dto.getSenha());
		
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha()));

		Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// 🟢 BLOQUEIO DE SEGURANÇA: Impede o login se o cadastro estiver inativo/pendente
		if (usuario.getAtivo() == null || !usuario.getAtivo()) {
			throw new RuntimeException("Seu cadastro ainda está aguardando a aprovação de um Administrador ou Líder.");
		}

		System.out.println("Usuário encontrado e ativo: " + usuario.getNome());

		String token = jwtService.gerarToken(usuario);

		Long empresaId = usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : null;
		String nomeEmpresa = usuario.getEmpresa() != null ? usuario.getEmpresa().getNome() : null;

		return new LoginResponseDTO(token, usuario.getNome(), usuario.getEmail(), usuario.getPerfil().name(), empresaId,
				nomeEmpresa);
	}


	// 🟢 ENDPOINT DE SOLICITAÇÃO DE CADASTRO PÚBLICO CORRIGIDO
	@PostMapping("/solicitar-cadastro")
	public ResponseEntity<?> solicitarCadastro(@Valid @RequestBody UsuarioRequestDTO dto) {
		try {
			if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este e-mail já está cadastrado no sistema.");
			}

			if (dto.getEmpresaId() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Selecione a igreja/empresa antes de enviar.");
			}

			Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
					.orElseThrow(() -> new RuntimeException("A igreja selecionada não foi encontrada."));

			Usuario novoUsuario = new Usuario();
			novoUsuario.setNome(dto.getNome());
			novoUsuario.setEmail(dto.getEmail());
			novoUsuario.setTelefone(dto.getTelefone());
			novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
			novoUsuario.setEmpresa(empresa);
			novoUsuario.setAtivo(false);
			novoUsuario.setDisponibilidade(dto.getDisponibilidade() != null ? dto.getDisponibilidade() : false);
			novoUsuario.setPerfil(dto.getPerfil() != null ? dto.getPerfil() : PerfilUsuario.VOLUNTARIO);

			usuarioRepository.save(novoUsuario);

			return ResponseEntity.status(HttpStatus.CREATED).body("Solicitação de cadastro enviada com sucesso!");

		} catch (Exception e) {
            // 🟢 Loga o erro técnico no console do servidor para você debugar, mas retorna um texto amigável para o usuário
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não foi possível realizar o cadastro. Verifique os dados preenchidos e tente novamente.");
        }
	}

}
