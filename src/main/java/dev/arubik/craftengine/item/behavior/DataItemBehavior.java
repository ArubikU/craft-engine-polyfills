package dev.arubik.craftengine.item.behavior;

import java.nio.file.Path;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.arubik.craftengine.item.ItemDefinition;
import dev.arubik.craftengine.item.ItemStateData;
import dev.arubik.craftengine.item.menu.ItemMenu;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.UpdateFlags;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.item.behavior.ItemBehavior;
import net.momirealms.craftengine.core.item.behavior.ItemBehaviorFactory;
import net.momirealms.craftengine.core.pack.Pack;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * The item-side analog of {@code DataMachineBehavior}: ONE generic {@link ItemBehavior},
 * registered under {@code polyfills:data_item}, that every {@code items/*.json}-defined item
 * reuses. Its factory resolves the item's own {@link ItemDefinition} BY ID at construction, but
 * deliberately does not cache the resolved object — {@link #definition()} looks it up fresh from
 * {@link ItemDefinition#byId(Key)} every call, so a {@code /cep reload items} that re-registers the
 * definition takes effect on every already-placed/held item immediately, no separate "refresh all
 * live instances" step needed (unlike the machine side's {@code DataMachineBlockEntity#refreshDefinitions()}).
 * Every {@link ExtendedItemBehavior} hook dispatches to that definition's script refs, its backpack
 * container, or its placed-block gesture.
 */
public class DataItemBehavior extends ExtendedItemBehavior {

    private final Key id;

    public DataItemBehavior(Key id) {
        this.id = id;
    }

    /** Resolved fresh from the registry every call — see class javadoc. */
    public ItemDefinition definition() { return ItemDefinition.byId(id); }

    // ------------------------------------------------------------- right click

    @Override
    public ItemStack onRightClick(ItemStack stack, Object... args) {
        ItemDefinition definition = definition();
        if (definition == null) return stack;
        PlayerInteractEvent event = findEvent(args);
        Player player = event != null ? event.getPlayer() : findBukkitPlayer(args);

        boolean hasMenu = definition.hasMenu();
        boolean sneaking = player != null && player.isSneaking();

        if (hasMenu && sneaking && definition.placeBlock() != null && event != null) {
            ItemStack placed = tryPlaceContainerBlock(definition, stack, event);
            if (placed != null) return placed;
        }

        if (hasMenu && ((sneaking && definition.openOnShiftRightClick())
                || (!sneaking && definition.openOnRightClick()))) {
            if (player != null) {
                ItemMenu.open(player, stack, definition);
                if (event != null) event.setCancelled(true);
            }
            return stack;
        }

        return runHook(definition, "on_right_click", stack, args);
    }

    /**
     * Shift-right-click on a block: place {@code place_block}, spill the backpack's contents
     * into it if it turns out to be a real machine, then run {@code on_place_block}.
     *
     * <p><b>Known gap:</b> this calls the same low-level {@code CraftEngineBlocks.place(...)}
     * primitive used elsewhere in this codebase (pipes, conveyors, chain anchors) rather than
     * going through CraftEngine's real {@code BlockItemBehavior} placement pipeline the way a
     * normal block-item does. A real {@link BlockPlaceEvent} IS fired below so protection plugins
     * (WorldGuard, etc.) get a say, but it is NOT verified whether {@code CraftEngineBlocks.place}
     * also invokes the placed block's own {@code setPlacedBy} hook — which is where a celled
     * multi-block (see {@code MultiCellGeometry}/{@code CelledDataMachineBehavior}) auto-places its
     * other cells. If {@code place_block} ever needs to point at a celled block, verify that first.
     */
    private ItemStack tryPlaceContainerBlock(ItemDefinition definition, ItemStack stack, PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        BlockFace face = event.getBlockFace();
        if (clicked == null || face == null) return null;
        Block target = clicked.getRelative(face);
        if (!target.getType().isAir() && !target.isReplaceable()) return null;

        BlockDefinition def = CraftEngineBlocks.byId(definition.placeBlock());
        if (def == null) return null;
        event.setCancelled(true);

        Location loc = target.getLocation();
        ImmutableBlockState state = def.defaultState();

        // A real BlockPlaceEvent so protection plugins (WorldGuard, Towny, ...) can see and cancel
        // this placement the same as any other block-item — CraftEngineBlocks.place itself does not
        // fire one for this manual-primitive placement path.
        org.bukkit.block.BlockState replacedState = target.getState();
        org.bukkit.block.BlockState fakeNewState = replacedState; // no Bukkit BlockData to preview here
        org.bukkit.event.block.BlockPlaceEvent placeEvent = new org.bukkit.event.block.BlockPlaceEvent(
                target, fakeNewState, clicked, stack.asBukkitMirror(), event.getPlayer(), true,
                event.getHand() != null ? event.getHand() : org.bukkit.inventory.EquipmentSlot.HAND);
        org.bukkit.Bukkit.getPluginManager().callEvent(placeEvent);
        if (!placeEvent.canBuild() || placeEvent.isCancelled()) return null;

        boolean placed;
        try {
            placed = CraftEngineBlocks.place(loc, state, UpdateFlags.UPDATE_ALL, false);
        } catch (Throwable t) {
            return null;
        }
        if (!placed) return null;

        ItemStack result = stack.copy();
        result.shrink(1);

        // Everything below needs the newly placed block's controller loaded, which
        // CraftEngineBlocks.place does not guarantee is true on the SAME tick — deferred exactly
        // like the on_place_block script dispatch always was.
        net.minecraft.world.item.ItemStack itemSnapshot = stack.copy();
        net.minecraft.server.level.ServerPlayer nmsPlayer =
                ((org.bukkit.craftbukkit.entity.CraftPlayer) event.getPlayer()).getHandle();
        net.minecraft.server.level.ServerLevel nmsLevel = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.core.BlockPos nmsPos =
                new net.minecraft.core.BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        String ref = definition.script("on_place_block");

        org.bukkit.Bukkit.getScheduler().runTask(
                dev.arubik.craftengine.CraftEnginePolyfills.instance(), () -> {
            fillMachineContainer(nmsLevel, nmsPos, definition, itemSnapshot, nmsPlayer);
            if (ref == null) return;
            ScriptCall call = ScriptCall.parse(ref);
            if (call == null) return;
            try {
                ScriptContext ctx = ScriptContext.builder()
                        .item("item", itemSnapshot)
                        .player(nmsPlayer)
                        .world(nmsLevel)
                        .block(nmsLevel, nmsPos)
                        .machineAt(nmsLevel, nmsPos)
                        // The block is already placed by the time this fires, so cancelling has
                        // nothing left to veto — bound anyway for a consistent event shape.
                        .event(new dev.arubik.craftengine.script.event.ItemActionEvent("on_place_block"))
                        .build();
                call.execute(ctx);
            } catch (Throwable ignored) {}
        });
        return result;
    }

    /** Writes {@code stack}'s page storage into the machine block entity's own NMS container at
     *  {@code pos} (its inherited {@code getContainerSize()}/{@code setItem} — CraftEngine machine
     *  entities are NMS {@code Container}s, never vanilla Bukkit {@code org.bukkit.block.Container}s,
     *  so that cast never matches a CraftEngine block), each page written at the SAME absolute
     *  container offset a machine built from the identical page schema would use — see
     *  {@link ItemStateData#writeToContainer} — so page 2's items land in page 2's container slots
     *  instead of being compacted into page 1's whenever page 1 wasn't completely full.
     *
     *  <p>Also copies the item's {@code energy}-kind tank (unambiguous — energy has no chemical
     *  identity, just a scalar) into the machine's own energy buffer. Fluid/gas tanks are NOT
     *  copied here: an item tank is just a name + amount with no fluid/gas TYPE recorded (see
     *  {@link ItemDefinition.TankSpec}), so there is nothing to construct a {@code FluidStack}/
     *  {@code GasStack} from — the round-trip inverse of {@code Machine#to_item}, which CAN copy
     *  those because the source machine tank already knows its own type.
     *
     *  <p>Each bridge step is gated by its own {@code place_block} toggle ({@link ItemDefinition
     *  #fillStorage()}/{@link ItemDefinition#fillFlags()}/{@link ItemDefinition#fillTyped()}), a
     *  constant or a {@code "script.pf:func[:args]"} condition evaluated here against a context
     *  binding {@code item} (this stack), {@code Player} (if known), and {@code Machine} (the
     *  freshly placed block) — so e.g. a locked backpack can refuse to spill its own storage. */
    private static void fillMachineContainer(net.minecraft.server.level.ServerLevel level,
            net.minecraft.core.BlockPos pos, ItemDefinition definition, net.minecraft.world.item.ItemStack stack,
            net.minecraft.server.level.ServerPlayer player) {
        BlockEntity be = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be == null || !(be.controller instanceof AbstractMachineBlockEntity machine)) return;

        ScriptContext toggleCtx = ScriptContext.builder().item("item", stack).player(player)
                .machineAt(level, pos).build();

        if (definition.fillStorage().evaluate(toggleCtx)) {
            ItemStateData.writeToContainer(stack, definition, machine::setItem, machine.getContainerSize());
            for (ItemDefinition.TankSpec spec : definition.tanks()) {
                if (!spec.isEnergy()) continue;
                try {
                    int amount = ItemStateData.tankAmount(stack, spec.name());
                    if (amount > 0) machine.setStoredEnergyRaw(level, amount);
                } catch (Throwable ignored) {}
            }
        }
        if (definition.fillFlags().evaluate(toggleCtx)) {
            for (String name : definition.bridgeFlags()) {
                try {
                    int value = ItemStateData.getFlag(stack, name);
                    machine.set(dev.arubik.craftengine.util.TypedKey.of(
                            "polyfills", "flag_" + name, dev.arubik.craftengine.util.NbtType.INTEGER), value);
                } catch (Throwable ignored) {}
            }
            for (String name : definition.bridgeStrFlags()) {
                try {
                    String value = ItemStateData.getStrFlag(stack, name);
                    machine.set(dev.arubik.craftengine.util.TypedKey.of(
                            "polyfills", "sflag_" + name, dev.arubik.craftengine.util.NbtType.STRING), value);
                } catch (Throwable ignored) {}
            }
        }
        if (definition.fillTyped().evaluate(toggleCtx)) {
            for (ItemDefinition.TypedBridgeSpec spec : definition.bridgeTyped()) {
                try {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec =
                            dev.arubik.craftengine.script.TypedKeyBridge.resolve(spec.type());
                    if (codec == null) continue;
                    Object value = dev.arubik.craftengine.script.types.primitive.ItemType
                            .readTypedRaw(stack, spec.name(), codec.storage());
                    if (value == null) continue;
                    dev.arubik.craftengine.util.TypedKey<Object> key = dev.arubik.craftengine.util.TypedKey.of(
                            "polyfills", "tkey_" + spec.name(), codec.storage());
                    machine.set(key, value);
                } catch (Throwable ignored) {}
            }
        }
    }

    // ------------------------------------------------------------- other hooks

    @Override
    public ItemStack onLeftClick(ItemStack stack, Object... args) {
        return runHook(definition(), "on_left_click", stack, args);
    }

    @Override
    public ItemStack onUse(ItemStack stack, Object... args) {
        return runHook(definition(), "on_use", stack, args);
    }

    @Override
    public ItemStack onConsume(ItemStack stack, Object... args) {
        return runHook(definition(), "on_consume", stack, args);
    }

    @Override
    public ItemStack onBreakBlock(ItemStack stack, Object... args) {
        return runHook(definition(), "on_break_block", stack, args);
    }

    @Override
    public ItemStack onPlaceBlock(ItemStack stack, Object... args) {
        return runHook(definition(), "on_place_block", stack, args);
    }

    @Override
    public ItemStack onEquip(ItemStack stack, Object... args) {
        return runHook(definition(), "on_equip", stack, args);
    }

    @Override
    public ItemStack onUnequip(ItemStack stack, Object... args) {
        return runHook(definition(), "on_unequip", stack, args);
    }

    @Override
    public ItemStack onAttackEntity(ItemStack stack, Object... args) {
        return runHook(definition(), "on_attack_entity", stack, args);
    }

    @Override
    public ItemStack onInteractEntity(ItemStack stack, Object... args) {
        return runHook(definition(), "on_interact_entity", stack, args);
    }

    @Override
    public ItemStack onDrop(ItemStack stack, Object... args) {
        return runHook(definition(), "on_drop", stack, args);
    }

    @Override
    public ItemStack onPickup(ItemStack stack, Object... args) {
        return runHook(definition(), "on_pickup", stack, args);
    }

    @Override
    public ItemStack onDamageTaken(ItemStack stack, Object... args) {
        return runHook(definition(), "on_damage_taken", stack, args);
    }

    @Override
    public ItemStack onSlotChange(ItemStack stack, Object... args) {
        return runHook(definition(), "on_slot_change", stack, args);
    }

    @Override
    public ItemStack onDeath(ItemStack stack, Object... args) {
        return runHook(definition(), "on_death", stack, args);
    }

    /** Fired for every rendered copy of this item packet-side (inventory slots, cursor, equipment
     *  — see {@code ItemListener.ItemPacketHandler}), potentially several times a tick per viewer;
     *  keep {@code on_render} scripts cheap. Binds a dedicated {@link dev.arubik.craftengine.script.event.RenderEvent}
     *  (holder/slot as real properties) instead of going through {@link #runHook}'s generic
     *  {@code ItemActionEvent} — holder isn't a "target" the way every other hook's other-entity
     *  is, and there's no real Bukkit event here to apply a cancel back onto. */
    @Override
    public ItemStack onRender(net.minecraft.world.entity.Entity holder, ItemStack stack, int slot) {
        ItemDefinition definition = definition();
        if (definition == null) return stack;
        String ref = definition.script("on_render");
        if (ref == null) return stack;
        ScriptCall call = ScriptCall.parse(ref);
        if (call == null) return stack;
        try {
            ScriptContext.Builder builder = ScriptContext.builder().item("item", stack)
                    .event(new dev.arubik.craftengine.script.event.RenderEvent(holder, slot));
            if (holder instanceof ServerPlayer sp) builder.player(sp);
            ScriptContext result = call.execute(builder.build());
            ScriptValue iv = result.getVar("item");
            if (iv instanceof ScriptValue.Item itemVal && itemVal.stack() != null) return itemVal.stack();
        } catch (Throwable ignored) {}
        return stack;
    }

    /** A bow/crossbow shot or a thrown item (ender pearl, snowball, egg, potion, hand-thrown
     *  trident) — see {@code ItemListener#onShootBow}/{@code #onProjectileLaunch}. */
    @Override
    public ItemStack onShot(ItemStack stack, Object... args) {
        return runHook(definition(), "on_shot", stack, args);
    }

    // ---------------------------------------------------------------- script dispatch

    /** Runs {@code definition}'s script ref for {@code eventKey} (if any) with an {@code item}
     *  (and, when available, {@code Player} class) bound context, plus an {@code event}
     *  ({@link dev.arubik.craftengine.script.event.ItemActionEvent}) the script can
     *  {@code event.cancel()} — applied back onto the real Bukkit event backing this hook (found in
     *  {@code args}, see {@code ItemListener#callBehavior}) when it implements {@code Cancellable},
     *  giving hooks like {@code on_drop}/{@code on_break_block}/{@code on_pickup} a real veto for
     *  the first time (previously the only effect a script could have was mutating {@code item}).
     *  Applies back whatever the script left in {@code item} either way — mirrors how
     *  {@code Machine.*} scripts mutate machine state through {@code ScriptContext}. */
    private ItemStack runHook(ItemDefinition definition, String eventKey, ItemStack stack, Object[] args) {
        if (definition == null) return stack;
        String ref = definition.script(eventKey);
        if (ref == null) return stack;
        ScriptCall call = ScriptCall.parse(ref);
        if (call == null) return stack;

        ServerPlayer player = findServerPlayer(args);
        org.bukkit.event.Event bukkitEvent = findBukkitEvent(args);
        dev.arubik.craftengine.script.event.ItemActionEvent scriptEvent =
                new dev.arubik.craftengine.script.event.ItemActionEvent(eventKey);
        // Generic extraction: any Entity in args OTHER than the holder is the attack/interact
        // target or the drop/pickup's item entity; any Double is the on_damage_taken amount.
        for (Object a : args) {
            if (a instanceof net.minecraft.world.entity.Entity e && !e.equals(player)) {
                scriptEvent.otherEntity(e);
            } else if (a instanceof Double d) {
                scriptEvent.amount(d);
            }
        }
        ScriptContext.Builder builder = ScriptContext.builder().item("item", stack).event(scriptEvent);
        if (player != null) builder.player(player);
        ScriptContext ctx = builder.build();
        try {
            ScriptContext result = call.execute(ctx);
            if (scriptEvent.isCancelled() && bukkitEvent instanceof org.bukkit.event.Cancellable c) {
                c.setCancelled(true);
            }
            ScriptValue iv = result.getVar("item");
            if (iv instanceof ScriptValue.Item itemVal && itemVal.stack() != null) {
                return itemVal.stack();
            }
        } catch (Throwable ignored) {}
        return stack;
    }

    private static ServerPlayer findServerPlayer(Object[] args) {
        for (Object a : args) if (a instanceof ServerPlayer sp) return sp;
        return null;
    }

    private static org.bukkit.event.Event findBukkitEvent(Object[] args) {
        for (Object a : args) if (a instanceof org.bukkit.event.Event e) return e;
        return null;
    }

    private static PlayerInteractEvent findEvent(Object[] args) {
        for (Object a : args) if (a instanceof PlayerInteractEvent e) return e;
        return null;
    }

    private static Player findBukkitPlayer(Object[] args) {
        for (Object a : args) {
            if (a instanceof net.minecraft.world.entity.Entity e) {
                Object bukkit = e.getBukkitEntity();
                if (bukkit instanceof Player p) return p;
            }
        }
        return null;
    }

    public static class Factory implements ItemBehaviorFactory<ItemBehavior> {
        @Override
        public ItemBehavior create(Pack pack, Path path, Key id, ConfigSection arguments) {
            return new DataItemBehavior(id);
        }
    }

    public static final Factory FACTORY = new Factory();
}
