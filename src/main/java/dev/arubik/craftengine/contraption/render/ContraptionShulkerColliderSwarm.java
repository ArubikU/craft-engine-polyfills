/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.entity.data.BaseEntityData
 *  net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData
 *  net.momirealms.craftengine.core.entity.player.Player
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.render;

import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.render.ShulkerBoxFit;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.BaseEntityData;
import net.momirealms.craftengine.bukkit.entity.data.monster.ShulkerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ContraptionShulkerColliderSwarm {
    private final Map<Object, Slot> fixedSlots = new LinkedHashMap<Object, Slot>();
    private boolean offAxis;
    private final Map<Object, CellGroup> cells = new LinkedHashMap<Object, CellGroup>();
    private final Set<UUID> currentRiders = new HashSet<UUID>();
    private volatile UUID excludedViewer;
    public static volatile boolean SHOW_COLLIDERS;
    private static final AtomicInteger VISIBILITY_GENERATION;
    public static final int BUDGET_FAR = 1;
    public static final int BUDGET_MID = 4;
    public static final int BUDGET_NEAR = 16;
    public static final int BUDGET_NONE = 0;
    public static final double NEAR_ENTER = 1.5;
    public static final double NEAR_EXIT = 2.5;
    public static final double MID_ENTER = 3.0;
    public static final double MID_EXIT = 4.0;
    public static final double FAR_ENTER = 4.0;
    public static final double FAR_EXIT = 5.0;
    public static final int MAX_LOD_SHULKERS_PER_VIEWER = 256;
    private static final long CAP_WARN_INTERVAL_MS = 60000L;
    private static volatile long lastCapWarnMs;
    private static final double OFF_AXIS_TOLERANCE_DEGREES = 1.0;
    private static final int MOUNT_REASSERT_TICKS = 40;
    private static final AtomicInteger ENTITY_COUNTER;

    public void setExcludedViewer(UUID excludedViewer) {
        this.excludedViewer = excludedViewer;
    }

    public static boolean toggleShowColliders() {
        boolean now;
        SHOW_COLLIDERS = now = !SHOW_COLLIDERS;
        VISIBILITY_GENERATION.incrementAndGet();
        return now;
    }

    public void addSlot(Object key, double lx, double ly, double lz, float scale) {
        this.addSlot(key, lx, ly, lz, scale, Direction.DOWN, 0);
    }

    public void addSlot(Object key, double lx, double ly, double lz, float scale, Direction attachFace, int peek) {
        Slot existing = this.fixedSlots.get(key);
        if (existing != null) {
            existing.updateOffset(lx, ly, lz, scale, attachFace, peek);
            return;
        }
        this.fixedSlots.put(key, new Slot(key, lx, ly, lz, scale, attachFace, peek));
    }

    public void setCell(Object key, List<AABB> localBoxes, List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        CellGroup group = this.cells.get(key);
        if (group == null) {
            this.cells.put(key, new CellGroup(localBoxes));
            return;
        }
        group.setBoxes(localBoxes, viewers);
    }

    public void prune(Set<Object> liveKeys, List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        Iterator<Map.Entry<Object, Slot>> fixedIt = this.fixedSlots.entrySet().iterator();
        while (fixedIt.hasNext()) {
            Map.Entry<Object, Slot> e = fixedIt.next();
            if (liveKeys.contains(e.getKey())) continue;
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                e.getValue().despawn(p);
            }
            fixedIt.remove();
        }
        Iterator<Map.Entry<Object, CellGroup>> cellIt = this.cells.entrySet().iterator();
        while (cellIt.hasNext()) {
            Map.Entry<Object, CellGroup> e = cellIt.next();
            if (liveKeys.contains(e.getKey())) continue;
            e.getValue().despawnAll(viewers);
            cellIt.remove();
        }
    }

    public void clear(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        for (Slot s : this.fixedSlots.values()) {
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                s.despawn(p);
            }
        }
        this.fixedSlots.clear();
        for (CellGroup g : this.cells.values()) {
            g.despawnAll(viewers);
        }
        this.cells.clear();
        this.currentRiders.clear();
    }

    public void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Vec3 bearingWorldPos, double yawRadians, boolean moved) {
        this.render(viewers, bearingWorldPos, yawRadians, 1.0, moved);
    }

    public void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Vec3 bearingWorldPos, double yawRadians, double scale, boolean moved) {
        this.render(viewers, bearingWorldPos, yawRadians, 0.0, scale, moved);
    }

    public void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double scale, boolean moved) {
        this.render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale, moved);
    }

    public void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double rollRadians, double scale, boolean moved) {
        for (Slot slot : this.fixedSlots.values()) {
            Vec3 pos = ContraptionMath.renderPosition(slot.localCenter(), bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
            slot.render(viewers, this.wantedExcluding(viewers), pos.x, pos.y, pos.z, scale, moved);
        }
        if (!this.cells.isEmpty()) {
            this.renderCells(viewers, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale, moved);
        }
    }

    private void renderCells(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians, double rollRadians, double scale, boolean moved) {
        this.offAxis = ContraptionShulkerColliderSwarm.isOffAxis(yawRadians) || ContraptionShulkerColliderSwarm.isOffAxis(pitchRadians) || ContraptionShulkerColliderSwarm.isOffAxis(rollRadians);
        ArrayList<CellGroup> live = new ArrayList<CellGroup>(this.cells.values());
        for (CellGroup cellGroup : live) {
            cellGroup.syncOffAxis(this.offAxis, viewers);
        }
        IdentityHashMap<CellGroup, Vec3> anchors = new IdentityHashMap<CellGroup, Vec3>();
        for (CellGroup cellGroup : live) {
            anchors.put(cellGroup, ContraptionMath.renderPosition(cellGroup.anchor, bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale));
        }
        IdentityHashMap<CellGroup, Map<Integer, Set<UUID>>> identityHashMap = new IdentityHashMap<CellGroup, Map<Integer, Set<UUID>>>();
        for (CellGroup g : live) {
            identityHashMap.put(g, new HashMap());
        }
        HashSet<UUID> hashSet = new HashSet<UUID>();
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            UUID id = ContraptionShulkerColliderSwarm.uuidOf(p);
            Vec3 viewerPos = ContraptionShulkerColliderSwarm.positionOf(p);
            Vec3 eyePos = ContraptionShulkerColliderSwarm.eyePositionOf(p);
            if (id == null || viewerPos == null) continue;
            hashSet.add(id);
            this.assignTiers(live, anchors, id, viewerPos, eyePos, scale, identityHashMap);
        }
        for (CellGroup g : live) {
            g.viewerTier.keySet().retainAll(hashSet);
        }
        for (CellGroup g : live) {
            Map byTier = (Map)identityHashMap.get(g);
            HashSet touched = new HashSet(byTier.keySet());
            touched.addAll(g.tierSlots.keySet());
            Iterator iterator = touched.iterator();
            while (iterator.hasNext()) {
                int tier = (Integer)iterator.next();
                Set<UUID> want = (Set<UUID>) byTier.getOrDefault(tier, Set.of());
                List<Slot> tierSlots = g.tierSlots.get(tier);
                if (tierSlots == null) {
                    if (want.isEmpty()) continue;
                    tierSlots = g.slotsFor(tier, this.offAxis);
                }
                if (want.isEmpty() && !g.anyTracked(tierSlots)) {
                    g.tierSlots.remove(tier);
                    continue;
                }
                for (Slot slot : tierSlots) {
                    Vec3 pos = ContraptionMath.renderPosition(slot.localCenter(), bearingWorldPos, yawRadians, pitchRadians, rollRadians, scale);
                    slot.render(viewers, want, pos.x, pos.y, pos.z, scale, moved);
                }
                if (!want.isEmpty()) continue;
                g.tierSlots.remove(tier);
            }
        }
    }

    private void assignTiers(List<CellGroup> live, Map<CellGroup, Vec3> anchors, UUID viewer, Vec3 viewerPos, Vec3 eyePos, double scale, Map<CellGroup, Map<Integer, Set<UUID>>> wanted) {
        IdentityHashMap<CellGroup, Integer> tierOf = new IdentityHashMap<CellGroup, Integer>();
        IdentityHashMap<CellGroup, Double> distOf = new IdentityHashMap<CellGroup, Double>();
        ArrayList<CellGroup> upgraded = new ArrayList<CellGroup>();
        boolean excluded = viewer.equals(this.excludedViewer);
        for (CellGroup g : live) {
            Vec3 a = anchors.get(g);
            double dist = ContraptionShulkerColliderSwarm.viewerDistance(a, viewerPos, eyePos);
            int tier = excluded ? 0 : ContraptionShulkerColliderSwarm.tierFor(g.viewerTier.getOrDefault(viewer, 0), dist, scale);
            tierOf.put(g, tier);
            distOf.put(g, dist);
            if (tier == 1 || tier == 0) continue;
            upgraded.add(g);
        }
        if (!upgraded.isEmpty()) {
            upgraded.sort(Comparator.comparingDouble(distOf::get));
            int used = 0;
            boolean capped = false;
            for (CellGroup g : upgraded) {
                int cost = g.cubesFor((Integer)tierOf.get(g), this.offAxis).size();
                if (used + cost > 256) {
                    tierOf.put(g, 1);
                    capped = true;
                    continue;
                }
                used += cost;
            }
            if (capped) {
                ContraptionShulkerColliderSwarm.warnCapped(live.size());
            }
        }
        for (CellGroup g : live) {
            int tier = (Integer)tierOf.get(g);
            g.viewerTier.put(viewer, tier);
            wanted.get(g).computeIfAbsent(tier, t -> new HashSet()).add(viewer);
        }
    }

    static int tierFor(int currentTier, double dist, double scale) {
        double s = scale <= 0.0 ? 1.0 : scale;
        double nearEnter = 1.5 * s;
        double nearExit = 2.5 * s;
        double midEnter = 3.0 * s;
        double midExit = 4.0 * s;
        double farEnter = 4.0 * s;
        double farExit = 5.0 * s;
        if (currentTier == 16) {
            if (dist <= nearExit) {
                return 16;
            }
            if (dist <= midExit) {
                return 4;
            }
            return dist <= farExit ? 1 : 0;
        }
        if (currentTier == 4) {
            if (dist <= nearEnter) {
                return 16;
            }
            if (dist <= midExit) {
                return 4;
            }
            return dist <= farExit ? 1 : 0;
        }
        if (currentTier == 1) {
            if (dist <= nearEnter) {
                return 16;
            }
            if (dist <= midEnter) {
                return 4;
            }
            return dist <= farExit ? 1 : 0;
        }
        if (dist <= nearEnter) {
            return 16;
        }
        if (dist <= midEnter) {
            return 4;
        }
        return dist <= farEnter ? 1 : 0;
    }

    private static void warnCapped(int cellCount) {
        long now = System.currentTimeMillis();
        if (now - lastCapWarnMs < 60000L) {
            return;
        }
        lastCapWarnMs = now;
        Bukkit.getLogger().warning("[Contraption] shulker collider LOD hit the per-viewer cap (256) on a " + cellCount + "-cell contraption; furthest upgraded cells fall back to one collider each.");
    }

    private Set<UUID> wantedExcluding(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
        UUID excluded = this.excludedViewer;
        if (excluded == null) {
            return null;
        }
        HashSet<UUID> wanted = new HashSet<UUID>();
        for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
            UUID id = ContraptionShulkerColliderSwarm.uuidOf(p);
            if (id == null || id.equals(excluded)) continue;
            wanted.add(id);
        }
        return wanted;
    }

    private static boolean isOffAxis(double radians) {
        double degrees = Math.toDegrees(radians) % 90.0;
        if (degrees < 0.0) {
            degrees += 90.0;
        }
        return degrees > 1.0 && degrees < 89.0;
    }

    private static Vec3 positionOf(net.momirealms.craftengine.core.entity.player.Player player) {
        Object pp = player.platformPlayer();
        if (!(pp instanceof Player)) {
            return null;
        }
        Player bukkitPlayer = (Player)pp;
        Location loc = bukkitPlayer.getLocation();
        return new Vec3(loc.getX(), loc.getY(), loc.getZ());
    }

    private static Vec3 eyePositionOf(net.momirealms.craftengine.core.entity.player.Player player) {
        Object pp = player.platformPlayer();
        if (!(pp instanceof Player)) {
            return null;
        }
        Player bukkitPlayer = (Player)pp;
        Location loc = bukkitPlayer.getEyeLocation();
        return new Vec3(loc.getX(), loc.getY(), loc.getZ());
    }

    private static double viewerDistance(Vec3 cell, Vec3 feet, Vec3 eyes) {
        double byFeet = feet == null ? Double.MAX_VALUE : cell.distanceTo(feet);
        double byEyes = eyes == null ? Double.MAX_VALUE : cell.distanceTo(eyes);
        return Math.min(byFeet, byEyes);
    }

    private static UUID uuidOf(net.momirealms.craftengine.core.entity.player.Player player) {
        UUID uUID;
        Object pp = player.platformPlayer();
        if (pp instanceof Player) {
            Player b = (Player)pp;
            uUID = b.getUniqueId();
        } else {
            uUID = null;
        }
        return uUID;
    }

    public int slotCount() {
        int count = this.fixedSlots.size();
        for (CellGroup g : this.cells.values()) {
            for (List<Slot> tier : g.tierSlots.values()) {
                count += tier.size();
            }
        }
        return count;
    }

    public boolean isStandingOnFootprint(Vec3 playerPos, Vec3 bearingWorldPos) {
        for (Slot slot : this.fixedSlots.values()) {
            double half = (double)slot.scale / 2.0;
            double minX = bearingWorldPos.x + slot.lx - half;
            double maxX = bearingWorldPos.x + slot.lx + half;
            double minZ = bearingWorldPos.z + slot.lz - half;
            double maxZ = bearingWorldPos.z + slot.lz + half;
            double topY = bearingWorldPos.y + slot.ly + (double)slot.scale + slot.peekGrowth();
            if (!(playerPos.x >= minX - 0.3) || !(playerPos.x <= maxX + 0.3) || !(playerPos.z >= minZ - 0.3) || !(playerPos.z <= maxZ + 0.3) || !(playerPos.y >= topY - 0.3) || !(playerPos.y <= topY + 0.9)) continue;
            return true;
        }
        return false;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }

    static {
        AtomicInteger counter;
        SHOW_COLLIDERS = false;
        VISIBILITY_GENERATION = new AtomicInteger();
        lastCapWarnMs = 0L;
        try {
            Field f = Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger)f.get(null);
        }
        catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static final class Slot {
        final Object key;
        double lx;
        double ly;
        double lz;
        float scale;
        Direction attachFace;
        int peek;
        private final int entityId = ContraptionShulkerColliderSwarm.nextEntityId();
        private final int anchorId = ContraptionShulkerColliderSwarm.nextEntityId();
        private int mountTick = Math.floorMod(this.entityId, 40);
        private final UUID uuid = UUID.randomUUID();
        private final UUID anchorUuid = UUID.randomUUID();
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();
        private volatile boolean scaleDirty = false;
        private volatile boolean dataDirty = false;
        private volatile int visibilityGeneration = VISIBILITY_GENERATION.get();
        private double contraptionScale = 1.0;

        Slot(Object key, double lx, double ly, double lz, float scale, Direction attachFace, int peek) {
            this.key = key;
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of((int)this.entityId, (int)this.anchorId));
        }

        Vec3 localCenter() {
            return new Vec3(this.lx, this.ly + (double)this.scale / 2.0, this.lz);
        }

        void updateOffset(double lx, double ly, double lz, float scale, Direction attachFace, int peek) {
            this.lx = lx;
            this.ly = ly;
            this.lz = lz;
            if (this.attachFace != attachFace || this.peek != peek) {
                this.dataDirty = true;
            }
            if (this.scale != scale) {
                this.scaleDirty = true;
            }
            this.scale = scale;
            this.attachFace = attachFace;
            this.peek = peek;
        }

        double peekGrowth() {
            float peekFrac = (float)this.peek / 100.0f;
            double physicalPeek = 0.5 - Math.sin((0.5 + (double)peekFrac) * Math.PI) * 0.5;
            return physicalPeek * (double)this.scale;
        }

        float effectiveScale() {
            return (float)((double)this.scale * this.contraptionScale);
        }

        private List<Object> metadata() {
            ArrayList<Object> values = new ArrayList<Object>();
            BaseEntityData.SharedFlags.addEntityData(((byte)(SHOW_COLLIDERS ? 0 : 32)), values);
            ShulkerData.AttachFace.addEntityData(this.attachFace, values);
            ShulkerData.RawPeekAmount.addEntityData(((byte)this.peek), values);
            ShulkerData.Color.addEntityData((byte)16, values);
            return values;
        }

        void spawn(net.momirealms.craftengine.core.entity.player.Player player, double x, double y, double z) {
            Object anchorPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.anchorId, this.anchorUuid, x, y, z, 0.0f, 0.0f, EntityType.ITEM_DISPLAY, 0, Vec3.ZERO, 0.0);
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(this.entityId, this.uuid, x, y, z, 0.0f, 0.0f, EntityType.SHULKER, 0, Vec3.ZERO, 0.0);
            Object mountPacket = MNms.INSTANCE.constructor$ClientboundSetPassengersPacket(this.anchorId, new int[]{this.entityId});
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata());
            Object scalePacket = MNms.INSTANCE.constructor$ClientboundScaleAttributePacket(this.entityId, this.effectiveScale());
            player.sendPackets(List.of(anchorPacket, addPacket, mountPacket, dataPacket, scalePacket), false);
        }

        void updatePosition(net.momirealms.craftengine.core.entity.player.Player player, double x, double y, double z) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundEntityPositionSyncPacket(this.anchorId, x, y, z, 0.0f, 0.0f, false), false);
        }

        void despawn(net.momirealms.craftengine.core.entity.player.Player player) {
            player.sendPacket(this.despawnPacket, false);
        }

        void render(List<net.momirealms.craftengine.core.entity.player.Player> viewers, Set<UUID> wanted, double x, double y, double z, double contraptionScale, boolean moved) {
            if (contraptionScale != this.contraptionScale) {
                this.contraptionScale = contraptionScale;
                this.scaleDirty = true;
            }
            y -= (double)this.effectiveScale() / 2.0;
            HashSet<UUID> current = new HashSet<UUID>();
            boolean resendScale = this.scaleDirty;
            int generation = VISIBILITY_GENERATION.get();
            if (generation != this.visibilityGeneration) {
                this.visibilityGeneration = generation;
                this.dataDirty = true;
            }
            boolean resendData = this.dataDirty;
            boolean reassertMount = false;
            if (++this.mountTick >= 40) {
                this.mountTick = 0;
                reassertMount = true;
            }
            for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                UUID id = ContraptionShulkerColliderSwarm.uuidOf(p);
                if (id == null) continue;
                if (wanted != null && !wanted.contains(id)) {
                    if (!this.shownTo.remove(id)) continue;
                    this.despawn(p);
                    continue;
                }
                current.add(id);
                if (this.shownTo.add(id)) {
                    this.spawn(p, x, y, z);
                    continue;
                }
                if (moved) {
                    this.updatePosition(p, x, y, z);
                }
                if (reassertMount) {
                    p.sendPackets(List.of(MNms.INSTANCE.constructor$ClientboundSetPassengersPacket(this.anchorId, new int[]{this.entityId}), MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata())), false);
                } else if (resendData) {
                    p.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(this.entityId, this.metadata()), false);
                }
                if (!resendScale) continue;
                p.sendPacket(MNms.INSTANCE.constructor$ClientboundScaleAttributePacket(this.entityId, this.effectiveScale()), false);
            }
            if (resendScale) {
                this.scaleDirty = false;
            }
            if (resendData) {
                this.dataDirty = false;
            }
            this.shownTo.retainAll(current);
        }
    }

    private static final class CellGroup {
        private List<AABB> boxes;
        private Vec3 anchor;
        private final Map<Integer, List<ShulkerBoxFit.Cube>> tierCubes = new HashMap<Integer, List<ShulkerBoxFit.Cube>>();
        private boolean cachedOffAxis;
        private final Map<Integer, List<Slot>> tierSlots = new HashMap<Integer, List<Slot>>();
        private final Map<UUID, Integer> viewerTier = new HashMap<UUID, Integer>();

        CellGroup(List<AABB> boxes) {
            this.setGeometry(boxes);
        }

        void setBoxes(List<AABB> next, List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
            if (this.boxes == next || this.boxes.equals(next)) {
                return;
            }
            this.setGeometry(next);
            this.tierCubes.clear();
            this.dropSlots(viewers);
        }

        private void dropSlots(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
            for (List<Slot> tier : this.tierSlots.values()) {
                for (Slot s : tier) {
                    for (net.momirealms.craftengine.core.entity.player.Player p : viewers) {
                        s.despawn(p);
                    }
                }
            }
            this.tierSlots.clear();
        }

        void syncOffAxis(boolean offAxis, List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
            if (offAxis == this.cachedOffAxis) {
                return;
            }
            this.cachedOffAxis = offAxis;
            this.tierCubes.clear();
            this.dropSlots(viewers);
        }

        private void setGeometry(List<AABB> next) {
            this.boxes = next;
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxX = -1.7976931348623157E308;
            double maxY = -1.7976931348623157E308;
            double maxZ = -1.7976931348623157E308;
            for (AABB b : next) {
                minX = Math.min(minX, b.minX);
                minY = Math.min(minY, b.minY);
                minZ = Math.min(minZ, b.minZ);
                maxX = Math.max(maxX, b.maxX);
                maxY = Math.max(maxY, b.maxY);
                maxZ = Math.max(maxZ, b.maxZ);
            }
            this.anchor = next.isEmpty() ? Vec3.ZERO : new Vec3((minX + maxX) / 2.0, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0);
        }

        List<ShulkerBoxFit.Cube> cubesFor(int budget, boolean offAxis) {
            if (budget <= 0) {
                return List.of();
            }
            return this.tierCubes.computeIfAbsent(budget, b -> ShulkerBoxFit.fit(this.boxes, b, offAxis));
        }

        List<Slot> slotsFor(int budget, boolean offAxis) {
            List<ShulkerBoxFit.Cube> fitted = this.cubesFor(budget, offAxis);
            return this.tierSlots.computeIfAbsent(budget, b -> {
                List<ShulkerBoxFit.Cube> cubes = fitted;
                ArrayList<Slot> out = new ArrayList<Slot>(cubes.size());
                for (ShulkerBoxFit.Cube c : cubes) {
                    out.add(new Slot(c, c.centerX(), c.y0(), c.centerZ(), (float)c.size(), Direction.DOWN, 0));
                }
                return out;
            });
        }

        boolean anyTracked(List<Slot> tier) {
            for (Slot s : tier) {
                if (s.shownTo.isEmpty()) continue;
                return true;
            }
            return false;
        }

        void despawnAll(List<net.momirealms.craftengine.core.entity.player.Player> viewers) {
            this.dropSlots(viewers);
            this.tierCubes.clear();
            this.viewerTier.clear();
        }
    }
}

