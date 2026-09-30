package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.entity.Escala;
import com.hope.escala.entity.Usuario;
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

    public EscalaAutomaticaService(
            UsuarioRepository usuarioRepository,
            EscalaMusicoRepository escalaMusicoRepository,
            EscalaRepository escalaRepository,
            SecurityUtils securityUtils,
            SuspensaoRepository suspensaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.escalaMusicoRepository = escalaMusicoRepository;
        this.securityUtils = securityUtils;
        this.suspensaoRepository = suspensaoRepository;
        this.escalaRepository = escalaRepository;
    }
    
    @Transactional(readOnly = true)
    public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId, Long empresaId) {
        Long empresaAlvo = empresaId != null ? empresaId : securityUtils.empresaId();

        if (empresaAlvo == null) {
            throw new RuntimeException("Não foi possível identificar a congregação para o rodízio automático.");
        }

        // 1. Busca a escala para obter a data (mês e ano) da realização do culto
        Escala escala = escalaRepository.findById(escalaId)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada com o ID: " + escalaId));

        int mesEscala = escala.getDataEscala().getMonthValue();
        int anoEscala = escala.getDataEscala().getYear();

        // 2. Busca os músicos disponíveis filtrados pela congregação
        List<Usuario> usuarios = usuarioRepository.buscarMusicosDisponiveisPorEmpresa(instrumentoId, departamentoId, empresaAlvo);

        // 3. Músicos já escalados nesse mesmo dia/evento
        List<Long> usuariosJaEscalados = escalaMusicoRepository.buscarUsuariosJaEscalados(escalaId);

        // 4. 🟢 Músicos suspensos no mês por critério disciplinar (faltas/recusas sem justificativa)
        List<Long> usuariosSuspensos = suspensaoRepository.buscarIdsSuspensosNoMes(
                departamentoId, mesEscala, anoEscala, empresaAlvo);

        // 5. Filtra retirando quem já está escalado e quem está suspenso no mês
        usuarios = usuarios.stream()
                .filter(usuario -> !usuariosJaEscalados.contains(usuario.getId()))
                .filter(usuario -> !usuariosSuspensos.contains(usuario.getId()))
                .toList();

        if (usuarios.isEmpty()) {
            return null;
        }

        // Apenas 1 músico disponível após os filtros
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
    @Transactional(readOnly = true)
    public Usuario escolherMusicoRodizio(Long instrumentoId, Long departamentoId, Long escalaId) {
        return escolherMusicoRodizio(instrumentoId, departamentoId, escalaId, null);
    }
    
  
    
    
}
