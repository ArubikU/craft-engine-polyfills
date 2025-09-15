package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;

/**
 * PumpBehavior: bloque conectable que añade presión al fluido almacenado.
 * Reimplementa mínima lógica de transferencia usando composición con la interfaz FluidCarrier.
 */
public class PumpBehavior extends ConnectableBlockBehavior implements FluidCarrier, net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior {
    public static final Factory FACTORY = new Factory();
    private static final int PRESSURE_BOOST = 8;

    private static final int CAPACITY = PipeBehavior.CAPACITY; // uniformidad
    private static final int TRANSFER_PER_TICK = PipeBehavior.TRANSFER_PER_TICK*10;

    public PumpBehavior(CustomBlock block, net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
                        net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> verticalDirectionProperty) {
        super(block, java.util.List.of(Direction.UP,Direction.DOWN), horizontalDirectionProperty, verticalDirectionProperty);
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public net.momirealms.craftengine.core.block.BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {

            // Leer las propiedades de dirección desde los argumentos
            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;
            
            // Aceptar claves alternativas comunes
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
            net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block.getProperty(horizontalDirectionProperty);
                    hProp = tmp;
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            
            if (verticalDirectionProperty != null) {
                try {
                    @SuppressWarnings("unchecked")
                    var tmp2 = (net.momirealms.craftengine.core.block.properties.EnumProperty<Direction>) block.getProperty(verticalDirectionProperty);
                    vProp = tmp2;
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            return new PumpBehavior(block,hProp,vProp);
        }
    }

    // --- Block Entity infra ---
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType() {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos pos, ImmutableBlockState state) {
        return new PersistentBlockEntity(pos, state);
    }

    // --- Ticker oficial ---
    @Override
    public <T extends BlockEntity> net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker<T> createBlockEntityTicker(net.momirealms.craftengine.core.world.CEWorld world, ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType()) return null;
        return (lvl, cePos, ceState, be) -> {
            net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) world.world().serverWorld();
            if (level == null || level.isClientSide()) return;
            BlockPos pos = BlockPos.of(cePos.asLong());
            PersistentBlockEntity pbe = getBE(level, pos);
            if (pbe != null) {
                FluidStack s = pbe.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY,0,0));
                if (!s.isEmpty()) {
                    pbe.set(FluidKeys.FLUID, new FluidStack(s.getType(), s.getAmount(), PRESSURE_BOOST));
                }
            }
            FluidStack stored = getStored(level, pos);
            // Dos cooldowns independientes: para recolección de bloques y para I/O con carriers
            int blockCd = 0, ioCd = 0;
            if (pbe != null) {
                blockCd = pbe.getOrDefault(FluidKeys.FLUID_BLOCK_COOLDOWN, 0);
                ioCd = pbe.getOrDefault(FluidKeys.FLUID_IO_COOLDOWN, 0);
                if (blockCd > 0) pbe.set(FluidKeys.FLUID_BLOCK_COOLDOWN, blockCd - 1);
                if (ioCd > 0) pbe.set(FluidKeys.FLUID_IO_COOLDOWN, ioCd - 1);
            }
            // PUMP desde bloques solo si no hay cooldown de bloques y hay espacio
            if(blockCd <= 0 && !stored.isFull(CAPACITY)) tryDirectional(level, pos, Direction.DOWN, PumpAction.PUMP);
            // PUSH/IO solo si no hay cooldown de IO y hay contenido
            if (ioCd <= 0 && !stored.isEmpty()) tryDirectional(level, pos, Direction.UP, PumpAction.PUSH);
        };
    }
    
    public enum PumpAction {
        PUMP,
        PUSH
    }
    protected boolean tryDirectional(Level level, BlockPos from, Direction dir, PumpAction action) {
        BlockPos target = offset(from, dir);
        // Respetar orientación del bloque si existen propiedades de dirección
        try {
            net.minecraft.world.level.block.state.BlockState bs = level.getBlockState(from);
            // redireccionar la dirección según el estado actual del bloque
            dir = redirectDirection(dir, bs);
            target = offset(from, dir);
        } catch (Throwable ignored) {
            // si algo falla, continuar con la dirección original
        }
        
        if (action == PumpAction.PUMP) {
            // Si el bloque objetivo es lava (o fuente compatible), intentar área para mejor recolección
            FluidStack stored = getStored(level, from);
            FluidType base = FluidType.getFluidTypeAt(target, level);
            FluidStack collected = base == FluidType.LAVA
                ? FluidType.collectArea(target, level, 32, TRANSFER_PER_TICK,stored.getType())
                : FluidType.collectAt(target, level, TRANSFER_PER_TICK,stored.getType());
            if (!collected.isEmpty()) {
                int accepted = insertFluid(level, from, collected);
                // setear cooldown según tipo recogido
                if (accepted > 0) {
                    int delay = FluidType.blockCollectDelay(collected.getType());
                    PersistentBlockEntity pbe = getBE(level, from);
                    if (pbe != null && delay > 1) pbe.set(FluidKeys.FLUID_BLOCK_COOLDOWN, delay);
                }
                return accepted > 0;
            }
            var opt = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(target));
            FluidCarrier carrier = opt.map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null).orElse(null);
            if (carrier != null && carrier.getAccessMode() == FluidAccessMode.ANYONE_CAN_TAKE) {
                if (!stored.isEmpty()) {
                    int move = Math.min(TRANSFER_PER_TICK, stored.getAmount());
                    final FluidStack[] extracted = {null};
                    int actualExtracted = FluidType.extractFromCarrier(carrier, level, target, move, f -> extracted[0] = f);
                    if (actualExtracted > 0 && extracted[0] != null) {
                        int accepted = insertFluid(level, from, extracted[0]);
                        if (accepted < actualExtracted) {
                            // Si no pudimos aceptar todo, devolver la diferencia
                            FluidStack remainder = new FluidStack(extracted[0].getType(), 
                                actualExtracted - accepted, extracted[0].getPressure());
                            FluidType.depositToCarrier(carrier, level, target, remainder);
                        }
                        if (accepted > 0) {
                            int delay = FluidType.carrierIODelay(extracted[0].getType());
                            PersistentBlockEntity pbe = getBE(level, from);
                            if (pbe != null && delay > 1) pbe.set(FluidKeys.FLUID_IO_COOLDOWN, delay);
                        }
                        return accepted > 0;
                    }
                }
            }
            
        } else if (action == PumpAction.PUSH) {
            FluidStack stored = getStored(level, from);
            if (stored.isEmpty()) return false;

            if (level.getBlockState(target).isAir()) {
                FluidStack temp = new FluidStack(stored.getType(), stored.getAmount(), stored.getPressure());
                int dispersed = FluidType.disperseIntoAir(temp, target, level);
                if (dispersed > 0) {
                    extractFluid(level, from, dispersed, f -> {});
                    int delay = FluidType.carrierIODelay(stored.getType());
                    PersistentBlockEntity pbe = getBE(level, from);
                    if (pbe != null && delay > 1) pbe.set(FluidKeys.FLUID_IO_COOLDOWN, delay);
                    return true;
                }
                return false;
            }
            var opt = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(target));
            FluidCarrier carrier = opt.map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null).orElse(null);
            
            if (carrier != null) {
                int move = Math.min(TRANSFER_PER_TICK, stored.getAmount());
                if (move <= 0) return false;

                int appliedPressure = stored.getPressure();
                if (dir == Direction.DOWN) {
                    appliedPressure = PRESSURE_BOOST; // empuje descendente extra
                } else if (dir == Direction.UP) {
                    if (stored.getPressure() <= 0) return false; // no subida sin presión
                    // mantener presión actual, sin boost adicional (ya se agregó al inicio de tick)
                } else {
                }
                
                FluidStack toSend = new FluidStack(stored.getType(), move, appliedPressure);
                int accepted = FluidType.depositToCarrier(carrier, level, target, toSend);
                if (accepted > 0) {
                    FluidType.extractFromCarrier(this, level, from, accepted, f -> {});
                    int delay = FluidType.carrierIODelay(stored.getType());
                    PersistentBlockEntity pbe = getBE(level, from);
                    if (pbe != null && delay > 1) pbe.set(FluidKeys.FLUID_IO_COOLDOWN, delay);
                    return true;
                }
            }
        }
        
        return false;
    }

    protected PersistentBlockEntity getBE(Level level, BlockPos pos){
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        if (be instanceof PersistentBlockEntity p) return p;
        return null;
    }

    private void withBE(Level level, BlockPos pos, java.util.function.Consumer<PersistentBlockEntity> c){
        PersistentBlockEntity p = getBE(level,pos); if (p!=null) c.accept(p);
    }
    public FluidStack getStored(Level level, BlockPos pos){
        PersistentBlockEntity p = getBE(level,pos);
        if (p==null) return new FluidStack(FluidType.EMPTY,0,0);
        return p.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY,0,0));
    }
    public int insertFluid(Level level, BlockPos pos, FluidStack stack){
        if (stack==null || stack.isEmpty()) return 0;
        final int[] acc={0};
        withBE(level,pos,p->{
            FluidStack stored=p.getOrDefault(FluidKeys.FLUID,new FluidStack(FluidType.EMPTY,0,0));
            FluidStack incoming = new FluidStack(stack.getType(), stack.getAmount(), PRESSURE_BOOST);
            if (stored.isEmpty()) {
                int mv=Math.min(CAPACITY,incoming.getAmount());
                p.set(FluidKeys.FLUID,new FluidStack(incoming.getType(),mv,incoming.getPressure()));
                acc[0]=mv;
            } else if (stored.getType()==incoming.getType()) {
                int space=CAPACITY-stored.getAmount();
                if (space>0) {
                    int mv=Math.min(space,incoming.getAmount());
                    stored.addAmount(mv);
                    int pressure=Math.max(stored.getPressure(), incoming.getPressure());
                    p.set(FluidKeys.FLUID,new FluidStack(stored.getType(), stored.getAmount(), pressure));
                    acc[0]=mv;
                }
            }
        });
        return acc[0];
    }
    public int extractFluid(Level level, BlockPos pos, int max, java.util.function.Consumer<FluidStack> drained){
        final int[] mv={0};
        withBE(level,pos,p->{
            FluidStack stored=p.getOrDefault(FluidKeys.FLUID,new FluidStack(FluidType.EMPTY,0,0));
            if (stored.isEmpty()) return;
            int take=Math.min(max,stored.getAmount());
            FluidStack out=new FluidStack(stored.getType(), take, stored.getPressure());
            stored.removeAmount(take);
            if (stored.isEmpty()) p.remove(FluidKeys.FLUID); else p.set(FluidKeys.FLUID, stored);
            mv[0]=take; drained.accept(out);
        });
        return mv[0];
    }
    public FluidAccessMode getAccessMode(){ return FluidAccessMode.PUMP_VALVE_CAN_TAKE; }

    private BlockPos offset(BlockPos pos, Direction dir){
        return switch(dir){
            case NORTH -> pos.north(); case SOUTH -> pos.south(); case EAST -> pos.east(); case WEST -> pos.west(); case UP -> pos.above(); case DOWN -> pos.below();
        }; }

        
    @Override
    public net.momirealms.craftengine.core.entity.player.InteractionResult useWithoutItem(UseOnContext context,
            ImmutableBlockState state) {

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

        

        FluidStack stored = getStored(level, pos);

        if (held == null || held.isEmpty() && player.isShiftKeyDown()) {
            String fluidName = stored.isEmpty() ? "fluid.minecraft.empty" : "fluid.minecraft." + stored.getType().toString().toLowerCase();
            Component msg = MiniMessage.miniMessage().deserialize("<lang:" + fluidName + "> " +
                    "<gray>" + stored.getAmount() + "/" + CAPACITY + " mb</gray>");
            player.getBukkitEntity().sendActionBar(msg);
            return net.momirealms.craftengine.core.entity.player.InteractionResult.SUCCESS_AND_CANCEL;
        }
        return net.momirealms.craftengine.core.entity.player.InteractionResult.PASS;
    }
}
