package dev.arubik.craftengine.machine.render.formula;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global registry of loaded {@link PolyScript} instances.
 *
 * <p>Call {@link #loadAll(File)} once during plugin enable (after saving default
 * script resources) to scan {@code plugins/CraftEnginePolyfill/scripts/*.pf}.
 * Scripts are indexed by their base name (without the {@code .pf} suffix) so a
 * renderer spec that says {@code "run": "crusher"} resolves to
 * {@code scripts/crusher.pf}.</p>
 */
public final class PolyScriptRegistry {

    private static final Map<String, PolyScript> scripts = new ConcurrentHashMap<>();

    private PolyScriptRegistry() {}

    /**
     * Clear the current registry and reload every {@code *.pf} file found in
     * {@code <dataFolder>/scripts/}.  The directory is created if absent.
     */
    public static void loadAll(File dataFolder) {
        scripts.clear();
        File scriptsDir = new File(dataFolder, "scripts");
        if (!scriptsDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            scriptsDir.mkdirs();
        }
        File[] files = scriptsDir.listFiles(f -> f.getName().endsWith(".pf"));
        if (files == null) return;
        int count = 0;
        for (File f : files) {
            PolyScript script = PolyScript.load(f);
            String base = f.getName().replaceAll("\\.pf$", "");
            scripts.put(base, script);          // lookup by base name, e.g. "crusher"
            scripts.put(f.getName(), script);   // also by full name,  e.g. "crusher.pf"
            count++;
        }
        if (count > 0)
            org.bukkit.Bukkit.getLogger().info("[CEPolyfills] Loaded " + count + " .pf script(s).");
    }

    /** Return the script registered under {@code name} (with or without {@code .pf}), or {@code null}. */
    public static PolyScript get(String name) { return scripts.get(name); }

    /** {@code true} if a script is registered under {@code name}. */
    public static boolean has(String name) { return scripts.containsKey(name); }
}
