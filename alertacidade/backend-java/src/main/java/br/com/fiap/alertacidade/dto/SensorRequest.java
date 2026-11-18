package br.com.fiap.alertacidade.dto;

import br.com.fiap.alertacidade.model.StatusSensor;
import br.com.fiap.alertacidade.model.TipoSensor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Corpo de POST/PUT /api/sensores. A unidade (cm/mm) e definida pelo tipo. */
public record SensorRequest(
        @NotNull(message = "pontoId e obrigatorio")
        Long pontoId,

        @NotBlank(message = "codigo e obrigatorio")
        @Size(max = 30, message = "codigo deve ter ate 30 caracteres")
        String codigo,

        @NotNull(message = "tipo e obrigatorio (NIVEL_AGUA ou PLUVIOMETRO)")
        TipoSensor tipo,

        /** Opcional; padrao ATIVO. */
        StatusSensor status,

        /** Opcional; padrao data atual. */
        @PastOrPresent(message = "dataInstalacao nao pode estar no futuro")
        LocalDate dataInstalacao
) {
}
