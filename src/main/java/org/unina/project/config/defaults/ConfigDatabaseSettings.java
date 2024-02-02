package org.unina.project.config.defaults;

import java.util.Map;

/**
 * Classe record per la configurazione del database SQL da utilizzare.
 * @param hostname del database SQL
 * @param port del database SQL
 * @param username di accesso al database SQL
 * @param password di accesso al database SQL
 * @param database SQL a cui collegarsi
 * @param driver da utilizzare
 * @param options di connessione
 */
public record ConfigDatabaseSettings(
        String hostname, int port,
        String username, String password,
        String database,
        String driver, Map<String, Object> options) {
}
