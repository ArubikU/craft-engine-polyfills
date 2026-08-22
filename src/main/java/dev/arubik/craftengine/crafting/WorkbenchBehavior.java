package dev.arubik.craftengine.crafting;

import java.util.List;

import org.bukkit.entity.Player;

import dev.arubik.craftengine.multiblock.MultiCellBlockBehavior;
import dev.arubik.craftengine.multiblock.MultiCellGeometry;
import net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.entity.player.InteractionResult;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;
import net.momirealms.craftengine.core.world.BlockPos;
import net.momirealms.craftengine.core.world.context.UseOnContext;

/**
 * Immersive-Engineering-style "engineer's workbench" block. Now a REAL 2-wide
 * double block: it extends {@link HorizontalDoubleBlockBehavior}, so the placed
 * block is the LEFT (master) half and a second RIGHT half is auto-placed beside
 * it (one cell toward {@code facing.clockWise()}). Breaking either half removes
 * both. Right-clicking EITHER half forwards to the master, which opens the
 * {@link WorkbenchMenu} (3x2 inputs + 2 outputs + a tool slot), backed by the
 * {@link StationRecipeRegistry}.
 *
 * <p>Registration: expose {@link #FACTORY} under {@link #FACTORY_KEY} (unchanged
 * key {@code polyfills:workbench}); the main thread wires it in
 * {@code block/BlockBehaviors.java}. Sample recipes self-register via
 * {@link WorkbenchSamples} (triggered from this class's static initializer so a
 * placed workbench always has its recipes).
 *
 * <p>Required block-config properties: a {@code 4-direction} {@code facing} and a
 * string {@code half} with values {@code "0"}/{@code "1"} (cell index — {@code 0} =
 * the placed master, {@code 1} = the auto-placed second cell one step toward {@code
 * facing.clockWise()}; same convention the deleted HorizontalDoubleBlockBehavior used
 * with left/right, now expressed as the generic {@link MultiCellBlockBehavior}'s single-
 * offset case). If absent, the behavior degrades to a single-block crafting station
 * (still opens its menu).
 */
