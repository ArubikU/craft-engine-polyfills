package dev.arubik.craftengine.data;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dev.arubik.craftengine.CraftEnginePolyfills;

/**
 * Shared plumbing for every JSON-backed data directory under the plugin's data
 * folder.
 *
 * <p>
 * Replaces the copy-pasted "mkdirs, seed from jar, walk recursively, parse,
 * catch per file" blocks that each loader used to carry. Two behavioural
 * differences from those:
 * <ul>
 * <li><b>Per-file seeding.</b> The old code only seeded when the whole directory
 * was missing, so JSON files added by a plugin update never reached existing
 * servers. Seeding is now tracked per file in a {@code .seeded} manifest: a
 * bundled file is copied out once, and a file the owner later deletes stays
 * deleted.</li>
 * <li><b>Errors name the file.</b> A parse failure is reported with the file
 * path and (via {@link JsonView}) the offending field, and only that file is
 * skipped.</li>
 * </ul>
 */
public final class DataFiles {

    private DataFiles() {
    }

    private static final Gson GSON = new Gson();
    private static final String MANIFEST = ".seeded";

    /**
     * Loads every {@code *.json} under {@code <dataFolder>/dir}, recursively,
     * seeding bundled defaults first.
     *
     * @param dir      directory name relative to the plugin data folder, e.g.
     *                 {@code "recipes"}
     * @param consumer receives each parsed file as {@code (view, fileName)}; the
     *                 view's path is already the file name, so nested errors read
     *                 like {@code "crusher.json > outputs[1] > id"}
     * @return how many files parsed successfully
     */
    public static int loadDirectory(String dir, BiConsumer<JsonView, String> consumer) {
        File root = new File(CraftEnginePolyfills.instance().getDataFolder(), dir);
        seed(dir, root);
        if (!root.isDirectory())
            return 0;
        List<File> files = new ArrayList<>();
        collect(root, files);
        int loaded = 0;
        for (File file : files) {
            String name = relativize(root, file);
            try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json == null)
                    throw new JsonView.MalformedDataException(name + ": file is empty");
                consumer.accept(JsonView.of(json, name), name);
                loaded++;
            } catch (JsonView.MalformedDataException e) {
                // Data-authoring mistake: the message already pinpoints the field, and a
                // stack trace into Gson would only bury it.
                warn(dir + "/" + name + " skipped — " + e.getMessage());
            } catch (Exception e) {
                CraftEnginePolyfills.instance().getLogger()
                        .log(Level.SEVERE, "Failed to load " + dir + "/" + name, e);
            }
        }
        return loaded;
    }

    /**
     * Copies bundled {@code dir/*.json} resources that this server has never been
     * given before, recording them in {@code <dir>/.seeded}.
     */
    private static void seed(String dir, File root) {
        CraftEnginePolyfills plugin = CraftEnginePolyfills.instance();
        List<String> bundled = plugin.listBundledResources(dir, ".json");
        if (bundled.isEmpty())
            return;
        if (!root.isDirectory() && !root.mkdirs()) {
            warn("Could not create data directory " + root);
            return;
        }
        File manifest = new File(root, MANIFEST);
        Set<String> alreadySeeded = readManifest(manifest);
        Set<String> updated = new LinkedHashSet<>(alreadySeeded);
        for (String resource : bundled) {
            if (!updated.add(resource))
                continue;
            plugin.saveDefaultResource(resource);
        }
        if (updated.size() != alreadySeeded.size())
            writeManifest(manifest, updated);
    }

    private static Set<String> readManifest(File manifest) {
        Set<String> out = new LinkedHashSet<>();
        if (!manifest.isFile())
            return out;
        try {
            for (String line : Files.readAllLines(manifest.toPath(), StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#"))
                    out.add(trimmed);
            }
        } catch (IOException e) {
            warn("Could not read " + manifest + ": " + e.getMessage());
        }
        return out;
    }

    private static void writeManifest(File manifest, Set<String> entries) {
        List<String> lines = new ArrayList<>();
        lines.add("# Bundled data files already copied to this server. Delete a line to have");
        lines.add("# that file restored on the next reload; delete this file to restore all.");
        lines.addAll(entries);
        try {
            Files.write(manifest.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            warn("Could not write " + manifest + ": " + e.getMessage());
        }
    }

    private static void collect(File directory, List<File> out) {
        File[] children = directory.listFiles();
        if (children == null)
            return;
        for (File child : children) {
            if (child.isDirectory())
                collect(child, out);
            else if (child.getName().endsWith(".json"))
                out.add(child);
        }
    }

    private static String relativize(File root, File file) {
        String rootPath = root.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        return filePath.startsWith(rootPath)
                ? filePath.substring(rootPath.length() + 1).replace(File.separatorChar, '/')
                : file.getName();
    }

    private static void warn(String message) {
        CraftEnginePolyfills.instance().getLogger().warning(message);
    }
}
