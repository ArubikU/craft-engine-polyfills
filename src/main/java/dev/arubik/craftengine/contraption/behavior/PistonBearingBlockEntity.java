/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.libraries.nbt.CompoundTag
 *  org.bukkit.Bukkit
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
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.listener.BearingHammerListener;
import dev.arubik.craftengine.contraption.type.LinearContraptionType;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.rotation.RpmProvider;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Bukkit;
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

public class PistonBearingBlockEntity
extends AbstractMachineBlockEntity {
    public static final int UPGRADE_SLOTS = 9;
    private static final int BASE_UNLOCKED = 3;
    private static final int DEFAULT_MENU_SIZE = 27;
    private int distance;
    private double speedBlocksPerSec;
    private PistonBearingBehavior.Mode mode;
    private long roundRobinDelayTicks;
    private final int defaultDistance;
    private final double defaultSpeed;
    private final PistonBearingBehavior.Mode defaultMode;
    private final long defaultRoundRobinDelay;
    private final double suPerBlock;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;
    private static final ItemStack FILLER = MenuText.emptyFiller();
    private float overclock = 0.0f;
    private double curGeneration = 0.0;
    private double curOverclockLimit = 0.0;
    private double curFuelEff = 0.0;
    private int curUnlocked = 3;
    private float inputRpm = 0.0f;
    private int page = 0;
    private MachineMenu active;
    private int shownUnlocked = -1;
    private static final TypedKey<Integer> KEY_DISTANCE = TypedKey.of("craftengine", "bearing_distance", NbtType.INTEGER);
    private static final TypedKey<Float> KEY_SPEED = TypedKey.of("craftengine", "bearing_speed", NbtType.FLOAT);
    private static final TypedKey<String> KEY_MODE = TypedKey.of("craftengine", "bearing_mode", NbtType.STRING);
    private static final TypedKey<Long> KEY_DWELL = TypedKey.of("craftengine", "bearing_dwell", NbtType.LONG);
    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "bearing_overclock", NbtType.FLOAT);
    private final boolean inert;
    private UUID cachedWorldId;
    private boolean prevRedstone = false;
    private int attachedBlocks = 1;
    private long homeDwellTimer = 0L;

    public PistonBearingBlockEntity(BlockEntity blockEntity, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig, int defaultDistance, double defaultSpeed, PistonBearingBehavior.Mode defaultMode, long defaultRoundRobinDelay, double suPerBlock) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new HashMap() : upgradeDefs;
        this.bars = bars == null ? new ArrayList() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : PistonBearingBlockEntity.defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 27;
        this.defaultDistance = Math.max(1, defaultDistance);
        this.defaultSpeed = defaultSpeed;
        this.defaultMode = defaultMode == null ? PistonBearingBehavior.Mode.LINEAR : defaultMode;
        this.defaultRoundRobinDelay = Math.max(0L, defaultRoundRobinDelay);
        this.suPerBlock = suPerBlock;
        this.distance = this.defaultDistance;
        this.speedBlocksPerSec = this.defaultSpeed;
        this.mode = this.defaultMode;
        this.roundRobinDelayTicks = this.defaultRoundRobinDelay;
        this.setIOConfiguration(new IOConfiguration.Closed());
        this.setMaxStackSize(64);
        this.requiresRedstone = false;
        boolean inertPart = false;
        try {
            Integer pi;
            Comparable v;
            Property partProp;
            ImmutableBlockState ce = blockEntity.blockState();
            if (ce != null && (partProp = ce.getProperty("part")) != null && (v = ce.get(partProp)) instanceof Integer && (pi = (Integer)v) != 0) {
                inertPart = true;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.inert = inertPart;
    }

    public boolean isInert() {
        return this.inert;
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(27, null, new int[0], new int[0], new int[0], new ArrayList<MachineMenuConfig.Button>(), -1);
    }

    @Override
    protected String getMachineId() {
        return "linear_bearing";
    }

    @Override
    protected boolean requiresFuel() {
        return false;
    }

    public int distance() {
        return Math.max(1, this.distance);
    }

    public double speedBlocksPerSec() {
        return this.speedBlocksPerSec;
    }

    public PistonBearingBehavior.Mode mode() {
        return this.mode == null ? PistonBearingBehavior.Mode.LINEAR : this.mode;
    }

    public long roundRobinDelayTicks() {
        return Math.max(0L, this.roundRobinDelayTicks);
    }

    public double suPerBlock() {
        return this.suPerBlock;
    }

    public UpgradeModifiers upgradeModifiers() {
        this.recomputeUpgrades();
        return this.upgradeModifiers;
    }

    @Override
    public int[] getUpgradeSlots() {
        int[] s = new int[9];
        for (int i = 0; i < 9; ++i) {
            s[i] = i;
        }
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

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < 9; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(i);
            if (m == null) continue;
            all.addAll(m);
        }
        int extra = (int)Math.round(MachineAttributes.compute(all).getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(3, Math.min(9, 3 + extra));
        ArrayList<MachineAttributes.Mod> active = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.curUnlocked; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(i);
            if (m == null) continue;
            active.addAll(m);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(active);
        this.curGeneration = PistonBearingBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = PistonBearingBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = PistonBearingBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);
        this.overclock = (float)PistonBearingBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        double speed = Math.max(0.05, 1.0 + this.curGeneration + (double)this.overclock);
        double fuel = PistonBearingBlockEntity.clamp(1.0 - this.curFuelEff, 0.05, 4.0);
        this.upgradeModifiers = new UpgradeModifiers(speed, fuel, 0.0);
    }

    public double previewSuPerBlock() {
        double distanceMult = 1.0 + 0.1 * (double)Math.max(0, this.distance() - 1);
        return this.suPerBlock * distanceMult * this.upgradeModifiers.fuelMultiplier();
    }

    public double previewSpeed() {
        double base = this.inputRpm > 0.0f ? (double)this.inputRpm / 60.0 : this.speedBlocksPerSec;
        return base * this.upgradeModifiers.speedMultiplier();
    }

    @Override
    public double[] barStat(String id) {
        if ("rpm".equals(id)) {
            return new double[]{this.inputRpm > 0.0f ? 100.0 : 0.0, 100.0};
        }
        return super.barStat(id);
    }

    @Override
    public Map<String, String> barPlaceholders(String id) {
        if ("rpm".equals(id)) {
            HashMap<String, String> m = new HashMap<String, String>();
            m.put("rpm", String.valueOf((int)this.inputRpm));
            m.put("req", String.valueOf((int)Math.round(this.previewSpeed() * 60.0)));
            m.put("su", String.valueOf((int)Math.round(this.previewSuPerBlock() * (double)this.attachedBlocks)));
            m.put("blocks", String.valueOf(this.attachedBlocks));
            m.put("speed", String.format(Locale.ROOT, "%.2f", this.previewSpeed()));
            return m;
        }
        return super.barPlaceholders(id);
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (this.inert) {
            return;
        }
        if (this.active != null) {
            this.active.tick();
        }
        if (!level.isClientSide()) {
            this.inputRpm = 0.0f;
            float bestPot = 0.0f;
            net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(this.getMachinePos().getX(), this.getMachinePos().getY(), this.getMachinePos().getZ());
            for (Direction d : Direction.values()) {
                RpmProvider p;
                BlockEntityController blockEntityController;
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(d));
                if (be == null || !((blockEntityController = be.controller) instanceof RpmProvider) || !(p = (RpmProvider)blockEntityController).isRpmSource() || !(p.potentialRpm() > bestPot) || !p.rpmReaches(cePos)) continue;
                bestPot = p.potentialRpm();
                this.inputRpm = p.getRpm();
            }
        }
        if (!level.isClientSide() && level instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)level;
            this.cachedWorldId = sl.getWorld().getUID();
            this.redstoneDriver(sl);
        }
        super.tick(level, pos, state);
        if (!level.isClientSide()) {
            this.refreshUpgradePageIfNeeded();
        }
    }

    private void redstoneDriver(ServerLevel sl) {
        ContraptionEntity entity;
        boolean redstone;
        BlockPos pos = this.getMachinePos();
        try {
            redstone = sl.hasNeighborSignal(pos);
        }
        catch (Throwable t) {
            return;
        }
        boolean rising = redstone && !this.prevRedstone;
        this.prevRedstone = redstone;
        CraftWorld world = sl.getWorld();
        ResourceKey worldId = world.getHandle().dimension();
        UUID cid = BearingHammerListener.assembledContraptionAt((ResourceKey<Level>)worldId, pos);
        ContraptionEntity contraptionEntity = entity = cid != null ? ContraptionManager.get(cid) : null;
        if (entity != null && entity.state().level() != null) {
            this.attachedBlocks = Math.max(1, entity.state().level().blockCount());
        } else {
            Vec3 f = BearingBlockBehavior.facingVecAt((Level)sl, pos);
            BlockPos front = pos.offset((int)Math.round(f.x), (int)Math.round(f.y), (int)Math.round(f.z));
            this.attachedBlocks = Math.max(1, GlueRegistry.structureAt((ResourceKey<Level>)sl.dimension(), front).size());
        }
        if (entity == null) {
            boolean go;
            if (LinearContraptionType.isExtendedSolid((ResourceKey<Level>)worldId, pos)) {
                this.homeDwellTimer = 0L;
                return;
            }
            if (this.mode() == PistonBearingBehavior.Mode.ROUND_ROBIN) {
                if (redstone) {
                    go = ++this.homeDwellTimer >= Math.max(1L, this.roundRobinDelayTicks);
                } else {
                    this.homeDwellTimer = 0L;
                    go = false;
                }
            } else {
                go = rising;
            }
            if (go) {
                this.homeDwellTimer = 0L;
                ContraptionEntity e = ContraptionAssembler.assemblePiston((World)world, pos);
                if (e != null) {
                    BearingHammerListener.markAssembled((ResourceKey<Level>)worldId, pos, e.state().id());
                }
            }
            return;
        }
        this.homeDwellTimer = 0L;
        PistonBearingBehavior piston = null;
        for (MovementBehavior b : entity.state().behaviors()) {
            PistonBearingBehavior p;
            if (!(b instanceof PistonBearingBehavior)) continue;
            piston = p = (PistonBearingBehavior)b;
            break;
        }
        if (piston != null && piston.isFullyRetracted()) {
            try {
                ContraptionAssembler.disassemble((World)world, entity);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            BearingHammerListener.forgetAssembled(cid);
        }
    }

    private void pushToLiveContraption() {
        if (this.cachedWorldId == null) {
            return;
        }
        BlockPos pos = this.getMachinePos();
        ResourceKey worldId = ((CraftWorld)Bukkit.getWorld((UUID)this.cachedWorldId)).getHandle().dimension();
        UUID cid = BearingHammerListener.assembledContraptionAt((ResourceKey<Level>)worldId, pos);
        if (cid == null) {
            return;
        }
        ContraptionEntity entity = ContraptionManager.get(cid);
        if (entity == null) {
            return;
        }
        for (MovementBehavior b : entity.state().behaviors()) {
            if (!(b instanceof PistonBearingBehavior)) continue;
            PistonBearingBehavior piston = (PistonBearingBehavior)b;
            piston.setMode(this.mode());
            piston.setMaxDistance(this.distance);
            piston.setBaseSpeedBlocksPerSec(this.speedBlocksPerSec * this.upgradeModifiers().speedMultiplier());
            piston.setRoundRobinDelayTicks(this.roundRobinDelayTicks);
            break;
        }
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide()) {
            return;
        }
        this.recomputeUpgrades();
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
        this.active = new MachineMenu(this, this.getLayout());
        this.active.syncFromMachine();
        this.active.open(player);
        this.shownUnlocked = this.curUnlocked;
    }

    private void refreshUpgradePageIfNeeded() {
        if (this.page != 1 || this.active == null || this.shownUnlocked == this.curUnlocked) {
            return;
        }
        ArrayList<HumanEntity> viewers = new ArrayList(this.active.getInventory().getViewers());
        this.shownUnlocked = this.curUnlocked;
        for (HumanEntity h : viewers) {
            if (!(h instanceof org.bukkit.entity.Player)) continue;
            org.bukkit.entity.Player p = (org.bukkit.entity.Player)h;
            this.openPage(p, 1);
        }
    }

    private MachineLayout buildMainLayout() {
        int infoSlot;
        MachineLayout l = new MachineLayout(InventoryType.CHEST, this.menuSize, "Linear Bearing");
        Component title = null;
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.bearing_title", NamedTextColor.AQUA)));
        for (MachineMenuConfig.Button b : this.menuConfig.buttons) {
            this.installButton(l, b);
        }
        MachineBars.install(l, this.bars);
        int n = infoSlot = this.menuConfig.infoSlot >= 0 ? this.menuConfig.infoSlot : -1;
        if (infoSlot >= 0) {
            l.setDynamicProvider(infoSlot, (m, t) -> ((PistonBearingBlockEntity)m).infoIcon());
        }
        this.fillRest(l);
        return l;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        BearingControl control = BearingControl.of(b);
        if (control != null) {
            l.addClickButton(b.slot, (m, t) -> {
                PistonBearingBlockEntity s = (PistonBearingBlockEntity)m;
                return MenuText.iconItem(PistonBearingBlockEntity.parseKey(b.icon), Material.PAPER, PistonBearingBlockEntity.label(b.name, NamedTextColor.AQUA), s.controlLore(control, b.lore));
            }, (m, p, c) -> ((PistonBearingBlockEntity)m).mutate(control, c));
            return;
        }
        l.addButton(b.slot, (m, t) -> {
            PistonBearingBlockEntity s = (PistonBearingBlockEntity)m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(PistonBearingBlockEntity.parseKey(iconSpec), Material.PAPER, PistonBearingBlockEntity.label(b.name, NamedTextColor.AQUA), PistonBearingBlockEntity.lore(b.lore));
        }, (m, p) -> {
            PistonBearingBlockEntity s = (PistonBearingBlockEntity)m;
            if (s.isButtonLocked(b)) {
                return;
            }
            switch (b.action.kind) {
                case OPEN_PAGE: {
                    s.openPage((org.bukkit.entity.Player)p, b.action.page);
                    break;
                }
            }
        });
    }

    private void mutate(BearingControl control, ClickType c) {
        boolean up = c != ClickType.RIGHT && c != ClickType.SHIFT_RIGHT;
        switch (control.ordinal()) {
            case 0: {
                this.distance = (int)PistonBearingBlockEntity.clamp(this.distance + (up ? 1 : -1), 1.0, 64.0);
                break;
            }
            case 1: {
                double step = c == ClickType.SHIFT_LEFT || c == ClickType.SHIFT_RIGHT ? 1.0 : 0.25;
                this.speedBlocksPerSec = PistonBearingBlockEntity.clamp(this.speedBlocksPerSec + (up ? step : -step), 0.05, 32.0);
                break;
            }
            case 2: {
                this.mode = PistonBearingBlockEntity.cycleMode(this.mode, up);
                break;
            }
            case 3: {
                long step = c == ClickType.SHIFT_LEFT || c == ClickType.SHIFT_RIGHT ? 100L : 20L;
                this.roundRobinDelayTicks = Math.max(0L, this.roundRobinDelayTicks + (up ? step : -step));
            }
        }
        this.setChanged();
        this.pushToLiveContraption();
    }

    private static PistonBearingBehavior.Mode cycleMode(PistonBearingBehavior.Mode m, boolean up) {
        PistonBearingBehavior.Mode[] vals = PistonBearingBehavior.Mode.values();
        int idx = m == null ? 0 : m.ordinal();
        idx = (idx + (up ? 1 : vals.length - 1)) % vals.length;
        return vals[idx];
    }

    private Component[] controlLore(BearingControl control, List<String> configured) {
        ArrayList<Component> out = new ArrayList<Component>();
        if (configured != null) {
            for (String line : configured) {
                out.add(PistonBearingBlockEntity.label(line, NamedTextColor.GRAY));
            }
        }
        String value = switch (control.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.distance() + " blocks";
            case 1 -> String.format(Locale.ROOT, "%.2f b/s", this.speedBlocksPerSec);
            case 2 -> this.mode().name().toLowerCase(Locale.ROOT);
            case 3 -> this.roundRobinDelayTicks() + " ticks";
        };
        out.add(MenuText.kv("polyfill.ui.value", NamedTextColor.GRAY, value, NamedTextColor.WHITE));
        return out.toArray(new Component[0]);
    }

    private ItemStack infoIcon() {
        this.recomputeUpgrades();
        NamedTextColor GRAY = NamedTextColor.GRAY;
        NamedTextColor WHITE = NamedTextColor.WHITE;
        NamedTextColor AQUA = NamedTextColor.AQUA;
        ItemStack stack = new ItemStack(Material.PISTON);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.bearing_title", AQUA)));
        meta.lore(List.of(MenuText.noI(MenuText.kv("polyfill.ui.distance", GRAY, "" + this.distance(), WHITE)), MenuText.noI(MenuText.kv("polyfill.ui.speed", GRAY, String.format(Locale.ROOT, "%.2f b/s", this.previewSpeed()), WHITE)), MenuText.noI(MenuText.kv("polyfill.ui.mode", GRAY, this.mode().name().toLowerCase(Locale.ROOT), WHITE)), MenuText.noI(MenuText.kv("polyfill.ui.rpm", GRAY, "" + (int)this.inputRpm, WHITE)), MenuText.noI(MenuText.kv("polyfill.ui.su", GRAY, (int)Math.round(this.previewSuPerBlock()) + " /block", WHITE))));
        stack.setItemMeta(meta);
        return stack;
    }

    private boolean isButtonLocked(MachineMenuConfig.Button b) {
        return b.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && this.curOverclockLimit <= 0.0;
    }

    private static Component label(String s, NamedTextColor color) {
        return MenuText.textOrTranslatable(s, color);
    }

    private static Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return new Component[0];
        }
        Component[] out = new Component[lines.size()];
        for (int i = 0; i < lines.size(); ++i) {
            out[i] = PistonBearingBlockEntity.label(lines.get(i), NamedTextColor.GRAY);
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
        int unlocked = this.curUnlocked;
        MachineLayout l = new MachineLayout(InventoryType.CHEST, 18, "Upgrades");
        Component title = null;
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.AQUA)));
        for (int i = 0; i < 9; ++i) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
                continue;
            }
            l.setDynamicProvider(i, (m, t) -> MenuText.lockedIcon(MenuText.tr("polyfill.ui.locked", NamedTextColor.RED), MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
        }
        l.addButton(17, (m, t) -> MenuText.backIcon(), (m, p) -> ((PistonBearingBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0));
        this.fillRest(l);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = OverclockMenu.build(this.getMachineId(), NamedTextColor.AQUA, () -> this.overclock, () -> this.curOverclockLimit, (up, c) -> this.bumpOverclock((boolean)up, (ClickType)c), p -> this.openPage((org.bukkit.entity.Player)p, 0));
        this.fillRest(l);
        return l;
    }

    public void bumpOverclock(boolean up, ClickType c) {
        float delta = OverclockMenu.step(c);
        this.overclock += up ? delta : -delta;
        this.overclock = (float)PistonBearingBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.setChanged();
    }

    private void fillRest(MachineLayout l) {
        for (int i = 0; i < l.getSize(); ++i) {
            if (l.getSlotType(i) != MenuSlotType.BACKGROUND) continue;
            l.setDynamicProvider(i, (m, t) -> FILLER);
        }
    }

    @Override
    public void saveCustomData(CompoundTag tag) {
        this.set(KEY_DISTANCE, this.distance);
        this.set(KEY_SPEED, Float.valueOf((float)this.speedBlocksPerSec));
        this.set(KEY_MODE, this.mode().name());
        this.set(KEY_DWELL, this.roundRobinDelayTicks);
        this.set(KEY_OC, Float.valueOf(this.overclock));
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.distance = this.getOrDefault(KEY_DISTANCE, this.defaultDistance);
        this.speedBlocksPerSec = this.getOrDefault(KEY_SPEED, Float.valueOf((float)this.defaultSpeed)).floatValue();
        this.mode = PistonBearingBehavior.Mode.fromString(this.getOrDefault(KEY_MODE, this.defaultMode.name()));
        this.roundRobinDelayTicks = this.getOrDefault(KEY_DWELL, this.defaultRoundRobinDelay);
        this.overclock = this.getOrDefault(KEY_OC, Float.valueOf(0.0f)).floatValue();
    }

    private static enum BearingControl {
        DISTANCE,
        SPEED,
        MODE,
        DWELL;


        static BearingControl of(MachineMenuConfig.Button b) {
            if (b == null || b.name == null) {
                return null;
            }
            String n = b.name.toLowerCase(Locale.ROOT);
            if (n.contains("distance")) {
                return DISTANCE;
            }
            if (n.contains("speed")) {
                return SPEED;
            }
            if (n.contains("mode")) {
                return MODE;
            }
            if (n.contains("dwell") || n.contains("round_robin") || n.contains("delay")) {
                return DWELL;
            }
            return null;
        }
    }
}

