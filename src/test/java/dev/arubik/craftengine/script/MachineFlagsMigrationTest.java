package dev.arubik.craftengine.script;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.arubik.craftengine.machine.MachineFlags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Guards the machine-definition {@code flags} model.
 *
 * <p>The legacy top-level keys were removed from the loader, so any machine JSON still carrying one
 * would silently fall back to defaults — e.g. a sensor would start running the recipe pipeline
 * again. These tests make that a build failure rather than an in-game mystery.
 */
class MachineFlagsMigrationTest {

    private static final Path MACHINES = Path.of("src/main/resources/machines");

    /** Keys the loader no longer understands. Their presence means an unmigrated file. */
    private static final Set<String> REMOVED_KEYS =
            Set.of("no_processing", "fuel_required", "open_ui", "continuous_fuel");

    /** Every key {@code MachineDefinitionLoader.parseFlags} reads. */
    private static final Set<String> KNOWN_FLAGS = Set.of(
            "recipes", "fuel", "continuous_fuel", "ui", "ui_tick", "kinetics",
            "io_pull", "renderers", "scripts", "animations", "redstone");

    private static Stream<Path> machineFiles() throws IOException {
        try (var s = Files.list(MACHINES)) {
            return s.filter(p -> p.toString().endsWith(".json")).toList().stream();
        }
    }

    private static JsonObject read(Path p) throws IOException {
        return JsonParser.parseString(Files.readString(p)).getAsJsonObject();
    }

    @Test
    @DisplayName("there are machine definitions to check")
    void machinesExist() throws IOException {
        assertTrue(machineFiles().count() > 40, "expected the shipped machine JSONs to be present");
    }

    @Test
    @DisplayName("no machine JSON still uses a removed legacy key")
    void noLegacyKeysRemain() throws IOException {
        List<String> offenders = new ArrayList<>();
        for (Path p : machineFiles().toList()) {
            JsonObject o = read(p);
            for (String k : REMOVED_KEYS) {
                if (o.has(k)) offenders.add(p.getFileName() + " -> " + k);
            }
        }
        assertTrue(offenders.isEmpty(),
                "these files were not migrated to \"flags\" and would silently take defaults: " + offenders);
    }

    @Test
    @DisplayName("every flags entry is a known key with a boolean value")
    void flagsAreWellFormed() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Path p : machineFiles().toList()) {
            JsonElement flags = read(p).get("flags");
            if (flags == null) continue;
            assertTrue(flags.isJsonObject(), p.getFileName() + ": \"flags\" must be an object");
            for (Map.Entry<String, JsonElement> e : flags.getAsJsonObject().entrySet()) {
                if (!KNOWN_FLAGS.contains(e.getKey())) {
                    problems.add(p.getFileName() + " -> unknown flag '" + e.getKey() + "'");
                } else if (!e.getValue().isJsonPrimitive() || !e.getValue().getAsJsonPrimitive().isBoolean()) {
                    problems.add(p.getFileName() + " -> '" + e.getKey() + "' is not a boolean");
                }
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    @DisplayName("flags only ever spell out non-default values")
    void flagsOmitDefaults() throws IOException {
        // Keeps the files readable: a flag equal to the default is noise.
        Map<String, Boolean> defaults = Map.of(
                "recipes", MachineFlags.DEFAULT.recipes(),
                "fuel", MachineFlags.DEFAULT.fuel(),
                "continuous_fuel", MachineFlags.DEFAULT.continuousFuel(),
                "ui", MachineFlags.DEFAULT.ui(),
                "ui_tick", MachineFlags.DEFAULT.uiTick(),
                "io_pull", MachineFlags.DEFAULT.ioPull(),
                "renderers", MachineFlags.DEFAULT.renderers(),
                "scripts", MachineFlags.DEFAULT.scripts(),
                "animations", MachineFlags.DEFAULT.animations(),
                "redstone", MachineFlags.DEFAULT.redstone());

        List<String> redundant = new ArrayList<>();
        for (Path p : machineFiles().toList()) {
            JsonElement flags = read(p).get("flags");
            if (flags == null) continue;
            for (Map.Entry<String, JsonElement> e : flags.getAsJsonObject().entrySet()) {
                Boolean def = defaults.get(e.getKey());
                // "kinetics" is excluded: its default is derived from power.consumes_stress,
                // so an explicit value is meaningful even when it matches MachineFlags.DEFAULT.
                if (def != null && def == e.getValue().getAsBoolean()) {
                    redundant.add(p.getFileName() + " -> " + e.getKey() + "=" + def);
                }
            }
        }
        assertTrue(redundant.isEmpty(), "redundant default-valued flags: " + redundant);
    }

    @Test
    @DisplayName("a machine that declares io.rpm also enables kinetics")
    void rpmMachinesAreKinetic() throws IOException {
        List<String> offenders = new ArrayList<>();
        for (Path p : machineFiles().toList()) {
            JsonObject o = read(p);
            JsonElement ioEl = o.get("io");
            boolean declaresRpm = ioEl != null && ioEl.isJsonObject() && ioEl.getAsJsonObject().has("rpm");
            if (!declaresRpm) continue;
            JsonElement flags = o.get("flags");
            boolean kinetic = flags != null && flags.isJsonObject()
                    && flags.getAsJsonObject().has("kinetics")
                    && flags.getAsJsonObject().get("kinetics").getAsBoolean();
            if (!kinetic) offenders.add(p.getFileName().toString());
        }
        assertTrue(offenders.isEmpty(),
                "these declare io.rpm but would never join the RPM network: " + offenders);
    }

    @Test
    @DisplayName("gas_motor_mk1 is a recipe-less kinetic source with a live UI")
    void gasMotorFlags() throws IOException {
        JsonObject flags = read(MACHINES.resolve("gas_motor_mk1.json")).getAsJsonObject("flags");
        assertNotNull(flags, "gas_motor_mk1 must declare flags");
        assertFalse(flags.get("recipes").getAsBoolean(), "the motor has no recipes");
        assertTrue(flags.get("kinetics").getAsBoolean(), "the motor drives the RPM network");
        // ui / ui_tick must stay at their true defaults: the frozen-GUI bug was exactly this.
        assertFalse(flags.has("ui") && !flags.get("ui").getAsBoolean(), "the motor's GUI must open");
        assertFalse(flags.has("ui_tick") && !flags.get("ui_tick").getAsBoolean(),
                "the motor's GUI must refresh, or the gas bar and RPM/SU buttons look dead again");
    }

    @Test
    @DisplayName("xp_collector no longer runs the recipe pipeline")
    void xpCollectorHasNoRecipes() throws IOException {
        JsonObject o = read(MACHINES.resolve("xp_collector.json"));
        assertFalse(o.getAsJsonObject("flags").get("recipes").getAsBoolean(),
                "xp_collector has no recipes registered, so the pipeline is pure overhead");

        String layout = o.getAsJsonArray("pages").get(0).getAsJsonObject().get("layout").toString();
        assertFalse(layout.contains("recipe_info.pf"),
                "recipe_info.pf is the sole source of the 'No matching recipe' lore line");
    }
}
