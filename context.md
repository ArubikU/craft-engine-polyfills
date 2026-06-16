# craft-engine-polyfills — Context

Working notes on the codebase, the CraftEngine 26.6.2 API patterns it relies on, the
conveyor / vapor-motor / aluminum systems, the `modern` pack migration, and the
dev/test workflow. Written so a future session can pick up without re-deriving everything.

---

## 0. Current state (read first)

- **Everything was migrated into the `modern` pack** (namespace **`cml`**). The old
  `polyfills_demo` pack (namespace `demo`) is **gone** — its configs/lang/recipe/RP were
  renamespaced `demo:` → `cml:` and merged into `modern`.
- In-game item ids are now `cml:<id>` (e.g. `/ce item get cml:conveyor`, `cml:aluminum_ore`).
- Server loads clean for the polyfill content: **216 blocks, 586 items, 10 machine
  recipes + 6 workbench recipes**. Remaining warnings (9) are **pre-existing `modern`
  cosmetic issues**, NOT from the polyfills: furniture `variant:` format
  (`street_lamp*`, `cactus_flower_pot`), `carved_wood.yml`, `farming.yml`
  (`sea_asparagus`), and a vanilla chicken/`cluckshroom` recipe parse error. Deferred.

---

## 1. Project & environment

- **Plugin**: `craft-engine-polyfills` — a Paper plugin extending CraftEngine (CE).
- **Target CE**: `26.6.2` (Mojang-mapped NMS via paperweight userdev, Java 21).
- **Build**: `./gradlew build -x test` → `build/libs/craft-engine-polyfills.jar`.
- **Tests**: `./gradlew test --tests "<FQCN>"` (JUnit 5). `ConveyorMathTest` covers the
  conveyor math (slope lift, ramp tilt, rotation, progress/tick).
- **Repo root**: `D:\Github\craft-engine-polyfills`. Main branch `main`; work branch
  `migrate/craftengine-26`.

### Resource pack sources & served copies
- **Repo-root `modern/`** is the authoring source (`configuration/` + `resourcepack/`).
  Layout: `configuration/{block,item,lang,templates}/` (singular `block`/`item`),
  plus migrated polyfills under `configuration/block/polyfills/` and
  `configuration/item/polyfills/`, lang at `configuration/lang/polyfills_lang.yml`,
  images at `configuration/polyfills_images.yml`.
- **`demo/` is deleted** (do not look for it). The migrated content lives only in `modern/`.
- **Served copy**: `testserver/plugins/CraftEngine/resources/modern/` (a COPY, not a
  symlink). After editing `modern/...`, copy the changed file into the served `modern`,
  then `reload all` (config/pack changes) or restart (block-state/registration changes).
