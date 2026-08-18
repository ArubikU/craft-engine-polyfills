/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package dev.arubik.craftengine.contraption;

import dev.arubik.craftengine.contraption.MovementContext;
import net.minecraft.world.phys.Vec3;

public interface MovementBehavior {
    public void tick(MovementContext var1);

    public boolean isStalled();

    default public Vec3 velocityThisTick() {
        return Vec3.ZERO;
    }
}

