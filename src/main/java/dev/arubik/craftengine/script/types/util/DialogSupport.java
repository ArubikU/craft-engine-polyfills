package dev.arubik.craftengine.script.types.util;

/**
 * Version guard for Paper's Dialog API (io.papermc.paper.dialog.*, added around Paper 1.21.6).
 * Confirmed present at the project's current paper_version (1.21.11) at compile time — this class
 * exists so a JAR BUILT against that API doesn't crash with NoClassDefFoundError if it's ever run
 * on an older Paper build (or a fork/API implementation without Dialogs) at runtime. Probed once
 * and cached; every Dialog-related script method must check this before touching any Dialog class.
 */
public final class DialogSupport {
    private static final boolean AVAILABLE = probe();

    private DialogSupport() {}

    public static boolean isAvailable() {
        return AVAILABLE;
    }

    private static boolean probe() {
        try {
            Class.forName("io.papermc.paper.dialog.Dialog");
            Class.forName("io.papermc.paper.registry.data.dialog.action.DialogAction");
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
