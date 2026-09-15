package com.hope.escala.controller;

import com.hope.escala.entity.ExcecaoEscalaData;
import com.hope.escala.service.ExcecaoEscalaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/excecoes-escala")
@CrossOrigin(origins = "*")
public class ExcecaoEscalaController {

    private final ExcecaoEscalaService service;

    public ExcecaoEscalaController(ExcecaoEscalaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExcecaoEscalaData>> listar(
            @RequestParam Long departamentoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        List<ExcecaoEscalaData> lista = service.listarPorPeriodo(departamentoId, inicio, fim);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<ExcecaoEscalaData> salvar(@RequestBody ExcecaoEscalaData excecao) {
        ExcecaoEscalaData salva = service.salvarOuAtualizar(excecao);
        return ResponseEntity.ok(salva);
    }
    
    // Novo endpoint para listar todas as exceções (usado no Dashboard)
    @GetMapping("/todas")
    public ResponseEntity<List<ExcecaoEscalaData>> listarTodas() {
        List<ExcecaoEscalaData> lista = service.listarTodas();
        return ResponseEntity.ok(lista);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
