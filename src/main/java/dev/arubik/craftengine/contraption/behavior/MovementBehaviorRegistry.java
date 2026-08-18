/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.core.util.Key;

public final class MovementBehaviorRegistry {
    private static final Map<Key, Factory> FACTORIES = new HashMap<Key, Factory>();

    private MovementBehaviorRegistry() {
    }

    public static void register(Key blockKey, Factory factory) {
        FACTORIES.put(blockKey, factory);
    }

    public static boolean has(Key blockKey) {
        return blockKey != null && FACTORIES.containsKey(blockKey);
    }

    public static MovementBehavior resolve(Key blockKey, BlockPos localOffset, BlockState state) {
        if (blockKey == null) {
            return null;
        }
        Factory factory = FACTORIES.get(blockKey);
        return factory != null ? factory.create(localOffset, state) : null;
    }

    public static void clear() {
        FACTORIES.clear();
    }

    public static interface Factory {
        public MovementBehavior create(BlockPos var1, BlockState var2);
    }
}

