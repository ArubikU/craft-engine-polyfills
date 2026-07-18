package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.nbt.CompoundTag;

/**
 * Byte-blob round-trip for {@link ContraptionNbt}. Uses vanilla {@code net.minecraft.nbt.CompoundTag}
 * (roadmap item #10 step 1 — the CraftEngine NBT seam was swapped to vanilla {@code NbtIo}); vanilla
 * getters return {@link java.util.Optional}, hence the {@code getIntOr}/{@code orElseThrow} reads.
 */
class ContraptionNbtTest {

    @Test
    void roundTripsScalarFields() throws Exception {
        CompoundTag tag = new CompoundTag();
        tag.putInt("count", 42);
        tag.putString("name", "miner");
        tag.putBoolean("moving", true);

        byte[] bytes = ContraptionNbt.toBytes(tag);
        CompoundTag restored = ContraptionNbt.fromBytes(bytes);

        assertEquals(42, restored.getIntOr("count", 0));
        assertEquals("miner", restored.getStringOr("name", ""));
        assertEquals(true, restored.getBooleanOr("moving", false));
    }

    @Test
    void roundTripsNestedCompoundsAndLists() throws Exception {
        CompoundTag inner = new CompoundTag();
        inner.putInt("x", 1);
        inner.putInt("y", 2);
        inner.putInt("z", 3);

        CompoundTag tag = new CompoundTag();
        tag.put("bearing", inner);
        tag.putIntArray("offsets", new int[] { 0, 1, 2, 3 });

        byte[] bytes = ContraptionNbt.toBytes(tag);
        CompoundTag restored = ContraptionNbt.fromBytes(bytes);

        CompoundTag restoredInner = restored.getCompound("bearing").orElseThrow();
        assertEquals(1, restoredInner.getIntOr("x", 0));
        assertEquals(2, restoredInner.getIntOr("y", 0));
        assertEquals(3, restoredInner.getIntOr("z", 0));
        assertEquals(4, restored.getIntArray("offsets").orElseThrow().length);
    }

    @Test
    void emptyCompoundRoundTrips() throws Exception {
        CompoundTag tag = new CompoundTag();
        byte[] bytes = ContraptionNbt.toBytes(tag);
        CompoundTag restored = ContraptionNbt.fromBytes(bytes);
        assertTrue(restored.isEmpty());
    }
}
