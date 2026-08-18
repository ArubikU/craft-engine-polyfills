/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ContraptionLightMap;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;

public record RenderContext(List<Player> viewers, Vec3 bearing, double yawRadians, double pitchRadians, double rollRadians, double scale, boolean moved, ContraptionLevel level, ServerLevel realLevel, ContraptionLightMap lightMap, List<ContraptionElement> elements) {
    public double yawDegrees() {
        return Math.toDegrees(this.yawRadians);
    }

    public double pitchDegrees() {
        return Math.toDegrees(this.pitchRadians);
    }
}

