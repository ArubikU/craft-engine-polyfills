package dev.arubik.craftengine.block.behavior;

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
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.property.BooleanProperty;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;

/**
 * Comportamiento de bloque que se puede encender/apagar con interacción del
 * jugador o proyectiles.
 * Basado en el template de LightUpBlock.
 */
public class LightUpBlockBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    // Propiedad LIT como en el template
    public final BooleanProperty LIT;

    public LightUpBlockBehavior(BlockDefinition customBlock) {
        super(customBlock);
        this.LIT = (BooleanProperty) customBlock.getProperty("lit");
    }

    /**
     * Verifica si el bloque está encendido
     */
    public boolean isLitUp(BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null && customState.owner().value() == this.blockDefinition) {
            return customState.get(LIT);
        }
        return false;
    }

    /**
     * Establece si el bloque está encendido
     */
    public void setLitUp(BlockState state, LevelAccessor world, BlockPos pos, boolean lit) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null && customState.owner().value() == this.blockDefinition) {
            ImmutableBlockState newState = customState.with(LIT, lit);
            world.setBlock(pos, (BlockState) newState.customBlockState().minecraftState(), 3);
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
    public void entityInside(Object thisBlock, Object[] args) {
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
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            return new LightUpBlockBehavior(block);
        }
    }
}