/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassBlockGrowEvent;

public class PolyClassBlockFormEvent
extends PolyClassBlockGrowEvent {
    public static void refresh() {
    }

    public PolyClassBlockFormEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockFormEvent of(Object object) {
        return new PolyClassBlockFormEvent(object);
    }
}
