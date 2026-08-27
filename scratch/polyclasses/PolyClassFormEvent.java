/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;

public class PolyClassFormEvent
extends PolyClassEvent {
    public static void refresh() {
    }

    public PolyClassFormEvent(Object object) {
        super(object);
    }

    public static PolyClassFormEvent of(Object object) {
        return new PolyClassFormEvent(object);
    }
}
