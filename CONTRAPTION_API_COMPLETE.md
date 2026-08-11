# Complete Contraption API Plan

## Goal
Full public API for external plugins to create:
- Custom contraption types (minecart, ghast, airship, submarine, mech)
- Custom motors/power sources (electric, hydraulic, manual crank, wind, steam)
- Custom physics behaviors (buoyancy, aerodynamics, magnets)
- Custom movement behaviors (drills, saws, crushers, propellers)
- Event-driven contraption interactions

---

## Current State (What Exists)

### Core Interfaces ✅
- `PowerSource` — abstract power provider
- `PowerConsumer` — behaviors consume power
- `ContraptionTickable` — blocks tick inside contraptions
- `MultiblockMember` — multiblock structure detection
- `ContraptionHitboxProvider` — custom collision shapes

### Existing Events ✅
- `ContraptionAssembleEvent` — before assembly
- `ContraptionAssembledEvent` — after assembly
- `ContraptionDisassembleEvent` — before disassembly
- `ContraptionDisassembledEvent` — after disassembly
- `ContraptionSpawnEvent` — entity spawned
- `ContraptionMoveEvent` — contraption moved
- `ContraptionBlockBreakEvent` — block broken inside contraption
- `ContraptionBlockPlaceEvent` — block placed inside contraption
- `ContraptionInteractEvent` — player interacted with contraption

### Existing Systems ✅
- Physics engine (XPBD solver)
- Collision detection (WorldBlockCache, CollisionShape)
- Contraption types (LINEAR, ROTATIONAL, VEHICLE, MINECART, GHAST_HARNESS)
- Movement behaviors (MinerBehavior, MoverBehavior, PhysicsBehavior)
- Glue system (GlueRegistry, GlueGraph)
- Chainery (chains attach contraptions)

---

## Missing API Surface

### 1. Contraption Creation API

**Need**: External plugin creates custom contraption type

**Current**: Hardcoded types in `BearingType` enum
```java
public enum BearingType {
    LINEAR, ROTATIONAL, VEHICLE, MINECART, GHAST_HARNESS
}
```

**Target**: Registry-based
```java
// Plugin code:
ContraptionTypeRegistry.register("myplugin:submarine", SubmarineType.class);

public class SubmarineType implements ContraptionType {
    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new SubmarineEntity(level, state);
    }
    
    @Override
    public boolean canAssemble(Level level, BlockPos anchor) {
        // Check for water nearby
    }
}
```

**Files to create:**
- `contraption/api/ContraptionType.java`
- `contraption/api/ContraptionTypeRegistry.java`

---

### 2. Movement Behavior API

**Need**: Custom movement logic (propellers, wings, legs, wheels)

**Current**: `MovementBehaviorRegistry` exists but uses Key lookup
```java
public interface MovementBehavior {
    void tick(ContraptionEntity contraption, BlockPos localPos);
}

MovementBehaviorRegistry.register(Key.of("myplugin:propeller"), (state, localPos) -> 
    new PropellerBehavior(state, localPos));
```

**Already works!** Just needs documentation.

**Document:**
- How to register custom behaviors
- What methods are available on `ContraptionEntity`
- How to apply forces/torques

---

### 3. Physics Material API

**Need**: Custom physics properties (friction, restitution, buoyancy)

**Current**: Table-based (friction.yml, restitution.yml, floatability.yml)

**Target**: Block behavior interface
```java
public interface PhysicsMaterial {
    float getFriction();
    float getRestitution();
    float getBuoyancy();
    float getDensity();
}
```

**Files to create:**
- `contraption/api/PhysicsMaterial.java`
- Document how to implement on custom blocks

---

### 4. Contraption Entity API

**Need**: Access contraption state, apply forces, query blocks

**Current**: `ContraptionEntity` exists but no clear public API contract

**Expose:**
```java
public interface ContraptionAccess {
    // State
    Vec3 getPosition();
    Vec3 getVelocity();
    Vec3 getAngularVelocity();
    Quaternion getRotation();
    
    // Block access
    BlockState getBlock(BlockPos localPos);
    Set<BlockPos> getAllBlocks();
    BlockEntity getBlockEntity(BlockPos localPos);
    
    // Physics
    void applyForce(Vec3 force, Vec3 worldPoint);
    void applyTorque(Vec3 torque);
    float getMass();
    Vec3 getCenterOfMass();
    
    // Metadata
    ContraptionType getType();
    Level getRealLevel();
    ContraptionLevel getContraptionLevel();
}
```

**Files to create:**
- `contraption/api/ContraptionAccess.java`
- Make `ContraptionEntity implements ContraptionAccess`

---

### 5. Assembly Hook API

**Need**: Custom assembly rules (require certain blocks, validate structure)

**Current**: Assembly is hardcoded in `ContraptionAssembler`

**Target**:
```java
public interface AssemblyValidator {
    boolean canAssemble(Level level, Set<BlockPos> blocks);
    String getFailureReason();
}

ContraptionTypeRegistry.register("myplugin:airship", new ContraptionType() {
    @Override
    public AssemblyValidator getValidator() {
        return (level, blocks) -> {
            // Must have balloon blocks above
            return blocks.stream().anyMatch(pos -> isBallon(level, pos));
        };
    }
});
```

**Files to create:**
- `contraption/api/AssemblyValidator.java`
- Hook into `ContraptionAssembler`

---

### 6. Power Network API

**Already done!** `PowerSource` and `PowerConsumer` interfaces exist.

**Just needs:**
- Documentation on how to implement
- Example: electric motor, steam engine, manual crank

---

### 7. Contraption Interaction API

