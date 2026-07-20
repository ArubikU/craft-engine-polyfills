package dev.arubik.craftengine.chainery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.joml.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * Proves a chain survives the {@code chains.dat} round-trip (CHAINERY — "creo que sigue sin guardarse ...
 * realiza pruebas de save"): id, endpoints, attach faces, length, material AND its persistent NBT all come
 * back intact after save → load.
 */
class ChainRegistryTest {

    @Test
    @DisplayName("a chain (with its NBT data) survives save then load")
    void chainSurvivesSaveLoad() throws Exception {
        UUID id = UUID.randomUUID();
        UUID world = UUID.randomUUID();
        ChainMaterial mat = new ChainMaterial("cml:anchor", "minecraft:iron_chain", 32, 0.1, 55.0, 0.8);
        Chain chain = new Chain(id, world, new BlockPos(1, 2, 3), new BlockPos(9, 4, -7),
                new Vector3d(0, -0.5, 0), new Vector3d(0.5, 0, 0), mat, 6, new CompoundTag());
        chain.setString("zipline.mode", "ride");
        chain.setInt("uses", 42);
        chain.setDouble("speed", 1.25);
        ChainRegistry.register(chain);

        Path file = Files.createTempFile("chains-test", ".dat");
        try {
            ChainRegistry.saveAll(file);
            ChainRegistry.remove(id);
            assertNull(ChainRegistry.get(id), "precondition: removed before load");

            ChainRegistry.loadAll(file);
            Chain back = ChainRegistry.get(id);
            assertNotNull(back, "the chain must come back after load");
            assertEquals(new BlockPos(1, 2, 3), back.a);
            assertEquals(new BlockPos(9, 4, -7), back.b);
            assertEquals(new Vector3d(0, -0.5, 0), back.offsetA);
            assertEquals(new Vector3d(0.5, 0, 0), back.offsetB);
            assertEquals(6, back.blocks);
            assertEquals("minecraft:iron_chain", back.material.linkItem());
            assertEquals(32, back.material.maxBlocks());
            assertEquals("ride", back.getString("zipline.mode"), "persistent NBT string must survive");
            assertEquals(42, back.getInt("uses", 0), "persistent NBT int must survive");
            assertEquals(1.25, back.getDouble("speed", 0.0), 1.0e-9, "persistent NBT double must survive");
        } finally {
            ChainRegistry.remove(id);
            Files.deleteIfExists(file);
        }
    }
}
