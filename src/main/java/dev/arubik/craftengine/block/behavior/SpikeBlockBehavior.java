package dev.arubik.craftengine.block.behavior;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import dev.arubik.craftengine.block.entity.BukkitBlockEntityTypes;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.bukkit.nms.FastNMS;
import net.momirealms.craftengine.bukkit.util.LocationUtils;
import net.momirealms.craftengine.bukkit.world.BukkitWorld;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.entity.player.InteractionHand;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.item.Item;
import net.momirealms.craftengine.core.util.ItemUtils;
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
import net.momirealms.craftengine.core.block.behavior.EntityBlock;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker;
import net.momirealms.craftengine.core.world.CEWorld;

public class SpikeBlockBehavior extends BukkitBlockBehavior implements EntityBlock {

    public static final Factory FACTORY = new Factory();

    private final double damageMultiplier;
    private final double cooldownMultiplier;

    public SpikeBlockBehavior(BlockDefinition customBlock, double damageMultiplier, double cooldownMultiplier) {
        super(customBlock);
        this.damageMultiplier = damageMultiplier;
        this.cooldownMultiplier = cooldownMultiplier;
    }

    private int controllerId;

    @Override
    public void initControllerId(int id) {
        this.controllerId = id;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new SpikeController(blockEntity, this);
    }

    /** Looks up the loaded {@link SpikeController} at {@code pos}, or null. */
    public static SpikeController getSpike(Level level, net.minecraft.core.BlockPos pos) {
        BlockEntity be = BukkitBlockEntityTypes.getIfLoaded(level, pos);
        return (be != null && be.controller instanceof SpikeController s) ? s : null;
    }

    /** Controller driving the spike's attack ticking; the held item/owner/cooldown live directly on it. */
    public static class SpikeController extends PersistentWorldlyBlockEntity {
        private final SpikeBlockBehavior behavior;
        private java.util.UUID ownerUUID;
        private long lastAttackTime = 0;

        public SpikeController(BlockEntity blockEntity, SpikeBlockBehavior behavior) {
            super(blockEntity, 1);
            this.behavior = behavior;
        }

        @Override
        public void setChanged() {
            // Persistence is pulled at chunk-save via saveCustomData/loadCustomData; no dirty flag needed.
        }

        public net.minecraft.world.item.ItemStack getItem() {
            return getItem(0);
        }

        public void setItem(net.minecraft.world.item.ItemStack item) {
            setItem(0, item);
            if (item == null || item.isEmpty())
                ownerUUID = null;
        }

        public java.util.UUID getOwnerUUID() {
            return ownerUUID;
        }

        public void setOwnerUUID(java.util.UUID owner) {
            this.ownerUUID = owner;
        }

        public long getLastAttackTime() {
            return lastAttackTime;
        }

        public void setLastAttackTime(long lastAttackTime) {
            this.lastAttackTime = lastAttackTime;
        }

        @Override
        public void saveCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
            super.saveCustomData(tag);
            tag.putString("owner", ownerUUID != null ? ownerUUID.toString() : "");
            tag.putLong("last_attack", lastAttackTime);
        }

        @Override
        public void loadCustomData(net.momirealms.craftengine.libraries.nbt.CompoundTag tag) {
            super.loadCustomData(tag);
            String o = tag.getString("owner");
            this.ownerUUID = (o != null && !o.isEmpty()) ? java.util.UUID.fromString(o) : null;
            this.lastAttackTime = tag.getLong("last_attack");
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, net.minecraft.world.item.ItemStack stack,
                net.minecraft.core.Direction direction) {
            return index == 0 && getItem(0).isEmpty(); // Only allow placing if empty
        }

        @Override
        public boolean canTakeItemThroughFace(int index, net.minecraft.world.item.ItemStack stack,
                net.minecraft.core.Direction direction) {
            return false;
        }

        @Override
        public net.minecraft.world.item.ItemStack removeItem(int slot, int amount) {
            net.minecraft.world.item.ItemStack result = super.removeItem(slot, amount);
            if (slot == 0 && getItem(0).isEmpty())
                ownerUUID = null;
            return result;
        }

