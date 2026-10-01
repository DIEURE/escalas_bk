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

        // Filtra os voluntários ativos que não estão suspensos nem escalados no mesmo evento
        List<Usuario> candidatosElegiveis = todosDoInstrumento.stream()
                .filter(u -> !usuariosJaEscalados.contains(u.getId()))
                .filter(u -> !usuariosSuspensos.contains(u.getId()))
                .toList();

        if (candidatosElegiveis.isEmpty()) {
            return null;
        }

        // 🟢 5. REGRA DO DIA 25 E DISPONIBILIDADE ATIVA
        List<Long> usuariosQueMarcaramPresenca = disponibilidadeUsuarioRepository
                .buscarIdsUsuariosDisponiveisNaData(dataEscala, empresaAlvo);

        LocalDate hoje = LocalDate.now();

        // Prazo limite oficial: dia 25 do mês anterior ao mês do culto que está sendo escalado
        LocalDate limiteDia25MesAnterior = dataEscala.withDayOfMonth(1).minusDays(1).withDayOfMonth(25);
        boolean prazoDisponibilidadeExpirado = hoje.isAfter(limiteDia25MesAnterior);

        // Separa quem deste instrumento marcou explicitamente que pode servir na data
        List<Usuario> queMarcaramDisponibilidade = candidatosElegiveis.stream()
                .filter(u -> usuariosQueMarcaramPresenca.contains(u.getId()))
                .toList();

        List<Usuario> poolFinal;

        // Se houver voluntários que marcaram disponibilidade ativa, a prioridade absoluta é deles
        if (!queMarcaramDisponibilidade.isEmpty()) {
            poolFinal = queMarcaramDisponibilidade;
        } else if (prazoDisponibilidadeExpirado || usuariosQueMarcaramPresenca.isEmpty()) {
            // Fallback automático: após o dia 25 (ou caso ninguém tenha marcado),
            // considera todo o contingente ativo elegível para não desfalcar a banda
            poolFinal = candidatosElegiveis;
        } else {
            // Fallback de segurança na geração da escala para evitar vagas em branco
            poolFinal = candidatosElegiveis;
        }

        if (poolFinal.isEmpty()) {
            return null;
        }

        // Se restou apenas um voluntário elegível, retorna diretamente
        if (poolFinal.size() == 1) {
            return poolFinal.get(0);
        }

        // 6. Critério de Rodízio Justo: seleciona quem está há mais tempo sem tocar
        Usuario escolhido = null;
        LocalDate dataMaisAntiga = null;

        for (Usuario usuario : poolFinal) {
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