**Need**: Handle player/entity interactions with contraption blocks

**Current**: `ContraptionInteractEvent` exists

**Target**: Make it easier to use
```java
@EventHandler
public void onContraptionInteract(ContraptionInteractEvent event) {
    BlockPos local = event.getLocalPos();
    Player player = event.getPlayer();
    ContraptionEntity contraption = event.getContraption();
    
    if (isLever(contraption.getBlock(local))) {
        toggleEngine(contraption);
    }
}
```

**Already works!** Just document it.

---

### 8. Contraption Rendering API

**Need**: Custom block rendering inside contraptions (animated, scaled)

**Current**: Display entity swarms (ContraptionDisplaySwarm, ContraptionEntityMirrorSwarm)

**Target**: Hook for custom renderers
```java
public interface ContraptionRenderer {
    void render(ContraptionEntity contraption, PoseStack pose, MultiBufferSource buffer, int light);
}
```

**Files to create:**
- `contraption/api/ContraptionRenderer.java`
- Register renderers per block type

---

### 9. Contraption Persistence API

**Need**: Save/load custom contraption data

**Current**: NBT-based (ContraptionStructureNbt)

**Target**: Hook for custom data
```java
public interface ContraptionData {
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
}

// On ContraptionType:
ContraptionData createData();
```

**Files to create:**
- `contraption/api/ContraptionData.java`
- Hook into save/load pipeline

---

### 10. Collision/Hitbox API

**Already done!** `ContraptionHitboxProvider` exists.

**Just needs:**
- Integration into collision system
- Documentation

---

## Implementation Priority

### Phase 1: Core API (This Session) ✅
- [x] PowerSource
- [x] PowerConsumer
- [x] ContraptionTickable
- [x] MultiblockMember
- [x] ContraptionHitboxProvider
- [x] Refactor contraption core to use interfaces
- [x] Delete unused TickableBehavior

### Phase 2: Creation & Access (Next)
- [ ] ContraptionType interface
- [ ] ContraptionTypeRegistry
- [ ] ContraptionAccess interface (expose ContraptionEntity methods)
- [ ] PhysicsMaterial interface
- [ ] AssemblyValidator interface

### Phase 3: Documentation
- [ ] API documentation (javadoc)
- [ ] Example: Custom contraption type (submarine)
- [ ] Example: Custom motor (electric)
- [ ] Example: Custom movement behavior (propeller)
- [ ] Event handler examples

### Phase 4: Advanced
- [ ] ContraptionRenderer interface
- [ ] ContraptionData persistence hooks
- [ ] Hitbox provider integration
- [ ] Physics material system refactor

---

## Example: Custom Submarine Contraption

```java
// 1. Define contraption type
public class SubmarineType implements ContraptionType {
    @Override
    public String getId() {
        return "myplugin:submarine";
    }
    
    @Override
    public ContraptionEntity createEntity(Level level, ContraptionState state) {
        return new SubmarineEntity(level, state);
    }
    
    @Override
    public AssemblyValidator getValidator() {
        return new SubmarineValidator();
    }
}

// 2. Validator ensures structure is waterproof
public class SubmarineValidator implements AssemblyValidator {
    @Override
    public boolean canAssemble(Level level, Set<BlockPos> blocks) {
        // Check all faces are solid (no leaks)
        for (BlockPos pos : blocks) {
            for (Direction dir : Direction.values()) {
                BlockPos adj = pos.relative(dir);
                if (!blocks.contains(adj) && level.getFluidState(adj).isEmpty()) {
                    return false; // hole to air = leak
                }
            }
        }
        return true;
    }
}

// 3. Entity handles buoyancy physics
public class SubmarineEntity extends ContraptionEntity {
    @Override
    public void tick() {
        super.tick();
        
        // Apply buoyancy force
        Vec3 buoyancy = calculateBuoyancy();
        applyForce(buoyancy, getCenterOfMass());
        
        // Propeller thrust (from powered blocks)
        for (BlockPos local : getPoweredBlocks()) {
            PowerConsumer consumer = getBlockEntity(local);
            float power = consumer.getPower();
            Vec3 thrust = getForwardVector().scale(power * 0.1);
            applyForce(thrust, toWorld(local));
        }
    }
    
    private Vec3 calculateBuoyancy() {
        int submergedBlocks = countSubmergedBlocks();
        float buoyancyForce = submergedBlocks * 9.8f * 1000f; // water density
        return new Vec3(0, buoyancyForce - getMass() * 9.8f, 0);
    }
}

// 4. Register
ContraptionTypeRegistry.register(new SubmarineType());

// 5. Bearing block config
blocks:
  myplugin:submarine_bearing:
    behavior:
      type: polyfills:bearing_block
      contraption_type: myplugin:submarine  # <-- custom type
```

---

## Documentation TODOs

### API Reference
- [ ] Interface overview (what each does)
- [ ] Method signatures and contracts
- [ ] Event lifecycle diagrams
- [ ] Registry usage patterns

### Tutorials
- [ ] "Your First Custom Contraption"
- [ ] "Adding a Custom Motor"
- [ ] "Custom Movement Behaviors"
- [ ] "Physics Materials and Collision"

### Examples
- [ ] Submarine (buoyancy)
- [ ] Airship (balloon physics)
- [ ] Mech (leg IK, walking)
- [ ] Electric motor
- [ ] Wind turbine (rotational power from environment)

---

## Next Steps

1. **Create ContraptionType interface and registry**
2. **Expose ContraptionAccess on ContraptionEntity**
3. **Create PhysicsMaterial interface**
4. **Write API documentation**
5. **Build example: submarine contraption**

Ready to continue?
