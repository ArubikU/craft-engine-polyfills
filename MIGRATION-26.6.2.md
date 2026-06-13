# Migration: craft-engine 0.0.66.26 → 26.6.2

Status: **compiles clean, tests pass, `shadowJar` builds**. The migration was a major API
rewrite, not a version bump. It has **not been runtime-tested on a live Paper 1.21.11 server** —
see the checklist at the bottom before shipping.

## Dependency changes
- `craftengine_version` 0.0.66.26 → **26.6.2**; `nms_helper_version` 1.0.163 → **1.0.212** (`gradle.properties`).
- Switched from the local `libs/craft-engine-paper-plugin-*.jar` to the Maven artifacts
  `net.momirealms:craft-engine-core` + `craft-engine-bukkit` (`build.gradle.kts`).

## Major API changes handled
| Old (0.0.66.26) | New (26.6.2) |
| --- | --- |
| `core.block.CustomBlock` | `core.block.BlockDefinition` |
| `core.block.properties.*` | `core.block.property.*` |
| `core.block.UpdateOption` / `.Flags` | `core.block.UpdateFlags` (int constants) |
| `core.item.context.*` | `core.world.context.*` |
| `core.util.HorizontalDirection` | `core.util.Direction` |
| `behavior.EntityBlockBehavior` | `behavior.EntityBlock` |
| `World.serverWorld()` | `World.minecraftWorld()` |
| behavior field `this.customBlock` | `this.block()` |
| `ResourceConfigUtils.getAs*` / `MiscUtils.getAsStringList` | `util.Utils.getAs*` (added) |
| `bukkit.plugin.reflection.minecraft.*` + `FastNMS.INSTANCE.method$*` | **removed**; direct NMS via `util.MNms` |
| `ItemBehaviorFactory.create(Pack,Path,String,Key,Map)` | `create(Pack,Path,Key,ConfigSection)` |
| `CustomItem.buildItemStack()` / `.behaviors()` | `BukkitItemDefinition.buildBukkitItem()` / `ItemBehavior.let(...)` |
| `LocalizedResourceConfigException` | `KnownResourceException` |

### Behavior callback convention
All block-behavior callbacks moved to the uniform `(Object thisBlock, Object[] args)` shape; argument
positions come from the `BukkitBlockBehavior` `<callback>$<name>` index constants. The trailing
`Callable superMethod` argument is gone (`super.x(tb, args)`).

### Block-entity redesign (largest change)
`core.block.entity.BlockEntity` is now **final**. Custom entities extend
`BlockEntityController` (composition) instead of subclassing `BlockEntity`. The machine + multiblock
hierarchy was re-based accordingly; new adapters: `block/entity/PersistentController.java`,
`machine/block/entity/AbstractMachineController.java`. Behaviors implement `EntityBlock` and override
`createBlockEntityController(BlockEntity)`.

## New helper classes
- `util.MNms` — direct Mojang-mapped NMS shim replacing the removed reflective bridge; method names
  mirror the old `method$/field$/constructor$` members so call sites only swapped `FastNMS.INSTANCE`→`MNms.INSTANCE`.
- `util.Utils` — config-coercion (`getAs*`, `require*`) + direction/NMS conversion helpers.

## Correctness fixes from post-migration review
- `NmsBlockBehavior.fallOn` — corrected arg order to vanilla `(level, state, pos, entity, fallDistance)`
  (previously swapped pos/state and cast an Entity/Number to BlockState/FallingBlockEntity → CCE).
- `PersistentWorldlyBlockEntity` — now serializes its `inventory` array in `saveCustomData/loadCustomData`
  (was silently dropped → machine slot item loss on unload/restart).
- `ItemListener` SLOT_CHANGE — passed the newItem ItemStack where the new-slot `int` was expected (CCE); fixed to `args[6]`.
- `PersistentBlockEntity`/`PersistentWorldlyBlockEntity.getBlockBehavior(Class)` — inverted `isInstance`
  always returned null; fixed to `clazz.isInstance(behavior())`.
- Removed per-render console debug logging in `EnchantmentUpgrade`; dropped dead imports and a
  per-tick `Direction.valueOf` round-trip in the fluid/gas transfer helpers.

## Deferred (recommended, not done — would benefit from runtime tests first)
- **Modularity:** `fluid/behavior/PipeBehavior` ≈ `gas/behavior/GasPipeBehavior` and
  `FluidTransferHelper` ≈ `GasTransferHelper` are near-duplicate parallel hierarchies. Extract a generic
  `AbstractCarrierPipeBehavior` / `CarrierTransferHelper` parameterized by a resource adapter.
- **Optimization:** `tickPipe()` re-fetches the block entity / stack 3–4× per tick; fetch once and thread through.
- Consolidate the repeated `new BukkitWorld(((ServerLevel) level).getWorld())` idiom and the
  direction→offset switch (duplicated in Utils + the pipe/transfer classes) into one helper.

## Runtime verification checklist (before shipping)
- [ ] Plugin loads against CraftEngine 26.6.2 on Paper 1.21.11.
- [ ] Custom block behaviors fire correctly (place/tick/neighbor/fallOn) — the `args[]` index mapping is the highest runtime risk.
- [ ] Machine/multiblock block entities persist inventory + state across chunk unload and restart.
- [ ] Item behaviors (slot change, enchantment upgrade, fake thorns) trigger as before.
- [ ] Shulker hitbox render element spawns/despawns correctly.
