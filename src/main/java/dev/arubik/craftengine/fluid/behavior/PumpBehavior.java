package dev.arubik.craftengine.fluid.behavior;

import java.util.Map;
import java.util.concurrent.Callable; // legado para compat manual tick (se podrá retirar tras migración completa)

import dev.arubik.craftengine.block.behavior.ConnectableBlockBehavior;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.fluid.FluidKeys;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.util.Direction;
import net.momirealms.craftengine.core.util.HorizontalDirection;

/**
 * PumpBehavior: bloque conectable que añade presión al fluido almacenado.
 * Reimplementa mínima lógica de transferencia usando composición con la interfaz FluidCarrier.
 */
public class PumpBehavior extends ConnectableBlockBehavior implements FluidCarrier, net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior {
    public static final Factory FACTORY = new Factory();
    private static final int PRESSURE_BOOST = 2;

    private static final int CAPACITY = PipeBehavior.CAPACITY; // uniformidad
    private static final int TRANSFER_PER_TICK = PipeBehavior.TRANSFER_PER_TICK;

    public PumpBehavior(CustomBlock block, net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> horizontalDirectionProperty,
                        net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> verticalDirectionProperty) {
        super(block, java.util.List.of(Direction.UP,Direction.DOWN), horizontalDirectionProperty, verticalDirectionProperty);
    }

    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        try { if (superMethod != null) superMethod.call(); } catch (Exception ignored) {}
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        if (level.isClientSide()) return;

