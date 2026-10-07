package com.hope.escala.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.hope.escala.dto.request.AtualizarStatusVotacaoDTO;
import com.hope.escala.dto.request.CriarPautaItemDTO;
import com.hope.escala.dto.request.RegistrarVotoRequestDTO;
import com.hope.escala.dto.response.PautaDetalheResponseDTO;
import com.hope.escala.service.AtaReuniaoService;

@RestController
@RequestMapping("/pautas")
public class PautaReuniaoController {

    private final AtaReuniaoService ataService;

    public PautaReuniaoController(AtaReuniaoService ataService) {
        this.ataService = ataService;
    }

    @PostMapping("/ata/{ataId}")
    public ResponseEntity<PautaDetalheResponseDTO> adicionarPauta(
            @PathVariable Long ataId,
            @RequestBody CriarPautaItemDTO dto) {
        PautaDetalheResponseDTO resposta = ataService.adicionarPauta(ataId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/{id}/votar")
    public ResponseEntity<Void> votar(
            @PathVariable Long id,
            @RequestBody RegistrarVotoRequestDTO dto,
            Authentication authentication) {
        
        // Passa a autenticação ativa ou delega a extração ao service
        ataService.registrarVoto(id, dto, authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping({ "/{id}/status-votacao", "/{id}/status" })
    public ResponseEntity<Void> alterarStatusVotacao(
            @PathVariable Long id,
            @RequestBody AtualizarStatusVotacaoDTO dto) {
        ataService.alterarStatusVotacaoPauta(id, dto.status());
        return ResponseEntity.noContent().build();
    }
}
