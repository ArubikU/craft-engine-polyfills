package dev.arubik.craftengine.crafting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.momirealms.craftengine.core.util.Key;

class CraftingRecipeTest {

    private static final Key DIAMOND = Key.of("minecraft", "diamond");
    private static final Key STICK = Key.of("minecraft", "stick");
    private static final Key COAL = Key.of("minecraft", "coal");
    private static final Key TORCH = Key.of("minecraft", "torch");
    private static final Key PICK = Key.of("minecraft", "diamond_pickaxe");
    private static final Key PLANK = Key.of("minecraft", "oak_planks");

    private CraftingRecipe pickaxe() {
        return CraftingRecipe.shaped(Key.of("t", "pick"))
                .row("DDD").row(" S ").row(" S ")
                .define('D', DIAMOND).define('S', STICK)
                .output(PICK, 1).build();
    }

    private CraftingRecipe torch() {
        return CraftingRecipe.shapeless(Key.of("t", "torch"))
                .ingredient(COAL).ingredient(STICK)
                .output(TORCH, 4).build();
    }

    private CraftingGrid grid3x3(Key[][] layout) {
        CraftingGrid.Builder b = CraftingGrid.builder(3, 3);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (layout[y][x] != null) {
                    b.set(x, y, layout[y][x]);
                }
            }
        }
        return b.build();
    }

    @Test
    void shapedMatchesExactLayout() {
        CraftingGrid g = grid3x3(new Key[][] {
                { DIAMOND, DIAMOND, DIAMOND },
                { null, STICK, null },
                { null, STICK, null },
        });
        assertTrue(pickaxe().matches(g));
    }

    @Test
    void shapedTrimAllowsOffsetPlacement() {
        CraftingRecipe block = CraftingRecipe.shaped(Key.of("t", "blk"))
                .row("PP").row("PP").define('P', PLANK).output(STICK, 1).build();
        CraftingGrid g = grid3x3(new Key[][] {
                { null, null, null },
                { null, PLANK, PLANK },
                { null, PLANK, PLANK },
        });
        assertTrue(block.matches(g));
    }

    @Test
    void shapedRejectsWrongShape() {
        CraftingGrid g = grid3x3(new Key[][] {
                { DIAMOND, DIAMOND, null },
                { null, STICK, null },
                { null, STICK, null },
        });
        assertFalse(pickaxe().matches(g));
    }

    @Test
    void shapelessMatchesAnyArrangementAndRejectsExtras() {
        CraftingGrid ok = grid3x3(new Key[][] {
                { null, null, null },
                { null, COAL, null },
                { STICK, null, null },
        });
        assertTrue(torch().matches(ok));

        CraftingGrid extra = grid3x3(new Key[][] {
                { COAL, STICK, STICK },
                { null, null, null },
                { null, null, null },
        });
        assertFalse(torch().matches(extra));
    }

    @Test
    void shapedMirrorOptional() {
        CraftingRecipe asym = CraftingRecipe.shaped(Key.of("t", "asym"))
                .row("D ").row("SS").define('D', DIAMOND).define('S', STICK)
                .output(PICK, 1).allowMirror(true).build();
        CraftingGrid mirrored = grid3x3(new Key[][] {
                { null, DIAMOND, null },
                { STICK, STICK, null },
                { null, null, null },
        });
        assertTrue(asym.matches(mirrored));
    }

    @Test
    void multipleOutputsPreserved() {
        CraftingRecipe r = CraftingRecipe.shapeless(Key.of("t", "multi"))
                .ingredient(PLANK)
                .output(STICK, 1).output(COAL, 2).build();
        assertEquals(2, r.outputs().size());
        assertEquals(STICK, r.outputs().get(0).id());
        assertEquals(2, r.outputs().get(1).count());
    }

    @Test
    void countRequirementEnforced() {
        CraftingRecipe r = CraftingRecipe.shapeless(Key.of("t", "needs2"))
                .ingredient(COAL, 2).output(TORCH, 1).build();
        CraftingGrid g1 = CraftingGrid.of(3, 3, List.of(
                CraftCell.of(COAL, 1), CraftCell.EMPTY, CraftCell.EMPTY,
                CraftCell.EMPTY, CraftCell.EMPTY, CraftCell.EMPTY,
                CraftCell.EMPTY, CraftCell.EMPTY, CraftCell.EMPTY));
        assertFalse(r.matches(g1));
        CraftingGrid g2 = CraftingGrid.of(3, 3, List.of(
                CraftCell.of(COAL, 2), CraftCell.EMPTY, CraftCell.EMPTY,
                CraftCell.EMPTY, CraftCell.EMPTY, CraftCell.EMPTY,
                CraftCell.EMPTY, CraftCell.EMPTY, CraftCell.EMPTY));
        assertTrue(r.matches(g2));
    }

    @Test
    void registryBucketsByDimensionAndMatches() {
        CraftingRecipeRegistry reg = new CraftingRecipeRegistry();
        reg.register(3, 3, pickaxe());
        reg.register(3, 3, torch());

        CraftingGrid pick = grid3x3(new Key[][] {
                { DIAMOND, DIAMOND, DIAMOND }, { null, STICK, null }, { null, STICK, null } });
        Optional<CraftingRecipeLike> m = reg.match(pick);
        assertTrue(m.isPresent());
        assertEquals(PICK, m.get().outputs(pick).get(0).id());

        assertTrue(reg.match(CraftingGrid.empty(5, 5)).isEmpty());
    }

    @Test
    void shapelessSupports3DGrid() {
        CraftingRecipe r = CraftingRecipe.shapeless(Key.of("t", "cube"))
                .ingredient(DIAMOND).ingredient(STICK).output(PICK, 1).build();
        CraftingGrid vol = CraftingGrid.of(1, 1, 2, List.of(
                CraftCell.of(DIAMOND, 1), CraftCell.of(STICK, 1)));
        assertTrue(r.matches(vol));
    }
}
