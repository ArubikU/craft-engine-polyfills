# ContraptionElement Refactor - Self-Contained Elements

## Philosophy

**Elements are self-contained, autonomous units.** Each element:
1. **Owns its config/data** - BlockState, furniture definition, entity NBT, seat mount UUID
2. **Handles its own rendering** - spawns/updates packet mirrors directly
3. **Manages its lifecycle** - capture → tick → render → disassemble

No centralized swarm orchestrator. Elements render themselves.

---

## Core Interface

```java
package dev.arubik.craftengine.contraption.furniture;

import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.World;
import java.util.List;

/**
 * Self-contained contraption element. Each element stores its own config,
 * renders itself, and handles its own lifecycle independently.
 */
public interface ContraptionElement {
    
    // ============ IDENTITY ============
    
    /** Element type for polymorphic dispatch. */
    ElementType type();
    
    /** Element's local position relative to bearing (yaw-0 basis). */
    Vec3 localOffset();
    
    /** True if this element is still valid (not destroyed/invalidated). */
    boolean isValid();
    
    // ============ LIFECYCLE ============
    
    /**
     * Per-tick update. Element mutates its own internal state (animations,
     * AI, mount positions) but does NOT modify ContraptionState.
     */
    default void tick(ContraptionState state, Vec3 bearing, 
                     double yaw, double pitch, double roll, double scale) {}
    
    /**
     * Render this element. Element spawns/updates its own packet mirrors
     * at the given bearing transform. Called every tick after tick().
     */
    void render(List<Player> viewers, Vec3 bearing, 
               double yaw, double pitch, double roll, double scale);
    
    /**
     * Despawn all packet mirrors spawned by this element.
     * Called when element is removed or contraption is torn down.
     */
    void despawn();
    
    /**
     * Disassemble: restore this element back into the real world.
     * Element handles its own restoration logic.
     */
    void disassemble(World world, BlockPos snappedBearingPos, int quarterTurns);
    
    // ============ PERSISTENCE ============
    
    /** Serialize element config/data to NBT for save. */
    CompoundTag toNbt();
    
    /** Deserialize element from NBT during load. */
    static ContraptionElement fromNbt(CompoundTag tag, ContraptionLevel level) {
        ElementType type = ElementType.valueOf(tag.getString("type"));
        return switch (type) {
            case BLOCK -> ContraptionBlock.fromNbt(tag, level);
            case FURNITURE -> ContraptionFurniture.fromNbt(tag, level);
            case SEAT -> ContraptionSeat.fromNbt(tag, level);
            case ENTITY -> ContraptionEntity.fromNbt(tag, level);
        };
    }
    
    enum ElementType {
        BLOCK,      // Vanilla/mod block
        FURNITURE,  // CraftEngine furniture
        SEAT,       // Abstract seat (block or furniture source)
        ENTITY      // Captured mob/item
    }
}
```

---

## Element Implementations

### 1. ContraptionBlock

