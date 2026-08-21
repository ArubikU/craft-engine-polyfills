package dev.arubik.craftengine.script;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global registry of loaded {@link ScriptProgram} instances.
 * Scripts are loaded from {@code <dataFolder>/scripts/*.pf}.
 */
public final class ScriptRegistry {

    private static final Map<String, ScriptProgram> scripts = new ConcurrentHashMap<>();

    private ScriptRegistry() {}

    public static void loadAll(File dataFolder) {
        scripts.clear();
        File scriptsDir = new File(dataFolder, "scripts");
        if (!scriptsDir.exists()) scriptsDir.mkdirs();
        File[] files = scriptsDir.listFiles(f -> f.getName().endsWith(".pf"));
        if (files == null) return;
        int count = 0;
        for (File f : files) {
            ScriptProgram script = ScriptProgram.load(f);
            String base = f.getName().replaceAll("\\.pf$", "");
            scripts.put(base, script);
            scripts.put(f.getName(), script);
            count++;
        }
        if (count > 0)
            java.util.logging.Logger.getLogger("CraftEnginePolyfills")
                .info("[CEPolyfills] Loaded " + count + " .pf script(s).");
    }

    /** How many .pf programs are currently loaded. */
    public static int size() { return scripts.size(); }

    public static ScriptProgram get(String name) { return scripts.get(name); }
    public static boolean has(String name) { return scripts.containsKey(name); }

    /** Register a script from string source (for testing or inline scripts). */
    public static void registerSource(String name, String source) {
        scripts.put(name, ScriptProgram.parse(name, source,
            java.util.logging.Logger.getLogger("CraftEnginePolyfills")));
    }
}
