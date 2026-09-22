package com.hope.escala.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hope.escala.dto.response.EmpresaResponseDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;
    private final UsuarioRepository usuarioRepository;

    public EmpresaService(EmpresaRepository empresaRepository, SecurityUtils securityUtils,UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
        this.usuarioRepository = usuarioRepository;
    }
    
    
    @Transactional
    public Empresa criarEmpresa(Empresa empresa) {
        empresa.setAtiva(true);
        empresa.setCriadoEm(LocalDateTime.now());
        return empresaRepository.save(empresa);
    }
    
    
    @Transactional
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }
    
    @Transactional
    public List<Empresa> listarEmpresasParaCadastro() {
    
        return empresaRepository.findAll();
    }
    
    public EmpresaResponseDTO buscarEmpresaLogadaDTO() {
        // 1. Obtém a autenticação atual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2. Busca o usuário pelo e-mail do token
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 3. Pega a empresa vinculada a esse usuário
        Empresa empresa = usuario.getEmpresa();
        if (empresa == null) {
            throw new RuntimeException("Usuário não possui instituição vinculada.");
        }

        // 4. Retorna o DTO da empresa DELE (empresa_id = 2)
        return new EmpresaResponseDTO(empresa);
    }


    public Empresa buscarEmpresaLogada() {
        Long empresaId = securityUtils.empresaId();
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o usuário logado."));
    }

    public Empresa buscarPorId(Long id) {
        // Opcional: validar se o usuário pertence a essa empresa antes de retornar
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada com o ID: " + id));
    }

    public Empresa atualizarEmpresa(Long id, Empresa dadosAtualizados) {
        Empresa empresa = buscarPorId(id);

        empresa.setNome(dadosAtualizados.getNome());
        empresa.setCnpj(dadosAtualizados.getCnpj());
        empresa.setTelefone(dadosAtualizados.getTelefone());
        empresa.setEmail(dadosAtualizados.getEmail());
        empresa.setEndereco(dadosAtualizados.getEndereco());

        return empresaRepository.save(empresa);
    }
    
    @Transactional
    public EmpresaResponseDTO atualizarEmpresaLogada(Empresa dados) {
        // 1. Pega o e-mail do usuário autenticado
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Empresa empresa = usuario.getEmpresa();
        if (empresa == null) {
            throw new RuntimeException("Usuário não possui instituição vinculada.");
        }

        // 2. Atualiza apenas os campos cadastrais permitidos para o Admin local
        if (dados.getNome() != null && !dados.getNome().isBlank()) {
            empresa.setNome(dados.getNome().trim());
        }
        empresa.setCnpj(dados.getCnpj());
        empresa.setTelefone(dados.getTelefone());
        empresa.setEmail(dados.getEmail());
        empresa.setEndereco(dados.getEndereco());
        
        // Obs: 'ativa' NÃO é alterado pelo admin comum, somente pelo Super Admin

        Empresa salva = empresaRepository.save(empresa);
        return new EmpresaResponseDTO(salva);
    }

    
}
