package dev.arubik.craftengine.machine;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.multiblock.IOConfigurationProvider;
import net.momirealms.craftengine.core.util.Key;

/**
 * A machine whose block is an auto-placing {@link dev.arubik.craftengine.multiblock.MultiCellGeometry}
 * structure (a "celled" machine) — neither a plain single-block {@link MachineDefinition} nor a
 * player-assembled {@link dev.arubik.craftengine.multiblock.MultiBlockDefinition}, but a third,
 * narrower thing: one machine, several physical blocks, each of which may need its OWN per-face I/O
 * rule rather than blanket-redirecting every face of every cell to the core.
 *
 * <p>Deliberately a thin wrapper, not a parallel copy of {@link MachineDefinition}: everything a
 * celled machine IS (pages, scripts, energy, flags, recipes) is an ordinary {@link MachineDefinition}
 * — {@link #machine()} — registered exactly as it always has been. This class adds exactly one new
 * thing, {@link #io()}, reusing the assembled-multiblock system's existing {@code IOSpec}/{@code
 * IOConfigurationProvider} rule language (see {@link
 * dev.arubik.craftengine.multiblock.MultiBlockDefinition.IOSpec#parse}) rather than inventing a
 * second one — a rule keyed by a cell's position relative to the core (the same convention {@code
 * IOConfigurationProvider#configurePartIO} already uses for assembled structures) grants that ONE
 * cell specific input/output types on specific faces; a cell with no matching rule falls back to
 * plain redirect-everything-to-core (today's default, unchanged for every machine that doesn't
 * declare {@code cell_io}).
 *
 * <p>The cell SHAPE itself ({@code cells: [...]}) is deliberately NOT stored here — it lives on the
 * block behavior's own config (see {@code MultiCellGeometry#parseCells}), because auto-placement is
 * a block-behavior capability usable by machine and non-machine blocks alike. This class only adds
 * the machine-flavoured extra: per-cell I/O.
 */
public final class CelledMachineDefinition {

    public static final Registry<CelledMachineDefinition> REGISTRY = Registries.create("celled_machine");

    private final Key id;
    private final MachineDefinition machine;
    private final IOConfigurationProvider io;

    public CelledMachineDefinition(Key id, MachineDefinition machine, IOConfigurationProvider io) {
        this.id = id;
        this.machine = machine;
        this.io = io;
    }

    public Key id() {
        return id;
    }

    /** The ordinary machine (pages/scripts/energy/flags/recipes) this celled structure runs. */
    public MachineDefinition machine() {
        return machine;
    }

    /** Per-cell I/O rules, keyed by relative-to-core position. Never {@code null} — a definition with
     * no {@code cell_io} block still gets one, via {@link IOConfigurationProvider#OPEN}-shaped
     * fall-through (every rule misses, so every cell defers to plain core redirect). */
    public IOConfigurationProvider io() {
        return io;
    }

    public static CelledMachineDefinition byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed) : Key.of("polyfills", trimmed));
    }
}
