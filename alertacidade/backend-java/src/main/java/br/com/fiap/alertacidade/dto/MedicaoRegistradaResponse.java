package br.com.fiap.alertacidade.dto;

import br.com.fiap.alertacidade.model.Alerta;
import br.com.fiap.alertacidade.model.Medicao;
import br.com.fiap.alertacidade.model.NivelRisco;

/**
 * Resposta de POST /api/medicoes.
 *
 * @param medicao            leitura gravada
 * @param classificacao      NORMAL/ATENCAO/CRITICO para sensores de nivel; null para pluviometro
 * @param alertaGerado       alerta criado por esta leitura, ou null
 * @param alertasEncerrados  quantidade de alertas automaticos encerrados (nivel voltou ao normal
 *                           ou foram substituidos por um alerta mais grave)
 */
public record MedicaoRegistradaResponse(
        Medicao medicao,
        NivelRisco classificacao,
        Alerta alertaGerado,
        int alertasEncerrados
) {
}
