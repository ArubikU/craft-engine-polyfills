package dev.arubik.craftengine.machine.render.formula;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link PolyClass} wrapper around a Bukkit {@link Inventory}.
 *
 * <h3>Properties</h3>
 * <ul>
 *   <li>{@code Inventory.size}     — number of slots ({@link PolyValue.Num})</li>
 *   <li>{@code Inventory.contents} — all non-null, non-AIR items
 *       ({@link PolyValue.ItemList})</li>
 * </ul>
 *
 * <h3>Methods</h3>
 * <ul>
 *   <li>{@code Inventory.slot(n)}             — item at slot n ({@link PolyValue.Item})</li>
 *   <li>{@code Inventory.slots(a, b, …)}       — items at the listed slots
 *       ({@link PolyValue.ItemList})</li>
 *   <li>{@code Inventory.count_of("id")}       — total item count matching material
 *       ({@link PolyValue.Num})</li>
 *   <li>{@code Inventory.has("id")}            — true if any matching item is present
 *       ({@link PolyValue.Bool})</li>
 * </ul>
 */
public final class InventoryClass implements PolyClass {

    private final Inventory inv;

    public InventoryClass(Inventory inv) {
        this.inv = inv;
    }

    @Override
    public PolyValue get(String property) {
        return switch (property) {
            case "size" -> PolyValue.of(inv.getSize());
            case "contents" -> {
                List<ItemStack> items = new ArrayList<>();
                for (ItemStack s : inv.getContents()) {
                    if (s != null && !s.getType().isAir()) items.add(s);
                }
                yield new PolyValue.Array(items.stream().map(PolyValue::ofItem).collect(java.util.stream.Collectors.toList()));
            }
            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        return switch (method) {

            // slot(n) → item at slot n
            case "slot" -> {
                if (args.isEmpty()) yield PolyValue.NULL;
                int n = (int) args.get(0).asNum();
                yield (n >= 0 && n < inv.getSize())
                        ? PolyValue.ofItem(inv.getItem(n))
                        : PolyValue.NULL;
            }

            // slots(a, b, c…) → ItemList of items at those slots
            case "slots" -> {
                List<ItemStack> result = new ArrayList<>();
                for (PolyValue a : args) {
                    int n = (int) a.asNum();
                    if (n >= 0 && n < inv.getSize()) result.add(inv.getItem(n));
                }
                yield new PolyValue.Array(result.stream().map(PolyValue::ofItem).collect(java.util.stream.Collectors.toList()));
            }

            // count_of("minecraft:stone") → total amount of matching items
            case "count_of" -> {
                if (args.isEmpty()) yield PolyValue.of(0);
                String id  = args.get(0).asStr();
                Material mat = Material.matchMaterial(id);
                int total = 0;
                for (ItemStack s : inv.getContents()) {
                    if (s == null) continue;
                    boolean match;
                    if (mat != null) {
                        match = s.getType() == mat;
                    } else if (s.getType().getKey().toString().equals(id)) {
                        match = true;
                    } else {
                        try {
                            net.momirealms.craftengine.core.util.Key ceKey =
                                    net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(s);
                            match = ceKey != null && ceKey.toString().equals(id);
                        } catch (Throwable ignored) { match = false; }
                    }
                    if (match) total += s.getAmount();
                }
                yield PolyValue.of(total);
            }

            // has("minecraft:stone") → boolean
            case "has" -> {
                if (args.isEmpty()) yield PolyValue.of(false);
                String id  = args.get(0).asStr();
                Material mat = Material.matchMaterial(id);
                for (ItemStack s : inv.getContents()) {
                    if (s == null || s.getType().isAir()) continue;
                    boolean match;
                    if (mat != null) {
                        match = s.getType() == mat;
                    } else if (s.getType().getKey().toString().equals(id)) {
                        match = true;
                    } else {
                        try {
                            net.momirealms.craftengine.core.util.Key ceKey =
                                    net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(s);
                            match = ceKey != null && ceKey.toString().equals(id);
                        } catch (Throwable ignored) { match = false; }
                    }
                    if (match) yield PolyValue.of(true);
                }
                yield PolyValue.of(false);
            }

            default -> PolyValue.NULL;
        };
    }
}
