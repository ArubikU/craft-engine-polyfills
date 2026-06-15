# craft-engine-polyfills — Context

Working notes on the codebase, the CraftEngine 26.6.2 API patterns it relies on, the
conveyor/vapor-motor systems built on top, and the dev/test workflow. Written for a
future session to pick up without re-deriving everything.

---

## 1. Project & environment

- **Plugin**: `craft-engine-polyfills` — a Paper plugin that extends CraftEngine (CE).
- **Target CE version**: `26.6.2` (Mojang-mapped NMS via paperweight userdev, Java 21).
- **Build**: `./gradlew build -x test` → `build/libs/craft-engine-polyfills.jar`.
- **Tests**: `./gradlew test --tests "<FQCN>"` (JUnit 5). Conveyor math is unit-tested
  in `ConveyorMathTest`.
- **Repo root**: `D:\Github\craft-engine-polyfills`. Main branch: `main`. Work branch:
  `migrate/craftengine-26`.

### Test server
- Location: `testserver/` (Paper `paper.jar`, MC Java **1.21.11**, offline mode).
- Connect: Direct Connect → `localhost:25599`.
- Start (background): `cd testserver && java -Xmx3G -Xms1G -jar paper.jar --nogui > server-run.log 2>&1 &`
- RCON: `node .migration/rcon.js "<cmd>"` (host 127.0.0.1:25575, password `polyfilltest`).
  - `/craftengine give|send` FAIL from RCON (cloud-command parser rejects console sender).
  - `/ce item get demo:<id>` works **in-game** for the player.
  - `/craftengine reload all` regenerates+uploads the resource pack (subcommands:
    `config | recipe | pack | all`).
- Op the player + creative for testing.

### Deploy loop (used every iteration)
```
./gradlew build -x test -q          # build jar
node .migration/rcon.js "stop"      # stop server (RCON)
sleep 10                            # let it release the jar
cp build/libs/craft-engine-polyfills.jar testserver/plugins/
cd testserver && java ... paper.jar --nogui > server-run.log 2>&1 &   # restart
# wait for "[CraftEnginePolyfill] CraftEngine Polyfills Enabled" in server-run.log
node .migration/rcon.js "craftengine reload all"   # only if config/pack changed
```
- Only ONE plugin jar in `testserver/plugins/` (build artifact is unversioned
  `craft-engine-polyfills.jar`; removed the old `-0.0.4.jar` to avoid double-load).
- Code-only changes need a jar swap + restart (no pack reload). Config/texture changes
  need `reload all` (no restart).

### Demo pack ↔ served pack
- Authoring source: `demo/` (configuration + resourcepack).
- Served copy: `testserver/plugins/CraftEngine/resources/polyfills_demo/` (a **copy**,
  not a symlink). After editing `demo/...`, copy the changed file into the served pack,
  then `reload all`. Keep them in sync (`diff -rq demo/configuration polyfills_demo/configuration`).
- Generated pack zip: `testserver/plugins/CraftEngine/generated/resource_pack.zip`.

---

## 2. CraftEngine 26.6.2 API patterns (the important gotchas)

- **Behavior key ≠ config id.** A behavior is registered under e.g. `polyfills:conveyor`
  (the FACTORY key) but the actual block/item id is from config, e.g. `demo:conveyor`.
  NEVER hardcode the behavior key where the config id is needed. Resolve item→block via
  `CraftEngineBlocks.byId(itemId)` and check the behavior type instead.
- **Behaviors are wrapped.** `ImmutableBlockState.behavior()` is usually a
  `DualBlockBehavior` or `CompositeBlockBehavior` (both in
  `net.momirealms.craftengine.bukkit.block.behavior`, both expose `getFirst(Class)` on
  the **concrete** type — the shared `CombinedBlockBehavior` interface does NOT). Unwrap:
  ```java
  Object b = state.behavior();
  if (b instanceof XBehavior) ...
  else if (b instanceof DualBlockBehavior d) d.getFirst(XBehavior.class) ...
  else if (b instanceof CompositeBlockBehavior c) c.getFirst(XBehavior.class) ...
  ```
