/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntitySpawnReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.component.ItemLore
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseRailBlock
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.entity.Entity
 */
package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import dev.arubik.craftengine.contraption.player.CePlayers;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class MinecartContraptionType
implements ContraptionType {
    public static final MinecartContraptionType INSTANCE = new MinecartContraptionType();

    private MinecartContraptionType() {
    }

    @Override
    public boolean isBearingEntity(net.minecraft.world.entity.Entity nmsEntity) {
        CraftEntity bukkitEntity = nmsEntity.getBukkitEntity();
        return MinecartBearing.isBearing((Entity)bukkitEntity) && MinecartBearing.isAssembled((Entity)bukkitEntity);
    }

    @Override
    public UUID getContraptionId(net.minecraft.world.entity.Entity nmsEntity) {
        CraftEntity bukkitEntity = nmsEntity.getBukkitEntity();
        return MinecartBearing.contraptionId((Entity)bukkitEntity);
    }

    @Override
    public void onBearingEntityLoad(net.minecraft.world.entity.Entity nmsEntity, Level level) {
        MinecartBearing.rehydrate((Entity)nmsEntity.getBukkitEntity());
    }

    @Override
    public void onBearingEntityUnload(net.minecraft.world.entity.Entity nmsEntity, ContraptionState state) {
        MinecartBearing.saveStructure((Entity)nmsEntity.getBukkitEntity(), state);
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public boolean canAssemble(Level level, BlockPos anchor, Set<BlockPos> blocks) {
        return level.getBlockState(anchor.below()).getBlock() instanceof BaseRailBlock;
    }

    @Override
    public String getAssemblyFailureMessage() {
        return "Minecart bearing requires rail beneath";
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        net.minecraft.world.entity.Entity cart = EntityType.MINECART.create((Level)serverLevel, EntitySpawnReason.TRIGGERED);
        if (cart == null) {
            return;
        }
        cart.setPos((double)anchor.getX() + 0.5, (double)anchor.getY(), (double)anchor.getZ() + 0.5);
        cart.setInvulnerable(true);
        serverLevel.addFreshEntity(cart);
        state.addBehavior(new MinecartFollowBehavior(cart.getUUID()));
    }

    @Override
    public void onDisassemble(ContraptionState state, Level level) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        for (MovementBehavior b : state.behaviors()) {
            if (!(b instanceof MinecartFollowBehavior)) continue;
            MinecartFollowBehavior follow = (MinecartFollowBehavior)b;
            net.minecraft.world.entity.Entity cart = serverLevel.getEntity(follow.entityId());
            if (cart == null) break;
            cart.discard();
            break;
        }
    }

    @Override
    public boolean isRotational() {
        return false;
    }

    @Override
    public boolean isVehicle() {
        return false;
    }

    @Override
    public boolean hasPhysics() {
        return false;
    }

    @Override
    public boolean canPackToItem() {
        return true;
    }

    @Override
    public boolean isPackedItem(ItemStack item) {
        CustomData customData = (CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.contains("contraption_structure") || customData.contains("contraption_file_id");
    }

    @Override
    public ContraptionEntity fromItem(ItemStack item, Level level, BlockPos spawnPos) {
        ContraptionLevel contraptionLevel;
        byte[] bytes;
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (!(level.getBlockState(spawnPos).getBlock() instanceof BaseRailBlock)) {
            return null;
        }
        CustomData customData = (CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        try {
            bytes = ContraptionStorage.loadFromItem(tag);
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to load minecart from item: " + String.valueOf(e));
            return null;
        }
        if (bytes == null) {
            return null;
        }
        net.minecraft.world.entity.Entity cart = EntityType.MINECART.create((Level)serverLevel, EntitySpawnReason.TRIGGERED);
        if (cart == null) {
            return null;
        }
        cart.setPos((double)spawnPos.getX() + 0.5, (double)spawnPos.getY(), (double)spawnPos.getZ() + 0.5);
        cart.setInvulnerable(true);
        serverLevel.addFreshEntity(cart);
        UUID id = UUID.randomUUID();
        try {
            contraptionLevel = ContraptionLevel.create((Level)serverLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0);
            ContraptionStorage.loadLevel(contraptionLevel, ContraptionStorage.fromBytes(bytes));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to deserialize minecart structure: " + String.valueOf(e));
            cart.discard();
            return null;
        }
        if (contraptionLevel.blockCount() == 0) {
            contraptionLevel.dispose();
            cart.discard();
            return null;
        }
        ContraptionState state = new ContraptionState(id, (ResourceKey<Level>)serverLevel.dimension(), contraptionLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        state.setBearingType(Key.of((String)"polyfills", (String)"minecart"));
        state.addBehavior(new MinecartFollowBehavior(cart.getUUID()));
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));
        ContraptionStorage.cleanupItem(tag);
        return entity;
    }

    @Override
    public ItemStack toItem(ContraptionEntity entity, Level level) {
        byte[] bytes;
        ContraptionState state = entity.state();
        if (state.level() == null || !(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        try {
            bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to pack minecart to item: " + String.valueOf(e));
            return null;
        }
        int blockCount = state.level().blockCount();
        ItemStack item = new ItemStack((ItemLike)Items.CHEST_MINECART);
        CustomData customData = (CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        try {
            customData = customData.update(tag -> {
                try {
                    ContraptionStorage.storeInItem(tag, bytes);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tag.putInt("contraption_block_count", blockCount);
            });
        }
        catch (RuntimeException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to save minecart to item: " + String.valueOf(e));
            return null;
        }
        item.set(DataComponents.CUSTOM_DATA, customData);
        item.set(DataComponents.CUSTOM_NAME, Component.translatable((String)"item.polyfills.minecart_contraption"));
        item.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable((String)"item.polyfills.contraption.place_on_rail"), Component.translatable((String)"item.polyfills.contraption.blocks", (Object[])new Object[]{Component.literal((String)String.valueOf(blockCount))}))));
        entity.despawn(CePlayers.resolve(serverLevel.getWorld().getPlayers()));
        ContraptionManager.remove(state.id());
        state.level().dispose();
        for (MovementBehavior b : state.behaviors()) {
            if (!(b instanceof MinecartFollowBehavior)) continue;
            MinecartFollowBehavior follow = (MinecartFollowBehavior)b;
            net.minecraft.world.entity.Entity cart = serverLevel.getEntity(follow.entityId());
            if (cart == null) break;
            cart.discard();
            break;
        }
        return item;
    }
}

