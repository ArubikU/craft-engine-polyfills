/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.Player
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.CEWorld
 *  net.momirealms.craftengine.core.world.ChunkPos
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.inventory.CraftInventory
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.joml.Quaternionf
 */
package dev.arubik.craftengine.machine.block.entity;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.conveyor.ConveyorItemDisplay;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidTank;
import dev.arubik.craftengine.fluid.FluidType;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTank;
import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenu;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.MenuText;
import dev.arubik.craftengine.machine.menu.OverclockMenu;
import dev.arubik.craftengine.machine.menu.RecipeInfoIcon;
import dev.arubik.craftengine.machine.menu.UpgradeMenu;
import dev.arubik.craftengine.machine.menu.bar.BarDefinition;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.machine.menu.layout.MachineLayout;
import dev.arubik.craftengine.machine.menu.layout.MenuSlotType;
import dev.arubik.craftengine.machine.recipe.AbstractProcessingRecipe;
import dev.arubik.craftengine.machine.recipe.CraftEngineItemInput;
import dev.arubik.craftengine.machine.recipe.ItemInput;
import dev.arubik.craftengine.machine.recipe.ItemOutput;
import dev.arubik.craftengine.machine.recipe.RecipeInput;
import dev.arubik.craftengine.machine.recipe.RecipeOutput;
import dev.arubik.craftengine.machine.recipe.TagInput;
import dev.arubik.craftengine.machine.recipe.loader.RecipeManager;
import dev.arubik.craftengine.machine.render.ModelRendersDriven;
import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptProgram;
import dev.arubik.craftengine.script.ScriptRegistry;
import dev.arubik.craftengine.script.types.machine.MachineType;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.machine.upgrade.UpgradeModifiers;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.ChunkPos;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joml.Quaternionf;

