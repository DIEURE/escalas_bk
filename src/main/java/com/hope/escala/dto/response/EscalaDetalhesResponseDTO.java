package com.hope.escala.dto.response;

import java.util.List;

public class EscalaDetalhesResponseDTO {

	private EscalaResponseDTO escala;

	private List<EscalaMusicoResponseDTO> musicos;

	private List<EscalaMusicaResponseDTO> musicas;

	public EscalaDetalhesResponseDTO() {
	}

	public EscalaResponseDTO getEscala() {
		return escala;
	}

	public void setEscala(EscalaResponseDTO escala) {
		this.escala = escala;
	}

	public List<EscalaMusicoResponseDTO> getMusicos() {
		return musicos;
	}

	public void setMusicos(List<EscalaMusicoResponseDTO> musicos) {
		this.musicos = musicos;
	}

	public List<EscalaMusicaResponseDTO> getMusicas() {
		return musicas;
	}

	public void setMusicas(List<EscalaMusicaResponseDTO> musicas) {
		this.musicas = musicas;
	}
}