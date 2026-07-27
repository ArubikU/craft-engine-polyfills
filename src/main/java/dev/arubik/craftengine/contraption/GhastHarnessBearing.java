package dev.arubik.craftengine.contraption;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import dev.arubik.craftengine.contraption.persistence.ContraptionStructureNbt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Happy-ghast harness bearing (2026-07-16 goal — "happy ghast harness contraption"): the second
 * entity-anchored bearing, built to the same shape as {@link MinecartBearing} (read that class first —
 * this deliberately mirrors its {@code isBearing}/{@code isAssembled}/{@code contraptionId}/
 * {@code assemble}/{@code disassembleInPlace}/{@code saveStructure}/{@code rehydrate} layout and its
 * save-to-item trio), with the HARNESS ITEM playing the role the chest-minecart item plays there and a
 * real vanilla {@code HappyGhast} playing the anchor role the real minecart plays.
 *
 * <h2>The structure is the ghast's glue, not a generated shell</h2>
 * The redesign — "lets gonna delete the default 4x4x4, just when right click a ghast with a harness
 * equiped and with a hammer, if the ghast is between some glued block ... it should make a entire
 * contraption" — makes this an ordinary glue capture, identical to {@link MinecartBearing#assemble}:
 * {@link #gluedStructureAround} probes {@link GlueRegistry} at every cell the ghast's own bounding box
 * overlaps, and the union of whatever glue components it finds IS the contraption. Nothing is conjured
 * from the harness's colour any more, so an unglued ghast assembles nothing at all (the caller reports
 * that) rather than being wrapped in wool.
 *
 * <h2>Why the anchor offset is per-contraption</h2>
 * Every other bearing's anchor is a block that keeps standing; this one's is a flying entity, and the
 * captured structure sits wherever the player glued it — it is NOT centred on the ghast. So the vector
 * from the ghast to the contraption's origin cell is a property of the individual assembly, captured
 * once ({@code anchorCell - ghastPosition}) and thereafter re-added to the ghast's live position every
 * tick by {@link GhastFollowBehavior}. That is what holds the structure in the exact pose it was built
 * in. It is persisted next to the structure in both places the structure itself goes (the ghast's PDC
 * for chunk load, the harness item for save-to-item), since neither can be re-derived from geometry.
 *
 * <p>The anchor has a ROTATIONAL half with exactly the same status, persisted in exactly the same two
 * places: {@link GhastHarness#captureYawOffset}'s {@code contraptionYaw - ghastYaw}, stored under
 * {@link #ANCHOR_YAW}. A pose is a position and an orientation, and persisting only the position is what
 * made a restart "acomodar al norte el contraption" — the structure came back at the right offset from
 * the ghast, facing the wrong way. Both halves are written by {@link #tag} (PDC) and {@link #packInto}
 * (item), and read back by {@link #rehydrate} and {@link #restoreFromItem} respectively.
 *
 * <h2>Why this bypasses {@code ContraptionAssembler#attachDefaultBehavior}</h2>
 * Same reason {@link BearingType#MINECART} does (see that method's own {@code GHAST}/{@code MINECART}
 * cases): {@link GhastFollowBehavior} needs the anchor ENTITY's UUID, which the block-anchored
 * {@code attachDefaultBehavior(Level, BlockPos, ...)} signature has no way to supply. The behavior is
 * therefore attached here, once the ghast is known, and {@link ContraptionState#setBearingType} is
 * recorded here too so the state still carries its own identity.
 *
 * <h2>Persistence</h2>
 * The ghast is a real entity, so — exactly like the minecart — its chunk load/unload is 100% natural
 * vanilla entity persistence and the dumped structure rides along in the entity's own Bukkit
 * {@link PersistentDataContainer} (serialized via {@link ContraptionStructureNbt#toBytes}, since a PDC's
 * values are typed primitives/byte-arrays only). {@code ContraptionChunkLifecycleListener} drives
 * save-then-teardown on unload and {@link #rehydrate} on load, the same as the minecart branch.
 */
public final class GhastHarnessBearing {

    private GhastHarnessBearing() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey(CraftEnginePolyfills.instance(), name);
    }

    private static final NamespacedKey IS_BEARING = key("contraption_ghast_bearing");
    private static final NamespacedKey ASSEMBLED = key("contraption_assembled");
    private static final NamespacedKey CONTRAPTION_ID = key("contraption_id");
    private static final NamespacedKey STRUCTURE = key("contraption_structure");
    /** Marks a harness ItemStack as carrying a packed contraption — see {@link #packInto}/{@link #structureBytesOf}. */
    private static final NamespacedKey ITEM_MARKER = key("contraption_ghast_item");
    /** The per-contraption ghast-to-origin vector — see the class javadoc. Three keys because a PDC has no vector/double-array type. */
    private static final NamespacedKey ANCHOR_X = key("contraption_ghast_anchor_x");
    private static final NamespacedKey ANCHOR_Y = key("contraption_ghast_anchor_y");
    private static final NamespacedKey ANCHOR_Z = key("contraption_ghast_anchor_z");
    /**
     * The per-contraption yaw offset — the rotational half of the anchor, stored beside it in both places
     * for the same reason: see "Why the anchor offset is per-contraption" and
     * {@link GhastHarness#captureYawOffset}. Absent on any assembly made before this key existed, which is
     * what {@link GhastFollowBehavior}'s legacy fallback is for.
     */
    private static final NamespacedKey ANCHOR_YAW = key("contraption_ghast_anchor_yaw");

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
     * The glued structure a harnessed {@code ghast} would assemble: the union of the glue components
     * reachable from any cell its own bounding box overlaps ("if the ghast is between some glued block
     * — like the blocks that the ghast ocupe are glued — it should make a entire contraption"). Empty
     * when nothing the ghast occupies is glued.
     *
     * <p>Only cells that are actually glue NODES are probed. {@link GlueRegistry#structureAt} answers a
     * singleton {@code {pos}} for an unknown position, so probing every cell blindly would return the
     * ghast's entire 4x4x4 of mostly-air as a "structure" and assemble a box of nothing.
     */
    public static Set<BlockPos> gluedStructureAround(World bukkitWorld, Entity ghast) {
        UUID worldId = bukkitWorld.getUID();
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        Set<BlockPos> structure = new HashSet<>();
        // Probe a block PAST the ghast's own box, not just the cells it occupies. A harnessed ghast
        // hovers INSIDE the thing being built — its box is full of the room's air, and the glued floor,
        // walls and ceiling it sits among are the cells immediately outside it. Probing only the
        // overlapped cells therefore found whichever surface the ghast happened to be clipping and
        // missed the rest of the build, which is what "the blocks are glued but don't join" looks like.
        for (BlockPos cell : GhastHarness.cellsOverlapping(
                handleOf(ghast).getBoundingBox().inflate(GLUE_PROBE_REACH))) {
            if (!graph.hasNode(cell) || structure.contains(cell)) {
                continue; // not glued, or already covered by a component gathered from an earlier cell
            }
            structure.addAll(GlueRegistry.structureAt(worldId, cell));
        }
        return structure;
    }

    /**
     * How far past the ghast's own bounding box to look for glue, in blocks.
     *
     * <p>One block: enough to reach whatever the ghast is resting against or enclosed by, and no
     * further. Reaching wider would start swallowing glued structures the ghast is merely parked NEXT
     * to, which is a much worse failure than missing one — an unwanted capture tears real blocks out of
     * the world.
     *
     * <p>This only widens where glue is LOOKED for. Each hit still contributes its whole connected
     * component via {@code GlueRegistry#structureAt}, so what actually gets taken is decided by how the
     * player glued it, not by this number.
     */
    private static final double GLUE_PROBE_REACH = 1.0;

    /**
     * Assembles the glued structure the harnessed {@code ghast} sits among onto that ghast, or — if
     * {@code harness} carries packed structure bytes ({@link #packInto} stamped it when it last left a
     * ghast) — restores those verbatim instead, so whatever the player built comes back where it was
     * relative to the ghast.
     *
     * <p>Returns {@code null} if {@code harness} isn't a harness, the ghast can't wear one
     * ({@code HappyGhast#canUseSlot(BODY)} is {@code isAlive() && !isBaby()} — a baby ghast can never
     * have a contraption), or there is no glued structure to take.
     */
    public static ContraptionEntity assemble(World bukkitWorld, LivingEntity ghast, ItemStack harness) {
        if (harness == null || !GhastHarness.isHarness(harness.getType()) || !canWearHarness(ghast)) {
            return null;
        }
        byte[] packed = structureBytesOf(harness);
        if (packed == null) {
            return assembleFromGlue(bukkitWorld, ghast);
        }
        PersistentDataContainer itemPdc = harness.getItemMeta().getPersistentDataContainer();
        return restoreFromItem(bukkitWorld, ghast, packed, readAnchorOffset(itemPdc), readYawOffset(itemPdc));
    }

    /** Glue-scan / capture / remove / register, exactly as {@link MinecartBearing#assemble} does it. */
    private static ContraptionEntity assembleFromGlue(World bukkitWorld, LivingEntity ghast) {
        Set<BlockPos> structure = gluedStructureAround(bukkitWorld, ghast);
        if (structure.isEmpty()) {
            return null;
        }
        Location loc = ghast.getLocation();
        BlockPos anchor = ContraptionMath.gridSnap(new Vec3(loc.getX(), loc.getY(), loc.getZ()));
        // Veto hook (public API) — parity with ContraptionAssembler#assemble/MinecartBearing#assemble,
        // fired BEFORE any real block is read/removed. Cancelling aborts the same as an empty structure.
        if (ContraptionAssembler.fireAssembleCancelled(bukkitWorld, anchor, BearingType.GHAST, structure, null)) {
            return null;
        }
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        ContraptionCapture.Result captured = ContraptionCapture.capture(realLevel, structure, anchor);
        ContraptionCapture.captureGlueEdges(bukkitWorld.getUID(), captured.level(), anchor);
        // Furniture scan BEFORE the blocks are removed — same ordering/reasoning as every other capture
        // path: it reads real entities near the footprint, which removeFromWorld would clear out first.
        ContraptionFurnitureCapture.Result furnitureResult = ContraptionFurnitureCapture.captureNear(realLevel,
                structure, anchor, captured.level());
        ContraptionCapture.removeFromWorld(realLevel, structure);

        ContraptionState state = new ContraptionState(UUID.randomUUID(), bukkitWorld.getUID(), captured.level(),
                anchor.getX(), anchor.getY(), anchor.getZ());
        state.setFurniture(furnitureResult.furniture());
        for (Map.Entry<UUID, Vec3> e : furnitureResult.seatedRiders().entrySet()) {
            state.addSeatedRider(e.getKey(), e.getValue());
            UUID mountId = furnitureResult.seatedRiderMounts().get(e.getKey());
            if (mountId != null) {
                state.setSeatedRiderMount(e.getKey(), mountId);
            }
        }
        state.setScale(ghastScale(ghast));
        for (MovementBehavior autoBehavior : captured.autoBehaviors()) {
            state.addBehavior(autoBehavior);
        }
        Vec3 anchorOffset = new Vec3(anchor.getX() - loc.getX(), anchor.getY() - loc.getY(),
                anchor.getZ() - loc.getZ());
        // Measured EAGERLY, here, against the heading the capture actually happened at — the only moment it
        // is knowable. state's yaw is 0 (a capture is world-aligned), so this is just the negated ghast
        // heading; it is written the long way round so the definition stays visible rather than encoded.
        OptionalDouble yawOffset = OptionalDouble.of(
                GhastHarness.captureYawOffset(state.yawRadians(), loc.getYaw()));
        return finish(bukkitWorld, ghast, state, anchorOffset, yawOffset);
    }

    /** Rebuilds a contraption from a packed harness's bytes, at the same pose relative to the ghast. */
    private static ContraptionEntity restoreFromItem(World bukkitWorld, LivingEntity ghast, byte[] packed,
            Vec3 anchorOffset, OptionalDouble yawOffset) {
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        Location loc = ghast.getLocation();
        Vec3 anchor = new Vec3(loc.getX() + anchorOffset.x, loc.getY() + anchorOffset.y,
                loc.getZ() + anchorOffset.z);
        ContraptionLevel level = ContraptionLevel.create(realLevel, anchor.x, anchor.y, anchor.z, 0.0);
        try {
            ContraptionStructureNbt.load(level, ContraptionStructureNbt.fromBytes(packed));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to restore ghast harness structure from item: " + e);
            level.dispose();
            return null;
        }
        if (level.blockCount() == 0) {
            level.dispose();
            return null;
        }
        ContraptionState state = new ContraptionState(UUID.randomUUID(), bukkitWorld.getUID(), level, anchor.x,
                anchor.y, anchor.z);
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        // The restored structure carries its own persisted scale; GhastFollowBehavior re-reads the live
        // ghast's next tick and corrects it either way.
        state.setScale(level.realScaleFactor());
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        return finish(bukkitWorld, ghast, state, anchorOffset, yawOffset);
    }

    /** The shared tail of both assemble paths: anchor the state to the ghast, tag, persist, register. */
    private static ContraptionEntity finish(World bukkitWorld, LivingEntity ghast, ContraptionState state,
            Vec3 anchorOffset, OptionalDouble yawOffset) {
        state.setBearingType(BearingType.GHAST);
        state.setAnchorEntityId(ghast.getUniqueId());
        state.addBehavior(new GhastFollowBehavior(ghast.getUniqueId(), anchorOffset, yawOffset));

        tag(ghast, state.id(), true, anchorOffset, yawOffset);
        saveStructure(ghast, state);

        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        ContraptionAssembler.fireAssembled(entity);
        return entity;
    }

    /**
     * The ghast's live uniform scale. {@code HappyGhast#sanitizeScale} clamps to {@code 1.0F} and
     * {@code getAgeScale()} is {@code BABY_SCALE = 0.2375F} / {@code 1.0F}, so this is the one
     * authoritative number — see {@link GhastFollowBehavior}'s "Scale inheritance".
     */
    private static double ghastScale(LivingEntity ghast) {
        return ((org.bukkit.craftbukkit.entity.CraftLivingEntity) ghast).getHandle().getScale();
    }

    private static net.minecraft.world.entity.Entity handleOf(Entity entity) {
        return ((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle();
    }

    /**
     * Mirrors {@code HappyGhast#canUseSlot(EquipmentSlot.BODY)}, which is {@code isAlive() && !isBaby()}
     * (verified against the mapped jar) — a baby ghast cannot wear a harness, so it can never have a
     * contraption. Read through the NMS handle rather than re-derived from Bukkit's {@code Ageable} so
     * the rule stays byte-identical to the one vanilla actually enforces.
     */
    private static boolean canWearHarness(LivingEntity ghast) {
        if (!(ghast instanceof org.bukkit.entity.HappyGhast)) {
            return false;
        }
        return ((org.bukkit.craftbukkit.entity.CraftLivingEntity) ghast).getHandle()
                .canUseSlot(net.minecraft.world.entity.EquipmentSlot.BODY);
    }

    private static void tag(Entity ghast, UUID contraptionId, boolean assembled, Vec3 anchorOffset,
            OptionalDouble yawOffset) {
        PersistentDataContainer pdc = ghast.getPersistentDataContainer();
        pdc.set(IS_BEARING, PersistentDataType.BYTE, (byte) 1);
        pdc.set(ASSEMBLED, PersistentDataType.BYTE, (byte) (assembled ? 1 : 0));
        pdc.set(CONTRAPTION_ID, PersistentDataType.STRING, contraptionId.toString());
        writeAnchor(pdc, anchorOffset, yawOffset);
    }

    /**
     * Public {@link #untag}: strips a ghast's bearing tags without touching the ghast itself, so it stops
     * being a resurrection source for a contraption that has already been destroyed. For discard-style
     * teardowns outside this class (see {@code ContraptionKill}) — the ordinary paths untag as part of
     * their own flow and should keep doing so.
     */
    public static void forget(Entity ghast) {
        untag(ghast);
    }

    /** Clears every bearing tag, so a bare ghast never looks like a mid-assembled one to {@link #rehydrate}. */
    private static void untag(Entity ghast) {
        PersistentDataContainer pdc = ghast.getPersistentDataContainer();
        pdc.remove(IS_BEARING);
        pdc.remove(ASSEMBLED);
        pdc.remove(CONTRAPTION_ID);
        pdc.remove(STRUCTURE);
        pdc.remove(ANCHOR_X);
        pdc.remove(ANCHOR_Y);
        pdc.remove(ANCHOR_Z);
        pdc.remove(ANCHOR_YAW);
    }

    /** Writes both halves of the anchor pose — see the class javadoc's "Why the anchor offset is per-contraption". */
    private static void writeAnchor(PersistentDataContainer pdc, Vec3 offset, OptionalDouble yawOffset) {
        pdc.set(ANCHOR_X, PersistentDataType.DOUBLE, offset.x);
        pdc.set(ANCHOR_Y, PersistentDataType.DOUBLE, offset.y);
        pdc.set(ANCHOR_Z, PersistentDataType.DOUBLE, offset.z);
        if (yawOffset.isPresent()) {
            pdc.set(ANCHOR_YAW, PersistentDataType.DOUBLE, yawOffset.getAsDouble());
        } else {
            // Only reachable when packing a legacy contraption that has never resolved an offset. Writing a
            // made-up 0.0 would be indistinguishable from a genuine one and would silently pin the restored
            // structure to the ghast's facing; leaving the key absent keeps it on the legacy path.
            pdc.remove(ANCHOR_YAW);
        }
    }

    /** The stored ghast-to-origin vector, or {@link Vec3#ZERO} if the container carries none. */
    private static Vec3 readAnchorOffset(PersistentDataContainer pdc) {
        Double x = pdc.get(ANCHOR_X, PersistentDataType.DOUBLE);
        Double y = pdc.get(ANCHOR_Y, PersistentDataType.DOUBLE);
        Double z = pdc.get(ANCHOR_Z, PersistentDataType.DOUBLE);
        if (x == null || y == null || z == null) {
            return Vec3.ZERO;
        }
        return new Vec3(x, y, z);
    }

    /**
     * The stored yaw offset, or empty for a container written before {@link #ANCHOR_YAW} existed. Empty is
     * NOT the same as {@code 0.0} — see {@link GhastFollowBehavior}'s constructor.
     */
    private static OptionalDouble readYawOffset(PersistentDataContainer pdc) {
        Double yaw = pdc.get(ANCHOR_YAW, PersistentDataType.DOUBLE);
        return yaw == null ? OptionalDouble.empty() : OptionalDouble.of(yaw);
    }

    /**
     * Re-dumps the CURRENT live structure into the ghast's PDC — see
     * {@link MinecartBearing#saveStructure} for the full rationale (call before any teardown so a later
     * chunk load rehydrates from where things left off, not from a frozen assemble-time snapshot).
     */
    public static void saveStructure(Entity ghast, ContraptionState state) {
        if (state.level() == null) {
            return;
        }
        try {
            byte[] bytes = ContraptionStructureNbt.toBytes(ContraptionStructureNbt.dump(state.level()));
            ghast.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to save ghast harness structure: " + e);
        }
    }

    /**
     * Rehydrates a {@link ContraptionEntity} from a ghast's PDC (call on chunk load for any ghast found
     * to be a mid-assembled bearing) — no-ops if it's already live.
     */
    public static void rehydrate(Entity ghast) {
        if (!isBearing(ghast) || !isAssembled(ghast) || !(ghast instanceof LivingEntity living)) {
            return;
        }
        UUID id = contraptionId(ghast);
        byte[] bytes = ghast.getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
        if (id == null || bytes == null || ContraptionManager.get(id) != null) {
            return;
        }
        World bukkitWorld = ghast.getWorld();
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        Vec3 anchorOffset = readAnchorOffset(ghast.getPersistentDataContainer());
        OptionalDouble yawOffset = readYawOffset(ghast.getPersistentDataContainer());
        Location loc = ghast.getLocation();
        Vec3 anchor = new Vec3(loc.getX() + anchorOffset.x, loc.getY() + anchorOffset.y,
                loc.getZ() + anchorOffset.z);
        ContraptionLevel level = ContraptionLevel.create(realLevel, anchor.x, anchor.y, anchor.z, 0.0);
        try {
            ContraptionStructureNbt.load(level, ContraptionStructureNbt.fromBytes(bytes));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to load ghast harness structure: " + e);
            level.dispose();
            return;
        }
        ContraptionState state = new ContraptionState(id, bukkitWorld.getUID(), level, anchor.x, anchor.y, anchor.z);
        state.setBearingType(BearingType.GHAST);
        state.setAnchorEntityId(living.getUniqueId());
        state.setScale(level.realScaleFactor());
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        state.addBehavior(new GhastFollowBehavior(living.getUniqueId(), anchorOffset, yawOffset));
        ContraptionManager.register(new ContraptionEntity(state));
    }

    // ---------------- pack to / restore from the harness item ----------------

    /** The serialized structure bytes carried by a packed harness, or {@code null} for a plain one. */
    public static byte[] structureBytesOf(ItemStack item) {
        if (item == null || !GhastHarness.isHarness(item.getType()) || !item.hasItemMeta()) {
            return null;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        Byte marked = pdc.get(ITEM_MARKER, PersistentDataType.BYTE);
        if (marked == null || marked == 0) {
            return null;
        }
        return pdc.get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
    }

    /** The ghast-to-origin vector a packed harness carries — see the class javadoc's "anchor offset". */
    private static Vec3 itemAnchorOffset(ItemStack item) {
        return readAnchorOffset(item.getItemMeta().getPersistentDataContainer());
    }

    /** True if {@code item} is a harness carrying a packed contraption. */
    public static boolean isContraptionItem(ItemStack item) {
        return structureBytesOf(item) != null;
    }

    /**
     * Stamps {@code state}'s CURRENT structure onto {@code harness} <b>in place</b>, returning whether it
     * worked. The harness item is this bearing's save-to-item counterpart to the chest-minecart item, but
     * the write has to happen while the harness is still EQUIPPED — see {@link GhastHarnessListener}'s
     * javadoc for why (vanilla's shear path drops the equipped stack itself, and the only Bukkit event
     * that reports the removal hands out copies).
     */
    public static boolean packInto(ItemStack harness, ContraptionState state) {
        if (harness == null || !GhastHarness.isHarness(harness.getType()) || state.level() == null) {
            return false;
        }
        byte[] bytes;
        try {
            bytes = ContraptionStructureNbt.toBytes(ContraptionStructureNbt.dump(state.level()));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to pack ghast contraption into harness: " + e);
            return false;
        }
        ItemMeta meta = harness.getItemMeta();
        meta.getPersistentDataContainer().set(ITEM_MARKER, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        writeAnchor(meta.getPersistentDataContainer(), anchorOffsetOf(state), yawOffsetOf(state));
        meta.setDisplayName("§bPacked Ghast Harness");
        harness.setItemMeta(meta);
        return true;
    }

    /** A live contraption's ghast-to-origin vector, read off the behavior that owns it. */
    /**
     * This contraption's live yaw offset, for persisting. Empty only if the follow behavior has not yet
     * measured one (a legacy assembly on its very first tick), in which case the reader falls back the
     * same way {@link GhastFollowBehavior} does.
     */
    private static OptionalDouble yawOffsetOf(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (behavior instanceof GhastFollowBehavior follow) {
                return follow.yawOffset();
            }
        }
        return OptionalDouble.empty();
    }

    private static Vec3 anchorOffsetOf(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (behavior instanceof GhastFollowBehavior follow) {
                return follow.anchorOffset();
            }
        }
        return Vec3.ZERO;
    }

    /**
     * A copy of {@code harness} with the packed structure stripped — a plain harness again. For the one
     * case where bytes stamped by {@link #packInto} must NOT be honoured: see
     * {@link GhastHarnessListener#onDeath}.
     */
    public static ItemStack unpack(ItemStack harness) {
        ItemStack clean = harness.clone();
        ItemMeta meta = clean.getItemMeta();
        meta.getPersistentDataContainer().remove(ITEM_MARKER);
        meta.getPersistentDataContainer().remove(STRUCTURE);
        meta.getPersistentDataContainer().remove(ANCHOR_X);
        meta.getPersistentDataContainer().remove(ANCHOR_Y);
        meta.getPersistentDataContainer().remove(ANCHOR_Z);
        meta.setDisplayName(null);
        clean.setItemMeta(meta);
        return clean;
    }

    /**
     * Tears a live ghast contraption down WITHOUT restoring any real blocks — for when the structure
     * lives on inside a harness item that {@link #packInto} already stamped and vanilla has handed to
     * the player. Mirrors the non-restoring half of {@link MinecartBearing#pickUpToItem}: despawn every
     * swarm (which also dismounts any seated rider), drop it from the manager, dispose the hidden level,
     * and clear the ghast's bearing tags. The ghast itself survives untouched — only its contraption goes.
     */
    public static void tearDownIntoItem(World bukkitWorld, Entity ghast, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        if (state.level() != null) {
            state.level().dispose();
        }
        untag(ghast);
    }

    /**
     * <b>Restores the structure into the real world</b> — the hammer-driven disassemble, and the safety
     * fallback for every removal path that does NOT carry the structure out on an item (the ghast dying,
     * a {@code /item replace entity} wiping the BODY slot, a third-party plugin clearing it, or the
     * anchor ghast going permanently unresolvable — see
     * {@link GhastFollowBehavior#wantsDisassembleInPlace()}). Puts the blocks back at the contraption's
     * LAST live position rather than leaking a stranded contraption or silently destroying what the
     * player built.
     *
     * <p>Identical teardown to {@link MinecartBearing#disassembleInPlace} and for identical reasons: the
     * {@code fireDisassembleCancelled} veto is deliberately NOT fired (this doubles as an emergency
     * integrity teardown, and must not be vetoable into leaking a dead-anchor contraption), grid-snap +
     * axis-snap-rotation restore at the current position, glue + furniture restore, dispose the hidden
     * level, fire {@code ContraptionDisassembledEvent}. {@code ghast} may be {@code null} (it is already
     * gone in the dead-anchor case).
     */
    public static void disassembleInPlace(World bukkitWorld, Entity ghast, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());

        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        ContraptionCapture.restoreGlue(bukkitWorld.getUID(), state.level(), state.originBearingBlockPos(),
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
        if (ghast != null) {
            untag(ghast);
        }
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
    }
}
