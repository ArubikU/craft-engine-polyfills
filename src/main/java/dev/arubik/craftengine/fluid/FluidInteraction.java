/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.criterion.ItemPredicate
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 */
package dev.arubik.craftengine.fluid;

import dev.arubik.craftengine.data.Amount;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.data.VanillaData;
import dev.arubik.craftengine.fluid.FluidType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public final class FluidInteraction {
    public static final Registry<FluidInteraction> REGISTRY = Registries.create("fluid_interaction");
    private final Kind kind;
    private final ItemPredicate input;
    private final FluidType fluid;
    private final Amount amount;
    private final List<ItemStack> results;
    private final Optional<LootItemCondition> condition;
    private final int priority;

    public FluidInteraction(Kind kind, ItemPredicate input, FluidType fluid, Amount amount, List<ItemStack> results, Optional<LootItemCondition> condition, int priority) {
        this.kind = kind;
        this.input = input;
        this.fluid = fluid;
        this.amount = amount;
        this.results = List.copyOf(results);
        this.condition = condition;
        this.priority = priority;
    }

    public Kind kind() {
        return this.kind;
    }

    public FluidType fluid() {
        return this.fluid;
    }

    public int priority() {
        return this.priority;
    }

    public boolean matchesItem(ItemStack stack) {
        return this.input == null || this.input.test(stack);
    }

    public int rollAmount(int fallback) {
        return this.amount == null ? Math.max(0, fallback) : this.amount.roll();
    }

    public boolean conditionHolds() {
        if (this.condition.isEmpty()) {
            return true;
        }
        LootContext context = VanillaData.lootContext(VanillaData.anyLevel());
        if (context == null) {
            return true;
        }
        try {
            return this.condition.get().test(context);
        }
        catch (Throwable ignored) {
            return true;
        }
    }

    public List<ItemStack> resultStacks() {
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(this.results.size());
        for (ItemStack result : this.results) {
            if (result.isEmpty()) continue;
            stacks.add(result.copy());
        }
        return stacks;
    }

    public ItemStack firstResultStack() {
        return this.results.isEmpty() ? ItemStack.EMPTY : this.results.get(0).copy();
    }

    public static List<FluidInteraction> of(Kind kind) {
        ArrayList<FluidInteraction> out = new ArrayList<FluidInteraction>();
        for (FluidInteraction interaction : REGISTRY.values()) {
            if (interaction.kind != kind) continue;
            out.add(interaction);
        }
        out.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return out;
    }

    public String toString() {
        return String.valueOf(this.kind) + "[" + String.valueOf(this.fluid == null ? "any" : this.fluid.id()) + "]";
    }

    public static enum Kind {
        FILL,
        DRAIN,
        REACTION;

    }
}

