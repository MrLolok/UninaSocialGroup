package org.unina.project.database.query;

import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Questa classe rappresenta un entità dedita al settaggio dei valori
 * relativi a uno specifico {@link PreparedStatement}.
 */
@FunctionalInterface
public interface PreparedStatementSetter {
    /**
     * Esegui il {@link PreparedStatement}.
     * @param statement da eseguire
     * @throws SQLException nel caso l'esecuzione non vada a buon fine
     */
    void set(@NotNull PreparedStatement statement) throws SQLException;
}
