package dev.arubik.craftengine.machine.menu.bar;

import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import net.momirealms.craftengine.core.util.Key;

/**
 * A gauge described once, independently of any machine.
 *
 * <p>
 * Bars used to be spelled out inside each machine's block config — a fluid gauge
 * ran to some 240 lines listing every fill level of every liquid, and a second
 * machine showing the same gauge had to repeat all of it. A bar is the same
 * everywhere it appears, so it belongs in {@code bars/*.json}; a machine then
 * only says <em>which</em> bars it shows and <em>where</em>.
 */
public final class BarDefinition {

    /** Every data-defined gauge. Rebuilt on reload. */
    public static final Registry<BarDefinition> REGISTRY = Registries.create("bar");

    private final Key id;
    private final String model;
    private final String family;
    private final String name;
    private final Map<MachineBar.Part, List<MachineBar.BarState>> states;
    private final List<List<MachineBar.BarState>> segments;

    public BarDefinition(Key id, String model, String family, String name,
            Map<MachineBar.Part, List<MachineBar.BarState>> states,
            List<List<MachineBar.BarState>> segments) {
        this.id = id;
        this.model = model;
        this.family = family;
        this.name = name;
        this.states = states;
        this.segments = segments;
    }

    public Key id() {
        return id;
    }

    /**
     * Binds this gauge to the slots a machine puts it on.
     *
     * <p>
     * A definition carries no slots of its own: where a gauge sits is a property of
     * the machine showing it, not of the gauge. Everything else here is immutable, so
     * one definition can back several machines at once.
     */
    public MachineBar toBar(int[] slots) {
        return new MachineBar(id.value(), model, slots == null ? new int[0] : slots,
                states, family, name, segments);
    }

    public static BarDefinition byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed) : Key.of("polyfills", trimmed));
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
