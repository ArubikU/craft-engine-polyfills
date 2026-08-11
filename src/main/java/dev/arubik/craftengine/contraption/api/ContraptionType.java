package dev.arubik.craftengine.contraption.api;

import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * Defines a custom contraption type.
 *
 * <p>
 * Implement this to create submarines (buoyancy), airships (balloon physics), mechs (leg IK),
 * or trains with derailment. Register via {@link ContraptionTypeRegistry}.
 *
 * <p>
 * Study {@link dev.arubik.craftengine.contraption.type.MinecartContraptionType} and
 * {@link dev.arubik.craftengine.contraption.type.GhastContraptionType} as reference implementations
 * for entity-anchored types. Study {@link dev.arubik.craftengine.contraption.type.PhysContraptionType}
 * for dynamic physics control.
 *
 * <p>
 * <b>Example</b>:
 * <pre>{@code
 * public class SubmarineType implements ContraptionType {
 *     &#64;Override
 *     public ContraptionEntity createEntity(Level level, ContraptionState state) {
 *         return new SubmarineEntity(level, state);
 *     }
 *
 *     &#64;Override
 *     public boolean canAssemble(Level level, BlockPos anchor, Set<BlockPos> blocks) {
 *         // Must be underwater
 *         return blocks.stream().allMatch(pos -> !level.getFluidState(pos).isEmpty());
 *     }
 *
 *     &#64;Override
 *     public void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
 *         // Add buoyancy + steering
 *         state.addBehavior(new BuoyancyBehavior(level, anchor));
 *         state.addBehavior(new SubmarineSteeringBehavior());
 *     }
 * }
 * }</pre>
 */
public interface ContraptionType {

    /**
     * Creates the contraption entity for this type.
     *
     * <p>
     * Called during assembly. The returned entity handles tick logic, physics, rendering.
     *
     * @param level the level the contraption will exist in
     * @param state the captured structure state (blocks, positions, NBT)
     * @return the contraption entity instance
     */
    ContraptionEntity createEntity(Level level, ContraptionState state);

    /**
     * Attaches movement behaviors to the contraption state.
     *
     * <p>
     * Called after structure capture but before entity creation. Add behaviors that control
     * movement, physics, or interaction. For entity-anchored types, spawn anchor entity here.
     *
     * <p>
     * <b>Entity-anchored example</b> (minecart pattern):
     * <pre>{@code
     * // Get ServerLevel for entity spawn
     * if (!(level instanceof ServerLevel serverLevel)) return;
     *
     * // Spawn minecart via EntityType
     * Entity cart = EntityType.MINECART.create(serverLevel, EntitySpawnReason.TRIGGERED);
     * if (cart == null) return;
     *
     * cart.setPos(anchor.getX() + 0.5, anchor.getY(), anchor.getZ() + 0.5);
     * cart.setInvulnerable(true);
     * serverLevel.addFreshEntity(cart);
     *
     * // Attach follow behavior with entity UUID
     * state.addBehavior(new MinecartFollowBehavior(cart.getUUID()));
     * }</pre>
     *
     * <p>
     * <b>Physics example</b>:
     * <pre>{@code
     * // Attached by PhysicsWorld automatically (no public constructor)
     * // See ContraptionAssembler#attachDefaultBehavior PHYS case
     * }</pre>
     *
     * @param state  the contraption state to attach behaviors to
     * @param level  the NMS level (net.minecraft.world.level.Level)
     * @param anchor the bearing/anchor block position
     */
    default void attachBehaviors(ContraptionState state, Level level, BlockPos anchor) {
        // Default: no behaviors. Override to attach movement/physics/control behaviors.
    }

    /**
     * Validates whether assembly can proceed for this type.
     *
     * <p>
     * Called before assembly. Return false to cancel assembly (e.g., submarine not underwater,
     * airship missing balloon blocks). Default allows all assemblies.
     *
     * @param level  the level being assembled in
     * @param anchor the bearing/anchor block position
     * @param blocks all blocks that will be captured
     * @return true if assembly can proceed
     */
    default boolean canAssemble(Level level, BlockPos anchor, Set<BlockPos> blocks) {
        return true;
    }

