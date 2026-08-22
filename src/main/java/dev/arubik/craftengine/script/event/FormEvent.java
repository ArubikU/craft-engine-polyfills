package dev.arubik.craftengine.script.event;

/**
 * Fired for a multiblock's {@code on_form} (structure just validated and swapped to its assembled
 * blocks/entities) or {@code on_disassemble} (about to revert to its unformed parts) — see
 * {@code MultiBlockBehavior#onForm}/{@code #onDisassemble}. {@link #type()} is {@code "form"} or
 * {@code "disassemble"}. By the time either fires the structural change has already happened, so
 * {@link #isCancelled()} is informational only (nothing left to veto) — bound anyway for a
 * consistent {@code event} shape across every hook.
 */
public final class FormEvent extends ScriptEvent {
    public FormEvent(boolean disassembling) {
        super(disassembling ? "disassemble" : "form");
    }
}
