package org.unina.project.database.exceptions;

/**
 * Eccezione lanciata quando si è impossibilitati a creare un nuovo {@link com.zaxxer.hikari.HikariDataSource}.
 */
public class HikariDataSourceCreationException extends RuntimeException {
    public HikariDataSourceCreationException(Exception e) {
        super(e);
    }

    public HikariDataSourceCreationException(String msg, Exception e) {
        super(msg, e);
    }
}