    /**
     * Optional message shown when {@link #canAssemble} returns false.
     *
     * @return user-facing error message, or null for generic message
     */
    default String getAssemblyFailureMessage() {
        return null;
    }

    /**
     * Called before disassembly. Perform cleanup (remove anchor entities, clear PDC tags).
     *
     * <p>
     * Entity-anchored types: remove the anchor entity via NMS here. Block-anchored types: no-op.
     *
     * <p>
     * <b>Example</b>:
     * <pre>{@code
     * // Find MinecartFollowBehavior, remove the NMS anchor
     * for (MovementBehavior b : state.behaviors()) {
     *     if (b instanceof MinecartFollowBehavior follow) {
     *         net.minecraft.world.entity.Entity cart =
     *             ((ServerLevel) level).getEntity(follow.entityId());
     *         if (cart != null) cart.discard();
     *         break;
     *     }
     * }
     * }</pre>
     *
     * @param state the contraption state being disassembled
     * @param level the NMS level
     */
    default void onDisassemble(ContraptionState state, Level level) {
        // Default: no cleanup
    }

    /**
     * Whether this contraption type supports rotation.
     *
     * <p>
     * Rotational contraptions (windmills, gears) have angular velocity. Non-rotational
     * contraptions (minecarts, submarines) move linearly only.
     *
     * @return true if this type rotates
     */
    default boolean isRotational() {
        return false;
    }

    /**
     * Whether this contraption type can be controlled as a vehicle.
     *
     * <p>
     * Vehicle contraptions accept player input (WASD, jump). Non-vehicle contraptions
     * are autonomous or externally controlled.
     *
     * @return true if players can drive this
     */
    default boolean isVehicle() {
        return false;
    }

    /**
     * Whether this contraption type uses dynamic physics (can enable/disable per instance).
     *
     * <p>
     * Physics-enabled types (PHYS, VEHICLE, custom submarines) can toggle gravity/collision
     * at runtime. Block-anchored types (LINEAR, ROTATIONAL) return false.
     *
     * @return true if physics can be controlled dynamically
     */
    default boolean hasPhysics() {
        return false;
    }

    /**
     * Maximum number of blocks this type can capture.
     *
     * <p>
     * Used to prevent lag from massive contraptions. Return -1 for no limit.
     *
     * @return max blocks, or -1 for unlimited
     */
    default int getMaxBlocks() {
        return -1;
    }

    /**
     * Called when anchor entity dies (entity-anchored types only).
     *
     * <p>
     * Implementation should:
     * - Disassemble contraption (restore blocks or despawn)
     * - Strip packed bytes from dropped items (prevent duplication)
     * - Clean up PDC tags
     *
     * <p>
     * <b>Ghast example</b>: death drops harness, must strip packed bytes + disassemble
     * <pre>{@code
     * // Strip packed bytes from harness drops
     * for (net.minecraft.world.item.ItemStack drop : drops) {
     *     if (isPackedItem(drop)) {
     *         // Clear structure bytes - blocks restored to world instead
     *         drop.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
     *     }
     * }
     * // Disassemble contraption
     * ContraptionEntity entity = findContraptionByAnchor(anchorId);
     * if (entity != null) disassembleInPlace(entity);
     * }</pre>
     *
     * @param anchorEntity the NMS entity that died
     * @param level        the NMS level
     * @param drops        NMS item drops (mutable, can strip bytes)
     */
    default void onAnchorDeath(net.minecraft.world.entity.Entity anchorEntity, Level level,
                               java.util.List<net.minecraft.world.item.ItemStack> drops) {
        // Default: no death handling
    }

    /**
     * Called when anchor entity takes damage (entity-anchored types only).
     *
     * <p>
     * Return true to cancel damage. Use this to protect anchor entities from destruction.
     *
     * <p>
     * <b>Minecart example</b>: bearing minecarts are invulnerable, cancel all damage
     * <pre>{@code
     * // Check if entity is bearing anchor
     * if (isBearingAnchor(entity)) {
     *     return true; // cancel damage
     * }
     * return false;
     * }</pre>
     *
     * @param anchorEntity the NMS entity being damaged
     * @param level        the NMS level
     * @return true to cancel damage event
     */
    default boolean onAnchorDamage(net.minecraft.world.entity.Entity anchorEntity, Level level) {
        return false; // default: don't cancel
    }

