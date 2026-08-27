package dev.arubik.craftengine.conveyor.belt;

import dev.arubik.craftengine.conveyor.belt.ConveyorBlockEntity;
import dev.arubik.craftengine.util.CeWorlds;
import dev.arubik.craftengine.util.NmsBlockBehavior;
import dev.arubik.craftengine.util.Utils;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.block.behavior.CompositeBlockBehavior;
import net.momirealms.craftengine.bukkit.block.behavior.DualBlockBehavior;
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
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.CEWorld;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ConveyorBehavior
extends NmsBlockBehavior
implements EntityBlock, dev.arubik.craftengine.energy.EnergyCarrier {
    public static final Key POLYFILL_CONVEYOR = Key.of((String)"polyfills:conveyor");
    public static final Factory FACTORY = new Factory();
    private final Direction defaultFacing;
    private final int baseTravelTicks;
    private final float baseRpm;
    private final float stressImpact;
    private final int slots;
    private final Key blockId;
    private int controllerId;
    private static final Set<String> LISTENER_HANDLED = ConcurrentHashMap.newKeySet();

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing) {
        this(block, defaultFacing, 16, 64.0f, 4.0f, 4);
    }

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing, int baseTravelTicks, float baseRpm) {
        this(block, defaultFacing, baseTravelTicks, baseRpm, 4.0f, 4);
    }

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing, int baseTravelTicks, float baseRpm, float stressImpact) {
        this(block, defaultFacing, baseTravelTicks, baseRpm, stressImpact, 4);
    }

    public ConveyorBehavior(BlockDefinition block, Direction defaultFacing, int baseTravelTicks, float baseRpm, float stressImpact, int slots) {
        super(block);
        this.defaultFacing = defaultFacing == null ? Direction.NORTH : defaultFacing;
        this.baseTravelTicks = baseTravelTicks > 0 ? baseTravelTicks : 16;
        this.baseRpm = baseRpm > 0.0f ? baseRpm : 64.0f;
        this.stressImpact = stressImpact >= 0.0f ? stressImpact : 4.0f;
        this.slots = slots > 0 ? slots : 4;
        this.blockId = block.id();
    }

    // ---- EnergyCarrier: only meaningful for ELECTRIC belt_types; NONE/KINETIC has 0 capacity,
    // so insert/extract are inert no-ops. Mirrors EnergyCableBehavior's EnergyCarrierImpl delegation.

    private BeltType beltType() {
        return BeltType.byBlockId(this.blockId);
    }

    private int energyCapacity() {
        BeltType type = beltType();
        return type != null && type.energyType() == BeltType.EnergyKind.ELECTRIC ? type.energyCapacity() : 0;
    }

    @Override
    public int getStoredEnergy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return dev.arubik.craftengine.energy.EnergyCarrierImpl.getStoredEnergy(level, pos);
    }

    @Override
    public int insertEnergy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, int amount, net.minecraft.core.Direction side) {
        return dev.arubik.craftengine.energy.EnergyCarrierImpl.insertEnergy(level, pos, amount, energyCapacity());
    }

    @Override
    public int extractEnergy(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, int max, net.minecraft.core.Direction side) {
        return dev.arubik.craftengine.energy.EnergyCarrierImpl.extractEnergy(level, pos, max);
    }

    @Override
    public long getEnergyCapacity(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return energyCapacity();
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    public void initControllerId(int id) {
        this.controllerId = id;
    }

    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new ConveyorBlockEntity(blockEntity, this.defaultFacing, this.baseTravelTicks, this.baseRpm, this.stressImpact, this.slots);
    }

    private static ConveyorBlockEntity controllerAt(Object levelObj, Object posObj) {
        try {
            BlockEntityController blockEntityController;
            CEWorld world = new BukkitWorld((World)((ServerLevel)levelObj).getWorld()).storageWorld();
            if (world == null) {
                return null;
            }
            net.momirealms.craftengine.core.world.BlockPos pos = LocationUtils.fromBlockPos(posObj);
            BlockEntity be = world.getBlockEntityAtIfLoaded(pos);
            if (be != null && (blockEntityController = be.controller) instanceof ConveyorBlockEntity) {
                ConveyorBlockEntity c = (ConveyorBlockEntity)blockEntityController;
                return c;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    public boolean hasAnalogOutputSignal(Object thisBlock, Object[] args) {
        return true;
    }

    public int getAnalogOutputSignal(Object thisBlock, Object[] args) {
        ConveyorBlockEntity c = ConveyorBehavior.controllerAt(args[1], args[2]);
        return c == null ? 0 : c.analogSignal();
    }

    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        BlockDefinition handDef;
        boolean handEmpty;
        Object object;
        CEWorld world = context.getLevel().ceWorld();
        if (world == null) {
            return InteractionResult.PASS;
        }
        net.momirealms.craftengine.core.world.BlockPos pos = context.getClickedPos();
        ConveyorBlockEntity be = ConveyorBlockEntity.conveyorAt(world, pos);
        if (be == null) {
            return InteractionResult.PASS;
        }
        net.momirealms.craftengine.core.entity.player.Player cePlayer = context.getPlayer();
        if (cePlayer == null || !((object = cePlayer.platformPlayer()) instanceof Player)) {
            return InteractionResult.PASS;
        }
        Player player = (Player)object;
        PlayerInventory inv = player.getInventory();
        ItemStack hand = inv.getItemInMainHand();
        boolean bl = handEmpty = hand == null || hand.getType().isAir();
        if (handEmpty) {
            ItemStack slot = be.takeSlot();
            if (slot == null || slot.getType().isAir()) {
                return InteractionResult.PASS;
            }
            HashMap<Integer, ItemStack> overflow = inv.addItem(slot);
            for (ItemStack left : overflow.values()) {
                player.getWorld().dropItem(player.getLocation(), left);
            }
            return InteractionResult.SUCCESS_AND_CANCEL;
        }
        Key handId = CraftEngineItems.getCustomItemId((ItemStack)hand);
        if (handId != null && (handDef = CraftEngineBlocks.byId((Key)handId)) != null && handDef.defaultState() != null) {
            CompositeBlockBehavior comp;
            DualBlockBehavior dual;
            boolean handIsConveyor;
            BlockBehavior hb = handDef.defaultState().behavior();
            boolean bl2 = handIsConveyor = hb instanceof ConveyorBehavior || hb instanceof DualBlockBehavior && (dual = (DualBlockBehavior)hb).getFirst(ConveyorBehavior.class) != null || hb instanceof CompositeBlockBehavior && (comp = (CompositeBlockBehavior)hb).getFirst(ConveyorBehavior.class) != null;
            if (handIsConveyor) {
                return InteractionResult.PASS;
            }
        }
        ItemStack leftover = be.putSlot(hand);
        inv.setItemInMainHand(leftover);
        return InteractionResult.SUCCESS_AND_CANCEL;
    }

    public static void markListenerHandled(int x, int y, int z) {
        LISTENER_HANDLED.add(x + "," + y + "," + z);
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Level level, BlockPos nmsPos, BlockState oldState, Boolean movedByPiston) {
        if (LISTENER_HANDLED.remove(nmsPos.getX() + "," + nmsPos.getY() + "," + nmsPos.getZ())) {
            return;
        }
        try {
            CEWorld world = CeWorlds.of((World)((ServerLevel)level).getWorld()).storageWorld();
            if (world == null) {
                return;
            }
            net.momirealms.craftengine.core.world.BlockPos pos = new net.momirealms.craftengine.core.world.BlockPos(nmsPos.getX(), nmsPos.getY(), nmsPos.getZ());
            ConveyorBlockEntity be = ConveyorBlockEntity.conveyorAt(world, pos);
            if (be != null) {
                be.onBroken(world, pos, be.facing());
                return;
            }
            ImmutableBlockState immutable = BlockStateUtils.getOptionalCustomBlockState(oldState).orElse(null);
            if (immutable == null) {
                return;
            }
            BlockEntity transientBe = new BlockEntity(pos, immutable);
            ConveyorBlockEntity transientController = new ConveyorBlockEntity(transientBe, this.defaultFacing);
            transientController.onBroken(world, pos, transientController.facing());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        // Speed/slots now come from belt_types/*.json (see BeltType), not YAML args — mirrors
        // pipe_types. "facing" stays a YAML default (placement, not a belt-engine tunable).
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Direction facing = Direction.NORTH;
            Object f = arguments.get("facing");
            if (f != null) {
                try {
                    facing = Direction.valueOf((String)f.toString().toUpperCase());
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            BeltType type = null;
            Object configured = arguments.get("belt_type");
            if (configured != null)
                type = BeltType.byName(String.valueOf(configured));
            if (type == null)
                type = BeltType.byBlockId(block.id());
            if (type == null)
                throw new IllegalStateException(
                        "conveyor block '" + block.id() + "' has no matching belt_types/*.json entry"
                                + " (set behavior.belt_type or bind a belt_types block id to it)");
            return new ConveyorBehavior(block, facing, type.baseTravelTicks(), type.baseRpm(),
                    type.stressImpact(), type.slots());
        }
    }
}

