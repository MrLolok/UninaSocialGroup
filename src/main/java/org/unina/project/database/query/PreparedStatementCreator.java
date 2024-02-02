package org.unina.project.database.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Questa classe rappresenta un elaboratore di nuovi {@link PreparedStatement} da eseguire.
 */
@FunctionalInterface
public interface PreparedStatementCreator {
    /**
     * Crea un nuovo {@link PreparedStatement} da utilizzare.
     * @param connection da cui crearlo
     * @return nuovo prepared statement
     * @throws SQLException nel caso in cui la creazione non vada a buon fine
     */
    @Nullable PreparedStatement create(@NotNull Connection connection) throws SQLException;
}
