package com.higienizaplus.backend.dto;

import com.higienizaplus.backend.model.Orcamento;
import com.higienizaplus.backend.model.OrcamentoStatus;

import java.time.LocalDateTime;

public record OrcamentoResponse(
        Long id,
        String nome,
        String email,
        String whatsapp,
        String servico,
        String mensagem,
        OrcamentoStatus status,
        LocalDateTime criadoEm,
        String linkWhatsapp
) {
    public static OrcamentoResponse from(Orcamento o, String numeroEmpresa) {
        String texto =
                "Olá! Gostaria de solicitar um orçamento.%0A%0A" +
                "*Nome:* " + o.getNome() + "%0A" +
                "*E-mail:* " + o.getEmail() + "%0A" +
                "*WhatsApp:* " + o.getWhatsapp() + "%0A" +
                "*Serviço:* " + o.getServico() + "%0A" +
                "*Mensagem:* " + (o.getMensagem() == null ? "" : o.getMensagem());

        String link = "https://wa.me/" + numeroEmpresa + "?text=" + texto;

        return new OrcamentoResponse(
                o.getId(),
                o.getNome(),
                o.getEmail(),
                o.getWhatsapp(),
                o.getServico(),
                o.getMensagem(),
                o.getStatus(),
                o.getCriadoEm(),
                link
        );
    }
}
