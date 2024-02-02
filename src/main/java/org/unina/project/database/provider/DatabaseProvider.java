package org.unina.project.database.provider;

import org.jetbrains.annotations.NotNull;
import org.unina.project.config.defaults.ConfigDatabaseSettings;
import org.unina.project.database.operations.AsyncDatabase;
import org.unina.project.database.operations.Database;

import javax.sql.DataSource;

/**
 * Questa classe si occupa di provvedere alla creazione di un nuovi {@link Database} e {@link AsyncDatabase}.
 */
public interface DatabaseProvider {
    /**
     * Istanza default dal medesimo DatabaseProvider
     */
    DatabaseProvider DEFAULT_DATABASE_PROVIDER = new DefaultDatabaseProvider();

    /**
     * Ottieni l'istanza default del DatabaseProvide
     * @return istanza default
     */
    static DatabaseProvider getProvider() {
        return DEFAULT_DATABASE_PROVIDER;
    }

    /**
     * Crea una nuova istanza di {@link Database}.
     * @param settings per la connessione e gestione del database
     * @return nuovo database sincrono
     */
    @NotNull Database newDatabase(@NotNull ConfigDatabaseSettings settings);

    /**
     * Crea una nuova istanza di {@link Database}.
     * @param dataSource da cui prelevare le connessioni
     * @return nuovo database sincrono
     */
    @NotNull Database newDatabase(@NotNull DataSource dataSource);

    /**
     * Crea una nuova istanza di {@link AsyncDatabase}.
     * @param settings per la connessione e gestione del database
     * @return nuovo database asincrono
     */
    @NotNull AsyncDatabase newAsyncDatabase(@NotNull ConfigDatabaseSettings settings);

    /**
     * Crea una nuova istanza di {@link AsyncDatabase}.
     * @param basic database da cui usufruire delle operazioni di base
     * @return nuovo database asincrono
     */
    @NotNull AsyncDatabase newAsyncDatabase(@NotNull Database basic);

    /**
     * Crea una nuova istanza di {@link AsyncDatabase}.
     * @param dataSource da cui prelevare le connessioni
     * @return nuovo database asincrono
     */
    @NotNull AsyncDatabase newAsyncDatabase(@NotNull DataSource dataSource);
}
