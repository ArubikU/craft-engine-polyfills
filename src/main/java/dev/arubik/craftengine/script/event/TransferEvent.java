package dev.arubik.craftengine.script.event;

import dev.arubik.craftengine.script.ScriptValue;

/**
 * Fired for {@code on_pipe_transfer} — every external item/fluid/gas/energy transfer attempt a
 * machine allows through its {@code IOConfiguration}. Read-only mirror of the same
 * {@code type}/{@code payload}/{@code direction}/{@code mode} data this hook has always bound as
 * separate global classes (kept for backward compatibility — see
 * {@code AbstractMachineBlockEntity#runOnTransferScript}), now also reachable off one {@code event}
 * object with a real {@link #cancel()} instead of the old
 * {@code Machine.set_flag("_transfer_cancel", 1)} NBT-flag convention (still checked too, so
 * existing scripts using the flag keep working — {@code event.cancel()} is simply the new,
 * preferred way to say the same thing).
 */
public final class TransferEvent extends ScriptEvent {
    private final String transferType;
    private final ScriptValue payload;
    private final String direction;
    private final String mode;

    public TransferEvent(String transferType, ScriptValue payload, String direction, String mode) {
        super("transfer");
        this.transferType = transferType;
        this.payload = payload;
        this.direction = direction;
        this.mode = mode;
    }

    public String transferType() { return transferType; }
    public ScriptValue payload() { return payload; }
    public String direction() { return direction; }
    public String mode() { return mode; }
}
