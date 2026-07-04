package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.momirealms.craftengine.libraries.nbt.CompoundTag;

/** Byte-blob round-trip for {@link ContraptionNbt} — no NMS/Bukkit runtime needed. */
class ContraptionNbtTest {

    @Test
    void roundTripsScalarFields() throws Exception {
        CompoundTag tag = new CompoundTag();
        tag.putInt("count", 42);
        tag.putString("name", "miner");
        tag.putBoolean("moving", true);

        byte[] bytes = ContraptionNbt.toBytes(tag);
        CompoundTag restored = ContraptionNbt.fromBytes(bytes);

        assertEquals(42, restored.getInt("count"));
        assertEquals("miner", restored.getString("name"));
        assertEquals(true, restored.getBoolean("moving"));
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

        CompoundTag restoredInner = restored.getCompound("bearing");
        assertEquals(1, restoredInner.getInt("x"));
        assertEquals(2, restoredInner.getInt("y"));
        assertEquals(3, restoredInner.getInt("z"));
        assertEquals(4, restored.getIntArray("offsets").length);
    }

    @Test
    void emptyCompoundRoundTrips() throws Exception {
        CompoundTag tag = new CompoundTag();
        byte[] bytes = ContraptionNbt.toBytes(tag);
        CompoundTag restored = ContraptionNbt.fromBytes(bytes);
        assertEquals(true, restored.isEmpty());
    }
}