        @Override
        public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot) {
            net.minecraft.world.item.ItemStack result = super.removeItemNoUpdate(slot);
            if (slot == 0)
                ownerUUID = null;
            return result;
        }

        @Override
        public <C extends BlockEntityController> BlockEntityTicker<C> createBlockEntityTicker(CEWorld world,
                ImmutableBlockState state) {
            @SuppressWarnings("unchecked")
            BlockEntityTicker<C> ticker = (BlockEntityTicker<C>) BlockEntityController
                    .createTickerHelper((BlockEntityTicker<SpikeController>) SpikeController::tick);
            return ticker;
        }

        public static void tick(CEWorld world, net.momirealms.craftengine.core.world.BlockPos cePos,
                ImmutableBlockState ceState, SpikeController self) {
            SpikeBlockBehavior behavior = self.behavior;
            double damageMultiplier = behavior.damageMultiplier;
            double cooldownMultiplier = behavior.cooldownMultiplier;

            ServerLevel level = (ServerLevel) world.world().minecraftWorld();
            if (level == null || level.isClientSide())
                return;

            net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.of(cePos.asLong());
            SpikeController entity = self;

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
                            ((net.minecraft.world.level.block.state.BlockState) behavior.block().defaultState()
                                    .customBlockState().minecraftState()).getBlock());

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
        }
    }

    @Override
    public void affectNeighborsAfterRemoval(Object thisBlock, Object[] args) {
        super.affectNeighborsAfterRemoval(thisBlock, args);
        // args: BlockState state, Level level, BlockPos pos, (movedByPiston)
        ServerLevel level = (ServerLevel) args[1]; // server level usually
        net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) args[2];

        // On removal, drop the stored item (the block entity itself is torn down by the engine).
        SpikeController spike = getSpike(level, pos);
        if (spike != null && !spike.getItem().isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), spike.getItem());
        }
    }

    @Override
    public boolean isSignalSource(Object thisBlock, Object[] args) {
        return true;
    }

    @Override
    public int getSignal(Object thisBlock, Object[] args) {
        // params: BlockState blockState, BlockGetter blockAccess, BlockPos pos,
        // Direction side
        Level level = (Level) args[1];
        net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) args[2];

        SpikeController spike = getSpike(level, pos);
        if (spike != null) {
            long time = level.getGameTime() - spike.getLastAttackTime();
            // Pulse 2 ticks
            if (time >= 0 && time < 2) {
                return 15;
            }
        }
        return 0;
    }

    // NOTE: getContainer is no longer a BlockBehavior callback in craft-engine 26.6.2.
    // Kept for reference; not invoked by the engine.
    public Object getContainer(Object thisBlock, Object[] args) {
        // params: BlockState state, LevelAccessor level, BlockPos pos
        // Returns WorldlyContainer for hopper interaction
        Level level = (Level) args[1];
        net.minecraft.core.BlockPos pos = (net.minecraft.core.BlockPos) args[2];

        return getSpike((ServerLevel) level, pos);
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

        SpikeController spike = getSpike((ServerLevel) ((BukkitWorld) world).minecraftWorld(),
                net.minecraft.core.BlockPos.of(pos.asLong()));
        if (spike == null)
            return InteractionResult.PASS;

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
            Item item = context.getItem();
            net.minecraft.world.item.ItemStack nmsItem = org.bukkit.craftbukkit.inventory.CraftItemStack
                    .asNMSCopy((org.bukkit.inventory.ItemStack) item.platformItem());

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
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            double damageMultiplier = dev.arubik.craftengine.util.Utils.getAsDouble(arguments.getOrDefault("damage-multiplier", 1.0),
                    "damage-multiplier");
            double cooldownMultiplier = dev.arubik.craftengine.util.Utils
                    .getAsDouble(arguments.getOrDefault("cooldown-multiplier", 1.0), "cooldown-multiplier");

            return new SpikeBlockBehavior(block, damageMultiplier, cooldownMultiplier);
        }
    }
}