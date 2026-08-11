package dev.arubik.craftengine.contraption.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

import dev.arubik.craftengine.contraption.core.ContraptionState;

/**
 * Pins the rule that decides which contraption type a contraption is written to disk as.
 *
 * <h2>The bug this exists for</h2>
 * Both save paths — shutdown and chunk-unload — used to re-derive the type by reading the anchor block
 * back out of the world, defaulting to ROTATIONAL when nothing was found. That is
 * correct for LINEAR/ROTATIONAL, whose bearing block stays put and pins the structure to it.
 *
 * <p>It is wrong for PHYS, and it fails silently. A phys contraption has no pinning
 * bearing: its anchor block is captured INTO the structure, and the body then falls away from the anchor
 * coordinates entirely. By the time anything saves it, the real world at that position is ordinary air —
 * so the lookup found nothing, took the ROTATIONAL fallback, and every phys contraption came back after a
 * restart as a spinning bearing.
 *
 * <p>The {@code level == null} cases below are not hypothetical padding: they are exactly the shape of the
 * failure (a lookup that answers "nothing here"), which is why a PHYS state must still resolve to PHYS
 * through them.
 */
class BearingTypePersistenceTest {

    @BeforeAll
    static void bootstrapRegistries() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    private static final ResourceKey<Level> TEST_WORLD =
        ResourceKey.create(Registries.DIMENSION, Identifier.parse("minecraft:test"));
    private static final BlockPos ANCHOR = new BlockPos(0, 64, 0);

    /** Pure kinematics state — no ContraptionLevel needed (see ContraptionState's null-tolerance javadoc). */
    private static ContraptionState state(Key type) {
        ContraptionState state = new ContraptionState(UUID.randomUUID(), TEST_WORLD, null, 0, 64, 0);
        if (type != null) {
            state.setBearingType(type);
        }
        return state;
    }

    @Test
    @DisplayName("a PHYS contraption persists as PHYS even though its anchor block is long gone")
    void physSurvivesAnAnchorThatIsNoLongerThere() {
        // A null level stands in for the real failure: the anchor lookup finding nothing, because the
        // block was captured into the body and the body fell away from that spot.
        assertEquals(Key.of("polyfills", "phys"),
                BlockAnchoredContraptionStore.typeToPersist(state(Key.of("polyfills", "phys")), null, ANCHOR));
    }

    @Test
    @DisplayName("the recorded type wins over the world for every kind, not just PHYS")
    void theRecordedTypeIsAuthoritative() {
        assertEquals(Key.of("polyfills", "linear"),
                BlockAnchoredContraptionStore.typeToPersist(state(Key.of("polyfills", "linear")), null, ANCHOR));
        assertEquals(Key.of("polyfills", "rotational"),
                BlockAnchoredContraptionStore.typeToPersist(state(Key.of("polyfills", "rotational")), null, ANCHOR));
        assertEquals(Key.of("polyfills", "minecart"),
                BlockAnchoredContraptionStore.typeToPersist(state(Key.of("polyfills", "minecart")), null, ANCHOR));
    }

    @Test
    @DisplayName("with nothing recorded and nothing in the world, it still falls back rather than failing")
    void fallsBackWhenNothingKnowsTheType() {
        // A state built by a path predating the recorded type, whose anchor block is also unreadable.
        // ROTATIONAL is the historical default and the only safe guess; the point is that it is now the
        // LAST resort rather than the answer a phys contraption got.
        assertEquals(Key.of("polyfills", "rotational"), BlockAnchoredContraptionStore.typeToPersist(state(null), null, ANCHOR));
        assertEquals(Key.of("polyfills", "rotational"), BlockAnchoredContraptionStore.typeToPersist(null, null, ANCHOR));
    }
}
