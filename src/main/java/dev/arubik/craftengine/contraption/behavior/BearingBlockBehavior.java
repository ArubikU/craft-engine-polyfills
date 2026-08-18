/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
 *  net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer
 *  net.momirealms.craftengine.bukkit.util.BlockStateUtils
 *  net.momirealms.craftengine.bukkit.util.LocationUtils
 *  net.momirealms.craftengine.bukkit.world.BukkitWorld
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.ImmutableBlockState
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.block.behavior.EntityBlock
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.Property
 *  net.momirealms.craftengine.core.entity.player.InteractionResult
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Key
 *  net.momirealms.craftengine.core.world.BlockPos
 *  net.momirealms.craftengine.core.world.context.UseOnContext
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.entity.Player
 */
package dev.arubik.craftengine.contraption.behavior;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBehavior;
import dev.arubik.craftengine.contraption.behavior.PistonBearingBlockEntity;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

public class BearingBlockBehavior
extends BukkitBlockBehavior
implements EntityBlock {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:bearing_block");
    public static final Factory FACTORY = new Factory();
    public static final double DEFAULT_RPM = 5.0;
    public static final double DEFAULT_SU_PER_BLOCK = 2.0;
    public static final int DEFAULT_DISTANCE = 1;
    public static final double DEFAULT_SPEED = 1.0;
    public static final long DEFAULT_ROUND_ROBIN_DELAY_TICKS = 100L;
    private final Key type;
    private final double rpm;
    private final double suPerBlock;
    private final int distance;
    private final double speedBlocksPerSec;
    private final PistonBearingBehavior.Mode pistonMode;
    private final long roundRobinDelayTicks;
    private final String headBlockId;
    private final String pipeBlockNS;
    private final String pipeBlockWE;
    private final String pipeBlockUD;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    protected int controllerId;
    private double explosionProof;

    public BearingBlockBehavior(BlockDefinition customBlock, Key type, double rpm, double suPerBlock, int distance, double speedBlocksPerSec, PistonBearingBehavior.Mode pistonMode, long roundRobinDelayTicks, String headBlockId, String pipeBlockNS, String pipeBlockWE, String pipeBlockUD, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig) {
        super(customBlock);
        this.type = type;
        this.rpm = rpm;
        this.suPerBlock = suPerBlock;
        this.distance = Math.max(1, distance);
        this.speedBlocksPerSec = speedBlocksPerSec;
        this.pistonMode = pistonMode == null ? PistonBearingBehavior.Mode.LINEAR : pistonMode;
        this.roundRobinDelayTicks = Math.max(0L, roundRobinDelayTicks);
        this.headBlockId = BearingBlockBehavior.emptyToNull(headBlockId);
        this.pipeBlockNS = BearingBlockBehavior.emptyToNull(pipeBlockNS);
        this.pipeBlockWE = BearingBlockBehavior.emptyToNull(pipeBlockWE);
        this.pipeBlockUD = BearingBlockBehavior.emptyToNull(pipeBlockUD);
        this.upgradeDefs = upgradeDefs == null ? new HashMap() : upgradeDefs;
        this.bars = bars == null ? new ArrayList() : bars;
        this.menuConfig = menuConfig;
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        if (!this.type.equals(Key.of((String)"polyfills", (String)"linear"))) {
            return new InertController(blockEntity);
        }
        return new PistonBearingBlockEntity(blockEntity, new HashMap<Key, List<MachineAttributes.Mod>>(this.upgradeDefs), new ArrayList<MachineBar>(this.bars), this.menuConfig, this.distance, this.speedBlocksPerSec, this.pistonMode, this.roundRobinDelayTicks, this.suPerBlock);
    }

    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        if (!this.type.equals(Key.of((String)"polyfills", (String)"linear"))) {
            return InteractionResult.PASS;
        }
        try {
            ServerLevel level = ((CraftWorld)((BukkitWorld)context.getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos)LocationUtils.toBlockPos((net.momirealms.craftengine.core.world.BlockPos)context.getClickedPos());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded((Level)level, pos);
            BukkitServerPlayer cePlayer = (BukkitServerPlayer)context.getPlayer();
            PistonBearingBlockEntity bearing = (PistonBearingBlockEntity)be.controller;
            if (be != null && bearing != null && !bearing.isInert() && cePlayer != null) {
                Player bukkit = cePlayer.platformPlayer();
                bearing.getMenu().open(bukkit);
                return InteractionResult.SUCCESS_AND_CANCEL;
            }



        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return InteractionResult.PASS;
    }

    public static PistonBearingBlockEntity controllerAt(Level level, BlockPos pos) {
        BlockEntityController blockEntityController;
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be != null && (blockEntityController = be.controller) instanceof PistonBearingBlockEntity) {
            PistonBearingBlockEntity bearing = (PistonBearingBlockEntity)blockEntityController;
            return bearing;
        }
        return null;
    }

    private static String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    public String headBlockId() {
        return this.headBlockId;
    }

    public String pipeBlockIdForAxis(char axis) {
        return switch (axis) {
            case 'x' -> this.pipeBlockWE;
            case 'y' -> this.pipeBlockUD;
            default -> this.pipeBlockNS;
        };
    }

    public static String headBlockIdAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = BearingBlockBehavior.behaviorAt(level, pos);
        return b == null ? null : b.headBlockId();
    }

    public static String pipeBlockIdAt(Level level, BlockPos pos, char axis) {
        BearingBlockBehavior b = BearingBlockBehavior.behaviorAt(level, pos);
        return b == null ? null : b.pipeBlockIdForAxis(axis);
    }

    public Key type() {
        return this.type;
    }

    public double rpm() {
        return this.rpm;
    }

    public double suPerBlock() {
        return this.suPerBlock;
    }

    public int distance() {
        return this.distance;
    }

    public double speedBlocksPerSec() {
        return this.speedBlocksPerSec;
    }

    public PistonBearingBehavior.Mode pistonMode() {
        return this.pistonMode;
    }

    public long roundRobinDelayTicks() {
        return this.roundRobinDelayTicks;
    }

    private static BearingBlockBehavior behaviorAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return null;
        }
        return (BearingBlockBehavior)(ce.behavior().getFirst(BearingBlockBehavior.class));
    }

    public static int distanceAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = BearingBlockBehavior.behaviorAt(level, pos);
        return b == null ? 1 : b.distance();
    }

    public static double speedAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = BearingBlockBehavior.behaviorAt(level, pos);
        return b == null ? 1.0 : b.speedBlocksPerSec();
    }

    public static PistonBearingBehavior.Mode pistonModeAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = BearingBlockBehavior.behaviorAt(level, pos);
        return b == null ? PistonBearingBehavior.Mode.LINEAR : b.pistonMode();
    }

    public static long roundRobinDelayAt(Level level, BlockPos pos) {
        BearingBlockBehavior b = BearingBlockBehavior.behaviorAt(level, pos);
        return b == null ? 100L : b.roundRobinDelayTicks();
    }

    public static Vec3 facingVecAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce != null && !ce.isEmpty()) {
            try {
                String name;
                Vec3 vec;
                Comparable v;
                Property p = ce.getProperty("facing");
                if (p != null && (v = ce.get(p)) != null && (vec = BearingBlockBehavior.facingToVec(name = Property.formatValue((Property)p, (Comparable)v))) != null) {
                    return vec;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return new Vec3(0.0, 1.0, 0.0);
    }

    private static Vec3 facingToVec(String name) {
        if (name == null) {
            return null;
        }
        switch (name.toLowerCase(Locale.ROOT)) {
            case "north": {
                return new Vec3(0.0, 0.0, -1.0);
            }
            case "south": {
                return new Vec3(0.0, 0.0, 1.0);
            }
            case "east": {
                return new Vec3(1.0, 0.0, 0.0);
            }
            case "west": {
                return new Vec3(-1.0, 0.0, 0.0);
            }
            case "up": {
                return new Vec3(0.0, 1.0, 0.0);
            }
            case "down": {
                return new Vec3(0.0, -1.0, 0.0);
            }
        }
        return null;
    }

    public static Key typeAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return null;
        }
        BearingBlockBehavior behavior = (BearingBlockBehavior)(ce.behavior().getFirst(BearingBlockBehavior.class));
        return behavior == null ? null : behavior.type();
    }

    public static double rpmAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return 5.0;
        }
        BearingBlockBehavior behavior = (BearingBlockBehavior)(ce.behavior().getFirst(BearingBlockBehavior.class));
        return behavior == null ? 5.0 : behavior.rpm();
    }

    public static double suPerBlockAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return 2.0;
        }
        BearingBlockBehavior behavior = (BearingBlockBehavior)(ce.behavior().getFirst(BearingBlockBehavior.class));
        return behavior == null ? 2.0 : behavior.suPerBlock();
    }

    public double explosionProof() {
        return this.explosionProof;
    }

    void setExplosionProof(double explosionProof) {
        this.explosionProof = Math.max(0.0, Math.min(1.0, explosionProof));
    }

    public static double explosionProofAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return 0.0;
        }
        BearingBlockBehavior behavior = (BearingBlockBehavior)(ce.behavior().getFirst(BearingBlockBehavior.class));
        return behavior == null ? 0.0 : behavior.explosionProof();
    }

    public static final class InertController
    extends BlockEntityController {
        public InertController(BlockEntity blockEntity) {
            super(blockEntity);
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String kindArg = arguments.getOrDefault("kind", "linear").toString().toLowerCase(Locale.ROOT);
            Key type = Key.of((String)"polyfills", (String)kindArg);
            double rpm = Double.parseDouble(arguments.getOrDefault("rpm", 5.0).toString());
            double suPerBlock = Double.parseDouble(arguments.getOrDefault("suPerBlock", 2.0).toString());
            int distance = Integer.parseInt(arguments.getOrDefault("distance", 1).toString());
            double speed = Double.parseDouble(arguments.getOrDefault("speed", 1.0).toString());
            PistonBearingBehavior.Mode pistonMode = PistonBearingBehavior.Mode.fromString(arguments.getOrDefault("mode", "linear").toString());
            long rrDelay = Long.parseLong(arguments.getOrDefault("roundRobinDelayTicks", 100L).toString());
            String headBlock = Factory.str(arguments.get("headBlock"));
            String pipeNS = Factory.str(arguments.get("pipeNS"));
            String pipeWE = Factory.str(arguments.get("pipeWE"));
            String pipeUD = Factory.str(arguments.get("pipeUD"));
            HashMap<Key, List<MachineAttributes.Mod>> upgrades = new HashMap<Key, List<MachineAttributes.Mod>>();
            Object uObj = arguments.get("upgrades");
            if (uObj instanceof Map) {
                Map<Key, Object> uMap = (Map)uObj;
                for (Map.Entry e : uMap.entrySet()) {
                    List<MachineAttributes.Mod> mods = Factory.parseMods(e.getValue());
                    if (mods.isEmpty()) continue;
                    upgrades.put(Factory.key(String.valueOf(e.getKey())), mods);
                }
            }
            List<MachineBar> bars = MachineBars.parse(arguments.get("bars"));
            MachineMenuConfig menuConfig = MachineMenuConfig.parse(arg_0 -> ((ConfigSection)arguments).get(arg_0));
            BearingBlockBehavior behavior = new BearingBlockBehavior(block, type, rpm, suPerBlock, distance, speed, pistonMode, rrDelay, headBlock, pipeNS, pipeWE, pipeUD, upgrades, bars, menuConfig);
            behavior.setExplosionProof(Factory.parseProof(arguments.getOrDefault("explosionProof", 0.0)));
            return behavior;
        }

        private static String str(Object o) {
            return o == null ? null : o.toString();
        }

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
            }
            catch (NumberFormatException e) {
                return 0.0;
            }
        }

        private static List<MachineAttributes.Mod> parseMods(Object value) {
            ArrayList<MachineAttributes.Mod> out = new ArrayList<MachineAttributes.Mod>();
            if (value instanceof List) {
                List list = (List)value;
                for (Object o : list) {
                    Map m;
                    Object attr;
                    if (!(o instanceof Map) || (attr = (m = (Map)o).get("attribute")) == null) continue;
                    Object opObj = m.get("operation");
                    MachineAttributes.Operation op = MachineAttributes.parseOperation(opObj == null ? "add" : String.valueOf(opObj));
                    Object vObj = m.get("value");
                    double v = Utils.getAsDouble(vObj == null ? Integer.valueOf(0) : vObj, "value");
                    out.add(new MachineAttributes.Mod(Factory.key(String.valueOf(attr)), op, v));
                }
            }
            return out;
        }

        private static Key key(String s) {
            int i = s.indexOf(58);
            return i < 0 ? Key.of((String)"minecraft", (String)s) : Key.of((String)s.substring(0, i), (String)s.substring(i + 1));
        }
    }
}

