/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.contraption.api;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface MultiblockMember {
    public Set<BlockPos> getStructurePositions(Level var1, BlockPos var2);

    public BlockPos getControllerPosition(Level var1, BlockPos var2);

    default public boolean isStructureComplete(Level level, BlockPos pos) {
        return true;
    }
}

