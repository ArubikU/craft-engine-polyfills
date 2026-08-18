/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.DisplayData$BlockDisplayData
 *  net.momirealms.craftengine.bukkit.entity.data.item.ItemEntityData
 *  net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.bukkit.entity.data.item.ItemEntityData;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ContraptionLiveEntityMirrorElement
implements ContraptionElement {
    private final Map<UUID, Mirror> mirrors = new HashMap<UUID, Mirror>();
    private List<ContraptionFurniture> furniture = List.of();

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
        int[] ids = new int[this.mirrors.size()];
        int i = 0;
        for (Mirror m : this.mirrors.values()) {
            ids[i++] = m.entityId;
        }
        return ids;
    }

    @Override
    public void tick(RenderContext ctx) {
    }

    @Override
    public void render(RenderContext ctx) {
        ContraptionLevel level = ctx.level();
        if (level == null) {
            return;
        }
        Set<Integer> furnitureIds = this.collectFurnitureOwnedEntityIds();
        HashSet<UUID> present = new HashSet<UUID>();
        float yawDegrees = (float)Math.toDegrees(ctx.yawRadians());
        double pitchRadians = ctx.pitchRadians();
        double rollRadians = ctx.rollRadians();
        double scale = ctx.scale();
        for (Entity entity : level.getAllEntities()) {
            Vec3 real;
            if (entity == null || entity.isRemoved() || furnitureIds.contains(entity.getId()) || ContraptionLiveEntityMirrorElement.isHandledElsewhere(entity)) continue;
            if (!ContraptionLiveEntityMirrorElement.canMirror(entity)) {
                ContraptionLiveEntityMirrorElement.eject(entity, level);
                continue;
            }
            UUID id = entity.getUUID();
            present.add(id);
            Mirror mirror = this.mirrors.computeIfAbsent(id, k -> new Mirror(ContraptionLiveEntityMirrorElement.entityTypeFor(entity)));
            mirror.updateAppearance(entity);
            if (mirror.type == EntityType.BLOCK_DISPLAY) {
                mirror.setOrientation((float)pitchRadians, (float)rollRadians, (float)scale);
                real = level.realWorldPositionOf(entity.position().add(0.0, 0.5 * scale, 0.0));
                mirror.render(ctx.viewers(), real.x, real.y, real.z, yawDegrees);
                continue;
            }
            real = level.realWorldPositionOf(entity.position());
            mirror.render(ctx.viewers(), real.x, real.y, real.z, yawDegrees);
        }
        this.mirrors.entrySet().removeIf(e -> {
            if (present.contains(e.getKey())) {
                return false;
            }
            ((Mirror)e.getValue()).despawnAll(ctx.viewers());
            return true;
        });
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (Mirror m : this.mirrors.values()) {
            m.despawnAll(viewers);
        }
        this.mirrors.clear();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    private Set<Integer> collectFurnitureOwnedEntityIds() {
        if (this.furniture.isEmpty()) {
            return Set.of();
        }
        HashSet<Integer> ids = new HashSet<Integer>();
        for (ContraptionFurniture cf : this.furniture) {
            if (!cf.hasLiveFurniture()) continue;
            try {
                int[] colliders;
                BukkitFurniture live = cf.liveFurniture();
                ids.add(live.entityId());
                int[] interactable = live.interactableEntityIds();
                if (interactable != null) {
                    for (int i : interactable) {
                        ids.add(i);
                    }
                }
                if ((colliders = live.colliderEntityIds()) == null) continue;
                for (int i : colliders) {
                    ids.add(i);
                }
            }
            catch (Throwable throwable) {
            }
        }
        return ids;
    }

    private static boolean canMirror(Entity entity) {
        return entity instanceof ItemEntity || entity instanceof FallingBlockEntity;
    }

    private static boolean isHandledElsewhere(Entity entity) {
        return entity instanceof ItemFrame;
    }

    private static void eject(Entity entity, ContraptionLevel level) {
        try {
            Level level2 = level.realLevel();
            if (!(level2 instanceof ServerLevel)) {
                return;
            }
            ServerLevel realLevel = (ServerLevel)level2;
            Vec3 real = level.realWorldPositionOf(entity.position());
            CraftWorld world = realLevel.getWorld();
            CraftEntity bukkit = entity.getBukkitEntity();
            Location dest = new Location((World)world, real.x, real.y, real.z, bukkit.getLocation().getYaw(), bukkit.getLocation().getPitch());
            bukkit.teleport(dest);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static EntityType<?> entityTypeFor(Entity entity) {
        if (entity instanceof FallingBlockEntity) {
            return EntityType.BLOCK_DISPLAY;
        }
        return EntityType.ITEM;
    }

    private static final class Mirror {
        final int entityId = Entity.nextEntityId();
        final UUID uuid = UUID.randomUUID();
        final EntityType<?> type;
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        Object nmsItemStack;
        BlockState blockState;
        float pitch;
        float roll;
        float scaleF = 1.0f;
        boolean metaDirty = true;

        Mirror(EntityType<?> type) {
            this.type = type;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
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
            FallingBlockEntity falling;
            BlockState bs;
            if (source instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity)source;
                ItemStack nms = itemEntity.getItem().copy();
                if (!Objects.equals(nms, this.nmsItemStack)) {
                    this.nmsItemStack = nms;
                    this.metaDirty = true;
                }
            } else if (source instanceof FallingBlockEntity && !(bs = (falling = (FallingBlockEntity)source).getBlockState()).equals(this.blockState)) {
                this.blockState = bs;
                this.metaDirty = true;
            }
        }

        List<Object> metadata() {
            ArrayList<Object> values = new ArrayList<Object>();
            if (this.type == EntityType.ITEM && this.nmsItemStack != null) {
                ItemEntityData.Item.addEntityData(this.nmsItemStack, values);
            } else if (this.type == EntityType.BLOCK_DISPLAY && this.blockState != null) {
                DisplayData.BlockDisplayData.BlockState.addEntityData(this.blockState, values);
                float s = this.scaleF;
                if (this.pitch == 0.0f && this.roll == 0.0f) {
                    DisplayData.Translation.addEntityData(new Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), values);
                    if (s != 1.0f) {
                        DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
                    }
                } else {
                    Quaternionf q = new Quaternionf().rotateX(this.pitch).rotateZ(this.roll);
                    Vector3f t = q.transform(new Vector3f(-0.5f * s, -0.5f * s, -0.5f * s));
                    DisplayData.Translation.addEntityData(t, values);
                    DisplayData.LeftRotation.addEntityData(q, values);
                    DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
                }
                DisplayData.BrightnessOverride.addEntityData(0xF000F0, values);
            }
            return values;
        }

        void render(List<Player> viewers, double x, double y, double z, float yawDeg) {
            boolean forceMeta = this.metaDirty;
            this.metaDirty = false;
            for (Player p : viewers) {
                UUID id = p.uuid();
                if (this.shownTo.add(id)) {
                    Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, yawDeg, this.type, 0, Vec3.ZERO, 0.0);
                    Object data = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata());
                    p.sendPackets(List.of(add, data), false);
                    continue;
                }
                p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, x, y, z, yawDeg, 0.0f, false), false);
                if (!forceMeta) continue;
                p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata()), false);
            }
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) {
                p.sendPacket(this.despawnPacket, false);
            }
            this.shownTo.clear();
        }
    }
}

