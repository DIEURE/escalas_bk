package com.hope.escala.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🟢 Usar a anotação do Spring

import com.hope.escala.dto.response.EmpresaResponseDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;
    private final UsuarioRepository usuarioRepository;

    public EmpresaService(EmpresaRepository empresaRepository, SecurityUtils securityUtils, UsuarioRepository usuarioRepository) {
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

    @Transactional(readOnly = true)
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarEmpresasParaCadastro() {
        return empresaRepository.findAll();
    }

    // 🟢 CORREÇÃO: Transacional e buscando a entidade direto pelo ID no repository
    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarEmpresaLogadaDTO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (usuario.getEmpresa() == null) {
            throw new RuntimeException("Usuário não possui instituição vinculada.");
        }

        // Pega o ID (o Hibernate consegue ler o ID do proxy sem abrir sessão)
        Long empresaId = usuario.getEmpresa().getId();

        // Busca a Empresa real e completa no banco, eliminando o erro de proxy
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada."));

        return new EmpresaResponseDTO(empresa);
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresaLogada() {
        Long empresaId = securityUtils.empresaId();
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o usuário logado."));
    }

    @Transactional(readOnly = true)
    public Empresa buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada com o ID: " + id));
    }

    @Transactional
    public Empresa atualizarEmpresa(Long id, Empresa dadosAtualizados) {
        Empresa empresa = buscarPorId(id);

        empresa.setNome(dadosAtualizados.getNome());
        empresa.setCnpj(dadosAtualizados.getCnpj());
        empresa.setTelefone(dadosAtualizados.getTelefone());
        empresa.setEmail(dadosAtualizados.getEmail());
        empresa.setEndereco(dadosAtualizados.getEndereco());

        return empresaRepository.save(empresa);
    }

    // 🟢 CORREÇÃO: Removido readOnly = true pois este método realiza UPDATE (save)
    @Transactional
    public EmpresaResponseDTO atualizarEmpresaLogada(Empresa dados) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (usuario.getEmpresa() == null) {
            throw new RuntimeException("Usuário não possui instituição vinculada.");
        }

        Long empresaId = usuario.getEmpresa().getId();
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada."));

        if (dados.getNome() != null && !dados.getNome().isBlank()) {
            empresa.setNome(dados.getNome().trim());
        }
        empresa.setCnpj(dados.getCnpj());
        empresa.setTelefone(dados.getTelefone());
        empresa.setEmail(dados.getEmail());
        empresa.setEndereco(dados.getEndereco());

        Empresa salva = empresaRepository.save(empresa);
        return new EmpresaResponseDTO(salva);
    }
}
