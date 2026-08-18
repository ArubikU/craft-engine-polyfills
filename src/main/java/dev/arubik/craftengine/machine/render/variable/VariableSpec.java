/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.machine.render.variable;

public sealed interface VariableSpec {

    public record Formula(String expr) implements VariableSpec
    {
    }

    public record TankVar(String tankName, boolean isGas, String property) implements VariableSpec
    {
    }

    public record ItemSlot(int slot) implements VariableSpec
    {
    }

    public record NumExpr(String expr) implements VariableSpec
    {
    }

    public record BoolExpr(String expr) implements VariableSpec
    {
    }

    public record BoolSource(String source) implements VariableSpec
    {
    }
}

