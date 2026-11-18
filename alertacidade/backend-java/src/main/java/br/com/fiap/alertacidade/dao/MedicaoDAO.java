package br.com.fiap.alertacidade.dao;

import br.com.fiap.alertacidade.model.Medicao;
import br.com.fiap.alertacidade.model.OrigemMedicao;
import br.com.fiap.alertacidade.model.TipoSensor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Acesso a dados da tabela T_AC_MEDICAO. */
@Repository
public class MedicaoDAO {

    private static final String SELECT_BASE = """
            SELECT m.id_medicao, m.id_sensor, m.nr_valor, m.dt_hora_medicao, m.ds_origem
              FROM T_AC_MEDICAO m
            """;

    private static final RowMapper<Medicao> MAPPER = (rs, linha) -> {
        Medicao m = new Medicao();
        m.setId(rs.getLong("id_medicao"));
        m.setSensorId(rs.getLong("id_sensor"));
        m.setValor(rs.getBigDecimal("nr_valor"));
        m.setDataHora(JdbcUtil.toLocalDateTime(rs.getTimestamp("dt_hora_medicao")));
        m.setOrigem(OrigemMedicao.valueOf(rs.getString("ds_origem")));
        return m;
    };

    private final JdbcTemplate jdbc;

    public MedicaoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Leituras mais recentes primeiro, limitadas a {@code limite} linhas. */
    public List<Medicao> listar(Long sensorId, int limite) {
        if (sensorId == null) {
            return jdbc.query(SELECT_BASE + " ORDER BY m.dt_hora_medicao DESC, m.id_medicao DESC FETCH FIRST ? ROWS ONLY",
                    MAPPER, limite);
        }
        return jdbc.query(SELECT_BASE + " WHERE m.id_sensor = ? ORDER BY m.dt_hora_medicao DESC, m.id_medicao DESC FETCH FIRST ? ROWS ONLY",
                MAPPER, sensorId, limite);
    }

    public Optional<Medicao> buscarPorId(Long id) {
        return jdbc.query(SELECT_BASE + " WHERE m.id_medicao = ?", MAPPER, id).stream().findFirst();
    }

    /** Ultima leitura de um tipo de sensor em um ponto (usado no status do dashboard). */
    public Optional<Medicao> buscarUltimaDoPonto(Long pontoId, TipoSensor tipo) {
        return jdbc.query(SELECT_BASE + """
                         JOIN T_AC_SENSOR s ON s.id_sensor = m.id_sensor
                        WHERE s.id_ponto = ? AND s.tp_sensor = ?
                        ORDER BY m.dt_hora_medicao DESC, m.id_medicao DESC
                        FETCH FIRST 1 ROWS ONLY
                        """,
                MAPPER, pontoId, tipo.name()).stream().findFirst();
    }

    public Long inserir(Medicao m) {
        String sql = """
                INSERT INTO T_AC_MEDICAO (id_sensor, nr_valor, dt_hora_medicao, ds_origem)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_MEDICAO"});
            ps.setLong(1, m.getSensorId());
            ps.setBigDecimal(2, m.getValor());
            ps.setTimestamp(3, JdbcUtil.toTimestamp(m.getDataHora()));
            ps.setString(4, m.getOrigem().name());
            return ps;
        }, chave);
        return Objects.requireNonNull(chave.getKey(), "ID da medicao nao retornado").longValue();
    }

    public void atualizar(Medicao m) {
        jdbc.update("""
                UPDATE T_AC_MEDICAO
                   SET nr_valor = ?, dt_hora_medicao = ?, ds_origem = ?
                 WHERE id_medicao = ?
                """,
                m.getValor(), JdbcUtil.toTimestamp(m.getDataHora()), m.getOrigem().name(), m.getId());
    }

    /** Alertas que apontam para a medicao ficam com id_medicao nulo (FK ON DELETE SET NULL). */
    public void excluir(Long id) {
        jdbc.update("DELETE FROM T_AC_MEDICAO WHERE id_medicao = ?", id);
    }
}
