/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.api.CraftEngineItems
 *  net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  net.momirealms.craftengine.libraries.nbt.CompoundTag
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.contraption.api.ContraptionTickable;
import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.conveyor.routing.ConveyorDisplayReceiver;
import dev.arubik.craftengine.conveyor.belt.ConveyorItemDisplay;
import dev.arubik.craftengine.conveyor.belt.ConveyorMath;
import dev.arubik.craftengine.conveyor.routing.ConveyorReceiver;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.MachineFuelRecipe;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.condition.RecipeCondition;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import dev.arubik.craftengine.machine.upgrade.UpgradeRegistry;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.multiblock.MachineMode;
import dev.arubik.craftengine.util.BridgeUtils;
import dev.arubik.craftengine.util.DirectionType;
import dev.arubik.craftengine.util.NbtType;
import dev.arubik.craftengine.util.TypedKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class AbstractMachineBlockEntity
extends PersistentWorldlyBlockEntity
implements ContraptionTickable,
ConveyorDisplayReceiver {
    private UUID ownerUuid;
    protected int progress = 0;
    protected double progressCarry = 0.0;
    protected int maxProgress = 0;
    protected int burnTime = 0;
    protected int maxBurnTime = 0;
    protected int overclockedTicks = 0;
    protected boolean isProcessing = false;
    protected IOConfiguration ioConfiguration;
    protected boolean requiresRedstone = false;
    protected boolean invertRedstone = false;
    protected float storedXp = 0.0f;
    private static final TypedKey<Float> KEY_XP = TypedKey.of("craftengine", "machine_xp", NbtType.FLOAT);
    protected final List<FluidTank> fluidTanks = new ArrayList<FluidTank>();
    protected final List<GasTank> gasTanks = new ArrayList<GasTank>();
    /** CraftEnergy buffer — a single int, unlike fluid/gas there's no type/tank list, just an
     *  amount capped by {@link #energyCapacity}. Zero capacity means this machine declared no
     *  {@code MachineDefinition.EnergySpec}, and every energy method below is then a no-op. */
    protected int energy = 0;
    protected int energyCapacity = 0;
    protected int energyMaxInput = 0;
    protected int energyMaxOutput = 0;
    protected int energyPerTick = 0;
    protected UpgradeModifiers upgradeModifiers = UpgradeModifiers.NONE;
    private static final TypedKey<Integer> KEY_PROGRESS = TypedKey.of("craftengine", "machine_progress", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_MAX_PROGRESS = TypedKey.of("craftengine", "machine_max_progress", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_BURN_TIME = TypedKey.of("craftengine", "machine_burn_time", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_MAX_BURN_TIME = TypedKey.of("craftengine", "machine_max_burn_time", NbtType.INTEGER);
    private static final TypedKey<Integer> KEY_OVERCLOCKED_TICKS = TypedKey.of("craftengine", "machine_overclocked_ticks", NbtType.INTEGER);
    private static final TypedKey<String> KEY_OWNER_UUID = TypedKey.of("polyfills", "machine_owner_uuid", NbtType.STRING);
    protected MachineMenu menu;
    public static boolean DEBUG_IO = false;
    private CEWorld ceWorld;
    private final Map<net.minecraft.core.Direction, FunnelTransit> funnelTransits = new EnumMap<net.minecraft.core.Direction, FunnelTransit>(net.minecraft.core.Direction.class);
    private Boolean lastActivated = null;

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public void setOwnerUuid(UUID uuid) {
        this.ownerUuid = uuid;
    }

    public boolean isProcessing() {
        return this.isProcessing;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public AbstractMachineBlockEntity(BlockEntity blockEntity, int size) {
        super(blockEntity, size);
    }

    @Override
    public net.momirealms.craftengine.core.world.BlockPos pos() {
        return this.blockEntity().pos();
    }

    public BlockPos getMachinePos() {
        return BlockPos.of((long)this.blockEntity().pos().asLong());
    }

    public float getStoredXp() {
        return this.storedXp;
    }

    public void accumulateXp(float amount) {
        this.storedXp += amount;
        this.setChanged();
    }

    public void awardXp(Player player) {
        if (this.storedXp > 0.0f) {
            player.giveExperiencePoints((int)this.storedXp);
            this.storedXp = 0.0f;
            this.setChanged();
        }
    }

    public List<GasTank> gasTankList() { return this.gasTanks; }
    public List<FluidTank> fluidTankList() { return this.fluidTanks; }

    public FluidStack getFluidInSlot(int slot) {
        return this.get(this.fluidTanks.get(slot).getKey());
    }

    public FluidStack getStoredFluidForCarrier() {
        if (this.fluidTanks.isEmpty()) {
            return FluidStack.EMPTY;
        }
        FluidStack s = this.fluidTanks.get(0).getFluid(this.getNMSLevel(), this.getMachinePos());
        return s == null ? FluidStack.EMPTY : s;
    }

    public long getFluidCapacityForCarrier() {
        return this.fluidTanks.isEmpty() ? 0L : (long)this.fluidTanks.get(0).getCapacity();
    }

    public void setStoredFluidRaw(Level level, FluidStack stack) {
        if (this.fluidTanks.isEmpty()) {
            return;
        }
        TypedKey<FluidStack> key = this.fluidTanks.get(0).getKey();
        PersistentBlockEntity.executeAt(level, this.getMachinePos(), be -> {
            if (stack == null || stack.isEmpty()) {
                be.remove(key);
            } else {
                be.set(key, stack);
            }
        });
    }

    public GasStack getGasInSlot(int slot) {
        return this.get(this.gasTanks.get(slot).getKey());
    }

    public int insertFluidInSlot(int slot, FluidStack stack, Level level) {
        return this.fluidTanks.get(slot).insert(level, this.getMachinePos(), stack);
    }

    public int extractFluidInSlot(int slot, int amount, Level level, Consumer<FluidStack> drained) {
        return this.fluidTanks.get(slot).extract(level, this.getMachinePos(), amount, drained);
    }

    public int insertGasInSlot(int slot, GasStack stack, Level level) {
        return this.gasTanks.get(slot).insert(level, this.getMachinePos(), stack);
    }

    public int extractGasInSlot(int slot, int amount, Level level, Consumer<GasStack> drained) {
        return this.gasTanks.get(slot).extract(level, this.getMachinePos(), amount, drained);
    }

    public void addFluidTank(FluidTank tank) {
        this.fluidTanks.add(tank);
    }

    public void addGasTank(GasTank tank) {
        this.gasTanks.add(tank);
    }

    public void setIOConfiguration(IOConfiguration config) {
        this.ioConfiguration = config;
    }

    public IOConfiguration getIOConfiguration() {
        return this.ioConfiguration;
    }

    /** Caches {@link #getFacing}/{@link #getFacingAxisRef}/{@link #faceSplitRpmAllows}'s shared
     *  block-state-derived resolution ({@code state} kept for reference-equality invalidation only
     *  — NMS interns identical property combinations, so two calls seeing the "same" BlockState get
     *  the literal same object; a real rotation/placement change always produces a different one).
     *  {@code faceVal} is the raw lowercased "face" property value ({@code null} if absent), used
     *  by {@link #faceSplitRpmAllows} without redoing the BlockDefinition/property lookup itself. */
    private record FacingCache(BlockState state, net.minecraft.core.Direction facing,
                                net.minecraft.core.Direction axisRef, String faceVal) {}
    private FacingCache facingCache;

    /** Single block-state-derived resolution pass backing {@link #getFacing}, {@link
     *  #getFacingAxisRef}, and {@link #faceSplitRpmAllows} — all three used to independently redo
     *  the same {@code getBlockState}/{@code getOptionalCustomBlockState}/{@code getProperty}/
     *  {@code get} lookups on every single call (profiling showed this as real, avoidable
     *  server-thread cost inside {@code pullRotationalPower}'s kinetic-routing checks, which call
     *  these several times per tick per machine). A machine's facing/face properties only change on
     *  rotation or placement — nowhere near every tick — so this recomputes ONLY when {@code
     *  level.getBlockState(pos)} returns a genuinely different {@link BlockState} than last seen,
     *  reusing the cached {@link FacingCache} otherwise. Faithfully reproduces each of the three
     *  original methods' own branching logic (see their current bodies below), just computed once
     *  instead of three times. */
    private FacingCache resolveFacingCache(Level level) {
        if (level == null) return null;
        BlockPos pos = this.getMachinePos();
        BlockState state = level.getBlockState(pos);
        FacingCache cached = this.facingCache;
        if (cached != null && cached.state() == state) {
            return cached;
        }
        net.minecraft.core.Direction facing = null;
        net.minecraft.core.Direction axisRef = null;
        String faceVal = null;
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null) {
            try {
                BlockDefinition def = (BlockDefinition) customState.owner().value();
                // Button-style split state (vanilla ButtonBlock/FaceAttachedHorizontalDirectionalBlock:
                // a "face" property — floor/wall/ceiling — PLUS a horizontal "facing" that only means
                // anything when face=wall) — the saw uses this (see saw.yml) to get 12 real states
                // (4 facings x 3 faces) instead of a plain 6-direction enum, so floor/ceiling mounting
                // can still track which horizontal axis the belt runs along underneath/above it.
                // Checked BEFORE the single-property loop below: a block with BOTH "face" and "facing"
                // would otherwise just report whatever "facing" says even when face=floor/ceiling,
                // which should really resolve to UP/DOWN regardless of the stored horizontal value.
                Property faceProp = def.getProperty("face");
                if (faceProp != null) {
                    faceVal = String.valueOf(customState.get(faceProp)).toLowerCase();
                    switch (faceVal) {
                        case "floor": facing = net.minecraft.core.Direction.UP; break;
                        case "ceiling": facing = net.minecraft.core.Direction.DOWN; break;
                        case "wall": {
                            Property wallFacingProp = def.getProperty("facing");
                            if (wallFacingProp != null) {
                                net.minecraft.core.Direction wallDir = net.minecraft.core.Direction.byName(
                                        String.valueOf(customState.get(wallFacingProp)).toLowerCase());
                                if (wallDir != null) facing = wallDir;
                            }
                            break;
                        }
                    }
                    // getFacingAxisRef's own independent RAW "facing" lookup — regardless of what
                    // faceVal is, matching its original "if def.getProperty(face) != null" gate.
                    Property rawFacingProp = def.getProperty("facing");
                    if (rawFacingProp != null) {
                        net.minecraft.core.Direction rawDir = net.minecraft.core.Direction.byName(
                                String.valueOf(customState.get(rawFacingProp)).toLowerCase());
                        if (rawDir != null) axisRef = rawDir;
                    }
                }
                if (facing == null) {
                    // Some block configs expose their orientation under "facing", others under
                    // "horizontal_facing" / "6_direction" / "4_direction" (see the equivalent lookup in
                    // DataMachineBlockEntity.getBlockFunctionalAxis). Checking only "facing" here made
                    // every bearing whose config uses one of the other names silently report NORTH no
                    // matter which way it was actually placed, which sent the windmill bearing's
                    // "seed" lookup (facing_dx/dy/dz) at the wrong neighbor whenever the bearing itself
                    // wasn't glued into the structure.
                    for (String propName : new String[]{"facing", "horizontal_facing", "6_direction", "4_direction"}) {
                        Property facingProp = def.getProperty(propName);
                        if (facingProp == null) continue;
                        net.minecraft.core.Direction d = net.minecraft.core.Direction.byName(
                                String.valueOf(customState.get(facingProp)).toLowerCase());
                        if (d != null) { facing = d; break; }
                    }
                }
            } catch (Throwable ignored) {
                // empty catch block — matches both original methods' own defensive catch
            }
            if (facing == null) {
                BlockBehavior beh = customState.behavior();
                ConnectableBlockBehavior cbb = beh instanceof ConnectableBlockBehavior ? (ConnectableBlockBehavior) beh
                        : (beh != null ? (ConnectableBlockBehavior) beh.getFirst(ConnectableBlockBehavior.class) : null);
                if (cbb != null) {
                    net.minecraft.core.Direction d = cbb.toDirection(state);
                    if (d != null) facing = d;
                }
            }
            if (facing == null) facing = net.minecraft.core.Direction.NORTH;
            if (axisRef == null) axisRef = facing;
        }
        // customState == null: facing/axisRef/faceVal stay null — matches getFacing returning
        // null, and getFacingAxisRef's "if (customState == null) return this.getFacing(level);"
        // (which itself also returns null in that case).
        FacingCache fresh = new FacingCache(state, facing, axisRef, faceVal);
        this.facingCache = fresh;
        return fresh;
    }

    protected net.minecraft.core.Direction getFacing(Level level) {
        FacingCache fc = this.resolveFacingCache(level);
        return fc != null ? fc.facing() : null;
    }

    /**
     * The RAW stored horizontal "facing" property, ignoring any "face" (floor/wall/ceiling) split
     * — for a button-style block (see {@link #getFacing}), {@code getFacing()} collapses
     * face=floor/ceiling down to plain UP/DOWN, which is correct for "front"/"back"/render-facing
     * purposes but throws away exactly the piece of information "left"/"right" IO faces need: which
     * horizontal axis a floor/ceiling-mounted saw's belt runs along, so its perpendicular
     * (left/right) shaft connection faces can be computed. {@code Direction.getClockWise()}/
     * {@code getCounterClockWise()} both throw for a Y-axis input (UP/DOWN has no inherent
     * "clockwise" without a reference axis) — see {@code DataMachineBlockEntity#rpmFacesContain}'s
     * horizontalRef parameter, which is exactly what this feeds. Falls back to {@link #getFacing}
     * for a plain (non-face-split) block, where the two concepts are the same thing anyway.
     */
    /**
     * The built-in RPM connection rule for a button-style face-split block (see {@link
     * #getFacing}/{@link #getFacingAxisRef}) — Create's own real saw: wall-mounted takes input
     * from directly BEHIND only and never outputs anywhere (the business end doesn't drive
     * anything further); floor/ceiling-mounted acts like a shaft passing crosswise underneath/
     * above it, connecting on BOTH the left and right of the belt axis, for BOTH input and output.
     * Takes precedence over the machine definition's own declared io.rpm face lists whenever this
     * returns non-null — a block with no "face" property at all (every machine other than the saw)
     * gets null here, meaning "not applicable, use the normal io.rpm declaration instead."
     */
    protected Boolean faceSplitRpmAllows(net.minecraft.core.Direction d, boolean forOutput, Level level) {
        if (d == null) return null;
        FacingCache fc = this.resolveFacingCache(level);
        if (fc == null || fc.faceVal() == null) return null;
        String faceVal = fc.faceVal();
        if ("wall".equals(faceVal)) {
            if (forOutput) return false;
            net.minecraft.core.Direction facing = fc.facing();
            return facing != null && d == facing.getOpposite();
        }
        // floor or ceiling
        net.minecraft.core.Direction axisRef = fc.axisRef();
        if (axisRef == null || axisRef.getAxis() == net.minecraft.core.Direction.Axis.Y) return false;
        return d == axisRef.getClockWise() || d == axisRef.getCounterClockWise();
    }

    public net.minecraft.core.Direction getFacingAxisRef(Level level) {
        FacingCache fc = this.resolveFacingCache(level);
        return fc != null ? fc.axisRef() : null;
    }

    public boolean fillTank(Level level, FluidStack fluid) {
        boolean changed = false;
        for (FluidTank tank : this.fluidTanks) {
            int accepted = tank.insert(level, this.getMachinePos(), fluid);
            if (accepted <= 0) continue;
            fluid.removeAmount(accepted);
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
        return fluid.isEmpty();
    }

    public int insertFluid(Level level, FluidStack stack, net.minecraft.core.Direction side) {
        return this.insertFluid(level, stack, side, -1);
    }

    public int insertFluid(Level level, FluidStack stack, net.minecraft.core.Direction side, int slot) {
        block8: {
            boolean changed;
            int accepted;
            block10: {
                FluidStack copy;
                block9: {
                    if (this.ioConfiguration == null) break block8;
                    net.minecraft.core.Direction localDir = side;
                    DirectionType dirType = DirectionType.HORIZONTAL;
                    BlockState state = level.getBlockState(this.getMachinePos());
                    Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
                    if (customState.isPresent()) {
                        CompositeBlockBehavior compositeBlockBehavior;
                        ConnectableBlockBehavior cbb;
                        BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                        if (behavior instanceof ConnectableBlockBehavior) {
                            ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                            localDir = connectableBlockBehavior.toLocalDirection(side, state);
                        }
                        if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                            localDir = cbb.toLocalDirection(side, state);
                        }
                    }
                    if (!this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.FLUID, localDir)) {
                        return 0;
                    }
                    if (this.runOnTransferScript("fluid", stackPayload(stack.getType() == null ? null : stack.getType().id().toString(), stack.getAmount()), side, "input")) {
                        return 0;
                    }
                    int targetSlot = slot;
                    if (targetSlot == -1) {
                        targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID, localDir);
                    }
                    copy = stack.copy();
                    accepted = 0;
                    changed = false;
                    if (targetSlot == -1) break block9;
                    if (targetSlot < 0 || targetSlot >= this.fluidTanks.size() || (accepted = this.fluidTanks.get(targetSlot).insert(level, this.getMachinePos(), copy)) <= 0) break block10;
                    changed = true;
                    break block10;
                }
                for (FluidTank tank : this.fluidTanks) {
                    int moved = tank.insert(level, this.getMachinePos(), copy);
                    if (moved > 0) {
                        copy.removeAmount(moved);
                        accepted += moved;
                        changed = true;
                    }
                    if (!copy.isEmpty()) continue;
                    break;
                }
            }
            if (changed) {
                this.setChanged();
            }
            return accepted;
        }
        return 0;
    }

    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side) {
        return this.extractFluid(level, max, drained, side, -1);
    }

    public int extractFluid(Level level, int max, Consumer<FluidStack> drained, net.minecraft.core.Direction side, int slot) {
        if (this.ioConfiguration != null) {
            net.minecraft.core.Direction localDir = side;
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior compositeBlockBehavior;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(side, state);
                }
            }
            if (!this.getIOConfiguration().providesOutput(IOConfiguration.IOType.FLUID, localDir)) {
                return 0;
            }
            FluidStack aboutToLeave = this.getStoredFluidForCarrier();
            if (this.runOnTransferScript("fluid",
                    stackPayload(aboutToLeave.isEmpty() ? null : aboutToLeave.getType().id().toString(), max), side,
                    "output")) {
                return 0;
            }
            boolean[] changed = new boolean[]{false};
            Consumer<FluidStack> hookDrained = s -> {
                if (drained != null) {
                    drained.accept((FluidStack)s);
                }
                changed[0] = true;
            };
            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.FLUID, localDir);
            }
            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < this.fluidTanks.size()) {
                    int extracted = this.fluidTanks.get(targetSlot).extract(level, this.getMachinePos(), max, hookDrained);
                    if (changed[0]) {
                        this.setChanged();
                    }
                    return extracted;
                }
                return 0;
            }
            for (FluidTank tank : this.fluidTanks) {
                int extracted = tank.extract(level, this.getMachinePos(), max, hookDrained);
                if (extracted <= 0) continue;
                if (changed[0]) {
                    this.setChanged();
                }
                return extracted;
            }
        }
        return 0;
    }

    public void fillGasTank(Level level, GasStack gas) {
        boolean changed = false;
        for (GasTank tank : this.gasTanks) {
            int accepted = tank.insert(level, this.getMachinePos(), gas);
            if (accepted <= 0) continue;
            gas.shrink(accepted);
            changed = true;
            if (!gas.isEmpty()) continue;
            break;
        }
        if (changed) {
            this.setChanged();
        }
    }

    public int insertGas(Level level, GasStack stack, net.minecraft.core.Direction side) {
        return this.insertGas(level, stack, side, -1);
    }

    /**
     * Does this machine's declared IO allow gas to cross {@code worldSide}?
     * {@code input=true} asks about gas coming IN through that face, {@code false} about gas
     * going OUT. Used by the gas network solver, which writes tanks directly and so would
     * otherwise bypass every rule enforced by {@link #insertGas} / {@link #extractGas}.
     */
    public boolean allowsGas(Level level, net.minecraft.core.Direction worldSide, boolean input) {
        if (this.ioConfiguration == null || this.gasTanks.isEmpty()) {
            return false;
        }
        net.minecraft.core.Direction localDir = worldSide;
        try {
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior composite;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior connectable) {
                    localDir = connectable.toLocalDirection(worldSide, state);
                }
                if (behavior instanceof CompositeBlockBehavior
                        && (cbb = (ConnectableBlockBehavior)((composite = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(worldSide, state);
                }
            }
        } catch (Throwable ignored) {
            // Fall back to the world direction; a wrong rotation is better than a crash in the solver.
        }
        return input
                ? this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.GAS, localDir)
                : this.getIOConfiguration().providesOutput(IOConfiguration.IOType.GAS, localDir);
    }

    /**
     * This machine's IO config as a mutable, block-entity-owned copy.
     *
     * <p>A machine is built pointing at the {@code IOConfiguration} held by its shared
     * {@link dev.arubik.craftengine.machine.MachineDefinition}. Editing that object directly would
     * silently reconfigure EVERY machine of the same type, so the first mutation swaps in a private
     * copy. Subsequent calls return that same copy.
     *
     * <p>The copy is rebuilt by probing every (type, face) pair through the public interface rather
     * than reaching into {@code Simple}'s fields: 7 types x 6 faces is trivial, and it works for any
     * implementation — {@code Open}, {@code Simple}, {@code WithTransferRate} alike.
     */
    public IOConfiguration.Simple mutableIO() {
        IOConfiguration current = this.getIOConfiguration();
        if (current instanceof IOConfiguration.Simple simple && this.ownsIOConfiguration) {
            return simple;
        }
        // No per-entity config yet (definition declared no "io" block — e.g. item_pipe_panel):
        // the BLOCK's own defaultIOConfig (typically Open — see ConnectableBlockBehavior's
        // no-arg constructor) is what every read (Machine.io_get, carrierConnectsHere, the
        // network engines' faceMode) actually falls back to and treats as this face's live
        // permission RIGHT NOW. Seeding the private copy from null instead of that default would
        // silently CLOSE every other face the moment the player touches just one of them — the
        // copy has to start from what's really in effect, not from "nothing granted".
        if (current == null) {
            current = this.resolveBehaviorDefaultIOConfig();
        }
        IOConfiguration.Simple copy = new IOConfiguration.Simple();
        if (current != null) {
            for (IOConfiguration.IOType type : IOConfiguration.IOType.values()) {
                for (net.minecraft.core.Direction dir : dev.arubik.craftengine.util.Utils.DIRECTIONS) {
                    try {
                        if (current.acceptsInput(type, dir)) copy.addInput(type, dir);
                        if (current.providesOutput(type, dir)) copy.addOutput(type, dir);
                    } catch (Throwable ignored) {
                        // An implementation that cannot answer for a pair simply grants nothing.
                    }
                }
                // acceptsInput/providesOutput only probe PER-FACE PERMISSION, never which physical
                // slots those permissions apply to (see IOConfiguration#getSlots's javadoc) — this
                // loop was silently dropping a machine's declared INPUT/OUTPUT/FUEL slot list every
                // time anything (a pipe panel, Machine.io_set, ...) triggered the very first
                // mutableIO() copy, since getSlots() was never probed/copied alongside
                // acceptsInput/providesOutput above. A machine with a real "io" block AND declared
                // page slots (e.g. the drill) would work correctly right up until the first io
                // mutation, then silently lose every output/input slot a hopper/funnel/pipe could
                // see — this restores them onto the copy.
                for (IOConfiguration.IORole role : IOConfiguration.IORole.values()) {
                    try {
                        int[] slots = current.getSlots(type, role);
                        if (slots.length > 0) copy.setSlots(type, role, slots);
                    } catch (Throwable ignored) {
                        // An implementation with no slot concept (Open, ...) simply carries none over.
                    }
                }
            }
        }
        this.setIOConfiguration(copy);
        this.ownsIOConfiguration = true;
        return copy;
    }

    /** The block's own static {@code defaultIOConfig} (see {@code ConnectableBlockBehavior}) —
     * what {@link #getIOConfiguration()} being {@code null} actually resolves to everywhere else. */
    private IOConfiguration resolveBehaviorDefaultIOConfig() {
        try {
            Level level = this.getNMSLevel();
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional<ImmutableBlockState> custom = BlockStateUtils.getOptionalCustomBlockState(state);
            if (custom.isEmpty()) return null;
            net.momirealms.craftengine.core.block.behavior.BlockBehavior behavior = custom.get().behavior();
            if (behavior instanceof MachineBlockBehavior mbb) return mbb.defaultIOConfig;
            if (behavior instanceof ConnectableBlockBehavior cbb) return cbb.defaultIOConfig;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /** True once {@link #mutableIO()} has given this machine its own config. */
    private boolean ownsIOConfiguration = false;

    public int insertGas(Level level, GasStack stack, net.minecraft.core.Direction side, int slot) {
        if (this.ioConfiguration != null) {
            net.minecraft.core.Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior compositeBlockBehavior;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(side, state);
                }
            }
            if (!this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.GAS, localDir)) {
                return 0;
            }
            if (stack == null || stack.isEmpty()) {
                return 0;
            }
            if (this.runOnTransferScript("gas",
                    stackPayload(stack.getType() == null ? null : stack.getType().id().toString(), stack.getAmount()),
                    side, "input")) {
                return 0;
            }
            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS, localDir);
            }
            GasStack copy = new GasStack(stack.getType(), stack.getAmount());
            int originalAmount = copy.getAmount();
            boolean changed = false;
            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < this.gasTanks.size()) {
                    int accepted = this.gasTanks.get(targetSlot).insert(level, this.getMachinePos(), copy);
                    if (accepted > 0) {
                        changed = true;
                    }
                    return accepted;
                }
                return 0;
            }
            this.fillGasTank(level, copy);
            if (originalAmount != copy.getAmount()) {
                changed = true;
            }
            if (changed) {
                this.setChanged();
            }
            return originalAmount - copy.getAmount();
        }
        return 0;
    }

    public int extractGas(Level level, int max, Consumer<GasStack> drained, net.minecraft.core.Direction side) {
        return this.extractGas(level, max, drained, side, -1);
    }

    public int extractGas(Level level, int max, Consumer<GasStack> drained, net.minecraft.core.Direction side, int slot) {
        if (this.ioConfiguration != null) {
            net.minecraft.core.Direction localDir = side;
            DirectionType dirType = DirectionType.HORIZONTAL;
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior compositeBlockBehavior;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior connectableBlockBehavior = (ConnectableBlockBehavior)behavior;
                    localDir = connectableBlockBehavior.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((compositeBlockBehavior = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(side, state);
                }
            }
            if (!this.getIOConfiguration().providesOutput(IOConfiguration.IOType.GAS, localDir)) {
                return 0;
            }
            GasStack aboutToLeaveGas = this.getStoredGasForCarrier();
            if (this.runOnTransferScript("gas",
                    stackPayload(aboutToLeaveGas.isEmpty() ? null : aboutToLeaveGas.getType().id().toString(), max),
                    side, "output")) {
                return 0;
            }
            boolean[] changed = new boolean[]{false};
            Consumer<GasStack> hookDrained = s -> {
                if (drained != null) {
                    drained.accept((GasStack)s);
                }
                changed[0] = true;
            };
            int targetSlot = slot;
            if (targetSlot == -1) {
                targetSlot = this.getIOConfiguration().getTargetSlot(IOConfiguration.IOType.GAS, localDir);
            }
            if (targetSlot != -1) {
                if (targetSlot >= 0 && targetSlot < this.gasTanks.size()) {
                    int extracted = this.gasTanks.get(targetSlot).extract(level, this.getMachinePos(), max, hookDrained);
                    if (changed[0]) {
                        this.setChanged();
                    }
                    return extracted;
                }
                return 0;
            }
            for (GasTank tank : this.gasTanks) {
                int extracted = tank.extract(level, this.getMachinePos(), max, hookDrained);
                if (extracted <= 0) continue;
                if (changed[0]) {
                    this.setChanged();
                }
                return extracted;
            }
        }
        return 0;
    }

    /** Installs this machine's energy buffer from its {@code MachineDefinition.EnergySpec}. Called once
     *  by the concrete subclass's constructor (mirrors how fluid/gas tanks are built from TankSpec). */
    public void configureEnergy(int capacity, int maxInput, int maxOutput, int perTick) {
        this.energyCapacity = Math.max(0, capacity);
        this.energyMaxInput = Math.max(0, maxInput);
        this.energyMaxOutput = Math.max(0, maxOutput);
        // Signed — see processTick's application. A machine that only ever consumes (a passive
        // drain, no generator) declares a negative per_tick; most machines declare 0
        // (or omit "energy" entirely) and never touch this branch at all.
        this.energyPerTick = perTick;
    }

    // ---- runtime overrides (Machine.set_energy_per_tick/etc.) — the JSON-declared EnergySpec is
    // just this machine's STARTING point; a script (e.g. a windmill scaling output with wind/height)
    // needs to be able to change it live without re-declaring the whole buffer. ----

    public int energyCapacity() { return energyCapacity; }
    public int energyMaxInput() { return energyMaxInput; }
    public int energyMaxOutput() { return energyMaxOutput; }
    /** Signed: positive feeds the buffer (generation), negative draws from it (consumption) — the
     * same sign convention as kinetic {@code report_su}'s network accounting, just mirrored (there
     * positive is the consumer; here positive is the producer, which matches how players already
     * read "per_tick" in every energy machine shipped this session). */
    public int energyPerTick() { return energyPerTick; }

    public void setEnergyMaxInput(int maxInput) { this.energyMaxInput = Math.max(0, maxInput); }
    public void setEnergyMaxOutput(int maxOutput) { this.energyMaxOutput = Math.max(0, maxOutput); }
    public void setEnergyPerTick(int perTick) {
        this.energyPerTick = perTick;
    }

    /**
     * Default "is this machine active" for {@code status: auto} when there's no recipe of its own
     * to drive {@link #isProcessing()} (a generator/passive-drain declares {@code energy.per_tick}
     * but {@code flags.recipes: false}, e.g. the energy windmill). A generator ({@code per_tick >
     * 0}) is active whenever it's configured to generate at all — production doesn't depend on the
     * buffer already holding charge. A drain ({@code per_tick < 0}) is only active while the
     * buffer actually has something to draw from — otherwise the tick's subtraction is a no-op
     * (clamped at 0) and showing it as "active" would be a visual lie.
     */
    protected boolean energyActiveDefault() {
        if (energyPerTick == 0 || energyCapacity <= 0) return false;
        return energyPerTick > 0 || energy > 0;
    }

    /**
     * Does this machine's declared IO allow energy to cross {@code worldSide}? Same convention and
     * purpose as {@link #allowsGas}: the energy network solver writes buffers directly, bypassing
     * {@link #insertEnergy}/{@link #extractEnergy}, so it must ask this first.
     */
    public boolean allowsEnergy(Level level, net.minecraft.core.Direction worldSide, boolean input) {
        if (this.ioConfiguration == null || this.energyCapacity <= 0) {
            return false;
        }
        net.minecraft.core.Direction localDir = worldSide;
        try {
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
            if (customState.isPresent()) {
                CompositeBlockBehavior composite;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState) customState.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior connectable) {
                    localDir = connectable.toLocalDirection(worldSide, state);
                }
                if (behavior instanceof CompositeBlockBehavior
                        && (cbb = (ConnectableBlockBehavior) ((composite = (CompositeBlockBehavior) behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    localDir = cbb.toLocalDirection(worldSide, state);
                }
            }
        } catch (Throwable ignored) {
            // Fall back to the world direction; a wrong rotation is better than a crash in the solver.
        }
        return input
                ? this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.ENERGY, localDir)
                : this.getIOConfiguration().providesOutput(IOConfiguration.IOType.ENERGY, localDir);
    }

    public int insertEnergy(Level level, int amount, net.minecraft.core.Direction side) {
        if (this.ioConfiguration == null || this.energyCapacity <= 0 || amount <= 0) {
            return 0;
        }
        net.minecraft.core.Direction localDir = side;
        BlockState state = level.getBlockState(this.getMachinePos());
        Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
        if (customState.isPresent()) {
            CompositeBlockBehavior compositeBlockBehavior;
            ConnectableBlockBehavior cbb;
            BlockBehavior behavior = ((ImmutableBlockState) customState.get()).behavior();
            if (behavior instanceof ConnectableBlockBehavior connectableBlockBehavior) {
                localDir = connectableBlockBehavior.toLocalDirection(side, state);
            }
            if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior) ((compositeBlockBehavior = (CompositeBlockBehavior) behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                localDir = cbb.toLocalDirection(side, state);
            }
        }
        if (!this.getIOConfiguration().acceptsInput(IOConfiguration.IOType.ENERGY, localDir)) {
            return 0;
        }
        if (this.runOnTransferScript("energy", dev.arubik.craftengine.script.ScriptValue.of(amount), side, "input")) {
            return 0;
        }
        int capped = this.energyMaxInput > 0 ? Math.min(amount, this.energyMaxInput) : amount;
        int space = this.energyCapacity - this.energy;
        int move = Math.max(0, Math.min(capped, space));
        if (move > 0) {
            this.energy += move;
            this.setChanged();
        }
        return move;
    }

    public int extractEnergy(Level level, int max, net.minecraft.core.Direction side) {
        if (this.ioConfiguration == null || this.energyCapacity <= 0 || max <= 0) {
            return 0;
        }
        net.minecraft.core.Direction localDir = side;
        BlockState state = level.getBlockState(this.getMachinePos());
        Optional customState = BlockStateUtils.getOptionalCustomBlockState(state);
        if (customState.isPresent()) {
            CompositeBlockBehavior compositeBlockBehavior;
            ConnectableBlockBehavior cbb;
            BlockBehavior behavior = ((ImmutableBlockState) customState.get()).behavior();
            if (behavior instanceof ConnectableBlockBehavior connectableBlockBehavior) {
                localDir = connectableBlockBehavior.toLocalDirection(side, state);
            }
            if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior) ((compositeBlockBehavior = (CompositeBlockBehavior) behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                localDir = cbb.toLocalDirection(side, state);
            }
        }
        if (!this.getIOConfiguration().providesOutput(IOConfiguration.IOType.ENERGY, localDir)) {
            return 0;
        }
        if (this.runOnTransferScript("energy", dev.arubik.craftengine.script.ScriptValue.of(max), side, "output")) {
            return 0;
        }
        int capped = this.energyMaxOutput > 0 ? Math.min(max, this.energyMaxOutput) : max;
        int move = Math.max(0, Math.min(capped, this.energy));
        if (move > 0) {
            this.energy -= move;
            this.setChanged();
        }
        return move;
    }

    public int getStoredEnergyForCarrier() {
        return this.energy;
    }

    public long getEnergyCapacityForCarrier() {
        return this.energyCapacity;
    }

    /** Engine apply path (bypasses the IO gating {@link #insertEnergy}/{@link #extractEnergy} enforce) —
     *  same role as {@link #setStoredGasRaw}. Clamped to capacity; the network never overfills a buffer. */
    public void setStoredEnergyRaw(Level level, int amount) {
        if (this.energyCapacity <= 0) {
            return;
        }
        this.energy = Math.max(0, Math.min(amount, this.energyCapacity));
        this.setChanged();
    }

    protected boolean requiresFuel() {
        return true;
    }

    /**
     * Applies this tick's {@code energy.per_tick} to the buffer. Gated on actively burning fuel for
     * a FUEL generator, NOT on recipe progress — a generator has no recipe/output of its own, just
     * fuel -> energy. A machine that declares {@code per_tick} but {@code requiresFuel()==false}
     * (e.g. an always-on windmill/solar panel — no fuel slot, nothing to burn) generates
     * unconditionally instead; {@code burnTime} is permanently 0 for those, so gating on it here
     * would silently generate nothing forever. Overflow beyond {@code energyCapacity} this tick is
     * simply discarded (no cross-tick carry, no queue): a generator that can make 10k/t but only has
     * room for 1k that tick banks 1k and the rest is lost, exactly like a real FE generator with a
     * too-small internal buffer and nowhere to push it. Signed, like kinetic SU/RPM reporting:
     * positive = feed the buffer (generation), negative = draw from it (a passive drain). Either
     * direction clamps at the buffer's own edge instead of carrying a deficit/overflow onward.
     *
     * <p>Called from BOTH {@link #processTick} (recipe-driven machines) AND {@code
     * DataMachineBlockEntity#tick}'s recipe-LESS branch — {@code flags.recipes: false} (the
     * windmill/solar panel's case) skips {@code processTick} entirely, so without this being called
     * from that other path too, a recipe-less generator would show as "active" (via {@link
     * #energyActiveDefault}) yet never actually accumulate anything.
     */
    public void applyEnergyPerTick() {
        boolean generating = this.requiresFuel() ? this.burnTime > 0 : true;
        if (this.energyPerTick != 0 && generating && this.energyCapacity > 0) {
            this.energy = Math.max(0, Math.min(this.energyCapacity, this.energy + this.energyPerTick));
        }
    }

    protected void processTick(Level level) {
        if (this.requiresFuel() && this.burnTime > 0) {
            --this.burnTime;
        }
        if (level.isClientSide()) {
            return;
        }
        this.applyEnergyPerTick();
        this.recomputeUpgrades();
        if (this.requiresRedstone && !this.isRedstoneEnabled(level)) {
            if (this.progress > 0) {
                this.progress = Math.max(0, this.progress - 1);
            }
            this.isProcessing = false;
            return;
        }
        AbstractProcessingRecipe recipe = this.getMatchingRecipe(level);
        if (this.canProcess(level, recipe)) {
            boolean needsFuel;
            boolean bl = needsFuel = this.requiresFuel() && recipe.isFuelRequired();
            if (needsFuel && this.burnTime <= 0) {
                if (this.hasFuel(level)) {
                    this.consumeFuel(level);
                    this.setChanged();
                } else {
                    if (this.progress > 0) {
                        this.progress = Math.max(0, this.progress - 2);
                    }
                    this.isProcessing = false;
                    return;
                }
            }
            // Energy-as-fuel: a recipe with energyCost > 0 needs that much drawn from the machine's
            // OWN buffer every tick it advances — same shape as the burnTime gate above, just a
            // continuous per-tick draw instead of a burn-time reservoir. No buffer (or an empty
            // one) simply stalls progress exactly like an unfed furnace, rather than crashing or
            // silently processing for free.
            int energyCost = recipe.getEnergyCost();
            if (energyCost > 0 && this.energy < energyCost) {
                if (this.progress > 0) {
                    this.progress = Math.max(0, this.progress - 2);
                }
                this.isProcessing = false;
                return;
            }
            if (!needsFuel || this.burnTime > 0) {
                if (energyCost > 0) {
                    this.energy -= energyCost;
                }
                this.isProcessing = true;
                if (this.maxProgress == 0) {
                    this.maxProgress = recipe.getProcessTime();
                }
                this.progressCarry += Math.max(0.0, this.upgradeModifiers.speedMultiplier());
                int adv = (int)this.progressCarry;
                if (adv > 0) {
                    this.progress += adv;
                    this.progressCarry -= (double)adv;
                }
                if (this.progress >= this.maxProgress) {
                    this.process(level, recipe);
                    this.progress = 0;
                    this.progressCarry = 0.0;
                    this.isProcessing = false;
                }
                this.setChanged();
            }
        } else {
            this.isProcessing = false;
            this.progress = 0;
            this.progressCarry = 0.0;
            this.setChanged();
        }
    }

    public Map<String, String> barPlaceholders(String id) {
        return Collections.emptyMap();
    }

    public int effectiveRpm(AbstractProcessingRecipe r) {
        return r == null ? 0 : r.getMinRpm();
    }

    public int effectiveSu(AbstractProcessingRecipe r) {
        return r == null ? 0 : r.getSuCost();
    }

    protected abstract AbstractProcessingRecipe getMatchingRecipe(Level var1);

    public AbstractProcessingRecipe getCurrentRecipe() {
        try {
            Level l = this.getNMSLevel();
            return l == null ? null : this.getMatchingRecipe(l);
        }
        catch (Throwable t) {
            return null;
        }
    }

    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (recipe == null) {
            return false;
        }
        if (recipe.isRequireOverclocked() && !this.isOverclocked()) {
            return false;
        }
        for (RecipeCondition condition : recipe.getConditions()) {
            if (condition.test(level, this)) continue;
            return false;
        }
        for (RecipeOutput output : recipe.getOutputs()) {
            if (this.canFitOutput(level, output)) continue;
            return false;
        }
        return true;
    }

    protected abstract boolean canFitOutput(Level var1, RecipeOutput var2);

    protected void process(Level level, AbstractProcessingRecipe recipe) {
        this.consumeInputs(level, recipe);
        for (RecipeOutput output : recipe.getOutputs()) {
            output.dispense(level, this);
        }
    }

    protected abstract void consumeInputs(Level var1, AbstractProcessingRecipe var2);

    protected boolean isRedstoneEnabled(Level level) {
        if (!this.requiresRedstone) {
            return true;
        }
        boolean hasSignal = level.hasNeighborSignal(this.getMachinePos());
        return this.invertRedstone ? !hasSignal : hasSignal;
    }

    public boolean isOverclocked() {
        return this.overclockedTicks > 0;
    }

    public void setOverclockedTicks(int ticks) {
        this.overclockedTicks = Math.max(0, ticks);
        this.setChanged();
    }

    public void incrementOverclocked(int ticks) {
        this.overclockedTicks += ticks;
        this.setChanged();
    }

    public int getOverclockedTicks() {
        return this.overclockedTicks;
    }

    public boolean addOutput(net.minecraft.world.item.ItemStack stack) {
        int[] slots;
        for (int i : slots = this.getOutputSlots()) {
            int space;
            int toAdd;
            net.minecraft.world.item.ItemStack current = this.getItem(i);
            if (current.isEmpty()) {
                this.setItem(i, stack);
                return true;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)current, (net.minecraft.world.item.ItemStack)stack) || (toAdd = Math.min(space = this.getMaxStackSize() - current.getCount(), stack.getCount())) <= 0) continue;
            current.grow(toAdd);
            stack.shrink(toAdd);
            if (!stack.isEmpty()) continue;
            return true;
        }
        return false;
    }

    public void addXp(float amount) {
        this.accumulateXp(amount);
    }

    public double[] barStat(String id) {
        if ("progress".equals(id)) {
            return new double[]{this.progress, this.maxProgress};
        }
        return new double[]{0.0, 0.0};
    }

    public String barSubtype(String id) {
        return "";
    }

    public void dropAllContents(Level level, BlockPos pos) {
        try {
            CraftWorld bw = level.getWorld();
            Location loc = new Location((World)bw, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
            for (int i = 0; i < this.inventory.length; ++i) {
                net.minecraft.world.item.ItemStack st = this.inventory[i];
                if (st == null || st.isEmpty()) continue;
                bw.dropItemNaturally(loc, BridgeUtils.toBukkit(st));
            }
            for (FunnelTransit t : this.funnelTransits.values()) {
                if (t.item == null || t.item.getType().isAir()) continue;
                bw.dropItemNaturally(loc, t.item.clone());
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.despawnAllFunnelTransits();
        this.clearContent();
    }

    public int[] getOutputSlots() {
        if (this.ioConfiguration != null) {
            return this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.OUTPUT);
        }
        return new int[0];
    }

    public int[] getInputSlots() {
        if (this.ioConfiguration != null) {
            return this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.INPUT);
        }
        return new int[0];
    }

    public GasStack getStoredGasForCarrier() {
        for (GasTank tank : this.gasTanks) {
            GasStack gs = tank.getGas(this.getNMSLevel(), this.getMachinePos());
            if (gs == null || gs.isEmpty()) continue;
            return gs;
        }
        return GasStack.EMPTY;
    }

    public long getGasCapacityForCarrier() {
        return this.gasTanks.isEmpty() ? 0L : (long)this.gasTanks.get(0).getCapacity();
    }

    public void setStoredGasRaw(Level level, GasStack stack) {
        if (this.gasTanks.isEmpty()) {
            return;
        }
        TypedKey<GasStack> key = this.gasTanks.get(0).getKey();
        PersistentBlockEntity.executeAt(level, this.getMachinePos(), be -> {
            if (stack == null || stack.isEmpty()) {
                be.remove(key);
            } else {
                be.set(key, stack);
            }
        });
    }

    public boolean isFuelItem(ItemStack bukkit) {
        if (bukkit == null || bukkit.getType().isAir()) {
            return false;
        }
        return RecipeManager.getFuel(this.getMachineId(), CraftItemStack.asNMSCopy((ItemStack)bukkit)) != null;
    }

    public int[] getFuelSlots() {
        if (this.ioConfiguration != null) {
            return this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL);
        }
        return new int[0];
    }

    /** Free/unrestricted {@link dev.arubik.craftengine.machine.menu.layout.MenuSlotType#STORAGE}
     *  slots — empty for machines with no such slots (i.e. most of them). See the paged override
     *  in {@link DataMachineBlockEntity#getStorageSlots()}. Used by {@link dev.arubik.craftengine.contraption.ContraptionContainerView}
     *  to build a combined pushable container across a whole contraption. */
    public int[] getStorageSlots() {
        return new int[0];
    }

    public int[] getUpgradeSlots() {
        return new int[0];
    }

    protected UpgradeRegistry upgradeRegistry() {
        return UpgradeRegistry.global();
    }

    public UpgradeModifiers getUpgradeModifiers() {
        return this.upgradeModifiers;
    }

    protected void recomputeUpgrades() {
        int[] slots = this.getUpgradeSlots();
        if (slots.length == 0) {
            this.upgradeModifiers = UpgradeModifiers.NONE;
            return;
        }
        HashMap<Key, Integer> counts = new HashMap<Key, Integer>();
        for (int slot : slots) {
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            Key id = this.upgradeItemId(stack);
            if (id == null) continue;
            counts.merge(id, Math.max(1, stack.getCount()), Integer::sum);
        }
        this.upgradeModifiers = UpgradeModifiers.compute(counts, this.upgradeRegistry());
    }

    protected Key upgradeItemId(net.minecraft.world.item.ItemStack nms) {
        if (nms == null || nms.isEmpty()) {
            return null;
        }
        ItemStack bukkit = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)nms);
        Key custom = CraftEngineItems.getCustomItemId((ItemStack)bukkit);
        if (custom != null) {
            return custom;
        }
        NamespacedKey nk = bukkit.getType().getKey();
        return Key.of((String)nk.getNamespace(), (String)nk.getKey());
    }

    @Override
    public void saveCustomData(CompoundTag tag) {
        super.saveCustomData(tag);
        tag.putInt("progress", this.progress);
        tag.putInt("max_progress", this.maxProgress);
        tag.putInt("burn_time", this.burnTime);
        tag.putInt("max_burn_time", this.maxBurnTime);
        tag.putInt("overclocked_ticks", this.overclockedTicks);
        tag.putFloat("stored_xp", this.storedXp);
        tag.putInt("energy", this.energy);
        if (this.ownerUuid != null) {
            this.set(KEY_OWNER_UUID, this.ownerUuid.toString());
        }
    }

    @Override
    public void loadCustomData(CompoundTag tag) {
        super.loadCustomData(tag);
        this.progress = tag.getInt("progress");
        this.maxProgress = tag.getInt("max_progress");
        this.burnTime = tag.getInt("burn_time");
        this.maxBurnTime = tag.getInt("max_burn_time");
        this.overclockedTicks = tag.getInt("overclocked_ticks");
        this.storedXp = tag.getFloat("stored_xp");
        this.energy = tag.getInt("energy");
        String ownerStr = this.getOrDefault(KEY_OWNER_UUID, null);
        if (ownerStr != null) {
            try {
                this.ownerUuid = UUID.fromString(ownerStr);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    @Override
    public void unregister() {
        super.unregister();
        if (this.menu != null) {
            this.menu = null;
        }
    }

    public abstract MachineLayout getLayout();

    public MachineMenu getMenu() {
        if (this.menu == null) {
            this.menu = new MachineMenu(this, this.getLayout());
            this.menu.syncFromMachine();
        }
        return this.menu;
    }

    /** {@link #getMenu()} without the lazy-create — null when nobody has ever opened this
     *  machine's menu (or a subclass overriding {@code openMenu} hasn't populated {@link #menu}
     *  yet), for a caller that only wants to act on an ALREADY-open menu (see {@code Machine.update()}). */
    public MachineMenu getOpenMenuOrNull() {
        return this.menu;
    }

    /** Re-runs {@link #getLayout()} (so a page's {@code "buttons"}/{@code "layout"} generator script
     *  re-evaluates and can add/remove/move slots — see {@code MachineMenu#rebuildLayout}, which
     *  {@code MachineMenu#refreshNow} can't do since a generator only runs inside
     *  {@code buildPageLayout()} at menu-construction time) and repaints the currently-open menu's
     *  SAME inventory in place — no close/reopen, so nothing flickers for the viewer. {@link
     *  #getLayout()} reads a subclass's own current-page state (e.g. {@code
     *  DataMachineBlockEntity#page}), so this stays on whatever page is open. No-op if nobody has
     *  this machine's menu open right now. Used by {@code Machine.update()}. */
    public boolean rebuildAndReopenMenu() {
        MachineMenu open = this.menu;
        if (open == null || open.getInventory().getViewers().isEmpty()) {
            return false;
        }
        open.rebuildLayout(this.getLayout());
        return true;
    }

    public boolean isValidInput(int slot, net.minecraft.world.item.ItemStack stack) {
        return this.getLayout().getSlotType(slot) == MenuSlotType.INPUT;
    }

    public boolean isValidFuel(net.minecraft.world.item.ItemStack stack) {
        return this.getBurnDuration(stack) > 0;
    }

    protected int getBurnDuration(net.minecraft.world.item.ItemStack stack) {
        MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    protected abstract String getMachineId();

    public boolean isValidFuel(FluidStack stack) {
        return RecipeManager.getFuel(this.getMachineId(), stack) != null;
    }

    protected int getFluidBurnTime(FluidStack stack) {
        MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    public boolean isValidFuel(GasStack stack) {
        return RecipeManager.getFuel(this.getMachineId(), stack) != null;
    }

    protected int getGasBurnTime(GasStack stack) {
        MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
        return recipe != null ? recipe.getBurnTime() : 0;
    }

    protected boolean canFitReplacement(Level level, MachineFuelRecipe recipe, int fuelSlot) {
        if (recipe.getReplacement() == null) {
            return true;
        }
        Object output = recipe.getReplacement().getOutput();
        if (output instanceof net.minecraft.world.item.ItemStack) {
            int[] outputSlots;
            net.minecraft.world.item.ItemStack itemOutput = (net.minecraft.world.item.ItemStack)output;
            net.minecraft.world.item.ItemStack currentStack = this.getItem(fuelSlot);
            if (currentStack.getCount() == recipe.getInput().getAmount()) {
                return true;
            }
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.OUTPUT)) {
                net.minecraft.world.item.ItemStack slotStack = this.getItem(slot);
                if (slotStack.isEmpty()) {
                    return true;
                }
                if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)slotStack, (net.minecraft.world.item.ItemStack)itemOutput) || slotStack.getCount() + itemOutput.getCount() > slotStack.getMaxStackSize()) continue;
                return true;
            }
            return false;
        }
        if (output instanceof FluidStack) {
            int[] outputSlots;
            FluidStack fluidOutput = (FluidStack)output;
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.OUTPUT)) {
                FluidTank tank;
                if (slot < 0 || slot >= this.fluidTanks.size() || (tank = this.fluidTanks.get(slot)).getCapacity() < fluidOutput.getAmount() || !tank.allows(fluidOutput)) continue;
                return true;
            }
            return false;
        }
        if (output instanceof GasStack) {
            int[] outputSlots;
            GasStack gasOutput = (GasStack)output;
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.OUTPUT)) {
                GasTank tank;
                if (slot < 0 || slot >= this.gasTanks.size() || (tank = this.gasTanks.get(slot)).getCapacity() < gasOutput.getAmount() || !tank.allows(gasOutput)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    protected boolean placeReplacement(Level level, MachineFuelRecipe recipe, int fuelSlot) {
        if (recipe.getReplacement() == null) {
            return false;
        }
        Object output = recipe.getReplacement().getOutput();
        if (output instanceof net.minecraft.world.item.ItemStack) {
            int[] outputSlots;
            net.minecraft.world.item.ItemStack itemOutput = (net.minecraft.world.item.ItemStack)output;
            net.minecraft.world.item.ItemStack currentStack = this.getItem(fuelSlot);
            if (currentStack.getCount() == recipe.getInput().getAmount()) {
                this.setItem(fuelSlot, itemOutput.copy());
                return true;
            }
            for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.OUTPUT)) {
                net.minecraft.world.item.ItemStack slotStack = this.getItem(slot);
                if (slotStack.isEmpty()) {
                    this.setItem(slot, itemOutput.copy());
                    return false;
                }
                if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)currentStack, (net.minecraft.world.item.ItemStack)itemOutput) || slotStack.getCount() + itemOutput.getCount() > slotStack.getMaxStackSize()) continue;
                slotStack.grow(itemOutput.getCount());
                return false;
            }
        } else {
            if (output instanceof FluidStack) {
                int[] outputSlots;
                FluidStack fluidOutput = (FluidStack)output;
                for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.OUTPUT)) {
                    FluidTank tank;
                    if (slot < 0 || slot >= this.fluidTanks.size() || (tank = this.fluidTanks.get(slot)).insert(level, this.getMachinePos(), fluidOutput.copy()) <= 0) continue;
                    return false;
                }
                return true;
            }
            if (output instanceof GasStack) {
                int[] outputSlots;
                GasStack gasOutput = (GasStack)output;
                for (int slot : outputSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.OUTPUT)) {
                    GasTank tank;
                    if (slot < 0 || slot >= this.gasTanks.size() || (tank = this.gasTanks.get(slot)).insert(level, this.getMachinePos(), gasOutput.copy()) <= 0) continue;
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    protected boolean hasFuel(Level level) {
        int[] gasFuelSlots;
        int[] fluidFuelSlots;
        int[] itemFuelSlots;
        if (this.ioConfiguration == null) {
            return false;
        }
        for (int slot : itemFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL)) {
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
            if (recipe == null || stack.getCount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        for (int slot : fluidFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.fluidTanks.size()) continue;
            FluidTank tank = this.fluidTanks.get(slot);
            FluidStack fluid = tank.getFluid(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), fluid);
            if (recipe == null || fluid.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        for (int slot : gasFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.gasTanks.size()) continue;
            GasTank tank = this.gasTanks.get(slot);
            GasStack gas = tank.getGas(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), gas);
            if (recipe == null || gas.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            return true;
        }
        return false;
    }

    protected void consumeFuel(Level level) {
        int[] gasFuelSlots;
        int[] fluidFuelSlots;
        int[] itemFuelSlots;
        if (this.ioConfiguration == null) {
            return;
        }
        for (int slot : itemFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.ITEM, IOConfiguration.IORole.FUEL)) {
            net.minecraft.world.item.ItemStack stack = this.getItem(slot);
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), stack);
            if (recipe == null || stack.getCount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = (int)Math.round((double)recipe.getBurnTime() / this.upgradeModifiers.fuelMultiplier());
            boolean consumedInPlace = this.placeReplacement(level, recipe, slot);
            if (!consumedInPlace) {
                this.removeItem(slot, recipe.getInput().getAmount());
            }
            this.burnTime += burn;
            this.maxBurnTime = burn;
            if (recipe.getOverclockedTime() > 0) {
                this.incrementOverclocked(recipe.getOverclockedTime());
            }
            return;
        }
        for (int slot : fluidFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.FLUID, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.fluidTanks.size()) continue;
            FluidTank tank = this.fluidTanks.get(slot);
            FluidStack fluid = tank.getFluid(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), fluid);
            if (recipe == null || fluid.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = recipe.getBurnTime();
            int toConsume = recipe.getInput().getAmount();
            this.placeReplacement(level, recipe, slot);
            tank.extract(level, this.getMachinePos(), toConsume, s -> {});
            this.burnTime += burn;
            this.maxBurnTime = burn;
            return;
        }
        for (int slot : gasFuelSlots = this.ioConfiguration.getSlots(IOConfiguration.IOType.GAS, IOConfiguration.IORole.FUEL)) {
            if (slot < 0 || slot >= this.gasTanks.size()) continue;
            GasTank tank = this.gasTanks.get(slot);
            GasStack gas = tank.getGas(level, this.getMachinePos());
            MachineFuelRecipe recipe = RecipeManager.getFuel(this.getMachineId(), gas);
            if (recipe == null || gas.getAmount() < recipe.getInput().getAmount() || !this.canFitReplacement(level, recipe, slot)) continue;
            int burn = recipe.getBurnTime();
            int toConsume = recipe.getInput().getAmount();
            this.placeReplacement(level, recipe, slot);
            tank.extract(level, this.getMachinePos(), toConsume, s -> {});
            this.burnTime += burn;
            this.maxBurnTime = burn;
            return;
        }
    }

    protected void dbgIO(String msg) {
        if (DEBUG_IO) {
            System.out.println("[MachineIO] " + this.getMachineId() + " @" + this.getMachinePos().toShortString() + " " + msg);
        }
    }

    protected net.minecraft.core.Direction toLocalItemDir(net.minecraft.core.Direction side) {
        try {
            Level level = this.getNMSLevel();
            if (level == null) {
                return side;
            }
            BlockState state = level.getBlockState(this.getMachinePos());
            Optional cs = BlockStateUtils.getOptionalCustomBlockState(state);
            if (cs.isPresent()) {
                CompositeBlockBehavior comp;
                ConnectableBlockBehavior cbb;
                BlockBehavior behavior = ((ImmutableBlockState)cs.get()).behavior();
                if (behavior instanceof ConnectableBlockBehavior) {
                    ConnectableBlockBehavior cbb2 = (ConnectableBlockBehavior)behavior;
                    return cbb2.toLocalDirection(side, state);
                }
                if (behavior instanceof CompositeBlockBehavior && (cbb = (ConnectableBlockBehavior)((comp = (CompositeBlockBehavior)behavior).getFirst(ConnectableBlockBehavior.class))) != null) {
                    return cbb.toLocalDirection(side, state);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return side;
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction worldSide) {
        if (this.ioConfiguration == null) {
            int[] all = new int[this.getContainerSize()];
            for (int i2 = 0; i2 < all.length; ++i2) {
                all[i2] = i2;
            }
            return all;
        }
        net.minecraft.core.Direction side = this.toLocalItemDir(worldSide);
        boolean acceptsInput = this.ioConfiguration.acceptsInput(IOConfiguration.IOType.ITEM, side);
        boolean providesOutput = this.ioConfiguration.providesOutput(IOConfiguration.IOType.ITEM, side);
        int dbgTarget = this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.ITEM, side);
        this.dbgIO("getSlotsForFace world=" + String.valueOf(worldSide) + " -> local=" + String.valueOf(side) + " in=" + acceptsInput + " out=" + providesOutput + " target=" + dbgTarget);
        if (!acceptsInput && !providesOutput) {
            return new int[0];
        }
        ArrayList<Integer> slots = new ArrayList<Integer>();
        int[] inputs = this.getInputSlots();
        int[] outputs = this.getOutputSlots();
        int[] fuels = this.getFuelSlots();
        if (acceptsInput) {
            int target = this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.ITEM, side);
            if (target >= 0) {
                slots.add(target);
            } else {
                for (int i3 : inputs) {
                    slots.add(i3);
                }
                int[] nArray = fuels;
                int n = nArray.length;
                for (int j = 0; j < n; ++j) {
                    int i3;
                    i3 = nArray[j];
                    slots.add(i3);
                }
            }
        }
        if (providesOutput) {
            for (int i4 : outputs) {
                slots.add(i4);
            }
        }
        return slots.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction side) {
        if (this.ioConfiguration == null) {
            return true;
        }
        net.minecraft.core.Direction local = this.toLocalItemDir(side);
        boolean accepts = this.ioConfiguration.acceptsInput(IOConfiguration.IOType.ITEM, local);
        int target = this.ioConfiguration.getTargetSlot(IOConfiguration.IOType.ITEM, local);
        this.dbgIO("canPlace slot=" + slot + " world=" + String.valueOf(side) + " -> local=" + String.valueOf(local) + " accepts=" + accepts + " target=" + target);
        if (!accepts) {
            return false;
        }
        boolean allowed;
        if (target >= 0) {
            allowed = slot == target;
        } else {
            allowed = false;
            for (int in : this.getInputSlots()) {
                if (in == slot) { allowed = true; break; }
            }
            if (!allowed) {
                for (int f : this.getFuelSlots()) {
                    if (f == slot) { allowed = true; break; }
                }
            }
        }
        if (!allowed) {
            return false;
        }
        return !this.runOnTransferScript("item", dev.arubik.craftengine.script.ScriptValue.ofItem(stack), side, "input");
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction side) {
        if (this.ioConfiguration == null) {
            return true;
        }
        net.minecraft.core.Direction local = this.toLocalItemDir(side);
        if (!this.ioConfiguration.providesOutput(IOConfiguration.IOType.ITEM, local)) {
            return false;
        }
        boolean allowed = false;
        for (int out : this.getOutputSlots()) {
            if (out == slot) { allowed = true; break; }
        }
        if (!allowed) {
            return false;
        }
        return !this.runOnTransferScript("item", dev.arubik.craftengine.script.ScriptValue.ofItem(stack), side, "output");
    }


    /**
     * Runs {@code MachineDefinition#onTransferScript()} (the generic {@code on_pipe_transfer} hook —
     * fires for ANY machine and ANY resource, not just item pipes) if this machine declares one, and
     * reports whether the script vetoed the transfer. Called AFTER the existing IOConfiguration gate
     * for the relevant resource already said "allowed" (from {@code canPlaceItemThroughFace}/{@code
     * canTakeItemThroughFace} for items, and from the slotted {@code insertFluid}/{@code extractFluid}/
     * {@code insertGas}/{@code extractGas}/{@code insertEnergy}/{@code extractEnergy} for the other
     * three) — this is an additional observation/veto layer on top, never a replacement for those
     * gates, so a machine with no hook declared behaves exactly as before this feature existed.
     *
     * @param type    "item" / "fluid" / "gas" / "energy" — bound as {@code type} in the script
     * @param payload the resource-appropriate value bound as {@code payload}: an {@code Item} for
     *                "item", a {@code Map{type, amount}} for "fluid"/"gas" (no dedicated FluidStack/
     *                GasStack script type exists), or a plain number for "energy"
     */
    private boolean runOnTransferScript(String type, dev.arubik.craftengine.script.ScriptValue payload,
            net.minecraft.core.Direction side, String mode) {
        if (!(this instanceof dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity dm))
            return false;
        dev.arubik.craftengine.machine.MachineDefinition definition = dm.definition();
        if (definition == null || definition.onTransferScript() == null)
            return false;
        try {
            ScriptContext base = this.buildScriptContext();
            if (base == null)
                return false;
            dev.arubik.craftengine.script.event.TransferEvent transferEvent =
                    new dev.arubik.craftengine.script.event.TransferEvent(type, payload, side.getName(), mode);
            ScriptContext ctx = ScriptContext.builder().copyFrom(base)
                    .typed("type", dev.arubik.craftengine.script.ScriptValue.of(type))
                    .typed("payload", payload)
                    .typed("direction", dev.arubik.craftengine.script.ScriptValue.of(side.getName()))
                    .typed("mode", dev.arubik.craftengine.script.ScriptValue.of(mode))
                    .event(transferEvent)
                    .build();
            dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall
                    .parse(definition.onTransferScript());
            if (call == null)
                return false;
            call.execute(ctx);
            return transferEvent.isCancelled();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** {@code {type, amount}} payload for a fluid/gas transfer — no dedicated FluidStack/GasStack
     * script type exists, and a plain map is enough for a script to react to ({@code payload.type},
     * {@code payload.amount}) without needing one. */
    private static dev.arubik.craftengine.script.ScriptValue stackPayload(String typeId, int amount) {
        java.util.LinkedHashMap<String, dev.arubik.craftengine.script.ScriptValue> map = new java.util.LinkedHashMap<>();
        map.put("type", dev.arubik.craftengine.script.ScriptValue.of(typeId == null ? "" : typeId));
        map.put("amount", dev.arubik.craftengine.script.ScriptValue.of(amount));
        return dev.arubik.craftengine.script.types.primitive.MapType.wrap(map);
    }

    public void openMenu(Player player) {
        this.getMenu().open((org.bukkit.entity.Player)player.getBukkitEntity());
    }

    public boolean canTakeFromSlot(int slot) {
        return true;
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        if (this.menu != null) {
            this.menu.tick();
        }
        this.processTick(level);
        if (!level.isClientSide()) {
            this.pushFunnelOutputs(level);
            this.pullFromInputFaces(level);
        }
        this.updateMachineModeProperty(level, pos, state);
    }

    protected void pullFromInputFaces(Level level) {
        // A machine whose panel the player never touched (no "io" json block either, e.g.
        // energy_cell) has this.ioConfiguration == null forever — but every OTHER reader
        // (Machine.io_get, carrierConnectsHere, canEnergyOutput/Input) already falls back to the
        // block's static defaultIOConfig (normally Open) instead of treating "no owned config" as
        // "closed". Bailing out here instead of doing the same fallback meant this pull simply
        // never ran until the player happened to open a side-config GUI and click a face at least
        // once — see resolveBehaviorDefaultIOConfig / mutableIO's identical reasoning.
        IOConfiguration cfg = this.ioConfiguration != null ? this.ioConfiguration : this.resolveBehaviorDefaultIOConfig();
        if (cfg == null) {
            return;
        }
        BlockPos pos = this.getMachinePos();
        for (net.minecraft.core.Direction world : dev.arubik.craftengine.util.Utils.DIRECTIONS) {
            net.minecraft.core.Direction local = this.toLocalItemDir(world);
            BlockPos src = pos.relative(world);
            net.minecraft.core.Direction sideFromSrc = world.getOpposite();
            if (cfg.acceptsInput(IOConfiguration.IOType.FLUID, local)) {
                this.pullFluidInto(level, src, sideFromSrc, cfg.getTargetSlot(IOConfiguration.IOType.FLUID, local));
            }
            if (cfg.acceptsInput(IOConfiguration.IOType.GAS, local)) {
                this.pullGasInto(level, src, sideFromSrc, cfg.getTargetSlot(IOConfiguration.IOType.GAS, local));
            }
            if (cfg.acceptsInput(IOConfiguration.IOType.ENERGY, local)) {
                this.pullEnergyInto(level, src, sideFromSrc, world);
            }
        }
    }

    /**
     * Direct machine-to-neighbour energy pull, mirroring {@link #pullFluidInto}/{@link
     * #pullGasInto} exactly — needed because, unlike fluid/gas, ENERGY has no equivalent here at
     * all otherwise. A cable's {@link dev.arubik.craftengine.energy.behavior.EnergyCableBehavior}
     * registers itself into {@link dev.arubik.craftengine.fluid.graph.EnergyEngine}'s seed set every
     * tick, but a plain machine never does — so two machines placed directly against each other
     * with NO cable anywhere nearby (e.g. a solar panel stacked straight on an energy cell) never
     * had ANY code path move energy between them: no cable meant no seed, meant EnergyEngine.step
     * never even ran for that pair, no matter how the per-face IO was configured. This closes that
     * gap the same way hopper-style direct adjacency already works for fluid/gas.
     */
    protected void pullEnergyInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc,
            net.minecraft.core.Direction worldSideOnThis) {
        if (this.energyCapacity <= 0) {
            return;
        }
        try {
            int space = this.energyCapacity - this.energy;
            if (space <= 0) {
                return;
            }
            dev.arubik.craftengine.energy.EnergyCarrier ec = AbstractMachineBlockEntity.energyCarrierAt(level, src);
            if (ec == null) {
                return;
            }
            int cap = this.energyMaxInput > 0 ? Math.min(space, this.energyMaxInput) : space;
            // extractEnergy's "side" is the face energy leaves THROUGH on the SOURCE (src), i.e.
            // pointing away from src back toward us — sideFromSrc. insertEnergy's "side" is the
            // face energy enters THROUGH on US (this machine) — the direction toward src, i.e.
            // worldSideOnThis — mirrors EnergyTransferHelper#transfer's own from/to convention.
            int extracted = ec.extractEnergy(level, src, cap, sideFromSrc);
            if (extracted > 0) {
                int accepted = this.insertEnergy(level, extracted, worldSideOnThis);
                int unused = extracted - accepted;
                if (unused > 0) {
                    // Give back whatever this side's own IO/cap rules didn't actually take.
                    ec.insertEnergy(level, src, unused, sideFromSrc);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    protected static dev.arubik.craftengine.energy.EnergyCarrier energyCarrierAt(Level level, BlockPos pos) {
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null) {
            return null;
        }
        BlockBehavior b = cs.behavior();
        if (b instanceof dev.arubik.craftengine.energy.EnergyCarrier ec) {
            return ec;
        }
        return b == null ? null : b.getFirst(dev.arubik.craftengine.energy.EnergyCarrier.class);
    }

    protected void pullFluidInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc, int tankIdx) {
        if (this.fluidTanks.isEmpty()) {
            return;
        }
        int idx = tankIdx >= 0 && tankIdx < this.fluidTanks.size() ? tankIdx : 0;
        FluidTank tank = this.fluidTanks.get(idx);
        try {
            int space = tank.getCapacity() - tank.getFluid(level, this.getMachinePos()).getAmount();
            if (space <= 0) {
                return;
            }
            FluidCarrier fc = AbstractMachineBlockEntity.fluidCarrierAt(level, src);
            if (DEBUG_IO) {
                System.out.println("[Pull] " + this.getMachineId() + " FLUID src=" + src.toShortString() + " carrier=" + (fc == null ? "null" : fc.getClass().getSimpleName()) + " space=" + space);
            }
            if (fc == null) {
                return;
            }
            int[] got = new int[]{0};
            fc.extractFluid(level, src, Math.min(space, 1000), (FluidStack f) -> {
                if (f != null && !f.isEmpty()) {
                    got[0] = got[0] + f.getAmount();
                    tank.insert(level, this.getMachinePos(), (FluidStack)f);
                }
            }, sideFromSrc);
            if (DEBUG_IO && got[0] > 0) {
                System.out.println("[Pull]   pulled " + got[0] + " mB into tank");
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected void pullGasInto(Level level, BlockPos src, net.minecraft.core.Direction sideFromSrc, int tankIdx) {
        if (this.gasTanks.isEmpty()) {
            return;
        }
        int idx = tankIdx >= 0 && tankIdx < this.gasTanks.size() ? tankIdx : 0;
        GasTank tank = this.gasTanks.get(idx);
        try {
            int space = tank.getCapacity() - tank.getGas(level, this.getMachinePos()).getAmount();
            if (space <= 0) {
                return;
            }
            GasCarrier gc = AbstractMachineBlockEntity.gasCarrierAt(level, src);
            if (gc == null) {
                return;
            }
            gc.extractGas(level, src, Math.min(space, 1000), (GasStack g) -> {
                if (g != null && !g.isEmpty()) {
                    tank.insert(level, this.getMachinePos(), (GasStack)g);
                }
            }, sideFromSrc);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    protected static FluidCarrier fluidCarrierAt(Level level, BlockPos pos) {
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null) {
            return null;
        }
        BlockBehavior b = cs.behavior();
        if (b instanceof FluidCarrier) {
            FluidCarrier fc = (FluidCarrier)b;
            return fc;
        }
        return b == null ? null : (FluidCarrier)b.getFirst(FluidCarrier.class);
    }

    protected static GasCarrier gasCarrierAt(Level level, BlockPos pos) {
        ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
        if (cs == null) {
            return null;
        }
        BlockBehavior b = cs.behavior();
        if (b instanceof GasCarrier) {
            GasCarrier gc = (GasCarrier)b;
            return gc;
        }
        return b == null ? null : (GasCarrier)b.getFirst(GasCarrier.class);
    }

    private static net.minecraft.core.Direction nmsDir(Direction d) {
        return net.minecraft.core.Direction.valueOf((String)d.name());
    }

    private static Direction coreDir(net.minecraft.core.Direction d) {
        return Direction.valueOf((String)d.name());
    }

    private boolean hasFunnelInput() {
        if (this.ioConfiguration == null) {
            return false;
        }
        for (net.minecraft.core.Direction d : dev.arubik.craftengine.util.Utils.DIRECTIONS) {
            if (!this.ioConfiguration.acceptsInput(IOConfiguration.IOType.FUNNEL, d)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isFull() {
        if (!this.hasFunnelInput()) {
            return false;
        }
        for (int s : this.getInputSlots()) {
            net.minecraft.world.item.ItemStack cur = this.getItem(s);
            if (cur != null && !cur.isEmpty() && cur.getCount() >= cur.getMaxStackSize()) continue;
            return false;
        }
        return true;
    }

    private net.minecraft.core.Direction inputFaceFor(Direction sourceFacing) {
        net.minecraft.core.Direction worldFace = AbstractMachineBlockEntity.nmsDir(sourceFacing.opposite());
        if (this.ioConfiguration == null) {
            return null;
        }
        net.minecraft.core.Direction local = this.toLocalItemDir(worldFace);
        if (!this.ioConfiguration.acceptsInput(IOConfiguration.IOType.FUNNEL, local)) {
            return null;
        }
        return worldFace;
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing) {
        return this.startInputTransit(stack, sourceFacing, 0.0f, null, false);
    }

    @Override
    public boolean receiveConveyorItem(ItemStack stack, Direction sourceFacing, float jitter) {
        return this.startInputTransit(stack, sourceFacing, jitter, null, false);
    }

    @Override
    public boolean adoptConveyorItem(ItemStack stack, float jitter, ConveyorItemDisplay display, boolean spawned, Direction sourceFacing) {
        return this.startInputTransit(stack, sourceFacing, jitter, display, spawned);
    }

    private boolean startInputTransit(ItemStack stack, Direction sourceFacing, float jitter, ConveyorItemDisplay display, boolean spawned) {
        if (this.ioConfiguration == null || stack == null || stack.getType().isAir()) {
            return false;
        }
        net.minecraft.core.Direction face = this.inputFaceFor(sourceFacing);
        if (face == null) {
            return false;
        }
        if (this.funnelTransits.containsKey(face) || this.isFull()) {
            return false;
        }
        FunnelTransit t = new FunnelTransit();
        t.item = stack.clone();
        t.item.setAmount(1);
        t.jitter = jitter;
        t.out = false;
        t.progress = 0.0f;
        t.display = display;
        t.spawned = spawned;
        this.funnelTransits.put(face, t);
        return true;
    }

    private boolean addToInputs(net.minecraft.world.item.ItemStack stack) {
        boolean changed = false;
        for (int s : this.getInputSlots()) {
            int space;
            int move;
            if (stack.isEmpty()) break;
            net.minecraft.world.item.ItemStack cur = this.getItem(s);
            if (cur == null || cur.isEmpty()) {
                this.setItem(s, stack.copy());
                stack.setCount(0);
                changed = true;
                continue;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItemSameComponents((net.minecraft.world.item.ItemStack)cur, (net.minecraft.world.item.ItemStack)stack) || (move = Math.min(space = cur.getMaxStackSize() - cur.getCount(), stack.getCount())) <= 0) continue;
            cur.grow(move);
            stack.shrink(move);
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
        return stack.isEmpty();
    }

    protected void pushFunnelOutputs(Level level) {
        if (this.ioConfiguration == null || !this.providesAnyFunnelOutput()) {
            return;
        }
        block0: for (net.minecraft.core.Direction d : dev.arubik.craftengine.util.Utils.DIRECTIONS) {
            ConveyorReceiver recv;
            Object object;
            BlockEntity be;
            net.minecraft.core.Direction local = this.toLocalItemDir(d);
            if (!this.ioConfiguration.providesOutput(IOConfiguration.IOType.FUNNEL, local) || this.funnelTransits.containsKey(d) || (be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(d))) == null || !((object = be.controller) instanceof ConveyorReceiver) || (recv = (ConveyorReceiver)object) instanceof AbstractMachineBlockEntity || recv.isFull()) continue;
            int[] outputSlots = this.getOutputSlots();
            int n = outputSlots.length;
            for (int i = 0; i < n; ++i) {
                int s = outputSlots[i];
                net.minecraft.world.item.ItemStack cur = this.getItem(s);
                if (cur == null || cur.isEmpty()) continue;
                ItemStack one = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack)cur).clone();
                one.setAmount(1);
                FunnelTransit t = new FunnelTransit();
                t.item = one;
                t.jitter = 0.0f;
                t.out = true;
                t.progress = 0.0f;
                this.funnelTransits.put(d, t);
                cur.shrink(1);
                this.setChanged();
                continue block0;
            }
        }
    }

    private void tickFunnelTransits(CEWorld world) {
        if (this.funnelTransits.isEmpty()) {
            return;
        }
        Level level = (Level)world.world.minecraftWorld();
        Iterator<Map.Entry<net.minecraft.core.Direction, FunnelTransit>> it = this.funnelTransits.entrySet().iterator();
        while (it.hasNext()) {
            ConveyorReceiver recv;
            ConveyorDisplayReceiver dr;
            BlockEntityController blockEntityController;
            ConveyorBlockEntity belt;
            BlockEntityController blockEntityController2;
            Map.Entry<net.minecraft.core.Direction, FunnelTransit> e = it.next();
            net.minecraft.core.Direction face = e.getKey();
            FunnelTransit t = e.getValue();
            float rpm = 64.0f;
            BlockEntity nbe = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(face));
            if (nbe != null && (blockEntityController2 = nbe.controller) instanceof ConveyorBlockEntity && (belt = (ConveyorBlockEntity)blockEntityController2).effectiveRpm() > 0.0f) {
                rpm = belt.effectiveRpm();
            }
            float inc = ConveyorMath.progressPerTick(rpm, 64.0f, 16);
            t.progress = Math.min(1.0f, t.progress + inc);
            this.renderFunnelTransit(world, face, t);
            if (t.progress < 1.0f) continue;
            if (!t.out) {
                net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy((ItemStack)t.item);
                if (!this.addToInputs(nms)) continue;
                this.despawnFunnelTransit(world, t);
                it.remove();
                continue;
            }
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(face));
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorBlockEntity) {
                ConveyorBlockEntity belt2 = (ConveyorBlockEntity)blockEntityController;
                if (!belt2.adoptFromFunnel(world, t.item, t.jitter, t.display, t.spawned, AbstractMachineBlockEntity.coreDir(face).opposite())) continue;
                t.display = null;
                it.remove();
                continue;
            }
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorDisplayReceiver && !((dr = (ConveyorDisplayReceiver)blockEntityController) instanceof AbstractMachineBlockEntity)) {
                if (dr.isFull() || !dr.adoptConveyorItem(t.item, t.jitter, t.display, t.spawned, AbstractMachineBlockEntity.coreDir(face))) continue;
                t.display = null;
                it.remove();
                continue;
            }
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorReceiver && !((recv = (ConveyorReceiver)blockEntityController) instanceof AbstractMachineBlockEntity)) {
                if (recv.isFull() || !recv.receiveConveyorItem(t.item, AbstractMachineBlockEntity.coreDir(face), t.jitter)) continue;
                this.despawnFunnelTransit(world, t);
                it.remove();
                continue;
            }
            this.addToInputs(CraftItemStack.asNMSCopy((ItemStack)t.item));
            this.despawnFunnelTransit(world, t);
            it.remove();
        }
    }

    private void renderFunnelTransit(CEWorld world, net.minecraft.core.Direction face, FunnelTransit t) {
        BlockPos pos = this.getMachinePos();
        List viewers = world.world().getTrackedBy(new ChunkPos(new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ())));
        if (t.display == null) {
            t.display = new ConveyorItemDisplay();
        }
        t.display.setNmsItem(CraftItemStack.asNMSCopy((ItemStack)t.item));
        Vector3f center = new Vector3f(0.5f, 0.28f, 0.5f);
        Vector3f faceEdge = new Vector3f(0.5f + (float)face.getStepX() * 0.5f, 0.28f, 0.5f + (float)face.getStepZ() * 0.5f);
        Vector3f rel = t.out ? ConveyorMath.interpolate(center, faceEdge, t.progress) : ConveyorMath.interpolate(faceEdge, center, t.progress);
        int mx = t.out ? face.getStepX() : -face.getStepX();
        int mz = t.out ? face.getStepZ() : -face.getStepZ();
        Quaternionf rot = ConveyorMath.itemRotation(mx, mz, 0);
        rot.rotateY(t.jitter);
        t.display.setRotation(rot);
        t.display.render(viewers, (float)pos.getX() + rel.x, (float)pos.getY() + rel.y, (float)pos.getZ() + rel.z, !t.spawned);
        t.display.consumeRotationDirty();
        t.spawned = true;
    }

    private void despawnFunnelTransit(CEWorld world, FunnelTransit t) {
        if (t.display == null) {
            return;
        }
        for (net.momirealms.craftengine.core.entity.player.Player p : world.world().getTrackedBy(new ChunkPos(new net.momirealms.craftengine.core.world.BlockPos(this.getMachinePos().getX(), this.getMachinePos().getY(), this.getMachinePos().getZ())))) {
            t.display.despawn(p);
        }
        t.display.clearShown();
        t.display = null;
        t.spawned = false;
    }

    protected void despawnAllFunnelTransits() {
        if (this.ceWorld == null || this.funnelTransits.isEmpty()) {
            return;
        }
        for (FunnelTransit t : this.funnelTransits.values()) {
            this.despawnFunnelTransit(this.ceWorld, t);
        }
        this.funnelTransits.clear();
    }

    private boolean providesAnyFunnelOutput() {
        for (net.minecraft.core.Direction d : dev.arubik.craftengine.util.Utils.DIRECTIONS) {
            if (!this.ioConfiguration.providesOutput(IOConfiguration.IOType.FUNNEL, d)) continue;
            return true;
        }
        return false;
    }

    public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world, ImmutableBlockState state) {
        return BlockEntityController.createTickerHelper(AbstractMachineBlockEntity::tickController);
    }

    public static void tickController(CEWorld world, net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state, AbstractMachineBlockEntity self) {
        Level level = (Level)world.world.minecraftWorld();
        self.ceWorld = world;
        self.tick(level, BlockPos.of((long)pos.asLong()), state);
        if (!level.isClientSide()) {
            self.tickFunnelTransits(world);
        }
    }

    private void updateMachineModeProperty(Level level, BlockPos pos, ImmutableBlockState state) {
        MachineMode newMode;
        BlockBehavior behavior = state.behavior();
        if (!(behavior instanceof MachineBlockBehavior)) {
            return;
        }
        MachineBlockBehavior machineBehavior = (MachineBlockBehavior)behavior;
        Property<MachineMode> machineModeProp = machineBehavior.MACHINE_MODE;
        if (machineModeProp == null) {
            return;
        }
        MachineMode currentMode = (MachineMode)(state.get(machineModeProp));
        if (currentMode != (newMode = this.burnTime > 0 ? (this.isOverclocked() ? MachineMode.OVERCLOCKING : MachineMode.WORKING) : MachineMode.IDLE)) {
            ImmutableBlockState newState = state.with(machineModeProp, newMode);
            level.setBlock(pos, (BlockState)newState.customBlockState().minecraftState(), 11);
        }
    }

    protected void maybeUpdateActivated(Level level, BlockPos pos, ImmutableBlockState state, boolean active) {
        if (this.lastActivated != null && this.lastActivated == active) {
            return;
        }
        try {
            Property p;
            Property property = p = state == null ? null : state.getProperty("activated");
            if (p == null) {
                this.lastActivated = active;
                return;
            }
            Comparable val = p.valueByName(String.valueOf(active));
            if (val == null) {
                return;
            }
            ImmutableBlockState ns = ImmutableBlockState.with((ImmutableBlockState)state, (Property)p, val);
            if (ns == state) {
                this.lastActivated = active;
                return;
            }
            level.setBlock(pos, (BlockState)ns.customBlockState().minecraftState(), 2);
            this.lastActivated = active;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void setChanged() {
        if (this.menu != null && !this.menu.getInventory().getViewers().isEmpty()) {
            this.menu.syncFromMachine();
        }
    }

    public boolean canOutputRedstone(net.minecraft.core.Direction dir) {
        IOConfiguration config = this.getIOConfiguration();
        return config != null && config.providesOutput(IOConfiguration.IOType.REDSTONE, dir);
    }

    public ScriptContext buildScriptContext() {
        return null;
    }

    /** The {@code "on_get_container"} script ref, if this machine declares one — see
     *  {@link dev.arubik.craftengine.machine.MachineDefinition#onGetContainerScript()}'s javadoc.
     *  {@code null} by default; overridden by {@link DataMachineBlockEntity}/
     *  {@link DataMultiBlockMachineBlockEntity} to read their own definition. */
    protected String onGetContainerScriptRef() {
        return null;
    }

    /** {@link #resolveContainerOverride(net.minecraft.core.BlockPos, net.minecraft.core.Direction)}
     *  with no accessor context — for a caller that isn't itself another block (e.g. a script
     *  directly reading {@code Machine.container}). */
    public java.util.Optional<net.minecraft.world.Container> resolveContainerOverride() {
        return this.resolveContainerOverride(null, null);
    }

    /**
     * Evaluates this machine's {@code on_get_container} hook (if declared) and, if it returned a
     * script {@code Container} value, returns that instead of this machine's own inventory — see
     * {@link dev.arubik.craftengine.machine.MachineDefinition#onGetContainerScript()}. Empty (no
     * override) if no hook is declared, the hook returns null/non-container, or anything throws —
     * callers fall back to treating this block entity as its own container as usual.
     *
     * {@code accessorPos}/{@code accessorFace} identify WHO is asking (a funnel/hopper/pipe
     * segment's own position, and which of this machine's faces it's reaching in from) — exposed to
     * the hook as {@code accessor_pos}/{@code accessor_x}/{@code accessor_y}/{@code accessor_z}/
     * {@code accessor_face} script vars (all NULL/empty when unknown) so a Portable Storage
     * Interface, say, can tell a hopper pulling from below apart from a script reading
     * {@code Machine.container} directly, or refuse a direction it doesn't expose.
     */
    public java.util.Optional<net.minecraft.world.Container> resolveContainerOverride(
            net.minecraft.core.BlockPos accessorPos, net.minecraft.core.Direction accessorFace) {
        String ref = this.onGetContainerScriptRef();
        if (ref == null || ref.isBlank()) return java.util.Optional.empty();
        try {
            ScriptContext base = this.buildScriptContext();
            if (base == null) return java.util.Optional.empty();
            ScriptContext.Builder ctxBuilder = ScriptContext.builder().copyFrom(base);
            if (accessorPos != null) {
                ctxBuilder.vector("accessor_pos", accessorPos.getX() + 0.5, accessorPos.getY() + 0.5, accessorPos.getZ() + 0.5)
                        .num("accessor_x", accessorPos.getX())
                        .num("accessor_y", accessorPos.getY())
                        .num("accessor_z", accessorPos.getZ());
            }
            ctxBuilder.str("accessor_face", accessorFace != null ? accessorFace.getName() : "");
            ScriptContext ctx = ctxBuilder.build();
            dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(ref);
            if (call == null) return java.util.Optional.empty();
            dev.arubik.craftengine.script.ScriptValue result = call.evaluate(ctx);
            if (result instanceof dev.arubik.craftengine.script.ScriptValue.Obj o
                    && o.instance() instanceof net.minecraft.world.Container c) {
                // Wrapped so ANY on_get_container redirect (not just PSI) automatically stamps
                // getLastTransferTick() whenever something actually moves through it — see that
                // method's javadoc for why this exists (a contraption-mounted actor deciding whether
                // to keep holding a contraption still while its connection is actively in use).
                return java.util.Optional.of(new TransferTrackingContainer(c, this));
            }
        } catch (Throwable ignored) {}
        return java.util.Optional.empty();
    }

    private static final dev.arubik.craftengine.util.TypedKey<Integer> LAST_TRANSFER_TICK_KEY =
            dev.arubik.craftengine.util.TypedKey.of("polyfills", "last_transfer_tick", dev.arubik.craftengine.util.NbtType.INTEGER);

    /**
     * The world tick {@link #LAST_TRANSFER_TICK_KEY} was last stamped on THIS machine — i.e. the
     * last time something actually moved an item into or out of whatever container its
     * {@code on_get_container} hook currently redirects to (see {@link TransferTrackingContainer}).
     * -1 if nothing has ever transferred. Exposed to scripts as {@code Machine.last_transfer_tick} —
     * e.g. a Portable Storage Interface mounted on a contraption reads the STATIONARY partner's
     * value (via {@code Block.machine}) to decide whether to keep {@code Contraption.hold()}ing
     * itself still: recent activity means transfers might still be in flight, so the contraption
     * should stay put a little longer rather than pulling away mid-transfer.
     */
    public int getLastTransferTick() {
        PersistentBlockEntity be = this;
        Integer v = be.get(LAST_TRANSFER_TICK_KEY);
        return v != null ? v : -1;
    }

    /** Delegates every {@link net.minecraft.world.Container} call to {@code delegate}, stamping
     *  {@link #LAST_TRANSFER_TICK_KEY} on {@code owner} whenever a mutating call actually changes
     *  something (not on a no-op removal of nothing, or a read). Generic — applies to whatever ANY
     *  machine's {@code on_get_container} hook returns, not just a Portable Storage Interface's. */
    private static final class TransferTrackingContainer implements net.minecraft.world.WorldlyContainer {
        private final net.minecraft.world.Container delegate;
        private final AbstractMachineBlockEntity owner;

        TransferTrackingContainer(net.minecraft.world.Container delegate, AbstractMachineBlockEntity owner) {
            this.delegate = delegate;
            this.owner = owner;
        }

        private void stamp() {
            try {
                int tick = net.minecraft.server.MinecraftServer.getServer().getTickCount();
                this.owner.set(LAST_TRANSFER_TICK_KEY, tick);
            } catch (Throwable ignored) {}
        }

        @Override public int getContainerSize() { return this.delegate.getContainerSize(); }
        @Override public boolean isEmpty() { return this.delegate.isEmpty(); }
        @Override public net.minecraft.world.item.ItemStack getItem(int i) { return this.delegate.getItem(i); }

        @Override
        public net.minecraft.world.item.ItemStack removeItem(int i, int count) {
            net.minecraft.world.item.ItemStack result = this.delegate.removeItem(i, count);
            if (!result.isEmpty()) this.stamp();
            return result;
        }

        @Override public net.minecraft.world.item.ItemStack removeItemNoUpdate(int i) { return this.delegate.removeItemNoUpdate(i); }

        @Override
        public void setItem(int i, net.minecraft.world.item.ItemStack stack) {
            net.minecraft.world.item.ItemStack before = this.delegate.getItem(i);
            this.delegate.setItem(i, stack);
            boolean changed = !net.minecraft.world.item.ItemStack.isSameItemSameComponents(before, stack)
                    || before.getCount() != stack.getCount();
            if (changed) this.stamp();
        }

        @Override public void setChanged() { this.delegate.setChanged(); }
        @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return this.delegate.stillValid(player); }
        @Override public void clearContent() { this.delegate.clearContent(); }
        @Override public org.bukkit.Location getLocation() { return this.delegate.getLocation(); }
        @Override public void setMaxStackSize(int size) { this.delegate.setMaxStackSize(size); }
        @Override public int getMaxStackSize() { return this.delegate.getMaxStackSize(); }
        @Override public int getMaxStackSize(net.minecraft.world.item.ItemStack stack) { return this.delegate.getMaxStackSize(stack); }
        @Override public void startOpen(net.minecraft.world.entity.ContainerUser user) { this.delegate.startOpen(user); }
        @Override public void stopOpen(net.minecraft.world.entity.ContainerUser user) { this.delegate.stopOpen(user); }
        @Override public java.util.List<net.minecraft.world.entity.ContainerUser> getEntitiesWithContainerOpen() { return this.delegate.getEntitiesWithContainerOpen(); }
        @Override public boolean canPlaceItem(int i, net.minecraft.world.item.ItemStack stack) { return this.delegate.canPlaceItem(i, stack); }
        @Override public boolean canTakeItem(net.minecraft.world.Container target, int i, net.minecraft.world.item.ItemStack stack) { return this.delegate.canTakeItem(target, i, stack); }
        @Override public java.util.List<net.minecraft.world.item.ItemStack> getContents() { return this.delegate.getContents(); }
        @Override public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity who) { this.delegate.onOpen(who); }
        @Override public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity who) { this.delegate.onClose(who); }
        @Override public java.util.List<org.bukkit.entity.HumanEntity> getViewers() { return this.delegate.getViewers(); }
        @Override public org.bukkit.inventory.InventoryHolder getOwner() { return this.delegate.getOwner(); }

        // WorldlyContainer (face-aware) methods — vanilla hoppers cast straight to this interface
        // for ANY block they find a Container at, so a wrapper that only implements plain Container
        // crashes them outright (ClassCastException) rather than just failing to extract anything.
        // Delegate to the wrapped container's own face rules if it happens to be WorldlyContainer
        // too; otherwise fall back to the same permissive "every slot, every face" default
        // PersistentWorldlyBlockEntity itself uses.
        @Override
        public int[] getSlotsForFace(net.minecraft.core.Direction side) {
            if (this.delegate instanceof net.minecraft.world.WorldlyContainer wc) return wc.getSlotsForFace(side);
            int size = this.delegate.getContainerSize();
            int[] slots = new int[size];
            for (int i = 0; i < size; i++) slots[i] = i;
            return slots;
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction direction) {
            if (this.delegate instanceof net.minecraft.world.WorldlyContainer wc) return wc.canPlaceItemThroughFace(index, stack, direction);
            return true;
        }

        @Override
        public boolean canTakeItemThroughFace(int index, net.minecraft.world.item.ItemStack stack, net.minecraft.core.Direction direction) {
            if (this.delegate instanceof net.minecraft.world.WorldlyContainer wc) return wc.canTakeItemThroughFace(index, stack, direction);
            return true;
        }
    }

    private static final class FunnelTransit {
        ItemStack item;
        float progress;
        float jitter;
        boolean out;
        ConveyorItemDisplay display;
        boolean spawned;

        private FunnelTransit() {
        }
    }
}

