/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.world.BlockPos
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.contraption.api.PowerSource;
import net.momirealms.craftengine.core.world.BlockPos;

public interface RpmProvider
extends PowerSource {
    public float getRpm();

    @Override
    default public float getPower() {
        return this.getRpm();
    }

    default public float potentialRpm() {
        return this.getRpm();
    }

    @Override
    default public float getPotentialPower() {
        return this.potentialRpm();
    }

    default public boolean isRpmSource() {
        return true;
    }

    @Override
    default public boolean isPowerSource() {
        return this.isRpmSource();
    }

    default public float stressCapacity() {
        return Float.MAX_VALUE;
    }

    default public void reportStressLoad(float su) {
    }

    default public BlockPos rpmHeadPos() {
        return null;
    }

    default public boolean rpmReaches(BlockPos consumerPos) {
        BlockPos head = this.rpmHeadPos();
        return head == null || head.x() == consumerPos.x() && head.y() == consumerPos.y() && head.z() == consumerPos.z();
    }

    @Override
    default public boolean powerReaches(BlockPos consumerPos) {
        return this.rpmReaches(consumerPos);
    }
}

