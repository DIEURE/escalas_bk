package com.hope.escala.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.dto.response.MatrizDisponibilidadeResponseDTO;
import com.hope.escala.dto.response.MatrizDisponibilidadeResponseDTO.MusicoMatrizDTO;
import com.hope.escala.dto.response.MatrizDisponibilidadeResponseDTO.StatusDataDTO;
import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.DisponibilidadeUsuario;
import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.Usuario;
import com.hope.escala.repository.AgendaMensalRepository;
import com.hope.escala.repository.DisponibilidadeUsuarioRepository;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.UsuarioRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class DisponibilidadeService {

    private final DisponibilidadeUsuarioRepository disponibilidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final AgendaMensalRepository agendaMensalRepository;
    private final SecurityUtils securityUtils;

    public DisponibilidadeService(
            DisponibilidadeUsuarioRepository disponibilidadeRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            AgendaMensalRepository agendaMensalRepository,
            SecurityUtils securityUtils) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.agendaMensalRepository = agendaMensalRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Retorna a lista de LocalDate que o usuário logado marcou como disponíveis no mês/ano informados.
     */
    @Transactional(readOnly = true)
    public List<LocalDate> buscarMinhasDatasPorMes(int mes, int ano) {
        Long usuarioId = securityUtils.usuarioId();
        Long empresaId = securityUtils.empresaId();

        if (usuarioId == null || empresaId == null) {
            throw new RuntimeException("Usuário ou congregação não identificados na sessão.");
        }

        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        return disponibilidadeRepository.buscarDatasMarcadasPorPeriodo(
                usuarioId, empresaId, inicioMes, fimMes);
    }

    /**
     * Salva a lista de datas em que o voluntário pode servir no mês informado.
     * Limpa as marcações anteriores daquele mês e grava o novo lote.
     */
    @Transactional
    public void salvarLote(int mes, int ano, List<LocalDate> datas) {
        Long usuarioId = securityUtils.usuarioId();
        Long empresaId = securityUtils.empresaId();

        if (usuarioId == null || empresaId == null) {
            throw new RuntimeException("Usuário ou congregação não identificados na sessão.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Congregação não encontrada: " + empresaId));

        // Busca ou vincula a agenda mensal do período (se existir na base)
        AgendaMensal agendaMensal = agendaMensalRepository
                .findByMesEAnoEEmpresaId(mes, ano, empresaId)
                .orElse(null);

        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        // 1. Remove as marcações prévias do voluntário para aquele mês (estratégia replace)
        disponibilidadeRepository.removerPorUsuarioPeriodoEEmpresa(usuarioId, empresaId, inicioMes, fimMes);

        // 2. Se a lista veio vazia (o voluntário desmarcou tudo), finaliza aqui
        if (datas == null || datas.isEmpty()) {
            return;
        }

        // 3. Insere apenas as datas válidas dentro do mês solicitado
        List<DisponibilidadeUsuario> novasDisponibilidades = datas.stream()
                .filter(d -> d.getMonthValue() == mes && d.getYear() == ano)
                .distinct()
                .map(data -> {
                    DisponibilidadeUsuario disp = new DisponibilidadeUsuario();
                    disp.setUsuario(usuario);
                    disp.setDataDisponivel(data);
                    disp.setEmpresa(empresa);
                    disp.setAgendaMensal(agendaMensal);
                    return disp;
                })
                .toList();

        disponibilidadeRepository.saveAll(novasDisponibilidades);
    }
    
    @Transactional(readOnly = true)
    public MatrizDisponibilidadeResponseDTO obterMatrizDisponibilidade(Long departamentoId, int mes, int ano) {
        Long empresaId = securityUtils.empresaId();

        YearMonth yearMonth = YearMonth.of(ano, mes);
        LocalDate inicioMes = yearMonth.atDay(1);
        LocalDate fimMes = yearMonth.atEndOfMonth();

        // 1. Considera apenas os domingos do mês (regra do Hope Escala Pro)
        List<LocalDate> domingos = new ArrayList<>();
        LocalDate cursor = inicioMes;
        while (!cursor.isAfter(fimMes)) {
            if (cursor.getDayOfWeek() == DayOfWeek.SUNDAY) {
                domingos.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }

        // 2. Busca voluntários ativos do departamento na congregação
        List<Usuario> voluntarios = usuarioRepository.buscarUsuariosPorDepartamentoEEmpresa(departamentoId, empresaId);

        // 3. Busca marcações de presença do mês
        List<DisponibilidadeUsuario> registros = disponibilidadeRepository.buscarPorPeriodoEEmpresa(
                empresaId, inicioMes, fimMes);

        // Mapeia por "usuarioId_data" -> true
        Map<String, Boolean> mapaPresenca = new HashMap<>();
        for (DisponibilidadeUsuario reg : registros) {
            String chave = reg.getUsuario().getId() + "_" + reg.getDataDisponivel();
            mapaPresenca.put(chave, true);
        }


        // 4. Monta as linhas da matriz para cada voluntário
        List<MusicoMatrizDTO> linhas = new ArrayList<>();

        for (Usuario musico : voluntarios) {
            List<StatusDataDTO> listaStatus = new ArrayList<>();

            for (LocalDate domingo : domingos) {
                String chave = musico.getId() + "_" + domingo;
                
                // Se a chave existe no mapa, o voluntário marcou disponibilidade
                String status = Boolean.TRUE.equals(mapaPresenca.get(chave)) ? "DISPONIVEL" : "PENDENTE";

                listaStatus.add(new StatusDataDTO(domingo, status));
            }

            String instrumentoNome = (musico.getInstrumentos() != null && !musico.getInstrumentos().isEmpty())
                    ? musico.getInstrumentos().iterator().next().getNome()
                    : "Geral";

            linhas.add(new MusicoMatrizDTO(
                    musico.getId(),
                    musico.getNome(),
                    instrumentoNome,
                    listaStatus
            ));
        }

        return new MatrizDisponibilidadeResponseDTO(mes, ano, domingos, linhas);
    }

}
