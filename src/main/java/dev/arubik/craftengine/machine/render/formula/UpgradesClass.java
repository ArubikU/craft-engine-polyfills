package dev.arubik.craftengine.machine.render.formula;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link PolyClass} wrapper around a machine's installed upgrade counts.
 *
 * <p>Registered as {@code Upgrades} in a {@link PolyContext}; accessible in
 * expressions as:</p>
 * <pre>
 *   Upgrades.total            — total number of upgrade modules installed (Num)
 *   Upgrades.{typeName}       — count for that upgrade type (Num), e.g. Upgrades.overclock
 *   Upgrades.inventory        — ItemList of all items in the upgrade inventory
 *   Upgrades.count("speed")   — count of "speed" upgrade modules (Num)
 *   Upgrades.slot(n)          — item at slot n in the upgrade inventory (Item)
 * </pre>
 *
 * <p>The {@code upgradesByType} map keys are upgrade type names (e.g. {@code "overclock"},
 * {@code "efficiency"}, {@code "speed"}).  Values are slot-counts (how many
 * modules of that type are installed).  Both the map and its values are null-safe.</p>
 *
 * <p>The optional {@code upgradeInventory} is the physical container that holds the
 * upgrade item-stacks.  When provided, {@code Upgrades.inventory} returns an
 * {@link PolyValue.ItemList} of its non-empty contents, and {@code Upgrades.slot(n)}
 * returns the item at slot {@code n}.</p>
 */
public final class UpgradesClass implements PolyClass {

    private final Map<String, Integer> upgradesByType;
    private final Inventory upgradeInventory; // nullable
    private final int total;

    public UpgradesClass(Map<String, Integer> upgradesByType) {
        this(upgradesByType, null);
    }

    public UpgradesClass(Map<String, Integer> upgradesByType, Inventory upgradeInventory) {
        this.upgradesByType    = upgradesByType != null ? upgradesByType : Map.of();
        this.upgradeInventory  = upgradeInventory;
        this.total             = this.upgradesByType.values().stream()
                                        .mapToInt(i -> i != null ? i : 0).sum();
    }

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "total" -> PolyValue.of(total);
            case "inventory" -> {
                if (upgradeInventory == null) yield new PolyValue.Array(List.of());
                List<ItemStack> items = new ArrayList<>();
                for (ItemStack s : upgradeInventory.getContents()) {
                    if (s != null && !s.getType().isAir()) items.add(s);
                }
                yield new PolyValue.Array(items.stream().map(PolyValue::ofItem).collect(java.util.stream.Collectors.toList()));
            }
            default -> {
                // Allow Upgrades.overclock as shorthand for Upgrades.count("overclock")
                Integer c = upgradesByType.get(property);
                yield PolyValue.of(c != null ? c : 0);
            }
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return switch (method) {
            case "count" -> {
                if (args.isEmpty()) yield PolyValue.of(0);
                String type = args.get(0).asStr();
                Integer c = upgradesByType.get(type);
                yield PolyValue.of(c != null ? c : 0);
            }
            case "slot" -> {
                if (upgradeInventory == null || args.isEmpty()) yield PolyValue.NULL;
                int n = (int) args.get(0).asNum();
                yield (n >= 0 && n < upgradeInventory.getSize())
                        ? PolyValue.ofItem(upgradeInventory.getItem(n))
                        : PolyValue.NULL;
            }
            default -> PolyValue.NULL;
        };
    }
}
