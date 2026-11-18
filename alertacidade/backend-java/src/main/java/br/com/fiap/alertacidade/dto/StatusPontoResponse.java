package br.com.fiap.alertacidade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Situacao atual de um ponto, para o dashboard.
 * estado: NORMAL, ATENCAO, CRITICO ou SEM_DADOS (nenhuma leitura de nivel registrada).
 */
public record StatusPontoResponse(
        Long pontoId,
        String nome,
        String bairro,
        boolean ativo,
        String estado,
        BigDecimal ultimoNivelCm,
        LocalDateTime dataUltimoNivel,
        BigDecimal ultimaChuvaMm,
        LocalDateTime dataUltimaChuva,
        BigDecimal cotaAtencaoCm,
        BigDecimal cotaCriticaCm,
        int alertasAbertos
) {
}