```java
package dev.arubik.craftengine.contraption.furniture;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/**
 * Vanilla/mod block captured into contraption.
 * Stores BlockState + local position, renders as Display entity.
 */
public record ContraptionBlock(
    Vec3 localOffset,
    BlockState blockState,
    CompoundTag blockEntityData,  // null if no BE
    
    // Rendering state (mutable)
    int mirrorEntityId           // Spawned Display entity ID
) implements ContraptionElement {
    
    @Override
    public ElementType type() { return ElementType.BLOCK; }
    
    @Override
    public boolean isValid() {
        // Block is valid if it hasn't been mined/destroyed
        return blockState != null && !blockState.isAir();
    }
    
    @Override
    public void render(List<Player> viewers, Vec3 bearing, 
                      double yaw, double pitch, double roll, double scale) {
        // Compute world position from bearing transform
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, bearing, yaw, pitch, roll, scale);
        
        // Spawn or update Display entity packet mirror
        if (mirrorEntityId == 0) {
            // Spawn new Display entity
            mirrorEntityId = spawnBlockDisplay(viewers, worldPos, blockState, yaw);
        } else {
            // Update existing Display entity position
            updateBlockDisplay(mirrorEntityId, viewers, worldPos, yaw);
        }
    }
    
    @Override
    public void despawn() {
        if (mirrorEntityId != 0) {
            despawnEntity(mirrorEntityId);
            mirrorEntityId = 0;
        }
    }
    
    @Override
    public void disassemble(World world, BlockPos snappedBearingPos, int quarterTurns) {
        // Rotate local offset by quarterTurns
        Vec3 rotated = ContraptionCapture.rotateLocalQuarterTurns(localOffset, quarterTurns);
        BlockPos worldPos = snappedBearingPos.offset(
            (int) rotated.x, (int) rotated.y, (int) rotated.z
        );
        
        // Rotate BlockState facing
        BlockState rotatedState = blockState.rotate(Rotation.values()[quarterTurns % 4]);
        
        // Place block in world
        world.setBlock(worldPos, rotatedState, 3);
        
        // Restore block entity data if present
        if (blockEntityData != null) {
            BlockEntity be = world.getBlockEntity(worldPos);
            if (be != null) {
                be.load(blockEntityData);
            }
        }
    }
    
    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "BLOCK");
        tag.put("pos", newDoubleList(localOffset.x, localOffset.y, localOffset.z));
        tag.put("state", NbtUtils.writeBlockState(blockState));
        if (blockEntityData != null) {
            tag.put("be", blockEntityData);
        }
        return tag;
    }
    
    public static ContraptionBlock fromNbt(CompoundTag tag, ContraptionLevel level) {
        ListTag pos = tag.getList("pos", 6);
        Vec3 localOffset = new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        BlockState state = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), tag.getCompound("state"));
        CompoundTag be = tag.contains("be") ? tag.getCompound("be") : null;
        return new ContraptionBlock(localOffset, state, be, 0);
    }
}
```

### 2. ContraptionFurniture (Updated)

```java
package dev.arubik.craftengine.contraption.furniture;

import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.util.Key;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/**
 * CraftEngine furniture captured into contraption.
 * Stores furniture definition + live instance, renders via furniture's own elements.
 */
public record ContraptionFurniture(
    Vec3 localOffset,
    float yawOffsetDegrees,
    Key definitionId,
    String variantName,
    BukkitFurniture liveFurniture,  // Lives in ContraptionLevel
    
    // Rendering state (mutable)
    List<Integer> mirrorEntityIds   // Display/Interaction entities spawned
) implements ContraptionElement {
    
    @Override
    public ElementType type() { return ElementType.FURNITURE; }
    
    @Override
    public boolean isValid() {
        return liveFurniture != null && liveFurniture.isValid();
    }
    
    @Override
    public void render(List<Player> viewers, Vec3 bearing,
                      double yaw, double pitch, double roll, double scale) {
        if (!isValid()) return;
        
        // Compute world position from bearing transform
        Vec3 worldPos = ContraptionMath.renderPosition(localOffset, bearing, yaw, pitch, roll, scale);
        float worldYaw = (float) Math.toDegrees(yaw) + yawOffsetDegrees;
        
        // Render furniture's elements (item displays, armor stands, interactions)
        // Either spawn new or update existing mirrors
        renderFurnitureElements(liveFurniture, viewers, worldPos, worldYaw, scale);
    }
    
    @Override
    public void despawn() {
        for (int id : mirrorEntityIds) {
            despawnEntity(id);
        }
        mirrorEntityIds.clear();
    }
    
    @Override
    public void disassemble(World world, BlockPos snappedBearingPos, int quarterTurns) {
        // Destroy live furniture in ContraptionLevel
        if (isValid()) {
            CraftEngineFurniture.remove(liveFurniture.getBukkitEntity(), false, false);
        }
        
        // Rotate and place new furniture in real world
        Vec3 rotated = ContraptionCapture.rotateLocalQuarterTurns(localOffset, quarterTurns);
        Vec3 worldPos = Vec3.atLowerCornerOf(snappedBearingPos).add(rotated);
        float worldYaw = yawOffsetDegrees + quarterTurns * 90f;
        
        Location loc = new Location(world, worldPos.x, worldPos.y, worldPos.z, worldYaw, 0f);
        CraftEngineFurniture.place(loc, definitionId, variantName, true);
    }
    
    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "FURNITURE");
        tag.put("pos", newDoubleList(localOffset.x, localOffset.y, localOffset.z));
        tag.putFloat("yaw", yawOffsetDegrees);
        tag.putString("id", definitionId.toString());
        tag.putString("variant", variantName);
        return tag;
    }
    
    public static ContraptionFurniture fromNbt(CompoundTag tag, ContraptionLevel level) {
        ListTag pos = tag.getList("pos", 6);
        Vec3 localOffset = new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        float yaw = tag.getFloat("yaw");
        Key id = Key.of(tag.getString("id"));
        String variant = tag.getString("variant");
        
        // Re-place furniture in ContraptionLevel
        BukkitFurniture live = ContraptionFurnitureCapture.placeInFakeLevel(
            level, id, variant, localOffset, yaw
        );
        
        return new ContraptionFurniture(localOffset, yaw, id, variant, live, new ArrayList<>());
    }
}
```

