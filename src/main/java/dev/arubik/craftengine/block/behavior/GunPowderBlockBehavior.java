package dev.arubik.craftengine.block.behavior;

import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.properties.BooleanProperty;
import net.momirealms.craftengine.core.entity.player.InteractionHand;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.item.context.UseOnContext;
import net.momirealms.craftengine.core.util.Key;

/**
 * Comportamiento de bloque de pólvora que extiende LightUpBlockBehavior.
 * Se enciende y explota después de un tiempo, basado en el template de
 * GunpowderBlock.
 * Incluye conexiones tipo redstone wire con propiedades direccionales.
 */
public class GunPowderBlockBehavior extends WireBlockBehavior implements EntityBlockBehavior {

    public static final Factory FACTORY = new Factory();

    protected int burnDelay;
    protected float explosionPower;
    protected boolean causesFire;
    private final BooleanProperty LIT;

    public GunPowderBlockBehavior(CustomBlock customBlock, int burnDelay, float explosionPower, boolean causesFire) {
        super(customBlock);
        this.burnDelay = Math.max(1, burnDelay);
        this.explosionPower = Math.max(0.1f, explosionPower);
        this.causesFire = causesFire;
        this.LIT = (BooleanProperty) customBlock.getProperty("lit");
    }

    @Override
    protected boolean canConnectTo(BlockState state, BlockGetter world, BlockPos pos, Direction dir) {
        Block b = state.getBlock();
        return BlockStateUtils.getOptionalCustomBlockState(state)
                .map(s -> s.owner().value().id().equals(this.customBlock.id())).orElse(false) ||
                b instanceof TntBlock || b instanceof AbstractCandleBlock;
    }

    @Override
    protected boolean canClimbTo(BlockState state, BlockGetter world, BlockPos pos) {
        Block b = state.getBlock();
        return BlockStateUtils.getOptionalCustomBlockState(state)
                .map(s -> s.owner().value().id().equals(this.customBlock.id())).orElse(false) ||
                b instanceof TntBlock || b instanceof AbstractCandleBlock;
    }

