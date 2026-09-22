package com.hope.escala.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 🟢 Novo: alimenta a Data Table do frontend com paginação, busca e filtros combinados
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPaginado(String busca, PerfilUsuario perfil, Boolean ativo, Pageable pageable) {
        Long empresaIdLogada = securityUtils.empresaId();
        return usuarioRepository.listarComFiltros(empresaIdLogada, busca, perfil, ativo, pageable)
                .map(this::converterParaDTO);
    }

    @PodeSerAdmin
    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {
        Long empresaIdLogada = securityUtils.empresaId();

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já existe");
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
        empresa.setId(empresaIdLogada);
        usuario.setEmpresa(empresa);

        Usuario salvo = usuarioRepository.save(usuario);
        return converterParaDTO(salvo);
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
        usuario.setAtivo(false); // REGRA DO PROJETO: Inativo até admin/líder liberar
        usuario.setEmpresa(empresa);

        Usuario salvo = usuarioRepository.save(usuario);
        return converterParaDTO(salvo);
    }

    public List<UsuarioResponseDTO> listarPendentes() {
        Long empresaIdLogada = securityUtils.empresaId();
        return usuarioRepository.findByEmpresaIdAndAtivoFalse(empresaIdLogada)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @PodeSerAdmin 
    public UsuarioResponseDTO aprovar(Long id, UsuarioRequestDTO dto) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário pendente não encontrado"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
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
            emailService.enviarEmailAprovacao(empresaIdLogada, aprovado.getEmail(), aprovado.getNome());
        } catch (Exception e) {
            System.err.println("Aviso: Usuário aprovado, mas falhou ao enviar o e-mail: " + e.getMessage());
        }

        return converterParaDTO(aprovado);
    }

    // 🟢 Novo: Alternar status rápido com um clique direto na tabela
    @PodeSerAdmin
    public UsuarioResponseDTO alternarStatus(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        boolean novoStatus = !Boolean.TRUE.equals(usuario.getAtivo());
        usuario.setAtivo(novoStatus);

        if (!novoStatus) {
            usuario.setDataInativacao(LocalDateTime.now());
        } else {
            usuario.setDataInativacao(null);
            // Dispara e-mail se foi liberado agora
            try {
                emailService.enviarEmailAprovacao(empresaIdLogada, usuario.getEmail(), usuario.getNome());
            } catch (Exception e) {
                System.err.println("Aviso: Status ativado, mas falhou envio de e-mail: " + e.getMessage());
            }
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        return converterParaDTO(atualizado);
    }

    public List<UsuarioResponseDTO> listar() {
        Long empresaIdLogada = securityUtils.empresaId();
        return usuarioRepository.findByEmpresaIdAndAtivoTrue(empresaIdLogada)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        return converterParaDTO(usuario);
    }

    public List<UsuarioResponseDTO> buscarPorDepartamento(Long departamentoId) {
        Long empresaIdLogada = securityUtils.empresaId();
        return usuarioRepository.findByDepartamentosIdAndEmpresaIdAndAtivoTrue(departamentoId, empresaIdLogada)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já existe");
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
        return converterParaDTO(atualizado);
    }

    @PodeSerAdmin
    public void inativar(Long id) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        usuario.setAtivo(false);
        usuario.setDataInativacao(LocalDateTime.now());

        usuarioRepository.save(usuario);
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

        if (usuario.getInstrumentos() != null) {
            dto.setInstrumentoIds(
                usuario.getInstrumentos().stream().map(Instrumento::getId).collect(Collectors.toSet())
            );
            // Dica: se o seu DTO tiver um campo para nomes ou objetos dos instrumentos:
            // dto.setInstrumentos(usuario.getInstrumentos().stream().map(Instrumento::getNome).collect(Collectors.toList()));
        }

        if (usuario.getDepartamentos() != null) {
            dto.setDepartamentos(
                usuario.getDepartamentos().stream().map(Departamento::getNome).collect(Collectors.toSet())
            );
        }

        return dto;
    }

    public UsuarioResponseDTO atualizarDisponibilidade(Long id, Boolean disponibilidade) {
        Long empresaIdLogada = securityUtils.empresaId();

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaIdLogada)) {
			throw new ResourceNotFoundException("Usuário não pertence à sua instituição");
        }

        usuario.setDisponibilidade(disponibilidade);
        Usuario atualizado = usuarioRepository.save(usuario);
        return converterParaDTO(atualizado);
    }
}
