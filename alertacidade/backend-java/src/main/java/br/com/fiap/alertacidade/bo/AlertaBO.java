package br.com.fiap.alertacidade.bo;

import br.com.fiap.alertacidade.dao.AlertaDAO;
import br.com.fiap.alertacidade.dto.AlertaRequest;
import br.com.fiap.alertacidade.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertacidade.exception.RegraNegocioException;
import br.com.fiap.alertacidade.model.Alerta;
import br.com.fiap.alertacidade.model.NivelRisco;
import br.com.fiap.alertacidade.model.PontoMonitoramento;
import br.com.fiap.alertacidade.model.StatusAlerta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negocio dos alertas (manuais e gerados por medicoes). */
@Service
public class AlertaBO {

    private final AlertaDAO alertaDAO;
    private final PontoBO pontoBO;

    public AlertaBO(AlertaDAO alertaDAO, PontoBO pontoBO) {
        this.alertaDAO = alertaDAO;
        this.pontoBO = pontoBO;
    }

    @Transactional(readOnly = true)
    public List<Alerta> listar(StatusAlerta status, Long pontoId) {
        return alertaDAO.listar(status, pontoId);
    }

    @Transactional(readOnly = true)
    public Alerta buscar(Long id) {
        return alertaDAO.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }

    /** Alerta manual (ex.: agente comunitario observou agua na via com o sensor em manutencao). */
    @Transactional
    public Alerta cadastrarManual(AlertaRequest req) {
        PontoMonitoramento ponto = pontoBO.buscar(req.pontoId());
        if (!ponto.isAtivo()) {
            throw new RegraNegocioException("Nao e possivel abrir alerta para um ponto desativado");
        }
        validarNivel(req.nivel());
        Alerta alerta = new Alerta();
        alerta.setPontoId(ponto.getId());
        alerta.setNivel(req.nivel());
        alerta.setMensagem(req.mensagem().trim());
        LocalDateTime agora = LocalDateTime.now();
        alerta.setDataHora(agora);
        if (req.status() == StatusAlerta.ENCERRADO) {
            alerta.encerrar(agora);
        }
        Long id = alertaDAO.inserir(alerta);
        return buscar(id);
    }

    /** Atualiza nivel/mensagem e permite encerrar ou reabrir o alerta. */
    @Transactional
    public Alerta atualizar(Long id, AlertaRequest req) {
        Alerta alerta = buscar(id);
        pontoBO.buscar(req.pontoId());
        validarNivel(req.nivel());
        alerta.setPontoId(req.pontoId());
        alerta.setNivel(req.nivel());
        alerta.setMensagem(req.mensagem().trim());
        if (req.status() == StatusAlerta.ENCERRADO && alerta.isAberto()) {
            alerta.encerrar(LocalDateTime.now());
        } else if (req.status() == StatusAlerta.ABERTO && !alerta.isAberto()) {
            alerta.reabrir();
        }
        alertaDAO.atualizar(alerta);
        return buscar(id);
    }

    @Transactional
    public void excluir(Long id) {
        buscar(id);
        alertaDAO.excluir(id);
    }

    private void validarNivel(NivelRisco nivel) {
        if (!nivel.geraAlerta()) {
            throw new RegraNegocioException("Alertas aceitam apenas os niveis ATENCAO ou CRITICO");
        }
    }
}
