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

    // Set once by loadAll() — lets an "import" statement resolve a path relative to the scripts/
    // folder without needing that path threaded through ScriptContext/ScriptFormula's evaluation
    // call chain (import resolution is a load-time file-system concern, not a per-tick one).
    private static volatile File scriptsDir;

    private ScriptRegistry() {}

    /** Re-(loads) every {@code .pf} file in {@code <dataFolder>/scripts/}. Calls {@code
     *  __unload__()} on every currently-loaded script that has one (BEFORE the old programs are
     *  discarded — a script closing a connection or clearing a global still sees its own state),
     *  then {@code __init__()} on every newly-loaded script that has one (see each hook's own
     *  javadoc in warps.pf-style script conventions: {@code __init__} is the natural place for
     *  idempotent {@code CREATE TABLE IF NOT EXISTS}-style setup, {@code __unload__} for releasing
     *  whatever it acquired). Both run on the calling thread — this method itself already only
     *  ever runs from plugin enable or an explicit {@code /cep reload scripts}, never a hot path. */
    public static void loadAll(File dataFolder) {
        // scripts.values() holds each program TWICE (once per base-name key, once per full
        // filename key) — dedupe by identity so __unload__ doesn't fire twice per script.
        runLifecycleHook("__unload__", new java.util.LinkedHashSet<>(scripts.values()));
        scripts.clear();
        File scriptsDirLocal = new File(dataFolder, "scripts");
        if (!scriptsDirLocal.exists()) scriptsDirLocal.mkdirs();
        scriptsDir = scriptsDirLocal;
        java.util.List<File> files = new java.util.ArrayList<>();
        collectPfFiles(scriptsDirLocal, files);
        // File read + tokenize + parse is pure CPU/IO — no Bukkit/NMS call anywhere in
        // ScriptProgram.load/parse (imports resolve lazily later, at evaluate() time, not here) —
        // and every file is independent, so this is safe to parallelize even though loadAll() as a
        // whole still only ever runs from plugin-enable or an explicit reload, never a hot path.
        // ConcurrentHashMap.put from multiple threads is safe; the ordering-sensitive bits
        // (count, freshlyLoaded for __init__) are just collected after the parallel step joins.
        record Loaded(File file, ScriptProgram script) {}
        java.util.List<Loaded> loaded = files.parallelStream()
                .map(f -> new Loaded(f, ScriptProgram.load(f)))
                .toList();
        int count = 0;
        java.util.List<ScriptProgram> freshlyLoaded = new java.util.ArrayList<>();
        for (Loaded l : loaded) {
            File f = l.file();
            ScriptProgram script = l.script();
            // Keyed by bare base name (e.g. "saw") for any OLD-style reference still using one, but
            // machine JSON should now use the FULL path relative to scripts/ (e.g. "kinetics/saw" or
            // "kinetics/saw.pf:_saw_setup_rpm") now that scripts live under category subfolders —
            // bare names only stay collision-free by convention, not by anything enforced. Also
            // registered under the WITH-.pf relPath (e.g. "kinetics/saw.pf") for import "..." (see
            // getOrLoadByPath, which always appends ".pf"), and WITHOUT it (e.g. "kinetics/saw") —
            // ScriptCall.parse always strips ".pf" before calling ScriptRegistry.get(), so that's the
            // key an "on_place": "kinetics/saw.pf:_saw_setup_rpm"-style reference actually resolves
            // through, not the with-extension form.
            String base = f.getName().replaceAll("\\.pf$", "");
            String relPath = scriptsDirLocal.toPath().relativize(f.toPath()).toString().replace('\\', '/');
            String relPathNoExt = relPath.replaceAll("\\.pf$", "");
            scripts.put(base, script);
            scripts.put(f.getName(), script);
            scripts.put(relPath, script);
            scripts.put(relPathNoExt, script);
            freshlyLoaded.add(script);
            count++;
        }
        if (count > 0)
            java.util.logging.Logger.getLogger("CraftEnginePolyfills")
                .info("[CEPolyfills] Loaded " + count + " .pf script(s).");
        runLifecycleHook("__init__", freshlyLoaded);
    }

    /** Evaluates each program's top-level defs, then calls {@code funcName()} if that program
     *  declares it — used for both {@code __init__} and {@code __unload__}. A program without the
     *  hook is untouched (no defs re-evaluation cost beyond what {@link ScriptProgram#evaluate}
     *  already caches). Failures are logged and do not stop the remaining scripts' hooks running. */
    /** Fires every currently-loaded script's {@code __unload__()} without discarding/replacing
     *  anything — the plugin-disable counterpart of {@code loadAll}'s own reload-time unload pass,
     *  since a full server shutdown never calls {@code loadAll} again to trigger it otherwise. Call
     *  from {@code onDisable} BEFORE closing any shared resource (SQLDriver, RedisDriver, ...) a
     *  script's {@code __unload__} might still want to use for one last flush. */
    public static void unloadAll() {
        runLifecycleHook("__unload__", new java.util.LinkedHashSet<>(scripts.values()));
    }

    /** Re-loads exactly ONE {@code <dataFolder>/scripts/<name>.pf}, leaving every other loaded
     *  script untouched — {@code loadAll}'s targeted counterpart for a {@code /cep reload scripts
     *  <name>} that shouldn't re-run every OTHER script's {@code __init__}/{@code __unload__} (a
     *  real concern for one like warps.pf whose {@code __init__} issues several {@code CREATE
     *  TABLE} statements — pointless churn on the SQL connection for a reload that has nothing to
     *  do with it). Still fires __unload__ on the OLD instance then __init__ on the NEW one, same
     *  ordering as loadAll, just scoped to this one file. Returns false if the file doesn't exist. */
    public static boolean reloadOne(File dataFolder, String name) {
        String norm = name.replace('\\', '/');
        String relNoExt = norm.endsWith(".pf") ? norm.substring(0, norm.length() - 3) : norm;
        String base = relNoExt.contains("/") ? relNoExt.substring(relNoExt.lastIndexOf('/') + 1) : relNoExt;
        File scriptsDirLocal = new File(dataFolder, "scripts");
        // "name" may be a full path (e.g. "kinetics/saw") now that scripts live under category
        // subfolders, or a bare filename (old-style) — try it as a direct relative path first, then
        // fall back to searching the whole tree for a matching bare filename.
        File file = new File(scriptsDirLocal, relNoExt + ".pf");
        if (!file.isFile()) {
            java.util.List<File> all = new java.util.ArrayList<>();
            collectPfFiles(scriptsDirLocal, all);
            for (File f : all) {
                if (f.getName().equals(base + ".pf")) { file = f; break; }
            }
        }
        if (!file.isFile()) return false;
        ScriptProgram old = scripts.get(base);
        if (old == null) old = scripts.get(relNoExt);
        if (old != null) runLifecycleHook("__unload__", java.util.List.of(old));
        ScriptProgram fresh = ScriptProgram.load(file);
        String relPath = scriptsDirLocal.toPath().relativize(file.toPath()).toString().replace('\\', '/');
        String relPathNoExt = relPath.replaceAll("\\.pf$", "");
        scripts.put(base, fresh);
        scripts.put(base + ".pf", fresh);
        scripts.put(relPath, fresh);
        scripts.put(relPathNoExt, fresh);
        runLifecycleHook("__init__", java.util.List.of(fresh));
        return true;
    }

    /** Collects every {@code .pf} file under {@code dir}, recursively — subfolders (kinetics/,
     *  redstone/, utils/, ...) are purely organizational, never part of a script's registry key. */
    private static void collectPfFiles(File dir, java.util.List<File> out) {
        File[] entries = dir.listFiles();
        if (entries == null) return;
        for (File f : entries) {
            if (f.isDirectory()) collectPfFiles(f, out);
            else if (f.getName().endsWith(".pf")) out.add(f);
        }
    }

    private static void runLifecycleHook(String funcName, java.util.Collection<ScriptProgram> programs) {
        for (ScriptProgram program : programs) {
            try {
                ScriptContext ctx = program.evaluate(dev.arubik.craftengine.script.ScriptBootstrap.commonContext());
                ScriptValue fnVal = ctx.getVar(funcName);
                if (fnVal instanceof ScriptValue.Obj fnObj && fnObj.typeName().equals(UserFunction.TYPE)) {
                    ((UserFunction) fnObj.instance()).call(java.util.List.of(), ctx);
                }
            } catch (Throwable t) {
                java.util.logging.Logger.getLogger("CraftEnginePolyfills")
                    .log(java.util.logging.Level.WARNING, "[CEPolyfills] " + funcName + "() threw", t);
            }
        }
    }

    /** How many .pf programs are currently loaded. */
    public static int size() { return scripts.size(); }

    public static ScriptProgram get(String name) { return scripts.get(name); }
    public static boolean has(String name) { return scripts.containsKey(name); }

    /** Resolves an {@code import "path"} statement's path — relative to {@code <dataFolder>/scripts/}
     *  (subfolders allowed, "/"-separated; ".pf" suffix optional) — loading and caching the target
     *  file on first use. Unlike {@link #loadAll}, this key is the RELATIVE PATH itself (e.g.
     *  "utils/math.pf"), never the bare basename, so it can't collide with — or get clobbered by —
     *  the flat top-level basename keys loadAll() registers; a subsequent {@code loadAll}/{@code
     *  reloadOne} pass only re-scans the top-level scripts/ folder and won't refresh an imported
     *  subfolder file, so during active iteration on one, re-import (or restart) to pick up edits.
     *  Returns null for a blank path, a path that resolves outside scripts/ (rejected, not just
     *  ignored — imports are config-authored but there's no reason to let one escape via "../.."),
     *  or a file that doesn't exist. */
    public static ScriptProgram getOrLoadByPath(String relPath) {
        if (relPath == null || relPath.isBlank()) return null;
        String norm = relPath.trim().replace('\\', '/');
        while (norm.startsWith("/")) norm = norm.substring(1);
        if (!norm.endsWith(".pf")) norm = norm + ".pf";
        ScriptProgram cached = scripts.get(norm);
        if (cached != null) return cached;
        File dir = scriptsDir;
        if (dir == null) return null;
        File f = new File(dir, norm);
        try {
            if (!f.isFile()) return null;
            if (!f.getCanonicalFile().toPath().startsWith(dir.getCanonicalFile().toPath())) return null;
        } catch (java.io.IOException ignored) { return null; }
        ScriptProgram prog = ScriptProgram.load(f);
        scripts.put(norm, prog);
        return prog;
    }

    /** Register a script from string source (for testing or inline scripts). */
    public static void registerSource(String name, String source) {
        scripts.put(name, ScriptProgram.parse(name, source,
            java.util.logging.Logger.getLogger("CraftEnginePolyfills")));
    }
}