- **NMS behavior callbacks** come in as `(Object thisBlock, Object[] args)`. `util.NmsBlockBehavior`
  bridges these to typed overrides: `onPlace(thisBlock, Level, BlockPos, BlockState)`,
  `tick`, `neighborChanged`, `affectNeighborsAfterRemoval(..., movedByPiston)`, `onLand`.
  The default impls log "Default <hook> behavior" (visible in console; just CE-level noise).
- **Right-click** = override `useWithoutItem(UseOnContext, ImmutableBlockState)` returning
  `InteractionResult` (`PASS` / `SUCCESS_AND_CANCEL`). Inside:
  - NMS ServerLevel: `((CraftWorld)((BukkitWorld)ctx.getLevel()).platformWorld()).getHandle()`
  - NMS BlockPos: `(BlockPos) LocationUtils.toBlockPos(ctx.getClickedPos())`
  - Bukkit player: `((BukkitServerPlayer) ctx.getPlayer()).platformPlayer()`
- **Block placement / removal**:
  - `CraftEngineBlocks.place(Location, ImmutableBlockState, UpdateFlags, boolean)` — REPLACES the
    block → **recreates the block entity** (wipes its state). Use it only for fresh placement.
  - `CraftEngineBlocks.remove(Block, true)` removes + drops loot (block item). `remove(Block, false)` no drop.
  - To change ONLY a property in place WITHOUT recreating the BE, use a flag-2 NMS setBlock:
    ```java
    Object lvl = world.world().minecraftWorld();
    Object bp = MNms.INSTANCE.constructor$BlockPos(x,y,z);
    Object nms = newState.customBlockState().minecraftState();
    MNms.INSTANCE.method$LevelWriter$setBlock(lvl, bp, nms, 2); // 2 = notify clients only
    ```
- **`auto_state`** in block appearances picks a vanilla host from an `AutoStateGroup`
  (`SOLID`, `NOTE_BLOCK`, `MUSHROOM`, `TRIPWIRE`, leaves, etc). `mappings.yml` (in CE
  internal) frees redundant vanilla states for custom use (e.g. trapdoor `powered=true`
  maps to `powered=false`, freeing the powered states).
- **Entity-rendered blocks** (real vanilla host + model drawn by a display entity):
  appearance uses `state:` + `entity_renderer:`:
  ```yaml
  appearances:
    foo:
      state: waxed_copper_trapdoor[facing=north,half=bottom,open=false,powered=true,waterlogged=false]
      entity_renderer: { item: demo:render_item, rotation: 90, scale: 1.0, translation: 0,0,0 }
  ```
  `entity_renderer.item` references a CE **item** whose model is rendered (no raw model
  path). The host vanilla block is NOT auto-hidden — to hide it, ship a resource-pack
  blockstate override mapping it to an empty model:
  - `resourcepack/assets/minecraft/models/custom/empty.json` → `{ "textures": {}, "elements": [] }`
  - `resourcepack/assets/minecraft/blockstates/waxed_copper_trapdoor.json` →
    `{ "multipart": [ { "apply": { "model": "minecraft:custom/empty" } } ] }`
  (Hides ALL of that block; fine for a demo.) The conveyor uses ONE trapdoor state as
  host for all 72 variants; facing is handled by `entity_renderer.rotation`.
- **i18n**: `item_name: "<!i><lang:item.demo.conveyor>"` + a CE lang config
  (`demo/configuration/lang.yml`):
  ```yaml
  lang#items:
    en_us: { item.demo.conveyor: Conveyor Belt }
    es_es: { item.demo.conveyor: Cinta Transportadora }
  ```
  Resolved client-side from the resource pack lang.

### Persistence (CRITICAL — learned the hard way)
- Block-entity controllers extend `PersistentWorldlyBlockEntity` (size N inventory) →
  `PersistentBlockEntity`. They expose `saveCustomData(tag)/loadCustomData(tag)` and a
  PDC-style `set(TypedKey,T)/get(TypedKey)`.
