# Motor System Migration Audit

## Changes Summary (HEAD~5 → HEAD)

### Deleted Files
- `GasMotorMk1Behavior.java` → `DataMotorBehavior.java` (renamed/refactored)
- `GasMotorMk1BlockEntity.java` → `DataMotorBlockEntity.java` (renamed/refactored)
- `GasMotorBreakListener.java` → `DataMotorBreakListener.java` (renamed)
- `src/main/resources/machines/gas_motor_mk1.json` → DELETED (moved to motors/)

### New Files
- `MotorDefinition.java` — motor registry and data model
- `MotorLoader.java` — loads motors/*.json
- `src/main/resources/motors/gas_motor_mk1.json` — motor definition

## Critical Implementation Details

### 1. Data Flow
```
Block Config (gas_motor_mk1.yml)
  └─ behavior.type: polyfills:data_motor
  └─ behavior.motor: polyfills:gas_motor_mk1
       ↓
DataMotorBehavior.Factory
  └─ resolves MotorDefinition.byName("polyfills:gas_motor_mk1")
  └─ merges block YAML config (gases, vapor-capacity, upgrades)
  └─ creates DataMotorBlockEntity(definition, ...)
       ↓
DataMotorBlockEntity
  └─ implements RpmProvider interface
  └─ uses definition.machine() for menu/UI
  └─ uses definition.fuels() for gas→rpm/su mapping
```

### 2. Registry Loading Order
```java
// CraftEnginePolyfills.initPlugin():
254: FluidTypeLoader.bootstrap()       // PHASE_TYPES
255: GasTypeLoader.bootstrap()         // PHASE_TYPES  
265: MotorLoader.bootstrap()           // PHASE_DEFINITIONS (after gases)
266: MotorLoader.load()                // immediate load
267: MachineDefinitionLoader.bootstrap()
```

**CRITICAL**: MotorLoader runs in PHASE_DEFINITIONS, **after** GasType registry populated. Fuels reference `polyfills:steam` which must exist first.

### 3. Behavior Factory Registration
```java
// BlockBehaviors.register():
124: RegistryUtils.registerBlockBehavior(
       DataMotorBehavior.FACTORY_KEY,  // polyfills:data_motor
       DataMotorBehavior.FACTORY);
```

### 4. RpmProvider Interface
DataMotorBlockEntity implements:
- `float getRpm()` — current delivered rpm (0 if overstressed)
- `float potentialRpm()` — rpm ignoring stress (for network sizing)
- `void reportStressLoad(float su)` — consumer reports its demand
- `boolean rpmReaches(BlockPos)` — reachability check
- `boolean isRpmSource()` — identification

### 5. Machine Integration
DataMachineBlockEntity pulls rotational power:
```java
// pullRotationalPower(Level):
for each adjacent Direction:
  if BlockEntity.controller instanceof RpmProvider p:
    if p.isRpmSource() && p.rpmReaches(machinePos):
      if p.potentialRpm() > bestPotential:
        activeMotor = p
        inputRpm = p.getRpm()
```

**CRITICAL**: Uses `potentialRpm()` for selection (not `getRpm()`), so stalled motor stays selected.

## Config Migration

### Old (machines/gas_motor_mk1.json)
```json
{
  "id": "polyfills:gas_motor_mk1",
  "menu_size": 54,
  "slots": { "upgrade": {"count": 9, "base_unlocked": 3} },
  "gas_tanks": [{"name": "vapor", "capacity": 10000}],
  "bars": [...],
  "buttons": [...]
}
```

### New (motors/gas_motor_mk1.json)
```json
{
  "id": "polyfills:gas_motor_mk1",
  "output_faces": ["front"],
  "upgrade_slots": 9,
  "base_unlocked": 3,
  "base_overclock": 2.0,
  "fuels": {
    "gas": {
      "polyfills:steam":       {"rpm": 32, "su": 512,  "per_tick": 20},
      "polyfills:heavy_steam": {"rpm": 64, "su": 1024, "per_tick": 20}
    }
  },
  "machine": { /* same body as old gas_motor_mk1.json */ }
}
```

**KEY CHANGE**: Fuel table moved from block config YAML to motor JSON.

## Block Config (gas_motor_mk1.yml)
```yaml
behavior:
  type: polyfills:data_motor
  motor: polyfills:gas_motor_mk1  # references motors/ registry
  horizontal-direction-property: facing
  vapor-capacity: 10000  # optional override
  gases:  # optional override/extension of motor's fuel table
    steam:       {rpm: 32, su: 64, gas-per-tick: 10}
    heavy_steam: {rpm: 48, su: 96, gas-per-tick: 15}
  upgrades:  # attribute modifiers
    cml:iron_cast:
      - {attribute: polyfill:extra_slots, operation: add, value: 2}
