/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.contraption.core;

import dev.arubik.craftengine.contraption.ContraptionPushSettings;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ContraptionInteractionOverlayElement;
import dev.arubik.craftengine.contraption.element.ContraptionLightMap;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;

public final class ContraptionState {
    private final UUID id;
    private ResourceKey<Level> worldId;
    private final ContraptionLevel level;
    private double x;
    private double y;
    private double z;
    private double yawRadians;
    private Vec3 anchorOffset = Vec3.ZERO;
    private OptionalDouble yawOffset = OptionalDouble.empty();
    private double pitchRadians;
    private double rollRadians;
    private double scale = 1.0;
    public static final double MIN_SCALE = 0.1;
    public static final double MAX_SCALE = 10.0;
    private boolean stalled;
    private final List<MovementBehavior> behaviors = new ArrayList<MovementBehavior>();
    private List<ContraptionFurniture> furniture = List.of();
    private ContraptionPushSettings pushSettings = ContraptionPushSettings.DEFAULT;
    private double lastDeltaX;
    private double lastDeltaY;
    private double lastDeltaZ;
    private double lastYawDelta;
    private double lastPitchDelta;
    private double lastRollDelta;
    private float globalRpm;
    private float suDemand;
    private final Map<UUID, Vec3> seatedRiders = new HashMap<UUID, Vec3>();
    private final Map<UUID, UUID> seatedRiderMount = new HashMap<UUID, UUID>();
    private final double originX;
    private final double originY;
    private final double originZ;
    private UUID owner;
    private long lastRegionCheckKey = Long.MIN_VALUE;
    private boolean lastRegionAllowed = true;
    private Key bearingType;
    private UUID anchorEntityId;
    private double explosionProof;
    private final ContraptionLightMap lightMap = new ContraptionLightMap();
    private final List<ContraptionElement> elements = new ArrayList<ContraptionElement>();
    private final Int2ObjectOpenHashMap<ContraptionElement> entityIdIndex = new Int2ObjectOpenHashMap();
    /** Server tick of the last {@code Contraption.set_spin()} call against this state — lets that
     *  script method scale its per-call increment by how many real ticks actually elapsed instead
     *  of always assuming exactly one, which under-rotates whenever the calling script runs less
     *  often than every tick (e.g. a machine's {@code action_interval}). {@code Long.MIN_VALUE}
     *  means "never called yet". */
    private long lastSetSpinTick = Long.MIN_VALUE;

    public long lastSetSpinTick() {
        return this.lastSetSpinTick;
    }

    public void setLastSetSpinTick(long tick) {
        this.lastSetSpinTick = tick;
    }

    public ContraptionState(UUID id, ResourceKey<Level> worldId, ContraptionLevel level, double x, double y, double z) {
        this.id = id;
        this.worldId = worldId;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        if (level != null) {
            level.setTransform(x, y, z, 0.0, 0.0, 0.0, this.scale);
        }
    }

    public Key bearingType() {
        return this.bearingType;
    }

    public void setBearingType(Key bearingType) {
        this.bearingType = bearingType;
    }

    public Vec3 anchorOffset() {
        return this.anchorOffset;
    }

    public OptionalDouble yawOffset() {
        return this.yawOffset;
    }

    public double explosionProof() {
        return this.explosionProof;
    }

    public void setExplosionProof(double explosionProof) {
        this.explosionProof = Math.max(0.0, Math.min(1.0, explosionProof));
    }

    public UUID anchorEntityId() {
        return this.anchorEntityId;
    }

    public void setAnchorEntityId(UUID anchorEntityId) {
        this.anchorEntityId = anchorEntityId;
    }

    public UUID id() {
        return this.id;
    }

    public ResourceKey<Level> worldId() {
        return this.worldId;
    }

    public ContraptionLevel level() {
        return this.level;
    }

    public BlockPos originBearingBlockPos() {
        return BlockPos.containing((double)this.originX, (double)this.originY, (double)this.originZ);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        if (this.level != null) {
            this.level.setTransform(x, y, z, this.yawRadians, this.pitchRadians, this.rollRadians, this.scale);
        }
    }

    public double yawRadians() {
        return this.yawRadians;
    }

    public void setYawRadians(double yawRadians) {
        this.yawRadians = yawRadians;
        if (this.level != null) {
            this.level.setTransform(this.x, this.y, this.z, yawRadians, this.pitchRadians, this.rollRadians, this.scale);
        }
    }

    public void setAnchorOffset(Vec3 anchorOffset) {
        this.anchorOffset = anchorOffset;
    }

    public void setYawOffset(OptionalDouble yawOffset) {
        this.yawOffset = yawOffset;
    }

    public double pitchRadians() {
        return this.pitchRadians;
    }

    public void setPitchRadians(double pitchRadians) {
        this.pitchRadians = pitchRadians;
        if (this.level != null) {
            this.level.setTransform(this.x, this.y, this.z, this.yawRadians, pitchRadians, this.rollRadians, this.scale);
        }
    }

    public double rollRadians() {
        return this.rollRadians;
    }

    public void setRollRadians(double rollRadians) {
        this.rollRadians = rollRadians;
        if (this.level != null) {
            this.level.setTransform(this.x, this.y, this.z, this.yawRadians, this.pitchRadians, rollRadians, this.scale);
        }
    }

    public double scale() {
        return this.scale;
    }

