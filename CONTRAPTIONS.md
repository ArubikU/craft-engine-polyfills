# Vanilla Contraptions — Architecture & Roadmap

Create-mod-style moving/rotating multi-block structures (miners, platforms, windmills)
built on vanilla-client-compatible tricks only — Display entities for visuals, invisible
entities for hitboxes, raw NBT for structure capture, no client mods.

> Requirements mined from `TODO.md`'s Gemini brainstorm transcript (Spanish, ~1300 lines,
> "Máquinas Móviles del Create Mod" thread). That file is prior art, not a maintained doc —
> **this file supersedes it** for contraption planning purposes. Left untouched, not deleted.

## 0. Current state

5 of 8 Phase-0 spikes resolved (§4) via static/code-level investigation. Phases 1-4 are now
implemented and unit-tested (45 tests, `dev.arubik.craftengine.contraption` package):

- **Phase 1** (`GlueGraph`, `GlueRegistry`, `ContraptionMath`, `ContraptionCapture`,
  `ContraptionNbt`): glue-based structure detection via `/cep contraption glue`, full
  capture/remove/restore NBT round-trip via `/cep contraption capture-test`. Found and
  fixed a real bug along the way: `PersistentBlockEntity.serializeToBytes()` was writing
  via `CompoundTag#write` (raw body, no type-id byte) — NOT byte-compatible with a generic
  reader. Now goes through CraftEngine's own `NBT.toBytes`/`NBT.fromBytes`, with a new
  `loadFromBytes` counterpart (the gap spike #2 flagged).
- **Phase 2** (`ContraptionWorld`, `ContraptionState`, `ContraptionEntity`,
  `ContraptionManager`, `render.ContraptionDisplaySwarm`): in-RAM block+NBT proxy, thin
  facade/registry, packet-only `BLOCK_DISPLAY` swarm rendering real block appearances.
  `/cep contraption spawn-holo` / `despawn-holo`.
- **Phase 3** (`MovementBehavior`, `MovementContext`, `behavior.LinearActuatorBehavior`,
  `ContraptionEngine`): master tick loop registered once in `onEnable` (mirrors
  `FluidEngine.tickAll`), stall-vote gating verified by test (internal state advances even
  while stalled; geographic movement only resumes once nothing is stalled).
  `/cep contraption move <dx> <dy> <dz>` (blocks/sec).
- **Phase 4** (`render.ContraptionHitboxSwarm`, `PlayerCarry`): one invisible Shulker per
  EXPOSED TOP surface cell (not per block, per spike #1's granularity note), and — this
  also resolves the long-standing player-carry-while-standing problem from outside this
  roadmap's original scope — riders standing on the footprint get carried via **ack-gated
  relative `ClientboundPlayerPositionPacket` corrections** (see `PlayerCarry`'s javadoc):
  every earlier attempt (SET/ADD velocity, `Entity.move(MoverType, ...)`, ArmorStand
  passenger-mounting) either fought client movement authority or felt unacceptably
  restrictive. The actual bug in every teleport-based attempt was sending a NEW correction
  every tick while the previous one's ack was still pending — the server distrusts/ignores
  the player's own WASD input for as long as `awaitingPositionFromClient` (private NMS
  field, read via reflection) is non-null, which reads as jitter. Fix: gate every send on
  that field, queue deltas that arrive while one's pending, and use `Relative` flags
  (add-to-current-position, not snap-to-absolute) so a multi-tick queued delta still lands
  correctly. Axis-aligned only so far (yaw ignored in the carry footprint math) — matches
  Phase 3's linear-only-kinematics MVP scope.

**Phase 5** (`ContraptionAccessor`, `behavior.MinerBehavior`, `behavior.MiningMath`): the
world-interaction boundary (only class allowed to mutate the real Overworld) plus a
stalling drill — RPM-vs-hardness damage accumulator (pure, unit-tested via `MiningMath`),
real block-break via `Block.getDrops`/`Level.removeBlock` collecting drops into the
behavior's own list instead of spawning dropped-item entities. `/cep contraption miner
<dx> <dy> <dz> <rpm>` attaches one to the active hologram. **Deferred within Phase 5**:
crack-stage overlay packets (the Phase-0 spike #4 shim is ready, but wiring it needs a
viewer list inside `MovementBehavior#tick`, which only has a `ServerLevel` today — left for
a follow-up rather than half-wiring).

**Phase 6 — DONE** (this session): the full assemble/disassemble state machine, all 3
bearing types, and anchor-keyed chunk lifecycle wiring, replacing the two Phase-1-era
building blocks that were superseded along the way:

- `ContraptionAssembler` (finally created — was in the original §3 package layout but never
  written): the production Static&harr;Dynamic state machine, promoted out of
  `HologramTest`'s inline logic. Shared glue-scan/capture/remove/spawn (assemble) and
  grid-snap/collision-check-restore (disassemble, spike #7's design) for the two
  block-anchored bearing types.
- **3 bearing types** (`BearingType`), triggered by `BearingHammerListener` (mirrors
  `multiblock.HammerAssembleListener`'s shape, own class, contraption package):
  1. **LINEAR** — block-anchored, `LinearActuatorBehavior` attached at a default constant
     rate on assembly.
  2. **ROTATIONAL** — block-anchored, new `behavior.RotationalBearingBehavior`: continuous
     yaw increment at a fixed RPM, reusing `ContraptionMath.renderPosition`'s existing
     rotate-around-bearing math (no new rotation code path) — this ships the continuous
     rotation §7 originally deferred "past MVP".
  3. **MINECART** — entity-anchored. An earlier draft of this phase planned a brand-new
     `net.minecraft.world.entity.EntityType` (reflectively un-freezing
     `BuiltInRegistries.ENTITY_TYPE`, extending `AbstractMinecart`) — **walked back mid-session
     as too risky/fragile for the value it added.** The shipped design instead spawns a REAL,
     already-existing vanilla minecart (`org.bukkit.entity.EntityType.MINECART`) as the
     anchor (`MinecartBearing`), riding unmodified vanilla rail physics; a new
     `behavior.MinecartFollowBehavior` just snaps `ContraptionState`'s position/yaw to the
     real entity every tick. The bearing block must sit directly above a real rail
     (`BaseRailBlock`) — verified before assembly.
- **Bearing detection — SUPERSEDED (later session, see "Glue item + real bearing behavior"
  below)**: the paragraph originally here described `behavior.BearingBehaviorRegistry` (a
  Key -&gt; BearingType map) as the "future hook" and `/cep contraption bearing-test-register`
  as the temporary placeholder standing in for it. Both are gone now — replaced by a real
  CraftEngine block behavior, `behavior.BearingBlockBehavior`. Kept only as historical
  context for why the placeholder existed in the first place.
- **Persistence — anchor-embedded, not folder-scanned**: the old
  `persistence.ContraptionPersistence` + `ContraptionChunkListener` (folder of `.mcdata`/
  `.nbt` files, `Math.floorDiv` on the contraption's CURRENT position to guess which
  real-world chunk to watch) has been **deleted** — wrong on both counts the user flagged: a
  contraption is anchored to a fixed real block/entity, not a floating XZ guess, and a moving
  contraption's live position drifts away from that anchor. Replaced by
  `ContraptionChunkLifecycleListener`, keyed off each bearing's FIXED anchor:
  - LINEAR/ROTATIONAL (anchor tracked in `BearingHammerListener`'s in-memory bookkeeping —
    see "Glue item + real bearing behavior" below): on real-chunk-unload, tears down the
    in-memory `ContraptionLevel` for any contraption whose tracked anchor position is in that
    chunk. **Known, documented gap, still unresolved**: since there's no real `BlockEntity`
    yet to persist into, this loses the structure and does not survive a server restart —
    acceptable for exercising the code path, not acceptable for a real bearing block (see
    `BearingHammerListener`'s javadoc for the real-`BlockEntity` follow-up this stands in
    for).
  - MINECART (real anchor): **not a placeholder** — the structure dump
    (`ContraptionStructureNbt.dump`/`toBytes`) rides in the real minecart's own Bukkit
    `PersistentDataContainer` (a raw vanilla `CompoundTag` can't be stored in a PDC directly,
    so it's compressed to a byte array first, mirroring `ContraptionNbt`'s byte-blob
    convention for the CE-library NBT type). Chunk unload re-dumps the CURRENT structure into
    the PDC then tears down the level; chunk load rehydrates from it. This is genuine vanilla
    entity persistence — survives a restart like any other entity.
- `ContraptionManifest` is now unused by production code (kept, still tested) — the
  anchor-embedded design above replaced its `.mcdata`-manifest role; nothing currently reads
  or writes manifest files.

**Glue item + real bearing behavior (this session)** — the user flagged two things built
during Phase 6 as wrong in kind, not just incomplete, and both are now fixed:

- **Glue is an item wand, not a command.** `/cep contraption glue` and `glue-clear` are
  **deleted**. `contraption.GlueWandListener` (registered in `onEnable`, mirrors
  `conveyor.ConveyorWandListener`/`pipe.PipeWandListener`'s right-click-twice shape and
  `multiblock.HammerAssembleListener`'s cheap held-item gate) does the same job as an item
  tool: gated on `CraftEngineItems.getCustomItemId(hand)` equalling `cml:slime_glue`
  (`GlueWandListener.SLIME_GLUE`). First right-click sets the pending anchor, a second
  right-click on an ADJACENT block glues them via `GlueGraph`/`GlueRegistry` (unchanged —
  these were already correct, pure data structures, per the user's own framing) and moves the
  anchor to the new block so a run of blocks can be chained with repeated clicks. Sneaking
  clears the pending anchor. Feedback is an action-bar message plus
  `BLOCK_SLIME_BLOCK_PLACE` on success — mirrors `HammerAssembleListener`'s minimal
  `playSound`-on-success pattern. **Caveat**: `cml:slime_glue` is an assumed item id, matching
  this project's `cml:` namespace convention for other tool items (`HammerItems`'s
  `cml:wooden_hammer` etc.) — it is **not defined anywhere in this repo** (this project ships
  no resource pack of its own). Until the user's CraftEngine item config defines a custom item
  at that exact key, `CraftEngineItems.getCustomItemId(hand)` returns null for whatever
  physical item a player holds and the listener silently no-ops on every click (same
  fail-open shape as every other wand listener here when its item doesn't exist yet).
- **Bearings are a real CraftEngine block behavior, not a command.**
  `/cep contraption bearing-test-register <linear|rotational|minecart>` and its
  `BearingAnchorRegistry` position-keyed stand-in are **deleted**. `behavior.BearingBehaviorRegistry`
  (the unused Key -&gt; BearingType "future hook" scaffold from the original Phase 6 pass) is
  **also deleted** — it was never populated and is now fully superseded, not left alongside
  the real mechanism. In their place: `contraption.behavior.BearingBlockBehavior`
  (`extends BukkitBlockBehavior`, registered under `polyfills:bearing_block` in
  `block.BlockBehaviors.register()`, exactly like every other custom block behavior in this
  codebase — `FanBlockBehavior` was the read-for-pattern reference). Its `Factory` reads a
  `type: linear|rotational|minecart` config field off the block definition
  (`ConfigSection.getOrDefault("type", "linear")` → `BearingType.valueOf(...)`, same
  `arguments.getOrDefault(...).toString()` idiom `FanBlockBehavior.Factory` uses for its own
  fields) and stores the resulting `BearingType` on the behavior instance. Detection is a
  static helper, `BearingBlockBehavior.typeAt(Level, BlockPos)`, using the exact
  `BlockStateUtils.getOptionalCustomBlockState(...)` → `ImmutableBlockState` →
  `.behavior().getFirst(BearingBlockBehavior.class)` idiom `HammerAssembleListener` already
  uses for `FluidBlockTankBehavior`. `BearingHammerListener` now calls `typeAt` instead of
  reading `BearingAnchorRegistry` — the detection mechanism changed, the downstream
  `ContraptionAssembler.assemble`/`MinecartBearing.assemble` call targets did not.
  - **Assembled-state bookkeeping moved, didn't disappear.** `BearingAnchorRegistry` did two
    jobs: (1) map a position to its `BearingType` (now `BearingBlockBehavior.typeAt`, real),
    and (2) track "is the bearing at this position currently assembled, and into which live
    `ContraptionEntity`" (job (2) is state a real persistent `BlockEntity` would normally own
    for LINEAR/ROTATIONAL — no such `BlockEntity` exists yet, unchanged gap). Job (2) now
    lives as a small in-memory map directly inside `BearingHammerListener`
    (`ASSEMBLED`/`BY_CONTRAPTION`, exposed via `assembledContraptionAt`/
    `assembledAnchorsSnapshot`/`forgetAssembled`) rather than a separate registry class, since
    it's the listener's own transient bookkeeping, not future-facing infrastructure the way
    the deleted Key-&gt;BearingType map was. `ContraptionChunkLifecycleListener` reads
    `assembledAnchorsSnapshot()` where it used to read `BearingAnchorRegistry.anchorForContraption`
    — same "known gap, doesn't survive a restart" caveat as before, just without a dangling
    reference to the deleted class.
  - **Still genuinely blocked on non-code content**: `BearingBlockBehavior` is real, compiles,
    and is registered — but until a `.yml` block definition in CraftEngine's resource-pack
    config actually references `polyfills:bearing_block` with a `type:` field, no in-game
    block exists that uses it. Example config a human would add under
    (typically) `configuration/blocks/contraption_bearing.yml` in the CraftEngine resource
    pack project (adjust the block id/material/model to taste):
    ```yaml
    contraption_bearing:
      material: minecraft:polished_blackstone   # or any placeholder vanilla base block
      behavior:
        type: "polyfills:bearing_block"
        settings:
          type: linear   # or: rotational | minecart
    ```
    Ship 3 separate block ids (one per `type:` value, e.g. `linear_bearing`/
    `rotational_bearing`/`minecart_bearing`) if distinct in-game appearances per bearing kind
    are wanted — the behavior only reads `type:`, it doesn't care about the block id.

**Live RPM: miner block + rotational-bearing-as-power-source (this session)** — closes the
gap flagged at the top of this section's predecessor writeups: `MinerBehavior`'s rpm used to
be a fixed constructor parameter set once and never updated, and `RotationalBearingBehavior`
spun at a single global constant (`ContraptionAssembler.DEFAULT_ROTATIONAL_RPM`). Both are
now live, wired through the pre-existing, contraption-unrelated `rotation/`
`RpmProvider`/`RpmConsumer` "SHARED ROTATION CONTRACT" (the interfaces themselves were left
untouched, per their own javadoc).

- **`behavior.MinerBlockBehavior`** (new, `polyfills:miner_block`) — a real CraftEngine block
  behavior, same shape as `BearingBlockBehavior` (`BukkitBlockBehavior` + `Factory` reading a
  `ConfigSection`, registered in `block.BlockBehaviors.register()`). Reads its target
  direction off the block's own `facing` property, exactly like `FanBlockBehavior`, plus a
  `gearRatio` multiplier (default `1.0`). It has two lives:
  - **Standing in the real world** (not yet captured): its `MinerController`
    (`BlockEntityController`) implements `RpmConsumer` and pull-scans its 6 neighbor
    `BlockPos` each tick for the strongest adjacent `RpmProvider` — the exact idiom
    `CrusherBlockEntity`/`SmelteryBlockEntity` already use for a real motor. A miner block
    next to a real `GasMotorMk1BlockEntity` spins even before ever being glued into a
    contraption.
  - **Captured into a contraption**: `MinerBlockBehavior.buildMovementBehavior` is registered
    as a `MovementBehaviorRegistry.Factory` for `polyfills:miner_block` (right next to the CE
    behavior's own registration in `BlockBehaviors.register()`, for discoverability) — so any
    captured miner block automatically gets a `MinerBehavior` built for it
    (`ContraptionCapture#resolveAutoBehaviors`'s existing auto-attach mechanism, previously
    never populated for anything — this is the first real consumer of it).
- **`behavior.MinerBehavior`** now implements `RpmConsumer` instead of taking a fixed `rpm`
  constructor parameter — it reads `getInputRpm() * gearRatio` fresh every tick inside
  `tick(MovementContext)`. **Design choice, documented in the class javadoc**: effective rpm
  &le; 0 (no power feeding it) is treated as **inert, not stalled** — `isStalled()` returns
  `false` — specifically so a powerless miner never permanently deadlocks a LINEAR-bearing
  contraption's movement forever (LINEAR bearings produce no RPM at all; a miner riding one
  has no in-structure power source under this design and simply never mines until something
  external calls `setInputRpm`). This is intentionally different from the pre-existing
  "hit bedrock" permanent stall (hardness &lt; 0), which stays unrecoverable by design.
  `MinerBehaviorTest` covers both the pre-existing `level == null` no-op path and the new
  zero-rpm-is-inert case.
- **`behavior.RotationalBearingBehavior`** now implements `RpmProvider` (`getRpm()` returns
  its own configured rpm — it never stalls, so this is always live) and, per-tick, pushes
  that rpm to every `RpmConsumer` `MovementBehavior` already in the SAME `ContraptionState`.
  **Documented simplification, not the real BlockPos-adjacency network**: faithfully
  replicating `GasMotorMk1BlockEntity`'s real push mechanism inside a `ContraptionLevel`
  would mean giving `BearingBlockBehavior` its own `BlockEntityController` and
  adjacency-scanning in local coordinates — judged disproportionate effort for this first
  pass. Instead, "captured together in the same contraption" stands in for "adjacency
  connected": every miner in a structure spins at the bearing's rpm regardless of
  in-structure distance/routing, unlike the real network's distance/relay-aware propagation
  (routers/relays, distance falloff, etc. — none of that applies here). This is an
  intentionally honest shortcut, not a hidden one — see that class's own javadoc for the same
  writeup in code. (`RpmProvider`/`RpmConsumer` themselves were **not modified** — both
  interfaces' signatures are exactly as the conveyor feature already depends on.)
- **`behavior.BearingBlockBehavior`** gained an `rpm:` config field (only meaningful for
  `type: rotational`, defaulting to `5.0` — same value as the old
  `ContraptionAssembler.DEFAULT_ROTATIONAL_RPM` global constant, which now only serves as a
  fallback for callers that don't have a specific block's config, e.g. tests). A ROTATIONAL
  bearing's spin speed is now configured per-block-definition, not one global constant.
  `ContraptionAssembler.assemble` gained an overload taking `rotationalRpm` explicitly;
  `BearingHammerListener` reads it via the new `BearingBlockBehavior.rpmAt(level, pos)` at
  assembly time and passes it through.
- **Example config** — miner block (mirrors the bearing example above):
  ```yaml
  contraption_miner:
    material: minecraft:iron_block   # or any placeholder vanilla base block
    behavior:
      type: "polyfills:miner_block"
      settings:
        gearRatio: 1.0
  ```
  (`facing` comes from the block definition's own `facing` property, same as the fan block —
  not a `settings:` field.) Updated bearing example showing the new `rpm:` field:
  ```yaml
  contraption_bearing:
    material: minecraft:polished_blackstone
    behavior:
      type: "polyfills:bearing_block"
      settings:
        type: rotational   # rpm: only applies to this type
        rpm: 12.0           # defaults to 5.0 if omitted
  ```
- **How rpm actually flows from a rotational bearing to a miner captured in the same
  structure**: (1) at assembly, `ContraptionAssembler` attaches one `RotationalBearingBehavior`
  configured with the bearing block's own `rpm:` (or the 5.0 default) to the new
  `ContraptionState`; (2) any miner block(s) among the glued structure were auto-captured as
  `MinerBehavior` instances into the SAME `ContraptionState.behaviors()` list via
  `ContraptionCapture#resolveAutoBehaviors`; (3) every tick, `ContraptionEngine`'s master
  clock calls `tick()` on every behavior in that list — when `RotationalBearingBehavior.tick`
  runs, it iterates `ctx.state().behaviors()` and calls `setInputRpm(getRpm())` on every
  `RpmConsumer` it finds (i.e. every `MinerBehavior` in the same structure); (4) each
  `MinerBehavior.tick` (order-independent within the same tick — it just reads whatever
  `inputRpm` was last set, which is fine since `RpmProvider`s never stall in this codebase's
  existing implementations either) then multiplies that by its own `gearRatio` and runs the
  existing `MiningMath` accumulator. No BlockPos scanning happens inside `ContraptionLevel`
  for this path — see the "documented simplification" bullet above for exactly why and what
  was skipped.
- **Still genuinely blocked on non-code content**: like `BearingBlockBehavior`, the miner
  behavior is real, compiles, and is registered — but needs a `.yml` block definition
  actually referencing `polyfills:miner_block` (with a `facing` property + optional
  `gearRatio`) before any in-game block exists that uses it.

**Remaining open work**: the 3 still-open Phase-0 spikes (#1 entity budget, #3
Shulker-standing feel, #6 tick budget under load) need a human in a live client/load-test and
cannot be resolved from the editor — Phase 4's `/cep contraption spawn-holo` + `/cep
contraption move`, and now the full bearing/hammer flow, give a real system to test them
against. The MINECART bearing's visual appearance is the plain default minecart model this
pass (a follow-up could ride a packet-only Display swarm on top of the real entity's
position, the same trick `ContraptionDisplaySwarm` already does for the other two bearing
types) — not attempted this session, documented gap rather than a half-wired attempt.

**Packet-renderer viewer-redirect fix (this session)** — a `ContraptionLevel` is a real,
separate, hidden `ServerLevel` with **zero real players ever inside it** (see that class's
javadoc). Several of this project's own packet-only renderers resolved "which players should
see this" via real chunk-tracking (`BukkitWorld#getTrackedBy`, ultimately backed by the
level's own player-tracker) — always empty against a `ContraptionLevel`, so a fluid tank or
conveyor belt riding a moving contraption rendered nothing at all, even though the blocks are
100% real and ticking.

- **Shared fix, `ContraptionLevel`**: three new methods every packet renderer should go
  through instead of resolving viewers/positions itself: `realViewers(BlockPos|Vec3)` (redirects
  to `realLevel`'s real chunk-tracking at the bearing's live transform, instead of this level's
  always-empty one), `realWorldPositionOf(Vec3)` (continuous-position sibling of the existing
  `realWorldPositionOf(BlockPos)`, for mid-travel positions like a conveyor item), and
  `realOrientationOf(Quaternionf)` (composes a local-space orientation with the bearing's
  current yaw — `Quaternionf#rotateY(-yaw)` matches `ContraptionMath#rotateYaw`'s `+yaw`
  convention on the (x,z) plane, see that method's javadoc for the sign derivation).
- **Fixed**: `fluid.behavior.FluidTankRender` (`viewersOf` redirects through `realViewers`;
  `update()` translates every fluid-display cell's target position through
  `realWorldPositionOf` and sets its orientation through `realOrientationOf` when riding a
  contraption — `fluid.behavior.FluidDisplay` gained a `setRotation`/`LeftRotation` field for
  this, previously always-identity). `conveyor.ConveyorBlockEntity` (new package-visible
  `viewersOf`/`contraptionOf` helpers reused by every `getTrackedBy` call site in the class,
  including `renderAll`, which now also redirects the rendered item's position + composed
  rotation through the bearing's transform) and `conveyor.FunnelBlockEntity` (same pattern,
  reusing `ConveyorBlockEntity`'s package-visible helpers rather than duplicating them).
  `conveyor.AbstractRouterBlockEntity`/`DepotBlockEntity` still use the old direct
  `getTrackedBy` pattern — **not fixed this session** (out of the explicitly requested
  fluid-tank/conveyor-item-display scope; same fix shape applies if/when needed).
- **New**: `contraption.render.ContraptionEntityMirrorSwarm` — real entities (dropped items,
  mobs) that end up physically living inside a `ContraptionLevel` keep ticking/moving with
  completely normal vanilla AI/physics, but since the level hosts no real players, nothing a
  real client ever renders them directly. This swarm is the entity-side counterpart to
  `ContraptionDisplaySwarm` (blocks) and `ContraptionHitboxSwarm` (rider collision): every
  tick it walks `ContraptionLevel#getAllEntities()`, and for each live one keeps a packet-only
  mirror positioned at `realWorldPositionOf(entity.position())` with the bearing's current yaw
  — a real `minecraft:item` mirror carrying the actual stack for `ItemEntity`s, a generic
  `ARMOR_STAND` marker for everything else (full per-mob fidelity explicitly out of scope, same
  call already made for the hitbox swarm's Shulkers). Despawns a mirror the tick its source
  entity goes missing/removed. Owned by `ContraptionEntity` right alongside `displaySwarm`/
  `hitboxSwarm`, driven through the exact same `render(viewers)`/`despawn(viewers)` lifecycle
  (no rebuild step needed — unlike the fixed block-cell set, the entity set is walked fresh
  every tick).
- **Investigated, genuinely blocked (not fixed)**: whether CraftEngine's own built-in
  `entity-renderer` block config (`ConstantBlockEntityElement`/`ConstantBlockEntityRenderer`,
  which this project already hooks into for two *currently-unused* element types —
  `fluid.render.FluidDisplayElementConfig`/`FluidDisplayElement` registered as
  `polyfill:fluid_display`, and `machine.render.element.ShulkerBoxHitboxElementConfig`/
  `ShulkerBoxHitboxElement` as `polyfill:shulker_box_hitbox`, neither referenced by any
  resource-pack block config in this repo yet) has the same bug, and whether it's fixable from
  this project. Confirmed by reading CraftEngine 26.6.2's own sources (sources jar, resolved
  via Gradle's dependency cache): **it has the identical bug, and it is NOT fixable from this
  project.** `BlockEntity#updateConstantRenderers()` calls `CEChunk#getTrackedBy()` ->
  `World#getTrackedBy(ChunkPos)` -> `BukkitWorld#getTrackedBy` — the exact same real
  chunk-tracker this project's own bug used, and the initial
  `CEChunk#spawnBlockEntities(Player)`/`despawnBlockEntities(Player)` calls are themselves only
  ever invoked from real per-player chunk-track/untrack events, which never fire for a
  `ContraptionLevel`. `BukkitWorldManager` (the singleton owning every Bukkit world's `CEWorld`)
  is package-private-constructed and final, with no registry/listener a plugin can use to
  substitute a different `World`/tracking implementation for one specific world — there is no
  extension point to redirect this from outside the jar. Documented as a hard limitation in
  `ContraptionLevel`'s class javadoc (search "Known limitation") rather than silently left
  broken: don't configure a block that might ever ride a contraption to use CraftEngine's
  `entity-renderer` config; use this project's own hand-rolled packet renderers instead (which
  now correctly redirect through `realViewers`/`realWorldPositionOf`).

## 1. Architecture summary

> **Superseded (this session)**: the "in-RAM data-only proxy, no real `ServerLevel`" call
> below was the original brainstorm's conclusion but is **no longer the design** — the
> user explicitly required real chunk/block-entity/entity semantics with genuine
> `instanceof` detection, which a data proxy can't provide. `ContraptionLevel` is now a
> real `net.minecraft.server.level.ServerLevel` subclass, **one dedicated mini dimension
> per contraption** (not shared) — see `contraption/level/ContraptionLevel.java`'s javadoc
> for the full rationale, including why a bare `Level` subclass (as the brainstorm and an
> earlier abandoned attempt both assumed) is impossible on this server: `Level`'s only
> constructor unconditionally does `new CraftWorld((ServerLevel) this, ...)` (confirmed via
> `javap` against this project's own mapped server jar), so any concrete `Level` must
> legitimately BE a `ServerLevel`. Bootstrapping one (unique dimension key + storage folder
> per contraption, registered via `MinecraftServer#addLevel`/`CraftServer#addWorld`) is
> heavier than the old proxy, but buys real vanilla ticking (a hopper still moves items, a
> furnace still smelts) for free, and honest `level instanceof ContraptionLevel` detection
> from any block/BE code holding a `Level` reference. `getUncachedNoiseBiome`/
> `addParticle`/`playSeededSound`/`levelEvent`/`destroyBlockProgress` are overridden to
> redirect into the real world at the bearing's live transform (`setTransform`) — this is
> the "real vs. fake positions" / "reproduce particles-sounds referentially" requirement.
> The paragraphs immediately below are kept for historical context (why a live mirror
> dimension was rejected) but the `ContraptionWorld` proxy design has been dropped —
> there is no `ContraptionWorld` class; `ContraptionLevel` fills that role for real.

The brainstorm transcript explored and **rejected** a live "mirror dimension" (a second
real, ticking Bukkit `World`) — a 1:1 coordinate mirror breaks the instant a contraption
moves (can't cheaply re-place blocks every tick to keep it in sync), and one `World` per
contraption (or even one shared RAM world à la SlimeWorldManager) still drags in a full
`ServerLevel`'s entity trackers/chunk providers/tickers — too heavy for dozens of
simultaneous contraptions. It converged instead on the following (this is how Create
itself works under the hood, per the transcript's own research into Create's
`ContraptionWorld`/`VirtualRenderWorld`) — **historical, see the superseding note above**:

- **`ContraptionWorld`** (historical name — the real class is `ContraptionLevel`, and it IS
  a real `ServerLevel` now, not a proxy; kept below only for the rejected-mirror-dimension
  context): originally spec'd as NOT a real `ServerLevel`. A lightweight in-RAM data-only
  proxy — `Map<BlockPos, BlockState>` + captured block-entity NBT blobs. No native ticking
  inside it; vanilla ticking would have been **suspended/frozen** the moment a structure
  becomes a contraption (ovens don't keep smelting mid-flight — this is the opposite of the
  now-current design, and the opposite of the transcript's very
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
- **Assembly/disassembly — explicit 4-state machine**: Static → [glue-scan(item mechanic? render outline or sliem block overlay on holdin slime glue?) + NBT-capture +
  remove real blocks + spawn `ContraptionWorld`/Display-swarm/hitbox-swarm] → Dynamic
  (moving; internal networks tick if present; world interaction only via
  `ContraptionAccessor`) → [only when linear+angular velocity are exactly zero: round
  every local-space block position to the nearest integer world coordinate ("grid
  snapping"), collision-check each target cell, write blocks back + restore NBT, or eject
  conflicting cells as dropped items rather than silently destroying data] → Static.
  (might add diferent bearings? rotational bearing? directional bearing? minecart bearing?)

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

> **Note (actual implementation deviates slightly)**: the glue tool shipped as
> `contraption/GlueWandListener.java` (flat in the `contraption` package, not a `glue/`
> subpackage) rather than `glue/GlueToolListener.java` as originally sketched below — kept
> alongside `BearingHammerListener` since both are simple item/hammer-gated
> `PlayerInteractEvent` listeners registered the same way in `onEnable`. Bearing detection
> lives in `contraption/behavior/BearingBlockBehavior.java`, not sketched in this diagram at
> all (written after this diagram, once bearings became a real CraftEngine block behavior
> rather than a data-only concept).

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

**Phase 6 — DONE.** `ContraptionAssembler` completed end to end for the 2 block-anchored
bearing types; `MinecartBearing` handles the entity-anchored type's own assemble/disassemble
(can't reuse `ContraptionAssembler` directly — its anchor is a real entity, not a fixed
block). Hammer-style right-click trigger via `BearingHammerListener` (mirrors
`HammerAssembleListener`'s shape). `ContraptionManager` wired to chunk load/unload via
`ContraptionChunkLifecycleListener`, keyed off each bearing's fixed anchor (not current
position — see §0's Phase 6 writeup for why that distinction mattered). **Updated in a later
session** (see §0's "Glue item + real bearing behavior" writeup): a real bearing block
behavior, `behavior.BearingBlockBehavior`, IS now registered via `BlockBehaviors.register()`
under `polyfills:bearing_block` — bearing detection is by that real registered behavior, not
the originally-planned-but-never-populated `BearingBehaviorRegistry` Key map (deleted) and
not the `/cep contraption bearing-test-register` command (also deleted). Still blocked on a
`.yml` block config actually referencing that key existing in CraftEngine's resource pack —
see §0 for the exact YAML.

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

- ~~Continuous rotation (windmills, rotating platforms)~~ — **shipped in Phase 6** as the
  ROTATIONAL bearing type (`behavior.RotationalBearingBehavior`), see §0.
- Nested contraptions / scene-graph (a contraption riding another contraption) — still out
  of scope; no code touches this.
- Docking/bridge-edge fluid & gas networks between a contraption's internal `FluidGraph`
  and the world's — needs genuinely new "dynamically add/remove one edge between two
  independently-built graphs" support in `FluidGraphBuilder`/`FluidNetworkSolver`/
  `FluidEngine`, none of which exists today (`FluidEngine.tickAll` rebuilds one graph per
  seed from scratch every pass). Real, separate engine work — not a small addition, still
  out of scope.
- Internal isolated fluid/gas/RPM networks in general, even without docking — still out of
  scope.
- Multiple simultaneous movement types combined (rotating AND translating at once) — still
  out of scope; a contraption gets exactly one of LINEAR/ROTATIONAL/MINECART kinematics.
- Non-miner contraption types beyond the MVP miner (windmill, collector, piston-pushed
  platform) — the 3 bearing types are movement/anchor primitives, not new contraption
  "kinds"; a windmill is just a ROTATIONAL bearing with no miner behavior attached, which
  already works, but no new *distinct* named contraption type was built beyond that.
- Real `BlockEntity`-backed persistence for LINEAR/ROTATIONAL bearings (currently the
  in-memory assembled-anchor bookkeeping in `BearingHammerListener` — see §0's "Glue item +
  real bearing behavior" writeup) — blocked on a real CraftEngine bearing block existing in
  resource-pack config, not a code gap. The behavior class itself (`BearingBlockBehavior`) is
  real and ready; only the `.yml` config content is missing.
- MINECART bearing custom visual appearance (currently the plain default minecart model) —
  a follow-up display-swarm-on-a-real-entity trick, not attempted this session.
- The 3 still-open Phase-0 spikes (#1 entity budget, #3 Shulker-standing feel, #6 tick
  budget under load) — genuinely need a human in a live client/load-test; cannot be resolved
  from the editor.

## 8. 2026-07-01 session — seats, right-click routing, miner config/UI, bearing-as-relay

Four tasks this session, all compiling and passing the full `dev.arubik.craftengine.contraption.*`
suite (49 tests, unchanged count — no new unit tests added this pass; every change here needs a
live server to actually exercise, same caveat as most of Phase 4-6).

**Task 1 — furniture seats (DONE, with an honest gap).** A real player sitting in a
CraftEngine furniture seat now keeps riding along when that furniture gets captured into a
moving/rotating contraption. `ContraptionFurnitureCapture#captureNear` now returns a
`Result(List<ContraptionFurniture> furniture, Map<UUID, Vec3> seatedRiders)`: a second scan
pass over the same nearby-entities query looks for real seat marker entities
(`CraftEngineFurniture.isSeat(Entity)`/`getLoadedFurnitureBySeat(Entity)`, same public API
class already used for furniture itself), and for any seat whose owning furniture was
captured, records the seat's OWN position as a fixed bearing-relative local offset plus
whichever real `Player` is currently riding it as a passenger. **Design choice** (option (a)
from the task, chosen over spawning a followed real vehicle entity): reuses
`PlayerCarry` — the exact ack-gated relative-position-correction mechanism Phase 4 already
proved for players standing on a moving hitbox footprint — via a new
`ContraptionState.seatedRiders()`/`seatedRiderLastReal()` pair and
`ContraptionEntity#carrySeatedRiders()` (called every tick from `ContraptionEngine.render`,
right after the existing `carryRiders`). Unlike the footprint carry (axis-aligned, yaw
ignored), seat carrying is fully yaw-aware: the per-tick delta is the difference between this
tick's and last tick's `ContraptionMath#renderPosition` of the seat's fixed local offset, so a
chair several blocks off-axis from a spinning bearing correctly sweeps an arc. Rejected
alternative (option (b), a real followed vehicle entity, mirroring `MinecartFollowBehavior`):
the SAME reasoning that made `ContraptionFurnitureCapture` destroy-and-rebuild the furniture
itself instead of live-following it (`Furniture#moveTo` doesn't rotate correctly under
continuous per-tick calls) applies identically to a live seat entity, so it was rejected for
consistency, not just convenience. **TODO(incomplete)**, documented in
`ContraptionFurnitureCapture#captureNear`'s own javadoc: there is no way to sit down on a
captured seat after assembly (no real seat entity exists anymore to mount), and a player who
stands up mid-flight is never explicitly detected/released — they stay carried at the seat's
fixed offset until the contraption disassembles. Files: `ContraptionFurnitureCapture.java`,
`ContraptionState.java`, `ContraptionEntity.java`, `ContraptionEngine.java`,
`ContraptionAssembler.java`. **To test in-game**: sit in a CraftEngine chair/furniture with a
seat, have someone else hammer-assemble a contraption whose glued footprint includes that
furniture while you're sitting, then trigger movement/rotation — you should keep riding
smoothly instead of being left behind or falling through.

**Task 2 — right-click routing into a `ContraptionLevel`; left-click is a no-op (DONE).** New
`ContraptionInteractionListener` (registered in `onEnable` right after `GlueWandListener`):
for every live `ContraptionEntity`, ray-clips the player's eye position/look vector (out to
`ServerPlayer#blockInteractionRange()`, the real per-player vanilla reach value, not a
hardcoded guess) against every captured cell's CURRENT real-world unit-cube AABB (computed the
same way `ContraptionDisplaySwarm` renders them, via `ContraptionMath#renderPosition`); the
closest hit across every contraption wins. The hit point is converted back to bearing-local
space via a new `ContraptionMath#realToLocal` (the inverse of `renderPosition`) and dispatched
directly against the real block inside that contraption's `ContraptionLevel` via the vanilla
`BlockState#useWithoutItem`/`useItemOn` methods (`BlockBehaviour.BlockStateBase`, confirmed via
`javap` against this project's own mapped server jar — CraftEngine's own
`BukkitBlockBehavior#useWithoutItem(UseOnContext, ImmutableBlockState)` hook needs a
`UseOnContext` built from several internal-only fields with no existing call site in this repo
constructing one from scratch, so the vanilla entry point was the cleaner, more direct choice;
it reaches the exact same CE dispatch since CraftEngine's custom blocks are real vanilla
`Block` instances underneath). Left-click (`LEFT_CLICK_BLOCK`/`LEFT_CLICK_AIR`) against a
contraption cell is explicitly cancelled and does nothing further, per the user's explicit
instruction. Same double-fire guard as every other listener in this package
(`event.getHand() == EquipmentSlot.HAND`). Furniture interaction is explicitly NOT routed — a
captured `ContraptionFurniture` cell has no live CraftEngine object to forward a click to
(destroyed at capture time, see Task 1's writeup and `ContraptionFurnitureCapture`'s own
javadoc); a click that only intersects a furniture hitbox falls through as a miss, documented
as a `TODO(incomplete)` rather than faked. Files: `ContraptionInteractionListener.java` (new),
`ContraptionMath.java` (`realToLocal` added), `CraftEnginePolyfills.java` (registration).
**To test in-game**: assemble a contraption containing a chest/furnace/any CE machine block,
let it move/rotate, then right-click where it visually appears — the real inventory should
open exactly as if it were still standing still in the real world. Left-clicking any part of
it should do nothing (no block-break attempt, no particles).

**Task 3 — miner: config-driven RPM/SU + a real status GUI (DONE, deliberately scoped
down from the Crusher's full menu framework).** Read `CrusherBlockEntity`/`CrusherBehavior`
first: RPM threshold/SU cost live per-RECIPE there (`AbstractProcessingRecipe#getMinRpm/
getSuCost`), reported to the driving motor every tick via `RpmProvider#reportStressLoad` with
a 20-tick grace window so load doesn't flicker across recipe/item transitions, and its "UI" is
a REAL Bukkit chest-inventory GUI (`MachineMenu`, opened via `useWithoutItem` on right-click,
ticked every server tick for live progress/rpm bars) — not actionbar/particles/bossbar.
`MinerBlockBehavior` now has no recipe system, so `minRpm`/`suCost` became plain per-block
config fields instead (`Factory` reads them the same `arguments.getOrDefault(...)` way as
`gearRatio`, defaults 20.0/5.0). `MinerController` (the real-world form) gained the identical
grace-window SU-reporting shape (`STRESS_GRACE_TICKS = 20`, `activeMotor` tracked each scan)
and a `minRpm` operating gate. `MinerBehavior` (the captured/in-contraption form) gained the
same `minRpm` gate (below threshold = inert, not stalled, same design choice as the existing
zero-rpm case) and now reports `suCost` into the new finite-SU-budget system (see Task 4/5
below) whenever it's actually cutting. **The "UI" itself is intentionally NOT a reproduction of
`MachineMenu`'s upgrade/overclock sub-page framework** — `MinerController#openStatusMenu`
(triggered by a new `MinerBlockBehavior#useWithoutItem`, same trigger shape as
`MachineBlockBehavior`'s own) is a plain read-only single-row Bukkit inventory: one colored
glass pane (lime = operating, red = idle) with lore showing input rpm, gear ratio, effective
rpm, configured minRpm, suCost, and whether a real motor was detected. Matches the crusher's
actual MECHANISM (a real inventory GUI) honestly without building machinery this block doesn't
need (no recipes, no upgrades, no overclock to browse). Files: `MinerBlockBehavior.java`,
`MinerBehavior.java`. **To test in-game**: place a miner block next to a motor, right-click the
miner — a small chest-like GUI should open showing live rpm/SU status; the miner should only
actually start cutting once the motor's delivered rpm clears the configured `minRpm`.

**Task 4 — bearing consumes a real adjacent motor instead of producing its own fixed
RPM/speed; finite local/global SU-RPM budget with local-over-global priority (DONE for the
one real network path that exists; internal per-block adjacency networks remain the
documented pre-existing gap).** Confirmed the starting state by reading
`RotationalBearingBehavior`/`LinearActuatorBehavior`/`ContraptionAssembler.attachDefaultBehavior`:
every freshly-assembled bearing got a FIXED constant (`DEFAULT_ROTATIONAL_RPM`/
`DEFAULT_LINEAR_SPEED`, or a per-block `rpm:` config value) that never changed — genuinely a
"free" power source, not consuming anything. Changed to mirror `SplitterBehavior`/
`MergerBehavior`'s "relay a REAL adjacent `RpmProvider`" pattern:

- New `behavior.RealMotorLink`: `findAdjacentMotor(Level, BlockPos)` (a ONE-TIME 6-neighbor
  scan at assembly time, run against the bearing's own real-world position — still valid even
  though the bearing's block is itself removed as part of the captured structure, since only
  the 6 NEIGHBOR cells actually matter and a motor block is never itself part of the
  glued/captured structure) and `currentRpm`/`reportStressLoad` (re-resolve the stored neighbor
  `BlockPos` fresh EVERY tick — a motor can be placed/broken/replaced after assembly).
- `RotationalBearingBehavior`/`LinearActuatorBehavior` both take a `motorPos` (nullable — falls
  back to the old fixed constant only when no motor was adjacent at assembly) and a
  `suPerBlock` (new `BearingBlockBehavior` config field, default 2.0 — SU demanded from the
  motor scales with how many blocks the contraption has captured, `ContraptionLevel#blockCount()`).
  Every tick: report `suPerBlock * blockCount + ctx.state().suDemand()` (see below) to the real
  motor, then read back whatever rpm it actually delivers — if the motor's own overstress logic
  decides that's too much load, it reduces/zeroes `RpmProvider#getRpm()` on its own, and the
  bearing (and therefore the whole contraption's rotation/translation speed) slows down or stops
  right along with it. This class does not implement overstress math itself — it only reports
  demand and reads back supply, same division of responsibility `CrusherBlockEntity` uses.
- **Global feed**: the bearing's resolved rpm is pushed to a new `ContraptionState#globalRpm()`
  every tick — available to every captured block in the structure.
- **Finite local/global SU-RPM budget, local-over-global priority** (the user's mid-session
  clarification: "rpm and su are finite resources... the contraption should stop or reduce
  speed until it can work"): new `ContraptionState#suDemand()`, reset once per engine tick
  (`ContraptionEngine.stepKinematics`, BEFORE any behavior runs) and accumulated by every
  operating `RpmConsumer` behavior via `addSuDemand` (currently only `MinerBehavior`, when it's
  actually cutting). Because the bearing behavior is always appended to `ContraptionState`
  AFTER every auto-captured consumer (`ContraptionAssembler#assemble`'s ordering), it always
  ticks LAST within the same pass and therefore sees the FULL accumulated demand before
  reporting it to the real motor — this is what actually closes the loop: a contraption with
  more/heavier active consumers than its real motor's `stressCapacity()` can sustain gets a
  real overstress response from that motor, which naturally throttles the bearing's own speed.
  `MinerBehavior`'s existing `RpmConsumer#getInputRpm()` (settable via `setInputRpm`, e.g. by a
  hypothetical local captured motor) is still preferred over `globalRpm()` whenever it's `> 0`
  — that's the "local-over-global priority" half.
- **Honestly scoped as incomplete**: the finite-budget enforcement above only closes the loop
  for the ONE real network path that exists — bearing pulls from a REAL adjacent motor outside
  the structure. An INTERNAL rpm/su net (a real motor block CAPTURED together with its
  consumers inside the same `ContraptionLevel`, feeding them via their own local adjacency scan
  rather than the bearing) is still not implemented — this was already a documented
  simplification before this session (`RotationalBearingBehavior`'s "documented simplification,
  NOT the real BlockPos-adjacency network" javadoc, unchanged in substance, just now describing
  the global-feed path instead of the old direct-push one) and remains the honest gap: a
  same-contraption local motor+consumer pair has no working adjacency scan inside
  `ContraptionLevel` today, so it cannot independently limit ITS OWN local rpm/su budget the
  way the external bearing path now does.
- `BearingBlockBehavior` gained `suPerBlock:` config (default 2.0, alongside the pre-existing
  `rpm:` — now honestly documented as "the bearing's own target speed, used only as a fallback
  when no real motor is adjacent" rather than the primary source). `ContraptionAssembler.assemble`
  gained a `suPerBlock` parameter (5-arg overload; the old 4-arg `rotationalRpm`-only overload is
  `@Deprecated` but kept for older call sites/tests); `BearingHammerListener` reads it via the
  new `BearingBlockBehavior.suPerBlockAt`.
- Example config:
  ```yaml
  contraption_bearing:
    material: minecraft:polished_blackstone
    behavior:
      type: "polyfills:bearing_block"
      settings:
        type: rotational
        rpm: 12.0          # fallback ONLY — used when no real motor sits next to the bearing at assembly
        suPerBlock: 2.0     # SU demanded from the real motor per captured block, on top of active consumers' own SU
  ```

Files touched this task: `RealMotorLink.java` (new), `RotationalBearingBehavior.java`,
`LinearActuatorBehavior.java`, `MinerBehavior.java`, `BearingBlockBehavior.java`,
`ContraptionAssembler.java`, `ContraptionState.java`, `ContraptionEngine.java`,
`BearingHammerListener.java`. **To test in-game**: place a real motor next to a bearing BEFORE
hammer-assembling it, confirm the assembled contraption's spin/movement speed tracks the
motor's live rpm (changing the motor's fuel/settings should visibly change the contraption's
speed); build a large/heavy contraption with several active miners on a comparatively weak
motor and confirm the whole thing visibly slows or stalls rather than spinning at full unlimited
speed regardless of load.
