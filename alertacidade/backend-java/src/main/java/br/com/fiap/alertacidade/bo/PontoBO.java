package br.com.fiap.alertacidade.bo;

import br.com.fiap.alertacidade.dao.AlertaDAO;
import br.com.fiap.alertacidade.dao.MedicaoDAO;
import br.com.fiap.alertacidade.dao.PontoDAO;
import br.com.fiap.alertacidade.dto.PontoRequest;
import br.com.fiap.alertacidade.dto.StatusPontoResponse;
import br.com.fiap.alertacidade.exception.ConflitoException;
import br.com.fiap.alertacidade.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertacidade.exception.RegraNegocioException;
import br.com.fiap.alertacidade.model.Medicao;
import br.com.fiap.alertacidade.model.PontoMonitoramento;
import br.com.fiap.alertacidade.model.TipoSensor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Regras de negocio dos pontos de monitoramento. */
@Service
public class PontoBO {

    private final PontoDAO pontoDAO;
    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;

    public PontoBO(PontoDAO pontoDAO, MedicaoDAO medicaoDAO, AlertaDAO alertaDAO) {
        this.pontoDAO = pontoDAO;
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
    }

    @Transactional(readOnly = true)
    public List<PontoMonitoramento> listar(String bairro) {
        return pontoDAO.listar(bairro);
    }

    @Transactional(readOnly = true)
    public PontoMonitoramento buscar(Long id) {
        return pontoDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ponto de monitoramento", id));
    }

    @Transactional
    public PontoMonitoramento cadastrar(PontoRequest req) {
        PontoMonitoramento ponto = new PontoMonitoramento();
        preencher(ponto, req);
        validar(ponto);
        if (pontoDAO.existeNome(ponto.getNome(), null)) {
            throw new ConflitoException("Ja existe um ponto com o nome '" + ponto.getNome() + "'");
        }
        Long id = pontoDAO.inserir(ponto);
        return buscar(id);
    }

    @Transactional
    public PontoMonitoramento atualizar(Long id, PontoRequest req) {
        PontoMonitoramento ponto = buscar(id);
        preencher(ponto, req);
        validar(ponto);
        if (pontoDAO.existeNome(ponto.getNome(), id)) {
            throw new ConflitoException("Ja existe outro ponto com o nome '" + ponto.getNome() + "'");
        }
        pontoDAO.atualizar(ponto);
        return buscar(id);
    }

    /** Pontos com sensores ou historico de alertas nao sao excluidos: desative-os (ativo=false). */
    @Transactional
    public void excluir(Long id) {
        buscar(id);
        int sensores = pontoDAO.contarSensores(id);
        int alertas = pontoDAO.contarAlertas(id);
        if (sensores > 0 || alertas > 0) {
            throw new ConflitoException("Ponto possui " + sensores + " sensor(es) e " + alertas
                    + " alerta(s) vinculados. Remova-os ou desative o ponto com PUT (ativo=false)");
        }
        pontoDAO.excluir(id);
    }

    @Transactional(readOnly = true)
    public List<StatusPontoResponse> statusDeTodos() {
        return pontoDAO.listar(null).stream().map(this::montarStatus).toList();
    }

    @Transactional(readOnly = true)
    public StatusPontoResponse status(Long id) {
        return montarStatus(buscar(id));
    }

    private StatusPontoResponse montarStatus(PontoMonitoramento ponto) {
        Optional<Medicao> nivel = medicaoDAO.buscarUltimaDoPonto(ponto.getId(), TipoSensor.NIVEL_AGUA);
        Optional<Medicao> chuva = medicaoDAO.buscarUltimaDoPonto(ponto.getId(), TipoSensor.PLUVIOMETRO);
        String estado = nivel.map(m -> ponto.classificar(m.getValor()).name()).orElse("SEM_DADOS");
        return new StatusPontoResponse(
                ponto.getId(), ponto.getNome(), ponto.getBairro(), ponto.isAtivo(), estado,
                nivel.map(Medicao::getValor).orElse(null),
                nivel.map(Medicao::getDataHora).orElse(null),
                chuva.map(Medicao::getValor).orElse(null),
                chuva.map(Medicao::getDataHora).orElse(null),
                ponto.getCotaAtencaoCm(), ponto.getCotaCriticaCm(),
                alertaDAO.contarAbertosDoPonto(ponto.getId()));
    }

    private void preencher(PontoMonitoramento ponto, PontoRequest req) {
        ponto.setNome(req.nome().trim());
        ponto.setBairro(req.bairro().trim());
        ponto.setCidade(req.cidade().trim());
        ponto.setLatitude(req.latitude());
        ponto.setLongitude(req.longitude());
        ponto.setCotaAtencaoCm(req.cotaAtencaoCm());
        ponto.setCotaCriticaCm(req.cotaCriticaCm());
        if (req.ativo() != null) {
            ponto.setAtivo(req.ativo());
        }
    }

    private void validar(PontoMonitoramento ponto) {
        if (!ponto.cotasValidas()) {
            throw new RegraNegocioException("cotaCriticaCm (" + ponto.getCotaCriticaCm()
                    + ") deve ser maior que cotaAtencaoCm (" + ponto.getCotaAtencaoCm() + ")");
        }
    }
}
