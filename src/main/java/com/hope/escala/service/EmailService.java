package com.hope.escala.service;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hope.escala.entity.YoutubeConfig; // Ou sua entidade onde fica o refreshToken do Google
import com.hope.escala.repository.YoutubeConfigRepository;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final YoutubeConfigRepository googleConfigRepository;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EmailService(YoutubeConfigRepository googleConfigRepository, ObjectMapper objectMapper) {
        this.googleConfigRepository = googleConfigRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Obtém um access_token temporário válido usando o refresh_token permanente via HTTPS
     */
    private String obterAccessToken(YoutubeConfig config) {
        try {
            String formBody = "client_id=" + URLEncoder.encode(config.getClientId(), StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(config.getClientSecret(), StandardCharsets.UTF_8)
                    + "&refresh_token=" + URLEncoder.encode(config.getRefreshToken(), StandardCharsets.UTF_8)
                    + "&grant_type=refresh_token";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Falha ao renovar token Google: " + response.body());
            }

            JsonNode jsonNode = objectMapper.readTree(response.body());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao autenticar na API do Google: " + e.getMessage(), e);
        }
    }

    /**
     * Converte a mensagem MIME para Base64 URL-Safe e dispara via Gmail REST API (Porta 443)
     */
    private void enviarEmailViaGmailApi(Long empresaId, String destinatario, String assunto, String htmlBody) {
        try {
            // Busca as credenciais OAuth da empresa
            YoutubeConfig config = googleConfigRepository.findByEmpresaId(empresaId)
                    .orElseThrow(() -> new RuntimeException("Google OAuth não configurado para esta instituição."));

            if (config.getRefreshToken() == null || config.getRefreshToken().isBlank()) {
                throw new RuntimeException("Refresh token do Google ausente. Vincule a conta do Google.");
            }

            String accessToken = obterAccessToken(config);

            // Cria mensagem MIME padrão
            Session session = Session.getDefaultInstance(new Properties(), null);
            MimeMessage mimeMessage = new MimeMessage(session);

            String remetenteNome = "Hope Escala Pro";
            mimeMessage.setFrom(new InternetAddress("me", remetenteNome));
            mimeMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(destinatario));
            mimeMessage.setSubject(assunto, "UTF-8");
            mimeMessage.setContent(htmlBody, "text/html; charset=UTF-8");

            // Serializa o MIME para bytes e codifica em Base64 URL-Safe sem padding
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mimeMessage.writeTo(outputStream);
            String rawMessage = Base64.getUrlEncoder().withoutPadding().encodeToString(outputStream.toByteArray());

            // Monta o payload JSON esperado pela Gmail API: {"raw": "..."}
            String payloadJson = objectMapper.writeValueAsString(new MensagemGmailDTO(rawMessage));

            // Dispara via POST HTTPS na porta 443 (sem bloqueios no Render)
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send"))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payloadJson))
                    .build();

            HttpResponse<String> apiResponse = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() >= 400) {
                throw new RuntimeException("Erro no envio pela Gmail API (Status " + apiResponse.statusCode() + "): " + apiResponse.body());
            }

            System.out.println("E-mail transacional enviado com sucesso via Gmail REST API para: " + destinatario);

        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail via Gmail API: " + e.getMessage());
            throw new RuntimeException("Falha no disparo do e-mail: " + e.getMessage(), e);
        }
    }

    public void enviarEmailSolicitacao(Long empresaId, String destinatario, String nomeUsuario, String nomeEmpresa) {
        String assunto = "Solicitação de Acesso Recebida - Hope Escala Pro";
        String htmlMensagem = """
            <div style="font-family: Arial, sans-serif; background-color: #090d16; padding: 30px; color: #f8fafc;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #111827; border-radius: 16px; border: 1px solid #1e293b; padding: 40px;">
                    <h2 style="color: #FF6B00; margin: 0; text-align: center;">Hope Escala Pro</h2>
                    <h3 style="color: #f1f5f9;">Olá, %s!</h3>
                    <p style="color: #cbd5e1;">Recebemos sua solicitação de acesso para a instituição <strong>%s</strong>.</p>
                    <p style="color: #cbd5e1;">Seu cadastro está aguardando a aprovação de um Administrador ou Líder.</p>
                </div>
            </div>
            """.formatted(nomeUsuario, nomeEmpresa);

        enviarEmailViaGmailApi(empresaId, destinatario, assunto, htmlMensagem);
    }

    public void enviarEmailAprovacao(Long empresaId, String destinatario, String nomeUsuario) {
        String assunto = "Acesso Liberado! - Hope Escala Pro";
        String corpoMensagem = "Boas notícias! Seu acesso foi aprovado por um administrador ou líder. Você já pode entrar no sistema e gerenciar suas escalas.";
        
        String htmlMensagem = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Hope Escala Pro</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #090d16; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #e2e8f0;">
                <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 600px; margin: 20px auto; background-color: #111827; border-radius: 16px; border: 1px solid #1f2937; overflow: hidden;">
                    <tr>
                        <td align="center" style="padding: 30px 20px; background-color: #090d16; border-bottom: 1px solid #1f2937;">
                            <div style="font-size: 20px; font-weight: bold; color: #ffffff; background: linear-gradient(135deg, #FF6B00, #ea580c); padding: 8px 20px; border-radius: 12px; display: inline-block; letter-spacing: 1px;">
                                HOPE ESCALA PRO
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 40px 30px;">
                            <h2 style="color: #ffffff; font-size: 22px; margin-top: 0; margin-bottom: 20px;">
                                Olá, %s! 🎉
                            </h2>
                            <p style="font-size: 16px; line-height: 1.6; color: #94a3b8; margin-bottom: 20px;">
                                %s
                            </p>
                            <div style="background-color: #090d16; border: 1px solid #1f2937; border-radius: 12px; padding: 20px; margin-top: 25px;">
                                <p style="font-size: 14px; color: #cbd5e1; margin: 0; text-align: center;">
                                    Status atual: <strong style="color: #10b981;">Aprovado e Ativo</strong>
                                </p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td align="center" style="padding: 20px; background-color: #090d16; color: #475569; font-size: 12px; border-top: 1px solid #1f2937;">
                            Gerenciado com carinho por <strong style="color: #FF6B00;">Hope Escala Pro</strong>.
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(nomeUsuario, corpoMensagem);

        enviarEmailViaGmailApi(empresaId, destinatario, assunto, htmlMensagem);
    }

    // DTO interno em Record Java 21 para o JSON da Gmail API
    private record MensagemGmailDTO(String raw) {}
}
