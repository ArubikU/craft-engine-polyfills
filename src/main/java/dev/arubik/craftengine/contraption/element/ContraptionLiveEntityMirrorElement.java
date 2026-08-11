package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.entity.data.item.ItemEntityData;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Single element managing all live entity mirrors inside the ContraptionLevel.
 * Replaces ContraptionEntityMirrorSwarm — walks level.getAllEntities() each tick,
 * spawns/despawns packet mirrors dynamically. One instance per contraption.
 */
public final class ContraptionLiveEntityMirrorElement implements ContraptionElement {

    private final Map<UUID, Mirror> mirrors = new HashMap<>();
    private List<ContraptionFurniture> furniture = List.of();

    public ContraptionLiveEntityMirrorElement() {}

    public void setFurniture(List<ContraptionFurniture> furniture) {
        this.furniture = furniture != null ? furniture : List.of();
    }

    @Override
    public Key type() {
        return ElementTypes.ENTITY;
    }

    @Override
    public Vec3 localOffset() {
        return Vec3.ZERO;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int[] entityIds() {
        int[] ids = new int[mirrors.size()];
        int i = 0;
        for (Mirror m : mirrors.values()) {
            ids[i++] = m.entityId;
        }
        return ids;
    }

    @Override
    public void tick(RenderContext ctx) {
        // Nothing — render handles the dynamic entity walk
    }

    @Override
    public void render(RenderContext ctx) {
        ContraptionLevel level = ctx.level();
        if (level == null) return;

        Set<Integer> furnitureIds = collectFurnitureOwnedEntityIds();
        Set<UUID> present = new HashSet<>();
        float yawDegrees = (float) Math.toDegrees(ctx.yawRadians());
        double pitchRadians = ctx.pitchRadians();
        double rollRadians = ctx.rollRadians();
        double scale = ctx.scale();

        for (Entity entity : level.getAllEntities()) {
            if (entity == null || entity.isRemoved()) continue;
            if (furnitureIds.contains(entity.getId())) continue;
            if (!canMirror(entity)) {
                eject(entity, level);
                continue;
            }
            UUID id = entity.getUUID();
            present.add(id);
            Mirror mirror = mirrors.computeIfAbsent(id, k -> new Mirror(entityTypeFor(entity)));
            mirror.updateAppearance(entity);
            if (mirror.type == EntityType.BLOCK_DISPLAY) {
                mirror.setOrientation((float) pitchRadians, (float) rollRadians, (float) scale);
                Vec3 real = level.realWorldPositionOf(entity.position().add(0.0, 0.5 * scale, 0.0));
                mirror.render(ctx.viewers(), real.x, real.y, real.z, yawDegrees);
            } else {
                Vec3 real = level.realWorldPositionOf(entity.position());
                mirror.render(ctx.viewers(), real.x, real.y, real.z, yawDegrees);
            }
        }

        mirrors.entrySet().removeIf(e -> {
            if (present.contains(e.getKey())) return false;
            e.getValue().despawnAll(ctx.viewers());
            return true;
        });
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Mirror m : mirrors.values()) {
            m.despawnAll(viewers);
        }
        mirrors.clear();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
        // Live entities are ejected on disassemble by ContraptionAssembler — nothing to do here
    }

    // ---- internals ----

    private Set<Integer> collectFurnitureOwnedEntityIds() {
        if (furniture.isEmpty()) return Set.of();
        Set<Integer> ids = new HashSet<>();
        for (ContraptionFurniture cf : furniture) {
            if (!cf.hasLiveFurniture()) continue;
            try {
                BukkitFurniture live = cf.liveFurniture();
                ids.add(live.entityId());
                int[] interactable = live.interactableEntityIds();
                if (interactable != null) for (int i : interactable) ids.add(i);
                int[] colliders = live.colliderEntityIds();
                if (colliders != null) for (int i : colliders) ids.add(i);
            } catch (Throwable ignored) {}
        }
        return ids;
    }

