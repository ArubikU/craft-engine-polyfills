package dev.arubik.craftengine.machine.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import dev.arubik.craftengine.util.NbtType;

import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.FanProcess;
import dev.arubik.craftengine.machine.recipe.FanRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.IOConfiguration.IOType;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.util.TypedKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.util.Key;

/**
 * Gas-powered Copper Fan (Create encased-fan style) as a FULL MACHINE. Migrated from the plain
 * {@code FanBlockBehavior} to the pump/crusher pattern: a config-driven multi-page menu (main gas bar +
 * status + Upgrades + Overclock), an attribute upgrade system, and a player-tunable overclock that
 * scales push strength / range AND processing speed.
 *
 * <p>The fan consumes NO fuel — GAS is its power. Each tick it pulls gas from the carrier on its BACK
 * face into an internal {@link GasTank} buffer (handled by the base machine's input-face auto-pull),
 * then while it has gas (and redstone isn't OFF) it BLOWS a column of air in front, pushing entities and
 * belt items, and PROCESSES items in the airflow:</p>
 * <ul>
 *   <li>The PROCESS BLOCK in the airflow column decides the FAMILY: fire/soul_fire/lit-campfire →
 *       smelting (HEAVY_STEAM upgrades it to blasting); lava/lava-cauldron → blasting; water/water
 *       cauldron → washing. No process block = blow only.</li>
 *   <li>The GAS TYPE decides the smelting tier (STEAM → furnace, HEAVY_STEAM → blasting).</li>
 *   <li>A custom {@code fan} recipe is matched first; if none matches, smelting/blasting fall back to
 *       vanilla furnace/blasting recipes. Washing has no vanilla fallback.</li>
 *   <li>Both DROPPED ItemEntities and items carried on CONVEYOR BELTS in the column are processed.</li>
 * </ul>
 *
 * <p>Redstone is an OFF switch: a signal pauses the fan (like the pump/conveyor).</p>
 */
public class FanMachineBlockEntity extends AbstractMachineBlockEntity {

    public static final int UPGRADE_SLOTS = 9;
    private static final int BASE_UNLOCKED = 3;

    // ---- config knobs ----
    private final int gasCapacity;
    private final int gasPerTick;     // mB drained per blowing tick — SAME for every gas type
    // One config object per gas type (push velocity + max distance + particle). Extensible: a future
    // gas just adds an entry. Defaults seeded in the ctor.
    private final java.util.Map<GasType, FanGasConfig> gasConfigs;
    private static final FanGasConfig DEFAULT_GAS = new FanGasConfig(0.1, 5, org.bukkit.Particle.CLOUD);
    // Floor for the push velocity (blocks/tick): underclock slows the push but never below this, so it
    // always still moves the player/entities.
    private static final double MIN_PUSH = 0.06;
    // Particle the airflow shows AFTER it passes a process block (fire/lava/water), per process family.
    private final java.util.Map<FanProcess, org.bukkit.Particle> processParticles;
    private final java.util.Set<Key> passableBlocks;              // extra blocks the airflow passes through
    private final int baseProcessingTicks; // ticks an item sits in airflow before a (vanilla) convert
    private final int tickDelay;

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;

    private static final org.bukkit.inventory.ItemStack FILLER = MenuText.emptyFiller();

    private float overclock = 0f;
    private double curOverclockLimit = 0;
    private double curGeneration = 0;   // buffer headroom
    private int curUnlocked = BASE_UNLOCKED;

    private int page = 0;
    private MachineMenu active;
    private int shownUnlocked = -1;

    // Per-target processing progress: dropped items keyed by entity id, belt items keyed by packed pos.
    private final java.util.HashMap<Integer, Integer> itemProgress = new java.util.HashMap<>();
    private final java.util.HashMap<Long, Integer> beltProgress = new java.util.HashMap<>();

