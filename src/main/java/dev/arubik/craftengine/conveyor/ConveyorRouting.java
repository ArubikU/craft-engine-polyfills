/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  org.bukkit.inventory.ItemStack
 */
package dev.arubik.craftengine.conveyor;

import dev.arubik.craftengine.conveyor.ConveyorReceiver;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import org.bukkit.inventory.ItemStack;

final class ConveyorRouting {
    private ConveyorRouting() {
    }

    static Direction cw(Direction d) {
        switch (d) {
            case NORTH: {
                return Direction.EAST;
            }
            case EAST: {
                return Direction.SOUTH;
            }
            case SOUTH: {
                return Direction.WEST;
            }
            case WEST: {
                return Direction.NORTH;
            }
        }
        return d;
    }

    static Direction ccw(Direction d) {
        switch (d) {
            case NORTH: {
                return Direction.WEST;
            }
            case WEST: {
                return Direction.SOUTH;
            }
            case SOUTH: {
                return Direction.EAST;
            }
            case EAST: {
                return Direction.NORTH;
            }
        }
        return d;
    }

    static ConveyorReceiver receiverAt(CEWorld world, BlockPos pos, Direction dir) {
        BlockPos[] cands;
        BlockPos flat = pos.relative(dir);
        for (BlockPos c : cands = new BlockPos[]{flat, new BlockPos(flat.x(), flat.y() + 1, flat.z()), new BlockPos(flat.x(), flat.y() - 1, flat.z())}) {
            BlockEntityController blockEntityController;
            BlockEntity be = world.getBlockEntityAtIfLoaded(c);
            if (be == null || !((blockEntityController = be.controller) instanceof ConveyorReceiver)) continue;
            ConveyorReceiver r = (ConveyorReceiver)blockEntityController;
            return r;
        }
        return null;
    }

    static boolean push(CEWorld world, BlockPos pos, Direction dir, ItemStack stack) {
        return ConveyorRouting.push(world, pos, dir, stack, 0.0f);
    }

    static boolean push(CEWorld world, BlockPos pos, Direction dir, ItemStack stack, float jitter) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        ConveyorReceiver r = ConveyorRouting.receiverAt(world, pos, dir);
        if (r == null || r.isFull()) {
            return false;
        }
        return r.receiveConveyorItem(stack, dir, jitter);
    }
}

