package dev.arubik.craftengine.contraption.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * Per-instance config controller for a LINEAR (6-directional piston) bearing block — the block
 * entity that backs {@code cml:linear_bearing}'s right-click config menu (2026-07-03 goal). Built
 * by copying the copper-fan / crusher machine pattern EXACTLY: it extends
 * {@link AbstractMachineBlockEntity} so it gets a config-driven {@link MachineMenu}, upgrade INPUT
 * slots, and NBT persistence for free; {@link BearingBlockBehavior} (an {@code EntityBlock}) creates
 * it and opens {@link #getMenu()} on {@code useWithoutItem}.
 *
 * <p>It stores the piston's per-instance tunables — {@link #distance}, {@link #speedBlocksPerSec},
 * {@link PistonBearingBehavior.Mode mode}, {@link #roundRobinDelayTicks dwell} — which the player
 * edits with the menu's control buttons, and which {@code ContraptionAssembler.attachDefaultBehavior}
 * reads (falling back to the block YAML defaults on {@link BearingBlockBehavior} when no block entity
 * exists) to build the {@link PistonBearingBehavior}.
 *
 * <p>rpm/su handling mirrors {@code CrusherBlockEntity}: every tick it scans the 6 neighbors for the
 * strongest adjacent {@link RpmProvider} to surface the bearing's LIVE rpm (what the piston will draw
 * from once assembled) and the SU it will demand, shown in the menu's rpm gauge. The SU figure uses
 * the SAME {@code suPerBlock * distanceMultiplier} scaling {@link PistonBearingBehavior} reports to the
 * real motor each tick, so the menu preview matches the running load.
 *
 * <p>Upgrades use the SAME {@code upgrades:} config form as the fan/crusher ({@link MachineAttributes}
 * item→{@link Mod} defs): GENERATION raises effective speed, FUEL_EFFICIENCY cuts the SU drawn,
 * OVERCLOCK_LIMIT unlocks the player overclock, EXTRA_SLOTS unlocks more upgrade slots. They are folded
 * into a single {@link UpgradeModifiers} ({@code speed = 1+generation+overclock}, {@code fuel = 1−eff})
 * exposed via {@link #upgradeModifiers()} — the exact object the assembler multiplies against.
 */
public class PistonBearingBlockEntity extends AbstractMachineBlockEntity {

    public static final int UPGRADE_SLOTS = 9; // container indices 0..8 (same as fan/crusher)
    private static final int BASE_UNLOCKED = 3;
    private static final int DEFAULT_MENU_SIZE = 27;

    // Per-instance config (seeded from the block YAML defaults, then player-tunable via the menu).
    private int distance;
    private double speedBlocksPerSec;
    private PistonBearingBehavior.Mode mode;
    private long roundRobinDelayTicks;

    // Config knobs handed down from the behavior (defaults + su cost per captured block).
    private final int defaultDistance;
    private final double defaultSpeed;
    private final PistonBearingBehavior.Mode defaultMode;
    private final long defaultRoundRobinDelay;
    private final double suPerBlock;

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;

    private static final org.bukkit.inventory.ItemStack FILLER = MenuText.emptyFiller();

    // Player-tunable overclock fraction in [-limit, +limit]; raises speed AND rpm/su demand.
    private float overclock = 0f;

    // Derived each tick from installed upgrades + overclock.
    private double curGeneration = 0;
    private double curOverclockLimit = 0;
    private double curFuelEff = 0;
    private int curUnlocked = BASE_UNLOCKED;

    // Live rpm read from the strongest adjacent motor (0 = no motor -> fallback speed).
    private float inputRpm = 0f;

    private int page = 0; // 0 main, 1 upgrades, 2 overclock
    private MachineMenu active;
    private int shownUnlocked = -1;

    private static final TypedKey<Integer> KEY_DISTANCE = TypedKey.of("craftengine", "bearing_distance",
            NbtType.INTEGER);
    private static final TypedKey<Float> KEY_SPEED = TypedKey.of("craftengine", "bearing_speed",
            NbtType.FLOAT);
    private static final TypedKey<String> KEY_MODE = TypedKey.of("craftengine", "bearing_mode",
            NbtType.STRING);
    private static final TypedKey<Long> KEY_DWELL = TypedKey.of("craftengine", "bearing_dwell",
            NbtType.LONG);
    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "bearing_overclock",
            NbtType.FLOAT);

    public PistonBearingBlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars, MachineMenuConfig menuConfig,
            int defaultDistance, double defaultSpeed, PistonBearingBehavior.Mode defaultMode,
            long defaultRoundRobinDelay, double suPerBlock) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : DEFAULT_MENU_SIZE);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : DEFAULT_MENU_SIZE;
        this.defaultDistance = Math.max(1, defaultDistance);
        this.defaultSpeed = defaultSpeed;
        this.defaultMode = defaultMode == null ? PistonBearingBehavior.Mode.LINEAR : defaultMode;
        this.defaultRoundRobinDelay = Math.max(0, defaultRoundRobinDelay);
        this.suPerBlock = suPerBlock;

        // Seed the live config from the definition defaults (loadCustomData overrides on reload).
        this.distance = this.defaultDistance;
        this.speedBlocksPerSec = this.defaultSpeed;
        this.mode = this.defaultMode;
        this.roundRobinDelayTicks = this.defaultRoundRobinDelay;

        // Upgrade module storage in container slots 0..8. No IO faces / recipes — a bearing is a pure
        // config block, so no IOConfiguration item roles are declared (upgrade slots aren't hopper-piped).
        setIOConfiguration(new IOConfiguration.Closed());
        setMaxStackSize(64);
        this.requiresRedstone = false;

        // Decorative piston-pole pieces (part=1/2/3: body/head/shaft, placed as REAL blocks by the
        // euler drop) must stay INERT — no redstone-driven assembly, no menu (2026-07-04 fix — this
        // used to be enforced by BearingBlockBehavior#createBlockEntityController returning null for
        // part!=0, which crashed CraftEngine's OWN chunk deserializer on a persisted world whose
        // schema had since drifted ("Cannot invoke BlockEntityController.hasElement() because
        // this.controller is null" — CraftEngine's BlockEntity constructor unconditionally assumes a
        // non-null controller once NBT records one existed; returning null there is never safe). The
        // controller is now ALWAYS created; this flag is the ONLY place part!=0 is special-cased.
        boolean inertPart = false;
        try {
            net.momirealms.craftengine.core.block.ImmutableBlockState ce = blockEntity.blockState();
            if (ce != null) {
                @SuppressWarnings("rawtypes")
                net.momirealms.craftengine.core.block.property.Property partProp = ce.getProperty("part");
                if (partProp != null) {
                    Object v = ce.get(partProp);
                    if (v instanceof Integer pi && pi != 0) {
                        inertPart = true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        this.inert = inertPart;
    }

    /** True for a decorative body/head/shaft piece (part!=0) — see constructor javadoc. */
    private final boolean inert;

    public boolean isInert() {
        return inert;
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(DEFAULT_MENU_SIZE, null, new int[0], new int[0], new int[0],
                new ArrayList<>(), -1);
    }

    @Override
    protected String getMachineId() {
        return "linear_bearing";
    }

    @Override
    protected boolean requiresFuel() {
        return false; // mechanical/config block — no fuel
    }

    // ---------------- per-instance config accessors (read by the assembler) ----------------

    /** Configured piston travel distance in blocks (after any upgrades/overclock have NOT been applied). */
    public int distance() {
        return Math.max(1, distance);
    }

    /** Configured base traction speed (blocks/sec) BEFORE upgrade/overclock speed multiplier. */
    public double speedBlocksPerSec() {
        return speedBlocksPerSec;
    }

    public PistonBearingBehavior.Mode mode() {
        return mode == null ? PistonBearingBehavior.Mode.LINEAR : mode;
    }

    public long roundRobinDelayTicks() {
        return Math.max(0, roundRobinDelayTicks);
    }

    public double suPerBlock() {
        return suPerBlock;
    }

    /**
     * Aggregate upgrade + overclock effect the assembler multiplies against (same object the crusher
     * exposes): {@code speedMultiplier = 1 + generation + overclock}, {@code fuelMultiplier = 1 − eff}.
     * A LOWER fuelMultiplier means the piston draws LESS su per block (efficiency upgrades). yieldBonus
     * is unused for a bearing.
     */
    public UpgradeModifiers upgradeModifiers() {
        recomputeUpgrades();
        return this.upgradeModifiers;
    }

    // ---------------- upgrades (attribute system, exactly like the crusher) ----------------

    @Override
    public int[] getUpgradeSlots() {
        int[] s = new int[UPGRADE_SLOTS];
        for (int i = 0; i < UPGRADE_SLOTS; i++)
            s[i] = i;
        return s;
    }

    private Key itemId(int slot) {
        ItemStack nms = getItem(slot);
        if (nms == null || nms.isEmpty())
            return null;
        org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
        Key custom = CraftEngineItems.getCustomItemId(b);
        if (custom != null)
            return custom;
        org.bukkit.NamespacedKey nk = b.getType().getKey();
        return Key.of(nk.getNamespace(), nk.getKey());
    }

    private List<Mod> modsOf(int slot) {
        Key id = itemId(slot);
        return id == null ? null : upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        // Pass 1: unlocked-slot count from EXTRA_SLOTS across all installed upgrades.
        List<Mod> all = new ArrayList<>();
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(MachineAttributes.compute(all)
                .getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(BASE_UNLOCKED, Math.min(UPGRADE_SLOTS, BASE_UNLOCKED + extra));

        // Pass 2: effect attributes only from upgrades in unlocked slots.
        List<Mod> active = new ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                active.addAll(m);
        }
        var attrs = MachineAttributes.compute(active);
        this.curGeneration = clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);

        // speed = base speed factor (1 + generation + overclock); fuel = 1 − efficiency (su multiplier).
        double speed = Math.max(0.05, 1.0 + curGeneration + overclock);
        double fuel = clamp(1.0 - curFuelEff, 0.05, 4.0);
        this.upgradeModifiers = new UpgradeModifiers(speed, fuel, 0.0);
    }

    // ---------------- rpm/su preview (mirrors the crusher's motor scan) ----------------

    /**
     * The SU this bearing WILL demand from the real motor per tick once assembled, previewed here for
     * the menu: {@code suPerBlock * distanceMultiplier}, times the upgrade fuel multiplier. This is the
     * per-captured-block figure {@link PistonBearingBehavior} multiplies by the live block count at run
     * time — the menu shows the per-block rate since the structure isn't captured yet.
     */
    public double previewSuPerBlock() {
        double distanceMult = 1.0 + 0.10 * Math.max(0, distance() - 1);
        return suPerBlock * distanceMult * this.upgradeModifiers.fuelMultiplier();
    }

    /** Effective traction speed (blocks/sec) once upgrades/overclock are applied — menu preview. */
    public double previewSpeed() {
        double base = inputRpm > 0f ? inputRpm / 60.0 : speedBlocksPerSec;
        return base * this.upgradeModifiers.speedMultiplier();
    }

    @Override
    public double[] barStat(String id) {
        if ("rpm".equals(id))
            return new double[] { inputRpm > 0f ? 100 : 0, 100 };
        return super.barStat(id);
    }

    @Override
    public java.util.Map<String, String> barPlaceholders(String id) {
        if ("rpm".equals(id)) {
            java.util.Map<String, String> m = new java.util.HashMap<>();
            m.put("rpm", String.valueOf((int) inputRpm));
            // Required rpm to reach the configured traction speed (speed = rpm / 60 — see
            // PistonBearingBehavior). Fills the %req% placeholder in the bar lore.
            m.put("req", String.valueOf((int) Math.round(previewSpeed() * 60.0)));
            // TOTAL su for the whole attached structure (per-block x attached block count) — what the
            // bearing will actually draw, not just the per-block figure.
            m.put("su", String.valueOf((int) Math.round(previewSuPerBlock() * attachedBlocks)));
            m.put("blocks", String.valueOf(attachedBlocks));
            m.put("speed", String.format(java.util.Locale.ROOT, "%.2f", previewSpeed()));
            return m;
        }
        return super.barPlaceholders(id);
    }

    // ---------------- tick: pull live rpm from the strongest adjacent motor ----------------

    @Override
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (inert) {
            return; // decorative body/head/shaft piece — no redstone/rpm/menu logic at all
        }
        if (active != null)
            active.tick();
        if (!level.isClientSide()) {
            this.inputRpm = 0f;
            float bestPot = 0f;
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(getMachinePos().getX(),
                            getMachinePos().getY(), getMachinePos().getZ());
            for (Direction d : Direction.values()) {
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, getMachinePos().relative(d));
                if (be != null && be.controller instanceof RpmProvider p && p.isRpmSource()
                        && p.potentialRpm() > bestPot) {
                    if (!p.rpmReaches(cePos))
                        continue;
                    bestPot = p.potentialRpm();
                    this.inputRpm = p.getRpm();
                }
            }
        }
        if (!level.isClientSide() && level instanceof net.minecraft.server.level.ServerLevel sl) {
            this.cachedWorldId = sl.getWorld().getUID(); // for pushToLiveContraption (menu edits)
            redstoneDriver(sl); // no-hammer: redstone assembles/extends/retracts this bearing
        }
        super.tick(level, pos, state); // recomputeUpgrades (via processTick) + menu bookkeeping
        if (!level.isClientSide())
            refreshUpgradePageIfNeeded();
    }

    /** Cached world UID (set each tick) so a menu edit can locate this bearing's live contraption. */
    private java.util.UUID cachedWorldId;
    private boolean prevRedstone = false;
    /** Blocks currently attached (assembled count, or the glued structure the bearing points at). */
    private int attachedBlocks = 1;

    /**
     * No-hammer redstone control (2026-07-03 — "quita el hammer assembly al bearing... para eso
     * existe la redstone"). Runs every server tick on the FULL bearing block. Grabs the glued
     * structure the bearing points at and assembles it itself; redstone drives it: LINEAR/EULER/
     * ROBIN_EULER pulse to extend, ROUND_ROBIN is on/off (powered = running, unpowered = return home).
     * When a returned load is fully retracted it disassembles back into real blocks.
     */
    private void redstoneDriver(net.minecraft.server.level.ServerLevel sl) {
        net.minecraft.core.BlockPos pos = getMachinePos();
        boolean redstone;
        try {
            redstone = sl.hasNeighborSignal(pos);
        } catch (Throwable t) {
            return;
        }
        boolean rising = redstone && !prevRedstone;
        prevRedstone = redstone;

        org.bukkit.World world = sl.getWorld();
        java.util.UUID worldId = world.getUID();
        java.util.UUID cid = dev.arubik.craftengine.contraption.BearingHammerListener
                .assembledContraptionAt(worldId, pos);
        dev.arubik.craftengine.contraption.ContraptionEntity entity =
                cid != null ? dev.arubik.craftengine.contraption.ContraptionManager.get(cid) : null;

        // Cache how many blocks are/will be moved, for the menu's live total su/rpm preview
        // (2026-07-03 — "el ui no muestra el rpm y su que usara ... de lo que esta pegado").
        if (entity != null && entity.state().level() != null) {
            this.attachedBlocks = Math.max(1, entity.state().level().blockCount());
        } else {
            net.minecraft.world.phys.Vec3 f = dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior
                    .facingVecAt(sl, pos);
            net.minecraft.core.BlockPos front = pos.offset((int) Math.round(f.x), (int) Math.round(f.y),
                    (int) Math.round(f.z));
            this.attachedBlocks = Math.max(1, dev.arubik.craftengine.contraption.GlueRegistry
                    .structureAt(worldId, front).size());
        }

        if (entity == null) {
            // At rest. If the load is EXTENDED-solid, EulerExtendedRegistry owns re-grab → skip.
            if (dev.arubik.craftengine.contraption.EulerExtendedRegistry.isExtendedSolid(worldId, pos)) {
                homeDwellTimer = 0;
                return;
            }
            // HOME-solid: trigger the next EXTEND. LINEAR = redstone pulse; ROUND_ROBIN = powered
            // auto-cycle after the dwell (2026-07-03 unify — solid at rest, move on trigger).
            boolean go;
            if (mode() == PistonBearingBehavior.Mode.ROUND_ROBIN) {
                if (redstone) {
                    go = (++homeDwellTimer >= Math.max(1, roundRobinDelayTicks));
                } else {
                    homeDwellTimer = 0;
                    go = false;
                }
            } else {
                go = rising;
            }
            if (go) {
                homeDwellTimer = 0;
                dev.arubik.craftengine.contraption.ContraptionEntity e =
                        dev.arubik.craftengine.contraption.ContraptionAssembler.assemblePiston(world, pos);
                if (e != null) {
                    dev.arubik.craftengine.contraption.BearingHammerListener.markAssembled(worldId, pos, e.state().id());
                }
            }
            return;
        }

        // Moving contraption: when it finishes RETRACTING, turn the load into real blocks at home.
        // (Reaching the extended end is handled by ContraptionEngine via wantsDisassembleAtEnd →
        // EulerExtendedRegistry, which drops it as real EXTENDED-solid blocks + shaft.)
        homeDwellTimer = 0;
        PistonBearingBehavior piston = null;
        for (dev.arubik.craftengine.contraption.MovementBehavior b : entity.state().behaviors()) {
            if (b instanceof PistonBearingBehavior p) {
                piston = p;
                break;
            }
        }
        if (piston != null && piston.isFullyRetracted()) {
            try {
                dev.arubik.craftengine.contraption.ContraptionAssembler.disassemble(world, entity);
            } catch (Throwable ignored) {
            }
            dev.arubik.craftengine.contraption.BearingHammerListener.forgetAssembled(cid);
        }
    }

    private long homeDwellTimer = 0;

    /**
     * Pushes the current config to a LIVE (assembled) piston contraption so menu edits take effect
     * immediately, not only on the next assembly (2026-07-03 — "cambiar el modo mientras esta
     * encendido"). No-op if this bearing isn't currently assembled.
     */
    private void pushToLiveContraption() {
        if (cachedWorldId == null) {
            return;
        }
        net.minecraft.core.BlockPos pos = getMachinePos();
        java.util.UUID cid = dev.arubik.craftengine.contraption.BearingHammerListener
                .assembledContraptionAt(cachedWorldId, pos);
        if (cid == null) {
            return;
        }
        dev.arubik.craftengine.contraption.ContraptionEntity entity =
                dev.arubik.craftengine.contraption.ContraptionManager.get(cid);
        if (entity == null) {
            return;
        }
        for (dev.arubik.craftengine.contraption.MovementBehavior b : entity.state().behaviors()) {
            if (b instanceof PistonBearingBehavior piston) {
                piston.setMode(mode());
                piston.setMaxDistance(distance);
                // Mirror the assembler: live speed = config speed x upgrade speedMultiplier.
                piston.setBaseSpeedBlocksPerSec(speedBlocksPerSec * upgradeModifiers().speedMultiplier());
                piston.setRoundRobinDelayTicks(roundRobinDelayTicks);
                break;
            }
        }
    }

    // The 6-neighbor motor scan above reuses the crusher's RpmProvider lookup contract; see RpmProvider.

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;
        recomputeUpgrades();
        // A bearing has no recipe loop — it is a pure config block. Nothing else to process.
    }

    // ---------------- unused slot-recipe abstracts ----------------

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

    // ---------------- multi-page menu ----------------

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
        this.active = new MachineMenu(this, getLayout());
        this.active.syncFromMachine();
        this.active.open(player);
        this.shownUnlocked = curUnlocked;
    }

    private void refreshUpgradePageIfNeeded() {
        if (page != 1 || active == null || shownUnlocked == curUnlocked)
            return;
        java.util.List<org.bukkit.entity.HumanEntity> viewers =
                new ArrayList<>(active.getInventory().getViewers());
        shownUnlocked = curUnlocked;
        for (org.bukkit.entity.HumanEntity h : viewers) {
            if (h instanceof org.bukkit.entity.Player p)
                openPage(p, 1);
        }
    }

    private MachineLayout buildMainLayout() {
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, menuSize, "Linear Bearing");
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.bearing_title", NamedTextColor.AQUA)));

        // Config-driven buttons (distance/speed/mode/dwell + upgrades/overclock pages). All slots yml-driven.
        for (MachineMenuConfig.Button b : menuConfig.buttons)
            installButton(l, b);

        // Config-driven bars (rpm/su gauge). barStat("rpm") + barPlaceholders drive the live values.
        MachineBars.install(l, bars);

        // Reusable info icon (distance/speed/mode/su/rpm) at the configurable info slot (-1 = none).
        int infoSlot = menuConfig.infoSlot >= 0 ? menuConfig.infoSlot : -1;
        if (infoSlot >= 0)
            l.setDynamicProvider(infoSlot, (m, t) -> ((PistonBearingBlockEntity) m).infoIcon());

        fillRest(l);
        return l;
    }

    /**
     * Render + wire one config-declared button. Adds the bearing-specific control actions on top of the
     * shared OPEN_PAGE (upgrades/overclock) handling: a button whose {@code name} is a bearing control
     * keyword mutates the stored config. Left click increments, right click decrements (mode cycles).
     */
    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        BearingControl control = BearingControl.of(b);
        if (control != null) {
            l.addClickButton(b.slot, (m, t) -> {
                PistonBearingBlockEntity s = (PistonBearingBlockEntity) m;
                return MenuText.iconItem(parseKey(b.icon), Material.PAPER,
                        label(b.name, NamedTextColor.AQUA), s.controlLore(control, b.lore));
            }, (m, p, c) -> ((PistonBearingBlockEntity) m).mutate(control, c));
            return;
        }
        l.addButton(b.slot, (m, t) -> {
            PistonBearingBlockEntity s = (PistonBearingBlockEntity) m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(parseKey(iconSpec), Material.PAPER,
                    label(b.name, NamedTextColor.AQUA), lore(b.lore));
        }, (m, p) -> {
            PistonBearingBlockEntity s = (PistonBearingBlockEntity) m;
            if (s.isButtonLocked(b))
                return;
            switch (b.action.kind) {
                case OPEN_PAGE -> s.openPage(p, b.action.page);
                case DEPLETE_FLUID, DEPLETE_GAS, NONE -> {
                }
            }
        });
    }

    /** Which bearing control (if any) a config button drives, keyed off its {@code name} lang key. */
    private enum BearingControl {
        DISTANCE, SPEED, MODE, DWELL;

        static BearingControl of(MachineMenuConfig.Button b) {
            if (b == null || b.name == null)
                return null;
            String n = b.name.toLowerCase(java.util.Locale.ROOT);
            if (n.contains("distance"))
                return DISTANCE;
            if (n.contains("speed"))
                return SPEED;
            if (n.contains("mode"))
                return MODE;
            if (n.contains("dwell") || n.contains("round_robin") || n.contains("delay"))
                return DWELL;
            return null;
        }
    }

    /** Apply a control button click: left = up/next, right = down/prev. Persists via setChanged(). */
    private void mutate(BearingControl control, ClickType c) {
        boolean up = !(c == ClickType.RIGHT || c == ClickType.SHIFT_RIGHT);
        switch (control) {
            case DISTANCE -> distance = (int) clamp(distance + (up ? 1 : -1), 1, 64);
            case SPEED -> {
                double step = (c == ClickType.SHIFT_LEFT || c == ClickType.SHIFT_RIGHT) ? 1.0 : 0.25;
                speedBlocksPerSec = clamp(speedBlocksPerSec + (up ? step : -step), 0.05, 32.0);
            }
            case MODE -> mode = cycleMode(mode, up);
            case DWELL -> {
                long step = (c == ClickType.SHIFT_LEFT || c == ClickType.SHIFT_RIGHT) ? 100 : 20;
                roundRobinDelayTicks = Math.max(0, roundRobinDelayTicks + (up ? step : -step));
            }
        }
        setChanged();
        pushToLiveContraption(); // apply the edit to a running contraption immediately
    }

    private static PistonBearingBehavior.Mode cycleMode(PistonBearingBehavior.Mode m, boolean up) {
        PistonBearingBehavior.Mode[] vals = PistonBearingBehavior.Mode.values();
        int idx = m == null ? 0 : m.ordinal();
        idx = (idx + (up ? 1 : vals.length - 1)) % vals.length;
        return vals[idx];
    }

    /** Lore for a control button: the configured lore lines PLUS the current value line. */
    private Component[] controlLore(BearingControl control, List<String> configured) {
        List<Component> out = new ArrayList<>();
        if (configured != null)
            for (String line : configured)
                out.add(label(line, NamedTextColor.GRAY));
        String value = switch (control) {
            case DISTANCE -> distance() + " blocks";
            case SPEED -> String.format(java.util.Locale.ROOT, "%.2f b/s", speedBlocksPerSec);
            case MODE -> mode().name().toLowerCase(java.util.Locale.ROOT);
            case DWELL -> roundRobinDelayTicks() + " ticks";
        };
        out.add(MenuText.kv("polyfill.ui.value", NamedTextColor.GRAY, value, NamedTextColor.WHITE));
        return out.toArray(new Component[0]);
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        recomputeUpgrades();
        var GRAY = NamedTextColor.GRAY;
        var WHITE = NamedTextColor.WHITE;
        var AQUA = NamedTextColor.AQUA;
        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(Material.PISTON);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.bearing_title", AQUA)));
        meta.lore(java.util.List.of(
                MenuText.noI(MenuText.kv("polyfill.ui.distance", GRAY, distance() + "", WHITE)),
                MenuText.noI(MenuText.kv("polyfill.ui.speed", GRAY,
                        String.format(java.util.Locale.ROOT, "%.2f b/s", previewSpeed()), WHITE)),
                MenuText.noI(MenuText.kv("polyfill.ui.mode", GRAY,
                        mode().name().toLowerCase(java.util.Locale.ROOT), WHITE)),
                MenuText.noI(MenuText.kv("polyfill.ui.rpm", GRAY, (int) inputRpm + "", WHITE)),
                MenuText.noI(MenuText.kv("polyfill.ui.su", GRAY,
                        (int) Math.round(previewSuPerBlock()) + " /block", WHITE))));
        stack.setItemMeta(meta);
        return stack;
    }

    private boolean isButtonLocked(MachineMenuConfig.Button b) {
        return b.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && curOverclockLimit <= 0;
    }

    private static Component label(String s, NamedTextColor color) {
        if (s == null)
            return Component.empty();
        String key = s.startsWith("lang:") ? s.substring(5) : s;
        if (key.contains(".") && !key.contains(" "))
            return MenuText.tr(key, color);
        return MenuText.lit(s, color);
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
        int unlocked = curUnlocked;
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, 18, "Upgrades");
        Component title = dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "upgrade");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.upgrades", NamedTextColor.AQUA)));
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            if (i < unlocked) {
                l.addSlot(i, MenuSlotType.INPUT);
            } else {
                l.setDynamicProvider(i, (m, t) -> MenuText.lockedIcon(
                        MenuText.tr("polyfill.ui.locked", NamedTextColor.RED),
                        MenuText.tr("polyfill.ui.locked_desc", NamedTextColor.GRAY)));
            }
        }
        l.addButton(17, (m, t) -> MenuText.backIcon(),
                (m, p) -> ((PistonBearingBlockEntity) m).openPage(p, 0));
        fillRest(l);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        MachineLayout l = dev.arubik.craftengine.machine.menu.OverclockMenu.build(
                getMachineId(), NamedTextColor.AQUA,
                () -> this.overclock, () -> this.curOverclockLimit,
                (up, c) -> bumpOverclock(up, c),
                p -> openPage(p, 0));
        fillRest(l);
        return l;
    }

    public void bumpOverclock(boolean up, ClickType c) {
        float delta = dev.arubik.craftengine.machine.menu.OverclockMenu.step(c);
        this.overclock += up ? delta : -delta;
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
        setChanged();
    }

    private void fillRest(MachineLayout l) {
        for (int i = 0; i < l.getSize(); i++) {
            if (l.getSlotType(i) == MenuSlotType.BACKGROUND)
                l.setDynamicProvider(i, (m, t) -> FILLER);
        }
    }

    // ---------------- persistence ----------------

    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        set(KEY_DISTANCE, distance);
        set(KEY_SPEED, (float) speedBlocksPerSec);
        set(KEY_MODE, mode().name());
        set(KEY_DWELL, roundRobinDelayTicks);
        set(KEY_OC, overclock);
        super.saveCustomData(tag); // flushes upgrade-slot items via TypedKeys.NMS_ITEMS
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.distance = getOrDefault(KEY_DISTANCE, defaultDistance);
        this.speedBlocksPerSec = getOrDefault(KEY_SPEED, (float) defaultSpeed);
        this.mode = PistonBearingBehavior.Mode.fromString(getOrDefault(KEY_MODE, defaultMode.name()));
        this.roundRobinDelayTicks = getOrDefault(KEY_DWELL, defaultRoundRobinDelay);
        this.overclock = getOrDefault(KEY_OC, 0f);
    }
}
