package dev.arubik.craftengine.chainery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.ints.IntList;
import net.momirealms.craftengine.bukkit.entity.data.InteractionData;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * A packet-only {@code INTERACTION} entity covering one chain link so the chain is CLICKABLE (CHAINERY —
 * "las interaction deben ser por packets y la detección de interacción tmb por packets"). Spawned/moved via
 * {@code MNms} packets, per-viewer, no real entity — {@code ChainInteractPacketListener} resolves a clicked
 * entity id back to its chain+segment through {@link #refOf(int)} and fires a {@link ChainInteractEvent}.
 */
public final class ChainInteraction {

    /** What a clicked interaction entity id maps back to. */
    public record Ref(UUID chainId, int segment) {
    }

    /** Global entity-id → chain+segment index, so the packet listener can resolve any clicked link. */
    private static final Map<Integer, Ref> BY_ENTITY = new ConcurrentHashMap<>();

    public static Ref refOf(int entityId) {
        return BY_ENTITY.get(entityId);
    }

    private final int entityId = nextEntityId();
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;
    private final java.util.Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
    private float width = 0.45f;
    private float height = 0.45f;

    public ChainInteraction(UUID chainId, int segment) {
        this.despawnPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        BY_ENTITY.put(entityId, new Ref(chainId, segment));
    }

    /** Sets the hitbox size (metadata is only re-sent on spawn, so set before first render). */
    public void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        InteractionData.Width.addEntityData(width, values);
        InteractionData.Height.addEntityData(height, values);
        InteractionData.Response.addEntityData(true, values); // register clicks (hit feedback)
        return values;
    }

    private void spawn(Player player, double x, double y, double z) {
        // INTERACTION box is bottom-anchored (Y from entity up to +height); drop the entity by height/2 so the
        // box centres on the link's midpoint.
        Object add = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, uuid, x, y - height / 2.0, z, 0f, 0f,
                net.minecraft.world.entity.EntityType.INTERACTION, 0, net.minecraft.world.phys.Vec3.ZERO, 0);
        Object data = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundSetEntityDataPacket(entityId, metadata());
        player.sendPackets(List.of(add, data), false);
    }

    private void updatePosition(Player player, double x, double y, double z) {
        player.sendPacket(dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y - height / 2.0, z, 0f, 0f, false),
                false);
    }

    public void despawn(Player player) {
        player.sendPacket(despawnPacket, false);
    }

    /** Per-viewer spawn/move, same shape as the other chain packet entities. */
    public void render(List<Player> viewers, double x, double y, double z) {
        java.util.Set<UUID> current = new java.util.HashSet<>();
        for (Player p : viewers) {
            UUID id = uuidOf(p);
            if (id == null) {
                continue;
            }
            current.add(id);
            if (shownTo.add(id)) {
                spawn(p, x, y, z);
            } else {
                updatePosition(p, x, y, z);
            }
        }
        shownTo.retainAll(current);
    }

    /** Despawns for the given viewers and drops this entity from the global lookup. */
    public void remove(List<Player> viewers) {
        for (Player p : viewers) {
            despawn(p);
        }
        shownTo.clear();
        BY_ENTITY.remove(entityId);
    }

    private static UUID uuidOf(Player player) {
        Object pp = player.platformPlayer();
        return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
    }

    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
