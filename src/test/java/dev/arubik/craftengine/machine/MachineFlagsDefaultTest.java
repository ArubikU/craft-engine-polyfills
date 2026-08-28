package dev.arubik.craftengine.machine;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the reasoning behind {@link MachineFlags#DEFAULT}.
 *
 * <p>Four flags default to TRUE, and that is only safe because every shipped machine config
 * declares them explicitly — so the default is unreachable for the shipped set and changes nothing
 * about how those machines behave. It exists purely to keep a config that PREDATES the flags schema
 * working, which is the bug that motivated it: such a config silently lost its {@code action_script}
 * and all its renderers, with no error anywhere.
 *
 * <p>If someone later ships a machine that omits one of those four, the default stops being
 * invisible and starts being a behaviour decision made by accident. This test fails first.
 */
class MachineFlagsDefaultTest {

    /** True in the default — and therefore required to be explicit in every shipped config. */
    private static final List<String> DEFAULTED_ON = List.of("io_pull", "renderers", "scripts", "animations");

    /** False in the default, because a large share of shipped configs rely on that. */
    private static final List<String> DEFAULTED_OFF =
            List.of("recipes", "fuel", "continuous_fuel", "ui", "ui_tick", "kinetics", "redstone");

    @Test
    void theFourFlagsThatDefaultOnAreDeclaredByEveryShippedConfig() throws IOException {
        Path root = Path.of("src/main/resources/machines");
        if (!Files.isDirectory(root)) return;

        List<String> offenders = new ArrayList<>();
        int configs = 0;
        try (Stream<Path> files = Files.walk(root)) {
            for (Path f : files.filter(p -> p.toString().endsWith(".json")).toList()) {
                JsonElement el = JsonParser.parseString(Files.readString(f, StandardCharsets.UTF_8));
                if (!el.isJsonObject()) continue;
                configs++;
                JsonObject flags = el.getAsJsonObject().has("flags")
                        ? el.getAsJsonObject().getAsJsonObject("flags") : new JsonObject();
                for (String flag : DEFAULTED_ON) {
                    if (!flags.has(flag)) {
                        offenders.add(root.relativize(f) + " does not declare \"" + flag + "\"");
                    }
                }
            }
        }

        assertTrue(configs > 50, "expected the shipped machine configs, found " + configs);
        assertTrue(offenders.isEmpty(),
                "MachineFlags.DEFAULT turns these on, which is only invisible while every config\n"
                        + "declares them. These do not, so the default would now silently decide\n"
                        + "their behaviour — declare the flag, or move it out of DEFAULTED_ON and\n"
                        + "re-justify the default:\n  " + String.join("\n  ", offenders));
    }

    @Test
    void theDefaultMatchesWhatThisTestClaimsItIs() {
        MachineFlags d = MachineFlags.DEFAULT;
        assertEquals(List.of(true, true, true, true),
                List.of(d.ioPull(), d.renderers(), d.scripts(), d.animations()),
                "DEFAULTED_ON no longer matches MachineFlags.DEFAULT");
        assertEquals(List.of(false, false, false, false, false, false, false),
                List.of(d.recipes(), d.fuel(), d.continuousFuel(), d.ui(), d.uiTick(),
                        d.kinetics(), d.redstone()),
                "DEFAULTED_OFF no longer matches MachineFlags.DEFAULT — a config relying on the\n"
                        + "false default would change behaviour");
    }
}
