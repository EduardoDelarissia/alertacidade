package br.com.fiap.alertacidade.model;

import java.time.LocalDate;

/** Tabela T_AC_SENSOR: dispositivo (real ou simulado) instalado em um ponto. */
public class Sensor {

    private Long id;
    private Long pontoId;
    private String codigo;
    private TipoSensor tipo;
    private String unidade;
    private StatusSensor status = StatusSensor.ATIVO;
    private LocalDate dataInstalacao;

    public Sensor() {
    }

    public boolean aceitaLeituras() {
        return status == StatusSensor.ATIVO;
    }

    public boolean medeNivelDeAgua() {
        return tipo == TipoSensor.NIVEL_AGUA;
    }

    /** Define o tipo e ajusta a unidade correspondente (cm ou mm). */
    public void definirTipo(TipoSensor tipo) {
        this.tipo = tipo;
        this.unidade = tipo.getUnidade();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPontoId() { return pontoId; }
    public void setPontoId(Long pontoId) { this.pontoId = pontoId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public TipoSensor getTipo() { return tipo; }
    public void setTipo(TipoSensor tipo) { this.tipo = tipo; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public StatusSensor getStatus() { return status; }
    public void setStatus(StatusSensor status) { this.status = status; }

    public LocalDate getDataInstalacao() { return dataInstalacao; }
    public void setDataInstalacao(LocalDate dataInstalacao) { this.dataInstalacao = dataInstalacao; }
}
