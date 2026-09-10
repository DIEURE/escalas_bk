package com.hope.escala.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.EscalaRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.EscalaDetalhesResponseDTO;
import com.hope.escala.dto.response.EscalaMusicaResponseDTO;
import com.hope.escala.dto.response.EscalaResponseDTO;
import com.hope.escala.enums.StatusEscala;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.service.EscalaService;
import com.hope.escala.service.PdfEscalaService;
import com.hope.escala.service.YoutubePlaylistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/escalas")
public class EscalaController {

	private final EscalaService escalaService;
	private final YoutubePlaylistService youTubePlaylistService;
	private final PdfEscalaService pdfEscalaService;
	private final EscalaRepository escalaRepository;

	public EscalaController(EscalaService escalaService, YoutubePlaylistService youTubePlaylistService,
			PdfEscalaService pdfEscalaService, EscalaRepository escalaRepository) {
		this.escalaService = escalaService;
		this.youTubePlaylistService = youTubePlaylistService;
		this.pdfEscalaService = pdfEscalaService;
		this.escalaRepository = escalaRepository;
	}

	@PostMapping
	public ResponseEntity<EscalaResponseDTO> salvar(@Valid @RequestBody EscalaRequestDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(escalaService.salvar(dto));
	}

	@GetMapping
	public ResponseEntity<List<EscalaResponseDTO>> listar() {
		return ResponseEntity.ok(escalaService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EscalaResponseDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(escalaService.buscarPorId(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EscalaResponseDTO> atualizar(@PathVariable Long id,
			@Valid @RequestBody EscalaRequestDTO dto) {
		return ResponseEntity.ok(escalaService.atualizar(id, dto));
	}

	@GetMapping("/verificar-conflito")
	public ResponseEntity<Boolean> verificarConflito(@RequestParam String data, @RequestParam String horario,
			@RequestParam Long departamentoId) {

		LocalDate localDate = LocalDate.parse(data);

		String horarioComSegundos = horario.length() == 5 ? horario + ":00" : horario;
		LocalTime localTime = LocalTime.parse(horarioComSegundos);

		boolean existe = escalaRepository.existeConflitoHorario(localDate, localTime, localTime, departamentoId);

		return ResponseEntity.ok(existe);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> inativar(@PathVariable Long id) {
		escalaService.inativar(id);
		return ResponseEntity.ok("Escala inativada com sucesso");
	}

	@GetMapping("/{id}/detalhes")
	public ResponseEntity<EscalaDetalhesResponseDTO> buscarDetalhesEscala(@PathVariable Long id) {
		return ResponseEntity.ok(escalaService.buscarDetalhesEscala(id));
	}

	@PostMapping("/{id}/gerar-playlist")
	public ResponseEntity<String> gerarPlaylist(@PathVariable Long id) {
		String playlistUrl = youTubePlaylistService.gerarLinkPlaylistFake(id);
		return ResponseEntity.ok(playlistUrl);
	}

	public record PlaylistManualRequest(
		    String tituloPlaylistManual,
		    List<Long> musicasIds
		) {}
	
	// ✅ NOVO: Playlist Manual (recebe lista de IDs de música e retorna a URL gerada)
	@PostMapping("/{id}/playlist-manual")
	public ResponseEntity<String> criarPlaylistManual(
	        @PathVariable Long id,
	        @RequestBody PlaylistManualRequest request) {
	    
	    String urlPlaylist = escalaService.salvarPlaylistManual(id, request.tituloPlaylistManual(), request.musicasIds());
	    return ResponseEntity.ok(urlPlaylist);
	}
	
	@GetMapping("/{id}/playlist-manual/musicas")
	public ResponseEntity<List<EscalaMusicaResponseDTO>> listarMusicasDaPlaylistManual(@PathVariable Long id) {
	    List<EscalaMusicaResponseDTO> musicas = escalaService.listarMusicasDaPlaylistManual(id);
	    return ResponseEntity.ok(musicas);
	}

	@GetMapping("/{id}/pdf")
	public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
		byte[] pdf = pdfEscalaService.gerarPdfEscala(id);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=escala.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(pdf);
	}

	@PostMapping("/agenda/{agendaMensalId}/gerar-automaticas")
	public ResponseEntity<List<EscalaResponseDTO>> gerarEscalasAutomaticas(@PathVariable Long agendaMensalId,
			@Valid @RequestBody GerarEscalasMesRequestDTO dto) {
		return ResponseEntity.ok(escalaService.gerarEscalasMes(agendaMensalId, dto));
	}

	@PostMapping("/{id}/adicionar-musicos")
	public ResponseEntity<Void> adicionarMusicos(@PathVariable Long id, @RequestBody List<Long> musicosIds) {
		escalaService.adicionarMusicos(id, musicosIds);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<EscalaResponseDTO> alterarStatus(@PathVariable Long id, @RequestBody StatusEscala status) {
		return ResponseEntity.ok(escalaService.alterarStatus(id, status));
	}
	
	 @DeleteMapping("/{id}/playlist")
	    public ResponseEntity<Void> desvincularPlaylist(@PathVariable Long id) {
	        escalaService.desvincularPlaylist(id);
	        return ResponseEntity.noContent().build();
	    }

}