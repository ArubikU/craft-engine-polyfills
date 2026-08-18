/*
 * Decompiled with CFR 0.152.
 */
package dev.arubik.craftengine.contraption.protection;

import dev.arubik.craftengine.contraption.core.ContraptionState;
import java.util.UUID;

public final class ContraptionOwnership {
    private ContraptionOwnership() {
    }

    public static boolean mayInteract(UUID actor, ContraptionState state) {
        return true;
    }

    public static boolean mayDisassemble(UUID actor, ContraptionState state) {
        return true;
    }
}

