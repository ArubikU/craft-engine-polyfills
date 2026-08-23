package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.machine.MachineRedstone;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.world.BlockType;
import dev.arubik.craftengine.script.types.entity.EntityType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
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
         * Returns cached contraption, or reconnects by UUID from str_flag "contraption_uuid".
         * Returns null if not assembled or contraption no longer alive.
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
            // Try to reconnect by UUID stored in the block entity
            if (blockEntity == null) return null;
            try {
                // Read str_flag "contraption_uuid" from CE block entity NBT (no Bukkit PDC)
                dev.arubik.craftengine.util.TypedKey<String> key =
                    dev.arubik.craftengine.util.TypedKey.of("polyfills", "sflag_contraption_uuid", dev.arubik.craftengine.util.NbtType.STRING);
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

    @SuppressWarnings("deprecation")
    public static void register() {
        PolyTypeRegistry.define("Machine", "Block")
            // --- Properties ---
            .property("pos", obj -> VectorType.wrap(ref(obj).pos().getX() + 0.5, ref(obj).pos().getY() + 0.5, ref(obj).pos().getZ() + 0.5))
            .property("x", obj -> ScriptValue.of(ref(obj).pos().getX()))
            .property("y", obj -> ScriptValue.of(ref(obj).pos().getY()))
            .property("z", obj -> ScriptValue.of(ref(obj).pos().getZ()))
            .property("facing", obj -> ScriptValue.of(ref(obj).facing()))
            // set_state override: fires on_state_change hook after changing the block state
            .method("set_state", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                net.minecraft.world.level.block.state.BlockState oldState = m.level().getBlockState(m.pos());
                // Use BlockType's readProperty/writeProperty logic
                String propName = args.get(0).asStr();
                String value = args.get(1).asStr();
                net.minecraft.world.level.block.state.BlockState newState = dev.arubik.craftengine.script.types.world.BlockType.writeProperty(oldState, propName, value);
                if (newState == null) return ScriptValue.of(false);
                m.level().setBlock(m.pos(), newState, 3);
                // Fire on_state_change if defined
                if (m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm
                        && dm.definition() != null && dm.definition().onStateChangeScript() != null) {
                    try {
                        dev.arubik.craftengine.script.ScriptContext baseCtx = dm.buildScriptContext();
                        if (baseCtx != null) {
                            dev.arubik.craftengine.script.ScriptContext ctx = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(baseCtx)
                                .typed("prevState", dev.arubik.craftengine.script.types.world.BlockStateType.wrap(oldState))
                                .typed("newState",  dev.arubik.craftengine.script.types.world.BlockStateType.wrap(newState))
                                .build();
                            dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(dm.definition().onStateChangeScript());
                            if (call != null) call.execute(ctx);
                        }
                    } catch (Throwable ignored) {}
                }
                return ScriptValue.of(true);
            })
            // turn_page(delta) — multipage chest page switch (stash+show items)
            .method("turn_page", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int delta = (int) args.get(0).asNum();
                MachineRef m = ref(obj);
                if (m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMultiBlockMachineBlockEntity dmb) {
                    dmb.turnPage(delta);
                    return ScriptValue.of(true);
                }
                return ScriptValue.of(false);
            })
            // --- Page navigation (browser-like history) ---
            // Machine.page(n) — navigate to page n
            .method("page", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                int n = (int) args.get(0).asNum();
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                // Use active player if available — for script-triggered navigation we store per-machine
                try {
                    // Find viewer via active MachineMenu
                    org.bukkit.entity.Player p = null;
                    if (dm.getMenu() != null) {
                        for (org.bukkit.entity.HumanEntity he : dm.getMenu().getInventory().getViewers()) {
                            if (he instanceof org.bukkit.entity.Player bp) { p = bp; break; }
                        }
                    }
                    if (p == null) return ScriptValue.of(false);
                    dm.openPage(p, n);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // Machine.next_page() — go to currentPage+1
            .method("next_page", (obj, args) -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                try {
                    // Find viewer via active MachineMenu
                    org.bukkit.entity.Player p = null;
                    if (dm.getMenu() != null) {
                        for (org.bukkit.entity.HumanEntity he : dm.getMenu().getInventory().getViewers()) {
                            if (he instanceof org.bukkit.entity.Player bp) { p = bp; break; }
                        }
                    }
                    if (p == null) return ScriptValue.of(false);
                    dm.openPage(p, Math.min(dm.currentPage() + 1, dm.pageCount() - 1));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // Machine.prev_page() — browser back (history)
            .method("prev_page", (obj, args) -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                try {
                    // Find viewer via active MachineMenu
                    org.bukkit.entity.Player p = null;
                    if (dm.getMenu() != null) {
                        for (org.bukkit.entity.HumanEntity he : dm.getMenu().getInventory().getViewers()) {
                            if (he instanceof org.bukkit.entity.Player bp) { p = bp; break; }
                        }
                    }
                    if (p == null) return ScriptValue.of(false);
                    dm.openPrevPage(p);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .property("current_page", obj -> {
                if (!(ref(obj).blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.currentPage());
            })
            .property("page_count", obj -> {
                if (!(ref(obj).blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.pageCount());
            })
            // Multi-cell structure (an auto-placing shape declared on the BLOCK's own "cells"
            // config, see MultiCellGeometry#parseCells — NOT a MachineDefinition concept, so this
            // just reflects whatever the block behavior actually set on this core). A script always
            // runs against the CORE (parts forward interaction/scripts to it), so there's no
            // separate "am I a part" accessor needed here.
            .property("is_multi_cell", obj -> {
                if (!(ref(obj).blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                return ScriptValue.of(dm.cellCount() > 0);
            })
            .property("cell_count", obj -> {
                if (!(ref(obj).blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.cellCount());
            })
            // ticks_alive: how many server ticks this block has been loaded (persisted via NBT)
            .property("ticks_alive", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)
                    return ScriptValue.of((double) dm.ticksAlive());
                return ScriptValue.of(0.0);
            })
            // rpm_network: in-memory Long network id (0 = not in network)
            .property("rpm_network", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of((double) dm.rpmNetworkId());
            })
            // source_distance: 0 = primary source, MAX_INT = not connected
            .property("source_distance", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)
                    return ScriptValue.of(dm.sourceDistance());
                return ScriptValue.of(Integer.MAX_VALUE);
            })
            // Shorthands for common block state properties used in renderer "when" expressions
            .property("axis", obj -> {
                MachineRef m = ref(obj);
                String v = BlockType.readProperty(m.level().getBlockState(m.pos()), "axis");
                return v != null ? ScriptValue.of(v) : ScriptValue.NULL;
            })
            .property("activated", obj -> {
                MachineRef m = ref(obj);
                String v = BlockType.readProperty(m.level().getBlockState(m.pos()), "activated");
                return ScriptValue.of("true".equals(v));
            })
            .property("facing_block", obj -> {
                MachineRef m = ref(obj);
                return BlockType.wrap(m.level(), m.facingBlockPos());
            })


            // --- Methods: Entity interaction ---
            .method("nearby_entities", (obj, args) -> {
                MachineRef m = ref(obj);
                double radius = args.isEmpty() ? 5 : args.get(0).asNum();
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
            .method("use_item_on_entity", (obj, args) -> {
                // use_item_on_entity(entity, slot) — feeds/interacts with entity using item from slot
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                ScriptValue eArg = args.get(0);
                int slot = (int) args.get(1).asNum();
                if (!(eArg instanceof ScriptValue.Obj eo) || !(eo.instance() instanceof LivingEntity target))
                    return ScriptValue.of(false);
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.of(false);
                ItemStack stack = worldly.getItem(slot);
                if (stack.isEmpty()) return ScriptValue.of(false);
                // Interact: feed animal or generic mob interact
                if (target instanceof net.minecraft.world.entity.animal.Animal animal) {
                    net.minecraft.world.item.ItemStack foodCopy = stack.copy();
                    animal.setInLoveTime(600);
                    stack.shrink(1);
                    worldly.setItem(slot, stack);
                    return ScriptValue.of(true);
                }
                return ScriptValue.of(false);
            })
            // spawn_entity → use World.spawn_entity(type, x, y, z) instead

            // --- Methods: Inventory ---
            .method("get_item_in_slot", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                MachineRef m = ref(obj);
                int slot = (int) args.get(0).asNum();
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.NULL;
                ItemStack stack = worldly.getItem(slot);
                return stack.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(stack);
            })
            // Generic "this machine becomes that item" primitive — builds a fresh instance of the
            // named ItemDefinition (items/*.json), pre-filled with THIS machine's own container
            // contents, each page's items read from the SAME absolute container offset that page
            // occupies (see dev.arubik.craftengine.item.ItemStateData#buildFromContainer) so page 2's
            // items land back on page 2 instead of being compacted onto page 1 whenever page 1
            // wasn't completely full. ALSO copies fluid/gas tank amounts by name, and energy, into
            // any tank the target ItemDefinition declares with a matching kind (a machine's TypedKey
            // flags — Machine.set_flag/set_str_flag — are NOT copied: there is no equivalent generic
            // flag store on items yet, and flag names aren't enumerable from a MachineDefinition the
            // way tanks are, so there's nothing structured to copy automatically — a script that
            // needs specific flag data on the item must carry it over itself). Used by e.g. a
            // placeable-container item's on_break script to hand itself back with contents intact —
            // see scripts/backpack_storage.pf — but generic over any machine/item pair, not
            // backpack-specific.
            // to_item(item_id[, include_storage[, include_flags[, include_typed]]]) — the 3 optional
            // trailing bools mirror place_block's fill_storage/fill_flags/fill_typed on the OTHER
            // direction (item→machine), all defaulting to true so a bare to_item("id") keeps its old
            // full-copy behavior; pass false to leave that part of the built item untouched (e.g. a
            // "peek" item with no live storage, or a variant that shouldn't leak a locked flag).
            .method("to_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                dev.arubik.craftengine.item.ItemDefinition itemDef =
                        dev.arubik.craftengine.item.ItemDefinition.byId(
                                net.momirealms.craftengine.core.util.Key.of(args.get(0).asStr()));
                if (itemDef == null) return ScriptValue.NULL;
                boolean includeStorage = args.size() < 2 || args.get(1).asBool();
                boolean includeFlags = args.size() < 3 || args.get(2).asBool();
                boolean includeTyped = args.size() < 4 || args.get(3).asBool();
                MachineRef m = ref(obj);
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
                    // Bridge explicitly-declared flags (see ItemDefinition#bridgeFlags/#bridgeStrFlags)
                    // using the EXACT same TypedKey convention Machine.get_flag/set_flag use, so a
                    // flag set via script on the machine round-trips through the item and back.
                    if (includeFlags) {
                        for (String name : itemDef.bridgeFlags()) {
                            try {
                                Integer v = machine.get(dev.arubik.craftengine.util.TypedKey.of(
                                        "polyfills", "flag_" + name, dev.arubik.craftengine.util.NbtType.INTEGER));
                                if (v != null) built = dev.arubik.craftengine.item.ItemStateData.setFlag(built, name, v);
                            } catch (Throwable ignored) {}
                        }
                        for (String name : itemDef.bridgeStrFlags()) {
                            try {
                                String v = machine.get(dev.arubik.craftengine.util.TypedKey.of(
                                        "polyfills", "sflag_" + name, dev.arubik.craftengine.util.NbtType.STRING));
                                if (v != null) built = dev.arubik.craftengine.item.ItemStateData.setStrFlag(built, name, v);
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
            .method("drop_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                ItemStack toDrop;
                ScriptValue arg = args.get(0);
                if (arg instanceof ScriptValue.Item i) toDrop = i.stack().copy();
                else if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) toDrop = is.copy();
                else return ScriptValue.of(false);
                if (toDrop.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                ItemEntity spawned = new ItemEntity(m.level(), m.pos().getX() + 0.5,
                        m.pos().getY() + 0.5, m.pos().getZ() + 0.5, toDrop);
                m.level().addFreshEntity(spawned);
                return ScriptValue.of(true);
            })
            .method("remove_item_in_slot", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                int slot = (int) args.get(0).asNum();
                int count = (int) args.get(1).asNum();
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.of(false);
                ItemStack removed = worldly.removeItem(slot, count);
                return ScriptValue.of(!removed.isEmpty());
            })
            .method("push_item_to_inventory", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.of(false);
                ItemStack toInsert;
                ScriptValue arg = args.get(0);
                if (arg instanceof ScriptValue.Item i) {
                    toInsert = i.stack().copy();
                } else if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) {
                    toInsert = is.copy();
                } else {
                    return ScriptValue.of(false);
                }
                // Try to merge into existing stacks or empty slots
                for (int i = 0; i < worldly.getContainerSize(); i++) {
                    if (toInsert.isEmpty()) break;
                    ItemStack existing = worldly.getItem(i);
                    if (existing.isEmpty()) {
                        worldly.setItem(i, toInsert.copy());
                        toInsert.setCount(0);
                    } else if (ItemStack.isSameItemSameComponents(existing, toInsert)) {
                        int space = existing.getMaxStackSize() - existing.getCount();
                        int transfer = Math.min(space, toInsert.getCount());
                        if (transfer > 0) {
                            existing.grow(transfer);
                            toInsert.shrink(transfer);
                            worldly.setItem(i, existing);
                        }
                    }
                }
                return ScriptValue.of(toInsert.isEmpty());
            })
            // open_menu(Player) — opens this machine's normal menu. A machine whose interact_script
            // fully replaces on-right-click handling (see MachineBlockBehavior#useWithoutItem — once
            // an interact_script is declared, the menu no longer opens on its own) calls this itself
            // for the branch where it still wants the ordinary UI, e.g. a storage_block whose
            // interact_script also handles a shift-right-click "pick myself back up as an item"
            // gesture (see Machine.pickup_as_item) needs an explicit way to fall through to its menu.
            .method("open_menu", (obj, args) -> {
                if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Obj po)
                        || !(po.instance() instanceof net.minecraft.world.entity.player.Player p))
                    return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity amb)) return ScriptValue.of(false);
                if (!(p.getBukkitEntity() instanceof org.bukkit.entity.Player bukkitPlayer)) return ScriptValue.of(false);
                try {
                    amb.getMenu().open(bukkitPlayer);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .method("add_items", (obj, args) -> {
                // add_items(array) — push all items in array to inventory
                if (args.isEmpty()) return ScriptValue.of(false);
                ScriptValue arrVal = args.get(0);
                if (!(arrVal instanceof ScriptValue.Array arr)) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                for (ScriptValue elem : arr.elements()) {
                    List<ScriptValue> singleArg = List.of(elem);
                    PolyTypeRegistry.callMethod("Machine", obj, "push_item_to_inventory", singleArg);
                }
                return ScriptValue.of(true);
            })

            // --- Methods: Redstone ---
            // Shorthand for Machine.redstone.set(n). Kept because 17 shipped scripts use it.
            // Delegates to MachineRedstone so writer and reader can never disagree on the key.
            .method("emit_redstone", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                return ScriptValue.of(MachineRedstone.setOutput(
                        m.level(), m.pos(), m.blockEntity(), (int) args.get(0).asNum()));
            })

            // --- Methods: Flags (persistent int storage) ---
            .method("get_flag", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                MachineRef m = ref(obj);
                String name = args.get(0).asStr();
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(0);
                TypedKey<Integer> key = TypedKey.of("polyfills", "flag_" + name, NbtType.INTEGER);
                Integer val = be.get(key);
                return ScriptValue.of(val != null ? val : 0);
            })
            .method("set_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                String name = args.get(0).asStr();
                int value = (int) args.get(1).asNum();
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<Integer> key = TypedKey.of("polyfills", "flag_" + name, NbtType.INTEGER);
                be.set(key, value);
                return ScriptValue.of(true);
            })

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
            .method("io_get", (obj, args) -> {
                // io_get(type, mode, dir) -> bool. mode is "input" or "output".
                if (args.size() < 3) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                dev.arubik.craftengine.multiblock.IOConfiguration.IOType type = parseIoType(args.get(0).asStr());
                Direction dir = Direction.byName(args.get(2).asStr());
                if (type == null || dir == null) return ScriptValue.of(false);
                dev.arubik.craftengine.multiblock.IOConfiguration cfg = resolveLiveIOConfig(m);
                if (cfg == null) return ScriptValue.of(false);
                boolean input = "input".equalsIgnoreCase(args.get(1).asStr());
                return ScriptValue.of(input ? cfg.acceptsInput(type, dir) : cfg.providesOutput(type, dir));
            })
            .method("io_set", (obj, args) -> {
                // io_set(type, mode, dir, bool) -> bool (whether it applied)
                if (args.size() < 4) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                dev.arubik.craftengine.multiblock.IOConfiguration.IOType type = parseIoType(args.get(0).asStr());
                Direction dir = Direction.byName(args.get(2).asStr());
                if (type == null || dir == null) return ScriptValue.of(false);
                dev.arubik.craftengine.multiblock.IOConfiguration.Simple cfg = liveSimpleConfig(machine);
                boolean input = "input".equalsIgnoreCase(args.get(1).asStr());
                boolean on = args.get(3).asBool();
                if (input) {
                    if (on) cfg.addInput(type, dir); else cfg.removeInput(type, dir);
                } else {
                    if (on) cfg.addOutput(type, dir); else cfg.removeOutput(type, dir);
                }
                // The mutation above is pure data — nothing re-derives the connected-face mask on
                // its own. Recompute now (self + the touched neighbour) so a panel toggle shows up
                // immediately instead of waiting for an unrelated block update.
                recomputeMaskIfConnected(m);
                return ScriptValue.of(true);
            })
            // Manual trigger for the same recompute io_set already does automatically — useful after
            // a batch of io_set calls, or after something else (a script, an upgrade) changed this
            // block's IOConfiguration through a path that doesn't already call it.
            .method("recompute_io", (obj, args) -> {
                recomputeMaskIfConnected(ref(obj));
                return ScriptValue.of(true);
            })

            // Bridges a panel-driven pipe segment into its resource network engine. Called every
            // tick (cheap: a single map put) from the panel machine's own action_script — the
            // existing periodic-hook mechanism (see machines/item_pipe_panel.json's action_script),
            // not a bespoke ticker. No-ops on any block that isn't an item pipe.
            .method("pipe_register_network", (obj, args) -> {
                MachineRef m = ref(obj);
                try {
                    net.minecraft.world.level.block.state.BlockState state = m.level().getBlockState(m.pos());
                    var custom = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                            .getOptionalCustomBlockState(state);
                    if (custom.isEmpty()) return ScriptValue.of(false);
                    var beh = custom.get().behavior();
                    dev.arubik.craftengine.pipe.item.ItemPipeBehavior pipe = beh instanceof dev.arubik.craftengine.pipe.item.ItemPipeBehavior p
                            ? p
                            : beh.getFirst(dev.arubik.craftengine.pipe.item.ItemPipeBehavior.class);
                    if (pipe == null) return ScriptValue.of(false);
                    dev.arubik.craftengine.pipe.item.ItemEngine.registerSeed(m.pos(), pipe.transferPerTick());
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {
                    return ScriptValue.of(false);
                }
            })

            // --- Methods: Block breaking ---
            .method("tick_break", (obj, args) -> {
                // tick_break(block, speed) → Array of Item drops when fully broken, else NULL
                // Progressive break: stores progress in a flag, returns drops when complete
                if (args.size() < 2) return ScriptValue.NULL;
                MachineRef m = ref(obj);
                ScriptValue blockArg = args.get(0);
                int speed = (int) args.get(1).asNum();
                if (!(blockArg instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef blockRef))
                    return ScriptValue.NULL;
                BlockPos target = blockRef.pos();
                net.minecraft.world.level.block.state.BlockState bs = m.level().getBlockState(target);
                if (bs.isAir()) return ScriptValue.NULL;
                float hardness = bs.getDestroySpeed(m.level(), target);
                if (hardness < 0) return ScriptValue.NULL; // unbreakable
                String flagName = "_break_" + target.getX() + "_" + target.getY() + "_" + target.getZ();
                TypedKey<Integer> progressKey = TypedKey.of("polyfills", "flag" + flagName, NbtType.INTEGER);
                PersistentBlockEntity be = m.blockEntity();
                int progress = be != null ? (be.get(progressKey) != null ? be.get(progressKey) : 0) : 0;
                int required = Math.max(1, (int)(hardness * 10));
                progress += speed;
                if (progress >= required) {
                    // Break and drop
                    List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(bs, m.level(), target, m.level().getBlockEntity(target));
                    m.level().destroyBlock(target, false);
                    if (be != null) be.set(progressKey, 0);
                    List<ScriptValue> result = new ArrayList<>(drops.size());
                    for (ItemStack drop : drops) {
                        result.add(ScriptValue.ofItem(drop));
                    }
                    return new ScriptValue.Array(result);
                } else {
                    if (be != null) be.set(progressKey, progress);
                    // Send break animation
                    int stage = (int)((float)progress / required * 9);
                    m.level().destroyBlockProgress(m.pos().hashCode(), target, stage);
                    return ScriptValue.NULL;
                }
            })

            // --- Methods: Contraption stall ---
            // Machine.contraption — lazy-cached contraption reference. Reconnects by UUID on restart.
            .property("contraption", obj -> {
                var cl = ref(obj).getContraption();
                return cl != null ? dev.arubik.craftengine.script.types.world.ContraptionType.wrap(cl) : ScriptValue.NULL;
            })
            // Machine.has_contraption — true if contraption is live
            .property("has_contraption", obj -> ScriptValue.of(ref(obj).getContraption() != null))
            // Machine.bars — Bars type to read bar stats (fluid, progress, etc.)
            .property("bars", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity m)
                    return BarsType.wrap(m);
                return ScriptValue.NULL;
            })
            // Machine.recipes — Array of Recipe for this machine type
            .property("recipes", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return new ScriptValue.Array(java.util.List.of());
                java.util.List<ScriptValue> list = new java.util.ArrayList<>();
                for (var r : dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getRecipes(dm.getMachineIdPublic()))
                    list.add(RecipeType.wrap(r));
                return new ScriptValue.Array(list);
            })
            // Machine.working_recipe — current processing recipe or NULL
            .property("working_recipe", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.NULL;
                try { return RecipeType.wrap(dm.getCurrentRecipe()); } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            // Machine.matching_recipe — first recipe matching current inventory or NULL
            .property("matching_recipe", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.NULL;
                try { return RecipeType.wrap(dm.findMatchingRecipe()); } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            // Machine.connect_contraption() — force reconnect from str_flag UUID, returns contraption or NULL
            .method("connect_contraption", (obj, args) -> {
                ref(obj).invalidateContraptionCache();
                var cl = ref(obj).getContraption();
                return cl != null ? dev.arubik.craftengine.script.types.world.ContraptionType.wrap(cl) : ScriptValue.NULL;
            })
            .method("hold_contraption", (obj, args) -> {
                // Signals the contraption system to hold this machine still
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<Integer> key = TypedKey.of("polyfills", "stall_contraption", NbtType.INTEGER);
                be.set(key, 1);
                return ScriptValue.of(true);
            })
            .method("release_contraption", (obj, args) -> {
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<Integer> key = TypedKey.of("polyfills", "stall_contraption", NbtType.INTEGER);
                be.set(key, 0);
                return ScriptValue.of(true);
            })

            // --- Methods: RPM ---
            .method("report_su", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity machine)) return ScriptValue.of(false);
                float su = (float) args.get(0).asNum();
                // lastSuLoad is private — access via reportSuToNetwork which updates it internally
                // Report to kinetic network (positive = consume, negative = generate)
                machine.reportSuToNetwork(su);
                return ScriptValue.of(true);
            })
            .property("is_overstressed", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(dm.rpmNetworkId());
                return ScriptValue.of(net != null && net.isOverStressed());
            })
            .property("network_capacity", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(dm.rpmNetworkId());
                return ScriptValue.of(net != null ? net.totalCapacity() : 0.0);
            })
            .property("network_stress", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (!(be instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(dm.rpmNetworkId());
                return ScriptValue.of(net != null ? net.totalStress() : 0.0);
            })

            // --- Properties: redstone port ---
            // Returns a Redstone object (.input/.output/.powered/.set(n)/.on()/.off()).
            // It coerces numerically to the INPUT level, so the older `Machine.redstone > 0`
            // and `power = Machine.redstone` usages keep working unchanged.
            .property("redstone", obj -> RedstoneType.wrap(ref(obj)))
            .property("facing_dx", obj -> ScriptValue.of(ref(obj).facingDirection().getStepX()))
            .property("facing_dy", obj -> ScriptValue.of(ref(obj).facingDirection().getStepY()))
            .property("facing_dz", obj -> ScriptValue.of(ref(obj).facingDirection().getStepZ()))

            // --- Methods: String flags ---
            .method("get_str_flag", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                MachineRef m = ref(obj);
                String name = args.get(0).asStr();
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of("");
                TypedKey<String> key = TypedKey.of("polyfills", "sflag_" + name, NbtType.STRING);
                String val = be.get(key);
                return ScriptValue.of(val != null ? val : "");
            })
            .method("set_str_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                String name = args.get(0).asStr();
                String value = args.get(1).asStr();
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<String> key = TypedKey.of("polyfills", "sflag_" + name, NbtType.STRING);
                be.set(key, value);
                return ScriptValue.of(true);
            })

            // --- Methods: FULL item flags (identity + every data component: enchantments, custom
            // name, durability, ...) — NOT just an id string. A string id (get_str_flag) is enough
            // for "any oak log", but not for "specifically THIS enchanted diamond sword"; storing
            // the whole stack (minus count, which nothing here should care about) is what makes
            // that possible. Same "sflag_"-style per-name storage as get/set_str_flag, just holding
            // a base64-encoded serialized ItemStack instead of plain text — a generic primitive any
            // future feature (not only item_pipe_panel's filter) can reuse the same way. */
            .method("get_item_flag", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                MachineRef m = ref(obj);
                String name = args.get(0).asStr();
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.NULL;
                TypedKey<String> key = TypedKey.of("polyfills", "iflag_" + name, NbtType.STRING);
                String encoded = be.get(key);
                if (encoded == null || encoded.isEmpty()) return ScriptValue.NULL;
                try {
                    byte[] bytes = java.util.Base64.getDecoder().decode(encoded);
                    org.bukkit.inventory.ItemStack bukkit = org.bukkit.inventory.ItemStack.deserializeBytes(bytes);
                    if (bukkit == null || bukkit.getType().isAir()) return ScriptValue.NULL;
                    net.minecraft.world.item.ItemStack nms =
                            org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                    return ScriptValue.ofItem(nms);
                } catch (Throwable ignored) {
                    return ScriptValue.NULL;
                }
            })
            .method("set_item_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                String name = args.get(0).asStr();
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<String> key = TypedKey.of("polyfills", "iflag_" + name, NbtType.STRING);
                if (!(args.get(1) instanceof ScriptValue.Item itemVal) || itemVal.stack().isEmpty()) {
                    be.set(key, "");
                    return ScriptValue.of(true);
                }
                try {
                    org.bukkit.inventory.ItemStack bukkit =
                            org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemVal.stack());
                    byte[] bytes = bukkit.serializeAsBytes();
                    be.set(key, java.util.Base64.getEncoder().encodeToString(bytes));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {
                    return ScriptValue.of(false);
                }
            })
            .method("clear_item_flag", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<String> key = TypedKey.of("polyfills", "iflag_" + args.get(0).asStr(), NbtType.STRING);
                be.set(key, "");
                return ScriptValue.of(true);
            })

            // --- Generic TypedKey storage — the standardized primitive get_flag/get_str_flag/
            // get_item_flag each hardcode one shape for; get_typed(key, type) picks any registered
            // type by name ("int"/"double"/"bool"/"byte_array"/"item"/"vector"/a custom-registered
            // one/...) so scripts share ONE convention instead of every feature growing its own ad
            // hoc get_X_flag pair. See TypedKeyBridge — including how to add a brand-new type name.
            .method("get_typed", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.NULL;
                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.NULL;
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be == null) return codec.fromStorage(null);
                TypedKey<Object> key = TypedKey.of("polyfills", "tkey_" + args.get(0).asStr(), codec.storage());
                return codec.fromStorage(be.get(key));
            })
            .method("set_typed", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.of(false);
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<Object> key = TypedKey.of("polyfills", "tkey_" + args.get(0).asStr(), codec.storage());
                try {
                    be.set(key, codec.toStorage(args.get(2)));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .method("has_typed", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(args.get(1).asStr());
                if (codec == null) return ScriptValue.of(false);
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be == null) return ScriptValue.of(false);
                TypedKey<Object> key = TypedKey.of("polyfills", "tkey_" + args.get(0).asStr(), codec.storage());
                return ScriptValue.of(be.has(key));
            })

            // --- Methods: block_at(dx,dy,dz) ---
            .method("block_at", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                MachineRef m = ref(obj);
                int dx = (int) args.get(0).asNum(), dy = (int) args.get(1).asNum(), dz = (int) args.get(2).asNum();
                return BlockType.wrap(m.level(), m.pos().offset(dx, dy, dz));
            })
            // neighbor_block(dir) — the block adjacent to THIS machine in a named world direction
            // ("north"/"south"/"east"/"west"/"up"/"down"), same direction-name convention io_get/
            // io_set already use. Lets a pipe panel script ask "what's actually sitting on this
            // face" (see Block.is_container) instead of assuming every face is the same kind of
            // neighbour.
            .method("neighbor_block", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                MachineRef m = ref(obj);
                Direction dir = Direction.byName(args.get(0).asStr());
                if (dir == null) return ScriptValue.NULL;
                return BlockType.wrap(m.level(), m.pos().relative(dir));
            })

            // --- Methods: Inventory add shortcuts ---
            .method("add_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.of(false);
                ScriptValue arg = args.get(0);
                ItemStack toInsert;
                if (arg instanceof ScriptValue.Item i) toInsert = i.stack().copy();
                else if (arg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) toInsert = is.copy();
                else return ScriptValue.of(false);
                for (int i = 0; i < worldly.getContainerSize(); i++) {
                    if (toInsert.isEmpty()) break;
                    ItemStack existing = worldly.getItem(i);
                    if (existing.isEmpty()) { worldly.setItem(i, toInsert.copy()); toInsert.setCount(0); }
                    else if (ItemStack.isSameItemSameComponents(existing, toInsert)) {
                        int space = existing.getMaxStackSize() - existing.getCount();
                        int transfer = Math.min(space, toInsert.getCount());
                        if (transfer > 0) { existing.grow(transfer); toInsert.shrink(transfer); worldly.setItem(i, existing); }
                    }
                }
                return ScriptValue.of(toInsert.isEmpty());
            })
            .method("add_item_to_slot", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                int slot = (int) args.get(0).asNum();
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof PersistentWorldlyBlockEntity worldly)) return ScriptValue.of(false);
                ScriptValue itemArg = args.get(1);
                ItemStack toPlace;
                if (itemArg instanceof ScriptValue.Item i) toPlace = i.stack().copy();
                else if (itemArg instanceof ScriptValue.Str s) {
                    // itemId string — a CraftEngine custom id ("cml:xxx") first (same resolution
                    // order as MenuText#iconItem), THEN plain vanilla, so re-populating a slot from
                    // a saved filter entry works for either kind of id, not just vanilla materials.
                    String id = s.value();
                    int count = args.size() >= 3 ? (int) args.get(2).asNum() : 1;
                    ItemStack fromCe = null;
                    try {
                        net.momirealms.craftengine.core.util.Key key = net.momirealms.craftengine.core.util.Key.of(id);
                        var def = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(key);
                        if (def != null) {
                            org.bukkit.inventory.ItemStack bukkit = def.buildBukkitItem();
                            if (bukkit != null && bukkit.getType() != org.bukkit.Material.AIR) {
                                fromCe = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkit);
                                fromCe.setCount(count);
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                    if (fromCe != null) {
                        toPlace = fromCe;
                    } else {
                        var item = (net.minecraft.world.item.Item) net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.Identifier.parse(id.contains(":") ? id : "minecraft:" + id));
                        if (item == null) return ScriptValue.of(false);
                        toPlace = new ItemStack(item, count);
                    }
                } else return ScriptValue.of(false);
                if (slot < 0 || slot >= worldly.getContainerSize()) return ScriptValue.of(false);
                worldly.setItem(slot, toPlace);
                return ScriptValue.of(true);
            })



            // --- Overclock properties & methods ---
            // Machine.overclock → current overclock fraction (-1.0 to limit)
            .property("overclock", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.getOverclock());
            })
            // Machine.overclock_limit → current max overclock (from upgrades)
            .property("overclock_limit", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.getOverclockLimit());
            })
            // Machine.efficiency → current fuel efficiency fraction (from upgrades)
            .property("efficiency", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.getFuelEfficiency());
            })
            // Machine.generation → current generation bonus fraction (from upgrades)
            .property("generation", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(0.0);
                return ScriptValue.of(dm.getGeneration());
            })
            // Machine.meter_state → integer 0..72 mapped from overclock (-1..+limit) — for clock_N icon names
            .property("meter_state", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(36);
                int s = (int) Math.round((dm.getOverclock() + 1.0) / 5.0 * 72.0);
                return ScriptValue.of(Math.max(0, Math.min(72, s)));
            })
            // Machine.set_overclock(value) → set overclock fraction directly
            .method("set_overclock", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                dm.setOverclock((float) args.get(0).asNum());
                return ScriptValue.of(true);
            })
            // Machine.bump_overclock(delta) → bump overclock by delta (clamped)
            .method("bump_overclock", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm)) return ScriptValue.of(false);
                dm.addOverclock((float) args.get(0).asNum());
                return ScriptValue.of(true);
            })

            // --- Methods: RPM output / relay ---
            .method("set_rpm_output", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                try {
                    var dm = (dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity) machine;
                    float rpm = (float) args.get(0).asNum();
                    // Source motors must have a network to report SU — auto-create if missing
                    if (rpm != 0f && dm.rpmNetworkId() == 0L) {
                        var net = dev.arubik.craftengine.rotation.RpmNetwork.create();
                        dm.joinNetwork(net.id());
                    }
                    dm.setRpmSourceOutput(rpm);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // relay_to(block, rpm, network_id) — passes RPM from this machine to a neighbor machine.
            // - network_id: shared string ID (e.g. motor pos "x,y,z") so all machines in chain report SU to same network
            // - Parent tracking via str_flag "rpm_parent": first machine to relay to a target becomes its parent
            //   The target will NOT push back to its parent → no feedback loop
            // - Source distance prevents overwriting a better (closer to source) connection
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
            .property("layout", obj -> LayoutType.wrap(ref(obj)))
            // Machine.io -> the machine's IO port map, editable at runtime.
            // Lets a script build its IO on the fly for any type instead of it being frozen in the
            // machine JSON: a pipe whose faces the player configures, a machine that opens an
            // output only once a recipe finishes, and so on.
            .property("io", obj -> IoType.wrap(ref(obj)))
            .method("rpm_out", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm))
                    return ScriptValue.of(0);
                Direction face = resolveDirection(m, args.get(0).asStr());
                if (face == null) return ScriptValue.of(0);
                return ScriptValue.of(dm.rpmThrough(face));
            })
            // rpm_out_faces() -> every direction this machine currently drives, as a map of
            // direction name to the RPM delivered there. Handy for debugging a build.
            .method("rpm_out_faces", (obj, args) -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm))
                    return dev.arubik.craftengine.script.types.primitive.MapType.wrap(new java.util.LinkedHashMap<>());
                java.util.LinkedHashMap<String, ScriptValue> out = new java.util.LinkedHashMap<>();
                for (Direction d : Direction.values()) {
                    float v = dm.rpmThrough(d);
                    if (v != 0f) out.put(d.getName(), ScriptValue.of(v));
                }
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(out);
            })
            .method("relay_to", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                ScriptValue bv = args.get(0);
                if (!(bv instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef r)) return ScriptValue.of(false);
                float rpm = (float) args.get(1).asNum();
                MachineRef m = ref(obj);
                try {
                    net.momirealms.craftengine.core.block.entity.BlockEntity nbe =
                        dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
                    if (nbe == null || nbe.controller == null) return ScriptValue.of(false);
                    if (!(nbe.controller instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity neighbor)) return ScriptValue.of(false);
                    if (!(m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity myMachine)) return ScriptValue.of(false);

                    // Anti-loop: only push to machines farther from source
                    if (neighbor.sourceDistance() <= myMachine.sourceDistance()) return ScriptValue.of(false);

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
                        return ScriptValue.of(false);
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
                        return ScriptValue.of(false);   // we are running on a stale value ourselves
                    }
                    neighbor.acceptRelayedRpm(rpm, myMachine.sourceDistance(), stamp);
                    neighbor.setTheoreticalSpeed(rpm);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // relay_rpm removed — use relay_to() + set_rpm_output() in scripts instead
            .method("push_rpm", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                ScriptValue bv = args.get(0);
                if (!(bv instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef r)) return ScriptValue.of(false);
                float pushRpm = (float) args.get(1).asNum();
                try {
                    net.momirealms.craftengine.core.block.entity.BlockEntity be2 =
                        dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(r.level(), r.pos());
                    if (be2 != null && be2.controller instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity neighbor) {
                        neighbor.setRpmSourceOutput(pushRpm);
                        return ScriptValue.of(true);
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // gas_tanks → Array of Map{name, level, capacity, contents_name, color}
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
            .method("spawn_display", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                MachineRef m = ref(obj);
                String itemId = args.get(0).asStr();
                // pos offset relative to machine center
                double ox = 0, oy = 0.5, oz = 0;
                if (args.size() >= 2 && args.get(1) instanceof ScriptValue.Obj vo && vo.instance() instanceof org.joml.Vector3d v) {
                    ox = v.x; oy = v.y; oz = v.z;
                }
                float scale = args.size() >= 3 ? (float) args.get(2).asNum() : 1.0f;
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
            .method("update_display", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                String uuidStr = args.get(0).asStr();
                ScriptValue opts = args.get(1);
                try {
                    java.util.UUID id = java.util.UUID.fromString(uuidStr);
                    net.minecraft.world.entity.Entity e = m.level().getEntity(id);
                    if (e == null) return ScriptValue.of(false);
                    // Read pos from options map
                    if (opts instanceof ScriptValue.Obj ov) {
                        ScriptValue posVal = opts.getProperty("pos");
                        if (posVal instanceof ScriptValue.Obj pv && pv.instance() instanceof org.joml.Vector3d v) {
                            e.setPos(m.pos().getX() + 0.5 + v.x, m.pos().getY() + v.y, m.pos().getZ() + 0.5 + v.z);
                        }
                    }
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // kill_display(uuid_str) → bool
            .method("kill_display", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                try {
                    java.util.UUID id = java.util.UUID.fromString(args.get(0).asStr());
                    net.minecraft.world.entity.Entity e = m.level().getEntity(id);
                    if (e != null) { e.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED); return ScriptValue.of(true); }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // play_animation(anim) — attach a ScriptAnimation to this machine's tick cycle
            .method("play_animation", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                ScriptValue animVal = args.get(0);
                if (!(animVal instanceof ScriptValue.Obj ao) ||
                    !(ao.instance() instanceof dev.arubik.craftengine.machine.render.ScriptAnimation anim))
                    return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (m.blockEntity() instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm) {
                    dm.setActiveAnimation(anim);
                    return ScriptValue.of(true);
                }
                return ScriptValue.of(false);
            })
            // fluid_tanks → Array of Map{name, level, capacity, contents_name, color}
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
            .method("get_gas_level", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                String tankName = args.get(0).asStr();
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(0);
                try {
                    for (dev.arubik.craftengine.gas.GasTank tank : machine.gasTankList()) {
                        if (tank.getName().equals(tankName)) {
                            dev.arubik.craftengine.gas.GasStack gas = tank.getGas(m.level(), m.pos());
                            return ScriptValue.of(gas.getAmount());
                        }
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(0);
            })
            // consume_gas(tankName, amount) → bool
            .method("consume_gas", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                String tankName = args.get(0).asStr();
                int amount = (int) args.get(1).asNum();
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                try {
                    for (dev.arubik.craftengine.gas.GasTank tank : machine.gasTankList()) {
                        if (tank.getName().equals(tankName)) {
                            tank.extract(m.level(), m.pos(), amount, null);
                            return ScriptValue.of(true);
                        }
                    }
                } catch (Throwable ignored) {}
                return ScriptValue.of(false);
            })
            // --- Properties/methods: live energy tuning ---
            // The JSON "energy" block is just a starting point; a script (e.g. a windmill scaling
            // its output with wind/height, or a reactor throttling under overload) needs to change
            // capacity/rate live. Reads/writes the SAME fields insertEnergy/extractEnergy/processTick
            // already use — no shadow state to fall out of sync.
            .property("energy_stored", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(0);
                return ScriptValue.of(machine.getStoredEnergyForCarrier());
            })
            // consume_energy(amount) — an internal cost for a script-driven ACTION (a teleport, an
            // ability, ...), not a transfer to/from a neighbour, so it deliberately bypasses the
            // IOConfiguration gating insertEnergy/extractEnergy enforce (setStoredEnergyRaw, the
            // same "engine apply" path the network solvers use). Returns false — and takes
            // nothing — if the buffer doesn't have enough; never partially consumes.
            .method("consume_energy", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                int amount = (int) args.get(0).asNum();
                if (amount <= 0 || machine.getStoredEnergyForCarrier() < amount) return ScriptValue.of(false);
                machine.setStoredEnergyRaw(m.level(), machine.getStoredEnergyForCarrier() - amount);
                return ScriptValue.of(true);
            })
            .property("energy_capacity", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(0);
                return ScriptValue.of(machine.energyCapacity());
            })
            .property("energy_max_input", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(0);
                return ScriptValue.of(machine.energyMaxInput());
            })
            .property("energy_max_output", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(0);
                return ScriptValue.of(machine.energyMaxOutput());
            })
            .property("energy_per_tick", obj -> {
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(0);
                return ScriptValue.of(machine.energyPerTick());
            })
            // Measured, not configured: max_input/max_output/per_tick are CAPS this machine
            // declares — real_input/real_output are what the network ACTUALLY moved through this
            // node on its last EnergyEngine step (see EnergyEngine#lastDelta). Pull the generator
            // feeding this cell off the network and real_input drops to 0 next tick even though
            // max_input/capacity haven't changed at all.
            .property("energy_real_input", obj -> {
                MachineRef m = ref(obj);
                int delta = dev.arubik.craftengine.fluid.graph.EnergyEngine.lastDelta(m.pos());
                return ScriptValue.of(Math.max(0, delta));
            })
            .property("energy_real_output", obj -> {
                MachineRef m = ref(obj);
                int delta = dev.arubik.craftengine.fluid.graph.EnergyEngine.lastDelta(m.pos());
                return ScriptValue.of(Math.max(0, -delta));
            })
            .method("set_energy_per_tick", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                machine.setEnergyPerTick((int) args.get(0).asNum());
                return ScriptValue.of(true);
            })
            .method("set_energy_max_input", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                machine.setEnergyMaxInput((int) args.get(0).asNum());
                return ScriptValue.of(true);
            })
            .method("set_energy_max_output", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                if (!(m.blockEntity() instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                machine.setEnergyMaxOutput((int) args.get(0).asNum());
                return ScriptValue.of(true);
            })

            // --- Methods: fill_fluid ---
            .method("fill_fluid", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (!(be instanceof AbstractMachineBlockEntity machine)) return ScriptValue.of(false);
                String fluidId = args.get(0).asStr();
                int amount = args.size() >= 2 ? (int) args.get(1).asNum() : 0;
                try {
                    FluidType ft = FluidType.byName(fluidId);
                    if (ft == null) return ScriptValue.of(false);
                    FluidStack stack = new FluidStack(ft, amount);
                    return ScriptValue.of(machine.fillTank(m.level(), stack));
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })

            // --- Methods: blocks_in_range, break_block, place_block_at ---
            .method("blocks_in_range", (obj, args) -> {
                MachineRef m = ref(obj);
                int radius = args.isEmpty() ? 3 : (int) args.get(0).asNum();
                java.util.List<ScriptValue> blocks = new java.util.ArrayList<>();
                BlockPos center = m.pos();
                for (int dx = -radius; dx <= radius; dx++)
                    for (int dy = -radius; dy <= radius; dy++)
                        for (int dz = -radius; dz <= radius; dz++) {
                            BlockPos p = center.offset(dx, dy, dz);
                            if (!m.level().getBlockState(p).isAir())
                                blocks.add(BlockType.wrap(m.level(), p));
                        }
                return new ScriptValue.Array(blocks);
            })
            .method("break_block", (obj, args) -> {
                if (args.isEmpty()) return new ScriptValue.Array(java.util.List.of());
                MachineRef m = ref(obj);
                ScriptValue bv = args.get(0);
                if (!(bv instanceof ScriptValue.Obj bo) || !(bo.instance() instanceof BlockType.BlockRef ref)) return new ScriptValue.Array(java.util.List.of());
                net.minecraft.world.level.block.state.BlockState bs = ref.state();
                if (bs.isAir()) return new ScriptValue.Array(java.util.List.of());
                java.util.List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(bs, m.level(), ref.pos(), m.level().getBlockEntity(ref.pos()));
                m.level().destroyBlock(ref.pos(), false);
                java.util.List<ScriptValue> result = new java.util.ArrayList<>(drops.size());
                for (ItemStack d : drops) result.add(ScriptValue.ofItem(d));
                return new ScriptValue.Array(result);
            })
            .method("place_block_at", (obj, args) -> {
                // place_block_at(dx, dy, dz, item) — place block from item at relative offset
                if (args.size() < 4) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                int dx = (int) args.get(0).asNum(), dy = (int) args.get(1).asNum(), dz = (int) args.get(2).asNum();
                BlockPos target = m.pos().offset(dx, dy, dz);
                ScriptValue itemArg = args.get(3);
                ItemStack stack = null;
                if (itemArg instanceof ScriptValue.Item i) stack = i.stack();
                else if (itemArg instanceof ScriptValue.Obj o && o.instance() instanceof ItemStack is) stack = is;
                if (stack == null || stack.isEmpty()) return ScriptValue.of(false);
                net.minecraft.world.level.block.state.BlockState existing = m.level().getBlockState(target);
                if (!existing.isAir()) return ScriptValue.of(false);
                if (!(stack.getItem() instanceof net.minecraft.world.item.BlockItem bi)) return ScriptValue.of(false);
                m.level().setBlock(target, bi.getBlock().defaultBlockState(), 3);
                stack.shrink(1);
                return ScriptValue.of(true);
            })

            // damage_entity/fire_entity/freeze_entity/apply_bone_meal removed — use entity.damage/fire/freeze and block.apply_bone_meal()
            .method("is_player_looking_at", (obj, args) -> {
                // Tests EVERY player in range, not just the nearest one: with getNearestPlayer a
                // bystander standing closer than the person actually looking suppressed the signal.
                MachineRef m = ref(obj);
                double dist = args.isEmpty() ? 8 : args.get(0).asNum();
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
                    if (toBlock.subtract(look.scale(dot)).length() < 1.0) return ScriptValue.of(true);
                }
                return ScriptValue.of(false);
            })
            .property("owner_uuid", obj -> {
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.NULL;
                TypedKey<String> key = TypedKey.of("polyfills", "machine_owner_uuid", NbtType.STRING);
                String val = be.get(key);
                return val != null ? ScriptValue.of(val) : ScriptValue.NULL;
            })
            .method("owner_uuid", (obj, args) -> {
                MachineRef m = ref(obj);
                PersistentBlockEntity be = m.blockEntity();
                if (be == null) return ScriptValue.NULL;
                TypedKey<String> key = TypedKey.of("polyfills", "machine_owner_uuid", NbtType.STRING);
                String val = be.get(key);
                return val != null ? ScriptValue.of(val) : ScriptValue.NULL;
            })
            // item_attack_damage removed — use item.attack_damage property

            // set_block/create_item removed — use World.set_block(x,y,z,id) and create_item(id,count) builtin

            // --- Methods: Player proximity ---
            .method("player_in_range", (obj, args) -> {
                MachineRef m = ref(obj);
                double dist = args.isEmpty() ? 8 : args.get(0).asNum();
                Vec3 center = Vec3.atCenterOf(m.pos());
                var player = m.level().getNearestPlayer(center.x, center.y, center.z, dist, false);
                return ScriptValue.of(player != null);
            })
            .method("player_facing", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                MachineRef m = ref(obj);
                String face = args.get(0).asStr();
                Vec3 center = Vec3.atCenterOf(m.pos());
                var player = m.level().getNearestPlayer(center.x, center.y, center.z, 32, false);
                if (player == null) return ScriptValue.of(false);
                Direction playerDir = Direction.orderedByNearest(player)[0];
                return ScriptValue.of(playerDir.getName().equalsIgnoreCase(face));
            })
            // close() — closes the nearest viewer's open inventory (same @p-style nearest-player
            // heuristic as player_in_range/player_facing, since a script method has no direct
            // handle on "whoever has this machine's GUI open"). Meant for scripts like
            // teleporter.pf's do_teleport() to close the GUI before moving the player elsewhere.
            .method("close", (obj, args) -> {
                MachineRef m = ref(obj);
                Vec3 center = Vec3.atCenterOf(m.pos());
                var player = m.level().getNearestPlayer(center.x, center.y, center.z, 8, false);
                if (player != null) player.closeContainer();
                return ScriptValue.of(player != null);
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
            .method("update", (obj, args) -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity m) {
                    return ScriptValue.of(m.rebuildAndReopenMenu());
                }
                return ScriptValue.of(false);
            })
            // rename_text — the current text typed into an ANVIL-type page's rename field (a
            // page declared with "size": "anvil" — see MachineDefinition.PageDef#inventoryType).
            // Bukkit's AnvilInventory#getRenameText() works on this inventory even though no real
            // anvil block backs it, so a machine can read whatever the player is typing live
            // (e.g. to price a custom repair/combine recipe by name) without needing a real anvil
            // recipe to be registered. "" if nobody has an anvil-type page of this machine open,
            // or the open page isn't an anvil.
            .property("rename_text", obj -> {
                PersistentBlockEntity be = ref(obj).blockEntity();
                if (be instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity m) {
                    var openMenu = m.getOpenMenuOrNull();
                    if (openMenu != null && openMenu.getInventory() instanceof org.bukkit.inventory.AnvilInventory anvil) {
                        String text = anvil.getRenameText();
                        return ScriptValue.of(text != null ? text : "");
                    }
                }
                return ScriptValue.of("");
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
