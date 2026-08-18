/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 */
package dev.arubik.craftengine.contraption.api;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public interface ContraptionHitboxProvider {
    public List<AABB> getContraptionHitboxes(Level var1, BlockPos var2);

    default public boolean hasDynamicHitboxes() {
        return false;
    }

    default public boolean hasContraptionCollision() {
        return true;
    }
}

