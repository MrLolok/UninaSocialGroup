package org.unina.project.database.query.impl;

import org.jetbrains.annotations.Nullable;
import org.unina.project.database.query.PreparedStatementCreator;
import org.unina.project.database.query.SQLQueryProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class DefaultPreparedStatementCreator implements PreparedStatementCreator, SQLQueryProvider {
    @Getter
    private final String query;

    @Override @Nullable
    public PreparedStatement create(@NotNull Connection connection) throws SQLException {
        return connection.prepareStatement(query);
    }
}
