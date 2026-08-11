package dev.arubik.craftengine.contraption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Pins the anchor re-keying that lets a PHYS contraption survive a restart.
 *
 * <h2>The bug this exists for</h2>
 * Every persistence path — save-on-chunk-unload, the {@code onDisable} shutdown flush, and
 * rehydrate-on-chunk-load — is driven off {@link BearingHammerListener}'s assembled-anchor map, keyed by a
 * block position. Phys contraptions were never put in it (recording {@link BearingType#PHYS} on the state
 * is necessary for persistence but NOT sufficient — registration is what triggers a save), so they were
 * never written to disk and vanished on every restart.
 *
 * <p>Putting them in is only half of it. For LINEAR/ROTATIONAL the key is the bearing block, which never
 * moves, so the map only ever had to handle a write-once key. A PHYS body has no bearing block: it FALLS
 * AWAY from its anchor. Its key therefore has to follow it, or "save when this chunk unloads" and
 * "restore when this chunk loads" name a chunk the body left long ago.
 *
 * <p>Which makes the re-key the load-bearing part, and the stale-key case the one that bites: the map is
 * keyed BY POSITION and drives chunk-unload teardown, so an old key left pointing at a live contraption
 * would let an unrelated chunk's unload believe it owned that contraption and tear it down mid-flight.
 */
class PhysAnchorPersistenceTest {

    private static final ResourceKey<Level> WORLD =
        ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:test"));

    @Test
    @DisplayName("re-anchoring a moving phys body drops the key it moved off")
    void reAnchorDropsTheStaleKey() {
        UUID contraption = UUID.randomUUID();
        BlockPos from = new BlockPos(10, 70, 10);
        BlockPos to = new BlockPos(10, 64, 11); // fell and drifted a block, as a phys body does
        try {
            BearingHammerListener.markAssembled(WORLD, from, contraption);
            assertEquals(contraption, BearingHammerListener.assembledContraptionAt(WORLD, from));

            BearingHammerListener.markAssembled(WORLD, to, contraption);

            assertEquals(contraption, BearingHammerListener.assembledContraptionAt(WORLD, to),
                    "the body must be anchored where it now is");
            // The whole point: a leftover key here is a live contraption that an unrelated chunk's unload
            // would happily tear down.
            assertNull(BearingHammerListener.assembledContraptionAt(WORLD, from),
                    "the vacated position must no longer claim the contraption");
            assertEquals(to, BearingHammerListener.assembledAnchorsSnapshot().get(contraption).pos(),
                    "the reverse mapping must agree with the forward one");
        } finally {
            BearingHammerListener.forgetAssembled(contraption);
        }
    }

    @Test
    @DisplayName("re-anchoring to the same block leaves the mapping intact")
    void reAnchorToSameBlockIsANoOp() {
        UUID contraption = UUID.randomUUID();
        BlockPos pos = new BlockPos(-4, 12, 300);
        try {
            BearingHammerListener.markAssembled(WORLD, pos, contraption);
            // PhysicsWorld#writeBack calls this EVERY tick for every phys body; it only actually moves
            // between blocks occasionally, and never once asleep. The repeat must not disturb anything.
            BearingHammerListener.markAssembled(WORLD, pos, contraption);
            BearingHammerListener.markAssembled(WORLD, pos, contraption);

            assertEquals(contraption, BearingHammerListener.assembledContraptionAt(WORLD, pos));
            assertEquals(pos, BearingHammerListener.assembledAnchorsSnapshot().get(contraption).pos());
        } finally {
            BearingHammerListener.forgetAssembled(contraption);
        }
    }

    @Test
    @DisplayName("forgetting a re-anchored contraption leaves nothing behind")
    void forgetAfterReAnchorClearsBothKeys() {
        UUID contraption = UUID.randomUUID();
        BlockPos from = new BlockPos(0, 100, 0);
        BlockPos to = new BlockPos(0, 40, 0); // a long fall
        BearingHammerListener.markAssembled(WORLD, from, contraption);
        BearingHammerListener.markAssembled(WORLD, to, contraption);

        BearingHammerListener.forgetAssembled(contraption);

        assertNull(BearingHammerListener.assembledContraptionAt(WORLD, to));
        assertNull(BearingHammerListener.assembledContraptionAt(WORLD, from));
        assertNull(BearingHammerListener.assembledAnchorsSnapshot().get(contraption),
                "a disassembled body must not be resurrected by a later chunk load");
    }
}
