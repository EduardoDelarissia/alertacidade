package br.com.fiap.alertacidade.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PontoMonitoramentoTest {

    private PontoMonitoramento ponto(String atencao, String critica) {
        PontoMonitoramento p = new PontoMonitoramento();
        p.setCotaAtencaoCm(new BigDecimal(atencao));
        p.setCotaCriticaCm(new BigDecimal(critica));
        return p;
    }

    @Test
    void classificaPelosLimitesInclusivos() {
        PontoMonitoramento p = ponto("80", "120");
        assertEquals(NivelRisco.NORMAL, p.classificar(new BigDecimal("79.99")));
        assertEquals(NivelRisco.ATENCAO, p.classificar(new BigDecimal("80")));
        assertEquals(NivelRisco.ATENCAO, p.classificar(new BigDecimal("119.9")));
        assertEquals(NivelRisco.CRITICO, p.classificar(new BigDecimal("120.0")));
    }

    @Test
    void validaCotas() {
        assertTrue(ponto("50", "90").cotasValidas());
        assertFalse(ponto("90", "50").cotasValidas());
        assertFalse(ponto("50", "50").cotasValidas());
    }
}
