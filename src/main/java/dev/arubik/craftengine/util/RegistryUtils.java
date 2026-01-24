package dev.arubik.craftengine.util;

import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorType;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviors;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorType;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviors;
import net.momirealms.craftengine.core.registry.BuiltInRegistries;
import net.momirealms.craftengine.core.registry.Registries;
import net.momirealms.craftengine.core.registry.WritableRegistry;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.util.ResourceKey;

public class RegistryUtils {

    public static void registerBlockBehavior(Key key, BlockBehaviorFactory factory) {
        BlockBehaviors.register(key, factory);
    }

    public static void registerItemBehavior(Key key, ItemBehaviorFactory factory) {
        ItemBehaviors.register(key, factory);
    }

}
