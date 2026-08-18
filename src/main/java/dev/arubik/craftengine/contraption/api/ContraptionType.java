/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.contraption.api;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ContraptionType {
    public ContraptionEntity createEntity(Level var1, ContraptionState var2);

    default public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
    }

    default public boolean canAssemble(Level level, BlockPos anchor, Set<BlockPos> blocks) {
        return true;
    }

    default public String getAssemblyFailureMessage() {
        return null;
    }

    default public void onDisassemble(ContraptionState state, Level level) {
    }

    default public boolean isRotational() {
        return false;
    }

    default public boolean isVehicle() {
        return false;
    }

    default public boolean hasPhysics() {
        return false;
    }

    default public int getMaxBlocks() {
        return -1;
    }

    default public void onAnchorDeath(Entity anchorEntity, Level level, List<ItemStack> drops) {
    }

    default public boolean onAnchorDamage(Entity anchorEntity, Level level) {
        return false;
    }

    default public boolean canPackToItem() {
        return false;
    }

    default public boolean isPackedItem(ItemStack item) {
        return false;
    }

    default public ContraptionEntity fromItem(ItemStack item, Level level, BlockPos spawnPos) {
        return null;
    }

    default public ItemStack toItem(ContraptionEntity entity, Level level) {
        return null;
    }

    default public boolean isBearingEntity(Entity entity) {
        return false;
    }

    default public UUID getContraptionId(Entity entity) {
        return null;
    }

    default public void onBearingEntityLoad(Entity entity, Level level) {
    }

    default public void onBearingEntityUnload(Entity entity, ContraptionState state) {
    }
}

