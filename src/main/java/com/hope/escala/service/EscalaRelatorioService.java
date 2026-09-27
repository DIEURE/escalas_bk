package com.hope.escala.service;

 

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hope.escala.entity.AgendaMensal;
import com.hope.escala.entity.Escala;
import com.hope.escala.entity.EscalaMusico;
import com.hope.escala.exception.ResourceNotFoundException;
import com.hope.escala.repository.AgendaMensalRepository;
import com.hope.escala.repository.EscalaMusicoRepository;
import com.hope.escala.repository.EscalaRepository;
import com.hope.escala.security.SecurityUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class EscalaRelatorioService {

    private final EscalaRepository escalaRepository;
    private final AgendaMensalRepository agendaMensalRepository;
    private final EscalaMusicoRepository escalaMusicoRepository;
    private final SecurityUtils securityUtils;

    // Cores padrão Hope Escala Pro
    private static final BaseColor COR_LARANJA_HOPE = new BaseColor(255, 107, 0);
    private static final BaseColor COR_FUNDO_CABECALHO = new BaseColor(245, 247, 250);
    private static final BaseColor COR_TEXTO_ESCURO = new BaseColor(30, 41, 59);

    public EscalaRelatorioService(EscalaRepository escalaRepository,
                                  AgendaMensalRepository agendaMensalRepository,
                                  EscalaMusicoRepository escalaMusicoRepository,
                                  SecurityUtils securityUtils) {
        this.escalaRepository = escalaRepository;
        this.agendaMensalRepository = agendaMensalRepository;
        this.escalaMusicoRepository = escalaMusicoRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public byte[] gerarRelatorioMensalPdf(Long agendaMensalId, Long departamentoId) {
        Long empresaIdLogada = securityUtils.empresaId();

        AgendaMensal agenda = agendaMensalRepository.findById(agendaMensalId)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda mensal não encontrada: " + agendaMensalId));

        if (!agenda.getEmpresa().getId().equals(empresaIdLogada)) {
            throw new ResourceNotFoundException("Agenda não pertence à sua instituição");
        }

        List<Escala> escalas = escalaRepository.findByAgendaMensalIdAndEmpresaIdAndAtivaTrue(agendaMensalId, empresaIdLogada);

        if (departamentoId != null) {
            escalas = escalas.stream()
                    .filter(e -> e.getDepartamento() != null && e.getDepartamento().getId().equals(departamentoId))
                    .toList();
        }

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fontes
            Font fonteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, COR_LARANJA_HOPE);
            Font fonteSubtitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COR_TEXTO_ESCURO);
            Font fonteDataEscala = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            Font fonteCabecalhoTabela = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COR_TEXTO_ESCURO);
            Font fonteCelula = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COR_TEXTO_ESCURO);

            // Cabeçalho do Documento
            Paragraph titulo = new Paragraph("HOPE ESCALA PRO", fonteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            String subtituloTexto = "Cronograma Mensal - " + agenda.getDescricao();
            Paragraph subtitulo = new Paragraph(subtituloTexto, fonteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(18f);
            document.add(subtitulo);

            DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            if (escalas.isEmpty()) {
                Paragraph semEscalas = new Paragraph("Nenhuma escala encontrada para este período.", fonteCelula);
                semEscalas.setAlignment(Element.ALIGN_CENTER);
                document.add(semEscalas);
            } else {
                for (Escala escala : escalas) {
                    // Bloco do Dia do Culto
                    PdfPTable tabelaEscala = new PdfPTable(2);
                    tabelaEscala.setWidthPercentage(100);
                    tabelaEscala.setWidths(new float[] { 3f, 7f });
                    tabelaEscala.setSpacingBefore(10f);
                    tabelaEscala.setSpacingAfter(10f);

                    // Cabeçalho da Escala (Data + Cultos)
                    String dataTexto = escala.getDataEscala() != null ? escala.getDataEscala().format(formatoData) : "—";
                    String infoCultos = "";
                    if (escala.getNomeCultoManha() != null && !escala.getNomeCultoManha().isBlank()) {
                        infoCultos += "Manhã: " + escala.getNomeCultoManha() + " ";
                    }
                    if (escala.getNomeCultoNoite() != null && !escala.getNomeCultoNoite().isBlank()) {
                        infoCultos += (infoCultos.isEmpty() ? "" : "| ") + "Noite: " + escala.getNomeCultoNoite();
                    }

                    PdfPCell cellHeader = new PdfPCell(new Phrase("DATA: " + dataTexto + "  -  " + infoCultos, fonteDataEscala));
                    cellHeader.setColspan(2);
                    cellHeader.setBackgroundColor(COR_LARANJA_HOPE);
                    cellHeader.setPadding(6f);
                    tabelaEscala.addCell(cellHeader);

                    // Cabeçalho das Colunas de Músicos
                    PdfPCell colInstrumento = new PdfPCell(new Phrase("Instrumento / Função", fonteCabecalhoTabela));
                    colInstrumento.setBackgroundColor(COR_FUNDO_CABECALHO);
                    colInstrumento.setPadding(5f);
                    tabelaEscala.addCell(colInstrumento);

                    PdfPCell colMusico = new PdfPCell(new Phrase("Voluntário / Músico", fonteCabecalhoTabela));
                    colMusico.setBackgroundColor(COR_FUNDO_CABECALHO);
                    colMusico.setPadding(5f);
                    tabelaEscala.addCell(colMusico);

                    // Lista de Músicos da Escala
                    List<EscalaMusico> musicos = escalaMusicoRepository.findByEscalaId(escala.getId());
                    if (musicos.isEmpty()) {
                        PdfPCell semMusico = new PdfPCell(new Phrase("Nenhum voluntário escalado ainda.", fonteCelula));
                        semMusico.setColspan(2);
                        semMusico.setPadding(5f);
                        tabelaEscala.addCell(semMusico);
                    } else {
                        for (EscalaMusico em : musicos) {
                            String instrumento = em.getInstrumento() != null ? em.getInstrumento() : "Geral";
                            String nomeMusico = em.getUsuario() != null ? em.getUsuario().getNome() : "Não informado";
                            
                            if (Boolean.TRUE.equals(em.getConfirmado())) {
                                nomeMusico += " (Confirmado)";
                            }

                            PdfPCell cInst = new PdfPCell(new Phrase(instrumento, fonteCelula));
                            cInst.setPadding(4f);
                            tabelaEscala.addCell(cInst);

                            PdfPCell cNome = new PdfPCell(new Phrase(nomeMusico, fonteCelula));
                            cNome.setPadding(4f);
                            tabelaEscala.addCell(cNome);
                        }
                    }

                    document.add(tabelaEscala);
                }
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório mensal em PDF com iText: " + e.getMessage(), e);
        }
    }
}
