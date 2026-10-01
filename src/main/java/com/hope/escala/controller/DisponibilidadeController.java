package com.hope.escala.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hope.escala.service.DisponibilidadeService;
 

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadeController {

    private final DisponibilidadeService service;

    public DisponibilidadeController(DisponibilidadeService service) {
        this.service = service;
    }

    // Retorna as datas que o voluntário logado marcou para um determinado mês/ano
    @GetMapping("/mes")
    public ResponseEntity<List<LocalDate>> buscarMinhasDatasPorMes(
            @RequestParam int mes, 
            @RequestParam int ano) {
        return ResponseEntity.ok(service.buscarMinhasDatasPorMes(mes, ano));
    }

    @GetMapping("/matriz")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN', 'VOLUNTARIO', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<MatrizDisponibilidadeResponseDTO> obterMatriz(
            @RequestParam Long departamentoId,
            @RequestParam int mes,
            @RequestParam int ano) {
        
        MatrizDisponibilidadeResponseDTO response = disponibilidadeService.obterMatrizDisponibilidade(
                departamentoId, mes, ano);
        return ResponseEntity.ok(response);
    }
    // Salva a lista completa de datas marcadas pelo usuário para o mês
    @PostMapping("/salvar-lote")
    public ResponseEntity<Void> salvarLote(
            @RequestParam int mes,
            @RequestParam int ano,
            @RequestBody List<LocalDate> datas) {
        service.salvarLote(mes, ano, datas);
        return ResponseEntity.ok().build();
    }
    
    
}
