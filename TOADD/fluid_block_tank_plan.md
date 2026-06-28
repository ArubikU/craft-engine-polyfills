# Fluid Block Tank — Create-style multiblock (implementation spec)

Goal: a new block `cml:fluid_block_tank` that behaves like Create's `FluidTankBlock` — 1×1×1 blocks
that auto-merge into a rectangular prism (footprint w×w, w∈{1,2,3}, height unlimited) forming ONE logical
tank with combined capacity. Controller = bottom corner block; others reference it. NOT hammer-assembly —
it's dynamic adjacency merge (recompute on place/break).

Sibling task DONE: existing fluid tank (`cml:copper_tank`) display renamed → "Fluid Personal Tank"
(machines_lang.yml; id unchanged). Its recipe (to add): 1 crystal center + 8 copper ingots.

## Item IDs (verify before use)
- copper plate = `cml:copper_plate` (confirmed in lang)
- fluid pipe   = `cml:copper_pipe` (the fluid pipe; base item — has connection variants copper_pipe_cccccc…)
- copper ingot = `cml:copper_ingot` OR `minecraft:copper_ingot` — VERIFY (grep found aluminum_ingot only)
- crystal      = VERIFY id (referenced in machine configs; find exact `cml:*crystal*`)

## Recipes (CraftingSamples.java-style: registry.register(3,3, CraftingRecipe.shaped(...)...))
- Fluid Personal Tank (`cml:copper_tank`): 8 copper ingots ring + 1 crystal center.
    row("III") row("ICI") row("III") define I=copper_ingot, C=crystal -> copper_tank
- Fluid Block Tank unit (`cml:fluid_block_tank`): 8 copper plates ring + 1 copper_pipe center.
    row("PPP") row("PEP") row("PPP") define P=copper_plate, E=copper_pipe -> fluid_block_tank
  Find where REAL recipes register (RecipeManager.loadRecipes / WorkbenchSamples), not the samples file.

## Blockstate (copy of Create create:fluid_tank — VERIFIED via raw github)
Properties: `bottom`(bool), `top`(bool), `shape` ∈ {plain, window, window_ne, window_nw, window_se, window_sw}.
24 variants = 2 bottom × 2 top × 6 shape. Model = `block/fluid_block_tank/block_<pos>[_<shape>]` where
pos = (bottom?,top?): (T,T)->single, (F,T)->top, (F,F)->middle, (T,F)->bottom; shape plain->"" else _<shape>.
Full variant→model table (Create's, rename create:->cml: and fluid_tank->fluid_block_tank):
  bottom=F,top=F: plain=block_middle, window=block_middle_window, window_ne/nw/se/sw=block_middle_window_*
  bottom=F,top=T: block_top[_window[_*]]
  bottom=T,top=F: block_bottom[_window[_*]]
  bottom=T,top=T: block_single[_window[_*]]

## Models (24 JSON, copy from Create models/block/fluid_tank/, rename namespace to cml + fluid_block_tank)
block_{single,top,middle,bottom} × {"", _window, _window_ne, _window_nw, _window_se, _window_sw}.json
Example (block_single_window.json) structure: parent "block/block"; textures:
  "0": cml:block/fluid_block_tank_top
  "1": cml:block/fluid_block_tank
  "4": cml:block/fluid_block_tank_inner
  "5": cml:block/fluid_block_tank_window_single   (corner variants use fluid_block_tank_window)
  particle: cml:block/fluid_block_tank
Elements: Lid (y12-16), 4×SideRight + 4×SideLeft frame strips (y4-12), 4×Window planes (#5), Bottom (y0-4).
Window planes only present on the relevant faces per shape variant (interior column = all 4; corner = subset).

## Textures (BINARY PNG — USER MUST DROP these from Create assets/create/textures/block/):
  fluid_tank.png, fluid_tank_top.png, fluid_tank_inner.png, fluid_tank_window_single.png,
  fluid_tank_window.png  (+ any fluid_tank_window_* if present)
Rename to fluid_block_tank*.png under resourcepack/assets/cml/textures/block/. WebFetch cannot fetch binary.

## Java behavior: FluidBlockTankBehavior (src/main/java/.../fluid/behavior/)
- extends ConnectableBlockBehavior implements FluidCarrier, EntityBlock (mirror TankBlockBehavior).
- Register: BlockBehaviors.java -> Key.of("polyfills:fluid_block_tank") -> FACTORY.
- Properties: bottom, top, shape (match blockstate). Plus the FluidCarrier store (CustomBlockData FLUID key
  on the CONTROLLER only).
- MERGE ALGORITHM (Create):
  * A tank group = maximal rectangular prism of fluid_block_tank blocks, footprint w×w (w≤3), height H.
  * Controller = the min-corner (lowest Y, then min X, min Z) block of the group.
  * Capacity = w*w*H * CAP_PER_BLOCK (Create CAP_PER_BLOCK = 8000 mB; pick a value, e.g. 8000).
  * Fluid stored on the controller; non-controllers are passive (getStored reads controller).
  * On place/break/neighborChanged: recompute the group from any member (BFS within footprint+vertical),
    elect controller, sum capacity, keep total fluid ≤ capacity (clamp/redistribute), update each block's
    bottom/top/shape blockstate.
- BLOCKSTATE per block:
  * bottom = (no fluid_block_tank directly below in same group) ; top = (none directly above).
  * shape = window if this block is on the group's OUTER edge of the footprint AND faces outward; the
    window corner (ne/nw/se/sw) depends on which footprint corner/edge the block sits on. For w=1 every
    block is window (single column shows window on all 4 sides -> shape=window). For w≥2 edge blocks get
    window_<corner>; interior (none, w=3 center) get plain.
    (Match Create exactly later; for a first cut w=1 -> window, else plain is acceptable.)
- FLUID TRANSPORT: it's a FluidCarrier, so the hydraulic FluidEngine already moves fluid in/out. Register
  the controller as a FluidEngine seed each tick (like TankBlockBehavior). getCapacity returns the GROUP
  capacity; getStored/setStoredRaw operate on the controller's store. onStoreChanged updates the fill
  blockstate/window tint if modeled.
- Shift-click readout: reuse TankBlockBehavior.fluidInfo(stored, groupCapacity, y).

## Config (testserver pack — gitignored, lives only in testserver/)
- block + item def: new file modern/configuration/block/polyfills/fluid_block_tank.yml (mirror copper_tank
  block def in machines.yml:2381). item-name <lang:item.cml.fluid_block_tank>.
- blockstate variants (24) -> the model paths above.
- lang: item.cml.fluid_block_tank = "Fluid Block Tank" (en) / "Tanque de Bloque de Fluidos" (es) in
  machines_lang.yml (all locale blocks).

## Order of implementation
1. Recipes (both) + verify item ids — quick, testable.
2. Models (24) + textures (user drops PNG) + blockstate + block/item config — visual block placeable.
3. FluidBlockTankBehavior Java: start with w=1 vertical-column merge (shape=window), controller+capacity,
   FluidEngine seed. Build + test stacking 1×1×N.
4. Extend merge to w×w footprint (2×2, 3×3) + corner window shapes. Build + test.
5. Polish: window fill tint, light, validation of non-square/oversize (reject >3 footprint like Create).

Reference (Create mc1.21.1): blockstates/fluid_tank.json, models/block/fluid_tank/*, textures/block/fluid_tank*,
content/fluids/tank/FluidTankBlock.java + FluidTankBlockEntity.java (merge/controller logic).
