package dev.arubik.craftengine.contraption.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.junit.jupiter.api.BeforeAll;

import dev.arubik.craftengine.contraption.ContraptionState;

/** Byte-blob and file round-trip for {@link ContraptionManifest}. */
class ContraptionManifestTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void byteRoundTripPreservesAllFields() throws Exception {
        UUID id = UUID.randomUUID();
        UUID world = UUID.randomUUID();
        ContraptionManifest original = new ContraptionManifest(id, world, 1.5, 64.0, -3.25, 0.7853981, 2.5, true, "abc.nbt");

        ContraptionManifest restored = ContraptionManifest.fromBytes(original.toBytes());

        assertEquals(original.id(), restored.id());
        assertEquals(original.worldId(), restored.worldId());
        assertEquals(original.x(), restored.x(), 1e-9);
        assertEquals(original.y(), restored.y(), 1e-9);
        assertEquals(original.z(), restored.z(), 1e-9);
        assertEquals(original.yawRadians(), restored.yawRadians(), 1e-9);
        assertEquals(original.scale(), restored.scale(), 1e-9);
        assertEquals(original.stalled(), restored.stalled());
        assertEquals(original.nbtFileName(), restored.nbtFileName());
    }

    @Test
    void fileRoundTrip(@TempDir Path tempDir) throws Exception {
        ContraptionManifest original = new ContraptionManifest(UUID.randomUUID(), UUID.randomUUID(),
                0, 64, 0, 0, 1.0, false, "miner_1.nbt");
        Path file = tempDir.resolve("test.mcdata");

        original.save(file);
        ContraptionManifest restored = ContraptionManifest.load(file);

        assertEquals(original, restored);
    }

    @Test
    void of_capturesStateFieldsExactly() throws Exception {
        ContraptionState state = new ContraptionState(UUID.randomUUID(), UUID.randomUUID(), null,
                10, 65, -2);
        state.setYawRadians(1.23);
        state.setStalled(true);

        ContraptionManifest manifest = ContraptionManifest.of(state, "x.nbt");

        assertEquals(state.id(), manifest.id());
        assertEquals(state.worldId(), manifest.worldId());
        assertEquals(10.0, manifest.x());
        assertEquals(65.0, manifest.y());
        assertEquals(-2.0, manifest.z());
        assertEquals(1.23, manifest.yawRadians());
        assertEquals(true, manifest.stalled());
        assertEquals("x.nbt", manifest.nbtFileName());
    }
}
