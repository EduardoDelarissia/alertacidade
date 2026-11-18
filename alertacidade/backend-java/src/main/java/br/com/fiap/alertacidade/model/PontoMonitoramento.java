package br.com.fiap.alertacidade.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Tabela T_AC_PONTO_MONITORAMENTO: local fisico observado (ex.: trecho de canal). */
public class PontoMonitoramento {

    private Long id;
    private String nome;
    private String bairro;
    private String cidade;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal cotaAtencaoCm;
    private BigDecimal cotaCriticaCm;
    private boolean ativo = true;
    private LocalDateTime dataCadastro;

    public PontoMonitoramento() {
    }

    /**
     * Regra de dominio: classifica um nivel de agua (cm) segundo as cotas de demonstracao do ponto.
     * valor >= cota critica -> CRITICO; valor >= cota de atencao -> ATENCAO; senao NORMAL.
     */
    public NivelRisco classificar(BigDecimal nivelCm) {
        if (nivelCm.compareTo(cotaCriticaCm) >= 0) {
            return NivelRisco.CRITICO;
        }
        if (nivelCm.compareTo(cotaAtencaoCm) >= 0) {
            return NivelRisco.ATENCAO;
        }
        return NivelRisco.NORMAL;
    }

    public boolean cotasValidas() {
        return cotaAtencaoCm != null && cotaCriticaCm != null
                && cotaAtencaoCm.signum() > 0
                && cotaCriticaCm.compareTo(cotaAtencaoCm) > 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getCotaAtencaoCm() { return cotaAtencaoCm; }
    public void setCotaAtencaoCm(BigDecimal cotaAtencaoCm) { this.cotaAtencaoCm = cotaAtencaoCm; }

    public BigDecimal getCotaCriticaCm() { return cotaCriticaCm; }
    public void setCotaCriticaCm(BigDecimal cotaCriticaCm) { this.cotaCriticaCm = cotaCriticaCm; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