- Other CE resource packs present (don't touch): `internal`, `legacy_armor`,
  `remove_shulker_head`. The CE `default_*` packs are **disabled** so `modern`'s own
  templates win (the migration relies on this).
- Generated pack zip: `testserver/plugins/CraftEngine/generated/resource_pack.zip`.

### Test server
- `testserver/` — Paper `paper.jar`, **MC Java 1.21.11**, offline mode.
- Connect: Direct Connect → `localhost:25599`. Op + creative for testing.
- Start (background): `cd testserver && java -Xmx3G -Xms1G -jar paper.jar --nogui > server-run.log 2>&1 &`
  - **Note: `> server-run.log` does not always truncate** if a lingering server still
    holds it — the log can contain multiple boots. Always check timestamps / the latest
    `Polyfills Enabled` line before trusting an error count.
- RCON: `node .migration/rcon.js "<cmd>"` (127.0.0.1:25575, password `polyfilltest`).
  - `/craftengine give|send` FAIL from RCON (console sender rejected). `/ce item get cml:<id>` works in-game.
  - `/craftengine reload <config|recipe|pack|all>` — `all` reloads configs + regenerates/uploads the pack.

### Deploy loop
```
./gradlew build -x test -q
node .migration/rcon.js "stop"     # then sleep ~10 so the jar/log releases
cp build/libs/craft-engine-polyfills.jar testserver/plugins/
cd testserver && java ... paper.jar --nogui > server-run.log 2>&1 &
# wait for "[CraftEnginePolyfill] CraftEngine Polyfills Enabled"
# reload all ONLY if config/pack changed (code-only = jar swap + restart)
```
Keep exactly ONE plugin jar in `testserver/plugins/` (unversioned `craft-engine-polyfills.jar`).

### CE-level server config the migration depends on (served, not in repo modern)
- `testserver/plugins/CraftEngine/config.yml` → `block.serverside-blocks: 8000` (was 2000;
  needed because `modern`'s full block set exhausts the default state pool). **Restart to apply.**
- `testserver/plugins/CraftEngine/resources/internal/configuration/mappings.yml` — the
  **chorus_plant block-state mappings were uncommented** to release the connection-shaped
  collision states used by the pipe blocks (see §6). `mappings.yml` collapses redundant
  vanilla states (e.g. `cactus[age=1..15]→age=0`, trapdoor `powered=true→false`) to free
  them as custom hosts.

---

## 2. CraftEngine 26.6.2 API patterns (gotchas)

- **Behavior key ≠ config id.** Behaviors register under a factory key (e.g.
  `polyfills:conveyor`); the block/item id comes from config (now `cml:conveyor`). NEVER
  hardcode the behavior key where the config id is needed — resolve item→block via
  `CraftEngineBlocks.byId(itemId)` and check the behavior type. (Namespace-agnostic, so the
  `demo→cml` rename needed no Java changes — all `demo:` in Java were comments.)
- **Behaviors are wrapped** in `DualBlockBehavior` or `CompositeBlockBehavior`
  (`net.momirealms.craftengine.bukkit.block.behavior`). `getFirst(Class)` exists only on
  the concrete types, not the shared `CombinedBlockBehavior` interface. Unwrap:
  ```java
  Object b = state.behavior();
  if (b instanceof XBehavior) ...
  else if (b instanceof DualBlockBehavior d) d.getFirst(XBehavior.class) ...
  else if (b instanceof CompositeBlockBehavior c) c.getFirst(XBehavior.class) ...
  ```
- **NMS behavior callbacks** arrive as `(Object thisBlock, Object[] args)`. `util.NmsBlockBehavior`
  bridges to typed overrides: `onPlace(thisBlock, Level, BlockPos, BlockState)`, `tick`,
  `neighborChanged`, `affectNeighborsAfterRemoval(..., movedByPiston)`, `onLand`. Defaults
  log "Default <hook> behavior" (CE-level noise).
- **Right-click**: override `useWithoutItem(UseOnContext, ImmutableBlockState)` → `InteractionResult`.
  - ServerLevel: `((CraftWorld)((BukkitWorld)ctx.getLevel()).platformWorld()).getHandle()`
  - BlockPos: `(BlockPos) LocationUtils.toBlockPos(ctx.getClickedPos())`
  - Player: `((BukkitServerPlayer) ctx.getPlayer()).platformPlayer()`
- **Place / remove**:
  - `CraftEngineBlocks.place(Location, ImmutableBlockState, UpdateFlags, boolean)` REPLACES the
    block → recreates the block entity (wipes state). Fresh placement only.
  - `CraftEngineBlocks.remove(Block, true)` removes + drops loot; `(Block, false)` no drop.
  - Change ONLY a property in place (keep the BE) via flag-2 NMS setBlock:
    ```java
    Object lvl = world.world().minecraftWorld();
    Object bp = MNms.INSTANCE.constructor$BlockPos(x,y,z);
    Object nms = newState.customBlockState().minecraftState();
    MNms.INSTANCE.method$LevelWriter$setBlock(lvl, bp, nms, 2); // 2 = notify clients only
    ```
- **`auto_state`** picks a vanilla host from an `AutoStateGroup` (`SOLID`, `NOTE_BLOCK`,
  `MUSHROOM`, `CACTUS`, `CHORUS`, `TRIPWIRE`, leaves, …). Each group has a finite freed
  pool. `auto_state: cactus` has only ~15 slots and is shared — full-cube machines should
  use `auto_state: solid` (the 8000 pool). Pipes that need exact per-connection hitboxes
  use explicit `state: minecraft:chorus_plant[...]` (see §6), not auto_state.
- **Entity-rendered blocks** (real vanilla host + model drawn by a display entity):
  ```yaml
  appearances:
    foo:
      state: waxed_copper_trapdoor[facing=north,half=bottom,open=false,powered=true,waterlogged=false]
      entity_renderer: { item: cml:render_item, rotation: 90, scale: 1.0, translation: 0,0,0 }
  ```
  `entity_renderer.item` references a CE **item** (its model is rendered — no raw model
  path). The host is NOT auto-hidden; ship a blockstate override mapping it to an empty model:
  - `resourcepack/assets/minecraft/models/custom/empty.json` → `{ "textures": {}, "elements": [] }`
  - `resourcepack/assets/minecraft/blockstates/waxed_copper_trapdoor.json` →
    `{ "multipart": [ { "apply": { "model": "minecraft:custom/empty" } } ] }`
  The **conveyor** uses ONE trapdoor state as host for all 72 variants; facing via
  `entity_renderer.rotation`. (`modern` does not otherwise use waxed_copper_trapdoor, so the
  full-block override is safe.) These RP files live in `modern/resourcepack/assets/minecraft/`.
- **i18n**: `item_name: "<!i><lang:item.cml.conveyor>"`. Two valid CE lang config roots:
  `modern`'s own files use `lang:` → `en_us:`/`es_es:` → `block.cml.X|item.cml.X`; the
  migrated polyfills lang uses `lang#items:` (also valid) at
  `modern/configuration/lang/polyfills_lang.yml`. Keys were renamespaced `item.demo.`→`item.cml.`.
- **Templates** (`modern/configuration/templates/`): `block_settings.yml`,
  `block_templates.yml`. Footguns hit during migration: `default:settings/stone` must exist
  (modern defines it ~line 241); the `simple_cube` block template must NOT use a
  `${settings_template:-default:settings/stone}` default-expansion in a template-ref
  position (SNBT validation rejects it) — pass `${settings_template}` (all callers supply it).

### Persistence (CRITICAL — learned the hard way)
- BE controllers extend `PersistentWorldlyBlockEntity` (size N inventory) → `PersistentBlockEntity`.
  They expose `saveCustomData(tag)/loadCustomData(tag)` + PDC-style `set/get(TypedKey)`.
- **The conveyor overrides `setChanged()` to a no-op** → the BE is never marked dirty →
  CE may not persist it on chunk save → state lost on restart.
- **Reliable, chunk-backed path** (used by `AbstractWorldlyContainer`/`BlockContainer`):
  `CustomBlockData.from(Block)` (or `from(Level, BlockPos)`) + `TypedKeys` + `CustomDataType`.
  - `TypedKeys.CONTENTS` = `List<ItemStackWithSlot>` (inventory) via `ArrayItemStackWithSlot.from(ItemStack[])`.
  - Ad-hoc keys: `TypedKey.of("craftengine", "key"+i, PersistentDataType.FLOAT|STRING|...)`.
  - `data.set(key,val)`, `getOrDefault(key,def)`, `getOptional(key)`, `clear()`.
  - Custom types: `TypedKey.of(ns,key,CustomDataType<T,P>)` — see `CustomDataType`
    (`ITEM_STACK_WITH_SLOT_LIST_TYPE`, `ITEM_STACK_TYPE`, `UUID_TYPE`).
- The conveyor persists via `CustomBlockData` directly: saves **only on item add/remove**
  (a `dirty` flag + `setPrevPos`), loads lazily on the first tick, loads inventory ONLY
  when persisted CONTENTS exist (never clobbers a fresh/just-handed-off segment), and
  **clears** the data on break and on fresh placement (wand/extend) so a new belt never
  inherits a previous belt's items/links.
- **Recipe load order**: the plugin's JSON recipes (`src/main/resources/recipes/`, copied
  to `testserver/plugins/CraftEnginePolyfill/recipes/`) reference CE custom items
  (`cml:aluminum_scraping`). `onEnable` runs BEFORE CE registers its items → `byId` null.
  Fixed by removing the premature `RecipeManager.loadRecipes()` from `onEnable`; the
  `CraftEngineReloadEvent` listener loads recipes after CE's items (incl. first load).

---

## 3. Conveyor belt system

Files: `src/main/java/dev/arubik/craftengine/conveyor/`
- `ConveyorBehavior` (`polyfills:conveyor`), `ConveyorBlockEntity` (transport/render/persist),
  `ConveyorItemDisplay` (per-item fake `item_display` entity), `ConveyorMath` (pure math,
  tested), `ConveyorPath`/`ConveyorPart`/`ConveyorSlope`, `ConveyorWandListener`,
  `ConveyorBreakListener`, `ConveyorReceiver`.

### Config (`modern/configuration/block/polyfills/conveyor.yml`, id `cml:conveyor`)
- Properties: `facing` (4-dir), `slope` (flat/up/down), `part` (start/middle/end),
  `activated` (bool) → 72 states. Host = ONE `waxed_copper_trapdoor` state + `entity_renderer`
  item `cml:cv_<model>` (18 render items, 3 slope × 3 part × 2 activated); facing via
  `rotation` (all baked +180° to match authored models).
- Behavior args (defaults): `base-travel-ticks: 16`, `base-rpm: 64`, `stress-impact: 4`,
  `slots: 4`.

### Placement / extend
- Wand = hold the belt item, LEFT-click A then B → straight single-axis run (optional
  consistent 45° slope), placed against the clicked face's adjacent cell, START at A / END
  at B, linked by `prevPos`. No motor required. Survival consumes 1 item/segment. Each cell's
  stale `CustomBlockData` is cleared first.
- RIGHT-click belt item: never drops a loose block; clicked END → extend forward,
  clicked START → extend backward, else cancel. New cell's data cleared before linking.

### Movement & multi-item
- Up to `slots` items/segment, one per inventory slot, each with own `progress`, `jitter`
  (yaw wobble), `entryDir`, display entity. Spacing = `1/slots`; followers cap behind the
  next-higher-progress item; front caps at 1.0.
- Hand-off carries the **overshoot** past 1.0 into the next segment (`adoptItem(...,startProgress,entry)`)
  for constant speed across boundaries; rejects if the incoming item wouldn't stay a full
  gap behind (no overlap/merge).
- **Display continuity**: the SAME `ConveyorItemDisplay` entity transfers to the next
  segment (no spawn/despawn pop); carried `jitter` keeps rotation stable along a straight run.
- **Rotation must be re-sent on change**: `updatePosition` sends position only; rotation is
  entity metadata (sent at spawn). `setRotation` flags `rotationDirty`; `renderAll` pushes
  `updateMetadata` to viewers only when dirty (slope→flat, corner turns) — else the slope
  tilt sticks on a flat belt.

### Geometry (`ConveyorMath`)
- `BELT_TOP_Y = 0.28`; slopes add `slopeLift = 0.5` so items ride ON the ramp.
- `itemRotation(stepX, stepZ, slopeStepY)` = lay flat (−90° X) + ramp pitch (up +45/down −45) + yaw.
- Render path = **edge → centre → exit** (50/50 at progress 0.5):
  - `center.y = (startRel.y + exitRel.y)/2` (midpoint — must include slopeLift, else the item
    sinks mid-ramp).
  - In-line item → straight line through centre (unchanged).
  - **T-junction/corner**: perpendicular hand-off enters from the SIDE edge
    (`entryDir = sourceFacing.opposite()`), goes edge→centre then centre→its own exit,
    rotating toward the new direction at centre. No teleport-to-start.

### Output / receivers (`ConveyorReceiver`)
- Interface: `boolean isFull()` + `boolean receiveConveyorItem(ItemStack, Direction sourceFacing)`.
  `ConveyorBlockEntity` implements it (`isFull = !backHasRoom()`).
- `tryHandOff` order: (1) conveyor→conveyor rich path; (2) generic ConveyorReceiver — if
  `isFull()` STALL (don't drop) else receive; (3) nothing ahead → drop off the end.
- **Backpressure**: a full receiver stalls the front item at 1.0 → followers queue → the
  whole 1×1 line stops until it frees. Per-item, automatic.
- The mergers/splitters (`conveyor_merger.yml`, `conveyor_splitter.yml`) build on this.

### Break (`ConveyorBreakListener` + `onBroken`)
- Teardown runs from a Bukkit `BlockBreakEvent` (MONITOR, ignoreCancelled) because CE's
  `affectNeighborsAfterRemoval` is unreliable for creative breaks. A static `LISTENER_HANDLED`
  set dedupes vs the behavior callback (still covers pistons).
- Topology: START broken → downstream becomes new START (−1); END → upstream becomes new
  END (−1); **2-segment belt → breaking either destroys both**; MIDDLE → destroy the WHOLE
  belt (drop block items + carried), static `teardownInProgress` guard stops re-entrancy
  from the `remove(block,true)` cascade. All items drop, displays despawn, cell data cleared.

### Power
- The HEAD scans the 3 open horizontal sides (front/left/right) of BOTH ends for a
  `VaporMotorBlockEntity` and uses the stronger rpm — motor on either end, any side, no
  direction change. Bodies read `upstream.effectiveRpm()`.

---

## 4. Vapor motor + stress economy (Create-style)

Files: `src/main/java/dev/arubik/craftengine/rotation/`
- `RpmProvider` (`getRpm()`, `stressCapacity()`, `reportStressLoad(su)`),
  `VaporMotorBlockEntity`, `VaporMotorBehavior` (`polyfills:vapor_motor`). There is also an
  `advanced_vapor_motor` block (host fixed to `auto_state: solid` during migration).

### Economy
- RPM = belt speed. Per-segment stress = `stress-impact × (motorRpm / base-rpm)` — SU per
  item **scales with speed** (2× rpm = 2× speed AND 2× SU).
- Head sums line stress (`segments × per-segment SU`); if > motor `stress-capacity` →
  OVERSTRESSED, line stalls.
- Motor vapor demand = `vapor-per-tick + load × vapor-per-stress`; rpm scales with the
  satisfied fraction of demand.
- Config (`vapor_motor.yml`): `vapor-capacity: 10000`, `vapor-per-tick: 20`, `max-rpm: 128`,
  `stress-capacity: 256`, `vapor-per-stress: 1`.

### Motor UI (right-click)
- 9-slot dispenser menu (`MachineLayout` + `addButton`/`setDynamicProvider`, clicks via
  `MachineMenuListener`). Row 0 = live stats (vapor stored/demand, current/max rpm, stress
  load/capacity). Row 2 = `−/+ RPM` (±16) and `−/+ Stress` (±32 SU); tuned values clamp +
  persist (`KEY_MAX_RPM`, `KEY_STRESS_CAP`). `MachineBlockBehavior.useWithoutItem` opens the menu.

---

## 5. Aluminum chain + other polyfill blocks

- **Aluminum** (`item/polyfills/aluminum.yml`): `cml:aluminum_ore`, `cml:aluminum_scraping`,
  `cml:purified_aluminum_dust`, `cml:aluminum_ingot`. Crusher recipe
  `src/main/resources/recipes/crusher_aluminum.json` (`cml:aluminum_ore` → 2× scraping
  guaranteed + 1× at 0.25 chance). The "aluminum epic/sprint" continues from here
  (further processing steps / blocks were in progress).
- **Crusher** (`crusher.yml`, `polyfills:crusher`) — machine consuming RPM+SU; recipes via
  the JSON `RecipeManager` (`type: crusher`, `rpm`, `su`, weighted `outputs` with `chance`).
- **Depot** (`depot.yml`) + **workbench** (`workbench.yml`) — both present; workbench is a
  real 2-wide double block via `HorizontalDoubleBlockBehavior` (LEFT master + RIGHT auto-placed
  at `facing.clockWise()`; aborts if the right cell is blocked); 4-row 6-wide chest UI, 3×2
  input grid, 2 outputs, 1 tool slot (durability), tool-gated recipes. (New workbench model +
  a **funnel** were on the TODO list.)
- **Creative gas tank** (`creative_gas_tank.yml`) — infinite STEAM source, pushes to 6 neighbours.
- **Upgradeable furnace** (`upgradeable_furnace.yml`) — sample machine; right-click opens menu;
  upgrades sugar=SPEED / redstone=EFFICIENCY / glowstone=YIELD.
- **JIT crafting table** (`crafting_table.yml`) — N×M crafting UI framework (`AbstractCraftingMenu`,
  `SlotLayout`, shaped/shapeless, multi-output).
- Blueprints/upgrades/wire/gears/brass items under `item/polyfills/`.

---

## 6. Pipes & exact hitboxes (chorus_plant)

- Pipe blocks (`block/machines.yml` in modern) need exact per-connection collision. Their
  collision is shaped like `minecraft:chorus_plant[down,east,north,south,up,west]` (a centre
  column + arms toward connected sides).
- The original author lazily used `chorus_plant:0` for all 63 pipe appearances (legacy
  `block:meta` notation, invalid in 26.6.2) → state-allocation errors. Fix applied:
  1. Uncommented the 63 `chorus_plant[...]` entries in `internal/mappings.yml` (releases the
     connection-shaped states for custom use).
  2. Rewrote each `pipe_<SWNEUD>` appearance to an explicit `state: minecraft:chorus_plant[...]`
     decoded from its connection-char name (char order **S,W,N,E,U,D**; `c`=connected/true,
     `n`=none/false).
  3. Full-block machines (pump, core) stay `auto_state: solid`.

---

## 7. Util layer

- `util.MNms` — direct-NMS shim (`constructor$X`, `method$X`, `field$X`).
- `util.NmsBlockBehavior` — `(Object,Object[])` → typed callback bridge.
- `util.Utils` — `getAsInt/getAsDouble/getAsFloat(Object value, String name)`.
- `util.CustomBlockData` / `TypedKey` / `TypedKeys` / `CustomDataType` /
  `ArrayItemStackWithSlot` — chunk-backed persistence stack (§2).
- `block.entity.BukkitBlockEntityTypes.getIfLoaded(Level, BlockPos)` — null-guarded BE lookup.
- `machine.block.MachineBlockBehavior` / `machine.block.entity.AbstractMachineBlockEntity` —
  reusable machine base (IO config, gas/fluid carriers, menu, ticking; null-guarded for the
  config-parse-time `(null,null)` calls).
- `machine.recipe.loader.RecipeManager` — loads the plugin's JSON machine recipes
  (`custom_item`/`item`/`gas`/`fluid`/`xp` inputs+outputs, weighted `chance`).
- `crafting.StationRecipeLoader` — workbench recipes.

---

## 8. Known footguns

- A fresh BE has no `level`/`world` until it ticks → do CustomBlockData I/O in/after the
  first `serverTick`, never in the constructor.
- `> server-run.log` may not truncate if a previous server lingers — verify by timestamp.
- `serverside-blocks` + `mappings.yml` changes need a **restart** (not reload).
- Resource-pack changes require the client to re-accept the pack (new hash) on reconnect.
- Pre-existing `modern` warnings (furniture `variant:` format, chicken/`cluckshroom` recipe,
  carved_wood, farming) are NOT from the polyfills migration — separate 26.6.2 format updates.

---

## 9. Outstanding / TODO (from the session)

- Pre-existing `modern` cosmetic warnings: migrate furniture `replace_furniture`/right-click
  to the new `variant:` config section; fix the chicken/`cluckshroom` vanilla recipe; missing
  `wisteria`/`fresno` recipe items in carved_wood.
- New workbench model; a **funnel** block; finish the **aluminum** processing epic.
- `Math.random()`/`new Date()` are fine in plugin code (only restricted inside Workflow scripts).
