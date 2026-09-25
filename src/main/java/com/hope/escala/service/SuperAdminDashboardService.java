package com.hope.escala.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.dto.CadastrosMesDTO;
import com.hope.escala.dto.DistribuicaoPerfilDTO;
import com.hope.escala.dto.EmpresaMetricaDTO;
import com.hope.escala.dto.SuperAdminDashboardDTO;
import com.hope.escala.entity.Empresa;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.UsuarioRepository;

@Service
public class SuperAdminDashboardService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    public SuperAdminDashboardService(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public SuperAdminDashboardDTO compilarMetricas() {
        // 1. Cards Numéricos de Topo
        long totalEmpresas = empresaRepository.count();
        long totalUsuariosAtivos = usuarioRepository.countByAtivoTrue();
        long totalSolicitacoesPendentes = usuarioRepository.countByAtivoFalse();

        // 2. Gráfico de Barras: Usuários Ativos e Pendentes por Empresa
        List<Empresa> empresas = empresaRepository.findAll();
        List<EmpresaMetricaDTO> usuariosPorEmpresa = new ArrayList<>();

        for (Empresa emp : empresas) {
            long ativos = usuarioRepository.countByEmpresaIdAndAtivoTrue(emp.getId());
            long pendentes = usuarioRepository.countByEmpresaIdAndAtivoFalse(emp.getId());
            usuariosPorEmpresa.add(new EmpresaMetricaDTO(emp.getId(), emp.getNome(), ativos, pendentes));
        }

        // 3. Gráfico de Linha/Área: Evolução de Cadastros nos Últimos 6 Meses
        List<CadastrosMesDTO> evolucaoCadastros = compilarEvolucaoUltimos6Meses();

        // 4. Gráfico Donut/Pizza: Distribuição por Perfil de Usuário
        List<Object[]> resultadosPerfil = usuarioRepository.contarUsuariosPorPerfilRaw();
        List<DistribuicaoPerfilDTO> distribuicaoPerfis = new ArrayList<>();

        for (Object[] linha : resultadosPerfil) {
            String perfil = linha[0] != null ? linha[0].toString() : "INDEFINIDO";
            long quantidade = linha[1] != null ? ((Number) linha[1]).longValue() : 0L;
            distribuicaoPerfis.add(new DistribuicaoPerfilDTO(perfil, quantidade));
        }

        return new SuperAdminDashboardDTO(
                totalEmpresas,
                totalUsuariosAtivos,
                totalSolicitacoesPendentes,
                usuariosPorEmpresa,
                evolucaoCadastros,
                distribuicaoPerfis
        );
    }

    private List<CadastrosMesDTO> compilarEvolucaoUltimos6Meses() {
        List<CadastrosMesDTO> lista = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/yy", new Locale("pt", "BR"));
        LocalDateTime agora = LocalDateTime.now();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime inicioMes = agora.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime fimMes = inicioMes.plusMonths(1).minusNanos(1);

            long totalNoMes = usuarioRepository.countByDataCadastroBetween(inicioMes, fimMes);
            String labelMes = inicioMes.format(formatter).toUpperCase();

            lista.add(new CadastrosMesDTO(labelMes, totalNoMes));
        }

        return lista;
    }
}
