package com.hope.escala.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.dto.request.SuspensaoRequestDTO;
import com.hope.escala.dto.response.SuspensaoResponseDTO;
import com.hope.escala.entity.SuspensaoVoluntario;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.SuspensaoRepository;
import com.hope.escala.repository.UsuarioRepository;

@Service
public class SuspensaoService {

    private final SuspensaoRepository suspensaoRepository;
    private final UsuarioRepository usuarioRepository;

    public SuspensaoService(SuspensaoRepository suspensaoRepository,
                                      UsuarioRepository usuarioRepository) {
        this.suspensaoRepository = suspensaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SuspensaoResponseDTO aplicarSuspensao(SuspensaoRequestDTO dto, Long empresaId, String usuarioLogado) {
        if (dto.usuarioId() == null || dto.departamentoId() == null) {
            throw new IllegalArgumentException("Usuário e departamento são obrigatórios.");
        }
        if (dto.mesBloqueio() == null || dto.mesBloqueio() < 1 || dto.mesBloqueio() > 12) {
            throw new IllegalArgumentException("Mês inválido.");
        }
        if (dto.anoBloqueio() == null || dto.anoBloqueio() < 2024) {
            throw new IllegalArgumentException("Ano inválido.");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Se já existir suspensão cadastrada para o mesmo período, atualiza o motivo e reativa
        Optional<SuspensaoVoluntario> existenteOpt = suspensaoRepository
                .findByUsuarioIdAndDepartamentoIdAndMesBloqueioAndAnoBloqueioAndEmpresaId(
                        dto.usuarioId(), dto.departamentoId(), dto.mesBloqueio(), dto.anoBloqueio(), empresaId);

        SuspensaoVoluntario suspensao;
        if (existenteOpt.isPresent()) {
            suspensao = existenteOpt.get();
            suspensao.setMotivo(dto.motivo());
            suspensao.setAtivo(true);
            suspensao.setCriadoPor(usuarioLogado);
        } else {
            suspensao = new SuspensaoVoluntario();
            suspensao.setUsuarioId(dto.usuarioId());
            suspensao.setDepartamentoId(dto.departamentoId());
            suspensao.setEmpresaId(empresaId);
            suspensao.setMesBloqueio(dto.mesBloqueio());
            suspensao.setAnoBloqueio(dto.anoBloqueio());
            suspensao.setMotivo(dto.motivo());
            suspensao.setCriadoPor(usuarioLogado);
            suspensao.setAtivo(true);
        }

        SuspensaoVoluntario salva = suspensaoRepository.save(suspensao);

        return toDTO(salva, usuario.getNome());
    }

    @Transactional(readOnly = true)
    public List<SuspensaoResponseDTO> listarPorPeriodo(Long departamentoId, Integer mes, Integer ano, Long empresaId) {
        List<SuspensaoVoluntario> lista = suspensaoRepository
                .findByDepartamentoIdAndMesBloqueioAndAnoBloqueioAndEmpresaId(departamentoId, mes, ano, empresaId);

        return lista.stream().map(s -> {
            String nome = usuarioRepository.findById(s.getUsuarioId())
                    .map(Usuario::getNome)
                    .orElse("Usuário #" + s.getUsuarioId());
            return toDTO(s, nome);
        }).toList();
    }

    @Transactional
    public void revogarSuspensao(Long id, Long empresaId) {
        SuspensaoVoluntario suspensao = suspensaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de suspensão não encontrado."));

        if (!suspensao.getEmpresaId().equals(empresaId)) {
            throw new SecurityException("Acesso negado.");
        }

        suspensao.setAtivo(false);
        suspensaoRepository.save(suspensao);
    }

    private SuspensaoResponseDTO toDTO(SuspensaoVoluntario s, String nomeUsuario) {
        return new SuspensaoResponseDTO(
                s.getId(),
                s.getUsuarioId(),
                nomeUsuario,
                s.getDepartamentoId(),
                s.getMesBloqueio(),
                s.getAnoBloqueio(),
                s.getMotivo(),
                s.getCriadoEm(),
                s.getCriadoPor(),
                s.getAtivo()
        );
    }
}
