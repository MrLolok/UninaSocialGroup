package org.unina.project.database.exceptions;

import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Eccezione lanciata quando si è impossibilitati ad eseguire uno statement SQL.
 */
public class StatementExecutionException extends RuntimeException {
    public StatementExecutionException(String msg, SQLException e) {
        this(msg, null, e);
    }

    public StatementExecutionException(String msg, @Nullable String statement, SQLException e) {
        super(String.format("%s - Commando: %s - Messaggio di errore: %s", msg, (statement == null ? "" : statement), e.getMessage()), e);
    }
}
