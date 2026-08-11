package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.behavior.MinecartFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import dev.arubik.craftengine.contraption.player.CePlayers;
import net.minecraft.core.BlockPos;
import net.momirealms.craftengine.core.util.Key;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Minecart-anchored contraption type (CONTRAPTIONS.md Phase 6).
 *
 * <h2>Design pattern: entity-anchored bearing</h2>
 * Spawns a REAL vanilla minecart as persistence anchor. Structure rides in entity PDC.
 * Minecart's own rail physics drives movement; {@link MinecartFollowBehavior} snaps
 * contraption to match each tick.
 *
 * <h2>Reference for external plugins</h2>
 * This is the canonical entity-anchored pattern. Study this to create:
 * - Trains with derailment (spawn minecart, validate rail continuity in canAssemble)
 * - Boats anchored to boat entities (same pattern, different entity type)
 * - Any contraption that follows a real entity
 *
 * <h2>Key elements</h2>
 * 1. {@link #canAssemble}: validates rail beneath bearing
 * 2. {@link #attachBehaviors}: spawns minecart, makes invulnerable, attaches follow behavior
 * 3. {@link #onDisassemble}: removes anchor minecart
 * 4. Chunk unload: {@link MinecartBearing#saveStructure} dumps to PDC
 * 5. Chunk load: {@link MinecartBearing#rehydrate} rebuilds from PDC
 *
 * <h2>Why invulnerable</h2>
 * Minecart holds entire structure in PDC. If it breaks to normal damage, structure lost forever.
 * {@code setInvulnerable(true)} blocks fire/fall/mobs/players; allows plugin {@code remove()}.
 *
 * <h2>Portal crossing</h2>
 * Minecart crosses portals naturally (UUID stable). {@link MinecartFollowBehavior} detects
 * world change, calls {@link ContraptionEntity#teleport} to re-anchor.
 */
public class MinecartContraptionType implements ContraptionType {

    public static final MinecartContraptionType INSTANCE = new MinecartContraptionType();

    private MinecartContraptionType() {
    }

    @Override
    public boolean isBearingEntity(Entity nmsEntity) {
        org.bukkit.entity.Entity bukkitEntity = nmsEntity.getBukkitEntity();
        return dev.arubik.craftengine.contraption.bearing.MinecartBearing.isBearing(bukkitEntity)
                && dev.arubik.craftengine.contraption.bearing.MinecartBearing.isAssembled(bukkitEntity);
    }

    @Override
    public java.util.UUID getContraptionId(Entity nmsEntity) {
        org.bukkit.entity.Entity bukkitEntity = nmsEntity.getBukkitEntity();
        return dev.arubik.craftengine.contraption.bearing.MinecartBearing.contraptionId(bukkitEntity);
    }

    @Override
    public void onBearingEntityLoad(Entity nmsEntity, Level level) {
        dev.arubik.craftengine.contraption.bearing.MinecartBearing.rehydrate(nmsEntity.getBukkitEntity());
    }

    @Override
    public void onBearingEntityUnload(Entity nmsEntity, ContraptionState state) {
        dev.arubik.craftengine.contraption.bearing.MinecartBearing.saveStructure(nmsEntity.getBukkitEntity(), state);
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public boolean canAssemble(Level level, BlockPos anchor, Set<BlockPos> blocks) {
        // Bearing must sit above rail (Create's convention)
        return level.getBlockState(anchor.below()).getBlock() instanceof BaseRailBlock;
    }

    @Override
    public String getAssemblyFailureMessage() {
        return "Minecart bearing requires rail beneath";
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // ServerLevel required for entity spawn
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Spawn minecart via EntityType
        Entity cart = EntityType.MINECART.create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (cart == null) return;

        cart.setPos(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5);

        // Critical: make invulnerable so PDC structure survives. Only discard() can kill it.
        cart.setInvulnerable(true);

        serverLevel.addFreshEntity(cart);

        // Attach follow behavior with minecart UUID
        state.addBehavior(new MinecartFollowBehavior(cart.getUUID()));

        // Note: PDC save/load handled by MinecartBearing static methods (chunk lifecycle).
        // See MinecartBearing#saveStructure (chunk unload) and #rehydrate (chunk load).
    }

    @Override
    public void onDisassemble(ContraptionState state, Level level) {
        // Remove NMS anchor minecart
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (MovementBehavior b : state.behaviors()) {
            if (b instanceof MinecartFollowBehavior follow) {
                Entity cart = serverLevel.getEntity(follow.entityId());
                if (cart != null) {
                    cart.discard();
                }
                break;
            }
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
        return false; // vanilla rail physics, not our XPBD solver
    }

    @Override
    public boolean canPackToItem() {
        return true; // minecart packs to chest-minecart item
    }

    @Override
    public boolean isPackedItem(ItemStack item) {
        CustomData customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.contains("contraption_structure") || customData.contains("contraption_file_id");
    }

    @Override
    public ContraptionEntity fromItem(ItemStack item, Level level, BlockPos spawnPos) {
        if (!(level instanceof ServerLevel serverLevel)) return null;

        // Check rail
        if (!(level.getBlockState(spawnPos).getBlock() instanceof BaseRailBlock)) {
            return null;
        }

        // Load structure bytes (inline or external file)
        CustomData customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        byte[] bytes;
        net.minecraft.nbt.CompoundTag tag = customData.copyTag();
        try {
            bytes = ContraptionStorage.loadFromItem(tag);
        } catch (IOException e) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to load minecart from item: " + e);
            return null;
        }
        if (bytes == null) return null;

        // Spawn anchor minecart
        Entity cart = EntityType.MINECART.create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (cart == null) return null;

        cart.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        cart.setInvulnerable(true);
        serverLevel.addFreshEntity(cart);

        // Create ContraptionState from bytes
        java.util.UUID id = java.util.UUID.randomUUID();
        ContraptionLevel contraptionLevel;
        try {
            contraptionLevel = ContraptionLevel.create(
                serverLevel,
                spawnPos.getX(),
                spawnPos.getY(),
                spawnPos.getZ(),
                0.0
            );
            ContraptionStorage.loadLevel(contraptionLevel,
                ContraptionStorage.fromBytes(bytes));
        } catch (IOException e) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to deserialize minecart structure: " + e);
            cart.discard();
            return null;
        }

        if (contraptionLevel.blockCount() == 0) {
            contraptionLevel.dispose();
            cart.discard();
            return null;
        }

        ContraptionState state = new ContraptionState(
            id,
            serverLevel.dimension(),
            contraptionLevel,
            spawnPos.getX(),
            spawnPos.getY(),
            spawnPos.getZ()
        );

        state.setBearingType(Key.of("polyfills", "minecart"));

        // Attach behaviors
        state.addBehavior(new MinecartFollowBehavior(cart.getUUID()));

        // Register
        ContraptionEntity entity = ContraptionManager.register(new ContraptionEntity(state));

        // Delete external file if used
        ContraptionStorage.cleanupItem(tag);

        return entity;
    }

    @Override
    public ItemStack toItem(ContraptionEntity entity, Level level) {
        ContraptionState state = entity.state();
        if (state.level() == null || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        // Serialize structure
        byte[] bytes;
        try {
            bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
        } catch (IOException e) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to pack minecart to item: " + e);
            return null;
        }

        int blockCount = state.level().blockCount();

        // Create NMS chest-minecart ItemStack
        ItemStack item = new ItemStack(Items.CHEST_MINECART);

        // Set custom data
        CustomData customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        try {
            customData = customData.update(tag -> {
                try {
                    ContraptionStorage.storeInItem(tag, bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tag.putInt("contraption_block_count", blockCount);
            });
        } catch (RuntimeException e) {
            dev.arubik.craftengine.CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to save minecart to item: " + e);
            return null;
        }
        item.set(DataComponents.CUSTOM_DATA, customData);

        // Set i18n display name + lore
        item.set(DataComponents.CUSTOM_NAME,
            Component.translatable("item.polyfills.minecart_contraption"));
        item.set(DataComponents.LORE, new ItemLore(List.of(
            Component.translatable("item.polyfills.contraption.place_on_rail"),
            Component.translatable("item.polyfills.contraption.blocks",
                Component.literal(String.valueOf(blockCount)))
        )));

        // Despawn contraption (without block restore - blocks live in item)
        entity.despawn(CePlayers.resolve(serverLevel.getWorld().getPlayers()));
        ContraptionManager.remove(state.id());
        state.level().dispose();

        // Remove anchor minecart
        for (MovementBehavior b : state.behaviors()) {
            if (b instanceof MinecartFollowBehavior follow) {
                Entity cart = serverLevel.getEntity(follow.entityId());
                if (cart != null) {
                    cart.discard();
                }
                break;
            }
        }

        return item;
    }
}