### 3. ContraptionSeat (New)

```java
package dev.arubik.craftengine.contraption.furniture;

import java.util.UUID;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Player;

/**
 * Abstract seat element - unified representation for block seats and furniture seats.
 * Handles rider mounting, carrying, and dismounting via ContraptionSeatMount.
 */
public class ContraptionSeat implements ContraptionElement {
    
    private final Vec3 localOffset;
    private final float yawOffsetDegrees;
    private final SeatSource source;
    
    // Mutable state
    private UUID occupantId;
    private UUID mountEntityId;
    
    public ContraptionSeat(Vec3 localOffset, float yawOffsetDegrees, 
                           SeatSource source, UUID occupantId, UUID mountEntityId) {
        this.localOffset = localOffset;
        this.yawOffsetDegrees = yawOffsetDegrees;
        this.source = source;
        this.occupantId = occupantId;
        this.mountEntityId = mountEntityId;
    }
    
    @Override
    public ElementType type() { return ElementType.SEAT; }
    
    @Override
    public Vec3 localOffset() { return localOffset; }
    
    @Override
    public boolean isValid() {
        return source != null && source.isValid();
    }
    
    @Override
    public void tick(ContraptionState state, Vec3 bearing,
                    double yaw, double pitch, double roll, double scale) {
        // If occupied, reposition mount entity to track bearing transform
        if (occupantId != null && mountEntityId != null) {
            org.bukkit.entity.Entity mount = ContraptionSeatMount.resolve(mountEntityId);
            if (mount == null) {
                // Mount despawned - clear rider
                occupantId = null;
                mountEntityId = null;
                return;
            }
            
            // Compute seat's current world position
            Vec3 worldPos = ContraptionMath.renderPosition(localOffset, bearing, yaw, pitch, roll, scale);
            float worldYaw = (float) Math.toDegrees(yaw) + yawOffsetDegrees;
            
            // Move mount (rider follows automatically via vanilla passenger mechanics)
            ContraptionSeatMount.reposition(mount, worldPos, worldYaw);
            
            // Apply contraption scale to rider
            Player rider = org.bukkit.Bukkit.getPlayer(occupantId);
            if (rider != null) {
                ContraptionSeatMount.applyContraptionScale(rider, scale);
            }
        }
    }
    
    @Override
    public void render(List<Player> viewers, Vec3 bearing,
                      double yaw, double pitch, double roll, double scale) {
        // Seats don't render visually - the mount entity (handled in tick) is invisible
        // The underlying block/furniture renders itself separately
    }
    
    @Override
    public void despawn() {
        // Dismount rider if still seated
        if (mountEntityId != null) {
            ContraptionSeatMount.unmount(mountEntityId);
            occupantId = null;
            mountEntityId = null;
        }
    }
    
    @Override
    public void disassemble(World world, BlockPos snappedBearingPos, int quarterTurns) {
        // Just dismount - the underlying block/furniture handles its own restoration
        despawn();
    }
    
    // ============ SEAT MANAGEMENT ============
    
    public boolean isOccupied() { return occupantId != null; }
    
    public void sitPlayer(Player player, org.bukkit.World world) {
        if (isOccupied()) return;
        
        // Compute current world position for mount spawn
        // (caller must provide current bearing transform)
        Vec3 worldPos = ...; // From current state
        
        org.bukkit.entity.ArmorStand mount = ContraptionSeatMount.mount(
            player, world, worldPos, yawOffsetDegrees
        );
        
        if (mount != null) {
            occupantId = player.getUniqueId();
            mountEntityId = mount.getUniqueId();
        }
    }
    
    public void standPlayer() {
        despawn();
    }
    
    // ============ PERSISTENCE ============
    
    @Override
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", "SEAT");
        tag.put("pos", newDoubleList(localOffset.x, localOffset.y, localOffset.z));
        tag.putFloat("yaw", yawOffsetDegrees);
        tag.put("source", source.toNbt());
        if (occupantId != null) {
            tag.putUUID("occupant", occupantId);
            tag.putUUID("mount", mountEntityId);
        }
        return tag;
    }
    
    public static ContraptionSeat fromNbt(CompoundTag tag, ContraptionLevel level) {
        ListTag pos = tag.getList("pos", 6);
        Vec3 localOffset = new Vec3(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
        float yaw = tag.getFloat("yaw");
        SeatSource source = SeatSource.fromNbt(tag.getCompound("source"), level);
        UUID occupant = tag.hasUUID("occupant") ? tag.getUUID("occupant") : null;
        UUID mount = tag.hasUUID("mount") ? tag.getUUID("mount") : null;
        return new ContraptionSeat(localOffset, yaw, source, occupant, mount);
    }
    
    // ============ SEAT SOURCE ============
    
    public sealed interface SeatSource permits BlockSeatSource, FurnitureSeatSource {
        boolean isValid();
        CompoundTag toNbt();
        static SeatSource fromNbt(CompoundTag tag, ContraptionLevel level) {
            String type = tag.getString("sourceType");
            return switch (type) {
                case "BLOCK" -> BlockSeatSource.fromNbt(tag, level);
                case "FURNITURE" -> FurnitureSeatSource.fromNbt(tag, level);
                default -> throw new IllegalArgumentException("Unknown seat source: " + type);
            };
        }
    }
    
    public record BlockSeatSource(BlockPos localBlockPos, String blockId) implements SeatSource {
        @Override
        public boolean isValid() { return true; }
        
        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putString("sourceType", "BLOCK");
            tag.putLong("blockPos", localBlockPos.asLong());
            tag.putString("blockId", blockId);
            return tag;
        }
        
        public static BlockSeatSource fromNbt(CompoundTag tag, ContraptionLevel level) {
            BlockPos pos = BlockPos.of(tag.getLong("blockPos"));
            String id = tag.getString("blockId");
            return new BlockSeatSource(pos, id);
        }
    }
    
    public record FurnitureSeatSource(UUID furnitureElementId, int seatIndex) implements SeatSource {
        @Override
        public boolean isValid() {
            // Furniture is valid if its element is still in the contraption
            // (checked by caller - ContraptionState)
            return true;
        }
        
        @Override
        public CompoundTag toNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putString("sourceType", "FURNITURE");
            tag.putUUID("furnitureId", furnitureElementId);
            tag.putInt("seatIndex", seatIndex);
            return tag;
        }
        
        public static FurnitureSeatSource fromNbt(CompoundTag tag, ContraptionLevel level) {
            UUID id = tag.getUUID("furnitureId");
            int index = tag.getInt("seatIndex");
            return new FurnitureSeatSource(id, index);
        }
    }
}
```

