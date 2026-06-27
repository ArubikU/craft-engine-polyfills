# Custom Crafter — build plan (architecture B, confirmed)

Goal: replace the vanilla crafter with a CraftEngine custom block (`cml:custom_crafter`) so that a
damageable ingredient with `craft_remainder: hurt_and_break` (the hammers) is **damaged in its slot**
instead of being **ejected out the front** (vanilla behavior). Same crafter look/UI, 6-direction facing,
redstone-pulse trigger. Use CraftEngine's **native block-entity storage**, NOT the `CustomBlockData`
(Bukkit PDC) used by pipes.

## Confirmed decisions
- Architecture **B**: full custom reimplementation (no mixin possible from a Paper plugin).
- Placement: in CE, override the vanilla **`minecraft:crafter` item** so it places `cml:custom_crafter`
  (a CE custom block) with the crafter behavior + 6-direction property.
- Trigger: redstone pulse (like vanilla).
- UI/look: same as vanilla crafter (reuse `CrafterMenu`; render via crafter look).

## CraftEngine native block-entity storage (USE THIS, not PDC)
- Extend `net.momirealms.craftengine.core.block.entity.BlockEntityController`.
- Persist via overrides `saveCustomData(CompoundTag)` / `loadCustomData(CompoundTag)` (CE's per-block
  NBT, saved by CE's chunk block-entity system). `dev.arubik.craftengine.block.entity.PersistentBlockEntity`
  already wraps a `CompoundTag container` with these — extend it or mirror it.
- Tick via `createBlockEntityTicker(...)` + `BlockEntityController.createTickerHelper(...)` (see
  `MultiBlockMachineBlockEntity`/`PipeBehavior.Controller` for the ticker pattern).
- Reach the controller from a position: `BukkitBlockEntityTypes.getIfLoaded(level, pos).controller`.

## Vanilla crafter logic (decompiled, real source)
Decompiled to `/tmp/ceins2/dec/net/minecraft/world/level/block/{CrafterBlock,entity/CrafterBlockEntity}.java`
(re-run: `java -jar vineflower.jar ex dec` after `unzip paper-1.21.11.jar net/minecraft/world/level/block/CrafterBlock*.class ...`).

`CrafterBlock`:
- properties: `ORIENTATION` (FrontAndTop), `TRIGGERED` (bool), `CRAFTING` (bool).
- `neighborChanged`: on rising redstone edge → `scheduleTick(pos,this,4)` + set TRIGGERED=true + BE.setTriggered.
  on falling edge → TRIGGERED=false, CRAFTING=false.
- `tick` → `dispenseFrom`.
- `getAnalogOutputSignal` → `BE.getRedstoneSignal()` (count of filled-or-disabled slots).
- `useWithoutItem` → `player.openMenu(crafterBE)`.

`CrafterBlock.dispenseFrom` (THE craft, where the bug is):
```
CraftingInput input = crafterBE.asCraftInput();
Optional<RecipeHolder<CraftingRecipe>> r = RECIPE_CACHE.get(level, input);   // RecipeCache(10)
if (r.isEmpty()) { level.levelEvent(1050,pos,0); return; }                    // fail sound
ItemStack result = r.value().assemble(input, level.registryAccess());
// CrafterCraftEvent (bukkit) ...
crafterBE.setCraftingTicksRemaining(6); setBlock CRAFTING=true;
result.onCraftedBySystem(level);
dispenseItem(result);                                  // to front container (HopperBlockEntity.getContainerAt) or drop
for (rem : r.value().getRemainingItems(input)) if(!rem.isEmpty()) dispenseItem(rem);  // <-- ejects damaged hammer
crafterBE.getItems().forEach(s -> if(!s.isEmpty()) s.shrink(1));               // <-- empties the slot
crafterBE.setChanged();
```
`dispenseItem`: front = `HopperBlockEntity.getContainerAt(level, pos.relative(front))`; if container →
`HopperBlockEntity.addItem(...)`; else `DefaultDispenseItemBehavior.spawnItem` (drop) + levelEvent 1049/2010.

`CrafterBlockEntity` (extends RandomizableContainerBlockEntity implements CraftingContainer):
- 9 `ItemStack` items; `containerData` = 9 slot-states (0 enabled / 1 disabled) + index 9 = triggered.
- `asCraftInput()` (via CraftingContainer) builds the 3x3 `CraftingInput`.
- `createMenu` → `new CrafterMenu(id, inv, this, containerData)` (REUSE THIS for the UI).
- `getRedstoneSignal()` = count slots that are non-empty OR disabled.
- `setSlotState/isSlotDisabled/slotCanBeDisabled` (only empty slots can be disabled).
- save/load: `items` + `crafting_ticks_remaining` + `disabled_slots` (int[]) + `triggered`.
- `serverTick`: decrement craftingTicksRemaining; at 0 → CRAFTING=false.

## The fix (damage-in-slot) — replace the remainder/shrink block
Instead of `getRemainingItems → dispense` + `shrink(1) all`, do PER SLOT:
```
for each slot i with non-empty stack s:
  Key id = CraftEngineItems.getCustomItemId(s)   // or read s components
  CraftRemainder rem = craftRemainderOf(s)        // from CE item settings / minecraft component
  if (rem == hurt_and_break):
      damage s by rem.damage; if now broken -> set slot empty (+ break sound); else keep s in slot (damaged)
      // DO NOT shrink, DO NOT dispense
  else if (rem == fixed item):
      shrink s by 1; dispenseItem(remainderItemStack)   // e.g. bucket
  else:
      shrink s by 1                                       // normal consume
```
Read `craft_remainder` from the CE item definition (the hammers set `settings: craft_remainder: {type: hurt_and_break, damage: 1}`).
For a generic solution, also honor vanilla `getRemainingItems` for non-CE items (buckets etc): keep the
vanilla loop for stacks that are NOT CE-damageable-remainder, so cakes/buckets behave normally.

## File plan
1. `block/behavior/CustomCrafterBehavior.java` (CE BlockBehavior, factory key `polyfills:custom_crafter`):
   - 6-direction facing property; `neighborChanged` (redstone edge) → schedule craft; `useWithoutItem` →
     open menu; `getAnalogOutputSignal`; `createBlockEntityController` → CustomCrafterBlockEntity.
2. `block/entity/CustomCrafterBlockEntity.java` (extends BlockEntityController / PersistentBlockEntity):
   - 9 ItemStacks in the CompoundTag (saveCustomData/loadCustomData), disabled-slots, triggered,
     craftingTicksRemaining; `asCraftInput()`; `craft()` with the damage-in-slot logic above; serverTick.
   - Implement a vanilla `Container` (or wrap) so `CrafterMenu` + `HopperBlockEntity.addItem` work, OR
     bridge to CE's WorldlyContainer like the machines do.
3. Register `polyfills:custom_crafter` in `BlockBehaviors.java`.
4. Config:
   - `cml:custom_crafter` block: host states for the crafter LOOK (orientation/crafting/triggered ->
     model variants), behavior `polyfills:custom_crafter`, 6-direction.
   - Override the vanilla `minecraft:crafter` item so on place it puts `cml:custom_crafter`
     (CE vanilla-item override / `block: cml:custom_crafter` on the crafter item). VERIFY exact CE syntax.

## Open unknowns to resolve at build time
- Exact CE syntax to (a) override the vanilla `minecraft:crafter` item to place a custom block, and
  (b) whether a CE block can reuse the real crafter blockstates for rendering without vanilla creating
  its own `CrafterBlockEntity` (if it does, suppress/ignore it). If not feasible, ship a custom crafter
  model in the pack.
- How to open `CrafterMenu` against our CE block entity (needs a `Container` + `ContainerData`); confirm
  hopper IO (`HopperBlockEntity.getContainerAt`/`addItem`) sees our block as a container (the machines
  already bridge WorldlyContainer — reuse that approach).

## Notes
- Keep vanilla behavior for non-hammer remainders (buckets, cakes) — only change damageable craft_remainder.
- This is plugin-only (no mixin), so the WHOLE crafter is reimplemented; maintainability cost noted by the
  CE dev — acceptable per user.
