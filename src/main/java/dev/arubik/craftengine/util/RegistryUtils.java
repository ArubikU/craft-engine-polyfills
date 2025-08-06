package dev.arubik.craftengine.util;

import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.registry.BuiltInRegistries;
import net.momirealms.craftengine.core.registry.Registries;
import net.momirealms.craftengine.core.registry.WritableRegistry;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.ResourceKey;

public class RegistryUtils {

    public static void registerBlockBehavior(Key key, BlockBehaviorFactory factory) {
        ((WritableRegistry<BlockBehaviorFactory>) BuiltInRegistries.BLOCK_BEHAVIOR_FACTORY)
                .register(ResourceKey.create(Registries.BLOCK_BEHAVIOR_FACTORY.location(), key), factory);
    }
}