    public boolean isLitUp(BlockState state) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null && customState.owner().value().id().equals(this.customBlock.id())) {
            return customState.get(LIT);
        }
        return false;
    }

    public void setLitUp(BlockState state, LevelAccessor world, BlockPos pos, boolean lit) {
        ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (customState != null && customState.owner().value().id().equals(this.customBlock.id())) {
            ImmutableBlockState newState = customState.with(LIT, lit);
            FastNMS.INSTANCE.method$LevelWriter$setBlock(world, pos, newState.customBlockState().literalObject(), 3);
        }
    }

    /**
     * Enciende el bloque iniciando el proceso de quemado
     */
    public boolean lightUp(Entity entity, BlockState state, BlockPos pos, Level world) {
        if (!isLitUp(state)) {
            ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (customState != null && customState.owner().value().id().equals(this.customBlock.id())) {
                // Iniciar el quemado
                ImmutableBlockState newState = customState.with(LIT, true);
                executeBlockEntity(world, pos, (blockEntity) -> {
                    if (blockEntity instanceof PersistentBlockEntity pbe) {
                        pbe.set(Key.of("gunpowder:burning"), PersistentDataType.INTEGER, 1);
                    }
                });
                FastNMS.INSTANCE.method$LevelWriter$setBlock(world, pos, newState.customBlockState().literalObject(),
                        3);

                // Programar el tick para continuar quemando
                world.scheduleTick(pos, world.getBlockState(pos).getBlock(), burnDelay);

                // Sonido de encendido
                world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);

                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {

        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        if (!level.isClientSide()) {
            ImmutableBlockState customState = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
            if (customState != null && customState.owner().value().id().equals(this.customBlock.id())) {
                int burning = 0;
                PersistentBlockEntity pbe = getBlockEntity(level, pos);
                if (pbe != null) {
                    Integer b = pbe.get(Key.of("gunpowder:burning"), PersistentDataType.INTEGER);
                    if (b != null) {
                        burning = b;
                    }
                }

                if (burning == 8) {
                    // ¡Explosión!
                    level.removeBlock(pos, false);
                    createMiniExplosion(level, pos);
                } else if (burning > 0) {
                    // Continuar quemando
                    final int currentBurning = burning;
                    executeBlockEntity(level, pos, (blockEntity) -> {
                        if (blockEntity instanceof PersistentBlockEntity pbe2) {
                            pbe2.set(Key.of("gunpowder:burning"), PersistentDataType.INTEGER, currentBurning + 1);
                        }
                    });
                    FastNMS.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(level, pos,
                            level.getBlockState(pos).getBlock(), burnDelay);

                    // Sonido periódico
                    if (burning % 2 == 0) {
                        level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.5f, 1.2f);
                    }
                } else {
                    // No está quemando, verificar si debería encenderse
                    checkForIgnitionSources(level, pos, state);
                }
            }
        }
    }

    /**
     * Verifica fuentes de ignición cercanas
     */
    private void checkForIgnitionSources(Level level, BlockPos pos, BlockState state) {
        for (Direction dir : Direction.values()) {
            BlockPos checkPos = pos.relative(dir);
            if (canLightMeOnFire(level, checkPos)) {
                lightUp(null, state, pos, level);
                break;
            }
        }
    }

    /**
     * Verifica si hay una fuente de fuego en la posición dada
     */
    private boolean canLightMeOnFire(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // Simplificado: verificar por fuego, lava, o bloques encendidos
        return state.is(net.minecraft.world.level.block.Blocks.FIRE) ||
                state.is(net.minecraft.world.level.block.Blocks.LAVA) ||
                (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) &&
                        state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT));
    }

    /**
     * Crea una mini explosión
     */
    private void createMiniExplosion(Level level, BlockPos pos) {
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                explosionPower, causesFire, Level.ExplosionInteraction.BLOCK);

        // Sonido de explosión
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                2.0f, (1.0f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f);
    }

    @Override
    public InteractionResult useWithoutItem(UseOnContext context, ImmutableBlockState state) {
        @SuppressWarnings("unchecked")
        Item<org.bukkit.inventory.ItemStack> item = (Item<org.bukkit.inventory.ItemStack>) context.getItem();

        Key tag = Key.of("minecraft:creeper_igniters");
        if (item != null && item.hasItemTag(tag)) {
            // check if has durability so just subtract 1 durability else destroy item and
            // burn the block
            // and switch hand if needed
            Integer maxDamage = item.getItem().getDataOrDefault(DataComponentTypes.MAX_DAMAGE, 0);
            if (maxDamage > 0) {
                int damage = item.getItem().getDataOrDefault(DataComponentTypes.DAMAGE, 0) + 1;
                if (damage >= maxDamage) {
                    // break item
                    if (context.getPlayer() instanceof BukkitServerPlayer bp) {
                        EquipmentSlot hand = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND
                                : EquipmentSlot.OFF_HAND;
                        bp.platformPlayer().getInventory().setItem(hand, new ItemStack(Material.AIR));
                    }
                } else {
                    ItemStack itemStack = item.getItem();
                    itemStack.setData(DataComponentTypes.DAMAGE, damage);
                    if (context.getPlayer() instanceof BukkitServerPlayer bp) {
                        EquipmentSlot hand = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND
                                : EquipmentSlot.OFF_HAND;
                        bp.platformPlayer().getInventory().setItem(hand, itemStack);
                    }
                }
            } else {
                if (context.getPlayer() instanceof BukkitServerPlayer bp) {
                    EquipmentSlot hand = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.HAND
                            : EquipmentSlot.OFF_HAND;
                    bp.platformPlayer().getInventory().setItem(hand, new ItemStack(Material.AIR));
                }
            }
            Level level = (Level) context.getLevel().serverWorld();
            lightUp(null, (BlockState) state.customBlockState().literalObject(),
                    (BlockPos) LocationUtils.toBlockPos(context.getClickedPos()), level);

        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        super.onPlace(thisBlock, args, superMethod);
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];
        // Programar verificación inicial de fuentes de ignición
        FastNMS.INSTANCE.method$ScheduledTickAccess$scheduleBlockTick(level, pos,
                level.getBlockState(pos).getBlock(), burnDelay);
    }

    @Override
    public void neighborChanged(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        super.neighborChanged(thisBlock, args, superMethod);
        BlockState state = (BlockState) args[0];
        Level level = (Level) args[1];
        BlockPos pos = (BlockPos) args[2];

        // Verificar si hay señal de redstone que pueda encenderlo
        for (Direction direction : Direction.values()) {
            if (level.getSignal(pos.relative(direction), direction) > 0) {
                lightUp(null, state, pos, level);
                break;
            }
        }
    }

    @Override
    public void onExplosionHit(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        if (args.length >= 4) {
            BlockState state = (BlockState) args[0];
            ServerLevel level = (ServerLevel) args[1];
            BlockPos pos = (BlockPos) args[2];
            Explosion explosion = (Explosion) args[3];

            // Explotar en cadena cuando es golpeado por otra explosión
            lightUp(explosion.getDirectSourceEntity(), state, pos, level);
        }
    }

    /**
     * Enciende cuando entidades específicas lo tocan
     */
    public void interactWithEntity(Level level, BlockState state, Entity entity, BlockPos pos) {
        if (!level.isClientSide() && !isLitUp(state)) {
            // Encender si es tocado por fuego o proyectiles flamígeros
            lightUp(entity, state, pos, level);
        }
    }

    public static class Factory implements BlockBehaviorFactory {
        @Override
        public GunPowderBlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            int burnDelay = 20;
            float explosionPower = 1.0f;
            boolean causesFire = false;

            Object delay = arguments.get("burn_delay");
            if (delay instanceof Number) {
                burnDelay = ((Number) delay).intValue();
            }

            Object power = arguments.get("explosion_power");
            if (power instanceof Number) {
                explosionPower = ((Number) power).floatValue();
            }

            Object fire = arguments.get("causes_fire");
            if (fire instanceof Boolean) {
                causesFire = (Boolean) fire;
            }

            return new GunPowderBlockBehavior(block, burnDelay, explosionPower, causesFire);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType() {
        @SuppressWarnings("unchecked")
        BlockEntityType<T> type = (BlockEntityType<T>) BukkitBlockEntityTypes.PERSISTENT_BLOCK_ENTITY_TYPE;
        return type;
    }

    @Override
    public BlockEntity createBlockEntity(net.momirealms.craftengine.core.world.BlockPos arg0,
            ImmutableBlockState arg1) {
        return new PersistentBlockEntity(arg0, arg1);
    }

    public PersistentBlockEntity getBlockEntity(Level world, BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(world, pos);
        if (be instanceof PersistentBlockEntity) {
            return (PersistentBlockEntity) be;
        }
        return null;
    }

    public void executeBlockEntity(Level world, BlockPos pos,
            java.util.function.Consumer<PersistentBlockEntity> consumer) {
        PersistentBlockEntity be = getBlockEntity(world, pos);
        if (be != null) {
            consumer.accept(be);
        }
    }
}