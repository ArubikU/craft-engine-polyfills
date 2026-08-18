/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.world.BlockPos
 */
package dev.arubik.craftengine.contraption.api;

import net.momirealms.craftengine.core.world.BlockPos;

public interface PowerSource {
    public float getPower();

    public float getPotentialPower();

    public boolean isPowerSource();

    default public boolean powerReaches(BlockPos consumerPos) {
        return true;
    }
}

