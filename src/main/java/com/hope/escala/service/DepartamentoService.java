package com.hope.escala.service;
 
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.UsuarioRepository;
 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository, UsuarioRepository usuarioRepository) {
        this.departamentoRepository = departamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));
    }

    public List<Departamento> listarPorEmpresaLogada() {
        Usuario usuario = getUsuarioLogado();
        return departamentoRepository.findByEmpresaIdOrderByNomeAsc(usuario.getEmpresa().getId());
    }

    public List<Departamento> listarAtivosPorEmpresaLogada() {
        Usuario usuario = getUsuarioLogado();
        return departamentoRepository.findByEmpresaIdAndAtivoTrueOrderByNomeAsc(usuario.getEmpresa().getId());
    }

    @Transactional
    public Departamento salvar(Departamento dados) {
        Usuario usuario = getUsuarioLogado();
        Long empresaId = usuario.getEmpresa().getId();

        if (departamentoRepository.existsByNomeIgnoreCaseAndEmpresaId(dados.getNome().trim(), empresaId)) {
            throw new RuntimeException("Já existe um departamento com este nome na congregação.");
        }

        Departamento dep = new Departamento();
        dep.setNome(dados.getNome().trim());
        dep.setEmpresa(usuario.getEmpresa());
        dep.setAtivo(true);

        return departamentoRepository.save(dep);
    }

    @Transactional
    public Departamento atualizar(Long id, Departamento dados) {
        Usuario usuario = getUsuarioLogado();
        Long empresaId = usuario.getEmpresa().getId();

        Departamento dep = departamentoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado ou sem permissão de acesso."));

        if (departamentoRepository.existsByNomeIgnoreCaseAndEmpresaIdAndIdNot(dados.getNome().trim(), empresaId, id)) {
            throw new RuntimeException("Já existe outro departamento com este nome.");
        }

        dep.setNome(dados.getNome().trim());
        if (dados.getAtivo() != null) {
            dep.setAtivo(dados.getAtivo());
        }

        return departamentoRepository.save(dep);
    }

    @Transactional
    public void alternarStatus(Long id) {
        Usuario usuario = getUsuarioLogado();
        Departamento dep = departamentoRepository.findByIdAndEmpresaId(id, usuario.getEmpresa().getId())
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado ou sem permissão de acesso."));

        dep.setAtivo(!dep.getAtivo());
        departamentoRepository.save(dep);
    }
    
    public List<Departamento> listarPorUsuarioLogado(Usuario usuarioLogado, Long empresaFiltroId) {
        // 1. Se for SUPER_ADMIN
        if (usuarioLogado.getPerfil() == PerfilUsuario.SUPER_ADMIN) {
            // Se ele selecionou uma empresa específica no painel:
            if (empresaFiltroId != null) {
                return departamentoRepository.findByEmpresaIdOrderByNomeAsc(empresaFiltroId);
            }
            // Se não selecionou nada, retorna todos os departamentos do SaaS:
            return departamentoRepository.findAllByOrderByNomeAsc();
        }

        // 2. Se for ADMIN ou LÍDER local
        Long empresaId = (usuarioLogado.getEmpresa() != null) ? usuarioLogado.getEmpresa().getId() : null;
        
        if (empresaId == null) {
            return List.of();
        }

        return departamentoRepository.findByEmpresaIdOrderByNomeAsc(empresaId);
    }
    
}
