package dev.arubik.craftengine.contraption.protection;

import java.util.UUID;

import dev.arubik.craftengine.contraption.ContraptionState;

/**
 * Contraption-<b>ownership</b> (anti-theft) checks — the second, purely-internal ownership axis
 * the design insists must stay distinct from the land-claim SPI
 * ({@link ContraptionProtection}): "whose contraption is this, and may this OTHER player
 * operate/disassemble it?" (roadmap item #7, {@code .migration/ROADMAP-claims-and-phys.md} §1).
 * Keyed off {@link ContraptionState#owner()} — no external plugin involved — so it is a small
 * always-present component, NOT a provider on the land-claim chain.
 *
 * <p><b>Phase 1 = default-allow (no gameplay change).</b> Every method here returns {@code true}
 * unconditionally today, exactly matching pre-protection behavior (any player with a hammer can
 * still disassemble any contraption, anyone can still operate any contraption's captured
 * chest/machine). This class exists so the enforcement seams are already wired and consulted;
 * the real rule — {@code owner == null || owner.equals(actor) || actor has an admin-bypass
 * permission} — lands in Phase C3 by changing ONLY the bodies below, with no call-site churn.
 */
public final class ContraptionOwnership {

    private ContraptionOwnership() {
    }

    /**
     * May {@code actor} operate (right-click / open menus of) the blocks of the contraption owned
     * per {@code state}? Phase 1: always {@code true}. Phase C3 will gate a non-owner out here
     * (anti-theft for chests/machines aboard someone else's contraption).
     */
    public static boolean mayInteract(UUID actor, ContraptionState state) {
        return true; // Phase 1 default-allow — see class javadoc
    }

    /**
     * May {@code actor} disassemble / pack up the contraption owned per {@code state}? Phase 1:
     * always {@code true}. Phase C3 will require {@code actor == owner || admin-bypass} so a
     * random player with a hammer can't steal another's contraption.
     */
    public static boolean mayDisassemble(UUID actor, ContraptionState state) {
        return true; // Phase 1 default-allow — see class javadoc
    }
}
