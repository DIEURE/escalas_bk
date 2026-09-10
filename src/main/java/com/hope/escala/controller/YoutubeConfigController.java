package com.hope.escala.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hope.escala.entity.YoutubeConfig;
import com.hope.escala.service.YoutubeService;

@RestController
@RequestMapping("/youtube-config")
@CrossOrigin(origins = "*")
public class YoutubeConfigController {

    @Autowired
    private YoutubeService youtubeService;

    @GetMapping
    public ResponseEntity<YoutubeConfig> getConfig() {
        return ResponseEntity.ok(youtubeService.getConfig());
    }

    @PostMapping
    public ResponseEntity<YoutubeConfig> salvarConfig(@RequestBody YoutubeConfig novaConfig) {
        YoutubeConfig salva = youtubeService.salvarConfig(novaConfig);
        return ResponseEntity.ok(salva);
    }

    @GetMapping("/auth-url")
    public ResponseEntity<String> getAuthUrl() {
        try {
            String authUrl = youtubeService.gerarAuthUrl();
            return ResponseEntity.ok(authUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/callback")
    public ResponseEntity<?> callbackGoogle(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Código de autorização não fornecido.");
        }

        try {
            youtubeService.trocarCodePorRefreshToken(code);
            return ResponseEntity.ok(Map.of("message", "Conta do YouTube conectada com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> verificarStatusConexao() {
        boolean conectado = youtubeService.isConfiguradoEConectado();
        return ResponseEntity.ok(Map.of("conectado", conectado));
    }
    
    /*Pesquisa direta Youtube*/
    @GetMapping("/search")
    public ResponseEntity<?> pesquisarVideos(@RequestParam String query) {
        try {
            List<Map<String, String>> resultados = youtubeService.pesquisarVideos(query);
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

}