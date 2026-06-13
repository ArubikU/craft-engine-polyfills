package dev.arubik.craftengine.crafting;

import java.util.List;

/**
 * Pure, Bukkit-free math for crafting transactions (normal take + shift-click
 * bulk craft). Kept deliberately free of any Bukkit/NMS types so the
 * (notoriously bug-prone) count arithmetic is fully unit-testable on the plain
 * JVM.
 *
 * <p>The two scenarios the framework must get right:
 * <ul>
 *   <li><b>Normal take</b> crafts exactly once: consume one of each occupied
 *       input, produce all outputs.</li>
 *   <li><b>Shift-click</b> crafts as many times as possible, bounded by BOTH
 *       (a) how many crafts the inputs allow and (b) how much room the
 *       destination (player inventory) has for ALL outputs of that many crafts.
 *       Never overflow / void items.</li>
 * </ul>
 */
public final class CraftingTransaction {

    private CraftingTransaction() {
    }

    /**
     * The maximum number of crafts that can be performed without overflowing the
     * destination, bounded by both input availability and output room.
     *
     * @param inputsAvailable per-occupied-input available counts (each entry is
     *        the stack size of one input cell that the recipe consumes one of per
     *        craft). The craft count an input allows is its own count, since each
     *        craft consumes exactly one from that cell.
     * @param outputsPerCraft per-output produced counts for a SINGLE craft
     *        (primary + secondary). Must be the same length/order as
     *        {@code freeSpaceForOutput}.
     * @param freeSpaceForOutput per-output total free space in the destination
     *        for that output item id (how many of that item can still be added).
     * @return the largest {@code n >= 0} such that every input has {@code >= n}
     *         available AND every output {@code i} satisfies
     *         {@code n * outputsPerCraft[i] <= freeSpaceForOutput[i]}.
     */
    public static int maxCrafts(int[] inputsAvailable, int[] outputsPerCraft, int[] freeSpaceForOutput) {
        if (outputsPerCraft.length != freeSpaceForOutput.length) {
            throw new IllegalArgumentException("outputs and freeSpace lengths differ");
        }
        // Bound (a): inputs. Each occupied input contributes one per craft, so the
        // limiting factor is the smallest available input count.
        int byInputs = Integer.MAX_VALUE;
        for (int avail : inputsAvailable) {
            byInputs = Math.min(byInputs, avail);
        }
        if (inputsAvailable.length == 0) {
            byInputs = 0;
        }
        if (byInputs <= 0) {
            return 0;
        }
        // Bound (b): destination room for each output.
        int byOutputs = byInputs;
        for (int i = 0; i < outputsPerCraft.length; i++) {
            int per = outputsPerCraft[i];
            if (per <= 0) {
                continue; // an output producing nothing imposes no constraint
            }
            int free = Math.max(0, freeSpaceForOutput[i]);
            int allowed = free / per; // floor: only whole crafts that fully fit
            byOutputs = Math.min(byOutputs, allowed);
        }
        return Math.max(0, byOutputs);
    }

    /**
     * Convenience overload working off {@link CraftCell} outputs. Maps each
     * output cell's {@code count()} to {@code outputsPerCraft}.
     */
    public static int maxCrafts(int[] inputsAvailable, List<CraftCell> outputs, int[] freeSpaceForOutput) {
        int[] per = new int[outputs.size()];
        for (int i = 0; i < per.length; i++) {
            per[i] = outputs.get(i).count();
        }
        return maxCrafts(inputsAvailable, per, freeSpaceForOutput);
    }
}
