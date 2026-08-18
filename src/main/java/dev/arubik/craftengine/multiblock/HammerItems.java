package dev.arubik.craftengine.multiblock;

import java.util.List;

import net.momirealms.craftengine.core.util.Key;

/** The engineer's-hammer item ids (low→high tier) used to assemble multiblocks. */
public final class HammerItems {

    private HammerItems() {
    }



    private static final java.util.Set<Key> DYNAMIC = java.util.concurrent.ConcurrentHashMap.newKeySet();

    public static void clear() {
        DYNAMIC.clear();
    }

    public static void register(Key id) {
        if (id != null) DYNAMIC.add(id);
    }

    public static boolean isHammer(Key id) {
        return id != null && ( DYNAMIC.contains(id));
    }
}
