/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package dev.arubik.craftengine.machine.render.formula;

import dev.arubik.craftengine.machine.render.formula.PolyClassRegistry;
import dev.arubik.craftengine.machine.render.formula.PolyFunctionRegistry;
import dev.arubik.craftengine.machine.render.formula.PolyValue;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public final class ItemClass
implements PolyClass {
    private final ItemStack stack;

    public ItemClass(ItemStack stack) {
        this.stack = stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public PolyValue get(String property) {
        if (this.stack == null || this.stack.isEmpty()) {
            return switch (property) {
                case "is_empty" -> PolyValue.of(true);
                case "amount", "count" -> PolyValue.of(0.0);
                case "type", "id" -> PolyValue.of("minecraft:air");
                default -> PolyValue.NULL;
            };
        }
        PolyValue v = PolyClassRegistry.getProperty(this.stack, property);
        if (v != null) {
            return v;
        }
        v = PolyFunctionRegistry.getItemProperty(property, this.stack);
        return v != null ? v : PolyValue.NULL;
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        if (this.stack == null || this.stack.isEmpty()) {
            return PolyValue.NULL;
        }
        PolyValue v = PolyClassRegistry.callMethod(this.stack, method, args);
        if (v != null) {
            return v;
        }
        v = PolyFunctionRegistry.callItemMethod(method, this.stack, args);
        return v != null ? v : this.get(method);
    }
}

