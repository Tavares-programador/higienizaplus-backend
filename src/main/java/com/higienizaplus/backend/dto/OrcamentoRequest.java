package com.higienizaplus.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrcamentoRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150)
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "WhatsApp é obrigatório")
        @Size(max = 30)
        String whatsapp,

        @NotBlank(message = "Serviço é obrigatório")
        @Size(max = 100)
        String servico,

        @Size(max = 4000)
        String mensagem
) {
}