- **The conveyor overrides `setChanged()` to a no-op**, so the BE is never marked dirty
  and CE may not persist it on chunk save → state lost on restart.
- **The reliable, chunk-backed path** (used by `AbstractWorldlyContainer` / `BlockContainer`):
  `CustomBlockData.from(Block)` (or `from(Level, BlockPos)`) + `TypedKeys` + `CustomDataType`.
  - `TypedKeys.CONTENTS` = `List<ItemStackWithSlot>` (inventory), via
    `ArrayItemStackWithSlot.from(ItemStack[])`.
  - Build ad-hoc keys: `TypedKey.of("craftengine", "key"+i, PersistentDataType.FLOAT|STRING|...)`.
  - `data.set(key, val)`, `data.getOrDefault(key, def)`, `data.getOptional(key)`, `data.clear()`.
  - Custom types: `TypedKey.of(ns, key, CustomDataType<T,P>)` — see `CustomDataType`
    (`ITEM_STACK_WITH_SLOT_LIST_TYPE`, `ITEM_STACK_TYPE`, `UUID_TYPE`).
- The conveyor now persists via `CustomBlockData` directly (see §3), saving **only on
  item add/remove** (a `dirty` flag), loading lazily on the first tick, and **clearing**
  the data on break and on fresh placement (so a new belt never reads a previous belt's
  items/links).

---

## 3. Conveyor belt system

Files: `src/main/java/dev/arubik/craftengine/conveyor/`
- `ConveyorBehavior` — block behavior (`polyfills:conveyor`), factory + config args.
- `ConveyorBlockEntity` — the controller; all transport/render/persistence logic.
- `ConveyorItemDisplay` — server-side fake `item_display` entity per carried item.
- `ConveyorMath` — pure (joml-only) movement/orientation math; unit-tested.
- `ConveyorPath` / `ConveyorPart` / `ConveyorSlope` — A→B planning + enums.
- `ConveyorWandListener` — left-click wand placement, right-click extend.
- `ConveyorBreakListener` — `BlockBreakEvent` → teardown (see "break" below).
- `ConveyorReceiver` — interface for anything that can accept a belt's output.

### Block config (`demo/configuration/blocks/conveyor.yml`)
- Properties: `facing` (4-direction), `slope` (string flat/up/down), `part`
  (string start/middle/end), `activated` (boolean). 4×3×3×2 = 72 states.
- Each appearance = host `waxed_copper_trapdoor` state + `entity_renderer` item
  (`demo:cv_<model>`), facing via `rotation` (all rotations are baked +180° to match the
  authored models). 18 render items (3 slope × 3 part × 2 activated).
- Behavior args (all optional, defaults shown):
  ```yaml
  behavior:
    type: polyfills:conveyor
    facing: north
    base-travel-ticks: 16   # ticks to cross 1 segment at base speed (lower = faster)
    base-rpm: 64            # reference rpm
    stress-impact: 4        # SU per segment (scaled by speed; see §4)
    slots: 4               # items carried per segment (spacing = 1/slots)
  ```

### Placement (wand)
- Hold the conveyor **block item**, **left-click** block A then block B. Path must be a
  straight single-axis horizontal run (optional consistent 45° slope via Y). Places the
  whole line A→B, `part` START at A, END at B, linked via `prevPos`.
- Placement targets the **clicked face's adjacent cell** (`clicked.getRelative(face)`),
  not the clicked block.
- **No vapor motor required** to place (motor can be added later).
- Survival consumes 1 belt item per segment (caps the run at held amount). Creative free.
- On placement each cell's stale `CustomBlockData` is **cleared** first.
- **Right-click** with the belt item: never drops a loose block. If the clicked block is
  a belt END → extend forward (+1); a belt START → extend backward (+1, new head). Else
  cancel (no placement). Consumes 1 in survival.

### Movement & multi-item transport
- Each segment carries up to `slots` items; one per inventory slot, each with its own
  `progress[0..1]`, `jitter` (yaw wobble), `entryDir`, and display entity.
- Spacing between items = `1/slots`; followers cap behind the next-higher-progress item;
  the front item caps at 1.0.