    private static boolean canMirror(Entity entity) {
        return entity instanceof ItemEntity
                || entity instanceof net.minecraft.world.entity.item.FallingBlockEntity;
    }

    private static void eject(Entity entity, ContraptionLevel level) {
        try {
            if (!(level.realLevel() instanceof ServerLevel realLevel)) return;
            Vec3 real = level.realWorldPositionOf(entity.position());
            org.bukkit.World world = realLevel.getWorld();
            org.bukkit.entity.Entity bukkit = entity.getBukkitEntity();
            org.bukkit.Location dest = new org.bukkit.Location(world, real.x, real.y, real.z,
                    bukkit.getLocation().getYaw(), bukkit.getLocation().getPitch());
            bukkit.teleport(dest);
        } catch (Throwable ignored) {}
    }

    private static EntityType<?> entityTypeFor(Entity entity) {
        if (entity instanceof net.minecraft.world.entity.item.FallingBlockEntity) {
            return EntityType.BLOCK_DISPLAY;
        }
        return EntityType.ITEM;
    }

    // ---- Mirror cell ----

    private static final class Mirror {
        final int entityId = net.minecraft.world.entity.Entity.nextEntityId();
        final UUID uuid = UUID.randomUUID();
        final EntityType<?> type;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        Object nmsItemStack;
        net.minecraft.world.level.block.state.BlockState blockState;
        float pitch, roll, scaleF = 1f;
        boolean metaDirty = true;

        Mirror(EntityType<?> type) {
            this.type = type;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        void setOrientation(float pitch, float roll, float scale) {
            if (pitch != this.pitch || roll != this.roll || scale != this.scaleF) {
                this.pitch = pitch;
                this.roll = roll;
                this.scaleF = scale;
                this.metaDirty = true;
            }
        }

        void updateAppearance(Entity source) {
            if (source instanceof ItemEntity itemEntity) {
                Object nms = itemEntity.getItem().copy();
                if (!Objects.equals(nms, nmsItemStack)) {
                    nmsItemStack = nms;
                    metaDirty = true;
                }
            } else if (source instanceof net.minecraft.world.entity.item.FallingBlockEntity falling) {
                var bs = falling.getBlockState();
                if (!bs.equals(this.blockState)) {
                    this.blockState = bs;
                    this.metaDirty = true;
                }
            }
        }

        List<Object> metadata() {
            List<Object> values = new ArrayList<>();
            if (type == EntityType.ITEM && nmsItemStack != null) {
                ItemEntityData.Item.addEntityData(nmsItemStack, values);
            } else if (type == EntityType.BLOCK_DISPLAY && blockState != null) {
                DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, values);
                float s = scaleF;
                if (pitch == 0f && roll == 0f) {
                    DisplayData.Translation.addEntityData(
                            new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), values);
                    if (s != 1f) {
                        DisplayData.Scale.addEntityData(new org.joml.Vector3f(s, s, s), values);
                    }
                } else {
                    org.joml.Quaternionf q = new org.joml.Quaternionf().rotateX(pitch).rotateZ(roll);
                    org.joml.Vector3f t = q.transform(new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s));
                    DisplayData.Translation.addEntityData(t, values);
                    DisplayData.LeftRotation.addEntityData(q, values);
                    DisplayData.Scale.addEntityData(new org.joml.Vector3f(s, s, s), values);
                }
                DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
            }
            return values;
        }

        void render(List<Player> viewers, double x, double y, double z, float yawDeg) {
            boolean forceMeta = metaDirty;
            metaDirty = false;
            for (Player p : viewers) {
                UUID id = p.uuid();
                if (shownTo.add(id)) {
                    Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                            entityId, uuid, x, y, z, 0f, yawDeg, type, 0, Vec3.ZERO, 0);
                    Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata());
                    p.sendPackets(List.of(add, data), false);
                } else {
                    p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(
                            entityId, x, y, z, yawDeg, 0f, false), false);
                    if (forceMeta) {
                        p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
                    }
                }
            }
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                p.sendPacket(despawnPacket, false);
            }
            shownTo.clear();
        }
    }
}
