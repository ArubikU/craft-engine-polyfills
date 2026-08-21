/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;

public final class ScriptStallBehavior
implements MovementBehavior {
    private volatile boolean stalled = false;

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    @Override
    public void tick(MovementContext ctx) {
    }

    @Override
    public boolean isStalled() {
        return this.stalled;
    }
}

