package dev.arubik.craftengine.contraption.assembly;

import dev.arubik.craftengine.contraption.MovementBehavior;
import dev.arubik.craftengine.contraption.glue.GlueGraph;
import dev.arubik.craftengine.contraption.glue.GlueRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.arubik.craftengine.block.behavior.ConnectedBlockBehavior;
import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.block.entity.PersistentWorldlyBlockEntity;
import dev.arubik.craftengine.contraption.behavior.MovementBehaviorRegistry;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.level.BukkitContraptionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.util.Key;

/**
 * Bearing-relative capture/restore of a glued structure (CONTRAPTIONS.md §1 "Capture" /
 * Phase 1 "prove the capture/restore round-trip for a stationary structure"). Reads each
 * world block's {@link BlockState} plus its real vanilla {@link BlockEntity} (if any) via
 * {@code BlockEntity#saveWithFullMetadata} — CraftEngine's own controllers live inside real
 * vanilla block entities too (confirmed against CraftEngine's own lookup path), so this one
 * mechanism covers both plain vanilla blocks (furnace/chest/hopper/...) and CE-controlled
 * ones; no separate CE-specific serialization path needed.
 *
 * <p>Also auto-resolves {@link MovementBehaviorRegistry} during capture (Create's own
 * pattern): a captured block whose CraftEngine custom-block {@link Key} is registered gets
 * a {@link MovementBehavior} built for it automatically — see {@link #capture}'s returned
 * {@link Result#autoBehaviors()}.
 */
public final class ContraptionCapture {

    private ContraptionCapture() {
    }

    /** {@code level} is populated and ready to render; {@code autoBehaviors} still need attaching to a {@link ContraptionState}. */
    public record Result(ContraptionLevel level, List<MovementBehavior> autoBehaviors) {
    }

    /**
     * Reads every block in {@code worldPositions} into a fresh {@link ContraptionLevel},
     * keyed by offset from {@code bearingWorldPos}, auto-resolving any registered
     * {@link MovementBehaviorRegistry} behavior along the way. Does NOT remove anything
     * from the real world — see {@link #removeFromWorld} for that half.
     */
    public static Result capture(Level realLevel, Set<BlockPos> worldPositions, BlockPos bearingWorldPos) {
        ContraptionLevel level = ContraptionLevel.create(realLevel,
                bearingWorldPos.getX(), bearingWorldPos.getY(), bearingWorldPos.getZ(), 0);

        for (BlockPos pos : worldPositions) {
            BlockState state = realLevel.getBlockState(pos);
            BlockPos local = ContraptionMath.toLocal(pos, bearingWorldPos);
            // quiet=true: suppress vanilla's neighbor/shape-update pass while placing the whole
            // batch -- Set<BlockPos> iteration order is unspecified, so a support-dependent block
            // (lever, redstone dust, ...) can easily land before its supporting neighbor; a noisy
            // setBlock would run canSurvive against that still-air neighbor and pop the block off
            // immediately. See ContraptionLevel#putBlock(BlockPos, BlockState, boolean) javadoc.
            level.putBlock(local, state, true);

            BlockEntity be = realLevel.getBlockEntity(pos);
            if (be != null) {
                level.putBlockEntity(local, be.saveWithFullMetadata(realLevel.registryAccess()));
            }

            // CraftEngine custom blocks (StorageBlockEntity et al.) do NOT hang off a real
            // vanilla BlockEntity — realLevel.getBlockEntity(pos) above returns null for them
            // (confirmed: CraftEngine's own controllers are bookkept in a parallel CEChunk,
            // reached only via BukkitBlockEntityTypes/PersistentBlockEntity#getIfLoaded — see
            // this class's own class javadoc). Capture that controller's data separately so a
            // storage block's inventory actually survives the round-trip.
            PersistentBlockEntity ce = PersistentBlockEntity.getIfLoaded(realLevel, pos);
            if (ce != null) {
                try {
                    byte[] bytes = ce.serializeToBytes();
                    level.putCeControllerData(local, bytes);
                    // Also feed the SAME bytes into the controller that putBlock(...) above just
                    // constructed fresh (empty) INSIDE the ContraptionLevel itself — not just the
                    // ceControllerData cache, which is only ever consumed later by
                    // restoreBlockEntity() on disassembly. Without this, a block living in the
                    // hologram (e.g. a fluid_block_tank's Controller) starts with zero fluid even
                    // though the real block it was captured from was full: the fluid render then
                    // legitimately sees fill<=0 and never draws anything, even though the shell
                    // (built from static block-state props, unaffected by this) renders fine.
                    PersistentBlockEntity inLevel = PersistentBlockEntity.getIfLoaded(level.serverLevel(), local);
                    if (inLevel != null) {
                        inLevel.loadFromBytes(bytes);
                    }
                } catch (Throwable ignored) {
                    // best-effort — a capture failure here shouldn't abort the whole structure capture
                }
            }
        }
        // Settle pass (2026-07-01 session — "las pipes... no se llegan a conectar bien al pasar
        // al mundo contraption... al volver al real estan bien"): quiet placement above
        // deliberately skips UPDATE_NEIGHBORS to stop levers/redstone from popping off mid-batch
        // (see putBlock's javadoc), but that ALSO means any block whose visual/connection STATE
        // is normally derived from a neighbor-triggered shape update (pipes, fences, glass panes,
        // any connectable) never gets that derivation run at all inside the ContraptionLevel —
        // it just keeps whatever blockstate it happened to carry from the real world, which was
        // computed against REAL neighbors that may not even be part of this captured structure.
        // Now that every captured block is actually present, one deliberate neighbor-update pass
        // per position is safe (no support blocks are still-missing anymore) and lets every
        // connectable block correctly re-derive its connections against its ACTUAL neighbors
        // inside the mini-dimension.
        //
        // TWO passes, not one (2026-07-02 follow-up — "algunas [pipes] quedan bien unidas, otras
        // no", inconsistent within the SAME structure): updateNeighborsAt(local, ...) notifies
        // local's 6 NEIGHBORS (vanilla semantics: it calls neighborChanged/updateShape on the
        // blocks AROUND pos, not on pos itself), and ConnectedBlockBehavior#vanillaMakeState
        // rebuilds a pipe's connection state FROM SCRATCH each time it fires (starts at
        // this.block().defaultState(), all faces NONE, then re-derives every face by reading each
        // neighbor's CURRENT live blockstate — see ConnectedBlockBehavior.java shouldConnect/
        // vanillaMakeState). So a given pipe P only gets re-derived when one of ITS neighbors is
        // visited in this loop; with a single unordered pass over a Set<BlockPos>, whichever of
        // P's neighbors happens to be iterated LAST determines whether P's final derivation runs
        // against neighbors that have themselves already settled, or against neighbors still in
        // a stale/pre-settle state. That race is exactly why some pipes end up connected and
        // others don't within one capture. Running the whole pass twice guarantees every pipe's
        // LAST recomputation (pass 2) happens only after every block in the structure already
        // received its pass-1 update, so by pass 2 every neighbor's state has converged.
        //
        // ORDERED settle (2026-07-17 session — user, Spanish: "al armar un bearing la redstone a
        // veces sigue rompiéndose... copiar los estados... instanciarlo como estaba"): this settle
        // pass fires a REAL neighbor block-update on each captured cell, and — exactly like the
        // disassemble RESTORE path already documents (see {@link #placeOrdered}'s root-cause
        // javadoc, "al deconstruir la redstone se termina rompiendo... se rompe la mitad") — when
        // that update lands on a SUPPORT-DEPENDENT block (redstone dust, repeater/comparator,
        // torch, lever, rail, ...) vanilla re-runs its {@code canSurvive}/{@code neighborChanged}
        // logic and can call {@code Block#updateOrDestroy}, POPPING the component right out of the
        // hologram if, at that instant, its evaluation against the still-transient local network/
        // supports says it can't stay. Iterating an unordered {@code Set<BlockPos>} made that
        // order-dependent — hence the user's "a veces": a redstone cell whose turn came before the
        // rest of its supports/network had themselves settled popped out and was silently lost from
        // the contraption for the entire round-trip (it never comes back on disassemble). The
        // capture side was never given the same fix the restore side got. Partition the cells the
        // SAME way {@link #placeOrdered} does ({@link #isSupportDependent}) and ALWAYS settle
        // STRUCTURAL (self-supporting) cells before SUPPORT-DEPENDENT ones, so every dependent's
        // supports are already stable the moment its own neighbor-update fires. Still TWO passes for
        // the pipe-connection convergence documented above; the structural-first ordering is applied
        // within each pass.
        List<BlockPos> structuralLocals = new ArrayList<>();
        List<BlockPos> dependentLocals = new ArrayList<>();
        for (BlockPos pos : worldPositions) {
            BlockPos local = ContraptionMath.toLocal(pos, bearingWorldPos);
            if (isSupportDependent(level.getBlockState(local))) {
                dependentLocals.add(local);
            } else {
                structuralLocals.add(local);
            }
        }
        for (int pass = 0; pass < 2; pass++) {
            settleLocalNeighbors(level, structuralLocals);
            settleLocalNeighbors(level, dependentLocals);
        }
        // Connection-MODEL settle (2026-07-19 — "las pipes siguen funcionando pero el blockstate y su
        // modelo mostrado no es el correcto, ni en el contraption world ni en el render"). The neighbor
        // settle above drives vanilla's neighborChanged broadcast, but a CraftEngine connected block
        // ({@link ConnectedBlockBehavior}: pipes, gas pipes, cables) derives its six connection faces
        // ONLY from the updateShape hook — it does not override neighborChanged — so that pass never
        // recomputed a pipe's MODEL at all. Fluid still flowed (routing is topology/capability based, not
        // blockstate based), which is exactly why it "sigue funcionando" while showing the wrong model.
        // Re-derive each connectable's own state directly (vanillaMakeState reads its live neighbours in
        // the hologram, identical to what updateShape returns) and write it back, so the displayed model
        // matches the assembled structure in BOTH the joinable contraption world and the render. Two
        // passes for the same convergence reason the neighbor settle documents.
        Level nms = level.serverLevel();
        for (int pass = 0; pass < 2; pass++) {
            settleConnectedModels(nms, structuralLocals);
            settleConnectedModels(nms, dependentLocals);
        }
        return new Result(level, resolveAutoBehaviors(level));
    }

