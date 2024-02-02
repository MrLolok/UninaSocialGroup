package org.unina.project.database.operations;

import org.unina.project.database.query.PreparedStatementCreator;
import org.unina.project.database.query.PreparedStatementSetter;
import org.unina.project.database.query.ResultSetExtractor;
import org.unina.project.database.query.StatementFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Questa classe si occupa di gestire ed eseguire operazioni sul database SQL
 * in sincrono.
 * L'alternativa asincrona è il {@link AsyncDatabase}.
 */
public interface Database {
    /**
     * Esegui uno statement SQL.
     * @param callback associato all'esecuzione dello statement
     * @return risultato dell'esecuzione
     * @param <T> tipo del valore di ritorno
     */
    @Nullable <T> T execute(@NotNull StatementFunction<T> callback);

    /**
     * Esegui uno statement SQL.
     * @param creator dello statement
     * @param setter dello statement
     * @param callback associato all'esecuzione dello statement
     * @return risultato dell'esecuzione
     * @param <T> tipo del valore di ritorno
     */
    @Nullable <T> T execute(@NotNull PreparedStatementCreator creator, @Nullable PreparedStatementSetter setter, @NotNull StatementFunction<T> callback);

    /**
     * Esegui uno statement SQL di aggiornamento.
     * @param sql query da eseguire
     * @return risultato dell'esecuzione
     */
    int update(@NotNull @Language("SQL") String sql);

    /**
     * Esegui uno statement SQL di aggiornamento.
     * @param sql query da eseguire
     * @param setter dello statement
     * @return risultato dell'esecuzione
     */
    int update(@NotNull @Language("SQL") String sql, @Nullable PreparedStatementSetter setter);

    /**
     * Esegui uno statement SQL di aggiornamento.
     * @param sql query da eseguire
     * @param params da settare allo statement
     * @param types associati ai parametri dello statement
     * @return risultato dell'esecuzione
     */
    int update(@NotNull @Language("SQL") String sql, Object[] params, int... types);

    /**
     * Esegui un'interrogazione SQL.
     * @param sql query da eseguire
     * @param extractor dei risultati prodotti
     * @return risultati estratti
     * @param <T> tipo dei risultati estratti
     */
    @Nullable <T> T query(@NotNull @Language("SQL") String sql, @NotNull ResultSetExtractor<T> extractor);

    /**
     * Esegui un'interrogazione SQL.
     * @param sql query da eseguire
     * @param setter dello statement
     * @param extractor dei risultati prodotti
     * @return risultati estratti
     * @param <T> tipo dei risultati estratti
     */
    @Nullable <T> T query(@NotNull @Language("SQL") String sql, @Nullable PreparedStatementSetter setter, @NotNull ResultSetExtractor<T> extractor);

    /**
     * Esegui un'interrogazione SQL.
     * @param sql query da eseguire
     * @param params da settare allo statement
     * @param extractor dei risultati prodotti
     * @param types associati ai parametri dello statement
     * @return risultati estratti
     * @param <T> tipo dei risultati estratti
     */
    @Nullable <T> T query(@NotNull @Language("SQL") String sql, Object[] params, @NotNull ResultSetExtractor<T> extractor, int... types);

    /**
     * Chiudi le connessioni del {@link javax.sql.DataSource} associato a questo database
     */
    void close();
}
