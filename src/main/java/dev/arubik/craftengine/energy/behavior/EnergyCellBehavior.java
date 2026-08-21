package dev.arubik.craftengine.energy.behavior;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.energy.EnergyCarrier;
import dev.arubik.craftengine.energy.EnergyCarrierImpl;
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
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * A finite CraftEnergy battery/buffer block — a demo consumer/storage endpoint for the cable
 * network, mirroring {@code GasTankBehavior} without its vertical multiblock stacking (a battery
 * doesn't stack the way a fluid/gas tank column does).
 */
public class EnergyCellBehavior extends ConnectableBlockBehavior implements EntityBlock, EnergyCarrier {

    public static final Key FACTORY_KEY = Key.of("polyfills:energy_cell");
    public static final Factory FACTORY = new Factory();

    public final int capacity;

    public EnergyCellBehavior(BlockDefinition block, int capacity,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty) {
        super(block, java.util.List.of(Direction.values()), horizontalDirectionProperty, verticalDirectionProperty);
        this.capacity = capacity;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        @SuppressWarnings("unchecked")
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> hProp = null;
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vProp = null;
            try {
                Object h = arguments.getOrDefault("horizontal", null);
                if (h instanceof String hs)
                    hProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                            .getProperty(hs);
            } catch (ClassCastException ignored) {
            }
            try {
                vProp = (net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction>) block
                        .getProperty("vertical");
            } catch (ClassCastException ignored) {
            }
            int cap = Integer.parseInt(String.valueOf(arguments.getOrDefault("capacity", 100000)));
            return new EnergyCellBehavior(block, cap, hProp, vProp);
        }
    }

    @Override
    public int getStoredEnergy(Level level, BlockPos pos) {
        return EnergyCarrierImpl.getStoredEnergy(level, pos);
    }

    @Override
    public int insertEnergy(Level level, BlockPos pos, int amount, Direction side) {
        return EnergyCarrierImpl.insertEnergy(level, pos, amount, capacity);
    }

    @Override
    public int extractEnergy(Level level, BlockPos pos, int max, Direction side) {
        return EnergyCarrierImpl.extractEnergy(level, pos, max);
    }

    @Override
    public long getEnergyCapacity(Level level, BlockPos pos) {
        return capacity;
    }

    @Override
    public dev.arubik.craftengine.util.TransferAccessMode getAccessMode() {
        return dev.arubik.craftengine.util.TransferAccessMode.ANYONE_CAN_TAKE;
    }

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new PersistentBlockEntity(blockEntity);
    }

    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(UseOnContext context,
            ImmutableBlockState state) {
        Level level = (Level) context.getLevel().minecraftWorld();
        BlockPos pos = (BlockPos) LocationUtils.toBlockPos(context.getClickedPos());
        BukkitServerPlayer bplayer = (BukkitServerPlayer) context.getPlayer();
        Player player = (Player) bplayer.serverPlayer();
        InteractionHand hand = context.getHand().equals(
                net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND) ? InteractionHand.MAIN_HAND
                        : InteractionHand.OFF_HAND;

        if (level.isClientSide())
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            int stored = getStoredEnergy(level, pos);
            double fill = capacity > 0 ? stored / (double) capacity : 0;
            Component msg = MiniMessage.miniMessage().deserialize(
                    "<yellow>⚡</yellow> <gray>" + stored + "/" + capacity + " CE</gray> "
                            + "<dark_gray>·</dark_gray> <yellow>" + (int) Math.round(fill * 100) + "%</yellow>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
