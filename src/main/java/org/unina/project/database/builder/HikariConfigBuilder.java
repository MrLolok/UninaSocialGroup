package org.unina.project.database.builder;

import com.zaxxer.hikari.HikariConfig;
import org.unina.project.config.defaults.ConfigDatabaseSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Builder per la configurazione di HikariCP
 * per inizializzare una nuova connection pool
 */
public class HikariConfigBuilder {
    private final static String DEFAULT_DRIVER = "postgresql";
    private final static Map<String, String> DEFAULT_PROPS = new HashMap<>();

    static {
        DEFAULT_PROPS.put("useUnicode", "true");
        DEFAULT_PROPS.put("characterEncoding", "utf8");
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        DEFAULT_PROPS.put("allowPublicKeyRetrieval", "true");
        DEFAULT_PROPS.put("cachePrepStmts", "true");
        DEFAULT_PROPS.put("prepStmtCacheSize", "250");
        DEFAULT_PROPS.put("prepStmtCacheSqlLimit", "2048");
        DEFAULT_PROPS.put("useServerPrepStmts", "true");
        DEFAULT_PROPS.put("useLocalSessionState", "true");
        DEFAULT_PROPS.put("rewriteBatchedStatements", "true");
        DEFAULT_PROPS.put("cacheResultSetMetadata", "true");
        DEFAULT_PROPS.put("cacheServerConfiguration", "true");
        DEFAULT_PROPS.put("elideSetAutoCommits", "true");
        DEFAULT_PROPS.put("maintainTimeStats", "false");
        DEFAULT_PROPS.put("alwaysSendSetIsolation", "false");
        DEFAULT_PROPS.put("cacheCallableStmts", "true");
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        DEFAULT_PROPS.put("socketTimeout", String.valueOf(TimeUnit.MINUTES.toMillis(30)));
    }

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private final static int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private final static int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private final static long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30);
    private final static long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private final static long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10);

    private String url, driver, poolName;
    private String username, password;
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Metodo statico per creare una nuova istanza di questo builder
     * con le credenziali e proprietà di base del database preparate.
     * @param settings da impostare
     * @return nuova istanza di Hikari config builder
     */
    public static HikariConfigBuilder create(ConfigDatabaseSettings settings) {
        return new HikariConfigBuilder().setSettings(settings);
    }

    /**
     * Imposta il JDBC URL di questo hikari config.
     * {@link #setJdbcUrl(String, int, String, String)}
     * @param host a cui connettersi
     * @param port dell'host
     * @param database da gestire
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setJdbcUrl(String host, int port, String database) {
        return setJdbcUrl(host, port, database, DEFAULT_DRIVER);
    }

    /**
     * Imposta il JDBC URL di questo hikari config.
     * @param host a cui connettersi
     * @param port dell'host
     * @param database da gestire
     * @param driver da usare
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setJdbcUrl(String host, int port, String database, String driver) {
        this.url = String.format("jdbc:%s://%s:%d/%s", driver, host, port, database);
        return this;
    }

    /**
     * Imposta il nome della classe del driver da utilizzare.
     * @param className del driver
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setDriverClassName(String className) {
        this.driver = className;
        return this;
    }

    /**
     * Imposta l'username da utilizzare per accedere al database.
     * @param username della configurazione
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Imposta la password da utilizzare per accedere al database.
     * @param password della configurazione
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Imposta il nome della pool che HikariCP creerà.
     * @param name della pool
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setPoolName(String name) {
        this.poolName = name;
        return this;
    }

    /**
     * Aggiungi una nuova proprietà a questo builder.
     * @param property nome
     * @param value della proprietà
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder addProperty(String property, Object value) {
        properties.putIfAbsent(property, value);
        return this;
    }

    /**
     * Imposta le credenziali di accesso e le proprietà di default per questo builder.
     * @param settings da impostare
     * @return questo HikariConfigBuilder
     */
    public HikariConfigBuilder setSettings(ConfigDatabaseSettings settings) {
        setJdbcUrl(settings.hostname(), settings.port(), settings.database());
        setUsername(settings.username()).setPassword(settings.password());
        setDriverClassName(settings.driver()).setPoolName("UninaSocialGroup");
        for (Map.Entry<String, Object> entry : settings.options().entrySet())
            addProperty(entry.getKey(), entry.getValue());
        return this;
    }

    /**
     * Crea una nuova istanza dell'{@link HikariConfig} partendo da questo builder.
     * @return nuova istanza di HikariConfig
     */
    public HikariConfig build() {
        if (url == null)
            throw new IllegalStateException("Building an HikariConfig without a JDBC URL");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);

        if (driver != null)
            config.setDriverClassName(driver);
        if (poolName != null)
            config.setPoolName(poolName);

        config.setUsername(username);
        config.setPassword(password);
        properties.forEach(config::addDataSourceProperty);

        DEFAULT_PROPS.forEach(config::addDataSourceProperty);
        config.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        config.setMinimumIdle(MINIMUM_IDLE);
        config.setMaxLifetime(MAX_LIFETIME);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);
        return config;
    }
}
