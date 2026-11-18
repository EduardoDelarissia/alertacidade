package br.com.fiap.alertacidade.model;

import java.time.LocalDateTime;

/**
 * Tabela T_AC_ALERTA: estado de risco registrado para um ponto.
 * medicaoId e nulo para alertas criados manualmente.
 */
public class Alerta {

    private Long id;
    private Long pontoId;
    private Long medicaoId;
    private NivelRisco nivel;
    private String mensagem;
    private LocalDateTime dataHora;
    private StatusAlerta status = StatusAlerta.ABERTO;
    private LocalDateTime dataEncerramento;

    public Alerta() {
    }

    public boolean isAberto() {
        return status == StatusAlerta.ABERTO;
    }

    public void encerrar(LocalDateTime quando) {
        this.status = StatusAlerta.ENCERRADO;
        this.dataEncerramento = quando;
    }

    public void reabrir() {
        this.status = StatusAlerta.ABERTO;
        this.dataEncerramento = null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPontoId() { return pontoId; }
    public void setPontoId(Long pontoId) { this.pontoId = pontoId; }

    public Long getMedicaoId() { return medicaoId; }
    public void setMedicaoId(Long medicaoId) { this.medicaoId = medicaoId; }

    public NivelRisco getNivel() { return nivel; }
    public void setNivel(NivelRisco nivel) { this.nivel = nivel; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public StatusAlerta getStatus() { return status; }
    public void setStatus(StatusAlerta status) { this.status = status; }

    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }
}
