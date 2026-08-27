package dev.arubik.craftengine.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * The Java side of the {@code SQL.*} scripting API (see {@link
 * dev.arubik.craftengine.script.types.util.SQLDriverType}) — a single JDBC connection (or small
 * pool, for a real server backend) driven entirely by {@code database.yml} in the plugin's data
 * folder, so a script never touches JDBC directly.
 *
 * <p><b>Backends</b> ({@code type} in database.yml):
 * <ul>
 *   <li>{@code sqlite} (default) — one file under the plugin's data folder, self-contained, no
 *       external server. Uses ONE shared connection (sqlite-jdbc's own recommendation — SQLite
 *       doesn't handle concurrent writers well anyway) guarded by {@link #LOCK}.</li>
 *   <li>{@code mysql}/{@code mariadb}/{@code postgresql} — an external server, reached through a
 *       small hand-rolled connection pool (see {@link #pool}) sized by {@code pool-size}. The
 *       actual JDBC driver for these is NOT bundled (licensing + jar size) — it must already be on
 *       the server's classpath (very often true already via another plugin, or an admin can drop
 *       the driver jar under the server's {@code libraries/} folder). {@link #init()} reports a
 *       clear error rather than silently falling back if the driver class can't be found.</li>
 *   <li>{@code jdbc} — an escape hatch: a fully custom {@code jdbc-url} for any other JDBC-
 *       compliant database, same pooling as mysql/postgresql.</li>
 * </ul>
 *
 * <p>Every query method is SYNCHRONOUS (blocks the calling thread on real I/O) — exactly like this
 * project's other explicitly-risky-but-useful primitives (Bukkit's own blocking calls this codebase
 * otherwise avoids). Call from a command/cron/dialog-callback, not from a per-tick machine script.
 */
public final class SQLDriver {

    public enum Backend { SQLITE, MYSQL, MARIADB, POSTGRESQL, JDBC }

    private static final Object LOCK = new Object();
    private static volatile Backend backend = Backend.SQLITE;
    private static volatile Connection sqliteConnection;
    private static volatile BlockingQueue<Connection> pool;
    private static volatile String jdbcUrl;
    private static volatile String jdbcUser;
    private static volatile String jdbcPassword;
    private static volatile boolean ready = false;
    private static volatile String lastError = null;

    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
    // Dedicated pool for SQL.*_async — real I/O must never run on the server's main thread (a
    // single 50ms query would eat a whole tick). Sized modestly since typical usage is "a handful
    // of commands/dialogs firing queries", not a hot per-tick path (that stays synchronous-only by
    // policy, same as every other explicitly-blocking primitive in this codebase).
    private static final ExecutorService ASYNC_POOL = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "CEPolyfills-SQL-" + THREAD_COUNTER.incrementAndGet());
        t.setDaemon(true);
        return t;
    });

    private SQLDriver() {}

    public static boolean isReady() { return ready; }
    public static String lastError() { return lastError; }
    public static Backend backend() { return backend; }

    /** Reads (or creates, with commented defaults) {@code database.yml} and opens the configured
     *  backend. Safe to call again (e.g. on {@code /cep reload database}) — closes whatever was
     *  open first. Never throws; failures are recorded in {@link #lastError()} and {@link
     *  #isReady()} so a script's SQL calls degrade to a clear "not connected" result instead of an
     *  unhandled exception at plugin boot. */
    public static synchronized void init(File dataFolder, Logger logger) {
        close();
        lastError = null;
        File configFile = new File(dataFolder, "database.yml");
        if (!configFile.exists()) writeDefaultConfig(configFile, logger);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        String type = cfg.getString("type", "sqlite").toLowerCase(java.util.Locale.ROOT);
        try {
            switch (type) {
                case "sqlite" -> {
                    backend = Backend.SQLITE;
                    File dbFile = new File(dataFolder, cfg.getString("sqlite.file", "database.db"));
                    Class.forName("org.sqlite.JDBC");
                    sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
                    // SQLite disables foreign-key enforcement (including ON DELETE CASCADE) by
                    // default on every new connection — a script relying on a FK relationship
                    // (e.g. warps.pf's warp_bans/warp_favourites/... referencing warps(id)) would
                    // silently get NO cascade/constraint checking without this.
                    try (var st = sqliteConnection.createStatement()) {
                        st.execute("PRAGMA foreign_keys = ON");
                    }
                    ready = true;
                }
                case "mysql", "mariadb", "postgresql", "jdbc" -> {
                    backend = switch (type) {
                        case "mysql" -> Backend.MYSQL;
                        case "mariadb" -> Backend.MARIADB;
                        case "postgresql" -> Backend.POSTGRESQL;
                        default -> Backend.JDBC;
                    };
                    jdbcUrl = type.equals("jdbc")
                            ? cfg.getString("jdbc.url")
                            : buildStandardUrl(type, cfg);
                    jdbcUser = cfg.getString(type.equals("jdbc") ? "jdbc.username" : type + ".username", "root");
                    jdbcPassword = cfg.getString(type.equals("jdbc") ? "jdbc.password" : type + ".password", "");
                    String driverClass = cfg.getString(type.equals("jdbc") ? "jdbc.driver-class" : type + ".driver-class",
                            switch (type) {
                                case "mysql" -> "com.mysql.cj.jdbc.Driver";
                                case "mariadb" -> "org.mariadb.jdbc.Driver";
                                case "postgresql" -> "org.postgresql.Driver";
                                default -> "";
                            });
                    if (!driverClass.isBlank()) {
                        try {
                            Class.forName(driverClass);
                        } catch (ClassNotFoundException e) {
                            lastError = "JDBC driver class '" + driverClass + "' not found on the classpath — "
                                    + "this project does not bundle the " + type + " driver (licensing/size); "
                                    + "add it to the server (e.g. another plugin that already ships it, or drop "
                                    + "the driver jar under the server's libraries folder) and reload.";
                            logger.warning("[SQLDriver] " + lastError);
                            ready = false;
                            return;
                        }
                    }
                    int poolSize = Math.max(1, cfg.getInt(type.equals("jdbc") ? "jdbc.pool-size" : type + ".pool-size", 5));
                    pool = new ArrayBlockingQueue<>(poolSize);
                    for (int i = 0; i < poolSize; i++) pool.add(openPooledConnection());
                    ready = true;
                }
                default -> {
                    lastError = "Unknown database type '" + type + "' in database.yml (expected sqlite/mysql/mariadb/postgresql/jdbc)";
                    logger.warning("[SQLDriver] " + lastError);
                    ready = false;
                    return;
                }
            }
            logger.info("[SQLDriver] Connected (" + backend.name().toLowerCase(java.util.Locale.ROOT) + ")");
        } catch (Throwable t) {
            lastError = t.getClass().getSimpleName() + ": " + t.getMessage();
            logger.log(Level.WARNING, "[SQLDriver] Failed to connect", t);
            ready = false;
        }
    }

    private static String buildStandardUrl(String type, YamlConfiguration cfg) {
        String host = cfg.getString(type + ".host", "localhost");
        int port = cfg.getInt(type + ".port", type.equals("postgresql") ? 5432 : 3306);
        String database = cfg.getString(type + ".database", "craftengine_polyfills");
        String extra = cfg.getString(type + ".params", type.equals("postgresql") ? "" : "useSSL=false&autoReconnect=true");
        String scheme = switch (type) {
            case "mysql" -> "mysql";
            case "mariadb" -> "mariadb";
            case "postgresql" -> "postgresql";
            default -> type;
        };
        return "jdbc:" + scheme + "://" + host + ":" + port + "/" + database + (extra.isBlank() ? "" : "?" + extra);
    }

    private static Connection openPooledConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    private static void writeDefaultConfig(File file, Logger logger) {
        try {
            file.getParentFile().mkdirs();
            java.nio.file.Files.writeString(file.toPath(), """
                    # SQLDriver backend for the SQL.* scripting API — see SQLDriver's own javadoc for the
                    # full backend list. Defaults to a self-contained embedded SQLite database (no external
                    # server, no extra setup) so /warps and friends work out of the box; switch `type` to
                    # mysql/mariadb/postgresql/jdbc to point at a real external database instead.
                    type: sqlite

                    sqlite:
                      # Relative to this plugin's data folder.
                      file: database.db

                    mysql:
                      host: localhost
                      port: 3306
                      database: craftengine_polyfills
                      username: root
                      password: ""
                      pool-size: 5
                      params: "useSSL=false&autoReconnect=true"
                      # driver-class: com.mysql.cj.jdbc.Driver   # override only if you need a different driver

                    mariadb:
                      host: localhost
                      port: 3306
                      database: craftengine_polyfills
                      username: root
                      password: ""
                      pool-size: 5

                    postgresql:
                      host: localhost
                      port: 5432
                      database: craftengine_polyfills
                      username: postgres
                      password: ""
                      pool-size: 5

                    # Escape hatch for any other JDBC-compliant database not listed above.
                    jdbc:
                      url: ""
                      username: ""
                      password: ""
                      driver-class: ""
                      pool-size: 5

                    redis:
                      enabled: false
                      host: localhost
                      port: 6379
                      password: ""
                      database: 0
                    """);
        } catch (IOException e) {
            logger.log(Level.WARNING, "[SQLDriver] Could not write default database.yml", e);
        }
    }

    /** Closes whatever is currently open. Safe to call even if nothing is open. */
    public static synchronized void close() {
        ready = false;
        if (sqliteConnection != null) {
            try { sqliteConnection.close(); } catch (SQLException ignored) {}
            sqliteConnection = null;
        }
        if (pool != null) {
            Connection c;
            while ((c = pool.poll()) != null) {
                try { c.close(); } catch (SQLException ignored) {}
            }
            pool = null;
        }
    }

    /** Runs {@code action} with a live connection — the sqlite single connection (synchronized) or
     *  one borrowed from the pool (returned afterward, replaced if it died). */
    private static <T> T withConnection(SqlFunction<T> action) throws SQLException {
        if (backend == Backend.SQLITE) {
            synchronized (LOCK) {
                if (sqliteConnection == null) throw new SQLException("SQLDriver not connected");
                return action.apply(sqliteConnection);
            }
        }
        if (pool == null) throw new SQLException("SQLDriver not connected");
        Connection c;
        try {
            c = pool.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("interrupted waiting for a pooled connection", e);
        }
        if (c == null) throw new SQLException("timed out waiting for a pooled connection (pool exhausted)");
        try {
            if (!c.isValid(2)) {
                try { c.close(); } catch (SQLException ignored) {}
                c = openPooledConnection();
            }
            return action.apply(c);
        } finally {
            pool.offer(c);
        }
    }

    @FunctionalInterface
    private interface SqlFunction<T> {
        T apply(Connection c) throws SQLException;
    }

    /** {@code SELECT ...} — each row as an ordered {@code column -> value} map (raw JDBC objects:
     *  String/Long/Double/Boolean/byte[]/null — {@link
     *  dev.arubik.craftengine.script.types.util.SQLDriverType} converts these to ScriptValues). */
    public static List<Map<String, Object>> query(String sql, List<Object> params) throws SQLException {
        return withConnection(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int cols = meta.getColumnCount();
                    List<Map<String, Object>> out = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= cols; i++) row.put(meta.getColumnLabel(i), rs.getObject(i));
                        out.add(row);
                    }
                    return out;
                }
            }
        });
    }

    /** {@code INSERT}/{@code UPDATE}/{@code DELETE}/DDL — returns affected row count. */
    public static int execute(String sql, List<Object> params) throws SQLException {
        return withConnection(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                bind(ps, params);
                return ps.executeUpdate();
            }
        });
    }

    /** Like {@link #execute} but for an {@code INSERT} whose auto-generated key the caller wants
     *  back (e.g. a new row's id) — -1 if the driver reports none. */
    public static long executeReturningId(String sql, List<Object> params) throws SQLException {
        return withConnection(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                bind(ps, params);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getLong(1) : -1L;
                }
            }
        });
    }

    private static void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
    }

    // ---- Async variants — run on ASYNC_POOL, never the caller's thread. SQLDriverType's
    // *_async script methods schedule the callback back onto the MAIN thread afterward (via
    // Bukkit's scheduler) so a callback script can safely touch the world/players, matching how
    // Dialog callbacks and other async-completing primitives in this codebase already behave. ----

    public static CompletableFuture<List<Map<String, Object>>> queryAsync(String sql, List<Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return query(sql, params);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, ASYNC_POOL);
    }

    public static CompletableFuture<Integer> executeAsync(String sql, List<Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(sql, params);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, ASYNC_POOL);
    }

    /** Shuts the async pool down — called from plugin disable so no query thread outlives the
     *  plugin (and, via the {@code __unload__} script hook, before a live {@code /cep reload
     *  scripts} in case a script wants to close its OWN resources first). Does not affect {@link
     *  #close()}'s connection lifecycle — call both on full shutdown. */
    public static void shutdownAsyncPool() {
        ASYNC_POOL.shutdown();
        try {
            if (!ASYNC_POOL.awaitTermination(5, TimeUnit.SECONDS)) ASYNC_POOL.shutdownNow();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ASYNC_POOL.shutdownNow();
        }
    }
}