- Speed: `inc = ConveyorMath.progressPerTick(effectiveRpm, baseRpm, baseTravelTicks)`.
- **Hand-off carries the overshoot** (the bit past 1.0) into the next segment so the
  item keeps a CONSTANT speed across the boundary (no hitch). `adoptItem(..., startProgress, entry)`
  rejects if the incoming item wouldn't stay a full gap behind existing items (no merge/overlap).
- **Display continuity**: on hand-off the SAME `ConveyorItemDisplay` entity is transferred
  to the next segment (`adoptItem` adopts it) — no spawn/despawn pop, and the carried
  `jitter` keeps the rotation stable along a straight run.
- **Rotation must be re-sent on change.** `updatePosition` sends position only; the
  rotation lives in entity metadata (sent at spawn). `ConveyorItemDisplay.setRotation`
  sets a `rotationDirty` flag when it changes; `renderAll` pushes fresh metadata
  (`updateMetadata`) to viewers only when dirty (slope→flat, corner turns). Without this
  the slope tilt sticks when the item moves to a flat belt.

### Geometry (`ConveyorMath`)
- `startPoint/endPoint(stepX, stepZ, slopeStepY)` — block-relative entry/exit. `BELT_TOP_Y = 0.28`.
  Slopes add a `slopeLift` of **0.5** so the item rides ON the ramp surface.
- `itemRotation(facingStepX, facingStepZ, slopeStepY)` — lays the item flat (−90° about X)
  + ramp pitch (up = +45, down = −45) + yaw from facing.
- Render path is **edge → centre → exit** (two halves, 50/50 at progress 0.5):
  - `center.y = (startRel.y + exitRel.y) / 2` (midpoint — must include slopeLift, else the
    item sinks at the middle of a ramp).
  - In-line item: entry = `startRel` → straight line through centre (unchanged behavior).
  - **T-junction / corner**: a perpendicular hand-off enters from the SIDE edge
    (`entryDir = sourceFacing.opposite()`), travels edge→centre, then centre→its own exit,
    rotating toward the new direction at the centre. No teleport-to-start.

### Output / receivers (`ConveyorReceiver`)
- Interface: `boolean isFull()` + `boolean receiveConveyorItem(ItemStack, Direction sourceFacing)`.
- `ConveyorBlockEntity` implements it (`isFull = !backHasRoom()`).
- Hand-off order in `tryHandOff`:
  1. **Conveyor→Conveyor** (linked downstream OR a conveyor at the exit cell): rich path
     (display transfer + overshoot + corner).
  2. **Conveyor→generic ConveyorReceiver** (machine/buffer): if `isFull()` → **STALL**
     (don't drop); else `receiveConveyorItem`.
  3. **Nothing ahead**: drop the item off the end (only case items hit the ground).
- **Backpressure**: a full receiver stalls the front item at progress 1.0 → followers
  queue (spacing) → the whole 1×1 line stops until the receiver frees. Per-item, automatic.

### Break behavior (`ConveyorBreakListener` + `onBroken`)
- CE's `affectNeighborsAfterRemoval` callback proved unreliable for player breaks
  (esp. creative), so teardown runs from a Bukkit `BlockBreakEvent` listener
  (MONITOR, ignoreCancelled). A static `LISTENER_HANDLED` set dedupes vs the behavior
  callback (which still covers piston/other removals).
- Topology decides the result:
  - **START** broken → downstream neighbour becomes the new START (end moves, −1).
  - **END** broken → upstream neighbour becomes the new END (−1).
  - **2-segment belt** → breaking either destroys BOTH.
  - **MIDDLE** broken → destroy the WHOLE belt, dropping every segment's block item +
    carried items. A static `teardownInProgress` guard stops re-entrancy from the
    `remove(block, true)` cascade.
- All carried items drop; all display entities despawn; the cell's `CustomBlockData` is cleared.

### RPM input (how a belt gets powered)
- The HEAD segment (no upstream) drives the whole line. It scans the 3 open horizontal
  sides (front/left/right) of BOTH ends (start + tail) for a `VaporMotorBlockEntity` and
  uses the stronger one's rpm — so a motor on either end, any side, powers the belt
  without changing its direction. Body segments read `upstream.effectiveRpm()`.

---

## 4. Vapor motor + stress economy (Create-style)

Files: `src/main/java/dev/arubik/craftengine/rotation/`
- `RpmProvider` — interface: `getRpm()`, `stressCapacity()`, `reportStressLoad(su)`.
- `VaporMotorBlockEntity` — consumes vapor (gas `STEAM`) → RPM; has a tunable UI.
- `VaporMotorBehavior` — block behavior (`polyfills:vapor_motor`), factory.

### Economy
- **RPM** = belt speed. A belt segment imposes stress `stress-impact × (motorRpm / base-rpm)` —
  i.e. SU per item **scales with speed** (128 rpm = 2× speed AND 2× SU/segment).
- The head sums line stress (`segments × per-segment SU`). If it exceeds the motor's
  `stress-capacity` → **OVERSTRESSED**, the line stalls (effectiveRpm 0).
- The motor's vapor demand = `vapor-per-tick + load × vapor-per-stress` (more belts driven
  = more vapor). RPM produced scales with how much of the demand the available vapor satisfies.
