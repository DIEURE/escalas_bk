package com.hope.escala.service;

import java.util.Map;

 

import org.apache.http.ssl.SSLContextBuilder;
import org.apache.tomcat.jni.SSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.hope.escala.entity.YoutubeConfig;
import com.hope.escala.repository.YoutubeConfigRepository;

@Service
public class YoutubeService {

    @Autowired
    private YoutubeConfigRepository repository;

    public YoutubeConfig getConfig() {
        // Pega o primeiro registro da tabela, independente do ID, ou cria um novo se estiver vazia
        return repository.findAll().stream().findFirst().orElseGet(() -> {
            YoutubeConfig novo = new YoutubeConfig();
            return repository.save(novo);
        });
    }

    public YoutubeConfig salvarConfig(YoutubeConfig novaConfig) {
        // Busca se já existe qualquer configuração salva na tabela
        YoutubeConfig configAtual = repository.findAll().stream().findFirst().orElse(new YoutubeConfig());

        if (novaConfig.getClientId() != null && !novaConfig.getClientId().trim().isEmpty()) {
            configAtual.setClientId(novaConfig.getClientId().trim());
        }
        if (novaConfig.getClientSecret() != null && !novaConfig.getClientSecret().trim().isEmpty()) {
            configAtual.setClientSecret(novaConfig.getClientSecret().trim());
        }
        if (novaConfig.getRedirectUri() != null && !novaConfig.getRedirectUri().trim().isEmpty()) {
            configAtual.setRedirectUri(novaConfig.getRedirectUri().trim());
        }
        if (novaConfig.getRefreshToken() != null && !novaConfig.getRefreshToken().trim().isEmpty()) {
            configAtual.setRefreshToken(novaConfig.getRefreshToken().trim());
        }

        // Salva sem forçar o ID manualmente, deixando o JPA gerenciar o registro único
        return repository.save(configAtual);
    }
    
 
    // Dentro do seu método trocarCodePorRefreshToken:
        public void trocarCodePorRefreshToken(String code) {
            YoutubeConfig config = getConfig();
            if (config.getClientId() == null || config.getClientSecret() == null || config.getRedirectUri() == null) {
                throw new IllegalArgumentException("Client ID, Client Secret e Redirect URI precisam estar configurados.");
            }

            String tokenUrl = "https://oauth2.googleapis.com/token";

            RestTemplate restTemplate;
            try {
                // Cria um TrustManager que confia em qualquer certificado (ignora o erro SSL local)
                javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
                };

                javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                restTemplate = new RestTemplate();
            } catch (Exception e) {
                restTemplate = new RestTemplate();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", code);
            body.add("client_id", config.getClientId().trim());
            body.add("client_secret", config.getClientSecret().trim());
            body.add("redirect_uri", config.getRedirectUri().trim());
            body.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    Map<String, Object> data = response.getBody();
                    String refreshToken = (String) data.get("refresh_token");

                    if (refreshToken != null) {
                        config.setRefreshToken(refreshToken);
                        repository.save(config);
                    } else {
                        throw new RuntimeException("O Google não retornou um refresh_token. Tente revogar o acesso na sua conta Google e conectar novamente.");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro ao trocar o código de autorização pelo token: " + e.getMessage());
            }
        }

    public String gerarAuthUrl() {
        YoutubeConfig config = getConfig();
        if (config.getClientId() == null || config.getRedirectUri() == null) {
            throw new IllegalArgumentException("Configure o Client ID e a Redirect URI primeiro.");
        }

        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + config.getClientId().trim() +
                "&redirect_uri=" + config.getRedirectUri().trim() +
                "&response_type=code" +
                "&scope=https://www.googleapis.com/auth/youtube" +
                "&access_type=offline" +
                "&prompt=consent";
    }
}