# ContraptionElement Interface Design

## Overview
Unified abstraction for all element types captured into a contraption (blocks, furniture, seats, entities). Each element type implements common lifecycle and rendering operations.

## Core Interface

```java
package dev.arubik.craftengine.contraption.furniture;

import net.minecraft.world.phys.Vec3;

/**
 * Base interface for all element types captured into a contraption.
 * Provides common operations for position tracking, rendering, and lifecycle hooks.
 */
public interface ContraptionElement {
    
    /**
     * Element's position relative to the bearing at capture time (yaw-0 basis).
     * This is the element's fixed local coordinate - the bearing's transform
     * is applied to this every tick to compute the real-world render position.
     */
    Vec3 localOffset();
    
    /**
     * Element type discriminator for polymorphic dispatch.
     * Avoids instanceof checks and enables efficient switching.
     */
    ElementType type();
    
    /**
     * True if this element is still valid and intact.
     * - Blocks: the block state hasn't been destroyed
     * - Furniture: the live furniture instance is still valid
     * - Seats: the mount entity exists and the seat config is still present
     * - Entities: the entity is still alive
     */
    boolean isValid();
    
    /**
     * Called once per tick to update any live state (e.g., furniture animations,
     * entity AI). Does NOT handle rendering - that's the swarm's responsibility.
     */
    default void tick(ContraptionState state) {}
    
    /**
     * Called when the contraption is being disassembled. Should restore this
     * element back into the real world at the given snapped position/rotation.
     * 
     * @param world The target world
     * @param snappedBearingPos Grid-snapped bearing position
     * @param quarterTurns Number of 90-degree rotations to apply
     */
    void restore(org.bukkit.World world, net.minecraft.core.BlockPos snappedBearingPos, int quarterTurns);
    
    enum ElementType {
        BLOCK,      // Vanilla/mod block
        FURNITURE,  // CraftEngine furniture piece
        SEAT,       // Abstract seat (from block or furniture)
        ENTITY      // Captured mob/item
    }
}
```

## Seat Abstraction

Seats come from two sources:
1. **Vanilla blocks** - stairs, slabs, or any block players can sit on
2. **CraftEngine furniture** - furniture with explicit seat configs

The `ContraptionSeat` class unifies both:

```java
package dev.arubik.craftengine.contraption.furniture;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Abstract seat element - unified representation regardless of source (block or furniture).
 * Tracks the seat's position, orientation, current rider, and mount entity.
 * 
 * <p><b>Source types:</b>
 * <ul>
 *   <li>BLOCK - vanilla block (stairs/slabs) or custom block with sit behavior
 *   <li>FURNITURE - CraftEngine furniture piece with a seat config
 * </ul>
 * 
 * <p>At capture time, the system detects blocks/furniture with seats and creates
 * a ContraptionSeat for each. The seat's local offset is computed from:
 * - Block source: block position + configured sit offset (default center + 0.6Y)
 * - Furniture source: furniture's own seat entity position (already in world coords)
 */
public record ContraptionSeat(
    Vec3 localOffset,
    float yawOffsetDegrees,
    SeatSource source,
    UUID occupantId,      // null if empty
    UUID mountEntityId    // ArmorStand UUID (spawned when occupied)
) implements ContraptionElement {
    
    @Override
    public ElementType type() {
        return ElementType.SEAT;
    }
    
    @Override
    public boolean isValid() {
        // A seat is valid if its source is still intact
        return source != null && source.isValid();
    }
    
    @Override
    public void restore(org.bukkit.World world, BlockPos snappedBearingPos, int quarterTurns) {
        // Dismount rider if still seated
        if (occupantId != null) {
            ContraptionSeatMount.unmount(mountEntityId);
        }
        // Source restoration handled by block/furniture restore separately
    }
    
    /**
     * Source of this seat - either a block or a furniture piece.
     */
    public sealed interface SeatSource permits BlockSeatSource, FurnitureSeatSource {
        boolean isValid();
    }
    
    /**
     * Seat originates from a vanilla/custom block (stairs, slabs, etc.).
     */
    public record BlockSeatSource(
        BlockPos localBlockPos,           // Block's local position in contraption
        String blockId                    // For logging/debugging
    ) implements SeatSource {
        @Override
        public boolean isValid() {
            // Block seats are valid if the block itself is still in the structure
            // (checked by ContraptionLevel or ContraptionState's block map)
            return true; // Actual validation done by caller
        }
    }
    
    /**
     * Seat originates from a CraftEngine furniture piece.
     */
    public record FurnitureSeatSource(
        ContraptionFurniture furniture,   // The owning furniture element
        int seatIndex                     // Which seat within the furniture (0-based)
    ) implements SeatSource {
        @Override
        public boolean isValid() {
            return furniture != null && furniture.isValid();
        }
    }
    
    /**
     * Creates an empty seat (no rider).
     */
    public static ContraptionSeat empty(Vec3 localOffset, float yawOffsetDegrees, SeatSource source) {
        return new ContraptionSeat(localOffset, yawOffsetDegrees, source, null, null);
    }
    
    /**
     * Returns a copy of this seat with the given rider seated.
     */
    public ContraptionSeat withRider(UUID riderId, UUID mountId) {
        return new ContraptionSeat(localOffset, yawOffsetDegrees, source, riderId, mountId);
    }
    
    /**
     * Returns a copy of this seat with no rider (dismounted).
     */
    public ContraptionSeat withoutRider() {
        return new ContraptionSeat(localOffset, yawOffsetDegrees, source, null, null);
    }
    
    public boolean isOccupied() {
        return occupantId != null;
    }
}
```

