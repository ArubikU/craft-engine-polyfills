/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption.listener;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ContraptionVoidDrop {
    private static final double VOID_MARGIN = 3.0;

    private ContraptionVoidDrop() {
    }

    public static void handle(ContraptionLevel level, ServerLevel realLevel) {
        if (level == null || realLevel == null) {
            return;
        }
        double minCellY = Double.MAX_VALUE;
        for (BlockPos p : level.localPositions()) {
            if (!((double)p.getY() < minCellY)) continue;
            minCellY = p.getY();
        }
        if (minCellY == Double.MAX_VALUE) {
            return;
        }
        double floor = minCellY - 3.0;
        ArrayList<Entity> all = new ArrayList<Entity>();
        for (Entity e : level.getAllEntities()) {
            all.add(e);
        }
        for (Entity e : all) {
            if (e == null || e.isRemoved() || e instanceof ServerPlayer || e.position().y >= floor) continue;
            Vec3 at = level.realWorldPositionOf(e.position());
            ContraptionVoidDrop.eject(e, realLevel, at);
            e.discard();
        }
    }

    private static void eject(Entity e, ServerLevel realLevel, Vec3 at) {
        try {
            if (e instanceof ItemEntity) {
                ItemEntity item = (ItemEntity)e;
                ContraptionVoidDrop.spawnItem(realLevel, at, item.getItem().copy());
            } else if (e instanceof FallingBlockEntity) {
                FallingBlockEntity falling = (FallingBlockEntity)e;
                ContraptionVoidDrop.spawnItem(realLevel, at, new ItemStack((ItemLike)falling.getBlockState().getBlock()));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void spawnItem(ServerLevel realLevel, Vec3 at, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        ItemEntity drop = new ItemEntity((Level)realLevel, at.x, at.y, at.z, stack);
        drop.setDeltaMovement(Vec3.ZERO);
        realLevel.addFreshEntity((Entity)drop);
    }
}

