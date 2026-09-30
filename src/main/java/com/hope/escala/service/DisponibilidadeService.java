package com.hope.escala.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
