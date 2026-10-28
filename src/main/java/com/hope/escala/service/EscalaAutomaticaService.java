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

        // 1. Busca a escala para obter a data do culto
        Escala escala = escalaRepository.findById(escalaId)
                .orElseThrow(() -> new RuntimeException("Escala não encontrada com o ID: " + escalaId));

        LocalDate dataEscala = escala.getDataEscala();
        int mesEscala = dataEscala.getMonthValue();
        int anoEscala = dataEscala.getYear();

        // 2. Busca todos os voluntários cadastrados para o instrumento/departamento na empresa
        List<Usuario> todosDoInstrumento = usuarioRepository.buscarMusicosDisponiveisPorEmpresa(instrumentoId, departamentoId, empresaAlvo);

        if (todosDoInstrumento.isEmpty()) {
            return null;
        }

        // 3. IDs de voluntários já escalados nesse mesmo culto/evento (evita duplicidade no mesmo culto)
        List<Long> usuariosJaEscalados = escalaMusicoRepository.buscarUsuariosJaEscalados(escalaId);

        // 4. IDs de voluntários suspensos no mês por faltas consecutivas ou recusas injustificadas
        List<Long> usuariosSuspensos = suspensaoRepository.buscarIdsSuspensosNoMes(
                departamentoId, mesEscala, anoEscala, empresaAlvo);

        // 5. Filtrar candidatos que marcaram disponibilidade na data exata
        List<Long> usuariosQueMarcaramPresenca = disponibilidadeUsuarioRepository
                .buscarIdsUsuariosDisponiveisNaData(dataEscala, empresaAlvo);

        // ✅ REGRA NOVA: sem marcação de disponibilidade = não entra. Ponto.
        // Não há fallback. Não há exceção. Não há "prazo do dia 25".
        List<Usuario> candidatosElegiveis = todosDoInstrumento.stream()
                .filter(u -> usuariosQueMarcaramPresenca.contains(u.getId()))
                .filter(u -> !usuariosJaEscalados.contains(u.getId()))
                .filter(u -> !usuariosSuspensos.contains(u.getId()))
                .toList();

        if (candidatosElegiveis.isEmpty()) {
            return null;
        }

        // Se restou apenas um voluntário elegível, retorna diretamente
        if (candidatosElegiveis.size() == 1) {
            return candidatosElegiveis.get(0);
        }

        // 6. Critério de Rodízio Justo: seleciona quem está há mais tempo sem tocar
        Usuario escolhido = null;
        LocalDate dataMaisAntiga = null;

        for (Usuario usuario : candidatosElegiveis) {
            LocalDate ultimaEscala = escalaMusicoRepository.buscarUltimaEscalaDoMusico(usuario.getId());

            // Prioridade máxima para quem nunca tocou
            if (ultimaEscala == null) {
                return usuario;
            }

            // Seleciona a data de escala mais remota no histórico
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