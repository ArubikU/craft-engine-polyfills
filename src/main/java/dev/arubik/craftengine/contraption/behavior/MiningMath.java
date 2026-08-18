/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.contraption.behavior;

public final class MiningMath {
    private MiningMath() {
    }

    public static double damagePerTick(double rpm, double hardness) {
        if (hardness < 0.0) {
            return 0.0;
        }
        if (hardness == 0.0) {
            return Double.MAX_VALUE;
        }
        return rpm / hardness;
    }

    public static boolean isBroken(double accumulatedDamage, double hardness) {
        return hardness >= 0.0 && accumulatedDamage >= hardness;
    }

    public static int crackStage(double accumulatedDamage, double hardness) {
        if (hardness <= 0.0) {
            return 0;
        }
        double fraction = accumulatedDamage / hardness;
        int stage = (int)Math.floor(fraction * 10.0);
        return Math.max(0, Math.min(9, stage));
    }
}

