package dev.arubik.craftengine.script;

import dev.arubik.craftengine.machine.MachineRedstone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the redstone API down so the original defect cannot come back.
 *
 * <p>The bug: the script layer wrote the emitted power under {@code polyfills:redstone_output}
 * while {@code MachineBlockBehavior} read {@code polyfills:_redstone_power}. Both literals were
 * hand-written in different files, so nothing caught the mismatch and every script-driven redstone
 * emitter silently produced no signal. {@link MachineRedstone} now owns the key and both sides go
 * through it; these tests fail if anyone reintroduces a literal.
 */
class RedstoneApiTest {

    @Test
    @DisplayName("the storage key keeps its on-disk name")
    void storageKeyIsStable() {
        // TypedKey stores the PATH only, matching the historical NamespacedKey.getKey() layout,
        // so this string is what actually appears in saved block-entity NBT. Changing it would
        // silently drop the emitted power of every already-placed sensor in existing worlds.
        assertEquals("_redstone_power", MachineRedstone.POWER.nbtKey());
        assertEquals("polyfills:_redstone_power", MachineRedstone.POWER.fullKey());
    }

    @Test
    @DisplayName("power is clamped to the vanilla redstone range")
    void powerIsClamped() {
        assertEquals(0, MachineRedstone.clamp(-5));
        assertEquals(0, MachineRedstone.clamp(0));
        assertEquals(15, MachineRedstone.clamp(15));
        assertEquals(15, MachineRedstone.clamp(99));
        assertEquals(7, MachineRedstone.clamp(7));
    }

    @Test
    @DisplayName("no source file hand-writes a redstone storage key any more")
    void noStrayRedstoneKeyLiterals() throws Exception {
        Path owner = Path.of("src/main/java/dev/arubik/craftengine/machine/MachineRedstone.java");
        List<String> offenders = new ArrayList<>();
        try (Stream<Path> files = Files.walk(Path.of("src/main/java"))) {
            for (Path p : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                if (p.equals(owner)) continue;   // MachineRedstone is allowed to name it once
                String src = Files.readString(p);
                if (src.contains("\"_redstone_power\"") || src.contains("\"redstone_output\"")) {
                    offenders.add(p.toString());
                }
            }
        }
        assertTrue(offenders.isEmpty(),
                "redstone storage keys must only be named in MachineRedstone, found in: " + offenders);
    }

    @Test
    @DisplayName("every script that emits redstone uses the supported API")
    void scriptsUseTheSupportedApi() throws Exception {
        List<String> offenders = new ArrayList<>();
        try (Stream<Path> files = Files.list(Path.of("src/main/resources/scripts"))) {
            for (Path p : files.filter(f -> f.toString().endsWith(".pf")).toList()) {
                String src = Files.readString(p);
                for (String line : src.split("\n")) {
                    String t = line.trim();
                    if (t.startsWith("#")) continue;
                    if (t.contains("set_redstone") || t.contains("redstone_output")) {
                        offenders.add(p.getFileName() + ": " + t);
                    }
                }
            }
        }
        assertTrue(offenders.isEmpty(),
                "scripts must use Machine.redstone.set(n) or Machine.emit_redstone(n): " + offenders);
    }
}
