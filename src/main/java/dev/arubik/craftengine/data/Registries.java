package dev.arubik.craftengine.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import dev.arubik.craftengine.CraftEnginePolyfills;

/**
 * Central owner of every {@link Registry} in the plugin and of the load-phase
 * lifecycle around them.
 *
 * <p>
 * The contract is deliberately simple:
 * <ol>
 * <li>Domains declare a registry once, statically, via {@link #create(String)}.</li>
 * <li>Domains declare a loader via {@link #addLoader}, tagged with a phase so
 * that types load before the definitions that reference them, which load before
 * the recipes that reference <em>those</em>.</li>
 * <li>{@link #reload()} thaws every registry, clears it, runs the loaders in
 * phase order, then freezes everything again.</li>
 * </ol>
 *
 * <p>
 * Outside of {@link #reload()} the registries are frozen, so anything that
 * reaches for {@code register(...)} at runtime fails loudly instead of silently
 * producing state that would vanish on the next reload.
 */
public final class Registries {

    private Registries() {
    }

    /** Raw types: fluids, gases, pipe kinds — nothing may reference anything else. */
    public static final int PHASE_TYPES = 0;
    /** Definitions built on top of types: machines, multiblocks, menus, upgrades. */
    public static final int PHASE_DEFINITIONS = 100;
    /** Recipes and fuels, which reference both of the above. */
    public static final int PHASE_RECIPES = 200;
    /** Anything that needs the fully populated world, e.g. validation passes. */
    public static final int PHASE_POST = 300;

    /** A named unit of loading work, run in {@code phase} order inside {@link #reload()}. */
    public record Loader(String name, int phase, Runnable action) {
    }

    private static final Map<String, Registry<?>> ALL = new LinkedHashMap<>();
    private static final List<Loader> LOADERS = new ArrayList<>();
    private static volatile boolean frozen;
    private static volatile boolean everLoaded;

    /**
     * Declares a registry. Call this exactly once per domain, from a static
     * initializer, and keep the returned handle in a {@code public static final}
     * field — that field is the public API other plugins read through.
     */
    public static synchronized <T> Registry<T> create(String name) {
        return Registries.<T>create(name, true);
    }

    /**
     * Declares a registry, choosing whether {@link #reload()} empties it.
     *
     * @param clearOnReload {@code false} for identity registries (fluid types, gas
     *                      types, pipe kinds) whose entries are referenced by live
     *                      block entities and save data — see
     *                      {@link Registry#clearsOnReload()}
     */
    public static synchronized <T> Registry<T> create(String name, boolean clearOnReload) {
        if (ALL.containsKey(name))
            throw new IllegalStateException("Registry '" + name + "' already exists");
        Registry<T> registry = new Registry<>(name, clearOnReload);
        ALL.put(name, registry);
        return registry;
    }

    /**
     * Registers a loader to run during {@link #reload()}.
     *
     * @param phase one of the {@code PHASE_*} constants; ties break on
     *              registration order
     */
    public static synchronized void addLoader(String name, int phase, Runnable action) {
        LOADERS.add(new Loader(name, phase, action));
    }

    /**
     * Runs a full load cycle: thaw, clear, load, freeze.
     *
     * <p>
     * A loader that throws is logged and skipped so one bad data file cannot take
     * the whole plugin down; the registries still end up frozen.
     */
    public static synchronized void reload() {
        for (Registry<?> registry : new ArrayList<>(ALL.values())) {
            registry.thaw();
            // Identity registries keep their entries across a reload; their loaders
            // overwrite each entry's properties in place instead (see
            // Registry#clearsOnReload).
            if (registry.clearsOnReload())
                registry.clear();
        }
        frozen = false;

        List<Loader> ordered = new ArrayList<>(LOADERS);
        ordered.sort(java.util.Comparator.comparingInt(Loader::phase));
        for (Loader loader : ordered) {
            try {
                loader.action().run();
            } catch (Throwable t) {
                log(Level.SEVERE, "Data loader '" + loader.name() + "' failed", t);
            }
        }

        freezeAll();
        everLoaded = true;
        log(Level.INFO, summary(), null);
    }

    /**
     * Re-runs only the named loaders, overwriting their entries in place.
     *
     * <p>Unlike {@link #reload()} nothing is cleared first: entries are replaced by key as each
     * loader re-registers them, so the registries this loader does not touch keep their contents.
     * That makes a targeted reload safe to run while the server is live — the cost is that an
     * entry deleted from disk lingers until a full reload or restart.
     *
     * <p>Registries are thawed for the duration and refrozen afterwards if they were frozen.
     *
     * @return the number of loaders that actually ran
     */
    public static synchronized int reloadLoaders(String... names) {
        java.util.Set<String> wanted = java.util.Set.of(names);
        List<Loader> ordered = LOADERS.stream()
                .filter(l -> wanted.contains(l.name()))
                .sorted(java.util.Comparator.comparingInt(Loader::phase))
                .collect(java.util.stream.Collectors.toList());
        if (ordered.isEmpty()) return 0;

        boolean wasFrozen = frozen;
        for (Registry<?> registry : ALL.values())
            registry.thaw();
        frozen = false;

        int ran = 0;
        for (Loader loader : ordered) {
            try {
                loader.action().run();
                ran++;
            } catch (Throwable t) {
                log(Level.SEVERE, "Data loader '" + loader.name() + "' failed", t);
            }
        }

        if (wasFrozen) freezeAll();
        return ran;
    }

    /** Names of every registered loader, for command tab-completion and diagnostics. */
    public static synchronized List<String> loaderNames() {
        return LOADERS.stream().map(Loader::name).sorted().collect(java.util.stream.Collectors.toList());
    }

    /** Seals every registry. Idempotent. */
    public static synchronized void freezeAll() {
        for (Registry<?> registry : ALL.values())
            registry.freeze();
        frozen = true;
    }

    /** {@code true} once the load phase has closed. */
    public static boolean isFrozen() {
        return frozen;
    }

    /** {@code true} once {@link #reload()} has completed at least once. */
    public static boolean isLoaded() {
        return everLoaded;
    }

    /** Every declared registry, in declaration order. */
    public static synchronized Collection<Registry<?>> all() {
        return Collections.unmodifiableCollection(new ArrayList<>(ALL.values()));
    }

    public static synchronized Registry<?> byName(String name) {
        return ALL.get(name);
    }

    /** Declared loaders, in phase order — useful for a debug dump. */
    public static synchronized List<Loader> loaders() {
        List<Loader> ordered = new ArrayList<>(LOADERS);
        ordered.sort(java.util.Comparator.comparingInt(Loader::phase));
        return ordered;
    }

    private static String summary() {
        StringBuilder sb = new StringBuilder("Data registries frozen:");
        for (Registry<?> registry : ALL.values())
            sb.append(' ').append(registry.name()).append('=').append(registry.size());
        return sb.toString();
    }

    private static void log(Level level, String message, Throwable t) {
        CraftEnginePolyfills plugin = CraftEnginePolyfills.instance();
        if (plugin == null) {
            if (t != null)
                t.printStackTrace();
            return;
        }
        if (t != null)
            plugin.getLogger().log(level, message, t);
        else
            plugin.getLogger().log(level, message);
    }
}
