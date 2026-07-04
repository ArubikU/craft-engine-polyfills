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

/**
 * REAL CraftEngine block behavior for a contraption miner/drill block — mirrors
 * {@link BearingBlockBehavior}'s shape (a {@code BukkitBlockBehavior} subclass + a
 * {@link Factory} reading a {@link ConfigSection}, registered in
 * {@code block.BlockBehaviors.register()} under {@link #FACTORY_KEY}) and
 * {@code FanBlockBehavior}'s idiom for reading its own {@code facing} property straight off
 * the block definition.
 *
 * <p><b>Two RPM-consuming lives, one class.</b> A miner block can exist in two contexts:
 * <ol>
 *   <li><b>Standing in the real world</b> (not yet captured into a contraption): its
 *   {@link MinerController} is a real {@link BlockEntityController} implementing
 *   {@link RpmConsumer}, pull-scanning its 6 neighbor {@code BlockPos} each tick for the
 *   strongest adjacent {@link RpmProvider} — the exact idiom {@code CrusherBlockEntity}/
 *   {@code SmelteryBlockEntity} already use for a real motor. This means a miner block next
 *   to a real {@code GasMotorMk1BlockEntity} spins even before ever being glued into a
 *   contraption.</li>
 *   <li><b>Captured into a contraption</b>: {@link #buildMovementBehavior} (registered as a
 *   {@code MovementBehaviorRegistry.Factory} in {@code BlockBehaviors.register()}) builds a
 *   {@link MinerBehavior} — the real block/controller is removed from the world at capture
 *   time (same as every other captured block), so from then on {@link MinerBehavior} is fed
 *   via {@link RpmConsumer#setInputRpm} by whatever {@link RpmProvider} is in the same
 *   {@code ContraptionState} (typically a captured {@code RotationalBearingBehavior} — see
 *   that class's javadoc for the exact, documented-as-simplified propagation mechanism).</li>
 * </ol>
 *
 * <p>Config fields (all under {@code behavior: settings:}) — {@code minRpm}/{@code suCost}
 * added Task 3 (CONTRAPTIONS.md 2026-07-01 session), matching {@code CrusherBlockEntity}'s
 * config-driven RPM-threshold/SU-cost shape (there {@code minRpm}/{@code suCost} live
 * per-RECIPE via {@code AbstractProcessingRecipe#getMinRpm/getSuCost}; the miner has no
 * recipe concept, so they're plain per-block config here instead — same idea, simpler
 * source):
 * <pre>
 *   facing: (read from the block's own "facing" property, like FanBlockBehavior)
 *   gearRatio: 1.0   # multiplier applied to whatever rpm is delivered
 *   minRpm: 20.0     # effective rpm must reach this before the miner will cut at all
 *   suCost: 5.0      # stress units reported to the driving motor while operating
 * </pre>
 */
