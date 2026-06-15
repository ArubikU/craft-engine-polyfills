package dev.arubik.craftengine.conveyor;

import net.minecraft.server.level.ServerLevel;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Block behavior backing a {@link ConveyorBlockEntity}. The belt is driven by
 * BLOCK-STATE PROPERTIES ({@code facing}/{@code slope}/{@code part}), not by
 * config — the factory only carries an OPTIONAL default facing for blocks that
 * don't define the {@code facing} property.
 *
 * <p>Right-click on an END segment extends the belt; breaking a segment shortens
 * (END) or tears down the whole belt (START/MIDDLE) — see
 * {@link ConveyorBlockEntity#extend} / {@link ConveyorBlockEntity#onBroken}.</p>
 *
 * <p>Registration: expose {@link #FACTORY} under {@link #POLYFILL_CONVEYOR}; the
 * main thread wires it in {@code block/BlockBehaviors.java}.</p>
 */
public class ConveyorBehavior extends dev.arubik.craftengine.util.NmsBlockBehavior implements EntityBlock {

    public static final Key POLYFILL_CONVEYOR = Key.of("polyfills:conveyor");
    public static final Factory FACTORY = new Factory();

    private final Direction defaultFacing;
    private final int baseTravelTicks;
    private final float baseRpm;
    private final float stressImpact;
    private final int slots;
    private int controllerId;

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing) {
        this(block, defaultFacing, ConveyorBlockEntity.BASE_TRAVEL_TICKS, ConveyorBlockEntity.BASE_RPM,
                ConveyorBlockEntity.DEFAULT_STRESS_IMPACT, ConveyorBlockEntity.DEFAULT_SLOTS);
    }

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing, int baseTravelTicks, float baseRpm) {
        this(block, defaultFacing, baseTravelTicks, baseRpm, ConveyorBlockEntity.DEFAULT_STRESS_IMPACT,
                ConveyorBlockEntity.DEFAULT_SLOTS);
    }

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing, int baseTravelTicks, float baseRpm,
            float stressImpact) {
        this(block, defaultFacing, baseTravelTicks, baseRpm, stressImpact, ConveyorBlockEntity.DEFAULT_SLOTS);
    }

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing, int baseTravelTicks, float baseRpm,
            float stressImpact, int slots) {
        super(block);
        this.defaultFacing = defaultFacing == null ? Direction.NORTH : defaultFacing;
        this.baseTravelTicks = baseTravelTicks > 0 ? baseTravelTicks : ConveyorBlockEntity.BASE_TRAVEL_TICKS;
        this.baseRpm = baseRpm > 0 ? baseRpm : ConveyorBlockEntity.BASE_RPM;
        this.stressImpact = stressImpact >= 0 ? stressImpact : ConveyorBlockEntity.DEFAULT_STRESS_IMPACT;
        this.slots = slots > 0 ? slots : ConveyorBlockEntity.DEFAULT_SLOTS;
    }

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new ConveyorBlockEntity(blockEntity, defaultFacing, baseTravelTicks, baseRpm, stressImpact, slots);
    }

    // ---------------- right-click: put / take the slot ----------------

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        CEWorld world = context.getLevel().ceWorld();
        if (world == null)
            return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos();
        ConveyorBlockEntity be = ConveyorBlockEntity.conveyorAt(world, pos);
        if (be == null)
            return InteractionResult.PASS;

        net.momirealms.craftengine.core.entity.player.Player cePlayer = context.getPlayer();
        if (cePlayer == null || !(cePlayer.platformPlayer() instanceof org.bukkit.entity.Player player))
            return InteractionResult.PASS;

        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        org.bukkit.inventory.ItemStack hand = inv.getItemInMainHand();
        boolean handEmpty = hand == null || hand.getType().isAir();

        if (handEmpty) {
            // Take: give the slot stack to the player.
            org.bukkit.inventory.ItemStack slot = be.takeSlot();
            if (slot == null || slot.getType().isAir())
                return InteractionResult.PASS;
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> overflow = inv.addItem(slot);
            for (org.bukkit.inventory.ItemStack left : overflow.values())
                player.getWorld().dropItem(player.getLocation(), left);
            return InteractionResult.SUCCESS_AND_CANCEL;
        }

        // Hand has an item: refuse conveyor block items (those place/extend the belt,
        // they must not be stuffed into the slot). Resolve item->block and check for a
        // ConveyorBehavior — the config item id (demo:conveyor) is NOT the behavior key.
        net.momirealms.craftengine.core.util.Key handId =
                net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(hand);
        if (handId != null) {
            net.momirealms.craftengine.core.block.BlockDefinition handDef =
                    net.momirealms.craftengine.bukkit.api.CraftEngineBlocks.byId(handId);
            if (handDef != null && handDef.defaultState() != null) {
                Object hb = handDef.defaultState().behavior();
                boolean handIsConveyor = hb instanceof ConveyorBehavior
                        || (hb instanceof net.momirealms.craftengine.bukkit.block.behavior.DualBlockBehavior dual
                                && dual.getFirst(ConveyorBehavior.class) != null)
                        || (hb instanceof net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior comp
                                && comp.getFirst(ConveyorBehavior.class) != null);
                if (handIsConveyor)
                    return InteractionResult.PASS;
            }
        }

        // Put: merge into the slot if same type, else swap with the slot content.
        org.bukkit.inventory.ItemStack leftover = be.putSlot(hand);
        inv.setItemInMainHand(leftover);
        return InteractionResult.SUCCESS_AND_CANCEL;
    }

    // ---------------- break ----------------

    /** Positions whose break was already handled by {@link ConveyorBreakListener}. */
    private static final java.util.Set<String> LISTENER_HANDLED =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    public static void markListenerHandled(int x, int y, int z) {
        LISTENER_HANDLED.add(x + "," + y + "," + z);
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, net.minecraft.world.level.Level level,
            net.minecraft.core.BlockPos nmsPos, net.minecraft.world.level.block.state.BlockState oldState,
            Boolean movedByPiston) {
        // The Bukkit break listener already ran the teardown for this position.
        if (LISTENER_HANDLED.remove(nmsPos.getX() + "," + nmsPos.getY() + "," + nmsPos.getZ()))
            return;
        try {
            CEWorld world = new BukkitWorld(((ServerLevel) level).getWorld()).storageWorld();
            if (world == null)
                return;
            BlockPos pos = new BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());

            // Prefer the live controller (still resolvable right after removal in CE).
            ConveyorBlockEntity be = ConveyorBlockEntity.conveyorAt(world, pos);
            if (be != null) {
                be.onBroken(world, pos, be.facing());
                return;
            }

            // Fallback: reconstruct facing/slope from the removed state so the
            // linked-list teardown can still run from neighbours.
            ImmutableBlockState immutable = BlockStateUtils.getOptionalCustomBlockState(oldState).orElse(null);
            if (immutable == null)
                return;
            BlockEntity transientBe = new BlockEntity(pos, immutable);
            ConveyorBlockEntity transientController = new ConveyorBlockEntity(transientBe, defaultFacing);
            transientController.onBroken(world, pos, transientController.facing());
        } catch (Throwable ignored) {
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            // facing is OPTIONAL now: states drive the belt. Kept only as a fallback
            // default for blocks that do not declare a "facing" property.
            Direction facing = Direction.NORTH;
            Object f = arguments.get("facing");
            if (f != null) {
                try {
                    facing = Direction.valueOf(f.toString().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                }
            }
            // Tunables: how many ticks an item takes to cross one segment at base
            // speed, and the reference rpm. Drive both transfer speed AND the visual
            // belt pace (the polyfill scales movement by effectiveRpm/baseRpm).
            int baseTravelTicks = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("base-travel-ticks", ConveyorBlockEntity.BASE_TRAVEL_TICKS),
                    "base-travel-ticks");
            float baseRpm = (float) dev.arubik.craftengine.util.Utils.getAsDouble(
                    arguments.getOrDefault("base-rpm", (double) ConveyorBlockEntity.BASE_RPM), "base-rpm");
            // Stress imposed per belt segment (SU). The line stalls if its total
            // exceeds the driving motor's stress capacity.
            float stressImpact = dev.arubik.craftengine.util.Utils.getAsFloat(
                    arguments.getOrDefault("stress-impact", ConveyorBlockEntity.DEFAULT_STRESS_IMPACT),
                    "stress-impact");
            // How many items this belt segment carries at once (each at its own
            // position; spacing = 1/slots). Drives the multi-item transport.
            int slots = dev.arubik.craftengine.util.Utils.getAsInt(
                    arguments.getOrDefault("slots", ConveyorBlockEntity.DEFAULT_SLOTS), "slots");
            return new ConveyorBehavior(block, facing, baseTravelTicks, baseRpm, stressImpact, slots);
        }
    }
}
