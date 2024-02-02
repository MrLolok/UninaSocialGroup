package org.unina.project.database.utils;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public final class ResultSetUtils {
    public static boolean hasColumn(@NotNull ResultSet set, @NotNull String column) throws SQLException {
        ResultSetMetaData metaData = set.getMetaData();
        int count = 1;
        boolean result = false;
        while (count <= metaData.getColumnCount() && !result)
            if (metaData.getColumnName(count++).equalsIgnoreCase(column))
                result = true;
        return result;
    }
}
