
package br.com.fiap.alertacidade.controller;

import br.com.fiap.alertacidade.bo.AlertaBO;
import br.com.fiap.alertacidade.dto.AlertaRequest;
import br.com.fiap.alertacidade.model.Alerta;
import br.com.fiap.alertacidade.model.StatusAlerta;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaBO alertaBO;

    public AlertaController(AlertaBO alertaBO) {
        this.alertaBO = alertaBO;
    }

    // GET /api/alertas
    // Permite filtrar por status e pontoId
    @GetMapping
    public List<Alerta> listar(
            @RequestParam(name = "status", required = false)
            StatusAlerta status,

            @RequestParam(name = "pontoId", required = false)
            Long pontoId) {

        return alertaBO.listar(status, pontoId);
    }

    // GET /api/alertas/{id}
    // Busca um alerta pelo ID
    @GetMapping("/{id}")
    public Alerta buscar(@PathVariable("id") Long id) {
        return alertaBO.buscar(id);
    }

    // POST /api/alertas
    // Cadastra um alerta manualmente
    // Alertas automaticos sao criados por POST /api/medicoes
    @PostMapping
    public ResponseEntity<Alerta> cadastrar(
            @Valid @RequestBody AlertaRequest req) {

        Alerta criado = alertaBO.cadastrarManual(req);

        URI local = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getId())
                .toUri();

        return ResponseEntity.created(local).body(criado);
    }

    // PUT /api/alertas/{id}
    // Atualiza um alerta existente
    // status ENCERRADO: encerra o alerta
    // status ABERTO: reabre o alerta
    @PutMapping("/{id}")
    public Alerta atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody AlertaRequest req) {

        return alertaBO.atualizar(id, req);
    }

    // DELETE /api/alertas/{id}
    // Exclui um alerta pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable("id") Long id) {

        alertaBO.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
