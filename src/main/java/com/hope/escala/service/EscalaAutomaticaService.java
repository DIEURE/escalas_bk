package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class EscalaAutomaticaService {

    private final UsuarioRepository usuarioRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final SecurityUtils securityUtils;

    public EscalaAutomaticaService(
            UsuarioRepository usuarioRepository,
            EscalaMusicoRepository escalaMusicoRepository,
            SecurityUtils securityUtils) {
        this.usuarioRepository = usuarioRepository;
        this.escalaMusicoRepository = escalaMusicoRepository;
        this.securityUtils = securityUtils;
    }

    // Sobrecarga para quando o empresaId for informado diretamente (ex: vindo da Escala ou do Super Admin)
    public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId, Long empresaId) {
        Long empresaAlvo = empresaId != null ? empresaId : securityUtils.empresaId();

        if (empresaAlvo == null) {
            throw new RuntimeException("Não foi possível identificar a congregação para o rodízio automático.");
        }

        // 🟢 Busca os músicos disponíveis filtrados pela congregação
        List<Usuario> usuarios = usuarioRepository.buscarMusicosDisponiveisPorEmpresa(instrumentoId, departamentoId, empresaAlvo);

        // Músicos já escalados nesse mesmo dia/evento
        List<Long> usuariosJaEscalados = escalaMusicoRepository.buscarUsuariosJaEscalados(escalaId);

        usuarios = usuarios.stream()
                .filter(usuario -> !usuariosJaEscalados.contains(usuario.getId()))
                .toList();

        if (usuarios.isEmpty()) {
            return null;
        }

        // Apenas 1 músico disponível
        if (usuarios.size() == 1) {
            return usuarios.get(0);
        }

        Usuario escolhido = null;
        LocalDate dataMaisAntiga = null;

        for (Usuario usuario : usuarios) {
            LocalDate ultimaEscala = escalaMusicoRepository.buscarUltimaEscalaDoMusico(usuario.getId());

            // Nunca tocou (prioridade máxima no rodízio)
            if (ultimaEscala == null) {
                return usuario;
            }

            // Critério do rodízio: quem está há mais tempo sem tocar
            if (dataMaisAntiga == null || ultimaEscala.isBefore(dataMaisAntiga)) {
                dataMaisAntiga = ultimaEscala;
                escolhido = usuario;
            }
        }

        return escolhido;
    }

    // Mantém compatibilidade com chamadas existentes que passam 3 parâmetros
    public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId) {
        return escolherMusicoRodizio(instrumentoId, departamentoId, escalaId, null);
    }
}
