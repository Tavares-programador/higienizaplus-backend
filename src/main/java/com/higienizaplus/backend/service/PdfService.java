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

    private static final Color VERDE_MARCA = new Color(0x1F, 0x7A, 0x4D);
    private static final Color VERDE_CLARO = new Color(0xEA, 0xF7, 0xF0);
    private static final Color CINZA_TEXTO = new Color(0x33, 0x33, 0x33);

    public byte[] gerarPdfListaDePrecos(List<ServicoPreco> servicos) {
        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 40);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            adicionarCabecalho(document);
            adicionarTabelaPrecos(document, servicos);
            adicionarRodape(document);

            document.close();
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF da lista de preços", e);
        }
    }

    private void adicionarCabecalho(Document document) throws DocumentException {
        Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD, VERDE_MARCA);
        Font subtituloFont = new Font(Font.HELVETICA, 12, Font.NORMAL, CINZA_TEXTO);

        Paragraph titulo = new Paragraph("HIGIENIZA+", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Lista de Orçamento", subtituloFont);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(4);
        document.add(subtitulo);

        Font dataFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
        Paragraph data = new Paragraph(
                "Gerado em " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                dataFont
        );
        data.setAlignment(Element.ALIGN_CENTER);
        data.setSpacingAfter(18);
        document.add(data);
    }

    private void adicionarTabelaPrecos(Document document, List<ServicoPreco> servicos) throws DocumentException {
        NumberFormat kz = NumberFormat.getInstance(new Locale("pt", "AO"));
        kz.setMaximumFractionDigits(0);

        // Agrupa por categoria preservando a ordem de chegada
        Map<String, java.util.List<ServicoPreco>> porCategoria = new LinkedHashMap<>();
        for (ServicoPreco s : servicos) {
            porCategoria.computeIfAbsent(s.getCategoria(), k -> new java.util.ArrayList<>()).add(s);
        }

        Font categoriaFont = new Font(Font.HELVETICA, 13, Font.BOLD, VERDE_MARCA);
        Font itemFont = new Font(Font.HELVETICA, 11, Font.NORMAL, CINZA_TEXTO);
        Font precoFont = new Font(Font.HELVETICA, 11, Font.BOLD, CINZA_TEXTO);

        for (Map.Entry<String, java.util.List<ServicoPreco>> entry : porCategoria.entrySet()) {
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

                String precoTexto = kz.format(s.getPrecoKz()) + " Kz";
                PdfPCell precoCell = new PdfPCell(new Phrase(precoTexto, precoFont));
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
        Font notaFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
        Paragraph nota = new Paragraph(
                "\nValores sujeitos a confirmação no local, conforme estado e dimensão real da peça. " +
                        "Atendemos Luanda, Icolo e Bengo.",
                notaFont
        );
        nota.setSpacingBefore(20);
        document.add(nota);

        Font contatoFont = new Font(Font.HELVETICA, 10, Font.BOLD, VERDE_MARCA);
        Paragraph contato = new Paragraph("\nWhatsApp: +244 949 943 236", contatoFont);
        document.add(contato);
    }
}
