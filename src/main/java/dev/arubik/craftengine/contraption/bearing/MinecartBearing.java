package dev.arubik.craftengine.contraption.bearing;

import dev.arubik.craftengine.contraption.glue.GlueRegistry;

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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.player.CePlayers;
import net.momirealms.craftengine.core.util.Key;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
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
 * {@link ContraptionStorage#toBytes}, since a PDC's values are typed
 * primitives/byte-arrays only — a raw vanilla {@code CompoundTag} can't be stored directly).
 *
 * <p>Movement: the minecart's own unmodified vanilla rail physics moves the real entity;
 * {@link MinecartFollowBehavior} just snaps {@code ContraptionState}'s position/yaw to match
 * every tick (see that class). Visual appearance is the plain default minecart model for
 * this pass — a follow-up could ride a packet-only Display swarm on top of the real entity's
 * position the same way {@code ContraptionDisplaySwarm} already does for the block-anchored
 * bearings, but that's not implemented here (documented gap, not attempted-and-broken).
 *
 * <h2>Migration status (2026-08-10)</h2>
 * <ul>
 *   <li>Lifecycle events (placement, damage, death): migrated to {@link ContraptionLifecycleListener} + {@link dev.arubik.craftengine.contraption.type.MinecartContraptionType}</li>
 *   <li>Pack/unpack: migrated to {@link dev.arubik.craftengine.contraption.type.MinecartContraptionType#toItem} / {@link dev.arubik.craftengine.contraption.type.MinecartContraptionType#fromItem}</li>
 *   <li>Kept: {@link #assemble} (from glue), {@link #disassemble} (restore blocks), PDC save/load helpers</li>
 *   <li>External plugins: use ContraptionType API, not these static methods</li>
 * </ul>
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
    private static final NamespacedKey YAW = key("contraption_yaw");
    private static final NamespacedKey EXPLOSION_PROOF = key("contraption_explosion_proof");
    /** Marks a held ItemStack as a packed minecart contraption (see {@link #pickUpToItem}/{@link #placeFromItem}). */
    private static final NamespacedKey ITEM_MARKER = key("contraption_minecart_item");

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

        Set<BlockPos> structure = GlueRegistry.structureAt(realLevel.dimension(), bearingPos);
        // Read the blast-immunity flag NOW, while the bearing block is still in the world (capture removes it below).
        double explosionProof = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior
                .explosionProofAt(realLevel, bearingPos);
        // Veto hook (public API) — parity with ContraptionAssembler#assemble, fired BEFORE any real
        // block is read/removed. Cancelling aborts (returns null, same as no rail beneath would).
        if (ContraptionAssembler.fireAssembleCancelled(bukkitWorld, bearingPos, Key.of("polyfills", "minecart"), structure, null)) {
            return null;
        }
        ContraptionCapture.Result captured = ContraptionCapture.capture(realLevel, structure, bearingPos);
        // Persist glue topology onto the captured level (2026-07-03) — see the matching call in
        // ContraptionAssembler#assemble and ContraptionCapture#captureGlueEdges. This is what makes
        // a minecart bearing's glue survive a restart (it rides along in the entity-PDC structure
        // NBT) and re-stick every block on disassemble.
        ContraptionCapture.captureGlueEdges(realLevel.dimension(), captured.level(), bearingPos);
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
        ContraptionState state = new ContraptionState(id,
                ((org.bukkit.craftbukkit.CraftWorld) bukkitWorld).getHandle().dimension(), captured.level(),
                bearingPos.getX(), bearingPos.getY(), bearingPos.getZ());
        state.setBearingType(Key.of("polyfills", "minecart")); // was never set — left bearingType null (broke the chain tether)
        state.setExplosionProof(explosionProof);
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

        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        ContraptionAssembler.fireAssembled(entity);
        return entity;
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
        // Veto hook (public API) — parity with ContraptionAssembler#disassemble, fired BEFORE any
        // teardown; cancelling leaves the contraption (and its real minecart anchor) live and intact.
        if (ContraptionAssembler.fireDisassembleCancelled(entity)) {
            return;
        }
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());

        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        // Re-stick every block as it lands (2026-07-03 — this path previously never restored glue
        // at all, so a disassembled minecart contraption's blocks scattered unglued). Rebuilds from
        // the level's persisted local edges (restart-safe), rotated to the same footprint below.
        ContraptionCapture.restoreGlue(realLevel.dimension(), state.level(), state.originBearingBlockPos(),
                snapped, quarterTurns);
        ContraptionCapture.restoreRotated(realLevel, state.level(), snapped, quarterTurns);
        // Cheaply-available final resting footprint for ContraptionDisassembledEvent — gathered before
        // dispose() discards the level. Same grid-snap + rotation the block restore just used.
        Set<BlockPos> restingPositions = new java.util.HashSet<>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns),
                        snapped));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        minecart.remove();
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
    }

    /**
     * <b>End-portal / dead-anchor safety fallback (roadmap item #1 — see
     * {@code .migration/ROADMAP-world-boundary.md} §1 "End portals destroy the minecart").</b> When the
     * real anchor minecart is DESTROYED (an end portal removes non-player entities outright; also
     * {@code /kill} or a third-party force-remove) while the contraption is still live,
     * {@link MinecartFollowBehavior} latches {@link MinecartFollowBehavior#wantsDisassembleInPlace()}
     * after a grace window and {@code ContraptionEngine.tickAll} routes here — restoring the structure
     * into the world at its LAST live position rather than leaking a permanently stalled, anchorless
     * contraption.
     *
     * <p>Identical teardown to {@link #disassemble} EXCEPT (a) there is no anchor minecart to pass or
     * remove — it is already gone (best-effort removal below covers the rare case it somehow still
     * resolves), and (b) the {@code fireDisassembleCancelled} veto is intentionally NOT fired: this is
     * an emergency integrity teardown, not a user-initiated disassemble, and must not be vetoable into
     * leaking a dead-anchor contraption. Grid-snap + axis-snap-rotation restore at the current position,
     * glue + furniture restore, dispose the hidden level, fire {@code ContraptionDisassembledEvent}.
     */
    public static void disassembleInPlace(World bukkitWorld, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());

        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        ContraptionCapture.restoreGlue(realLevel.dimension(), state.level(), state.originBearingBlockPos(),
                snapped, quarterTurns);
        ContraptionCapture.restoreRotated(realLevel, state.level(), snapped, quarterTurns);
        Set<BlockPos> restingPositions = new java.util.HashSet<>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns),
                        snapped));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        // Best-effort: the anchor is expected to be gone (that's why we're here), but if a
        // /kill-style removal left a resolvable-but-invalid handle, or any residue survives, drop it.
        try {
            for (MovementBehavior b : state.behaviors()) {
                if (b instanceof MinecartFollowBehavior follow) {
                    Entity cart = org.bukkit.Bukkit.getEntity(follow.entityId());
                    if (cart != null) {
                        cart.remove();
                    }
                    break;
                }
            }
        } catch (Throwable ignored) {
            // the whole contraption is being torn down regardless
        }
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
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
            byte[] bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
            minecart.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
            // Persist the contraption's CURRENT accumulated yaw so it keeps its orientation across a restart
            // (user: "al reiniciar el sv el minecart bearing... pierde su orientacion original"). The structure
            // NBT only holds the block layout at capture (yaw 0); the yaw grows as the minecart turns, so it must
            // be saved separately and re-applied on #rehydrate — mirrors the ghast's yawOffset persistence.
            minecart.getPersistentDataContainer().set(YAW, PersistentDataType.DOUBLE, state.yawRadians());
            minecart.getPersistentDataContainer().set(EXPLOSION_PROOF, PersistentDataType.DOUBLE,
                    state.explosionProof());
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
            ContraptionStorage.loadLevel(level, ContraptionStorage.fromBytes(bytes));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to load minecart bearing structure: " + e);
            return;
        }
        ContraptionState state = new ContraptionState(id,
                ((org.bukkit.craftbukkit.CraftWorld) bukkitWorld).getHandle().dimension(), level, loc.getX(), loc.getY(), loc.getZ());
        state.setBearingType(Key.of("polyfills", "minecart"));
        // Restore the persisted uniform SCALE (roadmap item #9). ContraptionStorage#load already put the
        // saved value back on the level; reaffirm it onto the state (which re-pushes the full transform),
        // so a scaled minecart contraption rehydrates at its saved size. 1.0 (default) for a pre-scale blob.
        state.setScale(level.realScaleFactor());
        // Re-place captured CraftEngine furniture inside the freshly-loaded hidden level (2026-07-03
        // — "todos los craft engine furnitures se pierden al reiniciar el sv"). The furniture
        // metadata rode along in the structure NBT (ContraptionStorage); this rebuilds the live
        // objects + the ContraptionFurniture list so the packet mirror and disassemble restore work
        // exactly like a freshly-assembled contraption.
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        // Restore the persisted orientation (user: "al reiniciar el sv el minecart bearing... pierde su
        // orientacion original"). The state is constructed at yaw 0 and the structure NBT only holds the
        // capture-time (yaw-0) block layout, so the accumulated yaw the contraption had turned to must be
        // re-applied here. MinecartFollowBehavior baselines its raw-yaw accumulator to state.yawRadians() on
        // its first tick (rawAccumYaw = refAccumYaw = state.yawRadians()), so setting it BEFORE the behavior
        // is added makes the cart's future turns accumulate from this restored orientation instead of 0.
        Double savedProof = minecart.getPersistentDataContainer().get(EXPLOSION_PROOF, PersistentDataType.DOUBLE);
        if (savedProof != null) {
            state.setExplosionProof(savedProof);
        }
        Double savedYaw = minecart.getPersistentDataContainer().get(YAW, PersistentDataType.DOUBLE);
        if (savedYaw != null) {
            state.setYawRadians(savedYaw);
        }
        state.addBehavior(new MinecartFollowBehavior(minecart.getUniqueId()));
        ContraptionManager.register(new ContraptionEntity(state));
    }

    // ---------------- pack to / spawn from a held item (2026-07-04 — "de el item al jugador... un
    // minecart con cofre que al ponerlo re spawnea el contraption") ----------------

    /** True if {@code item} is a packed minecart contraption produced by {@link #pickUpToItem}. */
    public static boolean isContraptionItem(ItemStack item) {
        if (item == null || item.getType() != Material.CHEST_MINECART || !item.hasItemMeta()) {
            return false;
        }
        Byte v = item.getItemMeta().getPersistentDataContainer().get(ITEM_MARKER, PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    /** The serialized structure bytes carried by a packed contraption item, or {@code null}. */
    private static byte[] structureBytesOf(ItemStack item) {
        if (!isContraptionItem(item)) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
    }

    /** Builds a chest-minecart item that carries {@code structureBytes} in its meta PDC. */
    private static ItemStack buildItem(byte[] structureBytes) {
        ItemStack item = new ItemStack(Material.CHEST_MINECART);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ITEM_MARKER, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, structureBytes);
        meta.setDisplayName("§bPacked Contraption Minecart");
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Packs a LIVE minecart contraption into a chest-minecart ITEM (2026-07-04 request) — the
     * "pick it up whole" counterpart to {@link #disassemble} (which restores the blocks into the
     * world in place). Dumps the current structure to the item's PDC (the SAME
     * {@link ContraptionStorage} bytes the minecart already carries in its own PDC), tears the
     * live contraption down WITHOUT restoring any real blocks (despawn render/hitbox swarms, dismount
     * seated riders, dispose the hidden level, remove the anchor minecart), and returns the item for
     * the caller to hand the player. {@link #placeFromItem} is the inverse. Returns {@code null} if
     * the structure can't be serialized (nothing is torn down in that failure case).
     *
     * @deprecated Use {@link dev.arubik.craftengine.contraption.type.MinecartContraptionType#toItem(ContraptionEntity, Level)} instead.
     * This static method duplicates the ContraptionType API and will be removed in a future version.
     */
    @Deprecated
    public static ItemStack pickUpToItem(World bukkitWorld, Entity minecart, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        if (state.level() == null) {
            return null;
        }
        byte[] bytes;
        try {
            bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to pack minecart contraption to item: " + e);
            return null;
        }
        ItemStack item = buildItem(bytes);
        // Teardown WITHOUT block restore (the blocks live on in the item, not the world) — mirrors
        // the non-restoring half of #disassemble: despawn everything (this also dismounts any seated
        // rider via ContraptionEntity#despawnRest) + dispose the level + remove the anchor minecart.
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        state.level().dispose();
        minecart.remove();
        return item;
    }

    /**
     * Re-spawns a minecart contraption from a packed {@link #pickUpToItem} item at {@code at}
     * (which must sit on a rail — same rail requirement as {@link #assemble}). Spawns a fresh anchor
     * minecart, stamps the item's structure bytes onto its PDC, and drives {@link #rehydrate} to
     * rebuild the live contraption exactly like a chunk-load rehydrate. The item is NOT consumed
     * here (the caller decides); a fresh contraption id is minted so multiple copies never collide.
     * Returns the new {@link ContraptionEntity}, or {@code null} if the item has no structure or the
     * spawn location isn't a rail.
     */
    public static ContraptionEntity placeFromItem(ItemStack item, World bukkitWorld, Location at) {
        byte[] bytes = structureBytesOf(item);
        if (bytes == null) {
            return null;
        }
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos railPos = new BlockPos(at.getBlockX(), at.getBlockY(), at.getBlockZ());
        if (!(realLevel.getBlockState(railPos).getBlock() instanceof BaseRailBlock)) {
            return null;
        }
        Entity minecart = bukkitWorld.spawnEntity(at, org.bukkit.entity.EntityType.MINECART);
        minecart.setInvulnerable(true);
        UUID id = UUID.randomUUID();
        tag(minecart, id, true);
        minecart.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        rehydrate(minecart); // builds the ContraptionEntity from the PDC we just stamped
        ContraptionEntity entity = ContraptionManager.get(id);
        if (entity == null) {
            // Rehydrate declined (shouldn't happen for a fresh id with valid bytes) — clean up the
            // orphan anchor so we don't leave a tagged, structure-less minecart behind.
            minecart.remove();
        }
        return entity;
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
