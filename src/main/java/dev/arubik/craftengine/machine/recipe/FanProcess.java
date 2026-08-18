/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.recipe;

import java.util.Locale;

public enum FanProcess {
    NONE,
    SMELTING,
    COOKING,
    BLASTING,
    WASHING,
    FREEZING;


    public static FanProcess parse(String s) {
        if (s == null) {
            return NONE;
        }
        return switch (s.trim().toLowerCase(Locale.ROOT)) {
            case "smelting", "smelt", "fire" -> SMELTING;
            case "cooking", "cook", "smoking", "smoker", "soul_fire", "soulfire" -> COOKING;
            case "blasting", "blast", "lava" -> BLASTING;
            case "washing", "wash", "splashing", "water" -> WASHING;
            case "freezing", "freeze", "ice", "frost" -> FREEZING;
            default -> NONE;
        };
    }
}

