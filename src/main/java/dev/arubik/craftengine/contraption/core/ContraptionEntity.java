/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.dedicated.DedicatedServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.entity.player.Player
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftServer
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.core;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import dev.arubik.craftengine.contraption.config.ContraptionConfig;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.element.ContraptionBlockElement;
import dev.arubik.craftengine.contraption.element.ContraptionElement;
import dev.arubik.craftengine.contraption.element.ContraptionHitboxElement;
import dev.arubik.craftengine.contraption.element.ContraptionLiveEntityMirrorElement;
import dev.arubik.craftengine.contraption.element.ContraptionPistonShaftElement;
import dev.arubik.craftengine.contraption.element.ElementBuilder;
import dev.arubik.craftengine.contraption.element.ElementTypes;
import dev.arubik.craftengine.contraption.element.RenderContext;
import dev.arubik.craftengine.contraption.furniture.ContraptionSeatMount;
import dev.arubik.craftengine.contraption.listener.ContraptionProjectileCollision;
import dev.arubik.craftengine.contraption.listener.ContraptionVoidDrop;
import dev.arubik.craftengine.contraption.player.CePlayers;
import dev.arubik.craftengine.contraption.render.ContraptionItemPickupSwarm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;

public final class ContraptionEntity {
    private final ContraptionState state;
    private final ContraptionItemPickupSwarm itemPickupSwarm = new ContraptionItemPickupSwarm();
    private double lastRenderX = Double.NaN;
    private double lastRenderY;
    private double lastRenderZ;
    private double lastRenderYaw;
    private double lastRenderPitch;
    private double lastRenderRoll;
    private double lastRenderScale = 1.0;
    // Adaptive interpolation window: a script-driven bearing (windmill/rotational_bearing) only
    // mutates rotation state once every action_interval real ticks, not every tick like a
    // physics-driven contraption does — a fixed short interpolation duration (previously a
    // hardcoded 2 everywhere) finishes smoothing well before the NEXT jump arrives, producing a
    // visible "snap then freeze" instead of continuous motion. Tracking the real gap between
    // consecutive actual moves and feeding it back as the interpolation duration for the next
    // update makes every element interpolate across exactly the gap it's actually seeing —
    // 1-2 ticks for a per-tick physics contraption (no change from before), N ticks for an
    // action_interval=N script bearing — with no per-machine tuning needed.
    private int ticksSinceLastMove = 0;
    private int lastMoveGapTicks = 2;
    private final Set<UUID> elementViewerIds = ConcurrentHashMap.newKeySet();
    private boolean renderSuspended;
    private final Map<UUID, Float> seatFacing = new HashMap<UUID, Float>();
    private final Map<UUID, Float> seatRiderAppliedYaw = new HashMap<UUID, Float>();

    public ContraptionEntity(ContraptionState state) {
        this.state = state;
        this.rebuildSwarm(List.of());
    }

    public void teleport(World world, double x, double y, double z, double yawRadians) {
        if (world == null) {
            return;
        }
        CraftWorld oldWorld = null;
        try {
            DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(this.state.worldId());
            oldWorld = level != null ? level.getWorld() : null;
        }
        catch (Throwable server) {
            // empty catch block
        }
        if (oldWorld != null) {
            this.despawn(CePlayers.resolve(oldWorld.getPlayers()));
        }
        if (this.state.level() != null) {
            ServerLevel newHandle = ((CraftWorld)world).getHandle();
            this.state.level().reanchor((Level)newHandle, x, y, z, yawRadians);
        }
        this.state.setAnchor((ResourceKey<Level>)((CraftWorld)world).getHandle().dimension(), x, y, z, yawRadians);
        this.markMoved();
    }

    public void markMoved() {
        this.lastRenderX = Double.NaN;
    }

    public boolean renderSuspended() {
        return this.renderSuspended;
    }

    public void suspendRender(List<Player> viewers) {
        this.hitboxElement().despawnAll(viewers);
        for (ContraptionElement e : this.state.elements()) {
            e.despawn(viewers);
        }
        this.elementViewerIds.clear();
        this.renderSuspended = true;
        this.markMoved();
    }

