/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.format.TextDecoration
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.libraries.nbt.CompoundTag
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.UpgradeMenu;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.formula.PolyContext;
import dev.arubik.craftengine.machine.render.formula.PolyScript;
import dev.arubik.craftengine.machine.render.formula.PolyScriptRegistry;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.multiblock.DirectionalIOHelper;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.rotation.MotorDefinition;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DataMotorBlockEntity
extends AbstractMachineBlockEntity
implements RpmProvider,
ModelRendersDriven {
    public static final int UPGRADE_SLOTS = 9;
    public static final int BASE_UNLOCKED = 3;
    private static final float BASE_OC = 2.0f;
    private final MotorDefinition definition;
    private RendererManager rendererManager;
    private final int vaporCapacity;
    private final Map<GasType, GasSpec> gases;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private float targetRpm = 32.0f;
    private float targetSu = 64.0f;
    private float currentRpm = 0.0f;
    private float potentialRpm = 0.0f;
    private boolean overstressed = false;
    private float currentStressCap = 0.0f;
    private float stressLoad = 0.0f;
    private int lastDemand = 0;
    private int lastConsumed = 0;
    private float lastStressLoad = 0.0f;
    private int page = 0;
    private int lastRenderedUnlocked = 3;
    private MachineMenu active;
    private static final TypedKey<Float> KEY_RPM_T = TypedKey.of("craftengine", "adv_motor_trpm", NbtType.FLOAT);
    private static final TypedKey<Float> KEY_SU_T = TypedKey.of("craftengine", "adv_motor_tsu", NbtType.FLOAT);
    private static final TypedKey<Float> KEY_RPM = TypedKey.of("craftengine", "adv_motor_rpm", NbtType.FLOAT);
    private Boolean lastActivated = null;

    private MotorDefinition motorDefinition() {
        return this.definition;
    }

    private MachineDefinition machineDefinition() {
        return this.definition == null ? null : this.definition.machine();
    }

    private int upgradeSlotCount() {
        MotorDefinition d = this.motorDefinition();
        return d != null ? d.upgradeSlots() : 9;
    }

    private int baseUnlockedCount() {
        MotorDefinition d = this.motorDefinition();
        return d != null ? d.baseUnlocked() : 3;
    }

    private float baseOverclock() {
        MotorDefinition d = this.motorDefinition();
        return d != null ? d.baseOverclock() : 2.0f;
    }

    public DataMotorBlockEntity(BlockEntity blockEntity, MotorDefinition definition, int vaporCapacity, Map<GasType, GasSpec> gases, Map<Key, List<MachineAttributes.Mod>> upgradeDefs) {
        super(blockEntity, definition != null ? definition.upgradeSlots() : 9);
        this.definition = definition;
        this.vaporCapacity = vaporCapacity > 0 ? vaporCapacity : 10000;
        this.gases = gases == null || gases.isEmpty() ? DataMotorBlockEntity.defaultGases() : gases;
        this.upgradeDefs = upgradeDefs == null ? new HashMap() : upgradeDefs;
        this.addGasTank(new GasTank("vapor", this.vaporCapacity));
        IOConfiguration.Simple config = new IOConfiguration.Simple();
        config.addInput(IOConfiguration.IOType.GAS, net.minecraft.core.Direction.UP, net.minecraft.core.Direction.DOWN);
        config.setSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.INPUT, 0);
        this.setIOConfiguration(config);
        GasSpec any = this.gases.values().iterator().next();
        this.targetRpm = any.rpm();
        this.targetSu = any.su();
        MachineDefinition machineDef = this.machineDefinition();
        if (machineDef != null && !machineDef.renderers().isEmpty()) {
            this.rendererManager = new RendererManager(machineDef.renderers(), machineDef.variables());
        }
    }

    @Override
    public RendererManager rendererManager() {
        return this.rendererManager;
    }

    private static Map<GasType, GasSpec> defaultGases() {
        HashMap<GasType, GasSpec> m = new HashMap<GasType, GasSpec>();
        m.put(GasType.STEAM, new GasSpec(32.0f, 64.0f, 20));
        return m;
    }

    private static ItemStack icon(Material mat, String name, String ... lore) {
        ItemStack s = new ItemStack(mat);
        ItemMeta m = s.getItemMeta();
        m.setDisplayName(name);
        if (lore.length > 0) {
            m.setLore(Arrays.asList(lore));
        }
        s.setItemMeta(m);
        return s;
    }

    private static Component noI(Component c) {
        return c.decoration(TextDecoration.ITALIC, false);
    }

    private static Component tr(String key, NamedTextColor color) {
        return Component.translatable((String)key).color((TextColor)color);
    }

    private static Component lit(String s, NamedTextColor color) {
        return Component.text((String)s, (TextColor)color);
    }

    private static Component kv(String key, NamedTextColor keyColor, String value, NamedTextColor valColor) {
        return DataMotorBlockEntity.tr(key, keyColor).append(DataMotorBlockEntity.lit(": " + value, valColor));
    }

    private static ItemStack tIcon(Material mat, Component name, Component ... lore) {
        ItemStack s = new ItemStack(mat);
        ItemMeta m = s.getItemMeta();
        m.displayName(DataMotorBlockEntity.noI(name));
        if (lore.length > 0) {
            ArrayList<Component> ls = new ArrayList<Component>();
            for (Component c : lore) {
                ls.add(DataMotorBlockEntity.noI(c));
            }
            m.lore(ls);
        }
        s.setItemMeta(m);
        return s;
    }

    private Key itemId(int slot) {
        net.minecraft.world.item.ItemStack nms = this.getItem(slot);
        if (nms == null || nms.isEmpty()) {
            return null;
        }
        ItemStack b = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms);
        Key custom = CraftEngineItems.getCustomItemId((ItemStack)b);
        if (custom != null) {
            return custom;
        }
        NamespacedKey nk = b.getType().getKey();
        return Key.of((String)nk.getNamespace(), (String)nk.getKey());
    }

    private List<MachineAttributes.Mod> modsOf(int slot) {
        Key id = this.itemId(slot);
        return id == null ? null : this.upgradeDefs.get(id);
    }

    private boolean givesSlots(int slot) {
        List<MachineAttributes.Mod> mods = this.modsOf(slot);
        if (mods == null) {
            return false;
        }
        for (MachineAttributes.Mod m : mods) {
            if (!m.attribute().equals(MachineAttributes.EXTRA_SLOTS) || m.amount() == 0.0) continue;
            return true;
        }
        return false;
    }

    private int extraSlotsOf(List<MachineAttributes.Mod> mods) {
        if (mods == null) {
            return 0;
        }
        return (int)Math.round(MachineAttributes.compute(mods).getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
    }

    @Override
    public boolean canTakeFromSlot(int slot) {
        if (slot < 0 || slot >= this.upgradeSlotCount() || !this.givesSlots(slot)) {
            return true;
        }
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.upgradeSlotCount(); ++i) {
            List<MachineAttributes.Mod> mods = this.modsOf(i);
            if (mods == null) continue;
            all.addAll(mods);
        }
        int newUnlocked = Math.max(this.baseUnlockedCount(), Math.min(this.upgradeSlotCount(), this.baseUnlockedCount() + this.extraSlotsOf(all) - this.extraSlotsOf(this.modsOf(slot))));
        int highestOccupied = -1;
        for (int i = 0; i < this.upgradeSlotCount(); ++i) {
            net.minecraft.world.item.ItemStack it = this.getItem(i);
            if (it == null || it.isEmpty()) continue;
            highestOccupied = i;
        }
        return highestOccupied < newUnlocked;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private Agg aggregate() {
        Key MA = MachineAttributes.EXTRA_SLOTS;
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.upgradeSlotCount(); ++i) {
            List<MachineAttributes.Mod> mods = this.modsOf(i);
            if (mods == null) continue;
            all.addAll(mods);
        }
        int extra = (int)Math.round(MachineAttributes.compute(all).getOrDefault(MA, 0.0));
        Agg a = new Agg();
        a.unlocked = Math.max(this.baseUnlockedCount(), Math.min(this.upgradeSlotCount(), this.baseUnlockedCount() + extra));
        ArrayList<MachineAttributes.Mod> active = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < a.unlocked; ++i) {
            List<MachineAttributes.Mod> mods = this.modsOf(i);
            if (mods == null) continue;
            active.addAll(mods);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(active);
        a.overclockLimit = DataMotorBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        a.consume = DataMotorBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);
        a.generation = DataMotorBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        return a;
    }

    private GasSpec activeSpec(Level level) {
        if (this.gasTanks.isEmpty()) {
            return null;
        }
        GasStack g = ((GasTank)this.gasTanks.get(0)).getGas(level, this.getMachinePos());
        return g != null && !g.isEmpty() ? this.gases.get(g.getType()) : null;
    }

    private GasSpec refSpec(Level level) {
        GasSpec a = this.activeSpec(level);
        return a != null ? a : this.gases.values().iterator().next();
    }

    private float ocFactor(Agg a) {
        return this.baseOverclock() * (1.0f + (float)a.overclockLimit);
    }

    private float rpmMax(Level level, Agg a) {
        return this.refSpec(level).rpm() * this.ocFactor(a);
    }

    private float suMax(Level level, Agg a) {
        return this.refSpec(level).su() * this.ocFactor(a);
    }

    private float overclockLimit() {
        return this.ocFactor(this.aggregate()) - 1.0f;
    }

    private float currentOverclock() {
        GasSpec spec = this.refSpec(this.getNMSLevel());
        if (spec == null || spec.rpm() <= 0.0f || spec.su() <= 0.0f) {
            return 0.0f;
        }
        float rpmFactor = this.targetRpm / spec.rpm();
        float suFactor = this.targetSu / spec.su();
        return Math.min(rpmFactor, suFactor) - 1.0f;
    }

    public void bumpOverclock(boolean up, ClickType click) {
        GasSpec spec = this.refSpec(this.getNMSLevel());
        if (spec == null) {
            return;
        }
        Agg a = this.aggregate();
        if (a.overclockLimit <= 0.0) {
            return;
        }
        float next = this.currentOverclock() + (up ? 1.0f : -1.0f) * OverclockMenu.step(click);
        next = (float)DataMotorBlockEntity.clamp(next, -0.99, this.overclockLimit());
        float factor = Math.max(0.0f, 1.0f + next);
        this.targetRpm = Math.max(0.0f, Math.min(this.rpmMax(this.getNMSLevel(), a), spec.rpm() * factor));
        this.targetSu = Math.max(0.0f, Math.min(this.suMax(this.getNMSLevel(), a), spec.su() * factor));
        this.setChanged();
    }

    private static int step(ClickType c) {
        if (c == ClickType.DROP || c == ClickType.CONTROL_DROP) {
            return 32;
        }
        if (c == ClickType.RIGHT) {
            return 8;
        }
        return 1;
    }

    private static boolean isShift(ClickType c) {
        return c == ClickType.SHIFT_LEFT || c == ClickType.SHIFT_RIGHT;
    }

    public void bumpRpm(boolean up, ClickType c) {
        Agg a = this.aggregate();
        float max = this.rpmMax(this.getNMSLevel(), a);
        this.targetRpm = DataMotorBlockEntity.isShift(c) ? (up ? max : -max) : (this.targetRpm += up ? (float)DataMotorBlockEntity.step(c) : (float)(-DataMotorBlockEntity.step(c)));
        this.targetRpm = Math.max(-max, Math.min(max, this.targetRpm));
        this.setChanged();
    }

    public void bumpSu(boolean up, ClickType c) {
        Agg a = this.aggregate();
        float max = this.suMax(this.getNMSLevel(), a);
        this.targetSu = DataMotorBlockEntity.isShift(c) ? (up ? max : 0.0f) : (this.targetSu += up ? (float)DataMotorBlockEntity.step(c) : (float)(-DataMotorBlockEntity.step(c)));
        this.targetSu = Math.max(0.0f, Math.min(max, this.targetSu));
        this.setChanged();
    }

    public void dropUpgrades() {
        Level lvl = this.getNMSLevel();
        if (lvl == null) {
            return;
        }
        CraftWorld bw = lvl.getWorld();
        BlockPos pos = this.getMachinePos();
        for (int i = 0; i < this.upgradeSlotCount(); ++i) {
            net.minecraft.world.item.ItemStack nms = this.getItem(i);
            if (nms == null || nms.isEmpty()) continue;
            ItemStack b = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms);
            if (bw != null && b != null && !b.getType().isAir()) {
                bw.dropItem(new Location((World)bw, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5), b);
            }
            this.setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
        }
        this.clear();
        this.setChanged();
    }

    public void depleteTank() {
        if (this.gasTanks.isEmpty()) {
            return;
        }
        Level lvl = this.getNMSLevel();
        if (lvl == null) {
            return;
        }
        GasTank tank = (GasTank)this.gasTanks.get(0);
        GasStack g = tank.getGas(lvl, this.getMachinePos());
        if (g != null && !g.isEmpty()) {
            tank.extract(lvl, this.getMachinePos(), g.getAmount(), null);
        }
        this.setChanged();
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (this.active != null) {
            this.active.tick();
        }
        if (level.isClientSide()) {
            return;
        }
        this.pullFromInputFaces(level);
        Agg a = this.aggregate();
        float rpmMax = this.rpmMax(level, a);
        this.targetRpm = Math.max(-rpmMax, Math.min(rpmMax, this.targetRpm));
        this.targetSu = Math.max(0.0f, Math.min(this.suMax(level, a), this.targetSu));
        float produced = 0.0f;
        float capSu = 0.0f;
        int demand = 0;
        int consumed = 0;
        GasSpec spec = this.activeSpec(level);
        if (spec != null && this.targetRpm != 0.0f && this.targetSu > 0.0f) {
            int avail;
            GasTank tank = (GasTank)this.gasTanks.get(0);
            float loadSu = Math.min(this.lastStressLoad, this.targetSu);
            float baseGas = (float)spec.gasPerTick() * (this.targetRpm / spec.rpm());
            float loadGas = (float)spec.gasPerTick() * (loadSu / spec.su());
            demand = Math.max(1, Math.round((baseGas + loadGas) * (1.0f - (float)a.consume)));
            GasStack stored = tank.getGas(level, this.getMachinePos());
            int n = avail = stored == null ? 0 : stored.getAmount();
            if (avail > 0) {
                consumed = Math.min(demand, avail);
                tank.extract(level, this.getMachinePos(), consumed, null);
                float ratio = (float)consumed / (float)demand;
                float gen = 1.0f + (float)a.generation;
                produced = this.targetRpm * ratio * gen;
                capSu = this.targetSu * gen;
            }
        }
        this.lastDemand = demand;
        this.lastConsumed = consumed;
        this.potentialRpm = Math.abs(produced);
        if (capSu > 0.0f && this.lastStressLoad > capSu + 0.001f) {
            produced = 0.0f;
            this.overstressed = true;
        } else {
            this.overstressed = false;
        }
        if (produced != this.currentRpm) {
            this.currentRpm = produced;
            this.setChanged();
        }
        this.currentStressCap = capSu;
        this.lastStressLoad = this.stressLoad;
        this.updateMotorActivated(level, pos, state, this.currentRpm != 0.0f);
        this.stressLoad = 0.0f;
        this.transferToHead(level);
        if (this.rendererManager != null) {
            try {
                float f;
                boolean gasPresent = this.lastConsumed > 0 || this.lastDemand > 0;
                MachineRenderContext ctx = new MachineRenderContext(this.currentRpm, this.currentOverclock(), 0.0, 0.0, 0.0, 0.0, this.currentRpm > 0.0f, this.currentRpm > 0.0f, this.currentOverclock() > 0.0f, gasPresent, null);
                net.minecraft.core.Direction facing = this.getFacing(level);
                if (facing == null) {
                    f = 0.0f;
                } else {
                    switch (facing) {
                        case SOUTH: {
                            f = 0.0f;
                            break;
                        }
                        case WEST: {
                            f = 90.0f;
                            break;
                        }
                        case NORTH: {
                            f = 180.0f;
                            break;
                        }
                        case EAST: {
                            f = 270.0f;
                            break;
                        }
                        default: {
                            f = 0.0f;
                        }
                    }
                }
                float yaw = f;
                this.rendererManager.tick(ctx, (ServerLevel)level, pos.getX(), pos.getY(), pos.getZ(), yaw);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (this.active != null && this.page == 1 && a.unlocked != this.lastRenderedUnlocked) {
            this.lastRenderedUnlocked = a.unlocked;
            this.active.syncToMachine();
            for (HumanEntity h : new ArrayList(this.active.getInventory().getViewers())) {
                if (!(h instanceof org.bukkit.entity.Player)) continue;
                org.bukkit.entity.Player p = (org.bukkit.entity.Player)h;
                this.openPage(p, 1);
            }
        }
    }

    private void updateMotorActivated(Level level, BlockPos pos, ImmutableBlockState state, boolean activeState) {
        if (this.lastActivated != null && this.lastActivated == activeState) {
            return;
        }
        try {
            Property p;
            Property property = p = state == null ? null : state.getProperty("activated");
            if (p == null) {
                this.lastActivated = activeState;
                return;
            }
            Comparable val = p.valueByName(String.valueOf(activeState));
            if (val == null) {
                return;
            }
            ImmutableBlockState ns = ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, val);
            if (ns == state) {
                this.lastActivated = activeState;
                return;
            }
            level.setBlock(pos, (BlockState)ns.customBlockState().minecraftState(), 2);
            this.lastActivated = activeState;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void transferToHead(Level level) {
        net.minecraft.core.Direction facing = this.getFacing(level);
        if (AbstractMachineBlockEntity.DEBUG_IO) {
            System.out.println("[Motor] avm @" + this.getMachinePos().toShortString() + " facing=" + String.valueOf(facing) + " currentRpm=" + this.currentRpm);
        }
        if (facing == null) {
            return;
        }
        BlockPos headPos = this.getMachinePos().relative(facing);
        BlockEntity head = BukkitBlockEntityTypes.getIfLoaded(level, headPos);
        if (AbstractMachineBlockEntity.DEBUG_IO) {
            System.out.println("[Motor]   headPos=" + headPos.toShortString() + " head=" + (head == null ? "null" : (head.controller == null ? "no-ctrl" : head.controller.getClass().getSimpleName())));
        }
        if (head == null) {
            return;
        }
        BlockEntityController c = head.controller;
        if (c instanceof RpmConsumer) {
            RpmConsumer consumer = (RpmConsumer)c;
            consumer.setInputRpm(this.currentRpm);
        }
    }

    @Override
    public net.momirealms.craftengine.core.world.BlockPos rpmHeadPos() {
        try {
            net.minecraft.core.Direction f = this.getFacing(this.getNMSLevel());
            if (f == null) {
                return null;
            }
            BlockPos h = this.getMachinePos().relative(f);
            return new net.momirealms.craftengine.core.world.BlockPos(h.getX(), h.getY(), h.getZ());
        }
        catch (Throwable t) {
            return null;
        }
    }

    @Override
    public boolean rpmReaches(net.momirealms.craftengine.core.world.BlockPos consumerPos) {
        MotorDefinition def = this.motorDefinition();
        if (def == null || def.outputFaces().isEmpty()) {
            return RpmProvider.super.rpmReaches(consumerPos);
        }
        net.minecraft.core.Direction facing = this.getFacing(this.getNMSLevel());
        if (facing == null) {
            return false;
        }
        Direction ceFacing = Direction.valueOf((String)facing.getName().toUpperCase());
        for (RelativeDirection relative : def.outputFaces()) {
            BlockPos head;
            net.minecraft.core.Direction world = DirectionalIOHelper.getHorizontalWorldDirection(relative, ceFacing);
            if (world == null || (head = this.getMachinePos().relative(world)).getX() != consumerPos.x() || head.getY() != consumerPos.y() || head.getZ() != consumerPos.z()) continue;
            return true;
        }
        return false;
    }

    @Override
    public float getRpm() {
        return this.currentRpm;
    }

    public net.minecraft.core.Direction facing(Level level) {
        return this.getFacing(level);
    }

    @Override
    public float potentialRpm() {
        return this.potentialRpm;
    }

    @Override
    public float stressCapacity() {
        return this.currentStressCap;
    }

    public float getSuCapacity() {
        return this.currentStressCap;
    }

    public boolean isOverstressed() {
        return this.overstressed;
    }

    @Override
    public void reportStressLoad(float su) {
        this.stressLoad += su;
    }

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
        return this.definition != null && this.definition.machine() != null ? this.definition.machine().recipeType() : "motor";
    }

    @Override
    public MachineLayout getLayout() {
        return switch (this.page) {
            case 1 -> this.buildUpgradeLayout();
            case 2 -> this.buildOverclockLayout();
            default -> this.buildMainLayout();
        };
    }

    @Override
    public MachineMenu getMenu() {
        if (this.active == null) {
            this.active = new MachineMenu(this, this.getLayout());
            this.active.syncFromMachine();
        }
        return this.active;
    }

    @Override
    public void openMenu(Player player) {
        this.openPage((org.bukkit.entity.Player)player.getBukkitEntity(), 0);
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        this.page = newPage;
        if (newPage == 1) {
            this.lastRenderedUnlocked = this.aggregate().unlocked;
        }
        this.active = new MachineMenu(this, this.getLayout());
        this.active.syncFromMachine();
        this.active.open(player);
    }

    private MachineLayout buildMainLayout() {
        MachineDefinition machineDef = this.machineDefinition();
        MachineLayout l = new MachineLayout(InventoryType.CHEST, machineDef != null ? machineDef.menuSize() : 27, machineDef != null ? machineDef.title() : "Motor");
        l.setTitleComponent(DataMotorBlockEntity.noI(DataMotorBlockEntity.tr("polyfill.ui.motor_title", NamedTextColor.DARK_AQUA)));
        l.setDynamicProvider(2, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity)m;
            GasStack g = ((GasTank)s.gasTanks.get(0)).getGas(s.getNMSLevel(), s.getMachinePos());
            GasType gt = g == null || g.isEmpty() ? GasType.EMPTY : g.getType();
            int amount = g == null ? 0 : g.getAmount();
            return DataMotorBlockEntity.tIcon(Material.GLASS_BOTTLE, DataMotorBlockEntity.tr("polyfill.ui.vapor", NamedTextColor.AQUA), DataMotorBlockEntity.tr("polyfill.ui.type", NamedTextColor.GRAY).append(DataMotorBlockEntity.lit(": ", NamedTextColor.GRAY)).append(DataMotorBlockEntity.tr(gt.translationKey(), NamedTextColor.WHITE)), DataMotorBlockEntity.kv("polyfill.ui.stored", NamedTextColor.GRAY, amount + " mB", NamedTextColor.WHITE), DataMotorBlockEntity.kv("polyfill.ui.use", NamedTextColor.GRAY, s.lastConsumed + "/" + s.lastDemand + " mB/t", NamedTextColor.WHITE));
        });
        l.setDynamicProvider(4, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity)m;
            return DataMotorBlockEntity.tIcon(Material.CLOCK, DataMotorBlockEntity.tr("polyfill.ui.output", NamedTextColor.YELLOW), DataMotorBlockEntity.kv("polyfill.ui.rpm", NamedTextColor.GRAY, String.format("%.0f", Float.valueOf(s.currentRpm)), NamedTextColor.WHITE), DataMotorBlockEntity.kv("polyfill.ui.stress", NamedTextColor.GRAY, String.format("%.0f", Float.valueOf(s.lastStressLoad)) + "/" + String.format("%.0f", Float.valueOf(s.currentStressCap)) + " SU", NamedTextColor.WHITE), DataMotorBlockEntity.kv("polyfill.ui.sucap", NamedTextColor.GRAY, String.format("%.0f", Float.valueOf(s.currentStressCap)), NamedTextColor.WHITE));
        });
        l.addClickButton(11, (m, t) -> DataMotorBlockEntity.tIcon(Material.RED_STAINED_GLASS_PANE, DataMotorBlockEntity.tr("polyfill.ui.rpm_minus", NamedTextColor.RED), DataMotorBlockEntity.tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)), (m, p, c) -> ((DataMotorBlockEntity)m).bumpRpm(false, c));
        l.setDynamicProvider(12, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity)m;
            return DataMotorBlockEntity.tIcon(Material.LIGHTNING_ROD, DataMotorBlockEntity.kv("polyfill.ui.rpm", NamedTextColor.YELLOW, String.format("%.0f", Float.valueOf(s.targetRpm)), NamedTextColor.WHITE), DataMotorBlockEntity.kv("polyfill.ui.max", NamedTextColor.GRAY, String.format("%.0f", Float.valueOf(s.rpmMax(s.getNMSLevel(), s.aggregate()))), NamedTextColor.WHITE));
        });
        l.addClickButton(13, (m, t) -> DataMotorBlockEntity.tIcon(Material.GREEN_STAINED_GLASS_PANE, DataMotorBlockEntity.tr("polyfill.ui.rpm_plus", NamedTextColor.GREEN), DataMotorBlockEntity.tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)), (m, p, c) -> ((DataMotorBlockEntity)m).bumpRpm(true, c));
        l.addClickButton(15, (m, t) -> DataMotorBlockEntity.tIcon(Material.RED_STAINED_GLASS_PANE, DataMotorBlockEntity.tr("polyfill.ui.su_minus", NamedTextColor.RED), DataMotorBlockEntity.tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)), (m, p, c) -> ((DataMotorBlockEntity)m).bumpSu(false, c));
        l.setDynamicProvider(16, (m, t) -> {
            DataMotorBlockEntity s = (DataMotorBlockEntity)m;
            return DataMotorBlockEntity.tIcon(Material.REDSTONE_BLOCK, DataMotorBlockEntity.kv("polyfill.ui.su", NamedTextColor.RED, String.format("%.0f", Float.valueOf(s.targetSu)), NamedTextColor.WHITE), DataMotorBlockEntity.kv("polyfill.ui.max", NamedTextColor.GRAY, String.format("%.0f", Float.valueOf(s.suMax(s.getNMSLevel(), s.aggregate()))), NamedTextColor.WHITE));
        });
        l.addClickButton(17, (m, t) -> DataMotorBlockEntity.tIcon(Material.GREEN_STAINED_GLASS_PANE, DataMotorBlockEntity.tr("polyfill.ui.su_plus", NamedTextColor.GREEN), DataMotorBlockEntity.tr("polyfill.ui.tune_hint", NamedTextColor.GRAY)), (m, p, c) -> ((DataMotorBlockEntity)m).bumpSu(true, c));
        if (machineDef != null) {
            for (MachineDefinition.ButtonSpec button : machineDef.buttons()) {
                this.installButton(l, DataMotorBlockEntity.toButton(button));
            }
        }
        l.fillBackground(DataMotorBlockEntity.icon(Material.GRAY_STAINED_GLASS_PANE, " ", new String[0]));
        return l;
    }

    private static MachineMenuConfig.Button toButton(MachineDefinition.ButtonSpec spec) {
        return new MachineMenuConfig.Button(spec.slot(), spec.icon(), MachineMenuConfig.Action.parse(spec.action()), spec.name(), spec.lore(), spec.lockedIcon(), MachineMenuConfig.LockedWhen.parse(spec.lockedWhen()));
    }

    private void installButton(MachineLayout layout, MachineMenuConfig.Button button) {
        layout.addButton(button.slot, (m, tick) -> {
            DataMotorBlockEntity self = (DataMotorBlockEntity)m;
            boolean locked = self.isButtonLocked(button);
            String iconSpec = locked && button.lockedIcon != null ? button.lockedIcon : button.icon;
            return MenuText.iconItem(DataMotorBlockEntity.parseKey(iconSpec), Material.PAPER, DataMotorBlockEntity.label(button.name, NamedTextColor.AQUA), DataMotorBlockEntity.lore(button.lore));
        }, (m, player) -> {
            DataMotorBlockEntity self = (DataMotorBlockEntity)m;
            if (self.isButtonLocked(button)) {
                return;
            }
            switch (button.action.kind) {
                case OPEN_PAGE: {
                    self.openPage((org.bukkit.entity.Player)player, button.action.page);
                    break;
                }
                case DEPLETE_GAS: {
                    self.depleteTank();
                    break;
                }
                case SCRIPT: {
                    PolyContext ctx;
                    PolyScript sc = PolyScriptRegistry.get(button.action.target);
                    if (sc == null || (ctx = self.buildEvalContext()) == null) break;
                    sc.evaluate(ctx);
                    break;
                }
            }
        });
    }

    private boolean isButtonLocked(MachineMenuConfig.Button button) {
        return button.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && this.aggregate().overclockLimit <= 0.0;
    }

    private static Component label(String value, NamedTextColor color) {
        return MenuText.textOrTranslatable(value, color);
    }

    private static Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return new Component[0];
        }
        Component[] out = new Component[lines.size()];
        for (int i = 0; i < lines.size(); ++i) {
            out[i] = DataMotorBlockEntity.label(lines.get(i), NamedTextColor.GRAY);
        }
        return out;
    }

    private static Key parseKey(String spec) {
        if (spec == null) {
            return Key.of((String)"cml", (String)"gui_empty");
        }
        int i = spec.indexOf(58);
        return i < 0 ? Key.of((String)"cml", (String)spec) : Key.of((String)spec.substring(0, i), (String)spec.substring(i + 1));
    }

    private MachineLayout buildUpgradeLayout() {
        return UpgradeMenu.build(this.getMachineId(), this.upgradeSlotCount(), () -> this.aggregate().unlocked, (m, p) -> ((DataMotorBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0), (m, t) -> {
            int u = ((DataMotorBlockEntity)m).aggregate().unlocked;
            return DataMotorBlockEntity.tIcon(Material.PAPER, DataMotorBlockEntity.kv("polyfill.ui.active_slots", NamedTextColor.YELLOW, u + "/9", NamedTextColor.WHITE), DataMotorBlockEntity.tr("polyfill.ui.active_slots_desc", NamedTextColor.GRAY));
        });
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = OverclockMenu.build(this.getMachineId(), NamedTextColor.RED, () -> this.currentOverclock(), () -> this.overclockLimit(), (up, click) -> this.bumpOverclock((boolean)up, (ClickType)click), p -> this.openPage((org.bukkit.entity.Player)p, 0));
        l.fillBackground(DataMotorBlockEntity.icon(Material.GRAY_STAINED_GLASS_PANE, " ", new String[0]));
        return l;
    }

    @Override
    public void saveCustomData(CompoundTag tag) {
        this.set(KEY_RPM_T, Float.valueOf(this.targetRpm));
        this.set(KEY_SU_T, Float.valueOf(this.targetSu));
        this.set(KEY_RPM, Float.valueOf(this.currentRpm));
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.targetRpm = this.getOrDefault(KEY_RPM_T, Float.valueOf(this.targetRpm)).floatValue();
        this.targetSu = this.getOrDefault(KEY_SU_T, Float.valueOf(this.targetSu)).floatValue();
        this.currentRpm = this.getOrDefault(KEY_RPM, Float.valueOf(0.0f)).floatValue();
    }

    @Override
    public void unregister() {
        try {
            Level level = this.getNMSLevel();
            BlockPos pos = this.getMachinePos();
            if (level != null && pos != null) {
                for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                    try {
                        BlockEntityController blockEntityController;
                        BlockEntity adjBe = BukkitBlockEntityTypes.getIfLoaded(level, pos.relative(d));
                        if (adjBe == null || !((blockEntityController = adjBe.controller) instanceof DataMachineBlockEntity)) continue;
                        DataMachineBlockEntity adj = (DataMachineBlockEntity)blockEntityController;
                        adj.invalidateRpm(level);
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        super.unregister();
        if (this.rendererManager != null) {
            this.rendererManager.close();
            this.rendererManager = null;
        }
    }

    public record GasSpec(float rpm, float su, int gasPerTick) {
    }

    private static final class Agg {
        int unlocked = 3;
        double overclockLimit = 0.0;
        double consume = 0.0;
        double generation = 0.0;

        private Agg() {
        }
    }
}

