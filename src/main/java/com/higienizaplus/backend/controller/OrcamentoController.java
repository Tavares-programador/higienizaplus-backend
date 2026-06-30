package com.higienizaplus.backend.controller;

import com.higienizaplus.backend.dto.OrcamentoRequest;
import com.higienizaplus.backend.dto.OrcamentoResponse;
import com.higienizaplus.backend.model.OrcamentoStatus;
import com.higienizaplus.backend.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    /**
     * Endpoint PÚBLICO — usado pelo formulário do site (contact.html).
     * Salva o orçamento no banco e devolve também o link pronto do WhatsApp,
     * para o frontend continuar abrindo o WhatsApp da empresa como já fazia.
     */
    @PostMapping
    public ResponseEntity<OrcamentoResponse> criar(@Valid @RequestBody OrcamentoRequest request) {
        OrcamentoResponse response = orcamentoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint PROTEGIDO (ROLE_ADMIN) — painel admin lista os pedidos, com paginação
     * e filtro opcional por status.
     */
    @GetMapping
    public ResponseEntity<Page<OrcamentoResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) OrcamentoStatus status
    ) {
        return ResponseEntity.ok(orcamentoService.listar(pageable, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrcamentoResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam OrcamentoStatus status
    ) {
        return ResponseEntity.ok(orcamentoService.atualizarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
