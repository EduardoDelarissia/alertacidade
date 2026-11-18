package br.com.fiap.alertacidade.dao;

import br.com.fiap.alertacidade.model.PontoMonitoramento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Acesso a dados da tabela T_AC_PONTO_MONITORAMENTO. */
@Repository
public class PontoDAO {

    private static final String SELECT_BASE = """
            SELECT id_ponto, nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude,
                   nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo, dt_cadastro
              FROM T_AC_PONTO_MONITORAMENTO
            """;

    private static final RowMapper<PontoMonitoramento> MAPPER = (rs, linha) -> {
        PontoMonitoramento p = new PontoMonitoramento();
        p.setId(rs.getLong("id_ponto"));
        p.setNome(rs.getString("nm_ponto"));
        p.setBairro(rs.getString("nm_bairro"));
        p.setCidade(rs.getString("nm_cidade"));
        p.setLatitude(rs.getBigDecimal("nr_latitude"));
        p.setLongitude(rs.getBigDecimal("nr_longitude"));
        p.setCotaAtencaoCm(rs.getBigDecimal("nr_cota_atencao_cm"));
        p.setCotaCriticaCm(rs.getBigDecimal("nr_cota_critica_cm"));
        p.setAtivo("S".equals(rs.getString("st_ativo")));
        p.setDataCadastro(JdbcUtil.toLocalDateTime(rs.getTimestamp("dt_cadastro")));
        return p;
    };

    private final JdbcTemplate jdbc;

    public PontoDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PontoMonitoramento> listar(String bairro) {
        if (bairro == null || bairro.isBlank()) {
            return jdbc.query(SELECT_BASE + " ORDER BY nm_bairro, nm_ponto", MAPPER);
        }
        return jdbc.query(SELECT_BASE + " WHERE UPPER(nm_bairro) = UPPER(?) ORDER BY nm_ponto",
                MAPPER, bairro.trim());
    }

    public Optional<PontoMonitoramento> buscarPorId(Long id) {
        return jdbc.query(SELECT_BASE + " WHERE id_ponto = ?", MAPPER, id).stream().findFirst();
    }

    public Long inserir(PontoMonitoramento p) {
        String sql = """
                INSERT INTO T_AC_PONTO_MONITORAMENTO
                    (nm_ponto, nm_bairro, nm_cidade, nr_latitude, nr_longitude,
                     nr_cota_atencao_cm, nr_cota_critica_cm, st_ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder chave = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID_PONTO"});
            ps.setString(1, p.getNome());
            ps.setString(2, p.getBairro());
            ps.setString(3, p.getCidade());
            ps.setBigDecimal(4, p.getLatitude());
            ps.setBigDecimal(5, p.getLongitude());
            ps.setBigDecimal(6, p.getCotaAtencaoCm());
            ps.setBigDecimal(7, p.getCotaCriticaCm());
            ps.setString(8, p.isAtivo() ? "S" : "N");
            return ps;
        }, chave);
        return Objects.requireNonNull(chave.getKey(), "ID do ponto nao retornado").longValue();
    }

    public void atualizar(PontoMonitoramento p) {
        jdbc.update("""
                UPDATE T_AC_PONTO_MONITORAMENTO
                   SET nm_ponto = ?, nm_bairro = ?, nm_cidade = ?, nr_latitude = ?, nr_longitude = ?,
                       nr_cota_atencao_cm = ?, nr_cota_critica_cm = ?, st_ativo = ?
                 WHERE id_ponto = ?
                """,
                p.getNome(), p.getBairro(), p.getCidade(), p.getLatitude(), p.getLongitude(),
                p.getCotaAtencaoCm(), p.getCotaCriticaCm(), p.isAtivo() ? "S" : "N", p.getId());
    }

    public void excluir(Long id) {
        jdbc.update("DELETE FROM T_AC_PONTO_MONITORAMENTO WHERE id_ponto = ?", id);
    }

    /** true se ja existe outro ponto com o mesmo nome (ignorando maiusculas). */
    public boolean existeNome(String nome, Long ignorarId) {
        Integer total = ignorarId == null
                ? jdbc.queryForObject(
                        "SELECT COUNT(*) FROM T_AC_PONTO_MONITORAMENTO WHERE UPPER(nm_ponto) = UPPER(?)",
                        Integer.class, nome.trim())
                : jdbc.queryForObject(
                        "SELECT COUNT(*) FROM T_AC_PONTO_MONITORAMENTO WHERE UPPER(nm_ponto) = UPPER(?) AND id_ponto <> ?",
                        Integer.class, nome.trim(), ignorarId);
        return total != null && total > 0;
    }

    public int contarSensores(Long id) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM T_AC_SENSOR WHERE id_ponto = ?", Integer.class, id);
        return total == null ? 0 : total;
    }

    public int contarAlertas(Long id) {
        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM T_AC_ALERTA WHERE id_ponto = ?", Integer.class, id);
        return total == null ? 0 : total;
    }
}