public class WorkbenchBehavior extends MultiCellBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:workbench");

    public static final Factory FACTORY = new Factory();

    static {
        // Mirror UpgradeableFurnace/CraftingSamples self-registration: ensure the
        // station recipes exist as soon as the behavior class is touched.
        WorkbenchSamples.registerDefaults();
    }

    private final String title;
    private final StationRecipeRegistry registry;
    /**
     * The data definition this station follows, or null for a config-only workbench.
     *
     * <p>
     * Carries the slot layout, the tool slots, the world-render slots and the
     * structure kind (single / horizontal double / multiblock), so those stop being
     * properties of this Java class.
     */
    private WorkbenchDefinition definition;

    // ---- tabletop render config (pixel coords authored for facing=south; rotate with facing) ----
    public float[] blueprintPos = { 4f, 13.5f, 4f };
    public float blueprintScale = 0.5f;
    public float[] outputPos = { 4f, 13.5f, 4f };
    public float outputScale = 0.375f;
    public float[] output2Offset = { 3f, 1f, 0f }; // added to outputPos for a 2nd output
    // Extra rotation (degrees, X/Y/Z) applied to each item after it's laid flat on the table.
    public float[] blueprintRotation = { 0f, 0f, 0f };
    public float[] outputRotation = { 0f, 0f, 0f };
    public float[] output2Rotation = { 0f, 0f, 22f };

    /** Per-item-id ADDITIVE render tweak (added on top of the slot's base pos/rotation/scale). */
    public static final class RenderOverride {
        public float[] pos = { 0f, 0f, 0f };
        public float[] rot = { 0f, 0f, 0f };
        public float scale = 0f;
    }

    /** item-id (e.g. "cml:funnel") -> additive render override. Empty = no overrides. */
    public final java.util.Map<String, RenderOverride> renders = new java.util.HashMap<>();

    public WorkbenchBehavior(BlockDefinition block, String title, StationRecipeRegistry registry,
            String facingProperty, String halfProperty) {
        // Single secondary cell one step toward facing.clockWise() — MultiCellGeometry's x=+1
        // rotates identically to the old HorizontalDoubleGeometry.rightDirection() convention
        // (verified: NORTH -> +x -> EAST; EAST -> +x -> SOUTH; same as facing.clockWise() both times).
        super(block, facingProperty, halfProperty, List.of(new MultiCellGeometry.Offset(1, 0, 0)));
        this.title = title != null ? title : "Engineer's Workbench";
        this.registry = registry != null ? registry : StationRecipeRegistry.global();
    }

    /** The data definition backing this station, or null. */
    public WorkbenchDefinition definition() {
        return definition;
    }

    public void setDefinition(WorkbenchDefinition definition) {
        this.definition = definition;
    }

    protected AbstractCraftingMenu createMenu(Player player) {
        return new WorkbenchMenu(title, registry, null);
    }

    protected AbstractCraftingMenu createMenu(Player player, WorkbenchBlockEntity be) {
        return new WorkbenchMenu(title, registry, be);
    }

    @Override
    protected net.momirealms.craftengine.core.block.entity.BlockEntityController createMasterController(
            net.momirealms.craftengine.core.block.entity.BlockEntity blockEntity) {
        WorkbenchBlockEntity be = new WorkbenchBlockEntity(blockEntity);
        be.setConfig(this); // pass parsed render config directly (getBlockBehavior() returns null here)
        return be;
    }

    /**
     * Right-click on either half lands here (resolved to the master). Open the
     * workbench menu for the clicking player. The RIGHT half never opens its own
     * menu; it always forwards through {@link HorizontalDoubleBlockBehavior}.
     */
    @Override
    protected InteractionResult onMasterUse(UseOnContext context, ImmutableBlockState masterState, BlockPos masterPos) {
        try {
            if (context.getPlayer() instanceof BukkitServerPlayer cePlayer
                    && cePlayer.platformPlayer() instanceof Player bukkit) {
                WorkbenchBlockEntity be = null;
                try {
                    net.momirealms.craftengine.core.world.CEWorld cw =
                            new net.momirealms.craftengine.bukkit.world.BukkitWorld(bukkit.getWorld()).storageWorld();
                    net.momirealms.craftengine.core.block.entity.BlockEntity bent =
                            cw.getBlockEntityAtIfLoaded(masterPos);
                    if (bent != null && bent.controller instanceof WorkbenchBlockEntity wbe)
                        be = wbe;
                } catch (Throwable ignored2) {
                }
                if (be != null) {
                    be.openMenu(bukkit, title, registry); // shared inventory -> no blueprint dup
                    return InteractionResult.SUCCESS_AND_CANCEL;
                }
                AbstractCraftingMenu menu = createMenu(bukkit, be);
                if (menu != null) {
                    menu.open(bukkit);
                    return InteractionResult.SUCCESS_AND_CANCEL;
                }
            }
        } catch (Throwable ignored) {
            // fall through to PASS
        }
        return InteractionResult.PASS;
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            String title = (String) arguments.getOrDefault("title", "Engineer's Workbench");
            String facingProp = (String) arguments.getOrDefault("facing_property", DEFAULT_FACING_PROPERTY);
            String halfProp = (String) arguments.getOrDefault("half_property", "half");
            WorkbenchBehavior b = new WorkbenchBehavior(block, title, StationRecipeRegistry.global(), facingProp, halfProp);
            // `workbench: <id>` binds this block to a workbenches/*.json definition. Without
            // it the behavior keeps its legacy config-only shape, so existing packs are
            // unaffected.
            Object wb = arguments.get("workbench");
            if (wb != null) {
                WorkbenchDefinition def = WorkbenchDefinition.byName(String.valueOf(wb));
                if (def == null)
                    throw new IllegalArgumentException("Block " + block.id() + " names workbench '" + wb
                            + "' which matches no entry in workbenches/*.json. Known: "
                            + WorkbenchDefinition.REGISTRY.keys());
                b.setDefinition(def);
            }
            b.blueprintPos = vec(arguments, "blueprint_pos", b.blueprintPos);
            b.blueprintScale = flt(arguments, "blueprint_scale", b.blueprintScale);
            b.outputPos = vec(arguments, "output_pos", b.outputPos);
            b.outputScale = flt(arguments, "output_scale", b.outputScale);
            b.output2Offset = vec(arguments, "output2_offset", b.output2Offset);
            b.blueprintRotation = vec(arguments, "blueprint_rotation", b.blueprintRotation);
            b.outputRotation = vec(arguments, "output_rotation", b.outputRotation);
            b.output2Rotation = vec(arguments, "output2_rotation", b.output2Rotation);
            Object rnd = arguments.get("renders");
            if (rnd instanceof java.util.Map<?, ?> m) {
                for (java.util.Map.Entry<?, ?> e : m.entrySet()) {
                    if (!(e.getValue() instanceof java.util.Map<?, ?> v))
                        continue;
                    RenderOverride r = new RenderOverride();
                    r.pos = vecMap(v, "pos", r.pos);
                    r.rot = vecMap(v, "rotation", r.rot);
                    Object sc = v.get("scale");
                    r.scale = sc instanceof Number n ? n.floatValue() : 0f;
                    b.renders.put(String.valueOf(e.getKey()), r);
                }
            }
            return b;
        }

        private static float flt(ConfigSection a, String key, float def) {
            Object v = a.get(key);
            return v instanceof Number n ? n.floatValue() : def;
        }

        private static float[] vec(ConfigSection a, String key, float[] def) {
            return toVec(a.get(key), def);
        }

        private static float[] vecMap(java.util.Map<?, ?> a, String key, float[] def) {
            return toVec(a.get(key), def);
        }

        private static float[] toVec(Object v, float[] def) {
            if (v instanceof java.util.List<?> l && l.size() >= 3) {
                float[] out = new float[3];
                for (int i = 0; i < 3; i++)
                    out[i] = l.get(i) instanceof Number n ? n.floatValue() : def[i];
                return out;
            }
            return def;
        }
    }
}
