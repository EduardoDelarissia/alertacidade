package br.com.fiap.alertacidade.dao;

import br.com.fiap.alertacidade.model.Alerta;
import br.com.fiap.alertacidade.model.NivelRisco;
import br.com.fiap.alertacidade.model.StatusAlerta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Acesso a dados da tabela T_AC_ALERTA. */
@Repository
public class AlertaDAO {

    private static final String SELECT_BASE = """
            SELECT id_alerta, id_ponto, id_medicao, nv_alerta, ds_mensagem,
                   dt_hora_alerta, st_alerta, dt_encerramento
              FROM T_AC_ALERTA
            """;

    private static final RowMapper<Alerta> MAPPER = (rs, linha) -> {
        Alerta a = new Alerta();
        a.setId(rs.getLong("id_alerta"));
        a.setPontoId(rs.getLong("id_ponto"));
        a.setMedicaoId(JdbcUtil.getLongOrNull(rs, "id_medicao"));
        a.setNivel(NivelRisco.valueOf(rs.getString("nv_alerta")));
        a.setMensagem(rs.getString("ds_mensagem"));
        a.setDataHora(JdbcUtil.toLocalDateTime(rs.getTimestamp("dt_hora_alerta")));
        a.setStatus(StatusAlerta.valueOf(rs.getString("st_alerta")));
        a.setDataEncerramento(JdbcUtil.toLocalDateTime(rs.getTimestamp("dt_encerramento")));
        return a;
    };

    private final JdbcTemplate jdbc;

    public AlertaDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Filtros opcionais por status e ponto; mais recentes primeiro. */
    public List<Alerta> listar(StatusAlerta status, Long pontoId) {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql.append(" AND st_alerta = ?");
            params.add(status.name());
        }
        if (pontoId != null) {
            sql.append(" AND id_ponto = ?");
            params.add(pontoId);
        }
        sql.append(" ORDER BY dt_hora_alerta DESC, id_alerta DESC");
        return jdbc.query(sql.toString(), MAPPER, params.toArray());
    }

    public Optional<Alerta> buscarPorId(Long id) {
        return jdbc.query(SELECT_BASE + " WHERE id_alerta = ?", MAPPER, id).stream().findFirst();
    }

    public List<Alerta> listarAbertosDoPonto(Long pontoId) {
        return jdbc.query(SELECT_BASE + " WHERE id_ponto = ? AND st_alerta = 'ABERTO' ORDER BY dt_hora_alerta DESC",
                MAPPER, pontoId);
    }

    public int contarAbertosDoPonto(Long pontoId) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM T_AC_ALERTA WHERE id_ponto = ? AND st_alerta = 'ABERTO'",
                Integer.class, pontoId);
        return total == null ? 0 : total;
    }

    public Long inserir(Alerta a) {
        String sql = """
                INSERT INTO T_AC_ALERTA
                    (id_ponto, id_medicao, nv_alerta, ds_mensagem, dt_hora_alerta, st_alerta, dt_encerramento)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_ALERTA"});
            ps.setLong(1, a.getPontoId());
            if (a.getMedicaoId() == null) {
                ps.setNull(2, Types.NUMERIC);
            } else {
                ps.setLong(2, a.getMedicaoId());
            }
            ps.setString(3, a.getNivel().name());
            ps.setString(4, a.getMensagem());
            ps.setTimestamp(5, JdbcUtil.toTimestamp(a.getDataHora()));
            ps.setString(6, a.getStatus().name());
            ps.setTimestamp(7, JdbcUtil.toTimestamp(a.getDataEncerramento()));
            return ps;
        }, chave);
        return Objects.requireNonNull(chave.getKey(), "ID do alerta nao retornado").longValue();
    }

    public void atualizar(Alerta a) {
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    UPDATE T_AC_ALERTA
                       SET id_ponto = ?, nv_alerta = ?, ds_mensagem = ?, st_alerta = ?, dt_encerramento = ?
                     WHERE id_alerta = ?
                    """);
            ps.setLong(1, a.getPontoId());
            ps.setString(2, a.getNivel().name());
            ps.setString(3, a.getMensagem());
            ps.setString(4, a.getStatus().name());
            ps.setTimestamp(5, JdbcUtil.toTimestamp(a.getDataEncerramento()));
            ps.setLong(6, a.getId());
            return ps;
        });
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM T_AC_ALERTA WHERE id_alerta = ?", id);
    }
}
