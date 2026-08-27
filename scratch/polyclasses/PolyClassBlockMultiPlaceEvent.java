/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassBlockPlaceEvent;

public class PolyClassBlockMultiPlaceEvent
extends PolyClassBlockPlaceEvent {
    public static void refresh() {
    }

    public PolyClassBlockMultiPlaceEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockMultiPlaceEvent of(Object object) {
        return new PolyClassBlockMultiPlaceEvent(object);
    }
}
