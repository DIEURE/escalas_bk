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
        this.securityUtils = securityUtils;
        this.suspensaoRepository = suspensaoRepository;
        this.escalaRepository = escalaRepository;
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

        // 2. Busca todos os músicos cadastrados para o instrumento/departamento na congregação
        List<Usuario> usuarios = usuarioRepository.buscarMusicosDisponiveisPorEmpresa(instrumentoId, departamentoId, empresaAlvo);

        // 3. Músicos já escalados nesse mesmo dia/evento (evita duplicidade no mesmo culto)
        List<Long> usuariosJaEscalados = escalaMusicoRepository.buscarUsuariosJaEscalados(escalaId);

        // 4. Músicos suspensos no mês por faltas consecutivas ou recusas injustificadas
        List<Long> usuariosSuspensos = suspensaoRepository.buscarIdsSuspensosNoMes(
                departamentoId, mesEscala, anoEscala, empresaAlvo);

        // 🟢 5. REGRA DO DIA 25: Disponibilidade Ativa vs Rodízio Completo
        // Busca quem marcou que PODIA tocar nesta data
        List<Long> usuariosQueMarcaramPresenca = disponibilidadeUsuarioRepository.buscarIdsUsuariosDisponiveisNaData(dataEscala, empresaAlvo);

        LocalDate hoje = LocalDate.now();
        // A escala sendo gerada é para um mês futuro e já passou do dia 25 do mês corrente?
        // Ou seja, se hoje for dia 26 ou mais e a lista de quem marcou estiver vazia/esgotada:
        boolean passouDoPrazoDia25 = hoje.getDayOfMonth() > 25;

        // Se houver voluntários que marcaram disponibilidade, damos prioridade absoluta a eles!
        if (!usuariosQueMarcaramPresenca.isEmpty()) {
            List<Usuario> filtradosPorDisponibilidade = usuarios.stream()
                    .filter(u -> usuariosQueMarcaramPresenca.contains(u.getId()))
                    .filter(u -> !usuariosJaEscalados.contains(u.getId()))
                    .filter(u -> !usuariosSuspensos.contains(u.getId()))
                    .toList();

            // Se encontrou alguém que declarou que pode ir, usa essa lista restrita
            if (!filtradosPorDisponibilidade.isEmpty()) {
                usuarios = filtradosPorDisponibilidade;
            } else if (passouDoPrazoDia25) {
                // Se passou do dia 25 e as opções de quem marcou se esgotaram, abre para o contingente geral
                usuarios = usuarios.stream()
                        .filter(u -> !usuariosJaEscalados.contains(u.getId()))
                        .filter(u -> !usuariosSuspensos.contains(u.getId()))
                        .toList();
            } else {
                return null;
            }
        } else {
            // NINGUÉM marcou previamente:
            if (passouDoPrazoDia25) {
                // Passou do prazo limite: gera automaticamente com todos do departamento (modo tradicional)
                usuarios = usuarios.stream()
                        .filter(u -> !usuariosJaEscalados.contains(u.getId()))
                        .filter(u -> !usuariosSuspensos.contains(u.getId()))
                        .toList();
            } else {
                // Ainda está dentro da janela de marcação (até o dia 25), não escala compulsoriamente
                return null;
            }
        }

        if (usuarios.isEmpty()) {
            return null;
        }

        // Apenas 1 voluntário elegível
        if (usuarios.size() == 1) {
            return usuarios.get(0);
        }

        // 6. Critério de Rodízio Justo: quem está há mais tempo sem tocar
        Usuario escolhido = null;
        LocalDate dataMaisAntiga = null;

        for (Usuario usuario : usuarios) {
            LocalDate ultimaEscala = escalaMusicoRepository.buscarUltimaEscalaDoMusico(usuario.getId());

            // Quem nunca tocou tem a prioridade máxima
            if (ultimaEscala == null) {
                return usuario;
            }

            // Seleciona a data mais antiga
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
