package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.entity.Escala;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.DisponibilidadeUsuarioRepository;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.repository.SuspensaoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class EscalaAutomaticaService {

    private final UsuarioRepository usuarioRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final EscalaRepository escalaRepository;
    private final SecurityUtils securityUtils;
    private final SuspensaoRepository suspensaoRepository;
    private final DisponibilidadeUsuarioRepository disponibilidadeUsuarioRepository;

    public EscalaAutomaticaService(
            UsuarioRepository usuarioRepository,
            EscalaMusicoRepository escalaMusicoRepository,
            EscalaRepository escalaRepository,
            SecurityUtils securityUtils,
            SuspensaoRepository suspensaoRepository,
            DisponibilidadeUsuarioRepository disponibilidadeUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.escalaMusicoRepository = escalaMusicoRepository;
        this.escalaRepository = escalaRepository;
        this.securityUtils = securityUtils;
        this.suspensaoRepository = suspensaoRepository;
        this.disponibilidadeUsuarioRepository = disponibilidadeUsuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId, Long empresaId) {
        Long empresaAlvo = empresaId != null ? empresaId : securityUtils.empresaId();

        if (empresaAlvo == null) {
            throw new RuntimeException("Não foi possível identificar a congregação para o rodízio automático.");
        }

        Escala escala = escalaRepository.findById(escalaId)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada com o ID: " + escalaId));

        LocalDate dataEscala = escala.getDataEscala();
        int mesEscala = dataEscala.getMonthValue();
        int anoEscala = dataEscala.getYear();

        List<Usuario> todosDoInstrumento = usuarioRepository
                .buscarMusicosDisponiveisPorEmpresa(instrumentoId, departamentoId, empresaAlvo);

        if (todosDoInstrumento.isEmpty()) {
            return null;
        }

        List<Long> usuariosJaEscalados = escalaMusicoRepository.buscarUsuariosJaEscalados(escalaId);

        List<Long> usuariosSuspensos = suspensaoRepository.buscarIdsSuspensosNoMes(
                departamentoId, mesEscala, anoEscala, empresaAlvo);

        List<Long> usuariosQueMarcaramPresenca = disponibilidadeUsuarioRepository
                .buscarIdsUsuariosDisponiveisNaData(dataEscala, empresaAlvo);

        List<Usuario> candidatosElegiveis = todosDoInstrumento.stream()
                .filter(u -> usuariosQueMarcaramPresenca.contains(u.getId()))
                .filter(u -> !usuariosJaEscalados.contains(u.getId()))
                .filter(u -> !usuariosSuspensos.contains(u.getId()))
                .filter(u -> Boolean.TRUE.equals(u.getElegivelEscalaAutomatica()))
                .toList();

        if (candidatosElegiveis.isEmpty()) {
            return null;
        }

        if (candidatosElegiveis.size() == 1) {
            return candidatosElegiveis.get(0);
        }

        Usuario escolhido = null;
        LocalDate dataMaisAntiga = null;

        for (Usuario usuario : candidatosElegiveis) {
            LocalDate ultimaEscala = escalaMusicoRepository.buscarUltimaEscalaDoMusico(usuario.getId());

            if (ultimaEscala == null) {
                return usuario;
            }

            if (dataMaisAntiga == null || ultimaEscala.isBefore(dataMaisAntiga)) {
                dataMaisAntiga = ultimaEscala;
                escolhido = usuario;
            }
        }

        return escolhido;
    }

    @Transactional(readOnly = true)
    public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId) {
        return escolherMusicoRodizio(instrumentoId, departamentoId, escalaId, null);
    }
}