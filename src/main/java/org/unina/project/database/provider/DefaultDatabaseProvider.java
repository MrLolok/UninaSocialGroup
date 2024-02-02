package org.unina.project.database.provider;

import com.zaxxer.hikari.HikariDataSource;
import org.unina.project.config.defaults.ConfigDatabaseSettings;
import org.unina.project.database.builder.HikariConfigBuilder;
import org.unina.project.database.exceptions.HikariDataSourceCreationException;
import org.unina.project.database.operations.AsyncDatabase;
import org.unina.project.database.operations.Database;
import org.unina.project.database.operations.impl.DefaultAsyncDatabase;
import org.unina.project.database.operations.impl.DefaultDatabase;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;

public class DefaultDatabaseProvider implements DatabaseProvider {
    @Override @NotNull
    public Database newDatabase(@NotNull ConfigDatabaseSettings settings) {
        DataSource dataSource = newDataSource(settings);
        return newDatabase(dataSource);
    }

    @Override @NotNull
    public Database newDatabase(@NotNull DataSource dataSource) {
        return new DefaultDatabase(dataSource);
    }

    @Override @NotNull
    public AsyncDatabase newAsyncDatabase(@NotNull ConfigDatabaseSettings settings) {
        DataSource dataSource = newDataSource(settings);
        return newAsyncDatabase(dataSource);
    }

    @Override @NotNull
    public AsyncDatabase newAsyncDatabase(@NotNull DataSource dataSource) {
        return new DefaultAsyncDatabase(dataSource);
    }

    @Override @NotNull
    public AsyncDatabase newAsyncDatabase(@NotNull Database basic) {
        return new DefaultAsyncDatabase(basic);
    }

    @NotNull
    private DataSource newDataSource(@NotNull ConfigDatabaseSettings settings) {
        HikariConfigBuilder builder = new HikariConfigBuilder().setSettings(settings);
        try {
            return new HikariDataSource(builder.build());
        } catch (Exception e) {
            throw new HikariDataSourceCreationException("Impossibile creare un nuovo DataSource.", e);
        }
    }
}
