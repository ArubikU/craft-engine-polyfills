/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.util.BoundingBox
 */
package dev.arubik.craftengine.contraption.protection;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.protection.ContraptionProtectionRegistry;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.util.BoundingBox;

public final class ContraptionMoveGuard {
    private ContraptionMoveGuard() {
    }

    public static boolean allowsMoveTo(ContraptionState state, double x, double y, double z) {
        CraftWorld world;
        UUID owner = state.owner();
        if (owner == null) {
            return true;
        }
        ContraptionLevel level = state.level();
        if (level == null) {
            return true;
        }
        try {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel serverLevel = server.getLevel(state.worldId());
            world = serverLevel != null ? serverLevel.getWorld() : null;
        }
        catch (Throwable t) {
            return true;
        }
        if (world == null) {
            return true;
        }
        long key = BlockPos.asLong((int)Mth.floor((double)x), (int)Mth.floor((double)y), (int)Mth.floor((double)z));
        if (key == state.lastRegionCheckKey()) {
            return state.lastRegionAllowed();
        }
        BoundingBox box = ContraptionMoveGuard.footprint(state, level, x, y, z);
        boolean allowed = box == null || ContraptionProtectionRegistry.canUseRegion(owner, (World)world, box);
        state.setLastRegionCheck(key, allowed);
        return allowed;
    }

    private static BoundingBox footprint(ContraptionState state, ContraptionLevel level, double x, double y, double z) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -1.7976931348623157E308;
        double maxY = -1.7976931348623157E308;
        double maxZ = -1.7976931348623157E308;
        boolean any = false;
        for (BlockPos local : level.localPositions()) {
            double cx = x + (double)local.getX();
            double cy = y + (double)local.getY();
            double cz = z + (double)local.getZ();
            minX = Math.min(minX, cx);
            minY = Math.min(minY, cy);
            minZ = Math.min(minZ, cz);
            maxX = Math.max(maxX, cx + 1.0);
            maxY = Math.max(maxY, cy + 1.0);
            maxZ = Math.max(maxZ, cz + 1.0);
            any = true;
        }
        return any ? new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ) : null;
    }
}

