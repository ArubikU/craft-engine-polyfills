package dev.arubik.craftengine.machine.render.formula;

import dev.arubik.craftengine.crafting.WorkbenchDefinition;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * {@link PolyClass} for workbench context.
 * Exposed as {@code "Workbench"} in {@link PolyContext} for workbench-specific renderers.
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code width}  — grid width (number of input columns)</li>
 *   <li>{@code height} — grid height (number of input rows)</li>
 *   <li>{@code total_inputs}  — total number of input slots</li>
 *   <li>{@code total_outputs} — total number of output slots</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code input(n)}  — item at the nth input slot</li>
 *   <li>{@code output(n)} — item at the nth output slot</li>
 *   <li>{@code tool(n)}   — item at the nth tool slot</li>
 * </ul>
 *
 * <p>The shorthand functions {@code input(n)}, {@code output(n)}, and {@code tool(n)}
 * are also available directly in PolyFormula expressions (without the {@code Workbench.}
 * prefix) when this class is registered in the context.</p>
 */
public final class WorkbenchClass implements PolyClass {

    private final WorkbenchDefinition def;
    private final Inventory inventory;

    public WorkbenchClass(WorkbenchDefinition def, Inventory inventory) {
        this.def = def;
        this.inventory = inventory;
    }

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "width"         -> PolyValue.of(def.layout().gridWidth());
            case "height"        -> PolyValue.of(def.layout().gridHeight());
            case "total_inputs"  -> PolyValue.of(def.layout().inputSlots().length);
            case "total_outputs" -> PolyValue.of(def.layout().outputSlots().size());
            default              -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        int idx = args.isEmpty() ? 0 : (int) args.get(0).asNum();
        return switch (method) {
            case "input" -> {
                int[] slots = def.layout().inputSlots();
                int slot = (idx >= 0 && idx < slots.length) ? slots[idx] : -1;
                yield (slot >= 0 && inventory != null)
                        ? PolyValue.ofItem(inventory.getItem(slot))
                        : PolyValue.NULL;
            }
            case "output" -> {
                java.util.List<Integer> slots = def.layout().outputSlots();
                int slot = (idx >= 0 && idx < slots.size()) ? slots.get(idx) : -1;
                yield (slot >= 0 && inventory != null)
                        ? PolyValue.ofItem(inventory.getItem(slot))
                        : PolyValue.NULL;
            }
            case "tool" -> {
                java.util.List<Integer> slots = def.toolSlots();
                int slot = (idx >= 0 && idx < slots.size()) ? slots.get(idx) : -1;
                yield (slot >= 0 && inventory != null)
                        ? PolyValue.ofItem(inventory.getItem(slot))
                        : PolyValue.NULL;
            }
            default -> PolyValue.NULL;
        };
    }
}
