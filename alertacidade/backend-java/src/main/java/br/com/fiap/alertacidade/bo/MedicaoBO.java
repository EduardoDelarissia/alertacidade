package br.com.fiap.alertacidade.bo;

import br.com.fiap.alertacidade.dao.AlertaDAO;
import br.com.fiap.alertacidade.dao.MedicaoDAO;
import br.com.fiap.alertacidade.dto.MedicaoRegistradaResponse;
import br.com.fiap.alertacidade.dto.MedicaoRequest;
import br.com.fiap.alertacidade.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertacidade.exception.RegraNegocioException;
import br.com.fiap.alertacidade.model.Alerta;
import br.com.fiap.alertacidade.model.Medicao;
import br.com.fiap.alertacidade.model.NivelRisco;
import br.com.fiap.alertacidade.model.OrigemMedicao;
import br.com.fiap.alertacidade.model.PontoMonitoramento;
import br.com.fiap.alertacidade.model.Sensor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Regras de negocio das medicoes. Ao registrar uma leitura de nivel de agua:
 * <ol>
 *   <li>classifica o valor pelas cotas do ponto (NORMAL / ATENCAO / CRITICO);</li>
 *   <li>ATENCAO ou CRITICO: abre um alerta, a menos que ja exista alerta aberto de nivel igual ou maior;
 *       alertas automaticos de nivel menor sao encerrados (substituidos pelo mais grave);</li>
 *   <li>NORMAL: encerra os alertas automaticos abertos do ponto (alertas manuais sao mantidos).</li>
 * </ol>
 * Leituras de pluviometro sao apenas registradas (sem classificacao).
 */
@Service
public class MedicaoBO {

    /** Tolerancia para diferenca de relogio entre o simulador e o servidor. */
    private static final long TOLERANCIA_FUTURO_MINUTOS = 5;
    static final int LIMITE_PADRAO = 50;
    static final int LIMITE_MAXIMO = 500;

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final SensorBO sensorBO;
    private final PontoBO pontoBO;

