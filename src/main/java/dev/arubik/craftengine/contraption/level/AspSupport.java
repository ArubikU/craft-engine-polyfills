package dev.arubik.craftengine.contraption.level;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.world.level.Level;

/**
 * Runtime gate for the Advanced Slime Paper integration. Isolates every reference to {@code ASP} + to
 * {@link AspContraptionLevel} behind a class whose own static init only does a {@code Class.forName}, so a
 * plain Paper server loads this class harmlessly, finds ASP absent, and NEVER loads {@link AspContraptionLevel}
 * (whose ASP-typed references would otherwise fail to link).
 */
public final class AspSupport {

    private static final boolean AVAILABLE;

    static {
        boolean present;
        try {
            Class.forName("com.infernalsuite.asp.api.AdvancedSlimePaperAPI");
            present = true;
        } catch (Throwable t) {
            present = false;
        }
        AVAILABLE = present;
    }

    private AspSupport() {
    }

    /** True when the ASP fork's API is on the classpath. */
    public static boolean available() {
        return AVAILABLE;
    }

    /** Only ever called when {@link #available()} is true, which is what keeps {@link AspContraptionLevel} unloaded on plain Paper. */
    public static ContraptionLevel create(Level realLevel, double x, double y, double z, double yawRadians) {
        return AspContraptionLevel.create(realLevel, x, y, z, yawRadians);
    }
}
