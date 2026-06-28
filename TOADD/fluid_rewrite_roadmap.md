# Fluid System Rewrite Roadmap — graph + hydraulic solver

Goal: replace our per-block push/pull fluid logic with a **graph-based hydraulic network
solver**, modeled on [StaticFX/create-pipes-n-physics](https://github.com/StaticFX/create-pipes-n-physics).
That mod solves the whole network globally each tick instead of shoving fluid block-by-block, which
is why it has none of our loop / recirculation / pressure-pinning bugs.

---

## 1. Reference architecture (create-pipes-n-physics)

Package `de.devin.pipesnphysics.engine`.

**Model = a graph, not blocks.**
- `Graph` / `GraphBuilder` / `Node` / `Edge` / `EdgeFlow` — the network is a graph:
  - **Node**: junction (3+ connections), pump, fluid handler (tank/basin/machine), or open end.
  - **Edge**: a *contracted* run of pass-through pipe (2-connection cells collapse into one edge).
    Carries `conductance` (mB/tick per block of head diff), `crestHeight`/`crestPos` (highest point,
    for siphon/cavitation), and an `allowedSign` (one-way / check-valve constraint).
  - Built by BFS from a seed (`build(Level,BlockPos)` → `findSeed` → `discover`), then edge-contraction.
    A **pump** splits its run into two edges so flow direction is enforced through the pump node.

**Solver = hydraulic network (electrical-circuit analogue).** `engine/solve/NetworkSolver`:
- Each node has `capacitance` (mB per block of head — tanks/open columns store; junctions/pumps = 0)
  and `head` (fluid surface elevation in blocks → gravity is just the Y term).
- Each branch has `conductance`, `emf` (pump boost in blocks), `allowedSign`, `crest`.
- Assembles **implicit-Euler** linear system `(C/dt + L) h' = (C/dt) h + pump terms`, L = weighted
  graph Laplacian. Solved by Gaussian elimination (≤128 nodes) or conjugate gradient (larger).
- **Active-set loop** enforces one-way valves: solve → compute `q = G·(Δhead + emf)` → deactivate
  branches violating `allowedSign` → re-solve (≤|branches| rounds).
- **Crest/siphon gating**: computes friction-free reachable potentials, tapers conductance to 0 as a
  rise approaches the suction limit (`CREST_TAPER_FRACTION = 0.25`) → no pumping over impossible lifts.
- **Pruning**: union-find removes zero-capacitance isolated loops so the matrix is non-singular.
- Output: `heads[]` (next-tick head/node), `flows[]` (mB/tick/branch), `netInflow[]` (per-node ΔV).
- **Implicit Euler ⇒ monotonic convergence, no oscillation/sloshing** regardless of dt. This is the
  whole point: tanks equalize smoothly, loops can't ping-pong.

**Tanks**: `physics/TankMassFormulas` + `FluidTankMassMixin` model fluid as head/mass; gravity flow via
`GravityFlowMixin` + `BoundaryColumn` (a fluid column with a free surface). Open ends via `OpenEndPipes`.

**Tick**: `FluidEngine` + `EngineTickHandler` — own the graphs, run the solver once/tick, apply flows.

---

## 2. Our current system + why it breaks

Package `dev.arubik.craftengine.fluid`.

- Storage: per-block `FluidStack` in `CustomBlockData` (PDC), key `FluidKeys.FLUID`
  (now name-serialized — the ordinal bug is fixed).
- Transport: each block ticks and **pushes/pulls to neighbors**:
  - `PipeBehavior`, `TankBlockBehavior.tryTransfer` (UP pull / DOWN push, pipe-only),
    `MachinePumpBlockEntity` (active extract on IN face + push on OUT), `ValveBehavior`,
    `FluidTransferHelper` (transfer/push/pull/balance between adjacent carriers),
    `FluidCarrierImpl` (the per-block insert/extract).
- "Pressure" is an int stamped onto each `FluidStack` and blended on merge.

Concrete failures this caused (all in this session):
- **Recirculation loops** (pump ↔ tank): two independent intake systems + neighbor push with no global
  conservation → fluid bounced (`pullFromInputFaces` sucked back the OUT neighbor).
- **Pressure pinning** ("9p everywhere"): max-merge of stamped pressure; patched with averaging hacks.
- **Direction/geometry fragility**: `transfer()` assumed vertical; `toLocalItemDir` vs fluid dir mismatch.
- **Order-dependent results**: outcome depends on which block ticks first; no equilibrium guarantee.
- **Tank instability**: dual source of truth (PDC `FluidStack` ↔ `fluidtype` blockstate) + ad-hoc
  chain flow. Per-block model has no global mass conservation.

The graph+solver model removes the *class* of bug: one global, conservative, convergent solve replaces
N order-dependent local pushes.

---

## 3. Concept mapping (reference → ours)

| Reference | Ours today | Rewrite target |
|---|---|---|
| `Node` (junction/pump/handler/open-end) | implicit (each block) | `FluidNode` (pos, kind, capacity, head) |
| `Edge` (contracted run + conductance/crest) | each pipe block ticks | `FluidEdge` (a,b, conductance, crest, sign) |
| `Graph` + `GraphBuilder` (BFS + contract) | none | `FluidGraph` + `FluidGraphBuilder` |
| `NetworkSolver` (Laplacian/implicit Euler) | `FluidTransferHelper` push/pull | `FluidNetworkSolver` |
| `capacitance` + `head` (tanks, gravity by Y) | `FluidTank` amount + stamped pressure | node capacitance + head=Y+fill |
| `emf` pump boost | pump stamps pressure int | pump edge `emf` (blocks of lift) |
| `allowedSign` check-valve | `ValveBehavior` ad-hoc | edge `allowedSign` |
| crest/siphon gating | none (pump spills) | crest height per edge + taper |
| `FluidEngine`/`EngineTickHandler` | per-block tickers | one `FluidEngine` per dimension |
| storage `FluidStack` in PDC | keep (good) | keep PDC as the per-node store |

We are NOT on Create/NeoForge — no mixins/capabilities. Equivalents:
- Their `IFluidHandler` handlers → our `FluidCarrier` + machine `fluidTanks`.
- Their Create pipe blocks → our CE custom pipe/tank/pump blocks (CustomBlockData-backed).
- Their mixins (gravity, open-end) → explicit code in our behaviors/engine.

---

## 4. Phased rewrite plan

Each phase compiles + deploys independently; old path stays until the new one is proven, then deleted.

### Phase 0 — Foundation & invariants (no behavior change)
- Keep name-based `FluidStack`/`GasStack` serialization (done).
- Add `FluidNetworkConfig` constants (dt, tolerances, direct-solve limit, crest taper) mirroring the
  reference (`FLOW_TOLERANCE=1e-7`, `DIRECT_SOLVE_LIMIT=128`, `CREST_TAPER_FRACTION=0.25`).
- Decide units: head in **blocks**, conductance in **mB/tick per block**, capacity in **mB**.
- Write a tiny linear-algebra util (`HydraulicMatrix`: dense Gaussian elimination + CG) ported from
  `NetworkSolver`. Unit-test it offline (mirror `NetworkSolverTest`).

### Phase 1 — Graph model + builder (read-only, parallel to live system)
- New `fluid.graph` package: `FluidNode`, `FluidEdge`, `FluidGraph`, `FluidGraphBuilder`.
- `FluidGraphBuilder.build(Level, BlockPos)`: BFS over our pipe/tank/pump/valve blocks
  (reuse `FluidCarrier` detection + `ConnectableBlockBehavior` connectable faces — pipes connect
  up/down + configured faces, tanks top/bottom, pump IN/OUT). 2-connection pipe cells → pass-through,
  contracted into edges; junctions/pumps/tanks/open-ends → nodes.
- Map each block's `conductance` from pipe tier (config), `crestHeight` from max Y along the run,
  `emf` from pump rating, `allowedSign` from valve direction.
- Add a debug command (mirror `PipeGraphCommand`) to dump the graph for a network → verify the builder
  against real layouts before wiring any flow.

### Phase 2 — Solver port
- Port `NetworkSolver` → `FluidNetworkSolver` (NodeSpec/BranchSpec/Result equivalents): assemble
  `(C/dt + L)`, active-set for `allowedSign`, crest gating, capacitance pruning.
- Unit-test with the reference's scenarios (two tanks equalize, pump lifts to a head, check valve blocks
  backflow, siphon over a crest fails past suction limit).

