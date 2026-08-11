# Contraption API Extraction Plan

## Goal
Separate contraption/physics logic from machines/fluid/gas/rotation systems so users can use craft-engine-polyfills as pure contraption+physics API without needing machine implementations.

## Current Dependencies Analysis

### Hard Dependencies Found

#### 1. **Bearing → Machine System**
**Files**: `BearingBlockBehavior.java`, `PistonBearingBlockEntity.java`

**Imports**:
- `machine.attribute.MachineAttributes` (upgrades)
- `machine.menu.*` (UI)
- `machine.recipe.*` (processing)
- `machine.block.entity.AbstractMachineBlockEntity` (base class)

**Usage**: Bearing has upgrade slots, menu pages, processes "recipes" (retraction speed modifiers)

**Problem**: Bearing IS a machine. Menu + upgrades deeply coupled.

#### 2. **Miner/Mover → Rotation System**
**Files**: `MinerBlockBehavior.java`, `MoverBlockBehavior.java`, `MinerBehavior.java`, `MoverBehavior.java`

**Imports**:
- `rotation.RpmConsumer`
- `rotation.RpmProvider`

**Usage**: Miner drills faster with more RPM. Mover propels contraption based on RPM.

**Problem**: Rotational power is core mechanic for these behaviors.

#### 3. **ContraptionLevel → Machine Tick**
**Files**: `AspContraptionLevel.java`, `BukkitContraptionLevel.java`

**Imports**:
- `machine.block.entity.AbstractMachineBlockEntity`

**Usage**: Ticks captured machines inside contraption (crushers, furnaces keep processing while moving).

**Problem**: Contraption needs to know how to tick machines.

#### 4. **MultiblockMembershipRegistry → FluidBlockTank**
**File**: `MultiblockMembershipRegistry.java`

**Import**:
- `fluid.behavior.FluidBlockTankBehavior`

**Usage**: Tracks multiblock structures, special-cases fluid tanks.

**Problem**: Hardcoded knowledge of specific block type.

#### 5. **RotationalBearingBehavior → Rotation**
**File**: `RotationalBearingBehavior.java`

**Imports**:
- `rotation.RpmConsumer`
- `rotation.RpmProvider`

**Usage**: Rotational bearing receives RPM from motor, drives contraption rotation.

---

## Extraction Strategy

### Phase 1: Interface Abstraction Layer

Create generic interfaces that contraption code depends on, implemented by machine/rotation systems.

#### New Interfaces (in `contraption.api` package):

```java
// contraption/api/PowerSource.java
public interface PowerSource {
    float getPower();           // generic "power" instead of RPM
    float getPotentialPower();  // for network sizing
    boolean isPowerSource();
    boolean powerReaches(BlockPos target);
}

// contraption/api/PowerConsumer.java  
public interface PowerConsumer {
    void setPower(float power);
    float getPower();
}

// contraption/api/UpgradeContainer.java
public interface UpgradeContainer {
    int getUpgradeSlotCount();
    ItemStack getUpgradeItem(int slot);
    Map<String, Double> getModifiers(); // attribute -> value
}

// contraption/api/MenuProvider.java (already exists in Bukkit, extend it)
public interface ContraptionMenuProvider {
    void openMenu(Player player);
    void closeMenu(Player player);
}

// contraption/api/TickableBehavior.java
public interface TickableBehavior {
    void tick(Level level, BlockPos pos);
}
```

#### Adapters (in machine/rotation packages):

```java
// rotation/RpmPowerSourceAdapter.java
public class RpmPowerSourceAdapter implements PowerSource {
    private final RpmProvider provider;
    
    @Override
    public float getPower() { return provider.getRpm(); }
    @Override
    public float getPotentialPower() { return provider.potentialRpm(); }
    // ...
}

// machine/MachinePowerConsumerAdapter.java
public class MachinePowerConsumerAdapter implements PowerConsumer {
    private final RpmConsumer consumer;
    
    @Override
    public void setPower(float power) { consumer.setInputRpm(power); }
    // ...
}
```