```

## Validation Checklist

- [x] MotorDefinition.java compiles
- [x] MotorLoader.java compiles
- [x] DataMotorBehavior.java compiles
- [x] DataMotorBlockEntity.java compiles
- [x] motors/gas_motor_mk1.json valid schema
- [x] BlockBehaviors registers polyfills:data_motor
- [x] CraftEnginePolyfills calls MotorLoader.bootstrap() + load()
- [x] gas_motor_mk1.yml references correct behavior type
- [x] RpmProvider interface methods implemented
- [x] DataMachineBlockEntity pulls from RpmProvider
- [ ] **IN-GAME TEST REQUIRED**: Place motor, verify menu opens
- [ ] **IN-GAME TEST REQUIRED**: Add gas, verify RPM output
- [ ] **IN-GAME TEST REQUIRED**: Connect crusher, verify power delivery
- [ ] **IN-GAME TEST REQUIRED**: Verify upgrade slots unlock
- [ ] **IN-GAME TEST REQUIRED**: Verify overclock buttons work

## Potential Issues

### 1. Motor Definition Not Found
**Symptom**: Motor block places but menu doesn't open, or crashes on open.
**Cause**: `MotorDefinition.byName("polyfills:gas_motor_mk1")` returns null.
**Debug**: Check server logs for "Loaded N motor definitions", verify motors/*.json in pack.

### 2. Gas Not Recognized
**Symptom**: Motor has gas but produces 0 RPM.
**Cause**: Fuel table references gas that doesn't exist in GasType.REGISTRY.
**Debug**: Check GasType.byName("polyfills:steam") != null.

### 3. Machine Not Receiving Power
**Symptom**: Crusher adjacent to running motor but doesn't process.
**Cause**: 
  - Motor's `output_faces` doesn't include direction toward machine
  - Machine's `power.consumes_stress` not true
  - Motor overstressed (SU load > motor SU capacity)
**Debug**: Check motor UI for "RPM" and "SU" values, check machine logs.

### 4. Upgrade Slots Locked
**Symptom**: Can't place items in upgrade slots 4-9.
**Cause**: No EXTRA_SLOTS upgrade in slots 0-3.
**Debug**: Verify upgrade attribute definitions in block config match items.

## Regression Risk: MEDIUM-HIGH

**Why**: Deep refactor touching:
- Rotational power delivery (RpmProvider interface)
- Registry loading order (MotorLoader in PHASE_DEFINITIONS)
- Config schema split (motor JSON vs block YAML)
- Machine power consumption (DataMachineBlockEntity.pullRotationalPower)

**Mitigation**: 
1. Build succeeded ✓
2. Tests passed ✓
3. **Requires full in-game validation** before merge

## Next Steps
1. Deploy to testserver
2. Check logs for "Loaded N motor definitions" (expect 1)
3. Place gas_motor_mk1, verify menu opens
4. Add steam, verify RPM gauge shows 32
5. Place crusher adjacent, verify power delivery
6. Test upgrade system
7. Test overclock controls
