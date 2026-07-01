# Vanilla Contraptions — Architecture & Roadmap

Create-mod-style moving/rotating multi-block structures (miners, platforms, windmills)
built on vanilla-client-compatible tricks only — Display entities for visuals, invisible
entities for hitboxes, raw NBT for structure capture, no client mods.

> Requirements mined from `TODO.md`'s Gemini brainstorm transcript (Spanish, ~1300 lines,
> "Máquinas Móviles del Create Mod" thread). That file is prior art, not a maintained doc —
> **this file supersedes it** for contraption planning purposes. Left untouched, not deleted.

## 0. Current state

5 of 8 Phase-0 spikes resolved (§4) via static/code-level investigation — no architecture
blockers found, and NBT-capture compatibility (the second-highest risk) turned out cheaper
than expected: CraftEngine ships its own complete NbtIo-equivalent library. One shim landed
already: `MNms.constructor$ClientboundBlockDestructionPacket` (spike #4). No
`dev.arubik.craftengine.contraption` package exists yet — Phases 1+ haven't started. The 3
remaining open spikes (#1 entity budget, #3 Shulker-standing feel, #6 tick budget under
load) all need a human in a live client or load-testing against real Phase 3/5 code; they
can't be resolved from the editor alone.

## 1. Architecture summary

The brainstorm transcript explored and **rejected** a live "mirror dimension" (a second
real, ticking Bukkit `World`) — a 1:1 coordinate mirror breaks the instant a contraption
moves (can't cheaply re-place blocks every tick to keep it in sync), and one `World` per
contraption (or even one shared RAM world à la SlimeWorldManager) still drags in a full
`ServerLevel`'s entity trackers/chunk providers/tickers — too heavy for dozens of
simultaneous contraptions. It converged instead on the following (this is how Create
itself works under the hood, per the transcript's own research into Create's
`ContraptionWorld`/`VirtualRenderWorld`):

- **`ContraptionWorld`**: NOT a real `ServerLevel`. A lightweight in-RAM data-only proxy —
  `Map<BlockPos, BlockState>` + captured block-entity NBT blobs. No native ticking inside
  it; vanilla ticking is **suspended/frozen** the moment a structure becomes a contraption
  (ovens don't keep smelting mid-flight — this is the opposite of the transcript's very
  first idea, abandoned once the mirror-dimension problems became clear).
- **Structure detection**: explicit **glue** (user marks adjacency) rather than flood-fill
  from a clicked block — flood-fill would accidentally absorb a player's whole house if
  the contraption is built touching their base. The glue graph doubles as the fracture
  graph: if part of the assembled structure is destroyed at runtime, the graph splits into
  independent sub-contraptions.
- **Coordinates**: the **bearing/actuator block is local-space origin (0,0,0)**. Every
  captured block's position is a bearing-relative offset. Rotation-around-an-arbitrary
  point needs translate→rotate→translate-back per block per tick; rotating around the
  bearing is one `T_bearing + R·p_local` per block per tick — the only affordable option
  at hundreds of blocks × 20 TPS.
- **Capture**: read the **raw NBT** of each block/block-entity directly (`NbtIo`) rather
  than hand-serializing every container/tank — mirrors how
  `PersistentWorldlyBlockEntity.saveCustomData` already lets `ItemStack.CODEC` do the
  heavy lifting for single-block inventory persistence, just scaled up to a whole
  structure. **Caveat (Spike #5 below)**: CraftEngine's own custom blocks may store data
  in CE's own `CompoundTag` type (`net.momirealms.craftengine.libraries.nbt.CompoundTag`),
  which is almost certainly not the same type as vanilla `net.minecraft.nbt.CompoundTag` —
  this needs verifying before assuming raw `NbtIo` capture "just works" for CE blocks.
- **Rendering**: a packet-only Display-entity swarm (one `BLOCK_DISPLAY` per captured
  cell), transform-driven by the bearing's kinematic state, `interpolation_duration=1-2`
  ticks for client-side smoothing — reuses the exact pattern already proven by
  `ConveyorItemDisplay`.
- **Collision/hitbox (MVP choice)**: a **Shulker swarm** — packet-spawned invisible
  Shulker entities repositioned every tick to approximate the structure's surface.
  Rejected alternatives: grid-snapped real Barrier blocks (perfect collision, but movement
  locked to whole-block jumps — no intermediate angles) and full custom OBB physics via
  player-movement packet interception (mathematically perfect, but requires client
  cooperation a plugin doesn't have — only real mods with Mixins, like Create itself,
  can do this without constant rubberbanding).
- **Kinematics — single master clock**: ONE authoritative tick loop per contraption, NOT
  a ticker per component (the transcript explicitly walked back that idea after
  recognizing the race-condition risk). Contraption-capable blocks implement
  `MovementBehavior { void tick(MovementContext); boolean isStalled(); }`. Each tick: the
  master clock calls `tick()` on every active behavior, then polls `isStalled()` — if ANY
  reports stalled (e.g. a drill still cutting stone), the whole contraption's
  translation/rotation is multiplied by 0 for that tick (internal RPM/state keeps
  advancing — only geographic movement freezes) until all components un-stall.
- **Miners specifically**: a stalling state machine — accumulate mining damage per tick
  from RPM vs. vanilla block hardness, send block-crack-stage packets while stalled, call
  the real block-break (synchronously, main thread) once damage exceeds hardness, then
  un-stall. Drops go straight into the contraption's own virtual inventory — never spawned
  as real dropped-item entities.
- **World interaction boundary**: a `ContraptionAccessor` is the ONLY class allowed to
  touch the real Overworld (break/place blocks, pull fluids from world blocks) — always
  synchronously, main-thread only. An earlier idea (simulate on a background thread, queue
  world mutations back to main thread) was explicitly rejected: Bukkit/Paper's
  thread-safety model makes async world mutation a non-starter, and this is in fact how
  Create itself works (all mechanical logic runs on the main thread; only render-mesh
  baking is offloaded).
- **Items — no entity mirroring**: an item/entity that touches the contraption's bounds is
  converted to pure in-memory data (a `VirtualItem`) and the real Bukkit entity is
  removed — it is never live in two places at once. It's rendered back to the player as
  an `ItemDisplay` hologram tied to the contraption's transform, and only becomes a real
  `ItemEntity` again when it's "expelled" (falls off the end of a conveyor-like component,
  etc.). This "absorb into data → render as hologram → expel back to reality" pattern is
  what prevents item duplication (a live 1:1 entity mirror was explicitly rejected in the
  transcript for exactly this dupe risk).
- **Internal networks (fluid/gas/RPM) while assembled**: deferred past MVP (see §6) — each
  contraption would get its own isolated subgraph (reusing `FluidGraphBuilder`/
  `FluidNetworkSolver`/`FluidEngine`), with a temporary bridge edge injected only while a
  contraption's "port" block aligns exactly with a real-world pipe/tank ("docking").
- **Entity/data separation**: `ContraptionEntity` is a thin facade (transform, stalled
  flag, owned Display/hitbox entity ids) that looks up the real state — block map,
  inventory, network graphs — from a `ContraptionState` keyed by UUID, kept in a separate
  manager. Keeps network-sync payloads small and lets the "brain" survive entity
  despawn/respawn across a chunk unload/reload.
- **Persistence**: a `contraptions/` folder, one `.nbt` (heavy: bearing-relative captured
  block/BE data, `NbtIo.writeCompressed`) + one `.mcdata`/JSON manifest (light: UUID,
  world, absolute position+rotation, moving/stalled state, `.nbt` file reference) per
  contraption. The manifest is what's scanned on server boot to cheaply re-instantiate
  facades; the heavy `.nbt` only loads into RAM when a contraption actually needs to
  simulate. Manifest saved on stop/interval/`onDisable` only — never every tick.
- **Assembly/disassembly — explicit 4-state machine**: Static → [glue-scan + NBT-capture +
  remove real blocks + spawn `ContraptionWorld`/Display-swarm/hitbox-swarm] → Dynamic
  (moving; internal networks tick if present; world interaction only via
  `ContraptionAccessor`) → [only when linear+angular velocity are exactly zero: round
  every local-space block position to the nearest integer world coordinate ("grid
  snapping"), collision-check each target cell, write blocks back + restore NBT, or eject
  conflicting cells as dropped items rather than silently destroying data] → Static.

## 2. Codebase patterns this builds on (confirmed present, reuse — don't reinvent)

| Need | Existing pattern | File |
|---|---|---|
| Packet-only Display entity, per-tick reposition, per-viewer tracking | `ConveyorItemDisplay` | `conveyor/ConveyorItemDisplay.java` |
| Batched multi-entity spawn/despawn (static cluster) | `FluidDisplayElement` | `fluid/render/FluidDisplayElement.java` |
| Fake invisible entity via packets (precedent only — static, CE-lifecycle-driven, not reusable as-is) | `ShulkerBoxHitboxElement` | `machine/render/element/ShulkerBoxHitboxElement.java` |
| Master tick-loop driver (seed registry + per-tick step, registered in `onEnable`) | `FluidEngine.tickAll` | `fluid/graph/FluidEngine.java` |
| Multi-block detection/assembly/disassembly, `REGISTRY` + click-listener discovery, capture-original-state-for-restore | `MultiBlockBehavior` / `MultiBlockSchema` / `MultiBlockPartBlockEntity` | `multiblock/` |
| CE-tag persistence (not chunk PDC), reusable typed serialization | `TypedKey` / `TypedKeys` / `NbtType` / `CustomDataType` (incl. `ITEM_CODEC_TYPE`/`ITEM_ARRAY_CODEC_TYPE`) | `util/` |
| Reflective NMS packet construction shim | `MNms` (currently only 4 entity-packet constructors — no block-destruction packet yet) | `util/MNms.java` |
| Bootstrap point for new listeners/tasks | `onEnable()` | `CraftEnginePolyfills.java` |

**Confirmed absent** (net-new work, no scaffolding): `Interaction` entity usage, any
structure/schematic capture, any secondary/virtual Bukkit `World`, continuous-angle
rotation math (existing multiblock rotation is 90°-step-only), any actual PacketEvents
packet interception (it's initialized but unused for that purpose today).

## 3. Package layout (not yet created)

```
dev.arubik.craftengine.contraption/
  ContraptionState.java          — the real owner of a contraption's data
  ContraptionManager.java        — UUID -> ContraptionState registry, chunk load/unload hooks
  ContraptionEngine.java         — master tick loop (mirrors FluidEngine.tickAll)
  ContraptionEntity.java         — thin facade (transform, stalled flag, owned entity ids)
  ContraptionWorld.java          — in-RAM BlockPos->BlockState + BE-NBT proxy (data only)
  ContraptionCapture.java        — glue-scan + NBT capture -> ContraptionNbt blob
  ContraptionAssembler.java      — the 4-state assemble/disassemble machine
  ContraptionAccessor.java       — the ONLY class allowed to touch the real Overworld
  GlueGraph.java                 — adjacency graph, fracture-on-break
  MovementBehavior.java          — tick(MovementContext) / isStalled()
  MovementContext.java
  behavior/
    LinearActuatorBehavior.java  — MVP: constant-velocity straight-line mover
    MinerBehavior.java           — MVP: stalling drill (hardness vs RPM accumulator)
  render/
    ContraptionDisplaySwarm.java — per-tick BLOCK_DISPLAY swarm (ConveyorItemDisplay-shaped)
    ContraptionHitboxSwarm.java  — per-tick Shulker swarm (ConveyorItemDisplay-shaped)
  persistence/
    ContraptionNbt.java          — NbtIo compress/decompress blob (CustomDataType-style)
    ContraptionManifest.java     — light .mcdata/JSON manifest
  glue/
    GlueToolListener.java        — right-click-to-glue tool (mirrors ConveyorWandListener)
```

## 4. Phase 0 — Spikes (run BEFORE any of the above is written)

Each is a throwaway test/command, not production code. Ordered by how hard they gate the
whole architecture. **5 of 8 resolved by static/code-level investigation** (marked below);
the remaining 3 need a human in a live client and can't be resolved from the editor.

1. **[OPEN — needs live testing] Packet-swarm entity budget at 20 Hz.** Spawn N packet-only `ITEM_DISPLAY`/`SHULKER`
   entities, reposition all every tick, measure server tick time + client jitter at
   N = 20/60/150. Determines max viable contraption size and hitbox granularity.
   **Highest risk in the whole feature.**
2. **[RESOLVED — low risk, better than expected]** CraftEngine ships its own complete,
   spec-compliant NBT library (`net.momirealms.craftengine.libraries.nbt`, verified via
   `javap` against the deployed `craft-engine.jar`): `Tag.write(DataOutput)` and the
   tag-type IDs match vanilla exactly (`TAG_COMPOUND_ID=10` etc.), and the `NBT` utility
   class exposes `readFile`/`writeFile`/`fromBytes`/`toBytes`/`readUnnamedTag`/
   `writeUnnamedTag` — a full NbtIo-equivalent toolkit, including file I/O (directly
   usable for the `contraptions/*.nbt` format, no need to hand-roll with vanilla `NbtIo`).
   Since NBT is one universal binary format, CE's own `CompoundTag` and vanilla's
   `net.minecraft.nbt.CompoundTag` are byte-compatible even though they're unrelated Java
   classes. `PersistentBlockEntity.serializeToBytes()`
   (`block/entity/PersistentBlockEntity.java:133`) already provides generic export for
   any CE-controlled block — it dumps the whole container regardless of which specific
   `TypedKey`s were used. **Gap found**: there's no matching `loadFromBytes(byte[])`
   counterpart yet (export exists, import doesn't) — a small Phase-1 addition, not a
   redesign. No new `exportAllKeys()`/`importAllKeys()` generic hook is needed.
3. **[OPEN — needs live testing] Shulker-swarm collision fidelity while moving.** Can a player stand on a
   repositioned-every-tick fake Shulker (teleport, not interpolated server-side) without
   falling through, at MVP linear speeds (~1-4 blocks/sec)? If not: fall back to
   push-only collision for MVP, or reposition hitboxes more often than the visual swarm.
4. **[RESOLVED]** `ClientboundBlockDestructionPacket` shim added to `MNms.java` —
   `constructor$ClientboundBlockDestructionPacket(int id, Object pos, int progress)`.
   This repo compiles directly against Mojang-mapped NMS (paperweight userdev, not
   reflection), so this was a two-line addition, not a feasibility question. Compiles
   clean. Ready for `MinerBehavior`'s crack-stage visuals.
5. **[RESOLVED]** `DisplayData.BlockDisplayData.BlockState` exists in the deployed
   `craft-engine.jar` (confirmed via `javap`), exact structural analog to
   `ItemDisplayData.ItemStack` already used by `ConveyorItemDisplay`/`FluidDisplayElement`.
   `ContraptionDisplaySwarm` can render real block appearances directly, no new metadata
   helper needed.
6. **[OPEN — needs live/load testing] Main-thread tick budget with inline synchronous
   block mutation.** Cost of running several contraptions' `ContraptionEngine.tickAll` in
   one server tick, including a miner's real block-break call, with several contraptions
   active simultaneously. Can't be meaningfully measured until Phase 3/5 code exists to
   load-test against.
7. **[RESOLVED — by design, not by reuse]** Checked `MultiBlockBehavior.disassemble`
   (`multiblock/MultiBlockBehavior.java:960`) as a candidate reusable pattern — it does
   **not** collision-check before restoring, because its parts are never actually absent
   from the world (always present as PART blocks mid-assembly), so nothing else could
   occupy their cells. That assumption doesn't hold for a contraption (its cells are
   genuinely empty in the real world while flying/assembled) — disassembly needs new
   logic: check each target cell's occupancy (`isAir()`/replaceable) before restore;
   eject via `Containers.dropItemStack` (the same helper already used by
   `MultiBlockBehavior.dropInventory`, `multiblock/MultiBlockBehavior.java:1074`) for any
   cell that's blocked, rather than overwrite silently.
8. **[RESOLVED]** Confirmed `ENTITY_COUNTER` (`conveyor/ConveyorItemDisplay.java:184-199`)
   is a plain `AtomicInteger`, used as an ordinary `int` nowhere assuming non-negativity —
   no real overflow/collision risk from a contraption swarm's higher allocation volume
   within any realistic single-server-uptime.

## 5. Phases 1–6 (after Phase 0 spikes resolve)

**Phase 1 — Data model & static capture.** `GlueGraph`, `glue/GlueToolListener`,
`ContraptionCapture` (bearing-relative capture, dual vanilla-NBT/CE-TypedKey path per
Spike #2's finding), `persistence/ContraptionNbt`, `ContraptionAssembler`'s assemble-half
+ restore-to-original-position reverse path. No movement, no rendering yet — prove the
capture/restore round-trip for a stationary structure. Builds on `MultiBlockSchema`'s
relative-offset idea and `MultiBlockBehavior.disassemble`'s restore-original-state logic.

**Phase 2 — `ContraptionWorld` + rendering (still stationary).** `ContraptionWorld`
(minimal read-only proxy, widen only if forced), `render/ContraptionDisplaySwarm`
(`ConveyorItemDisplay`-shaped, `BLOCK_DISPLAY` per cell), `ContraptionEntity`,
`ContraptionManager` (chunk load/unload hooks). Spawn as a hologram exactly where the
real blocks used to be, so any bug is obvious against nothing having moved.

**Phase 3 — Linear-only kinematics (MVP movement).** `MovementBehavior`/`MovementContext`,
`behavior/LinearActuatorBehavior`, `ContraptionEngine` (the `FluidEngine`-shaped master
loop, registered in `onEnable`), `persistence/ContraptionManifest`. Proves the stall-gate
math even with a single behavior in the list.

**Phase 4 — Shulker hitbox swarm, moving.** `render/ContraptionHitboxSwarm`
(`ConveyorItemDisplay`-shaped, not `ShulkerBoxHitboxElement`-shaped), granularity per
Spike #1's findings (likely one Shulker per exposed surface face-cell, not per block).

**Phase 5 — World-interaction boundary + Miner.** `ContraptionAccessor`,
`behavior/MinerBehavior` (stall accumulator vs. hardness, crack-stage packets via the
Spike #4 shim, real block-break on main thread, drops into the contraption's virtual
inventory). Builds on `MultiBlockBehavior.dropInventory`'s container-draining pattern
(inverted) and `FluidCarrier`'s small-interface style for the virtual inventory.

**Phase 6 — Full assemble/disassemble state machine.** `ContraptionAssembler` completed
end to end, a bearing block behavior registered via `BlockBehaviors.register()`, hammer-
style right-click trigger (mirrors `HammerAssembleListener`), `ContraptionManager` wired
to `ChunkLoadEvent`/`ChunkUnloadEvent`/`onDisable`.

## 6. MVP scope cut

Ship: a small (~20-30 block) miner — glue+hammer-assembled, NBT-captured, Display-swarm
rendered, coarse Shulker hitbox (push-only acceptable if Spike #3 says "standing on it"
isn't reliable), straight-line movement only, stall-based mining into its own virtual
inventory (real blocks touched only via `ContraptionAccessor`), disassemble-at-zero-
velocity-only with grid-snap + collision-check-or-eject. Persists (manifest + `.nbt`)
across a server restart while stationary. This exercises every architectural risk
(capture format, master clock, stall gating, world-interaction boundary, packet-swarm
cost, hitbox feel) with the smallest possible surface area.

## 7. Explicitly deferred (Phase 2+, not in the first shippable slice)

- Continuous rotation (windmills, rotating platforms) — transcript recommends linear
  translation ships first.
- Nested contraptions / scene-graph (a contraption riding another contraption).
- Docking/bridge-edge fluid & gas networks between a contraption's internal `FluidGraph`
  and the world's — needs genuinely new "dynamically add/remove one edge between two
  independently-built graphs" support in `FluidGraphBuilder`/`FluidNetworkSolver`/
  `FluidEngine`, none of which exists today (`FluidEngine.tickAll` rebuilds one graph per
  seed from scratch every pass). Real, separate engine work — not a small addition.
- Internal isolated fluid/gas/RPM networks in general, even without docking.
- Multiple simultaneous movement types combined (rotating AND translating at once).
- Non-miner contraption types (windmill, collector, piston-pushed platform) beyond the
  MVP miner.
