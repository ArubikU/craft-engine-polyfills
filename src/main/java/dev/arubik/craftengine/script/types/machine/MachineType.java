package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.machine.MachineRedstone;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.resources.Identifier;
import org.bukkit.NamespacedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

/**
 * "Machine" type for the PolyFill script system.
 * Instance object: {@link MachineRef} holding NMS level, position, facing, and block entity.
 * All NMS — no Bukkit in the script interface.
 */
public final class MachineType {

    public static final class MachineRef {
        private final ServerLevel level;
        private final BlockPos pos;
        private final String facing;
        private final PersistentBlockEntity blockEntity;
        /** Transient cache — not serialized. Reconnected lazily by UUID. */
        private dev.arubik.craftengine.contraption.core.ContraptionLevel cachedContraption = null;

        public MachineRef(ServerLevel level, BlockPos pos, String facing, PersistentBlockEntity blockEntity) {
            this.level = level;
            this.pos = pos;
            this.facing = facing != null ? facing : "north";
            this.blockEntity = blockEntity;
        }

        public ServerLevel level() { return level; }
        public BlockPos pos() { return pos; }
        public String facing() { return facing; }
        public PersistentBlockEntity blockEntity() { return blockEntity; }

        /**
         * Returns cached contraption, reconnecting either:
         * <ul>
         *   <li>directly from the level — this block IS one carried inside a contraption's own
         *       virtual level (any machine glued into the structure, e.g. a drill riding along),
         *       detected with no stored UUID needed since {@code level} itself already implements
         *       {@link dev.arubik.craftengine.contraption.core.ContraptionLevel} in that case; or</li>
         *   <li>by UUID from typed key "contraption_uuid" (the same "tkey_"+name key
         *       {@code Machine.get_typed/set_typed} use — see {@link MachineType#TYPED_PREFIX}) —
         *       for the BEARING itself, which stays behind in the real world and remembers which
         *       contraption it's driving.</li>
         * </ul>
         * Returns null if not assembled/embedded, or the contraption is no longer alive.
         */
        public dev.arubik.craftengine.contraption.core.ContraptionLevel getContraption() {
            // Validate cache is still alive
            if (cachedContraption != null) {
                try {
                    if (dev.arubik.craftengine.contraption.ContraptionWorlds.entityOf(cachedContraption).isPresent())
                        return cachedContraption;
                } catch (Throwable ignored) {}
                cachedContraption = null;
            }
            // This machine's own block lives inside a contraption's virtual level right now —
            // carried along as part of the structure, not the bearing pivoting it.
            if (level instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel ownLevel) {
                try {
                    if (dev.arubik.craftengine.contraption.ContraptionWorlds.entityOf(ownLevel).isPresent()) {
                        cachedContraption = ownLevel;
                        return ownLevel;
                    }
                } catch (Throwable ignored) {}
            }
            // Try to reconnect by UUID stored in the block entity (the bearing's own case)
            if (blockEntity == null) return null;
            try {
                dev.arubik.craftengine.util.TypedKey<String> key =
                    dev.arubik.craftengine.util.TypedKey.of("polyfills", TYPED_PREFIX + "contraption_uuid", dev.arubik.craftengine.util.NbtType.STRING);
                String uuidStr = blockEntity.get(key);
                if (uuidStr == null || uuidStr.isEmpty()) return null;
                java.util.UUID id = java.util.UUID.fromString(uuidStr);
                dev.arubik.craftengine.contraption.core.ContraptionEntity entity =
                    dev.arubik.craftengine.contraption.core.ContraptionManager.get(id);
                if (entity != null && entity.state().level() instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl) {
                    cachedContraption = cl;
                    return cl;
                }
            } catch (Throwable ignored) {}
            return null;
        }

        /** Invalidate the cache (called when contraption UUID changes). */
        public void invalidateContraptionCache() { cachedContraption = null; }

        public Direction facingDirection() {
            Direction d = Direction.byName(facing);
            return d != null ? d : Direction.NORTH;
        }

        public BlockPos facingBlockPos() {
            return pos.relative(facingDirection());
        }
    }

    private MachineType() {}

    /** Storage-key prefix for every TypedKeyBridge-backed accessor below — one flat namespace
     *  ("polyfills:tkey_" + name) over the block entity's own CE tag, mirroring ItemType's
     *  identical prefix so a value bridged between a machine and an item (Machine.to_item,
     *  ItemDefinition#bridgeTyped) round-trips under the exact same key either side reads. */
    private static final String TYPED_PREFIX = "tkey_";