    public MedicaoBO(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO, SensorBO sensorBO, PontoBO pontoBO) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.sensorBO = sensorBO;
        this.pontoBO = pontoBO;
    }

    @Transactional(readOnly = true)
    public List<Medicao> listar(Long sensorId, Integer limite) {
        int n = limite == null ? LIMITE_PADRAO : limite;
        if (n < 1 || n > LIMITE_MAXIMO) {
            throw new RegraNegocioException("limite deve estar entre 1 e " + LIMITE_MAXIMO);
        }
        if (sensorId != null) {
            sensorBO.buscar(sensorId);
        }
        return medicaoDAO.listar(sensorId, n);
    }

    @Transactional(readOnly = true)
    public Medicao buscar(Long id) {
        return medicaoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Medicao", id));
    }

    @Transactional
    public MedicaoRegistradaResponse registrar(MedicaoRequest req) {
        Sensor sensor = sensorBO.buscar(req.sensorId());
        if (!sensor.aceitaLeituras()) {
            throw new RegraNegocioException("Sensor " + sensor.getCodigo() + " esta " + sensor.getStatus()
                    + "; apenas sensores ATIVOS registram leituras");
        }
        LocalDateTime agora = LocalDateTime.now();

        Medicao medicao = new Medicao();
        medicao.setSensorId(sensor.getId());
        medicao.setValor(req.valor());
        medicao.setDataHora(validarDataHora(req.dataHora(), agora));
        medicao.setOrigem(req.origem() != null ? req.origem() : OrigemMedicao.SIMULADOR);
        medicao.setId(medicaoDAO.inserir(medicao));

        if (!sensor.medeNivelDeAgua()) {
            return new MedicaoRegistradaResponse(medicao, null, null, 0);
        }

        PontoMonitoramento ponto = pontoBO.buscar(sensor.getPontoId());
        NivelRisco nivel = ponto.classificar(medicao.getValor());
        List<Alerta> abertos = alertaDAO.listarAbertosDoPonto(ponto.getId());

        if (!nivel.geraAlerta()) {
            int encerrados = encerrarAutomaticos(abertos, NivelRisco.CRITICO, agora);
            return new MedicaoRegistradaResponse(medicao, nivel, null, encerrados);
        }

        // Ja existe alerta aberto (manual ou automatico) de nivel igual ou maior: nao duplica
        boolean jaCoberto = abertos.stream().anyMatch(a -> !nivel.maisGraveQue(a.getNivel()));
        if (jaCoberto) {
            return new MedicaoRegistradaResponse(medicao, nivel, null, 0);
        }

        // Escalada: encerra alertas automaticos menos graves e abre o novo
        NivelRisco abaixo = NivelRisco.values()[nivel.ordinal() - 1];
        int encerrados = encerrarAutomaticos(abertos, abaixo, agora);

        Alerta alerta = new Alerta();
        alerta.setPontoId(ponto.getId());
        alerta.setMedicaoId(medicao.getId());
        alerta.setNivel(nivel);
        alerta.setMensagem(mensagemAutomatica(nivel, medicao, ponto));
        alerta.setDataHora(medicao.getDataHora());
        alerta.setId(alertaDAO.inserir(alerta));

        return new MedicaoRegistradaResponse(medicao, nivel, alerta, encerrados);
    }

    /**
     * Corrige valor, data/hora ou origem de uma leitura. O sensor nao pode ser trocado.
     * A correcao nao reprocessa alertas ja emitidos (o historico de alertas e preservado).
     */
    @Transactional
    public Medicao atualizar(Long id, MedicaoRequest req) {
        Medicao medicao = buscar(id);
        if (!medicao.getSensorId().equals(req.sensorId())) {
            throw new RegraNegocioException("Nao e permitido trocar o sensor de uma medicao; exclua e registre novamente");
        }
        medicao.setValor(req.valor());
        if (req.dataHora() != null) {
            medicao.setDataHora(validarDataHora(req.dataHora(), LocalDateTime.now()));
        }
        if (req.origem() != null) {
            medicao.setOrigem(req.origem());
        }
        medicaoDAO.atualizar(medicao);
        return buscar(id);
    }

    @Transactional
    public void excluir(Long id) {
        buscar(id);
        medicaoDAO.excluir(id);
    }

    private LocalDateTime validarDataHora(LocalDateTime informada, LocalDateTime agora) {
        if (informada == null) {
            return agora;
        }
        if (informada.isAfter(agora.plusMinutes(TOLERANCIA_FUTURO_MINUTOS))) {
            throw new RegraNegocioException("dataHora da medicao nao pode estar no futuro");
        }
        return informada;
    }

    /** Encerra alertas automaticos (com medicao de origem) abertos de nivel ate {@code nivelMaximo}. */
    private int encerrarAutomaticos(List<Alerta> abertos, NivelRisco nivelMaximo, LocalDateTime quando) {
        int total = 0;
        for (Alerta a : abertos) {
            boolean automatico = a.getMedicaoId() != null;
            if (automatico && !a.getNivel().maisGraveQue(nivelMaximo)) {
                a.encerrar(quando);
                alertaDAO.atualizar(a);
                total++;
            }
        }
        return total;
    }

    private String mensagemAutomatica(NivelRisco nivel, Medicao medicao, PontoMonitoramento ponto) {
        boolean critico = nivel == NivelRisco.CRITICO;
        return String.format("Nivel de agua em %s cm em %s (cota %s: %s cm). Valores de demonstracao, nao oficiais.",
                medicao.getValor().stripTrailingZeros().toPlainString(),
                ponto.getNome(),
                critico ? "critica" : "de atencao",
                (critico ? ponto.getCotaCriticaCm() : ponto.getCotaAtencaoCm()).stripTrailingZeros().toPlainString());
    }
}
