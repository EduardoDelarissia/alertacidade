package br.com.fiap.alertacidade.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Tabela T_AC_MEDICAO: leitura de um sensor (nivel em cm ou chuva em mm). */
public class Medicao {

    private Long id;
    private Long sensorId;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private OrigemMedicao origem = OrigemMedicao.SIMULADOR;

    public Medicao() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSensorId() { return sensorId; }
    public void setSensorId(Long sensorId) { this.sensorId = sensorId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public OrigemMedicao getOrigem() { return origem; }
    public void setOrigem(OrigemMedicao origem) { this.origem = origem; }
}