        // Boost interno primero
        PersistentBlockEntity be = getBE(level, pos);
        if (be != null) {
            FluidStack s = be.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY,0,0));
            if (!s.isEmpty()) {
                be.set(FluidKeys.FLUID, new FluidStack(s.getType(), s.getAmount(), s.getPressure()+PRESSURE_BOOST));
            }
        }

        // Intentar empujar en orden (DOWN, horizontales, UP si presión>0)
        FluidStack stored = getStored(level, pos);
        // Si está vacío, intentar drenar bloque justo abajo (o frente en prioridad) usando collectLimited
        if (stored.isEmpty()) {
            BlockPos below = pos.below();
            FluidStack drained = FluidType.collectLimited(below, level, TRANSFER_PER_TICK);
            if (!drained.isEmpty()) {
                insertFluid(level, pos, drained);
                stored = getStored(level,pos);
            }
        }
        if (stored.isEmpty()) return;
        if (tryTransfer(level, pos, Direction.DOWN, stored)) return;
        for (Direction d : new Direction[]{Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST}) {
            if (tryTransfer(level, pos, d, stored)) return;
        }
        if (stored.getPressure() > 0) {
            tryTransfer(level, pos, Direction.UP, stored);
        }
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public net.momirealms.craftengine.core.block.BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {

            // Leer las propiedades de dirección desde los argumentos
            String horizontalDirectionProperty = null;
            String verticalDirectionProperty = null;
            
            Object horizontalProp = arguments.get("horizontal-direction-property");
            if (horizontalProp instanceof String) {
                horizontalDirectionProperty = (String) horizontalProp;
            }
            
            Object verticalProp = arguments.get("vertical-direction-property");
            if (verticalProp instanceof String) {
                verticalDirectionProperty = (String) verticalProp;
            }

            net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection> hProp = null;
            net.momirealms.craftengine.core.block.properties.EnumProperty<Direction> vProp = null;

            if (horizontalDirectionProperty != null) {
                try {
                    hProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<HorizontalDirection>) block.getProperty(horizontalDirectionProperty);
                } catch (ClassCastException ignored) {
                    // Property type mismatch, keep as null
                }
            }
            
            if (verticalDirectionProperty != null) {
                try {
                    vProp = (net.momirealms.craftengine.core.block.properties.EnumProperty<Direction>) block.getProperty(verticalDirectionProperty);
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

            // Boost interno sólo si hay fluido
            PersistentBlockEntity pbe = getBE(level, pos);
            if (pbe != null) {
                FluidStack s = pbe.getOrDefault(FluidKeys.FLUID, new FluidStack(FluidType.EMPTY,0,0));
                if (!s.isEmpty()) {
                    pbe.set(FluidKeys.FLUID, new FluidStack(s.getType(), s.getAmount(), s.getPressure()+PRESSURE_BOOST));
                }
            }

            FluidStack stored = getStored(level, pos);
            if (stored.isEmpty()) {
                BlockPos below = pos.below();
                FluidStack drained = FluidType.collectLimited(below, level, TRANSFER_PER_TICK);
                if (!drained.isEmpty()) {
                    insertFluid(level, pos, drained);
                    stored = getStored(level,pos);
                }
            }
            if (stored.isEmpty()) return;

            // DIRECCIONALIDAD: si la bomba tiene orientación (no implementado aún), aplicar prioridad.
            // Mientras tanto usamos misma prioridad que Pipe.
            if (tryDirectional(level, pos, Direction.DOWN, stored)) return;
            for (Direction d : new Direction[]{Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST}) {
                if (tryDirectional(level, pos, d, stored)) return;
            }
            if (stored.getPressure() > 0) {
                tryDirectional(level, pos, Direction.UP, stored);
            }
        };
    }

    // Transferencia adaptada: laterales actúan como extracción simple (no añaden presión adicional al paquete),
    // hacia abajo mantiene/usa boost actual, hacia arriba requiere presión existente (>0) y mantiene la presión.
    protected boolean tryDirectional(Level level, BlockPos from, Direction dir, FluidStack snapshot) {
        if (snapshot.isEmpty()) return false;
        BlockPos target = offset(from, dir);
        var opt = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(target));
        FluidCarrier carrier = opt.map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null).orElse(null);
        // Intentar como carrier primero; si no existe, colocar
        if (carrier == null) {
            if (snapshot.getAmount() >= snapshot.getType().mbPerFullBlock()) {
                FluidStack temp = new FluidStack(snapshot.getType(), snapshot.getAmount(), snapshot.getPressure());
                if (FluidType.place(temp, target, level)) {
                    int placed = snapshot.getAmount() - temp.getAmount();
                    if (placed > 0) {
                        extractFluid(level, from, placed, f -> {});
                        return true;
                    }
                }
            }
            return false;
        }
        int move = Math.min(TRANSFER_PER_TICK, snapshot.getAmount());
        if (move <= 0) return false;

    int appliedPressure = snapshot.getPressure();
        if (dir == Direction.DOWN) {
            appliedPressure += PRESSURE_BOOST; // empuje descendente extra
        } else if (dir == Direction.UP) {
            if (snapshot.getPressure() <= 0) return false; // no subida sin presión
            // mantener presión actual, sin boost adicional (ya se agregó al inicio de tick)
        } else {
            // horizontal: actuar como extractor plano, no sumar más presión.
        }
        FluidStack toSend = new FluidStack(snapshot.getType(), move, appliedPressure);
        int accepted = FluidType.depositToCarrier(carrier, level, target, toSend);
        if (accepted > 0) {
            FluidType.extractFromCarrier(this, level, from, accepted, f -> {});
            return true;
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

    // --- FluidCarrier impl ---
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
            FluidStack incoming = new FluidStack(stack.getType(), stack.getAmount(), stack.getPressure()+PRESSURE_BOOST);
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

    private boolean tryTransfer(Level level, BlockPos from, Direction dir, FluidStack snapshot){
        if (snapshot.isEmpty()) return false;
        BlockPos target=offset(from,dir);
        var opt = net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(level.getBlockState(target));
        FluidCarrier carrier = opt.map(cs -> cs.behavior() instanceof FluidCarrier fc ? fc : null).orElse(null);
        if (carrier==null) return false;
        // Verificar si se permite extracción de ESTE (pump) y aceptación en destino
        if (this.getAccessMode()==FluidAccessMode.PUMP_VALVE_CAN_TAKE || this.getAccessMode()==FluidAccessMode.ANYONE_CAN_TAKE){
            int move=Math.min(TRANSFER_PER_TICK, snapshot.getAmount());
            if (move<=0) return false;
            FluidStack toSend=new FluidStack(snapshot.getType(), move, snapshot.getPressure()+PRESSURE_BOOST);
            int accepted=FluidType.depositToCarrier(carrier, level,target,toSend);
            if (accepted>0){
                extractFluid(level,from,accepted,f->{});
                return true;
            }
        }
        return false;
    }
}