## Furniture Implementation

Update `ContraptionFurniture` to implement `ContraptionElement`:

```java
package dev.arubik.craftengine.contraption.furniture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.util.Key;

/**
 * CraftEngine furniture piece captured into a contraption.
 * (existing javadoc...)
 */
public record ContraptionFurniture(
    Key definitionId, 
    String variantName, 
    Vec3 localOffset, 
    float yawOffsetDegrees,
    BukkitFurniture liveFurniture
) implements ContraptionElement {
    
    @Override
    public ElementType type() {
        return ElementType.FURNITURE;
    }
    
    @Override
    public boolean isValid() {
        return hasLiveFurniture();
    }
    
    /** True if {@link #liveFurniture} is still a valid, non-destroyed CraftEngine furniture instance. */
    public boolean hasLiveFurniture() {
        return liveFurniture != null && liveFurniture.isValid();
    }
    
    @Override
    public void restore(org.bukkit.World world, BlockPos snappedBearingPos, int quarterTurns) {
        // Delegate to existing restore logic in ContraptionFurnitureCapture
        ContraptionFurnitureCapture.restoreSingle(world, this, snappedBearingPos, quarterTurns);
    }
}
```

## Capture Flow

### 1. Block Seat Detection (New)

```java
/**
 * Scans blocks in the footprint for seats (stairs, slabs, custom sit blocks).
 * Called during assembly alongside furniture capture.
 */
public static List<ContraptionSeat> captureBlockSeats(
    Set<BlockPos> worldPositions, 
    BlockPos bearingWorldPos,
    ContraptionLevel fakeLevel
) {
    List<ContraptionSeat> seats = new ArrayList<>();
    
    for (BlockPos worldPos : worldPositions) {
        BlockState state = fakeLevel.getBlockState(worldPos); // Already captured
        
        // Check if this block is sittable (stairs, slabs, custom blocks)
        if (!isSittableBlock(state)) {
            continue;
        }
        
        // Compute seat position: block center + sit offset
        Vec3 seatWorldPos = getSeatPositionForBlock(worldPos, state);
        Vec3 localOffset = seatWorldPos.subtract(Vec3.atLowerCornerOf(bearingWorldPos));
        
        BlockPos localBlockPos = worldPos.subtract(bearingWorldPos);
        float yaw = getSeatYawForBlock(state); // Extract from block facing
        
        var source = new ContraptionSeat.BlockSeatSource(localBlockPos, state.getBlock().toString());
        seats.add(ContraptionSeat.empty(localOffset, yaw, source));
    }
    
    return seats;
}

private static boolean isSittableBlock(BlockState state) {
    // Stairs and slabs are always sittable
    if (state.getBlock() instanceof StairBlock || state.getBlock() instanceof SlabBlock) {
        return true;
    }
    
    // Check for custom block behaviors registered as sittable
    return BlockBehaviors.get(state.getBlock()) instanceof SittableBlockBehavior;
}
```

