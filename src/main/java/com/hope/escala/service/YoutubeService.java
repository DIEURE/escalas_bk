package com.hope.escala.service;

import java.util.List;
import java.util.Map;
 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.hope.escala.entity.Empresa;
import com.hope.escala.entity.YoutubeConfig;
import com.hope.escala.repository.EmpresaRepository;
import com.hope.escala.repository.YoutubeConfigRepository;
import com.hope.escala.security.SecurityUtils;

@Service
public class YoutubeService {

	private final YoutubeConfigRepository repository;
	private final SecurityUtils securityUtils; 
	private final EmpresaRepository empresaRepository;
	

	public YoutubeService(YoutubeConfigRepository repository, SecurityUtils securityUtils, EmpresaRepository empresaRepository
			) {
	    this.repository = repository;
	    this.securityUtils = securityUtils;
	    this.empresaRepository = empresaRepository;
	}

	 
    public YoutubeConfig getConfig() {
        Long empresaId = securityUtils.empresaId(); // Pega a empresa da sessão JWT
        
        return repository.findByEmpresaId(empresaId).orElseGet(() -> {
            YoutubeConfig novo = new YoutubeConfig();
            
            // Busca a entidade Empresa para associar corretamente na criação
            Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada para o ID: " + empresaId));
            
            novo.setEmpresa(empresa);
            return repository.save(novo);
        });
    }


	public YoutubeConfig salvarConfig(YoutubeConfig novaConfig) {
		// 🟢 CORREÇÃO: Utiliza o getConfig() para garantir que pertence à empresa logada
		YoutubeConfig configAtual = getConfig();

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
		if (novaConfig.getApiKey() != null && !novaConfig.getApiKey().trim().isEmpty()) {
		    configAtual.setApiKey(novaConfig.getApiKey().trim());
		}

		return repository.save(configAtual);
	}


	public void trocarCodePorRefreshToken(String code) {
		YoutubeConfig config = getConfig();
		if (config.getClientId() == null || config.getClientSecret() == null || config.getRedirectUri() == null) {
			throw new IllegalArgumentException("Client ID, Client Secret e Redirect URI precisam estar configurados.");
		}

		String tokenUrl = "https://oauth2.googleapis.com/token";

		RestTemplate restTemplate;
		try {
			javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] {
					new javax.net.ssl.X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}

						public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
						}

