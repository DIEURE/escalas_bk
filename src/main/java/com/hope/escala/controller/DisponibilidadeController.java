package com.hope.escala.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 

@RestController
@RequestMapping("/api/disponibilidades")
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
