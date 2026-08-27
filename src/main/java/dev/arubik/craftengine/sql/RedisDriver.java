package dev.arubik.craftengine.sql;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * The Java side of the {@code Redis.*} scripting API (see {@code RedisDriverType}) — a thin Jedis
 * pool driven by the SAME {@code database.yml}'s {@code redis:} block {@link SQLDriver} reads its
 * own config from, since both are "external persistence backend" concerns for the same feature.
 * Disabled by default ({@code redis.enabled: false}) — most servers don't need it and it's an
 * extra process to run, unlike SQLDriver's zero-setup SQLite default.
 */
public final class RedisDriver {

    private static volatile JedisPool pool;
    private static volatile boolean enabled = false;
    private static volatile boolean ready = false;
    private static volatile String lastError = null;

    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();
    private static final ExecutorService ASYNC_POOL = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "CEPolyfills-Redis-" + THREAD_COUNTER.incrementAndGet());
        t.setDaemon(true);
        return t;
    });

    private RedisDriver() {}

    public static boolean isEnabled() { return enabled; }
    public static boolean isReady() { return ready; }
    public static String lastError() { return lastError; }

    /** Reads {@code redis:} out of {@code database.yml} (created by {@link SQLDriver#init} if the
     *  file doesn't exist yet — call {@link SQLDriver#init} first). No-ops (leaves {@link
     *  #isReady()} false) when {@code redis.enabled} is false, which is the default. */
    public static synchronized void init(File dataFolder, Logger logger) {
        close();
        lastError = null;
        File configFile = new File(dataFolder, "database.yml");
        if (!configFile.exists()) {
            enabled = false;
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        enabled = cfg.getBoolean("redis.enabled", false);
        if (!enabled) return;
        try {
            String host = cfg.getString("redis.host", "localhost");
            int port = cfg.getInt("redis.port", 6379);
            String password = cfg.getString("redis.password", "");
            int database = cfg.getInt("redis.database", 0);
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(8);
            pool = password.isBlank()
                    ? new JedisPool(poolConfig, host, port, 2000, null, database)
                    : new JedisPool(poolConfig, host, port, 2000, password, database);
            try (var jedis = pool.getResource()) {
                jedis.ping();
            }
            ready = true;
            logger.info("[RedisDriver] Connected (" + host + ":" + port + ")");
        } catch (Throwable t) {
            lastError = t.getClass().getSimpleName() + ": " + t.getMessage();
            logger.log(Level.WARNING, "[RedisDriver] Failed to connect", t);
            ready = false;
        }
    }

    public static synchronized void close() {
        ready = false;
        if (pool != null) {
            try { pool.close(); } catch (Throwable ignored) {}
            pool = null;
        }
    }

    private static <T> T with(java.util.function.Function<redis.clients.jedis.Jedis, T> action) {
        if (pool == null) throw new IllegalStateException("RedisDriver not connected (redis.enabled is false, or connect failed — see lastError())");
        try (var jedis = pool.getResource()) {
            return action.apply(jedis);
        }
    }

    public static String get(String key) { return with(j -> j.get(key)); }
    public static void set(String key, String value) { with(j -> j.set(key, value)); }
    public static void setex(String key, long seconds, String value) { with(j -> j.setex(key, seconds, value)); }
    public static boolean del(String key) { return with(j -> j.del(key) > 0); }
    public static boolean exists(String key) { return with(j -> j.exists(key)); }
    public static boolean expire(String key, long seconds) { return with(j -> j.expire(key, seconds) == 1); }
    public static long incr(String key) { return with(j -> j.incr(key)); }
    public static long incrBy(String key, long amount) { return with(j -> j.incrBy(key, amount)); }
    public static long publish(String channel, String message) { return with(j -> j.publish(channel, message)); }

    public static CompletableFuture<String> getAsync(String key) {
        return CompletableFuture.supplyAsync(() -> get(key), ASYNC_POOL);
    }

    public static CompletableFuture<Void> setAsync(String key, String value) {
        return CompletableFuture.runAsync(() -> set(key, value), ASYNC_POOL);
    }

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
