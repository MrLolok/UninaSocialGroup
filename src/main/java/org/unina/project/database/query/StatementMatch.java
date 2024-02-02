package org.unina.project.database.query;

import org.jetbrains.annotations.NotNull;
import org.unina.project.database.utils.PreparedStatementUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Record rappresentativo di associazione di un valore a un parametro di un {@link PreparedStatement}.
 * @param index del parametro
 * @param value da impostare
 * @param type SQL del valore
 */
public record StatementMatch(int index, Object value, int type) {
    public void set(@NotNull PreparedStatement statement) {
        try {
            PreparedStatementUtils.setValue(statement, index, type, value);
        } catch (SQLException e) {
            throw new RuntimeException("Impossibile impostare valori al PreparedStatement.", e);
        }
    }
}
