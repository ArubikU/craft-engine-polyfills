# Redstone Add-On 4.0 - Implementation Status

Source: `pb_red` namespace (Bedrock Edition). CE = CraftEngine Polyfills.

Legend: ✅ = 100% (model + block config + machine JSON + script) · 🔧 = Partial · ❌ = Not Implemented

---

## ✅ 100% Implemented (model + config + script)

| Machine | Model | Machine JSON | Script | Block YML |
|---|---|---|---|---|
| `auto_composter` | `redstone/auto_composter` | `auto_composter.json` | `auto_composter.pf` | `redstone_machines.yml`† |
| `block_breaker` | *(uses CE custom model)* | `block_breaker.json` | `block_breaker.pf` | `machines.yml` |
| `block_dispenser` | `redstone/block_dispenser` | `block_dispenser.json` | `block_dispenser.pf` | `machines.yml` |
| `block_placer` | *(same as dispenser)* | `block_placer.json` | `block_placer.pf` | `machines.yml` |
| `block_rotater` | `redstone/block_rotater` | `block_rotater.json` | `block_rotater.pf` | `machines.yml` |
| `block_sensor` | `redstone/block_sensor` | `block_sensor.json` | `block_sensor.pf` | `machines.yml` |
| `breeder` | `redstone/breeder` | `breeder.json` | `breeder.pf` | `machines.yml` |
| `crusher` | *(BetterModel: crusher.bbmodel)* | `crusher.json` | `crusher.pf` | `crusher.yml` |
| `depot` | *(CE custom model)* | `depot.json` | `depot.pf` + `depot_tick.pf` | `depot.yml` |
| `entity_sensor` | `redstone/entity_sensor` | `entity_sensor.json` | `entity_sensor.pf` | `machines.yml` |
| `fertilizer` | `redstone/fertilizer` | `fertilizer.json` | `fertilizer.pf` | `machines.yml` |
| `freezer` | *(CE custom)* | `freezer.json` | `freezer.pf` | `machines.yml` |
| `harvester` | *(CE custom)* | `harvester.json` | `harvester.pf` | `machines.yml` |
| `hopper_dropper` | `redstone/hopper_dropper` | `hopper_dropper.json` | *(none - CE IO)* | `machines.yml` |
| `item_magnet` | `redstone/item_magnet` | `item_magnet.json` | `item_magnet.pf` | `machines.yml` |
| `item_trash` | *(CE custom)* | `item_trash.json` | `item_trash.pf` | `machines.yml` |
| `jump_pad` | `redstone/jump_pad_block` | `jump_pad.json` | `jump_pad.pf` | `machines.yml` |
| `lava_spike` | `redstone/spike_lava` | `lava_spike.json` | `lava_spike.pf` | `machines.yml` |
| `rain_sensor` | `redstone/rain_sensor` | `rain_sensor.json` | `rain_sensor.pf` | `machines.yml` |
| `redstone_counter` | `redstone/redstone_counter` | `redstone_counter.json` | `redstone_counter.pf` | `machines.yml` |
| `redstone_latch` | `redstone/redstone_latch` | `redstone_latch.json` | `redstone_latch.pf` | `machines.yml` |
| `redstone_looper` | `redstone/redstone_looper` | `redstone_looper.json` | `redstone_looper.pf` | `machines.yml` |
| `redstone_randomizer` | `redstone/redstone_randomizer` | `redstone_randomizer.json` | `redstone_randomizer.pf` | `machines.yml` |
| `shaft` | `cml:block/shaft` | `shaft.json` | `shaft.pf` | `shaft.yml` |
| `gearbox_h` | `cml:block/gearbox_h_stubs` | `gearbox_h.json` | `gearbox_h.pf` | `gearbox.yml` |
| `gearbox_v` | `cml:block/gearbox_v_stubs` | `gearbox_v.json` | `gearbox_v.pf` | `gearbox.yml` |
| `smeltery` | *(CE custom)* | `smeltery.json` | *(recipe machine)* | `smeltery.yml` |
| `speed_sensor` | *(CE custom)* | `speed_sensor.json` | `speed_sensor.pf` | `machines.yml` |
| `spike` | `redstone/spike` | `spike.json` | `spike.pf` | `machines.yml` |
| `upgradeable_furnace` | *(CE custom)* | `upgradeable_furnace.json` | *(recipe)* | `upgradeable_furnace.yml` |
| `vapor_furnace_mk1` | *(CE custom)* | `vapor_furnace_mk1.json` | *(recipe)* | `vapor_furnace_mk1.yml` |
| `xp_collector` | `redstone/xp_tank` | `xp_collector.json` | `xp_collector.pf` | `machines.yml` |
| `copper_fan` | `cml:block/misc/copper_fan` | *(FanBlockBehavior Java)* | *(Java)* | `machines.yml` |

†Some older machines have block configs in the monolithic `machines.yml` rather than individual polyfills YMLs.

---

## 🔧 Partial (config exists, model converted, needs testing/fixes)

