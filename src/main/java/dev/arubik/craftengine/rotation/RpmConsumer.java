/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.contraption.api.PowerConsumer;

public interface RpmConsumer
extends PowerConsumer {
    public void setInputRpm(float var1);

    public float getInputRpm();

    @Override
    default public void setPower(float power) {
        this.setInputRpm(power);
    }

    @Override
    default public float getPower() {
        return this.getInputRpm();
    }
}

