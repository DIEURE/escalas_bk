package com.hope.escala.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmailService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${resend.api.key:}")
    private String resendApiKey;

    @Value("${resend.email.from:Hope Escala Pro <nao-responda@hopeescalapro.com.br>}")
    private String emailFrom;

    public EmailService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Disparo HTTP REST direto para o Resend (Porta 443 HTTPS)
     */
    private boolean enviarViaResend(String destinatario, String assunto, String htmlBody) {
     
            System.out.println(">>> [Resend] Iniciando disparo para: " + destinatario);
            System.out.println(">>> [Resend] Key configurada? " + (resendApiKey != null && !resendApiKey.isBlank()));
            System.out.println(">>> [Resend] Remetente: " + emailFrom);

            if (resendApiKey == null || resendApiKey.isBlank()) {
                System.err.println(">>> [Resend] AVISO: RESEND_API_KEY não configurada no servidor. E-mail não enviado.");
                return false;
            }
            // ... restante do método


        try {
            Map<String, Object> payload = Map.of(
                    "from", emailFrom,
                    "to", List.of(destinatario),
                    "subject", assunto,
                    "html", htmlBody
            );

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println(">>> [Resend] E-mail enviado com sucesso para: " + destinatario);
                return true;
            } else {
                System.err.println(">>> [Resend] Erro da API (Status " + response.statusCode() + "): " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println(">>> [Resend] Falha de conexão ao enviar e-mail: " + e.getMessage());
            return false;
        }
    }

    public boolean enviarEmailRecuperacaoSenha(Long empresaId, String destinatario, String nomeUsuario, String codigo) {
        String assunto = "Código de Recuperação de Senha - Hope Escala Pro";
        String htmlMensagem = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Recuperação de Senha</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #121418; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #f8fafc;">
                <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 600px; margin: 20px auto; background-color: #1a1d24; border-radius: 16px; border: 1px solid #262b35; overflow: hidden;">
                    <tr>
                        <td align="center" style="padding: 28px 20px; background-color: #121418; border-bottom: 1px solid #262b35;">
                            <div style="font-size: 20px; font-weight: bold; color: #ffffff; background: linear-gradient(135deg, #FF6B00, #ea580c); padding: 8px 22px; border-radius: 10px; display: inline-block; letter-spacing: 1px;">
                                HOPE ESCALA PRO
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 36px 30px;">
                            <h2 style="color: #ffffff; font-size: 22px; margin-top: 0; margin-bottom: 16px;">
                                Olá, %s! 👋
                            </h2>
                            <p style="font-size: 15px; line-height: 1.6; color: #94a3b8; margin-bottom: 24px;">
                                Recebemos uma solicitação para redefinir a senha da sua conta. Use o código de 6 dígitos abaixo para confirmar a alteração:
                            </p>
                            <div style="background-color: #121418; border: 1px solid #FF6B00; border-radius: 12px; padding: 22px; margin-bottom: 24px; text-align: center;">
                                <span style="font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #FF6B00; font-family: monospace;">
                                    %s
                                </span>
                            </div>
                            <p style="font-size: 13px; line-height: 1.5; color: #64748b; margin: 0;">
                                • O código expira em <strong>15 minutos</strong>.<br>
                                • Se não foi você quem fez este pedido, ignore este e-mail por segurança.
                            </p>
                        </td>
                    </tr>
                    <tr>
                        <td align="center" style="padding: 18px; background-color: #121418; color: #475569; font-size: 12px; border-top: 1px solid #262b35;">
                            Hope Escala Pro • Segurança de Acesso
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(nomeUsuario != null ? nomeUsuario : "Usuário", codigo);

        return enviarViaResend(destinatario, assunto, htmlMensagem);
    }

    public boolean enviarEmailLiberacaoMusico(Long empresaId, String destinatario, String nomeUsuario) {
        String assunto = "Escala Liberada! - Hope Escala Pro";
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
                                Olá, %s! 🎸
                            </h2>
                            <p style="font-size: 16px; line-height: 1.6; color: #94a3b8; margin-bottom: 20px;">
                                Sua participação nas escalas foi restabelecida pela liderança. Você já está disponível novamente para ser convocado nas próximas escalas.
                            </p>
                            <div style="background-color: #090d16; border: 1px solid #1f2937; border-radius: 12px; padding: 20px; margin-top: 25px;">
                                <p style="font-size: 14px; color: #cbd5e1; margin: 0; text-align: center;">
                                    Status do Voluntário: <strong style="color: #10b981;">Disponível / Liberado</strong>
                                </p>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td align="center" style="padding: 20px; background-color: #090d16; color: #475569; font-size: 12px; border-top: 1px solid #1f2937;">
                            Hope Escala Pro • Gestão de Ministérios e Escalas
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(nomeUsuario);

        return enviarViaResend(destinatario, assunto, htmlMensagem);
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

        enviarViaResend(destinatario, assunto, htmlMensagem);
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

        enviarViaResend(destinatario, assunto, htmlMensagem);
    }
}