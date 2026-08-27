/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.AbstractCookingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.libraries.nbt.CompoundTag
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 *  org.joml.Vector3d
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import dev.arubik.craftengine.contraption.physics.PhysicsWorld;
import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.block.entity.FanGasConfig;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.FanProcess;
import dev.arubik.craftengine.machine.recipe.FanRecipe;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.RelativeDirection;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

public class FanMachineBlockEntity
extends AbstractMachineBlockEntity {
    public static final int UPGRADE_SLOTS = 9;
    private static final int BASE_UNLOCKED = 3;
    private final int gasCapacity;
    private final int gasPerTick;
    private final Map<GasType, FanGasConfig> gasConfigs;
    private static final FanGasConfig DEFAULT_GAS = new FanGasConfig(0.1, 5, Particle.CLOUD);
    private static final double MIN_PUSH = 0.06;
    private final Map<FanProcess, Particle> processParticles;
    private final Set<Key> passableBlocks;
    private final int baseProcessingTicks;
    private final int tickDelay;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int menuSize;
    private static final ItemStack FILLER = MenuText.emptyFiller();
    private float overclock = 0.0f;
    private double curOverclockLimit = 0.0;
    private double curGeneration = 0.0;
    private int curUnlocked = 3;
    private int page = 0;
    private MachineMenu active;
    private int shownUnlocked = -1;
    private final HashMap<Integer, Integer> itemProgress = new HashMap();
    private final HashMap<Long, Integer> beltProgress = new HashMap();
    private FanProcess lastFamily = FanProcess.NONE;
    private boolean lastBlowing = false;
    private static final TypedKey<Float> KEY_OC = TypedKey.of("craftengine", "fan_overclock", NbtType.FLOAT);
    private int delayCounter = 0;
    private int upgradeRecomputeCd = 0;
    private static final double THRUST_PER_PUSH = 1.0;
    private static final String PROP_POWERED = "powered";

    public FanMachineBlockEntity(BlockEntity blockEntity, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig, int gasCapacity, int gasPerTick, Map<GasType, FanGasConfig> gasConfigs, Map<FanProcess, Particle> processParticles, Set<Key> passableBlocks, int baseProcessingTicks, int tickDelay) {
        super(blockEntity, menuConfig != null && menuConfig.menuSize > 0 ? menuConfig.menuSize : 27);
        this.upgradeDefs = upgradeDefs == null ? new HashMap() : upgradeDefs;
        this.bars = bars == null ? new ArrayList() : bars;
        this.menuConfig = menuConfig != null ? menuConfig : FanMachineBlockEntity.defaultMenuConfig();
        this.menuSize = this.menuConfig.menuSize > 0 ? this.menuConfig.menuSize : 27;
        this.gasCapacity = gasCapacity;
        this.gasPerTick = gasPerTick;
        this.gasConfigs = gasConfigs != null ? gasConfigs : new HashMap();
        this.gasConfigs.putIfAbsent(GasType.STEAM, new FanGasConfig(0.1, 5, Particle.CLOUD));
        this.gasConfigs.putIfAbsent(GasType.HEAVY_STEAM, new FanGasConfig(0.16, 7, Particle.SMOKE));
        this.gasConfigs.putIfAbsent(GasType.NITROGEN, new FanGasConfig(0.07, 8, Particle.SNOWFLAKE));
        this.processParticles = processParticles != null ? processParticles : new HashMap();
        this.processParticles.putIfAbsent(FanProcess.SMELTING, Particle.FLAME);
        this.processParticles.putIfAbsent(FanProcess.COOKING, Particle.CAMPFIRE_COSY_SMOKE);
        this.processParticles.putIfAbsent(FanProcess.BLASTING, Particle.LAVA);
        this.processParticles.putIfAbsent(FanProcess.WASHING, Particle.SPLASH);
        this.processParticles.putIfAbsent(FanProcess.FREEZING, Particle.SNOWFLAKE);
        this.passableBlocks = passableBlocks != null ? passableBlocks : new HashSet();
        this.baseProcessingTicks = Math.max(1, baseProcessingTicks);
        this.tickDelay = Math.max(1, tickDelay);
        this.setMaxStackSize(64);
        this.addGasTank(new GasTank("fan_buffer", gasCapacity));
        this.requiresRedstone = false;
        this.setIOConfiguration(this.buildIO());
    }

    private static MachineMenuConfig defaultMenuConfig() {
        return new MachineMenuConfig(27, null, new int[0], new int[0], new int[0], new ArrayList<MachineMenuConfig.Button>(), 4);
    }

    private IOConfiguration buildIO() {
        IOConfiguration.RelativeIO cfg = new IOConfiguration.RelativeIO();
        cfg.addInput(IOConfiguration.IOType.GAS, RelativeDirection.DOWN);
        return cfg;
    }

    @Override
    protected String getMachineId() {
        return "fan";
    }

    @Override
    protected boolean requiresFuel() {
        return false;
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
        ArrayList<MachineAttributes.Mod> activeMods = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.curUnlocked; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(i);
            if (m == null) continue;
            activeMods.addAll(m);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(activeMods);
        this.curOverclockLimit = FanMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curGeneration = FanMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), 0.0, 2.0);
        this.overclock = (float)FanMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private int effCapacity() {
        return this.gasCapacity;
    }

    private double speedFactor() {
        return Math.max(0.05, 1.0 + (double)this.overclock);
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (this.active != null) {
            this.active.tick();
        }
        super.tick(level, pos, state);
    }

    @Override
    protected void processTick(Level level) {
        boolean blowing;
        if (level.isClientSide()) {
            return;
        }
        if (this.upgradeRecomputeCd-- <= 0) {
            this.recomputeUpgrades();
            this.upgradeRecomputeCd = 10;
        }
        this.refreshUpgradePageIfNeeded();
        if (this.delayCounter > 0) {
            --this.delayCounter;
            return;
        }
        this.delayCounter = this.tickDelay - 1;
        BlockPos pos = this.getMachinePos();
        ServerLevel serverLevel = (ServerLevel)level;
        boolean redstoneOff = level.hasNeighborSignal(pos);
        GasTank buffer = (GasTank)this.gasTanks.get(0);
        GasStack stored = buffer.getGas(level, pos);
        boolean gasAvailable = stored != null && !stored.isEmpty();
        GasType tier = gasAvailable ? stored.getType() : GasType.EMPTY;
        this.lastBlowing = blowing = gasAvailable && !redstoneOff && tier != GasType.EMPTY;
        Direction facing = this.getFacing(level);
        if (facing == null) {
            facing = Direction.NORTH;
        }
        this.syncPowered(serverLevel, pos, blowing);
        if (!blowing) {
            this.lastFamily = FanProcess.NONE;
            this.isProcessing = false;
            this.itemProgress.clear();
            this.beltProgress.clear();
            return;
        }
        FanGasConfig gc = this.gasConfigs.getOrDefault(tier, DEFAULT_GAS);
        int limit = gc.pushLimit();
        int genCells = Math.min(limit, (int)Math.round(this.curGeneration * 2.0));
        int range = limit + genCells;
        double pushStr = Math.max(0.06, gc.pushStrength() * this.speedFactor());
        Particle particle = gc.particle();
        this.applyFanThrust(serverLevel, pos, facing, pushStr * (double)range);
        FanProcess family = FanProcess.NONE;
        CraftWorld bukkitWorld = serverLevel.getWorld();
        HashMap<Integer, Integer> seenItems = new HashMap<Integer, Integer>();
        HashMap<Long, Integer> seenBelts = new HashMap<Long, Integer>();
        boolean nitroSteam = false;
        Particle heavySteamParticle = this.gasConfigs.getOrDefault(GasType.HEAVY_STEAM, DEFAULT_GAS).particle();
        for (int i = 1; i <= range; ++i) {
            BlockState bs;
            BlockPos cell = pos.relative(facing, i);
            FanProcess here = this.processFamilyAt(level, cell, bs = level.getBlockState(cell), tier);
            if (here != FanProcess.NONE && family == FanProcess.NONE) {
                family = here;
            }
            if (!nitroSteam && tier == GasType.NITROGEN && family == FanProcess.NONE && FanMachineBlockEntity.isFireBlock(bs)) {
                nitroSteam = true;
            }
            if (!this.isPassable(bs)) break;
            Particle cellParticle = family != FanProcess.NONE ? this.processParticles.getOrDefault(family, particle) : (nitroSteam ? heavySteamParticle : particle);
            this.applyPushAndParticles((World)bukkitWorld, serverLevel, cell, facing, pushStr, cellParticle);
            this.applyGasEntityEffects(serverLevel, cell, tier, family, nitroSteam);
            if (family == FanProcess.NONE) continue;
            this.processDroppedItems(serverLevel, cell, family, tier, seenItems);
            this.processBeltItems(serverLevel, cell, family, tier, seenBelts);
        }
        this.lastFamily = family;
        this.isProcessing = family != FanProcess.NONE && (!seenItems.isEmpty() || !seenBelts.isEmpty());
        this.itemProgress.keySet().retainAll(seenItems.keySet());
        this.itemProgress.putAll(seenItems);
        this.beltProgress.keySet().retainAll(seenBelts.keySet());
        this.beltProgress.putAll(seenBelts);
        buffer.extract(level, pos, Math.max(1, this.gasPerTick), null);
        this.setChanged();
    }

    private int effTicks(int time) {
        return Math.max(1, (int)Math.round((double)time / this.speedFactor()));
    }

    private int progressStep() {
        return Math.max(1, this.tickDelay);
    }

    private FanProcess processFamilyAt(Level level, BlockPos cell, BlockState bs, GasType tier) {
        if (bs.is(Blocks.LAVA) || bs.is(Blocks.LAVA_CAULDRON)) {
            return tier == GasType.NITROGEN ? FanProcess.WASHING : FanProcess.BLASTING;
        }
        if (bs.is(Blocks.WATER) || bs.is(Blocks.WATER_CAULDRON)) {
            return tier == GasType.NITROGEN ? FanProcess.FREEZING : FanProcess.WASHING;
        }
        if (FanMachineBlockEntity.isFireBlock(bs)) {
            if (tier == GasType.NITROGEN) {
                return FanProcess.NONE;
            }
            if (tier == GasType.HEAVY_STEAM) {
                return FanProcess.BLASTING;
            }
            return FanMachineBlockEntity.isSoulFire(bs) ? FanProcess.COOKING : FanProcess.SMELTING;
        }
        return FanProcess.NONE;
    }

    private static boolean isSoulFire(BlockState bs) {
        if (bs.is(Blocks.SOUL_FIRE)) {
            return true;
        }
        return bs.is(Blocks.SOUL_CAMPFIRE) && bs.hasProperty((Property)CampfireBlock.LIT) && (Boolean)bs.getValue((Property)CampfireBlock.LIT) != false;
    }

    private static boolean isFireBlock(BlockState bs) {
        if (bs.is(Blocks.FIRE) || bs.is(Blocks.SOUL_FIRE)) {
            return true;
        }
        return (bs.is(Blocks.CAMPFIRE) || bs.is(Blocks.SOUL_CAMPFIRE)) && bs.hasProperty((Property)CampfireBlock.LIT) && (Boolean)bs.getValue((Property)CampfireBlock.LIT) != false;
    }

    private void processDroppedItems(ServerLevel level, BlockPos cell, FanProcess family, GasType tier, Map<Integer, Integer> seen) {
        double cx = (double)cell.getX() + 0.5;
        double cy = (double)cell.getY() + 0.5;
        double cz = (double)cell.getZ() + 0.5;
        AABB aabb = new AABB(cx - 0.5, cy - 0.5, cz - 0.5, cx + 0.5, cy + 0.5, cz + 0.5);
        for (ItemEntity item : BukkitContraptionLevel.unionEntities((Level)level, ItemEntity.class, aabb, e -> !e.isRemoved())) {
            ResolvedRecipe rr;
            int id = item.getId();
            net.minecraft.world.item.ItemStack stack = item.getItem();
            if (stack == null || stack.isEmpty() || (rr = this.resolve(level, stack, family, tier)) == null) continue;
            int ticks = this.itemProgress.getOrDefault(id, 0) + this.progressStep();
            if (ticks >= this.effTicks(rr.time)) {
                int amt = rr.inputAmount;
                int sets = stack.getCount() / amt;
                if (sets <= 0) {
                    seen.put(id, ticks);
                    continue;
                }
                net.minecraft.world.item.ItemStack shrunk = stack.copy();
                shrunk.shrink(sets * amt);
                item.setItem(shrunk);
                if (shrunk.isEmpty()) {
                    item.discard();
                }
                this.spawnOutputs(level, (Entity)item, cx, cy, cz, rr, sets);
                this.convertEffect(level, cx, cy, cz, family);
                seen.put(id, 0);
                continue;
            }
            seen.put(id, ticks);
        }
    }

    private void processBeltItems(ServerLevel level, BlockPos cell, FanProcess family, GasType tier, Map<Long, Integer> seen) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)level, cell);
        if (be == null || !((blockEntityController = be.controller) instanceof ConveyorBlockEntity)) {
            return;
        }
        ConveyorBlockEntity belt = (ConveyorBlockEntity)blockEntityController;
        double dx = (double)cell.getX() + 0.5;
        double dy = (double)cell.getY() + 0.7;
        double dz = (double)cell.getZ() + 0.5;
        belt.forEachCarried((displayId, carried) -> {
            if (carried == null || carried.getType().isAir() || displayId < 0) {
                return carried;
            }
            net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy((ItemStack)carried);
            ResolvedRecipe rr = this.resolve(level, nms, family, tier);
            if (rr == null) {
                return carried;
            }
            int ticks = this.beltProgress.getOrDefault(displayId, 0) + this.progressStep();
            if (ticks < this.effTicks(rr.time)) {
                seen.put(Long.valueOf(displayId), ticks);
                return carried;
            }
            int amt = rr.inputAmount;
            int sets = carried.getAmount() / amt;
            if (sets <= 0) {
                seen.put(Long.valueOf(displayId), ticks);
                return carried;
            }
            int remainder = carried.getAmount() - sets * amt;
            seen.put(Long.valueOf(displayId), 0);
            this.convertEffect(level, dx, dy, dz, family);
            if (remainder > 0) {
                this.spawnOutputs(level, null, dx, dy, dz, rr, sets);
                ItemStack rem = carried.clone();
                rem.setAmount(remainder);
                return rem;
            }
            return this.convertOnBelt(level, dx, dy, dz, rr, sets);
        });
    }

    private ResolvedRecipe resolve(ServerLevel level, net.minecraft.world.item.ItemStack stack, FanProcess family, GasType tier) {
        block12: {
            for (FanRecipe fr : dev.arubik.craftengine.machine.recipe.loader.RecipeManager.getFanRecipes()) {
                if (!fr.matches(stack, family, tier)) continue;
                ResolvedRecipe rr = new ResolvedRecipe();
                rr.inputAmount = fr.inputAmount();
                rr.time = Math.max(1, fr.getProcessTime());
                for (RecipeOutput out : fr.getOutputs()) {
                    if (!(out instanceof ItemOutput)) continue;
                    ItemOutput io = (ItemOutput)out;
                    rr.outputs.add(io.getItem().copy());
                    rr.chances.add(Float.valueOf(io.getChance()));
                }
                return rr.outputs.isEmpty() ? null : rr;
            }
            if (family == FanProcess.WASHING || family == FanProcess.FREEZING) {
                return null;
            }
            try {
                net.minecraft.world.item.ItemStack out;
                net.minecraft.world.item.ItemStack single = stack.copy();
                single.setCount(1);
                SingleRecipeInput input = new SingleRecipeInput(single);
                RecipeManager rm = level.getServer().getRecipeManager();
                RecipeType rtype = switch (family) {
                    case FanProcess.BLASTING -> RecipeType.BLASTING;
                    case FanProcess.COOKING -> RecipeType.SMOKING;
                    default -> RecipeType.SMELTING;
                };
                AbstractCookingRecipe cook = null;
                Optional h = rm.getRecipeFor(rtype, (RecipeInput)input, (Level)level);
                if (h.isPresent()) {
                    cook = (AbstractCookingRecipe)((RecipeHolder)h.get()).value();
                }
                if (cook == null || (out = cook.assemble(input, (HolderLookup.Provider)level.registryAccess())) == null || out.isEmpty()) break block12;
                ResolvedRecipe rr = new ResolvedRecipe();
                rr.inputAmount = 1;
                try {
                    rr.time = Math.max(1, cook.cookingTime());
                }
                catch (Throwable t) {
                    rr.time = family == FanProcess.SMELTING ? 200 : 100;
                }
                rr.outputs.add(out.copy());
                rr.chances.add(Float.valueOf(1.0f));
                return rr;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return null;
    }

    private void spawnOutputs(ServerLevel level, double x, double y, double z, ResolvedRecipe rr) {
        this.spawnOutputs(level, null, x, y, z, rr, 1);
    }

    private ItemStack convertOnBelt(ServerLevel level, double x, double y, double z, ResolvedRecipe rr, int sets) {
        ItemStack beltStack = null;
        for (int i = 0; i < rr.outputs.size(); ++i) {
            int total;
            net.minecraft.world.item.ItemStack base = rr.outputs.get(i);
            float chance = rr.chances.get(i).floatValue();
            if (chance >= 1.0f) {
                total = base.getCount() * sets;
            } else {
                total = 0;
                for (int s = 0; s < sets; ++s) {
                    if (!(ThreadLocalRandom.current().nextFloat() < chance)) continue;
                    total += base.getCount();
                }
            }
            if (total <= 0) continue;
            int max = Math.max(1, base.getMaxStackSize());
            if (i == 0) {
                int onBelt = Math.min(max, total);
                beltStack = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)base.copy());
                beltStack.setAmount(onBelt);
                total -= onBelt;
            }
            while (total > 0) {
                int c = Math.min(max, total);
                net.minecraft.world.item.ItemStack out = base.copy();
                out.setCount(c);
                FanMachineBlockEntity.dropItem(level, null, x, y, z, out);
                total -= c;
            }
        }
        return beltStack;
    }

    private void convertEffect(ServerLevel level, double x, double y, double z, FanProcess family) {
        CraftWorld w = level.getWorld();
        Location loc = new Location((World)w, x, y, z);
        switch (family) {
            case BLASTING: {
                w.spawnParticle(Particle.LAVA, x, y, z, 6, 0.18, 0.18, 0.18, 0.0);
                w.spawnParticle(Particle.LARGE_SMOKE, x, y, z, 4, 0.15, 0.2, 0.15, 0.01);
                w.playSound(loc, Sound.BLOCK_LAVA_POP, 0.6f, 1.4f);
                break;
            }
            case SMELTING: {
                w.spawnParticle(Particle.FLAME, x, y, z, 6, 0.18, 0.18, 0.18, 0.01);
                w.spawnParticle(Particle.SMOKE, x, y, z, 5, 0.15, 0.2, 0.15, 0.01);
                w.playSound(loc, Sound.BLOCK_LAVA_POP, 0.5f, 1.8f);
                break;
            }
            case COOKING: {
                w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, x, y, z, 6, 0.15, 0.25, 0.15, 0.01);
                w.spawnParticle(Particle.SMOKE, x, y, z, 4, 0.15, 0.2, 0.15, 0.01);
                w.playSound(loc, Sound.BLOCK_CAMPFIRE_CRACKLE, 0.6f, 1.2f);
                break;
            }
            case WASHING: {
                w.spawnParticle(Particle.SPLASH, x, y, z, 10, 0.2, 0.25, 0.2, 0.05);
                w.spawnParticle(Particle.BUBBLE, x, y, z, 6, 0.18, 0.1, 0.18, 0.02);
                w.playSound(loc, Sound.ENTITY_PLAYER_SPLASH, 0.5f, 1.6f);
                break;
            }
            case FREEZING: {
                w.spawnParticle(Particle.SNOWFLAKE, x, y, z, 12, 0.2, 0.25, 0.2, 0.02);
                w.spawnParticle(Particle.ITEM_SNOWBALL, x, y, z, 6, 0.18, 0.15, 0.18, 0.01);
                w.playSound(loc, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.4f);
                break;
            }
        }
    }

    private void spawnOutputs(ServerLevel level, Entity inputEntity, double x, double y, double z, ResolvedRecipe rr, int sets) {
        if (sets <= 0) {
            return;
        }
        for (int i = 0; i < rr.outputs.size(); ++i) {
            int total;
            net.minecraft.world.item.ItemStack base = rr.outputs.get(i);
            float chance = rr.chances.get(i).floatValue();
            int perSet = base.getCount();
            if (chance >= 1.0f) {
                total = perSet * sets;
            } else {
                total = 0;
                for (int s = 0; s < sets; ++s) {
                    if (!(ThreadLocalRandom.current().nextFloat() < chance)) continue;
                    total += perSet;
                }
            }
            if (total <= 0) continue;
            int max = Math.max(1, base.getMaxStackSize());
            while (total > 0) {
                int c = Math.min(max, total);
                net.minecraft.world.item.ItemStack out = base.copy();
                out.setCount(c);
                FanMachineBlockEntity.dropItem(level, inputEntity, x, y, z, out);
                total -= c;
            }
        }
    }

    private void spawnSecondaryOutputs(ServerLevel level, double x, double y, double z, ResolvedRecipe rr) {
        for (int i = 1; i < rr.outputs.size(); ++i) {
            if (rr.chances.get(i).floatValue() < 1.0f && ThreadLocalRandom.current().nextFloat() >= rr.chances.get(i).floatValue()) continue;
            FanMachineBlockEntity.dropItem(level, null, x, y, z, rr.outputs.get(i).copy());
        }
    }

    private static void dropItem(ServerLevel level, Entity inputEntity, double x, double y, double z, net.minecraft.world.item.ItemStack out) {
        BukkitContraptionLevel.spawnProcessingOutput(level, inputEntity, x, y, z, out);
    }

    private boolean isPassable(BlockState bs) {
        Identifier rl;
        if (bs.isAir() || !bs.isSolidRender() || bs.is(Blocks.FIRE) || bs.is(Blocks.SOUL_FIRE) || bs.is(Blocks.WATER) || bs.is(Blocks.LAVA)) {
            return true;
        }
        return !this.passableBlocks.isEmpty() && (rl = BuiltInRegistries.BLOCK.getKey(bs.getBlock())) != null && this.passableBlocks.contains(Key.of((String)rl.getNamespace(), (String)rl.getPath()));
    }

    private void applyPushAndParticles(World bukkitWorld, ServerLevel level, BlockPos cell, Direction facing, double pushStrength, Particle particle) {
        double cx = (double)cell.getX() + 0.5;
        double cy = (double)cell.getY() + 0.5;
        double cz = (double)cell.getZ() + 0.5;
        Vector push = new Vector(facing.getStepX(), facing.getStepY(), facing.getStepZ()).multiply(pushStrength);
        double back = 0.45;
        double lat = 0.22;
        double sx = cx - (double)facing.getStepX() * back + FanMachineBlockEntity.rand(lat) * (double)(1 - Math.abs(facing.getStepX()));
        double sy = cy - (double)facing.getStepY() * back + FanMachineBlockEntity.rand(lat) * (double)(1 - Math.abs(facing.getStepY()));
        double sz = cz - (double)facing.getStepZ() * back + FanMachineBlockEntity.rand(lat) * (double)(1 - Math.abs(facing.getStepZ()));
        double flow = Math.max(0.06, pushStrength * 0.9);
        if (level instanceof ContraptionLevel) {
            ParticleOptions nms = FanMachineBlockEntity.nmsParticle(particle);
            level.sendParticlesSource(level.players(), null, nms, true, true, sx, sy, sz, 0, (double)facing.getStepX(), (double)facing.getStepY(), (double)facing.getStepZ(), flow);
        } else {
            bukkitWorld.spawnParticle(particle, sx, sy, sz, 0, (double)facing.getStepX(), (double)facing.getStepY(), (double)facing.getStepZ(), flow);
        }
        AABB aabb = new AABB(cx - 0.5, cy - 0.5, cz - 0.5, cx + 0.5, cy + 0.5, cz + 0.5);
        for (Entity nms : BukkitContraptionLevel.unionEntities((Level)level, Entity.class, aabb, e -> !e.isRemoved())) {
            BukkitContraptionLevel.pushEntity((Level)level, nms, push);
        }
    }

    private static double rand(double range) {
        return (ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0) * range;
    }

    private static ParticleOptions nmsParticle(Particle particle) {
        if (particle == null) {
            return ParticleTypes.CLOUD;
        }
        switch (particle) {
            case SMOKE: {
                return ParticleTypes.SMOKE;
            }
            case LARGE_SMOKE: {
                return ParticleTypes.LARGE_SMOKE;
            }
            case SNOWFLAKE: {
                return ParticleTypes.SNOWFLAKE;
            }
        }
        return ParticleTypes.CLOUD;
    }

    private void applyFanThrust(ServerLevel serverLevel, BlockPos cell, Direction facing, double magnitude) {
        if (!(serverLevel instanceof ContraptionLevel)) {
            return;
        }
        ContraptionLevel cl = (ContraptionLevel)serverLevel;
        ContraptionState owner = null;
        for (ContraptionEntity ce : ContraptionManager.all()) {
            if (ce.state().level() != cl) continue;
            owner = ce.state();
            break;
        }
        if (owner == null || !Key.of((String)"polyfills", (String)"phys").equals(owner.bearingType())) {
            return;
        }
        Vec3 worldDir = cl.rotateToRealWorld(new Vec3((double)(-facing.getStepX()), (double)(-facing.getStepY()), (double)(-facing.getStepZ())));
        double len = worldDir.length();
        if (len < 1.0E-9) {
            return;
        }
        double f = magnitude * 1.0 / len;
        Vec3 point = cl.realWorldPositionOf(new Vec3((double)cell.getX() + 0.5, (double)cell.getY() + 0.5, (double)cell.getZ() + 0.5));
        PhysicsWorld.applyThrust(owner.id(), new Vector3d(point.x, point.y, point.z), new Vector3d(worldDir.x * f, worldDir.y * f, worldDir.z * f));
    }

    private void applyGasEntityEffects(ServerLevel level, BlockPos cell, GasType tier, FanProcess family, boolean nitroSteam) {
        if (tier != GasType.NITROGEN && tier != GasType.STEAM && tier != GasType.HEAVY_STEAM) {
            return;
        }
        double cx = (double)cell.getX() + 0.5;
        double cy = (double)cell.getY() + 0.5;
        double cz = (double)cell.getZ() + 0.5;
        AABB aabb = new AABB(cx - 0.5, cy - 0.5, cz - 0.5, cx + 0.5, cy + 0.5, cz + 0.5);
        for (Entity entity : BukkitContraptionLevel.unionEntities((Level)level, net.minecraft.world.entity.LivingEntity.class, aabb, e -> !e.isRemoved())) {
            CraftEntity be = entity.getBukkitEntity();
            if (!(be instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity)be;
            if (tier == GasType.NITROGEN) {
                if (family == FanProcess.FREEZING) {
                    FanMachineBlockEntity.freeze(living);
                    FanMachineBlockEntity.slow(living);
                    continue;
                }
                if (nitroSteam) {
                    FanMachineBlockEntity.slow(living);
                    continue;
                }
                FanMachineBlockEntity.freeze(living);
                continue;
            }
            if (family != FanProcess.WASHING) continue;
            living.setFireTicks(0);
        }
    }

    private static void freeze(LivingEntity living) {
        int max = Math.max(140, living.getMaxFreezeTicks());
        living.setFreezeTicks(Math.min(max, living.getFreezeTicks() + 60));
    }

    private static void slow(LivingEntity living) {
        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, true, false, true));
    }

    private void syncPowered(ServerLevel level, BlockPos pos, boolean blowing) {
        try {
            BlockState ms = level.getBlockState(pos);
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(ms).orElse(null);
            if (cs == null || cs.isEmpty()) {
                return;
            }
            net.momirealms.craftengine.core.block.property.Property p = cs.getProperty(PROP_POWERED);
            if (p == null) {
                return;
            }
            net.momirealms.craftengine.core.block.property.Property pp = p;
            if ((Boolean)cs.get(pp) == blowing) {
                return;
            }
            ImmutableBlockState ns = cs.with(pp, (Comparable)Boolean.valueOf(blowing));
            level.setBlock(pos, (BlockState)ns.customBlockState().minecraftState(), 3);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
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

    private GasStack storedGas() {
        GasStack s = ((GasTank)this.gasTanks.get(0)).getGas(this.getNMSLevel(), this.getMachinePos());
        return s == null ? GasStack.EMPTY : s;
    }

    @Override
    public double[] barStat(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "fan_buffer".equals(id)) {
            GasStack s = this.storedGas();
            return new double[]{s.isEmpty() ? 0.0 : (double)s.getAmount(), Math.max(1, this.effCapacity())};
        }
        if ("progress".equals(id)) {
            int total = this.effTicks(this.baseProcessingTicks);
            int best = 0;
            for (int v : this.itemProgress.values()) {
                best = Math.max(best, v);
            }
            for (int v : this.beltProgress.values()) {
                best = Math.max(best, v);
            }
            return new double[]{Math.min(best, total), Math.max(1, total)};
        }
        return super.barStat(id);
    }

    @Override
    public String barSubtype(String id) {
        if ("gas".equals(id) || "buffer".equals(id) || "fan_buffer".equals(id)) {
            GasStack s = this.storedGas();
            return s.isEmpty() ? "" : s.getType().toString().toLowerCase(Locale.ROOT);
        }
        return super.barSubtype(id);
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
        ArrayList<HumanEntity> viewers = new ArrayList<>(this.active.getInventory().getViewers());
        this.shownUnlocked = this.curUnlocked;
        for (HumanEntity h : viewers) {
            if (!(h instanceof org.bukkit.entity.Player)) continue;
            org.bukkit.entity.Player p = (org.bukkit.entity.Player)h;
            this.openPage(p, 1);
        }
    }

    private MachineLayout buildMainLayout() {
        int infoSlot;
        MachineLayout l = new MachineLayout(InventoryType.CHEST, this.menuSize, "Fan");
        Component title = null;
        l.setTitleComponent(title != null ? title : MenuText.noI(MenuText.tr("polyfill.ui.fan_title", NamedTextColor.AQUA)));
        for (MachineMenuConfig.Button b : this.menuConfig.buttons) {
            this.installButton(l, b);
        }
        MachineBars.install(l, this.bars);
        int n = infoSlot = this.menuConfig.infoSlot >= 0 ? this.menuConfig.infoSlot : -1;
        if (infoSlot >= 0) {
            l.setDynamicProvider(infoSlot, (m, t) -> ((FanMachineBlockEntity)m).infoIcon());
        }
        this.fillRest(l);
        return l;
    }

    private ItemStack infoIcon() {
        String string;
        GasStack stored = this.storedGas();
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS);
        ItemMeta meta = stack.getItemMeta();
        NamedTextColor GRAY = NamedTextColor.GRAY;
        NamedTextColor WHITE = NamedTextColor.WHITE;
        NamedTextColor AQUA = NamedTextColor.AQUA;
        Component gasName = stored.isEmpty() ? MenuText.tr("polyfill.gas.empty", WHITE) : MenuText.tr(stored.getType().translationKey(), WHITE);
        meta.displayName(MenuText.noI(MenuText.tr("polyfill.ui.gas", AQUA).append((Component)Component.text((String)": ", (TextColor)GRAY)).append(gasName)));
        if (!this.lastBlowing) {
            string = "polyfill.ui.fan_status_idle";
        } else {
            switch (this.lastFamily) {
                case SMELTING: {
                    string = "polyfill.ui.fan_status_smelting";
                    break;
                }
                case COOKING: {
                    string = "polyfill.ui.fan_status_cooking";
                    break;
                }
                case BLASTING: {
                    string = "polyfill.ui.fan_status_blasting";
                    break;
                }
                case WASHING: {
                    string = "polyfill.ui.fan_status_washing";
                    break;
                }
                case FREEZING: {
                    string = "polyfill.ui.fan_status_freezing";
                    break;
                }
                default: {
                    string = "polyfill.ui.fan_status_blowing";
                }
            }
        }
        String statusKey = string;
        meta.lore(List.of(MenuText.noI(MenuText.kv("polyfill.ui.amount", GRAY, (stored.isEmpty() ? 0 : stored.getAmount()) + " / " + this.effCapacity() + " mB", WHITE)), MenuText.noI(MenuText.tr(statusKey, GRAY))));
        stack.setItemMeta(meta);
        return stack;
    }

    private void installButton(MachineLayout l, MachineMenuConfig.Button b) {
        l.addButton(b.slot, (m, t) -> {
            FanMachineBlockEntity s = (FanMachineBlockEntity)m;
            boolean locked = s.isButtonLocked(b);
            String iconSpec = locked && b.lockedIcon != null ? b.lockedIcon : b.icon;
            return MenuText.iconItem(FanMachineBlockEntity.parseKey(iconSpec), Material.PAPER, FanMachineBlockEntity.label(b.name, NamedTextColor.AQUA), FanMachineBlockEntity.lore(b.lore));
        }, (m, p) -> {
            FanMachineBlockEntity s = (FanMachineBlockEntity)m;
            if (s.isButtonLocked(b)) {
                return;
            }
            switch (b.action.kind) {
                case OPEN_PAGE: {
                    s.openPage((org.bukkit.entity.Player)p, b.action.page);
                    break;
                }
                case DEPLETE_GAS: {
                    ((GasTank)s.gasTanks.get(0)).deplete(s.getNMSLevel(), s.getMachinePos());
                    s.setChanged();
                    break;
                }
            }
        });
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
            out[i] = FanMachineBlockEntity.label(lines.get(i), NamedTextColor.GRAY);
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
        l.addButton(17, (m, t) -> MenuText.backIcon(), (m, p) -> ((FanMachineBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0));
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
        this.overclock = (float)FanMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.upgradeRecomputeCd = 0;
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
        this.set(KEY_OC, Float.valueOf(this.overclock));
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.overclock = this.getOrDefault(KEY_OC, Float.valueOf(0.0f)).floatValue();
    }

    private static final class ResolvedRecipe {
        int inputAmount = 1;
        int time = 40;
        final List<net.minecraft.world.item.ItemStack> outputs = new ArrayList<net.minecraft.world.item.ItemStack>();
        final List<Float> chances = new ArrayList<Float>();

        private ResolvedRecipe() {
        }

        ItemStack primaryBukkit() {
            if (this.outputs.isEmpty()) {
                return null;
            }
            return CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)this.outputs.get(0).copy());
        }
    }
}

