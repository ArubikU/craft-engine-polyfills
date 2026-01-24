package dev.arubik.craftengine.block.behavior;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import dev.arubik.craftengine.block.entity.SpikeBlockEntity;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionHand;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.util.ItemUtils;
import net.momirealms.craftengine.core.util.ResourceConfigUtils;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.World;
import net.momirealms.craftengine.core.world.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.core.Holder;
import net.minecraft.world.Containers;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.block.behavior.EntityBlockBehavior;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityType;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;
import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;

public class SpikeBlockBehavior extends BukkitBlockBehavior implements EntityBlockBehavior {

    public static final Factory FACTORY = new Factory();

    private final double damageMultiplier;
    private final double cooldownMultiplier;

    public SpikeBlockBehavior(CustomBlock customBlock, double damageMultiplier, double cooldownMultiplier) {
        super(customBlock);
        this.damageMultiplier = damageMultiplier;
        this.cooldownMultiplier = cooldownMultiplier;
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> blockEntityType(ImmutableBlockState state) {
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
    public <T extends BlockEntity> BlockEntityTicker<T> createSyncBlockEntityTicker(CEWorld world,
            ImmutableBlockState state, BlockEntityType<T> type) {
        if (type != blockEntityType(state))
            return null;

        return (lvl, cePos, ceState, be) -> {
            ServerLevel level = (ServerLevel) world.world().serverWorld();
            if (level == null || level.isClientSide())
                return;

            net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.of(cePos.asLong());
            SpikeBlockEntity entity = SpikeBlockEntity.getOrCreate(level, pos);

            if (!entity.getItem().isEmpty()) {
                net.minecraft.world.item.ItemStack itemStack = entity.getItem();

                AABB aabb = new AABB(pos).inflate(0.1, 0.5, 0.1);
                List<net.minecraft.world.entity.LivingEntity> targets = level
                        .getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, aabb);

                UUID ownerId = entity.getOwnerUUID();
                net.minecraft.world.entity.player.Player owner = ownerId != null ? level.getPlayerByUUID(ownerId)
                        : null;

                // Calculate Attributes
                double baseDamage = 0;
                double attackSpeed = 4.0; // Default punch speed

                // Attributes from item
                Multimap<Holder<Attribute>, AttributeModifier> modifiers = MultimapBuilder.hashKeys().hashSetValues()
                        .build();
                itemStack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
                    modifiers.put(attribute, modifier);
                });

                for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : modifiers.entries()) {
                    if (entry.getKey().value() == Attributes.ATTACK_DAMAGE.value()) {
                        baseDamage += entry.getValue().amount();
                    } else if (entry.getKey().value() == Attributes.ATTACK_SPEED.value()) {
                        attackSpeed += entry.getValue().amount();
                    }
                }
                double finalDamage = baseDamage * damageMultiplier;

                long cooldownTicks = (long) (20.0 / attackSpeed * cooldownMultiplier);
                long currentTime = level.getGameTime();

                if (currentTime - entity.getLastAttackTime() < cooldownTicks) {
                    return;
                }

                boolean attacked = false;
                for (net.minecraft.world.entity.LivingEntity target : targets) {
                    if (target == owner)
                        continue;
                    if (!target.isAlive())
                        continue;

                    // Use proper enchantment damage calculation
                    DamageSource source = (owner != null) ? level.damageSources().playerAttack(owner)
                            : level.damageSources().generic();

                    float totalDamage = (float) (finalDamage * damageMultiplier);
                    totalDamage = EnchantmentHelper.modifyDamage(level, itemStack, target, source, totalDamage);

                    // Apply post attack effects (knockback, sweeping, etc.)
                    EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, source, itemStack);

                    if (target.hurtOrSimulate(source, totalDamage)) {
                        attacked = true;
                    }
                }

