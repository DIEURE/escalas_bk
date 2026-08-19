
package com.hope.escala.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.hope.escala.dto.response.EscalaDetalhesResponseDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.dto.response.EscalaMusicoResponseDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfEscalaService {

	private final EscalaService escalaService;

	public PdfEscalaService(EscalaService escalaService) {

		this.escalaService = escalaService;
	}

	public byte[] gerarPdfEscala(Long escalaId) {

		try {

			EscalaDetalhesResponseDTO detalhes = escalaService.buscarDetalhesEscala(escalaId);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Document document = new Document();

			PdfWriter.getInstance(document, outputStream);

			document.open();

			adicionarCabecalho(document, detalhes);

			adicionarMusicos(document, detalhes);

			adicionarRepertorio(document, detalhes);

			adicionarPlaylist(document, detalhes);

			document.close();

			return outputStream.toByteArray();

		} catch (Exception e) {

			throw new RuntimeException("Erro ao gerar PDF da escala");
		}
	}

	private void adicionarCabecalho(Document document, EscalaDetalhesResponseDTO detalhes) throws Exception {

		document.add(new Paragraph("ESCALA DO CULTO"));

		document.add(new Paragraph("Culto: " + detalhes.getEscala().getCulto()));

		document.add(new Paragraph("Data: " + detalhes.getEscala().getDataEscala()));

		document.add(new Paragraph("Horário: " + detalhes.getEscala().getHorario()));

		if (detalhes.getEscala().getNomeDepartamento() != null) {

			document.add(new Paragraph("Departamento: " + detalhes.getEscala().getNomeDepartamento()));
		}

		document.add(new Paragraph(" "));
	}

	private void adicionarMusicos(Document document, EscalaDetalhesResponseDTO detalhes) throws Exception {

		document.add(new Paragraph("MÚSICOS"));

		for (EscalaMusicoResponseDTO musico : detalhes.getMusicos()) {

			String linha = musico.getNomeUsuario() + " - " + musico.getInstrumento();

			if (Boolean.TRUE.equals(musico.getConfirmado())) {

				linha += " (CONFIRMADO)";
			}

			if (Boolean.TRUE.equals(musico.getSubstituido())) {

				linha += " -> Substituto: " + musico.getNomeSubstituto();
			}

			document.add(new Paragraph(linha));
		}

		document.add(new Paragraph(" "));
	}

	private void adicionarRepertorio(Document document, EscalaDetalhesResponseDTO detalhes) throws Exception {

		document.add(new Paragraph("REPERTÓRIO"));

		for (EscalaMusicaResponseDTO musica : detalhes.getMusicas()) {

			String linha = musica.getOrdem() + " - " + musica.getNomeMusica() + " (Tom " + musica.getTom() + ")";

			if (Boolean.TRUE.equals(musica.getSubstituida())) {

				linha += " -> Substituta: " + musica.getNomeMusicaSubstituta();
			}

			document.add(new Paragraph(linha));

			if (musica.getCifraUrl() != null) {

				document.add(new Paragraph("Cifra: " + musica.getCifraUrl()));
			}

			if (musica.getYoutubeVideoId() != null) {

				document.add(new Paragraph("YouTube: " + musica.getYoutubeVideoId()));
			}

			document.add(new Paragraph(" "));
		}
	}

	private void adicionarPlaylist(Document document, EscalaDetalhesResponseDTO detalhes) throws Exception {

		if (detalhes.getEscala().getYoutubePlaylistUrl() != null) {

			document.add(new Paragraph("PLAYLIST YOUTUBE"));

			document.add(new Paragraph(detalhes.getEscala().getYoutubePlaylistUrl()));
		}
	}
}
