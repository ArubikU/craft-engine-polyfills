/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 */
package dev.arubik.craftengine.contraption.bearing;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public final class GhastHarness {
    private static final Set<Material> HARNESSES;
    private static final double PIVOT_XZ = 0.5;

    private GhastHarness() {
    }

    public static boolean isHarness(Material material) {
        return material != null && HARNESSES.contains(material);
    }

    public static Set<Material> harnesses() {
        return HARNESSES;
    }

    public static List<BlockPos> cellsOverlapping(AABB box) {
        ArrayList<BlockPos> cells = new ArrayList<BlockPos>();
        for (int x = GhastHarness.floor(box.minX); x <= GhastHarness.lastCell(box.minX, box.maxX); ++x) {
            for (int y = GhastHarness.floor(box.minY); y <= GhastHarness.lastCell(box.minY, box.maxY); ++y) {
                for (int z = GhastHarness.floor(box.minZ); z <= GhastHarness.lastCell(box.minZ, box.maxZ); ++z) {
                    cells.add(new BlockPos(x, y, z));
                }
            }
        }
        return cells;
    }

    public static double contraptionYaw(float ghastYawDegrees) {
        return Math.toRadians(ghastYawDegrees);
    }

    public static double captureYawOffset(double contraptionYawRadians, float ghastYawDegrees) {
        return contraptionYawRadians - GhastHarness.contraptionYaw(ghastYawDegrees);
    }

    public static double followTargetYaw(double yawOffset, float ghastYawDegrees) {
        return GhastHarness.contraptionYaw(ghastYawDegrees) + yawOffset;
    }

    public static double shortestAngleDelta(double from, double to) {
        double delta = (to - from) % (Math.PI * 2);
        if (delta > Math.PI) {
            delta -= Math.PI * 2;
        } else if (delta < -Math.PI) {
            delta += Math.PI * 2;
        }
        return delta;
    }

    public static Vec3 bearingOrigin(Vec3 ghastPos, double centreHeight, Vec3 anchorOffset, double yawRadians) {
        Vec3 pivotOffset = new Vec3(anchorOffset.x + 0.5, anchorOffset.y - centreHeight, anchorOffset.z + 0.5);
        Vec3 rotated = ContraptionMath.rotateYaw(pivotOffset, yawRadians);
        return new Vec3(ghastPos.x + rotated.x - 0.5, ghastPos.y + centreHeight + rotated.y, ghastPos.z + rotated.z - 0.5);
    }

    private static int lastCell(double min, double max) {
        int last = GhastHarness.floor(max);
        return last > GhastHarness.floor(min) && max == (double)last ? last - 1 : last;
    }

    private static int floor(double v) {
        int i = (int)v;
        return v < (double)i ? i - 1 : i;
    }

    static {
        EnumSet<Material> set = EnumSet.noneOf(Material.class);
        for (DyeColor color : DyeColor.values()) {
            set.add(Material.valueOf((String)(color.name() + "_HARNESS")));
        }
        HARNESSES = Collections.unmodifiableSet(set);
    }
}