                if (attacked) {
                    entity.setLastAttackTime(currentTime);
                    level.updateNeighborsAt(pos,
                            (net.minecraft.world.level.block.Block) customBlock.defaultState().customBlockState()
                                    .literalObject());

                    // Damage the item
                    if (itemStack.isDamageableItem()) {
                        itemStack.hurtAndBreak(1, level, owner, (item) -> {
                            // Item broke - clear it from the spike
                            entity.setItem(net.minecraft.world.item.ItemStack.EMPTY);
                            entity.setOwnerUUID(null);
                        });

                        // Update the item in entity if it wasn't broken
                        if (!itemStack.isEmpty()) {
                            entity.setItem(itemStack);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onPlace(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        superMethod.call();
        // args: BlockState state, Level level, BlockPos pos, BlockState oldState,
        // boolean movedByPiston
        // 1.20.5+: BlockState state, Level level, BlockPos pos, BlockState oldState,
        // boolean movedByPiston
        // args[1] is Level, args[2] is BlockPos.
        Object levelObj = args[1];
        Object posObj = args[2];

        if (levelObj instanceof ServerLevel level && posObj instanceof net.minecraft.core.BlockPos pos) {
            SpikeBlockEntity entity = SpikeBlockEntity.getOrCreate(level, pos);
        }
    }

    @Override
    public void onRemove(Object thisBlock, Object[] args, Callable<Object> superMethod) throws Exception {
        superMethod.call();
        // args: BlockState state, Level level, BlockPos pos, BlockState newState,
        // boolean movedByPiston
        ServerLevel level = (ServerLevel) args[1]; // server level usually
        net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) args[2];
        Object newStateObj = args[3];

        // Check if block changed
        BlockState state = (BlockState) args[0];
        BlockState newState = (BlockState) newStateObj;

        if (state.getBlock() != newState.getBlock()) {
            SpikeBlockEntity spike = SpikeBlockEntity.get(level, pos).orElse(null);
            if (spike != null) {
                if (!spike.getItem().isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), spike.getItem());
                }
                spike.destroy();
            }
        }
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        return true;
    }

    @Override
    public int getSignal(Object thisBlock, Object[] args, Callable<Object> superMethod) {
        // params: BlockState blockState, BlockGetter blockAccess, BlockPos pos,
        // Direction side
        Level level = (Level) args[1];
        net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) args[2];

        SpikeBlockEntity spike = SpikeBlockEntity.get(level, pos).orElse(null);
        if (spike != null) {
            long time = level.getGameTime() - spike.getLastAttackTime();
            // Pulse 2 ticks
            if (time >= 0 && time < 2) {
                return 15;
            }
        }
        return 0;
    }

    @Override
    public Object getContainer(Object thisBlock, Object[] args) {
        // params: BlockState state, LevelAccessor level, BlockPos pos
        // Returns WorldlyContainer for hopper interaction
        Level level = (Level) args[1];
        net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) args[2];

        return SpikeBlockEntity.getOrCreate(level, pos);
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, ImmutableBlockState state) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!(world.platformWorld() instanceof org.bukkit.World))
            return InteractionResult.PASS;

        SpikeBlockEntity spike = SpikeBlockEntity.getOrCreate((ServerLevel) ((BukkitWorld) world).serverWorld(),
                net.minecraft.core.BlockPos.of(pos.asLong()));

        // Put/Take item logic
        if (context.getHand() == InteractionHand.MAIN_HAND && ItemUtils.isEmpty(context.getItem())) {
            // Take item
            if (!spike.getItem().isEmpty()) {
                net.minecraft.world.item.ItemStack item = spike.getItem();
                spike.setItem(net.minecraft.world.item.ItemStack.EMPTY);
                spike.setOwnerUUID(null);

                net.minecraft.world.entity.player.Player mcPlayer = (net.minecraft.world.entity.player.Player) player
                        .serverPlayer();
                if (!mcPlayer.getInventory().add(item)) {
                    mcPlayer.drop(item, false);
                }
                player.swingHand(context.getHand());
                return InteractionResult.SUCCESS_AND_CANCEL;
            }
        } else if (spike.getItem().isEmpty() && !ItemUtils.isEmpty(context.getItem())) {
            // Put item
            Item<org.bukkit.inventory.ItemStack> item = (Item<org.bukkit.inventory.ItemStack>) context.getItem();
            net.minecraft.world.item.ItemStack nmsItem = org.bukkit.craftbukkit.inventory.CraftItemStack
                    .asNMSCopy(item.getItem());

            net.minecraft.world.item.ItemStack copy = nmsItem.copy();
            copy.setCount(1);
            spike.setItem(copy);
            spike.setOwnerUUID(((org.bukkit.entity.Player) player.platformPlayer()).getUniqueId());

            if (!player.canInstabuild()) {
                item.shrink(1);
            }
            player.swingHand(context.getHand());
            return InteractionResult.SUCCESS_AND_CANCEL;
        }

        return InteractionResult.SUCCESS_AND_CANCEL;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(CustomBlock block, Map<String, Object> arguments) {
            double damageMultiplier = ResourceConfigUtils.getAsDouble(arguments.getOrDefault("damage-multiplier", 1.0),
                    "damage-multiplier");
            double cooldownMultiplier = ResourceConfigUtils
                    .getAsDouble(arguments.getOrDefault("cooldown-multiplier", 1.0), "cooldown-multiplier");

            return new SpikeBlockBehavior(block, damageMultiplier, cooldownMultiplier);
        }
    }
}