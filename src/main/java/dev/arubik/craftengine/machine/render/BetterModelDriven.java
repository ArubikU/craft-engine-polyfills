package dev.arubik.craftengine.machine.render;

/**
 * Implemented by a CraftEngine {@code BlockEntity} that owns a {@link BetterModelMachineRenderer}
 * as its processing visual (e.g. {@code CrusherBlockEntity}). Exists so generic code — in
 * particular {@code dev.arubik.craftengine.contraption.render.ContraptionBlockEntityElementMirror}
 * — can discover "this block has a BetterModel-driven visual" and mirror it into a contraption's
 * real-world transform without depending on any specific machine class. The BE's OWN renderer
 * (see {@link #betterModelRenderer()}) keeps ticking/spawning in whatever world the BE actually
 * lives in (harmless no-op visually when that world is a hidden {@code ContraptionLevel}, since
 * nothing ever calls {@code Player#sendPackets} against a real viewer there) — the mirror drives a
 * SEPARATE tracker instance targeting the bearing's real position instead of touching this one.
 */
public interface BetterModelDriven {
    /** The renderer instance this block entity owns. Never null. */
    BetterModelMachineRenderer betterModelRenderer();
}
