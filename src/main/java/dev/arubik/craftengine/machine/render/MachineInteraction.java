package dev.arubik.craftengine.machine.render;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import dev.arubik.craftengine.util.MNms;

/**
 * A packet-only {@code INTERACTION} entity backing a {@link RendererSpec.InteractionSpec} renderer
 * element — same technique as {@code dev.arubik.craftengine.chainery.ChainInteraction}, adapted to
 * the {@code RendererManager}'s per-spec, distance-gated {@code ServerLevel.players()} render loop
 * (see {@link RendererManager.ArmorStandDisplay}/{@code BlockDisplayHelper} for that shape). No real
 * server-side entity exists under this id — {@link MachineInteractPacketListener} resolves a clicked
 * entity id back to its {@link Ref} through {@link #refOf(int)}.
 */
public final class MachineInteraction {

    private static final double VIEW_DISTANCE_SQ = 2304.0;

    /** What a clicked interaction entity id maps back to — the script to run, the owning
     *  {@link RendererManager} whose {@link RendererManager.InteractScriptRunner} actually runs it,
     *  and the marker itself (so the packet listener can read its last rendered offset — see
     *  {@link #lastOffset()}). */
    public record Ref(String onInteractRef, RendererManager owner, MachineInteraction marker) {
    }

    /** Global entity-id → owner, so the packet listener can resolve any clicked marker. */
    private static final Map<Integer, Ref> BY_ENTITY = new ConcurrentHashMap<>();

    public static Ref refOf(int entityId) {
        return BY_ENTITY.get(entityId);
    }

    private final int entityId = Entity.nextEntityId();
    private final UUID entityUuid = UUID.randomUUID();
    private final Object despawnPacket;
    private final Set<UUID> shownTo = new HashSet<>();
    private float width = 0.5f;
    private float height = 0.5f;
    /** The offset (from the machine's own position) this marker was last rendered at — see
     *  {@link RendererManager}'s {@code InteractionSpec} dispatch branch, which keeps this current
     *  every tick the marker is active. Read by {@link MachineInteractPacketListener} at click time
     *  so a multi-block machine can work out WHICH part was touched. */
    private volatile double[] lastOffset = new double[]{0, 0, 0};

    public MachineInteraction(String onInteractRef, RendererManager owner) {
        this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        BY_ENTITY.put(entityId, new Ref(onInteractRef, owner, this));
    }

    /** Sets the hitbox size (metadata is only re-sent on spawn, so set before first render). */
    public void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public void setLastOffset(double dx, double dy, double dz) {
        this.lastOffset = new double[]{dx, dy, dz};
    }

    public double[] lastOffset() {
        return this.lastOffset;
    }

    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        InteractionData.Width.addEntityData(width, values);
        InteractionData.Height.addEntityData(height, values);
        InteractionData.Response.addEntityData(true, values); // register clicks (hit feedback)
        return values;
    }

    private void spawn(ServerPlayer sp, double wx, double wy, double wz) {
        // INTERACTION is bottom-anchored (Y from entity up to +height); drop by height/2 so the box
        // centres on the resolved location, matching ChainInteraction's own convention.
        Object add = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(entityId, entityUuid, wx,
                wy - height / 2.0, wz, 0f, 0f, EntityType.INTERACTION, 0, Vec3.ZERO, 0);
        sendPacket(sp, add);
        sendPacket(sp, MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata()));
    }

    private void updatePosition(ServerPlayer sp, double wx, double wy, double wz) {
        sendPacket(sp, MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(entityId, wx,
                wy - height / 2.0, wz, 0f, 0f, false));
    }

    /** Same distance-gated, per-viewer spawn/move/despawn shape as the other per-spec render helpers
     *  in {@link RendererManager} (e.g. {@code ArmorStandDisplay}) — every player within view range of
     *  the machine sees and can click the hitbox. */
    public void render(ServerLevel serverLevel, double wx, double wy, double wz) {
        if (serverLevel == null) {
            return;
        }
        HashSet<UUID> stillVisible = new HashSet<>();
        for (ServerPlayer sp : serverLevel.players()) {
            double dx = sp.getX() - wx;
            double dy = sp.getY() - wy;
            double dz = sp.getZ() - wz;
            if (dx * dx + dy * dy + dz * dz > VIEW_DISTANCE_SQ) {
                if (shownTo.contains(sp.getUUID())) {
                    sendPacket(sp, despawnPacket);
                }
                continue;
            }
            stillVisible.add(sp.getUUID());
            if (shownTo.contains(sp.getUUID())) {
                updatePosition(sp, wx, wy, wz);
            } else {
                spawn(sp, wx, wy, wz);
            }
        }
        shownTo.retainAll(stillVisible);
        shownTo.addAll(stillVisible);
    }

    public void despawnAll(ServerLevel serverLevel) {
        if (shownTo.isEmpty()) {
            return;
        }
        if (serverLevel != null) {
            for (ServerPlayer sp : serverLevel.players()) {
                if (shownTo.contains(sp.getUUID())) {
                    sendPacket(sp, despawnPacket);
                }
            }
        } else {
            for (Player bp : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (!shownTo.contains(bp.getUniqueId())) {
                    continue;
                }
                try {
                    ServerPlayer sp = ((CraftPlayer) bp).getHandle();
                    sp.connection.send((Packet) despawnPacket);
                } catch (Throwable ignored) {
                }
            }
        }
        shownTo.clear();
    }

    /** Drops this marker from the global click-lookup map — called once the marker is discarded for
     *  good (machine removed) so a stale entity id never resolves to a dangling {@link Ref}. */
    public void discard() {
        BY_ENTITY.remove(entityId);
    }

    private static void sendPacket(ServerPlayer sp, Object packet) {
        try {
            sp.connection.send((Packet) packet);
        } catch (Throwable ignored) {
        }
    }
}
