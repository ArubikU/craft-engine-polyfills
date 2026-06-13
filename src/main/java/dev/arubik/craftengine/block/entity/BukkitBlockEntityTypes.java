package dev.arubik.craftengine.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.world.CEWorld;

/**
 * In craft-engine 26.6.2 there is no global block-entity-type registry and no
 * {@code BukkitBlockEntityTypes.register(Key)}. Block entities are created by the
 * engine and owned by the block behavior (via {@code EntityBlock#createBlockEntityController}).
 * This class keeps only the convenience lookup; callers must read {@code BlockEntity#controller}
 * to reach the concrete controller.
 */
public class BukkitBlockEntityTypes {

    public static BlockEntity getIfLoaded(Level world, BlockPos pos) {
        CEWorld ceWorld = new BukkitWorld(world.getWorld()).storageWorld();
        return ceWorld.getBlockEntityAtIfLoaded(net.momirealms.craftengine.core.world.BlockPos.of(pos.asLong()));
    }
}
