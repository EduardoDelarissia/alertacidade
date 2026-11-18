package br.com.fiap.alertacidade.controller;

import br.com.fiap.alertacidade.bo.MedicaoBO;
import br.com.fiap.alertacidade.dto.MedicaoRegistradaResponse;
import br.com.fiap.alertacidade.dto.MedicaoRequest;
import br.com.fiap.alertacidade.model.Medicao;
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
@RequestMapping("/api/medicoes")
public class MedicaoController {

    private final MedicaoBO medicaoBO;

    public MedicaoController(MedicaoBO medicaoBO) {
        this.medicaoBO = medicaoBO;
    }

    /** GET /api/medicoes?sensorId=...&limite=50 (mais recentes primeiro). */
    @GetMapping
    public List<Medicao> listar(@RequestParam(name = "sensorId", required = false) Long sensorId,
                                @RequestParam(name = "limite", required = false) Integer limite) {
        return medicaoBO.listar(sensorId, limite);
    }

    @GetMapping("/{id}")
    public Medicao buscar(@PathVariable("id") Long id) {
        return medicaoBO.buscar(id);
    }

    /** Registra uma leitura, classifica o risco e gera/encerra alertas. */
    @PostMapping
    public ResponseEntity<MedicaoRegistradaResponse> registrar(@Valid @RequestBody MedicaoRequest req) {
        MedicaoRegistradaResponse resposta = medicaoBO.registrar(req);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(resposta.medicao().getId()).toUri();
        return ResponseEntity.created(local).body(resposta);
    }

    @PutMapping("/{id}")
    public Medicao atualizar(@PathVariable("id") Long id, @Valid @RequestBody MedicaoRequest req) {
        return medicaoBO.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        medicaoBO.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
