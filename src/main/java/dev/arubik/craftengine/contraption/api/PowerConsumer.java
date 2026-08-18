/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.contraption.api;

public interface PowerConsumer {
    public void setPower(float var1);

    public float getPower();

    default public float getRequiredPower() {
        return 0.0f;
    }
}