    public void resumeRender() {
        this.renderSuspended = false;
        this.markMoved();
    }

    public void markDisplayDirty(BlockPos local) {
        ContraptionElement el = this.state.elementByLocalPos(local);
        if (el instanceof ContraptionBlockElement) {
            ContraptionBlockElement blockEl = (ContraptionBlockElement)el;
            blockEl.markDirty();
        }
    }

    public ContraptionState state() {
        return this.state;
    }

    public void setScale(double scale) {
        this.state.setScale(scale);
        this.markMoved();
    }

    public void rebuildSwarm(List<Player> viewers) {
        if (this.state.level() == null) {
            return;
        }
        this.state.level().refreshLocalPositions();
        this.state.lightMap().bake(this.state.level());
        ElementBuilder.rebuild(this.state, viewers);
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        ContraptionHitboxElement hb = this.hitboxElement();
        if (hb != null) {
            hb.rebuild(this.state.level(), viewers, bearing);
        }
    }

    public void render(List<Player> viewers) {
        this.render(viewers, null);
    }

    public void render(List<Player> viewers, ServerLevel realLevel) {
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        double yaw = this.state.yawRadians();
        double pitch = this.state.pitchRadians();
        double roll = this.state.rollRadians();
        double scale = this.state.scale();
        boolean moved = Double.isNaN(this.lastRenderX) || bearing.x != this.lastRenderX || bearing.y != this.lastRenderY || bearing.z != this.lastRenderZ || yaw != this.lastRenderYaw || pitch != this.lastRenderPitch || roll != this.lastRenderRoll || scale != this.lastRenderScale;
        this.lastRenderX = bearing.x;
        this.lastRenderY = bearing.y;
        this.lastRenderZ = bearing.z;
        this.lastRenderYaw = yaw;
        this.lastRenderPitch = pitch;
        this.lastRenderRoll = roll;
        this.lastRenderScale = scale;
        if (moved) {
            // Bound to [1,20]: 1 covers the always-true first render (a spawn, not a jump worth
            // smoothing), 20 caps how long a client will coast in the wrong direction if a
            // contraption stalls for a long time then suddenly resumes.
            this.lastMoveGapTicks = Math.max(1, Math.min(20, this.ticksSinceLastMove));
            this.ticksSinceLastMove = 0;
        } else {
            this.ticksSinceLastMove++;
        }
        this.hitboxElement().setColliderExcludedViewer(this.anchorRiderId());
        this.hitboxElement().render(viewers, bearing, yaw, pitch, roll, scale, moved);
        if (realLevel != null) {
            ContraptionVoidDrop.handle(this.state.level(), realLevel);
            ContraptionProjectileCollision.tick(this.state, this.state.level(), realLevel);
        }
        this.itemPickupSwarm.tick(this.state.level());
        this.renderPistonShaft(viewers, realLevel, moved);
        this.renderElements(viewers, bearing, yaw, pitch, roll, scale, moved, realLevel, this.lastMoveGapTicks);
    }

