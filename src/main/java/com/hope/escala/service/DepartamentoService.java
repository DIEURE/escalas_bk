package com.hope.escala.service;
 
import com.hope.escala.dto.response.DepartamentoResponseDTO;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               UsuarioRepository usuarioRepository,
                               EmpresaRepository empresaRepository, SecurityUtils securityUtils) {
        this.departamentoRepository = departamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarPorEmpresaLogada() {
        Long empresaId = securityUtils.empresaId();
        
        // Se for ADMIN comum, filtra pela empresa dele; se for SUPER_ADMIN sem empresa, traz todos
        if (empresaId != null) {
            return departamentoRepository.findByEmpresaIdComEmpresa(empresaId);
        }
        return departamentoRepository.findAllComEmpresa();
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarAtivosPorEmpresaLogada() {
        Usuario usuario = getUsuarioLogado();
        return departamentoRepository.findByEmpresaIdAndAtivoTrueOrderByNomeAsc(usuario.getEmpresa().getId());
    }

    @Transactional
    public DepartamentoResponseDTO salvar(Departamento dados) {
        Usuario usuario = getUsuarioLogado();
        boolean isSuperAdmin = usuario.getPerfil() == PerfilUsuario.SUPER_ADMIN;

        // 1. Identifica a congregação
        Long empresaIdFinal;
        if (isSuperAdmin && dados.getEmpresaId() != null) {
            empresaIdFinal = dados.getEmpresaId();
        } else {
            if (usuario.getEmpresa() == null) {
                throw new RuntimeException("Usuário logado não possui congregação vinculada.");
            }
            empresaIdFinal = usuario.getEmpresa().getId();
        }

        // 2. Busca a Empresa real completa no banco (carregando os dados dentro da transação)
        Empresa empresaDestino = empresaRepository.findById(empresaIdFinal)
                .orElseThrow(() -> new RuntimeException("Congregação não encontrada com ID: " + empresaIdFinal));

        // 3. Força a inicialização dos dados da empresa antes de fechar a sessão
        org.hibernate.Hibernate.initialize(empresaDestino);

        // 4. Validação de duplicidade
        if (departamentoRepository.existsByNomeIgnoreCaseAndEmpresaId(dados.getNome().trim(), empresaIdFinal)) {
            throw new RuntimeException("Já existe um departamento com este nome nesta congregação.");
        }

        // 5. Salva o departamento
        Departamento dep = new Departamento();
        dep.setNome(dados.getNome().trim());
        dep.setEmpresa(empresaDestino);
        dep.setAtivo(dados.getAtivo() != null ? dados.getAtivo() : true);

        Departamento salvo = departamentoRepository.save(dep);

        // 🟢 CRUCIAL: converte para DTO AQUI DENTRO, com a sessão do banco ABERTA:
        return new DepartamentoResponseDTO(
            salvo.getId(),
            salvo.getNome(),
            salvo.getAtivo(),
            empresaDestino.getId(),
            empresaDestino.getNome()
        );
    }



    @Transactional
    public DepartamentoResponseDTO atualizar(Long id, Departamento dados) {
        Usuario usuario = getUsuarioLogado();
        boolean isSuperAdmin = usuario.getPerfil() == PerfilUsuario.SUPER_ADMIN;

        Departamento dep;
        if (isSuperAdmin) {
            dep = departamentoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Departamento não encontrado."));
        } else {
            dep = departamentoRepository.findByIdAndEmpresaId(id, usuario.getEmpresa().getId())
                    .orElseThrow(() -> new RuntimeException("Departamento não encontrado ou sem permissão."));
        }

        dep.setNome(dados.getNome().trim());
        if (dados.getAtivo() != null) {
            dep.setAtivo(dados.getAtivo());
        }

        Departamento atualizado = departamentoRepository.save(dep);
        org.hibernate.Hibernate.initialize(atualizado.getEmpresa());

        return new DepartamentoResponseDTO(
            atualizado.getId(),
            atualizado.getNome(),
            atualizado.getAtivo(),
            atualizado.getEmpresa().getId(),
            atualizado.getEmpresa().getNome()
        );
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
    
    @Transactional(readOnly = true)
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
