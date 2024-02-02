package org.unina.project.database.operations.impl;

import org.unina.project.database.DatabaseAccessor;
import org.unina.project.database.IDatabaseAccessor;
import org.unina.project.database.exceptions.StatementExecutionException;
import org.unina.project.database.operations.Database;
import org.unina.project.database.query.*;
import org.unina.project.database.query.impl.DefaultPreparedStatementCreator;
import org.unina.project.database.query.impl.DefaultPreparedStatementSetter;
import org.unina.project.database.query.impl.QueryRetrieveStatementFunction;
import org.unina.project.database.query.impl.SimpleUpdateStatementFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DefaultDatabase extends DatabaseAccessor implements IDatabaseAccessor, Database {
    public DefaultDatabase(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Nullable
    public <T> T execute(@NotNull StatementFunction<T> callback) {
        Connection connection = getConnection();
        if (connection == null) return null;

        T result;
        try (Statement statement = connection.createStatement()) {
            result = callback.execute(statement);
        } catch (SQLException e) {
            throw new StatementExecutionException("Errore durante l'esecuzione di uno Statement.", getQuery(callback), e);
        } finally {
            close(connection);
        }
        return result;
    }

    @Override
    @Nullable
    public <T> T execute(@NotNull PreparedStatementCreator creator, @Nullable PreparedStatementSetter setter, @NotNull StatementFunction<T> callback) {
        Connection connection = getConnection();
        if (connection == null) return null;

        T result = null;
        try (PreparedStatement statement = creator.create(connection)) {
            if (statement != null) {
                if (setter != null) setter.set(statement);
                result = callback.execute(statement);
            }
        } catch (SQLException e) {
            throw new StatementExecutionException("Errore durante l'esecuzione di uno Statement.", getQuery(callback), e);
        } finally {
            close(connection);
        }
        return result;
    }

    @Override
    public int update(@NotNull @Language("SQL") String sql) {
        SimpleUpdateStatementFunction callback = new SimpleUpdateStatementFunction(sql);
        Object result = execute(callback);
        return result == null ? -1 : (int) result;
    }

    @Override
    public int update(@Language("SQL") @NotNull String sql, @Nullable PreparedStatementSetter setter) {
        SimpleUpdateStatementFunction callback = new SimpleUpdateStatementFunction(sql);
        Object result = execute(new DefaultPreparedStatementCreator(sql), setter, callback);
        return result == null ? -1 : (int) result;
    }

    @Override
    public int update(@Language("SQL") @NotNull String sql, Object[] params, int... types) {
        return update(sql, new DefaultPreparedStatementSetter(params, types));
    }

    @Override
    @Nullable
    public <T> T query(@NotNull @Language("SQL") String sql, @NotNull ResultSetExtractor<T> extractor) {
        return execute(new QueryRetrieveStatementFunction<>(sql, extractor));
    }

    @Override
    @Nullable
    public <T> T query(@NotNull @Language("SQL") String sql, @Nullable PreparedStatementSetter setter, @NotNull ResultSetExtractor<T> extractor) {
        return execute(new DefaultPreparedStatementCreator(sql), setter, new QueryRetrieveStatementFunction<>(sql, extractor));
    }

    @Override
    @Nullable
    public <T> T query(@NotNull @Language("SQL") String sql, Object[] params, @NotNull ResultSetExtractor<T> extractor, int... types) {
        return query(sql, new DefaultPreparedStatementSetter(params, types), extractor);
    }

    @Nullable
    private String getQuery(StatementFunction<?> callback) {
        return callback instanceof SQLQueryProvider provider ? provider.getQuery() : null;
    }
}
