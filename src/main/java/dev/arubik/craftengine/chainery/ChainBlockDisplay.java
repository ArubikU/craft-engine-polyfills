package dev.arubik.craftengine.chainery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import org.joml.Vector3f;

/**
 * A packet-only {@code minecraft:block_display} for one chain link (CHAINERY — user: "render the block model
 * ... it should check his behaviors to find a behavior block"). Renders a real {@link BlockState}'s block
 * MODEL (vanilla or a CraftEngine block's mapped state → its pack model), NOT an item model. Modelled on
 * {@code ConveyorItemDisplay}/{@code ContraptionDisplaySwarm.Cell}: spawn/reposition/despawn via {@code MNms}
 * packets with per-viewer {@code shownTo} tracking, no real Bukkit entity.
 *
 * <p>Orientation is baked into the block STATE (e.g. a chain's {@code axis}), because a block_display ignores
 * the transformation {@code LeftRotation} in this codebase's testing (see {@code ContraptionDisplaySwarm}'s
 * "ROTATION VIA ENTITY YAW" note) — so a chain link is oriented by its axis property, which the block model
 * honours natively.
 */
public final class ChainBlockDisplay {

    private final int entityId = nextEntityId();
    private final UUID uuid = UUID.randomUUID();
    private final Object despawnPacket;
    private final java.util.Set<UUID> shownTo = java.util.concurrent.ConcurrentHashMap.newKeySet();

    private BlockState blockState;
    private org.joml.Quaternionf rotation = new org.joml.Quaternionf();
    private boolean metaDirty = false;

    public ChainBlockDisplay() {
        this.despawnPacket = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
    }

    /** Sets the block state this link renders; flags a metadata resend if it changed. */
    public void setBlockState(BlockState state) {
        if (state != null && !state.equals(this.blockState)) {
            this.blockState = state;
            this.metaDirty = true;
        }
    }

    /**
     * Sets the link's full 3D orientation (transformation LeftRotation) — this is what gives it yaw AND pitch,
     * so the block model points along the rope segment in any direction, not just the block's cardinal axis.
     */
    public void setRotation(org.joml.Quaternionf rot) {
        org.joml.Quaternionf next = rot != null ? rot : new org.joml.Quaternionf();
        if (!next.equals(this.rotation, 1.0e-4f)) {
            this.rotation = next;
            this.metaDirty = true;
        }
    }

    private List<Object> metadata() {
        List<Object> values = new ArrayList<>();
        if (blockState != null) {
            DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, values);
        }
        // Orient the block MODEL by the transformation LeftRotation (yaw+pitch+roll, full 3D — the vanilla
        // display rotation lever, same one ConveyorItemDisplay uses). Pivot about the model CENTRE: compose is
        // Translation + LeftRotation·v, so Translation = LeftRotation·(-0.5,-0.5,-0.5) makes it LeftRotation·(v−½)
        // — the model spins about its own centre, which sits on the rope particle midpoint we teleport it to.
        Vector3f t = rotation.transform(new Vector3f(-0.5f, -0.5f, -0.5f));
        DisplayData.Translation.addEntityData(t, values);
        DisplayData.LeftRotation.addEntityData(rotation, values);
        DisplayData.BrightnessOverride.addEntityData((15 << 4) | (15 << 20), values);
        DisplayData.PosRotInterpolationDuration.addEntityData(1, values);
        DisplayData.TransformationInterpolationDuration.addEntityData(2, values);
        return values;
    }

    private void spawn(Player player, double x, double y, double z) {
        Object add = dev.arubik.craftengine.util.MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                entityId, uuid, x, y, z, 0f, 0f,
                net.minecraft.world.entity.EntityType.BLOCK_DISPLAY, 0,
                net.minecraft.world.phys.Vec3.ZERO, 0);
        Object data = dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundSetEntityDataPacket(entityId, metadata());
        player.sendPackets(List.of(add, data), false);
    }

    private void updatePosition(Player player, double x, double y, double z) {
        player.sendPacket(dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, 0f, 0f, false), false);
    }

    private void updateMetadata(Player player) {
        player.sendPacket(dev.arubik.craftengine.util.MNms.INSTANCE
                .constructor$ClientboundSetEntityDataPacket(entityId, metadata()), false);
    }

    public void despawn(Player player) {
        player.sendPacket(despawnPacket, false);
    }

    /** Per-viewer spawn/move/meta sync, same shape as {@code ConveyorItemDisplay#render}. */
    public void render(List<Player> viewers, double x, double y, double z) {
        boolean forceMeta = metaDirty;
        metaDirty = false;
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
                if (forceMeta) {
                    updateMetadata(p);
                }
            }
        }
        shownTo.retainAll(current);
    }

    public void clearShown() {
        shownTo.clear();
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
