package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
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
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import org.bukkit.persistence.PersistentDataType;

/**
 * A vapor-powered motor. Holds a vapor (gas) tank and, each tick, consumes a
 * configurable amount of vapor to produce RPM. RPM is delivered to the block
 * directly in front of the motor (its facing direction) if that block's
 * controller implements {@link RpmConsumer}.
 *
 * <p>Vapor is modelled with the existing gas subsystem; the vapor medium is
 * {@link GasType#STEAM}.</p>
 */
public class VaporMotorBlockEntity extends AbstractMachineBlockEntity implements RpmProvider {

    /** The gas type used as the "vapor" medium. */
    public static final GasType VAPOR = GasType.STEAM;

    public static final int DEFAULT_VAPOR_CAPACITY = 10000;
    public static final int DEFAULT_VAPOR_PER_TICK = 20;
    public static final float DEFAULT_MAX_RPM = 256f;
    /** Default stress this motor can drive (SU). Raise it (↑vapor) to power longer belts. */
    public static final float DEFAULT_STRESS_CAPACITY = 256f;
    /** Extra vapor/tick consumed per SU of load actually driven. */
    public static final float DEFAULT_VAPOR_PER_STRESS = 1f;

    private final int vaporPerTick;
    private final float vaporPerStress;
    // Player-tunable via the motor UI (persisted), seeded from config.
    private float maxRpm;
    private float stressCapacity;

    private float currentRpm = 0f;
    /** Stress (SU) the driven network reported this tick; scales vapor demand. */
    private float stressLoad = 0f;

    // UI adjustment steps + bounds.
    private static final float RPM_STEP = 16f, RPM_MAX = 1024f;
    private static final float STRESS_STEP = 32f, STRESS_MAX = 8192f;

    private static final TypedKey<Float> KEY_MAX_RPM = TypedKey.of(
            "craftengine", "vapor_motor_max_rpm", PersistentDataType.FLOAT);
    private static final TypedKey<Float> KEY_STRESS_CAP = TypedKey.of(
            "craftengine", "vapor_motor_stress_cap", PersistentDataType.FLOAT);

    public void adjustMaxRpm(float delta) {
        this.maxRpm = Math.max(0f, Math.min(RPM_MAX, this.maxRpm + delta));
        setChanged();
    }

    public void adjustStressCapacity(float delta) {
        this.stressCapacity = Math.max(0f, Math.min(STRESS_MAX, this.stressCapacity + delta));
        setChanged();
    }

    private static final TypedKey<Float> KEY_RPM = TypedKey.of(
            "craftengine", "vapor_motor_rpm", PersistentDataType.FLOAT);

    private final MachineLayout layout = new MachineLayout(
            org.bukkit.event.inventory.InventoryType.DISPENSER, 9, "Vapor Motor");

    private static org.bukkit.inventory.ItemStack icon(org.bukkit.Material mat, String name, String... lore) {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta m = s.getItemMeta();
        m.setDisplayName(name);
        if (lore.length > 0)
            m.setLore(java.util.Arrays.asList(lore));
        s.setItemMeta(m);
        return s;
    }