    private void renderElements(List<Player> viewers, Vec3 bearing, double yaw, double pitch, double roll, double scale, boolean moved, ServerLevel realLevel, int interpTicks) {
        List<Player> culledViewers;
        List<ContraptionElement> elements = this.state.elements();
        if (elements.isEmpty()) {
            return;
        }
        ContraptionConfig cfg = ContraptionConfig.get();
        if (cfg.entityCullingEnabled()) {
            double maxDistSq = cfg.entityCullingDistance() * cfg.entityCullingDistance();
            culledViewers = new ArrayList<Player>(viewers.size());
            for (Player player : viewers) {
                try {
                    double dz;
                    double dy;
                    org.bukkit.entity.Player bp;
                    Location loc;
                    double dx;
                    Object object = player.platformPlayer();
                    if (!(object instanceof org.bukkit.entity.Player) || !((dx = (loc = (bp = (org.bukkit.entity.Player)object).getLocation()).getX() - bearing.x) * dx + (dy = loc.getY() - bearing.y) * dy + (dz = loc.getZ() - bearing.z) * dz <= maxDistSq)) continue;
                    culledViewers.add(player);
                }
                catch (Throwable throwable) {
                    culledViewers.add(player);
                }
            }
            if (!this.elementViewerIds.isEmpty()) {
                HashSet<UUID> newIds = new HashSet<UUID>(culledViewers.size());
                for (Player player : culledViewers) {
                    newIds.add(player.uuid());
                }
                ArrayList<Player> arrayList = new ArrayList<Player>();
                for (Player p : viewers) {
                    if (!this.elementViewerIds.contains(p.uuid()) || newIds.contains(p.uuid())) continue;
                    arrayList.add(p);
                }
                if (!arrayList.isEmpty()) {
                    for (ContraptionElement el : elements) {
                        el.despawn(arrayList);
                    }
                }
            }
            if (cfg.entityCullingFrustumEnabled() && !culledViewers.isEmpty()) {
                double halfFovCos = Math.cos(Math.toRadians(cfg.entityCullingFovDegrees() / 2.0));
                double d = cfg.entityCullingNearBypass() * cfg.entityCullingNearBypass();
                double expansion = cfg.entityCullingExpansion();
                ArrayList<Player> frustumPassed = new ArrayList<Player>(culledViewers.size());
                for (Player p : culledViewers) {
                    try {
                        Object pp = p.platformPlayer();
                        if (!(pp instanceof org.bukkit.entity.Player)) {
                            frustumPassed.add(p);
                            continue;
                        }
                        org.bukkit.entity.Player bp = (org.bukkit.entity.Player)pp;
                        Location eye = bp.getEyeLocation();
                        double dx = bearing.x - eye.getX();
                        double dy = bearing.y - eye.getY();
                        double dz = bearing.z - eye.getZ();
                        double distSq = dx * dx + dy * dy + dz * dz;
                        if (distSq <= d) {
                            frustumPassed.add(p);
                            continue;
                        }
                        double dist = Math.sqrt(distSq);
                        float eyeYaw = eye.getYaw();
                        float eyePitch = eye.getPitch();
                        double cosP = Math.cos(Math.toRadians(eyePitch));
                        double lookX = -Math.sin(Math.toRadians(eyeYaw)) * cosP;
                        double lookY = -Math.sin(Math.toRadians(eyePitch));
                        double lookZ = Math.cos(Math.toRadians(eyeYaw)) * cosP;
                        double dot = (dx * lookX + dy * lookY + dz * lookZ) / dist;
                        double expandedHalfFovCos = halfFovCos;
                        if (expansion > 0.0) {
                            double expandAngle = Math.atan(expansion / dist);
                            expandedHalfFovCos = Math.cos(Math.max(0.0, Math.toRadians(cfg.entityCullingFovDegrees() / 2.0) - expandAngle));
                        }
                        if (!(dot >= expandedHalfFovCos)) continue;
                        frustumPassed.add(p);
                    }
                    catch (Throwable ignored) {
                        frustumPassed.add(p);
                    }
                }
                if (frustumPassed.size() < culledViewers.size() && !this.elementViewerIds.isEmpty()) {
                    ArrayList<Player> frustumDeparted = new ArrayList<Player>();
                    HashSet<UUID> frustumPassedIds = new HashSet<UUID>();
                    for (Player p : frustumPassed) {
                        frustumPassedIds.add(p.uuid());
                    }
                    for (Player p : culledViewers) {
                        if (!this.elementViewerIds.contains(p.uuid()) || frustumPassedIds.contains(p.uuid())) continue;
                        frustumDeparted.add(p);
                    }
                    if (!frustumDeparted.isEmpty()) {
                        for (ContraptionElement el : elements) {
                            el.despawn(frustumDeparted);
                        }
                    }
                }
                culledViewers = frustumPassed;
            }
            this.elementViewerIds.clear();
            for (Player player : culledViewers) {
                this.elementViewerIds.add(player.uuid());
            }
        } else {
            culledViewers = viewers;
        }
        RenderContext ctx = new RenderContext(culledViewers, bearing, yaw, pitch, roll, scale, moved, this.state.level(), realLevel, this.state.lightMap(), elements, interpTicks);
        for (ContraptionElement element : elements) {
            if (!element.isValid()) continue;
            element.tick(ctx);
            element.render(ctx);
        }
    }

