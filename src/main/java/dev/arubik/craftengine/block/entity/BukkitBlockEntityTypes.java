package dev.arubik.craftengine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;

public class BukkitBlockEntityTypes {
    public static final BlockEntityType<PersistentBlockEntity> PERSISTENT_BLOCK_ENTITY_TYPE;

    static {
        PERSISTENT_BLOCK_ENTITY_TYPE = net.momirealms.craftengine.bukkit.block.entity.BukkitBlockEntityTypes.register(Key.of("polyfills:persistent_block_entity"), PersistentBlockEntity::new);
    }

    public static BlockEntity getIfLoaded(Level world, BlockPos pos) {
        CEWorld ceWorld = new BukkitWorld(world.getWorld()).storageWorld();
        return ceWorld.getBlockEntityAtIfLoaded(net.momirealms.craftengine.core.world.BlockPos.of(pos.asLong()));
    }
}