- Motor config (`demo/configuration/blocks/vapor_motor.yml`):
  ```yaml
  behavior:
    type: polyfills:vapor_motor
    vapor-capacity: 10000
    vapor-per-tick: 20        # base vapor for full rpm
    max-rpm: 128             # output speed
    stress-capacity: 256     # SU it can drive
    vapor-per-stress: 1      # extra vapor/tick per SU of load
  ```

### Motor UI (right-click)
- 9-slot dispenser menu (`MachineLayout` + `addButton`/`setDynamicProvider`,
  click handling via `MachineMenuListener`).
- Row 0 = live stats: vapor (stored/demand/per-stress), output (current/max rpm),
  stress (load/capacity).
- Row 2 = tuning buttons: `− RPM` / max-rpm / `+ RPM` (±16) and `− Stress` / cap /
  `+ Stress` (±32 SU). Tuned values clamp + **persist** (`KEY_MAX_RPM`, `KEY_STRESS_CAP`).
- `MachineBlockBehavior.useWithoutItem` opens the machine menu (the motor + the
  upgradeable furnace inherit it). Machine BEs expose `getMenu()` / `getLayout()`.

---

## 5. Other blocks (brief)

- **Creative gas tank** (`polyfills:creative_gas_tank`) — infinite STEAM source; pushes
  to 6 neighbours each tick. Use it to power a motor with no fuel setup.
- **Upgradeable furnace** (`polyfills:upgradeable_furnace`) — sample machine; input +
  output + fuel + 3 upgrade slots (sugar=SPEED, redstone=EFFICIENCY, glowstone=YIELD).
  Right-click opens its machine menu. Model: `block/custom/upgradeable_furnace.json`
  (parents vanilla furnace).
- **Workbench** (`polyfills:workbench`) — IE-style station; a real 2-wide double block via
  `HorizontalDoubleBlockBehavior` (LEFT master + RIGHT auto-placed at `facing.clockWise()`;
  aborts placement if the right cell is blocked). 4-row (6-wide) chest UI; 3×2 input grid,
  2 outputs, 1 tool slot (consumes durability); tool-gated recipes (`condition`+`executor`).
- **Crafting table** (`polyfills:crafting_table`) — JIT N×M crafting UI framework
  (`AbstractCraftingMenu`, `SlotLayout`, shaped/shapeless, multi-output).
- **Conveyor merger** (`polyfills:conveyor_merger`) — directional; 3 inputs (back/left/
  right), 1 output (front). `MergerBlockEntity` (a `ConveyorReceiver`) buffers items
  (`slots`, default 6) and feeds the front belt FIFO with backpressure. Rejects a belt
  feeding from the front side.