    /**
     * One in-hologram neighbor-update pass over {@code locals} — the capture-side twin of
     * {@link #settleNeighbors} (the real-world restore pass). Split out so {@link #capture}'s settle
     * can run STRUCTURAL cells before SUPPORT-DEPENDENT ones (see the ordered-settle comment in
     * {@link #capture}) instead of one unordered sweep that could pop redstone out of the hologram.
     */
    private static void settleLocalNeighbors(ContraptionLevel level, List<BlockPos> locals) {
        for (BlockPos local : locals) {
            try {
                level.updateNeighborsAt(local, level.getBlockState(local).getBlock());
            } catch (Throwable ignored) {
                // best-effort — a single misbehaving neighbor update shouldn't abort the capture
            }
        }
    }

    /** Flags for a connection-model rewrite: refresh the client/CE renderer, but cascade no shape/neighbor update. */
    private static final int CONNECT_MODEL_FLAGS =
            Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;

    /**
     * Re-derives and writes the connection state of every CraftEngine connected block among {@code positions}
     * (see the connection-MODEL settle comment in {@link #capture}). For each cell that carries a
     * {@link ConnectedBlockBehavior}, computes its faces from scratch against its live neighbours via
     * {@code vanillaMakeState} — the exact state its {@code updateShape} hook would return — and setBlock's
     * it into {@code level} when it differs, refreshing the displayed model. Non-connectable and vanilla
     * cells are skipped. Uses {@link #CONNECT_MODEL_FLAGS}: no cascading shape/neighbor update (nothing to
     * pop redstone or re-trigger this pass), while CraftEngine's palette hook still fires on the state
     * change and refreshes its renderer.
     *
     * <p>Works on ANY {@link Level} against ANY position set, so it serves BOTH the capture side (the
     * hologram's {@code serverLevel()} + local cells) and the disassemble side (the real world + the
     * restored world positions) — the {@code neighborChanged}-only settle each of those runs never
     * recomputes a connected block's faces.
     */
    private static void settleConnectedModels(Level level, Iterable<BlockPos> positions) {
        for (BlockPos pos : positions) {
            try {
                BlockState state = level.getBlockState(pos);
                ImmutableBlockState current = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
                if (current == null) {
                    continue;
                }
                ConnectedBlockBehavior conn = asConnected(current.behavior());
                if (conn == null) {
                    continue;
                }
                ImmutableBlockState recomputed = (ImmutableBlockState) conn.vanillaMakeState(pos, level);
                if (recomputed != null && !recomputed.equals(current)) {
                    level.setBlock(pos, (BlockState) recomputed.customBlockState().minecraftState(), CONNECT_MODEL_FLAGS);
                }
            } catch (Throwable ignored) {
                // best-effort — one block's failed re-derivation shouldn't abort the pass
            }
        }
    }

