package org.unina.project.database.operations.impl;

import org.unina.project.database.operations.AsyncDatabase;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.PreparedStatementCreator;
import org.unina.project.database.query.PreparedStatementSetter;
import org.unina.project.database.query.ResultSetExtractor;
import org.unina.project.database.query.StatementFunction;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class DefaultAsyncDatabase implements AsyncDatabase {
    /**
     * Istanza di un {@link Database} basico da cui richiamare
     * i metodi di base per l'esecuzione di query implementandoli
     * come completable future ed eseguendoli su thread different.
     * @see AsyncDatabase#QUERY_EXECUTOR
     */
    private final Database basic;

    public DefaultAsyncDatabase(DataSource dataSource) {
        this.basic = new DefaultDatabase(dataSource);
    }

    @Override
    public <T> @NotNull CompletableFuture<T> execute(@NotNull StatementFunction<T> callback) {
        return CompletableFuture.supplyAsync(() -> basic.execute(callback), QUERY_EXECUTOR);
    }

    @Override
    public <T> @NotNull CompletableFuture<T> execute(@NotNull PreparedStatementCreator creator, PreparedStatementSetter setter, @NotNull StatementFunction<T> callback) {
        return CompletableFuture.supplyAsync(() -> basic.execute(creator, setter, callback), QUERY_EXECUTOR);
    }

    @Override
    public @NotNull CompletableFuture<Integer> update(@NotNull @Language("SQL") String sql) {
        return CompletableFuture.supplyAsync(() -> basic.update(sql), QUERY_EXECUTOR);
    }

    @Override
    public @NotNull CompletableFuture<Integer> update(@NotNull String sql, PreparedStatementSetter setter) {
        return CompletableFuture.supplyAsync(() -> basic.update(sql, setter), QUERY_EXECUTOR);
    }

    @Override
    public @NotNull CompletableFuture<Integer> update(@NotNull String sql, Object[] params, int... types) {
        return CompletableFuture.supplyAsync(() -> basic.update(sql, params, types), QUERY_EXECUTOR);
    }

    @Override
    public <T> @NotNull CompletableFuture<T> query(@NotNull @Language("SQL") String sql, @NotNull ResultSetExtractor<T> extractor) {
        return CompletableFuture.supplyAsync(() -> basic.query(sql, extractor), QUERY_EXECUTOR);
    }

    @Override
    public <T> @NotNull CompletableFuture<T> query(@NotNull String sql, PreparedStatementSetter setter, @NotNull ResultSetExtractor<T> extractor) {
        return CompletableFuture.supplyAsync(() -> basic.query(sql, setter, extractor), QUERY_EXECUTOR);
    }

    @Override
    public <T> @NotNull CompletableFuture<T> query(@NotNull String sql, Object[] params, @NotNull ResultSetExtractor<T> extractor, int... types) {
        return CompletableFuture.supplyAsync(() -> basic.query(sql, params, extractor, types), QUERY_EXECUTOR);
    }

    public void close() {
        basic.close();
    }
}
