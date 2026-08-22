package dev.arubik.craftengine.script;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Every data loader has to be bootstrapped from the plugin's enable, or it silently never runs.
 *
 * <p>{@code HammerLoader} was not, so {@code HammerItems} stayed empty, {@code isHammer()} answered
 * false for every item, and the hammer listener returned on its first line — no multiblock could be
 * assembled at all, with nothing in the log to say why. A loader that is declared but never
 * bootstrapped produces no error anywhere; this test is the only thing that would notice.
 */
class LoaderBootstrapTest {

    private static final Path MAIN = Path.of("src/main/java/dev/arubik/craftengine");
    private static final Path PLUGIN = MAIN.resolve("CraftEnginePolyfills.java");

    /** Classes that expose a bootstrap() registering a data loader. */
    private List<Path> loaderClasses() throws IOException {
        List<Path> out = new ArrayList<>();
        try (Stream<Path> files = Files.walk(MAIN)) {
            for (Path p : files.filter(f -> f.toString().endsWith("Loader.java")).toList()) {
                String src = Files.readString(p);
                if (src.contains("Registries.addLoader(")) out.add(p);
            }
        }
        return out;
    }

    private static String className(Path p) {
        String n = p.getFileName().toString();
        return n.substring(0, n.length() - ".java".length());
    }

    @Test
    @DisplayName("there are loaders to check")
    void loadersExist() throws IOException {
        assertFalse(loaderClasses().isEmpty(), "expected the data loaders to be present");
    }

    @Test
    @DisplayName("every loader that declares one is bootstrapped from the plugin")
    void everyLoaderIsBootstrapped() throws IOException {
        String plugin = Files.readString(PLUGIN);
        List<String> missing = new ArrayList<>();
        for (Path p : loaderClasses()) {
            String name = className(p);
            if (!plugin.contains(name + ".bootstrap()")) missing.add(name);
        }
        assertTrue(missing.isEmpty(),
                "these register a data loader but are never bootstrapped, so they never run: " + missing);
    }

    @Test
    @DisplayName("the loader names a targeted reload can use are all real")
    void reloadNamesResolve() throws IOException {
        // /cep reload machines names loaders explicitly; a typo there would silently reload nothing.
        String cmd = Files.readString(MAIN.resolve("CepCommand.java"));
        Matcher m = Pattern.compile("reloadLoaders\\(([^)]*)\\)", Pattern.DOTALL).matcher(cmd);

        List<String> declared = new ArrayList<>();
        for (Path p : loaderClasses()) {
            Matcher d = Pattern.compile("addLoader\\(\\s*\"([^\"]+)\"").matcher(Files.readString(p));
            while (d.find()) declared.add(d.group(1));
        }
        // Loaders registered straight from the plugin rather than a *Loader class.
        Matcher inline = Pattern.compile("addLoader\\(\\s*\"([^\"]+)\"").matcher(Files.readString(PLUGIN));
        while (inline.find()) declared.add(inline.group(1));

        List<String> unknown = new ArrayList<>();
        while (m.find()) {
            Matcher names = Pattern.compile("\"([^\"]+)\"").matcher(m.group(1));
            while (names.find()) {
                if (!declared.contains(names.group(1))) unknown.add(names.group(1));
            }
        }
        assertTrue(unknown.isEmpty(),
                "reload names no such loader: " + unknown + " (declared: " + declared + ")");
    }

    @Test
    @DisplayName("the hammer loader specifically is wired — multiblocks cannot be built without it")
    void hammerLoaderIsWired() throws IOException {
        assertTrue(Files.readString(PLUGIN).contains("HammerLoader.bootstrap()"),
                "no hammer is recognised without this, so no multiblock can ever be assembled");
    }

    @Test
    @DisplayName("every startSystem() is actually started from the plugin")
    void everySystemIsStarted() throws IOException {
        // The same trap as an unbootstrapped loader, and just as silent: GlueItemBehavior declared
        // startSystem(plugin) — scheduling its preview loop and registering its listener — and
        // nothing ever called it, so the glue overlay could not appear however correct its
        // renderer was. A class that guards itself with a `systemStarted` flag is opting into
        // exactly this failure mode if nobody starts it.
        String plugin = Files.readString(PLUGIN);
        List<String> missing = new ArrayList<>();
        try (Stream<Path> files = Files.walk(MAIN)) {
            for (Path p : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                if (p.equals(PLUGIN)) continue;
                String src = Files.readString(p);
                if (!src.contains("public static void startSystem(")) continue;
                String name = className(p);
                if (!plugin.contains(name + ".startSystem(")) missing.add(name);
            }
        }
        assertTrue(missing.isEmpty(),
                "these declare startSystem() but are never started, so they do nothing: " + missing);
    }

    @Test
    @DisplayName("the glue preview specifically is started")
    void gluePreviewIsStarted() throws IOException {
        assertTrue(Files.readString(PLUGIN).contains("GlueItemBehavior.startSystem("),
                "without this there is no way to see what super glue has attached");
    }
}