### 2. Furniture Seat Extraction (Modified)

When a furniture piece is captured, extract its seats:

```java
/**
 * Extracts seats from a captured furniture piece.
 * Called after ContraptionFurniture is created in captureNear().
 */
public static List<ContraptionSeat> extractSeatsFromFurniture(
    ContraptionFurniture furniture,
    BlockPos bearingWorldPos
) {
    if (!furniture.hasLiveFurniture()) {
        return List.of();
    }
    
    BukkitFurniture live = furniture.liveFurniture();
    List<ContraptionSeat> seats = new ArrayList<>();
    
    // Iterate through furniture's seat configs
    // (CraftEngine API: furniture.config().seats() or similar)
    int seatIndex = 0;
    for (SeatConfig seatCfg : getSeatConfigs(live)) {
        Vec3 seatWorldPos = computeSeatWorldPosition(live, seatCfg);
        Vec3 localOffset = seatWorldPos.subtract(Vec3.atLowerCornerOf(bearingWorldPos));
        float yaw = seatCfg.yaw() + furniture.yawOffsetDegrees();
        
        var source = new ContraptionSeat.FurnitureSeatSource(furniture, seatIndex);
        seats.add(ContraptionSeat.empty(localOffset, yaw, source));
        seatIndex++;
    }
    
    return seats;
}
```

### 3. Unified Capture Result

```java
public record CaptureResult(
    List<ContraptionElement> elements  // Blocks, furniture, seats, entities all unified
) {
    // Helpers to filter by type
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

## State Management

`ContraptionState` stores all elements:

```java
public final class ContraptionState {
    private List<ContraptionElement> elements = List.of();
    
    // Deprecated - kept for migration
    @Deprecated
    private List<ContraptionFurniture> furniture = List.of();
    
    public List<ContraptionElement> elements() { return elements; }
    public void setElements(List<ContraptionElement> elements) { this.elements = elements; }
    
    // Convenience accessors
    public List<ContraptionSeat> seats() {
        return elements.stream()
            .filter(e -> e.type() == ElementType.SEAT)
            .map(e -> (ContraptionSeat) e)
            .toList();
    }
    
    public List<ContraptionFurniture> furniture() {
        return elements.stream()
            .filter(e -> e.type() == ElementType.FURNITURE)
            .map(e -> (ContraptionFurniture) e)
            .toList();
    }
}
```

## Rendering

Swarms adapt to render by element type:

```java
public class ContraptionElementSwarm {
    private final ContraptionFurnitureSwarm furnitureSwarm;
    private final ContraptionSeatSwarm seatSwarm;
    // etc.
    
    public void render(List<ContraptionElement> elements, Vec3 bearing, double yaw) {
        for (ContraptionElement e : elements) {
            switch (e.type()) {
                case FURNITURE -> furnitureSwarm.renderOne((ContraptionFurniture) e, bearing, yaw);
                case SEAT -> seatSwarm.renderOne((ContraptionSeat) e, bearing, yaw);
                // etc.
            }
        }
    }
}
```

## Migration Path

1. **Phase 1** (this design): Create interface + `ContraptionSeat` abstraction
2. **Phase 2**: Update `ContraptionFurniture` to implement interface
3. **Phase 3**: Modify capture to detect block seats + extract furniture seats
4. **Phase 4**: Unified storage in `ContraptionState.elements`
5. **Phase 5**: Adapt swarms to polymorphic rendering
6. **Phase 6**: Deprecate old `ContraptionState.furniture()` and seat-specific tracking

## Benefits

1. **Unified abstraction** - all element types implement same interface
2. **Polymorphic dispatch** - no instanceof checks, clean switch on type()
3. **Seat abstraction** - vanilla block seats and furniture seats treated identically
4. **Extensible** - new element types (e.g., ContraptionEntity for mobs) fit naturally
5. **Type safety** - sealed interfaces for seat sources ensure exhaustive handling
