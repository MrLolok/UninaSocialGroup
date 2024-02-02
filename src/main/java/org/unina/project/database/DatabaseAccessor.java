package org.unina.project.database;

import lombok.AccessLevel;
import org.jetbrains.annotations.NotNull;
import org.unina.project.database.exceptions.ConnectionCloseException;
import org.unina.project.database.exceptions.ConnectionRecoveryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class DatabaseAccessor implements IDatabaseAccessor {
    @Getter(AccessLevel.PROTECTED)
    private final DataSource dataSource;

    @Override @Nullable
    public Connection getConnection() {
        if (dataSource == null)
            throw new NullPointerException("DataSource non può essere null.");
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ConnectionRecoveryException("Impossibile recuperare una Connection dal DataSource.", e);
        }
    }

    @Override
    public void close() {
        if (dataSource instanceof Closeable closeable)
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.warning(String.format("Si è verificato un errore durante la chiusura della pool di connessioni: %s", e.getMessage()));
            }
    }

    @Override
    public void close(@NotNull Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new ConnectionCloseException("Impossibile chiudere la connessione.", e);
        }
    }
}
