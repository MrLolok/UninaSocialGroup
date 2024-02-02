package org.unina.project.database.query.impl;

import org.unina.project.database.query.ResultSetExtractor;
import org.unina.project.database.query.SQLQueryProvider;
import org.unina.project.database.query.StatementFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
public class QueryRetrieveStatementFunction<T> implements StatementFunction<T>, SQLQueryProvider {
    @Getter
    private final String query;
    @Getter
    private final ResultSetExtractor<T> extractor;

    @Override
    public T execute(@NotNull Statement statement) throws SQLException {
        T result;
        try (ResultSet set = statement instanceof PreparedStatement ps ? ps.executeQuery() : statement.executeQuery(query)) {
            result = extractor.extract(set);
        }
        return result;
    }
}
