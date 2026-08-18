/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.data.InteractionData
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.entity.Player;

public final class ContraptionInteractionOverlayElement
implements ContraptionElement {
    private final Map<Integer, ContraptionElement> entityToElement = new HashMap<Integer, ContraptionElement>();
    private final Map<ContraptionElement, List<InteractionCell>> elementCells = new HashMap<ContraptionElement, List<InteractionCell>>();
    private static final double LOD_RADIUS_SQ = 100.0;

    @Override
    public Key type() {
        return ElementTypes.INTERACTION;
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
        return new int[0];
    }

    public ContraptionElement ownerOf(int entityId) {
        return this.entityToElement.get(entityId);
    }

    @Override
    public void render(RenderContext ctx) {
        List<ContraptionElement> elements = ctx.elements();
        if (elements == null) {
            return;
        }
        List<net.momirealms.craftengine.core.entity.player.Player> viewers = ctx.viewers();
        ArrayList<Vec3> viewerPos = new ArrayList<Vec3>(viewers.size());
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            Vec3 vec3;
            Object pp = p.platformPlayer();
            if (pp instanceof Player) {
                Player bp = (Player)pp;
                vec3 = new Vec3(bp.getLocation().getX(), bp.getLocation().getY(), bp.getLocation().getZ());
            } else {
                vec3 = null;
            }
            viewerPos.add(vec3);
        }
        HashSet<ContraptionElement> seen = new HashSet<ContraptionElement>();
        for (ContraptionElement element : elements) {
            List<AABB> bounds;
            if (element == this || (bounds = element.interactionBounds()).isEmpty()) continue;
            seen.add(element);
            List<InteractionCell> cells = this.elementCells.computeIfAbsent(element, e -> {
                ArrayList<InteractionCell> created = new ArrayList<InteractionCell>(bounds.size());
                for (AABB box : bounds) {
                    InteractionCell cell = new InteractionCell(box);
                    this.entityToElement.put(cell.entityId, (ContraptionElement)e);
                    created.add(cell);
                }
                return created;
            });
            if (cells.size() != bounds.size()) {
                for (InteractionCell c : cells) {
                    c.despawnAll(viewers);
                    this.entityToElement.remove(c.entityId);
                }
                cells.clear();
                for (AABB box : bounds) {
                    InteractionCell cell = new InteractionCell(box);
                    this.entityToElement.put(cell.entityId, element);
                    cells.add(cell);
                }
            }
            for (int i = 0; i < cells.size(); ++i) {
                InteractionCell cell = (InteractionCell)cells.get(i);
                AABB box = bounds.get(i);
                double cx = (box.minX + box.maxX) / 2.0;
                double cy = (box.minY + box.maxY) / 2.0;
                double cz = (box.minZ + box.maxZ) / 2.0;
                Vec3 worldCenter = ContraptionMath.renderPosition(new Vec3(cx, cy, cz), ctx.bearing(), ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());
                float w = (float)(Math.max(box.maxX - box.minX, box.maxZ - box.minZ) * ctx.scale());
                float h = (float)((box.maxY - box.minY) * ctx.scale());
                double spawnY = worldCenter.y - (double)h / 2.0;
                ArrayList<net.momirealms.craftengine.core.entity.player.Player> near = new ArrayList<net.momirealms.craftengine.core.entity.player.Player>();
                for (int j = 0; j < viewers.size(); ++j) {
                    Vec3 vp = (Vec3)viewerPos.get(j);
                    if (vp == null || !(vp.distanceToSqr(worldCenter) <= 100.0)) continue;
                    near.add(viewers.get(j));
                }
                cell.render(near, viewers, worldCenter.x, spawnY, worldCenter.z, w, h, ctx.moved());
            }
        }
        this.elementCells.entrySet().removeIf(entry -> {
            if (seen.contains(entry.getKey())) {
                return false;
            }
            for (InteractionCell c : (List<InteractionCell>)entry.getValue()) {
                c.despawnAll(viewers);
                this.entityToElement.remove(c.entityId);
            }
            return true;
        });
    }

    @Override
    public void despawn(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        for (List<InteractionCell> cells : this.elementCells.values()) {
            for (InteractionCell c : cells) {
                c.despawnAll(viewers);
            }
        }
        this.elementCells.clear();
        this.entityToElement.clear();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {
    }

    private static final class InteractionCell {
        final AABB localBox;
        final int entityId = Entity.nextEntityId();
        final UUID uuid = UUID.randomUUID();
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        InteractionCell(AABB localBox) {
            this.localBox = localBox;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId));
        }

        void render(List<net.momirealms.craftengine.core.entity.player.Player> near, List<net.momirealms.craftengine.core.entity.player.Player> all, double x, double y, double z, float w, float h, boolean moved) {
            HashSet<UUID> current = new HashSet<UUID>();
            for (net.momirealms.craftengine.core.entity.player.Player p : near) {
                UUID id2 = InteractionCell.uuidOf(p);
                if (id2 == null) continue;
                current.add(id2);
                if (this.shownTo.add(id2)) {
                    this.spawn(p, x, y, z, w, h);
                    continue;
                }
                if (!moved) continue;
                p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.entityId, x, y, z, 0.0f, 0.0f, false), false);
            }
            this.shownTo.removeIf(id -> {
                if (current.contains(id)) {
                    return false;
                }
                for (net.momirealms.craftengine.core.entity.player.Player p : all) {
                    if (!id.equals(InteractionCell.uuidOf(p))) continue;
                    p.sendPacket(this.despawnPacket, false);
                    break;
                }
                return true;
            });
        }

        void spawn(net.momirealms.craftengine.core.entity.player.Player p, double x, double y, double z, float w, float h) {
            ArrayList meta = new ArrayList();
            InteractionData.Width.addEntityData(Float.valueOf(w), meta);
            InteractionData.Height.addEntityData(Float.valueOf(h), meta);
            InteractionData.Response.addEntityData(false, meta);
            p.sendPackets(List.of(MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, 0.0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0.0), MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, meta)), false);
        }

        void despawnAll(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                if (!this.shownTo.remove(InteractionCell.uuidOf(p))) continue;
                p.sendPacket(this.despawnPacket, false);
            }
        }

        private static UUID uuidOf(net.momirealms.craftengine.core.entity.player.Player p) {
            UUID uUID;
            Object pp = p.platformPlayer();
            if (pp instanceof Player) {
                Player b = (Player)pp;
                uUID = b.getUniqueId();
            } else {
                uUID = null;
            }
            return uUID;
        }
    }
}

