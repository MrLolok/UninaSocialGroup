package org.unina.project.database.query.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.unina.project.database.query.PreparedStatementSetter;
import org.unina.project.database.query.StatementMatch;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class MatchesPreparedStatementSetter implements PreparedStatementSetter {
    @Getter @Nullable
    private final StatementMatch[] matches;

    @Override
    public void set(@NotNull PreparedStatement statement) throws SQLException {
        if (matches == null) return;
        for (StatementMatch match : matches)
            match.set(statement);
    }
}
