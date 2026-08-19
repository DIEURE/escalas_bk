package com.hope.escala.dto.request;

import jakarta.validation.constraints.NotNull;

public class EscalaMusicoRequestDTO {

	@NotNull(message = "Escala é obrigatória")
	private Long escalaId;

	@NotNull(message = "Usuário é obrigatório")
	private Long usuarioId;

	private String observacao;

	public EscalaMusicoRequestDTO() {
	}

	public Long getEscalaId() {
		return escalaId;
	}

	public void setEscalaId(Long escalaId) {
		this.escalaId = escalaId;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}