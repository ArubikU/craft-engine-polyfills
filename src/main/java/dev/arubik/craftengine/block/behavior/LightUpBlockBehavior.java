package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.concurrent.Callable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.properties.BooleanProperty;

/**
 * Comportamiento de bloque que se puede encender/apagar con interacción del
 * jugador o proyectiles.
 * Basado en el template de LightUpBlock.
 */
public class LightUpBlockBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    // Propiedad LIT como en el template
    public final BooleanProperty LIT;

    public LightUpBlockBehavior(CustomBlock customBlock) {
        super(customBlock);
        this.LIT = (BooleanProperty) customBlock.getProperty("lit");
    }

    /**
     * Verifica si el bloque está encendido
     */
    public boolean isLitUp(BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null && customState.owner().value() == this.customBlock) {
            return customState.get(LIT);
        }
        return false;
    }

    /**
     * Establece si el bloque está encendido
     */
    public void setLitUp(BlockState state, LevelAccessor world, BlockPos pos, boolean lit) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null && customState.owner().value() == this.customBlock) {
            ImmutableBlockState newState = customState.with(LIT, lit);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(world, pos, newState.customBlockState().literalObject(), 3);
        }
    }

    /**
     * Interacción con entidad (especialmente proyectiles)
     */
    protected void interactWithEntity(Level level, BlockState state, Entity entity, BlockPos pos) {
        if (!level.isClientSide() && entity instanceof Projectile projectile && projectile.isOnFire()) {
            boolean currentlyLit = isLitUp(state);
            setLitUp(state, level, pos, !currentlyLit);
        }
    }

    @Override
    public void entityInside(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        if (args.length >= 4) {
            BlockState state = (BlockState) args[0];
            Level level = (Level) args[1];
            BlockPos pos = (BlockPos) args[2];
            Entity entity = (Entity) args[3];

            this.interactWithEntity(level, state, entity, pos);
        }
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            return new LightUpBlockBehavior(block);
        }
    }
}