package dev.arubik.craftengine.crafting;

import java.util.List;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import net.momirealms.craftengine.core.util.Key;

/**
 * A crafting station described entirely by data: its menu, its slot layout, and
 * the shape of the structure that hosts it.
 *
 * <p>
 * Previously each station was a Java class pairing a hardcoded {@link SlotLayout}
 * with a behavior, and a recipe was bound to a station by naming the
 * <em>tool item</em> it required — which meant two stations could not share a
 * tool, and adding a station meant adding code. A workbench now has an id;
 * recipes name that id, one generic behavior serves every station, and the tool
 * goes back to being just a special kind of slot.
 */
public final class WorkbenchDefinition {

    /** Every data-defined workbench. Rebuilt on reload. */
    public static final Registry<WorkbenchDefinition> REGISTRY = Registries.create("workbench");

    /**
     * How the station occupies the world, which decides what the behavior must
     * verify before opening and where the block entity lives.
     */
    public enum Structure {
        /** One block, standing alone. */
        SINGLE,
        /** Two horizontally adjacent blocks, like the current workbench. */
        HORIZONTAL_DOUBLE,
        /** A full assembled multiblock, named by {@link #multiblock()}. */
        MULTIBLOCK
    }

    private final Key id;
    private final String title;
    private final int size;
    private final SlotLayout layout;
    private final Structure structure;
    private final Key multiblock;
    private final List<Integer> toolSlots;
    private final List<RenderSlot> renderSlots;

    public WorkbenchDefinition(Key id, String title, int size, SlotLayout layout, Structure structure,
            Key multiblock, List<Integer> toolSlots, List<RenderSlot> renderSlots) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.layout = layout;
        this.structure = structure;
        this.multiblock = multiblock;
        this.toolSlots = List.copyOf(toolSlots);
        this.renderSlots = List.copyOf(renderSlots);
    }

    public Key id() {
        return id;
    }

    public String title() {
        return title;
    }

    /** Container size in slots. */
    public int size() {
        return size;
    }

    /** Slot roles and the recipe-grid mapping. */
    public SlotLayout layout() {
        return layout;
    }

    public Structure structure() {
        return structure;
    }

    /** The multiblock this station requires, when {@link #structure()} is MULTIBLOCK. */
    public Key multiblock() {
        return multiblock;
    }

    /**
     * Slots holding the station's tool.
     *
     * <p>
     * A tool is a {@code CUSTOM} slot as far as the menu framework is concerned —
     * it is not part of the recipe grid and is not consumed like an ingredient —
     * but it is singled out here because recipes may demand a specific tool and
     * spend its durability.
     */
    public List<Integer> toolSlots() {
        return toolSlots;
    }

    public boolean isToolSlot(int slot) {
        return toolSlots.contains(slot);
    }

    /**
     * One slot drawn in the world.
     *
     * <p>
     * Same parameters an item renderer takes — position, rotation, scale — minus the
     * item itself, because the item is whatever currently sits in {@code slot} of the
     * station's block entity. That is the whole point of the {@code workbench_slot}
     * renderer: it is bound to the behavior's block entity, not to the position it is
     * drawn at, so a multi-block station renders its slots wherever it likes.
     *
     * @param slot     container index whose contents to draw
     * @param position offset in pixel coords, authored for {@code facing=south} and
     *                 rotated with the block's facing
     * @param rotation extra XYZ degrees applied after the item is laid flat
     * @param scale    uniform scale
     */
    public record RenderSlot(int slot, float[] position, float[] rotation, float scale, boolean onRightHalf) {
    }

    /**
     * Slots whose contents are drawn in the world by a {@code workbench_slot}
     * renderer.
     */
    public List<RenderSlot> renderSlots() {
        return renderSlots;
    }

    public static WorkbenchDefinition byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed) : Key.of("polyfills", trimmed));
    }

    @Override
    public String toString() {
        return id + "[" + size + " slots, " + structure + "]";
    }
}
