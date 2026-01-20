package dev.arubik.craftengine.gas.behavior;

import java.util.Map;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.gas.GasCarrier;
import dev.arubik.craftengine.gas.GasCarrierImpl;
import dev.arubik.craftengine.gas.GasKeys;
import dev.arubik.craftengine.gas.GasStack;
import dev.arubik.craftengine.gas.GasTransferHelper;
import dev.arubik.craftengine.gas.GasType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.HorizontalDirection;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * GasPumpBehavior: bloque conectable que añade presión al gas almacenado.
 */
public class GasPumpBehavior extends ConnectableBlockBehavior
        implements GasCarrier, net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior {
    public static final Factory FACTORY = new Factory();
    private static final int PRESSURE_BOOST = 8;

    protected static final int CAPACITY = GasPipeBehavior.CAPACITY;
    protected static final int TRANSFER_PER_TICK = GasPipeBehavior.TRANSFER_PER_TICK * 10;

    public GasPumpBehavior(CustomBlock block,
            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, java.util.List.of(net.minecraft.core.Direction.UP, net.minecraft.core.Direction.DOWN),
                horizontalDirectionProperty,
                verticalDirectionProperty);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;

            Object horizontalProp = arguments.getOrDefault("horizontal-direction-property",
                    arguments.getOrDefault("horizontal_facing",
                            arguments.getOrDefault("facing", arguments.get("horizontal"))));
            if (horizontalProp instanceof String) {
                horizontalDirectionProperty = (String) horizontalProp;
            }

            Object verticalProp = arguments.getOrDefault("vertical-direction-property",
                    arguments.getOrDefault("vertical_facing",
                            arguments.getOrDefault("direction", arguments.get("vertical"))));
            if (verticalProp instanceof String) {
                verticalDirectionProperty = (String) verticalProp;
            }

            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> hProp = null;
            net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block
                            .getProperty(horizontalDirectionProperty);
                    hProp = tmp;
                } catch (ClassCastException ignored) {
                }
            }

            if (verticalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp2 = (net.momirealms.craftengine.core.block.properties.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(verticalDirectionProperty);
                    vProp = tmp2;
                } catch (ClassCastException ignored) {
                }
            }
            return new GasPumpBehavior(block, hProp, vProp);
        }
    }

    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    public <T extends BlockEntity> BlockEntityType<T> blockEntityType() {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    @Override
    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos,
            ImmutableBlockState state) {
        return new PersistentBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<T> createAsyncBlockEntityTicker(
            net.momirealms.craftengine.core.world.CEWorld world, ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType())
            return null;
        return (lvl, cePos, ceState, be) -> {
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) world.world().serverWorld();
            if (level == null || level.isClientSide())
                return;
            BlockPos pos = BlockPos.of(cePos.asLong());
            PersistentBlockEntity pbe = getBE(level, pos);
            if (pbe != null) {
                GasStack s = pbe.getOrDefault(GasKeys.GAS, GasStack.EMPTY);
                if (!s.isEmpty()) {
                    pbe.set(GasKeys.GAS, new GasStack(s.getType(), s.getAmount(), PRESSURE_BOOST));
                }
            }
            GasStack stored = getStoredGas(level, pos);

            int blockCd = 0, ioCd = 0;
            // Keys in PumpBehavior were FLUID_BLOCK_COOLDOWN etc. mapping to generic
            // string/int keys
            // Using ad-hoc strings for now to match logic
            if (pbe != null) {
                blockCd = pbe.getOrDefault(GasKeys.GAS_BLOCK_COOLDOWN, 0);
                ioCd = pbe.getOrDefault(GasKeys.GAS_IO_COOLDOWN, 0);
                if (blockCd > 0)
                    pbe.set(GasKeys.GAS_BLOCK_COOLDOWN, blockCd - 1);
                if (ioCd > 0)
                    pbe.set(GasKeys.GAS_IO_COOLDOWN, ioCd - 1);
            }

            // Gas Physics:
            // Pumping (Inputs): Gas rises, so try to suck from BELOW (DOWN) by default, or
            // inputs.
            // Pushing (Outputs): Gas rises, so push UP naturally. Pressure allows pushing
            // DOWN.

            // PUMP from logic (Suction)
            if (blockCd <= 0 && stored.getAmount() < CAPACITY) {
                // Try sucking from DOWN (below) first as gas rises into us
                tryDirectional(level, pos, Direction.DOWN, PumpAction.PUMP);
                // Also could try horizontal inputs?
            }

            // PUSH to logic (Ejection)
            if (ioCd <= 0 && !stored.isEmpty()) {
                // Try pushing UP (natural rise)
                tryDirectional(level, pos, Direction.UP, PumpAction.PUSH);
                // Or other directions if configured
            }
        };
    }

    public enum PumpAction {
        PUMP,
        PUSH
    }

    // Reuse logic structure but adapted for Gas
    protected boolean tryDirectional(Level level, BlockPos from, Direction dir, PumpAction action) {
        BlockPos target = offset(from, dir);
        try {
            net.minecraft.world.level.block.state.BlockState bs = level.getBlockState(from);
            dir = redirectDirection(dir, bs); // Handles rotateable blocks
            target = offset(from, dir);
        } catch (Throwable ignored) {
        }

        if (action == PumpAction.PUMP) {
            // Cannot collect area/blocks for gas usually? Unless gas blocks exist. Assume
            // carriers for now.
            // ...

            var opt = net.momirealms.craftengine.bukkit.util.BlockStateUtils
                    .getOptionalCustomBlockState(level.getBlockState(target));
            GasCarrier carrier = opt.map(cs -> cs.behavior() instanceof GasCarrier fc ? fc : null).orElse(null);

            Direction fromTarget = dev.arubik.craftengine.util.Utils.oppositeDirection(dir);

            if (carrier != null
                    && carrier.getAccessMode() == dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE) {
                GasStack stored = getStoredGas(level, from);
                int free = Math.max(0, CAPACITY - stored.getAmount());
                if (free > 0) {
                    int move = Math.min(TRANSFER_PER_TICK, free);
                    final GasStack[] extracted = { null };
                    int actualExtracted = carrier.extractGas(level, target, move, f -> extracted[0] = f, fromTarget);
                    if (actualExtracted > 0 && extracted[0] != null) {
                        int accepted = insertGas(level, from, extracted[0], null);
                        if (accepted < actualExtracted) {
                            GasStack remainder = new GasStack(extracted[0].getType(),
                                    actualExtracted - accepted, extracted[0].getPressure());
                            carrier.insertGas(level, target, remainder, fromTarget);
                        }
                        return accepted > 0;
                    }
                }
            }

        } else if (action == PumpAction.PUSH) {
            GasStack stored = getStoredGas(level, from);
            if (stored.isEmpty())
                return false;

            // Determine pressure physics
            int pressureModifier = 0;
            if (dir == Direction.DOWN) {
                if (stored.getPressure() <= 0)
                    return false; // Needs pressure to go DOWN
                pressureModifier = 1; // Losses pressure going DOWN
            } else if (dir == Direction.UP) {
                pressureModifier = 0; // Free to go UP
            } else {
                pressureModifier = 0;
            }

            // Try pushing to carrier
            boolean transferred = GasTransferHelper.push(level, from, dir, TRANSFER_PER_TICK, pressureModifier);
            if (transferred) {
                return true;
            }

            // If not transferred, check if we should VENT
            // Venting happens if target is Air and we are pushing

            if (level.getBlockState(target).isAir()) {
                // Vent gas
                int amount = Math.min(TRANSFER_PER_TICK, stored.getAmount());
                if (stored.getType().vent(level, target, amount)) {
                    // Remove vented gas
                    extractGas(level, from, amount, null, dir);
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof PersistentBlockEntity p)
            return p;
        return null;
    }

    public GasStack getStoredGas(Level level, BlockPos pos) {
        return GasCarrierImpl.getStoredGas(level, pos, GasKeys.GAS); // Need to expose/copy impl
    }

    public int insertGas(Level level, BlockPos pos, GasStack stack, Direction side) {
        // Delegate to Impl using generic keys or specific method
        return GasCarrierImpl.insertGas(level, pos, stack, CAPACITY, GasKeys.GAS);
    }

    public int extractGas(Level level, BlockPos pos, int max, java.util.function.Consumer<GasStack> drained,
            Direction side) {
        return GasCarrierImpl.extractGas(level, pos, max, drained, GasKeys.GAS);
    }

    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.PUMP_VALVE_CAN_TAKE;
    }

    private BlockPos offset(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case UP -> pos.above();
            case DOWN -> pos.below();
        };
    }

    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(UseOnContext context,
            ImmutableBlockState state) {
        // Similar display logic
        Level level = (Level) context.getLevel().serverWorld();
        BlockPos pos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
        BukkitServerPlayer bplayer = (BukkitServerPlayer) context.getPlayer();
        Player player = (Player) bplayer.serverPlayer();
        InteractionHand hand = context.getHand().equals(
                net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND;

        if (level.isClientSide())
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        GasStack stored = getStoredGas(level, pos);

        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            String gasName = stored.isEmpty() ? "gas.minecraft.empty"
                    : "gas.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + gasName + "> " +
                    "<gray>" + stored.getAmount() + "/" + CAPACITY + " mb</gray>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
