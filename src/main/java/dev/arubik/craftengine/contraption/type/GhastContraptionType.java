/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.event.entity.EntityEquipmentChangedEvent
 *  io.papermc.paper.event.entity.EntityEquipmentChangedEvent$EquipmentChange
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.item.component.ItemLore
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.momirealms.craftengine.core.util.Key
 *  org.bukkit.Location
 *  org.bukkit.NamespacedKey
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.CraftWorld
 *  org.bukkit.craftbukkit.entity.CraftEntity
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HappyGhast
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.persistence.PersistentDataContainer
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 */
package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionCapture;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.bearing.GhastHarness;
import dev.arubik.craftengine.contraption.behavior.GhastFollowBehavior;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.glue.GlueGraph;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import dev.arubik.craftengine.contraption.player.CePlayers;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class GhastContraptionType
implements ContraptionType,
Listener {
    public static final GhastContraptionType INSTANCE = new GhastContraptionType();
    private static final NamespacedKey IS_BEARING = GhastContraptionType.key("contraption_ghast_bearing");
    private static final NamespacedKey ASSEMBLED = GhastContraptionType.key("contraption_assembled");
    private static final NamespacedKey CONTRAPTION_ID = GhastContraptionType.key("contraption_id");
    private static final NamespacedKey STRUCTURE = GhastContraptionType.key("contraption_structure");
    private static final NamespacedKey ANCHOR_X = GhastContraptionType.key("contraption_ghast_anchor_x");
    private static final NamespacedKey ANCHOR_Y = GhastContraptionType.key("contraption_ghast_anchor_y");
    private static final NamespacedKey ANCHOR_Z = GhastContraptionType.key("contraption_ghast_anchor_z");
    private static final NamespacedKey ANCHOR_YAW = GhastContraptionType.key("contraption_ghast_anchor_yaw");
    private static final double GLUE_PROBE_REACH = 1.0;

    private GhastContraptionType() {
    }

    private static NamespacedKey key(String name) {
        return new NamespacedKey((Plugin)CraftEnginePolyfills.instance(), name);
    }

    public static boolean isBearing(Entity entity) {
        return entity.getPersistentDataContainer().has(IS_BEARING, PersistentDataType.BYTE);
    }

    public static boolean isAssembled(Entity entity) {
        Byte v = (Byte)entity.getPersistentDataContainer().get(ASSEMBLED, PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    public static UUID contraptionId(Entity entity) {
        String id = (String)entity.getPersistentDataContainer().get(CONTRAPTION_ID, PersistentDataType.STRING);
        return id == null ? null : UUID.fromString(id);
    }

    public static Set<BlockPos> gluedStructureAround(World bukkitWorld, Entity ghast) {
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        ResourceKey worldId = realLevel.dimension();
        GlueGraph graph = GlueRegistry.graphFor((ResourceKey<Level>)worldId);
        HashSet<BlockPos> structure = new HashSet<BlockPos>();
        for (BlockPos cell : GhastHarness.cellsOverlapping(GhastContraptionType.handleOf(ghast).getBoundingBox().inflate(1.0))) {
            if (!graph.hasNode(cell) || structure.contains(cell)) continue;
            structure.addAll(GlueRegistry.structureAt((ResourceKey<Level>)worldId, cell));
        }
        return structure;
    }

    public static void tearDownIntoItem(World bukkitWorld, Entity ghast, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        if (state.level() != null) {
            state.level().dispose();
        }
        GhastContraptionType.untag(ghast);
    }

    public static void disassembleInPlace(World bukkitWorld, Entity ghast, ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0.0, state.yawRadians());
        ContraptionCapture.restoreGlue((ResourceKey<Level>)realLevel.dimension(), state.level(), state.originBearingBlockPos(), snapped, quarterTurns);
        ContraptionCapture.restoreRotated((Level)realLevel, state.level(), snapped, quarterTurns);
        HashSet<BlockPos> restingPositions = new HashSet<BlockPos>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns), snapped));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        if (ghast != null) {
            GhastContraptionType.untag(ghast);
        }
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
    }

    public static void saveStructure(Entity ghast, ContraptionState state) {
        if (state.level() == null) {
            return;
        }
        try {
            byte[] bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
            ghast.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to save ghast harness structure: " + String.valueOf(e));
        }
    }

    public static void rehydrate(Entity ghast) {
        if (!(GhastContraptionType.isBearing(ghast) && GhastContraptionType.isAssembled(ghast) && ghast instanceof org.bukkit.entity.LivingEntity)) {
            return;
        }
        org.bukkit.entity.LivingEntity living = (org.bukkit.entity.LivingEntity)ghast;
        UUID id = GhastContraptionType.contraptionId(ghast);
        byte[] bytes = (byte[])ghast.getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
        if (id == null || bytes == null || ContraptionManager.get(id) != null) {
            return;
        }
        World bukkitWorld = ghast.getWorld();
        ServerLevel realLevel = ((CraftWorld)bukkitWorld).getHandle();
        Vec3 anchorOffset = GhastContraptionType.readAnchorOffset(ghast.getPersistentDataContainer());
        OptionalDouble yawOffset = GhastContraptionType.readYawOffset(ghast.getPersistentDataContainer());
        Location loc = ghast.getLocation();
        Vec3 anchor = new Vec3(loc.getX() + anchorOffset.x, loc.getY() + anchorOffset.y, loc.getZ() + anchorOffset.z);
        ContraptionLevel level = ContraptionLevel.create((Level)realLevel, anchor.x, anchor.y, anchor.z, 0.0);
        try {
            ContraptionStorage.loadLevel(level, ContraptionStorage.fromBytes(bytes));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to load ghast harness structure: " + String.valueOf(e));
            level.dispose();
            return;
        }
        ContraptionState state = new ContraptionState(id, (ResourceKey<Level>)((CraftWorld)bukkitWorld).getHandle().dimension(), level, anchor.x, anchor.y, anchor.z);
        state.setBearingType(Key.of((String)"polyfills", (String)"ghast"));
        state.setAnchorEntityId(living.getUniqueId());
        state.setScale(level.realScaleFactor());
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        state.addBehavior(new GhastFollowBehavior(living.getUniqueId(), anchorOffset, yawOffset));
        ContraptionManager.register(new ContraptionEntity(state));
    }

    public static void forget(Entity ghast) {
        GhastContraptionType.untag(ghast);
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEquipmentChanged(EntityEquipmentChangedEvent e) {
        org.bukkit.entity.LivingEntity livingEntity = e.getEntity();
        if (!(livingEntity instanceof HappyGhast)) {
            return;
        }
        HappyGhast ghast = (HappyGhast)livingEntity;
        EntityEquipmentChangedEvent.EquipmentChange change = (EntityEquipmentChangedEvent.EquipmentChange)e.getEquipmentChanges().get(org.bukkit.inventory.EquipmentSlot.BODY);
        if (change == null) {
            return;
        }
        if (GhastHarness.isHarness(change.oldItem().getType()) && !GhastHarness.isHarness(change.newItem().getType())) {
            this.onHarnessRemoved(ghast);
        }
    }

    private void onHarnessRemoved(HappyGhast ghast) {
        ContraptionEntity entity;
        UUID cId = GhastContraptionType.contraptionId((Entity)ghast);
        ContraptionEntity contraptionEntity = entity = cId == null ? null : ContraptionManager.get(cId);
        if (entity == null) {
            return;
        }
        GhastContraptionType.disassembleInPlace(ghast.getWorld(), (Entity)ghast, entity);
    }

    @Override
    public boolean isBearingEntity(net.minecraft.world.entity.Entity nmsEntity) {
        CraftEntity craftEntity = nmsEntity.getBukkitEntity();
        if (!(craftEntity instanceof HappyGhast)) {
            return false;
        }
        HappyGhast ghast = (HappyGhast)craftEntity;
        return GhastContraptionType.isBearing((Entity)ghast);
    }

    @Override
    public UUID getContraptionId(net.minecraft.world.entity.Entity nmsEntity) {
        return GhastContraptionType.contraptionId((Entity)nmsEntity.getBukkitEntity());
    }

    @Override
    public void onBearingEntityLoad(net.minecraft.world.entity.Entity nmsEntity, Level level) {
        GhastContraptionType.rehydrate((Entity)nmsEntity.getBukkitEntity());
    }

    @Override
    public void onBearingEntityUnload(net.minecraft.world.entity.Entity nmsEntity, ContraptionState state) {
        GhastContraptionType.saveStructure((Entity)nmsEntity.getBukkitEntity(), state);
    }

    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new ContraptionEntity(state);
    }

    @Override
    public boolean canAssemble(Level level, BlockPos anchor, Set<BlockPos> blocks) {
        return !blocks.isEmpty();
    }

    @Override
    public String getAssemblyFailureMessage() {
        return "Ghast must be surrounded by glued blocks";
    }

    @Override
    public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
    }

    @Override
    public void onDisassemble(ContraptionState state, Level level) {
    }

    @Override
    public void onAnchorDeath(net.minecraft.world.entity.Entity anchorEntity, Level level, List<ItemStack> drops) {
        ContraptionEntity entity;
        for (int i = 0; i < drops.size(); ++i) {
            ItemStack drop = drops.get(i);
            if (!this.isPackedItem(drop)) continue;
            drop.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            drops.set(i, drop);
        }
        if (!(anchorEntity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)anchorEntity;
        org.bukkit.entity.LivingEntity bukkitEntity = (org.bukkit.entity.LivingEntity)livingEntity.getBukkitEntity();
        if (!(bukkitEntity instanceof HappyGhast)) {
            return;
        }
        HappyGhast ghast = (HappyGhast)bukkitEntity;
        if (!GhastContraptionType.isBearing((Entity)ghast) || !GhastContraptionType.isAssembled((Entity)ghast)) {
            return;
        }
        UUID cId = GhastContraptionType.contraptionId((Entity)ghast);
        ContraptionEntity contraptionEntity = entity = cId == null ? null : ContraptionManager.get(cId);
        if (entity != null) {
            GhastContraptionType.disassembleInPlace(ghast.getWorld(), (Entity)ghast, entity);
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
        CustomData customData = (CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        try {
            bytes = ContraptionStorage.loadFromItem(tag);
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to load ghast harness from item: " + String.valueOf(e));
            return null;
        }
        if (bytes == null) {
            return null;
        }
        try {
            contraptionLevel = ContraptionLevel.create((Level)serverLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0);
            ContraptionStorage.loadLevel(contraptionLevel, ContraptionStorage.fromBytes(bytes));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to deserialize ghast harness structure: " + String.valueOf(e));
            return null;
        }
        if (contraptionLevel.blockCount() == 0) {
            contraptionLevel.dispose();
            return null;
        }
        ContraptionState state = new ContraptionState(UUID.randomUUID(), (ResourceKey<Level>)serverLevel.dimension(), contraptionLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        state.setBearingType(Key.of((String)"polyfills", (String)"ghast"));
        if (!tag.contains("contraption_anchor_x")) {
            return null;
        }
        double anchorX = tag.getDouble("contraption_anchor_x").orElse(0.0);
        double anchorY = tag.getDouble("contraption_anchor_y").orElse(0.0);
        double anchorZ = tag.getDouble("contraption_anchor_z").orElse(0.0);
        state.setAnchorOffset(new Vec3(anchorX, anchorY, anchorZ));
        if (tag.contains("contraption_yaw_offset")) {
            state.setYawOffset(OptionalDouble.of(tag.getDouble("contraption_yaw_offset").orElse(0.0)));
        }
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
        GhastFollowBehavior ghastBehavior = GhastContraptionType.findFollowBehavior(state);
        if (ghastBehavior == null) {
            return null;
        }
        try {
            bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
        }
        catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to pack ghast harness to item: " + String.valueOf(e));
            return null;
        }
        int blockCount = state.level().blockCount();
        Vec3 anchorOffset = ghastBehavior.anchorOffset();
        OptionalDouble yawOffset = ghastBehavior.yawOffset();
        net.minecraft.world.entity.Entity ghastEntity = serverLevel.getEntity(ghastBehavior.entityId());
        if (!(ghastEntity instanceof LivingEntity)) {
            return null;
        }
        LivingEntity ghast = (LivingEntity)ghastEntity;
        ItemStack equippedHarness = ghast.getItemBySlot(EquipmentSlot.BODY);
        if (equippedHarness.isEmpty()) {
            equippedHarness = new ItemStack((ItemLike)Items.WHITE_HARNESS);
        }
        ItemStack item = equippedHarness.copy();
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
                tag.putDouble("contraption_anchor_x", anchorOffset.x);
                tag.putDouble("contraption_anchor_y", anchorOffset.y);
                tag.putDouble("contraption_anchor_z", anchorOffset.z);
                if (yawOffset.isPresent()) {
                    tag.putDouble("contraption_yaw_offset", yawOffset.getAsDouble());
                }
            });
        }
        catch (RuntimeException e) {
            CraftEnginePolyfills.instance().getLogger().warning("[Contraption] failed to save ghast harness to item: " + String.valueOf(e));
            return null;
        }
        item.set(DataComponents.CUSTOM_DATA, customData);
        item.set(DataComponents.CUSTOM_NAME, Component.translatable((String)"item.polyfills.ghast_harness_contraption"));
        item.set(DataComponents.LORE, new ItemLore(List.of(Component.translatable((String)"item.polyfills.contraption.equip_on_ghast"), Component.translatable((String)"item.polyfills.contraption.blocks", (Object[])new Object[]{Component.literal((String)String.valueOf(blockCount))}))));
        entity.despawn(CePlayers.resolve(serverLevel.getWorld().getPlayers()));
        ContraptionManager.remove(state.id());
        state.level().dispose();
        ghast.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        return item;
    }

    private static net.minecraft.world.entity.Entity handleOf(Entity entity) {
        return ((CraftEntity)entity).getHandle();
    }

    private static void untag(Entity ghast) {
        PersistentDataContainer pdc = ghast.getPersistentDataContainer();
        pdc.remove(IS_BEARING);
        pdc.remove(ASSEMBLED);
        pdc.remove(CONTRAPTION_ID);
        pdc.remove(STRUCTURE);
        pdc.remove(ANCHOR_X);
        pdc.remove(ANCHOR_Y);
        pdc.remove(ANCHOR_Z);
        pdc.remove(ANCHOR_YAW);
    }

    private static void writeAnchor(PersistentDataContainer pdc, Vec3 offset, OptionalDouble yawOffset) {
        pdc.set(ANCHOR_X, PersistentDataType.DOUBLE, offset.x);
        pdc.set(ANCHOR_Y, PersistentDataType.DOUBLE, offset.y);
        pdc.set(ANCHOR_Z, PersistentDataType.DOUBLE, offset.z);
        if (yawOffset.isPresent()) {
            pdc.set(ANCHOR_YAW, PersistentDataType.DOUBLE, yawOffset.getAsDouble());
        } else {
            pdc.remove(ANCHOR_YAW);
        }
    }

    private static Vec3 readAnchorOffset(PersistentDataContainer pdc) {
        Double x = (Double)pdc.get(ANCHOR_X, PersistentDataType.DOUBLE);
        Double y = (Double)pdc.get(ANCHOR_Y, PersistentDataType.DOUBLE);
        Double z = (Double)pdc.get(ANCHOR_Z, PersistentDataType.DOUBLE);
        if (x == null || y == null || z == null) {
            return Vec3.ZERO;
        }
        return new Vec3(x.doubleValue(), y.doubleValue(), z.doubleValue());
    }

    private static OptionalDouble readYawOffset(PersistentDataContainer pdc) {
        Double yaw = (Double)pdc.get(ANCHOR_YAW, PersistentDataType.DOUBLE);
        return yaw == null ? OptionalDouble.empty() : OptionalDouble.of(yaw);
    }

    private static GhastFollowBehavior findFollowBehavior(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (!(behavior instanceof GhastFollowBehavior)) continue;
            GhastFollowBehavior follow = (GhastFollowBehavior)behavior;
            return follow;
        }
        return null;
    }
}

