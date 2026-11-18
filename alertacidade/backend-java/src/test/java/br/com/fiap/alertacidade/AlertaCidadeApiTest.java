package br.com.fiap.alertacidade;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integracao: sobem a aplicacao com o perfil dev (H2 em modo Oracle + dados de exemplo)
 * e exercitam os endpoints pela camada HTTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AlertaCidadeApiTest {

    @Autowired
    private MockMvc mvc;

    private long criar(String url, String json) throws Exception {
        MvcResult r = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        Number id = JsonPath.read(r.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private String medicao(long sensorId, String valor) {
        return "{\"sensorId\":" + sensorId + ",\"valor\":" + valor + "}";
    }

    @Test
    void listaDadosDeExemplo() throws Exception {
        mvc.perform(get("/api/pontos")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(6)));
        mvc.perform(get("/api/pontos/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("NORMAL"));
    }

    @Test
    void cenarioDeSubidaDaAguaMudaOEstadoDoPonto() throws Exception {
        long ponto = criar("/api/pontos", """
                {"nome":"Canal Teste API","bairro":"Bairro Teste","cidade":"Sao Paulo",
                 "latitude":-23.5,"longitude":-46.6,"cotaAtencaoCm":50,"cotaCriticaCm":90}
                """);
        long sensor = criar("/api/sensores",
                "{\"pontoId\":" + ponto + ",\"codigo\":\"S-API-NIV-01\",\"tipo\":\"NIVEL_AGUA\"}");

        mvc.perform(post("/api/medicoes").contentType(MediaType.APPLICATION_JSON).content(medicao(sensor, "30")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.classificacao").value("NORMAL"))
                .andExpect(jsonPath("$.alertaGerado").value(nullValue()));

        mvc.perform(post("/api/medicoes").contentType(MediaType.APPLICATION_JSON).content(medicao(sensor, "60")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.classificacao").value("ATENCAO"))
                .andExpect(jsonPath("$.alertaGerado.nivel").value("ATENCAO"));

        mvc.perform(post("/api/medicoes").contentType(MediaType.APPLICATION_JSON).content(medicao(sensor, "95")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.classificacao").value("CRITICO"))
                .andExpect(jsonPath("$.alertasEncerrados").value(1));

        mvc.perform(get("/api/pontos/" + ponto + "/status"))
                .andExpect(jsonPath("$.estado").value("CRITICO"))
                .andExpect(jsonPath("$.alertasAbertos").value(1));

        mvc.perform(post("/api/medicoes").contentType(MediaType.APPLICATION_JSON).content(medicao(sensor, "20")))
                .andExpect(jsonPath("$.classificacao").value("NORMAL"))
                .andExpect(jsonPath("$.alertasEncerrados").value(1));

        mvc.perform(get("/api/alertas").param("status", "ABERTO").param("pontoId", String.valueOf(ponto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void crudCompletoDeAlertaManual() throws Exception {
        long alerta = criar("/api/alertas",
                "{\"pontoId\":2,\"nivel\":\"ATENCAO\",\"mensagem\":\"Teste manual\"}");
        mvc.perform(put("/api/alertas/" + alerta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pontoId\":2,\"nivel\":\"ATENCAO\",\"mensagem\":\"Encerrado\",\"status\":\"ENCERRADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCERRADO"))
                .andExpect(jsonPath("$.dataEncerramento").exists());
        mvc.perform(delete("/api/alertas/" + alerta)).andExpect(status().isNoContent());
        mvc.perform(get("/api/alertas/" + alerta)).andExpect(status().isNotFound());
    }

    @Test
    void errosSaoPadronizados() throws Exception {
        // 400 - Bean Validation
        mvc.perform(post("/api/pontos").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes").isArray());
        // 400 - enum invalido
        mvc.perform(post("/api/sensores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pontoId\":1,\"codigo\":\"X\",\"tipo\":\"RADAR\"}"))
                .andExpect(status().isBadRequest());
        // 400 - id nao numerico
        mvc.perform(get("/api/pontos/abc")).andExpect(status().isBadRequest());
        // 404
        mvc.perform(get("/api/pontos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.caminho").value("/api/pontos/9999"));
        // 409 - ponto com sensores
        mvc.perform(delete("/api/pontos/1")).andExpect(status().isConflict());
        // 422 - sensor em manutencao (S-ESC-NIV-01, id 5 nos dados de exemplo)
        mvc.perform(post("/api/medicoes").contentType(MediaType.APPLICATION_JSON).content(medicao(5, "10")))
                .andExpect(status().isUnprocessableEntity());
        // 422 - cotas invertidas
        mvc.perform(post("/api/pontos").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nome":"Cotas Invertidas","bairro":"B","cidade":"C",
                         "latitude":0,"longitude":0,"cotaAtencaoCm":90,"cotaCriticaCm":50}
                        """))
                .andExpect(status().isUnprocessableEntity());
    }
}
