package org.unina.project.database.exceptions;

import java.sql.SQLException;

/**
 * Eccezione lanciata quando si è impossibilitati ad estrarre dati da un {@link java.sql.ResultSet}.
 */
public class ResultSetExtractionException extends RuntimeException {
    public ResultSetExtractionException(SQLException e) {
        super(e);
    }

    public ResultSetExtractionException(String msg, SQLException e) {
        super(msg, e);
    }
}
