# Optimization + De-Bukkit TODO

CraftEngine 26.6.2 / MC 1.21.11 / Paper / Java 21. Ranked highest-impact first.
Status: [ ] todo  [~] in progress  [x] done

## Cross-cutting root causes
- `CustomBlockData.from(level,pos)` allocates a wrapper + double map lookup + `getBlockAt` per read/write; used by ALL fluid/gas/conveyor logic every tick. `util/CustomBlockData.java:725-746`.
- Bukkit `getNearbyEntities(...)` in tick paths instead of NMS `level.getEntitiesOfClass(class, AABB, predicate)` (+ `Vector.clone()`/`Location` allocs).

## Ranked findings
- [x] 1. HIGH — `fluid/FluidCarrierImpl.java:14-16` + `gas/GasCarrierImpl.java:17,40,62` — `CustomBlockData.from()` per getStored/insert/extract. Cache the PDC handle per block-entity, or move storage into NMS block-entity CompoundTag.
- [ ] 2. HIGH — `fluid/TankBlockBehavior.java:298-320` — recursive tank-column scan calls getStored() per block (depth up to 256). Cache column top-Y/type, update on place/break.
- [ ] 3. HIGH — `AbstractMachineBlockEntity.java:~814 + 1570/1613/1645/1662` — `CraftItemStack.asBukkitCopy/asNMSCopy` round-trips in upgrade-id resolution (per tick) + funnel transit (per item). Resolve custom item IDs from NMS stack/tag; keep funnel transit in NMS ItemStack.
- [~] 4. HIGH/MED (DONE setChanged: sync only when viewers present + menu.tick() early-return when no viewers. TODO: precompute IO faces for input-pull loop) — `AbstractMachineBlockEntity.java:~1765 setChanged()->menu.syncFromMachine()` (re-syncs all slots) + input-pull loop `~1327-1341` (6 getBlockState/tick regardless of IO faces). Sync only dirty slots; precompute actual input/output face arrays.
- [x] 5. HIGH/MED (recomputeUpgrades throttled to 10-tick cadence + immediate on bumpOverclock; pressure restamp only when pressure differs) — `MachinePumpBlockEntity.java:215-241 recomputeUpgrades()` + `:444 writeTank` — both unconditional every tick. Dirty-flag upgrades (invalidate on slot change); cache tank CustomBlockData handle, write only on change.
- [x] 6. MED-HIGH (light: removed split() allocs; kept persisted String format) — `FluidTransferHelper.java:214-228` + `GasPipeBehavior.java:301-306` — string loop-detection (`history.split(";")` + `x,y,z` concat) per transfer. Replace with `long[3]` of `BlockPos.asLong()`, O(3) membership, zero alloc.
- [x] 7. (DONE NMS getEntitiesOfClass, velocity via getBukkitEntity().setVelocity) HIGH — `block/behavior/FanBlockBehavior.java:170` — Bukkit `getNearbyEntities(BoundingBox)` per push-distance cell (up to 5x/fan/tick) + forEach lambda. One cached NMS AABB + single `getEntitiesOfClass`.
- [ ] 8. MED-HIGH — `fluid/TankBlockBehavior.java:391-419 updateShapeState()` — allocates ImmutableBlockState + setBlock + separate CustomBlockData.set (two chunk writes) per insert/extract. Skip setBlock when level prop unchanged; batch state+PDC write per tick (dirty flag).
- [ ] 9. MED — `fluid/PumpBehavior.java:~205-213 collectArea()` — 3x3x3 (27-block) scan every tick while pumping. Throttle re-scan every N ticks; remember drained source positions.
- [ ] 10. MED — `gas/GasPipeBehavior.java:105-112` — all 6 dirs x 3 modes (18 tryTransfer) per pipe per tick regardless of connectivity. Cache 6-bit connected mask, attempt only connected faces.
- [x] 11. (DONE pickup scan -> NMS getEntitiesOfClass(ItemEntity)) MED — `conveyor/ConveyorBlockEntity.java:489` Bukkit getNearbyEntities pickup every 5 ticks + `:372/392/425 facing()/slope()/part()` re-parse enums every tick. NMS getEntitiesOfClass(ItemEntity,...); cache facing/slope/part fields invalidated on state change.
- [x] 12. MED (CustomBlockData batch mode: conveyor saveState flushes 1 chunk write instead of ~14; handle cached via from() #1. NOTE: full triple-persistence consolidation NOT done — would break existing belt data) — `conveyor/ConveyorBlockEntity.java` blockData()/saveState — CustomBlockData.from() per access; `CustomBlockData.set()` calls save() (PDC write) on EVERY set (~6-7 writes/dirty tick). Cache handle; batch/commit so keys flush in one save().
- [x] 13. (DONE NMS getEntitiesOfClass (randomTick)) MED — `block/behavior/MagnetBlockBehavior.java:60` — Bukkit getNearbyEntities + Vector.clone() per entity (gated to randomTick, lower freq). NMS getEntitiesOfClass + squared-distance predicate, compute delta in place.
- [x] 14. N/A — each face reads its OWN distinct neighbor (6 faces = 6 reads, no redundancy) — `block/behavior/ConnectedBlockBehavior.java:235` — level.getBlockState() per face (x6) in shouldConnect/vanillaMakeState (event-driven). Read 6 neighbor states once into local array.
- [x] 15. ALREADY GUARDED — setBlock only fires when mode actually changes — `AbstractMachineBlockEntity.java:1733-1761 updateMachineModeProperty` — state.get(prop) + potential setBlock every tick, no change guard. Cache last burnTime/mode, early-return when unchanged.
- [x] 16. MED (skip setItem when unchanged) — `machine/menu/MachineMenu.java:~49-52,154-163 updateDynamicSlots()` — rebuilds every dynamic slot ItemStack every 2 ticks per viewer regardless of change. Track last bar values; rebuild only changed slots.
- [x] 17. (DONE cached repeated getBlockState; orb scans already NMS) MED/LOW — `fluid/FluidCollector.java:136,185,265` — Bukkit getEntitiesOfClass(ExperienceOrb, new AABB); `:108-183` same block re-getBlockState 2-4x. NMS entity query; cache block state local.
- [ ] 18. LOW — `AbstractMachineBlockEntity.java:~713-720 dropAllContents` — getWorld() cast + new Location + Bukkit dropItemNaturally (one-shot on break). NMS `new ItemEntity` + addFreshEntity.
- [ ] 19. LOW — `FluidTransferHelper.java:36-40` + `CustomBlockData.getOptional` (`:785`) — Optional wrapping on hot lookups. Return @Nullable + null-check.
- [x] 20. (DONE early-out on empty hand before CE lookups) MED — `conveyor/ConveyorWandListener.java:131-199 onInteract` — CraftEngineItems.getCustomItemId + CraftEngineBlocks.byId on EVERY block click before confirming it's the wand; tickAim raycasts+pathfinds all players every 4 ticks. Cheap early-out (material/tag) before CE lookups; cache last wand item + pathfind result.

## Correctness-adjacent (latent bugs — fix opportunistically)
- [ ] CRITICAL — Conveyor TRIPLE persistence: `PersistentBlockEntity:35-43,146-150` (in-mem CompoundTag→chunk NBT) + `ConveyorBlockEntity:1284-1366` (Bukkit PDC via CustomBlockData) + `:1371-1403` (re-writes progress to NMS CompoundTag); `loadState` reads ONLY the PDC. Stores can flush at different times / crash between → belt desync/rollback. Consolidate onto ONE store (NMS block-entity CompoundTag) — also delivers win #1.
- [ ] `CustomBlockData.from` double map lookup (`:727-728,735-736,742-743`): containsKey then get; should be single get + null check. Miss returns UNCACHED fresh instance — confirm intended.
- [ ] Polling where events exist (magnet/fan/collector scans, wand tickAim) — could be event/redstone gated.
- [ ] Swallowed exceptions around enum parsing (Direction.valueOf) in ConveyorBlockEntity hot-path property reads.

## Top 5 quick wins (recommended order)
1. Cache CustomBlockData/PDC handle per block-entity + fix double CACHE.get in from() (#1,#5,#12).
2. String loop-history -> long[3] BlockPos.asLong() in pipes (#6).
3. Bukkit getNearbyEntities -> NMS getEntitiesOfClass (Fan/Magnet/Conveyor/Collector) (#7,#11,#13,#17).
4. Dirty-flag upgrade recompute + menu slot rebuild; sync only changed slots (#3,#4,#5,#16).
5. Cache tank-column top + precompute machine IO faces (#2,#4).
+ Resolve conveyor triple-persistence (same change as #1).
