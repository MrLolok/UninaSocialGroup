package org.unina.project.database.query.impl;

import org.unina.project.database.query.SQLQueryProvider;
import org.unina.project.database.query.StatementFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@RequiredArgsConstructor
public class SimpleUpdateStatementFunction implements StatementFunction<Integer>, SQLQueryProvider {
    @Getter
    private final String query;

    @Override
    public Integer execute(@NotNull Statement statement) throws SQLException {
        return statement instanceof PreparedStatement ps ? ps.executeUpdate() : statement.executeUpdate(query);
    }
}
