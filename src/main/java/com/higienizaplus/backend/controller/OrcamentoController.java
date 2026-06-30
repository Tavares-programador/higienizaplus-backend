package com.higienizaplus.backend.controller;

import com.higienizaplus.backend.dto.OrcamentoRequest;
import com.higienizaplus.backend.dto.OrcamentoResponse;
import com.higienizaplus.backend.model.OrcamentoStatus;
import com.higienizaplus.backend.repository.ServicoPrecoRepository;
import com.higienizaplus.backend.service.OrcamentoService;
import com.higienizaplus.backend.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final PdfService pdfService;
    private final ServicoPrecoRepository servicoPrecoRepository;

    /** Endpoint PÚBLICO — salva o orçamento e devolve PDF para download */
    @PostMapping("/pdf")
    public ResponseEntity<byte[]> criarComPdf(@Valid @RequestBody OrcamentoRequest request) {
        // Salva na base de dados
        orcamentoService.criar(request);

        // Tenta encontrar o preço do serviço selecionado
        BigDecimal preco = servicoPrecoRepository.findAll()
                .stream()
                .filter(s -> s.getItem().equalsIgnoreCase(request.servico()))
                .findFirst()
                .map(s -> s.getPrecoKz())
                .orElse(null);

        // Gera o PDF
        byte[] pdf = pdfService.gerarPdfOrcamento(
                request.nome(),
                request.whatsapp(),
                request.email(),
                request.servico(),
                request.mensagem(),
                preco
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "orcamento-higienizaplus.pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    /** Endpoint PÚBLICO — submete orçamento sem PDF (compatibilidade com o formulário antigo) */
    @PostMapping
    public ResponseEntity<OrcamentoResponse> criar(@Valid @RequestBody OrcamentoRequest request) {
        OrcamentoResponse response = orcamentoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Endpoint PROTEGIDO (ROLE_ADMIN) — lista orçamentos com paginação */
    @GetMapping
    public ResponseEntity<Page<OrcamentoResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) OrcamentoStatus status) {
        return ResponseEntity.ok(orcamentoService.listar(pageable, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrcamentoResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam OrcamentoStatus status) {
        return ResponseEntity.ok(orcamentoService.atualizarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}