						public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
						}
					} };

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
					throw new RuntimeException(
							"O Google não retornou um refresh_token. Tente revogar o acesso na sua conta Google e conectar novamente.");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao trocar o código de autorização pelo token: " + e.getMessage());
		}
	}
	 public boolean isConfiguradoEConectado() {
	        Long empresaId = securityUtils.empresaId();
	        
	        // 🟢 Busca usando o Optional de forma limpa
	        YoutubeConfig config = repository.findByEmpresaId(empresaId).orElse(null);
	        
	        return config != null 
	            && config.getClientId() != null && !config.getClientId().isBlank()
	            && config.getClientSecret() != null && !config.getClientSecret().isBlank()
	            && config.getRefreshToken() != null && !config.getRefreshToken().isBlank();
	    }


	public String gerarAuthUrl() {
		YoutubeConfig config = getConfig();
		if (config.getClientId() == null || config.getRedirectUri() == null) {
			throw new IllegalArgumentException("Configure o Client ID e a Redirect URI primeiro.");
		}

		return "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id=" + config.getClientId().trim()
				+ "&redirect_uri=" + config.getRedirectUri().trim() + "&response_type=code"
				+ "&scope=https://www.googleapis.com/auth/youtube" + "&access_type=offline" + "&prompt=consent";
	}

	public String criarPlaylistNoYoutube(String accessToken, String tituloPlaylist) {
		// 🟢 CORREÇÃO: Utiliza a API Key correta salva no banco de dados para chamadas autenticadas de API
		String apiKey = getConfig().getApiKey();
		if (apiKey == null || apiKey.isBlank()) {
			throw new RuntimeException("A Chave da API do YouTube (API Key) não está configurada.");
		}
		
		String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet,status&key=" + apiKey.trim();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String tituloFinal = (tituloPlaylist != null && !tituloPlaylist.isBlank()) ? tituloPlaylist
				: "Playlist Hope Escala";

		String requestBody = "{" + "\"snippet\": {" + "\"title\": \"" + tituloFinal + "\","
				+ "\"description\": \"Criado automaticamente pelo sistema Hope Escala\"" + "}," + "\"status\": {"
				+ "\"privacyStatus\": \"public\"" + "}" + "}";

		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				Map<String, Object> data = response.getBody();
				return (String) data.get("id");
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao criar playlist no YouTube: " + e.getMessage());
		}
		return null;
	}
	
	public String obterAccessToken() {
        YoutubeConfig config = getConfig();
        if (config.getRefreshToken() == null || config.getClientId() == null || config.getClientSecret() == null) {
            throw new RuntimeException("Configurações do YouTube (Client ID, Secret ou Refresh Token) ausentes.");
        }

        String tokenUrl = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", config.getClientId().trim());
        body.add("client_secret", config.getClientSecret().trim());
        body.add("refresh_token", config.getRefreshToken().trim());
        body.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                return (String) data.get("access_token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao renovar o Access Token do YouTube: " + e.getMessage());
        }
        return null;
    }
	 
	public void adicionarVideoNaPlaylist(String accessToken, String playlistId, String youtubeVideoId) {
		String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = "{" +
				"\"snippet\": {" +
					"\"playlistId\": \"" + playlistId + "\"," +
					"\"resourceId\": {" +
						"\"kind\": \"youtube#video\"," +
						"\"videoId\": \"" + youtubeVideoId + "\"" +
					"}" +
				"}" +
			"}";

		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

		try {
			restTemplate.postForEntity(url, entity, Map.class);
		} catch (Exception e) {
			System.err.println("Erro ao adicionar o vídeo " + youtubeVideoId + " na playlist: " + e.getMessage());
		}
	}
	 
	public List<Map<String, String>> pesquisarVideos(String query) {
	    if (query == null || query.isBlank()) {
	        return List.of();
	    }

	    String apiKey = getConfig().getApiKey();
	    if (apiKey == null || apiKey.isBlank()) {
	        throw new RuntimeException("Chave da API do YouTube não configurada.");
	    }

	    String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=45&q=" 
	                 + java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8) 
	                 + "&key=" + apiKey.trim();

	    RestTemplate restTemplate = new RestTemplate();
	    List<Map<String, String>> listaResultados = new java.util.ArrayList<>();

	    try {
	        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
	        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	            Map<String, Object> body = response.getBody();
	            java.util.List<Map<String, Object>> items = (java.util.List<Map<String, Object>>) body.get("items");

	            if (items != null) {
	                for (Map<String, Object> item : items) {
	                    Map<String, String> videoInfo = new java.util.HashMap<>();
	                    
	                    Map<String, Object> idMap = (Map<String, Object>) item.get("id");
	                    if (idMap == null) continue;
	                    
	                    String videoId = (String) idMap.get("videoId");
	                    if (videoId == null) continue;

	                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
	                    String title = snippet != null ? (String) snippet.get("title") : "";
	                    String channelTitle = snippet != null ? (String) snippet.get("channelTitle") : "";

	                    String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
	                    if (snippet != null && snippet.get("thumbnails") instanceof Map) {
	                        Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
	                        if (thumbnails.get("high") instanceof Map) {
	                            Map<String, Object> high = (Map<String, Object>) thumbnails.get("high");
	                            if (high.get("url") != null) {
	                                thumbnailUrl = (String) high.get("url");
	                            }
	                        }
	                    }

	                    videoInfo.put("id", videoId);
	                    videoInfo.put("titulo", title);
	                    videoInfo.put("canal", channelTitle);
	                    videoInfo.put("thumbnail", thumbnailUrl);

	                    listaResultados.add(videoInfo);
	                }
	            }
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Erro ao pesquisar vídeos no YouTube: " + e.getMessage());
	    }

	    return listaResultados;
	}
}
