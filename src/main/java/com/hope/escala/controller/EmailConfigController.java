package com.hope.escala.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.EmailConfigRequestDTO;
import com.hope.escala.entity.EmailConfig;
import com.hope.escala.entity.Empresa;
import com.hope.escala.repository.EmailConfigRepository;
import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.AdminOuLider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/configuracoes/email")
public class EmailConfigController {

	private final EmailConfigRepository emailConfigRepository;
	private final SecurityUtils securityUtils;

	public EmailConfigController(EmailConfigRepository emailConfigRepository, SecurityUtils securityUtils) {
		this.emailConfigRepository = emailConfigRepository;
		this.securityUtils = securityUtils;
	}

	// 🟢 Busca a configuração SMTP da empresa logada
	@AdminOuLider
	@GetMapping
	public ResponseEntity<EmailConfig> buscarConfiguracao() {
		Long empresaIdLogada = securityUtils.empresaId();

		EmailConfig config = emailConfigRepository.findByEmpresa_Id(empresaIdLogada).orElseGet(() -> {
			 
			Empresa empresa = new Empresa();
			empresa.setId(empresaIdLogada);

			EmailConfig novaConfig = new EmailConfig();
			novaConfig.setEmpresa(empresa);
			novaConfig.setUsarTls(true);
			return novaConfig;
		});

		return ResponseEntity.ok(config);
	}

	// 🟢 Salva ou atualiza a configuração SMTP da empresa logada (Multi-Tenant)
	@AdminOuLider
	@PostMapping
	public ResponseEntity<EmailConfig> salvarOuAtualizar(@Valid @RequestBody EmailConfigRequestDTO dto) {
		Long empresaIdLogada = securityUtils.empresaId();

		EmailConfig config = emailConfigRepository.findByEmpresa_Id(empresaIdLogada).orElse(new EmailConfig());

		Empresa empresa = new Empresa();
		empresa.setId(empresaIdLogada);

		config.setEmpresa(empresa);
		config.setHost(dto.getHost());
		config.setPorta(dto.getPorta());
		config.setUsuario(dto.getUsuario());
		config.setSenha(dto.getSenha());
		config.setRemetenteNome(dto.getRemetenteNome());
		config.setUsarTls(dto.getUsarTls() != null ? dto.getUsarTls() : true);

		EmailConfig salvo = emailConfigRepository.save(config);

		return ResponseEntity.ok(salvo);
	}
}
