package dev.arubik.craftengine.rotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.inventory.ClickType;
import dev.arubik.craftengine.util.NbtType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.util.TypedKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.util.Key;

/**
 * Advanced, upgradeable, multi-gas vapor motor.
 *
 * <ul>
 *   <li><b>Multi-gas (one tank):</b> config map {@link GasType} → (rpm, su, gas/tick). Whatever
 *       supported gas is in the tank drives the motor; unsupported gases do nothing.</li>
 *   <li><b>Manual RPM/SU:</b> the player tunes the target RPM and SU directly (− / + buttons).
 *       "Overclock" is only the headroom: the max you can set = gasBase × 2 × (1 + overclock_limit
 *       upgrade). Gas demand = {@code gasPerTick × (rpm/base) × (su/base) × (1−consume)} so pushing
 *       both costs much more gas.</li>
 *   <li><b>Upgrades:</b> a 9-slot upgrade page (3 effective by default). Config maps item → modifier:
 *       {@code extra_slots} (more effective slots), {@code overclock_limit}, {@code consume},
 *       {@code generation}.</li>
 * </ul>
 */
public class DataMotorBlockEntity extends AbstractMachineBlockEntity
        implements RpmProvider, dev.arubik.craftengine.machine.render.ModelRendersDriven {

    public record GasSpec(float rpm, float su, int gasPerTick) {
    }

    public static final int UPGRADE_SLOTS = 9;
    public static final int BASE_UNLOCKED = 3;
    /** Overclock headroom over a gas's base before any upgrade (2x = the requested 32→64). */
    private static final float BASE_OC = 2.0f;

    /**
     * This motor's definition from {@code motors/*.json}.
     *
     * <p>
     * The grid size and overclock headroom were constants; they come from data now so
     * a second motor needs no second class. Falls back to the historical values when
     * no definition is present.
     */
    private final MotorDefinition definition;
    private dev.arubik.craftengine.machine.render.RendererManager rendererManager;

    private MotorDefinition motorDefinition() {
        return definition;
    }

    private MachineDefinition machineDefinition() {
        return definition == null ? null : definition.machine();
    }

    private int upgradeSlotCount() {
        var d = motorDefinition();
        return d != null ? d.upgradeSlots() : UPGRADE_SLOTS;
    }

    private int baseUnlockedCount() {
        var d = motorDefinition();
        return d != null ? d.baseUnlocked() : BASE_UNLOCKED;
    }

    private float baseOverclock() {
        var d = motorDefinition();
        return d != null ? d.baseOverclock() : BASE_OC;
    }


    private final int vaporCapacity;
    private final Map<GasType, GasSpec> gases;
    /** item id → its attribute modifiers (a single item may buff one attr, debuff another). */
    private final Map<Key, java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs;

    private float targetRpm = 32f;
    private float targetSu = 64f;
    private float currentRpm = 0f;
    private float potentialRpm = 0f; // rpm ignoring overstress, for stable network load sizing
    private boolean overstressed = false;
    private float currentStressCap = 0f;
    private float stressLoad = 0f;
    /** Last tick's gas figures for the UI. */
    private int lastDemand = 0;
    private int lastConsumed = 0;
    /** Stress (SU) the driven network reported last tick (for the UI). */
    private float lastStressLoad = 0f;

    private int page = 0; // 0 main, 1 upgrades, 2 overclock
    private int lastRenderedUnlocked = BASE_UNLOCKED;
    private MachineMenu active;

    private static final TypedKey<Float> KEY_RPM_T = TypedKey.of("craftengine", "adv_motor_trpm", NbtType.FLOAT);
    private static final TypedKey<Float> KEY_SU_T = TypedKey.of("craftengine", "adv_motor_tsu", NbtType.FLOAT);
    private static final TypedKey<Float> KEY_RPM = TypedKey.of("craftengine", "adv_motor_rpm", NbtType.FLOAT);

    public DataMotorBlockEntity(BlockEntity blockEntity, MotorDefinition definition, int vaporCapacity,
            Map<GasType, GasSpec> gases,
            Map<Key, java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs) {
        super(blockEntity, definition != null ? definition.upgradeSlots() : UPGRADE_SLOTS);
        this.definition = definition;
        this.vaporCapacity = vaporCapacity > 0 ? vaporCapacity : 10000;
        this.gases = (gases == null || gases.isEmpty()) ? defaultGases() : gases;
        this.upgradeDefs = upgradeDefs == null ? new HashMap<>() : upgradeDefs;

        addGasTank(new GasTank("vapor", this.vaporCapacity)); // null filter = accept any gas

        IOConfiguration.Simple config = new IOConfiguration.Simple();
        config.addInput(IOType.GAS, Direction.UP, Direction.DOWN); // vapor enters from top OR bottom
        config.setSlots(IOType.GAS, IOConfiguration.IORole.INPUT, 0);
        setIOConfiguration(config);

        GasSpec any = this.gases.values().iterator().next();
        this.targetRpm = any.rpm();
        this.targetSu = any.su();

        dev.arubik.craftengine.machine.MachineDefinition machineDef = machineDefinition();
        if (machineDef != null && !machineDef.renderers().isEmpty()) {
            this.rendererManager = new dev.arubik.craftengine.machine.render.RendererManager(
                    machineDef.renderers(), machineDef.variables());
        }
    }

    @Override
    public dev.arubik.craftengine.machine.render.RendererManager rendererManager() {
        return rendererManager;
    }

    private static Map<GasType, GasSpec> defaultGases() {
        Map<GasType, GasSpec> m = new HashMap<>();
        m.put(GasType.STEAM, new GasSpec(32f, 64f, 20));
        return m;
    }

    private static org.bukkit.inventory.ItemStack icon(org.bukkit.Material mat, String name, String... lore) {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta m = s.getItemMeta();
        m.setDisplayName(name);
        if (lore.length > 0)
            m.setLore(java.util.Arrays.asList(lore));
        s.setItemMeta(m);
        return s;
    }

    // ---- i18n component helpers (everything in the UI resolves client-side) ----
    private static Component noI(Component c) {
        return c.decoration(TextDecoration.ITALIC, false);
    }

    /** Translatable label. */
    private static Component tr(String key, NamedTextColor color) {
        return Component.translatable(key).color(color);
    }

    /** Plain literal (numbers / separators only). */
    private static Component lit(String s, NamedTextColor color) {
        return Component.text(s, color);
    }

    /** {@code <label>: <value>} as a single component. */
    private static Component kv(String key, NamedTextColor keyColor, String value, NamedTextColor valColor) {
        return tr(key, keyColor).append(lit(": " + value, valColor));
    }

    private static org.bukkit.inventory.ItemStack tIcon(org.bukkit.Material mat, Component name, Component... lore) {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta m = s.getItemMeta();
        m.displayName(noI(name));
        if (lore.length > 0) {
            java.util.List<Component> ls = new java.util.ArrayList<>();
            for (Component c : lore)
                ls.add(noI(c));
            m.lore(ls);
        }
        s.setItemMeta(m);
        return s;
    }

    // ---------------- upgrades ----------------
    private static final class Agg {
        int unlocked = BASE_UNLOCKED;
        double overclockLimit = 0;
        double consume = 0;
        double generation = 0;
    }

    private Key itemId(int slot) {
        net.minecraft.world.item.ItemStack nms = getItem(slot);
        if (nms == null || nms.isEmpty())
            return null;
        org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
        Key custom = CraftEngineItems.getCustomItemId(b);
        if (custom != null)
            return custom;
        org.bukkit.NamespacedKey nk = b.getType().getKey();
        return Key.of(nk.getNamespace(), nk.getKey());
    }

    private java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> modsOf(int slot) {
        Key id = itemId(slot);
        return id == null ? null : upgradeDefs.get(id);
    }

    /** True if the upgrade in {@code slot} contributes any EXTRA_SLOTS amount. */
    private boolean givesSlots(int slot) {
        var mods = modsOf(slot);
        if (mods == null)
            return false;
        for (var m : mods)
            if (m.attribute().equals(dev.arubik.craftengine.machine.attribute.MachineAttributes.EXTRA_SLOTS)
                    && m.amount() != 0)
                return true;
        return false;
    }

    private int extraSlotsOf(java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> mods) {
        if (mods == null)
            return 0;
        return (int) Math.round(dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(mods)
                .getOrDefault(dev.arubik.craftengine.machine.attribute.MachineAttributes.EXTRA_SLOTS, 0.0));
    }

    /** Freeze a slot-giver while a slot it unlocks is occupied (pulling it would orphan items). */
    @Override
    public boolean canTakeFromSlot(int slot) {
        if (slot < 0 || slot >= upgradeSlotCount() || !givesSlots(slot))
            return true;
        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> all = new java.util.ArrayList<>();
        for (int i = 0; i < upgradeSlotCount(); i++) {
            var mods = modsOf(i);
            if (mods != null)
                all.addAll(mods);
        }
        int newUnlocked = Math.max(baseUnlockedCount(), Math.min(upgradeSlotCount(),
                baseUnlockedCount() + extraSlotsOf(all) - extraSlotsOf(modsOf(slot))));
        int highestOccupied = -1;
        for (int i = 0; i < upgradeSlotCount(); i++) {
            net.minecraft.world.item.ItemStack it = getItem(i);
            if (it != null && !it.isEmpty())
                highestOccupied = i;
        }
        return highestOccupied < newUnlocked; // ok only if no occupied slot would lock
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private Agg aggregate() {
        var MA = dev.arubik.craftengine.machine.attribute.MachineAttributes.EXTRA_SLOTS;
        // Pass 1: how many slots are unlocked (EXTRA_SLOTS from ALL installed upgrades).
        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> all = new java.util.ArrayList<>();
        for (int i = 0; i < upgradeSlotCount(); i++) {
            var mods = modsOf(i);
            if (mods != null)
                all.addAll(mods);
        }
        int extra = (int) Math.round(dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(all)
                .getOrDefault(MA, 0.0));
        Agg a = new Agg();
        a.unlocked = Math.max(baseUnlockedCount(),
                Math.min(upgradeSlotCount(), baseUnlockedCount() + extra));
        // Pass 2: effect attributes only from upgrades in unlocked slots.
        java.util.List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> active = new java.util.ArrayList<>();
        for (int i = 0; i < a.unlocked; i++) {
            var mods = modsOf(i);
            if (mods != null)
                active.addAll(mods);
        }
        var attrs = dev.arubik.craftengine.machine.attribute.MachineAttributes.compute(active);
        // Loose safety bounds only so upgrades actually STACK. fuel_efficiency can go far
        // negative (stacked scrapped upgrades burn much more); positive capped just under
        // 100% so fuel never becomes free.
        a.overclockLimit = clamp(attrs.getOrDefault(
                dev.arubik.craftengine.machine.attribute.MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        a.consume = clamp(attrs.getOrDefault(
                dev.arubik.craftengine.machine.attribute.MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);
        a.generation = clamp(attrs.getOrDefault(
                dev.arubik.craftengine.machine.attribute.MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        return a;
    }

    /** The gas currently in the tank that the motor supports, or null. */
    private GasSpec activeSpec(Level level) {
        if (gasTanks.isEmpty())
            return null;
        GasStack g = gasTanks.get(0).getGas(level, getMachinePos());
        return (g != null && !g.isEmpty()) ? gases.get(g.getType()) : null;
    }

    /** Reference gas base for caps when the tank is empty (the first configured gas). */
    private GasSpec refSpec(Level level) {
        GasSpec a = activeSpec(level);
        return a != null ? a : gases.values().iterator().next();
    }

    private float ocFactor(Agg a) {
        return baseOverclock() * (1f + (float) a.overclockLimit);
    }

    private float rpmMax(Level level, Agg a) {
        return refSpec(level).rpm() * ocFactor(a);
    }

    private float suMax(Level level, Agg a) {
        return refSpec(level).su() * ocFactor(a);
    }

    private float overclockLimit() {
        return ocFactor(aggregate()) - 1f;
    }

    private float currentOverclock() {
        GasSpec spec = refSpec(getNMSLevel());
        if (spec == null || spec.rpm() <= 0f || spec.su() <= 0f)
            return 0f;
        float rpmFactor = targetRpm / spec.rpm();
        float suFactor = targetSu / spec.su();
        return Math.min(rpmFactor, suFactor) - 1f;
    }

    public void bumpOverclock(boolean up, ClickType click) {
        GasSpec spec = refSpec(getNMSLevel());
        if (spec == null)
            return;
        Agg a = aggregate();
        if (a.overclockLimit <= 0.0)
            return;
        float next = currentOverclock() + (up ? 1f : -1f)
                * dev.arubik.craftengine.machine.menu.OverclockMenu.step(click);
        next = (float) clamp(next, -0.99, overclockLimit());
        float factor = Math.max(0f, 1f + next);
        targetRpm = Math.max(0f, Math.min(rpmMax(getNMSLevel(), a), spec.rpm() * factor));
        targetSu = Math.max(0f, Math.min(suMax(getNMSLevel(), a), spec.su() * factor));
        setChanged();
    }

    // ---------------- tuning buttons ----------------
    private static int step(ClickType c) {
        if (c == ClickType.DROP || c == ClickType.CONTROL_DROP)
            return 32;
        if (c == ClickType.RIGHT)
            return 8;
        return 1;
    }

    private static boolean isShift(ClickType c) {
        return c == ClickType.SHIFT_LEFT || c == ClickType.SHIFT_RIGHT;
    }

    public void bumpRpm(boolean up, ClickType c) {
        Agg a = aggregate();
        float max = rpmMax(getNMSLevel(), a);
        if (isShift(c))
            targetRpm = up ? max : 0f;
        else
            targetRpm += up ? step(c) : -step(c);
        targetRpm = Math.max(0f, Math.min(max, targetRpm));
        setChanged();
    }

    public void bumpSu(boolean up, ClickType c) {
        Agg a = aggregate();
        float max = suMax(getNMSLevel(), a);
        if (isShift(c))
            targetSu = up ? max : 0f;
        else
            targetSu += up ? step(c) : -step(c);
        targetSu = Math.max(0f, Math.min(max, targetSu));
        setChanged();
    }

    /** Drop every installed upgrade item at the block (called on break). */
    public void dropUpgrades() {
        Level lvl = getNMSLevel();
        if (lvl == null)
            return;
        org.bukkit.World bw = lvl.getWorld();
        BlockPos pos = getMachinePos();
        for (int i = 0; i < upgradeSlotCount(); i++) {
            net.minecraft.world.item.ItemStack nms = getItem(i);
            if (nms == null || nms.isEmpty())
                continue;
            org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
            if (bw != null && b != null && !b.getType().isAir())
                bw.dropItem(new org.bukkit.Location(bw, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), b);
            setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
        }
        clear();
        setChanged();
    }

    public void depleteTank() {
        if (gasTanks.isEmpty())
            return;
        Level lvl = getNMSLevel();
        if (lvl == null)
            return;
        GasTank tank = gasTanks.get(0);
        GasStack g = tank.getGas(lvl, getMachinePos());
        if (g != null && !g.isEmpty())
            tank.extract(lvl, getMachinePos(), g.getAmount(), null);
        setChanged();
    }

    // ---------------- tick ----------------

    @Override
    public void tick(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick();
        if (level.isClientSide())
            return;

        // Pull vapor from a connected gas pipe/tank on the GAS input face (UP) — pressureless steam
        // won't be pushed DOWN into us by the pipe, so WE pull it in each tick.
        pullFromInputFaces(level);

        Agg a = aggregate();
        // Keep targets within the (possibly upgrade-extended) caps.
        targetRpm = Math.max(0f, Math.min(rpmMax(level, a), targetRpm));
        targetSu = Math.max(0f, Math.min(suMax(level, a), targetSu));

        float produced = 0f;
        float capSu = 0f;
        int demand = 0;
        int consumed = 0;
        GasSpec spec = activeSpec(level);
        if (spec != null && targetRpm > 0f && targetSu > 0f) {
            GasTank tank = gasTanks.get(0);
            // Gas demand = base (spin at speed) + load (drive the WHOLE network's measured stress,
            // capped at the SU setpoint). Reading lastStressLoad makes the advanced motor burn more
            // gas under more tree load, exactly like the normal motor -- not a fixed setpoint cost.
            float loadSu = Math.min(this.lastStressLoad, targetSu);
            float baseGas = spec.gasPerTick() * (targetRpm / spec.rpm());
            float loadGas = spec.gasPerTick() * (loadSu / spec.su());
            demand = Math.max(1, Math.round((baseGas + loadGas) * (1f - (float) a.consume)));
            GasStack stored = tank.getGas(level, getMachinePos());
            int avail = stored == null ? 0 : stored.getAmount();
            if (avail > 0) {
                consumed = Math.min(demand, avail);
                tank.extract(level, getMachinePos(), consumed, null);
                float ratio = (float) consumed / (float) demand;
                float gen = 1f + (float) a.generation;
                produced = targetRpm * ratio * gen;
                capSu = targetSu * gen;
            }
        }
        this.lastDemand = demand;
        this.lastConsumed = consumed;

        // Whole-tree overstress: belts report their load every tick (sized from potentialRpm so it
        // stays stable even while stalled). If the accumulated network load from the PREVIOUS tick
        // exceeds our capacity, stall the output (rpm 0) but keep potentialRpm so the load stays
        // reported -> the motor stays latched off until the player removes load.
        this.potentialRpm = produced;
        if (capSu > 0f && this.lastStressLoad > capSu + 0.001f) {
            produced = 0f;
            this.overstressed = true;
        } else {
            this.overstressed = false;
        }

        if (produced != currentRpm) {
            currentRpm = produced;
            setChanged();
        }
        this.currentStressCap = capSu;
        this.lastStressLoad = this.stressLoad; // remember before resetting (for the UI)
        maybeUpdateActivated(level, pos, state, currentRpm > 0f);
        this.stressLoad = 0f;
        transferToHead(level);

        if (rendererManager != null) {
            try {
                boolean gasPresent = lastConsumed > 0 || lastDemand > 0;
                dev.arubik.craftengine.machine.render.variable.MachineRenderContext ctx =
                        new dev.arubik.craftengine.machine.render.variable.MachineRenderContext(
                                currentRpm, currentOverclock(), 0,
                                0, 0, 0,
                                currentRpm > 0, currentRpm > 0, currentOverclock() > 0f,
                                gasPresent,
                                null);
                Direction facing = getFacing(level);
                float yaw = facing == null ? 0f : switch (facing) {
                    case SOUTH -> 0f;
                    case WEST  -> 90f;
                    case NORTH -> 180f;
                    case EAST  -> 270f;
                    default    -> 0f;
                };
                rendererManager.tick(ctx, (net.minecraft.server.level.ServerLevel) level, pos.getX(), pos.getY(), pos.getZ(), yaw);
            } catch (Throwable ignored) {}
        }

        // Reactive barriers: when the unlocked count changes (extra_slots added/removed)
        // while the upgrade page is open, flush placed items to the BE then rebuild the
        // page (re-opening re-syncs FROM the BE, so nothing is lost).
        if (active != null && page == 1 && a.unlocked != lastRenderedUnlocked) {
            lastRenderedUnlocked = a.unlocked;
            active.syncToMachine();
            for (org.bukkit.entity.HumanEntity h : new java.util.ArrayList<>(active.getInventory().getViewers())) {
                if (h instanceof org.bukkit.entity.Player p)
                    openPage(p, 1);
            }
        }

    }

    private Boolean lastActivated = null;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void maybeUpdateActivated(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state, boolean activeState) {
        if (lastActivated != null && lastActivated == activeState)
            return;
        try {
            net.momirealms.craftengine.core.block.property.Property p =
                    (state == null) ? null : state.getProperty("activated");
            if (p == null) {
                lastActivated = activeState;
                return;
            }
            Object val = p.valueByName(String.valueOf(activeState));
            if (val == null)
                return;
            net.momirealms.craftengine.core.block.ImmutableBlockState ns =
                    net.momirealms.craftengine.core.block.ImmutableBlockState.with(state, p, (Comparable) val);
            if (ns == state) {
                lastActivated = activeState;
                return;
            }
            level.setBlock(pos,
                    (net.minecraft.world.level.block.state.BlockState) ns.customBlockState().minecraftState(), 2);
            lastActivated = activeState;
        } catch (Throwable ignored) {
        }
    }

    private void transferToHead(Level level) {
        Direction facing = getFacing(level);
        if (dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity.DEBUG_IO)
            System.out.println("[Motor] avm @" + getMachinePos().toShortString() + " facing=" + facing
                    + " currentRpm=" + currentRpm);
        if (facing == null)
            return;
        BlockPos headPos = getMachinePos().relative(facing);
        BlockEntity head = BukkitBlockEntityTypes.getIfLoaded(level, headPos);
        if (dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity.DEBUG_IO)
            System.out.println("[Motor]   headPos=" + headPos.toShortString()
                    + " head=" + (head == null ? "null" : (head.controller == null ? "no-ctrl"
                            : head.controller.getClass().getSimpleName())));
        if (head == null)
            return;
        BlockEntityController c = head.controller;
        if (c instanceof RpmConsumer consumer)
            consumer.setInputRpm(currentRpm);
    }

    @Override
    public net.momirealms.craftengine.core.world.BlockPos rpmHeadPos() {
        try {
            Direction f = getFacing(getNMSLevel());
            if (f == null)
                return null;
            BlockPos h = getMachinePos().relative(f);
            return new net.momirealms.craftengine.core.world.BlockPos(h.getX(), h.getY(), h.getZ());
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Whether the shaft drives the block at {@code consumerPos}.
     *
     * <p>
     * Which faces a motor drives is declared in {@code motors/*.json} as
     * facing-relative directions, so one definition works at every rotation and a
     * motor with several outputs needs no code. Falls back to the single front face
     * when no definition is present.
     */
    @Override
    public boolean rpmReaches(net.momirealms.craftengine.core.world.BlockPos consumerPos) {
        var def = motorDefinition();
        if (def == null || def.outputFaces().isEmpty())
            return dev.arubik.craftengine.rotation.RpmProvider.super.rpmReaches(consumerPos);
        Direction facing = getFacing(getNMSLevel());
        if (facing == null)
            return false;
        for (var relative : def.outputFaces()) {
            Direction world = dev.arubik.craftengine.multiblock.DirectionalIOHelper
                    .getVerticalWorldDirection(relative, facing);
            if (world == null)
                continue;
            BlockPos head = getMachinePos().relative(world);
            if (head.getX() == consumerPos.x() && head.getY() == consumerPos.y()
                    && head.getZ() == consumerPos.z())
                return true;
        }
        return false;
    }

    // ---------------- RpmProvider ----------------
    @Override
    public float getRpm() {
        return currentRpm;
    }

    /** Public accessor for the motor's facing (where it delivers RPM), or null. */
    public net.minecraft.core.Direction facing(Level level) {
        return getFacing(level);
    }

    @Override
    public float potentialRpm() {
        return potentialRpm;
    }

    @Override
    public float stressCapacity() {
        return currentStressCap;
    }

    @Override
    public void reportStressLoad(float su) {
        this.stressLoad += su; // accumulate the WHOLE driven network (every line/branch through routers)
    }

    // ---------------- machine plumbing (no recipes) ----------------
    @Override
    protected boolean requiresFuel() {
        return false;
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        return null;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
    }

    @Override
    protected String getMachineId() {
        return definition != null && definition.machine() != null ? definition.machine().recipeType() : "motor";
    }

    // ---------------- menu (chest, two pages) ----------------
    @Override
    public MachineLayout getLayout() {
        return switch (page) {
            case 1 -> buildUpgradeLayout();
            case 2 -> buildOverclockLayout();
            default -> buildMainLayout();
        };
    }

    @Override
    public MachineMenu getMenu() {
        if (active == null) {
            active = new MachineMenu(this, getLayout());
            active.syncFromMachine();
        }
        return active;
    }

    @Override
    public void openMenu(net.minecraft.world.entity.player.Player player) {
        openPage((org.bukkit.entity.Player) player.getBukkitEntity(), 0);
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        this.page = newPage;
        if (newPage == 1)
            this.lastRenderedUnlocked = aggregate().unlocked;
        this.active = new MachineMenu(this, getLayout());
        // Populate INPUT (upgrade) slots from the BE BEFORE the player can click —
        // otherwise a later full syncToMachine() would wipe upgrades the menu never showed.
        this.active.syncFromMachine();
        this.active.open(player);
    }

    private MachineLayout buildMainLayout() {
        MachineDefinition machineDef = machineDefinition();
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST,
                machineDef != null ? machineDef.menuSize() : 27,
                machineDef != null ? machineDef.title() : "Motor");
        l.setTitleComponent(noI(tr("polyfill.ui.motor_title", NamedTextColor.DARK_AQUA)));
        l.setDynamicProvider(2, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity) m;
            GasStack g = s.gasTanks.get(0).getGas(s.getNMSLevel(), s.getMachinePos());
            GasType gt = (g == null || g.isEmpty()) ? GasType.EMPTY : g.getType();
            int amount = (g == null) ? 0 : g.getAmount();
            return tIcon(org.bukkit.Material.GLASS_BOTTLE, tr("polyfill.ui.vapor", NamedTextColor.AQUA),
                    tr("polyfill.ui.type", NamedTextColor.GRAY).append(lit(": ", NamedTextColor.GRAY))
                            .append(tr(gt.translationKey(), NamedTextColor.WHITE)),
                    kv("polyfill.ui.stored", NamedTextColor.GRAY, amount + " mB", NamedTextColor.WHITE),
                    kv("polyfill.ui.use", NamedTextColor.GRAY,
                            s.lastConsumed + "/" + s.lastDemand + " mB/t", NamedTextColor.WHITE));
        });
        l.setDynamicProvider(4, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity) m;
            return tIcon(org.bukkit.Material.CLOCK, tr("polyfill.ui.output", NamedTextColor.YELLOW),
                    kv("polyfill.ui.rpm", NamedTextColor.GRAY, String.format("%.0f", s.currentRpm), NamedTextColor.WHITE),
                    kv("polyfill.ui.stress", NamedTextColor.GRAY,
                            String.format("%.0f", s.lastStressLoad) + "/" + String.format("%.0f", s.currentStressCap) + " SU",
                            NamedTextColor.WHITE),
                    kv("polyfill.ui.sucap", NamedTextColor.GRAY, String.format("%.0f", s.currentStressCap), NamedTextColor.WHITE));
        });
        // RPM row: − / value / +
        l.addClickButton(11, (m, t) -> tIcon(org.bukkit.Material.RED_STAINED_GLASS_PANE,
                tr("polyfill.ui.rpm_minus", NamedTextColor.RED), tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)),
                (m, p, c) -> ((DataMotorBlockEntity) m).bumpRpm(false, c));
        l.setDynamicProvider(12, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity) m;
            return tIcon(org.bukkit.Material.LIGHTNING_ROD,
                    kv("polyfill.ui.rpm", NamedTextColor.YELLOW, String.format("%.0f", s.targetRpm), NamedTextColor.WHITE),
                    kv("polyfill.ui.max", NamedTextColor.GRAY,
                            String.format("%.0f", s.rpmMax(s.getNMSLevel(), s.aggregate())), NamedTextColor.WHITE));
        });
        l.addClickButton(13, (m, t) -> tIcon(org.bukkit.Material.GREEN_STAINED_GLASS_PANE,
                tr("polyfill.ui.rpm_plus", NamedTextColor.GREEN), tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)),
                (m, p, c) -> ((DataMotorBlockEntity) m).bumpRpm(true, c));
        // SU row: − / value / +
        l.addClickButton(15, (m, t) -> tIcon(org.bukkit.Material.RED_STAINED_GLASS_PANE,
                tr("polyfill.ui.su_minus", NamedTextColor.RED), tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)),
                (m, p, c) -> ((DataMotorBlockEntity) m).bumpSu(false, c));
        l.setDynamicProvider(16, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity) m;
            return tIcon(org.bukkit.Material.REDSTONE_BLOCK,
                    kv("polyfill.ui.su", NamedTextColor.RED, String.format("%.0f", s.targetSu), NamedTextColor.WHITE),
                    kv("polyfill.ui.max", NamedTextColor.GRAY,
                            String.format("%.0f", s.suMax(s.getNMSLevel(), s.aggregate())), NamedTextColor.WHITE));
        });
        l.addClickButton(17, (m, t) -> tIcon(org.bukkit.Material.GREEN_STAINED_GLASS_PANE,
                tr("polyfill.ui.su_plus", NamedTextColor.GREEN), tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)),
                (m, p, c) -> ((DataMotorBlockEntity) m).bumpSu(true, c));
        if (machineDef != null)
            for (MachineDefinition.ButtonSpec button : machineDef.buttons())
                installButton(l, toButton(button));
        l.fillBackground(icon(org.bukkit.Material.GRAY_STAINED_GLASS_PANE, " "));
        return l;
    }

    private static MachineMenuConfig.Button toButton(MachineDefinition.ButtonSpec spec) {
        return new MachineMenuConfig.Button(spec.slot(), spec.icon(),
                MachineMenuConfig.Action.parse(spec.action()), spec.name(), spec.lore(),
                spec.lockedIcon(), MachineMenuConfig.LockedWhen.parse(spec.lockedWhen()));
    }

    private void installButton(MachineLayout layout, MachineMenuConfig.Button button) {
        layout.addButton(button.slot, (m, tick) -> {
            DataMotorBlockEntity self = (DataMotorBlockEntity) m;
            boolean locked = self.isButtonLocked(button);
            String iconSpec = locked && button.lockedIcon != null ? button.lockedIcon : button.icon;
            return MenuText.iconItem(parseKey(iconSpec), org.bukkit.Material.PAPER,
                    label(button.name, NamedTextColor.AQUA), lore(button.lore));
        }, (m, player) -> {
            DataMotorBlockEntity self = (DataMotorBlockEntity) m;
            if (self.isButtonLocked(button))
                return;
            switch (button.action.kind) {
                case OPEN_PAGE -> self.openPage(player, button.action.page);
                case DEPLETE_GAS -> self.depleteTank();
                case DEPLETE_FLUID, NONE -> {
                }
            }
        });
    }

    private boolean isButtonLocked(MachineMenuConfig.Button button) {
        return button.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && aggregate().overclockLimit <= 0.0;
    }

    private static Component label(String value, NamedTextColor color) {
        return MenuText.textOrTranslatable(value, color);
    }

    private static Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty())
            return new Component[0];
        Component[] out = new Component[lines.size()];
        for (int i = 0; i < lines.size(); i++)
            out[i] = label(lines.get(i), NamedTextColor.GRAY);
        return out;
    }

    private static Key parseKey(String spec) {
        if (spec == null)
            return Key.of("cml", "gui_empty");
        int i = spec.indexOf(':');
        return i < 0 ? Key.of("cml", spec) : Key.of(spec.substring(0, i), spec.substring(i + 1));
    }

    private MachineLayout buildUpgradeLayout() {
        int unlocked = aggregate().unlocked;
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18,
                "Upgrades (" + unlocked + "/" + UPGRADE_SLOTS + ")");
        l.setTitleComponent(noI(tr("polyfill.ui.upgrades", NamedTextColor.DARK_AQUA)
                .append(lit(" (" + unlocked + "/" + UPGRADE_SLOTS + ")", NamedTextColor.GRAY))));
        for (int i = 0; i < upgradeSlotCount(); i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT, i);
            } else {
                l.setDynamicProvider(i, (m, t) -> tIcon(org.bukkit.Material.BARRIER,
                        tr("polyfill.ui.locked", NamedTextColor.RED),
                        tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
            }
        }
        l.setDynamicProvider(13, (m, t) -> {
            int u = ((DataMotorBlockEntity) m).aggregate().unlocked;
            return tIcon(org.bukkit.Material.PAPER,
                    kv("polyfill.ui.active_slots", NamedTextColor.YELLOW, u + "/" + UPGRADE_SLOTS, NamedTextColor.WHITE),
                    tr("polyfill.ui.active_slots_desc", NamedTextColor.GRAY));
        });
        l.addButton(17, (m, t) -> tIcon(org.bukkit.Material.ARROW, tr("polyfill.ui.back", NamedTextColor.YELLOW)),
                (m, p) -> ((DataMotorBlockEntity) m).openPage(p, 0));
        l.fillBackground(icon(org.bukkit.Material.GRAY_STAINED_GLASS_PANE, " "));
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = dev.arubik.craftengine.machine.menu.OverclockMenu.build(
                getMachineId(), NamedTextColor.RED,
                () -> this.currentOverclock(),
                () -> this.overclockLimit(),
                (up, click) -> bumpOverclock(up, click),
                p -> openPage(p, 0));
        l.fillBackground(icon(org.bukkit.Material.GRAY_STAINED_GLASS_PANE, " "));
        return l;
    }

    // ---------------- persistence ----------------
    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_RPM_T, targetRpm);
        set(KEY_SU_T, targetSu);
        set(KEY_RPM, currentRpm);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.targetRpm = getOrDefault(KEY_RPM_T, targetRpm);
        this.targetSu = getOrDefault(KEY_SU_T, targetSu);
        this.currentRpm = getOrDefault(KEY_RPM, 0f);
    }

    @Override
    public void unregister() {
        super.unregister();
        if (rendererManager != null) {
            rendererManager.close();
            rendererManager = null;
        }
    }
}
