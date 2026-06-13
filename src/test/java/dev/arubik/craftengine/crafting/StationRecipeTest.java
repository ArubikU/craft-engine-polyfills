package dev.arubik.craftengine.crafting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.momirealms.craftengine.core.util.Key;

/**
 * Pure-JVM coverage of the workbench station: {@link StationRecipe} matching
 * (delegated to the base shaped/shapeless matcher over the 3x2 grid) and the
 * {@link StationToolMath} durability arithmetic. No Bukkit/NMS.
 */
class StationRecipeTest {

    private static final Key IRON = Key.of("minecraft", "iron_ingot");
    private static final Key STICK = Key.of("minecraft", "stick");
    private static final Key IRON_BLOCK = Key.of("minecraft", "iron_block");
    private static final Key NUGGET = Key.of("minecraft", "iron_nugget");
    private static final Key PICK = Key.of("minecraft", "iron_pickaxe");
    private static final Key COPPER = Key.of("minecraft", "copper_ingot");
    private static final Key COPPER_BLOCK = Key.of("minecraft", "copper_block");

    private StationRecipe shapedAssembly() {
        StationRecipe.Builder b = StationRecipe.builder(Key.of("t", "assembly"))
                .requiredTool(PICK).toolUsesPerCraft(5);
        b.shaped().row("III").row(" S ")
                .define('I', IRON).define('S', STICK)
                .output(IRON_BLOCK, 1).output(NUGGET, 4);
        return b.build();
    }

    private CraftingGrid grid3x2(Key[][] layout) {
        CraftingGrid.Builder b = CraftingGrid.builder(3, 2);
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                if (layout[y][x] != null) {
                    b.set(x, y, layout[y][x]);
                }
            }
        }
        return b.build();
    }

    @Test
    void delegatesShapedMatchToBaseOver3x2() {
        StationRecipe r = shapedAssembly();
        CraftingGrid ok = grid3x2(new Key[][] {
                { IRON, IRON, IRON },
                { null, STICK, null },
        });
        assertTrue(r.matches(ok));
        assertEquals(2, r.outputs(ok).size());
        assertEquals(IRON_BLOCK, r.outputs(ok).get(0).id());
        assertEquals(4, r.outputs(ok).get(1).count());
    }

    @Test
    void rejectsWrongPattern() {
        StationRecipe r = shapedAssembly();
        CraftingGrid bad = grid3x2(new Key[][] {
                { IRON, IRON, null },
                { null, STICK, null },
        });
        assertFalse(r.matches(bad));
    }

    @Test
    void registryMatchesAndCarriesToolAndExtensions() {
        StationRecipeRegistry reg = new StationRecipeRegistry();
        StationRecipe r = shapedAssembly();
        reg.register(r);
        CraftingGrid g = grid3x2(new Key[][] {
                { IRON, IRON, IRON }, { null, STICK, null } });
        Optional<StationRecipe> m = reg.match(g);
        assertTrue(m.isPresent());
        assertEquals(PICK, m.get().requiredTool());
        assertEquals(5, m.get().toolUsesPerCraft());
        assertFalse(m.get().hasCondition());
        assertFalse(m.get().hasExecutor());
    }

    @Test
    void shapelessWithConditionAndExecutor() {
        boolean[] ran = { false };
        StationRecipe.Builder b = StationRecipe.builder(Key.of("t", "press"))
                .requiredTool(PICK).toolUsesPerCraft(2)
                .condition(ctx -> ctx.crafts() >= 2)
                .executor(ctx -> ran[0] = true);
        b.shapeless().ingredient(COPPER, 9).output(COPPER_BLOCK, 1);
        StationRecipe r = b.build();
        assertTrue(r.hasCondition());
        assertTrue(r.hasExecutor());

        // condition: crafts>=2 gate via test(ctx)
        assertFalse(r.test(new CraftContext(null, null, 1, null, null)));
        assertTrue(r.test(new CraftContext(null, null, 2, null, null)));

        // executor runs
        r.run(new CraftContext(null, null, 2, null, null));
        assertTrue(ran[0]);
    }

    // ---- tool-use math ----

    @Test
    void remainingUsesIsCeilOfDurabilityOverUses() {
        // iron pickaxe max durability 250 (vanilla). 5 per craft -> 50 crafts.
        assertEquals(50, StationToolMath.remainingUses(250, 0, 5));
        // 248 damage, 2 left, 5/craft -> still 1 craft (the partial use breaks it).
        assertEquals(1, StationToolMath.remainingUses(250, 248, 5));
        // fully damaged -> 0.
        assertEquals(0, StationToolMath.remainingUses(250, 250, 5));
        // non-damageable -> 0.
        assertEquals(0, StationToolMath.remainingUses(0, 0, 5));
    }

    @Test
    void canAffordRespectsRemaining() {
        assertTrue(StationToolMath.canAfford(250, 0, 5, 50));
        assertFalse(StationToolMath.canAfford(250, 0, 5, 51));
        assertFalse(StationToolMath.canAfford(250, 0, 5, 0));
    }

    @Test
    void damageAfterClampsAndDetectsBreak() {
        assertEquals(25, StationToolMath.damageAfter(250, 0, 5, 5));
        // 248 + 5 = 253 clamps to 250 and breaks.
        assertEquals(250, StationToolMath.damageAfter(250, 248, 5, 1));
        assertTrue(StationToolMath.breaksAfter(250, 248, 5, 1));
        assertFalse(StationToolMath.breaksAfter(250, 0, 5, 1));
        // overflow-safe with large craft counts.
        assertEquals(250, StationToolMath.damageAfter(250, 0, 5, Integer.MAX_VALUE));
    }
}
