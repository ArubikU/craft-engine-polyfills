package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;

import java.util.List;

/**
 * Shared per-tick render state passed to every element during render/tick.
 * Avoids each element needing the full ContraptionState reference.
 */
public record RenderContext(
        List<Player> viewers,
        Vec3 bearing,
        double yawRadians,
        double pitchRadians,
        double rollRadians,
        double scale,
        boolean moved,
        ContraptionLevel level,
        ServerLevel realLevel,
        ContraptionLightMap lightMap
) {
    public double yawDegrees() {
        return Math.toDegrees(yawRadians);
    }
    public double pitchDegrees() {
        return Math.toDegrees(pitchRadians);
    }
}