| Machine | Model | JSON | Script | Missing |
|---|---|---|---|---|
| `drill` | `redstone/drill` ✅ | `drill.json` ✅ | `drill.pf` ✅ | Block YML in `redstone_machines.yml`. Not tested in-game. |
| `saw` | `redstone/saw` ✅ | `saw.json` ✅ | `saw.pf` ✅ | Block YML in `redstone_machines.yml`. Log-chain logic untested. |
| `mob_generator` | `redstone/mob_generator` ✅ | `mob_generator.json` ✅ | `mob_generator.pf` ✅ | Block YML in `redstone_machines.yml`. spawn_entity needs testing. |
| `arithmetic_gate` | `redstone/arithmetic_gate` ✅ | `arithmetic_gate.json` ✅ | `arithmetic_gate.pf` ✅ | Block YML ✅. Needs interact script for mode cycling. |
| `logic_gate` | `redstone/redstone_logic_gates` ✅ | `logic_gate.json` ✅ | `logic_gate.pf` ✅ | Block YML ✅. 6 modes need testing. |
| `signal_inverter` | `redstone/custom_repeater` ✅ | `signal_inverter.json` ✅ | `signal_inverter.pf` ✅ | Block YML ✅. Simple NOT gate. |
| `signal_modifier` | `redstone/signal_modifier` ✅ | `signal_modifier.json` ✅ | `signal_modifier.pf` + `signal_modifier_interact.pf` ✅ | Block YML ✅. |
| `pulse_trigger` | `redstone/pulse_trigger` ✅ | `pulse_trigger.json` ✅ | `pulse_trigger.pf` ✅ | Block YML ✅. Edge detection modes. |
| `pulser` | `redstone/pulser` ✅ | `pulser.json` ✅ | `pulser.pf` ✅ | Block YML ✅. Timed pulse countdown. |
| `eye_sensor` | `redstone/entity_sensor` ✅ | `eye_sensor.json` ✅ | `eye_sensor.pf` ✅ | Block YML ✅. Uses `is_player_looking_at()`. |
| `light_sensor` | `redstone/rain_sensor`* ✅ | `light_sensor.json` ✅ | `light_sensor.pf` ✅ | Needs `Machine.block_light()` Java primitive. |
| `bucket_refiller` | `redstone/bucket_reffiler` ✅ | `bucket_refiller.json` ✅ | `bucket_refiller.pf` ✅ | Block YML ✅. Needs `consume_front_block` testing. |
| `item_filter` | `redstone/item_filter_left` ✅ | `item_filter.json` ✅ | `item_filter.pf` ✅ | Block YML missing. Needs per-slot filter logic testing. |
| `redstone_alarm` | `redstone/redstone_alarm` ✅ | `redstone_alarm.json` ✅ | `redstone_alarm.pf` ✅ | Block YML missing. Needs `block.play_sound()` Java method. |


*light_sensor uses rain_sensor model as placeholder

---

## ❌ Not Implemented - Needs New Systems

### `wireless_redstone` - Wireless Signal Transmitter/Receiver
**Requires:** GlobalBlockNetwork (world-level channel registry)
**Blocks:** 1 block, 2 modes (transmitter/receiver)
**States:** `is_transmitter` (bool), color channel (0-15), `powered`
**Logic:** Transmitter reads input power → broadcasts to GlobalBlockNetwork on channel. Receiver listens → outputs received power level.
**Assets available:** Model `redstone/wireless_redstone` ✅, Texture `wireless_redstone_*.png` ✅

### `entity_teleporter` - Entity Teleporter
**Requires:** GlobalBlockNetwork (channel-keyed position registry)
**Blocks:** 1 block
**States:** color channel (0-15), `powered`
**Logic:** Entities within 1.5 radius get teleported to random paired receiver on same channel. 10-tick cooldown tag.
**Assets available:** Model `redstone/entity_teleporter` ✅ (34 elements), Texture `entity_teleporter.png` ✅

### `item_teleporter` - Item Teleporter
**Requires:** GlobalBlockNetwork (same as entity_teleporter)
**Blocks:** 1 block
**States:** `tp_in` (transmit/receive), color channel (0-15)
**Logic:** `tp_in=true`: items touching → teleport to random receiver on channel. `tp_in=false`: receives.
**Assets available:** Model `redstone/item_teleporter` ✅ (36 elements), Texture `item_teleporter.png` ✅

### `super_piston` - Multi-Block Piston
**Requires:** NMS piston event chain (custom multi-block push/pull)
**Blocks:** 4 parts (normal body, arm, head, sticky variant)
**States:** `facing_direction` (6-way), `powered`, arm extension (0-3)
**Logic:** Pushes up to 12 blocks in facing direction. Sticky variant pulls. Animated arm extension over 4 ticks.
**Assets available:** Models `redstone/super_piston_normal` (2 elem), `super_piston_arm` (1), `super_piston_body` (2), `super_piston_head` (3) ✅