    private void renderPistonShaft(List<Player> viewers, ServerLevel realLevel, boolean moved) {
        PistonBearingBehavior piston = null;
        for (MovementBehavior b : this.state.behaviors()) {
            PistonBearingBehavior p;
            if (!(b instanceof PistonBearingBehavior)) continue;
            piston = p = (PistonBearingBehavior)b;
            break;
        }
        if (piston == null || realLevel == null) {
            return;
        }
        double extended = piston.extendedBlocks();
        Vec3 facing = piston.direction();
        String headItemId = ContraptionPistonShaftElement.headItem();
        String shaftItemId = ContraptionPistonShaftElement.shaftItemFor(facing);
        ContraptionPistonShaftElement shaftElem = this.pistonShaftElement();
        if (shaftElem != null) {
            shaftElem.updateShaft(facing, extended, headItemId, shaftItemId);
        }
    }

    private ContraptionPistonShaftElement pistonShaftElement() {
        for (ContraptionElement e : this.state.elements()) {
            if (!(e instanceof ContraptionPistonShaftElement)) continue;
            ContraptionPistonShaftElement s = (ContraptionPistonShaftElement)e;
            return s;
        }
        return null;
    }

    private ContraptionHitboxElement hitboxElement() {
        for (ContraptionElement e : this.state.elements()) {
            if (!(e instanceof ContraptionHitboxElement)) continue;
            ContraptionHitboxElement h = (ContraptionHitboxElement)e;
            return h;
        }
        return null;
    }

    private UUID anchorRiderId() {
        UUID anchorId = this.state.anchorEntityId();
        if (anchorId == null) {
            return null;
        }
        Entity anchor = Bukkit.getEntity((UUID)anchorId);
        if (anchor == null) {
            return null;
        }
        for (Entity passenger : anchor.getPassengers()) {
            if (!(passenger instanceof org.bukkit.entity.Player)) continue;
            org.bukkit.entity.Player player = (org.bukkit.entity.Player)passenger;
            return player.getUniqueId();
        }
        return null;
    }

    public void carryRiders(List<ServerPlayer> candidates) {
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        Map<UUID, Vec3> seated = this.state.seatedRiders();
        List<ServerPlayer> standingOnly = seated.isEmpty() ? candidates : candidates.stream().filter(sp -> !seated.containsKey(sp.getUUID())).toList();
        this.hitboxElement().carryRiders(standingOnly, bearing, this.state.lastDeltaX(), this.state.lastDeltaY(), this.state.lastDeltaZ(), this.state.level(), this.state.yawRadians(), this.state.lastYawDelta());
    }

    public void carryEntities(Level realLevel) {
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        this.hitboxElement().carryNearbyEntities(realLevel, bearing, this.state.lastDeltaX(), this.state.lastDeltaY(), this.state.lastDeltaZ(), this.state.yawRadians(), this.state.anchorEntityId());
    }

    public void pushBackBystanders(Level realLevel) {
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        this.hitboxElement().pushBackNearbyBystanders(realLevel, bearing, this.state.lastDeltaX(), this.state.lastDeltaY(), this.state.lastDeltaZ(), this.state.yawRadians(), this.state.pitchRadians(), this.state.rollRadians(), this.state.scale(), this.state.pushSettings());
    }

    public void pushBackEntities(Level realLevel) {
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        this.hitboxElement().pushBackNearbyEntities(realLevel, bearing, this.state.lastDeltaX(), this.state.lastDeltaY(), this.state.lastDeltaZ(), this.state.yawRadians(), this.state.pitchRadians(), this.state.rollRadians(), this.state.scale(), this.state.pushSettings(), this.state.anchorEntityId());
    }

