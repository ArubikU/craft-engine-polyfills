package dev.arubik.craftengine.contraption.type;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.MovementContext;
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
import dev.arubik.craftengine.contraption.furniture.ContraptionFurniture;
import dev.arubik.craftengine.contraption.furniture.ContraptionFurnitureCapture;
import dev.arubik.craftengine.contraption.glue.GlueGraph;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;
import dev.arubik.craftengine.contraption.persistence.ContraptionStorage;
import dev.arubik.craftengine.contraption.player.CePlayers;
import net.minecraft.core.BlockPos;
import net.momirealms.craftengine.core.util.Key;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.HappyGhast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;

/**
 * Ghast-harness contraption type (2026-07-16 "happy ghast harness contraption").
 *
 * <p>This class is both a {@link ContraptionType} implementation (registered as {@link #INSTANCE}) and a
 * Bukkit {@link Listener} (registered in {@code onEnable}) that handles harness unequip teardowns.
 * Everything ghast-contraption lives here: PDC tagging, glue scan, assembly, disassembly, persistence,
 * pack/unpack, and the event-driven lifecycle.
 *
 * <h2>Lifecycle (post-consolidation)</h2>
 * <ul>
 *   <li><b>Hammer on unassembled ghast</b> = assemble (glue scan or restore from packed harness)</li>
 *   <li><b>Hammer on assembled ghast</b> = pack to item (structure goes into harness, harness drops)</li>
 *   <li><b>Shearing / harness removal</b> = disassemble in place (blocks restored to world)</li>
 *   <li><b>Ghast death</b> = disassemble in place (handled by {@link #onAnchorDeath})</li>
 * </ul>
 *
 * <h2>Design pattern: entity-anchored bearing with relative pose</h2>
 * Attaches to a REAL vanilla {@code HappyGhast}. Structure glued around ghast, not generated.
 * Inherits movement, yaw, scale. Pivot rotates around ghast centre.
 *
 * <h2>Anchor offset</h2>
 * Vector from ghast to origin cell, captured once at assembly. Persisted in both the ghast's PDC
 * (for chunk-load rehydrate) and the packed harness item (for save-to-item). The rotational half
 * ({@code contraptionYaw - ghastYaw}) is captured and persisted alongside it.
 *
 * <h2>Persistence</h2>
 * The ghast is a real entity; chunk load/unload is vanilla entity persistence. The serialized
 * structure rides in the entity's Bukkit PDC. {@code ContraptionChunkLifecycleListener} drives
 * save-then-teardown on unload and {@link #rehydrate} on load.
 */
public class GhastContraptionType implements ContraptionType, Listener {

    public static final GhastContraptionType INSTANCE = new GhastContraptionType();

    private GhastContraptionType() {
    }

    // ============================== Constants ==============================

    private static NamespacedKey key(String name) {
        return new NamespacedKey(CraftEnginePolyfills.instance(), name);
    }

    private static final NamespacedKey IS_BEARING = key("contraption_ghast_bearing");
    private static final NamespacedKey ASSEMBLED = key("contraption_assembled");
    private static final NamespacedKey CONTRAPTION_ID = key("contraption_id");
    private static final NamespacedKey STRUCTURE = key("contraption_structure");
    private static final NamespacedKey ANCHOR_X = key("contraption_ghast_anchor_x");
    private static final NamespacedKey ANCHOR_Y = key("contraption_ghast_anchor_y");
    private static final NamespacedKey ANCHOR_Z = key("contraption_ghast_anchor_z");
    private static final NamespacedKey ANCHOR_YAW = key("contraption_ghast_anchor_yaw");

    /** How far past the ghast's bounding box to look for glue, in blocks. */
    private static final double GLUE_PROBE_REACH = 1.0;

    // ============================== PDC queries ==============================

    public static boolean isBearing(org.bukkit.entity.Entity entity) {
        return entity.getPersistentDataContainer().has(IS_BEARING, PersistentDataType.BYTE);
    }