    // ---- i18n component helpers (client-resolved) ----
    private static net.kyori.adventure.text.Component noI(net.kyori.adventure.text.Component c) {
        return c.decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false);
    }

    private static net.kyori.adventure.text.Component tr(String key, net.kyori.adventure.text.format.NamedTextColor color) {
        return net.kyori.adventure.text.Component.translatable(key).color(color);
    }

    private static net.kyori.adventure.text.Component kv(String key, net.kyori.adventure.text.format.NamedTextColor kc,
            String value, net.kyori.adventure.text.format.NamedTextColor vc) {
        return tr(key, kc).append(net.kyori.adventure.text.Component.text(": " + value, vc));
    }

    private static org.bukkit.inventory.ItemStack tIcon(org.bukkit.Material mat,
            net.kyori.adventure.text.Component name, net.kyori.adventure.text.Component... lore) {
        org.bukkit.inventory.ItemStack s = new org.bukkit.inventory.ItemStack(mat);
        org.bukkit.inventory.meta.ItemMeta m = s.getItemMeta();
        m.displayName(noI(name));
        if (lore.length > 0) {
            java.util.List<net.kyori.adventure.text.Component> ls = new java.util.ArrayList<>();
            for (net.kyori.adventure.text.Component c : lore)
                ls.add(noI(c));
            m.lore(ls);
        }
        s.setItemMeta(m);
        return s;
    }

    public VaporMotorBlockEntity(BlockEntity blockEntity) {
        this(blockEntity, DEFAULT_VAPOR_CAPACITY, DEFAULT_VAPOR_PER_TICK, DEFAULT_MAX_RPM,
                DEFAULT_STRESS_CAPACITY, DEFAULT_VAPOR_PER_STRESS);
    }

    public VaporMotorBlockEntity(BlockEntity blockEntity, int vaporCapacity, int vaporPerTick, float maxRpm) {
        this(blockEntity, vaporCapacity, vaporPerTick, maxRpm, DEFAULT_STRESS_CAPACITY, DEFAULT_VAPOR_PER_STRESS);
    }

    public VaporMotorBlockEntity(BlockEntity blockEntity, int vaporCapacity, int vaporPerTick, float maxRpm,
            float stressCapacity, float vaporPerStress) {
        // No item slots are needed; the motor runs purely on vapor.
        super(blockEntity, 0);
        this.vaporPerTick = Math.max(1, vaporPerTick);
        this.maxRpm = Math.max(0f, maxRpm);
        this.stressCapacity = Math.max(0f, stressCapacity);
        this.vaporPerStress = Math.max(0f, vaporPerStress);

        // Single vapor tank, filterable only to the vapor medium.
        addGasTank(new GasTank("vapor", vaporCapacity, VAPOR));

        // Accept vapor ONLY from the TOP face, output none.
        IOConfiguration.Simple config = new IOConfiguration.Simple();
        config.addInput(IOType.GAS, Direction.UP);
        config.setSlots(IOType.GAS, IOConfiguration.IORole.INPUT, 0);
        setIOConfiguration(config);

        // Row 0 = live stats. Row 2 = tuning buttons (− value +).
        // 0: vapor, 1: current rpm/output, 2: stress load/capacity.
        final var GRAY = net.kyori.adventure.text.format.NamedTextColor.GRAY;
        final var WHITE = net.kyori.adventure.text.format.NamedTextColor.WHITE;
        final var AQUA = net.kyori.adventure.text.format.NamedTextColor.AQUA;
        final var YELLOW = net.kyori.adventure.text.format.NamedTextColor.YELLOW;
        final var RED = net.kyori.adventure.text.format.NamedTextColor.RED;
        final var GREEN = net.kyori.adventure.text.format.NamedTextColor.GREEN;
        this.layout.setDynamicProvider(0, (machine, tick) -> {
            VaporMotorBlockEntity self = (VaporMotorBlockEntity) machine;
            GasStack vapor = self.gasTanks.get(0).getGas(self.getNMSLevel(), self.getMachinePos());
            int demand = self.vaporPerTick + Math.round(self.stressLoad * self.vaporPerStress);
            return tIcon(org.bukkit.Material.GLASS_BOTTLE, tr("polyfill.ui.vapor", AQUA),
                    kv("polyfill.ui.stored", GRAY, vapor.getAmount() + " mB", WHITE),
                    kv("polyfill.ui.demand", GRAY, demand + " mB/t", WHITE),
                    kv("polyfill.ui.per_stress", GRAY, self.vaporPerStress + " mB/SU", WHITE));
        });
        this.layout.setDynamicProvider(1, (machine, tick) -> {
            VaporMotorBlockEntity self = (VaporMotorBlockEntity) machine;
            return tIcon(org.bukkit.Material.CLOCK, tr("polyfill.ui.output", YELLOW),
                    kv("polyfill.ui.current_rpm", GRAY, String.format("%.0f", self.currentRpm), WHITE),
                    kv("polyfill.ui.max_rpm", GRAY, String.format("%.0f", self.maxRpm), WHITE));
        });
        this.layout.setDynamicProvider(2, (machine, tick) -> {
            VaporMotorBlockEntity self = (VaporMotorBlockEntity) machine;
            return tIcon(org.bukkit.Material.REDSTONE, tr("polyfill.ui.stress", RED),
                    kv("polyfill.ui.load", GRAY, String.format("%.0f", self.stressLoad) + " SU", WHITE),
                    kv("polyfill.ui.capacity", GRAY, String.format("%.0f", self.stressCapacity) + " SU", WHITE));
        });

        // RPM −/+ (slots 3,5), STRESS −/+ (slots 6,8).
        this.layout.addButton(3,
                (m, t) -> tIcon(org.bukkit.Material.RED_STAINED_GLASS_PANE, tr("polyfill.ui.rpm_minus", RED)),
                (m, p) -> { ((VaporMotorBlockEntity) m).adjustMaxRpm(-RPM_STEP); });
        this.layout.setDynamicProvider(4, (machine, tick) -> {
            VaporMotorBlockEntity self = (VaporMotorBlockEntity) machine;
            return tIcon(org.bukkit.Material.LIGHTNING_ROD,
                    kv("polyfill.ui.max_rpm", YELLOW, String.format("%.0f", self.maxRpm), WHITE),
                    tr("polyfill.ui.tune_click", GRAY));
        });
        this.layout.addButton(5,
                (m, t) -> tIcon(org.bukkit.Material.GREEN_STAINED_GLASS_PANE, tr("polyfill.ui.rpm_plus", GREEN)),
                (m, p) -> { ((VaporMotorBlockEntity) m).adjustMaxRpm(RPM_STEP); });
        this.layout.addButton(6,
                (m, t) -> tIcon(org.bukkit.Material.RED_STAINED_GLASS_PANE, tr("polyfill.ui.stress_minus", RED)),
                (m, p) -> { ((VaporMotorBlockEntity) m).adjustStressCapacity(-STRESS_STEP); });
        this.layout.setDynamicProvider(7, (machine, tick) -> {
            VaporMotorBlockEntity self = (VaporMotorBlockEntity) machine;
            return tIcon(org.bukkit.Material.REDSTONE_BLOCK,
                    kv("polyfill.ui.stress_cap", RED, String.format("%.0f", self.stressCapacity) + " SU", WHITE),
                    tr("polyfill.ui.tune_click", GRAY));
        });
        this.layout.addButton(8,
                (m, t) -> tIcon(org.bukkit.Material.GREEN_STAINED_GLASS_PANE, tr("polyfill.ui.stress_plus", GREEN)),
                (m, p) -> { ((VaporMotorBlockEntity) m).adjustStressCapacity(STRESS_STEP); });
    }

    // The motor is not a processing machine; vapor->RPM is handled in tick().
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
        // no-op
    }

    @Override
    protected String getMachineId() {
        return "vapor_motor";
    }

    @Override
    public MachineLayout getLayout() {
        return layout;
    }

    // --- RpmProvider ---
    @Override
    public float getRpm() {
        return currentRpm;
    }

    /** Public accessor for the motor's facing (where it delivers RPM), or null. */
    public net.minecraft.core.Direction facing(Level level) {
        return getFacing(level);
    }

    // --- Tick: consume vapor -> produce RPM -> push to head ---
    @Override
    public void tick(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        super.tick(level, pos, state);
        if (level.isClientSide())
            return;

        // Vapor demand = base (speed) + load×perStress (work driven). Raising max-rpm
        // or driving more stress both cost more vapor.
        int demand = vaporPerTick + Math.round(stressLoad * vaporPerStress);
        if (demand < 1)
            demand = 1;

        float producedRpm = 0f;
        if (!gasTanks.isEmpty()) {
            GasTank tank = gasTanks.get(0);
            GasStack vapor = tank.getGas(level, getMachinePos());
            int available = vapor.getAmount();
            if (available > 0) {
                int toConsume = Math.min(demand, available);
                tank.extract(level, getMachinePos(), toConsume, null);
                // RPM scales with how much of the demanded vapor was satisfied.
                float ratio = (float) toConsume / (float) demand;
                producedRpm = maxRpm * ratio;
            }
        }

        if (producedRpm != currentRpm) {
            currentRpm = producedRpm;
            setChanged();
        }

        // Drive the on/off visual state from whether the motor is actually running.
        maybeUpdateActivated(level, pos, state, currentRpm > 0f);

        // Consume the reported load; the network re-reports it next tick if still driven.
        this.stressLoad = 0f;

        transferToHead(level);
    }

    private Boolean lastActivated = null;

    /** Flip the {@code activated} block-state (on/off model) without recreating the BE. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void maybeUpdateActivated(Level level, BlockPos pos,
            net.momirealms.craftengine.core.block.ImmutableBlockState state, boolean active) {
        if (lastActivated != null && lastActivated == active) {
            return;
        }
        try {
            net.momirealms.craftengine.core.block.property.Property p =
                    (state == null) ? null : state.getProperty("activated");
            if (p == null) {
                lastActivated = active;
                return;
            }
            Object val = p.valueByName(String.valueOf(active));
            if (val == null) {
                return;
            }
            net.momirealms.craftengine.core.block.ImmutableBlockState ns =
                    net.momirealms.craftengine.core.block.ImmutableBlockState.with(state, p, (Comparable) val);
            if (ns == state) {
                lastActivated = active;
                return;
            }
            level.setBlock(pos,
                    (net.minecraft.world.level.block.state.BlockState) ns.customBlockState().minecraftState(), 2);
            lastActivated = active;
        } catch (Throwable ignored) {
        }
    }

    @Override
    public float stressCapacity() {
        return stressCapacity;
    }

    @Override
    public void reportStressLoad(float su) {
        if (su > this.stressLoad)
            this.stressLoad = su;
    }

    /**
     * Delivers RPM to the directly-connected block in the motor's facing
     * direction. No network graph: a single block, the "head".
     */
    private void transferToHead(Level level) {
        Direction facing = getFacing(level);
        if (facing == null)
            return;
        BlockPos headPos = getMachinePos().relative(facing);
        BlockEntity head = BukkitBlockEntityTypes.getIfLoaded(level, headPos);
        if (head == null)
            return;
        BlockEntityController controller = head.controller;
        if (controller instanceof RpmConsumer consumer) {
            consumer.setInputRpm(currentRpm);
        }
    }

    // --- Persistence ---
    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_RPM, currentRpm);
        set(KEY_MAX_RPM, maxRpm);          // player-tuned values persist
        set(KEY_STRESS_CAP, stressCapacity);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.currentRpm = getOrDefault(KEY_RPM, 0f);
        this.maxRpm = getOrDefault(KEY_MAX_RPM, this.maxRpm);
        this.stressCapacity = getOrDefault(KEY_STRESS_CAP, this.stressCapacity);
    }
}
