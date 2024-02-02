package org.unina.project.database.exceptions;

/**
 * Eccezione lanciata quando si è impossibilitati a chiedere una connessione SQL.
 */
public class ConnectionCloseException extends RuntimeException {
    public ConnectionCloseException(Exception e) {
        super(e);
    }

    public ConnectionCloseException(String msg, Exception e) {
        super(msg, e);
    }
}
