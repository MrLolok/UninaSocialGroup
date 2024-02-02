package org.unina.project.database.query;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Questa classe rappresenta un callback associato all'esecuzione di statement SQL.
 * Si occupa di eseguire lo statement SQL e ritornare un valore relativo al risultato di esecuzione.
 * @param <T> tipo del valore di ritorno
 */
@FunctionalInterface
public interface StatementFunction<T> {
    /**
     * Esegui uno statement SQL.
     * @param statement da eseguire
     * @return risultato
     * @throws SQLException nel caso l'esecuzione non vada a buon fine
     */
    T execute(@NotNull Statement statement) throws SQLException;
}
