/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.arubik.craftengine.contraption.level;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.AspContraptionLevel;
import net.minecraft.world.level.Level;

public final class AspSupport {
    private static final boolean AVAILABLE;

    private AspSupport() {
    }

    public static boolean available() {
        return AVAILABLE;
    }

    public static ContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        return AspContraptionLevel.create(realLevel, x, y, z, yawRadians);
    }

    static {
        boolean present;
        try {
            Class.forName("com.infernalsuite.asp.api.AdvancedSlimePaperAPI");
            present = true;
        }
        catch (Throwable t) {
            present = false;
        }
        AVAILABLE = present;
    }
}