### Phase 3 — Engine + apply (the switch)
- `FluidEngine` (per `Level`): owns graphs, dirty-tracks networks, rebuilds on block place/break
  (incremental: only the touched network), runs `FluidNetworkSolver` once/tick, writes `netInflow`
  back into each node's PDC `FluidStack` (clamped to capacity, type-checked).
- `EngineTickHandler`: one tick entry instead of every pipe/tank/pump ticking.
- Tanks: node capacitance = tank capacity; head = base Y + fill fraction; the solver moves fluid; the
  behavior only renders (`updateShapeState`) from the resulting amount. Removes the dual-source-of-truth.
- Pumps: become a node with two edges + `emf`; drop the active extract/push + `pullFromInputFaces`
  override + the carrier-extract hack in `MachinePumpBlockEntity` (all superseded by the solver).
- World boundaries: open-end pipes + cauldron/source interaction become source/sink nodes
  (reuse `FluidCollector`/`FluidPlacer` at the boundary only).

### Phase 4 — Delete the old path
- Remove `FluidTransferHelper` push/pull/balance/transfer, per-block `tryTransfer`,
  `PipeBehavior` flow tick, pump active IO, `ValveBehavior` ad-hoc flow. Keep `FluidCarrierImpl`
  ONLY as the per-node PDC accessor (getStored/insert/extract by amount), or fold into nodes.
