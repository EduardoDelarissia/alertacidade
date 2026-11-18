package br.com.fiap.alertacidade.dto;

import br.com.fiap.alertacidade.model.NivelRisco;
import br.com.fiap.alertacidade.model.StatusAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Corpo de POST /api/alertas (alerta manual) e PUT /api/alertas/{id}.
 * nivel aceita ATENCAO ou CRITICO. status e usado no PUT para encerrar/reabrir.
 */
public record AlertaRequest(
        @NotNull(message = "pontoId e obrigatorio")
        Long pontoId,

        @NotNull(message = "nivel e obrigatorio (ATENCAO ou CRITICO)")
        NivelRisco nivel,

        @NotBlank(message = "mensagem e obrigatoria")
        @Size(max = 255, message = "mensagem deve ter ate 255 caracteres")
        String mensagem,

        /** Opcional; padrao ABERTO. */
        StatusAlerta status
) {
}
