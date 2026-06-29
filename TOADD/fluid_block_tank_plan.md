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

## REVISED DESIGN — UNIFIED STORE (per user; supersedes the per-block equalization idea)
The group MUST behave as ONE tank: a pipe on ANY face of ANY member inserts/extracts the WHOLE group's
fluid, even if that member block is locally "empty". Per-block stores + engine equalization is NOT enough —
needs a true single store on a controller, every member routing to it.

DONE (committed): recipes on vanilla table (personal tank live), CraftingSamples deleted, Create textures
copied to pack as fluid_block_tank*.png, blockstate/model reference captured above. A first per-block
behavior was written then REMOVED (wrong model). Key constant POLYFILL_FLUID_BLOCK_TANK still in
BlockBehaviors (currently unused) for the rewrite.

Architecture:
- CONTROLLER = canonical member (min Y, then min X, min Z) of the connected group. Holds the combined
  FluidStack (FluidKeys.FLUID at controller pos). Capacity = memberCount × CAP_PER_BLOCK (8000).
- Dedicated BlockEntity per member; resolves group + controller via BFS over adjacent fluid_block_tank
  (6-dir, cap 4096). Recompute on place/break/neighborChanged.
- FluidCarrier on a member ROUTES to controller: getStored=controller store, getCapacity=group cap,
  insert/extract on controller store. Pipe touching any member sees the whole tank.
- FLUIDENGINE INTEGRATION (key): the whole group = ONE graph node, NOT one per member (else engine
  N×-counts). Change FluidGraphBuilder: when BFS visits a fluid_block_tank, resolve controller and use
  CONTROLLER pos as node identity; dedupe all members to it; internal member↔member adjacency = NO edge;
  external neighbors of ANY member = edge to the group node. makeNode group: cap=group cap, head=Y(ctrl)+fill.
  Register ONE seed per group (controller) per tick.
- LIQUID RENDER IN WINDOW: per-member blockstate props fluidtype (group type) + level (0..16). F=amount/cap,
  H=member rows, member vertical index k (0=bottom): surface=F*H; level=clamp16((F*H - k)*16). Window model
  overlays the fluid plane at `level` using the SAME water/lava/xp textures as copper_tank (personal tank).
  Recompute on every store change (onStoreChanged).
- Blockstate props: bottom, top, shape(plain/window/window_*), fluidtype, level.

Rewrite order:
  1. FluidBlockTankBehavior + BlockEntity: group BFS + controller election + unified FluidCarrier routing.
     Test insert/extract via a pipe on any face hits the group total.
  2. FluidGraphBuilder group-collapse (one node per group). Correct transport, no N× counting.
  3. Frame blockstate (bottom/top) + window fluid render (fluidtype/level per member from group fill).
  4. w≥2 corner window shapes (window_ne/nw/se/sw) to match Create.
  5. Config/models/lang + fluid_block_tank recipe (8 copper_plate + glass) into recipe/polyfills_tanks.yml.
GOTCHA: ImmutableBlockState.with needs Property<T extends Comparable<T>> + matching value — copy
TankBlockBehavior.updateShapeState (~line 408) for the exact property-set API (raw Property + s.with fails).

## HORIZONTAL CTM — next step (assets ready in TOADD/ctm_tiles/)
_connected.png = 64x64 = 4x4 CTM tilesheet. Border analysis (T/B/L/R lines per tile):
  row0: T-LR T-LR T-LR T-LR   row1: --LR --LR --LR ---R
  row2: TBLR -BLR -BLR -B-R   row3: TBLR TBLR TBLR TBLR
Rows = vertical state (DONE via per-position wall texture conn_top/middle/bottom). Columns intended for
horizontal but the L/R detection is noisy -> open the exported tiles in TOADD/ctm_tiles/ (tile_rY_cX.png +
_sheet_4x.png) and confirm which column = "connected left", "connected right", "connected both" by eye.

Approach (deterministic, no blockstate explosion, mirrors the vertical fix): the window_corner MODELS
(block_*_window_ne/nw/se/sw) already know which faces are INTERIOR (toward footprint neighbours). Edit each
corner model so its interior side faces use the horizontally-seamless wall tile (no L/R ridge) while the
exterior faces keep the ridged wall + window. The blockstate already picks the right corner model per
footprint position (windowShape), so no new states are needed. Slice the confirmed seamless tiles into
named 16x16 textures and set them as the per-face #1 on the interior faces of the corner models.
User's real complaint (screenshot): 3x3 corner/center render all faces -> verify interior faces are culled
/ use the seamless texture so the inside (fluid) reads cleanly.

## CORRECTION — _connected is NOT a 16-tile CTM grid
Viewing the sheet (TOADD/ctm_tiles/_sheet_4x.png) shows _connected.png (64x64) is a single CONNECTED-FACE
tank texture at 4x detail: 4 quadrants = the 4 corner pieces of one big window/frame spanning a 2x2 face,
plus panel/drawer trim. Create's window_corner models UV-map their window/wall faces into the matching
quadrant so a w×w face reads as ONE continuous window. So the earlier "slice 16 tiles" idea is wrong.

Correct horizontal-CTM step (intricate, do carefully, one model at a time + test):
- For each window_corner model (block_*_window_ne/nw/se/sw), point the window-plane texture (#5) at
  cml:block/fluid_block_tank_connected (the 64x64) and set that face's UV to the corner's 32x32 quadrant
  (nw=[0,0,32,32], ne=[32,0,64,32], sw=[0,32,32,64], se=[32,32,64,64]) so the 4 corners tile into one window.
- The plain WINDOW model (single-block face) keeps fluid_block_tank_window_single.
- Verify on a real 2x2 and 3x3 in-world before mass-applying.
DONE meanwhile: vertical CTM (per-position wall texture), stacked per-layer fluid render (ItemDisplay,
translate +0.5/axis to centre at 0.5/0.5 and lift), unified multiblock store, deterministic partition.

## PER-FACE DISPLAY-ENTITY SHELL (next — replaces the 24 blockstate variants; saves blockstates)
can-occlude:true did NOT cull interior faces (CraftEngine auto_state). Decision: render the shell via
display entities per exterior face; the block model becomes EMPTY (invisible) so interiors are see-through.

New ShellRender (mirror FluidTankRender lifecycle, keyed by controller -> List<UUID>):
- For each member cell of a group, for each of the 6 faces:
    * neighbour in that dir SAME group (owner map) -> INTERIOR -> skip (see-through).
    * else EXTERIOR -> spawn an ItemDisplay quad for that face.
- Face quad models (flat 16x16, 1px), 3 textures:
    * cml:block/fluid_shell/window  -> fluid_block_tank_window_single (exterior SIDE faces)
    * cml:block/fluid_shell/cap     -> fluid_block_tank_top           (exterior TOP/BOTTOM faces)
    * (optional wall for non-window side variants)
- Orient the north-facing quad to each dir via display rotation quaternion:
    NORTH=identity, SOUTH=Y180, EAST=Y-90, WEST=Y90, UP=X-90, DOWN=X90; translate to the face plane.
- CTM tile: pick from the 64x64 sheet (rows=vertical state by bottom/top, cols=horizontal by lateral
    same-group neighbours) -> the quad's UV maps that 16px tile; OR keep window_single for v1.
- Block config: replace the 24 frame variants with ONE empty/invisible model (keep collision via auto_state).
- Wire ShellRender.update(owner, ctrl) in refreshGroupFromOwner alongside FluidTankRender; remove in
    recomputeArea cleanup + Controller.onRemove (same as the fluid box).