- **Conveyor splitter** (`polyfills:conveyor_splitter`) — directional; 1 input (back),
  3 outputs (front/right/left). `SplitterBlockEntity` distributes EVENLY regardless of
  per-output drain rate / stack size / rpm: each item goes to the in-room output with the
  fewest dispatched so far (running `long[3]` counts); blocked outputs are skipped and
  re-balanced when they free. Accepts only from the back (src == facing).
- **Depot** (`polyfills:depot`) — 14³ buffer + inventory. `DepotBlockEntity` is a
  `ConveyorReceiver`; storage is a CE-native `BukkitWorldlyStorageContainer` (owner =
  `bukkit.world.WorldlyContainerHolder` supplying the `WorldPosition`). **No GUI** —
  right-click empty hand takes the last stack, held stack puts in. `size` slots
  configurable (default 27). Items render as floating `item_display`s in a 3×3 grid on
  top. Comparator via `getAnalogOutputSignal`; persistence via `CustomBlockData`
  (load first tick, save-on-change + `updateNeighbourForOutputSignal`).
  - **HOPPER BRIDGE (the real CE mechanism — verified by decompiling craft-engine.jar):**
    a custom block is hopper-accessible ONLY if its **behavior** `implements
    net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder` and overrides
    `getContainer(Object thisBlock, Object[] args)` (args[1]=NMS Level, args[2]=NMS
    BlockPos). CE's injected `CraftEngineBlock.getContainer` **casts the returned object to
    an NMS `net.minecraft.world.WorldlyContainer`** — so you MUST return
    `FastNMS.INSTANCE.createContainer(bukkitContainer)` (wraps a CE `BukkitContainer` into
    the NMS `CustomWorldlyContainer`), NOT the raw CE container and NOT the NMS-interface BE.
    Implementing the *NMS* `WorldlyContainer` on the block entity does nothing (CE ignores
    it). NB: `block/behavior/StorageBlockBehavior` has the same latent bug (defines
    `getContainer` but doesn't implement `WorldlyContainerHolder`) → its hoppers never worked.
  - Item↔CE conversion: `BukkitItemManager.instance().wrap(bukkitStack)` / `Item.minecraftItem()`.
- Shared conveyor-network plumbing: `conveyor/ConveyorRouting` (find/push to a
  `ConveyorReceiver` at an exit cell, cw/ccw helpers), `conveyor/AbstractRouterBlockEntity`
  (buffer + ticker base for merger/splitter), `conveyor/RouterDrops` (break teardown).
  Models reuse existing conveyor/vapor-motor textures under
  `models/block/custom/conveyor_io/` (merger/splitter/depot).

---

## 6. Util layer

- `util.MNms` — direct-NMS shim (`constructor$X`, `method$X`, `field$X`).
- `util.NmsBlockBehavior` — `(Object,Object[])` → typed callback bridge (see §2).
- `util.Utils` — `getAsInt/getAsDouble/getAsFloat(Object value, String name)` config helpers.
- `util.CustomBlockData` / `util.TypedKey` / `util.TypedKeys` / `util.CustomDataType` /
  `util.ArrayItemStackWithSlot` — the chunk-backed persistence stack (see §2).
- `block.entity.BukkitBlockEntityTypes.getIfLoaded(Level, BlockPos)` — null-guarded BE lookup
  (`new BukkitWorld(level.getWorld()).storageWorld().getBlockEntityAtIfLoaded(...)`).
- `machine.block.MachineBlockBehavior` / `machine.block.entity.AbstractMachineBlockEntity` —
  reusable machine base (IO config, gas/fluid carriers, menu, ticking). `getIOConfiguration`
  and `getIfLoaded` are null-guarded for the config-parse-time `(null,null)` call.

---

## 7. Known constraints / footguns

- `Math.random()`/`new Date()` are fine in plugin code (only restricted inside Workflow scripts).
- A fresh BE has no `level`/`world` until it ticks → do CustomBlockData I/O in/after the
  first `serverTick`, never in the constructor.
- When transferring display entities across a chunk boundary, the new segment's viewer set
  may differ; the common (same-chunk) case is handled, edge case accepted.
- Resource-pack changes require the client to re-accept the pack (new hash) on reconnect.
