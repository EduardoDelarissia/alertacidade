package br.com.fiap.alertacidade.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Conversoes entre tipos JDBC (Oracle) e java.time. */
final class JdbcUtil {

    private JdbcUtil() {
    }

    static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    static Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt == null ? null : Timestamp.valueOf(ldt);
    }

    static LocalDate toLocalDate(Date d) {
        return d == null ? null : d.toLocalDate();
    }

    static Date toSqlDate(LocalDate ld) {
        return ld == null ? null : Date.valueOf(ld);
    }

    /** Le uma coluna NUMBER que pode ser nula. */
    static Long getLongOrNull(ResultSet rs, String coluna) throws SQLException {
        long valor = rs.getLong(coluna);
        return rs.wasNull() ? null : valor;
    }
}
