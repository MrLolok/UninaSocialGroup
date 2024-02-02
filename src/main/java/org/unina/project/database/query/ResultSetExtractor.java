package org.unina.project.database.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;

/**
 * Questa classe rappresenta un estrattore di dati da un {@link ResultSet}.
 * @see org.unina.project.database.query.impl.AbstractExtractor
 * @param <T> tipo di dato da estrarre
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {
    /**
     * Estrai i dati da un {@link ResultSet}.
     * @param set da operare
     * @return dati estratti
     */
    @Nullable
    T extract(@NotNull ResultSet set);
}
