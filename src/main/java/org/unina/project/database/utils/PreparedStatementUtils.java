package org.unina.project.database.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;

/**
 * Classe utils per semplificare interazioni comuni e ripetitive con i {@link PreparedStatement}
 */
public final class PreparedStatementUtils {
    /**
     * Imposta i parametri di un {@link PreparedStatement} rifacendosi alle
     * tipologie di parametro SQL.
     * @param statement da impostare
     * @param paramIndex del parametro da impostare
     * @param sqlType del parametro da impostare
     * @param value da impostare
     * @throws SQLException nel caso in cui il settaggio non vada a buon fine
     */
    public static void setValue(@NotNull PreparedStatement statement, int paramIndex, int sqlType, @Nullable Object value) throws SQLException {
        if (value == null) {
            statement.setNull(paramIndex, sqlType);
            return;
        }

        switch (sqlType) {
            case Types.VARCHAR, Types.LONGVARCHAR -> statement.setString(paramIndex, value.toString());
            case Types.DECIMAL, Types.NUMERIC -> {
                if (value instanceof BigDecimal) {
                    statement.setBigDecimal(paramIndex, (BigDecimal) value);
                } else {
                    statement.setObject(paramIndex, value, sqlType);
                }
            }
            case Types.BOOLEAN -> {
                if (value instanceof Boolean) {
                    statement.setBoolean(paramIndex, (Boolean) value);
                } else {
                    statement.setObject(paramIndex, value, Types.BOOLEAN);
                }
            }
            case Types.DATE -> {
                if (value instanceof java.util.Date) {
                    if (value instanceof java.sql.Date)
                        statement.setDate(paramIndex, (java.sql.Date) value);
                    else
                        statement.setDate(paramIndex, new java.sql.Date(((java.util.Date) value).getTime()));
                } else if (value instanceof Calendar cal) {
                    statement.setDate(paramIndex, new java.sql.Date(cal.getTime().getTime()), cal);
                } else {
                    statement.setObject(paramIndex, value, Types.DATE);
                }
            }
            case Types.TIME -> {
                if (value instanceof java.util.Date) {
                    if (value instanceof java.sql.Time) {
                        statement.setTime(paramIndex, (java.sql.Time) value);
                    } else {
                        statement.setTime(paramIndex, new java.sql.Time(((java.util.Date) value).getTime()));
                    }
                } else if (value instanceof Calendar cal) {
                    statement.setTime(paramIndex, new java.sql.Time(cal.getTime().getTime()), cal);
                } else {
                    statement.setObject(paramIndex, value, Types.TIME);
                }
            }
            case Types.TIMESTAMP -> {
                if (value instanceof java.util.Date) {
                    if (value instanceof java.sql.Timestamp) {
                        statement.setTimestamp(paramIndex, (java.sql.Timestamp) value);
                    } else {
                        statement.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) value).getTime()));
                    }
                } else if (value instanceof Calendar cal) {
                    statement.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
                } else {
                    statement.setObject(paramIndex, value, Types.TIMESTAMP);
                }
            }
            default -> statement.setObject(paramIndex, value, sqlType);
        }
    }
}
