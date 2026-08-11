package dev.arubik.craftengine.contraption.element;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages packet-only INTERACTION entities for every element in a contraption.
 * Reads interactionBounds() from each element, handles LOD spawn/despawn,
 * maps entity IDs back to elements for onInteract dispatch.
 */
public final class ContraptionInteractionOverlayElement implements ContraptionElement {

    /** entity ID → owning element */
    private final Map<Integer, ContraptionElement> entityToElement = new HashMap<>();
    private final Map<ContraptionElement, List<InteractionCell>> elementCells = new HashMap<>();

    private static final double LOD_RADIUS_SQ = 10.0 * 10.0;

    @Override public Key type() { return ElementTypes.INTERACTION; }
    @Override public Vec3 localOffset() { return Vec3.ZERO; }
    @Override public boolean isValid() { return true; }
    @Override public int[] entityIds() { return new int[0]; }

    /** Resolve which element owns an interaction entity ID. */
    public ContraptionElement ownerOf(int entityId) {
        return entityToElement.get(entityId);
    }

    @Override
    public void render(RenderContext ctx) {
        List<ContraptionElement> elements = ctx.elements();
        if (elements == null) return;

        // Resolve viewer positions once
        List<Player> viewers = ctx.viewers();
        List<Vec3> viewerPos = new ArrayList<>(viewers.size());
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            viewerPos.add(pp instanceof org.bukkit.entity.Player bp
                ? new Vec3(bp.getLocation().getX(), bp.getLocation().getY(), bp.getLocation().getZ())
                : null);
        }

        Set<ContraptionElement> seen = new HashSet<>();

        for (ContraptionElement element : elements) {
            if (element == this) continue;
            List<AABB> bounds = element.interactionBounds();
            if (bounds.isEmpty()) continue;
            seen.add(element);

            List<InteractionCell> cells = elementCells.computeIfAbsent(element, e -> {
                List<InteractionCell> created = new ArrayList<>(bounds.size());
                for (AABB box : bounds) {
                    InteractionCell cell = new InteractionCell(box);
                    entityToElement.put(cell.entityId, e);
                    created.add(cell);
                }
                return created;
            });

            // Sync cells if bounds changed
            if (cells.size() != bounds.size()) {
                for (InteractionCell c : cells) { c.despawnAll(viewers); entityToElement.remove(c.entityId); }
                cells.clear();
                for (AABB box : bounds) {
                    InteractionCell cell = new InteractionCell(box);
                    entityToElement.put(cell.entityId, element);
                    cells.add(cell);
                }
            }

            for (int i = 0; i < cells.size(); i++) {
                InteractionCell cell = cells.get(i);
                AABB box = bounds.get(i);
                double cx = (box.minX + box.maxX) / 2.0;
                double cy = (box.minY + box.maxY) / 2.0;
                double cz = (box.minZ + box.maxZ) / 2.0;
                Vec3 worldCenter = ContraptionMath.renderPosition(
                    new Vec3(cx, cy, cz), ctx.bearing(),
                    ctx.yawRadians(), ctx.pitchRadians(), ctx.rollRadians(), ctx.scale());

                float w = (float)(Math.max(box.maxX - box.minX, box.maxZ - box.minZ) * ctx.scale());
                float h = (float)((box.maxY - box.minY) * ctx.scale());
                double spawnY = worldCenter.y - h / 2.0;

                List<Player> near = new ArrayList<>();
                for (int j = 0; j < viewers.size(); j++) {
                    Vec3 vp = viewerPos.get(j);
                    if (vp != null && vp.distanceToSqr(worldCenter) <= LOD_RADIUS_SQ) near.add(viewers.get(j));
                }
                cell.render(near, viewers, worldCenter.x, spawnY, worldCenter.z, w, h, ctx.moved());
            }
        }

        // Despawn cells for elements no longer present
        elementCells.entrySet().removeIf(entry -> {
            if (seen.contains(entry.getKey())) return false;
            for (InteractionCell c : entry.getValue()) { c.despawnAll(viewers); entityToElement.remove(c.entityId); }
            return true;
        });
    }

    @Override
    public void despawn(List<Player> viewers) {
        for (List<InteractionCell> cells : elementCells.values()) {
            for (InteractionCell c : cells) c.despawnAll(viewers);
        }
        elementCells.clear();
        entityToElement.clear();
    }

    @Override
    public void disassemble(ServerLevel level, BlockPos bearingPos, int quarterTurns) {}

    // ---- InteractionCell ----

    private static final class InteractionCell {
        final AABB localBox;
        final int entityId = net.minecraft.world.entity.Entity.nextEntityId();
        final UUID uuid = UUID.randomUUID();
        final Object despawnPacket;
        final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        InteractionCell(AABB localBox) {
            this.localBox = localBox;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        void render(List<Player> near, List<Player> all, double x, double y, double z, float w, float h, boolean moved) {
            Set<UUID> current = new HashSet<>();
            for (Player p : near) {
                UUID id = uuidOf(p); if (id == null) continue;
                current.add(id);
                if (shownTo.add(id)) spawn(p, x, y, z, w, h);
                else if (moved) p.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, 0f, 0f, false), false);
            }
            // LOD despawn
            shownTo.removeIf(id -> {
                if (current.contains(id)) return false;
                for (Player p : all) { if (id.equals(uuidOf(p))) { p.sendPacket(despawnPacket, false); break; } }
                return true;
            });
        }

        void spawn(Player p, double x, double y, double z, float w, float h) {
            List<Object> meta = new ArrayList<>();
            InteractionData.Width.addEntityData(w, meta);
            InteractionData.Height.addEntityData(h, meta);
            InteractionData.Response.addEntityData(false, meta);
            p.sendPackets(List.of(
                MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, uuid, x, y, z, 0f, 0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0),
                MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, meta)
            ), false);
        }

        void despawnAll(List<Player> viewers) {
            for (Player p : viewers) { if (shownTo.remove(uuidOf(p))) p.sendPacket(despawnPacket, false); }
        }

        private static UUID uuidOf(Player p) {
            Object pp = p.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }
}