### Phase 2: Decouple Specific Implementations

#### A. Bearing System

**Current**: `PistonBearingBlockEntity extends AbstractMachineBlockEntity`

**Target**: `PistonBearingBlockEntity extends AbstractBearingBlockEntity`

**Changes**:
1. Extract bearing-specific logic from machine base class
2. Create `AbstractBearingBlockEntity` with:
   - Upgrade system (generic, not MachineAttributes)
   - Menu system (generic, not MachineMenu)
   - Config-driven (bearing.json, not hardcoded)
3. Machine system provides **adapter** that wraps bearing as machine

**File Structure**:
```
contraption/behavior/bearing/
  AbstractBearingBlockEntity.java    // base bearing logic
  BearingUpgradeSystem.java          // generic upgrades
  BearingMenuProvider.java           // generic menu

machine/adapter/
  BearingMachineAdapter.java         // wraps bearing as machine
```

#### B. Miner/Mover Power

**Current**: Direct `RpmConsumer` interface

**Target**: `PowerConsumer` interface

**Changes**:
1. Miner/Mover depend on `PowerConsumer` (abstract)
2. Rotation system provides `RpmPowerConsumerAdapter`
3. Other power systems (electric, steam pressure, etc.) can implement `PowerConsumer`

**Benefits**: 
- Electric motor → miner (future)
- Manual crank → miner (future)
- Pure kinetic contraptions (no machines needed)

#### C. Machine Ticking in Contraption

**Current**: `if (be instanceof AbstractMachineBlockEntity) { machine.tick(); }`

**Target**: `if (be instanceof TickableBehavior) { behavior.tick(); }`

**Changes**:
1. ContraptionLevel checks for `TickableBehavior` interface
2. Machines implement `TickableBehavior` (already effectively do)
3. Other mods can add tickable behaviors without machine dependency

#### D. MultiblockMembershipRegistry

**Current**: Hardcoded `FluidBlockTankBehavior` check

**Target**: Generic multiblock detection

**Changes**:
1. Registry checks for `MultiblockMember` interface
2. FluidBlockTankBehavior implements `MultiblockMember`
3. Other multiblocks (machine cores, refineries) implement same interface

---

## Module Structure (Post-Extraction)

### Option A: Single JAR with Optional Dependencies

```
craft-engine-polyfills.jar
├─ contraption/ (core, no deps)
├─ physics/ (core, no deps)
├─ machine/ (optional, depends on contraption)
├─ rotation/ (optional, depends on contraption)
├─ fluid/ (optional)
└─ gas/ (optional)
```

**Classpath Isolation**: Use separate classloaders or service-provider pattern.

### Option B: Split JARs

```
craft-engine-contraptions.jar    // core physics + contraption
craft-engine-machines.jar        // machines + adapters
craft-engine-rotation.jar        // motors + adapters
craft-engine-fluids.jar          // fluid/gas systems
```

**Dependencies**:
- `machines.jar` → depends on `contraptions.jar`
- `rotation.jar` → depends on `contraptions.jar`
- `contraptions.jar` → standalone

**User Scenarios**:
- Want contraptions only: Load `craft-engine-contraptions.jar`
- Want full system: Load all JARs

---

## API Package Structure

