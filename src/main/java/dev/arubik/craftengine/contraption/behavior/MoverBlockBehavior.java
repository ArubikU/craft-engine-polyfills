package dev.arubik.craftengine.contraption.behavior;

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.block.property.Property;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.rotation.RpmConsumer;
import dev.arubik.craftengine.rotation.RpmProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * REAL CraftEngine block behavior for a contraption "mover" propulsion block
 * (ROADMAP-world-boundary.md §2, roadmap item #4 — the self-propelled thruster / self-moving
 * chassis). It is a near-verbatim clone of {@link MinerBlockBehavior}'s shape: a
 * {@code BukkitBlockBehavior} subclass + a {@link Factory} reading a {@link ConfigSection},
 * registered in {@code block.BlockBehaviors.register()} under {@link #FACTORY_KEY}, that ALSO
 * registers {@link #buildMovementBehavior} as a {@code MovementBehaviorRegistry.Factory} for the
 * same key — so a captured occurrence auto-attaches a {@link MoverBehavior} via
 * {@code ContraptionCapture#resolveAutoBehaviors}, the identical mechanism the miner uses. The
 * mover reads its own {@code facing} property straight off the block definition, exactly like
 * {@code FanBlockBehavior}/{@code MinerBlockBehavior}.
 *
 * <p><b>Augment, not assemble.</b> Unlike {@link BearingBlockBehavior}, a mover is NOT a bearing
 * and does NOT define an anchor or an assembly trigger — it only contributes propulsion once some
 * OTHER bearing has assembled the structure it is glued inside. See {@link MoverBehavior}'s class
 * javadoc for how this deliberately sidesteps the "one bearing per contraption" rule (the mover is
 * a plain velocity contributor, never a competing kinematics source).
 *
 * <p><b>Two lives, one class</b> (same split as the miner):
 * <ol>
 *   <li><b>Standing in the real world</b> (not yet captured): its {@link MoverController} is a
 *   real {@link BlockEntityController} implementing {@link RpmConsumer}, pull-scanning its 6
 *   neighbor {@code BlockPos} each tick for the strongest adjacent {@link RpmProvider} — the exact
 *   idiom {@code MinerBlockBehavior.MinerController}/{@code CrusherBlockEntity} use. A mover block
 *   next to a real motor "spins" (and reports SU load) even before being glued into a contraption,
 *   which is what drives its on/off status.</li>
 *   <li><b>Captured into a contraption</b>: {@link #buildMovementBehavior} builds a
 *   {@link MoverBehavior}; the real block/controller is removed at capture time (like every
 *   captured block), so from then on the behavior is fed via {@link RpmConsumer#setInputRpm} by
 *   whatever {@link RpmProvider} / bearing is in the same {@code ContraptionState}.</li>
 * </ol>
 *
 * <p>Config fields (all under {@code behavior:}), same shape as {@code MinerBlockBehavior}'s:
 * <pre>
 *   facing: (read from the block's own "facing" property, like FanBlockBehavior)
 *   speed: 1.0          # push speed in blocks/sec along the facing
 *   gearRatio: 1.0      # multiplier applied to whatever rpm is delivered
 *   minRpm: 0.0         # effective rpm floor before a powered mover pushes (requiresPower only)
 *   suCost: 2.0         # stress units added to the contraption's per-tick SU demand while pushing
 *   requiresPower: false # false = constant self-propelled push (MVP default); true = rpm/SU-gated like the miner
 * </pre>
 */
public class MoverBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    /** Registration key for {@code block.BlockBehaviors#register()}. */
    public static final Key FACTORY_KEY = Key.of("polyfills:mover_block");

    public static final Factory FACTORY = new Factory();

    /** Default push speed (blocks/sec) when config omits {@code speed:}. */
    public static final double DEFAULT_SPEED = 1.0;
    /** Default gear ratio when config omits {@code gearRatio:}. */
    public static final double DEFAULT_GEAR_RATIO = 1.0;
    /** Default rpm floor when config omits {@code minRpm:} (0 = no floor). */
    public static final double DEFAULT_MIN_RPM = 0.0;
    /** Default SU demand per pushing tick when config omits {@code suCost:}. */
    public static final double DEFAULT_SU_COST = 2.0;
    /** Default power gate: constant self-propelled push (see {@link MoverBehavior} class javadoc). */
    public static final boolean DEFAULT_REQUIRES_POWER = false;

    private final Property<Direction> facingProperty;
    /** On/off block-state switch (nullable — if the block YAML omits an {@code enabled} property, movers are always on). */
    private final Property<Boolean> enabledProperty;
    private final double speedBlocksPerSec;
    private final double gearRatio;
    private final double minRpm;
    private final double suCost;
    private final boolean requiresPower;
    private int controllerId;

    public MoverBlockBehavior(BlockDefinition customBlock, Property<Direction> facingProperty,
            Property<Boolean> enabledProperty, double speedBlocksPerSec,
            double gearRatio, double minRpm, double suCost, boolean requiresPower) {
        super(customBlock);
        this.facingProperty = facingProperty;
        this.enabledProperty = enabledProperty;
        this.speedBlocksPerSec = speedBlocksPerSec;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
        this.requiresPower = requiresPower;
    }

    public double speedBlocksPerSec() {
        return speedBlocksPerSec;
    }

    public double gearRatio() {
        return gearRatio;
    }

    /** Effective rpm floor before a {@code requiresPower} mover pushes — mirrors {@code MinerBlockBehavior#minRpm}. */
    public double minRpm() {
        return minRpm;
    }

    /** Stress units added to the contraption's per-tick SU demand while pushing — mirrors {@code MinerBlockBehavior#suCost}. */
    public double suCost() {
        return suCost;
    }

    /** When true the captured mover is rpm/SU-gated like the miner; when false it is a constant push. */
    public boolean requiresPower() {
        return requiresPower;
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    /**
     * Right-click a REAL (uncaptured) mover block to TOGGLE it on/off (2026-07-04 — "haz que el mover
     * se pueda apagar o desactivar"): flips the {@code enabled} block-state property, which
     * {@link #buildMovementBehavior} reads at capture, so a mover glued in disabled starts inert. A
     * SNEAK right-click instead opens the read-only status GUI (the original behavior). If the block
     * has no {@code enabled} property configured, falls back to just opening the status GUI.
     */
    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(
            net.momirealms.craftengine.core.world.context.UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = ((org.bukkit.craftbukkit.CraftWorld) ((net.momirealms.craftengine.bukkit.world.BukkitWorld) context
                    .getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos) net.momirealms.craftengine.bukkit.util.LocationUtils
                    .toBlockPos(context.getClickedPos());
            if (!(context.getPlayer() instanceof net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer)
                    || !(cePlayer.platformPlayer() instanceof org.bukkit.entity.Player bukkit)) {
                return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
            }
            // Plain right-click toggles enabled; sneak opens the status GUI.
            if (enabledProperty != null && !bukkit.isSneaking() && state != null && !state.isEmpty()) {
                boolean cur = Boolean.TRUE.equals(state.get(enabledProperty));
                BlockState toggled = (BlockState) ImmutableBlockState
                        .with(state, enabledProperty, Boolean.valueOf(!cur)).customBlockState().minecraftState();
                level.setBlock(pos, toggled, 3);
                bukkit.playSound(bukkit.getLocation(),
                        !cur ? org.bukkit.Sound.BLOCK_LEVER_CLICK : org.bukkit.Sound.BLOCK_LEVER_CLICK, 0.7f, !cur ? 1.2f : 0.8f);
                bukkit.sendMessage(!cur ? "§aMover enabled." : "§7Mover disabled.");
                return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
            }
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && be.controller instanceof MoverController mover) {
                mover.openStatusMenu(bukkit);
                return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MoverController(blockEntity, this);
    }

    /**
     * Builds the {@link MoverBehavior} a captured occurrence of a mover block should get — the
     * {@code MovementBehaviorRegistry.Factory} entry point used by
     * {@code ContraptionCapture#resolveAutoBehaviors}. Returns {@code null} if {@code state} isn't
     * (or no longer resolves to) a CraftEngine mover block — same fail-open shape
     * {@code MovementBehaviorRegistry.resolve} and {@code MinerBlockBehavior#buildMovementBehavior}
     * already use.
     */
    public static MovementBehavior buildMovementBehavior(BlockPos localOffset, BlockState state) {
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return null;
        }
        MoverBlockBehavior behavior = ce.behavior().getFirst(MoverBlockBehavior.class);
        if (behavior == null) {
            return null;
        }
        Direction ceFacing = ce.get(behavior.facingProperty);
        Vec3 localDir = toLocalDir(ceFacing);
        // Capture the on/off switch state (2026-07-04) — a disabled mover is captured inert. If the
        // block has no `enabled` property configured, movers are always on.
        boolean enabled = behavior.enabledProperty == null || Boolean.TRUE.equals(ce.get(behavior.enabledProperty));
        return new MoverBehavior(localDir, behavior.speedBlocksPerSec, behavior.gearRatio, behavior.minRpm,
                behavior.suCost, behavior.requiresPower, enabled);
    }

    /** CraftEngine {@link Direction} to a unit local push vector — matches {@code BearingBlockBehavior#facingToVec}. */
    private static Vec3 toLocalDir(Direction dir) {
        return switch (dir) {
            case UP -> new Vec3(0, 1, 0);
            case DOWN -> new Vec3(0, -1, 0);
            case NORTH -> new Vec3(0, 0, -1);
            case SOUTH -> new Vec3(0, 0, 1);
            case EAST -> new Vec3(1, 0, 0);
            case WEST -> new Vec3(-1, 0, 0);
        };
    }

    /**
     * Real-world {@link RpmConsumer} companion — same pull-scan shape
     * {@code MinerBlockBehavior.MinerController#tick} uses for a real adjacent motor. Only relevant
     * while the mover block is standing in the real world (uncaptured); once captured, the
     * {@link MoverBehavior} is fed via {@code setInputRpm} instead.
     */
    public static class MoverController extends BlockEntityController implements RpmConsumer {

        /** Same grace window {@code MinerBlockBehavior.MinerController} uses — keeps SU reporting stable across brief input gaps. */
        private static final int STRESS_GRACE_TICKS = 20;

        private final MoverBlockBehavior behavior;
        private float inputRpm;
        private RpmProvider activeMotor;
        private int stressGrace;

        public MoverController(BlockEntity blockEntity, MoverBlockBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world,
                ImmutableBlockState state) {
            return (BlockEntityTicker<C>) BlockEntityController
                    .createTickerHelper((BlockEntityTicker<MoverController>) MoverController::tick);
        }

        public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, MoverController self) {
            Object levelObj = world.world().minecraftWorld();
            if (!(levelObj instanceof ServerLevel level)) {
                return;
            }
            BlockPos nmsPos = new BlockPos(cePos.x(), cePos.y(), cePos.z());

            // Pull rpm from the strongest adjacent RpmProvider — mirrors MinerController#tick.
            float bestPotential = 0f;
            float actual = 0f;
            RpmProvider best = null;
            for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
                BlockPos neighborPos = nmsPos.relative(d);
                BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, neighborPos);
                if (be != null && be.controller instanceof RpmProvider provider && provider.isRpmSource()
                        && provider.potentialRpm() > bestPotential) {
                    net.momirealms.craftengine.core.world.BlockPos consumerPos =
                            new net.momirealms.craftengine.core.world.BlockPos(cePos.x(), cePos.y(), cePos.z());
                    if (!provider.rpmReaches(consumerPos)) {
                        continue;
                    }
                    bestPotential = provider.potentialRpm();
                    actual = provider.getRpm();
                    best = provider;
                }
            }
            self.inputRpm = actual;
            self.activeMotor = best;

            // Config-driven SU cost with the same grace-window reporting shape as MinerController#tick:
            // while powered above minRpm, keep reporting suCost load to the motor for a few extra ticks
            // past the last active tick instead of flickering the report on/off every tick.
            boolean operating = self.inputRpm * self.behavior.gearRatio() >= self.behavior.minRpm()
                    && self.inputRpm > 0;
            if (operating) {
                self.stressGrace = STRESS_GRACE_TICKS;
            }
            if (self.stressGrace > 0 && self.activeMotor != null) {
                self.activeMotor.reportStressLoad((float) self.behavior.suCost());
                self.stressGrace--;
            }
        }

        @Override
        public void setInputRpm(float rpm) {
            this.inputRpm = rpm;
        }

        @Override
        public float getInputRpm() {
            return inputRpm;
        }

        /** See {@code MinerBlockBehavior.MinerController#openStatusMenu} for why this is a plain read-only inventory. */
        public void openStatusMenu(org.bukkit.entity.Player player) {
            org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 9,
                    net.kyori.adventure.text.Component.text("Mover Status"));
            boolean powered = inputRpm * behavior.gearRatio() >= behavior.minRpm() && inputRpm > 0;
            boolean pushing = powered || !behavior.requiresPower();
            org.bukkit.inventory.ItemStack status = new org.bukkit.inventory.ItemStack(
                    pushing ? org.bukkit.Material.LIME_STAINED_GLASS_PANE : org.bukkit.Material.RED_STAINED_GLASS_PANE);
            org.bukkit.inventory.meta.ItemMeta meta = status.getItemMeta();
            meta.displayName(net.kyori.adventure.text.Component.text(pushing ? "Pushing" : "Idle"));
            meta.lore(java.util.List.of(
                    net.kyori.adventure.text.Component.text("Speed: " + behavior.speedBlocksPerSec() + " blocks/s"),
                    net.kyori.adventure.text.Component.text("Requires power: " + behavior.requiresPower()),
                    net.kyori.adventure.text.Component.text("RPM in: " + inputRpm),
                    net.kyori.adventure.text.Component.text("Gear ratio: " + behavior.gearRatio()),
                    net.kyori.adventure.text.Component.text("Effective rpm: " + (inputRpm * behavior.gearRatio())),
                    net.kyori.adventure.text.Component.text("Min rpm required: " + behavior.minRpm()),
                    net.kyori.adventure.text.Component.text("SU cost while pushing: " + behavior.suCost()),
                    net.kyori.adventure.text.Component
                            .text(activeMotor != null ? "Driven by a real adjacent motor" : "No motor detected")));
            status.setItemMeta(meta);
            inv.setItem(4, status);
            player.openInventory(inv);
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        @SuppressWarnings("unchecked")
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Property<Direction> facing = (Property<Direction>) block.getProperty("facing");
            if (facing == null) {
                throw new IllegalArgumentException("Missing property 'facing' for mover_block");
            }
            // Optional on/off switch property (2026-07-04). Absent = movers always on.
            Property<Boolean> enabled = (Property<Boolean>) block.getProperty("enabled");
            double speed = Double.parseDouble(arguments.getOrDefault("speed", Double.valueOf(DEFAULT_SPEED)).toString());
            double gearRatio = Double
                    .parseDouble(arguments.getOrDefault("gearRatio", Double.valueOf(DEFAULT_GEAR_RATIO)).toString());
            double minRpm = Double
                    .parseDouble(arguments.getOrDefault("minRpm", Double.valueOf(DEFAULT_MIN_RPM)).toString());
            double suCost = Double
                    .parseDouble(arguments.getOrDefault("suCost", Double.valueOf(DEFAULT_SU_COST)).toString());
            boolean requiresPower = Boolean.parseBoolean(
                    arguments.getOrDefault("requiresPower", Boolean.valueOf(DEFAULT_REQUIRES_POWER)).toString());
            return new MoverBlockBehavior(block, facing, enabled, speed, gearRatio, minRpm, suCost, requiresPower);
        }
    }
}