    public void setScale(double scale) {
        if (Double.isNaN(scale)) {
            return;
        }
        this.scale = Math.max(0.1, Math.min(10.0, scale));
        if (this.level != null) {
            this.level.setTransform(this.x, this.y, this.z, this.yawRadians, this.pitchRadians, this.rollRadians, this.scale);
        }
    }

    public void setAnchor(ResourceKey<Level> worldId, double x, double y, double z, double yawRadians) {
        if (worldId != null) {
            this.worldId = worldId;
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.yawRadians = yawRadians;
        if (this.level != null) {
            this.level.setTransform(x, y, z, yawRadians, this.pitchRadians, this.rollRadians, this.scale);
        }
    }

    public boolean isStalled() {
        return this.stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public List<MovementBehavior> behaviors() {
        return this.behaviors;
    }

    public void addBehavior(MovementBehavior behavior) {
        this.behaviors.add(behavior);
    }

    public void setLastDelta(double dx, double dy, double dz) {
        this.lastDeltaX = dx;
        this.lastDeltaY = dy;
        this.lastDeltaZ = dz;
    }

    public double lastDeltaX() {
        return this.lastDeltaX;
    }

    public double lastDeltaY() {
        return this.lastDeltaY;
    }

    public double lastDeltaZ() {
        return this.lastDeltaZ;
    }

    public void setLastYawDelta(double dyaw) {
        this.lastYawDelta = dyaw;
    }

    public double lastYawDelta() {
        return this.lastYawDelta;
    }

    public void setLastPitchDelta(double dpitch) {
        this.lastPitchDelta = dpitch;
    }

    public double lastPitchDelta() {
        return this.lastPitchDelta;
    }

    public void setLastRollDelta(double droll) {
        this.lastRollDelta = droll;
    }

    public double lastRollDelta() {
        return this.lastRollDelta;
    }

    public List<ContraptionFurniture> furniture() {
        return this.furniture;
    }

    public void setFurniture(List<ContraptionFurniture> furniture) {
        this.furniture = furniture != null ? furniture : List.of();
    }

    public float globalRpm() {
        return this.globalRpm;
    }

    public void setGlobalRpm(float globalRpm) {
        this.globalRpm = globalRpm;
    }

    public void resetSuDemand() {
        this.suDemand = 0.0f;
    }

    public void addSuDemand(float su) {
        this.suDemand += su;
    }

    public float suDemand() {
        return this.suDemand;
    }

    public void addSeatedRider(UUID playerId, Vec3 localOffset) {
        this.seatedRiders.put(playerId, localOffset);
    }

    public void setSeatedRiderMount(UUID playerId, UUID mountEntityId) {
        this.seatedRiderMount.put(playerId, mountEntityId);
    }

    public UUID seatedRiderMount(UUID playerId) {
        return this.seatedRiderMount.get(playerId);
    }

    public void removeSeatedRider(UUID playerId) {
        this.seatedRiders.remove(playerId);
        this.seatedRiderMount.remove(playerId);
    }

    public Map<UUID, Vec3> seatedRiders() {
        return Map.copyOf(this.seatedRiders);
    }

    public UUID owner() {
        return this.owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public long lastRegionCheckKey() {
        return this.lastRegionCheckKey;
    }

    public boolean lastRegionAllowed() {
        return this.lastRegionAllowed;
    }

    public void setLastRegionCheck(long key, boolean allowed) {
        this.lastRegionCheckKey = key;
        this.lastRegionAllowed = allowed;
    }

    public ContraptionPushSettings pushSettings() {
        return this.pushSettings;
    }

    public void setPushSettings(ContraptionPushSettings pushSettings) {
        this.pushSettings = pushSettings != null ? pushSettings : ContraptionPushSettings.DEFAULT;
    }

    public ContraptionLightMap lightMap() {
        return this.lightMap;
    }

    public List<ContraptionElement> elements() {
        return Collections.unmodifiableList(this.elements);
    }

    public void addElement(ContraptionElement element) {
        this.elements.add(element);
        for (int eid : element.entityIds()) {
            this.entityIdIndex.put(eid, element);
        }
    }

    public void removeElement(ContraptionElement element) {
        this.elements.remove(element);
        for (int eid : element.entityIds()) {
            this.entityIdIndex.remove(eid);
        }
    }

    public void setElements(List<ContraptionElement> newElements) {
        this.elements.clear();
        this.entityIdIndex.clear();
        if (newElements != null) {
            for (ContraptionElement e : newElements) {
                this.addElement(e);
            }
        }
    }

    public ContraptionElement elementByEntityId(int entityId) {
        return (ContraptionElement)this.entityIdIndex.get(entityId);
    }

    public ContraptionElement elementByInteractionEntityId(int entityId) {
        for (ContraptionElement e : this.elements) {
            if (!(e instanceof ContraptionInteractionOverlayElement)) continue;
            ContraptionInteractionOverlayElement overlay = (ContraptionInteractionOverlayElement)e;
            return overlay.ownerOf(entityId);
        }
        return null;
    }

    public ContraptionElement elementByLocalPos(BlockPos local) {
        for (ContraptionElement e : this.elements) {
            Vec3 offset = e.localOffset();
            if (!BlockPos.containing((double)offset.x, (double)offset.y, (double)offset.z).equals(local)) continue;
            return e;
        }
        return null;
    }

    public void rebuildEntityIdIndex() {
        this.entityIdIndex.clear();
        for (ContraptionElement e : this.elements) {
            for (int eid : e.entityIds()) {
                this.entityIdIndex.put(eid, e);
            }
        }
    }
}

