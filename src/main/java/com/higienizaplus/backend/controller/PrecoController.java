package com.higienizaplus.backend.controller;

import com.higienizaplus.backend.model.ServicoPreco;
import com.higienizaplus.backend.service.PdfService;
import com.higienizaplus.backend.service.ServicoPrecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrecoController {

    private final ServicoPrecoService servicoPrecoService;
    private final PdfService pdfService;

    /**
     * Lista de preços em JSON (caso o frontend queira renderizar a tabela
     * diretamente na página, sem precisar abrir o PDF).
     */
    @GetMapping("/api/precos")
    public ResponseEntity<List<ServicoPreco>> listarPrecos() {
        return ResponseEntity.ok(servicoPrecoService.listarTodos());
    }

    /**
     * Gera e devolve o PDF "Lista de Orçamento" com todos os serviços e preços,
     * para download direto pelo botão do site.
     */
    @GetMapping("/api/orcamentos/pdf")
    public ResponseEntity<byte[]> baixarPdfDePrecos() {
        List<ServicoPreco> servicos = servicoPrecoService.listarTodos();
        byte[] pdf = pdfService.gerarPdfListaDePrecos(servicos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "higienizaplus-lista-de-precos.pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