    /** Unwraps a possibly-composite behaviour to its {@link ConnectedBlockBehavior}, or null if it has none. */
    private static ConnectedBlockBehavior asConnected(Object behavior) {
        if (behavior instanceof ConnectedBlockBehavior connected) {
            return connected;
        }
        if (behavior instanceof net.momirealms.craftengine.core.block.behavior.BlockBehavior bb) {
            try {
                return bb.getFirst(ConnectedBlockBehavior.class);
            } catch (Throwable ignored) {
                // getFirst can throw on an unusual composite — treat as "no connected behavior"
            }
        }
        return null;
    }

    /**
     * Captures the structure's internal glue topology into {@code level} as LOCAL edges
     * (2026-07-03 session — "persistir los glue block en el nbt del contraption"). Reads the real
     * world's {@link GlueRegistry} for every edge among the just-captured cells, converts both
     * endpoints to bearing-local coordinates, and stores them on the level so
     * {@code ContraptionStructureNbt} can serialize them (surviving restart — the in-memory
     * registry does not) and {@link #restoreGlue} can re-apply them on disassemble.
     *
     * <p><b>Always-stick guarantee</b> ("siempre todos sus bloques se peguen"): after collecting
     * the real glue edges, this forces the topology to be FULLY connected — any captured cell not
     * reachable through real glue (e.g. a multiblock member pulled in by
     * {@code ContraptionAssembler#expandMultiblockMembers} without its own glue edge, or a legacy/
     * singleton capture) is chained into the structure with a synthetic edge, so on disassemble the
     * whole contraption re-sticks as one piece and can be re-assembled from any of its blocks.
     */
    public static void captureGlueEdges(ResourceKey<Level> worldId, ContraptionLevel level, BlockPos origin) {
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        Set<BlockPos> localSet = new HashSet<>(level.localPositions());
        List<long[]> edges = new ArrayList<>();
        java.util.Set<Long> seenEdges = new HashSet<>();
        // Union-find over local cells (keyed by packed BlockPos long) to track connectivity.
        java.util.Map<Long, Long> parent = new java.util.HashMap<>();
        for (BlockPos local : localSet) {
            parent.put(local.asLong(), local.asLong());
        }
        for (BlockPos local : localSet) {
            BlockPos world = ContraptionMath.toWorld(local, origin);
            for (BlockPos neighborWorld : graph.neighbors(world)) {
                BlockPos neighborLocal = ContraptionMath.toLocal(neighborWorld, origin);
                if (!localSet.contains(neighborLocal)) {
                    continue; // glued to something outside this capture — not an internal edge
                }
                long la = local.asLong();
                long lb = neighborLocal.asLong();
                long lo = Math.min(la, lb);
                long hi = Math.max(la, lb);
                long edgeKey = lo * 31 + hi; // cheap dedup of the undirected edge
                if (seenEdges.add(edgeKey)) {
                    edges.add(new long[] {lo, hi});
                    union(parent, lo, hi);
                }
            }
        }
        // Always-stick: chain every still-disconnected component to a single anchor cell.
        Long anchor = null;
        for (BlockPos local : localSet) {
            long key = local.asLong();
            if (anchor == null) {
                anchor = key;
                continue;
            }
            if (find(parent, key) != find(parent, anchor)) {
                edges.add(new long[] {Math.min(anchor, key), Math.max(anchor, key)});
                union(parent, anchor, key);
            }
        }
        level.setGlueEdgesLocal(edges);
    }

    private static long find(java.util.Map<Long, Long> parent, long x) {
        long root = x;
        while (parent.get(root) != root) {
            root = parent.get(root);
        }
        // path-compress
        long cur = x;
        while (parent.get(cur) != root) {
            long next = parent.get(cur);
            parent.put(cur, root);
            cur = next;
        }
        return root;
    }

    private static void union(java.util.Map<Long, Long> parent, long a, long b) {
        long ra = find(parent, a);
        long rb = find(parent, b);
        if (ra != rb) {
            parent.put(ra, rb);
        }
    }

    /**
     * Re-establishes the world {@link GlueRegistry} glue for a contraption's blocks as they land
     * on disassemble (2026-07-03 session). Replaces the old registry re-key approach
     * ({@code GlueGraph#translateAll}), which silently did nothing after a restart (the in-memory
     * registry was empty) — this instead rebuilds the glue directly from the level's persisted
     * LOCAL edges (see {@link #captureGlueEdges}), rotated + translated to the exact world
     * positions {@link #restoreRotated} writes into, so it works identically whether the glue came
     * from a live session or was just loaded back from the bearing's NBT after a restart.
     *
     * <p>First scrubs any stale glue nodes still keyed at the ORIGIN capture footprint (now air),
     * then glues the landing positions. If the level carries no saved edges at all (legacy
     * capture), every restored cell is still chained into one component here, honoring the
     * always-stick guarantee. {@code quarterTurns} MUST match the value passed to
     * {@link #restoreRotated} for this same disassembly.
     */
    public static void restoreGlue(ResourceKey<Level> worldId, ContraptionLevel level, BlockPos origin,
            BlockPos snappedBearing, int quarterTurns) {
        if (level == null) {
            return;
        }
        GlueGraph graph = GlueRegistry.graphFor(worldId);
        // Scrub stale origin-footprint nodes (blocks were removed from there at capture; if this is
        // the same session they may still linger in the registry keyed at the original positions).
        for (BlockPos local : level.localPositions()) {
            graph.removeNode(ContraptionMath.toWorld(local, origin));
        }
        // Map each local cell to its landing world position (rotated like restoreRotated does).
        java.util.Map<Long, BlockPos> localToWorld = new java.util.HashMap<>();
        for (BlockPos local : level.localPositions()) {
            BlockPos rotatedLocal = rotateLocal(local, quarterTurns);
            localToWorld.put(local.asLong(), ContraptionMath.toWorld(rotatedLocal, snappedBearing));
        }
        List<long[]> edges = level.glueEdgesLocal();
        if (edges.isEmpty()) {
            // Legacy/no-glue capture — chain everything so it still sticks.
            BlockPos anchor = null;
            for (BlockPos world : localToWorld.values()) {
                if (anchor == null) {
                    anchor = world;
                } else if (!anchor.equals(world)) {
                    graph.glue(anchor, world);
                }
            }
            return;
        }
        for (long[] edge : edges) {
            BlockPos wa = localToWorld.get(edge[0]);
            BlockPos wb = localToWorld.get(edge[1]);
            if (wa != null && wb != null && !wa.equals(wb)) {
                graph.glue(wa, wb);
            }
        }
    }

