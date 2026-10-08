package com.hope.escala.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hope.escala.dto.AtualizarPerfilDTO;
import com.hope.escala.dto.UsuarioPerfilDTO;
import com.hope.escala.dto.request.RedefinirSenhaDTO;
import com.hope.escala.dto.request.SolicitarRecuperacaoSenhaDTO;
import com.hope.escala.dto.request.UsuarioDisponibilidadeDTO;
import com.hope.escala.dto.request.UsuarioRequestDTO;
import com.hope.escala.dto.response.UsuarioResponseDTO;
import com.hope.escala.entity.Departamento;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Instrumento;
import com.hope.escala.entity.Usuario;
import com.hope.escala.enums.PerfilUsuario;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.DepartamentoRepository;
import com.hope.escala.repository.InstrumentoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;
import com.hope.escala.security.annotation.PodeSerAdmin;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final InstrumentoRepository instrumentoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, InstrumentoRepository instrumentoRepository,
            DepartamentoRepository departamentoRepository, PasswordEncoder passwordEncoder,
            SecurityUtils securityUtils, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.instrumentoRepository = instrumentoRepository;
        this.departamentoRepository = departamentoRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.emailService = emailService;
    }

    @Transactional
    public void solicitarRecuperacaoSenha(SolicitarRecuperacaoSenhaDTO dto) {
        usuarioRepository.findByEmail(dto.email().trim().toLowerCase()).ifPresent(usuario -> {
            // Gera código numérico seguro de 6 dígitos
            int codigo = 100000 + new SecureRandom().nextInt(900000);
            usuario.setTokenRecuperacaoSenha(String.valueOf(codigo));
            usuario.setTokenRecuperacaoExpiraEm(LocalDateTime.now().plusMinutes(15));
            usuarioRepository.save(usuario);

            System.out.println(">>> [HOPE ESCALA] CÓDIGO DE RECUPERAÇÃO PARA " + usuario.getEmail() + ": " + codigo);

            try {
                Long empresaId = usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : 1L;
                // Caso seu EmailService tenha método específico ou você queira delegar
                // emailService.enviarEmailRecuperacao(empresaId, usuario.getEmail(), usuario.getNome(), String.valueOf(codigo));
            } catch (Exception e) {
                System.err.println("Aviso: Falha ao enviar e-mail com código de recuperação: " + e.getMessage());
            }
        });
    }

    @Transactional
    public void redefinirSenhaComCodigo(RedefinirSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Código de verificação inválido ou expirado."));

        if (usuario.getTokenRecuperacaoSenha() == null
                || usuario.getTokenRecuperacaoExpiraEm() == null
                || !usuario.getTokenRecuperacaoSenha().equals(dto.tokenOuCodigo().trim())
                || LocalDateTime.now().isAfter(usuario.getTokenRecuperacaoExpiraEm())) {
            throw new IllegalArgumentException("Código de verificação inválido ou expirado.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuario.setTokenRecuperacaoSenha(null);
        usuario.setTokenRecuperacaoExpiraEm(null);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPaginado(String busca, PerfilUsuario perfil, Boolean ativo, Pageable pageable) {
        Long empresaIdLogada = securityUtils.empresaId();
        return usuarioRepository.listarComFiltros(empresaIdLogada, busca, perfil, ativo, pageable)
                .map(this::converterParaDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar(Long empresaFiltroId) {
        List<Usuario> usuarios;

        if (securityUtils.isSuperAdmin()) {
            if (empresaFiltroId != null) {
                usuarios = usuarioRepository.findByEmpresaIdOrderByNomeAsc(empresaFiltroId);
            } else {
                usuarios = usuarioRepository.findAllByOrderByNomeAsc();
            }
        } else {
            Long empresaId = securityUtils.empresaId();
            if (empresaId == null) {
                return List.of();
            }
            usuarios = usuarioRepository.findByEmpresaIdOrderByNomeAsc(empresaId);
        }

        return usuarios.stream()
                .map(this::converterParaDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public UsuarioPerfilDTO buscarMeuPerfilPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + email));

        String empresaNome = usuario.getEmpresa() != null ? usuario.getEmpresa().getNome() : "Matriz";

        return new UsuarioPerfilDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.getPerfil().name(),
            empresaNome
        );
    }

    @Transactional
    public UsuarioPerfilDTO atualizarMeuPerfilPorEmail(String email, AtualizarPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail: " + email));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome());
        }
        
        usuario.setTelefone(dto.telefone());

        if (dto.novaSenha() != null && !dto.novaSenha().isBlank()) {
            if (dto.senhaAtual() == null || dto.senhaAtual().isBlank()) {
                throw new IllegalArgumentException("Informe a senha atual para cadastrar uma nova.");
            }
            if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
                throw new IllegalArgumentException("Senha atual informada está incorreta.");
            }
            usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        }

        Usuario salvo = usuarioRepository.save(usuario);
        String empresaNome = salvo.getEmpresa() != null ? salvo.getEmpresa().getNome() : "Matriz";

        return new UsuarioPerfilDTO(
            salvo.getId(),
            salvo.getNome(),
            salvo.getEmail(),
            salvo.getTelefone(),
            salvo.getPerfil().name(),
            empresaNome
        );
    }

    @Transactional
    @PodeSerAdmin
    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já existe");
        }

        Long empresaIdDestino = securityUtils.empresaId();
        if (securityUtils.isSuperAdmin() && dto.getEmpresaId() != null) {
            empresaIdDestino = dto.getEmpresaId();
        }

        if (empresaIdDestino == null) {
            throw new RuntimeException("A congregação (empresa) é obrigatória para cadastrar um usuário.");
        }

        Set<Instrumento> instrumentosEncontrados = new HashSet<>();
        if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
            instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
        }

        Set<Departamento> departamentos = new HashSet<>();
        if (dto.getDepartamentoIds() != null && !dto.getDepartamentoIds().isEmpty()) {
            departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(dto.getPerfil());
        usuario.setDisponibilidade(dto.getDisponibilidade());
        usuario.setObservacao(dto.getObservacao());

        usuario.setInstrumentos(instrumentosEncontrados);
        usuario.setDepartamentos(departamentos);
        usuario.setAtivo(true);

        Empresa empresa = new Empresa();
        empresa.setId(empresaIdDestino);
        usuario.setEmpresa(empresa);

        Usuario salvo = usuarioRepository.save(usuario);
        
        return converterParaDTO(usuarioRepository.findByIdComEmpresa(salvo.getId()).orElse(salvo));
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilDTO buscarMeuPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        String empresaNome = usuario.getEmpresa() != null ? usuario.getEmpresa().getNome() : "Matriz";

        return new UsuarioPerfilDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.getPerfil().name(),
            empresaNome
        );
    }

    @Transactional
    public UsuarioPerfilDTO atualizarMeuPerfil(Long usuarioId, AtualizarPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());

        if (dto.novaSenha() != null && !dto.novaSenha().isBlank()) {
            if (dto.senhaAtual() == null || dto.senhaAtual().isBlank()) {
                throw new IllegalArgumentException("Informe sua senha atual para definir uma nova.");
            }
            if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
                throw new IllegalArgumentException("A senha atual informada está incorreta.");
            }
            usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        }

        Usuario salvo = usuarioRepository.save(usuario);
        String empresaNome = salvo.getEmpresa() != null ? salvo.getEmpresa().getNome() : "Matriz";

        return new UsuarioPerfilDTO(
            salvo.getId(),
            salvo.getNome(),
            salvo.getEmail(),
            salvo.getTelefone(),
            salvo.getPerfil().name(),
            empresaNome
        );
    }

    public UsuarioResponseDTO solicitarCadastroPublico(UsuarioRequestDTO dto) {
        if (dto.getEmpresaId() == null) {
            throw new RuntimeException("A instituição/empresa é obrigatória para o cadastro.");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Este e-mail já está cadastrado no sistema.");
        }

        Empresa empresa = new Empresa();
        empresa.setId(dto.getEmpresaId());

        Set<Instrumento> instrumentosEncontrados = new HashSet<>();
        if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
            instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setPerfil(PerfilUsuario.VOLUNTARIO);
        usuario.setDisponibilidade(true);
        
        usuario.setInstrumentos(instrumentosEncontrados);
        usuario.setAtivo(false);
        usuario.setEmpresa(empresa);

        Usuario salvo = usuarioRepository.save(usuario);
        return converterParaDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPendentes(Long empresaFiltroId) {
        Long usuarioLogadoId = securityUtils.usuarioId();
        Usuario usuarioLogado = usuarioRepository.findById(usuarioLogadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        List<Usuario> pendentes;

        if (securityUtils.isSuperAdmin()) {
            if (empresaFiltroId != null) {
                pendentes = usuarioRepository.findByAtivoFalseAndEmpresaIdOrderByNomeAsc(empresaFiltroId);
            } else {
                pendentes = usuarioRepository.findByAtivoFalseOrderByNomeAsc();
            }
        } else {
            Long empresaId = (usuarioLogado.getEmpresa() != null) ? usuarioLogado.getEmpresa().getId() : null;
            if (empresaId == null) {
                return List.of();
            }
            pendentes = usuarioRepository.findByAtivoFalseAndEmpresaIdOrderByNomeAsc(empresaId);
        }

        return pendentes.stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional
    @PodeSerAdmin 
    public UsuarioResponseDTO aprovar(Long id, UsuarioRequestDTO dto) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findByIdComEmpresa(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário pendente não encontrado"));

        if (!securityUtils.isSuperAdmin() && (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada))) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        usuario.setAtivo(true);

        if (dto.getPerfil() != null) {
            usuario.setPerfil(dto.getPerfil());
        }

        Set<Instrumento> instrumentosEncontrados = new HashSet<>();
        if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
            instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
        }
        usuario.setInstrumentos(instrumentosEncontrados);

        if (dto.getDepartamentoIds() != null && !dto.getDepartamentoIds().isEmpty()) {
            Set<Departamento> departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));
            usuario.setDepartamentos(departamentos);
        }

        Usuario aprovado = usuarioRepository.save(usuario);

        try {
            Long empresaDestino = aprovado.getEmpresa() != null ? aprovado.getEmpresa().getId() : empresaIdLogada;
            emailService.enviarEmailAprovacao(empresaDestino, aprovado.getEmail(), aprovado.getNome());
        } catch (Exception e) {
            System.err.println("Aviso: Usuário aprovado, mas falhou ao enviar o e-mail: " + e.getMessage());
        }

        return converterParaDTO(aprovado);
    }

    @PodeSerAdmin
    public UsuarioResponseDTO alternarStatus(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!securityUtils.isSuperAdmin() && (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada))) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        boolean novoStatus = !Boolean.TRUE.equals(usuario.getAtivo());
        usuario.setAtivo(novoStatus);

        if (!novoStatus) {
            usuario.setDataInativacao(LocalDateTime.now());
        } else {
            usuario.setDataInativacao(null);
            try {
                Long empresaDestino = usuario.getEmpresa() != null ? usuario.getEmpresa().getId() : empresaIdLogada;
                emailService.enviarEmailAprovacao(empresaDestino, usuario.getEmail(), usuario.getNome());
            } catch (Exception e) {
                System.err.println("Aviso: Status ativado, mas falhou envio de e-mail: " + e.getMessage());
            }
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        return converterParaDTO(atualizado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        Long empresaIdLogada = securityUtils.empresaId();
        List<Usuario> usuarios = securityUtils.isSuperAdmin() 
                ? usuarioRepository.findAllByOrderByNomeAsc()
                : usuarioRepository.findByEmpresaIdOrderByNomeAsc(empresaIdLogada);

        return usuarios.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findByIdComEmpresa(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!securityUtils.isSuperAdmin() && (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada))) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        return converterParaDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> buscarPorDepartamento(Long departamentoId) {
        Long empresaIdLogada = securityUtils.empresaId();
        return usuarioRepository.findByDepartamentosIdAndEmpresaIdAndAtivoTrue(departamentoId, empresaIdLogada)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @PodeSerAdmin
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findByIdComEmpresa(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        if (!securityUtils.isSuperAdmin() && (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada))) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já existe");
        }

        if (securityUtils.isSuperAdmin() && dto.getEmpresaId() != null) {
            Empresa novaEmpresa = new Empresa();
            novaEmpresa.setId(dto.getEmpresaId());
            usuario.setEmpresa(novaEmpresa);
        }

        Set<Instrumento> instrumentosEncontrados = new HashSet<>();
        if (dto.getInstrumentoIds() != null && !dto.getInstrumentoIds().isEmpty()) {
            instrumentosEncontrados = new HashSet<>(instrumentoRepository.findAllById(dto.getInstrumentoIds()));
        }

        Set<Departamento> departamentos = new HashSet<>();
        if (dto.getDepartamentoIds() != null && !dto.getDepartamentoIds().isEmpty()) {
            departamentos = new HashSet<>(departamentoRepository.findAllById(dto.getDepartamentoIds()));
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        usuario.setPerfil(dto.getPerfil());
        usuario.setDisponibilidade(dto.getDisponibilidade());
        usuario.setObservacao(dto.getObservacao());

        usuario.setInstrumentos(instrumentosEncontrados);
        usuario.setDepartamentos(departamentos);

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {  
            usuario.setSenha(passwordEncoder.encode(dto.getSenha())); 
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        
        if (securityUtils.isSuperAdmin() && dto.getEmpresaId() != null) {
            return converterParaDTO(usuarioRepository.findByIdComEmpresa(atualizado.getId()).orElse(atualizado));
        }

        return converterParaDTO(atualizado);
    }

    @PodeSerAdmin
    public void inativar(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!securityUtils.isSuperAdmin() && (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada))) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        usuario.setAtivo(false);
        usuario.setDataInativacao(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }

    @PodeSerAdmin
    public UsuarioResponseDTO atualizarDisponibilidade(Long id, Boolean disponibilidade) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!securityUtils.isSuperAdmin() && (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada))) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        usuario.setDisponibilidade(disponibilidade);
        Usuario atualizado = usuarioRepository.save(usuario);
        return converterParaDTO(atualizado);
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setDisponibilidade(usuario.getDisponibilidade());
        dto.setPerfil(usuario.getPerfil());
        dto.setAtivo(usuario.getAtivo());

        if (usuario.getEmpresa() != null) {
            dto.setEmpresaId(usuario.getEmpresa().getId());
            dto.setEmpresaNome(usuario.getEmpresa().getNome());
        }

        if (usuario.getInstrumentos() != null) {
            dto.setInstrumentoIds(
                usuario.getInstrumentos().stream().map(Instrumento::getId).collect(Collectors.toSet())
            );
        }

        if (usuario.getDepartamentos() != null) {
            dto.setDepartamentos(
                usuario.getDepartamentos().stream().map(Departamento::getNome).collect(Collectors.toSet())
            );
        }

        return dto;
    }
    
    public List<Usuario> listarPendentesPorUsuarioLogado(Usuario usuarioLogado, Long empresaFiltroId) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.SUPER_ADMIN) {
            if (empresaFiltroId != null) {
                return usuarioRepository.findByAtivoFalseAndEmpresaIdOrderByNomeAsc(empresaFiltroId);
            }
            return usuarioRepository.findByAtivoFalseOrderByNomeAsc();
        }

        if (usuarioLogado.getEmpresa() == null) {
            return List.of();
        }

        return usuarioRepository.findByAtivoFalseAndEmpresaIdOrderByNomeAsc(usuarioLogado.getEmpresa().getId());
    }
    
}