    public void carrySeatedRiders() {
        Vec3 bearing = new Vec3(this.state.x(), this.state.y(), this.state.z());
        double yaw = this.state.yawRadians();
        double pitch = this.state.pitchRadians();
        double roll = this.state.rollRadians();
        double scale = this.state.scale();
        for (Map.Entry<UUID, Vec3> e : this.state.seatedRiders().entrySet()) {
            UUID id = e.getKey();
            UUID mountId = this.state.seatedRiderMount(id);
            Entity mount = ContraptionSeatMount.resolve(mountId);
            if (mount == null) continue;
            Vec3 target = ContraptionMath.renderPosition(e.getValue(), bearing, yaw, pitch, roll, scale);
            float seatYaw = 0.0f;
            ContraptionSeatMount.reposition(mount, target, seatYaw);
            this.rotateSeatedRiderView(id, seatYaw);
            org.bukkit.entity.Player seated = Bukkit.getPlayer((UUID)id);
            if (seated == null) continue;
            ContraptionSeatMount.applyContraptionScale(seated, scale);
        }
        this.seatFacing.keySet().retainAll(this.state.seatedRiders().keySet());
        this.seatRiderAppliedYaw.keySet().retainAll(this.state.seatedRiders().keySet());
    }

    private void rotateSeatedRiderView(UUID id, float seatYaw) {
        boolean turningSelf;
        Float prevSeat = this.seatFacing.put(id, Float.valueOf(seatYaw));
        if (prevSeat == null) {
            return;
        }
        float seatDelta = Location.normalizeYaw((float)(seatYaw - prevSeat.floatValue()));
        if (Math.abs(seatDelta) < 0.001f) {
            return;
        }
        org.bukkit.entity.Player bp = Bukkit.getPlayer((UUID)id);
        if (bp == null) {
            return;
        }
        float curYaw = bp.getLocation().getYaw();
        Float applied = this.seatRiderAppliedYaw.get(id);
        boolean bl = turningSelf = applied != null && Math.abs(Location.normalizeYaw((float)(curYaw - applied.floatValue()))) > 0.75f;
        if (turningSelf) {
            this.seatRiderAppliedYaw.put(id, Float.valueOf(curYaw));
            return;
        }
        ContraptionSeatMount.rotateRiderView(bp, seatDelta);
        this.seatRiderAppliedYaw.put(id, Float.valueOf(Location.normalizeYaw((float)(curYaw + seatDelta))));
    }

    public void despawn(List<Player> viewers) {
        this.despawnRest(viewers);
        this.despawnHitboxesOnly(viewers);
    }

    public void despawnRest(List<Player> viewers) {
        this.itemPickupSwarm.despawnAll();
        for (ContraptionElement e : this.state.elements()) {
            e.despawn(viewers);
        }
        for (UUID id : this.state.seatedRiders().keySet()) {
            this.state.removeSeatedRider(id);
        }
    }

    public void despawnHitboxesOnly(List<Player> viewers) {
        ContraptionHitboxElement h = this.hitboxElement();
        if (h != null) {
            h.despawnAll(viewers);
        }
    }

    public Set<UUID> currentRiderIds() {
        ContraptionHitboxElement h = this.hitboxElement();
        return h != null ? h.currentRiderIds() : Set.of();
    }

    public int cellCount() {
        return (int)this.state.elements().stream().filter(e -> e.type().equals(ElementTypes.BLOCK)).count();
    }

    public int hitboxCellCount() {
        ContraptionHitboxElement h = this.hitboxElement();
        return h != null ? h.cellCount() : 0;
    }

    public int mirroredEntityCount() {
        for (ContraptionElement e : this.state.elements()) {
            if (!(e instanceof ContraptionLiveEntityMirrorElement)) continue;
            ContraptionLiveEntityMirrorElement m = (ContraptionLiveEntityMirrorElement)e;
            return m.entityIds().length;
        }
        return 0;
    }

    public int pickupMirrorCount() {
        return this.itemPickupSwarm.mirrorCount();
    }
}

