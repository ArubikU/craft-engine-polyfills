package dev.arubik.craftengine.contraption;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.contraption.persistence.ContraptionStructureNbt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Entity-anchored bearing (CONTRAPTIONS.md Phase 6 "Minecart bearing"). Per an explicit
 * course-correction during this session: this does NOT register a new custom
 * {@code net.minecraft.world.entity.EntityType} (an earlier draft considered reflectively
 * un-freezing {@code BuiltInRegistries.ENTITY_TYPE} and extending {@code AbstractMinecart} —
 * walked back as too risky/fragile for the value it added). Instead it spawns a REAL,
 * already-existing vanilla minecart ({@code org.bukkit.entity.EntityType.MINECART}) as the
 * bearing's persistence anchor — its chunk load/unload is then 100% natural vanilla entity
 * persistence, and the contraption's dumped structure rides along in the entity's own
 * Bukkit {@link PersistentDataContainer} (serialized via
 * {@link ContraptionStructureNbt#toBytes}, since a PDC's values are typed
 * primitives/byte-arrays only — a raw vanilla {@code CompoundTag} can't be stored directly).
 *
 * <p>Movement: the minecart's own unmodified vanilla rail physics moves the real entity;
 * {@link MinecartFollowBehavior} just snaps {@code ContraptionState}'s position/yaw to match
 * every tick (see that class). Visual appearance is the plain default minecart model for
 * this pass — a follow-up could ride a packet-only Display swarm on top of the real entity's
 * position the same way {@code ContraptionDisplaySwarm} already does for the block-anchored
 * bearings, but that's not implemented here (documented gap, not attempted-and-broken).
 */
public final class MinecartBearing {

    private MinecartBearing() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey(CraftEnginePolyfills.instance(), name);
    }

    private static final NamespacedKey IS_BEARING = key("contraption_minecart_bearing");
    private static final NamespacedKey ASSEMBLED = key("contraption_assembled");
    private static final NamespacedKey CONTRAPTION_ID = key("contraption_id");
    private static final NamespacedKey STRUCTURE = key("contraption_structure");

    public static boolean isBearing(Entity entity) {
        return entity.getPersistentDataContainer().has(IS_BEARING, PersistentDataType.BYTE);
    }

    public static boolean isAssembled(Entity entity) {
        Byte v = entity.getPersistentDataContainer().get(ASSEMBLED, PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    public static UUID contraptionId(Entity entity) {
        String id = entity.getPersistentDataContainer().get(CONTRAPTION_ID, PersistentDataType.STRING);
        return id == null ? null : UUID.fromString(id);
    }

    /**
     * Static -&gt; Dynamic for a MINECART bearing: {@code bearingPos} must sit directly above
     * a rail block (Create's own "bearing rides the rail beneath it" convention). Captures
     * the glued structure at {@code bearingPos} (the bearing block itself included, same as
     * the other two bearing types), removes it from the world, spawns a real minecart at the
     * bearing's old location, and tags it with the dumped structure. Returns {@code null} if
     * there's no rail beneath — the caller reports that to the player.
     */
    public static ContraptionEntity assemble(World bukkitWorld, BlockPos bearingPos) {
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockState below = realLevel.getBlockState(bearingPos.below());
        if (!(below.getBlock() instanceof BaseRailBlock)) {
            return null;
        }

        Set<BlockPos> structure = GlueRegistry.structureAt(bukkitWorld.getUID(), bearingPos);
        ContraptionCapture.Result captured = ContraptionCapture.capture(realLevel, structure, bearingPos);
        // Persist glue topology onto the captured level (2026-07-03) — see the matching call in
        // ContraptionAssembler#assemble and ContraptionCapture#captureGlueEdges. This is what makes
        // a minecart bearing's glue survive a restart (it rides along in the entity-PDC structure
        // NBT) and re-stick every block on disassemble.
        ContraptionCapture.captureGlueEdges(bukkitWorld.getUID(), captured.level(), bearingPos);
        // Furniture scan BEFORE the blocks are removed — same ordering/reasoning as
        // ContraptionAssembler#assemble/HologramTest#start (bug fix — the minecart bearing path
        // never called this at all, so a furniture piece glued into a minecart-bearing structure
        // was silently left behind in the real world instead of being captured into the
        // contraption): it only reads real entities near the footprint, so it must run before
        // removeFromWorld clears them out from under it.
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear(realLevel, structure, bearingPos, captured.level());
        ContraptionCapture.removeFromWorld(realLevel, structure);

        Location spawnAt = new Location(bukkitWorld, bearingPos.getX() + 0.5, bearingPos.getY(), bearingPos.getZ() + 0.5);
        Entity minecart = bukkitWorld.spawnEntity(spawnAt, org.bukkit.entity.EntityType.MINECART);
        // Bug fix (2026-07-02 live-test report — "puedo romper el minecart del minecart
        // bearing"): a plain vanilla minecart takes damage/breaks like any other minecart, but
        // THIS minecart secretly holds the entire captured contraption's structure NBT in its
        // PDC (see #saveStructure below) — if it dies to normal damage, that structure is lost
        // forever (no restore-blocks cleanup is ever triggered by vanilla entity death). The
        // ONLY sanctioned way to remove a minecart bearing is the explicit hammer-driven
        // #disassemble flow. Per Bukkit's Entity#setInvulnerable semantics this blocks normal
        // damage/death (fire, fall, explosions, mobs, players, void-adjacent sources) while still
        // allowing plugin code (this class) to remove() it directly.
        minecart.setInvulnerable(true);

        UUID id = UUID.randomUUID();
        ContraptionState state = new ContraptionState(id, bukkitWorld.getUID(), captured.level(),
                bearingPos.getX(), bearingPos.getY(), bearingPos.getZ());
        state.setFurniture(furnitureResult.furniture());
        // Task 1 parity (see ContraptionAssembler#assemble's matching block): re-register any
        // real player who was sitting in a captured seat at the exact moment of assembly.
        for (Map.Entry<UUID, Vec3> e : furnitureResult.seatedRiders().entrySet()) {
            state.addSeatedRider(e.getKey(), e.getValue());
            UUID mountId = furnitureResult.seatedRiderMounts().get(e.getKey());
            if (mountId != null) {
                state.setSeatedRiderMount(e.getKey(), mountId);
            }
        }
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        state.addBehavior(new MinecartFollowBehavior(minecart.getUniqueId()));

        tag(minecart, id, true);
        saveStructure(minecart, state);

        return ContraptionManager.register(new ContraptionEntity(state));
    }

    /**
     * Dynamic -&gt; Static: grid-snap + axis-snap-rotation + collision-check restore at the
     * minecart's CURRENT position, then remove the minecart.
     *
     * <p>Furniture restore (bug fix — see this class's own gap: unlike {@code
     * ContraptionAssembler#disassemble}/{@code HologramTest#stop}, this method never called
     * {@code ContraptionFurnitureCapture#restoreFurniture} at all, so a furniture piece captured
     * by a minecart bearing (see {@link #assemble}'s matching fix) just vanished forever on
     * disassemble instead of being re-placed as a real entity). Same ordering as those two: AFTER
     * the real blocks are restored, so a wall/ceiling/floor-anchored furniture variant has its
     * supporting block back first.
     *
     * <p>{@code quarterTurns} mirrors {@code ContraptionAssembler#disassemble}'s own axis-snap
     * comment — a minecart bearing's yaw is continuous (driven by real vanilla rail physics via
     * {@link MinecartFollowBehavior}, can stop at any heading), and captures always start at yaw
     * 0, so {@code quarterTurnsBetween(0, state.yawRadians())} is exactly how far the current yaw
     * has drifted from that origin, snapped to the nearest cardinal direction — same value now
     * fed to both the block restore ({@code restoreRotated}, replacing the old non-rotating
     * {@code restoreWithCollisionCheck} call) and the furniture restore below, so both land on the
     * SAME rotated footprint.
     */
    public static void disassemble(World bukkitWorld, Entity minecart, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());

        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        // Re-stick every block as it lands (2026-07-03 — this path previously never restored glue
        // at all, so a disassembled minecart contraption's blocks scattered unglued). Rebuilds from
        // the level's persisted local edges (restart-safe), rotated to the same footprint below.
        ContraptionCapture.restoreGlue(bukkitWorld.getUID(), state.level(), state.originBearingBlockPos(),
                snapped, quarterTurns);
        ContraptionCapture.restoreRotated(realLevel, state.level(), snapped, quarterTurns);
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        minecart.remove();
    }

    private static void tag(Entity minecart, UUID contraptionId, boolean assembled) {
        PersistentDataContainer pdc = minecart.getPersistentDataContainer();
        pdc.set(IS_BEARING, PersistentDataType.BYTE, (byte) 1);
        pdc.set(ASSEMBLED, PersistentDataType.BYTE, (byte) (assembled ? 1 : 0));
        pdc.set(CONTRAPTION_ID, PersistentDataType.STRING, contraptionId.toString());
    }

    /**
     * Re-dumps the CURRENT live structure into the minecart's PDC. Call this before any
     * teardown (e.g. right before a chunk unload disposes the in-memory {@link
     * ContraptionLevel}) so a later chunk load can rehydrate from exactly where things left
     * off — mirrors how a normal chunk save captures a block-entity's current NBT, not a
     * frozen capture-time snapshot.
     */
    public static void saveStructure(Entity minecart, ContraptionState state) {
        if (state.level() == null) {
            return;
        }
        try {
            byte[] bytes = ContraptionStructureNbt.toBytes(ContraptionStructureNbt.dump(state.level()));
            minecart.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to save minecart bearing structure: " + e);
        }
    }

    /**
     * Rehydrates a {@link ContraptionEntity} from a minecart's PDC (call on chunk load for
     * any minecart found to be a mid-assembled bearing) — no-ops if it's already live (e.g.
     * this is the very entity that was just spawned by {@link #assemble}).
     */
    public static void rehydrate(Entity minecart) {
        if (!isBearing(minecart) || !isAssembled(minecart)) {
            return;
        }
        // Same invulnerability fix as #assemble — re-applied here too since a bearing minecart
        // saved to disk by an older build (before this fix existed) would load back in with
        // Bukkit's persisted "invulnerable" flag still false, and setInvulnerable is otherwise
        // only ever called at spawn time in #assemble, never touched again for the entity's
        // lifetime. Idempotent — a no-op if already true.
        minecart.setInvulnerable(true);
        UUID id = contraptionId(minecart);
        byte[] bytes = minecart.getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
        if (id == null || bytes == null || ContraptionManager.get(id) != null) {
            return;
        }
        World bukkitWorld = minecart.getWorld();
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        Location loc = minecart.getLocation();
        ContraptionLevel level = ContraptionLevel.create(realLevel, loc.getX(), loc.getY(), loc.getZ(), 0);
        try {
            ContraptionStructureNbt.load(level, ContraptionStructureNbt.fromBytes(bytes));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to load minecart bearing structure: " + e);
            return;
        }
        ContraptionState state = new ContraptionState(id, bukkitWorld.getUID(), level, loc.getX(), loc.getY(), loc.getZ());
        // Re-place captured CraftEngine furniture inside the freshly-loaded hidden level (2026-07-03
        // — "todos los craft engine furnitures se pierden al reiniciar el sv"). The furniture
        // metadata rode along in the structure NBT (ContraptionStructureNbt); this rebuilds the live
        // objects + the ContraptionFurniture list so the packet mirror and disassemble restore work
        // exactly like a freshly-assembled contraption.
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        state.addBehavior(new MinecartFollowBehavior(minecart.getUniqueId()));
        ContraptionManager.register(new ContraptionEntity(state));
    }

    /**
     * Belt-and-suspenders data-loss guard (2026-07-02 session, Bug B). {@code
     * Entity#setInvulnerable(true)} (set in both {@link #assemble} and {@link #rehydrate}) already
     * blocks every normal {@code EntityDamageEvent}-driven destruction path (sword hits,
     * explosions, fire/lava, mob attacks, fall damage — Bukkit's invulnerable flag is a base
     * {@code Entity#hurt} short-circuit, not overridden per-subtype, so it applies uniformly to
     * {@code AbstractMinecart} same as any other entity). This listener exists purely as defense
     * in depth for whatever setInvulnerable does NOT cover: an explicit {@code /kill}, a
     * third-party plugin force-removing vehicles, or any future code path that fires {@link
     * VehicleDamageEvent}/{@link VehicleDestroyEvent} without going through the normal damage
     * pipeline. Both events fire for any {@code Vehicle} (a plain {@code Minecart} included), so
     * cancelling them whenever the vehicle is a bearing (assembled or not — an unassembled/orphan
     * tag is still a plugin-owned entity, not something vanilla destruction should be touching)
     * closes the gap unconditionally. The ONLY sanctioned removal path remains the explicit
     * hammer-driven {@link #disassemble} flow (which calls {@code minecart.remove()} directly,
     * never through damage/destroy events, so it's unaffected by this cancellation).
     */
    public static final class DamageGuard implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onVehicleDamage(VehicleDamageEvent event) {
            if (isBearing(event.getVehicle())) {
                event.setCancelled(true);
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onVehicleDestroy(VehicleDestroyEvent event) {
            if (isBearing(event.getVehicle())) {
                event.setCancelled(true);
            }
        }
    }
}