    public static boolean isAssembled(org.bukkit.entity.Entity entity) {
        Byte v = entity.getPersistentDataContainer().get(ASSEMBLED, PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    public static UUID contraptionId(org.bukkit.entity.Entity entity) {
        String id = entity.getPersistentDataContainer().get(CONTRAPTION_ID, PersistentDataType.STRING);
        return id == null ? null : UUID.fromString(id);
    }

    // ============================== Glue scan ==============================

    /**
     * The glued structure a harnessed ghast would assemble: the union of glue components reachable
     * from any cell its bounding box (inflated by {@link #GLUE_PROBE_REACH}) overlaps.
     */
    public static Set<BlockPos> gluedStructureAround(World bukkitWorld, org.bukkit.entity.Entity ghast) {
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        net.minecraft.resources.ResourceKey<Level> worldId = realLevel.dimension();
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        Set<BlockPos> structure = new HashSet<>();
        for (BlockPos cell : GhastHarness.cellsOverlapping(
                handleOf(ghast).getBoundingBox().inflate(GLUE_PROBE_REACH))) {
            if (!graph.hasNode(cell) || structure.contains(cell)) {
                continue;
            }
            structure.addAll(GlueRegistry.structureAt(worldId, cell));
        }
        return structure;
    }


    // ============================== Teardown ==============================

    /**
     * Tears down WITHOUT restoring blocks — the structure lives on inside a packed harness item.
     */
    public static void tearDownIntoItem(World bukkitWorld, org.bukkit.entity.Entity ghast,
            ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());
        if (state.level() != null) {
            state.level().dispose();
        }
        untag(ghast);
    }

    /**
     * Restores the structure into the real world. Used by shear/unequip, anchor death, and any
     * removal path that does NOT carry the structure on an item.
     */
    public static void disassembleInPlace(World bukkitWorld, org.bukkit.entity.Entity ghast,
            ContraptionEntity entity) {
        ContraptionState state = entity.state();
        entity.despawn(CePlayers.resolve(bukkitWorld.getPlayers()));
        ContraptionManager.remove(state.id());

        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        BlockPos snapped = ContraptionMath.gridSnap(new Vec3(state.x(), state.y(), state.z()));
        int quarterTurns = ContraptionMath.quarterTurnsBetween(0, state.yawRadians());
        ContraptionCapture.restoreGlue(realLevel.dimension(), state.level(), state.originBearingBlockPos(),
                snapped, quarterTurns);
        ContraptionCapture.restoreRotated(realLevel, state.level(), snapped, quarterTurns);
        Set<BlockPos> restingPositions = new HashSet<>();
        if (state.level() != null) {
            for (BlockPos local : state.level().localPositions()) {
                restingPositions.add(ContraptionMath.toWorld(ContraptionCapture.rotateLocal(local, quarterTurns),
                        snapped));
            }
        }
        ContraptionFurnitureCapture.restoreFurniture(bukkitWorld, state.furniture(), snapped, quarterTurns);
        if (state.level() != null) {
            state.level().dispose();
        }
        if (ghast != null) {
            untag(ghast);
        }
        ContraptionAssembler.fireDisassembled(state.id(), bukkitWorld, snapped, restingPositions, quarterTurns);
    }

    // ============================== Persistence (PDC on ghast entity) ==============================

    /**
     * Re-dumps the CURRENT live structure into the ghast's PDC so a later chunk-load rehydrate
     * gets the latest state.
     */
    public static void saveStructure(org.bukkit.entity.Entity ghast, ContraptionState state) {
        if (state.level() == null) {
            return;
        }
        try {
            byte[] bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
            ghast.getPersistentDataContainer().set(STRUCTURE, PersistentDataType.BYTE_ARRAY, bytes);
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to save ghast harness structure: " + e);
        }
    }

    /**
     * Rehydrates a ContraptionEntity from a ghast's PDC on chunk load. No-ops if already live.
     */
    public static void rehydrate(org.bukkit.entity.Entity ghast) {
        if (!isBearing(ghast) || !isAssembled(ghast) || !(ghast instanceof org.bukkit.entity.LivingEntity living)) {
            return;
        }
        UUID id = contraptionId(ghast);
        byte[] bytes = ghast.getPersistentDataContainer().get(STRUCTURE, PersistentDataType.BYTE_ARRAY);
        if (id == null || bytes == null || ContraptionManager.get(id) != null) {
            return;
        }
        World bukkitWorld = ghast.getWorld();
        Level realLevel = ((CraftWorld) bukkitWorld).getHandle();
        Vec3 anchorOffset = readAnchorOffset(ghast.getPersistentDataContainer());
        OptionalDouble yawOffset = readYawOffset(ghast.getPersistentDataContainer());
        Location loc = ghast.getLocation();
        Vec3 anchor = new Vec3(loc.getX() + anchorOffset.x, loc.getY() + anchorOffset.y,
                loc.getZ() + anchorOffset.z);
        ContraptionLevel level = ContraptionLevel.create(realLevel, anchor.x, anchor.y, anchor.z, 0.0);
        try {
            ContraptionStorage.loadLevel(level, ContraptionStorage.fromBytes(bytes));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                    .warning("[Contraption] failed to load ghast harness structure: " + e);
            level.dispose();
            return;
        }
        ContraptionState state = new ContraptionState(id,
                ((CraftWorld) bukkitWorld).getHandle().dimension(), level, anchor.x, anchor.y, anchor.z);
        state.setBearingType(Key.of("polyfills", "ghast"));
        state.setAnchorEntityId(living.getUniqueId());
        state.setScale(level.realScaleFactor());
        state.setFurniture(ContraptionFurnitureCapture.restoreIntoFakeLevel(level, level.furnitureRecords()));
        for (MovementBehavior autoBehavior : ContraptionCapture.resolveAutoBehaviors(level)) {
            state.addBehavior(autoBehavior);
        }
        state.addBehavior(new GhastFollowBehavior(living.getUniqueId(), anchorOffset, yawOffset));
        ContraptionManager.register(new ContraptionEntity(state));
    }

    // ============================== Pack / unpack (Bukkit PDC on harness item) ==============================


    /**
     * Strips bearing tags without touching the ghast itself — stops it being a resurrection source.
     */
    public static void forget(org.bukkit.entity.Entity ghast) {
        untag(ghast);
    }

    // ============================== Harness unequip listener ==============================

    /**
     * Detects harness removal from the BODY slot and disassembles the contraption in place.
     * Shearing, {@code /item replace}, dispensers, plugins — any path that removes the harness
     * funnels through here and triggers a block-restoring disassemble.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquipmentChanged(EntityEquipmentChangedEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) {
            return;
        }
        EntityEquipmentChangedEvent.EquipmentChange change = e.getEquipmentChanges().get(EquipmentSlot.BODY);
        if (change == null) {
            return;
        }
        if (GhastHarness.isHarness(change.oldItem().getType()) && !GhastHarness.isHarness(change.newItem().getType())) {
            onHarnessRemoved(ghast);
        }
    }

    /**
     * Called when a harness leaves the ghast's BODY slot. Always disassembles in place — the
     * hammer "pack to item" path tears down via {@link #tearDownIntoItem} BEFORE the harness is
     * removed, so by the time this fires the contraption is already gone and this is a no-op.
     */
    private void onHarnessRemoved(HappyGhast ghast) {
        UUID cId = contraptionId(ghast);
        ContraptionEntity entity = cId == null ? null : ContraptionManager.get(cId);
        if (entity == null) {
            return;
        }
        disassembleInPlace(ghast.getWorld(), ghast, entity);
    }

    // ============================== ContraptionType interface ==============================

    @Override
    public boolean isBearingEntity(net.minecraft.world.entity.Entity nmsEntity) {
        if (!(nmsEntity.getBukkitEntity() instanceof HappyGhast ghast)) {
            return false;
        }
        return isBearing(ghast);
    }

    @Override
    public UUID getContraptionId(net.minecraft.world.entity.Entity nmsEntity) {
        return contraptionId((org.bukkit.entity.Entity) nmsEntity.getBukkitEntity());
    }

    @Override
    public void onBearingEntityLoad(net.minecraft.world.entity.Entity nmsEntity, Level level) {
        rehydrate(nmsEntity.getBukkitEntity());
    }

    @Override
    public void onBearingEntityUnload(net.minecraft.world.entity.Entity nmsEntity, ContraptionState state) {
        saveStructure(nmsEntity.getBukkitEntity(), state);
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
        // Ghast assembly goes through assemble() -> finishAssembly() which attaches the follow behavior
        // directly. This interface method exists for the block-anchored path which never reaches ghast.
    }

    @Override
    public void onDisassemble(ContraptionState state, Level level) {
        // Ghast anchor stays alive. No cleanup needed.
    }

    @Override
    public void onAnchorDeath(Entity anchorEntity, Level level, List<ItemStack> drops) {
        // Strip packed bytes from harness drops (prevent structure duplication)
        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (isPackedItem(drop)) {
                drop.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                drops.set(i, drop);
            }
        }

        // Disassemble contraption if ghast was a bearing
        if (!(anchorEntity instanceof LivingEntity livingEntity)) return;
        org.bukkit.entity.LivingEntity bukkitEntity = (org.bukkit.entity.LivingEntity) livingEntity.getBukkitEntity();
        if (!(bukkitEntity instanceof HappyGhast ghast)) return;

        if (!isBearing(ghast) || !isAssembled(ghast)) {
            return;
        }

        UUID cId = contraptionId(ghast);
        ContraptionEntity entity = cId == null ? null : ContraptionManager.get(cId);
        if (entity != null) {
            disassembleInPlace(ghast.getWorld(), ghast, entity);
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
        CustomData customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.contains("contraption_structure") || customData.contains("contraption_file_id");
    }

    @Override
    public ContraptionEntity fromItem(ItemStack item, Level level, BlockPos spawnPos) {
        if (!(level instanceof ServerLevel serverLevel)) return null;

        CustomData customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = customData.copyTag();

        byte[] bytes;
        try {
            bytes = ContraptionStorage.loadFromItem(tag);
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to load ghast harness from item: " + e);
            return null;
        }
        if (bytes == null) return null;

        ContraptionLevel contraptionLevel;
        try {
            contraptionLevel = ContraptionLevel.create(serverLevel,
                spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0);
            ContraptionStorage.loadLevel(contraptionLevel, ContraptionStorage.fromBytes(bytes));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to deserialize ghast harness structure: " + e);
            return null;
        }

        if (contraptionLevel.blockCount() == 0) {
            contraptionLevel.dispose();
            return null;
        }

        ContraptionState state = new ContraptionState(UUID.randomUUID(), serverLevel.dimension(),
            contraptionLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        state.setBearingType(Key.of("polyfills", "ghast"));

        if (!tag.contains("contraption_anchor_x")) return null;
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
        ContraptionState state = entity.state();
        if (state.level() == null || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        GhastFollowBehavior ghastBehavior = findFollowBehavior(state);
        if (ghastBehavior == null) return null;

        byte[] bytes;
        try {
            bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
        } catch (IOException e) {
            CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to pack ghast harness to item: " + e);
            return null;
        }

        int blockCount = state.level().blockCount();
        Vec3 anchorOffset = ghastBehavior.anchorOffset();
        OptionalDouble yawOffset = ghastBehavior.yawOffset();

        Entity ghastEntity = serverLevel.getEntity(ghastBehavior.entityId());
        if (!(ghastEntity instanceof LivingEntity ghast)) return null;

        ItemStack equippedHarness = ghast.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.BODY);
        if (equippedHarness.isEmpty()) {
            equippedHarness = new ItemStack(Items.WHITE_HARNESS);
        }

        ItemStack item = equippedHarness.copy();

        CustomData customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        try {
            customData = customData.update(tag -> {
                try {
                    ContraptionStorage.storeInItem(tag, bytes);
                } catch (IOException e) {
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
        } catch (RuntimeException e) {
            CraftEnginePolyfills.instance().getLogger()
                .warning("[Contraption] failed to save ghast harness to item: " + e);
            return null;
        }
        item.set(DataComponents.CUSTOM_DATA, customData);

        item.set(DataComponents.CUSTOM_NAME,
            Component.translatable("item.polyfills.ghast_harness_contraption"));
        item.set(DataComponents.LORE, new ItemLore(List.of(
            Component.translatable("item.polyfills.contraption.equip_on_ghast"),
            Component.translatable("item.polyfills.contraption.blocks",
                Component.literal(String.valueOf(blockCount)))
        )));

        entity.despawn(CePlayers.resolve(serverLevel.getWorld().getPlayers()));
        ContraptionManager.remove(state.id());
        state.level().dispose();

        ghast.setItemSlot(net.minecraft.world.entity.EquipmentSlot.BODY, ItemStack.EMPTY);

        return item;
    }

    // ============================== Private helpers ==============================

    private static Entity handleOf(org.bukkit.entity.Entity entity) {
        return ((org.bukkit.craftbukkit.entity.CraftEntity) entity).getHandle();
    }

    private static void untag(org.bukkit.entity.Entity ghast) {
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
        Double x = pdc.get(ANCHOR_X, PersistentDataType.DOUBLE);
        Double y = pdc.get(ANCHOR_Y, PersistentDataType.DOUBLE);
        Double z = pdc.get(ANCHOR_Z, PersistentDataType.DOUBLE);
        if (x == null || y == null || z == null) {
            return Vec3.ZERO;
        }
        return new Vec3(x, y, z);
    }

    private static OptionalDouble readYawOffset(PersistentDataContainer pdc) {
        Double yaw = pdc.get(ANCHOR_YAW, PersistentDataType.DOUBLE);
        return yaw == null ? OptionalDouble.empty() : OptionalDouble.of(yaw);
    }


    private static GhastFollowBehavior findFollowBehavior(ContraptionState state) {
        for (MovementBehavior behavior : state.behaviors()) {
            if (behavior instanceof GhastFollowBehavior follow) {
                return follow;
            }
        }
        return null;
    }
}
