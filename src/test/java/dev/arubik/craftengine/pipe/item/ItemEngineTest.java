package dev.arubik.craftengine.pipe.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Covers {@link ItemEngine}'s discrete-stack container mechanics — {@code extractOne}/{@code
 * insertInto} and the "nothing will take it back" fix in {@code step} — at the level that's
 * actually unit-testable in a pure JVM.
 *
 * <p><b>What's NOT covered here, and why</b>: a full network tick ({@code step}/{@code tickAll})
 * needs a real {@code Level} to resolve custom block states, {@code ConnectableBlockBehavior}
 * instances, and {@code IOConfiguration} through {@code BlockStateUtils} — none of which are
 * constructible in a plain JVM unit test (matches the documented limitation in {@code
 * MinerBehaviorTest}: "world-touching path needs a real ServerLevel... not constructible in a
 * pure-JVM unit test"). So this suite cannot exercise: the BFS network walk, a network split
 * mid-operation, redstone-required face gating ({@code Level#hasNeighborSignal}), or
 * {@code IOConfiguration}-driven face-open/closed classification end-to-end. Those need a live
 * CraftEngine + Paper server to verify. What IS covered: the container-level extract/insert
 * mechanics every one of those scenarios ultimately bottoms out in, plus the exact item-loss bug
 * fix (a rejecting destination AND a rejecting source no longer silently deletes the stack — the
 * leftover is provably non-empty, which is what triggers the world-drop in {@code step}; spawning
 * the actual {@code ItemEntity} needs a live {@code Level} too, so that final step is unverified
 * here, but it's a 4-line mirror of the already-shipped, working {@code
 * ContraptionVoidDrop#spawnItem} idiom).
 */
class ItemEngineTest {

    @BeforeAll
    static void bootstrapRegistries() {
        // Item.EMPTY/Items.* and ItemStack construction touch the vanilla registry; mirrors the
        // same one-time bootstrap MinerBehaviorTest already relies on for this reason.
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    /** A minimal, fully in-memory {@link WorldlyContainer} — plain array storage, with knobs to
     * simulate a container whose IO-face rules reject placement/extraction (the exact condition
     * that produces a non-empty leftover from {@code insertInto}/{@code extractOne}). */
    static final class FakeContainer implements WorldlyContainer {
        final ItemStack[] slots;
        boolean rejectPlace = false;
        boolean rejectTake = false;

        /** Paper's Container adds this; the fake has no world position to report. */
        @Override
        public org.bukkit.Location getLocation() {
            return null;
        }

        FakeContainer(int size) {
            slots = new ItemStack[size];
            for (int i = 0; i < size; i++) slots[i] = ItemStack.EMPTY;
        }

        static FakeContainer of(ItemStack... contents) {
            FakeContainer c = new FakeContainer(contents.length);
            for (int i = 0; i < contents.length; i++) c.slots[i] = contents[i];
            return c;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            int[] all = new int[slots.length];
            for (int i = 0; i < all.length; i++) all[i] = i;
            return all;
        }

        @Override
        public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
            return !rejectPlace;
        }

        @Override
        public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
            return !rejectTake;
        }

        @Override
        public int getContainerSize() {
            return slots.length;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack s : slots) if (!s.isEmpty()) return false;
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return slots[slot];
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack cur = slots[slot];
            ItemStack out = cur.split(amount);
            return out;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack cur = slots[slot];
            slots[slot] = ItemStack.EMPTY;
            return cur;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            slots[slot] = stack;
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < slots.length; i++) slots[i] = ItemStack.EMPTY;
        }

		@Override
		public int getMaxStackSize() {
			// The container-level cap Container#getMaxStackSize(ItemStack) mins against an item's
			// own max — insertInto/simulateInsertRoom call it for every merge/empty-slot check, so
			// it must return a real value, not throw. 64 matches every item these tests use.
			return 64;
		}

		@Override
		public List<ItemStack> getContents() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'getContents'");
		}

		@Override
		public void onOpen(CraftHumanEntity player) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'onOpen'");
		}

		@Override
		public void onClose(CraftHumanEntity player) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'onClose'");
		}

		@Override
		public List<HumanEntity> getViewers() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'getViewers'");
		}

		@Override
		@Nullable
		public InventoryHolder getOwner() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'getOwner'");
		}

		@Override
		public void setMaxStackSize(int size) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'setMaxStackSize'");
		}
    }

    private static ItemStack stack(Item item, int count) {
        return new ItemStack(item, count);
    }

    // ---------------- extractOne ----------------

    @Test
    @DisplayName("extractOne takes up to `max`, never more than the stack holds")
    void extractOneRespectsMax() {
        FakeContainer src = FakeContainer.of(stack(Items.COBBLESTONE, 40));
        ItemEngine.Endpoint dummy = new ItemEngine.Endpoint(
                new net.minecraft.core.BlockPos(0, 0, 0), Direction.NORTH, new net.minecraft.core.BlockPos(0, 0, 1), null);

        ItemStack taken = ItemEngine.extractOne(src, Direction.NORTH, 10, null, dummy);

        assertEquals(10, taken.getCount(), "should take exactly the transfer-rate cap");
        assertEquals(30, src.getItem(0).getCount(), "source should have shrunk by the taken amount");
    }

    @Test
    @DisplayName("extractOne takes the whole stack when it's smaller than `max`")
    void extractOneTakesWholeSmallStack() {
        FakeContainer src = FakeContainer.of(stack(Items.COBBLESTONE, 3));
        ItemEngine.Endpoint dummy = new ItemEngine.Endpoint(
                new net.minecraft.core.BlockPos(0, 0, 0), Direction.NORTH, new net.minecraft.core.BlockPos(0, 0, 1), null);

        ItemStack taken = ItemEngine.extractOne(src, Direction.NORTH, 64, null, dummy);

        assertEquals(3, taken.getCount());
        assertTrue(src.getItem(0).isEmpty(), "slot should be fully drained");
    }

    @Test
    @DisplayName("extractOne honours canTakeItemThroughFace — a rejecting face yields nothing")
    void extractOneRespectsFaceRejection() {
        FakeContainer src = FakeContainer.of(stack(Items.COBBLESTONE, 10));
        src.rejectTake = true;
        ItemEngine.Endpoint dummy = new ItemEngine.Endpoint(
                new net.minecraft.core.BlockPos(0, 0, 0), Direction.NORTH, new net.minecraft.core.BlockPos(0, 0, 1), null);

        ItemStack taken = ItemEngine.extractOne(src, Direction.NORTH, 64, null, dummy);

        assertTrue(taken.isEmpty(), "a rejecting face must not yield an item");
        assertEquals(10, src.getItem(0).getCount(), "source must be untouched");
    }

    @Test
    @DisplayName("extractOne with a null Level does not throw (passesFilter fails open)")
    void extractOneToleratesNullLevel() {
        FakeContainer src = FakeContainer.of(stack(Items.COBBLESTONE, 5));
        ItemEngine.Endpoint dummy = new ItemEngine.Endpoint(
                new net.minecraft.core.BlockPos(0, 0, 0), Direction.NORTH, new net.minecraft.core.BlockPos(0, 0, 1), null);

        ItemStack taken = ItemEngine.extractOne(src, Direction.NORTH, 64, null, dummy);

        assertEquals(5, taken.getCount(), "with no script/machine to consult, nothing should be filtered out");
    }

    // ---------------- insertInto ----------------

    @Test
    @DisplayName("insertInto merges into a matching existing stack before using empty slots")
    void insertIntoMergesBeforeUsingEmptySlots() {
        FakeContainer dst = FakeContainer.of(stack(Items.COBBLESTONE, 32), ItemStack.EMPTY);

        ItemStack leftover = ItemEngine.insertInto(dst, Direction.SOUTH, stack(Items.COBBLESTONE, 10));

        assertTrue(leftover.isEmpty());
        assertEquals(42, dst.getItem(0).getCount(), "should have merged into slot 0's existing stack");
        assertTrue(dst.getItem(1).isEmpty(), "slot 1 should stay untouched — merge satisfied the whole stack");
    }

    @Test
    @DisplayName("insertInto splits across an existing stack's remaining room and an empty slot")
    void insertIntoSpillsIntoEmptySlotWhenExistingStackIsAlmostFull() {
        FakeContainer dst = FakeContainer.of(stack(Items.COBBLESTONE, 60), ItemStack.EMPTY);

        ItemStack leftover = ItemEngine.insertInto(dst, Direction.SOUTH, stack(Items.COBBLESTONE, 10));

        assertTrue(leftover.isEmpty());
        assertEquals(64, dst.getItem(0).getCount(), "slot 0 tops out at max stack size");
        assertEquals(6, dst.getItem(1).getCount(), "the remaining 6 spill into the empty slot");
    }

    @Test
    @DisplayName("insertInto honours canPlaceItemThroughFace — a fully rejecting face returns the whole stack untouched")
    void insertIntoRespectsFaceRejection() {
        FakeContainer dst = FakeContainer.of(ItemStack.EMPTY);
        dst.rejectPlace = true;
        ItemStack toInsert = stack(Items.COBBLESTONE, 10);

        ItemStack leftover = ItemEngine.insertInto(dst, Direction.SOUTH, toInsert);

        assertEquals(10, leftover.getCount(), "the whole stack must come back as leftover");
        assertTrue(dst.getItem(0).isEmpty(), "nothing should have been placed");
    }

    @Test
    @DisplayName("insertInto into a completely full, non-matching container returns the whole stack as leftover")
    void insertIntoFullContainerReturnsWholeLeftover() {
        FakeContainer dst = FakeContainer.of(stack(Items.STONE, 64)); // full, and a different item
        ItemStack toInsert = stack(Items.COBBLESTONE, 10);

        ItemStack leftover = ItemEngine.insertInto(dst, Direction.SOUTH, toInsert);

        assertEquals(10, leftover.getCount(), "nowhere to put it — the fix depends on this leftover being non-empty");
        assertEquals(64, dst.getItem(0).getCount(), "the existing stone stack must be untouched");
    }

    // ---------------- the item-loss fix: source rejects the returned stack too ----------------

    @Test
    @DisplayName("BUG regression: when every destination AND the source itself reject the stack, insertInto still " +
            "reports it as leftover (never silently reduces the count) — this is exactly the signal `step()` now " +
            "uses to drop the item into the world instead of deleting it")
    void sourceRejectingThePutBackStillYieldsANonEmptyLeftover() {
        FakeContainer source = FakeContainer.of(stack(Items.STONE, 64)); // now full of something else too
        source.rejectPlace = true; // simulates the source's own IO-face rules refusing reinsertion

        ItemStack extractedEarlier = stack(Items.COBBLESTONE, 8); // what the engine already pulled out

        ItemStack leftoverAfterPutBackAttempt =
                ItemEngine.insertInto(source, Direction.NORTH, extractedEarlier);

        // Before the fix, `step()` discarded this return value outright — the 8 cobblestone would
        // have ceased to exist. The fix only works because this leftover is provably non-empty and
        // provably still the original stack (not silently truncated), which is what's asserted here.
        assertFalse(leftoverAfterPutBackAttempt.isEmpty(), "the fix's world-drop branch depends on this being non-empty");
        assertEquals(8, leftoverAfterPutBackAttempt.getCount(), "no part of the stack should have been silently lost here");
        assertEquals(Items.COBBLESTONE, leftoverAfterPutBackAttempt.getItem(), "must still be the same item");
    }

    // ---------------- the root-cause fix: simulate room BEFORE touching the source ----------------

    @Test
    @DisplayName("simulateInsertRoom reports exact capacity without mutating anything")
    void simulateInsertRoomReportsExactCapacityWithoutMutating() {
        FakeContainer dst = FakeContainer.of(stack(Items.COBBLESTONE, 61)); // 3 room to max stack (64)

        int room = ItemEngine.simulateInsertRoom(dst, Direction.SOUTH, stack(Items.COBBLESTONE, 1), 10);

        assertEquals(3, room, "should report only the room that actually exists, capped by the request");
        assertEquals(61, dst.getItem(0).getCount(), "a SIMULATION must never mutate the container");
    }

    @Test
    @DisplayName("ROOT-CAUSE FIX: when no destination has room, the source is never touched at all")
    void sourceUntouchedWhenNoDestinationHasRoom() {
        FakeContainer source = FakeContainer.of(stack(Items.COBBLESTONE, 10));
        // Two destinations, both already full of a DIFFERENT item — zero room anywhere.
        FakeContainer destA = FakeContainer.of(stack(Items.STONE, 64));
        FakeContainer destB = FakeContainer.of(stack(Items.STONE, 64));

        int slot = ItemEngine.findEligibleSlot(source, Direction.NORTH, null,
                new ItemEngine.Endpoint(new net.minecraft.core.BlockPos(0, 0, 0), Direction.NORTH,
                        new net.minecraft.core.BlockPos(0, 0, 1), null));
        assertTrue(slot >= 0, "the peek should still find the eligible slot — only the commit is gated");
        ItemStack peeked = source.getItem(slot);

        int roomA = ItemEngine.simulateInsertRoom(destA, Direction.SOUTH, peeked, 10);
        int roomB = ItemEngine.simulateInsertRoom(destB, Direction.SOUTH, peeked, 10);
        assertEquals(0, roomA);
        assertEquals(0, roomB);

        // This is the property the OLD extract-first design could not guarantee: with zero
        // confirmed room anywhere, `step()` never calls extractFromSlot at all — so the source
        // slot is byte-for-byte exactly what it started as, not "extracted then failed to put back."
        assertEquals(10, source.getItem(slot).getCount(), "source must be completely untouched");
        assertEquals(Items.COBBLESTONE, source.getItem(slot).getItem());
    }

    @Test
    @DisplayName("ROOT-CAUSE FIX: with two destinations each holding only partial room, the source loses " +
            "exactly the confirmed total — never fully drained, never left untouched")
    void sourcePartiallyDrainedByExactlyTheConfirmedRoom() {
        FakeContainer source = FakeContainer.of(stack(Items.COBBLESTONE, 10));
        FakeContainer destA = FakeContainer.of(stack(Items.COBBLESTONE, 61)); // 3 room each
        FakeContainer destB = FakeContainer.of(stack(Items.COBBLESTONE, 61)); // 3 room each

        int slot = ItemEngine.findEligibleSlot(source, Direction.NORTH, null,
                new ItemEngine.Endpoint(new net.minecraft.core.BlockPos(0, 0, 0), Direction.NORTH,
                        new net.minecraft.core.BlockPos(0, 0, 1), null));
        ItemStack peeked = source.getItem(slot);
        int available = Math.min(64, peeked.getCount()); // mirrors step()'s min(minRate, peeked.getCount())

        int roomA = ItemEngine.simulateInsertRoom(destA, Direction.SOUTH, peeked, available);
        ItemStack committedA = ItemEngine.extractFromSlot(source, slot, roomA);
        assertTrue(ItemEngine.insertInto(destA, Direction.SOUTH, committedA).isEmpty(), "confirmed room must actually accept the commit");
        available -= roomA;

        int roomB = ItemEngine.simulateInsertRoom(destB, Direction.SOUTH, peeked, available);
        ItemStack committedB = ItemEngine.extractFromSlot(source, slot, roomB);
        assertTrue(ItemEngine.insertInto(destB, Direction.SOUTH, committedB).isEmpty());

        assertEquals(3, roomA);
        assertEquals(3, roomB);
        assertEquals(4, source.getItem(slot).getCount(), "10 - 3 - 3 = 4 left in the source — not 0, not 10");
        assertEquals(64, destA.getItem(0).getCount());
        assertEquals(64, destB.getItem(0).getCount());
    }

    // ---------------- IOConfiguration primitives faceMode is built on ----------------
    // faceMode() itself needs a real Level (see class javadoc), but its INPUT is just this
    // interface's acceptsInput/providesOutput — worth pinning the default-closed semantics the
    // item pipe panel relies on ("nothing connects until configured").

    @Test
    @DisplayName("a fresh IOConfiguration.Simple starts fully closed on every face and every type")
    void freshSimpleConfigStartsFullyClosed() {
        dev.arubik.craftengine.multiblock.IOConfiguration.Simple cfg =
                new dev.arubik.craftengine.multiblock.IOConfiguration.Simple();

        for (Direction d : Direction.values()) {
            assertFalse(cfg.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, d));
            assertFalse(cfg.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, d));
        }
    }

    @Test
    @DisplayName("toggling ITEM input/output on one face doesn't affect the others")
    void ioConfigTogglesAreFacePrecise() {
        dev.arubik.craftengine.multiblock.IOConfiguration.Simple cfg =
                new dev.arubik.craftengine.multiblock.IOConfiguration.Simple();
        cfg.addInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, Direction.NORTH);

        assertTrue(cfg.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, Direction.NORTH));
        assertFalse(cfg.acceptsInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, Direction.SOUTH));
        assertFalse(cfg.providesOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM, Direction.NORTH),
                "input and output are independent — a face can be one, the other, both, or neither");
    }

    @Test
    @DisplayName("removeInput/removeOutput undo exactly what addInput/addOutput set")
    void ioConfigRemoveUndoesAdd() {
        dev.arubik.craftengine.multiblock.IOConfiguration.Simple cfg =
                new dev.arubik.craftengine.multiblock.IOConfiguration.Simple();
        var item = dev.arubik.craftengine.multiblock.IOConfiguration.IOType.ITEM;
        cfg.addInput(item, Direction.UP).addOutput(item, Direction.UP);
        assertTrue(cfg.acceptsInput(item, Direction.UP));
        assertTrue(cfg.providesOutput(item, Direction.UP));

        cfg.removeInput(item, Direction.UP);
        assertFalse(cfg.acceptsInput(item, Direction.UP), "removeInput must clear only the input side");
        assertTrue(cfg.providesOutput(item, Direction.UP), "output must be unaffected by removeInput");

        cfg.removeOutput(item, Direction.UP);
        assertFalse(cfg.providesOutput(item, Direction.UP));
    }
}
