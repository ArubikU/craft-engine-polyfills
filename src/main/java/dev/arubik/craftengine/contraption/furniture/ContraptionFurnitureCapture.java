/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineFurniture
 *  net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture
 *  net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitBox
 *  net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitboxPart
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.WorldPosition
 *  net.momirealms.craftengine.core.world.collision.AABB
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.furniture;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.contraption.furniture.ContraptionSeatMount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitBox;
import net.momirealms.craftengine.core.entity.furniture.hitbox.FurnitureHitboxPart;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.WorldPosition;
import net.momirealms.craftengine.core.world.collision.AABB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class ContraptionFurnitureCapture {
    private ContraptionFurnitureCapture() {
    }

    public static Result captureNear(Level level, Set<BlockPos> worldPositions, BlockPos bearingWorldPos, ContraptionLevel fakeLevel) {
        CraftEntity bukkit;
        List<net.minecraft.world.entity.Entity> nearby;
        ArrayList<ContraptionFurniture> out = new ArrayList<ContraptionFurniture>();
        HashMap<UUID, Vec3> seatedRiders = new HashMap<UUID, Vec3>();
        HashMap<UUID, UUID> seatedRiderMounts = new HashMap<UUID, UUID>();
        if (worldPositions.isEmpty()) {
            return new Result(out, seatedRiders, seatedRiderMounts);
        }
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos p : worldPositions) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB((double)minX, (double)minY, (double)minZ, (double)(maxX + 1), (double)(maxY + 1), (double)(maxZ + 1)).inflate(2.0);
        try {
            nearby = level.getEntities((net.minecraft.world.entity.Entity)null, bounds, e -> true);
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] furniture capture: getEntities(bounds=" + String.valueOf(bounds) + ") threw: " + String.valueOf(t));
            return new Result(out, seatedRiders, seatedRiderMounts);
        }
        ArrayList<CraftEntity> toDestroy = new ArrayList<CraftEntity>();
        ArrayList<CraftEntity> seatsToRemove = new ArrayList<CraftEntity>();
        ArrayList<BukkitFurniture> capturedFurnitureObjects = new ArrayList<BukkitFurniture>();
        for (net.minecraft.world.entity.Entity entity : nearby) {
            bukkit = entity.getBukkitEntity();
            if (bukkit == null || !CraftEngineFurniture.isFurniture((Entity)bukkit)) continue;
            BukkitFurniture furniture = CraftEngineFurniture.getLoadedFurnitureByMetaEntity((Entity)bukkit);
            if (furniture == null) {
                Bukkit.getLogger().warning("[Contraption] furniture capture: entity id=" + bukkit.getEntityId() + " at " + String.valueOf(bukkit.getLocation()) + " is tagged as CraftEngine furniture but getLoadedFurnitureByMetaEntity returned null \u2014 not captured into the contraption.");
                continue;
            }
            try {
                if (!ContraptionFurnitureCapture.belongsToStructure(furniture, worldPositions)) continue;
                WorldPosition pos = furniture.position();
                Vec3 localOffset = new Vec3(pos.x() - (double)bearingWorldPos.getX(), pos.y() - (double)bearingWorldPos.getY(), pos.z() - (double)bearingWorldPos.getZ());
                String variantName = furniture.getCurrentVariant().name();
                Key definitionId = furniture.config().id();
                BukkitFurniture liveFurniture = ContraptionFurnitureCapture.placeInFakeLevel(fakeLevel, definitionId, variantName, localOffset, pos.yRot());
                out.add(new ContraptionFurniture(definitionId, variantName, localOffset, pos.yRot(), liveFurniture));
                if (fakeLevel != null) {
                    fakeLevel.addFurnitureRecord(new ContraptionLevel.FurnitureRecord(definitionId.toString(), variantName, localOffset.x, localOffset.y, localOffset.z, pos.yRot()));
                }
                capturedFurnitureObjects.add(furniture);
                toDestroy.add(bukkit);
            }
            catch (Throwable t) {
                Bukkit.getLogger().warning("[Contraption] furniture capture: entity id=" + bukkit.getEntityId() + " at " + String.valueOf(bukkit.getLocation()) + " matched CraftEngine furniture but capture threw: " + String.valueOf(t));
            }
        }
        for (net.minecraft.world.entity.Entity entity : nearby) {
            bukkit = entity.getBukkitEntity();
            if (bukkit == null) continue;
            try {
                BukkitFurniture owningFurniture;
                if (!CraftEngineFurniture.isSeat((Entity)bukkit) || (owningFurniture = CraftEngineFurniture.getLoadedFurnitureBySeat((Entity)bukkit)) == null || !capturedFurnitureObjects.contains(owningFurniture)) continue;
                Location seatLoc = bukkit.getLocation();
                Vec3 seatLocalOffset = new Vec3(seatLoc.getX() - (double)bearingWorldPos.getX(), seatLoc.getY() - (double)bearingWorldPos.getY(), seatLoc.getZ() - (double)bearingWorldPos.getZ());
                Vec3 seatRealPos = new Vec3(seatLoc.getX(), seatLoc.getY(), seatLoc.getZ());
                for (Entity passenger : bukkit.getPassengers()) {
                    Player player;
                    ArmorStand mount;
                    if (!(passenger instanceof Player) || (mount = ContraptionSeatMount.mount(player = (Player)passenger, bukkit.getWorld(), seatRealPos, seatLoc.getYaw())) == null) continue;
                    seatedRiders.put(player.getUniqueId(), seatLocalOffset);
                    seatedRiderMounts.put(player.getUniqueId(), mount.getUniqueId());
                }
                seatsToRemove.add(bukkit);
            }
            catch (Throwable throwable) {}
        }
        for (Entity entity : toDestroy) {
            try {
                boolean removed = CraftEngineFurniture.remove((Entity)entity, (boolean)false, (boolean)false);
                if (removed) continue;
                Bukkit.getLogger().warning("[Contraption] furniture capture: entity id=" + entity.getEntityId() + " at " + String.valueOf(entity.getLocation()) + " was captured but CraftEngineFurniture.remove returned false (not recognised as furniture anymore) \u2014 the real entity may still be visible alongside its packet-mirror.");
            }
            catch (Throwable t) {
                Bukkit.getLogger().warning("[Contraption] furniture capture: entity id=" + entity.getEntityId() + " at " + String.valueOf(entity.getLocation()) + " was captured but CraftEngineFurniture.remove threw \u2014 the real entity is still visible alongside its packet-mirror: " + String.valueOf(t));
            }
        }
        for (Entity entity : seatsToRemove) {
            try {
                entity.remove();
            }
            catch (Throwable throwable) {}
        }
        if (!out.isEmpty() || !seatedRiders.isEmpty()) {
            Bukkit.getLogger().info("[Contraption] furniture capture: captured " + out.size() + " furniture piece(s), " + seatedRiders.size() + " seated rider(s), from " + nearby.size() + " nearby entities scanned.");
        }
        return new Result(out, seatedRiders, seatedRiderMounts);
    }

    public static List<ContraptionFurniture> restoreIntoFakeLevel(ContraptionLevel fakeLevel, List<ContraptionLevel.FurnitureRecord> records) {
        ArrayList<ContraptionFurniture> out = new ArrayList<ContraptionFurniture>();
        if (fakeLevel == null || records == null || records.isEmpty()) {
            return out;
        }
        for (ContraptionLevel.FurnitureRecord r : records) {
            try {
                Key definitionId = Key.of((String)r.definitionId());
                Vec3 localOffset = new Vec3(r.lx(), r.ly(), r.lz());
                BukkitFurniture live = ContraptionFurnitureCapture.placeInFakeLevel(fakeLevel, definitionId, r.variantName(), localOffset, r.yaw());
                out.add(new ContraptionFurniture(definitionId, r.variantName(), localOffset, r.yaw(), live));
            }
            catch (Throwable t) {
                Bukkit.getLogger().warning("[Contraption] furniture rehydrate: " + r.definitionId() + " variant=" + r.variantName() + " failed to re-place: " + String.valueOf(t));
            }
        }
        return out;
    }

    private static boolean belongsToStructure(BukkitFurniture furniture, Set<BlockPos> worldPositions) {
        List<FurnitureHitBox> hitboxes = furniture.hitboxes();
        boolean checkedAnyPart = false;
        if (hitboxes != null) {
            for (FurnitureHitBox hitBox : hitboxes) {
                List<FurnitureHitboxPart> parts = hitBox.parts();
                if (parts == null) continue;
                for (FurnitureHitboxPart part : parts) {
                    checkedAnyPart = true;
                    if (!ContraptionFurnitureCapture.aabbOverlapsAnyBlock(part.aabb(), worldPositions)) continue;
                    return true;
                }
            }
        }
        if (checkedAnyPart) {
            return false;
        }
        WorldPosition pos = furniture.position();
        BlockPos anchorBlock = BlockPos.containing((double)pos.x(), (double)pos.y(), (double)pos.z());
        if (worldPositions.contains(anchorBlock)) {
            return true;
        }
        for (Direction dir : Direction.values()) {
            if (!worldPositions.contains(anchorBlock.relative(dir))) continue;
            return true;
        }
        return false;
    }

    private static BukkitFurniture placeInFakeLevel(ContraptionLevel fakeLevel, Key definitionId, String variantName, Vec3 localOffset, float yaw) {
        if (fakeLevel == null) {
            return null;
        }
        try {
            BlockPos local = BlockPos.containing((double)localOffset.x, (double)localOffset.y, (double)localOffset.z);
            fakeLevel.ensureChunkReady(local);
            Location loc = new Location(fakeLevel.getWorld(), localOffset.x, localOffset.y, localOffset.z, yaw, 0.0f);
            BukkitFurniture placed = CraftEngineFurniture.place((Location)loc, (Key)definitionId, (String)variantName, (boolean)true);
            if (placed == null) {
                Bukkit.getLogger().warning("[Contraption] furniture capture: " + String.valueOf(definitionId) + " variant=" + variantName + " failed to place inside the hidden ContraptionLevel at " + String.valueOf(loc) + " \u2014 this piece will have no live backing instance (mirror falls back to static config).");
            }
            return placed;
        }
        catch (Throwable t) {
            Bukkit.getLogger().warning("[Contraption] furniture capture: " + String.valueOf(definitionId) + " threw while placing inside the hidden ContraptionLevel: " + String.valueOf(t));
            return null;
        }
    }

    public static void restoreFurniture(World bukkitWorld, List<ContraptionFurniture> furniture, BlockPos snappedBearingWorldPos, int quarterTurns) {
        if (furniture == null || furniture.isEmpty()) {
            return;
        }
        for (ContraptionFurniture cf : furniture) {
            if (cf.hasLiveFurniture()) {
                try {
                    CraftEngineFurniture.remove((Entity)cf.liveFurniture().getBukkitEntity(), (boolean)false, (boolean)false);
                }
                catch (Throwable t) {
                    Bukkit.getLogger().warning("[Contraption] furniture restore: " + String.valueOf(cf.definitionId()) + " threw while removing its live fake-level instance: " + String.valueOf(t));
                }
            }
            try {
                float yaw;
                double worldZ;
                double worldY;
                Vec3 local = cf.localOffset();
                Vec3 rotatedLocal = ContraptionFurnitureCapture.rotateLocalQuarterTurns(local, quarterTurns);
                double worldX = (double)snappedBearingWorldPos.getX() + rotatedLocal.x;
                Location loc = new Location(bukkitWorld, worldX, worldY = (double)snappedBearingWorldPos.getY() + rotatedLocal.y, worldZ = (double)snappedBearingWorldPos.getZ() + rotatedLocal.z, yaw = cf.yawOffsetDegrees() + (float)quarterTurns * 90.0f, 0.0f);
                BukkitFurniture placed = CraftEngineFurniture.place((Location)loc, (Key)cf.definitionId(), (String)cf.variantName(), (boolean)true);
                if (placed != null) continue;
                Bukkit.getLogger().warning("[Contraption] furniture restore: " + String.valueOf(cf.definitionId()) + " variant=" + cf.variantName() + " at " + String.valueOf(loc) + " failed to place (definition/variant no longer valid?) \u2014 this piece is lost on disassemble.");
            }
            catch (Throwable t) {
                Bukkit.getLogger().warning("[Contraption] furniture restore: " + String.valueOf(cf.definitionId()) + " threw during CraftEngineFurniture.place: " + String.valueOf(t));
            }
        }
    }

    private static Vec3 rotateLocalQuarterTurns(Vec3 local, int quarterTurns) {
        double x = local.x;
        double z = local.z;
        for (int i = 0; i < (quarterTurns % 4 + 4) % 4; ++i) {
            double newX = -z;
            double newZ = x;
            x = newX;
            z = newZ;
        }
        return new Vec3(x, local.y, z);
    }

    private static boolean aabbOverlapsAnyBlock(AABB aabb, Set<BlockPos> worldPositions) {
        if (aabb == null) {
            return false;
        }
        for (BlockPos p : worldPositions) {
            boolean overlapsZ;
            boolean overlapsX = aabb.maxX > (double)p.getX() && aabb.minX < (double)(p.getX() + 1);
            boolean overlapsY = aabb.maxY > (double)p.getY() && aabb.minY < (double)(p.getY() + 1);
            boolean bl = overlapsZ = aabb.maxZ > (double)p.getZ() && aabb.minZ < (double)(p.getZ() + 1);
            if (!overlapsX || !overlapsY || !overlapsZ) continue;
            return true;
        }
        return false;
    }

    public record Result(List<ContraptionFurniture> furniture, Map<UUID, Vec3> seatedRiders, Map<UUID, UUID> seatedRiderMounts) {
    }
}

