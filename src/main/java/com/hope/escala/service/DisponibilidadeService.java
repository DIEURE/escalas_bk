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

        AgendaMensal agendaMensal = agendaMensalRepository
                .findByMesEAnoEEmpresaId(mes, ano, empresaId)
                .orElse(null);

        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        disponibilidadeRepository.removerPorUsuarioPeriodoEEmpresa(usuarioId, empresaId, inicioMes, fimMes);

        if (datas == null || datas.isEmpty()) {
            return;
        }

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

        List<LocalDate> domingos = new ArrayList<>();
        LocalDate cursor = inicioMes;
        while (!cursor.isAfter(fimMes)) {
            if (cursor.getDayOfWeek() == DayOfWeek.SUNDAY) {
                domingos.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }

        List<Usuario> voluntarios = usuarioRepository.buscarUsuariosPorDepartamentoEEmpresaComPrincipal(departamentoId, empresaId);

        List<DisponibilidadeUsuario> registros = disponibilidadeRepository.buscarPorPeriodoEEmpresa(
                empresaId, inicioMes, fimMes);

        Map<String, Boolean> mapaPresenca = new HashMap<>();
        for (DisponibilidadeUsuario reg : registros) {
            String chave = reg.getUsuario().getId() + "_" + reg.getDataDisponivel();
            mapaPresenca.put(chave, true);
        }

        List<MusicoMatrizDTO> linhas = new ArrayList<>();

        for (Usuario musico : voluntarios) {
            List<StatusDataDTO> listaStatus = new ArrayList<>();

            for (LocalDate domingo : domingos) {
                String chave = musico.getId() + "_" + domingo;
                String status = Boolean.TRUE.equals(mapaPresenca.get(chave)) ? "DISPONIVEL" : "PENDENTE";
                listaStatus.add(new StatusDataDTO(domingo, status));
            }

            // ✅ CORRIGIDO: usa o campo instrumentoPrincipal (não a coleção)
            String instrumentoNome = musico.getInstrumentoPrincipal() != null
                    ? musico.getInstrumentoPrincipal().getNome()
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