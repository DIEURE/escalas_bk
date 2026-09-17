package com.hope.escala.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.hope.escala.dto.response.EmpresaResponseDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.security.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public EmpresaService(EmpresaRepository empresaRepository, SecurityUtils securityUtils) {
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }
    
    
    @Transactional
    public Empresa criarEmpresa(Empresa empresa) {
        empresa.setAtiva(true);
        empresa.setCriadoEm(LocalDateTime.now());
        return empresaRepository.save(empresa);
    }
    
    
    @Transactional
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }
    
    public EmpresaResponseDTO buscarEmpresaLogadaDTO() {
        Empresa empresa = buscarEmpresaLogada();
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
}
