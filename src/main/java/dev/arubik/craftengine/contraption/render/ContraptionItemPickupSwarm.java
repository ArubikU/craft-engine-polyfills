/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

public final class ContraptionItemPickupSwarm {
    private final Map<UUID, UUID> mirrors = new HashMap<UUID, UUID>();
    private static final Map<UUID, UUID> MIRROR_TO_SOURCE = new ConcurrentHashMap<UUID, UUID>();
    private static final Map<UUID, ContraptionLevel> SOURCE_LEVEL = new ConcurrentHashMap<UUID, ContraptionLevel>();

    static UUID sourceOf(UUID mirrorUuid) {
        return MIRROR_TO_SOURCE.get(mirrorUuid);
    }

    static ContraptionLevel levelOf(UUID sourceUuid) {
        return SOURCE_LEVEL.get(sourceUuid);
    }

    public static boolean isMirror(UUID mirrorUuid) {
        return MIRROR_TO_SOURCE.containsKey(mirrorUuid);
    }

    static void forgetSource(UUID sourceUuid) {
        SOURCE_LEVEL.remove(sourceUuid);
    }

    public void tick(ContraptionLevel level) {
        if (level == null) {
            return;
        }
        Level realLevel = level.realLevel();
        if (!(realLevel instanceof ServerLevel)) {
            return;
        }
        ServerLevel realServerLevel = (ServerLevel)realLevel;
        HashSet<UUID> present = new HashSet<UUID>();
        for (Entity entity : level.getAllEntities()) {
            ItemEntity mirror;
            ItemEntity internal;
            if (!(entity instanceof ItemEntity) || (internal = (ItemEntity)entity).isRemoved()) continue;
            UUID sourceId = internal.getUUID();
            present.add(sourceId);
            Vec3 realPos = level.realWorldPositionOf(internal.position());
            UUID mirrorId = this.mirrors.get(sourceId);
            ItemEntity itemEntity = mirror = mirrorId != null ? ContraptionItemPickupSwarm.findByUuid(realServerLevel, mirrorId) : null;
            if (mirror == null || mirror.isRemoved()) {
                mirror = ContraptionItemPickupSwarm.spawnMirror(realServerLevel, internal, realPos);
                if (mirror == null) continue;
                this.mirrors.put(sourceId, mirror.getUUID());
                MIRROR_TO_SOURCE.put(mirror.getUUID(), sourceId);
                SOURCE_LEVEL.put(sourceId, level);
                continue;
            }
            if (!ItemEntity.areMergable((ItemStack)mirror.getItem(), (ItemStack)internal.getItem()) || mirror.getItem().getCount() != internal.getItem().getCount()) {
                mirror.setItem(internal.getItem().copy());
            }
            if (!ContraptionItemPickupSwarm.targetIsObstructed(realServerLevel, mirror, realPos)) {
                mirror.snapTo(realPos.x, realPos.y, realPos.z, mirror.getYRot(), mirror.getXRot());
            }
            mirror.setDeltaMovement(Vec3.ZERO);
        }
        this.mirrors.entrySet().removeIf(e -> {
            if (present.contains(e.getKey())) {
                return false;
            }
            ItemEntity mirror = ContraptionItemPickupSwarm.findByUuid(realServerLevel, (UUID)e.getValue());
            if (mirror != null) {
                mirror.discard();
            }
            MIRROR_TO_SOURCE.remove(e.getValue());
            SOURCE_LEVEL.remove(e.getKey());
            return true;
        });
    }

    public void despawnAll() {
        block0: for (Map.Entry<UUID, UUID> e : this.mirrors.entrySet()) {
            SOURCE_LEVEL.remove(e.getKey());
            UUID mirrorId = e.getValue();
            MIRROR_TO_SOURCE.remove(mirrorId);
            for (World w : Bukkit.getWorlds()) {
                ServerLevel sl = ((CraftWorld)w).getHandle();
                ItemEntity mirror = ContraptionItemPickupSwarm.findByUuid(sl, mirrorId);
                if (mirror == null) continue;
                mirror.setNoGravity(false);
                mirror.setPickUpDelay(10);
                continue block0;
            }
        }
        this.mirrors.clear();
    }

    public int mirrorCount() {
        return this.mirrors.size();
    }

    private static ItemEntity spawnMirror(ServerLevel realServerLevel, ItemEntity internal, Vec3 realPos) {
        ItemEntity mirror = new ItemEntity((Level)realServerLevel, realPos.x, realPos.y, realPos.z, internal.getItem().copy());
        if (!realServerLevel.noCollision((Entity)mirror, mirror.getBoundingBox())) {
            Vec3 fallback = internal.position();
            mirror.setPos(fallback.x, fallback.y, fallback.z);
            if (!realServerLevel.noCollision((Entity)mirror, mirror.getBoundingBox())) {
                return null;
            }
        }
        mirror.setDeltaMovement(Vec3.ZERO);
        mirror.setNoGravity(true);
        mirror.setPickUpDelay(0);
        realServerLevel.addFreshEntity((Entity)mirror);
        return mirror;
    }

    private static boolean targetIsObstructed(ServerLevel realServerLevel, ItemEntity mirror, Vec3 target) {
        AABB targetBox = mirror.getBoundingBox().move(target.subtract(mirror.position()));
        return !realServerLevel.noCollision((Entity)mirror, targetBox);
    }

    private static ItemEntity findByUuid(ServerLevel level, UUID uuid) {
        ItemEntity ie;
        Entity e = level.getEntity(uuid);
        return e instanceof ItemEntity ? (ie = (ItemEntity)e) : null;
    }
}

