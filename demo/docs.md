# Demo configs + mechanics reference

CraftEngine block/item definitions for the polyfill features live in
`demo/configuration/blocks/`. They follow the CraftEngine pack format (same shape as
CraftEngine's bundled `default_assets/configuration/blocks/*.yml`). Drop them into your
CraftEngine pack's `configuration/` directory (or merge into your existing pack) and
supply the referenced models in your resource pack. Behavior keys are matched to the
registered polyfill behaviors:

| Block (demo id) | behavior `type` | properties read by the polyfill |
| --- | --- | --- |
| `demo:conveyor` | `polyfills:conveyor` | `facing` (4-direction), `slope` (string flat/up/down), `part` (string start/middle/end) |
| `demo:vapor_motor` | `polyfills:vapor_motor` | `facing` (4-direction) |
| `demo:crafting_table` | `polyfills:crafting_table` | — (args grid-width/height/title) |
| `demo:upgradeable_furnace` | `polyfills:upgradeable_furnace` | `facing` (4-direction) |

> Only `facing` uses a native enum type. CraftEngine has no generic enum property, so
> `slope`/`part` are `string` properties with an explicit `values` list — the polyfill
> reads them by value name (`flat`/`up`/`down`, `start`/`middle`/`end`).

---

## Conveyor belt

### Does it render the moving item?
**Yes.** Each belt segment spawns a server-side fake `minecraft:item_display`
(`ConveyorItemDisplay`) and broadcasts spawn/move/despawn packets to players tracking the
chunk. The displayed item is the belt's slot content; its position is interpolated from
the segment's **start point** to its **end point** every tick (client-side smooth lerp).
No real entity exists on the server world — it's packet-only.

- Display scale: **0.5** (item sits on the belt top).
- Client interpolation: ~**1 tick** position lerp (matches the server tick cadence).
- Belt-top height (block-relative Y): `BELT_TOP_Y = 0.95`.

### Animation & direction
Two independent motions:
1. **Belt surface (animated texture):** `belt_top.png` is a vertical strip of frames
   (`belt_top.png.mcmeta` scrolls it). This is a **fixed client-side scroll** — it does
   **not** read RPM, so a stalled belt still visually scrolls unless you supply a static
   texture. The scroll runs along the belt length (the +Z axis of the base model).
2. **Carried item (server-driven):** the `item_display` is moved start→end each tick by
   the actual speed (RPM); when RPM is 0 it doesn't advance. This is the truthful motion.

**Direction** is handled entirely by the model's `facing` rotation: each `facing` value
maps to a `y:` rotation (north 0 / east 90 / south 180 / west 270) that rotates the whole
model — including the animated top face — so the visible tread always scrolls toward the
belt's travel direction. `up`/`down` models tilt the surface along the 45° ramp, so the
animation climbs/descends with it. Author `belt_top.png` so its frames advance toward +Z
(the exit side of the base/north model); every other facing/slope inherits the correct
direction via rotation. No per-facing texture variants are needed.

### Speed / translation metrics
Movement is RPM-driven. Constants (in `ConveyorBlockEntity` / `ConveyorMath`):

| Constant | Value | Meaning |
| --- | --- | --- |
| `BASE_RPM` | `64` | RPM at which the belt runs at base speed |
| `BASE_TRAVEL_TICKS` | `16` | ticks to cross ONE segment at `BASE_RPM` |
| `MAX_LENGTH` | `64` | max segments per belt line |
| `PICKUP_INTERVAL` | `5` | ticks between dropped-item pickup scans |
| `PICKUP_RADIUS` | `0.75` | scan radius (blocks) around the segment center |

**Progress per tick** (fraction of a segment advanced each tick), from
`ConveyorMath.progressPerTick(rpm, baseRpm, baseTravelTicks)`:

```
ticksToCross = baseTravelTicks * (baseRpm / rpm)   # clamped to >= 1
progressPerTick = 1 / ticksToCross                 # 0 when rpm <= 0 (belt stalled)
```

So higher RPM ⇒ fewer ticks to cross ⇒ faster. At `rpm = BASE_RPM` a segment takes
`BASE_TRAVEL_TICKS` (16) ticks; at `rpm = 128` it takes 8 ticks; `rpm = 0` ⇒ stalled.
An item reaches the end when accumulated `progress >= 1`, then transfers to the next
segment (or drops).

### Slope geometry (45° only)
`slope` ∈ {flat, up, down}. The render **start/end points** ramp the Y by ±0.5 each so the
item visibly climbs/descends across the segment (`ConveyorMath.startPoint/endPoint`):

- `flat` — constant Y (`BELT_TOP_Y`).
- `up` — entry low, exit high (+1 block over the segment).
- `down` — entry high, exit low (−1 block).

A sloped belt line therefore steps ±1 block of elevation per segment (a 45° staircase).

### The slot (chest-like)
Each segment is a **1-slot** container (`PersistentWorldlyBlockEntity`, size 1):

- Holds up to **one full stack of one item type**.
- **Hoppers** insert (empty/same-type) and extract on **all faces**.
- **Right-click**: empty hand → take the slot stack; holding a (non-conveyor) item → put/merge/swap into the slot.
- **Auto-pickup**: every 5 ticks, if the slot is empty, a nearby dropped item (≤0.75 blocks) is pulled into the slot.
- On movement, the **whole stack** travels to the next segment's slot; if it can't be accepted (no next belt / full) it is **dropped** at the end point.
- **Breaking** a segment **drops its slot**. Breaking a non-tail segment tears down the whole belt (each segment drops its slot); breaking the tail (`part=end`) just shortens.

### Placement — the wand (2 points)
There is **no separate "head" block**. The "head" is just the segment adjacent to the motor.

1. Hold the **conveyor item** in the main hand.
2. **Left-click** block **A** (start), then **left-click** block **B** (end).
3. The line places automatically if **all** of these hold:
   - A→B is a straight run along **one** horizontal axis (N/S or E/W);
   - if B is higher/lower, the elevation change is a **consistent 45°** (each step ±1 Y);
   - every cell along the path is air/replaceable (nothing in the way);
   - a **`demo:vapor_motor`** sits on a neighbor of **A**, facing **into A** (powers the head).
4. On success each segment is placed + linked with the right `facing`/`slope`/`part`
   (START at A, END at B, MIDDLE between). Failures send a red chat message; the selection
   resets on each second click.

> Point **A must be an empty (replaceable) cell** — the START segment is placed there;
> the motor goes in the block adjacent to A, looking at A.

### Powering it
`demo:vapor_motor` consumes **vapor** (gas `STEAM`) to produce RPM and pushes it to the
single block in its facing direction (the belt head). RPM then propagates down the belt
by reference (no per-tick global rescan). Feed the motor steam via the gas subsystem.
Motor config: `vapor-capacity`, `vapor-per-tick`, `max-rpm` (see `vapor_motor.yml`).

---

## Upgradeable furnace
Sample machine proving the reusable upgrade-module system. Slots: input, output, fuel,
and **3 upgrade slots**. Drop upgrade items into the upgrade slots:

| Item | Effect | Per item | Max |
| --- | --- | --- | --- |
| `minecraft:sugar` | SPEED (progress/tick) | +1× | 3 |
| `minecraft:redstone` | EFFICIENCY (less fuel) | −20% fuel | 3 |
| `minecraft:glowstone_dust` | YIELD (bonus output) | +0.5 expected | ∞ |

Aggregation is capped (speed ≤ 16×, fuel ≥ 10%). Smelting recipes are loaded by the
polyfill `RecipeManager` under machine id `upgradeable_furnace`.

---

## JIT crafting table
Right-click `demo:crafting_table` opens an N×M crafting GUI (default 3×3). Place inputs →
the matched result previews in the output slot → take it to consume one of each input.
Supports shaped + shapeless + **multiple outputs**; shift-click bulk-crafts bounded by both
input availability and free inventory space (never overflows/voids). Recipes are registered
in code (`CraftingSamples` / `CraftingRecipeRegistry.register(width, height, recipe)`); a
5×5 table = `grid-width: 5 / grid-height: 5` + 5×5 recipes. The framework
(`AbstractCraftingMenu` + `SlotLayout`) lets a dev define a custom crafting block with its
own input/output/background/CUSTOM (e.g. fuel/fluid) slots — see the `Forge` sample
(2×2 + fuel slot).

---

## Caveats (need in-world verification)
- Models / `auto_state` here are placeholders (`minecraft:block/custom/...`, `auto_state: solid`).
  Supply real models + pick a base state that fits your CraftEngine version; the polyfill
  only reads the listed **properties**, it does not ship assets.
- `slope`/`part` have no model variants by default — add `variants` entries if you want
  distinct ramp / end-cap models.
- The conveyor `extend` re-places blocks with `UPDATE_ALL`; if you see state/slot loss on
  growth, lower the update flag. (Validated to load + enable cleanly; full in-world item
  motion / hopper / pickup behavior should be confirmed on your server with these assets.)
