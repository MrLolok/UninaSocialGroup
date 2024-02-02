package org.unina.project.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Questa classe rappresenta, come dice il nome, un accessorio alle implementazioni
 * dei vari database che implementa metodi di utilizzo comune come ad esempio quello
 * per ottenere nuove connessioni al database e quello per chiuderle.
 * @see org.unina.project.database.operations.Database
 * @see org.unina.project.database.operations.AsyncDatabase
 */
public interface IDatabaseAccessor {
    Logger LOGGER = Logger.getLogger("UninaSocialGroup-Database");

    /**
     * Preleva una nuova connessione del database dal {@link javax.sql.DataSource}.
     * @return nuova connessione
     */
    @Nullable Connection getConnection();

    /**
     * Chiudi l'intero {@link javax.sql.DataSource}.
     */
    void close();

    /**
     * Chiudi una specifica connessione al database.
     * @param connection da chiudere
     */
    void close(@NotNull Connection connection);
}
