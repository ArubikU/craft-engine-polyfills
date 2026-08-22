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
 * Guards the multiblock definitions.
 *
 * <p>Two things went unnoticed here for a long time, both silent:
 * <ul>
 *   <li>The {@code machine} block nested inside a mode still used the removed legacy keys. The
 *       machines/ folder was migrated to {@code flags} but these were missed, so a pressurizer
 *       well quietly stopped requiring fuel.</li>
 *   <li>{@code can_form} was parsed nowhere and read nowhere — {@code canFormAt} was a stub
 *       returning true — so a condition written in a JSON did absolutely nothing.</li>
 * </ul>
 */
class MultiblockConfigTest {

    private static final Path MULTIBLOCKS = Path.of("src/main/resources/multiblocks");

    /** Keys the machine loader no longer understands. */
    private static final Set<String> REMOVED_KEYS =
            Set.of("no_processing", "fuel_required", "open_ui", "continuous_fuel");

    private static final Set<String> KNOWN_FLAGS = Set.of(
            "recipes", "fuel", "continuous_fuel", "ui", "ui_tick", "kinetics",
            "io_pull", "renderers", "scripts", "animations", "redstone");

    private List<Path> files() throws IOException {
        try (Stream<Path> s = Files.list(MULTIBLOCKS)) {
            return s.filter(p -> p.toString().endsWith(".json")).toList();
        }
    }

    private static JsonObject read(Path p) throws IOException {
        return JsonParser.parseString(Files.readString(p)).getAsJsonObject();
    }

    /** Every mode's nested machine object, with the file name for reporting. */
    private Map<String, JsonObject> machines() throws IOException {
        Map<String, JsonObject> out = new LinkedHashMap<>();
        for (Path p : files()) {
            JsonElement modes = read(p).get("modes");
            if (modes == null || !modes.isJsonArray()) continue;
            int i = 0;
            for (JsonElement m : modes.getAsJsonArray()) {
                JsonElement machine = m.isJsonObject() ? m.getAsJsonObject().get("machine") : null;
                if (machine != null && machine.isJsonObject()) {
                    out.put(p.getFileName() + " mode[" + i + "]", machine.getAsJsonObject());
                }
                i++;
            }
        }
        return out;
    }

    @Test
    @DisplayName("there are multiblock definitions to check")
    void definitionsExist() throws IOException {
        assertFalse(files().isEmpty());
        assertFalse(machines().isEmpty(), "expected nested machine definitions");
    }

    @Test
    @DisplayName("no nested machine still uses a removed legacy key")
    void nestedMachinesAreMigrated() throws IOException {
        List<String> offenders = new ArrayList<>();
        for (Map.Entry<String, JsonObject> e : machines().entrySet()) {
            for (String k : REMOVED_KEYS) {
                if (e.getValue().has(k)) offenders.add(e.getKey() + " -> " + k);
            }
        }
        assertTrue(offenders.isEmpty(),
                "the machines/ folder was migrated but these were missed, so they silently take "
                        + "defaults now: " + offenders);
    }

    @Test
    @DisplayName("nested machine flags are well formed")
    void nestedFlagsAreValid() throws IOException {
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, JsonObject> e : machines().entrySet()) {
            JsonElement flags = e.getValue().get("flags");
            if (flags == null) continue;
            assertTrue(flags.isJsonObject(), e.getKey() + ": flags must be an object");
            for (Map.Entry<String, JsonElement> f : flags.getAsJsonObject().entrySet()) {
                if (!KNOWN_FLAGS.contains(f.getKey())) {
                    problems.add(e.getKey() + " -> unknown flag '" + f.getKey() + "'");
                } else if (!f.getValue().isJsonPrimitive()
                        || !f.getValue().getAsJsonPrimitive().isBoolean()) {
                    problems.add(e.getKey() + " -> '" + f.getKey() + "' is not a boolean");
                }
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    @DisplayName("the pressurizer well keeps burning fuel continuously")
    void pressurizerWellKeepsItsFuelBehaviour() throws IOException {
        JsonObject machine = machines().get("pressurizer_well.json mode[0]");
        assertNotNull(machine, "pressurizer_well must declare a machine");
        JsonObject flags = machine.getAsJsonObject("flags");
        assertNotNull(flags, "its fuel behaviour must survive the migration");
        assertTrue(flags.get("continuous_fuel").getAsBoolean(),
                "continuous_fuel was a legacy key here and must have become a flag");
        // fuel defaults to true, so the migration must NOT have spelled it out.
        assertFalse(flags.has("fuel") && !flags.get("fuel").getAsBoolean(),
                "the well still requires fuel");
        assertTrue(MachineFlags.DEFAULT.fuel(), "…which is the default");
    }

    @Test
    @DisplayName("a can_form condition is actually wired up now")
    void canFormIsWired() throws IOException {
        // It used to be inert: never parsed, and canFormAt was a stub returning true.
        String mode = Files.readString(
                Path.of("src/main/java/dev/arubik/craftengine/multiblock/MultiBlockDefinition.java"));
        assertTrue(mode.contains("canForm"), "Mode must carry the condition");

        String loader = Files.readString(
                Path.of("src/main/java/dev/arubik/craftengine/multiblock/MultiBlockLoader.java"));
        assertTrue(loader.contains("can_form"), "the loader must read it from the JSON");

        String behavior = Files.readString(
                Path.of("src/main/java/dev/arubik/craftengine/multiblock/MultiBlockBehavior.java"));
        assertTrue(behavior.contains("evaluateCanForm"), "canFormAt must actually evaluate it");
        assertFalse(behavior.contains("protected boolean canFormAt(Level level, BlockPos corePos) {\n        return true;\n    }"),
                "canFormAt must no longer be a stub that always allows forming");
    }

    @Test
    @DisplayName("every can_form expression only calls conditions that exist")
    void canFormExpressionsResolve() throws IOException {
        // A typo here would have been invisible before, and now refuses to form — so it is worth
        // catching at build time rather than in game.
        String conditions = Files.readString(
                Path.of("src/main/java/dev/arubik/craftengine/multiblock/FormConditionClass.java"));
        List<String> unknown = new ArrayList<>();
        for (Path p : files()) {
            JsonElement modes = read(p).get("modes");
            if (modes == null || !modes.isJsonArray()) continue;
            for (JsonElement m : modes.getAsJsonArray()) {
                if (!m.isJsonObject()) continue;
                JsonElement cf = m.getAsJsonObject().get("can_form");
                if (cf == null) continue;
                String expr = cf.getAsString();
                var matcher = java.util.regex.Pattern
                        .compile("World\\.([a-z_]+)\\s*\\(").matcher(expr);
                while (matcher.find()) {
                    String call = matcher.group(1);
                    if (!conditions.contains("\"" + call + "\"")) {
                        unknown.add(p.getFileName() + " -> World." + call + "()");
                    }
                }
            }
        }
        assertTrue(unknown.isEmpty(), "can_form calls conditions that do not exist: " + unknown);
    }

    @Test
    @DisplayName("the pressurizer well's gas-provider condition is preserved")
    void pressurizerWellKeepsItsCondition() throws IOException {
        JsonObject mode = read(MULTIBLOCKS.resolve("pressurizer_well.json"))
                .getAsJsonArray("modes").get(0).getAsJsonObject();
        assertTrue(mode.has("can_form"), "the well must still require a gas provider beneath it");
        assertTrue(mode.get("can_form").getAsString().contains("is_gas_provider"));
    }
}
