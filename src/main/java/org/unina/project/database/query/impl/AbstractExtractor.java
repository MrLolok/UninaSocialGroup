package org.unina.project.database.query.impl;

import org.unina.project.database.exceptions.ResultSetExtractionException;
import org.unina.project.database.query.ResultSetExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExtractor<T> implements ResultSetExtractor<List<T>> {
    @Override
    public @Nullable List<T> extract(@NotNull ResultSet set) {
        try {
            List<T> list = new ArrayList<>();
            while (set.next())
                list.add(map(set));
            return list;
        } catch (SQLException e) {
            throw new ResultSetExtractionException("Impossibile estrarre i dati dal ResultSet.", e);
        }
    }

    public abstract @Nullable T map(ResultSet set);
}
