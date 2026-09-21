package com.hope.escala.controller;

import com.hope.escala.entity.ExcecaoEscalaData;
import com.hope.escala.security.annotation.AdminOuLider;
import com.hope.escala.service.ExcecaoEscalaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/excecoes-escala")
@CrossOrigin(origins = "*")
public class ExcecaoEscalaController {

    private final ExcecaoEscalaService service;

    public ExcecaoEscalaController(ExcecaoEscalaService service) {
        this.service = service;
    }

    @AdminOuLider
    @GetMapping
    public ResponseEntity<List<ExcecaoEscalaData>> listar(
            @RequestParam(required = false) Long departamentoId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim
    ) {
        List<ExcecaoEscalaData> lista = service.listarPorPeriodo(departamentoId, inicio, fim);
        return ResponseEntity.ok(lista);
    }

    @AdminOuLider
    @PostMapping
    public ResponseEntity<ExcecaoEscalaData> salvar(@RequestBody ExcecaoEscalaData excecao) {
        ExcecaoEscalaData salva = service.salvarOuAtualizar(excecao);
        return ResponseEntity.ok(salva);
    }
    
    
    @AdminOuLider
    @GetMapping("/todas")
    public ResponseEntity<List<ExcecaoEscalaData>> listarTodas() {
        List<ExcecaoEscalaData> lista = service.listarTodasPorEmpresa();
        return ResponseEntity.ok(lista);
    }

 

    @AdminOuLider
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