    /**
     * Whether this contraption type can be packed to an item (save-to-item).
     *
     * <p>
     * Entity-anchored types (MINECART, GHAST) pack to items (chest-minecart, harness).
     * Block-anchored types (LINEAR, ROTATIONAL) stay in world.
     *
     * @return true if this type supports save-to-item
     */
    default boolean canPackToItem() {
        return false;
    }

    /**
     * Whether this NMS ItemStack is a packed contraption of this type.
     *
     * <p>
     * Check CustomData for "contraption_structure" bytes.
     *
     * @param item the NMS ItemStack to check
     * @return true if item contains packed contraption
     */
    default boolean isPackedItem(net.minecraft.world.item.ItemStack item) {
        return false;
    }

    /**
     * Reassembles a packed contraption from an NMS ItemStack.
     *
     * <p>
     * Called when player places packed item (right-click rail for minecart, equip for ghast).
     * Implementation should:
     * 1. Deserialize structure from item CustomData
     * 2. Spawn anchor entity (if entity-anchored)
     * 3. Create ContraptionState from structure
     * 4. Attach behaviors
     * 5. Register in ContraptionManager
     * 6. Return ContraptionEntity
     *
     * @param item      the packed NMS ItemStack
     * @param level     the NMS level to spawn in
     * @param spawnPos  the block position to spawn at (rail for minecart, ghast position for ghast)
     * @return the reassembled ContraptionEntity, or null if failed
     */
    default ContraptionEntity fromItem(net.minecraft.world.item.ItemStack item, Level level, BlockPos spawnPos) {
        return null; // default: not restorable
    }

    /**
     * Packs this contraption into an NMS ItemStack.
     *
     * <p>
     * Called when player interacts with hammer + sneak (or type-specific trigger).
     * Implementation should:
     * 1. Serialize structure to bytes (ContraptionStorage)
     * 2. Create NMS ItemStack with custom data (DataComponents)
     * 3. Set display name/lore via i18n (TranslatableComponent)
     * 4. Despawn contraption (remove from manager, dispose level, remove anchor entity)
     * 5. Return packed NMS ItemStack
     *
     * <p>
     * <b>Data format</b> (consistent across all types):
     * - structure: byte[] (ContraptionStorage serialized)
     * - anchor_offset: Vec3 (for entity-anchored types, offset from anchor to origin)
     * - yaw_offset: double (for entity-anchored types, yaw correction)
     * - block_count: int (for display lore)
     *
     * <p>
     * <b>Example</b> (minecart pattern):
     * <pre>{@code
     * // Serialize structure
     * byte[] bytes = ContraptionStorage.toBytes(
     *     ContraptionStorage.dumpLevel(state.level()));
     *
     * // Create NMS ItemStack
     * ItemStack item = new ItemStack(Items.CHEST_MINECART);
     *
     * // Set custom data
     * CustomData customData = item.getOrDefault(
     *     DataComponents.CUSTOM_DATA, CustomData.EMPTY);
     * customData = customData.update(tag -> {
     *     tag.putByteArray("contraption_structure", bytes);
     *     tag.putInt("contraption_block_count", state.level().blockCount());
     * });
     * item.set(DataComponents.CUSTOM_DATA, customData);
     *
     * // Set i18n display
     * item.set(DataComponents.CUSTOM_NAME,
     *     Component.translatable("item.polyfills.minecart_contraption"));
     * item.set(DataComponents.LORE, new ItemLore(List.of(
     *     Component.translatable("item.polyfills.contraption.blocks",
     *         Component.literal(String.valueOf(blockCount)))
     * )));
     *
     * // Despawn contraption
     * entity.despawn(players);
     * ContraptionManager.remove(state.id());
     * state.level().dispose();
     * // Remove anchor entity
     * anchorEntity.discard();
     *
     * return item;
     * }</pre>
     *
     * @param entity the contraption entity to pack
     * @param level  the NMS level
     * @return the packed NMS ItemStack, or null if packing failed
     */
    default net.minecraft.world.item.ItemStack toItem(ContraptionEntity entity, Level level) {
        return null; // default: not packable
    }

