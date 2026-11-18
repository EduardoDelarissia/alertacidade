package br.com.fiap.alertacidade.dto;

import br.com.fiap.alertacidade.model.OrigemMedicao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Corpo de POST/PUT /api/medicoes.
 * No PUT o sensorId deve ser o mesmo da medicao original.
 */
public record MedicaoRequest(
        @NotNull(message = "sensorId e obrigatorio")
        Long sensorId,

        @NotNull(message = "valor e obrigatorio")
        @DecimalMin(value = "0", message = "valor nao pode ser negativo")
        @Digits(integer = 5, fraction = 2, message = "valor aceita ate 5 digitos inteiros e 2 decimais")
        BigDecimal valor,

        /** Opcional; padrao: data/hora do servidor. */
        LocalDateTime dataHora,

        /** Opcional; padrao SIMULADOR. */
        OrigemMedicao origem
) {
}
