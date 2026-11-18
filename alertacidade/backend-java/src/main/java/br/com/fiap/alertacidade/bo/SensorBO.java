package br.com.fiap.alertacidade.bo;

import br.com.fiap.alertacidade.dao.SensorDAO;
import br.com.fiap.alertacidade.dto.SensorRequest;
import br.com.fiap.alertacidade.exception.ConflitoException;
import br.com.fiap.alertacidade.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertacidade.exception.RegraNegocioException;
import br.com.fiap.alertacidade.model.Sensor;
import br.com.fiap.alertacidade.model.StatusSensor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Regras de negocio dos sensores. */
@Service
public class SensorBO {

    private final SensorDAO sensorDAO;
    private final PontoBO pontoBO;

    public SensorBO(SensorDAO sensorDAO, PontoBO pontoBO) {
        this.sensorDAO = sensorDAO;
        this.pontoBO = pontoBO;
    }

    @Transactional(readOnly = true)
    public List<Sensor> listar(Long pontoId) {
        if (pontoId != null) {
            pontoBO.buscar(pontoId);
        }
        return sensorDAO.listar(pontoId);
    }

    @Transactional(readOnly = true)
    public Sensor buscar(Long id) {
        return sensorDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor", id));
    }

    @Transactional
    public Sensor cadastrar(SensorRequest req) {
        pontoBO.buscar(req.pontoId());
        String codigo = req.codigo().trim().toUpperCase();
        if (sensorDAO.existeCodigo(codigo, null)) {
            throw new ConflitoException("Ja existe um sensor com o codigo '" + codigo + "'");
        }
        Sensor sensor = new Sensor();
        sensor.setPontoId(req.pontoId());
        sensor.setCodigo(codigo);
        sensor.definirTipo(req.tipo());
        sensor.setStatus(req.status() != null ? req.status() : StatusSensor.ATIVO);
        sensor.setDataInstalacao(req.dataInstalacao() != null ? req.dataInstalacao() : LocalDate.now());
        Long id = sensorDAO.inserir(sensor);
        return buscar(id);
    }

    /** O tipo so pode mudar se o sensor ainda nao tem leituras (evita misturar cm e mm no historico). */
    @Transactional
    public Sensor atualizar(Long id, SensorRequest req) {
        Sensor sensor = buscar(id);
        pontoBO.buscar(req.pontoId());
        String codigo = req.codigo().trim().toUpperCase();
        if (sensorDAO.existeCodigo(codigo, id)) {
            throw new ConflitoException("Ja existe outro sensor com o codigo '" + codigo + "'");
        }
        if (req.tipo() != sensor.getTipo() && sensorDAO.contarMedicoes(id) > 0) {
            throw new RegraNegocioException("Nao e possivel alterar o tipo de um sensor que ja possui medicoes");
        }
        sensor.setPontoId(req.pontoId());
        sensor.setCodigo(codigo);
        sensor.definirTipo(req.tipo());
        if (req.status() != null) {
            sensor.setStatus(req.status());
        }
        if (req.dataInstalacao() != null) {
            sensor.setDataInstalacao(req.dataInstalacao());
        }
        sensorDAO.atualizar(sensor);
        return buscar(id);
    }

    @Transactional
    public void excluir(Long id) {
        buscar(id);
        int medicoes = sensorDAO.contarMedicoes(id);
        if (medicoes > 0) {
            throw new ConflitoException("Sensor possui " + medicoes
                    + " medicao(oes). Para preservar o historico, altere o status para INATIVO");
        }
        sensorDAO.excluir(id);
    }
}
