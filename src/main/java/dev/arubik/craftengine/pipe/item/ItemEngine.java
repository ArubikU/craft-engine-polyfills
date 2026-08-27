package dev.arubik.craftengine.pipe.item;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.arubik.craftengine.block.entity.PersistentBlockEntity;
import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.util.BlockStateUtils;
import net.momirealms.craftengine.core.block.ImmutableBlockState;

/**
 * Item pipe network — a plain, discrete-stack conduit between whatever storage containers
 * (vanilla OR custom) sit at its open ends. Unlike the fluid/gas networks this is NOT a
 * continuous-equalization solve (see {@code FluidNetworkSolver}) — items are discrete, so each
 * network tick does a direct round-robin pass instead, structured as PEEK -&gt; SIMULATE -&gt; COMMIT:
 * a source is never actually extracted from until a destination has already confirmed room for it
 * ({@link #simulateInsertRoom}), so a stack is never removed from the world's item graph without
 * an already-confirmed home — see {@link #step} for why this replaced an earlier extract-first
 * design that could (rarely) lose an item if nothing downstream, including the source itself,
 * would take it back.
 *
 * <p>Per-face behaviour is entirely read from the pipe segment's OWN live {@code IOConfiguration}
 * (the same object its panel's {@code Machine.io_get}/{@code io_set} script calls mutate, and the
 * same object {@code ConnectedBlockBehavior#shouldConnect} consults for the visual mask):
 * <ul>
 *   <li>{@code acceptsInput(ITEM, localDir)} — this face is a SOURCE (extract from the neighbour).
 *   <li>{@code providesOutput(ITEM, localDir)} — this face is a DESTINATION (insert into the
 *       neighbour). A face can be both, or neither (fully closed).
 *   <li>{@code acceptsInput(REDSTONE, localDir)} — this face additionally requires a live redstone
 *       signal to be active this tick (a coarse "any neighbour signal" check, matching the existing
 *       valve behaviors' simplicity).
 * </ul>
 * Item IDENTITY filtering (not just input/output/redstone) is a separate, finer axis, and lives
 * ENTIRELY in script — {@code MachineDefinition#onTransferScript()} (the generic {@code
 * on_pipe_transfer} hook, shared with every other machine's own transfer gate, see
 * {@code AbstractMachineBlockEntity#runOnTransferScript}), run per-candidate-item with
 * `type`/`payload`/`direction`/`mode`/`event` bound in its script context, and expected to call
 * {@code event.cancel()} to block. Java never compares item ids itself; the shipped {@code
 * item_pipe_panel.json} stores its filter as one Item value per slot (`pipe_filter_<dir>_<slot>`,
 * via the generic {@code Machine.get_typed}/{@code set_typed}) and its script (`scripts/
 * item_pipe_panel.pf`) reads those to decide — swap the script and you change the rule, no Java
 * involved.
 */
public final class ItemEngine {

    public static volatile boolean ENABLED = true;
    private static final int MAX_BLOCKS = 4096;
    /** Seed pipe positions -> that segment's configured stack-count-per-tick throughput. */
    private static final Map<Long, Integer> SEEDS = new java.util.concurrent.ConcurrentHashMap<>();

    private ItemEngine() {
    }

    public static void registerSeed(BlockPos pos, int transferPerTick) {
        if (pos != null)
            SEEDS.put(pos.asLong(), Math.max(1, transferPerTick));
    }

    public static void tickAll(Level level) {
        if (!ENABLED || SEEDS.isEmpty() || level == null)
            return;
        Set<Long> handled = new HashSet<>();
        for (Map.Entry<Long, Integer> entry : SEEDS.entrySet()) {
            long key = entry.getKey();
            if (handled.contains(key))
                continue;
            BlockPos seed = BlockPos.of(key);
            if (!isPipe(level, seed)) {
                handled.add(key);
                continue;
            }
            try {
                handled.addAll(step(level, seed));
            } catch (Throwable ignored) {
                handled.add(key);
            }
        }
    }

    /** One STORAGE endpoint reachable from the pipe network: which pipe segment owns the face,
     * the world direction from that segment toward the container, and the container itself.
     * {@code behavior} is the pipe segment's own behavior instance, ALREADY resolved once while
     * walking the network in {@link #step} — carrying it here avoids re-resolving it (a block-state
     * fetch + custom-state lookup + behavior-list search) from scratch in {@link #faceMode} for
     * every endpoint, every tick. */
    record Endpoint(BlockPos pipePos, Direction fromPipe, BlockPos containerPos, ItemPipeBehavior behavior) {
    }

