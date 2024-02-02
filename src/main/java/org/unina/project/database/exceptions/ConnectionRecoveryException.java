package org.unina.project.database.exceptions;

/**
 * Eccezione lanciata quando si è impossibilitati a recuperare una connessione SQL dal {@link javax.sql.DataSource}.
 */
public class ConnectionRecoveryException extends RuntimeException {
    public ConnectionRecoveryException(Exception e) {
        super(e);
    }

    public ConnectionRecoveryException(String msg, Exception e) {
        super(msg, e);
    }
}