    /**
     * Re-derives every registered {@link MovementBehaviorRegistry} behavior for a
     * {@link ContraptionLevel}'s CURRENT contents — used both by {@link #capture} (fresh
     * structure) and by lazy load-on-chunk-load restore (structure read back from a `.nbt`
     * blob), so a contraption that persisted across a restart re-attaches the same
     * behaviors it had before saving, without duplicating this resolution logic.
     */
    public static List<MovementBehavior> resolveAutoBehaviors(ContraptionLevel level) {
        List<MovementBehavior> autoBehaviors = new ArrayList<>();
        for (BlockPos local : level.localPositions()) {
            BlockState state = level.getBlockState(local);
            Key blockKey = customBlockKey(state);
            MovementBehavior behavior = MovementBehaviorRegistry.resolve(blockKey, local, state);
            if (behavior != null) {
                autoBehaviors.add(behavior);
            }
        }
        return autoBehaviors;
    }

    /** The CraftEngine custom-block {@link Key} for {@code state}, or null if it isn't a CE custom block. */
    private static Key customBlockKey(BlockState state) {
        try {
            return BlockStateUtils.getOptionalCustomBlockState(state)
                    .map(ImmutableBlockState::owner)
                    .filter(owner -> owner != null)
                    .map(owner -> owner.value().id())
                    .orElse(null);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Sets every captured cell to air in the real world (the "remove real blocks" assembly
     * step). Deletes the real vanilla {@link BlockEntity} at each position FIRST — vanilla
     * container blocks (a hopper, a chest — confirmed the actual repro) unconditionally drop
     * their contents from their own {@code Block#onRemove}/{@code Containers
     * #dropContentsOnDestroy} hook whenever the block type changes, re-fetching whatever block
     * entity is STILL there at that instant — not something this project's code can intercept
     * or flag-gate (it isn't this project's hook). By the time that runs there is nothing left
     * for it to find: {@link #capture} already read the block entity's full NBT into the
     * {@link ContraptionLevel} moments before this method runs, so nothing is lost, and
     * pre-deleting it here means vanilla's own drop-on-destroy has nothing left to drop.
     *
     * <p><b>CraftEngine-controlled blocks are a SEPARATE mechanism</b> — confirmed by reading
     * CraftEngine's own {@code WorldStorageInjector#compareAndUpdateBlockState}: it is
     * ByteBuddy-injected directly into the chunk-section palette storage, and fires on ANY
     * state change at a position (including this method's own {@code setBlock(AIR)} call
     * below), completely independent of {@code level.removeBlockEntity(pos)} above (which only
     * touches the real vanilla block-entity map — CraftEngine's controllers aren't stored
     * there). When the new state is vanilla and the previous CE state
     * {@code hasBlockEntity()}, that injected hook calls {@code BlockEntity#preRemove() ->
     * controller.onRemove()} synchronously, right there inside {@code setBlock}. For
     * {@code StorageBlockEntity} that's exactly {@code dropAllContents()} — so the inventory
     * gets dropped as ground items no matter what order {@code removeBlockEntity}/
     * {@code setBlock} run in; reordering them doesn't help because the trigger IS
     * {@code setBlock}, not {@code removeBlockEntity}. The fix ("copiarlo, limpiarlo,
     * removerlo, y borrarlo"): after capture has already read the controller's data (see
     * {@link #capture}), clear its live inventory IN PLACE — via
     * {@link PersistentWorldlyBlockEntity#clearContent()}, a non-dropping clear — so whichever
     * hook fires finds nothing left to drop. Vanilla chest/barrel/hopper have no CE controller
     * at their position, so this lookup is simply null for them and this step is a no-op —
     * their already-working path above is untouched.
     *
     * <p><b>Synthetic-removal signal.</b> A {@code BlockBehavior} hook reached from this method's
     * {@code setBlock(AIR)} loop cannot otherwise tell a capture from a player or piston break.
     * Behaviors that must NOT run their real teardown during a capture (the belt in
     * {@code ConveyorBlockEntity#onBroken} tears down the WHOLE line when its middle goes away —
     * catastrophic if a capture triggered it) ask {@link #isRemovingForCapture()} instead of
     * guessing. See that flag's javadoc for why a proxy such as "is the container empty?" is not
     * a sound substitute.</p>
     */
    public static void removeFromWorld(Level level, Set<BlockPos> worldPositions) {
        boolean prevRemovingForCapture = removingForCapture;
        removingForCapture = true;
        try {
            removeFromWorld0(level, worldPositions);
        } finally {
            // MUST be restored on the exception path too: a stuck `true` would leave every
            // conveyor in the world permanently un-teardownable (breaks would silently no-op).
            removingForCapture = prevRemovingForCapture;
        }
    }

    /**
     * True while {@link #removeFromWorld} is setting captured cells to air, i.e. while any
     * {@code affectNeighborsAfterRemoval} / {@code onRemove} hook reached from that loop is
     * running. Lets a block behavior tell a SYNTHETIC removal ("this structure is becoming a
     * contraption hologram; its full state was already read into the ContraptionLevel moments
     * ago") apart from a GENUINE break (player in either game mode, or a piston), which are
     * otherwise identical at the behavior hook.
     *
     * <p>Belt and braces, deliberately: {@link #removeFromWorld}'s quiet flags omit
     * {@code UPDATE_NEIGHBORS}, and vanilla only fires {@code affectNeighborsAfterRemoval} when
     * {@code (flags & UPDATE_NEIGHBORS) != 0 || movedByPiston} (verified in
     * {@code LevelChunk#setBlockState}, Paper 1.21.11 mapped sources), so today no such hook is
     * actually reached from here. The signal exists so a behavior's correctness does not silently
     * depend on that flag choice — the quiet flags were themselves introduced later, to fix an
     * unrelated duplication bug, and could be revisited the same way. {@code onRemove} (CraftEngine's
     * palette-level injection) DOES still fire from here regardless of flags.
     *
     * <p>This replaces an earlier {@code isEmpty()} proxy in {@code ConveyorBlockEntity#onBroken}
     * that inferred "this is a capture" from the segment's container being empty (capture clears
     * content via {@link PersistentWorldlyBlockEntity#clearContent()} just below, so a capture is
     * always empty). The inference does not hold in the other direction: a belt a player breaks is
     * usually empty too, so the proxy swallowed the real teardown for the common case (user report:
     * "las conveyor al romperlas por el medio no se rompe todo", and "si al romper el inicio o final
     * no actualiza el conveyor anterior"). An explicit signal set by the capture itself cannot be
     * confused with a genuine break.
     *
     * <p>Not volatile/thread-local by design, mirroring {@code ConveyorBlockEntity}'s
     * {@code teardownInProgress}: every writer and reader is on the server main thread —
     * {@code removeFromWorld}'s callers are assembly paths driven from a Bukkit command/ticked
     * bearing, {@code Level#setBlock} is main-thread-only anyway, and the hooks that read this
     * run synchronously inside that same {@code setBlock} on the same thread. A cross-thread read
     * is therefore not a supported call, and making the field {@code volatile} would only make
     * such a read reliably observe {@code true} — i.e. skip a legitimate teardown — which is
     * strictly worse than the plain field's non-guarantee.
     */
    private static boolean removingForCapture = false;

    /** @see #isRemovingForCapture */
    public static boolean isRemovingForCapture() {
        return removingForCapture;
    }

    private static void removeFromWorld0(Level level, Set<BlockPos> worldPositions) {
        // BetterModel real-tracker leak fix (2026-07-02 session — "se bugea y termina apareciendo
        // 2 modelos"): CraftEngine's own onRemove()/preRemove() hook chain (see
        // PersistentBlockEntity#onRemove — invoked by WorldStorageInjector on THIS method's own
        // setBlock(AIR) below) only runs preRemoveHook/preCleanup; it never calls
        // AbstractMachineBlockEntity#unregister() (the only place a machine closes its
        // BetterModelMachineRenderer's DummyTracker outside of a real Bukkit BlockBreakEvent — see
        // MachineBreakListener, which does NOT fire here either since capture uses a raw NMS
        // setBlock, not a player break). Left alone, the crusher's REAL-WORLD tracker stays
        // spawned forever at the original position while ContraptionBlockEntityElementMirror's
        // BetterModelCell spawns a SECOND tracker following the contraption -> "2 modelos". Close
        // it explicitly here, same "copiarlo, limpiarlo, removerlo" pattern as the
        // PersistentWorldlyBlockEntity#clearContent() call below: capture() already read this
        // block entity's full NBT moments ago, so closing its live BetterModel tracker here loses
        // nothing.
        for (BlockPos pos : worldPositions) {
            PersistentBlockEntity ce = PersistentBlockEntity.getIfLoaded(level, pos);
            if (ce instanceof dev.arubik.craftengine.machine.render.BetterModelDriven driven) {
                try {
                    driven.betterModelRenderer().close();
                } catch (Throwable ignored) {
                    // best-effort — never abort the capture over a BetterModel hiccup
                }
            }
            if (ce instanceof dev.arubik.craftengine.machine.render.ModelEngineDriven med) {
                try {
                    med.modelEngineRenderer().close();
                } catch (Throwable ignored) {
                    // best-effort — never abort the capture over a ModelEngine hiccup
                }
            }
            // Leftover-emitter fix (2026-07-02 "al convertir un fan en contraption el particle emisor
            // original no se borra"): a machine block-entity (the copper fan) emits its particle stream
            // purely from its own tick loop, and — exactly like the BetterModel tracker leak documented
            // above — CraftEngine's raw-setBlock(AIR) onRemove hook chain never calls the machine's
            // AbstractMachineBlockEntity#unregister() (no player break -> MachineBreakListener never
            // fires either). Left alone the ORIGINAL fan's controller keeps ticking/emitting at the old
            // real-world position while the captured copy also emits (redirected via
            // ContraptionLevel#sendParticlesSource) -> a duplicate/orphan emitter. capture() has already
            // read this block-entity's full NBT moments ago, so tearing it down here loses nothing.
            if (ce instanceof dev.arubik.craftengine.machine.block.entity.AbstractMachineBlockEntity machine) {
                try {
                    machine.unregister();
                } catch (Throwable ignored) {
                    // best-effort — never abort the capture over a machine-teardown hiccup
                }
                // ...and actually STOP it ticking/emitting. machine.unregister() frees the machine's own
                // menu/renderer but does NOT invalidate the backing CraftEngine block-entity, and CE's
                // tick loop only self-evicts a ticker once BlockEntity#isValid() is false. The plain
                // setBlock(AIR) below runs CE's onRemove hook but leaves the CEChunk's ticker in place, so
                // the ORIGINAL real-world fan would keep ticking/emitting at the old position (the capture-
                // side "el particle emisor original no se borra"). Explicitly drop it from the real world's
                // CEChunk — CEChunk#removeBlockEntity invalidates the BE (isValid=false) AND removes its
                // ticker + dynamic renderer (verified via javap), which is the same generalizing teardown
                // ContraptionLevel#unloadCeWorld does for the disassemble direction.
                try {
                    removeCeBlockEntity(level, pos);
                } catch (Throwable ignored) {
                    // best-effort — never abort the capture over a CraftEngine teardown hiccup
                }
            }
        }
        // Quiet removal (mirrors ContraptionLevel#putBlock's "quiet" capture-side fix, same root
        // cause on the opposite side of the same batch): flag 3 (UPDATE_CLIENTS|UPDATE_NEIGHBORS)
        // runs a full neighbor/shape-update pass on every single setBlock(AIR) call. Since
        // `worldPositions` iteration order is unspecified, a support block (wall a lever/redstone
        // dust was glued to) can get set to AIR BEFORE the lever's own position is processed --
        // that neighbor-update pass then detects the lever has no support and pops it as a
        // dropped item in the REAL world, even though it was already safely captured into the
        // ContraptionLevel moments earlier ("al ensamblar la palanca se duplica": one copy safely
        // captured, one copy popped/dropped by this exact real-world neighbor-update race). Using
        // the same quiet flags (UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_DROPS) as
        // ContraptionLevel#putBlock skips that shape/support check entirely for the whole batch --
        // trade-off: a real redstone component just OUTSIDE the captured structure that depended
        // on one of these blocks for power won't get an immediate neighbor-update either, but
        // that's a much smaller, self-correcting (next real block update / redstone tick nearby)
        // issue than silently duplicating an item.
        int quietFlags = net.minecraft.world.level.block.Block.UPDATE_CLIENTS
                | net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE
                | net.minecraft.world.level.block.Block.UPDATE_SUPPRESS_DROPS;
        for (BlockPos pos : worldPositions) {
            PersistentBlockEntity ce = PersistentBlockEntity.getIfLoaded(level, pos);
            if (ce instanceof PersistentWorldlyBlockEntity worldly) {
                worldly.clearContent();
            }
            level.removeBlockEntity(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), quietFlags);
        }
    }

    /**
     * Drops a CraftEngine block-entity from its real-world {@code CEChunk} so its CraftEngine ticker is
     * invalidated and evicted — the capture-side equivalent of {@code ContraptionLevel#unloadCeWorld}'s
     * per-BE teardown, used to stop a captured machine (the copper fan) still ticking/emitting at its
     * original position. {@code CEChunk#removeBlockEntity} sets the BE invalid and removes its ticker +
     * dynamic renderer (verified via javap against this project's craft-engine jar).
     */
    private static void removeCeBlockEntity(Level level, BlockPos pos) {
        net.momirealms.craftengine.core.world.CEWorld ceWorld =
                new net.momirealms.craftengine.bukkit.world.BukkitWorld(level.getWorld()).storageWorld();
        if (ceWorld == null)
            return;
        net.momirealms.craftengine.core.world.chunk.CEChunk chunk =
                ceWorld.getChunkAtIfLoaded(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk != null)
            chunk.removeBlockEntity(net.momirealms.craftengine.core.world.BlockPos.of(pos.asLong()));
    }

    /**
     * Writes a captured {@link ContraptionLevel} back into the real world at
     * {@code bearingWorldPos}, restoring the block entity where present. Used both for a
     * same-position round-trip test and for real disassembly (grid-snap collision-checking
     * is the caller's job — see CONTRAPTIONS.md §4 spike #7 — this method always writes).
     */
    public static void restore(Level realLevel, ContraptionLevel level, BlockPos bearingWorldPos) {
        List<Placement> placements = new ArrayList<>();
        for (BlockPos local : new HashSet<>(level.localPositions())) {
            BlockPos worldPos = ContraptionMath.toWorld(local, bearingWorldPos);
            placements.add(new Placement(local, worldPos, level.getBlockState(local)));
        }
        placeOrdered(realLevel, level, placements);
    }

    /**
     * Grid-snap disassembly restore (CONTRAPTIONS.md §1 "Assembly/disassembly", spike #7):
     * for each captured cell, if the target world position is already occupied by
     * something else (not air, not replaceable — e.g. a player built there while the
     * contraption was flying), eject the captured block as a dropped item instead of
     * silently overwriting whatever's there. Otherwise behaves exactly like
     * {@link #restore}. {@code bearingWorldPos} should already be grid-snapped
     * ({@link ContraptionMath#gridSnap}) by the caller — this method only checks
     * occupancy, it doesn't round anything itself.
     */
    public static void restoreWithCollisionCheck(Level realLevel, ContraptionLevel level, BlockPos bearingWorldPos) {
        List<Placement> placements = new ArrayList<>();
        for (BlockPos local : new HashSet<>(level.localPositions())) {
            BlockPos worldPos = ContraptionMath.toWorld(local, bearingWorldPos);
            BlockState toPlace = level.getBlockState(local);
            BlockState existing = realLevel.getBlockState(worldPos);

            if (!existing.isAir() && !existing.canBeReplaced()) {
                ItemStack drop = new ItemStack(toPlace.getBlock().asItem());
                if (!drop.isEmpty()) {
                    Containers.dropItemStack(realLevel, worldPos.getX(), worldPos.getY(), worldPos.getZ(), drop);
                }
                continue;
            }

            placements.add(new Placement(local, worldPos, toPlace));
        }
        placeOrdered(realLevel, level, placements);
    }

    /**
     * Same as {@link #restoreWithCollisionCheck} but also rotates every captured cell by
     * {@code quarterTurns} 90-degree clockwise steps around the bearing origin BEFORE
     * restoring — the axis-snap disassembly requirement (CONTRAPTIONS.md "al desarmar
     * intentara acomodar en uno de los 4 ejes nunca en diagonal. y rotara los bloques de la
     * estructura"): a contraption can end up at any continuous yaw mid-flight (rotational
     * bearing, minecart heading, ...), but the restored structure must land on one of the 4
     * cardinal rotations, with every block's shape AND facing rotated to match.
     *
     * <p>Two independent rotations happen per cell:
     * <ul>
     *   <li>the LOCAL POSITION is rotated around the bearing's local origin (0,0,0) using the
     *   same clockwise 2D formula as {@code ContraptionMath#rotateYaw} but on the integer grid:
     *   one 90-degree clockwise step maps {@code (x,z) -> (-z,x)} (matching {@code Rotation
     *   .CLOCKWISE_90}'s own convention on {@link BlockPos#rotate}), applied {@code
     *   quarterTurns} times — this reshapes WHERE each block ends up, i.e. the structure's
     *   footprint;</li>
     *   <li>the {@link BlockState} itself is rotated via vanilla's own {@code BlockState
     *   #rotate(Rotation)} — this reorients each block's own facing/axis/rotation property
     *   (a furnace, a piston, a stairs block, ...) so it still points the "same" relative
     *   direction after the structure as a whole has turned.</li>
     * </ul>
     *
     * <p>{@code quarterTurns} must be in {@code [0, 3]} — see
     * {@link ContraptionMath#quarterTurnsBetween}.
     */
    public static void restoreRotated(Level realLevel, ContraptionLevel level, BlockPos bearingWorldPos,
            int quarterTurns) {
        if (quarterTurns == 0) {
            restoreWithCollisionCheck(realLevel, level, bearingWorldPos);
            return;
        }
        net.minecraft.world.level.block.Rotation rotation = rotationFor(quarterTurns);
        List<Placement> placements = new ArrayList<>();
        for (BlockPos local : new HashSet<>(level.localPositions())) {
            BlockPos rotatedLocal = rotateLocal(local, quarterTurns);
            BlockPos worldPos = ContraptionMath.toWorld(rotatedLocal, bearingWorldPos);
            BlockState toPlace = level.getBlockState(local).rotate(rotation);
            BlockState existing = realLevel.getBlockState(worldPos);

            if (!existing.isAir() && !existing.canBeReplaced()) {
                ItemStack drop = new ItemStack(toPlace.getBlock().asItem());
                if (!drop.isEmpty()) {
                    Containers.dropItemStack(realLevel, worldPos.getX(), worldPos.getY(), worldPos.getZ(), drop);
                }
                continue;
            }

            // BlockState carries its ROTATED facing/axis (toPlace), but the block-entity NBT and
            // CE-controller bytes are still looked up by the ORIGINAL local cell (level.getBlockState/
            // saveBlockEntity(local)) -- so Placement keeps the un-rotated `local` for restoreBlockEntity.
            placements.add(new Placement(local, worldPos, toPlace));
        }
        placeOrdered(realLevel, level, placements);
    }

    /** {@code quarterTurns} (0-3) 90-degree-clockwise steps, as the matching vanilla {@code Rotation} constant. */
    private static net.minecraft.world.level.block.Rotation rotationFor(int quarterTurns) {
        return switch (((quarterTurns % 4) + 4) % 4) {
            case 1 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
            case 2 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
            case 3 -> net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;
            default -> net.minecraft.world.level.block.Rotation.NONE;
        };
    }

    /**
     * Rotates a LOCAL {@link BlockPos} around the bearing's local origin (0,0,0) by
     * {@code quarterTurns} 90-degree clockwise steps in the XZ plane (Y/height untouched —
     * this codebase only ever tracks yaw around the vertical Y axis, see {@code
     * ContraptionMath}/{@code ContraptionState} class javadocs). Matches vanilla {@code
     * BlockPos#rotate(Rotation.CLOCKWISE_90)}'s own {@code (x,z) -> (-z,x)} convention (and
     * {@code ContraptionMath#rotateYaw}'s {@code rx = x*cos - z*sin} at +90 degrees, which
     * reduces to the same {@code (x,z) -> (-z,x)}) so the block-shape rotation
     * ({@link BlockState#rotate}) and the position rotation agree on which way "clockwise" is.
     */
    public static BlockPos rotateLocal(BlockPos local, int quarterTurns) {
        int x = local.getX();
        int z = local.getZ();
        for (int i = 0; i < ((quarterTurns % 4) + 4) % 4; i++) {
            int newX = -z;
            int newZ = x;
            x = newX;
            z = newZ;
        }
        return new BlockPos(x, local.getY(), z);
    }

    /**
     * Same physics-freeze fix as {@link dev.arubik.craftengine.contraption.level.ContraptionLevel
     * #putBlock}'s {@code quiet} placement and {@link #removeFromWorld}'s quiet removal, applied
     * to the REAL-WORLD restore side too (2026-07-01 session — "al desesamblar un coso... debes
     * aplicar las reglas de ignorar las fisicas ya que si no puede destruirse cosas como la
     * redstone"). {@code restore}/{@code restoreWithCollisionCheck} iterate an unordered
     * {@code Set<BlockPos>}, so a support-dependent block (lever, redstone dust) can get placed
     * in the real world before its supporting neighbor is placed a moment later in the same
     * batch — the immediate neighbor/shape-update pass from a noisy {@code setBlock} pops it off
     * right then, even though the support IS coming. Skips {@code UPDATE_NEIGHBORS}/shape checks
     * during the whole placement batch, same flags as the other two quiet call sites.
     */
    private static final int QUIET_PLACE_FLAGS = net.minecraft.world.level.block.Block.UPDATE_CLIENTS
            | net.minecraft.world.level.block.Block.UPDATE_KNOWN_SHAPE
            | net.minecraft.world.level.block.Block.UPDATE_SUPPRESS_DROPS;

    /**
     * A single captured cell about to be written back to the real world. {@code local} is the
     * ORIGINAL (un-rotated) cell in the {@link ContraptionLevel} — used only to look the block
     * entity / CE-controller data back up ({@link #restoreBlockEntity}); {@code state} is the
     * final (already-rotated, if any) {@link BlockState} to write at {@code worldPos}.
     */
    private record Placement(BlockPos local, BlockPos worldPos, BlockState state) {
    }

    /**
     * Ordered two-pass restore — the actual fix for "al deconstruir la redstone se termina
     * rompiendo... se rompe la mitad de la redstone" (about half a contraption's redstone popped
     * off as items on disassemble).
     *
     * <p><b>Root cause.</b> The old restore iterated an unordered {@code Set<BlockPos>} and, for
     * each cell, immediately {@code setBlock}'d it (quiet) then, only AFTER the whole batch, ran a
     * single unordered {@code updateNeighborsAt} pass. Quiet placement itself is fine — it skips
     * the shape/support check (see {@link #QUIET_PLACE_FLAGS}) so nothing pops MID-placement. But
     * the deferred settle pass fires a real neighbor block-update on each cell's 6 neighbors, and
     * when that update lands on a support-dependent block (redstone dust, repeater, torch, lever,
     * rail, ...) vanilla re-runs its {@code canSurvive}/{@code neighborChanged} logic and calls
     * {@code Block#updateOrDestroy} — popping the component if, at that instant, its evaluation of
     * the still-transient redstone network / support says it can't stay. Because the settle pass
     * ran in unspecified {@code Set} order, some components got updated before the rest of their
     * local network/supports had themselves settled and others after — so roughly HALF survived
     * and half popped. Same race the capture-side pipe-connection comment documents ("algunas
     * quedan bien unidas, otras no").
     *
     * <p><b>Fix.</b> Split every placement into two tiers by {@link #isSupportDependent}:
     * STRUCTURAL (solid/self-supporting) blocks first, SUPPORT-DEPENDENT (redstone-ish, attached,
     * rails) blocks second. Then:
     * <ol>
     *   <li>place ALL structural blocks (quiet), then ALL dependents (quiet) — so every
     *   dependent's support already physically exists the moment it lands (belt-and-suspenders
     *   on top of the quiet flags);</li>
     *   <li>run the deferred neighbor-update settle pass in the SAME tier order — structural
     *   supports settle first and reach a stable state, THEN the redstone components settle once,
     *   exactly once, against a fully-placed-and-settled structure. That single ordered
     *   re-evaluation is also what lets redstone recompute a correct power level after being
     *   frozen inside the fake level.</li>
     * </ol>
     * This mirrors how vanilla structure/schematic placement and Create-style contraption restore
     * avoid the "half the redstone breaks" symptom.
     */
    private static void placeOrdered(Level realLevel, ContraptionLevel level, List<Placement> placements) {
        List<Placement> structural = new ArrayList<>();
        List<Placement> dependent = new ArrayList<>();
        for (Placement p : placements) {
            if (isSupportDependent(p.state())) {
                dependent.add(p);
            } else {
                structural.add(p);
            }
        }

        // Pass 1: place supports first, then dependents -- every dependent's support exists first.
        for (Placement p : structural) {
            realLevel.setBlock(p.worldPos(), p.state(), QUIET_PLACE_FLAGS);
            restoreBlockEntity(realLevel, level, p.local(), p.worldPos());
        }
        for (Placement p : dependent) {
            realLevel.setBlock(p.worldPos(), p.state(), QUIET_PLACE_FLAGS);
            restoreBlockEntity(realLevel, level, p.local(), p.worldPos());
        }

        // Pass 2: deferred settle in the same tier order -- structure settles before redstone.
        settleNeighbors(realLevel, structural);
        settleNeighbors(realLevel, dependent);

        // Pass 3: connection-MODEL settle for CE connected blocks (2026-07-19 — "al desensamblar las
        // pipes quedan raras"). settleNeighbors above drives neighborChanged, which ConnectedBlockBehavior
        // does not override, so a restored pipe kept the connection faces it carried out of the hologram —
        // computed against contraption-local neighbours, and possibly rotated stale (BlockState#rotate does
        // not turn the north/east/... enum faces). Re-derive each connectable's own state against its REAL
        // neighbours now that the whole structure has landed, so the pipe's model matches what it actually
        // touches in the world. Two passes for the same convergence reason capture uses.
        List<BlockPos> structuralWorld = new ArrayList<>();
        List<BlockPos> dependentWorld = new ArrayList<>();
        for (Placement p : structural) {
            structuralWorld.add(p.worldPos());
        }
        for (Placement p : dependent) {
            dependentWorld.add(p.worldPos());
        }
        for (int pass = 0; pass < 2; pass++) {
            settleConnectedModels(realLevel, structuralWorld);
            settleConnectedModels(realLevel, dependentWorld);
        }
    }

    /**
     * One deliberate neighbor-update pass AFTER the whole batch has landed (every support block
     * is now genuinely in place) — needed so real-world redstone actually recalculates correct
     * power state once the structure is fully restored, instead of staying permanently "quiet"
     * (unpowered/stale) forever just because placement itself skipped neighbor updates.
     */
    private static void settleNeighbors(Level realLevel, List<Placement> placed) {
        for (Placement p : placed) {
            try {
                realLevel.updateNeighborsAt(p.worldPos(), realLevel.getBlockState(p.worldPos()).getBlock());
            } catch (Throwable ignored) {
                // best-effort — a single misbehaving neighbor update shouldn't abort the rest
            }
        }
    }

    /**
     * Whether {@code state} is a block that needs a supporting/attached neighbor to legally exist
     * — the class of block vanilla's shape/neighbor-update machinery pops off as a dropped item
     * when its support is missing. Used by {@link #placeOrdered} to guarantee such blocks are
     * placed (and settled) only AFTER every self-supporting structural block already exists.
     *
     * <p>Deliberately classified from the block/blockstate alone (no world probing mid-restore),
     * covering the redstone family the bug report is about plus the broader "attached/needs-a-face"
     * family that would fail the same way:
     * <ul>
     *   <li>redstone wire, repeaters &amp; comparators ({@link DiodeBlock}), rails
     *   ({@link BaseRailBlock}) — sit on top of a block below;</li>
     *   <li>anything face-attached or ground-supported that carries one of the vanilla
     *   attachment/support properties: levers, buttons, torches (incl. standing/wall redstone
     *   torch), tripwire hooks, signs, banners, saplings/plants, pressure plates, ... — detected
     *   structurally via the {@code face}/{@code attachment}/{@code hanging}/{@code lit} +
     *   {@code facing} property signatures.</li>
     * </ul>
     * This is intentionally broad: mis-classifying a genuinely self-supporting block as
     * "dependent" only defers its placement into pass 2, which is harmless; the failure we must
     * avoid is the reverse (a dependent placed/settled in pass 1 before its support exists), so we
     * err toward tagging anything remotely attachment-like as dependent.
     */
    private static boolean isSupportDependent(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof RedStoneWireBlock
                || block instanceof DiodeBlock
                || block instanceof BaseRailBlock) {
            return true;
        }
        // Face-attached components (levers, buttons, torches, tripwire hooks, wall signs/banners,
        // ...) all carry one of these vanilla attachment properties; if a block exposes any of
        // them it is placed against a specific neighbor face and must land after that neighbor.
        for (Property<?> prop : state.getProperties()) {
            String name = prop.getName();
            if (name.equals("face") || name.equals("attachment") || name.equals("hanging")) {
                return true;
            }
        }
        return false;
    }

    private static void restoreBlockEntity(Level realLevel, ContraptionLevel level, BlockPos local, BlockPos worldPos) {
        CompoundTag tag = level.saveBlockEntity(local);
        if (tag != null) {
            BlockEntity be = BlockEntity.loadStatic(worldPos, realLevel.getBlockState(worldPos), tag, realLevel.registryAccess());
            if (be != null) {
                be.setLevel(realLevel);
                realLevel.setBlockEntity(be);
            }
        }

        // CraftEngine-controlled blocks (see capture()/removeFromWorld() javadoc): the real
        // setBlock(worldPos, ...) call the caller already made (restore()/
        // restoreWithCollisionCheck()) re-triggers WorldStorageInjector#compareAndUpdateBlockState,
        // which — for a CE custom state with hasBlockEntity() — freshly constructs a brand-new
        // BlockEntityController (StorageBlockEntity et al.) at worldPos, starting empty. Feed
        // the captured controller bytes back into THAT fresh controller here.
        //
        // <p>MUST re-serialize the LIVE in-level controller here, NOT replay the STALE
        // level.getCeControllerData(local) byte-snapshot taken once at CAPTURE time (2026-07-02
        // session — "/ce item get cml:copper_tank si se llena pero solo en el mundo ficticio...
        // al volver se muestra 0mb, no se guardo bien"). Any interaction while the block lived
        // inside the ContraptionLevel (a bucket fill, a hopper feeding it, etc.) updates the LIVE
        // controller correctly, but that snapshot was frozen the instant capture() ran, before
        // any of that could happen — restoring from it silently discarded everything that
        // happened in between. Falls back to the capture-time snapshot only if the live
        // controller can't be found for some reason (e.g. a cell whose block never actually
        // finished loading inside the mini-dimension).
        PersistentBlockEntity live = PersistentBlockEntity.getIfLoaded(level.serverLevel(), local);
        byte[] ceBytes;
        if (live != null) {
            try {
                ceBytes = live.serializeToBytes();
            } catch (Throwable t) {
                ceBytes = level.getCeControllerData(local);
            }
        } else {
            ceBytes = level.getCeControllerData(local);
        }
        if (ceBytes != null) {
            PersistentBlockEntity ce = PersistentBlockEntity.getIfLoaded(realLevel, worldPos);
            if (ce != null) {
                try {
                    ce.loadFromBytes(ceBytes);
                } catch (Throwable ignored) {
                    // best-effort — a restore failure here shouldn't abort the whole structure restore
                }
            }
        }
    }
}
