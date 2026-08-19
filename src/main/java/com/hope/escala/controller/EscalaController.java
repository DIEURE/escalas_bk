package com.hope.escala.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.dto.request.EscalaRequestDTO;
import com.hope.escala.dto.request.GerarEscalasMesRequestDTO;
import com.hope.escala.dto.response.EscalaDetalhesResponseDTO;
import com.hope.escala.dto.response.EscalaResponseDTO;
import com.hope.escala.security.annotation.PodeGerenciarDepartamento;
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

    public EscalaController(
            EscalaService escalaService,
            YoutubePlaylistService youTubePlaylistService,
            PdfEscalaService pdfEscalaService) {
        this.escalaService = escalaService;
        this.youTubePlaylistService = youTubePlaylistService;
        this.pdfEscalaService = pdfEscalaService;
    }

    @PostMapping
     
    public ResponseEntity<EscalaResponseDTO> salvar(
            @Valid @RequestBody EscalaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(escalaService.salvar(dto));
    }

    @GetMapping
    // ✅ Público - todos podem listar (depois filtrar por departamento no service)
    public ResponseEntity<List<EscalaResponseDTO>> listar() {
        return ResponseEntity.ok(escalaService.listar());
    }

    @GetMapping("/{id}")
      // ← Protegido
    public ResponseEntity<EscalaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(escalaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    
    public ResponseEntity<EscalaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EscalaRequestDTO dto) {
        return ResponseEntity.ok(escalaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
     
    public ResponseEntity<String> inativar(@PathVariable Long id) {
        escalaService.inativar(id);
        return ResponseEntity.ok("Escala inativada com sucesso");
    }

    @GetMapping("/{id}/detalhes")
     
    public ResponseEntity<EscalaDetalhesResponseDTO> buscarDetalhesEscala(
            @PathVariable Long id) {
        return ResponseEntity.ok(escalaService.buscarDetalhesEscala(id));
    }

    @PostMapping("/{id}/gerar-playlist")
     
    public ResponseEntity<String> gerarPlaylist(@PathVariable Long id) {
        String playlistUrl = youTubePlaylistService.gerarLinkPlaylistFake(id);
        return ResponseEntity.ok(playlistUrl);
    }

    @GetMapping("/{id}/pdf")
     
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        byte[] pdf = pdfEscalaService.gerarPdfEscala(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=escala.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @PostMapping("/agenda/{agendaMensalId}/gerar-automaticas")
     
    public ResponseEntity<List<EscalaResponseDTO>> gerarEscalasAutomaticas(
            @PathVariable Long agendaMensalId,
            @Valid @RequestBody GerarEscalasMesRequestDTO dto) {
        return ResponseEntity.ok(escalaService.gerarEscalasMes(agendaMensalId, dto));
    }
}