### `elevator` - Elevator System (5 block types)
**Requires:** Custom minecart vertical rail physics + floor detection + control signals
**Blocks:**
1. `elevator_rail` - vertical rail segment (placed on wall)
2. `elevator_floor` - floor marker (detects arrival)
3. `elevator_control_rail_up` - sends cart upward
4. `elevator_control_rail_down` - sends cart downward
5. `elevator_control_rail_stop` - stops cart at this floor
**States per block:** `facing_direction`, `powered`
**Logic:** Minecart rides vertical rails. Floor blocks detect cart presence → output redstone. Control rails apply vertical velocity (+0.5 up / -0.5 down / 0 stop). Needs custom minecart physics override.
**Assets available:** Models `redstone/elevator_rail` (4 elem), `redstone/elevator_floor` (1 elem) ✅. Textures `elevator.png`, `elevator_rail.png`, `elevator_floor.png`, `elevator_control_rail_*.png` ✅

### `friction_booster` - Surface Friction Modifier
**Requires:** NMS entity friction attribute (`Entity.setFriction` or slowness)
**Blocks:** 1 block
**States:** `friction_level` (0-15)
**Logic:** Reads max redstone power from back → stores as friction_level. Entities on top get modified speed multiplier.
**Assets available:** Textures `friction_booster_0.png` through `friction_booster_4.png` ✅

### `quarry_machine` - Automated Quarry Vehicle
**Requires:** Contraption rail-laying behavior + drill integration
**Entities:** 5-entity compound (quarry_machine, rail_placer_furnace, rail_placer_seat, portable_block_dispenser, portable_drill)
**Logic:** Rides minecart. Places rails ahead from inventory. Coal powers furnace for locomotion. Drill entity breaks ore in front. 6 placement modes.
**Assets available:** Model `redstone/flying_machine` ✅ (generic vehicle shell)

### `transplanter` - Seed Planter Vehicle
**Requires:** Contraption script-driven machine + farmland detection
**Entities:** 1 rider entity on minecart
**Logic:** When moving ≥0.01 speed, plants seeds in 3×2 strip perpendicular to motion. Scans for farmland below.
**Assets available:** None (uses quarry_machine shell)

### `redstone_ore_detector` - Ore Finder
**Requires:** Block scanning + particle effect system
**Blocks:** 1 block
**States:** `ore_index` (0-11 ore types), `particle` (bool)
**Logic:** Scans 10-block cube. Shows colored directional particles. Distance determines color: <2.5m=green, 2.5-3.5=yellow, 3.5-6=orange, >6=red.
**Assets available:** Model `redstone/redstone_ore_detector` ✅ (29 elements)

### `redstone_display` - Visual Display Block
**Requires:** Custom text/pixel rendering on block face
**Blocks:** 1 block
**Logic:** Shows configurable text/numbers on face. Can display redstone signal level.
**Assets available:** Model `redstone/redstone_display` ✅ (1 element)

### `remote_controlled_redstone` - Remote Control
**Requires:** GlobalBlockNetwork + player interaction (item-triggered channel broadcast)
**Blocks:** 1 block + 1 item (remote controller)
**Logic:** Player holds remote → right-click sends pulse to all blocks on linked channel.
**Assets available:** Model `redstone/remote_controlled_redstone` ✅ (30 elements)

### `ore_refiner` - Ore Credit Processor
**Requires:** Custom credit-based recipe system (not standard machine recipes)
**Blocks:** 1 block
**States:** `ore_result` (0-8), `level` (0-10 fill visual)
**Logic:** Credit accumulation per ore type. Outputs refined product when credits ≥ threshold.
**Assets available:** Model `redstone/ore_refiner` ✅ (29 elements), Texture `ore_refiner.png` ✅

### `super_smelter` - Bulk Furnace
**Requires:** Item entity detection + fuel consumption + 40 recipes
**Blocks:** 1 block
**Logic:** Detects item entities on block, smelts if fuel (coal/charcoal) available in chest above.
**Assets available:** Model `redstone/super_smelter` ✅ (24 elements)

### `precision_wire_gutter` / `redstone_gutter` - Redstone Wire Routing
**Requires:** Custom redstone wire routing system (directional signal propagation)
**Blocks:** 5 variants each (straight, up, down, left, right)
**Logic:** Routes redstone signal in specific direction without bleed.
**Assets available:** Models ✅ (33-83 elements each), Textures ✅

---

## Available Assets Not Yet Assigned

These models/textures exist but have no machine implementation yet:

| Asset | Type | Elements |
|---|---|---|
| `redstone/conveyor` | Model | 7 |
| `redstone/diagonal_up_conveyor` | Model | 7 |
| `redstone/diagonal_down_conveyor` | Model | 7 |
| `redstone/craftingtable_redstone` | Model | 1 |
| `redstone/breeder_test` | Model | 7 |
| `redstone/grinder` | Model | 8 |

Textures without models: `grinder_normal.png`, `grinder_turning.png`, `light_sensor_0-5.png`, various `lamp/*.png` files, `particle/*.png`.
