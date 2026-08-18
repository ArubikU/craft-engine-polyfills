/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 */
package dev.arubik.craftengine.contraption.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

public interface ContraptionTickable {
    public void tick(Level var1, BlockPos var2, ImmutableBlockState var3);

    default public void unregister() {
    }
}