public class MinerBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    /** Registration key for {@code block.BlockBehaviors#register()}. */
    public static final Key FACTORY_KEY = Key.of("polyfills:miner_block");

    public static final Factory FACTORY = new Factory();

    private final Property<Direction> facingProperty;
    private final double gearRatio;
    private final double minRpm;
    private final double suCost;
    private int controllerId;

    public MinerBlockBehavior(BlockDefinition customBlock, Property<Direction> facingProperty, double gearRatio,
            double minRpm, double suCost) {
        super(customBlock);
        this.facingProperty = facingProperty;
        this.gearRatio = gearRatio;
        this.minRpm = minRpm;
        this.suCost = suCost;
    }

    public double gearRatio() {
        return gearRatio;
    }

    /** Effective rpm must reach this before the miner will cut at all — mirrors {@code AbstractProcessingRecipe#getMinRpm}. */
    public double minRpm() {
        return minRpm;
    }

    /** Stress units reported to the driving motor while operating — mirrors {@code AbstractProcessingRecipe#getSuCost}. */
    public double suCost() {
        return suCost;
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    /**
     * Right-click opens the miner's status GUI (Task 3, CONTRAPTIONS.md 2026-07-01 session) —
     * same trigger shape {@code MachineBlockBehavior#useWithoutItem} uses for
     * {@code AbstractMachineBlockEntity#getMenu()} (resolve the real {@code ServerLevel}/
     * {@code BlockPos} off the CE {@code UseOnContext}, look up the block entity, delegate to
     * its controller). <b>Deliberately simplified vs. {@code CrusherBlockEntity}'s full
     * {@code MachineMenu} framework</b> (upgrade/overclock sub-pages, live progress/rpm bars
     * driven by {@code MachineMenu#tick()}): the miner has no recipe/upgrade system to browse,
     * so {@link MinerController#openStatusMenu} is a plain read-only Bukkit inventory —
     * current rpm/SU/minRpm as item lore, nothing clickable. Matches the crusher's actual
     * MECHANISM (a real inventory GUI, not actionbar/particles/bossbar) without reproducing
     * machinery this block doesn't need.
     */
    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(
            net.momirealms.craftengine.core.world.context.UseOnContext context, ImmutableBlockState state) {
        try {
            ServerLevel level = ((org.bukkit.craftbukkit.CraftWorld) ((net.momirealms.craftengine.bukkit.world.BukkitWorld) context
                    .getLevel()).platformWorld()).getHandle();
            BlockPos pos = (BlockPos) net.momirealms.craftengine.bukkit.util.LocationUtils
                    .toBlockPos(context.getClickedPos());
            BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
            if (be != null && be.controller instanceof MinerController miner
                    && context.getPlayer() instanceof net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer
                    && cePlayer.platformPlayer() instanceof org.bukkit.entity.Player bukkit) {
                miner.openStatusMenu(bukkit);
                return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
            }
        } catch (Throwable ignored) {
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MinerController(blockEntity, this);
    }

    /**
     * Builds the {@link MinerBehavior} a captured occurrence of a miner block should get —
     * the {@code MovementBehaviorRegistry.Factory} entry point used by
     * {@code ContraptionCapture#resolveAutoBehaviors}. Returns {@code null} if {@code state}
     * isn't (or no longer resolves to) a CraftEngine miner block — same fail-open shape as
     * {@code MovementBehaviorRegistry.resolve} already expects.
     */
    public static MovementBehavior buildMovementBehavior(BlockPos localOffset, BlockState state) {
        ImmutableBlockState ce = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (ce == null || ce.isEmpty()) {
            return null;
        }
        MinerBlockBehavior behavior = ce.behavior().getFirst(MinerBlockBehavior.class);
        if (behavior == null) {
            return null;
        }
        Direction ceFacing = ce.get(behavior.facingProperty);
        net.minecraft.core.Direction facing = toNms(ceFacing);
        BlockPos targetOffset = localOffset.relative(facing);
        return new MinerBehavior(targetOffset, behavior.gearRatio, behavior.minRpm, behavior.suCost);
    }

    private static net.minecraft.core.Direction toNms(Direction dir) {
        return switch (dir) {
            case UP -> net.minecraft.core.Direction.UP;
            case DOWN -> net.minecraft.core.Direction.DOWN;
            case NORTH -> net.minecraft.core.Direction.NORTH;
            case SOUTH -> net.minecraft.core.Direction.SOUTH;
            case EAST -> net.minecraft.core.Direction.EAST;
            case WEST -> net.minecraft.core.Direction.WEST;
        };
    }

    /**
     * Real-world {@link RpmConsumer} companion — same pull-scan shape
     * {@code CrusherBlockEntity#tick} uses for a real adjacent motor. Only relevant while the
     * miner block is standing in the real world (or, since {@code ContraptionLevel} is a real
     * ticking {@code ServerLevel}, it would keep working there too IF the driving bearing had
     * a real block entity at an adjacent position — it normally doesn't, see
     * {@link RotationalBearingBehavior}'s javadoc for the simplified path actually used for
     * in-contraption propagation instead).
     */
    public static class MinerController extends BlockEntityController implements RpmConsumer {

        /** Same grace window {@code CrusherBlockEntity} uses (Task 3, CONTRAPTIONS.md 2026-07-01 session) — keeps
         * SU reporting stable across brief input gaps instead of flickering the motor's load every tick. */
        private static final int STRESS_GRACE_TICKS = 20;

        private final MinerBlockBehavior behavior;
        private float inputRpm;
        private RpmProvider activeMotor;
        private int stressGrace;

        public MinerController(BlockEntity blockEntity, MinerBlockBehavior behavior) {
            super(blockEntity);
            this.behavior = behavior;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world,
                ImmutableBlockState state) {
            return (BlockEntityTicker<C>) BlockEntityController
                    .createTickerHelper((BlockEntityTicker<MinerController>) MinerController::tick);
        }

        public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, MinerController self) {
            Object levelObj = world.world().minecraftWorld();
            if (!(levelObj instanceof ServerLevel level)) {
                return;
            }
            BlockPos nmsPos = new BlockPos(cePos.x(), cePos.y(), cePos.z());

            // Pull rpm from the strongest adjacent RpmProvider — mirrors
            // CrusherBlockEntity#tick's motor-selection logic.
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

            // Task 3 (CONTRAPTIONS.md 2026-07-01 session): config-driven SU cost, same grace-window
            // reporting shape as CrusherBlockEntity#tick (lines 465-474 of that class) — while
            // powered above minRpm, keep reporting suCost load to the motor for a few extra ticks
            // past the last "active" tick, instead of flickering the report on/off every tick.
            boolean operating = self.inputRpm * self.behavior.gearRatio() >= self.behavior.minRpm();
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

        public double gearRatio() {
            return behavior.gearRatio();
        }

        /** See {@code MinerBlockBehavior#useWithoutItem}'s javadoc for why this is a plain read-only inventory, not a full {@code MachineMenu}. */
        public void openStatusMenu(org.bukkit.entity.Player player) {
            org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 9,
                    net.kyori.adventure.text.Component.text("Miner Status"));
            boolean operating = inputRpm * behavior.gearRatio() >= behavior.minRpm();
            org.bukkit.inventory.ItemStack status = new org.bukkit.inventory.ItemStack(
                    operating ? org.bukkit.Material.LIME_STAINED_GLASS_PANE : org.bukkit.Material.RED_STAINED_GLASS_PANE);
            org.bukkit.inventory.meta.ItemMeta meta = status.getItemMeta();
            meta.displayName(net.kyori.adventure.text.Component.text(operating ? "Operating" : "Idle"));
            meta.lore(java.util.List.of(
                    net.kyori.adventure.text.Component.text("RPM in: " + inputRpm),
                    net.kyori.adventure.text.Component.text("Gear ratio: " + behavior.gearRatio()),
                    net.kyori.adventure.text.Component.text("Effective rpm: " + (inputRpm * behavior.gearRatio())),
                    net.kyori.adventure.text.Component.text("Min rpm required: " + behavior.minRpm()),
                    net.kyori.adventure.text.Component.text("SU cost while operating: " + behavior.suCost()),
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
                throw new IllegalArgumentException("Missing property 'facing' for miner_block");
            }
            double gearRatio = Double.parseDouble(arguments.getOrDefault("gearRatio", Double.valueOf(1.0)).toString());
            double minRpm = Double.parseDouble(arguments.getOrDefault("minRpm", Double.valueOf(20.0)).toString());
            double suCost = Double.parseDouble(arguments.getOrDefault("suCost", Double.valueOf(5.0)).toString());
            return new MinerBlockBehavior(block, facing, gearRatio, minRpm, suCost);
        }
    }
}
