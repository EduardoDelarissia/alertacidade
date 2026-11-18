package br.com.fiap.alertacidade.controller;

import br.com.fiap.alertacidade.bo.PontoBO;
import br.com.fiap.alertacidade.dto.PontoRequest;
import br.com.fiap.alertacidade.dto.StatusPontoResponse;
import br.com.fiap.alertacidade.model.PontoMonitoramento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pontos")
public class PontoController {

    private final PontoBO pontoBO;

    public PontoController(PontoBO pontoBO) {
        this.pontoBO = pontoBO;
    }

    /** GET /api/pontos?bairro=... */
    @GetMapping
    public List<PontoMonitoramento> listar(@RequestParam(name = "bairro", required = false) String bairro) {
        return pontoBO.listar(bairro);
    }

    @GetMapping("/{id}")
    public PontoMonitoramento buscar(@PathVariable("id") Long id) {
        return pontoBO.buscar(id);
    }

    /** Situacao atual de todos os pontos (dashboard). */
    @GetMapping("/status")
    public List<StatusPontoResponse> statusDeTodos() {
        return pontoBO.statusDeTodos();
    }

    @GetMapping("/{id}/status")
    public StatusPontoResponse status(@PathVariable("id") Long id) {
        return pontoBO.status(id);
    }

    @PostMapping
    public ResponseEntity<PontoMonitoramento> cadastrar(@Valid @RequestBody PontoRequest req) {
        PontoMonitoramento criado = pontoBO.cadastrar(req);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public PontoMonitoramento atualizar(@PathVariable("id") Long id, @Valid @RequestBody PontoRequest req) {
        return pontoBO.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        pontoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
