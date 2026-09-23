package com.hope.escala.service;
 
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.UsuarioRepository;
 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               UsuarioRepository usuarioRepository,
                               EmpresaRepository empresaRepository) {
        this.departamentoRepository = departamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
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
        boolean isSuperAdmin = usuario.getPerfil() == PerfilUsuario.SUPER_ADMIN;

        // 🟢 Lê o empresaId que o frontend mandou no payload
        Long empresaIdDestino = dados.getEmpresaId() != null
                ? dados.getEmpresaId()
                : (dados.getEmpresa() != null ? dados.getEmpresa().getId() : null);

        Empresa empresaDestino;

        // Se for SUPER_ADMIN e selecionou uma empresa (ex: 2), busca ela no banco:
        if (isSuperAdmin && empresaIdDestino != null) {
            empresaDestino = empresaRepository.findById(empresaIdDestino)
                    .orElseThrow(() -> new RuntimeException("Congregação de destino não encontrada: " + empresaIdDestino));
        } else {
            // Admin ou líder local vincula à sua própria empresa
            empresaDestino = usuario.getEmpresa();
        }

        Long empresaIdFinal = empresaDestino.getId();

        if (departamentoRepository.existsByNomeIgnoreCaseAndEmpresaId(dados.getNome().trim(), empresaIdFinal)) {
            throw new RuntimeException("Já existe um departamento com este nome na congregação.");
        }

        Departamento dep = new Departamento();
        dep.setNome(dados.getNome().trim());
        dep.setEmpresa(empresaDestino); // 🟢 Agora grava a Empresa 2!
        dep.setAtivo(dados.getAtivo() != null ? dados.getAtivo() : true);

        return departamentoRepository.save(dep);
    }

    @Transactional
    public Departamento atualizar(Long id, Departamento dados) {
        Usuario usuario = getUsuarioLogado();
        boolean isSuperAdmin = usuario.getPerfil() == PerfilUsuario.SUPER_ADMIN;

        Departamento dep;
        if (isSuperAdmin) {
            dep = departamentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));
        } else {
            dep = departamentoRepository.findByIdAndEmpresaId(id, usuario.getEmpresa().getId())
                    .orElseThrow(() -> new RuntimeException("Departamento não encontrado ou sem permissão de acesso."));
        }

        Long empresaIdVerificacao = dep.getEmpresa().getId();

        // 🟢 Se o Super Admin alterou a congregação do departamento
        Long novaEmpresaId = dados.getEmpresaId() != null
                ? dados.getEmpresaId()
                : (dados.getEmpresa() != null ? dados.getEmpresa().getId() : null);

        if (isSuperAdmin && novaEmpresaId != null && !novaEmpresaId.equals(dep.getEmpresa().getId())) {
            Empresa novaEmpresa = empresaRepository.findById(novaEmpresaId)
                    .orElseThrow(() -> new RuntimeException("Congregação não encontrada: " + novaEmpresaId));
            dep.setEmpresa(novaEmpresa);
            empresaIdVerificacao = novaEmpresa.getId();
        }

        if (departamentoRepository.existsByNomeIgnoreCaseAndEmpresaIdAndIdNot(dados.getNome().trim(), empresaIdVerificacao, id)) {
            throw new RuntimeException("Já existe outro departamento com este nome nesta congregação.");
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
        boolean isSuperAdmin = usuario.getPerfil() == PerfilUsuario.SUPER_ADMIN;

        Departamento dep;
        if (isSuperAdmin) {
            dep = departamentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));
        } else {
            dep = departamentoRepository.findByIdAndEmpresaId(id, usuario.getEmpresa().getId())
                    .orElseThrow(() -> new RuntimeException("Departamento não encontrado ou sem permissão de acesso."));
        }

        dep.setAtivo(!dep.getAtivo());
        departamentoRepository.save(dep);
    }
    
    public List<Departamento> listarPorUsuarioLogado(Usuario usuarioLogado, Long empresaFiltroId) {
        // 1. Se for SUPER_ADMIN
        if (usuarioLogado.getPerfil() == PerfilUsuario.SUPER_ADMIN) {
            if (empresaFiltroId != null) {
                return departamentoRepository.findByEmpresaIdOrderByNomeAsc(empresaFiltroId);
            }
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