    /** Steps the network containing {@code start}; returns every pipe position visited (so the caller
     * can mark them handled and skip re-walking the same network from another seed this tick). */
    private static Set<Long> step(Level level, BlockPos start) {
        Set<Long> visitedPipes = new HashSet<>();
        List<BlockPos> pipeNodes = new ArrayList<>();
        List<Endpoint> endpoints = new ArrayList<>();
        int minRate = Integer.MAX_VALUE;

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start.immutable());
        visitedPipes.add(start.asLong());

        while (!queue.isEmpty() && pipeNodes.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            ItemPipeBehavior beh = pipeBehaviorAt(level, pos);
            if (beh == null)
                continue;
            pipeNodes.add(pos.immutable());
            minRate = Math.min(minRate, SEEDS.getOrDefault(pos.asLong(), beh.transferPerTick()));
            for (Direction dir : Direction.values()) {
                if (!beh.isConnectedTo(dir, pos, level))
                    continue;
                BlockPos neighborPos = pos.relative(dir);
                ItemPipeBehavior neighborPipe = pipeBehaviorAt(level, neighborPos);
                if (neighborPipe != null) {
                    if (visitedPipes.add(neighborPos.asLong()))
                        queue.add(neighborPos.immutable());
                    continue;
                }
                if (ItemTransferHelper.getContainer(level, neighborPos, pos.immutable(), dir.getOpposite()).isPresent())
                    endpoints.add(new Endpoint(pos.immutable(), dir, neighborPos.immutable(), beh));
            }
        }

        if (endpoints.size() < 2 || minRate == Integer.MAX_VALUE)
            return visitedPipes; // nothing to shuttle between, or no legal rate

        List<Endpoint> sources = new ArrayList<>();
        List<Endpoint> dests = new ArrayList<>();
        for (Endpoint e : endpoints) {
            FaceMode mode = faceMode(level, e);
            if (mode.source)
                sources.add(e);
            if (mode.destination)
                dests.add(e);
        }