public class DataMachineBlockEntity
extends AbstractMachineBlockEntity
implements RpmConsumer,
RpmProvider,
ModelRendersDriven,
dev.arubik.craftengine.rotation.KineticMember {
    /** Not final: a targeted reload re-points this at the freshly parsed definition. */
    private MachineDefinition definition;
    private RendererManager rendererManager;
    private dev.arubik.craftengine.machine.render.ScriptAnimation activeAnimation = null;
    private ConveyorItemDisplay[] specDisplays;
    private int[] specDisplayHashes;
    private ServerLevel lastKnownLevel;
    // Throttles for tickSpecDisplays() packet/CPU cost — see its use for why. Kept as a constant
    // rather than per-spec config: every continuously-rotating kinetic renderer wants the same
    // treatment, and ConveyorItemDisplay's interpolation duration is widened to match
    // ROTATION_PACKET_INTERVAL so the client still animates smoothly across the gap.
    private static final long LIGHT_CHECK_INTERVAL = 10;
    private static final long ROTATION_PACKET_INTERVAL = 4;
    public static volatile boolean SPEC_DISPLAY_DEBUG = false;
    public static volatile boolean SCRIPT_DEBUG = false;
    private static final Set<DataMachineBlockEntity> INSTANCES = Collections.newSetFromMap(new WeakHashMap());
    private float inputRpm = 0.0f;
    private final List<RpmProvider> activeMotors = new ArrayList<RpmProvider>();
    private RpmProvider activeMotor;
    private int lastSuLoad = 0;
    // ---- RpmNetwork fields --------------------------------------------------
    private long rpmNetworkId    = 0L;
    private float theoreticalSpeed = 0f;
    private int rpmConflictTicks = 0;
    /**
     * Game tick at which the SOURCE behind our current rpm produced it; -1 when we have none.
     * Carried unchanged along the whole chain, so a stopped source expires every block at once.
     */
    private long rpmSourceStamp = -1L;
    /** World direction this block last took power from, for gearbox-style relative output. */
    private Direction rpmInputFace = null;
    /** Consecutive pulls that produced an identical result; drives the scan backoff. */
    private int stablePulls = 0;
    private int stressGrace = 0;
    private int sourceDistance = Integer.MAX_VALUE;
    private float rpmSourceOutput = 0.0f;
    private boolean rpmSourceActive = false;
    private MachineMenuConfig menuConfig = MachineMenuConfig.parse(key -> null);
    private List<MachineBar> bars = List.of();
    private boolean invalidating = false;
    private int actionTickCounter = 0;
    private long ticksAlive = 0L;
    private static final dev.arubik.craftengine.util.TypedKey<Long> KEY_TICKS_ALIVE =
        dev.arubik.craftengine.util.TypedKey.of("polyfills", "ticks_alive", dev.arubik.craftengine.util.NbtType.LONG);

    public long ticksAlive() { return ticksAlive; }
    private int page = 0;
    /** Browser-like page history for back navigation. */
    private final java.util.ArrayDeque<Integer> pageHistory = new java.util.ArrayDeque<>(8);
    private float overclock = 0.0f;
    private MachineMenu active;
    private int curUnlocked = -1;
    private double curOverclockLimit = 0.0;
    private double curFuelEff = 0.0;
    private double curGeneration = 0.0;
    private Map<Key, List<MachineAttributes.Mod>> upgradeDefs = Map.of();
    private double genBuffer = 0.0;

    /**
     * Re-points every live machine at its freshly parsed definition and drops its renderers, so
     * a reload reaches blocks already placed in the world instead of only new placements.
     *
     * <p>Only definition-derived behaviour is refreshed: flags, io, renderers, script hooks and
     * menu layout. Tanks and inventories were sized when the block entity was built and are left
     * alone — resizing them under a running machine would strand or duplicate their contents.
     *
     * @return the number of machines re-pointed
     */
    public static int refreshDefinitions() {
        int n = 0;
        for (DataMachineBlockEntity be : new ArrayList<DataMachineBlockEntity>(INSTANCES)) {
            if (be.definition == null) continue;
            MachineDefinition fresh = MachineDefinition.REGISTRY.get(be.definition.id());
            if (fresh == null || fresh == be.definition) continue;
            be.definition = fresh;
            try {
                if (fresh.io() != null) be.setIOConfiguration(fresh.io());
            } catch (Throwable ignored) {
                // A machine whose io block was removed keeps the config it was built with.
            }
            n++;
        }
        reloadAll();   // renderers are rebuilt from the new definition on the next tick
        return n;
    }

    public static int reloadAll() {
        int n = 0;
        for (DataMachineBlockEntity be : new ArrayList<DataMachineBlockEntity>(INSTANCES)) {
            if (be.rendererManager != null) {
                try {
                    be.rendererManager.close();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            be.rendererManager = null;
            be.despawnSpecDisplays();
            ++n;
        }
        return n;
    }

    public int sourceDistance() {
        return this.sourceDistance;
    }

    public List<RpmProvider> getActiveMotors() {
        return Collections.unmodifiableList(this.activeMotors);
    }

    /** True when this machine declares an rpm INPUT face, i.e. it relays rather than originates. */
    /** 0=X, 1=Y, 2=Z — the axis index RpmPropagation's gearbox rule expects. */
    private static int axisIndex(Direction d) {
        return switch (d.getAxis()) {
            case X -> 0;
            case Y -> 1;
            case Z -> 2;
        };
    }

    /** +1 for EAST/UP/SOUTH, -1 for WEST/DOWN/NORTH — Direction.getAxisDirection() as a sign. */
    private static int axisSign(Direction d) {
        return d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
    }

    /**
     * The RPM this machine actually delivers through {@code face}, sign included, or 0 when that
     * face is not a declared rpm output.
     *
     * <p>This is the single place that answers "what comes out of this side?". It folds together
     * the three things that decide it — whether {@code io.rpm} allows the face at all, the
     * gearbox-style relative sign, and any static {@code output_inverted} list — so a script
     * asking {@code Machine.rpm_out("front")} gets exactly what the neighbouring block would
     * read when it pulls, rather than having to reconstruct the rule itself.
     */
    public float rpmThrough(Direction face) {
        if (face == null || this.definition == null) return 0f;
        Level level = this.getNMSLevel();
        if (level == null) return 0f;
        if (!this.definition.kinetics()) return 0f;
        if (!this.isValidOutputFace(face, level)) return 0f;

        float raw = this.getRpm();
        if (raw == 0f) return 0f;

        if (this.definition.rpmOutputRelative()) {
            Direction in = this.rpmInputFace;
            if (in == null) return 0f;   // an undriven gearbox has no direction to hand on
            return raw * dev.arubik.craftengine.rotation.RpmPropagation.gearboxModifier(
                    axisIndex(face), axisSign(face), axisIndex(in), axisSign(in));
        }

        Set<String> inverted = this.definition.rpmOutputInvertedRaw();
        boolean flip = !inverted.isEmpty()
                && rpmFacesContain(inverted, face, this.getFacing(level));
        return dev.arubik.craftengine.rotation.RpmPropagation.applyInversion(raw, flip);
    }

    /** The face this block last took power from, or null when it is a source / unpowered. */
    public Direction rpmInputFace() {
        return this.rpmInputFace;
    }

    public boolean isRpmRelay() {
        return this.definition != null && !this.definition.rpmInputFacesRaw().isEmpty();
    }

    public void setRpmSourceOutput(float rpm) {
        this.rpmSourceOutput = rpm;
        boolean bl = this.rpmSourceActive = rpm != 0.0f;
        if (this.rpmSourceActive) {
            // Only a genuine source sits at distance 0. A relay — cogwheel, gearbox, shaft — just
            // re-emits what it pulled and must KEEP its distance from the real source. Stamping 0
            // here made every relay claim to be the origin, so relay_to's anti-loop guard
            // (neighbour <= me) blocked the whole chain after the first hop.
            if (!this.isRpmRelay()) {
                this.sourceDistance = 0;
                // Only a genuine origin mints a stamp. Relays carry along whatever they were
                // given, so the whole chain shares one expiry instead of each hop having its own.
                this.rpmSourceStamp = this.gameTime();
            }
            this.inputRpm = rpm;
        } else {
            leaveNetwork();
            this.theoreticalSpeed  = 0f;
            this.rpmConflictTicks  = 0;
            this.rpmSourceStamp    = -1L;
        }
    }

    /**
     * Take an RPM value handed over by a neighbouring relay (see {@code Machine.relay_to}).
     *
     * <p>Distance is {@code fromDistance + 1} so each hop is strictly farther from the source than
     * the block that drove it — which is exactly what the anti-loop guard in {@code relay_to}
     * tests. Using {@link #setRpmSourceOutput} for this instead left every hop at the same
     * distance and stalled chains of three or more cogwheels.
     */
    /** Game tick the source behind our rpm last produced it, or -1 if we have no source. */
    public long rpmSourceStamp() {
        return this.rpmSourceStamp;
    }

    /** Current game tick, or -1 when this block has no level yet. */
    private long gameTime() {
        Level l = this.getNMSLevel();
        return l == null ? -1L : l.getGameTime();
    }

    public void acceptRelayedRpm(float rpm, int fromDistance) {
        this.acceptRelayedRpm(rpm, fromDistance, this.gameTime());
    }

    /** @param sourceStamp the tick the ORIGINAL source produced this value, passed along unchanged */
    public void acceptRelayedRpm(float rpm, int fromDistance, long sourceStamp) {
        this.rpmSourceOutput = rpm;
        this.rpmSourceActive = rpm != 0.0f;
        this.inputRpm = rpm;
        this.sourceDistance = dev.arubik.craftengine.rotation.RpmPropagation.distanceAfterPull(fromDistance);
        // Cogs mesh ACROSS their axis while their rpm input faces run ALONG it, so a relayed cog
        // cannot re-discover its driver with its own pull. Without this grace the next tick's
        // fruitless pull zeroed the value straight back out, which is what made the model flicker
        // and stopped chains of stacked cogs from ever forming.
        this.rpmSourceStamp = rpm != 0.0f ? sourceStamp : -1L;
        if (!this.rpmSourceActive) {
            this.theoreticalSpeed = 0f;
            this.rpmConflictTicks = 0;
        }
    }

    // ---- RpmNetwork API -----------------------------------------------------

    public long rpmNetworkId()           { return rpmNetworkId; }
    public void setRpmNetworkId(long id) { this.rpmNetworkId = id; }
    public float theoreticalSpeed()      { return theoreticalSpeed; }
    public void setTheoreticalSpeed(float s) { this.theoreticalSpeed = s; }
    public int  getRpmConflictTicks()    { return rpmConflictTicks; }
    public void setRpmConflictTicks(int v) { this.rpmConflictTicks = v; }

    /** Called by RpmNetwork when overstress state changes. */
    public void onNetworkOverstressChanged(boolean overstressed) {
        float actual = overstressed ? 0f : theoreticalSpeed;
        this.inputRpm = actual;
        if (this.rpmSourceActive) this.rpmSourceOutput = actual;
    }

    /** Report SU to this machine's network (negative = generate, positive = consume). */
    public void reportSuToNetwork(float suValue) {
        if (rpmNetworkId == 0L) return;
        dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(rpmNetworkId);
        if (net != null) net.updateMemberSu(this, suValue);
    }

    /** Join a network. Leaves current network first if different. */
    public void joinNetwork(long networkId) {
        if (this.rpmNetworkId == networkId) return;
        leaveNetwork();
        this.rpmNetworkId = networkId;
        dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(networkId);
        if (net != null) net.addMember(this, 0f);
    }

    /** Leave current network. No-op if not in one. */
    public void leaveNetwork() {
        if (rpmNetworkId == 0L) return;
        dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(rpmNetworkId);
        if (net != null) net.removeMember(this);
        this.rpmNetworkId = 0L;
    }

    @Override
    public float getRpm() {
        return this.rpmSourceActive ? this.rpmSourceOutput : this.inputRpm;
    }

    @Override
    public boolean isRpmSource() {
        return this.rpmSourceActive;
    }

    @Override
    public float potentialRpm() {
        if (this.rpmSourceActive) {
            return Math.abs(this.rpmSourceOutput);
        }
        return this.sourceDistance < Integer.MAX_VALUE ? Math.abs(this.inputRpm) : 0.0f;
    }

    public boolean isValidOutputFace(Direction face, Level level) {
        // Only kinetic blocks restrict which faces may emit RPM. A machine that declares no rpm
        // output faces falls through to the unrestricted return just below, so recipe machines
        // that merely consume stress are unaffected.
        if (this.definition == null || !this.definition.kinetics()) {
            return true;
        }
        Set<String> same = this.definition.rpmOutputFacesRaw();
        Set<String> inv = this.definition.rpmOutputInvertedRaw();
        if (same.isEmpty() && inv.isEmpty() && !this.definition.rpmOutputDeclared()) {
            return true;
        }
        Direction facing = this.getFacing(level);
        return this.rpmFacesContainWithAxis(same, face, facing, level) || this.rpmFacesContainWithAxis(inv, face, facing, level);
    }

    /**
     * Derive the functional rotation axis of a CE block from its block state.
     * Supports: "axis" (x/y/z), "facing" / "horizontal_facing" (direction → axis of that direction).
     * Returns null if the block has no rotation-related property.
     */
    @SuppressWarnings("unchecked")
    private static Direction.Axis getBlockFunctionalAxis(net.momirealms.craftengine.core.block.ImmutableBlockState cs) {
        net.momirealms.craftengine.core.block.BlockDefinition def = (net.momirealms.craftengine.core.block.BlockDefinition) cs.owner().value();
        // Try "axis" property (x/y/z)
        Property axisProp = def.getProperty("axis");
        if (axisProp != null) {
            String v = String.valueOf(cs.get(axisProp)).toLowerCase();
            return switch (v) { case "x" -> Direction.Axis.X; case "y" -> Direction.Axis.Y; default -> Direction.Axis.Z; };
        }
        // Try "facing" / "horizontal_facing" / "6_direction" / "4_direction"
        for (String propName : new String[]{"facing", "horizontal_facing", "6_direction", "4_direction"}) {
            Property facingProp = def.getProperty(propName);
            if (facingProp != null) {
                try {
                    Direction dir = Direction.byName(String.valueOf(cs.get(facingProp)).toLowerCase());
                    if (dir != null) return dir.getAxis();
                } catch (Throwable ignored) {}
            }
        }
        return null;
    }

    public static boolean rpmFacesContainWithAxisStatic(Set<String> raw, Direction d, Direction facing, ServerLevel level, BlockPos pos) {
        boolean hasAxisNames;
        boolean bl = hasAxisNames = raw.contains("axis_pos") || raw.contains("axis_neg") || raw.contains("axis_perp");
        if (!hasAxisNames) {
            return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
        }
        try {
            Property axisProp;
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(pos)).orElse(null);
            if (cs != null && (axisProp = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                String axis;
                Direction posDir = switch (axis = String.valueOf(cs.get(axisProp)).toLowerCase()) {
                    case "x" -> Direction.EAST;
                    case "y" -> Direction.UP;
                    default -> Direction.SOUTH;
                };
                Direction.Axis cogAxis = posDir.getAxis();
                Iterator<String> iterator = raw.iterator();
                while (iterator.hasNext()) {
                    boolean match;
                    String s;
                    if (!(match = (switch (s = iterator.next()) {
                        case "axis_pos" -> {
                            if (d == posDir) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_neg" -> {
                            if (d == posDir.getOpposite()) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_perp" -> {
                            if (d.getAxis() != cogAxis) {
                                // Create mod rule: neighbor's functional axis parallel to d = shaft continuation, not gear mesh
                                // Supports: axis (x/y/z), facing, horizontal_facing, 4_direction, 6_direction
                                try {
                                    BlockPos neighborPos2 = pos.relative(d);
                                    ImmutableBlockState neighborCs2 = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(neighborPos2)).orElse(null);
                                    if (neighborCs2 != null) {
                                        Direction.Axis neighborFuncAxis = getBlockFunctionalAxis(neighborCs2);
                                        if (neighborFuncAxis != null && neighborFuncAxis == d.getAxis()) {
                                            yield false;
                                        }
                                    }
                                } catch (Throwable ignored) {}
                                yield true;
                            }
                            yield false;
                        }
                        default -> false;
                    }))) continue;
                    return true;
                }
                HashSet<String> nonAxis = new HashSet<String>(raw);
                nonAxis.remove("axis_pos");
                nonAxis.remove("axis_neg");
                nonAxis.remove("axis_perp");
                if (!nonAxis.isEmpty()) {
                    return DataMachineBlockEntity.rpmFacesContain(nonAxis, d, facing);
                }
                return false;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
    }

    public Direction getFacingPublic(Level level) {
        return this.getFacing(level);
    }

    boolean rpmFacesContainWithAxis(Set<String> raw, Direction d, Direction facing, Level level) {
        boolean hasAxisNames;
        boolean bl = hasAxisNames = raw.contains("axis_pos") || raw.contains("axis_neg") || raw.contains("axis_perp");
        if (!hasAxisNames) {
            return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
        }
        try {
            Property axisProp;
            BlockState bs = level.getBlockState(this.getMachinePos());
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(bs).orElse(null);
            if (cs != null && (axisProp = ((BlockDefinition)cs.owner().value()).getProperty("axis")) != null) {
                String axis;
                Direction posDir = switch (axis = String.valueOf(cs.get(axisProp)).toLowerCase()) {
                    case "x" -> Direction.EAST;
                    case "y" -> Direction.UP;
                    default -> Direction.SOUTH;
                };
                Direction.Axis cogAxis = posDir.getAxis();
                Iterator<String> iterator = raw.iterator();
                while (iterator.hasNext()) {
                    boolean match;
                    String s;
                    if (!(match = (switch (s = iterator.next()) {
                        case "axis_pos" -> {
                            if (d == posDir) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_neg" -> {
                            if (d == posDir.getOpposite()) {
                                yield true;
                            }
                            yield false;
                        }
                        case "axis_perp" -> {
                            if (d.getAxis() != cogAxis) {
                                // Create mod rule: neighbor's functional axis parallel to d = shaft continuation, not gear mesh
                                try {
                                    BlockPos neighborPosInst = this.getMachinePos().relative(d);
                                    ImmutableBlockState neighborCsInst = BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(neighborPosInst)).orElse(null);
                                    if (neighborCsInst != null) {
                                        Direction.Axis neighborFuncAxisInst = getBlockFunctionalAxis(neighborCsInst);
                                        if (neighborFuncAxisInst != null && neighborFuncAxisInst == d.getAxis()) {
                                            yield false;
                                        }
                                    }
                                } catch (Throwable ignored) {}
                                yield true;
                            }
                            yield false;
                        }
                        default -> false;
                    }))) continue;
                    return true;
                }
                HashSet<String> nonAxis = new HashSet<String>(raw);
                nonAxis.remove("axis_pos");
                nonAxis.remove("axis_neg");
                nonAxis.remove("axis_perp");
                if (!nonAxis.isEmpty()) {
                    return DataMachineBlockEntity.rpmFacesContain(nonAxis, d, facing);
                }
                return false;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return DataMachineBlockEntity.rpmFacesContain(raw, d, facing);
    }

    @Override
    public float getPower() {
        return this.inputRpm;
    }

    @Override
    public void reportStressLoad(float su) {
        for (RpmProvider m : this.activeMotors) {
            m.reportStressLoad(su);
        }
        if (this.activeMotors.isEmpty() && this.activeMotor != null) {
            this.activeMotor.reportStressLoad(su);
        }
    }

    public DataMachineBlockEntity(BlockEntity blockEntity, MachineDefinition definition) {
        super(blockEntity, definition.menuSize());
        FluidType filter;
        this.definition = definition;
        for (MachineDefinition.TankSpec spec : definition.fluidTanks()) {
            filter = spec.filter() == null ? null : FluidType.REGISTRY.get(spec.filter());
            this.addFluidTank(filter == null ? new FluidTank(spec.name(), spec.capacity()) : new FluidTank(spec.name(), spec.capacity(), filter));
        }
        for (MachineDefinition.TankSpec spec : definition.gasTanks()) {
            GasType gasFilter = spec.filter() == null ? null : GasType.REGISTRY.get(spec.filter());
            this.addGasTank(gasFilter == null ? new GasTank(spec.name(), spec.capacity()) : new GasTank(spec.name(), spec.capacity(), gasFilter));
        }
        if (definition.io() != null) {
            this.setIOConfiguration(definition.io());
        }
        if (!definition.energy().isEmpty()) {
            this.configureEnergy(definition.energy().capacity(), definition.energy().maxInput(),
                    definition.energy().maxOutput(), definition.energy().perTick());
        }
        if (!definition.renderers().isEmpty()) {
            this.rendererManager = new RendererManager(definition.renderers(), definition.variables());
            int n = definition.renderers().size();
            this.specDisplays = new ConveyorItemDisplay[n];
            this.specDisplayHashes = new int[n];
        }
        INSTANCES.add(this);
    }

    private void ensureRenderer() {
        if (this.rendererManager == null) {
            MachineDefinition eff;
            MachineDefinition fresh = MachineDefinition.REGISTRY.get(this.definition.id());
            MachineDefinition machineDefinition = eff = fresh != null ? fresh : this.definition;
            if (!eff.renderers().isEmpty()) {
                this.rendererManager = new RendererManager(eff.renderers(), eff.variables());
                int n = eff.renderers().size();
                this.specDisplays = new ConveyorItemDisplay[n];
                this.specDisplayHashes = new int[n];
            }
        }
    }

    @Override
    public RendererManager rendererManager() {
        return this.rendererManager;
    }

    public void setActiveAnimation(dev.arubik.craftengine.machine.render.ScriptAnimation anim) {
        if (this.activeAnimation != null && this.lastKnownLevel != null) {
            this.activeAnimation.clearEntities(this.lastKnownLevel);
        }
        this.activeAnimation = anim;
        if (anim != null) anim.play();
    }

    public MachineDefinition definition() {
        return this.definition;
    }

    @Override
    protected String getMachineId() {
        return this.definition.recipeType();
    }

    @Override
    public int[] getInputSlots() {
        return this.definition.inputSlots();
    }

    @Override
    public int[] getOutputSlots() {
        return this.definition.outputSlots();
    }

    @Override
    public int[] getFuelSlots() {
        return this.definition.fuelSlots();
    }

    @Override
    public int[] getUpgradeSlots() {
        return this.definition.upgrades().slots();
    }

    @Override
    protected AbstractProcessingRecipe getMatchingRecipe(Level level) {
        for (AbstractProcessingRecipe recipe : RecipeManager.getRecipes(this.getMachineId())) {
            if (this.matchingInputSlot(recipe) < 0) continue;
            return recipe;
        }
        return null;
    }

    protected int matchingInputSlot(AbstractProcessingRecipe recipe) {
        boolean wantsItem = false;
        for (RecipeInput input : recipe.getInputs()) {
            if (!DataMachineBlockEntity.isItemInput(input)) continue;
            wantsItem = true;
        }
        if (!wantsItem) {
            return this.definition.inputSlots().length > 0 ? this.definition.inputSlots()[0] : 0;
        }
        for (Object slot : this.definition.inputSlots()) {
            net.minecraft.world.item.ItemStack stack = this.getItem((int)slot);
            if (stack == null || stack.isEmpty()) continue;
            boolean all = true;
            for (RecipeInput input : recipe.getInputs()) {
                if (!DataMachineBlockEntity.isItemInput(input) || input.matches(stack) && stack.getCount() >= input.getAmount()) continue;
                all = false;
                break;
            }
            if (!all) continue;
            return (int)slot;
        }
        return -1;
    }

    private static boolean isItemInput(RecipeInput input) {
        return input instanceof ItemInput || input instanceof CraftEngineItemInput || input instanceof TagInput;
    }

    @Override
    protected boolean canFitOutput(Level level, RecipeOutput output) {
        if (!(output instanceof ItemOutput)) {
            return true;
        }
        ItemOutput itemOutput = (ItemOutput)output;
        net.minecraft.world.item.ItemStack produced = (net.minecraft.world.item.ItemStack)itemOutput.getOutput();
        for (int slot : this.definition.outputSlots()) {
            net.minecraft.world.item.ItemStack current = this.getItem(slot);
            if (current == null || current.isEmpty()) {
                return true;
            }
            if (!net.minecraft.world.item.ItemStack.isSameItem((net.minecraft.world.item.ItemStack)current, (net.minecraft.world.item.ItemStack)produced) || current.getCount() + produced.getCount() > current.getMaxStackSize()) continue;
            return true;
        }
        return false;
    }

    @Override
    protected void consumeInputs(Level level, AbstractProcessingRecipe recipe) {
        int slot = this.matchingInputSlot(recipe);
        if (slot < 0) {
            return;
        }
        for (RecipeInput input : recipe.getInputs()) {
            if (!DataMachineBlockEntity.isItemInput(input)) continue;
            this.removeItem(slot, input.getAmount());
        }
    }

    public void setMenuConfig(MachineMenuConfig config) {
        this.menuConfig = config != null ? config : MachineMenuConfig.parse(key -> null);
    }

    public void setBars(List<MachineBar> bars) {
        this.bars = bars != null ? bars : List.of();
    }

    /** How many secondary cells this machine's own multi-cell structure has (0 = ordinary single
     * block) — set by {@code DataMachineBehavior} from the block's OWN "cells" config, never from
     * this machine's JSON. Exposed to scripts via {@code Machine.is_multi_cell}/{@code cell_count}. */
    private int cellCount = 0;

    public void setCellCount(int cellCount) {
        this.cellCount = Math.max(0, cellCount);
    }

    public int cellCount() {
        return cellCount;
    }

    public List<MachineBar> getBars() { return bars; }

    /** Public accessor for current processing recipe (null if not processing). */
    public AbstractProcessingRecipe getCurrentRecipe() {
        try { return this.getMatchingRecipe(this.getNMSLevel()); } catch (Throwable ignored) { return null; }
    }

    /** Public accessor to find first recipe matching current inventory. */
    public AbstractProcessingRecipe findMatchingRecipe() {
        return getCurrentRecipe();
    }

    /** Public wrapper for getMachineId() — needed by script types. */
    public String getMachineIdPublic() { return this.getMachineId(); }

    @Override
    public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        this.set(KEY_TICKS_ALIVE, this.ticksAlive);
        super.saveCustomData(tag);
    }

    @Override
    public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
        super.loadCustomData(tag);
        Long saved = this.get(KEY_TICKS_ALIVE);
        this.ticksAlive = saved != null ? saved : 0L;
    }

    @Override
    public void setInputRpm(float rpm) {
        this.inputRpm = rpm;
    }

    @Override
    public float getInputRpm() {
        return this.inputRpm;
    }

    private boolean hasPower() {
        if (!this.definition.power().consumesStress()) {
            return true;
        }
        AbstractProcessingRecipe recipe = this.getMatchingRecipe(this.getNMSLevel());
        return recipe == null ? this.inputRpm > 0.0f : this.canProcess(this.getNMSLevel(), recipe);
    }

    // AbstractMachineBlockEntity#requiresFuel() defaults to true (a plain furnace-style machine
    // burns something). A machine whose JSON declares "flags": {"fuel": false} — the windmill,
    // solar panel — has no fuel slot and burnTime never leaves 0, so applyEnergyPerTick's
    // requiresFuel() gate (generating = burnTime > 0) would silently and permanently block energy
    // generation for exactly those "always-on" generators, without this override.
    @Override
    protected boolean requiresFuel() {
        return this.definition == null || this.definition.fuelRequired();
    }

    @Override
    protected boolean canProcess(Level level, AbstractProcessingRecipe recipe) {
        if (!super.canProcess(level, recipe)) {
            return false;
        }
        if (!this.definition.power().consumesStress()) {
            return true;
        }
        return recipe.getMinRpm() <= 0 || this.inputRpm >= (float)this.effectiveRpm(recipe);
    }

    @Override
    public void tick(Level level, BlockPos pos, ImmutableBlockState state) {
        boolean needsRpm;
        this.ensureRenderer();
        if (!level.isClientSide() && level instanceof ServerLevel) {
            ServerLevel sl;
            this.lastKnownLevel = sl = (ServerLevel)level;
        }
        boolean bl = needsRpm = this.definition.kinetics();
        if (!level.isClientSide()
                && dev.arubik.craftengine.rotation.RpmPropagation.shouldPull(
                        needsRpm, this.isRpmRelay(), this.rpmSourceActive)
                && dev.arubik.craftengine.rotation.RpmPropagation.pullDueThisTick(
                        this.stablePulls, this.ticksAlive,
                        this.rpmSourceActive || this.sourceDistance < Integer.MAX_VALUE)) {
            this.pullRotationalPower(level);
        }
        if (this.definition.runsRecipes()) {
            super.tick(level, pos, state);
            if (!level.isClientSide()) {
                if (this.ticksAlive == 0 && this.definition.onPlaceScript() != null) {
                    // First tick after placement — fire on_place hook
                    try { runScriptRef(this.definition.onPlaceScript()); } catch (Throwable ignored) {}
                }
                this.ticksAlive++;
                // Every 5 ticks: verify our source machine still exists; dissolve network if gone
                if (this.ticksAlive % 5 == 0 && rpmNetworkId() != 0L
                        && sourceDistance > 0 && activeMotor instanceof DataMachineBlockEntity src) {
                    try {
                        var srcBe = dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes.getIfLoaded(level, src.getMachinePos());
                        if (srcBe == null || srcBe.controller != src) {
                            leaveNetwork();
                            setRpmSourceOutput(0f);
                            this.activeMotor = null;
                        }
                    } catch (Throwable ignored) {}
                }
            }
            if (this.definition.power().consumesStress() && !level.isClientSide()) {
                this.reportStressLoad();
            }
            if (!level.isClientSide()) {
                // status field: null/"auto" → isProcessing() (or, for a recipe-less energy
                // generator/drain, energyActiveDefault()); "{file}.pf:{func}" → script bool
                boolean active = this.isProcessing() || this.energyActiveDefault();
                String statusRef = this.definition != null ? this.definition.statusScript() : null;
                if (statusRef != null && statusRef.contains(".pf:")) {
                    try {
                        dev.arubik.craftengine.script.ScriptContext sctx = this.buildScriptContext();
                        if (sctx != null) {
                            String val = evalPfFuncStr(statusRef, sctx);
                            active = !"false".equalsIgnoreCase(val) && !"0".equals(val) && !val.isEmpty();
                        }
                    } catch (Throwable ignored) {}
                }
                this.maybeUpdateActivated(level, pos, state, active);
            }
        } else if (!level.isClientSide()) {
            // Machines with recipes off skip super.tick(), and super.tick() is the only place the
            // open MachineMenu is ticked. Without this the GUI stays the snapshot taken when the
            // page was opened: bars and script-driven layout icons never re-render, so gas gauges
            // look frozen and deplete_gas / rpm / su buttons appear dead even though they ran.
            // Kept in this branch only, so the recipe path does not tick the menu twice.
            if (this.menu != null && this.definition.tickUi()) {
                this.menu.tick();
            }
            this.ticksAlive++;
            this.setChanged();
            // Recipe-less generators (the windmill/solar panel) never take the processTick() path
            // above — without this, energyActiveDefault() would show them as "active" while the
            // buffer silently never actually grew.
            this.applyEnergyPerTick();
            // Recipe-less machines still need gas/fluid/ENERGY pull — this used to only fire for
            // machines with a gas tank (written back when gas was the only resource a recipe-less
            // machine could receive this way), silently skipping it for every recipe-less energy
            // machine (energy_cell, energy_generator, solar_panel, windmill, ...) even after
            // AbstractMachineBlockEntity grew pullEnergyInto — the whole "solar panel stacked
            // directly on an energy cell" fix was dead code without this. Also no longer requires
            // this.ioConfiguration != null — pullFromInputFaces itself now falls back to the
            // block's default IO config, so a machine that never had its side panel touched still
            // pulls via whatever's open by default, exactly like every other IO reader already does.
            if (this.definition.ioPull()
                    && (!this.gasTanks.isEmpty() || !this.fluidTanks.isEmpty() || this.getEnergyCapacityForCarrier() > 0)) {
                this.pullFromInputFaces(level);
            }
            // noProcessing machines still need status evaluation for blockstate updates
            String statusRef = this.definition != null && this.definition.scripts() ? this.definition.statusScript() : null;
            if (statusRef != null && statusRef.contains(".pf:")) {
                try {
                    dev.arubik.craftengine.script.ScriptContext sctx = this.buildScriptContext();
                    if (sctx != null) {
                        String val = evalPfFuncStr(statusRef, sctx);
                        boolean active = !"false".equalsIgnoreCase(val) && !"0".equals(val) && !val.isEmpty();
                        this.maybeUpdateActivated(level, pos, state, active);
                    }
                } catch (Throwable ignored) {}
            } else {
                // No script override: a recipe-less machine (a generator/passive-drain like the
                // energy windmill) still needs SOME "auto" default — energyActiveDefault() is that
                // default here, the same way isProcessing() is the default in the recipe branch above.
                this.maybeUpdateActivated(level, pos, state, this.energyActiveDefault());
            }
        }
        if (this.rendererManager != null && this.definition.tickRenderers()) {
            try {
                float f;
                int n = 0;
                LinkedHashMap<String, double[]> fluidTankData = new LinkedHashMap<String, double[]>();
                for (MachineDefinition.TankSpec tankSpec : this.definition.fluidTanks()) {
                    FluidTank fluidTank = this.fluidTank(tankSpec.name());
                    if (fluidTank == null) continue;
                    FluidStack stored = fluidTank.getFluid(this.getNMSLevel(), this.getMachinePos());
                    fluidTankData.put(tankSpec.name(), new double[]{stored.getAmount(), fluidTank.getCapacity()});
                }
                LinkedHashMap<String, double[]> gasTankData = new LinkedHashMap<String, double[]>();
                for (MachineDefinition.TankSpec tankSpec : this.definition.gasTanks()) {
                    GasTank tank = this.gasTank(tankSpec.name());
                    if (tank == null) continue;
                    GasStack stored = tank.getGas(this.getNMSLevel(), this.getMachinePos());
                    gasTankData.put(tankSpec.name(), new double[]{stored.getAmount(), tank.getCapacity()});
                }
                LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
                if (!this.upgradeDefs.isEmpty()) {
                    for (int upSlot : this.definition.upgrades().slots()) {
                        net.minecraft.world.item.ItemStack nmsItem = this.getItem(upSlot);
                        Key uid = this.upgradeItemId(nmsItem);
                        if (uid == null) continue;
                        linkedHashMap.merge(uid.namespace() + ":" + uid.value(), 1, Integer::sum);
                    }
                }
                boolean bl2 = false;
                try {
                    n = level.getBestNeighborSignal(pos);
                }
                catch (Throwable tank) {
                    // empty catch block
                }
                MachineRenderContext ctx = new MachineRenderContext(this.inputRpm, this.overclock, this.curFuelEff, this.progress, this.maxProgress, this.curGeneration, this.isProcessing(), this.inputRpm > 0.0f, this.isOverclocked(), this.burnTime > 0, null, linkedHashMap, fluidTankData, gasTankData, n);
                Direction facing = this.getFacing(level);
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
                ScriptContext machineScriptCtx = this.buildScriptContext();
                if (machineScriptCtx != null) {
                    ctx = ctx.augmented(machineScriptCtx);
                }
                this.rendererManager.tick(ctx, (ServerLevel)level, pos.getX(), pos.getY(), pos.getZ(), yaw);
                this.tickSpecDisplays((ServerLevel)level, pos);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (this.definition != null && this.definition.scripts() && this.definition.actionScript() != null && ++this.actionTickCounter >= this.definition.actionInterval()) {
            this.actionTickCounter = 0;
            this.runActionScript(this.definition.actionScript());
        }
        // Deliver this tick's RPM to neighbours that cannot pull for themselves. Machine-to-machine
        // links resolve by pull (pullRotationalPower), but plain RpmConsumers — conveyors, bearings,
        // movers — never declare an io.rpm.input face and so would never see a source. Driven purely
        // by the declared io.rpm output faces, so set_rpm_output + io config is all a machine needs.
        if (!level.isClientSide() && this.rpmSourceActive) {
            this.pushRotationalPower(level);
        }
        // Tick script animation if one is active
        if (this.activeAnimation != null && this.definition.animations() && level instanceof ServerLevel sl) {
            try {
                dev.arubik.craftengine.script.ScriptContext animCtx = buildScriptContext();
                this.activeAnimation.tickOn(sl, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, animCtx);
                if (!this.activeAnimation.isPlaying()) this.activeAnimation = null;
            } catch (Throwable ignored) {}
        }
    }

    @Override
    public void onRemove() {
        // Fire on_break hook before teardown, with a cancellable/adjustable BreakEvent pre-filled
        // with this machine's own container contents as the DEFAULT drops — a script that declares
        // on_break for an unrelated reason (a sound effect, a stat counter, ...) doesn't have to
        // also re-specify drops just to keep today's behavior; only a script that actually calls
        // event.set_drops(...) changes what lands on the ground, and event.cancel() suppresses
        // dropping anything at all. Replaces the old unconditional dropAllContents call — see
        // MachineBreakListener, which skips that call whenever on_break is declared, leaving this
        // event-driven path as the single source of truth for what a broken machine drops.
        if (this.definition != null && this.definition.onBreakScript() != null) {
            try {
                List<net.minecraft.world.item.ItemStack> defaultDrops = new java.util.ArrayList<>();
                for (int i = 0; i < this.getContainerSize(); i++) {
                    net.minecraft.world.item.ItemStack it = this.getItem(i);
                    if (it != null && !it.isEmpty()) defaultDrops.add(it.copy());
                }
                dev.arubik.craftengine.script.event.BreakEvent breakEvent =
                        new dev.arubik.craftengine.script.event.BreakEvent(defaultDrops);
                ScriptContext base = this.buildScriptContext();
                if (base != null) {
                    dev.arubik.craftengine.script.ScriptCall call =
                            dev.arubik.craftengine.script.ScriptCall.parse(this.definition.onBreakScript());
                    if (call != null) {
                        ScriptContext ctx = ScriptContext.builder().copyFrom(base).event(breakEvent).build();
                        call.execute(ctx);
                    }
                }
                if (!breakEvent.isCancelled() && this.lastKnownLevel != null) {
                    net.minecraft.core.BlockPos pos = this.getMachinePos();
                    for (net.minecraft.world.item.ItemStack drop : breakEvent.drops()) {
                        if (drop == null || drop.isEmpty()) continue;
                        net.minecraft.world.entity.item.ItemEntity spawned = new net.minecraft.world.entity.item.ItemEntity(
                                this.lastKnownLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                        this.lastKnownLevel.addFreshEntity(spawned);
                    }
                }
            } catch (Throwable ignored) {}
        }
        if (this.activeAnimation != null && this.lastKnownLevel != null) {
            try { this.activeAnimation.clearEntities(this.lastKnownLevel); } catch (Throwable ignored) {}
            this.activeAnimation = null;
        }
        super.onRemove();
        this.despawnSpecDisplays();
        if (this.rendererManager != null) {
            this.rendererManager.close();
            this.rendererManager = null;
        }
    }

    @Override
    public void unregister() {
        // Leave RpmNetwork first so the network dissolves and notifies remaining members
        if (rpmNetworkId() != 0L) leaveNetwork();
        if (this.inputRpm != 0.0f || this.sourceDistance < Integer.MAX_VALUE) {
            this.inputRpm = 0.0f;
            this.sourceDistance = Integer.MAX_VALUE;
            this.activeMotor = null;
            this.activeMotors.clear();
            try {
                Level level = this.getNMSLevel();
                BlockPos pos = this.getMachinePos();
                if (level != null && pos != null) {
                    for (Direction d : Direction.values()) {
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
        }
        super.unregister();
        this.despawnSpecDisplays();
        if (this.rendererManager != null) {
            this.rendererManager.close();
            this.rendererManager = null;
        }
    }

    public void despawnSpecDisplays(ServerLevel level) {
        if (this.specDisplays == null) {
            return;
        }
        try {
            for (ConveyorItemDisplay d : this.specDisplays) {
                if (d == null) continue;
                d.despawnAll(level);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.specDisplays = null;
        this.specDisplayHashes = null;
    }

    private void despawnSpecDisplays() {
        ServerLevel nmsLevel = this.lastKnownLevel;
        if (nmsLevel == null) {
            try {
                nmsLevel = (ServerLevel)this.getNMSLevel();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (nmsLevel != null) {
            this.despawnSpecDisplays(nmsLevel);
        } else {
            this.specDisplays = null;
            this.specDisplayHashes = null;
        }
    }

    private void tickSpecDisplays(ServerLevel nmsLevel, BlockPos pos) {
        block11: {
            if (this.rendererManager == null || this.specDisplays == null) {
                return;
            }
            try {
                CEWorld ceWorld = this.blockEntity().world();
                if (ceWorld == null) {
                    if (SPEC_DISPLAY_DEBUG) {
                        System.out.println("[CEP specDisplay] ceWorld null at " + String.valueOf(pos));
                    }
                    return;
                }
                net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(pos.getX(), pos.getY(), pos.getZ());
                List<Player> viewers = ceWorld.world().getTrackedBy(new ChunkPos(cePos));
                List<RendererSpec> specs = this.rendererManager.specs();
                net.minecraft.world.item.ItemStack[] items = this.rendererManager.currentItems();
                if (SPEC_DISPLAY_DEBUG) {
                    System.out.println("[CEP specDisplay] pos=" + String.valueOf(pos) + " viewers=" + viewers.size() + " specs=" + specs.size());
                }
                for (int i = 0; i < specs.size(); ++i) {
                    RendererSpec rendererSpec = specs.get(i);
                    if (!(rendererSpec instanceof RendererSpec.ItemDisplaySpec)) continue;
                    RendererSpec.ItemDisplaySpec idSpec = (RendererSpec.ItemDisplaySpec)rendererSpec;
                    RendererManager.EvalResult er = this.rendererManager.evalResult(i);
                    net.minecraft.world.item.ItemStack item = items[i];
                    if (SPEC_DISPLAY_DEBUG) {
                        System.out.println("[CEP specDisplay]  spec[" + i + "] active=" + String.valueOf(er != null ? Boolean.valueOf(er.active) : "null") + " item=" + String.valueOf(item != null ? item.getItem() : "null") + " itemDisplay=" + String.valueOf(er != null ? er.itemDisplay : "null"));
                    }
                    if (er != null && er.active && item != null && !item.isEmpty() && er.itemDisplay != null) {
                        if (this.specDisplays[i] == null) {
                            this.specDisplays[i] = new ConveyorItemDisplay();
                        }
                        RendererSpec.EvaluatedItemDisplay eid = er.itemDisplay;
                        double wx = (double)pos.getX() + 0.5 + eid.offsetX();
                        double wy = (double)pos.getY() + eid.offsetY();
                        double wz = (double)pos.getZ() + 0.5 + eid.offsetZ();
                        Quaternionf q = new Quaternionf().rotateY((float)Math.toRadians(eid.rotY())).rotateX((float)Math.toRadians(eid.rotX())).rotateZ((float)Math.toRadians(eid.rotZ()));
                        // Light rarely changes tick-to-tick — the 6-neighbor brightness scan this
                        // does is real per-tick CPU for every active display; only pay for it every
                        // LIGHT_CHECK_INTERVAL ticks.
                        boolean lightChanged = this.ticksAlive % LIGHT_CHECK_INTERVAL == 0
                                && this.specDisplays[i].setLightFromLevel(nmsLevel, wx, wy, wz);
                        this.specDisplays[i].setScale(eid.scale());
                        this.specDisplays[i].setRotation(q);
                        // Always consumed so it never accumulates stale, but a continuous spin
                        // formula (tick()*rpm*...) marks this dirty EVERY tick — only actually flush
                        // a metadata packet for that case every ROTATION_PACKET_INTERVAL ticks
                        // (matching the widened interpolation duration in ConveyorItemDisplay), the
                        // client smooths across the gap instead of needing a packet every tick.
                        boolean rotDirty = this.specDisplays[i].consumeRotationDirty();
                        boolean sendRotUpdate = rotDirty && this.ticksAlive % ROTATION_PACKET_INTERVAL == 0;
                        int h = item.hashCode();
                        boolean itemChanged = h != this.specDisplayHashes[i];
                        this.specDisplays[i].setNmsItem(item);
                        List<Player> effectiveViewers = viewers;
                        if (er.qualifyingPlayers != null) {
                            effectiveViewers = viewers.stream().filter(v -> {
                                org.bukkit.entity.Player bukkit;
                                Object pp = v.platformPlayer();
                                return pp instanceof org.bukkit.entity.Player && er.qualifyingPlayers.contains((bukkit = (org.bukkit.entity.Player)pp).getUniqueId());
                            }).collect(Collectors.toList());
                        }
                        this.specDisplays[i].render(effectiveViewers, wx, wy, wz, lightChanged || itemChanged || sendRotUpdate);
                        this.specDisplayHashes[i] = h;
                        continue;
                    }
                    if (this.specDisplays[i] == null) continue;
                    this.specDisplays[i].despawnAll(nmsLevel);
                    this.specDisplays[i] = null;
                    this.specDisplayHashes[i] = 0;
                }
            }
            catch (Throwable e) {
                if (!SPEC_DISPLAY_DEBUG) break block11;
                System.out.println("[CEP specDisplay] EXCEPTION: " + String.valueOf(e));
                e.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidateRpm(Level level) {
        if (this.invalidating) {
            return;
        }
        if (this.definition == null) {
            return;
        }
        if (!this.definition.kinetics()) {
            return;
        }
        if (this.rpmSourceActive) {
            return;
        }
        this.invalidating = true;
        try {
            BlockPos pos;
            // The world changed around us: drop back to scanning every tick immediately.
            this.stablePulls = 0;
            float before = this.inputRpm;
            this.pullRotationalPower(level);
            if (before != this.inputRpm && (pos = this.getMachinePos()) != null) {
                for (Direction d : Direction.values()) {
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
                BlockState bs = level.getBlockState(pos);
                level.updateNeighborsAt(pos, bs.getBlock());
            }
        }
        finally {
            this.invalidating = false;
        }
    }

    private void pullRotationalPower(Level level) {
        Object p2;
        boolean hasRpmInput;
        this.activeMotor = null;
        this.activeMotors.clear();
        float bestPotential = 0.0f;
        float delivered = 0.0f;
        net.momirealms.craftengine.core.world.BlockPos cePos = new net.momirealms.craftengine.core.world.BlockPos(this.getMachinePos().getX(), this.getMachinePos().getY(), this.getMachinePos().getZ());
        EnumSet<Direction> autoFaces = null;
        try {
            BlockState bs = level.getBlockState(this.getMachinePos());
            ImmutableBlockState cs = BlockStateUtils.getOptionalCustomBlockState(bs).orElse(null);
            if (cs != null) {
                Property shaftProp = ((BlockDefinition)cs.owner().value()).getProperty("axis");
                Property gearProp = ((BlockDefinition)cs.owner().value()).getProperty("gear_axis");
                if (shaftProp != null) {
                    String v;
                    autoFaces = switch (v = String.valueOf(cs.get(shaftProp)).toLowerCase()) {
                        case "x" -> EnumSet.of(Direction.EAST, Direction.WEST);
                        case "y" -> EnumSet.of(Direction.UP, Direction.DOWN);
                        default -> EnumSet.of(Direction.NORTH, Direction.SOUTH);
                    };
                }
            }
        }
        catch (Throwable bs) {
            // empty catch block
        }
        EnumSet<Direction> autoFacesFinal = autoFaces;
        boolean bl = hasRpmInput = autoFacesFinal != null || this.definition != null && !this.definition.rpmInputFacesRaw().isEmpty();
        if (!hasRpmInput) {
            // A source (set_rpm_output) declares no rpm INPUT face, so it lands here every tick.
            // Resetting its sourceDistance to MAX_VALUE would make it invisible to neighbours
            // that tick before its action script re-stamps distance 0 at the end of the tick —
            // block-entity iteration order is arbitrary, so that raced. Leave a live source alone.
            if (!this.rpmSourceActive) {
                this.inputRpm = 0.0f;
                this.sourceDistance = Integer.MAX_VALUE;
            }
            return;
        }
        int bestSourceDist = Integer.MAX_VALUE;
        Direction bestInputFace = null;
        long bestStamp = -1L;
        boolean sawConflict = false;
        for (Direction d : Direction.values()) {
            float pot;
            int providerDist;
            BlockEntityController blockEntityController;
            if (this.definition == null || this.definition.rpmInputFacesRaw().isEmpty() ? autoFacesFinal != null && !autoFacesFinal.contains(d) : !this.rpmFacesContainWithAxis(this.definition.rpmInputFacesRaw(), d, this.getFacing(level), level)) continue;
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(d));
            if (be == null || !((blockEntityController = be.controller) instanceof RpmProvider)) continue;
            RpmProvider p = (RpmProvider)blockEntityController;
            if (p instanceof DataMachineBlockEntity) {
                Set<String> perpFilter;
                DataMachineBlockEntity dm = (DataMachineBlockEntity)p;
                providerDist = dev.arubik.craftengine.rotation.RpmPropagation.providerDistance(
                        dm.isRpmSource(), dm.isRpmRelay(), dm.sourceDistance);
                if (!dev.arubik.craftengine.rotation.RpmPropagation.canPullFrom(providerDist, this.sourceDistance)
                        || !dm.isValidOutputFace(d.getOpposite(), level)) continue;
                if (!dm.definition.rpmOutputBlockFilter().isEmpty() && (perpFilter = dm.definition.rpmOutputBlockFilter().get("axis_perp")) != null) {
                    boolean weAreAllowed;
                    boolean bl2 = weAreAllowed = this.definition != null && this.definition.rpmInputFacesRaw().contains("axis_perp");
                    if (!weAreAllowed) {
                        continue;
                    }
                }
            } else {
                providerDist = 0;
            }
            if (this.definition != null && !this.definition.rpmInputFacesRaw().isEmpty()) {
                Direction myFacing = this.getFacing(level);
                if (!this.rpmFacesContainWithAxis(this.definition.rpmInputFacesRaw(), d, myFacing, level)) {
                    continue;
                }
            } else if (p instanceof DataMachineBlockEntity) {
                DataMachineBlockEntity dm2 = (DataMachineBlockEntity)p;
                if (dm2.definition != null) {
                    boolean providerOutputsAxisPerp;
                    boolean bl3 = providerOutputsAxisPerp = dm2.definition.rpmOutputInvertedRaw().contains("axis_perp") || dm2.definition.rpmOutputFacesRaw().contains("axis_perp");
                    if (providerOutputsAxisPerp) {
                        boolean weAcceptAxisPerp;
                        boolean bl4 = weAcceptAxisPerp = this.definition != null && this.definition.rpmInputFacesRaw().contains("axis_perp");
                        if (!weAcceptAxisPerp) continue;
                    }
                }
            }
            if ((pot = p.potentialRpm()) <= 0.0f || !p.rpmReaches(cePos)) continue;
            float raw = p.getRpm();
            if (p instanceof DataMachineBlockEntity) {
                Set<String> inv2;
                DataMachineBlockEntity dm2 = (DataMachineBlockEntity)p;
                if (dm2.definition != null && dm2.definition.rpmOutputRelative()) {
                    // Gearbox: the sign follows which face is actually driven, exactly as Create's
                    // RotationPropagator.getAxisModifier does. See RpmPropagation#gearboxModifier.
                    Direction providerIn = dm2.rpmInputFace();
                    Direction outFace = d.getOpposite();
                    if (providerIn != null) {
                        raw *= dev.arubik.craftengine.rotation.RpmPropagation.gearboxModifier(
                                axisIndex(outFace), axisSign(outFace),
                                axisIndex(providerIn), axisSign(providerIn));
                    }
                } else {
                    Set<String> set = inv2 = dm2.definition != null ? dm2.definition.rpmOutputInvertedRaw() : Set.of();
                    if (!inv2.isEmpty() && DataMachineBlockEntity.rpmFacesContain(inv2, d.getOpposite(), dm2.getFacing(level))) {
                        raw = dev.arubik.craftengine.rotation.RpmPropagation.applyInversion(raw, true);
                    }
                }
            }
            // Two independent sources fighting over one shaft is a build error worth breaking.
            // A TRANSIENT disagreement is not: changing a motor's speed reaches the blocks around
            // it over several ticks, so mid-change one neighbour still holds the old value. Feeds
            // that carry the same source stamp are exempt outright, and anything else has to hold
            // for CONFLICT_TICKS_BEFORE_BREAK before it costs the player a block.
            long candidateStamp = p instanceof DataMachineBlockEntity pdm ? pdm.rpmSourceStamp() : -1L;
            if (dev.arubik.craftengine.rotation.RpmPropagation.isConflict(delivered, raw, bestStamp, candidateStamp)
                    && !p.isRpmSource() && this.activeMotor != null && !this.activeMotor.isRpmSource()) {
                sawConflict = true;
                if (dev.arubik.craftengine.rotation.RpmPropagation.conflictShouldBreak(this.rpmConflictTicks + 1)) {
                    try {
                        if (bestPotential < pot) {
                            level.destroyBlock(this.getMachinePos(), true);
                            return;
                        }
                        level.destroyBlock(this.getMachinePos().relative(d), true);
                        continue;
                    }
                    catch (Throwable dm2) {
                        // empty catch block
                    }
                }
                // Not long enough yet: ignore this feed for now and let the build settle.
                continue;
            }
            if (providerDist < bestSourceDist || providerDist == bestSourceDist && pot > bestPotential) {
                bestSourceDist = providerDist;
                bestPotential = pot;
                bestInputFace = d;
                bestStamp = p instanceof DataMachineBlockEntity pd ? pd.rpmSourceStamp() : level.getGameTime();
                delivered = raw;
                this.activeMotor = p;
                this.activeMotors.clear();
                this.activeMotors.add(p);
                continue;
            }
            if (providerDist != bestSourceDist || pot != bestPotential || raw == 0.0f || Math.signum(raw) != Math.signum(delivered)) continue;
            this.activeMotors.add(p);
        }
        // NOTE: the diagonal cogwheel meshing that used to sit here (large<->small in-plane
        // diagonals and large<->large cross-axis diagonals) has been removed. It was dead code:
        // it required "axis_perp" in a machine's rpm input faces, which no machine JSON ever
        // declared, so large cogs never meshed at all. Cog meshing is script-driven now, the way
        // small<->small already worked — see cogwheel.pf and Machine.relay_to.
        // Stability drives the scan backoff: a result identical to last time earns a longer
        // interval, anything different drops straight back to scanning every tick.
        if (delivered == this.inputRpm && bestInputFace == this.rpmInputFace
                && dev.arubik.craftengine.rotation.RpmPropagation.distanceAfterPull(bestSourceDist)
                        == this.sourceDistance) {
            if (this.stablePulls < Integer.MAX_VALUE) this.stablePulls++;
        } else {
            this.stablePulls = 0;
        }

        // A conflict has to be seen on consecutive scans to count; one clean scan forgives it.
        this.rpmConflictTicks = sawConflict ? this.rpmConflictTicks + 1 : 0;

        boolean foundSource = bestSourceDist < Integer.MAX_VALUE;
        boolean stillFresh = dev.arubik.craftengine.rotation.RpmPropagation.isFresh(
                this.rpmSourceStamp, level.getGameTime());

        if (foundSource) {
            this.inputRpm = delivered;
            this.rpmInputFace = bestInputFace;
            // Carry the SOURCE's stamp, not our own tick: freshness must expire chain-wide at once.
            this.rpmSourceStamp = bestStamp;
            this.sourceDistance =
                    dev.arubik.craftengine.rotation.RpmPropagation.distanceAfterPull(bestSourceDist);
        } else if (this.isRpmRelay() && stillFresh) {
            // Found nothing, but a neighbour handed us a value that has not expired yet. Cogwheels
            // mesh ACROSS their axis while their rpm input faces run ALONG it, so a meshed cog can
            // never rediscover its driver by pulling. Keep the whole reading — value, face AND
            // distance: zeroing the distance here broke every chain past the first mesh, because
            // the next hop then saw its driver as infinitely far away.
        } else {
            this.inputRpm = 0.0f;
            this.rpmInputFace = null;
            this.sourceDistance = Integer.MAX_VALUE;

            // Nothing upstream and nothing fresh: this relay is DEAD.
            //
            // getRpm() returns rpmSourceOutput while rpmSourceActive is true, so a relay that kept
            // that flag re-read its OWN last output and re-emitted it forever: cut the motor away
            // from a pair of cogwheels and they carried on spinning off each other's stale value.
            if (this.isRpmRelay() && this.rpmSourceActive) {
                this.rpmSourceActive = false;
                this.rpmSourceOutput = 0.0f;
                this.theoreticalSpeed = 0.0f;
                this.rpmConflictTicks = 0;
                this.rpmSourceStamp = -1L;
                this.leaveNetwork();
            }
        }

        this.syncNetworkWithSource();
    }

    /**
     * Join whatever kinetic network the machine we are pulling from belongs to.
     *
     * <p>Without this a source's network only ever contained the source itself: joinNetwork was
     * reached from set_rpm_output/relay_to but never from the pull path, so a consumer's
     * report_su landed in its own empty network and is_overstressed was meaningless for the
     * whole chain.
     */
    private void syncNetworkWithSource() {
        if (this.rpmSourceActive) return;   // we are the source; our own network is authoritative
        if (!(this.activeMotor instanceof dev.arubik.craftengine.rotation.KineticMember source)) {
            if (this.rpmNetworkId != 0L && this.sourceDistance == Integer.MAX_VALUE) {
                this.leaveNetwork();
            }
            return;
        }
        long netId = source.rpmNetworkId();
        if (netId == 0L) {
            dev.arubik.craftengine.rotation.RpmNetwork created = dev.arubik.craftengine.rotation.RpmNetwork.create();
            source.joinNetwork(created.id());
            netId = created.id();
        }
        if (this.rpmNetworkId != netId) {
            this.joinNetwork(netId);
        }
    }

    /**
     * Push {@link #rpmSourceOutput} out through every face allowed by {@code io.rpm}.
     *
     * <p>Restores the behaviour of the removed {@code DataMotorBlockEntity#transferToHead}, but
     * face selection comes from the machine definition rather than being hardcoded to {@code facing},
     * so {@code "output_same": ["front"]}, {@code ["back"]}, multiple faces and inverted faces all
     * work. Other {@link DataMachineBlockEntity}s are skipped: they pull, and pushing to them too
     * would fight {@code pullRotationalPower} over {@code inputRpm}.
     */
    private void pushRotationalPower(Level level) {
        if (this.definition == null || !this.definition.kinetics()) return;
        for (Direction d : Direction.values()) {
            if (!this.isValidOutputFace(d, level)) continue;
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, this.getMachinePos().relative(d));
            if (be == null) continue;
            BlockEntityController c = be.controller;
            if (c instanceof DataMachineBlockEntity) continue;
            if (c instanceof RpmConsumer consumer) {
                consumer.setInputRpm(this.rpmSourceOutput);
            }
        }
    }

    private void reportStressLoad() {
        int demand;
        AbstractProcessingRecipe recipe = this.getMatchingRecipe(this.getNMSLevel());
        int n = demand = recipe != null ? this.effectiveSu(recipe) : 0;
        if (demand > 0) {
            this.lastSuLoad = demand;
            this.stressGrace = this.definition.power().stressGraceTicks();
        }
        if (this.stressGrace > 0 && this.lastSuLoad > 0) {
            for (RpmProvider m : this.activeMotors) {
                m.reportStressLoad(this.lastSuLoad);
            }
            if (this.activeMotors.isEmpty() && this.activeMotor != null) {
                this.activeMotor.reportStressLoad(this.lastSuLoad);
            }
            --this.stressGrace;
        }
    }

    // infoIcon() removed — info display is now fully script-driven via layout items with recipe_info.pf/gas_info.pf

    private String barSource(String barId) {
        for (MachineDefinition.BarRef ref : this.definition.bars()) {
            if (!ref.bar().value().equals(barId)) continue;
            return ref.source();
        }
        return barId;
    }

    private FluidTank fluidTank(String name) {
        for (FluidTank t : this.fluidTanks) {
            if (!t.getName().equalsIgnoreCase(name)) continue;
            return t;
        }
        return this.fluidTanks.isEmpty() ? null : (FluidTank)this.fluidTanks.get(0);
    }

    private GasTank gasTank(String name) {
        for (GasTank t : this.gasTanks) {
            if (!t.getName().equalsIgnoreCase(name)) continue;
            return t;
        }
        return this.gasTanks.isEmpty() ? null : (GasTank)this.gasTanks.get(0);
    }

    @Override
    public double[] barStat(String id) {
        String source = this.barSource(id);
        if (source.startsWith("fluid:") || source.equals("fluid")) {
            FluidTank tank = this.fluidTank(source.startsWith("fluid:") ? source.substring(6) : "");
            if (tank == null) {
                return new double[]{0.0, 0.0};
            }
            return new double[]{tank.getFluid(this.getNMSLevel(), this.getMachinePos()).getAmount(), tank.getCapacity()};
        }
        if (source.startsWith("gas:") || source.equals("gas")) {
            GasTank tank = this.gasTank(source.startsWith("gas:") ? source.substring(4) : "");
            if (tank == null) {
                return new double[]{0.0, 0.0};
            }
            return new double[]{tank.getGas(this.getNMSLevel(), this.getMachinePos()).getAmount(), tank.getCapacity()};
        }
        if (source.equals("fuel")) {
            return new double[]{this.burnTime, Math.max(1, this.maxBurnTime)};
        }
        if (source.equals("energy")) {
            return new double[]{this.energy, Math.max(1, this.energyCapacity)};
        }
        if (source.equals("progress")) {
            return new double[]{this.getProgress(), Math.max(1, this.getMaxProgress())};
        }
        if (source.equals("rpm") || source.equals("power")) {
            // For source motors (rpmSourceActive): show actual RPM / max (512).
            // For consumers: on/off (has power or is processing).
            if (this.rpmSourceActive) {
                return new double[]{Math.abs(this.inputRpm), 512.0};
            }
            return new double[]{this.isProcessing() || this.hasPower() ? 100.0 : 0.0, 100.0};
        }
        if (source.equals("overclock")) {
            // Normalized 0..1 over range (-1 → limit+1), so bars can map to icon states
            double limit = this.curOverclockLimit;
            double range = Math.max(1.0, limit + 1.0);
            return new double[]{this.overclock + 1.0, range};
        }
        // {file}.pf:{func} — execute function and use returned [value, max] or single value (0-1 fraction → *100)
        if (source.contains(".pf:")) {
            try {
                int colon = source.indexOf(':');
                String scriptFile = source.substring(0, colon);
                String funcName = source.substring(colon + 1);
                String lookupKey = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
                dev.arubik.craftengine.script.ScriptProgram prog = dev.arubik.craftengine.script.ScriptRegistry.get(lookupKey);
                if (prog != null) {
                    dev.arubik.craftengine.script.ScriptContext baseCtx = this.buildScriptContext();
                    if (baseCtx != null) {
                        dev.arubik.craftengine.script.ScriptContext withDefs = prog.evaluate(baseCtx);
                        dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(funcName);
                        if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                                && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                            dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                            dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(withDefs);
                            fn.executor().accept(withDefs, rb);
                            dev.arubik.craftengine.script.ScriptValue result = rb.build().getVar("__return__");
                            if (result instanceof dev.arubik.craftengine.script.ScriptValue.Array arr && arr.elements().size() >= 2) {
                                return new double[]{arr.elements().get(0).asNum(), arr.elements().get(1).asNum()};
                            }
                            return new double[]{result.asNum() * 100.0, 100.0};
                        }
                    }
                }
            } catch (Throwable ignored) {}
            return new double[]{0.0, 100.0};
        }
        return super.barStat(id);
    }

    @Override
    public Map<String, String> barPlaceholders(String id) {
        String source = this.barSource(id);
        if (source.equals("rpm") || source.equals("power")) {
            HashMap<String, String> out = new HashMap<String, String>();
            if (this.rpmSourceActive) {
                out.put("rpm", String.valueOf((int)Math.abs(this.rpmSourceOutput)));
                out.put("req", String.valueOf((int)Math.abs(this.rpmSourceOutput)));
                float suVal = 0f;
                dev.arubik.craftengine.rotation.RpmNetwork net = dev.arubik.craftengine.rotation.RpmNetwork.get(this.rpmNetworkId);
                if (net != null) suVal = Math.abs(net.totalCapacity());
                out.put("su", String.valueOf((int)suVal));
            } else {
                AbstractProcessingRecipe recipe = this.getMatchingRecipe(this.getNMSLevel());
                out.put("rpm", String.valueOf((int)this.getInputRpm()));
                out.put("req", String.valueOf(recipe != null ? this.effectiveRpm(recipe) : 0));
                out.put("su", String.valueOf(recipe != null ? this.effectiveSu(recipe) : 0));
            }
            return out;
        }
        return super.barPlaceholders(id);
    }

    @Override
    public String barSubtype(String id) {
        String source = this.barSource(id);
        if (source.startsWith("fluid:") || source.equals("fluid")) {
            FluidTank tank = this.fluidTank(source.startsWith("fluid:") ? source.substring(6) : "");
            if (tank == null) {
                return "";
            }
            FluidStack stored = tank.getFluid(this.getNMSLevel(), this.getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(Locale.ROOT);
        }
        if (source.startsWith("gas:") || source.equals("gas")) {
            GasTank tank = this.gasTank(source.startsWith("gas:") ? source.substring(4) : "");
            if (tank == null) {
                return "";
            }
            GasStack stored = tank.getGas(this.getNMSLevel(), this.getMachinePos());
            return stored.isEmpty() ? "" : stored.getType().name().toLowerCase(Locale.ROOT);
        }
        return super.barSubtype(id);
    }

    public void setUpgradeDefs(Map<Key, List<MachineAttributes.Mod>> defs) {
        this.upgradeDefs = defs == null ? Map.of() : defs;
    }

    private List<MachineAttributes.Mod> modsOf(int slot) {
        Key id = this.upgradeItemId(this.getItem(slot));
        return id == null ? null : this.upgradeDefs.get(id);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    protected void recomputeUpgrades() {
        if (this.upgradeDefs.isEmpty()) {
            super.recomputeUpgrades();
            return;
        }
        int count = this.definition.upgrades().size();
        ArrayList<MachineAttributes.Mod> all = new ArrayList<MachineAttributes.Mod>();
        int[] slots = this.definition.upgrades().slots();
        for (int i = 0; i < count; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(slots[i]);
            if (m == null) continue;
            all.addAll(m);
        }
        int extra = (int)Math.round(MachineAttributes.compute(all).getOrDefault(MachineAttributes.EXTRA_SLOTS, 0.0));
        this.curUnlocked = Math.max(this.definition.upgrades().baseUnlocked(), Math.min(count, this.definition.upgrades().baseUnlocked() + extra));
        ArrayList<MachineAttributes.Mod> active = new ArrayList<MachineAttributes.Mod>();
        for (int i = 0; i < this.curUnlocked; ++i) {
            List<MachineAttributes.Mod> m = this.modsOf(slots[i]);
            if (m == null) continue;
            active.addAll(m);
        }
        Map<Key, Double> attrs = MachineAttributes.compute(active);
        this.curGeneration = DataMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.GENERATION, 0.0), -0.95, 32.0);
        this.curOverclockLimit = DataMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.OVERCLOCK_LIMIT, 0.0), 0.0, 32.0);
        this.curFuelEff = DataMachineBlockEntity.clamp(attrs.getOrDefault(MachineAttributes.FUEL_EFFICIENCY, 0.0), -32.0, 0.95);
        this.overclock = (float)DataMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.upgradeModifiers = new UpgradeModifiers(1.0 + (double)this.overclock, 1.0, 0.0);
    }

    private int unlockedSlots() {
        if (this.curUnlocked < 0) {
            this.curUnlocked = this.definition.upgrades().baseUnlocked();
        }
        return Math.min(this.definition.upgrades().size(), Math.max(0, this.curUnlocked));
    }

    @Override
    public MachineMenu getMenu() {
        if (this.active == null) {
            this.active = new MachineMenu(this, this.getLayout());
            this.active.syncFromMachine();
            this.menu = this.active;
        }
        return this.active;
    }

    public void openPage(org.bukkit.entity.Player player, int newPage) {
        if (newPage != this.page) {
            pageHistory.addLast(this.page); // push current to history
            if (pageHistory.size() > 16) pageHistory.removeFirst(); // cap history
        }
        this.page = newPage;
        this.active = new MachineMenu(this, this.getLayout());
        this.active.syncFromMachine();
        this.menu = this.active;
        this.active.open(player);
    }

    /** Navigate back in page history (browser back button). */
    public void openPrevPage(org.bukkit.entity.Player player) {
        if (pageHistory.isEmpty()) return;
        int prev = pageHistory.removeLast();
        this.page = prev;
        this.active = new MachineMenu(this, this.getLayout());
        this.active.syncFromMachine();
        this.menu = this.active;
        this.active.open(player);
    }

    public int currentPage() { return page; }
    public int pageCount() { return definition.pages().isEmpty() ? 3 : definition.pages().size(); }

    @Override
    public void openMenu(net.minecraft.world.entity.player.Player player) {
        this.openPage((org.bukkit.entity.Player)player.getBukkitEntity(), 0);
    }

    @Override
    public MachineLayout getLayout() {
        List<dev.arubik.craftengine.machine.MachineDefinition.PageDef> pages = this.definition.pages();
        if (!pages.isEmpty()) {
            int idx = Math.max(0, Math.min(this.page, pages.size() - 1));
            return this.buildPageLayout(pages.get(idx), idx);
        }
        // Legacy system (backwards compat)
        if (this.definition.upgrades().isInline()) {
            return this.buildMainLayout();
        }
        return switch (this.page) {
            case 1 -> this.buildUpgradeLayout();
            case 2 -> this.buildOverclockLayout();
            default -> this.buildMainLayout();
        };
    }

    /** Cumulative {@link dev.arubik.craftengine.machine.MachineDefinition.PageDef#storageSlots()}
     *  count across every page BEFORE {@code pageIndex} — the underlying machine {@link net.minecraft.world.Container}
     *  is ONE physical inventory shared by every page (unlike an item's per-page NBT storage), so
     *  without this offset two pages that both declare e.g. {@code "storage": [0..17]} would silently
     *  alias the SAME 18 container slots instead of getting independent storage. */
    private int storageBaseOffset(int pageIndex) {
        // Shared with the item side (ItemStateData#buildFromContainer/writeToContainer) so a
        // placeable-container item and the machine it becomes can never disagree about which
        // container slots belong to which page.
        return dev.arubik.craftengine.item.ItemStateData.pageStorageOffset(this.definition.pages(), pageIndex);
    }

    private MachineLayout buildPageLayout(dev.arubik.craftengine.machine.MachineDefinition.PageDef page, int pageIndex) {
        if ("upgrades".equals(page.specialType())) {
            Component upgTitle = page.title() != null ? buildTitleComponent(page.title(), page.guiImage(), page.guiImageShift()) : null;
            return buildUpgradeLayoutWithTitle(upgTitle, page.specialItems());
        }

        org.bukkit.event.inventory.InventoryType invType = page.inventoryType();
        boolean isChest = page.isChestType();
        int size = page.resolvedSize();

        String titleStr = page.title() != null ? page.title() : this.definition.title();
        // Evaluate inline ${expr} in title
        try {
            dev.arubik.craftengine.script.ScriptContext ctx = this.buildScriptContext();
            if (ctx != null) titleStr = dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button.evaluateInlineRawStatic(titleStr, ctx);
        } catch (Throwable ignored) {}

        MachineLayout layout = new MachineLayout(invType, isChest ? size : -1, titleStr);
        // Build title Component: supports {Images.from(id)}, {Shift:n}, MiniMessage, ${expr}
        if (titleStr != null) {
            try {
                layout.setTitleComponent(buildTitleComponent(titleStr, page.guiImage(), page.guiImageShift()));
            } catch (Throwable ignored) {}
        }

        // Static layout items — fully script-driven name/lore/item. No hardcoded info slot.
        for (dev.arubik.craftengine.machine.MachineDefinition.PageDef.StaticSlot s : page.layout()) {
            this.installStaticSlot(layout, s);
        }
        // "layout": "file.pf:func" — generated on the fly at menu-open time instead of (or
        // alongside) the static array above; see PageDef#layoutGenerator.
        if (page.layoutGenerator() != null) {
            dev.arubik.craftengine.script.ScriptContext genCtx = this.buildScriptContext();
            if (genCtx != null) {
                for (dev.arubik.craftengine.machine.MachineDefinition.PageDef.StaticSlot s
                        : dev.arubik.craftengine.machine.menu.GeneratedPageContent.layout(page.layoutGenerator(), genCtx)) {
                    this.installStaticSlot(layout, s);
                }
            }
        }

        // Machine slots
        for (int s : page.inputSlots())  layout.addSlot(s, dev.arubik.craftengine.machine.menu.layout.MenuSlotType.INPUT);
        for (int s : page.outputSlots()) layout.addSlot(s, dev.arubik.craftengine.machine.menu.layout.MenuSlotType.OUTPUT);
        for (int s : page.fuelSlots())   layout.addSlot(s, dev.arubik.craftengine.machine.menu.layout.MenuSlotType.FUEL);
        int storageBase = storageBaseOffset(pageIndex);
        int[] pageStorageSlots = page.storageSlots();
        for (int i = 0; i < pageStorageSlots.length; i++) {
            layout.addSlot(pageStorageSlots[i], dev.arubik.craftengine.machine.menu.layout.MenuSlotType.STORAGE, storageBase + i);
        }
        layout.setStorageFilter(page.storageFilter());

        // Inline upgrades
        if (this.definition.upgrades().isInline()) {
            for (int s : this.definition.upgrades().slots())
                layout.addSlot(s, dev.arubik.craftengine.machine.menu.layout.MenuSlotType.UPGRADE);
        }

        // Buttons from page
        for (dev.arubik.craftengine.machine.MachineDefinition.ButtonSpec spec : page.buttons())
            this.installButton(layout, DataMachineBlockEntity.toButton(spec));

        // "buttons": "file.pf:func" — generated fresh every time the page is built, which is every
        // menu OPEN (buildPageLayout runs once per open, not per tick — per-slot content still
        // refreshes continuously afterward via each installed button's own icon lambda, same as any
        // static button already does).
        if (page.buttonsGenerator() != null) {
            dev.arubik.craftengine.script.ScriptContext genCtx = this.buildScriptContext();
            if (genCtx != null) {
                for (dev.arubik.craftengine.machine.MachineDefinition.ButtonSpec spec
                        : dev.arubik.craftengine.machine.menu.GeneratedPageContent.buttons(page.buttonsGenerator(), genCtx)) {
                    this.installButton(layout, DataMachineBlockEntity.toButton(spec));
                }
            }
        }

        // Bars from page
        if (!page.bars().isEmpty()) {
            List<dev.arubik.craftengine.machine.menu.bar.MachineBar> resolved = new ArrayList<>();
            for (dev.arubik.craftengine.machine.MachineDefinition.BarRef ref : page.bars()) {
                dev.arubik.craftengine.machine.menu.bar.BarDefinition barDef = dev.arubik.craftengine.machine.menu.bar.BarDefinition.REGISTRY.get(ref.bar());
                if (barDef != null) resolved.add(barDef.toBar(ref.slots()));
            }
            dev.arubik.craftengine.machine.menu.bar.MachineBars.install(layout, resolved);
        }

        // Ghost slots from page — script-backed identity markers (item filters, ...). Every click
        // immediately calls the spec's `set` script (no separate Save step, no real ItemStack ever
        // moves — see MenuSlotType#GHOST / MachineDefinition.PageDef.GhostSlotSpec).
        for (dev.arubik.craftengine.machine.MachineDefinition.PageDef.GhostSlotSpec spec : page.ghostSlots()) {
            for (int slot : spec.slots()) {
                final int ghostSlot = slot;
                layout.addGhostSlot(ghostSlot,
                    (machine, tick) -> {
                        dev.arubik.craftengine.script.ScriptContext ctx = machine instanceof DataMachineBlockEntity dm3
                            ? dm3.buildScriptContext() : null;
                        if (ctx == null) return org.bukkit.inventory.ItemStack.empty();
                        dev.arubik.craftengine.script.ScriptContext slotCtx = dev.arubik.craftengine.script.ScriptContext
                            .builder().copyFrom(ctx).val("slot", dev.arubik.craftengine.script.ScriptValue.of(ghostSlot)).build();
                        // evalPfFuncItem accepts EITHER a full Item return (exact display — every
                        // enchant/component intact, see Machine.get_item_flag) OR a plain Str id
                        // (a generic representative icon) — the `get` script picks its own fidelity.
                        org.bukkit.inventory.ItemStack resolved = evalPfFuncItem(spec.getRef(), slotCtx);
                        return resolved != null ? resolved
                            : MenuText.iconItem(parseKey(spec.emptyIcon()), Material.AIR, Component.empty(), new Component[0]);
                    },
                    (machine, player, cursor, click) -> {
                        if (!(machine instanceof DataMachineBlockEntity dm4)) return;
                        dev.arubik.craftengine.script.ScriptContext base = dm4.buildScriptContext();
                        if (base == null) return;
                        boolean cursorEmpty = cursor == null || cursor.getType().isAir();
                        String clickTypeName = click != null ? click.name().toLowerCase(java.util.Locale.ROOT) : "left";
                        String clickedIdStr = cursorEmpty ? "" : itemStackId(cursor);
                        // Both a plain id string AND the FULL clicked item (every component intact)
                        // are bound — a `set` script that only needs "which item type" can use
                        // clicked_id; one that needs to preserve enchantments/custom data (via
                        // Machine.set_item_flag) uses clicked_item instead. Never touches/consumes
                        // the actual cursor stack either way — see MenuSlotType#GHOST.
                        dev.arubik.craftengine.script.ScriptContext.Builder setBuilder = dev.arubik.craftengine.script.ScriptContext
                            .builder().copyFrom(base)
                            .val("slot", dev.arubik.craftengine.script.ScriptValue.of(ghostSlot))
                            .str("clicked_id", clickedIdStr)
                            .str("click_type", clickTypeName)
                            .event(new dev.arubik.craftengine.script.event.GhostSlotEvent(ghostSlot, clickedIdStr, clickTypeName));
                        setBuilder.val("clicked_item", cursorEmpty ? dev.arubik.craftengine.script.ScriptValue.NULL
                            : dev.arubik.craftengine.script.ScriptValue.ofItem(
                                org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(cursor)));
                        evalPfFuncStr(spec.setRef(), setBuilder.build());
                    });
            }
        }

        // Info slot removed — define recipe info via layout slot + inline scripts or {file}.pf:{func} lore
        return layout;
    }

    private void installStaticSlot(MachineLayout layout, dev.arubik.craftengine.machine.MachineDefinition.PageDef.StaticSlot s) {
            final int slot = s.slot();
            final String itemKey = s.item();
            final String name = s.name();
            final List<String> lore = s.lore();
            if (s.locked()) layout.setLocked(slot, true);
            layout.addButton(slot, (machine, tick) -> {
                // Evaluate inline ${expr} in name/lore at render time
                dev.arubik.craftengine.script.ScriptContext ctx = machine instanceof DataMachineBlockEntity dm2 ? dm2.buildScriptContext() : null;
                // name supports: plain text, "${expr}", or "{file}.pf:{func}" returning a string
                String evalName = name;
                if (ctx != null && name != null) {
                    evalName = name.contains(".pf:") ? evalPfFuncStr(name, ctx)
                        : dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button.evaluateInlineRawStatic(name, ctx);
                }

                // item supports: namespaced key, "${expr}", or "{file}.pf:{func}" returning Item/string
                org.bukkit.inventory.ItemStack item = null;
                if (ctx != null && itemKey != null && itemKey.contains(".pf:")) {
                    // Script function returns ScriptValue — Item → use stack, Str → use as key
                    item = evalPfFuncItem(itemKey, ctx);
                }
                if (item == null) {
                    org.bukkit.Material mat = org.bukkit.Material.GRAY_STAINED_GLASS_PANE;
                    Component nameComp2 = evalName != null
                        ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(evalName)
                            .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false)
                        : Component.empty();
                    // Evaluate ${expr} anywhere in the key, not just if it starts with ${
                    String resolvedKey = (ctx != null && itemKey != null && itemKey.contains("${"))
                        ? dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button.evaluateInlineScriptStatic(itemKey, ctx) : itemKey;
                    item = dev.arubik.craftengine.machine.menu.MenuText.iconItem(
                        resolvedKey != null ? parseKey(resolvedKey) : null, mat, nameComp2, new Component[0]);
                } else if (evalName != null) {
                    // Apply script-evaluated name to the item returned by script
                    org.bukkit.inventory.meta.ItemMeta m2 = item.getItemMeta();
                    if (m2 != null) {
                        m2.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(evalName)
                            .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
                        item.setItemMeta(m2);
                    }
                }
                if (item != null && !lore.isEmpty()) {
                    // Evaluate lore (supports ${expr} and {file}.pf:{func} returning string array)
                    List<String> evalLore = ctx != null
                        ? new dev.arubik.craftengine.machine.menu.MachineMenuConfig.Button(slot, itemKey, null, name, lore, null, dev.arubik.craftengine.machine.menu.MachineMenuConfig.LockedWhen.NEVER).evaluateLoreRaw(ctx)
                        : lore;
                    org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
                    if (meta != null) { meta.lore(mmLore(evalLore)); item.setItemMeta(meta); }
                }
                return item;
            }, (machine, player) -> {
                // Execute layout slot action on click (if defined)
                String action = s.action();
                if (action == null || action.isBlank()) return;
                if (!(machine instanceof DataMachineBlockEntity dmBtn)) return;
                dev.arubik.craftengine.machine.menu.MachineMenuConfig.Action parsed =
                    dev.arubik.craftengine.machine.menu.MachineMenuConfig.Action.parse(action);
                switch (parsed.kind) {
                    case OPEN_PAGE -> dmBtn.openPage((org.bukkit.entity.Player) player, parsed.page);
                    case SCRIPT -> {
                        dev.arubik.craftengine.script.ScriptContext sCtx = dmBtn.buildScriptContext();
                        if (sCtx == null) break;
                        String target = parsed.target;
                        if (target.contains(":")) {
                            int col = target.indexOf(':'); String file = target.substring(0, col); String func = target.substring(col + 1);
                            String key = file.endsWith(".pf") ? file.substring(0, file.length() - 3) : file;
                            dev.arubik.craftengine.script.ScriptProgram prog = dev.arubik.craftengine.script.ScriptRegistry.get(key);
                            if (prog != null) {
                                dev.arubik.craftengine.script.ScriptContext withDefs = prog.evaluate(sCtx);
                                dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(func);
                                if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                                    dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                                    dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(withDefs);
                                    fn.executor().accept(withDefs, rb);
                                }
                            }
                        }
                    }
                    default -> {}
                }
            });
    }

    /** The id a GHOST slot's `set` hook should record for a held item — a CraftEngine custom id if
     * the stack carries one, else its vanilla key. */
    private static String itemStackId(org.bukkit.inventory.ItemStack stack) {
        try {
            var ceId = net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(stack);
            if (ceId != null) return ceId.toString();
        } catch (Throwable ignored) {
        }
        return stack.getType().getKey().toString();
    }

    /**
     * Build a title Component for a menu page.
     * Title is evaluated as a ScriptFormula expression. Supports:
     *   Images.from("id")          → CraftEngine background image with default shift -8
     *   Images.from("id", shift)   → background image with custom pixel shift
     *   Shift(Images.from("id"), n)→ same with explicit shift
     *   "MiniMessage text"         → colour/formatting via MiniMessage
     *   "${expr}"                  → inline script expression result
     *
     * gui_image + guiImageShift fields are the non-expression alternative.
     */
    /**
     * Build a title Component for a menu page.
     * Template syntax: literal text + {expr} blocks evaluated as ScriptFormula.
     * Escape literal braces with \{ or \}.
     *
     * Examples:
     *   "My Machine"                          → plain MiniMessage text
     *   "{Images.from('cml:crusher_gui')}"    → image background component
     *   "<gold>Crusher {Machine.x},{Machine.z}" → mixed text + script values
     *   "Shift(Images.from('cml:gui'), -12)"  → already-a-formula (no braces) also supported
     *   "\{literal braces\}"                  → escaped, won't be evaluated
     */
    private static Component buildTitleComponent(String title, String guiImage, int guiImageShift) {
        if (title == null) return Component.empty();

        // Parse template: split on {expr} blocks
        // \{ → literal {, \} → literal }
        java.util.List<Component> parts = new java.util.ArrayList<>();
        StringBuilder textBuf = new StringBuilder();
        int i = 0;
        while (i < title.length()) {
            char c = title.charAt(i);
            // Escaped brace
            if (c == '\\' && i + 1 < title.length() && (title.charAt(i + 1) == '{' || title.charAt(i + 1) == '}')) {
                textBuf.append(title.charAt(i + 1));
                i += 2;
                continue;
            }
            // Start of {expr}
            if (c == '{') {
                // Flush text buffer
                if (textBuf.length() > 0) {
                    String txt = textBuf.toString();
                    textBuf.setLength(0);
                    try { parts.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(txt)); }
                    catch (Throwable ignored) { parts.add(net.kyori.adventure.text.Component.text(txt)); }
                }
                // Find matching }
                int depth = 1, j = i + 1;
                while (j < title.length() && depth > 0) {
                    if (title.charAt(j) == '{') depth++;
                    else if (title.charAt(j) == '}') depth--;
                    if (depth > 0) j++;
                }
                String expr = title.substring(i + 1, j);
                i = j + 1;
                // Evaluate expr as ScriptFormula
                try {
                    // Provide Images singleton so Images.from('id') resolves via PolyType
                    dev.arubik.craftengine.script.ScriptContext evalCtx =
                        dev.arubik.craftengine.script.ScriptContext.builder()
                            .typed("Images", "images_singleton").build();
                    dev.arubik.craftengine.script.ScriptValue result =
                        dev.arubik.craftengine.script.ScriptFormula.compile(expr)
                            .evaluate(evalCtx);
                    Component comp = scriptValueToTitleComponent(result);
                    if (comp != null) { parts.add(comp); continue; }
                    // Not a component → use as string
                    String s = result.asStr();
                    try { parts.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(s)); }
                    catch (Throwable ignored) { parts.add(net.kyori.adventure.text.Component.text(s)); }
                } catch (Throwable ignored) {
                    parts.add(net.kyori.adventure.text.Component.text("{" + expr + "}"));
                }
                continue;
            }
            textBuf.append(c);
            i++;
        }
        // Flush remaining text
        if (textBuf.length() > 0) {
            String txt = textBuf.toString();
            try { parts.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(txt)); }
            catch (Throwable ignored) { parts.add(net.kyori.adventure.text.Component.text(txt)); }
        }

        // Combine all parts
        if (parts.isEmpty()) {
            // Fallback: gui_image field
            if (guiImage != null && !guiImage.isBlank()) {
                try {
                    Component img = dev.arubik.craftengine.machine.menu.MenuText.imageTitle(guiImage, guiImageShift);
                    if (img != null) return img;
                } catch (Throwable ignored) {}
            }
            return Component.empty();
        }
        if (parts.size() == 1) return parts.get(0);
        net.kyori.adventure.text.TextComponent.Builder builder = net.kyori.adventure.text.Component.text();
        for (Component p : parts) builder.append(p);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static Component scriptValueToTitleComponent(dev.arubik.craftengine.script.ScriptValue val) {
        // _TitleImage type from Images.from() builtin
        if (val instanceof dev.arubik.craftengine.script.ScriptValue.Obj obj
                && "_TitleImage".equals(obj.typeName())
                && obj.instance() instanceof String[] data && data.length >= 2) {
            try {
                int shift = Integer.parseInt(data[1]);
                return dev.arubik.craftengine.machine.menu.MenuText.imageTitle(data[0], shift);
            } catch (Throwable ignored) {}
        }
        // String result → MiniMessage
        if (val instanceof dev.arubik.craftengine.script.ScriptValue.Str s && !s.value().isBlank()) {
            try { return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(s.value()); }
            catch (Throwable ignored) {}
        }
        return null;
    }

    /** Call a {file}.pf:{func} and return its __return__ value as String. */
    /** Convert a list of MiniMessage/legacy strings to Adventure Components for item lore.
     *  Uses non-italic by default so lore doesn't render purple italic. */
    static java.util.List<net.kyori.adventure.text.Component> mmLore(java.util.List<String> lines) {
        if (lines == null) return java.util.List.of();
        java.util.List<net.kyori.adventure.text.Component> result = new java.util.ArrayList<>(lines.size());
        for (String line : lines) {
            if (line == null) continue;
            net.kyori.adventure.text.Component c;
            try {
                c = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(line);
            } catch (Throwable ignored) {
                c = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(line);
            }
            result.add(c.decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
        }
        return result;
    }

    private static String evalPfFuncStr(String ref, dev.arubik.craftengine.script.ScriptContext ctx) {
        try {
            int colon = ref.indexOf(':');
            String scriptFile = ref.substring(0, colon);
            String funcName = ref.substring(colon + 1);
            String key = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
            dev.arubik.craftengine.script.ScriptProgram prog = dev.arubik.craftengine.script.ScriptRegistry.get(key);
            if (prog == null) return "false";
            dev.arubik.craftengine.script.ScriptContext withDefs = prog.evaluate(ctx);
            dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(funcName);
            if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                    && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(withDefs);
                fn.executor().accept(withDefs, rb);
                return rb.build().getVar("__return__").asStr();
            }
        } catch (Throwable ignored) {}
        return "false";
    }

    /** Call a {file}.pf:{func} and return the ItemStack from __return__ (ScriptValue.Item → NMS→Bukkit, Str → key lookup). */
    private static org.bukkit.inventory.ItemStack evalPfFuncItem(String ref, dev.arubik.craftengine.script.ScriptContext ctx) {
        try {
            int colon = ref.indexOf(':');
            String scriptFile = ref.substring(0, colon);
            String funcName = ref.substring(colon + 1);
            String key = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
            dev.arubik.craftengine.script.ScriptProgram prog = dev.arubik.craftengine.script.ScriptRegistry.get(key);
            if (prog == null) return null;
            dev.arubik.craftengine.script.ScriptContext withDefs = prog.evaluate(ctx);
            dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(funcName);
            if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                    && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(withDefs);
                fn.executor().accept(withDefs, rb);
                dev.arubik.craftengine.script.ScriptValue result = rb.build().getVar("__return__");
                if (result instanceof dev.arubik.craftengine.script.ScriptValue.Item i) {
                    // NMS ItemStack → Bukkit
                    return org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(i.stack());
                }
                if (result instanceof dev.arubik.craftengine.script.ScriptValue.Str s) {
                    // Try as namespaced key → CraftEngine item or Bukkit material
                    try {
                        var ceDef = net.momirealms.craftengine.bukkit.api.CraftEngineItems.byId(net.momirealms.craftengine.core.util.Key.of(s.value()));
                        if (ceDef != null) return ceDef.buildBukkitItem();
                    } catch (Throwable ignored) {}
                    try {
                        org.bukkit.Material mat = org.bukkit.Material.matchMaterial(s.value());
                        if (mat != null) return new org.bukkit.inventory.ItemStack(mat);
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private MachineLayout buildUpgradeLayout() {
        return buildUpgradeLayoutWithTitle(null, null);
    }
    private MachineLayout buildUpgradeLayoutWithTitle(Component customTitle,
            java.util.Map<String, dev.arubik.craftengine.machine.MachineDefinition.ItemSpec> items) {
        MachineLayout l = dev.arubik.craftengine.machine.menu.UpgradeMenu.build(
            this.getMachineId(), this.definition.upgrades().size(), this::unlockedSlots,
            (m, p) -> ((DataMachineBlockEntity)m).openPage((org.bukkit.entity.Player)p, 0),
            null, items);
        if (customTitle != null) l.setTitleComponent(customTitle);
        return l;
    }

    private MachineLayout buildOverclockLayout() {
        return OverclockMenu.build(this.getMachineId(), NamedTextColor.RED, () -> this.overclock, () -> (float)this.curOverclockLimit, this::bumpOverclock, p -> this.openPage((org.bukkit.entity.Player)p, 0));
    }

    @Override
    protected void process(Level level, AbstractProcessingRecipe recipe) {
        this.consumeInputs(level, recipe);
        int extra = 0;
        if (this.curGeneration > 0.0) {
            this.genBuffer += this.curGeneration;
            while (this.genBuffer >= 1.0) {
                this.genBuffer -= 1.0;
                ++extra;
            }
        }
        for (int set = 0; set < 1 + extra; ++set) {
            for (RecipeOutput output : recipe.getOutputs()) {
                output.dispense(level, this);
            }
        }
        if (extra > 0) {
            this.setChanged();
        }
    }

    @Override
    public int effectiveRpm(AbstractProcessingRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        return Math.round((float)((double)recipe.getMinRpm() * (1.0 + (double)this.overclock) * (1.0 - this.curFuelEff)));
    }

    @Override
    public int effectiveSu(AbstractProcessingRecipe recipe) {
        if (recipe == null) {
            return 0;
        }
        double factor = Math.pow(Math.max(0.0, 1.0 + (double)this.overclock), this.definition.power().suExponent());
        return (int)Math.round((double)recipe.getSuCost() * factor);
    }

    @Override
    public boolean isOverclocked() {
        return super.isOverclocked() || this.overclock > 0.0f;
    }

    public void bumpOverclock(boolean up, ClickType click) {
        float delta = OverclockMenu.step(click);
        this.overclock += up ? delta : -delta;
        this.overclock = (float)DataMachineBlockEntity.clamp(this.overclock, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.setChanged();
    }

    // --- Public accessors for MachineType script methods ---
    public float getOverclock()              { return this.overclock; }
    public double getOverclockLimit()        { return this.curOverclockLimit; }
    public double getFuelEfficiency()        { return this.curFuelEff; }
    public double getGeneration()            { return this.curGeneration; }
    public void setOverclock(float v) {
        this.overclock = (float) DataMachineBlockEntity.clamp(v, -Math.min(this.curOverclockLimit, 0.99), this.curOverclockLimit);
        this.upgradeModifiers = new UpgradeModifiers(1.0 + this.overclock, 1.0, 0.0);
        this.setChanged();
    }
    public void addOverclock(float delta) { setOverclock(this.overclock + delta); }
    public static double clampPublic(double v, double min, double max) { return clamp(v, min, max); }

    @Override
    public ScriptContext buildScriptContext() {
        try {
            float f;
            int n = 0;
            Level level = this.getNMSLevel();
            BlockPos pos = this.getMachinePos();
            if (level == null || pos == null) {
                return null;
            }
            LinkedHashMap<String, double[]> fluidTankData = new LinkedHashMap<String, double[]>();
            for (MachineDefinition.TankSpec tankSpec : this.definition.fluidTanks()) {
                FluidTank fluidTank = this.fluidTank(tankSpec.name());
                if (fluidTank == null) continue;
                FluidStack stored = fluidTank.getFluid(level, pos);
                fluidTankData.put(tankSpec.name(), new double[]{stored.getAmount(), fluidTank.getCapacity()});
            }
            LinkedHashMap<String, double[]> gasTankData = new LinkedHashMap<String, double[]>();
            for (MachineDefinition.TankSpec tankSpec : this.definition.gasTanks()) {
                GasTank tank = this.gasTank(tankSpec.name());
                if (tank == null) continue;
                GasStack stored = tank.getGas(level, pos);
                gasTankData.put(tankSpec.name(), new double[]{stored.getAmount(), tank.getCapacity()});
            }
            LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<String, Integer>();
            if (!this.upgradeDefs.isEmpty()) {
                for (int upSlot : this.definition.upgrades().slots()) {
                    net.minecraft.world.item.ItemStack nmsItem = this.getItem(upSlot);
                    Key uid = this.upgradeItemId(nmsItem);
                    if (uid == null) continue;
                    linkedHashMap.merge(uid.namespace() + ":" + uid.value(), 1, Integer::sum);
                }
            }
            boolean bl = false;
            try {
                n = level.getBestNeighborSignal(pos);
            }
            catch (Throwable tank) {
                // empty catch block
            }
            MachineRenderContext mrc = new MachineRenderContext(this.inputRpm, this.overclock, this.curFuelEff, this.progress, this.maxProgress, this.curGeneration, this.isProcessing(), this.inputRpm > 0.0f, this.isOverclocked(), this.burnTime > 0, null, linkedHashMap, fluidTankData, gasTankData, n);
            Direction facing = this.getFacing(level);
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
            String facingName = facing != null ? facing.getName().toLowerCase() : "north";
            ScriptContext base = mrc.toScriptContext();
            ScriptContext.Builder b = ScriptContext.builder().copyFrom(base)
                .facing(facingName, yaw)
                .redstone(n)
                .num("burn_time", this.burnTime)
                .num("max_burn_time", this.maxBurnTime)
                .num("ticks_alive", this.ticksAlive)
                .num("overclock_limit", this.curOverclockLimit)
                .num("generation", this.curGeneration)
                .num("rpm_ratio", this.definition != null ? this.definition.rpmRatio() : 1.0f)
                .typed("Machine", new dev.arubik.craftengine.script.types.machine.MachineType.MachineRef((ServerLevel)level, pos, facingName, this))
                .typed("Network", new dev.arubik.craftengine.script.types.machine.NetworkType.NetworkRef((ServerLevel)level, ((ServerLevel)level).getWorld().getUID(), pos.getX(), pos.getY(), pos.getZ()));
            if (level instanceof dev.arubik.craftengine.contraption.core.ContraptionLevel cl)
                b.typed("Contraption", cl);
            // Global singletons available in all machine scripts
            b.typed("ContraptionManager", dev.arubik.craftengine.script.types.world.ContraptionManagerType.INSTANCE);
            b.typed("Server", dev.arubik.craftengine.script.types.world.ServerType.INSTANCE);
            b.typed("ChainManager", dev.arubik.craftengine.script.types.chainery.ChainManagerType.INSTANCE);
            b.typed("TypedKey", dev.arubik.craftengine.script.types.util.TypedKeyManagerType.INSTANCE);
            b.typed("Glue", dev.arubik.craftengine.script.types.world.GlueType.INSTANCE);
            if (level instanceof ServerLevel sl) b.world(sl);
            return b.build();
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private void runActionScript(String scriptRef) {
        dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(scriptRef);
        if (call == null) return;
        try {
            ScriptContext ctx = this.buildScriptContext();
            if (ctx == null) return;
            if (SCRIPT_DEBUG) System.out.println("[CEP script] RUN " + scriptRef);
            call.execute(ctx);
            if (SCRIPT_DEBUG) System.out.println("[CEP script] OK " + scriptRef);
        } catch (Throwable t) {
            if (SCRIPT_DEBUG) t.printStackTrace();
        }
    }

    public void runInteractScript(String scriptRef, ServerPlayer player) {
        runInteractScript(scriptRef, player, "on_right_click");
    }

    /** Like {@link #runInteractScript(String, ServerPlayer)} but binds an {@code event}
     *  ({@link dev.arubik.craftengine.script.event.InteractEvent}) tagged with {@code hookName} —
     *  used by both {@code on_right_click} (interactScript) and {@code on_left_click}
     *  (attackScript), which share this one method. */
    public void runInteractScript(String scriptRef, ServerPlayer player, String hookName) {
        dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(scriptRef);
        if (call == null) return;
        try {
            ScriptContext base = this.buildScriptContext();
            if (base == null) return;
            ScriptContext ctx = ScriptContext.builder().copyFrom(base).player(player)
                    .event(new dev.arubik.craftengine.script.event.InteractEvent(hookName, null))
                    .build();
            call.execute(ctx);
        } catch (Throwable ignored) {}
    }

    /** Run an arbitrary script ref (on_place, on_break, etc.) with the machine's context. */
    public void runScriptRef(String scriptRef) {
        dev.arubik.craftengine.script.ScriptCall call = dev.arubik.craftengine.script.ScriptCall.parse(scriptRef);
        if (call == null) return;
        try {
            ScriptContext ctx = this.buildScriptContext();
            if (ctx != null) call.execute(ctx);
        } catch (Throwable ignored) {}
    }


    private MachineLayout buildMainLayout() {
        int infoSlot;
        Object object;
        MachineLayout layout = new MachineLayout(InventoryType.CHEST, this.definition.menuSize(), this.definition.title());
        Component title = null;
        if (title != null) {
            layout.setTitleComponent(title);
        }
        for (Object slot : this.definition.inputSlots()) {
            layout.addSlot((int)slot, MenuSlotType.INPUT);
        }
        for (Object slot : this.definition.outputSlots()) {
            layout.addSlot((int)slot, MenuSlotType.OUTPUT);
        }
        for (Object slot : this.definition.fuelSlots()) {
            layout.addSlot((int)slot, MenuSlotType.FUEL);
        }
        if (this.definition.upgrades().isInline()) {
            int[] upgradeSlots = this.definition.upgrades().slots();
            for (int i = 0; i < upgradeSlots.length; ++i) {
                layout.addSlot(upgradeSlots[i], MenuSlotType.UPGRADE);
            }
        }
        if (!this.definition.buttons().isEmpty()) {
            for (MachineDefinition.ButtonSpec spec : this.definition.buttons()) {
                this.installButton(layout, DataMachineBlockEntity.toButton(spec));
            }
        } else {
            for (MachineMenuConfig.Button button : this.menuConfig.buttons) {
                this.installButton(layout, button);
            }
        }
        List<MachineBar> effectiveBars = this.bars;
        if (!this.definition.bars().isEmpty()) {
            ArrayList<MachineBar> resolved = new ArrayList<MachineBar>();
            for (MachineDefinition.BarRef ref : this.definition.bars()) {
                BarDefinition def = BarDefinition.REGISTRY.get(ref.bar());
                if (def == null) continue;
                resolved.add(def.toBar(ref.slots()));
            }
            if (!resolved.isEmpty()) {
                effectiveBars = resolved;
            }
        }
        MachineBars.install(layout, effectiveBars);
        // info_slot removed — use pages[].layout with item:"recipe_info.pf:item" etc.
        return layout;
    }

    static MachineMenuConfig.Button toButton(MachineDefinition.ButtonSpec spec) {
        return new MachineMenuConfig.Button(spec.slot(), spec.icon(), MachineMenuConfig.Action.parse(spec.action()), spec.name(), spec.lore(), spec.lockedIcon(), MachineMenuConfig.LockedWhen.parse(spec.lockedWhen()));
    }

    private void installButton(MachineLayout layout, MachineMenuConfig.Button button) {
        layout.addClickButton(button.slot, (machine, tick) -> {
            boolean locked = DataMachineBlockEntity.isLocked(machine, button.lockedWhen);
            Key icon = locked && button.lockedIcon != null ? DataMachineBlockEntity.parseKey(button.lockedIcon) : DataMachineBlockEntity.parseKey(button.icon);
            // Evaluate name/lore with inline ${expr} + MiniMessage
            ScriptContext ctx = machine instanceof DataMachineBlockEntity dm ? dm.buildScriptContext() : null;
            String rawName = button.evaluateNameRaw(ctx);
            java.util.List<String> loreLines = button.evaluateLoreRaw(ctx);
            // Use MiniMessage directly so <lang:key> translatable components are preserved.
            Component nameComp = rawName == null || rawName.isBlank() ? Component.empty()
                : net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                    .deserialize(rawName).decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false);
            org.bukkit.inventory.ItemStack item = MenuText.iconItem(icon, Material.PAPER, nameComp, new Component[0]);
            if (item != null && loreLines != null && !loreLines.isEmpty()) {
                org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.lore(mmLore(loreLines));
                    item.setItemMeta(meta);
                }
            }
            return item;
        }, (machine, player, clickType) -> {
            if (DataMachineBlockEntity.isLocked(machine, button.lockedWhen)) return;
            switch (button.action.kind) {
                case DEPLETE_FLUID: {
                    for (FluidTank t : machine.fluidTanks) {
                        if (!button.action.targets(t.getName())) continue;
                        t.extract(machine.getNMSLevel(), machine.getMachinePos(), t.getCapacity(), null);
                    }
                    break;
                }
                case DEPLETE_GAS: {
                    for (GasTank t : machine.gasTanks) {
                        if (!button.action.targets(t.getName())) continue;
                        t.extract(machine.getNMSLevel(), machine.getMachinePos(), t.getCapacity(), null);
                    }
                    break;
                }
                case OPEN_PAGE: {
                    if (machine instanceof DataMachineBlockEntity self)
                        self.openPage((org.bukkit.entity.Player)player, button.action.page);
                    break;
                }
                case SCRIPT: {
                    if (!(machine instanceof DataMachineBlockEntity dmBtn)) break;
                    ScriptContext sCtx = dmBtn.buildScriptContext();
                    if (sCtx == null) break;

                    // Format: "gas_motor.pf:increase_rpm" stored as target="gas_motor.pf:increase_rpm"
                    String actionTarget = button.action.target;
                    String scriptFile, funcName;
                    if (actionTarget.contains(":")) {
                        int colon = actionTarget.indexOf(':');
                        scriptFile = actionTarget.substring(0, colon);
                        funcName = actionTarget.substring(colon + 1);
                    } else {
                        scriptFile = actionTarget;
                        funcName = null;
                    }

                    // Remove ".pf" suffix for registry lookup
                    String lookupKey = scriptFile.endsWith(".pf") ? scriptFile.substring(0, scriptFile.length() - 3) : scriptFile;
                    ScriptProgram btnScript = ScriptRegistry.get(lookupKey);
                    if (btnScript == null) break;

                    // Inject args + click_type so scripts can branch on left/right/drop etc.
                    dev.arubik.craftengine.script.ScriptContext.Builder b = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(sCtx);
                    if (player != null)
                        b.player(((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle());
                    b.str("click_type", clickType != null ? clickType.name().toLowerCase(java.util.Locale.ROOT) : "left");
                    b.val("is_right_click",  dev.arubik.craftengine.script.ScriptValue.of(clickType == org.bukkit.event.inventory.ClickType.RIGHT || clickType == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT));
                    b.val("is_shift_click",  dev.arubik.craftengine.script.ScriptValue.of(clickType == org.bukkit.event.inventory.ClickType.SHIFT_LEFT || clickType == org.bukkit.event.inventory.ClickType.SHIFT_RIGHT));
                    b.val("is_drop_click",   dev.arubik.craftengine.script.ScriptValue.of(clickType == org.bukkit.event.inventory.ClickType.DROP || clickType == org.bukkit.event.inventory.ClickType.CONTROL_DROP));
                    b.event(new dev.arubik.craftengine.script.event.ButtonEvent(button.slot,
                            clickType != null ? clickType.name().toLowerCase(java.util.Locale.ROOT) : "left"));
                    for (int ai = 0; ai < button.action.args.size(); ai++)
                        b.str("arg" + ai, button.action.args.get(ai));

                    if (funcName == null || funcName.isEmpty()) {
                        // No function — execute whole script
                        btnScript.evaluate(b.build());
                    } else {
                        // Execute script to register defs, then call the named function
                        ScriptContext withDefs = btnScript.evaluate(b.build());
                        dev.arubik.craftengine.script.ScriptValue fnVal = withDefs.getVar(funcName);
                        if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                                && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                            dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                            // Bind action args to function params by position (e.g. "8" → amount)
                            java.util.List<dev.arubik.craftengine.script.ScriptValue> svArgs = new java.util.ArrayList<>(button.action.args.size());
                            for (String a : button.action.args) svArgs.add(dev.arubik.craftengine.script.ScriptValue.of(a));
                            fn.call(svArgs, withDefs);
                        }
                    }
                    break;
                }
                case BUMP_OVERCLOCK: {
                    if (!(machine instanceof DataMachineBlockEntity dmOc)) break;
                    String deltaStr = button.action.target != null ? button.action.target.trim() : "0.01";
                    try {
                        float baseStep = Float.parseFloat(deltaStr);
                        // Scale by click type: right=25x, drop=50x of the base step
                        float scale = 1.0f;
                        if (clickType != null) scale = switch (clickType) {
                            case DROP, CONTROL_DROP -> 50.0f;
                            case RIGHT, SHIFT_RIGHT -> 25.0f;
                            default -> 1.0f;
                        };
                        dmOc.addOverclock(baseStep * scale);
                    } catch (NumberFormatException ignored2) {}
                    break;
                }
                case PF_FUNCTION: {
                    // Call a named function defined in a .pf script loaded in the registry
                    // Function name = button.action.target, args injected as arg0, arg1, ...
                    if (!(machine instanceof DataMachineBlockEntity dmBtn)) break;
                    ScriptContext sCtx = dmBtn.buildScriptContext();
                    if (sCtx == null) break;
                    // Inject args
                    dev.arubik.craftengine.script.ScriptContext.Builder b = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(sCtx);
                    if (player != null)
                        b.player(((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle());
                    for (int ai = 0; ai < button.action.args.size(); ai++)
                        b.str("arg" + ai, button.action.args.get(ai));
                    b.str("_fn", button.action.target);
                    ScriptContext fnCtx = b.build();
                    // Try to find the function variable and call it
                    dev.arubik.craftengine.script.ScriptValue fnVal = fnCtx.getVar(button.action.target);
                    if (fnVal instanceof dev.arubik.craftengine.script.ScriptValue.Obj fnObj
                            && fnObj.typeName().equals(dev.arubik.craftengine.script.UserFunction.TYPE)) {
                        dev.arubik.craftengine.script.UserFunction fn = (dev.arubik.craftengine.script.UserFunction) fnObj.instance();
                        dev.arubik.craftengine.script.ScriptContext.Builder rb = dev.arubik.craftengine.script.ScriptContext.builder().copyFrom(fnCtx);
                        fn.executor().accept(fnCtx, rb);
                    }
                    break;
                }
            }
        });
    }

    private static boolean isLocked(AbstractMachineBlockEntity machine, MachineMenuConfig.LockedWhen lw) {
        if (lw == null) return false;
        double ocLimit = machine instanceof DataMachineBlockEntity dm ? dm.curOverclockLimit : 0.0;
        ScriptContext ctx = machine instanceof DataMachineBlockEntity dm2 ? dm2.buildScriptContext() : null;
        return lw.isLocked(ctx, ocLimit);
    }

    public static Direction rpmFacesContainDir(String s, Direction facing) {
        if (facing == null) {
            facing = Direction.NORTH;
        }
        return switch (s.toLowerCase()) {
            case "front" -> facing;
            case "back" -> facing.getOpposite();
            case "right" -> facing.getClockWise();
            case "left" -> facing.getCounterClockWise();
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> Direction.byName((String)s.toLowerCase());
        };
    }

    private static boolean rpmFacesContain(Set<String> raw, Direction d, Direction facing) {
        if (facing == null) {
            facing = Direction.NORTH;
        }
        Iterator<String> iterator = raw.iterator();
        while (iterator.hasNext()) {
            boolean match;
            String s;
            if (!(match = (switch (s = iterator.next()) {
                case "front" -> {
                    if (d == facing) {
                        yield true;
                    }
                    yield false;
                }
                case "back" -> {
                    if (d == facing.getOpposite()) {
                        yield true;
                    }
                    yield false;
                }
                case "right" -> {
                    if (d == facing.getClockWise()) {
                        yield true;
                    }
                    yield false;
                }
                case "left" -> {
                    if (d == facing.getCounterClockWise()) {
                        yield true;
                    }
                    yield false;
                }
                case "up" -> {
                    if (d == Direction.UP) {
                        yield true;
                    }
                    yield false;
                }
                case "down" -> {
                    if (d == Direction.DOWN) {
                        yield true;
                    }
                    yield false;
                }
                case "horizontal" -> {
                    if (d.getAxis() != Direction.Axis.Y) {
                        yield true;
                    }
                    yield false;
                }
                case "vertical" -> {
                    if (d.getAxis() == Direction.Axis.Y) {
                        yield true;
                    }
                    yield false;
                }
                case "axis_perp" -> true;
                case "all" -> true;
                default -> d.getName().equals(s);
            }))) continue;
            return true;
        }
        return false;
    }

    private static Key parseKey(String spec) {
        if (spec == null) {
            return Key.of((String)"cml", (String)"gui_empty");
        }
        int i = spec.indexOf(58);
        return i < 0 ? Key.of((String)"cml", (String)spec) : Key.of((String)spec.substring(0, i), (String)spec.substring(i + 1));
    }
}

