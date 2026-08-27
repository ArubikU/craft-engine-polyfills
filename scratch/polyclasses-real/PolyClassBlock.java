/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.script;

public class PolyClassBlock {
    protected final Object instance;

    public static void refresh() {
    }

    public PolyClassBlock(Object object) {
        this.instance = object;
    }

    public static PolyClassBlock of(Object object) {
        return new PolyClassBlock(object);
    }
}
