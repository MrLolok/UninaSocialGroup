package org.unina.project.database.query.impl;

import org.unina.project.database.query.PreparedStatementSetter;
import org.unina.project.database.utils.PreparedStatementUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class DefaultPreparedStatementSetter implements PreparedStatementSetter {
    @Getter @Nullable
    private final Object[] params;
    @Getter
    private final int[] types;

    @Override
    public void set(@NotNull PreparedStatement statement) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            PreparedStatementUtils.setValue(statement, i + 1, types[i], param);
        }
    }
}