        for (Endpoint source : sources) {
            Direction srcFace = source.fromPipe().getOpposite();
            Container src = ItemTransferHelper.getContainer(level, source.containerPos(), source.pipePos(), srcFace).orElse(null);
            if (src == null)
                continue;

            // PEEK, don't take: find what's eligible without mutating anything yet. Extracting
            // first and only THEN looking for somewhere to put it (the old design) meant a stack
            // with nowhere to go had already been removed from the world's item graph — the source
            // rejecting the put-back too was a genuine, if rare, item disappearance. Simulating
            // room in every candidate destination BEFORE touching the source means nothing is ever
            // taken without a confirmed home; the item never has a tick where it exists nowhere.
            int slot = findEligibleSlot(src, srcFace, level, source);
            if (slot < 0)
                continue;
            ItemStack peeked = src.getItem(slot);
            if (peeked.isEmpty())
                continue;

            int available = Math.min(minRate, peeked.getCount());
            boolean movedAny = false;
            for (Endpoint dest : dests) {
                if (available <= 0)
                    break;
                if (dest.containerPos().equals(source.containerPos()))
                    continue;
                if (!passesFilter(level, dest, peeked, "output"))
                    continue;
                Direction dstFace = dest.fromPipe().getOpposite();
                Container dst = ItemTransferHelper.getContainer(level, dest.containerPos(), dest.pipePos(), dstFace).orElse(null);
                if (dst == null)
                    continue;

                int room = simulateInsertRoom(dst, dstFace, peeked, available);
                if (room <= 0)
                    continue; // no confirmed home here — the source is never touched for this attempt

                // Room is confirmed: NOW commit. Single-threaded tick, nothing runs between the
                // simulation and this pair of calls, so the real insert normally succeeds in full —
                // but a defensive fallback stays wired (put back, then world-drop as an absolute
                // last resort) rather than assuming that invariant can never break.
                ItemStack committed = extractFromSlot(src, slot, room);
                if (committed.isEmpty())
                    continue;
                movedAny = true;
                ItemStack leftover = insertInto(dst, dstFace, committed);
                if (!leftover.isEmpty()) {
                    leftover = insertInto(src, srcFace, leftover);
                    if (!leftover.isEmpty())
                        dropStack(level, source.pipePos(), leftover);
                }
                available -= room;
            }
            if (movedAny)
                src.setChanged();
        }
        return visitedPipes;
    }

    // ---------------- per-face mode (IOConfiguration-driven) ----------------

    private record FaceMode(boolean source, boolean destination) {
    }

    private static FaceMode faceMode(Level level, Endpoint e) {
        ItemPipeBehavior beh = e.behavior();
        if (beh == null)
            return new FaceMode(false, false);
        IOConfiguration cfg = beh.getIOConfiguration(level, e.pipePos());
        if (cfg == null)
            return new FaceMode(false, false);
        BlockState selfState = level.getBlockState(e.pipePos());
        Direction local = beh.toLocalDirection(e.fromPipe(), selfState);
        boolean redstoneRequired = cfg.acceptsInput(IOConfiguration.IOType.REDSTONE, local);
        if (redstoneRequired && !hasAnyRedstoneSignal(level, e.pipePos()))
            return new FaceMode(false, false);
        boolean in = cfg.acceptsInput(IOConfiguration.IOType.ITEM, local);
        boolean out = cfg.providesOutput(IOConfiguration.IOType.ITEM, local);
        return new FaceMode(in, out);
    }

    /** Coarse redstone gate — "does ANY neighbour of this pipe carry a signal" — mirrors the
     * existing valve behaviors' simplicity rather than resolving a true per-face reading. */
    private static boolean hasAnyRedstoneSignal(Level level, BlockPos pipePos) {
        try {
            return level.hasNeighborSignal(pipePos);
        } catch (Throwable ignored) {
            return false;
        }
    }

    // ---------------- item identity filter ----------------

    /**
     * Whether {@code candidate} may cross this face. Deliberately NOT a hardcoded comparison — this
     * calls the SAME generic {@code MachineDefinition#onTransferScript()} hook ({@code
     * on_pipe_transfer} in JSON) that {@code AbstractMachineBlockEntity} runs for every OTHER
     * machine's own transfer gates, so a pipe segment's per-face filter and a destination machine's
     * transfer veto are one mechanism, not two. The actual whitelist/blacklist decision lives
     * entirely in the declared {@code .pf} script (see {@code scripts/item_pipe_panel.pf}'s
     * {@code filter_item()}) — Java only wires the context and reads back {@code event.cancelled}.
     * A panel that declares no {@code on_pipe_transfer} accepts everything (no filtering at all).
     */
    static boolean passesFilter(Level level, Endpoint e, ItemStack candidate, String mode) {
        if (level == null) // defensive: fail-open rather than propagate a NPE from getIfLoaded below
            return true;
        PersistentBlockEntity be = PersistentBlockEntity.getIfLoaded(level, e.pipePos());
        if (be instanceof DataMachineBlockEntity dm && dm.definition() != null
                && dm.definition().onTransferScript() != null) {
            return !runTransferScript(level, e, dm, dm.definition(), candidate, mode);
        }
        return true;
    }

    /** @return true if the script vetoed this transfer via {@code event.cancel()} (mirrors {@code
     * AbstractMachineBlockEntity#runOnTransferScript}'s return convention exactly, so both call
     * sites use the same veto mechanism). */
    private static boolean runTransferScript(Level level, Endpoint e, DataMachineBlockEntity dm,
            MachineDefinition definition, ItemStack candidate, String mode) {
        try {
            ScriptContext base = dm.buildScriptContext();
            if (base == null)
                return false;
            dev.arubik.craftengine.script.event.TransferEvent transferEvent =
                    new dev.arubik.craftengine.script.event.TransferEvent(
                            "item", ScriptValue.ofItem(candidate), e.fromPipe().getName(), mode);
            ScriptContext ctx = ScriptContext.builder().copyFrom(base)
                    .typed("type", ScriptValue.of("item"))
                    .typed("payload", ScriptValue.ofItem(candidate))
                    .typed("direction", ScriptValue.of(e.fromPipe().getName()))
                    .typed("mode", ScriptValue.of(mode))
                    .event(transferEvent)
                    .build();
            ScriptCall call = ScriptCall.parse(definition.onTransferScript());
            if (call == null)
                return false;
            call.execute(ctx);
            return transferEvent.isCancelled();
        } catch (Throwable ignored) {
            return false;
        }
    }

    // ---------------- container IO (mirrors vanilla hopper face rules) ----------------

    private static int[] slotsFor(Container c, Direction face) {
        if (c instanceof WorldlyContainer wc)
            return wc.getSlotsForFace(face);
        int[] all = new int[c.getContainerSize()];
        for (int i = 0; i < all.length; i++)
            all[i] = i;
        return all;
    }

    /** Finds the first slot on {@code face} eligible to give up an item (face rules + the {@code
     * on_pipe_transfer} input filter), without touching it. {@code -1} if nothing qualifies. */
    static int findEligibleSlot(Container src, Direction face, Level level, Endpoint source) {
        for (int slot : slotsFor(src, face)) {
            ItemStack stack = src.getItem(slot);
            if (stack.isEmpty())
                continue;
            if (src instanceof WorldlyContainer wc && !wc.canTakeItemThroughFace(slot, stack, face))
                continue;
            if (!passesFilter(level, source, stack, "input"))
                continue;
            return slot;
        }
        return -1;
    }

    /** Removes up to {@code amount} from an ALREADY-KNOWN slot — the actual mutation, split out from
     * the eligibility scan so {@link #step} can confirm a destination has room before calling this. */
    static ItemStack extractFromSlot(Container src, int slot, int amount) {
        ItemStack stack = src.getItem(slot);
        if (stack.isEmpty() || amount <= 0)
            return ItemStack.EMPTY;
        int take = Math.min(amount, stack.getCount());
        ItemStack taken = stack.copy();
        taken.setCount(take);
        stack.shrink(take);
        src.setItem(slot, stack);
        return taken;
    }

    /** Convenience combining the two above — scan-then-take in one call, kept for anyone (tests
     * included) that just wants "give me up to {@code max} eligible items" without the two-step
     * peek/commit split {@link #step} now uses to avoid extracting before a destination is confirmed. */
    static ItemStack extractOne(Container src, Direction face, int max, Level level, Endpoint source) {
        int slot = findEligibleSlot(src, face, level, source);
        if (slot < 0)
            return ItemStack.EMPTY;
        return extractFromSlot(src, slot, max);
    }

    static ItemStack insertInto(Container dest, Direction face, ItemStack stack) {
        if (stack.isEmpty())
            return stack;
        int[] slots = slotsFor(dest, face);
        // Pass 1: merge into matching stacks.
        for (int slot : slots) {
            if (stack.isEmpty())
                break;
            if (dest instanceof WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, stack, face))
                continue;
            ItemStack existing = dest.getItem(slot);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, stack))
                continue;
            int room = Math.min(existing.getMaxStackSize(), dest.getMaxStackSize(existing)) - existing.getCount();
            int move = Math.min(room, stack.getCount());
            if (move > 0) {
                existing.grow(move);
                dest.setItem(slot, existing);
                stack.shrink(move);
            }
        }
        // Pass 2: drop the remainder into an empty slot.
        for (int slot : slots) {
            if (stack.isEmpty())
                break;
            if (dest instanceof WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, stack, face))
                continue;
            if (!dest.getItem(slot).isEmpty() || !dest.canPlaceItem(slot, stack))
                continue;
            int move = Math.min(stack.getCount(), dest.getMaxStackSize(stack));
            ItemStack placed = stack.copy();
            placed.setCount(move);
            dest.setItem(slot, placed);
            stack.shrink(move);
        }
        dest.setChanged();
        return stack;
    }

    /** Dry-run sibling of {@link #insertInto}: sums how much of {@code amount} would actually fit
     * right now — same two-pass rule (merge room first, then empty slots), but touches no container
     * state at all. This is what lets {@link #step} confirm a destination BEFORE taking anything
     * from the source, instead of taking first and hoping. */
    static int simulateInsertRoom(Container dest, Direction face, ItemStack template, int amount) {
        if (template == null || template.isEmpty() || amount <= 0)
            return 0;
        int[] slots = slotsFor(dest, face);
        int room = 0;
        // Pass 1: room left in matching stacks.
        for (int slot : slots) {
            if (room >= amount)
                break;
            if (dest instanceof WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, template, face))
                continue;
            ItemStack existing = dest.getItem(slot);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, template))
                continue;
            int slotRoom = Math.min(existing.getMaxStackSize(), dest.getMaxStackSize(existing)) - existing.getCount();
            room += Math.max(0, slotRoom);
        }
        // Pass 2: empty slots that would accept this item.
        for (int slot : slots) {
            if (room >= amount)
                break;
            if (!dest.getItem(slot).isEmpty())
                continue;
            if (dest instanceof WorldlyContainer wc && !wc.canPlaceItemThroughFace(slot, template, face))
                continue;
            if (!dest.canPlaceItem(slot, template))
                continue;
            room += dest.getMaxStackSize(template);
        }
        return Math.min(room, amount);
    }

    // ---------------- helpers ----------------

    /** Last-resort escape hatch for a stack the network extracted but nothing (not even the source
     * it came from) will take back — see the call site in {@link #step}. Mirrors how vanilla
     * hoppers/dispensers spill an item they can't place rather than deleting it. */
    private static void dropStack(Level level, BlockPos pos, ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return;
        net.minecraft.world.phys.Vec3 at = net.minecraft.world.phys.Vec3.atCenterOf(pos);
        net.minecraft.world.entity.item.ItemEntity drop =
                new net.minecraft.world.entity.item.ItemEntity(level, at.x, at.y, at.z, stack);
        drop.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        level.addFreshEntity(drop);
    }

    private static boolean isPipe(Level level, BlockPos pos) {
        return pipeBehaviorAt(level, pos) != null;
    }

    private static ItemPipeBehavior pipeBehaviorAt(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos))
            return null;
        BlockState state = level.getBlockState(pos);
        ImmutableBlockState custom = BlockStateUtils.getOptionalCustomBlockState(state).orElse(null);
        if (custom == null || custom.isEmpty())
            return null;
        return custom.behavior().getFirst(ItemPipeBehavior.class);
    }
}
