/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.contraption;

public final class ContraptionPushSettings {
    public final boolean pushUpEnabled;
    public final double maxStepUpHeight;
    public final double pushStrength;
    public final double pushUpStrength;
    public final boolean carryEntities;
    public static final ContraptionPushSettings DEFAULT = ContraptionPushSettings.builder().build();

    private ContraptionPushSettings(Builder b) {
        this.pushUpEnabled = b.pushUpEnabled;
        this.maxStepUpHeight = b.maxStepUpHeight;
        this.pushStrength = b.pushStrength;
        this.pushUpStrength = b.pushUpStrength;
        this.carryEntities = b.carryEntities;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean pushUpEnabled = false;
        private double maxStepUpHeight = 0.6;
        private double pushStrength = 1.0;
        private double pushUpStrength = 1.0;
        private boolean carryEntities = false;

        public Builder pushUpEnabled(boolean pushUpEnabled) {
            this.pushUpEnabled = pushUpEnabled;
            return this;
        }

        public Builder maxStepUpHeight(double maxStepUpHeight) {
            this.maxStepUpHeight = maxStepUpHeight;
            return this;
        }

        public Builder pushStrength(double pushStrength) {
            this.pushStrength = pushStrength;
            return this;
        }

        public Builder pushUpStrength(double pushUpStrength) {
            this.pushUpStrength = pushUpStrength;
            return this;
        }

        public Builder carryEntities(boolean carryEntities) {
            this.carryEntities = carryEntities;
            return this;
        }

        public ContraptionPushSettings build() {
            return new ContraptionPushSettings(this);
        }
    }
}