```
dev.arubik.craftengine/
├─ contraption/
│  ├─ api/                    // Public API interfaces
│  │  ├─ PowerSource.java
│  │  ├─ PowerConsumer.java
│  │  ├─ UpgradeContainer.java
│  │  ├─ TickableBehavior.java
│  │  └─ MultiblockMember.java
│  ├─ core/                   // Core contraption logic (no external deps)
│  │  ├─ ContraptionEntity.java
│  │  ├─ ContraptionState.java
│  │  ├─ ContraptionAssembler.java
│  │  └─ ContraptionEngine.java
│  ├─ physics/                // Physics engine (XPBD solver)
│  │  ├─ PhysicsWorld.java
│  │  ├─ CollisionShape.java
│  │  └─ XpbdSolver.java
│  ├─ behavior/               // Movement behaviors
│  │  ├─ bearing/
│  │  │  ├─ AbstractBearingBlockEntity.java
│  │  │  └─ BearingBehavior.java
│  │  ├─ miner/
│  │  │  └─ MinerBehavior.java
│  │  └─ mover/
│  │     └─ MoverBehavior.java
│  └─ level/                  // Contraption world management
│     ├─ ContraptionLevel.java
│     └─ BukkitContraptionLevel.java
│
├─ machine/                   // Machine system (depends on contraption)
│  ├─ adapter/
│  │  ├─ MachinePowerConsumerAdapter.java
│  │  └─ BearingMachineAdapter.java
│  └─ block/
│     └─ entity/
│        └─ AbstractMachineBlockEntity.java (implements TickableBehavior)
│
└─ rotation/                  // Rotation system (depends on contraption)
   ├─ adapter/
   │  ├─ RpmPowerSourceAdapter.java
   │  └─ RpmPowerConsumerAdapter.java
   ├─ RpmProvider.java (extends PowerSource)
   └─ RpmConsumer.java (extends PowerConsumer)
```

---

## Migration Path

### Step 1: Create Interface Layer (Non-Breaking)
- Add `contraption.api` package
- Define all interfaces
- **Don't change existing code yet**

### Step 2: Implement Interfaces (Non-Breaking)
- `RpmProvider implements PowerSource`
- `RpmConsumer implements PowerConsumer`
- `AbstractMachineBlockEntity implements TickableBehavior`
- Existing code still works via concrete types

### Step 3: Refactor Contraption Core (Breaking, Controlled)
- Change contraption code to use interfaces
- Test with existing machine/rotation implementations
- Fix any issues

### Step 4: Extract Bearing (Major)
- Create `AbstractBearingBlockEntity`
- Move bearing logic out of machine system
- Create `BearingMachineAdapter` for backward compat
- Migrate bearing configs

### Step 5: Document API (Final)
- Javadoc all public interfaces
- Write integration guide
- Example: "How to add electric motor support"

---

## Benefits

### For Users
- Use contraptions without machines (lighter plugin)
- Add custom power sources (electric, hydraulic, manual)
- Add custom contraption behaviors
- Cleaner dependencies

### For Developers
- Separation of concerns
- Easier testing (mock power sources)
- Future-proof (new power systems plug in)
- Modular development

---

## Risks

### High Risk
- **Bearing extraction**: Deeply coupled to machine system
- **Save compatibility**: Existing bearings in worlds
- **API stability**: Once published, hard to change

### Medium Risk
- **Performance**: Adapter layer overhead
- **Complexity**: More interfaces to maintain

### Low Risk
- **Miner/Mover refactor**: Already loosely coupled
- **TickableBehavior**: Simple interface addition

---

## Effort Estimate

| Task | Files | Lines | Risk | Time |
|------|-------|-------|------|------|
| Interface layer | 5 new | ~200 | Low | 2h |
| Implement interfaces | 10 files | ~100 | Low | 2h |
| Refactor contraption core | 20 files | ~500 | Medium | 8h |
| Extract bearing system | 15 files | ~1000 | High | 16h |
| Testing + fixes | - | - | High | 8h |
| Documentation | - | - | Low | 4h |
| **Total** | **50+** | **~1800** | - | **40h** |

---

## Next Steps

1. **Prototype interface layer** (contraption/api/)
2. **Test adapters** with one existing system (rotation)
3. **Validate approach** before full refactor
4. **Plan breaking change window** (major version bump)

---

## Questions for User

1. **Module split**: Single JAR or split JARs?
2. **Breaking changes**: Acceptable now or wait for next major version?
3. **API scope**: Just contraptions or include physics directly?
4. **Priority**: Bearing extraction most important or miner/mover first?
5. **Backward compat**: Keep old classes deprecated or hard break?
