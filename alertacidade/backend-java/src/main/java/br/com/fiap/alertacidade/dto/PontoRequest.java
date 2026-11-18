package br.com.fiap.alertacidade.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Corpo de POST/PUT /api/pontos. */
public record PontoRequest(
        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 100, message = "nome deve ter ate 100 caracteres")
        String nome,

        @NotBlank(message = "bairro e obrigatorio")
        @Size(max = 80, message = "bairro deve ter ate 80 caracteres")
        String bairro,

        @NotBlank(message = "cidade e obrigatoria")
        @Size(max = 80, message = "cidade deve ter ate 80 caracteres")
        String cidade,

        @NotNull(message = "latitude e obrigatoria")
        @DecimalMin(value = "-90", message = "latitude minima e -90")
        @DecimalMax(value = "90", message = "latitude maxima e 90")
        @Digits(integer = 3, fraction = 6, message = "latitude aceita ate 6 casas decimais")
        BigDecimal latitude,

        @NotNull(message = "longitude e obrigatoria")
        @DecimalMin(value = "-180", message = "longitude minima e -180")
        @DecimalMax(value = "180", message = "longitude maxima e 180")
        @Digits(integer = 3, fraction = 6, message = "longitude aceita ate 6 casas decimais")
        BigDecimal longitude,

        @NotNull(message = "cotaAtencaoCm e obrigatoria")
        @Positive(message = "cotaAtencaoCm deve ser positiva")
        @Digits(integer = 5, fraction = 1, message = "cotaAtencaoCm aceita ate 1 casa decimal")
        BigDecimal cotaAtencaoCm,

        @NotNull(message = "cotaCriticaCm e obrigatoria")
        @Positive(message = "cotaCriticaCm deve ser positiva")
        @Digits(integer = 5, fraction = 1, message = "cotaCriticaCm aceita ate 1 casa decimal")
        BigDecimal cotaCriticaCm,

        /** Opcional; se omitido o ponto e criado/mantido ativo. */
        Boolean ativo
) {
}