    // Live status for the menu (the process family the airflow is running this tick).
    private FanProcess lastFamily = FanProcess.NONE;
    private boolean lastBlowing = false;

    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "fan_overclock",
            NbtType.FLOAT);

    public FanMachineBlockEntity(BlockEntity blockEntity, Map<Key, List<Mod>> upgradeDefs,
            List<MachineBar> bars, MachineMenuConfig menuConfig,
            int gasCapacity, int gasPerTick,
            java.util.Map<GasType, FanGasConfig> gasConfigs,
            java.util.Map<FanProcess, org.bukkit.Particle> processParticles,
            java.util.Set<Key> passableBlocks,
            int baseProcessingTicks, int tickDelay) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new ArrayList<>() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 27;
        this.gasCapacity = gasCapacity;
        this.gasPerTick = gasPerTick;
        // Per-gas defaults (config overrides). Future gas types just add an entry here / in config.
        this.gasConfigs = gasConfigs != null ? gasConfigs : new java.util.HashMap<>();
        this.gasConfigs.putIfAbsent(GasType.STEAM, new FanGasConfig(0.1, 5, org.bukkit.Particle.CLOUD));
        this.gasConfigs.putIfAbsent(GasType.HEAVY_STEAM, new FanGasConfig(0.16, 7, org.bukkit.Particle.SMOKE));
        // Nitrogen: weaker pressure (slower push) but a bit longer reach than steam.
        this.gasConfigs.putIfAbsent(GasType.NITROGEN, new FanGasConfig(0.07, 8, org.bukkit.Particle.SNOWFLAKE));
        // Process-particle defaults (config overrides): fire->flame, lava->lava, water->splash, freeze->snow.
        this.processParticles = processParticles != null ? processParticles : new java.util.HashMap<>();
        this.processParticles.putIfAbsent(FanProcess.SMELTING, org.bukkit.Particle.FLAME);
        this.processParticles.putIfAbsent(FanProcess.COOKING, org.bukkit.Particle.CAMPFIRE_COSY_SMOKE);
        this.processParticles.putIfAbsent(FanProcess.BLASTING, org.bukkit.Particle.LAVA);
        this.processParticles.putIfAbsent(FanProcess.WASHING, org.bukkit.Particle.SPLASH);
        this.processParticles.putIfAbsent(FanProcess.FREEZING, org.bukkit.Particle.SNOWFLAKE);
        this.passableBlocks = passableBlocks != null ? passableBlocks : new java.util.HashSet<>();
        this.baseProcessingTicks = Math.max(1, baseProcessingTicks);
        this.tickDelay = Math.max(1, tickDelay);

        setMaxStackSize(64);
        addGasTank(new GasTank("fan_buffer", gasCapacity));
        // Redstone is an OFF switch: machine runs unless a signal is present.
        this.requiresRedstone = false;
        setIOConfiguration(buildIO());
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(27, null, new int[0], new int[0], new int[0], new ArrayList<>(), 4);
    }

    /** Gas intake on the BACK face (the base machine auto-pulls a carrier there into our tank). */
    private IOConfiguration buildIO() {
        IOConfiguration.RelativeIO cfg = new IOConfiguration.RelativeIO();
        // Intake the BACK face = facing.getOpposite() for EVERY facing. RelativeDirection.DOWN maps to
        // facing.getOpposite() in DirectionalIOHelper (BACK only does that for horizontal facings and
        // wrongly resolves to a SIDE on a vertical facing — that's why a facing-up fan's pipe connected
        // on the side instead of below).
        cfg.addInput(IOType.GAS, RelativeDirection.DOWN);
        return cfg;
    }

    @Override
    protected String getMachineId() {
        return "fan";
    }

    @Override
    protected boolean requiresFuel() {
        return false; // gas is the power
    }

    // ---------------- upgrades ----------------

    @Override
    public int[] getUpgradeSlots() {
        int[] s = new int[UPGRADE_SLOTS];
        for (int i = 0; i < UPGRADE_SLOTS; i++)
            s[i] = i;
        return s;
    }

    private Key itemId(int slot) {
        net.minecraft.world.item.ItemStack nms = getItem(slot);
        if (nms == null || nms.isEmpty())
            return null;
        org.bukkit.inventory.ItemStack b = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nms);
        Key custom = net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(b);
        if (custom != null)
            return custom;
        org.bukkit.NamespacedKey nk = b.getType().getKey();
        return Key.of(nk.getNamespace(), nk.getKey());
    }

    private List<Mod> modsOf(int slot) {
        Key id = itemId(slot);
        return id == null ? null : upgradeDefs.get(id);
    }

    @Override
    protected void recomputeUpgrades() {
        List<Mod> all = new ArrayList<>();
        for (int i = 0; i < UPGRADE_SLOTS; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                all.addAll(m);
        }
        int extra = (int) Math.round(MachineAttributes.compute(all)
                .getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(BASE_UNLOCKED, Math.min(UPGRADE_SLOTS, BASE_UNLOCKED + extra));

        List<Mod> activeMods = new ArrayList<>();
        for (int i = 0; i < curUnlocked; i++) {
            List<Mod> m = modsOf(i);
            if (m != null)
                activeMods.addAll(m);
        }
        var attrs = MachineAttributes.compute(activeMods);
        this.curOverclockLimit = clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curGeneration = clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), 0.0, 2.0);
        this.overclock = (float) clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99),
                this.curOverclockLimit);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private int effCapacity() {
        // Buffer is flat; GENERATION now feeds the airflow REACH (range), not the tank size.
        return gasCapacity;
    }

    /** Overclock speed factor: >1 speeds processing AND shortens the per-item tick budget. */
    private double speedFactor() {
        return Math.max(0.05, 1.0 + overclock);
    }

    // ---------------- fan core (overrides the slot-based processTick) ----------------

    private int delayCounter = 0;
    private int upgradeRecomputeCd = 0;

    @Override
    public void tick(Level level, BlockPos pos, net.momirealms.craftengine.core.block.ImmutableBlockState state) {
        if (active != null)
            active.tick();
        super.tick(level, pos, state);
    }

    @Override
    protected void processTick(Level level) {
        if (level.isClientSide())
            return;

        if (upgradeRecomputeCd-- <= 0) {
            recomputeUpgrades();
            upgradeRecomputeCd = 10;
        }
        refreshUpgradePageIfNeeded();

        if (delayCounter > 0) {
            delayCounter--;
            return;
        }
        delayCounter = tickDelay - 1;

        BlockPos pos = getMachinePos();
        net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) level;

        // Redstone OFF switch: a signal pauses the fan.
        boolean redstoneOff = level.hasNeighborSignal(pos);

        GasTank buffer = gasTanks.get(0);
        GasStack stored = buffer.getGas(level, pos);
        boolean gasAvailable = stored != null && !stored.isEmpty();
        GasType tier = gasAvailable ? stored.getType() : GasType.EMPTY;
        boolean blowing = gasAvailable && !redstoneOff && tier != GasType.EMPTY;
        this.lastBlowing = blowing;

        Direction facing = getFacing(level);
        if (facing == null)
            facing = Direction.NORTH;

        // Keep the `powered` block-state in sync with the actual blowing state (drives the model swap).
        syncPowered(serverLevel, pos, blowing);

        if (!blowing) {
            this.lastFamily = FanProcess.NONE;
            this.isProcessing = false;
            itemProgress.clear();
            beltProgress.clear();
            return;
        }

        // Per-gas config: push velocity (blocks/tick), max distance (cells), particle. GENERATION
        // upgrade + overclock extend the reach on top of the gas's limit; overclock also speeds the air.
        FanGasConfig gc = gasConfigs.getOrDefault(tier, DEFAULT_GAS);
        int limit = gc.pushLimit();
        // Reach: gas base limit + GENERATION ONLY (nerfed: ~2 cells per full generation point, capped at
        // the base limit). Overclock NEVER affects range — it only scales push speed + recipe speed.
        int genCells = Math.min(limit, (int) Math.round(curGeneration * 2.0));
        int range = limit + genCells;
        // Push velocity scales with overclock BOTH ways (underclock = slower push + slower particles),
        // but is FLOORED at MIN_PUSH so it never drops to "doesn't move the player" (the old -70% bug).
        double pushStr = Math.max(MIN_PUSH, gc.pushStrength() * speedFactor());
        org.bukkit.Particle particle = gc.particle();

        // Walk the airflow column: stop at the first non-passable block; the PROCESS BLOCK along the way
        // sets the family for the WHOLE column (first one found wins).
        FanProcess family = FanProcess.NONE;
        org.bukkit.World bukkitWorld = serverLevel.getWorld();
        java.util.HashMap<Integer, Integer> seenItems = new java.util.HashMap<>();
        java.util.HashMap<Long, Integer> seenBelts = new java.util.HashMap<>();
        // Nitrogen + fire: no recipe, but the airflow "boils" into heavy steam — swap the particle to the
        // heavy-steam one from the fire cell downstream (sticky, like the process family).
        boolean nitroSteam = false;
        org.bukkit.Particle heavySteamParticle = gasConfigs.getOrDefault(GasType.HEAVY_STEAM, DEFAULT_GAS).particle();

        for (int i = 1; i <= range; i++) {
            BlockPos cell = pos.relative(facing, i);
            net.minecraft.world.level.block.state.BlockState bs = level.getBlockState(cell);
            FanProcess here = processFamilyAt(level, cell, bs, tier);
            if (here != FanProcess.NONE && family == FanProcess.NONE)
                family = here;
            if (!nitroSteam && tier == GasType.NITROGEN && family == FanProcess.NONE && isFireBlock(bs))
                nitroSteam = true;
            // A process block itself is solid (fire/water/lava are passable). Stop pushing past a solid.
            if (!isPassable(bs))
                break;
            // Particle: CLEAN gas particle until the airflow passes the process block; downstream of it
            // the air is "charged" with that process (fire -> flame, lava -> lava, water -> splash/snow).
            org.bukkit.Particle cellParticle = family != FanProcess.NONE
                    ? processParticles.getOrDefault(family, particle)
                    : (nitroSteam ? heavySteamParticle : particle);
            // Push entities + particles (ALWAYS — independent of whether a recipe is processing).
            applyPushAndParticles(bukkitWorld, serverLevel, cell, facing, pushStr, cellParticle);
            // Gas atmosphere effects on living entities in the airflow (nitrogen freezes, steam over water
            // douses fire, etc). family/nitroSteam encode the cell's wet/fire context.
            applyGasEntityEffects(serverLevel, cell, tier, family, nitroSteam);
            // Process dropped items + belt items in this cell.
            if (family != FanProcess.NONE) {
                processDroppedItems(serverLevel, cell, family, tier, seenItems);
                processBeltItems(serverLevel, cell, family, tier, seenBelts);
            }
        }
        this.lastFamily = family;
        this.isProcessing = family != FanProcess.NONE && (!seenItems.isEmpty() || !seenBelts.isEmpty());

        // Drop progress for targets that left the airflow.
        itemProgress.keySet().retainAll(seenItems.keySet());
        itemProgress.putAll(seenItems);
        beltProgress.keySet().retainAll(seenBelts.keySet());
        beltProgress.putAll(seenBelts);

        // Consume gas for the blowing tick — SAME amount for every gas type.
        buffer.extract(level, pos, Math.max(1, gasPerTick), null);
        setChanged();
    }

    /** Effective tick budget for a conversion of base duration {@code time} (overclock shortens it). */
    private int effTicks(int time) {
        return Math.max(1, (int) Math.round(time / speedFactor()));
    }

    /** Per blowing operation we advance progress by tickDelay (the throttle interval). */
    private int progressStep() {
        return Math.max(1, tickDelay);
    }

    // ---------------- process-block → family ----------------

    private FanProcess processFamilyAt(Level level, BlockPos cell,
            net.minecraft.world.level.block.state.BlockState bs, GasType tier) {
        // Full gas × block map:
        //   lava     : nitrogen -> WASHING (quenched), else BLASTING (hottest).
        //   water    : nitrogen -> FREEZING, else WASHING.
        //   fire      : nitrogen -> NONE (boils to heavy steam + slows entities), heavy -> BLASTING,
        //               steam -> SMELTING (furnace).
        //   soul_fire : same as fire BUT steam -> COOKING (smoker / food); heavy -> BLASTING.
        if (bs.is(net.minecraft.world.level.block.Blocks.LAVA)
                || bs.is(net.minecraft.world.level.block.Blocks.LAVA_CAULDRON)) {
            return tier == GasType.NITROGEN ? FanProcess.WASHING : FanProcess.BLASTING;
        }
        if (bs.is(net.minecraft.world.level.block.Blocks.WATER)
                || bs.is(net.minecraft.world.level.block.Blocks.WATER_CAULDRON)) {
            return tier == GasType.NITROGEN ? FanProcess.FREEZING : FanProcess.WASHING;
        }
        if (isFireBlock(bs)) {
            if (tier == GasType.NITROGEN)
                return FanProcess.NONE; // nitrogen over fire: no recipe (boils to heavy steam upstream)
            if (tier == GasType.HEAVY_STEAM)
                return FanProcess.BLASTING;
            // STEAM: soul fire cooks food (smoker), ordinary fire smelts (furnace).
            return isSoulFire(bs) ? FanProcess.COOKING : FanProcess.SMELTING;
        }
        return FanProcess.NONE;
    }

    /** Soul fire family: soul_fire block or a lit soul campfire (the "smoker" heat source for COOKING). */
    private static boolean isSoulFire(net.minecraft.world.level.block.state.BlockState bs) {
        if (bs.is(net.minecraft.world.level.block.Blocks.SOUL_FIRE))
            return true;
        return bs.is(net.minecraft.world.level.block.Blocks.SOUL_CAMPFIRE)
                && bs.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)
                && bs.getValue(net.minecraft.world.level.block.CampfireBlock.LIT);
    }

    /** Fire family: fire, soul fire, or a lit (soul) campfire. */
    private static boolean isFireBlock(net.minecraft.world.level.block.state.BlockState bs) {
        if (bs.is(net.minecraft.world.level.block.Blocks.FIRE)
                || bs.is(net.minecraft.world.level.block.Blocks.SOUL_FIRE))
            return true;
        return (bs.is(net.minecraft.world.level.block.Blocks.CAMPFIRE)
                || bs.is(net.minecraft.world.level.block.Blocks.SOUL_CAMPFIRE))
                && bs.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)
                && bs.getValue(net.minecraft.world.level.block.CampfireBlock.LIT);
    }

    // ---------------- dropped item processing ----------------

    private void processDroppedItems(net.minecraft.server.level.ServerLevel level, BlockPos cell,
            FanProcess family, GasType tier, Map<Integer, Integer> seen) {
        double cx = cell.getX() + 0.5D, cy = cell.getY() + 0.5D, cz = cell.getZ() + 0.5D;
        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
                cx - 0.5D, cy - 0.5D, cz - 0.5D, cx + 0.5D, cy + 0.5D, cz + 0.5D);
        for (net.minecraft.world.entity.item.ItemEntity item :
                dev.arubik.craftengine.contraption.level.ContraptionLevel.unionEntities(
                level, net.minecraft.world.entity.item.ItemEntity.class, aabb, e -> !e.isRemoved())) {
            int id = item.getId();
            net.minecraft.world.item.ItemStack stack = item.getItem();
            if (stack == null || stack.isEmpty())
                continue;
            ResolvedRecipe rr = resolve(level, stack, family, tier);
            if (rr == null)
                continue; // not processable: just gets blown
            int ticks = itemProgress.getOrDefault(id, 0) + progressStep();
            if (ticks >= effTicks(rr.time)) {
                // Convert the WHOLE stack at once: as many recipe SETS as the stack holds.
                int amt = rr.inputAmount;
                int sets = stack.getCount() / amt;
                if (sets <= 0) {
                    seen.put(id, ticks); // not even one set's worth — wait for more
                    continue;
                }
                net.minecraft.world.item.ItemStack shrunk = stack.copy();
                shrunk.shrink(sets * amt);
                item.setItem(shrunk);
                if (shrunk.isEmpty())
                    item.discard();
                // Pass the INPUT item through so the level bridge can land the output in whatever world
                // the input truly lives in — real world for a union-captured real item, this fake level
                // for a co-captured one (residual 2). Outside a contraption this is just the fake level.
                spawnOutputs(level, item, cx, cy, cz, rr, sets); // output count scales with the whole stack
                convertEffect(level, cx, cy, cz, family);
                seen.put(id, 0);
            } else {
                seen.put(id, ticks);
            }
        }
    }

    // ---------------- belt item processing ----------------

    private void processBeltItems(net.minecraft.server.level.ServerLevel level, BlockPos cell,
            FanProcess family, GasType tier, Map<Long, Integer> seen) {
        net.momirealms.craftengine.core.block.entity.BlockEntity be =
                dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, cell);
        if (be == null || !(be.controller instanceof dev.arubik.craftengine.conveyor.ConveyorBlockEntity belt))
            return;
        double dx = cell.getX() + 0.5, dy = cell.getY() + 0.7, dz = cell.getZ() + 0.5;
        // Cook EVERY carried item on this belt (not just the front), each tracked by its own display id.
        belt.forEachCarried((displayId, carried) -> {
            if (carried == null || carried.getType().isAir() || displayId < 0)
                return carried; // unchanged (no display yet -> don't reset others)
            net.minecraft.world.item.ItemStack nms =
                    org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(carried);
            ResolvedRecipe rr = resolve(level, nms, family, tier);
            if (rr == null)
                return carried; // not processable: just rides the belt
            int ticks = beltProgress.getOrDefault((long) displayId, 0) + progressStep();
            if (ticks < effTicks(rr.time)) {
                seen.put((long) displayId, ticks);
                return carried; // still cooking
            }
            int amt = rr.inputAmount;
            int sets = carried.getAmount() / amt;
            if (sets <= 0) {
                seen.put((long) displayId, ticks); // not a full set yet
                return carried;
            }
            int remainder = carried.getAmount() - sets * amt;
            seen.put((long) displayId, 0);
            convertEffect(level, dx, dy, dz, family);
            if (remainder > 0) {
                // Partial: the leftover input keeps riding the belt; all outputs drop. Belt source has
                // no dropped-item input, so null -> outputs stay in this (fake) level's local space.
                spawnOutputs(level, null, dx, dy, dz, rr, sets);
                org.bukkit.inventory.ItemStack rem = carried.clone();
                rem.setAmount(remainder);
                return rem;
            }
            // Whole stack consumed: the PRIMARY output stays on the belt (replace in place, no eject);
            // overflow + secondary/chance outputs drop. Returns null only if there's no primary output.
            return convertOnBelt(level, dx, dy, dz, rr, sets);
        });
    }

    // ---------------- recipe resolution (custom fan recipe, else vanilla) ----------------

    /** A resolved set of outputs for one craft of {@code input}. */
    private static final class ResolvedRecipe {
        int inputAmount = 1;
        int time = 40; // base ticks this conversion takes — from the recipe, NOT a flat number
        final List<net.minecraft.world.item.ItemStack> outputs = new ArrayList<>(); // index 0 = primary
        final List<Float> chances = new ArrayList<>();

        /** The primary output as a Bukkit stack (for in-place belt replacement), or null. */
        org.bukkit.inventory.ItemStack primaryBukkit() {
            if (outputs.isEmpty())
                return null;
            // Primary always drops (chance is honored only for secondaries when on a belt).
            return org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(outputs.get(0).copy());
        }
    }

    private ResolvedRecipe resolve(net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.item.ItemStack stack, FanProcess family, GasType tier) {
        // 1) custom fan recipe.
        for (FanRecipe fr : RecipeManager.getFanRecipes()) {
            if (fr.matches(stack, family, tier)) {
                ResolvedRecipe rr = new ResolvedRecipe();
                rr.inputAmount = fr.inputAmount();
                rr.time = Math.max(1, fr.getProcessTime()); // custom fan recipe's own duration
                for (RecipeOutput out : fr.getOutputs()) {
                    if (out instanceof dev.arubik.craftengine.machine.recipe.ItemOutput io) {
                        rr.outputs.add(io.getItem().copy());
                        rr.chances.add(io.getChance());
                    }
                }
                return rr.outputs.isEmpty() ? null : rr;
            }
        }
        // 2) vanilla fallback — washing/freezing have none (custom fan recipes only).
        if (family == FanProcess.WASHING || family == FanProcess.FREEZING)
            return null;
        try {
            net.minecraft.world.item.ItemStack single = stack.copy();
            single.setCount(1);
            net.minecraft.world.item.crafting.SingleRecipeInput input =
                    new net.minecraft.world.item.crafting.SingleRecipeInput(single);
            net.minecraft.world.item.crafting.RecipeManager rm = level.getServer().getRecipeManager();
            // Map the fan family to the matching vanilla cooking recipe type.
            net.minecraft.world.item.crafting.RecipeType<? extends net.minecraft.world.item.crafting.AbstractCookingRecipe> rtype =
                    switch (family) {
                        case BLASTING -> net.minecraft.world.item.crafting.RecipeType.BLASTING;
                        case COOKING -> net.minecraft.world.item.crafting.RecipeType.SMOKING;
                        default -> net.minecraft.world.item.crafting.RecipeType.SMELTING;
                    };
            net.minecraft.world.item.crafting.AbstractCookingRecipe cook = null;
            var h = rm.getRecipeFor(rtype, input, level);
            if (h.isPresent())
                cook = h.get().value();
            if (cook != null) {
                net.minecraft.world.item.ItemStack out = cook.assemble(input, level.registryAccess());
                if (out != null && !out.isEmpty()) {
                    ResolvedRecipe rr = new ResolvedRecipe();
                    rr.inputAmount = 1;
                    // The vanilla recipe's OWN cooking time (smelting ~200t, blasting/smoking ~100t).
                    try {
                        rr.time = Math.max(1, cook.cookingTime());
                    } catch (Throwable t) {
                        rr.time = family == FanProcess.SMELTING ? 200 : 100;
                    }
                    rr.outputs.add(out.copy());
                    rr.chances.add(1.0f);
                    return rr;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** Spawn ALL outputs (respecting chance) as dropped items at the cell centre. */
    private void spawnOutputs(net.minecraft.server.level.ServerLevel level, double x, double y, double z,
            ResolvedRecipe rr) {
        spawnOutputs(level, null, x, y, z, rr, 1);
    }

    /**
     * Belt conversion: the PRIMARY output (index 0) × sets stays on the belt (returned as the slot's
     * new stack, capped at max stack size); overflow + every SECONDARY output drops. Returns null when
     * the recipe has no item output.
     */
    private org.bukkit.inventory.ItemStack convertOnBelt(net.minecraft.server.level.ServerLevel level,
            double x, double y, double z, ResolvedRecipe rr, int sets) {
        org.bukkit.inventory.ItemStack beltStack = null;
        for (int i = 0; i < rr.outputs.size(); i++) {
            net.minecraft.world.item.ItemStack base = rr.outputs.get(i);
            float chance = rr.chances.get(i);
            int total;
            if (chance >= 1.0f) {
                total = base.getCount() * sets;
            } else {
                total = 0;
                for (int s = 0; s < sets; s++)
                    if (java.util.concurrent.ThreadLocalRandom.current().nextFloat() < chance)
                        total += base.getCount();
            }
            if (total <= 0)
                continue;
            int max = Math.max(1, base.getMaxStackSize());
            if (i == 0) {
                // Primary -> belt slot (capped); overflow drops below.
                int onBelt = Math.min(max, total);
                beltStack = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(base.copy());
                beltStack.setAmount(onBelt);
                total -= onBelt;
            }
            while (total > 0) {
                int c = Math.min(max, total);
                net.minecraft.world.item.ItemStack out = base.copy();
                out.setCount(c);
                // Belt source: no dropped-item input (belt items are fake displays), so null -> the
                // output stays in this fake level's local space, exactly as before.
                dropItem(level, null, x, y, z, out);
                total -= c;
            }
        }
        return beltStack;
    }

    /** Burst of particles + a sound when an item finishes cooking/washing, themed by process family. */
    private void convertEffect(net.minecraft.server.level.ServerLevel level, double x, double y, double z,
            FanProcess family) {
        org.bukkit.World w = level.getWorld();
        org.bukkit.Location loc = new org.bukkit.Location(w, x, y, z);
        switch (family) {
            case BLASTING -> {
                w.spawnParticle(org.bukkit.Particle.LAVA, x, y, z, 6, 0.18, 0.18, 0.18, 0);
                w.spawnParticle(org.bukkit.Particle.LARGE_SMOKE, x, y, z, 4, 0.15, 0.2, 0.15, 0.01);
                w.playSound(loc, org.bukkit.Sound.BLOCK_LAVA_POP, 0.6f, 1.4f);
            }
            case SMELTING -> {
                w.spawnParticle(org.bukkit.Particle.FLAME, x, y, z, 6, 0.18, 0.18, 0.18, 0.01);
                w.spawnParticle(org.bukkit.Particle.SMOKE, x, y, z, 5, 0.15, 0.2, 0.15, 0.01);
                w.playSound(loc, org.bukkit.Sound.BLOCK_LAVA_POP, 0.5f, 1.8f);
            }
            case COOKING -> {
                w.spawnParticle(org.bukkit.Particle.CAMPFIRE_COSY_SMOKE, x, y, z, 6, 0.15, 0.25, 0.15, 0.01);
                w.spawnParticle(org.bukkit.Particle.SMOKE, x, y, z, 4, 0.15, 0.2, 0.15, 0.01);
                w.playSound(loc, org.bukkit.Sound.BLOCK_CAMPFIRE_CRACKLE, 0.6f, 1.2f);
            }
            case WASHING -> {
                w.spawnParticle(org.bukkit.Particle.SPLASH, x, y, z, 10, 0.2, 0.25, 0.2, 0.05);
                w.spawnParticle(org.bukkit.Particle.BUBBLE, x, y, z, 6, 0.18, 0.1, 0.18, 0.02);
                w.playSound(loc, org.bukkit.Sound.ENTITY_PLAYER_SPLASH, 0.5f, 1.6f);
            }
            case FREEZING -> {
                w.spawnParticle(org.bukkit.Particle.SNOWFLAKE, x, y, z, 12, 0.2, 0.25, 0.2, 0.02);
                w.spawnParticle(org.bukkit.Particle.ITEM_SNOWBALL, x, y, z, 6, 0.18, 0.15, 0.18, 0.01);
                w.playSound(loc, org.bukkit.Sound.BLOCK_GLASS_BREAK, 0.5f, 1.4f);
            }
            default -> {
            }
        }
    }

    /**
     * Spawn outputs for {@code sets} recipe completions at once (whole-stack conversion). {@code inputEntity}
     * is the dropped item they were produced from (or {@code null} for belt/positional sources) — threaded
     * to {@link #dropItem} so the level bridge lands each output in the input's own world (residual 2).
     */
    private void spawnOutputs(net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.entity.Entity inputEntity, double x, double y, double z,
            ResolvedRecipe rr, int sets) {
        if (sets <= 0)
            return;
        for (int i = 0; i < rr.outputs.size(); i++) {
            net.minecraft.world.item.ItemStack base = rr.outputs.get(i);
            float chance = rr.chances.get(i);
            int perSet = base.getCount();
            int total;
            if (chance >= 1.0f) {
                total = perSet * sets;
            } else {
                total = 0;
                for (int s = 0; s < sets; s++)
                    if (java.util.concurrent.ThreadLocalRandom.current().nextFloat() < chance)
                        total += perSet;
            }
            if (total <= 0)
                continue;
            int max = Math.max(1, base.getMaxStackSize());
            while (total > 0) {
                int c = Math.min(max, total);
                net.minecraft.world.item.ItemStack out = base.copy();
                out.setCount(c);
                dropItem(level, inputEntity, x, y, z, out);
                total -= c;
            }
        }
    }

    /** Spawn only the SECONDARY outputs (index > 0) — the primary went onto the belt in place. */
    private void spawnSecondaryOutputs(net.minecraft.server.level.ServerLevel level, double x, double y, double z,
            ResolvedRecipe rr) {
        for (int i = 1; i < rr.outputs.size(); i++) {
            if (rr.chances.get(i) < 1.0f
                    && java.util.concurrent.ThreadLocalRandom.current().nextFloat() >= rr.chances.get(i))
                continue;
            dropItem(level, null, x, y, z, rr.outputs.get(i).copy());
        }
    }

    /**
     * Spawn a produced output item. {@code inputEntity} is the dropped item this output was made from
     * (or {@code null} for belt/positional outputs with no dropped-item source): the level bridge uses it
     * to place the output in whatever world the input truly lives in — the real world for a union-captured
     * real item, this fake contraption level for a co-captured one (residual 2). {@code x,y,z} are the
     * fake-level fallback coordinates used whenever the input is fake or the fan isn't in a contraption.
     */
    private static void dropItem(net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.entity.Entity inputEntity, double x, double y, double z,
            net.minecraft.world.item.ItemStack out) {
        dev.arubik.craftengine.contraption.level.ContraptionLevel.spawnProcessingOutput(
                level, inputEntity, x, y, z, out);
    }

    // ---------------- push / particles (NMS) ----------------

    private boolean isPassable(net.minecraft.world.level.block.state.BlockState bs) {
        // Air, fire, water, lava and other non-solid blocks let air through, PLUS any block id in the
        // configured `passableBlocks` list (so packs can let the airflow pass through custom blocks).
        if (bs.isAir() || !bs.isSolidRender()
                || bs.is(net.minecraft.world.level.block.Blocks.FIRE)
                || bs.is(net.minecraft.world.level.block.Blocks.SOUL_FIRE)
                || bs.is(net.minecraft.world.level.block.Blocks.WATER)
                || bs.is(net.minecraft.world.level.block.Blocks.LAVA))
            return true;
        if (!passableBlocks.isEmpty()) {
            var rl = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(bs.getBlock());
            if (rl != null && passableBlocks.contains(Key.of(rl.getNamespace(), rl.getPath())))
                return true;
        }
        return false;
    }

    private void applyPushAndParticles(org.bukkit.World bukkitWorld, net.minecraft.server.level.ServerLevel level,
            BlockPos cell, Direction facing, double pushStrength, org.bukkit.Particle particle) {
        double cx = cell.getX() + 0.5D, cy = cell.getY() + 0.5D, cz = cell.getZ() + 0.5D;
        // push velocity is in BLOCKS per tick directly (no hidden ×0.1 factor).
        org.bukkit.util.Vector push = new org.bukkit.util.Vector(
                facing.getStepX(), facing.getStepY(), facing.getStepZ()).multiply(pushStrength);
        // ANIMATED airflow: spawn the particle at the cell's BACK edge (toward the fan) with a DIRECTIONAL
        // velocity so it streams forward (count=0 makes MC use the offset xyz as velocity × extra),
        // instead of a static blob at the centre. Lateral jitter spreads the stream a little.
        double back = 0.45D;
        double lat = 0.22D;
        double sx = cx - facing.getStepX() * back + rand(lat) * (1 - Math.abs(facing.getStepX()));
        double sy = cy - facing.getStepY() * back + rand(lat) * (1 - Math.abs(facing.getStepY()));
        double sz = cz - facing.getStepZ() * back + rand(lat) * (1 - Math.abs(facing.getStepZ()));
        double flow = Math.max(0.06D, pushStrength * 0.9D); // stream speed scales with push strength
        bukkitWorld.spawnParticle(particle, sx, sy, sz, 0,
                facing.getStepX(), facing.getStepY(), facing.getStepZ(), flow);
        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
                cx - 0.5D, cy - 0.5D, cz - 0.5D, cx + 0.5D, cy + 0.5D, cz + 0.5D);
        for (net.minecraft.world.entity.Entity nms :
                dev.arubik.craftengine.contraption.level.ContraptionLevel.unionEntities(
                level, net.minecraft.world.entity.Entity.class, aabb, e -> !e.isRemoved())) {
            // The scan is the dual-world union (fake + transformed real) when this fan is inside a
            // contraption, so `push` is a LOCAL-space vector that must be yaw-rotated for real-world
            // targets but left raw for co-captured fake ones. The level bridge owns that distinction
            // (and no-ops the rotation when we're not inside a contraption) — the fan stays a pure
            // local-space actor. See ContraptionLevel#pushEntity.
            dev.arubik.craftengine.contraption.level.ContraptionLevel.pushEntity(level, nms, push);
        }
    }

    private static double rand(double range) {
        return (java.util.concurrent.ThreadLocalRandom.current().nextDouble() * 2.0D - 1.0D) * range;
    }

    /**
     * Apply gas-atmosphere effects to living entities standing in this airflow cell.
     * <ul>
     *   <li>Nitrogen (dry air): freezing.</li>
     *   <li>Nitrogen over water (family FREEZING): slowness + freezing.</li>
     *   <li>Nitrogen over fire (nitroSteam): slowness only.</li>
     *   <li>Steam / heavy steam over water (family WASHING): douses the entity's fire.</li>
     * </ul>
     */
    private void applyGasEntityEffects(net.minecraft.server.level.ServerLevel level, BlockPos cell, GasType tier,
            FanProcess family, boolean nitroSteam) {
        if (tier != GasType.NITROGEN && tier != GasType.STEAM && tier != GasType.HEAVY_STEAM)
            return;
        double cx = cell.getX() + 0.5D, cy = cell.getY() + 0.5D, cz = cell.getZ() + 0.5D;
        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
                cx - 0.5D, cy - 0.5D, cz - 0.5D, cx + 0.5D, cy + 0.5D, cz + 0.5D);
        for (net.minecraft.world.entity.Entity nms :
                dev.arubik.craftengine.contraption.level.ContraptionLevel.unionEntities(
                level, net.minecraft.world.entity.LivingEntity.class, aabb, e -> !e.isRemoved())) {
            org.bukkit.entity.Entity be = nms.getBukkitEntity();
            if (!(be instanceof org.bukkit.entity.LivingEntity living))
                continue;
            if (tier == GasType.NITROGEN) {
                if (family == FanProcess.FREEZING) { // nitrogen over water -> slowness + freezing
                    freeze(living);
                    slow(living);
                } else if (nitroSteam) { // nitrogen over fire -> slowness only
                    slow(living);
                } else { // plain nitrogen air -> freezing
                    freeze(living);
                }
            } else { // STEAM / HEAVY_STEAM
                if (family == FanProcess.WASHING) // steam over water -> put out fire
                    living.setFireTicks(0);
            }
        }
    }

    private static void freeze(org.bukkit.entity.LivingEntity living) {
        int max = Math.max(140, living.getMaxFreezeTicks());
        living.setFreezeTicks(Math.min(max, living.getFreezeTicks() + 60));
    }

    private static void slow(org.bukkit.entity.LivingEntity living) {
        living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.SLOWNESS, 60, 1, true, false, true));
    }

    private static final String PROP_POWERED = "powered";

    private void syncPowered(net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean blowing) {
        try {
            net.minecraft.world.level.block.state.BlockState ms = level.getBlockState(pos);
            net.momirealms.craftengine.core.block.ImmutableBlockState cs =
                    net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(ms).orElse(null);
            if (cs == null || cs.isEmpty())
                return;
            net.momirealms.craftengine.core.block.property.Property<?> p = cs.getProperty(PROP_POWERED);
            if (p == null)
                return;
            @SuppressWarnings("unchecked")
            net.momirealms.craftengine.core.block.property.Property<Boolean> pp =
                    (net.momirealms.craftengine.core.block.property.Property<Boolean>) p;
            if (((Boolean) cs.get(pp)).booleanValue() == blowing)
                return;
            net.momirealms.craftengine.core.block.ImmutableBlockState ns = cs.with(pp, Boolean.valueOf(blowing));
            ((net.minecraft.world.level.LevelWriter) level).setBlock(pos,
                    (net.minecraft.world.level.block.state.BlockState) ns.customBlockState().minecraftState(),
                    net.momirealms.craftengine.core.block.UpdateFlags.UPDATE_ALL);
        } catch (Throwable ignored) {
        }
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

    // ---------------- bars ----------------

    private GasStack storedGas() {
        GasStack s = gasTanks.get(0).getGas(getNMSLevel(), getMachinePos());
        return s == null ? GasStack.EMPTY : s;
    }

    @Override
    public double[] barStat(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "fan_buffer".equals(id)) {
            GasStack s = storedGas();
            return new double[] { s.isEmpty() ? 0 : s.getAmount(), Math.max(1, effCapacity()) };
        }
        if ("progress".equals(id)) {
            // Display estimate: per-recipe time varies, so gauge against the configured base duration.
            int total = effTicks(baseProcessingTicks);
            int best = 0;
            for (int v : itemProgress.values())
                best = Math.max(best, v);
            for (int v : beltProgress.values())
                best = Math.max(best, v);
            return new double[] { Math.min(best, total), Math.max(1, total) };
        }
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "fan_buffer".equals(id)) {
            GasStack s = storedGas();
            return s.isEmpty() ? "" : s.getType().toString().toLowerCase(java.util.Locale.ROOT);
        }
        return super.barSubtype(id);
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
        MachineLayout l = new MachineLayout(org.bukkit.event.inventory.InventoryType.CHEST, menuSize, "Fan");
        net.kyori.adventure.text.Component title =
                dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "main");
        l.setTitleComponent(title != null ? title
                : MenuText.noI(MenuText.tr("polyfill.ui.fan_title", NamedTextColor.AQUA)));

        for (MachineMenuConfig.Button b : menuConfig.buttons)
            installButton(l, b);

        MachineBars.install(l, bars);

        int infoSlot = menuConfig.infoSlot >= 0 ? menuConfig.infoSlot : -1;
        if (infoSlot >= 0)
            l.setDynamicProvider(infoSlot, (m, t) -> ((FanMachineBlockEntity) m).infoIcon());

        fillRest(l);
        return l;
    }

    private org.bukkit.inventory.ItemStack infoIcon() {
        GasStack stored = storedGas();
        org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(Material.WHITE_STAINED_GLASS);
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        var GRAY = NamedTextColor.GRAY;
        var WHITE = NamedTextColor.WHITE;
        var AQUA = NamedTextColor.AQUA;
        net.kyori.adventure.text.Component gasName = stored.isEmpty()
                ? MenuText.tr("polyfill.gas.empty", WHITE)
                : MenuText.tr(stored.getType().translationKey(), WHITE);
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.gas", AQUA)
                .append(net.kyori.adventure.text.Component.text(": ", GRAY)).append(gasName)));
        String statusKey = !lastBlowing ? "polyfill.ui.fan_status_idle"
                : switch (lastFamily) {
                    case SMELTING -> "polyfill.ui.fan_status_smelting";
                    case COOKING -> "polyfill.ui.fan_status_cooking";
                    case BLASTING -> "polyfill.ui.fan_status_blasting";
                    case WASHING -> "polyfill.ui.fan_status_washing";
                    case FREEZING -> "polyfill.ui.fan_status_freezing";
                    default -> "polyfill.ui.fan_status_blowing";
                };
        meta.lore(java.util.List.of(
                MenuText.noI(MenuText.kv("polyfill.ui.amount", GRAY,
                        (stored.isEmpty() ? 0 : stored.getAmount()) + " / " + effCapacity() + " mB", WHITE)),
                MenuText.noI(MenuText.tr(statusKey, GRAY))));
        stack.setItemMeta(meta);
        return stack;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            FanMachineBlockEntity s = (FanMachineBlockEntity) m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(parseKey(iconSpec), Material.PAPER,
                    label(b.name, NamedTextColor.AQUA), lore(b.lore));
        }, (m, p) -> {
            FanMachineBlockEntity s = (FanMachineBlockEntity) m;
            if (s.isButtonLocked(b))
                return;
            switch (b.action.kind) {
                case OPEN_PAGE -> s.openPage(p, b.action.page);
                case DEPLETE_GAS -> {
                    s.gasTanks.get(0).deplete(s.getNMSLevel(), s.getMachinePos());
                    s.setChanged();
                }
                case DEPLETE_FLUID, NONE -> {
                }
            }
        });
    }

    private boolean isButtonLocked(MachineMenuConfig.Button b) {
        return b.lockedWhen == MachineMenuConfig.LockedWhen.NO_OVERCLOCK && curOverclockLimit <= 0;
    }

    private static net.kyori.adventure.text.Component label(String s, NamedTextColor color) {
        if (s == null)
            return net.kyori.adventure.text.Component.empty();
        String key = s.startsWith("lang:") ? s.substring(5) : s;
        if (key.contains(".") && !key.contains(" "))
            return MenuText.tr(key, color);
        return MenuText.lit(s, color);
    }

    private static net.kyori.adventure.text.Component[] lore(List<String> lines) {
        if (lines == null || lines.isEmpty())
            return new net.kyori.adventure.text.Component[0];
        net.kyori.adventure.text.Component[] out = new net.kyori.adventure.text.Component[lines.size()];
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
        net.kyori.adventure.text.Component title =
                dev.arubik.craftengine.machine.menu.GuiTitles.title(getMachineId(), "upgrade");
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
                (m, p) -> ((FanMachineBlockEntity) m).openPage(p, 0));
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
        this.upgradeRecomputeCd = 0;
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
        set(KEY_OC, overclock);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        this.overclock = getOrDefault(KEY_OC, 0f);
    }
}