---

## ContraptionState Refactor

```java
public final class ContraptionState {
    
    // NEW: Unified element storage
    private List<ContraptionElement> elements = new ArrayList<>();
    
    // OLD: Deprecated - kept for migration
    @Deprecated private List<ContraptionFurniture> furniture = List.of();
    @Deprecated private Map<UUID, Vec3> seatedRiders = Map.of();
    @Deprecated private Map<UUID, UUID> seatedRiderMounts = Map.of();
    
    // ============ NEW API ============
    
    public List<ContraptionElement> elements() {
        return Collections.unmodifiableList(elements);
    }
    
    public void setElements(List<ContraptionElement> elements) {
        this.elements = new ArrayList<>(elements);
    }
    
    public void addElement(ContraptionElement element) {
        elements.add(element);
    }
    
    public void removeElement(ContraptionElement element) {
        element.despawn();
        elements.remove(element);
    }
    
    // Convenience filters
    public List<ContraptionBlock> blocks() {
        return elements.stream()
            .filter(e -> e.type() == ElementType.BLOCK)
            .map(e -> (ContraptionBlock) e)
            .toList();
    }
    
    public List<ContraptionFurniture> furniture() {
        return elements.stream()
            .filter(e -> e.type() == ElementType.FURNITURE)
            .map(e -> (ContraptionFurniture) e)
            .toList();
    }
    
    public List<ContraptionSeat> seats() {
        return elements.stream()
            .filter(e -> e.type() == ElementType.SEAT)
            .map(e -> (ContraptionSeat) e)
            .toList();
    }
}
```

