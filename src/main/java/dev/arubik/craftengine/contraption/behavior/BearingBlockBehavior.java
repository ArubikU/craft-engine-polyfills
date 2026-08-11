package dev.arubik.craftengine.contraption.behavior;

import java.util.List;
import java.util.Map;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * REAL CraftEngine block behavior for a contraption bearing (CONTRAPTIONS.md §5 Phase 6
 * follow-up) — replaces the {@code /cep contraption bearing-test-register} placeholder command
 * and its {@code BearingAnchorRegistry} stand-in. The bearing's {@link BearingType}
 * (LINEAR/ROTATIONAL/MINECART) is read straight from the block's own CraftEngine config via
 * {@link Factory}, exactly as {@code FanBlockBehavior} reads its own config fields — no
 * command, no side registry keyed by position.
 *
 * <p>Registered ({@link #FACTORY_KEY}) with real {@code cml:linear_bearing}/
 * {@code cml:rotational_bearing} block definitions — see {@code block/polyfills/bearing.yml}.
 * A block's {@code behavior:} section reads {@code kind: linear|rotational|minecart}
 * (deliberately NOT {@code type} — {@code type} is CraftEngine's own dispatch key on the SAME
 * section, handed to {@link Factory#create} unmodified; a same-named inner field would collide
 * with it), plus optional {@code rpm}/{@code suPerBlock}.
 *
 * <p>{@link #typeAt(Level, BlockPos)} is the detection entry point other code (
 * {@code BearingHammerListener}) uses — same {@code getOptionalCustomBlockState} /
 * {@code behavior().getFirst(...)} idiom already used throughout this codebase (e.g.
 * {@code HammerAssembleListener}'s {@code FluidBlockTankBehavior} lookup).
 */
public class BearingBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    /** Registration key for {@link dev.arubik.craftengine.block.BlockBehaviors#register()}. */
    public static final Key FACTORY_KEY = Key.of("polyfills:bearing_block");

    public static final Factory FACTORY = new Factory();

    /** Fallback ROTATIONAL rpm when a bearing's config omits {@code rpm:} (matches the old
     * {@code ContraptionAssembler.DEFAULT_ROTATIONAL_RPM} global constant this field replaces
     * as the per-block source of truth). */
    public static final double DEFAULT_RPM = 5.0;

    /**
     * Default stress-unit (SU) cost per captured block (Task 4's "small detail" follow-up,
     * CONTRAPTIONS.md 2026-07-01 session): a bigger structure attached to a bearing should
     * demand more SU from the real motor driving it, same "more load = more SU reported"
     * pattern {@code CrusherBlockEntity} already uses per-recipe — here it's per-captured-block
     * instead, since a bearing has no recipe concept.
     */
    public static final double DEFAULT_SU_PER_BLOCK = 2.0;

    /** Default piston travel distance (blocks) when a LINEAR bearing's config omits {@code distance:}. */
    public static final int DEFAULT_DISTANCE = 1;
    /** Default piston traction speed (blocks/sec) — matches {@code cep contraption move}'s 1 block/s. */
    public static final double DEFAULT_SPEED = 1.0;
    /** Default ROUND_ROBIN / ROBIN_EULER dwell at the extended end, in ticks (5s). */
    public static final long DEFAULT_ROUND_ROBIN_DELAY_TICKS = 100;

    private final Key type;
    /** The bearing's OWN configured target rotation/movement speed — used as-is when no real
     * motor is adjacent, and as the un-throttled base value reported into {@link #suPerBlock}
     * scaling when one is (see {@code RotationalBearingBehavior}/{@code LinearActuatorBehavior}). */
    private final double rpm;
    /** SU demanded from the real motor, per captured block, scaling with structure size. */
    private final double suPerBlock;
    /** LINEAR piston: how many blocks the structure is pushed out (config default; per-instance menu override later). */
    private final int distance;
    /** LINEAR piston: base traction speed (blocks/sec) when no real motor is adjacent. */
    private final double speedBlocksPerSec;
    /** LINEAR piston end-of-travel behaviour — see {@link PistonBearingBehavior.Mode}. */
    private final PistonBearingBehavior.Mode pistonMode;
    /** LINEAR piston ROUND_ROBIN/ROBIN_EULER dwell at the end, in ticks. */
    private final long roundRobinDelayTicks;
    /** CraftEngine block id for the piston HEAD (leading face) — null → no shaft render. */
    private final String headBlockId;
    /** CraftEngine block ids for the extending PIPE, per axis (north-south / west-east / up-down iron_pipe models). */
    private final String pipeBlockNS;
    private final String pipeBlockWE;
    private final String pipeBlockUD;

    /** LINEAR config-menu backing: upgrade defs / bars / menu layout parsed from the block yml (same
     * form the copper fan uses). Null/empty for non-LINEAR bearings (they have no config menu). */
    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;

    public BearingBlockBehavior(BlockDefinition customBlock, Key type, double rpm, double suPerBlock,
            int distance, double speedBlocksPerSec, PistonBearingBehavior.Mode pistonMode, long roundRobinDelayTicks,
            String headBlockId, String pipeBlockNS, String pipeBlockWE, String pipeBlockUD,
            Map<Key, List<Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig) {
        super(customBlock);
        this.type = type;
        this.rpm = rpm;
        this.suPerBlock = suPerBlock;
        this.distance = Math.max(1, distance);
        this.speedBlocksPerSec = speedBlocksPerSec;
        this.pistonMode = pistonMode == null ? PistonBearingBehavior.Mode.LINEAR : pistonMode;
        this.roundRobinDelayTicks = Math.max(0, roundRobinDelayTicks);
        this.headBlockId = emptyToNull(headBlockId);
        this.pipeBlockNS = emptyToNull(pipeBlockNS);
        this.pipeBlockWE = emptyToNull(pipeBlockWE);
        this.pipeBlockUD = emptyToNull(pipeBlockUD);
        this.upgradeDefs = upgradeDefs == null ? new java.util.HashMap<>() : upgradeDefs;
        this.bars = bars == null ? new java.util.ArrayList<>() : bars;
        this.menuConfig = menuConfig;
    }

    // --- EntityBlock: a LINEAR bearing backs a PistonBearingBlockEntity (right-click config menu),
    // copied straight from MachineBlockBehavior's controller-composition idiom. ROTATIONAL/MINECART
    // bearings create no controller (they have no per-instance config menu). ---
    protected int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        if (!type.equals(Key.of("polyfills", "linear")))
            // NEVER null (2026-07-04 fix — "rompiste el minecart bearing... no se ensambla"): this
            // exact same null-return pattern already crashed CraftEngine's own chunk deserializer for
            // LINEAR part!=0 pieces (fixed below); ROTATIONAL/MINECART bearings had the SAME flaw at
            // the type-check level the whole time, just harder to trigger — capturing a rotational/
            // minecart bearing block into a contraption's hidden ContraptionLevel calls
            // `level.setBlock`, which goes through CraftEngine's WorldStorageInjector and asserts a
            // non-null controller for ANY EntityBlock-declared behavior, unconditionally. Use the
            // SAME no-op controller convention HorizontalDoubleBlockBehavior's own RIGHT-half uses
            // (`TransientController`) instead of null.
            return new InertController(blockEntity);
        // ALWAYS create a controller, even for a decorative part=1/2/3 (body/head/shaft) piece
        // placed as a REAL block by the euler drop (2026-07-04 fix — returning null here used to
        // crash CraftEngine's OWN chunk deserializer on ANY world whose persisted blockstate schema
        // had since drifted: "Cannot invoke BlockEntityController.hasElement() because this.controller
        // is null" — CraftEngine's BlockEntity constructor unconditionally assumes non-null once NBT
        // recorded a controller existed, and that single crash aborts the WHOLE chunk's CEChunk
        // construction, silently breaking every OTHER entity-renderer-backed block in that same
        // chunk too (gas motors, pipes, tanks, ...) for the rest of the session). The part!=0 "no
        // menu / inert" behavior now lives entirely in PistonBearingBlockEntity#isInert +
        // this class's own useWithoutItem check below — never by withholding the controller itself.
        return new PistonBearingBlockEntity(blockEntity,
                new java.util.HashMap<>(upgradeDefs), new java.util.ArrayList<>(bars), menuConfig,
                distance, speedBlocksPerSec, pistonMode, roundRobinDelayTicks, suPerBlock);
    }

    /**
     * Right-click opens the LINEAR bearing's config menu — copied from
     * {@code MachineBlockBehavior.useWithoutItem} (resolve the NMS pos, fetch the block entity,
     * cast its controller, {@code getMenu().open(bukkit)}). Only fires for a LINEAR bearing whose
     * block entity is present; a hammer right-click still assembles/disassembles via the existing
     * {@code BearingHammerListener} (that path runs on {@code PlayerInteractEvent} and is unaffected —
     * a config menu opens only when the click carries no hammer, since the hammer listener consumes
     * that interaction first). Non-LINEAR bearings PASS (no menu).
     */
    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(
            net.momirealms.craftengine.core.world.context.UseOnContext context, ImmutableBlockState state) {
        if (!type.equals(Key.of("polyfills", "linear")))
            return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
        try {
            net.minecraft.server.level.ServerLevel level = ((org.bukkit.craftbukkit.CraftWorld) ((net.momirealms.craftengine.bukkit.world.BukkitWorld) context
                    .getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos) net.momirealms.craftengine.bukkit.util.LocationUtils
                    .toBlockPos(context.getClickedPos());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && be.controller instanceof PistonBearingBlockEntity bearing && !bearing.isInert()
                    && context.getPlayer() instanceof net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer
                    && cePlayer.platformPlayer() instanceof org.bukkit.entity.Player bukkit) {
                bearing.getMenu().open(bukkit);
                return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    /** The LINEAR bearing's per-instance config controller at {@code pos}, or null (unconfigured /
     *  non-LINEAR / unloaded). Used by {@code ContraptionAssembler} to read per-instance distance/
     *  speed/mode/dwell + upgrade modifiers, falling back to the {@code *At} YAML defaults otherwise. */
    public static PistonBearingBlockEntity controllerAt(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && be.controller instanceof PistonBearingBlockEntity bearing)
            return bearing;
        return null;
    }

    /** No-op controller for ROTATIONAL/MINECART bearings (see {@link #createBlockEntityController}) —
     *  same minimal shape as {@code HorizontalDoubleBlockBehavior.TransientController}. */
    public static final class InertController extends BlockEntityController {
        public InertController(BlockEntity blockEntity) {
            super(blockEntity);
        }
    }

    private static String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    public String headBlockId() {
        return headBlockId;
    }

    /** Pipe block id for a facing axis: {@code axis} is 'x','y','z'. */
    public String pipeBlockIdForAxis(char axis) {
        return switch (axis) {
            case 'x' -> pipeBlockWE;
            case 'y' -> pipeBlockUD;
            default -> pipeBlockNS;
        };
    }

    public static String headBlockIdAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = behaviorAt(level, pos);
        return b == null ? null : b.headBlockId();
    }

    public static String pipeBlockIdAt(Level level, BlockPos pos, char axis) {
        BearingBlockBehavior b = behaviorAt(level, pos);
        return b == null ? null : b.pipeBlockIdForAxis(axis);
    }

    public Key type() {
        return type;
    }

    /** Configured spin rate (rpm), meaningful only when {@link #type()} is ROTATIONAL. */
    public double rpm() {
        return rpm;
    }

    /** SU demanded from the real motor per captured block (see {@link #DEFAULT_SU_PER_BLOCK}). */
    public double suPerBlock() {
        return suPerBlock;
    }

    /** LINEAR piston travel distance in blocks (config default). */
    public int distance() {
        return distance;
    }

    /** LINEAR piston base traction speed (blocks/sec). */
    public double speedBlocksPerSec() {
        return speedBlocksPerSec;
    }

    /** LINEAR piston end-of-travel mode. */
    public PistonBearingBehavior.Mode pistonMode() {
        return pistonMode;
    }

    /** LINEAR piston ROUND_ROBIN/ROBIN_EULER dwell (ticks). */
    public long roundRobinDelayTicks() {
        return roundRobinDelayTicks;
    }

    private static BearingBlockBehavior behaviorAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return null;
        }
        return ce.behavior().getFirst(BearingBlockBehavior.class);
    }

    /** Configured LINEAR piston distance at {@code pos}, or {@link #DEFAULT_DISTANCE}. */
    public static int distanceAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = behaviorAt(level, pos);
        return b == null ? DEFAULT_DISTANCE : b.distance();
    }

    /** Configured LINEAR piston speed (blocks/sec) at {@code pos}, or {@link #DEFAULT_SPEED}. */
    public static double speedAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = behaviorAt(level, pos);
        return b == null ? DEFAULT_SPEED : b.speedBlocksPerSec();
    }

    /** Configured LINEAR piston mode at {@code pos}, or {@link PistonBearingBehavior.Mode#LINEAR}. */
    public static PistonBearingBehavior.Mode pistonModeAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = behaviorAt(level, pos);
        return b == null ? PistonBearingBehavior.Mode.LINEAR : b.pistonMode();
    }

    /** Configured ROUND_ROBIN dwell (ticks) at {@code pos}, or {@link #DEFAULT_ROUND_ROBIN_DELAY_TICKS}. */
    public static long roundRobinDelayAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = behaviorAt(level, pos);
        return b == null ? DEFAULT_ROUND_ROBIN_DELAY_TICKS : b.roundRobinDelayTicks();
    }

    /**
     * The bearing's own push direction, read from its {@code facing} block property (north/south/
     * east/west/up/down). Returns a unit {@link net.minecraft.world.phys.Vec3}; falls back to UP
     * {@code (0,1,0)} when the block has no {@code facing} property (e.g. an older/plain bearing
     * definition without the 6-directional piston property yet). See
     * {@code ContraptionAssembler.attachDefaultBehavior} for where this drives the piston.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static net.minecraft.world.phys.Vec3 facingVecAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce != null && !ce.isEmpty()) {
            try {
                net.momirealms.craftengine.core.block.property.Property p = ce.getProperty("facing");
                if (p != null) {
                    Object v = ce.get(p);
                    if (v != null) {
                        String name = net.momirealms.craftengine.core.block.property.Property
                                .formatValue(p, (Comparable<?>) v);
                        net.minecraft.world.phys.Vec3 vec = facingToVec(name);
                        if (vec != null) {
                            return vec;
                        }
                    }
                }
            } catch (Throwable ignored) {
                // no facing property / unexpected type — fall through to default
            }
        }
        return new net.minecraft.world.phys.Vec3(0, 1, 0); // default: push UP
    }

    private static net.minecraft.world.phys.Vec3 facingToVec(String name) {
        if (name == null) {
            return null;
        }
        switch (name.toLowerCase(java.util.Locale.ROOT)) {
            case "north":
                return new net.minecraft.world.phys.Vec3(0, 0, -1);
            case "south":
                return new net.minecraft.world.phys.Vec3(0, 0, 1);
            case "east":
                return new net.minecraft.world.phys.Vec3(1, 0, 0);
            case "west":
                return new net.minecraft.world.phys.Vec3(-1, 0, 0);
            case "up":
                return new net.minecraft.world.phys.Vec3(0, 1, 0);
            case "down":
                return new net.minecraft.world.phys.Vec3(0, -1, 0);
            default:
                return null;
        }
    }

    /**
     * Returns the contraption type of the bearing block at {@code pos}, or {@code null} if
     * there is no CraftEngine custom block there, or it isn't a bearing at all.
     */
    public static Key typeAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty())
            return null;
        BearingBlockBehavior behavior = ce.behavior().getFirst(BearingBlockBehavior.class);
        return behavior == null ? null : behavior.type();
    }

    /**
     * Returns the configured ROTATIONAL rpm of the bearing block at {@code pos}, or
     * {@link #DEFAULT_RPM} if there's no CraftEngine bearing there (defensive fallback —
     * callers should already have checked {@link #typeAt} is ROTATIONAL).
     */
    public static double rpmAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty())
            return DEFAULT_RPM;
        BearingBlockBehavior behavior = ce.behavior().getFirst(BearingBlockBehavior.class);
        return behavior == null ? DEFAULT_RPM : behavior.rpm();
    }

    /**
     * Returns the configured SU-per-block of the bearing block at {@code pos}, or
     * {@link #DEFAULT_SU_PER_BLOCK} if there's no CraftEngine bearing there.
     */
    public static double suPerBlockAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty())
            return DEFAULT_SU_PER_BLOCK;
        BearingBlockBehavior behavior = ce.behavior().getFirst(BearingBlockBehavior.class);
        return behavior == null ? DEFAULT_SU_PER_BLOCK : behavior.suPerBlock();
    }

    /** Config blast immunity 0..1: 0 = cells break normally, 1 = immune (only pushed), between = partial. */
    private double explosionProof;

    public double explosionProof() {
        return explosionProof;
    }

    void setExplosionProof(double explosionProof) {
        this.explosionProof = Math.max(0.0, Math.min(1.0, explosionProof));
    }

    /** The {@code explosionProof} value (0..1) of the bearing block at {@code pos}, or 0 if there's no bearing there. */
    public static double explosionProofAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty())
            return 0.0;
        BearingBlockBehavior behavior = ce.behavior().getFirst(BearingBlockBehavior.class);
        return behavior == null ? 0.0 : behavior.explosionProof();
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            // NOTE: this field is named "kind", NOT "type" — CraftEngine's own BlockBehaviors
            // #fromConfig dispatches on a "type" key (e.g. "polyfills:bearing_block") read from
            // this SAME ConfigSection and hands the section down UNMODIFIED, so a same-named
            // inner "type" field would collide with the dispatch key (both YAML-duplicate and,
            // even if it parsed, would read back the dispatch string instead of the bearing kind).
            String kindArg = arguments.getOrDefault("kind", "linear").toString().toLowerCase(java.util.Locale.ROOT);
            Key type = Key.of("polyfills", kindArg);
            double rpm = Double.parseDouble(arguments.getOrDefault("rpm", Double.valueOf(DEFAULT_RPM)).toString());
            double suPerBlock = Double
                    .parseDouble(arguments.getOrDefault("suPerBlock", Double.valueOf(DEFAULT_SU_PER_BLOCK)).toString());
            // LINEAR piston config (6-directional extending piston rework, 2026-07-03). These are the
            // per-DEFINITION defaults; a per-block-instance override via the right-click config menu is
            // a later phase (needs a block entity — see the goal notes).
            int distance = Integer
                    .parseInt(arguments.getOrDefault("distance", Integer.valueOf(DEFAULT_DISTANCE)).toString());
            double speed = Double
                    .parseDouble(arguments.getOrDefault("speed", Double.valueOf(DEFAULT_SPEED)).toString());
            PistonBearingBehavior.Mode pistonMode = PistonBearingBehavior.Mode
                    .fromString(arguments.getOrDefault("mode", "linear").toString());
            long rrDelay = Long.parseLong(arguments
                    .getOrDefault("roundRobinDelayTicks", Long.valueOf(DEFAULT_ROUND_ROBIN_DELAY_TICKS)).toString());
            // Optional 3-piece shaft render block ids (2026-07-03). Head = piston_x leading face;
            // pipe = iron_pipe model per facing axis. Absent → the mechanic still works, just no shaft.
            String headBlock = str(arguments.get("headBlock"));
            String pipeNS = str(arguments.get("pipeNS"));
            String pipeWE = str(arguments.get("pipeWE"));
            String pipeUD = str(arguments.get("pipeUD"));

            // LINEAR right-click config menu (2026-07-03): parse the SAME upgrades:/bars:/menu form the
            // copper fan uses, so the same upgrade items work and the layout is fully yml-driven. Only a
            // LINEAR bearing builds a block entity, so only it needs these (harmless empties otherwise).
            Map<Key, List<Mod>> upgrades = new java.util.HashMap<>();
            Object uObj = arguments.get("upgrades");
            if (uObj instanceof Map<?, ?> uMap) {
                for (Map.Entry<?, ?> e : uMap.entrySet()) {
                    List<Mod> mods = parseMods(e.getValue());
                    if (!mods.isEmpty())
                        upgrades.put(key(String.valueOf(e.getKey())), mods);
                }
            }
            List<MachineBar> bars = MachineBars.parse(arguments.get("bars"));
            MachineMenuConfig menuConfig = MachineMenuConfig.parse(arguments::get);

            BearingBlockBehavior behavior = new BearingBlockBehavior(block, type, rpm, suPerBlock, distance, speed,
                    pistonMode, rrDelay, headBlock, pipeNS, pipeWE, pipeUD, upgrades, bars, menuConfig);
            behavior.setExplosionProof(parseProof(arguments.getOrDefault("explosionProof", 0.0)));
            return behavior;
        }

        private static String str(Object o) {
            return o == null ? null : o.toString();
        }

        /** Parses {@code explosionProof} as a 0..1 float, accepting legacy {@code true}/{@code false}. */
        private static double parseProof(Object o) {
            String s = String.valueOf(o).trim();
            if (s.equalsIgnoreCase("true")) {
                return 1.0;
            }
            if (s.equalsIgnoreCase("false")) {
                return 0.0;
            }
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        /** Parse one upgrade item's attribute-modifier list — identical to FanMachineBehavior.parseMods. */
        private static List<Mod> parseMods(Object value) {
            List<Mod> out = new java.util.ArrayList<>();
            if (value instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Map<?, ?> m) {
                        Object attr = m.get("attribute");
                        if (attr == null)
                            continue;
                        Object opObj = m.get("operation");
                        MachineAttributes.Operation op = MachineAttributes.parseOperation(
                                opObj == null ? "add" : String.valueOf(opObj));
                        Object vObj = m.get("value");
                        double v = Utils.getAsDouble(vObj == null ? 0 : vObj, "value");
                        out.add(new Mod(key(String.valueOf(attr)), op, v));
                    }
                }
            }
            return out;
        }

        private static Key key(String s) {
            int i = s.indexOf(':');
            return (i < 0) ? Key.of("minecraft", s) : Key.of(s.substring(0, i), s.substring(i + 1));
        }
    }
}
