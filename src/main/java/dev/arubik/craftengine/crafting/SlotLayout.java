package dev.arubik.craftengine.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A declarative slot map for a crafting menu: every chest slot index is tagged
 * with a {@link SlotRole}, and INPUT slots additionally carry their (x, y)
 * position in the abstract recipe grid so the menu can assemble a
 * {@link CraftingGrid} without the subclass writing any index math.
 *
 * <p>Pure value type (no Bukkit). A subclass builds one of these once to declare
 * its layout; {@link AbstractCraftingMenu} consumes it.
 */
public final class SlotLayout {

    private final int size;
    private final SlotRole[] roles;
    /** For INPUT slots, the grid position encoded as y*gridWidth + x; else -1. */
    private final int[] gridIndex;
    private final int gridWidth;
    private final int gridHeight;
    private final int gridDepth;
    private final int[] inputSlots; // chest slot indices, ordered by grid index
    private final List<Integer> outputSlots;

    private SlotLayout(int size, SlotRole[] roles, int[] gridIndex, int gridWidth, int gridHeight, int gridDepth,
            int[] inputSlots, List<Integer> outputSlots) {
        this.size = size;
        this.roles = roles;
        this.gridIndex = gridIndex;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gridDepth = gridDepth;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }

    public int size() {
        return size;
    }

    public SlotRole role(int slot) {
        if (slot < 0 || slot >= size) {
            return SlotRole.BACKGROUND;
        }
        return roles[slot];
    }

    public int gridWidth() {
        return gridWidth;
    }

    public int gridHeight() {
        return gridHeight;
    }

    public int gridDepth() {
        return gridDepth;
    }

    /** Chest slot indices for the input grid, in grid (row-major) order. */
    public int[] inputSlots() {
        return inputSlots.clone();
    }

    /** Chest slot indices declared as OUTPUT, in declaration order. */
    public List<Integer> outputSlots() {
        return outputSlots;
    }

    public boolean isInput(int slot) {
        return role(slot) == SlotRole.INPUT;
    }

    public boolean isOutput(int slot) {
        return role(slot) == SlotRole.OUTPUT;
    }

    public boolean isBackground(int slot) {
        return role(slot) == SlotRole.BACKGROUND;
    }

    public boolean isCustom(int slot) {
        return role(slot) == SlotRole.CUSTOM;
    }

    public static Builder builder(int size) {
        return new Builder(size);
    }

    public static final class Builder {
        private final int size;
        private final SlotRole[] roles;
        private final int[] gridIndex;
        private int gridWidth = 0;
        private int gridHeight = 0;
        private int gridDepth = 1;
        private final List<Integer> outputSlots = new ArrayList<>();

        private Builder(int size) {
            this.size = size;
            this.roles = new SlotRole[size];
            this.gridIndex = new int[size];
            Arrays.fill(roles, SlotRole.BACKGROUND);
            Arrays.fill(gridIndex, -1);
        }

        /** Declares the recipe grid dimensions the INPUT slots map to. */
        public Builder grid(int width, int height) {
            return grid(width, height, 1);
        }

        public Builder grid(int width, int height, int depth) {
            this.gridWidth = width;
            this.gridHeight = height;
            this.gridDepth = depth;
            return this;
        }

        /** Maps chest {@code slot} to grid position {@code (gx, gy)} as an INPUT. */
        public Builder input(int slot, int gx, int gy) {
            roles[slot] = SlotRole.INPUT;
            gridIndex[slot] = gy * gridWidth + gx;
            return this;
        }

        public Builder output(int slot) {
            roles[slot] = SlotRole.OUTPUT;
            outputSlots.add(slot);
            return this;
        }

        public Builder custom(int slot) {
            roles[slot] = SlotRole.CUSTOM;
            return this;
        }

        public Builder background(int slot) {
            roles[slot] = SlotRole.BACKGROUND;
            return this;
        }

        public SlotLayout build() {
            if (gridWidth <= 0 || gridHeight <= 0) {
                throw new IllegalStateException("grid(width,height) must be declared before build()");
            }
            int gridCells = gridWidth * gridHeight * gridDepth;
            int[] inputSlots = new int[gridCells];
            Arrays.fill(inputSlots, -1);
            for (int slot = 0; slot < size; slot++) {
                if (roles[slot] == SlotRole.INPUT) {
                    int gi = gridIndex[slot];
                    if (gi < 0 || gi >= gridCells) {
                        throw new IllegalStateException("input slot " + slot + " maps outside grid");
                    }
                    if (inputSlots[gi] != -1) {
                        throw new IllegalStateException("two slots map to grid index " + gi);
                    }
                    inputSlots[gi] = slot;
                }
            }
            for (int gi = 0; gi < gridCells; gi++) {
                if (inputSlots[gi] == -1) {
                    throw new IllegalStateException("grid index " + gi + " has no INPUT slot mapped");
                }
            }
            return new SlotLayout(size, roles.clone(), gridIndex.clone(), gridWidth, gridHeight, gridDepth,
                    inputSlots, List.copyOf(outputSlots));
        }
    }
}
