package dev.arubik.craftengine.contraption.behavior;

import java.util.HashMap;
import java.util.Map;

import dev.arubik.craftengine.contraption.MovementBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.util.Key;

/**
 * Block-type -> {@link MovementBehavior} factory registry (Create's own pattern —
 * {@code MovementBehaviour.REGISTRY.get(blockState)}, confirmed against Create's real
 * source). A captured block whose CraftEngine custom-block {@link Key} is registered here
 * gets a behavior auto-attached during {@code ContraptionCapture} — no manual
 * {@code state.addBehavior(...)} call needed; a Drill-type block just always drills once
 * captured, the same way it would in Create.
 *
 * <p>Blocks with no registered factory are captured as inert data — CONTRAPTIONS.md's
 * "vanilla ticking is suspended" design still holds for anything not explicitly opted in
 * here.
 */
public final class MovementBehaviorRegistry {

    private MovementBehaviorRegistry() {
    }

    /** Builds one behavior instance for a single captured occurrence of a registered block type. */
    public interface Factory {
        MovementBehavior create(BlockPos localOffset, BlockState state);
    }

    private static final Map<Key, Factory> FACTORIES = new HashMap<>();

    public static void register(Key blockKey, Factory factory) {
        FACTORIES.put(blockKey, factory);
    }

    public static boolean has(Key blockKey) {
        return blockKey != null && FACTORIES.containsKey(blockKey);
    }

    /** Resolves a behavior for one captured block occurrence, or null if that block type isn't registered. */
    public static MovementBehavior resolve(Key blockKey, BlockPos localOffset, BlockState state) {
        if (blockKey == null) {
            return null;
        }
        Factory factory = FACTORIES.get(blockKey);
        return factory != null ? factory.create(localOffset, state) : null;
    }

    /** Test/administrative use: drop every registered factory. */
    public static void clear() {
        FACTORIES.clear();
    }
}