- Keep: `FluidStack`, `FluidType`, `FluidTank`, `FluidKeys`, `FluidCollector`, `FluidPlacer`,
  `FluidItemConverter`, `FluidReactions` (item↔fluid + world boundary — still needed).

### Phase 5 — Gas system (same engine, second fluid family)
- Gas pipes/pumps have the same topology. Generalize the engine over a `CarrierKind` (FLUID|GAS) or
  instantiate a second engine. Reuse solver; gas "head" = pressure (no gravity term, or inverted).
- Migrate `GasCarrierImpl`/`GasPipeBehavior`/`GasPumpBlockEntity`/`GasTank` the same way.

---

## 5. Risks / decisions to confirm with user

- **Scope**: full rewrite is large (≈ all of `fluid.behavior` + pump/tank entities + a new solver).
  Phases 1–2 are safe/parallel; Phase 3 is the risky switch. Recommend landing 1–2 first, demo the
  graph dump + solver tests, then commit to the switch.
- **Performance**: solver is O(n³) Gaussian (n≤128 nodes/network) or CG. Most player networks are
  small; cap network size + fall back to CG. Solve only dirty networks, not every tick from scratch.
- **CE-native, no Bukkit/mixins**: builder must rely on our `FluidCarrier`/`ConnectableBlockBehavior`
  detection, not Create capabilities.
- **Migration**: existing placed networks keep working (PDC per-node store is unchanged); only the
  *transport* mechanism changes. No data migration needed beyond the serialization fix already done.
- **Pressure semantics change**: stamped-int pressure → solver heads/emf. The `pressure` field on
  `FluidStack` becomes vestigial for transport (can keep for display, or drop).

---

## STATUS (live)

- **Storage migration — DONE & deployed.** Pump/machines (scalars incl. burnTime, inventory, fluid) and
  tanks (stored fluid) now persist via the CraftEngine-native block-entity NBT (`saveCustomData` `tag`),
  not the Bukkit PDC that wasn't surviving restarts. No new blockstates; tank stacking untouched.
- **`FluidStack`/`GasStack` serialization — DONE.** Type persisted by NAME, not enum ordinal (the
  "stored liquid changes to another" bug).
- **Phase 0 — DONE.** Solver constants + dense Gaussian elimination embedded in the solver.
- **Phase 1 — DONE & deployed.** `fluid.graph`: `FluidNode`/`FluidEdge`/`FluidGraph`/`FluidGraphBuilder`
  (BFS over connected carriers, kind classification). Debug dump: `/cep fluid graph`.
- **Phase 2 — DONE & deployed.** `FluidNetworkSolver` (implicit-Euler `(C/dt+L)h'=(C/dt)h+emf`,
  active-set for one-way valves). Self-test: `/cep fluid solvetest` (equalize / check-valve / pump-lift
  all pass; hand-verified). CG path + crest/siphon gating = documented follow-ups.
- **Phase 3 (apply core) — DONE & deployed.** `FluidEngine.step(level, graph)`: build specs from live
  stores → solve one step → write ΔV back (PIPE/TANK mutated, PUMP/HANDLER as fixed-head boundary).
  Manual trigger: `/cep fluid step`. **Gated ON PURPOSE** — runs beside the still-live old transport so
  it can be validated before the cutover.

### Remaining (gated on in-world validation of the apply)
These replace/remove live behavior, so they MUST follow validating `FluidEngine.step` against real
layouts (use `/cep fluid graph` + `/cep fluid step`) — a blind always-on cutover would regress the
just-stabilized fluid system.

- **Phase 3 (rest):** always-on per-tick `FluidEngine` + dirty-network tracking; pump `emf` wiring
  (pumps drive their edges instead of active push/pull); world source/sink boundary (open ends,
  cauldrons) as fixed nodes; multi-type networks.
- **Phase 4:** delete the old transport (`FluidTransferHelper` push/pull/balance, `tryTransfer`,
  per-block pump IO, `ValveBehavior` flow). Keep `FluidStack/Type/Tank/Keys/Collector/Placer/
  Converter/Reactions` + `FluidCarrierImpl` as the per-node store accessor.
- **Phase 5:** gas — same engine over a `CarrierKind`, migrate `Gas*`.

## 6. First concrete step

Implement Phase 1 (`fluid.graph` builder) + a `/cep fluidgraph` debug dump, validated against a real
pipe/tank/pump layout in-world — no flow change yet. That de-risks everything downstream.
