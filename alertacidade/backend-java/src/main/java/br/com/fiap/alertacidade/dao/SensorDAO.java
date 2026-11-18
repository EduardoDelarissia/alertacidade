package br.com.fiap.alertacidade.dao;

import br.com.fiap.alertacidade.model.Sensor;
import br.com.fiap.alertacidade.model.StatusSensor;
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

/** Acesso a dados da tabela T_AC_SENSOR. */
@Repository
public class SensorDAO {

    private static final String SELECT_BASE = """
            SELECT id_sensor, id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao
              FROM T_AC_SENSOR
            """;

    private static final RowMapper<Sensor> MAPPER = (rs, linha) -> {
        Sensor s = new Sensor();
        s.setId(rs.getLong("id_sensor"));
        s.setPontoId(rs.getLong("id_ponto"));
        s.setCodigo(rs.getString("cd_sensor"));
        s.setTipo(TipoSensor.valueOf(rs.getString("tp_sensor")));
        s.setUnidade(rs.getString("ds_unidade"));
        s.setStatus(StatusSensor.valueOf(rs.getString("st_sensor")));
        s.setDataInstalacao(JdbcUtil.toLocalDate(rs.getDate("dt_instalacao")));
        return s;
    };

    private final JdbcTemplate jdbc;

    public SensorDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Sensor> listar(Long pontoId) {
        if (pontoId == null) {
            return jdbc.query(SELECT_BASE + " ORDER BY cd_sensor", MAPPER);
        }
        return jdbc.query(SELECT_BASE + " WHERE id_ponto = ? ORDER BY cd_sensor", MAPPER, pontoId);
    }

    public Optional<Sensor> buscarPorId(Long id) {
        return jdbc.query(SELECT_BASE + " WHERE id_sensor = ?", MAPPER, id).stream().findFirst();
    }

    public Long inserir(Sensor s) {
        String sql = """
                INSERT INTO T_AC_SENSOR (id_ponto, cd_sensor, tp_sensor, ds_unidade, st_sensor, dt_instalacao)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_SENSOR"});
            ps.setLong(1, s.getPontoId());
            ps.setString(2, s.getCodigo());
            ps.setString(3, s.getTipo().name());
            ps.setString(4, s.getUnidade());
            ps.setString(5, s.getStatus().name());
            ps.setDate(6, JdbcUtil.toSqlDate(s.getDataInstalacao()));
            return ps;
        }, chave);
        return Objects.requireNonNull(chave.getKey(), "ID do sensor nao retornado").longValue();
    }

    public void atualizar(Sensor s) {
        jdbc.update("""
                UPDATE T_AC_SENSOR
                   SET id_ponto = ?, cd_sensor = ?, tp_sensor = ?, ds_unidade = ?, st_sensor = ?, dt_instalacao = ?
                 WHERE id_sensor = ?
                """,
                s.getPontoId(), s.getCodigo(), s.getTipo().name(), s.getUnidade(), s.getStatus().name(),
                JdbcUtil.toSqlDate(s.getDataInstalacao()), s.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM T_AC_SENSOR WHERE id_sensor = ?", id);
    }

    public boolean existeCodigo(String codigo, Long ignorarId) {
        Integer total = ignorarId == null
                ? jdbc.queryForObject(
                        "SELECT COUNT(*) FROM T_AC_SENSOR WHERE UPPER(cd_sensor) = UPPER(?)",
                        Integer.class, codigo.trim())
                : jdbc.queryForObject(
                        "SELECT COUNT(*) FROM T_AC_SENSOR WHERE UPPER(cd_sensor) = UPPER(?) AND id_sensor <> ?",
                        Integer.class, codigo.trim(), ignorarId);
        return total != null && total > 0;
    }

    public int contarMedicoes(Long id) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM T_AC_MEDICAO WHERE id_sensor = ?", Integer.class, id);
        return total == null ? 0 : total;
    }
}