---

## ContraptionEntity Refactor

```java
public final class ContraptionEntity {
    
    private final ContraptionState state;
    
    // OLD: Per-type swarms (deprecated)
    @Deprecated private ContraptionDisplaySwarm displaySwarm;
    @Deprecated private ContraptionFurnitureSwarm furnitureSwarm;
    @Deprecated private ContraptionHitboxSwarm hitboxSwarm;
    
    // ============ TICK & RENDER ============
    
    public void tick() {
        Vec3 bearing = state.position();
        double yaw = state.yawRadians();
        double pitch = state.pitchRadians();
        double roll = state.rollRadians();
        double scale = state.scale();
        
        // Tick all elements
        for (ContraptionElement e : state.elements()) {
            if (e.isValid()) {
                e.tick(state, bearing, yaw, pitch, roll, scale);
            }
        }
        
        // Render all elements
        List<Player> viewers = getViewers();
        for (ContraptionElement e : state.elements()) {
            if (e.isValid()) {
                e.render(viewers, bearing, yaw, pitch, roll, scale);
            }
        }
    }
    
    public void despawn(List<Player> viewers) {
        for (ContraptionElement e : state.elements()) {
            e.despawn();
        }
    }
}
```

---

## Capture Refactor

```java
public static List<ContraptionElement> captureAll(
    Level level,
    Set<BlockPos> worldPositions,
    BlockPos bearingWorldPos,
    ContraptionLevel fakeLevel
) {
    List<ContraptionElement> elements = new ArrayList<>();
    
    // 1. Capture blocks
    for (BlockPos worldPos : worldPositions) {
        BlockState state = level.getBlockState(worldPos);
        if (state.isAir()) continue;
        
        Vec3 localOffset = Vec3.atLowerCornerOf(worldPos.subtract(bearingWorldPos));
        CompoundTag beData = getBlockEntityData(level, worldPos);
        
        elements.add(new ContraptionBlock(localOffset, state, beData, 0));
    }
    
    // 2. Capture furniture + extract furniture seats
    List<ContraptionFurniture> capturedFurniture = captureFurniture(level, worldPositions, bearingWorldPos, fakeLevel);
    elements.addAll(capturedFurniture);
    
    for (ContraptionFurniture furn : capturedFurniture) {
        elements.addAll(extractSeatsFromFurniture(furn, bearingWorldPos));
    }
    
    // 3. Capture block seats (stairs, slabs, custom sittable blocks)
    elements.addAll(captureBlockSeats(worldPositions, bearingWorldPos, fakeLevel));
    
    // 4. Capture entities (future)
    // elements.addAll(captureEntities(level, worldPositions, bearingWorldPos, fakeLevel));
    
    return elements;
}
```

---

## Migration Strategy

### Phase 1: Interface + ContraptionSeat (Current)
- [x] Create ContraptionElement interface
- [ ] Implement ContraptionSeat with sealed SeatSource
- [ ] Keep old ContraptionFurniture unchanged

### Phase 2: ContraptionBlock
- [ ] Create ContraptionBlock implementing interface
- [ ] Add block capture to captureAll()
- [ ] Keep old block storage parallel

### Phase 3: Update ContraptionFurniture
- [ ] Add interface implementation to ContraptionFurniture
- [ ] Add render() and despawn() methods
- [ ] Keep old furniture swarm parallel

### Phase 4: ContraptionState Migration
- [ ] Add elements list to ContraptionState
- [ ] Populate elements during capture
- [ ] Keep old furniture/seatedRiders parallel

### Phase 5: ContraptionEntity Migration
- [ ] Update tick loop to use elements
- [ ] Update render loop to use elements
- [ ] Keep old swarms as fallback

### Phase 6: Remove Old Code
- [ ] Delete old swarms
- [ ] Delete old state fields
- [ ] Clean up deprecated methods

---

## Benefits

1. **Self-contained** - Elements own their config and rendering
2. **Polymorphic** - No type checks, clean switch on type()
3. **Extensible** - New element types slot in naturally
4. **Seat abstraction** - Block/furniture seats are identical
5. **Simpler state** - One list instead of many parallel structures
6. **Testable** - Elements can be tested in isolation
