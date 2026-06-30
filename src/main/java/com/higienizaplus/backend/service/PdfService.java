package com.higienizaplus.backend.service;

import java.awt.Color;
import com.higienizaplus.backend.model.ServicoPreco;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {

    private static final Color VERDE_MARCA  = new Color(0x1F, 0x7A, 0x4D);
    private static final Color VERDE_CLARO  = new Color(0xEA, 0xF7, 0xF0);
    private static final Color CINZA_TEXTO  = new Color(0x33, 0x33, 0x33);

    /* ------------------------------------------------------------------ */
    /*  PDF 1 — Lista geral de preços (já existia)                         */
    /* ------------------------------------------------------------------ */
    public byte[] gerarPdfListaDePrecos(List<ServicoPreco> servicos) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 40);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            adicionarCabecalho(document, "Lista de Orçamento");
            adicionarTabelaPrecos(document, servicos);
            adicionarRodape(document);

            document.close();
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF da lista de preços", e);
        }
    }

    /* ------------------------------------------------------------------ */
    /*  PDF 2 — Orçamento personalizado do cliente                         */
    /* ------------------------------------------------------------------ */
    public byte[] gerarPdfOrcamento(String nome, String whatsapp, String email,
                                    String servico, String mensagem, BigDecimal precoKz) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 40);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            adicionarCabecalho(document, "Pedido de Orçamento");

            Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD,  VERDE_MARCA);
            Font valorFont = new Font(Font.HELVETICA, 11, Font.NORMAL, CINZA_TEXTO);
            Font secFont   = new Font(Font.HELVETICA, 13, Font.BOLD,  VERDE_MARCA);

            // --- Secção: Dados do Cliente ---
            adicionarSeccao(document, "Dados do Cliente", secFont);

            adicionarLinha(document, "Nome",     nome,     labelFont, valorFont);
            adicionarLinha(document, "WhatsApp", whatsapp, labelFont, valorFont);
            if (email != null && !email.isBlank()) {
                adicionarLinha(document, "Email", email, labelFont, valorFont);
            }

            // --- Secção: Serviço Solicitado ---
            adicionarSeccao(document, "Serviço Solicitado", secFont);
            adicionarLinha(document, "Serviço", servico, labelFont, valorFont);

            if (precoKz != null) {
                NumberFormat kz = NumberFormat.getInstance(new Locale("pt", "AO"));
                kz.setMaximumFractionDigits(0);
                Font precoFont = new Font(Font.HELVETICA, 13, Font.BOLD, VERDE_MARCA);
                Paragraph pPreco = new Paragraph("Preço estimado: " + kz.format(precoKz) + " Kz", precoFont);
                pPreco.setSpacingBefore(6);
                document.add(pPreco);
            } else {
                Font semPrecoFont = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);
                document.add(new Paragraph("Preço a confirmar no local.", semPrecoFont));
            }

            // --- Secção: Observações ---
            if (mensagem != null && !mensagem.isBlank()) {
                adicionarSeccao(document, "Observações do Cliente", secFont);
                Paragraph obs = new Paragraph(mensagem, valorFont);
                obs.setSpacingBefore(4);
                document.add(obs);
            }

            // --- Nota de validade ---
            Font notaFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
            Paragraph nota = new Paragraph(
                    "\nEste orçamento é uma estimativa. Os valores finais ficam sujeitos a " +
                            "confirmação no local, conforme o estado e a dimensão real da peça. " +
                            "A nossa equipa entrará em contacto brevemente.", notaFont);
            nota.setSpacingBefore(24);
            document.add(nota);

            adicionarRodape(document);

            document.close();
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF do orçamento", e);
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Helpers partilhados                                                 */
    /* ------------------------------------------------------------------ */
    private void adicionarCabecalho(Document document, String subtitulo) throws DocumentException {
        Font tituloFont   = new Font(Font.HELVETICA, 22, Font.BOLD,   VERDE_MARCA);
        Font subFont      = new Font(Font.HELVETICA, 12, Font.NORMAL, CINZA_TEXTO);
        Font dataFont     = new Font(Font.HELVETICA,  9, Font.ITALIC, Color.GRAY);

        Paragraph titulo = new Paragraph("HIGIENIZA+", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph sub = new Paragraph(subtitulo, subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(4);
        document.add(sub);

        Paragraph data = new Paragraph(
                "Gerado em " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataFont);
        data.setAlignment(Element.ALIGN_CENTER);
        data.setSpacingAfter(20);
        document.add(data);

        // Linha separadora verde
        PdfPTable linha = new PdfPTable(1);
        linha.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(VERDE_MARCA);
        cell.setFixedHeight(3f);
        cell.setBorder(Rectangle.NO_BORDER);
        linha.addCell(cell);
        linha.setSpacingAfter(16);
        document.add(linha);
    }

    private void adicionarSeccao(Document document, String titulo, Font font) throws DocumentException {
        Paragraph p = new Paragraph(titulo, font);
        p.setSpacingBefore(16);
        p.setSpacingAfter(6);
        document.add(p);
    }

    private void adicionarLinha(Document document, String label, String valor,
                                Font labelFont, Font valorFont) throws DocumentException {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{1.5f, 4f});

        PdfPCell cLabel = new PdfPCell(new Phrase(label + ":", labelFont));
        cLabel.setBorder(Rectangle.NO_BORDER);
        cLabel.setPadding(4);
        cLabel.setBackgroundColor(VERDE_CLARO);

        PdfPCell cValor = new PdfPCell(new Phrase(valor != null ? valor : "—", valorFont));
        cValor.setBorder(Rectangle.NO_BORDER);
        cValor.setPadding(4);
        cValor.setBackgroundColor(VERDE_CLARO);

        t.addCell(cLabel);
        t.addCell(cValor);
        t.setSpacingAfter(3);
        document.add(t);
    }

    private void adicionarTabelaPrecos(Document document, List<ServicoPreco> servicos) throws DocumentException {
        NumberFormat kz = NumberFormat.getInstance(new Locale("pt", "AO"));
        kz.setMaximumFractionDigits(0);

        Map<String, List<ServicoPreco>> porCategoria = new LinkedHashMap<>();
        for (ServicoPreco s : servicos) {
            porCategoria.computeIfAbsent(s.getCategoria(), k -> new java.util.ArrayList<>()).add(s);
        }

        Font categoriaFont = new Font(Font.HELVETICA, 13, Font.BOLD,   VERDE_MARCA);
        Font itemFont      = new Font(Font.HELVETICA, 11, Font.NORMAL, CINZA_TEXTO);
        Font precoFont     = new Font(Font.HELVETICA, 11, Font.BOLD,   CINZA_TEXTO);

        for (Map.Entry<String, List<ServicoPreco>> entry : porCategoria.entrySet()) {
            Paragraph categoria = new Paragraph(entry.getKey(), categoriaFont);
            categoria.setSpacingBefore(14);
            categoria.setSpacingAfter(6);
            document.add(categoria);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1.2f});

            boolean linhaPar = false;
            for (ServicoPreco s : entry.getValue()) {
                PdfPCell itemCell = new PdfPCell(new Phrase(s.getItem(), itemFont));
                itemCell.setBorder(Rectangle.NO_BORDER);
                itemCell.setPadding(6);
                itemCell.setBackgroundColor(linhaPar ? VERDE_CLARO : Color.WHITE);

                PdfPCell precoCell = new PdfPCell(
                        new Phrase(kz.format(s.getPrecoKz()) + " Kz", precoFont));
                precoCell.setBorder(Rectangle.NO_BORDER);
                precoCell.setPadding(6);
                precoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                precoCell.setBackgroundColor(linhaPar ? VERDE_CLARO : Color.WHITE);

                table.addCell(itemCell);
                table.addCell(precoCell);
                linhaPar = !linhaPar;
            }
            document.add(table);
        }
    }

    private void adicionarRodape(Document document) throws DocumentException {
        Font notaFont    = new Font(Font.HELVETICA,  9, Font.ITALIC, Color.GRAY);
        Font contatoFont = new Font(Font.HELVETICA, 10, Font.BOLD,   VERDE_MARCA);

        Paragraph nota = new Paragraph(
                "\nValores sujeitos a confirmação no local, conforme estado e dimensão real da peça. " +
                        "Atendemos Luanda, Icolo e Bengo.", notaFont);
        nota.setSpacingBefore(20);
        document.add(nota);

        document.add(new Paragraph("WhatsApp: +244 949 943 236", contatoFont));
    }
}