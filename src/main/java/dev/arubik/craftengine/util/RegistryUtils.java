package dev.arubik.craftengine.util;

import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorType;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorType;
import net.momirealms.craftengine.core.registry.BuiltInRegistries;
import net.momirealms.craftengine.core.registry.Registries;
import net.momirealms.craftengine.core.registry.WritableRegistry;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.ResourceKey;

public class RegistryUtils {

    public static void registerBlockBehavior(Key key, BlockBehaviorFactory factory) {
        ((WritableRegistry<BlockBehaviorType>) Registries.BLOCK_BEHAVIOR_TYPE)
                .register(ResourceKey.create(BuiltInRegistries.BLOCK_BEHAVIOR_TYPE.key().location(), key),
                        new BlockBehaviorType<>(key, factory));
    }

    public static void registerItemBehavior(Key key, ItemBehaviorFactory factory) {
        ((WritableRegistry<ItemBehaviorType>) Registries.ITEM_BEHAVIOR_TYPE)
                .register(ResourceKey.create(BuiltInRegistries.ITEM_BEHAVIOR_TYPE.key().location(), key),
                        new ItemBehaviorType<>(key, factory));
    }

}