    @SuppressWarnings("deprecation")
    public static void register() {
        PolyTypeRegistry.define("Machine", "Block")
            // --- Properties ---
            // VectorType.wrap(x,y,z) is exactly ofObj("Vector", new Vector3d(x,y,z)) — the polyType
            // codec re-boxes the same way, so this is the identical value.
            .propertyTyped("pos", TypeCodecs.polyType("Vector", Vector3d.class),
                (MachineRef m) -> new Vector3d(m.pos().getX() + 0.5, m.pos().getY() + 0.5, m.pos().getZ() + 0.5))
            .propertyTyped("x", TypeCodecs.DOUBLE, (MachineRef m) -> (double) m.pos().getX())
            .propertyTyped("y", TypeCodecs.DOUBLE, (MachineRef m) -> (double) m.pos().getY())
            .propertyTyped("z", TypeCodecs.DOUBLE, (MachineRef m) -> (double) m.pos().getZ())
            // Machine.facing and Machine.raw_facing were removed (along with the bare "facing"
            // script global — see ScriptContext.Builder) — two overlapping named properties for
            // "which way is this block facing" (one collapsed/effective, one raw) was exactly the
            // kind of naming collision that caused real bugs this session (Machine.block.get_state
            // ("facing") silently returning something different from Machine.facing). Scripts now
            // read facing through the SAME generic property path used for every other block-state
            // property: Machine.block.property("facing") for the raw stored value (e.g. the saw's
            // own horizontal 4-direction, even when floor/ceiling-mounted), and, for a button-style
            // face+facing block that needs the EFFECTIVE direction (floor/ceiling collapsed to up/
            // down), Machine.block.property("face") combined with the raw "facing" explicitly in
            // script — see saw.pf for the pattern.
            // The machine's OWN raw inventory — the exact same Container backing its normal menu
            // UI(s) — wrapped generically so a script can read/write/push/pull it with the same
            // uniform surface used for Contraption.container, an arbitrary chest, etc. (see
            // ContainerType). Slot indices here are ABSOLUTE container indices, same convention
            // Machine.get_item/set_item/push_item_to_inventory already use.
            //
            // Goes through ItemTransferHelper.getContainer (level+pos), NOT a raw
            // blockEntity() instanceof Container check — a "machine" here is any CraftEngine custom
            // block, and not every one of those is backed by a PersistentWorldlyBlockEntity
            // implementing Container directly; some instead expose a WorldlyContainerHolder
            // capability on their BlockBehavior (the same hopper-compat hook funnels/pipes already
            // resolve through). Using the shared helper is what makes this property correct for
            // BOTH shapes instead of only the common one.
            .propertyTyped("container",
                TypeCodecs.polyType("Container", net.minecraft.world.Container.class),
                (MachineRef m) -> dev.arubik.craftengine.pipe.item.ItemTransferHelper
                        .getContainer(m.level(), m.pos()).orElse(null))
            // The world tick something last actually moved an item into/out of this machine's
            // exposed container (see AbstractMachineBlockEntity.TransferTrackingContainer) — -1 if
            // never. Only ever gets stamped for a machine using on_get_container; a plain machine
            // with no such hook stays -1 forever (nothing wraps its container that way). Meant to be
            // read cross-machine via Block.machine — e.g. a contraption-mounted Portable Storage
            // Interface checking a STATIONARY partner's value to decide whether to keep holding the
            // contraption still while a transfer might still be in flight.
            .propertyTyped("last_transfer_tick", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (be instanceof AbstractMachineBlockEntity machine) return (double) machine.getLastTransferTick();
                return -1.0;
            })
            // The old Machine.set_state was a near-duplicate of Block.set_property that additionally
            // fired on_property_change (itself renamed from on_state_change) — removed; that hook
            // now fires from Block.set_property itself whenever the position's block entity happens
            // to be a machine with one configured, so Machine.set_property(...) still works exactly
            // the same way via inheritance from Block (see Block.ref()'s MachineRef handling)
            // without a second implementation to keep in sync.
            // Per-instance RPM face overrides, meant to be called from on_place/on_load to
            // (re)compute which faces this machine's own current state should accept/emit
            // rotational power from — mirrors Machine.io's allow_input/allow_output for item/
            // fluid/gas, but for rotational power. csv is a comma-joined list of direction names
            // (absolute "north".."down" or facing-relative "front"/"back"/"left"/"right", plus
            // "axis_pos"/"axis_neg"/"axis_perp" for shaft/gear-style blocks) — same vocabulary the
            // static io block's rpm entries use. Once ANY set_rpm_output_* call has been made, all
            // four output sets (same/same_inverted/new-network/new-network-inverted) override the
            // static declaration together — see DataMachineBlockEntity#currentRpmOutputSets.
            //   set_rpm_output_same          — relay: stays on the SAME kinetic/stress network
            //   set_rpm_output_same_inverted — relay, sign flipped
            //   set_rpm_output_new_network   — NEW-NETWORK boundary (a fresh network starts here)
            //   set_rpm_output_inverted      — new-network boundary, sign flipped
            // set_rpm_output_new_network was originally just "set_rpm_output" — collided with an
            // entirely unrelated LATER method of the exact same name (an actual RPM VALUE setter for
            // source motors, see the "--- Methods: RPM output / relay ---" section below). Since
            // PolyType.method() is a plain Map.put, the later registration silently won and this one
            // was dead/unreachable — every real .pf caller of "set_rpm_output" was already passing a
            // number (the value setter), never the face CSV this one expects, so renaming it is a
            // pure bug fix, not a behavior change for any shipped script.
            .methodTyped1("set_rpm_input", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String csv) -> {
                    if (m.blockEntity() instanceof DataMachineBlockEntity machine) {
                        machine.setRpmInputOverride(csv);
                        return true;
                    }
                    return false;
                })
            .methodTyped1("set_rpm_output_same", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String csv) -> {
                    if (m.blockEntity() instanceof DataMachineBlockEntity machine) {
                        machine.setRpmOutputSameOverride(csv);
                        return true;
                    }
                    return false;
                })
            .methodTyped1("set_rpm_output_same_inverted", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String csv) -> {
                    if (m.blockEntity() instanceof DataMachineBlockEntity machine) {
                        machine.setRpmOutputSameInvertedOverride(csv);
                        return true;
                    }
                    return false;
                })
            .methodTyped1("set_rpm_output_new_network", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String csv) -> {
                    if (m.blockEntity() instanceof DataMachineBlockEntity machine) {
                        machine.setRpmOutputNewNetworkOverride(csv);
                        return true;
                    }
                    return false;
                })
            .methodTyped1("set_rpm_output_inverted", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String csv) -> {
                    if (m.blockEntity() instanceof DataMachineBlockEntity machine) {
                        machine.setRpmOutputInvertedOverride(csv);
                        return true;
                    }
                    return false;
                })
            .methodTyped0("clear_rpm_override", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    if (m.blockEntity() instanceof DataMachineBlockEntity machine) {
                        machine.clearRpmOverride();
                        return true;
                    }
                    return false;
                })
            // turn_page(delta) — multipage chest page switch (stash+show items)
            .methodTyped1("turn_page", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double deltaArg) -> {
                    int delta = deltaArg.intValue();
                    if (m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMultiBlockMachineBlockEntity dmb) {
                        dmb.turnPage(delta);
                        return true;
                    }
                    return false;
                })
            // --- Page navigation (browser-like history) ---
            // Machine.page(n) — navigate to page n
            .methodTyped1("page", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double nArg) -> {
                    int n = nArg.intValue();
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                    // Use active player if available — for script-triggered navigation we store per-machine
                    try {
                        // Find viewer via active MachineMenu
                        org.bukkit.entity.Player p = null;
                        if (dm.getMenu() != null) {
                            for (org.bukkit.entity.HumanEntity he : dm.getMenu().getInventory().getViewers()) {
                                if (he instanceof org.bukkit.entity.Player bp) { p = bp; break; }
                            }
                        }
                        if (p == null) return false;
                        dm.openPage(p, n);
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            // Machine.next_page() — go to currentPage+1
            .methodTyped0("next_page", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                    try {
                        // Find viewer via active MachineMenu
                        org.bukkit.entity.Player p = null;
                        if (dm.getMenu() != null) {
                            for (org.bukkit.entity.HumanEntity he : dm.getMenu().getInventory().getViewers()) {
                                if (he instanceof org.bukkit.entity.Player bp) { p = bp; break; }
                            }
                        }
                        if (p == null) return false;
                        dm.openPage(p, Math.min(dm.currentPage() + 1, dm.pageCount() - 1));
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            // Machine.prev_page() — browser back (history)
            .methodTyped0("prev_page", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                    try {
                        // Find viewer via active MachineMenu
                        org.bukkit.entity.Player p = null;
                        if (dm.getMenu() != null) {
                            for (org.bukkit.entity.HumanEntity he : dm.getMenu().getInventory().getViewers()) {
                                if (he instanceof org.bukkit.entity.Player bp) { p = bp; break; }
                            }
                        }
                        if (p == null) return false;
                        dm.openPrevPage(p);
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            .propertyTyped("current_page", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return (double) dm.currentPage();
            })
            .propertyTyped("page_count", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return (double) dm.pageCount();
            })
            // Multi-cell structure (an auto-placing shape declared on the BLOCK's own "cells"
            // config, see MultiCellGeometry#parseCells — NOT a MachineDefinition concept, so this
            // just reflects whatever the block behavior actually set on this core). A script always
            // runs against the CORE (parts forward interaction/scripts to it), so there's no
            // separate "am I a part" accessor needed here.
            .propertyTyped("is_multi_cell", TypeCodecs.BOOL, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                return dm.cellCount() > 0;
            })
            .propertyTyped("cell_count", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return (double) dm.cellCount();
            })
            // ticks_alive: how many server ticks this block has been loaded (persisted via NBT)
            .propertyTyped("ticks_alive", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)
                    return (double) dm.ticksAlive();
                return 0.0;
            })
            // rpm_network: in-memory Long network id (0 = not in network)
            .propertyTyped("rpm_network", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return (double) dm.rpmNetworkId();
            })
            // source_distance: 0 = primary source, MAX_INT = not connected
            .propertyTyped("source_distance", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)
                    return (double) dm.sourceDistance();
                return (double) Integer.MAX_VALUE;
            })
            // Shorthands for common block state properties used in renderer "when" expressions
            // STRING encodes a Java null back to NULL, so the null branch is unchanged.
            .propertyTyped("axis", TypeCodecs.STRING,
                (MachineRef m) -> BlockType.readProperty(m.level().getBlockState(m.pos()), "axis"))
            .propertyTyped("activated", TypeCodecs.BOOL,
                (MachineRef m) -> "true".equals(BlockType.readProperty(m.level().getBlockState(m.pos()), "activated")))
            .propertyTyped("facing_block", TypeCodecs.polyType("Block", BlockType.BlockRef.class),
                (MachineRef m) -> {
                    BlockPos target = m.facingBlockPos();
                    return m.level() == null ? null : new BlockType.BlockRef(m.level(), target);
                })
            // Machine.block — this machine's OWN position as a Block wrapper. Lets a script read a
            // RAW blockstate property directly (Machine.block.get_state("facing")) when it needs
            // the actual stored value rather than the derived Machine.facing — e.g. a button-style
            // face-split block (see AbstractMachineBlockEntity#getFacing) collapses face=floor/
            // ceiling down to plain up/down, discarding exactly the stored horizontal "facing" a
            // shaft renderer needs to know which axis to protrude along.
            .propertyTyped("block", TypeCodecs.polyType("Block", BlockType.BlockRef.class),
                (MachineRef m) -> m.level() == null || m.pos() == null ? null : new BlockType.BlockRef(m.level(), m.pos()))


            // --- Methods: Entity interaction ---
            // Return stays RAW: EntityType.wrap picks one of NINE PolyType names per element
            // ("Player"/"Animal"/"Mob"/"LivingEntity"/"ItemEntity"/... ) — not one element type.
            .methodTypedOpt1("nearby_entities", TypeCodecs.DOUBLE, 5.0, TypeCodecs.RAW,
                (MachineRef m, Double radius) -> {
                    Vec3 center = Vec3.atCenterOf(m.pos());
                    AABB box = AABB.ofSize(center, radius * 2, radius * 2, radius * 2);
                    List<Entity> entities = m.level().getEntities((Entity) null, box, e -> true);
                    List<ScriptValue> result = new ArrayList<>(entities.size());
                    for (Entity e : entities) {
                        result.add(EntityType.wrap(e));
                    }
                    return new ScriptValue.Array(result);
                })
            // kill_entity / push_entity kept as convenience aliases for Machine context
            // (entity.kill() and entity.push(vec) are the canonical API)
            // use_item_on_entity(entity, slot) — feeds/interacts with entity using item from slot.
            // arg0 stays TypeCodecs.RAW: it's an Entity script object, not a native scalar.
            .methodTyped2("use_item_on_entity", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue eArg, Double slotArg) -> {
                    int slot = slotArg.intValue();
                    if (!(eArg instanceof ScriptValue.Obj eo) || !(eo.instance() instanceof LivingEntity target))
                        return false;
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return false;
                    ItemStack stack = worldly.getItem(slot);
                    if (stack.isEmpty()) return false;
                    // Interact: feed animal or generic mob interact
                    if (target instanceof net.minecraft.world.entity.animal.Animal animal) {
                        animal.setInLoveTime(600);
                        stack.shrink(1);
                        worldly.setItem(slot, stack);
                        return true;
                    }
                    return false;
                })
            // spawn_entity → use World.spawn_entity(type, x, y, z) instead

            // --- Methods: Inventory ---
            // Slot-level reads/writes (get_item_in_slot, remove_item_in_slot, add_item, add_item_to_slot,
            // push_item_to_inventory, push_item_toward) used to be duplicated here on Machine, hand-rolling
            // the exact same merge-then-fill logic ContainerType.push already implements generically —
            // that's what Machine.container (this machine's own inventory) and Machine.container_at(dx,dy,dz)
            // (a neighbor's) already expose. Removed in favor of Machine.container.get_item/set_item/
            // remove_item/push and Machine.container_at(...).push(...) — see ContainerType.
            // Generic "this machine becomes that item" primitive — builds a fresh instance of the
            // named ItemDefinition (items/*.json), pre-filled with THIS machine's own container
            // contents, each page's items read from the SAME absolute container offset that page
            // occupies (see dev.arubik.craftengine.item.ItemStateData#buildFromContainer) so page 2's
            // items land back on page 2 instead of being compacted onto page 1 whenever page 1
            // wasn't completely full. ALSO copies fluid/gas tank amounts by name, and energy, into
            // any tank the target ItemDefinition declares with a matching kind. Used by e.g. a
            // placeable-container item's on_break script to hand itself back with contents intact —
            // see scripts/backpack_storage.pf — but generic over any machine/item pair, not
            // backpack-specific.
            // to_item(item_id[, include_storage[, include_typed]]) — the 2 optional trailing bools
            // mirror place_block's fill_storage/fill_typed on the OTHER direction (item→machine),
            // both defaulting to true so a bare to_item("id") keeps its old full-copy behavior; pass
            // false to leave that part of the built item untouched (e.g. a "peek" item with no live
            // storage).
            // Typed with a null sentinel on arg0: no codec can decode a PRESENT argument to Java
            // null (asStr() is total), so `itemId == null` is exactly the old `args.isEmpty()`
            // early return, checked before any side effect. args 1-2 keep their default-true.
            .methodTypedOpt3("to_item", TypeCodecs.STRING, null, TypeCodecs.BOOL, true,
                TypeCodecs.BOOL, true, TypeCodecs.RAW,
                (MachineRef m, String itemId, Boolean includeStorage, Boolean includeTyped) -> {
                if (itemId == null) return ScriptValue.NULL;
                dev.arubik.craftengine.item.ItemDefinition itemDef =
                        dev.arubik.craftengine.item.ItemDefinition.byId(
                                net.momirealms.craftengine.core.util.Key.of(itemId));
                if (itemDef == null) return ScriptValue.NULL;
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.NULL;
                ItemStack built = includeStorage
                        ? dev.arubik.craftengine.item.ItemStateData.buildFromContainer(
                                itemDef, worldly::getItem, worldly.getContainerSize())
                        : dev.arubik.craftengine.item.ItemStateData.buildFromContainer(itemDef, i -> ItemStack.EMPTY, 0);
                if (be instanceof AbstractMachineBlockEntity machine) {
                    if (includeStorage) {
                        for (dev.arubik.craftengine.item.ItemDefinition.TankSpec spec : itemDef.tanks()) {
                            try {
                                if (spec.isFluid()) {
                                    for (dev.arubik.craftengine.fluid.FluidTank t : machine.fluidTankList()) {
                                        if (!t.getName().equals(spec.name())) continue;
                                        int amount = t.getFluid(m.level(), m.pos()).getAmount();
                                        built = dev.arubik.craftengine.item.ItemStateData.setTankAmount(
                                                built, spec.name(), amount, spec.capacity());
                                    }
                                } else if (spec.isGas()) {
                                    for (dev.arubik.craftengine.gas.GasTank t : machine.gasTankList()) {
                                        if (!t.getName().equals(spec.name())) continue;
                                        int amount = t.getGas(m.level(), m.pos()).getAmount();
                                        built = dev.arubik.craftengine.item.ItemStateData.setTankAmount(
                                                built, spec.name(), amount, spec.capacity());
                                    }
                                } else if (spec.isEnergy()) {
                                    built = dev.arubik.craftengine.item.ItemStateData.setTankAmount(
                                            built, spec.name(), machine.getStoredEnergyForCarrier(), spec.capacity());
                                }
                            } catch (Throwable ignored) {}
                        }
                    }
                    // Bridge explicitly-declared TypedKey entries (see ItemDefinition#bridgeTyped) —
                    // same "tkey_"+name convention Item.get_typed/with_typed and Machine.get_typed/
                    // set_typed both already use, so nothing new to invent on either side.
                    if (includeTyped) {
                        for (dev.arubik.craftengine.item.ItemDefinition.TypedBridgeSpec spec : itemDef.bridgeTyped()) {
                            try {
                                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec =
                                        dev.arubik.craftengine.script.TypedKeyBridge.resolve(spec.type());
                                if (codec == null) continue;
                                Object v = machine.get(dev.arubik.craftengine.util.TypedKey.of(
                                        "polyfills", "tkey_" + spec.name(), codec.storage()));
                                if (v != null) built = dev.arubik.craftengine.script.types.primitive.ItemType
                                        .writeTypedRaw(built, spec.name(), codec.storage(), v);
                            } catch (Throwable ignored) {}
                        }
                    }
                }
                return ScriptValue.ofItem(built);
            })
            // Spawns a real dropped-item entity at this machine's position — the twin of to_item,
            // used to replace a machine's break-drop with a single reconstructed item instead of
            // its raw contents (see MachineBreakListener, which skips the default drop-everything
            // behavior whenever a machine declares on_break, leaving the script fully responsible).
            // Works for every removal cause (player break, explosion, ...) since it only needs a
            // position, not a player.
            // drop_item(item_or_list, px?, py?, pz?) — item_or_list is either a single item (as
            // before) or an Array of items, dropping each as its own entity in one call. Optional
            // trailing (px,py,pz) — same block-relative, +0.5-centered convention as drop_item_at —
            // spawns away from this machine's own tile center; omitted, it's the original behavior
            // (spawn AT the machine's own center).
            // Left untyped: (px,py,pz) is an ALL-OR-NOTHING positional group — only honoured when
            // args.size() >= 4, so a 2-arg call must ignore arg1 rather than read it as px. Per-arg
            // defaults (methodTypedOptN) cannot express a group gate on the total arg count.
            .method("drop_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                java.util.List<ItemStack> toDrop = extractStacks(args.get(0));
                if (toDrop.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                double px = args.size() >= 4 ? args.get(1).asNum() : 0.0;
                double py = args.size() >= 4 ? args.get(2).asNum() : 0.0;
                double pz = args.size() >= 4 ? args.get(3).asNum() : 0.0;
                for (ItemStack stack : toDrop) {
                    ItemEntity spawned = new ItemEntity(m.level(), m.pos().getX() + px + 0.5,
                            m.pos().getY() + py + 0.5, m.pos().getZ() + pz + 0.5, stack);
                    m.level().addFreshEntity(spawned);
                }
                return ScriptValue.of(true);
            })
            // drop_item_toward(item_or_list, dx, dy, dz, px?, py?, pz?) — same as drop_item but
            // tosses the entity(ies) with an initial velocity (dx,dy,dz), for machines whose
            // ejection direction depends on their own facing (e.g. the saw's ground-pickup output:
            // north ejects the opposite way from south). item_or_list is either a single item or an
            // Array of items — every one gets the SAME velocity/position (they'll naturally spread
            // apart from collision once spawned). Optional trailing (px,py,pz) spawns at that
            // block-relative offset (same +0.5-centered convention as drop_item_at) instead of this
            // machine's own tile center — e.g. a funnel wants to drop from the exact point it was
            // facing out of, not from the middle of its own block. Backward compatible: existing
            // 4-arg single-item callers (saw.pf) keep spawning at the machine's own center exactly
            // as before.
            // Left untyped: same all-or-nothing (px,py,pz) positional group as drop_item, gated on
            // args.size() >= 7 — not expressible with per-argument defaults.
            .method("drop_item_toward", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                java.util.List<ItemStack> toDrop = extractStacks(args.get(0));
                if (toDrop.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                double dx = args.get(1).asNum(), dy = args.get(2).asNum(), dz = args.get(3).asNum();
                double px = args.size() >= 7 ? args.get(4).asNum() : 0.0;
                double py = args.size() >= 7 ? args.get(5).asNum() : 0.0;
                double pz = args.size() >= 7 ? args.get(6).asNum() : 0.0;
                for (ItemStack stack : toDrop) {
                    ItemEntity spawned = new ItemEntity(m.level(), m.pos().getX() + px + 0.5,
                            m.pos().getY() + py + 0.5, m.pos().getZ() + pz + 0.5, stack);
                    spawned.setDeltaMovement(dx, dy, dz);
                    m.level().addFreshEntity(spawned);
                }
                return ScriptValue.of(true);
            })
            // drop_item_at(item, dx, dy, dz) — like drop_item, but at an OFFSET position (block
            // coordinates relative to this machine, +0.5 centered) instead of this machine's own
            // tile — for a machine ejecting into open air one or more blocks away (a chute with
            // nothing below to catch its output). Builds the ItemEntity with its stack already set
            // in the constructor, same as drop_item/drop_item_toward — unlike a
            // World.spawn_entity("item", ...) + a separate set_item() call after, which creates the
            // entity EMPTY first; vanilla ItemEntity discards itself if it ticks while empty, and
            // depending on exactly when that first tick lands relative to the second script call,
            // the item can vanish before set_item ever reaches it.
            .methodTyped4("drop_item_at", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue arg, Double dx, Double dy, Double dz) -> {
                    ItemStack toDrop;
                    if (arg instanceof ScriptValue.Item i) toDrop = i.stack().copy();
                    else if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) toDrop = is.copy();
                    else return false;
                    if (toDrop.isEmpty()) return false;
                    ItemEntity spawned = new ItemEntity(m.level(), m.pos().getX() + dx + 0.5,
                            m.pos().getY() + dy + 0.5, m.pos().getZ() + dz + 0.5, toDrop);
                    m.level().addFreshEntity(spawned);
                    return true;
                })
            // open_menu(Player) — opens this machine's normal menu. A machine whose interact_script
            // fully replaces on-right-click handling (see MachineBlockBehavior#useWithoutItem — once
            // an interact_script is declared, the menu no longer opens on its own) calls this itself
            // for the branch where it still wants the ordinary UI, e.g. a storage_block whose
            // interact_script also handles a shift-right-click "pick myself back up as an item"
            // gesture (see Machine.pickup_as_item) needs an explicit way to fall through to its menu.
            //
            // For a DataMachineBlockEntity this ALWAYS builds a fresh MachineLayout (same as
            // DataMachineBlockEntity#openPage) instead of reusing whatever MachineMenu instance is
            // already cached in `active` — a plain getMenu().open(...) would silently reopen the
            // SAME stale layout, which is exactly why a "buttons": "file.pf:func" generator (e.g.
            // specialized_teleporter.pf#generate_buttons, whose destination names are baked strings
            // computed once when the layout is built, not live ${expr} refs re-evaluated per tick)
            // never picked up a changed sign/alias on a plain right-click reopen: MachineBlockBehavior
            // only reopens fresh when an on_right_click script explicitly calls this, so a machine
            // that wants its generator to reflect current state on every open declares
            // "on_right_click": "....pf:on_right_click" with a body that just calls
            // Machine.open_menu(Player).
            .methodTyped1("open_menu", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue playerArg) -> {
                    if (!(playerArg instanceof ScriptValue.Obj po)
                            || !(po.instance() instanceof net.minecraft.world.entity.player.Player p))
                        return false;
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity amb)) return false;
                    if (!(p.getBukkitEntity() instanceof org.bukkit.entity.Player bukkitPlayer)) return false;
                    try {
                        if (amb instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm) {
                            dm.openPage(bukkitPlayer, dm.currentPage());
                        } else {
                            amb.getMenu().open(bukkitPlayer);
                        }
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            // add_items(array) — batch convenience over this machine's OWN container: pushes every
            // item in `array` via the same merge-then-fill Container.push already implements
            // generically (see ContainerType) — a loop of Machine.container.push(...) calls, not a
            // separate implementation.
            .methodTyped1("add_items", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue arrVal) -> {
                    if (!(arrVal instanceof ScriptValue.Array arr)) return false;
                    var containerOpt = dev.arubik.craftengine.pipe.item.ItemTransferHelper.getContainer(m.level(), m.pos());
                    if (containerOpt.isEmpty()) return false;
                    net.minecraft.world.Container container = containerOpt.get();
                    for (ScriptValue elem : arr.elements()) {
                        ItemStack stack;
                        if (elem instanceof ScriptValue.Item i) stack = i.stack().copy();
                        else if (elem instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) stack = is.copy();
                        else continue;
                        dev.arubik.craftengine.script.types.util.ContainerType.push(container, stack);
                    }
                    return true;
                })

            // --- Methods: Redstone ---
            // Shorthand for Machine.redstone.set(n). Kept because 17 shipped scripts use it.
            // Delegates to MachineRedstone so writer and reader can never disagree on the key.
            .methodTyped1("emit_redstone", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double level) -> MachineRedstone.setOutput(
                        m.level(), m.pos(), m.blockEntity(), level.intValue()))

            // --- Methods: generic per-face IOConfiguration read/write ---
            // These are the one true primitive for "does this face pass X" / "make this face pass X" —
            // deliberately NOT specialized per feature. A pipe panel (or any future block wanting the
            // same knob) composes them: e.g. a face's displayed mode is (io_get("item","input",dir),
            // io_get("item","output",dir)) computed IN THE SCRIPT, not as a Java-side enum. Works for
            // any IOType by name — item/fluid/gas/energy/xp/redstone/funnel/visual_connection — so the
            // exact same two methods drive an item pipe's transfer mask, a "needs redstone" toggle
            // (reusing IOType.REDSTONE), and the visual connected-face mask (IOType.VISUAL_CONNECTION,
            // consulted by ConnectedBlockBehavior#shouldConnect) uniformly, for whichever resource the
            // owning pipe/machine actually carries.
            // io_get(type, mode, dir) -> bool. mode is "input" or "output".
            .methodTyped3("io_get", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String typeName, String mode, String dirName) -> {
                    dev.arubik.craftengine.multiblock.IOConfiguration.IOType type = parseIoType(typeName);
                    Direction dir = Direction.byName(dirName);
                    if (type == null || dir == null) return false;
                    dev.arubik.craftengine.multiblock.IOConfiguration cfg = resolveLiveIOConfig(m);
                    if (cfg == null) return false;
                    boolean input = "input".equalsIgnoreCase(mode);
                    return input ? cfg.acceptsInput(type, dir) : cfg.providesOutput(type, dir);
                })
            // io_set(type, mode, dir, bool) -> bool (whether it applied)
            .methodTyped4("io_set", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL,
                TypeCodecs.BOOL, false,
                (MachineRef m, String typeName, String mode, String dirName, Boolean on) -> {
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return false;
                    dev.arubik.craftengine.multiblock.IOConfiguration.IOType type = parseIoType(typeName);
                    Direction dir = Direction.byName(dirName);
                    if (type == null || dir == null) return false;
                    dev.arubik.craftengine.multiblock.IOConfiguration.Simple cfg = liveSimpleConfig(machine);
                    boolean input = "input".equalsIgnoreCase(mode);
                    if (input) {
                        if (on) cfg.addInput(type, dir); else cfg.removeInput(type, dir);
                    } else {
                        if (on) cfg.addOutput(type, dir); else cfg.removeOutput(type, dir);
                    }
                    // The mutation above is pure data — nothing re-derives the connected-face mask on
                    // its own. Recompute now (self + the touched neighbour) so a panel toggle shows up
                    // immediately instead of waiting for an unrelated block update.
                    recomputeMaskIfConnected(m);
                    return true;
                })
            // Manual trigger for the same recompute io_set already does automatically — useful after
            // a batch of io_set calls, or after something else (a script, an upgrade) changed this
            // block's IOConfiguration through a path that doesn't already call it.
            .methodTyped0("recompute_io", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    recomputeMaskIfConnected(m);
                    return true;
                })

            // get_renderer(id) -> MegRenderer / BetterModelRenderer / NULL. Resolves a renderer
            // entry declared with "id": "<id>" in this machine's own renderer config (see
            // RendererSpec#id / RendererManager#indexOfId) so a script can drive it directly — play
            // animations, set/pseudo-IK bones, tint — instead of only the declarative when/location
            // expressions the renderer config itself supports. NULL if this machine has no renderer
            // config, or no entry with that id, or the entry with that id isn't currently backed by
            // a live BetterModel/ModelEngine instance (e.g. the "when" condition is currently false).
            .methodTyped1("get_renderer", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (MachineRef m, String id) -> {
                    dev.arubik.craftengine.machine.render.RendererManager rm = rendererManagerOf(m);
                    if (rm == null) return ScriptValue.NULL;
                    dev.arubik.craftengine.machine.render.renderer.BetterModelRenderer bm = rm.betterModelRendererById(id);
                    if (bm != null) return dev.arubik.craftengine.script.types.machine.renderer.BetterModelRendererType.wrap(bm);
                    dev.arubik.craftengine.machine.render.renderer.MegRenderer me = rm.modelEngineRendererById(id);
                    if (me != null) return dev.arubik.craftengine.script.types.machine.renderer.MegRendererType.wrap(me);
                    return ScriptValue.NULL;
                })

            // Bridges a panel-driven pipe segment into its resource network engine. Called every
            // tick (cheap: a single map put) from the panel machine's own action_script — the
            // existing periodic-hook mechanism (see machines/item_pipe_panel.json's action_script),
            // not a bespoke ticker. No-ops on any block that isn't an item pipe.
            .methodTyped0("pipe_register_network", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    try {
                        net.minecraft.world.level.block.state.BlockState state = m.level().getBlockState(m.pos());
                        var custom = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                                .getOptionalCustomBlockState(state);
                        if (custom.isEmpty()) return false;
                        var beh = custom.get().behavior();
                        dev.arubik.craftengine.pipe.item.ItemPipeBehavior pipe = beh instanceof dev.arubik.craftengine.pipe.item.ItemPipeBehavior p
                                ? p
                                : beh.getFirst(dev.arubik.craftengine.pipe.item.ItemPipeBehavior.class);
                        if (pipe == null) return false;
                        dev.arubik.craftengine.pipe.item.ItemEngine.registerSeed(m.pos(), pipe.transferPerTick());
                        return true;
                    } catch (Throwable ignored) {
                        return false;
                    }
                })

            // --- Methods: Block breaking ---
            // tick_break(block, speed) → Array of Item drops when fully broken, else NULL
            // Progressive break: stores progress in a flag, returns drops when complete.
            //
            // Reads/destroys in the TARGET block's own level (blockRef.level()), not the
            // calling machine's level (m.level()) — those differ the moment a contraption-borne
            // drill targets a real-world block via ContraptionWorld.real_block(): the drill's
            // own block entity lives in the contraption's virtual level, but the block it's
            // cutting into is real. Using m.level() here would silently read/destroy air at the
            // target's raw coordinates in the WRONG level instead. For a standalone (non-
            // contraption) drill the two levels are the same object anyway, so this changes
            // nothing for the common case.
            // Return stays RAW: it is either NULL (still breaking) or an Array of ScriptValue.Item
            // drops — Items are not Obj-wrapped PolyType instances, so listOf cannot express it.
            .methodTyped2("tick_break", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (MachineRef m, ScriptValue blockArg, Double speedArg) -> {
                    int speed = speedArg.intValue();
                    if (!(blockArg instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef blockRef))
                        return ScriptValue.NULL;
                    ServerLevel targetLevel = blockRef.level();
                    BlockPos target = blockRef.pos();
                    net.minecraft.world.level.block.state.BlockState bs = targetLevel.getBlockState(target);
                    if (bs.isAir()) return ScriptValue.NULL;
                    float hardness = bs.getDestroySpeed(targetLevel, target);
                    if (hardness < 0) return ScriptValue.NULL; // unbreakable
                    String flagName = "_break_" + target.getX() + "_" + target.getY() + "_" + target.getZ();
                    TypedKey<Integer> progressKey = TypedKey.of("polyfills", "flag" + flagName, NbtType.INTEGER);
                    PersistentBlockEntity be = m.blockEntity();
                    int progress = be != null ? (be.get(progressKey) != null ? be.get(progressKey) : 0) : 0;
                    int required = Math.max(1, (int)(hardness * 10));
                    progress += speed;
                    if (progress >= required) {
                        // Break and drop
                        List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(bs, targetLevel, target, targetLevel.getBlockEntity(target));
                        targetLevel.destroyBlock(target, false);
                        BlockType.spawnExpDrop(targetLevel, target, bs);
                        if (be != null) be.set(progressKey, 0);
                        List<ScriptValue> result = new ArrayList<>(drops.size());
                        for (ItemStack drop : drops) {
                            result.add(ScriptValue.ofItem(drop));
                        }
                        return new ScriptValue.Array(result);
                    } else {
                        if (be != null) be.set(progressKey, progress);
                        // Send break animation — in the target's own level, so real-world viewers
                        // actually see it (the machine's own level, if it's a contraption's virtual
                        // one, has no real players in it to broadcast to).
                        int stage = (int)((float)progress / required * 9);
                        targetLevel.destroyBlockProgress(m.pos().hashCode(), target, stage);
                        return ScriptValue.NULL;
                    }
                })

            // --- Methods: Contraption stall ---
            // Machine.contraption — lazy-cached contraption reference. Reconnects by UUID on restart.
            // ContraptionType.wrap only ever produces an Obj("Contraption", cl) for a ContraptionLevel
            // — which getContraption() already returns — so the polyType codec re-boxes identically.
            .propertyTyped("contraption",
                TypeCodecs.polyType("Contraption", dev.arubik.craftengine.contraption.core.ContraptionLevel.class),
                (MachineRef m) -> m.getContraption())
            // Machine.has_contraption — true if contraption is live
            .propertyTyped("has_contraption", TypeCodecs.BOOL, (MachineRef m) -> m.getContraption() != null)
            // Machine.bars — Bars type to read bar stats (fluid, progress, etc.)
            .propertyTyped("bars", TypeCodecs.polyType("Bars", AbstractMachineBlockEntity.class),
                (MachineRef m) -> m.blockEntity() instanceof AbstractMachineBlockEntity be ? be : null)
            // Machine.recipes — Array of Recipe for this machine type. Every element comes from
            // RecipeManager.getRecipes, a List<AbstractProcessingRecipe> — one uniform element type
            // (never the VanillaRecipeRef half of the "Recipe" PolyType) — so the list codec's own
            // ofObj("Recipe", element) wrapping is exactly what RecipeType.wrap did per element.
            .propertyTyped("recipes",
                TypeCodecs.listOf("Recipe", dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe.class),
                (MachineRef m) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm))
                        return java.util.List.of();
                    return dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getRecipes(dm.getMachineIdPublic());
                })
            // Machine.working_recipe — current processing recipe or NULL
            .propertyTyped("working_recipe",
                TypeCodecs.polyType("Recipe", dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe.class),
                (MachineRef m) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return null;
                    try { return dm.getCurrentRecipe(); } catch (Throwable ignored) { return null; }
                })
            // Machine.matching_recipe — first recipe matching current inventory or NULL
            .propertyTyped("matching_recipe",
                TypeCodecs.polyType("Recipe", dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe.class),
                (MachineRef m) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return null;
                    try { return dm.findMatchingRecipe(); } catch (Throwable ignored) { return null; }
                })
            // Machine.connect_contraption() — force reconnect from str_flag UUID, returns contraption or NULL
            .methodTyped0("connect_contraption", TypeCodecs.RAW,
                (MachineRef m) -> {
                    m.invalidateContraptionCache();
                    var cl = m.getContraption();
                    return cl != null ? dev.arubik.craftengine.script.types.world.ContraptionType.wrap(cl) : ScriptValue.NULL;
                })
            .methodTyped0("hold_contraption", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    // Signals the contraption system to hold this machine still
                    PersistentBlockEntity be = m.blockEntity();
                    if (be == null) return false;
                    TypedKey<Integer> key = TypedKey.of("polyfills", "stall_contraption", NbtType.INTEGER);
                    be.set(key, 1);
                    return true;
                })
            .methodTyped0("release_contraption", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (be == null) return false;
                    TypedKey<Integer> key = TypedKey.of("polyfills", "stall_contraption", NbtType.INTEGER);
                    be.set(key, 0);
                    return true;
                })

            // --- Methods: RPM ---
            // Migrated to the typed-registration API (see get_typed/set_typed above).
            .methodTyped1("report_su", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double suArg) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity machine)) return false;
                    float su = suArg.floatValue();
                    // lastSuLoad is private — access via reportSuToNetwork which updates it internally
                    // Report to kinetic network (positive = consume, negative = generate)
                    machine.reportSuToNetwork(su);
                    return true;
                })
            // progress/max_progress/progress_percent — the recipe processing bar's own raw ticks
            // (matches the "polyfills:progress" bar's barStat("progress") source exactly), exposed
            // to scripts so a layout item (e.g. recipe_info.pf) can show a live "% done" without
            // needing its own separate progress-tracking convention.
            .propertyTyped("progress", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.getProgress();
            })
            .propertyTyped("max_progress", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) Math.max(1, machine.getMaxProgress());
            })
            .propertyTyped("progress_percent", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return 100.0 * machine.getProgress() / Math.max(1, machine.getMaxProgress());
            })
            .propertyTyped("is_overstressed", TypeCodecs.BOOL, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(dm.rpmNetworkId());
                return net != null && net.isOverStressed();
            })
            .propertyTyped("network_capacity", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(dm.rpmNetworkId());
                return net != null ? (double) net.totalCapacity() : 0.0;
            })
            .propertyTyped("network_stress", TypeCodecs.DOUBLE, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(dm.rpmNetworkId());
                return net != null ? (double) net.totalStress() : 0.0;
            })

            // --- Properties: redstone port ---
            // Returns a Redstone object (.input/.output/.powered/.set(n)/.on()/.off()).
            // It coerces numerically to the INPUT level, so the older `Machine.redstone > 0`
            // and `power = Machine.redstone` usages keep working unchanged.
            .propertyTyped("redstone", TypeCodecs.polyType("Redstone", RedstoneType.RedstoneRef.class),
                (MachineRef m) -> m == null ? null : new RedstoneType.RedstoneRef(m))
            .propertyTyped("facing_dx", TypeCodecs.DOUBLE, (MachineRef m) -> (double) m.facingDirection().getStepX())
            .propertyTyped("facing_dy", TypeCodecs.DOUBLE, (MachineRef m) -> (double) m.facingDirection().getStepY())
            .propertyTyped("facing_dz", TypeCodecs.DOUBLE, (MachineRef m) -> (double) m.facingDirection().getStepZ())

            // --- Generic TypedKey storage — get_typed(key, type) picks any registered type by name
            // ("int"/"double"/"bool"/"string"/"byte_array"/"item"/"vector"/a custom-registered
            // one/...) so scripts share ONE convention instead of every feature growing its own ad
            // hoc accessor pair. See TypedKeyBridge — including how to add a brand-new type name.
            // get_typed/set_typed migrated to the typed-registration API (PolyType.methodTyped2/3,
            // dev.arubik.craftengine.script.TypeCodecs) as a prototype for the wider typed-PolyType
            // initiative — key/type are always strings (TypeCodecs.STRING); the stored VALUE itself
            // stays TypeCodecs.RAW (a ScriptValue passthrough) because its real coercion is dynamic,
            // decided at call time by whichever TypedKeyBridge.Codec `type` names, not by anything
            // fixed at registration time. Behavior is unchanged from the untyped version above.
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (MachineRef m, String key, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return ScriptValue.NULL;
                    PersistentBlockEntity be = m.blockEntity();
                    if (be == null) return codec.fromStorage(null);
                    TypedKey<Object> tk = TypedKey.of("polyfills", "tkey_" + key, codec.storage());
                    return codec.fromStorage(be.get(tk));
                })
            .methodTyped3("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (MachineRef m, String key, String typeName, ScriptValue value) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return false;
                    PersistentBlockEntity be = m.blockEntity();
                    if (be == null) return false;
                    TypedKey<Object> tk = TypedKey.of("polyfills", "tkey_" + key, codec.storage());
                    try {
                        be.set(tk, codec.toStorage(value));
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            .methodTyped2("has_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String keyName, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return false;
                    PersistentBlockEntity be = m.blockEntity();
                    if (be == null) return false;
                    TypedKey<Object> key = TypedKey.of("polyfills", "tkey_" + keyName, codec.storage());
                    return be.has(key);
                })

            // --- Methods: block_at(dx,dy,dz) ---
            .methodTyped3("block_at", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW,
                ScriptValue.NULL,
                (MachineRef m, Double dxA, Double dyA, Double dzA) ->
                    BlockType.wrap(m.level(), m.pos().offset(dxA.intValue(), dyA.intValue(), dzA.intValue())))
            // Belt at THIS machine's own position — for a block that sits directly over/beside a
            // conveyor segment (e.g. a saw processing items riding through its own footprint).
            // Belt itself is null-safe: it's always a valid script value even when the block there
            // isn't (or is no longer) a conveyor — its is_full/has_item/peek/etc. just answer "no
            // belt here" (full=true, has_item=false, peek=empty) rather than throwing.
            .propertyTyped("belt", TypeCodecs.polyType("Belt", BeltType.BeltRef.class),
                (MachineRef m) -> m.level() == null || m.pos() == null ? null : new BeltType.BeltRef(m.level(), m.pos()))
            // belt_at(dx,dy,dz) — Belt at an arbitrary offset, for a machine whose relevant conveyor
            // segment isn't its own position (e.g. the one it's facing, or one block below it).
            .methodTyped3("belt_at", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW,
                ScriptValue.NULL,
                (MachineRef m, Double dxA, Double dyA, Double dzA) ->
                    dev.arubik.craftengine.script.types.machine.BeltType.wrap(
                            m.level(), m.pos().offset(dxA.intValue(), dyA.intValue(), dzA.intValue())))
            // container_at(dx,dy,dz) — the generic Container (depot, chest, another machine's own
            // inventory, ...) at this offset, via the same ItemTransferHelper lookup funnels/pipes/
            // push_item_toward already use — NULL if nothing container-backed is there. Callers use
            // the returned Container's own push/pull/pull_item (see ContainerType) uniformly, same
            // as belt_at's Belt wrapper for conveyor segments.
            .methodTyped3("container_at", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.RAW,
                ScriptValue.NULL,
                (MachineRef m, Double dxA, Double dyA, Double dzA) -> {
                    var containerOpt = dev.arubik.craftengine.pipe.item.ItemTransferHelper.getContainer(
                            m.level(), m.pos().offset(dxA.intValue(), dyA.intValue(), dzA.intValue()));
                    return dev.arubik.craftengine.script.types.util.ContainerType.wrap(containerOpt.orElse(null));
                })
            // neighbor_block(dir) — the block adjacent to THIS machine in a named world direction
            // ("north"/"south"/"east"/"west"/"up"/"down"), same direction-name convention io_get/
            // io_set already use. Lets a pipe panel script ask "what's actually sitting on this
            // face" (see Block.is_container) instead of assuming every face is the same kind of
            // neighbour.
            .methodTyped1("neighbor_block", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (MachineRef m, String dirName) -> {
                    Direction dir = Direction.byName(dirName);
                    if (dir == null) return ScriptValue.NULL;
                    return BlockType.wrap(m.level(), m.pos().relative(dir));
                })

            // add_item/add_item_to_slot removed — hand-rolled the same merge-then-fill Container.push
            // already implements (add_item), and CE-id-then-vanilla resolution ItemType.create already
            // implements (add_item_to_slot). Use Machine.container.push(item) and
            // Machine.container.set_item(slot, Item.create(id).with_count(n)) instead.



            // --- Overclock properties & methods ---
            // Machine.overclock → current overclock fraction (-1.0 to limit)
            .propertyTyped("overclock", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return (double) dm.getOverclock();
            })
            // Machine.overclock_limit → current max overclock (from upgrades)
            .propertyTyped("overclock_limit", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return dm.getOverclockLimit();
            })
            // Machine.efficiency → current fuel efficiency fraction (from upgrades)
            .propertyTyped("efficiency", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return dm.getFuelEfficiency();
            })
            // Machine.generation → current generation bonus fraction (from upgrades)
            .propertyTyped("generation", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 0.0;
                return dm.getGeneration();
            })
            // Machine.meter_state → integer 0..72 mapped from overclock (-1..+limit) — for clock_N icon names
            .propertyTyped("meter_state", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return 36.0;
                int s = (int) Math.round((dm.getOverclock() + 1.0) / 5.0 * 72.0);
                return (double) Math.max(0, Math.min(72, s));
            })
            // Machine.set_overclock(value) → set overclock fraction directly
            .methodTyped1("set_overclock", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double value) -> {
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                    dm.setOverclock(value.floatValue());
                    return true;
                })
            // Machine.bump_overclock(delta) → bump overclock by delta (clamped)
            .methodTyped1("bump_overclock", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double delta) -> {
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return false;
                    dm.addOverclock(delta.floatValue());
                    return true;
                })

            // --- Methods: RPM output / relay ---
            // Migrated to the typed-registration API (see get_typed/set_typed above for the same
            // note) — a single native double arg, boolean return.
            .methodTyped1("set_rpm_output", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double rpmArg) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof AbstractMachineBlockEntity machine)) return false;
                    try {
                        var dm = (dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity) machine;
                        float rpm = rpmArg.floatValue();
                        // Source motors must have a network to report SU — auto-create if missing
                        if (rpm != 0f && dm.rpmNetworkId() == 0L) {
                            var net = dev.arubik.craftengine.rotation.RpmNetwork.create();
                            dm.joinNetwork(net.id());
                        }
                        dm.setRpmSourceOutput(rpm);
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            // relay_to(block, rpm [, ignored_network_id]) — passes RPM to neighbor machine.
            // Uses in-memory RpmNetwork (no PDC). Anti-loop via sourceDistance.
            // Conflict detection: grace period of 3 ticks before breaking conflicting block.
            // rpm_out(direction) -> the RPM this machine actually delivers through that face.
            //
            // Everything the kinetic system knows about a face is folded in: whether io.rpm
            // declares it as an output at all, and what sign it carries there — a gearbox's
            // relative rule, or a static output_inverted list. Scripts previously had to
            // reimplement that guesswork from Machine.rpm, and got the sign wrong.
            //
            // Accepts an absolute direction ("north".."down"), a relative one ("front", "back",
            // "left", "right") resolved against the block's facing, or the axis names
            // ("axis_pos", "axis_neg"). Returns 0 when the face is not an output.
            // Machine.layout -> runtime control over the open menu's slots.
            .propertyTyped("layout", TypeCodecs.polyType("Layout", LayoutType.LayoutRef.class),
                (MachineRef m) -> m == null ? null : new LayoutType.LayoutRef(m))
            // Machine.io -> the machine's IO port map, editable at runtime.
            // Lets a script build its IO on the fly for any type instead of it being frozen in the
            // machine JSON: a pipe whose faces the player configures, a machine that opens an
            // output only once a recipe finishes, and so on.
            .propertyTyped("io", TypeCodecs.polyType("Io", IoType.IoRef.class),
                (MachineRef m) -> m == null ? null : new IoType.IoRef(m))
            .methodTyped1("rpm_out", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (MachineRef m, String faceName) -> {
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm))
                        return 0.0;
                    Direction face = resolveDirection(m, faceName);
                    if (face == null) return 0.0;
                    return (double) dm.rpmThrough(face);
                })
            // rpm_out_faces() -> every direction this machine currently drives, as a map of
            // direction name to the RPM delivered there. Handy for debugging a build.
            .methodTyped0("rpm_out_faces", TypeCodecs.RAW,
                (MachineRef m) -> {
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm))
                        return dev.arubik.craftengine.script.types.primitive.MapType.wrap(new java.util.LinkedHashMap<>());
                    java.util.LinkedHashMap<String, ScriptValue> out = new java.util.LinkedHashMap<>();
                    for (Direction d : Direction.values()) {
                        float v = dm.rpmThrough(d);
                        if (v != 0f) out.put(d.getName(), ScriptValue.of(v));
                    }
                    return dev.arubik.craftengine.script.types.primitive.MapType.wrap(out);
                })
            // The documented third argument (network_id) is deliberately ignored by the body, so the
            // typed registration declares only the two it actually reads — extra trailing arguments
            // are dropped by the dispatcher exactly as before.
            .methodTyped2("relay_to", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue bv, Double rpmArg) -> {
                if (!(bv instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef r)) return false;
                float rpm = rpmArg.floatValue();
                try {
                    net.momirealms.craftengine.core.block.entity.BlockEntity nbe =
                        dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
                    if (nbe == null || nbe.controller == null) return false;
                    if (!(nbe.controller instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity neighbor)) return false;
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity myMachine)) return false;

                    // Anti-loop: only push to machines farther from source
                    if (neighbor.sourceDistance() <= myMachine.sourceDistance()) return false;

                    // Conflict detection. A neighbour already carrying OUR source's value is not
                    // in conflict with us however different the number is: that is a speed change
                    // still spreading, and breaking on it destroyed meshed cogwheels every time
                    // the player adjusted the motor's RPM.
                    float current = neighbor.getRpm();
                    boolean sameSource = neighbor.rpmSourceStamp() >= 0L
                            && neighbor.rpmSourceStamp() == myMachine.rpmSourceStamp();
                    if (!sameSource && current != 0f && Math.abs(current - rpm) > 0.5f) {
                        int ct = neighbor.getRpmConflictTicks() + 1;
                        neighbor.setRpmConflictTicks(ct);
                        if (dev.arubik.craftengine.rotation.RpmPropagation.conflictShouldBreak(ct)) {
                            try { r.level().destroyBlock(r.pos(), true); } catch (Throwable ignored2) {}
                        }
                        return false;
                    }
                    neighbor.setRpmConflictTicks(0);

                    // Ensure both machines share the same RpmNetwork
                    long myNetId = myMachine.rpmNetworkId();
                    if (myNetId == 0L) {
                        dev.arubik.craftengine.rotation.RpmNetwork newNet = dev.arubik.craftengine.rotation.RpmNetwork.create();
                        myMachine.joinNetwork(newNet.id());
                        myNetId = newNet.id();
                    }
                    neighbor.joinNetwork(myNetId);

                    // Set RPM, stamping the neighbour one hop farther from the source so the
                    // anti-loop guard above lets the NEXT hop through on the following tick.
                    // Hand on the SOURCE's stamp unchanged. A relay must not mint a fresh one:
                    // that is what let two meshed cogs keep renewing each other forever after
                    // their motor was gone.
                    long stamp = myMachine.rpmSourceStamp();
                    if (!dev.arubik.craftengine.rotation.RpmPropagation.isFresh(
                            stamp, r.level().getGameTime())) {
                        return false;   // we are running on a stale value ourselves
                    }
                    neighbor.acceptRelayedRpm(rpm, myMachine.sourceDistance(), stamp);
                    neighbor.setTheoreticalSpeed(rpm);
                    return true;
                } catch (Throwable ignored) {}
                return false;
            })
            // relay_rpm removed — use relay_to() + set_rpm_output() in scripts instead
            .methodTyped2("push_rpm", TypeCodecs.RAW, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue bv, Double pushRpmArg) -> {
                    if (!(bv instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef r)) return false;
                    float pushRpm = pushRpmArg.floatValue();
                    try {
                        net.momirealms.craftengine.core.block.entity.BlockEntity be2 =
                            dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
                        if (be2 != null && be2.controller instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity neighbor) {
                            neighbor.setRpmSourceOutput(pushRpm);
                            return true;
                        }
                    } catch (Throwable ignored) {}
                    return false;
                })
            // gas_tanks → Array of Map{name, level, capacity, contents_name, color}
            // Stays untyped: the elements are Maps built per tank, not instances of one PolyType
            // that TypeCodecs.listOf could re-box — same call this codebase already makes for
            // RecipeOutputs.fluids/.gases.
            .property("gas_tanks", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine)) return new ScriptValue.Array(java.util.List.of());
                java.util.List<ScriptValue> result = new java.util.ArrayList<>();
                for (dev.arubik.craftengine.gas.GasTank tank : machine.gasTankList()) {
                    try {
                        dev.arubik.craftengine.gas.GasStack gas = tank.getGas(m.level(), m.pos());
                        java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
                        map.put("name", ScriptValue.of(tank.getName()));
                        map.put("level", ScriptValue.of(gas.getAmount()));
                        map.put("capacity", ScriptValue.of(tank.getCapacity()));
                        map.put("is_empty", ScriptValue.of(gas.isEmpty()));
                        map.put("contents_name", gas.isEmpty() ? ScriptValue.of("") : ScriptValue.of(gas.getType().getDisplayName()));
                        // contents_key = registry ID e.g. "polyfills:steam"
                        // contents_id = same (alias for clarity)
                        String gasId = gas.isEmpty() ? "" : gas.getType().id().toString();
                        map.put("contents_key", ScriptValue.of(gasId));
                        map.put("contents_id", ScriptValue.of(gasId));
                        map.put("color", gas.isEmpty() ? ScriptValue.of(0) : ScriptValue.of(gas.getType().color()));
                        result.add(dev.arubik.craftengine.script.types.primitive.MapType.wrap(map));
                    } catch (Throwable ignored) {}
                }
                return new ScriptValue.Array(result);
            })
            // --- Programmatic renderer methods (ephemeral item_display entities) ---
            // spawn_display(item, vec_pos, options_map?) → uuid string
            // Typed with a null sentinel on arg0 (see to_item): `itemId == null` is exactly the old
            // `args.isEmpty()` early return. arg1 defaults to null, which fails the instanceof
            // exactly as an absent/non-Vector argument did; arg2 keeps its 1.0 default.
            .methodTypedOpt3("spawn_display", TypeCodecs.STRING, null, TypeCodecs.RAW, null,
                TypeCodecs.DOUBLE, 1.0, TypeCodecs.RAW,
                (MachineRef m, String itemId, ScriptValue posArg, Double scaleArg) -> {
                if (itemId == null) return ScriptValue.NULL;
                // pos offset relative to machine center
                double ox = 0, oy = 0.5, oz = 0;
                if (posArg instanceof ScriptValue.Obj vo && vo.instance() instanceof org.joml.Vector3d v) {
                    ox = v.x; oy = v.y; oz = v.z;
                }
                float scale = (float) (double) scaleArg;
                try {
                    net.minecraft.world.phys.Vec3 wpos = new net.minecraft.world.phys.Vec3(
                        m.pos().getX() + 0.5 + ox, m.pos().getY() + oy, m.pos().getZ() + 0.5 + oz);
                    var entity = dev.arubik.craftengine.machine.render.ProgrammaticRendererState.spawnItemDisplay(
                        m.level(), wpos, itemId, scale);
                    if (entity == null) return ScriptValue.NULL;
                    return ScriptValue.of(entity.getUUID().toString());
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            // update_display(uuid_str, options_map) → bool
            .methodTyped2("update_display", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (MachineRef m, String uuidStr, ScriptValue opts) -> {
                    try {
                        java.util.UUID id = java.util.UUID.fromString(uuidStr);
                        net.minecraft.world.entity.Entity e = m.level().getEntity(id);
                        if (e == null) return false;
                        // Read pos from options map
                        if (opts instanceof ScriptValue.Obj ov) {
                            ScriptValue posVal = opts.getProperty("pos");
                            if (posVal instanceof ScriptValue.Obj pv && pv.instance() instanceof org.joml.Vector3d v) {
                                e.setPos(m.pos().getX() + 0.5 + v.x, m.pos().getY() + v.y, m.pos().getZ() + 0.5 + v.z);
                            }
                        }
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            // kill_display(uuid_str) → bool
            .methodTyped1("kill_display", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String uuidStr) -> {
                    try {
                        java.util.UUID id = java.util.UUID.fromString(uuidStr);
                        net.minecraft.world.entity.Entity e = m.level().getEntity(id);
                        if (e != null) { e.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED); return true; }
                    } catch (Throwable ignored) {}
                    return false;
                })
            // play_animation(anim) — attach a ScriptAnimation to this machine's tick cycle
            .methodTyped1("play_animation", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (MachineRef m, ScriptValue animVal) -> {
                    if (!(animVal instanceof ScriptValue.Obj ao) ||
                        !(ao.instance() instanceof dev.arubik.craftengine.machine.render.ScriptAnimation anim))
                        return false;
                    if (m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm) {
                        dm.setActiveAnimation(anim);
                        return true;
                    }
                    return false;
                })
            // fluid_tanks → Array of Map{name, level, capacity, contents_name, color}
            // Stays untyped for the same reason as gas_tanks above (Map elements, not one PolyType).
            .property("fluid_tanks", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine)) return new ScriptValue.Array(java.util.List.of());
                java.util.List<ScriptValue> result = new java.util.ArrayList<>();
                for (dev.arubik.craftengine.fluid.FluidTank tank : machine.fluidTankList()) {
                    try {
                        dev.arubik.craftengine.fluid.FluidStack fluid = tank.getFluid(m.level(), m.pos());
                        java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
                        map.put("name", ScriptValue.of(tank.getName()));
                        map.put("level", ScriptValue.of(fluid.getAmount()));
                        map.put("capacity", ScriptValue.of(tank.getCapacity()));
                        map.put("is_empty", ScriptValue.of(fluid.isEmpty()));
                        map.put("contents_name", fluid.isEmpty() ? ScriptValue.of("") : ScriptValue.of(fluid.getType().translationKey()));
                        map.put("color", fluid.isEmpty() ? ScriptValue.of(0) : ScriptValue.of(fluid.getType().color()));
                        result.add(dev.arubik.craftengine.script.types.primitive.MapType.wrap(map));
                    } catch (Throwable ignored) {}
                }
                return new ScriptValue.Array(result);
            })
            // get_gas_level(tankName) → amount of gas in named tank
            .methodTyped1("get_gas_level", TypeCodecs.STRING, TypeCodecs.DOUBLE, 0.0,
                (MachineRef m, String tankName) -> {
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                    try {
                        for (dev.arubik.craftengine.gas.GasTank tank : machine.gasTankList()) {
                            if (tank.getName().equals(tankName)) {
                                dev.arubik.craftengine.gas.GasStack gas = tank.getGas(m.level(), m.pos());
                                return (double) gas.getAmount();
                            }
                        }
                    } catch (Throwable ignored) {}
                    return 0.0;
                })
            // consume_gas(tankName, amount) → bool
            .methodTyped2("consume_gas", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, String tankName, Double amountArg) -> {
                    int amount = amountArg.intValue();
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return false;
                    try {
                        for (dev.arubik.craftengine.gas.GasTank tank : machine.gasTankList()) {
                            if (tank.getName().equals(tankName)) {
                                tank.extract(m.level(), m.pos(), amount, null);
                                return true;
                            }
                        }
                    } catch (Throwable ignored) {}
                    return false;
                })
            // --- Properties/methods: live energy tuning ---
            // The JSON "energy" block is just a starting point; a script (e.g. a windmill scaling
            // its output with wind/height, or a reactor throttling under overload) needs to change
            // capacity/rate live. Reads/writes the SAME fields insertEnergy/extractEnergy/processTick
            // already use — no shadow state to fall out of sync.
            // burn_time/max_burn_time — remaining/total ticks of fuel currently lit (a real
            // furnace-style item fuel, e.g. coal), mirroring energy_stored/energy_capacity's shape
            // so a script (recipe_info.pf) can show "is this recipe waiting on fuel" without
            // reaching for anything burn-specific. 0/0 for a machine that doesn't burn fuel at all.
            .propertyTyped("burn_time", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.getBurnTime();
            })
            .propertyTyped("max_burn_time", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.getMaxBurnTime();
            })
            .propertyTyped("energy_stored", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.getStoredEnergyForCarrier();
            })
            // consume_energy(amount) — an internal cost for a script-driven ACTION (a teleport, an
            // ability, ...), not a transfer to/from a neighbour, so it deliberately bypasses the
            // IOConfiguration gating insertEnergy/extractEnergy enforce (setStoredEnergyRaw, the
            // same "engine apply" path the network solvers use). Returns false — and takes
            // nothing — if the buffer doesn't have enough; never partially consumes.
            .methodTyped1("consume_energy", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double amountArg) -> {
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return false;
                    int amount = amountArg.intValue();
                    if (amount <= 0 || machine.getStoredEnergyForCarrier() < amount) return false;
                    machine.setStoredEnergyRaw(m.level(), machine.getStoredEnergyForCarrier() - amount);
                    return true;
                })
            .propertyTyped("energy_capacity", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.energyCapacity();
            })
            .propertyTyped("energy_max_input", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.energyMaxInput();
            })
            .propertyTyped("energy_max_output", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.energyMaxOutput();
            })
            .propertyTyped("energy_per_tick", TypeCodecs.DOUBLE, (MachineRef m) -> {
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return 0.0;
                return (double) machine.energyPerTick();
            })
            // Measured, not configured: max_input/max_output/per_tick are CAPS this machine
            // declares — real_input/real_output are what the network ACTUALLY moved through this
            // node on its last EnergyEngine step (see EnergyEngine#lastDelta). Pull the generator
            // feeding this cell off the network and real_input drops to 0 next tick even though
            // max_input/capacity haven't changed at all.
            .propertyTyped("energy_real_input", TypeCodecs.DOUBLE, (MachineRef m) -> {
                int delta = dev.arubik.craftengine.fluid.graph.EnergyEngine.lastDelta(m.pos());
                return (double) Math.max(0, delta);
            })
            .propertyTyped("energy_real_output", TypeCodecs.DOUBLE, (MachineRef m) -> {
                int delta = dev.arubik.craftengine.fluid.graph.EnergyEngine.lastDelta(m.pos());
                return (double) Math.max(0, -delta);
            })
            .methodTyped1("set_energy_per_tick", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double value) -> {
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return false;
                    machine.setEnergyPerTick(value.intValue());
                    return true;
                })
            .methodTyped1("set_energy_max_input", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double value) -> {
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return false;
                    machine.setEnergyMaxInput(value.intValue());
                    return true;
                })
            .methodTyped1("set_energy_max_output", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, Double value) -> {
                    if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return false;
                    machine.setEnergyMaxOutput(value.intValue());
                    return true;
                })

            // --- Methods: fill_fluid ---
            .methodTyped2("fill_fluid", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MachineRef m, String fluidId, Double amountArg) -> {
                    PersistentBlockEntity be = m.blockEntity();
                    if (!(be instanceof AbstractMachineBlockEntity machine)) return false;
                    int amount = amountArg.intValue();
                    try {
                        FluidType ft = FluidType.byName(fluidId);
                        if (ft == null) return false;
                        FluidStack stack = new FluidStack(ft, amount);
                        return machine.fillTank(m.level(), stack);
                    } catch (Throwable ignored) { return false; }
                })

            // --- Methods: blocks_in_range, break_block, place_block_at ---
            .methodTypedOpt1("blocks_in_range", TypeCodecs.DOUBLE, 3.0,
                TypeCodecs.listOf("Block", BlockType.BlockRef.class),
                (MachineRef m, Double radiusArg) -> {
                    int radius = radiusArg.intValue();
                    java.util.List<BlockType.BlockRef> blocks = new java.util.ArrayList<>();
                    BlockPos center = m.pos();
                    for (int dx = -radius; dx <= radius; dx++)
                        for (int dy = -radius; dy <= radius; dy++)
                            for (int dz = -radius; dz <= radius; dz++) {
                                BlockPos p = center.offset(dx, dy, dz);
                                if (!m.level().getBlockState(p).isAir())
                                    blocks.add(new BlockType.BlockRef(m.level(), p));
                            }
                    return blocks;
                })
            // break_block(block)/place_block_at(dx,dy,dz,item) removed — both hand-rolled logic that
            // now lives on the Block value itself (see BlockType.break_and_drop/place_from_item),
            // which every caller already had in hand (Machine.facing_block, World.get_block, ...):
            // use block.break_and_drop() / Machine.block_at(dx,dy,dz).place_from_item(item).

            // damage_entity/fire_entity/freeze_entity/apply_bone_meal removed — use entity.damage/fire/freeze and block.apply_bone_meal()
            // Tests EVERY player in range, not just the nearest one: with getNearestPlayer a
            // bystander standing closer than the person actually looking suppressed the signal.
            .methodTypedOpt1("is_player_looking_at", TypeCodecs.DOUBLE, 8.0, TypeCodecs.BOOL,
                (MachineRef m, Double dist) -> {
                    double distSq = dist * dist;
                    net.minecraft.world.phys.Vec3 center = net.minecraft.world.phys.Vec3.atCenterOf(m.pos());
                    for (var player : m.level().players()) {
                        if (player.isSpectator()) continue;
                        net.minecraft.world.phys.Vec3 eyePos = player.getEyePosition();
                        if (eyePos.distanceToSqr(center) > distSq) continue;
                        net.minecraft.world.phys.Vec3 look = player.getLookAngle().normalize();
                        net.minecraft.world.phys.Vec3 toBlock = center.subtract(eyePos);
                        double dot = toBlock.dot(look);
                        if (dot <= 0 || dot > dist) continue;
                        // Perpendicular distance from the look ray to the block centre.
                        if (toBlock.subtract(look.scale(dot)).length() < 1.0) return true;
                    }
                    return false;
                })
            // STRING encodes a Java null back to NULL, so both null branches are unchanged.
            .propertyTyped("owner_uuid", TypeCodecs.STRING, (MachineRef m) -> {
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return null;
                TypedKey<String> key = TypedKey.of("polyfills", "machine_owner_uuid", NbtType.STRING);
                return be.get(key);
            })
            // owner_uuid was also registered as a .method() here with an identical body — dead code,
            // nothing ever called Machine.owner_uuid() with parens; the .property above is the only
            // form any script uses. Removed the duplicate.
            // item_attack_damage removed — use item.attack_damage property

            // set_block/create_item removed — use World.set_block(x,y,z,id) and create_item(id,count) builtin

            // --- Methods: Player proximity ---
            .methodTypedOpt1("player_in_range", TypeCodecs.DOUBLE, 8.0, TypeCodecs.BOOL,
                (MachineRef m, Double dist) -> {
                    Vec3 center = Vec3.atCenterOf(m.pos());
                    var player = m.level().getNearestPlayer(center.x, center.y, center.z, dist, false);
                    return player != null;
                })
            .methodTyped1("player_facing", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MachineRef m, String face) -> {
                    Vec3 center = Vec3.atCenterOf(m.pos());
                    var player = m.level().getNearestPlayer(center.x, center.y, center.z, 32, false);
                    if (player == null) return false;
                    Direction playerDir = Direction.orderedByNearest(player)[0];
                    return playerDir.getName().equalsIgnoreCase(face);
                })
            // close() — closes the nearest viewer's open inventory (same @p-style nearest-player
            // heuristic as player_in_range/player_facing, since a script method has no direct
            // handle on "whoever has this machine's GUI open"). Meant for scripts like
            // teleporter.pf's do_teleport() to close the GUI before moving the player elsewhere.
            .methodTyped0("close", TypeCodecs.BOOL,
                (MachineRef m) -> {
                    Vec3 center = Vec3.atCenterOf(m.pos());
                    var player = m.level().getNearestPlayer(center.x, center.y, center.z, 8, false);
                    if (player != null) player.closeContainer();
                    return player != null;
                })
            // update() — forces the currently-open menu (if any) to immediately re-evaluate,
            // instead of waiting up to 2 ticks for the normal periodic refresh: this re-runs the
            // page's "buttons"/"layout" generator script too (so a destination/button appearing,
            // disappearing, or moving shows up right away, not just already-installed slots'
            // name/lore), and re-syncs input/output/storage — repainted onto the SAME open
            // inventory in place, so nothing flickers for the viewer. For a script that just
            // changed something a button/generator reads (a flag, a str_flag, the registry data a
            // generator scans) and wants it visible on the SAME click that changed it — a no-op if
            // nobody has this machine's menu open right now.
            .methodTyped0("update", TypeCodecs.BOOL,
                (MachineRef ref) -> {
                    PersistentBlockEntity be = ref.blockEntity();
                    if (be instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity m) {
                        return m.rebuildAndReopenMenu();
                    }
                    return false;
                })
            // rename_text — the current text typed into an ANVIL-type page's rename field (a
            // page declared with "size": "anvil" — see MachineDefinition.PageDef#inventoryType).
            // Bukkit's AnvilInventory#getRenameText() works on this inventory even though no real
            // anvil block backs it, so a machine can read whatever the player is typing live
            // (e.g. to price a custom repair/combine recipe by name) without needing a real anvil
            // recipe to be registered. "" if nobody has an anvil-type page of this machine open,
            // or the open page isn't an anvil.
            .propertyTyped("rename_text", TypeCodecs.STRING, (MachineRef ref) -> {
                PersistentBlockEntity be = ref.blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity m) {
                    var openMenu = m.getOpenMenuOrNull();
                    if (openMenu != null && openMenu.getInventory() instanceof org.bukkit.inventory.AnvilInventory anvil) {
                        String text = anvil.getRenameText();
                        return text != null ? text : "";
                    }
                }
                return "";
            });
    }

    public static ScriptValue wrap(ServerLevel level, BlockPos pos, String facing, PersistentBlockEntity be) {
        return ScriptValue.ofObj("Machine", new MachineRef(level, pos, facing, be));
    }

    /**
     * Resolves a script-facing direction name to a world direction.
     *
     * <p>Absolute ("north".."down"), facing-relative ("front", "back", "left", "right"), or
     * axis-relative ("axis_pos", "axis_neg") against the block's own axis property — the same
     * vocabulary the io.rpm config uses, so a script and a machine JSON can name faces alike.
     */
    private static Direction resolveDirection(MachineRef m, String name) {
        if (name == null || name.isEmpty()) return null;
        String n = name.trim().toLowerCase(java.util.Locale.ROOT);
        Direction facing = m.facingDirection();
        switch (n) {
            case "front":     return facing;
            case "back":      return facing.getOpposite();
            case "right":     return facing.getClockWise();
            case "left":      return facing.getCounterClockWise();
            case "axis_pos":
            case "axis_neg": {
                String axis = BlockType.readProperty(m.level().getBlockState(m.pos()), "axis");
                Direction pos = switch (axis == null ? "y" : axis) {
                    case "x" -> Direction.EAST;
                    case "z" -> Direction.SOUTH;
                    default  -> Direction.UP;
                };
                return n.equals("axis_pos") ? pos : pos.getOpposite();
            }
            default:
                return Direction.byName(n);
        }
    }

    private static MachineRef ref(Object obj) {
        return (MachineRef) obj;
    }

    /** Extracts one or more Bukkit-side {@link ItemStack}s from a script value that's EITHER a
     *  single item (a {@code ScriptValue.Item} or a raw {@code ItemStack} Obj — same two shapes
     *  {@code drop_item}/{@code drop_item_toward}/{@code drop_item_at} already accepted) OR an
     *  Array of either — used by {@code drop_item}/{@code drop_item_toward} so a script can drop a
     *  whole batch (e.g. a chute's overflow) as one call instead of looping calls itself. Empty
     *  items are skipped, not spawned as ghost entities; a non-array, non-item arg yields an empty
     *  list, same "silently no-op" contract every one of these methods already had for a bad arg0. */
    private static java.util.List<ItemStack> extractStacks(ScriptValue arg) {
        java.util.List<ItemStack> out = new java.util.ArrayList<>();
        if (arg instanceof ScriptValue.Array arr) {
            for (ScriptValue v : arr.elements()) {
                ItemStack s = singleStack(v);
                if (s != null && !s.isEmpty()) out.add(s);
            }
            return out;
        }
        ItemStack s = singleStack(arg);
        if (s != null && !s.isEmpty()) out.add(s);
        return out;
    }

    private static ItemStack singleStack(ScriptValue arg) {
        if (arg instanceof ScriptValue.Item i) return i.stack().copy();
        if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) return is.copy();
        return null;
    }

    /** {@code get_renderer}'s block-entity-shape lookup — {@code DataMachineBlockEntity} and
     *  {@code DataMultiBlockMachineBlockEntity} each own a {@code RendererManager} but share no
     *  common supertype exposing it, so this just checks both. NULL for any other block entity
     *  shape (no renderer config at all). */
    private static dev.arubik.craftengine.machine.render.RendererManager rendererManagerOf(MachineRef m) {
        PersistentBlockEntity be = m.blockEntity();
        if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity d) return d.rendererManager();
        if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMultiBlockMachineBlockEntity d) return d.rendererManager();
        return null;
    }

    /** Case-insensitive {@code IOType} lookup for {@code io_get}/{@code io_set}'s type-name argument. */
    private static dev.arubik.craftengine.multiblock.IOConfiguration.IOType parseIoType(String name) {
        if (name == null) return null;
        try {
            return dev.arubik.craftengine.multiblock.IOConfiguration.IOType.valueOf(
                    name.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * The machine's LIVE per-instance IOConfiguration as a mutable {@code Simple}, installing a
     * fresh one (via {@code setIOConfiguration}) the first time something tries to write to it. A
     * machine with no {@code io} block in its definition starts with {@code ioConfiguration == null};
     * one that declared a static {@code io} block already has a {@code Simple} from the loader (see
     * {@code MachineDefinitionLoader#parseIO}) and that same instance keeps being mutated in place.
     */
    private static dev.arubik.craftengine.multiblock.IOConfiguration.Simple liveSimpleConfig(
            AbstractMachineBlockEntity machine) {
        // Delegates to mutableIO(), which hands back a copy OWNED BY THIS BLOCK ENTITY.
        //
        // Returning the existing Simple directly would be a trap: a machine is built pointing at
        // the IOConfiguration instance held by its shared MachineDefinition, so editing it in
        // place silently reconfigures every other machine of the same type. Starting from a blank
        // Simple instead is the opposite failure — it would throw away the machine's declared io.
        return machine.mutableIO();
    }

    /** The face permission actually in effect RIGHT NOW — the entity's own owned config once one
     * exists, else the block's static {@code defaultIOConfig} (what every other reader —
     * {@code carrierConnectsHere}, the energy/item network engines' faceMode — already falls back
     * to). {@code io_get} reading ONLY the entity's (possibly still-null) field used to report
     * "Disabled" on a face that was actually open by default until the player's first click. */
    private static dev.arubik.craftengine.multiblock.IOConfiguration resolveLiveIOConfig(MachineRef m) {
        if (m.blockEntity() instanceof AbstractMachineBlockEntity machine) {
            dev.arubik.craftengine.multiblock.IOConfiguration cfg = machine.getIOConfiguration();
            if (cfg != null) return cfg;
        }
        try {
            net.minecraft.world.level.block.state.BlockState state = m.level().getBlockState(m.pos());
            var custom = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(state);
            if (custom.isEmpty()) return null;
            net.momirealms.craftengine.core.block.behavior.BlockBehavior beh = custom.get().behavior();
            if (beh instanceof dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior cbb)
                return cbb.getIOConfiguration(m.level(), m.pos());
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** Re-derives the connected-face mask for whatever block sits at {@code m}'s position (if it's
     * itself a {@code ConnectedBlockBehavior}, e.g. an item pipe's own per-face IO), AND for every
     * adjacent pipe/cable — a plain machine (energy_cell, energy_generator, ...) has no mask of its
     * own, but {@code io_set} changing ITS IOConfiguration is exactly what a neighbouring cable's
     * {@code carrierConnectsHere} reads to decide whether it connects to THIS block. Without this,
     * toggling a machine's side to Disabled left the adjacent cable's connected-face render stale
     * until some unrelated block update happened to recompute it. */
    private static void recomputeMaskIfConnected(MachineRef m) {
        try {
            net.minecraft.world.level.block.state.BlockState state = m.level().getBlockState(m.pos());
            var custom = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(state);
            if (custom.isEmpty())
                return;
            net.momirealms.craftengine.core.block.behavior.BlockBehavior beh = custom.get().behavior();
            dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior cbb =
                    beh instanceof dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior c ? c
                            : (beh == null ? null
                                    : beh.getFirst(dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior.class));
            if (cbb != null) {
                cbb.recomputeMaskAndNeighbors(m.level(), m.pos());
                return;
            }
            for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
                net.minecraft.core.BlockPos neighborPos = m.pos().relative(dir);
                var neighborCustom = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                        .getOptionalCustomBlockState(m.level().getBlockState(neighborPos));
                if (neighborCustom.isEmpty())
                    continue;
                net.momirealms.craftengine.core.block.behavior.BlockBehavior neighborBeh = neighborCustom.get().behavior();
                dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior neighborCbb =
                        neighborBeh instanceof dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior nc ? nc
                                : (neighborBeh == null ? null
                                        : neighborBeh.getFirst(dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior.class));
                if (neighborCbb != null)
                    neighborCbb.recomputeMaskAndNeighbors(m.level(), neighborPos);
            }
        } catch (Throwable ignored) {
        }
    }
}
