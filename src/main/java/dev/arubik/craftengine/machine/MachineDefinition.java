package dev.arubik.craftengine.machine;

import java.util.List;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import net.momirealms.craftengine.core.util.Key;

/**
 * A machine described by data instead of by a Java class.
 *
 * <p>
 * Every machine in this plugin used to be a hand-written
 * {@code BlockBehavior} + {@code BlockEntity} pair, with its recipe namespace,
 * tank sizes, slot indices and upgrade-grid geometry baked in as constants. That
 * is fine for a machine with bespoke logic (a pump scanning the world for a
 * vein, a fan classifying processes) but it is pure overhead for the common
 * shape — take items in, optionally burn fuel, run a recipe, put items out.
 * Those are now a single JSON file plus the block config.
 *
 * <p>
 * A definition only describes the <em>declarative</em> half. Machines whose core
 * loop is genuinely custom keep their Java class and simply do not have a
 * definition.
 */
public final class MachineDefinition {

    /** Every data-defined machine. Rebuilt on reload. */
    public static final Registry<MachineDefinition> REGISTRY = Registries.create("machine");

    /**
     * A button on the machine's main page.
     *
     * <p>
     * Declared here rather than only in the pack YAML so that a machine's whole UI —
     * including where the upgrades and overclock buttons sit — travels with the
     * machine definition.
     *
     * @param slot       menu slot the button occupies
     * @param icon       item id for the icon
     * @param action     {@code open_page:N}, {@code deplete_fluid} or {@code deplete_gas}
     * @param name       display name; a lang key or a literal
     * @param lore       tooltip lines; lang keys or literals
     * @param lockedIcon icon shown while the button is locked
     * @param lockedWhen condition that locks it, e.g. {@code no_overclock}
     */
    public record ButtonSpec(int slot, String icon, String action, String name, List<String> lore,
            String lockedIcon, String lockedWhen) {
    }

    /**
     * How a machine relates to rotational power.
     *
     * <p>
     * A recipe already carries the {@code rpm} and {@code su} it demands, but nothing
     * said how the <em>machine</em> converts that demand — the exponent and the grace
     * window were constants inside one block entity, so every stress consumer had to
     * agree by hand.
     *
     * @param consumesStress   whether the machine needs a rotational supply at all;
     *                         false for a purely fuel-burning machine
     * @param suExponent       SU scales as {@code (1+overclock)^suExponent}. Above 1.0
     *                         pushing speed costs disproportionately more stress, which
     *                         is what keeps overclocking a trade rather than free
     * @param stressGraceTicks ticks a machine keeps running after its supply dips,
     *                         so a momentary network wobble does not stall a craft
     * @param generatesRpm     rpm this machine supplies to the network, 0 if it is not
     *                         a generator
     * @param generatesSu      SU capacity it adds to the network
     * @param baseOverclock    overclock headroom a generator has before any upgrade.
     *                         Distinct from {@code suExponent}: this is how far the
     *                         slider may go, that is how much stress each step costs
     */
    public record PowerSpec(boolean consumesStress, double suExponent, int stressGraceTicks,
            float generatesRpm, int generatesSu, float baseOverclock) {

        /** A machine that neither consumes nor produces rotational power. */
        public static PowerSpec none() {
            return new PowerSpec(false, 1.25, 20, 0f, 0, 2.0f);
        }
    }

    /**
     * A gauge this machine shows: which {@code bars/*.json} definition, and where.
     *
     * @param bar    the definition id
     * @param slots  menu slots it occupies
     * @param source what the gauge reads: {@code progress}, {@code fuel}, or
     *               {@code fluid:<tank>} / {@code gas:<tank>} naming one of this
     *               machine's tanks. A machine with two tanks needs this to say which
     *               gauge shows which; it defaults to the bar's own id, which covers
     *               the single-tank case
     */
    public record BarRef(Key bar, int[] slots, String source) {
    }

    /** A tank the machine owns. */
    public record TankSpec(String name, int capacity, Key filter) {
    }

    /**
     * The machine's upgrade grid.
     *
     * <p>
     * Upgrade slots are <b>not</b> menu slots. They occupy reserved container
     * indices {@code 0 .. count-1}, which the main page never displays — they are
     * shown on their own page instead. A definition therefore states how many there
     * are, not where they sit, and the loader rejects any main-page slot that would
     * collide with the reserved range.
     *
     * @param count        how many upgrade slots exist, i.e. indices 0..count-1
     * @param baseUnlocked how many are usable before any EXTRA_SLOTS upgrade
     */
    public record UpgradeSpec(int count, int baseUnlocked, int[] explicitSlots) {

