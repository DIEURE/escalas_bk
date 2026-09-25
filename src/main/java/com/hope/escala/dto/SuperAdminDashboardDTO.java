package com.hope.escala.dto;

 

import java.util.List;

public record SuperAdminDashboardDTO(
    long totalEmpresas,
    long totalUsuariosAtivos,
    long totalSolicitacoesPendentes,
    List<EmpresaMetricaDTO> usuariosPorEmpresa,
    List<CadastrosMesDTO> evolucaoCadastros,
    List<DistribuicaoPerfilDTO> distribuicaoPerfis
) {

	public long totalEmpresas() {
		return totalEmpresas;
	}

	public long totalUsuariosAtivos() {
		return totalUsuariosAtivos;
	}

	public long totalSolicitacoesPendentes() {
		return totalSolicitacoesPendentes;
	}

	public List<EmpresaMetricaDTO> usuariosPorEmpresa() {
		return usuariosPorEmpresa;
	}

	public List<CadastrosMesDTO> evolucaoCadastros() {
		return evolucaoCadastros;
	}

	public List<DistribuicaoPerfilDTO> distribuicaoPerfis() {
		return distribuicaoPerfis;
	}}