    /**
     * Checks if an entity is a bearing of this contraption type.
     *
     * <p>
     * Used by {@code BearingHammerListener} to identify which type owns an entity.
     * Return true if this entity is a bearing anchor for this type.
     *
     * <p>
     * <b>Minecart example</b>: check PDC for bearing marker
     * <pre>{@code
     * org.bukkit.entity.Entity bukkit = entity.getBukkitEntity();
     * return bukkit.getPersistentDataContainer()
     *     .has(MINECART_BEARING_KEY, PersistentDataType.BYTE);
     * }</pre>
     *
     * <p>
     * <b>Ghast example</b>: check if HappyGhast with harness + bearing marker
     * <pre>{@code
     * if (!(entity.getBukkitEntity() instanceof HappyGhast ghast)) return false;
     * return ghast.getPersistentDataContainer()
     *     .has(GHAST_BEARING_KEY, PersistentDataType.BYTE);
     * }</pre>
     *
     * @param entity NMS entity to check
     * @return true if this entity is a bearing of this type
     */
    default boolean isBearingEntity(net.minecraft.world.entity.Entity entity) {
        return false; // default: not entity-anchored
    }

    /**
     * Gets the contraption UUID from a bearing entity.
     *
     * <p>
     * Called AFTER {@link #isBearingEntity} returns true. Listener uses this to look up
     * the live {@link dev.arubik.craftengine.contraption.ContraptionEntity} in ContraptionManager.
     *
     * <p>
     * <b>Example</b>: read from entity PDC
     * <pre>{@code
     * org.bukkit.entity.Entity bukkit = entity.getBukkitEntity();
     * String id = bukkit.getPersistentDataContainer()
     *     .get(CONTRAPTION_ID_KEY, PersistentDataType.STRING);
     * return id == null ? null : UUID.fromString(id);
     * }</pre>
     *
     * @param entity NMS entity (guaranteed to pass {@link #isBearingEntity})
     * @return contraption UUID, or null if not found/orphaned
     */
    default java.util.UUID getContraptionId(net.minecraft.world.entity.Entity entity) {
        return null; // default: not entity-anchored
    }

    /**
     * Rehydrates a contraption from a bearing entity on chunk load.
     *
     * <p>
     * Called by {@code ContraptionChunkLifecycleListener} when chunk loads with a bearing entity.
     * Type should check if already live (via {@link #getContraptionId} + ContraptionManager lookup),
     * deserialize structure from entity PDC, rebuild ContraptionLevel, re-register in ContraptionManager.
     *
     * <p>
     * <b>Example</b>: read bytes from PDC, rebuild level
     * <pre>{@code
     * UUID id = getContraptionId(entity);
     * if (id == null || ContraptionManager.get(id) != null) return; // already live or no data
     *
     * byte[] bytes = readStructureFromPDC(entity);
     * if (bytes == null) return;
     *
     * ContraptionLevel level = ContraptionLevel.create(...);
     * ContraptionStorage.loadLevel(level, ContraptionStorage.fromBytes(bytes));
     *
     * ContraptionState state = new ContraptionState(id, ...);
     * // ... attach behaviors, furniture, etc.
     * ContraptionManager.register(new ContraptionEntity(state));
     * }</pre>
     *
     * @param entity NMS bearing entity
     * @param level NMS level
     */
    default void onBearingEntityLoad(net.minecraft.world.entity.Entity entity, Level level) {
        // Default: no-op (block-anchored types handled separately)
    }

    /**
     * Saves contraption structure to bearing entity on chunk unload.
     *
     * <p>
     * Called by {@code ContraptionChunkLifecycleListener} BEFORE despawn/dispose/remove.
     * Type should serialize CURRENT state to entity PDC so {@link #onBearingEntityLoad} can restore it.
     *
     * <p>
     * <b>Example</b>: dump level to bytes, write to PDC
     * <pre>{@code
     * if (state.level() == null) return;
     * byte[] bytes = ContraptionStorage.toBytes(ContraptionStorage.dumpLevel(state.level()));
     * writeToPDC(entity, bytes);
     * }</pre>
     *
     * @param entity NMS bearing entity
     * @param state contraption state (live, not yet despawned)
     */
    default void onBearingEntityUnload(net.minecraft.world.entity.Entity entity,
                                        ContraptionState state) {
        // Default: no-op
    }
}