        public UpgradeSpec(int count, int baseUnlocked) {
            this(count, baseUnlocked, null);
        }

        /**
         * Whether the upgrade slots sit on the main page rather than a reserved,
         * separately-paged block.
         *
         * <p>
         * Both conventions exist in this codebase and both are legitimate: a small
         * machine (the furnace: size 9, upgrades at 3-5) shows them inline, while a
         * large one (the crusher: 9 upgrades) reserves indices 0..8 and gives them
         * their own screen. A definition picks one by declaring either {@code slots}
         * or {@code count}.
         */
        public boolean isInline() {
            return explicitSlots != null;
        }

        /** The container indices holding upgrade modules. */
        public int[] slots() {
            if (explicitSlots != null)
                return explicitSlots.clone();
            int[] out = new int[count];
            for (int i = 0; i < count; i++)
                out[i] = i;
            return out;
        }

        /** How many upgrade slots there are, under either convention. */
        public int size() {
            return explicitSlots != null ? explicitSlots.length : count;
        }
    }

    private final Key id;
    private final String recipeType;
    private final String title;
    private final int menuSize;
    private final int[] inputSlots;
    private final int[] outputSlots;
    private final int[] fuelSlots;
    private final UpgradeSpec upgrades;
    private final int infoSlot;
    private final List<TankSpec> fluidTanks;
    private final List<TankSpec> gasTanks;
    private final boolean fuelRequired;
    private final IOConfiguration io;
    private final List<ButtonSpec> buttons;
    private final PowerSpec power;
    private final List<BarRef> bars;

    public MachineDefinition(Key id, String recipeType, String title, int menuSize, int[] inputSlots,
            int[] outputSlots, int[] fuelSlots, UpgradeSpec upgrades, int infoSlot, List<TankSpec> fluidTanks,
            List<TankSpec> gasTanks, boolean fuelRequired, IOConfiguration io, List<ButtonSpec> buttons,
            PowerSpec power, List<BarRef> bars) {
        this.id = id;
        this.recipeType = recipeType;
        this.title = title;
        this.menuSize = menuSize;
        this.inputSlots = inputSlots.clone();
        this.outputSlots = outputSlots.clone();
        this.fuelSlots = fuelSlots.clone();
        this.upgrades = upgrades;
        this.infoSlot = infoSlot;
        this.fluidTanks = List.copyOf(fluidTanks);
        this.gasTanks = List.copyOf(gasTanks);
        this.fuelRequired = fuelRequired;
        this.io = io;
        this.buttons = List.copyOf(buttons);
        this.power = power == null ? PowerSpec.none() : power;
        this.bars = bars == null ? List.of() : List.copyOf(bars);
    }

    public Key id() {
        return id;
    }

    /**
     * The key recipes are filed under in {@code recipes/*.json}.
     *
     * <p>
     * Separate from {@link #id()} so a data machine can adopt the recipe namespace
     * of a machine that used to be Java — replacing a class does not invalidate its
     * recipes.
     */
    public String recipeType() {
        return recipeType;
    }

    public String title() {
        return title;
    }

    public int menuSize() {
        return menuSize;
    }

    public int[] inputSlots() {
        return inputSlots.clone();
    }

    public int[] outputSlots() {
        return outputSlots.clone();
    }

    public int[] fuelSlots() {
        return fuelSlots.clone();
    }

    public UpgradeSpec upgrades() {
        return upgrades;
    }

    /** Slot showing the recipe readout, or -1 for none. */
    public int infoSlot() {
        return infoSlot;
    }

    public List<TankSpec> fluidTanks() {
        return fluidTanks;
    }

    public List<TankSpec> gasTanks() {
        return gasTanks;
    }

    public boolean fuelRequired() {
        return fuelRequired;
    }

    /** Which faces accept or emit what, or {@code null} to leave every face open. */
    public IOConfiguration io() {
        return io;
    }

    /** Buttons on the main page, including the upgrades and overclock entry points. */
    public List<ButtonSpec> buttons() {
        return buttons;
    }

    /** How this machine consumes or produces rotational power. */
    public PowerSpec power() {
        return power;
    }

    /** Gauges this machine shows, resolved against {@code bars/*.json}. */
    public List<BarRef> bars() {
        return bars;
    }

    public static MachineDefinition byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(':') >= 0 ? Key.of(trimmed) : Key.of("polyfills", trimmed));
    }

    @Override
    public String toString() {
        return id + "(recipes=" + recipeType + ")";
    }
}
