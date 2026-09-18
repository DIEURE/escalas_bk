package com.hope.escala.service;

import java.util.Properties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.hope.escala.entity.EmailConfig;
import com.hope.escala.repository.EmailConfigRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final EmailConfigRepository emailConfigRepository;

    public EmailService(EmailConfigRepository emailConfigRepository) {
        this.emailConfigRepository = emailConfigRepository;
    }

    private JavaMailSender criarMailSenderParaEmpresa(Long empresaId) {
        EmailConfig config = emailConfigRepository.findByEmpresa_Id(empresaId)
                .orElseThrow(() -> new RuntimeException("Servidor SMTP não configurado para esta instituição."));

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPorta());
        mailSender.setUsername(config.getUsuario());
        mailSender.setPassword(config.getSenha());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(config.getUsarTls()));
        props.put("mail.debug", "false");

        return mailSender;
    }

    public void enviarEmailSolicitacao(Long empresaId, String destinatario, String nomeUsuario, String nomeEmpresa) {
        String assunto = "Solicitação de Acesso Recebida - Hope Escala Pro";
        String htmlMensagem = """
            <div style="font-family: Arial, sans-serif; background-color: #090d16; padding: 30px; color: #f8fafc;">
                <div style="max-width: 600px; margin: 0 auto; background-color: #111827; border-radius: 16px; border: 1px solid #1e293b; padding: 40px;">
                    <h2 style="color: #f97316; margin: 0; text-align: center;">Hope Escala Pro</h2>
                    <h3 style="color: #f1f5f9;">Olá, %s!</h3>
                    <p style="color: #cbd5e1;">Recebemos sua solicitação de acesso para a instituição <strong>%s</strong>.</p>
                    <p style="color: #cbd5e1;">Seu cadastro está aguardando a aprovação de um Administrador ou Líder.</p>
                </div>
            </div>
            """.formatted(nomeUsuario, nomeEmpresa);

        enviarHtmlDinamico(empresaId, destinatario, assunto, htmlMensagem);
    }

    public void enviarEmailAprovacao(Long empresaId, String destinatario, String nomeUsuario) {
        String assunto = "Acesso Liberado! - Hope Escala Pro";
        
        // Você pode buscar o nome da empresa pelo ID se precisar para o rodapé ou texto
        // Empresa empresa = empresaRepository.findById(empresaId).orElse(null);
        // String nomeEmpresa = empresa != null ? empresa.getNome() : "Hope Escala Pro";
        
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
                    
                    <!-- CABEÇALHO -->
                    <tr>
                        <td align="center" style="padding: 30px 20px; background-color: #090d16; border-bottom: 1px solid #1f2937;">
                            <div style="font-size: 20px; font-weight: bold; color: #ffffff; background: linear-gradient(135deg, #f97316, #ea580c); padding: 8px 20px; border-radius: 12px; display: inline-block; letter-spacing: 1px;">
                                HOPE ESCALA PRO
                            </div>
                        </td>
                    </tr>

                    <!-- CORPO DA MENSAGEM -->
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

                    <!-- RODAPÉ -->
                    <tr>
                        <td align="center" style="padding: 20px; background-color: #090d16; color: #475569; font-size: 12px; border-top: 1px solid #1f2937;">
                            Gerenciado com carinho por <strong style="color: #f97316;">Hope Escala Pro</strong>.
                        </td>
                    </tr>

                </table>
            </body>
            </html>
        """.formatted(nomeUsuario, corpoMensagem);

        enviarHtmlDinamico(empresaId, destinatario, assunto, htmlMensagem);
    }


     

    private void enviarHtmlDinamico(Long empresaId, String para, String assunto, String htmlBody) {
        try {
            JavaMailSender mailSender = criarMailSenderParaEmpresa(empresaId);
            EmailConfig config = emailConfigRepository.findByEmpresa_Id(empresaId).orElseThrow();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(config.getUsuario(), config.getRemetenteNome() != null ? config.getRemetenteNome() : "Hope Escala Pro");
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(htmlBody, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail multi-tenant: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail. Verifique as configurações de SMTP da empresa.");
        }
    }
}
