package br.com.fiap.alertacidade.controller;

import br.com.fiap.alertacidade.bo.SensorBO;
import br.com.fiap.alertacidade.dto.SensorRequest;
import br.com.fiap.alertacidade.model.Sensor;
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
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorBO sensorBO;

    public SensorController(SensorBO sensorBO) {
        this.sensorBO = sensorBO;
    }

    /** GET /api/sensores?pontoId=... */
    @GetMapping
    public List<Sensor> listar(@RequestParam(name = "pontoId", required = false) Long pontoId) {
        return sensorBO.listar(pontoId);
    }

    @GetMapping("/{id}")
    public Sensor buscar(@PathVariable("id") Long id) {
        return sensorBO.buscar(id);
    }

    @PostMapping
    public ResponseEntity<Sensor> cadastrar(@Valid @RequestBody SensorRequest req) {
        Sensor criado = sensorBO.cadastrar(req);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public Sensor atualizar(@PathVariable("id") Long id, @Valid @RequestBody SensorRequest req) {
        return sensorBO.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        sensorBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
