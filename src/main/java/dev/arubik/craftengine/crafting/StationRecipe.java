package dev.arubik.craftengine.crafting;

import java.util.List;

import net.momirealms.craftengine.core.util.Key;

/**
 * An Immersive-Engineering-style "engineer's workbench" recipe: a base shaped or
 * shapeless {@link CraftingRecipe} over the 3x2 input grid, PLUS a required work
 * {@code tool} (consuming durability per craft) and OPTIONAL {@link RecipeCondition}
 * / {@link RecipeExecutor} hooks.
 *
 * <p>Matching/output is delegated to the wrapped base recipe so this type reuses
 * the entire shaped/shapeless matcher. It implements {@link CraftingRecipeLike}
 * (the unchanged base SPI) and conditionally {@link RecipeCondition} /
 * {@link RecipeExecutor}; {@link WorkbenchMenu} checks those via {@code instanceof}
 * so the base framework stays clean.
 *
 * <p>The required tool is matched by craft-engine custom id OR vanilla key, the
 * same id space {@link CraftItemAdapter} and {@code AbstractMachineBlockEntity}
 * resolve items into. The tool-slot gate itself (presence + remaining uses) lives
 * in {@link WorkbenchMenu}; this class only declares the requirement.
 */
public final class StationRecipe implements CraftingRecipeLike, RecipeCondition, RecipeExecutor, ChanceOutputs {

    private final CraftingRecipe base;
    private final Key requiredTool;
    private final int toolUsesPerCraft;
    private final RecipeCondition condition;
    private final RecipeExecutor executor;
    /** Per-output drop chance (0..100), aligned to base.outputs(); 100 = guaranteed. */
    private final int[] chances;

    private StationRecipe(CraftingRecipe base, Key requiredTool, int toolUsesPerCraft,
            RecipeCondition condition, RecipeExecutor executor, int[] chances) {
        this.base = base;
        this.requiredTool = requiredTool;
        this.toolUsesPerCraft = Math.max(1, toolUsesPerCraft);
        this.condition = condition;
        this.executor = executor;
        this.chances = chances;
    }

    @Override
    public int outputChance(int index) {
        if (chances == null || index < 0 || index >= chances.length) {
            return 100;
        }
        return chances[index];
    }

    // ---- CraftingRecipeLike (delegate to the wrapped base recipe) ----

    @Override
    public Key id() {
        return base.id();
    }

    @Override
    public boolean matches(CraftingGrid grid) {
        return base.matches(grid);
    }

    @Override
    public List<CraftCell> outputs(CraftingGrid grid) {
        return base.outputs(grid);
    }

    @Override
    public List<CraftCell> remainders(CraftingGrid grid) {
        return base.remainders(grid);
    }

    // ---- station-specific ----

    /** The wrapped base shaped/shapeless recipe (for auto-fill / introspection). */
    public CraftingRecipe base() {
        return base;
    }

    /** The id of the item that must sit in the TOOL slot (custom or vanilla key). */
    public Key requiredTool() {
        return requiredTool;
    }

    /** Durability points consumed from the tool per single craft. */
    public int toolUsesPerCraft() {
        return toolUsesPerCraft;
    }

    public boolean hasCondition() {
        return condition != null;
    }

    public boolean hasExecutor() {
        return executor != null;
    }

    // ---- optional hooks (only consulted by the menu) ----

    @Override
    public boolean test(CraftContext ctx) {
        return condition == null || condition.test(ctx);
    }

    @Override
    public void run(CraftContext ctx) {
        if (executor != null) {
            executor.run(ctx);
        }
    }

    // ---- builder ----

    public static Builder builder(Key id) {
        return new Builder(id);
    }

    /**
     * Fluent builder. Define the base pattern via {@link #shaped()} /
     * {@link #shapeless()} (returning the underlying {@link CraftingRecipe}
     * builder for rows/ingredients/up-to-2 outputs), then set the tool and
     * optional condition/executor here.
     */
    public static final class Builder {
        private final Key id;
        private CraftingRecipe.ShapedBuilder shaped;
        private CraftingRecipe.ShapelessBuilder shapeless;
        private Key requiredTool;
        private int toolUsesPerCraft = 1;
        private RecipeCondition condition;
        private RecipeExecutor executor;
        private final java.util.Map<Integer, Integer> chances = new java.util.HashMap<>();

        private Builder(Key id) {
            this.id = id;
        }

        /** Set the drop chance (0..100) for the output at {@code index} (default 100). */
        public Builder chance(int index, int pct) {
            chances.put(index, Math.max(0, Math.min(100, pct)));
            return this;
        }

        /** Start a shaped base recipe (use its row/define/output methods). */
        public CraftingRecipe.ShapedBuilder shaped() {
            if (shapeless != null) {
                throw new IllegalStateException("base already declared shapeless");
            }
            if (shaped == null) {
                shaped = CraftingRecipe.shaped(id);
            }
            return shaped;
        }

        /** Start a shapeless base recipe (use its ingredient/output methods). */
        public CraftingRecipe.ShapelessBuilder shapeless() {
            if (shaped != null) {
                throw new IllegalStateException("base already declared shaped");
            }
            if (shapeless == null) {
                shapeless = CraftingRecipe.shapeless(id);
            }
            return shapeless;
        }

        public Builder requiredTool(Key tool) {
            this.requiredTool = tool;
            return this;
        }

        public Builder toolUsesPerCraft(int uses) {
            this.toolUsesPerCraft = Math.max(1, uses);
            return this;
        }

        public Builder condition(RecipeCondition condition) {
            this.condition = condition;
            return this;
        }

        public Builder executor(RecipeExecutor executor) {
            this.executor = executor;
            return this;
        }

        public StationRecipe build() {
            if (requiredTool == null) {
                throw new IllegalStateException("StationRecipe requires a tool (requiredTool)");
            }
            CraftingRecipe base = shaped != null ? shaped.build()
                    : shapeless != null ? shapeless.build() : null;
            if (base == null) {
                throw new IllegalStateException("StationRecipe needs a shaped() or shapeless() base");
            }
            if (base.outputs().size() > 2) {
                throw new IllegalStateException("StationRecipe supports at most 2 outputs");
            }
            int[] ch = new int[base.outputs().size()];
            for (int i = 0; i < ch.length; i++) {
                ch[i] = chances.getOrDefault(i, 100);
            }
            return new StationRecipe(base, requiredTool, toolUsesPerCraft, condition, executor, ch);
        }
    }
}
