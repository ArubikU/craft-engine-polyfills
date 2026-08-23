/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.machine;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.momirealms.craftengine.core.util.Key;

public final class MachineDefinition {
    public static final Registry<MachineDefinition> REGISTRY = Registries.create("machine");
    private final Key id;
    private final String recipeType;
    private final String title;
    private final int menuSize;
    private final int[] inputSlots;
    private final int[] outputSlots;
    private final int[] fuelSlots;
    private final UpgradeSpec upgrades;
    private final int infoSlot;
    private final InfoSpec info;
    private final PagingSpec paging;
    private final List<TankSpec> fluidTanks;
    private final List<TankSpec> gasTanks;
    /** One field per responsibility; see {@link MachineFlags}. Single source of truth for
     *  fuel / recipes / ui / kinetics — the individual accessors below delegate to it. */
    private MachineFlags flags = MachineFlags.DEFAULT;
    private final IOConfiguration io;
    private final List<ButtonSpec> buttons;
    private final PowerSpec power;
    private final List<BarRef> bars;
    private final Map<String, VariableSpec> variables;
    private final List<RendererSpec> renderers;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;
    private final String actionScript;
    private final int actionInterval;
    private Set<String> rpmInputFacesRaw = Set.of();
    private Map<String, Set<String>> rpmInputBlockFilter = Map.of();
    private Map<String, Set<String>> rpmOutputBlockFilter = Map.of();
    private Set<String> rpmOutputFacesRaw = Set.of();
    private boolean rpmOutputDeclared = false;
    private float rpmRatio = 1.0f;
    private boolean isSail = false;
    private float sailRpmBonus = 1.0f;
    private Set<String> rpmOutputInvertedRaw = Set.of();
    /** Gearbox-style: derive the output sign from the driven face instead of a static face list. */
    private boolean rpmOutputRelative = false;
    private String interactScript = null;
    private String attackScript = null;
    /** "auto" = Java default (isProcessing||hasPower), "{file}.pf:{func}" = script-driven activated state. */
    private String statusScript = null;
    private String onPlaceScript = null;
    private String onBreakScript = null;
    /** Fires when a player physically triggers a vanilla pressure plate / lever / button adjacent
     * to this machine — the exact player is available (Bukkit gives it directly, unlike a command
     * block's @p nearest-player guess), bound the same way on_interact binds Player. */
    private String redstoneActuatorScript = null;
    private String onStateChangeScript = null;
    private List<PageDef> pages = List.of();
    /** CraftEnergy buffer (Forge-Energy-alike). Empty by default — no energy field on a machine
     * that doesn't declare one. See {@code energy.EnergyCarrier}. */
    private EnergySpec energy = EnergySpec.none();

    public List<PageDef> pages() { return pages; }
    public void setPages(List<PageDef> p) { this.pages = p != null ? List.copyOf(p) : List.of(); }

    public EnergySpec energy() { return energy; }
    public void setEnergy(EnergySpec e) { this.energy = e == null ? EnergySpec.none() : e; }

    // NOTE: auto-placing multi-cell structure ("cells") is deliberately NOT a MachineDefinition
    // concept — it's a property of the BLOCK BEHAVIOR (parsed from the block's own YAML `behavior:`
    // args, see DataMachineBehavior.Factory and MultiCellGeometry#parseCells), so a non-machine
    // block behavior can use the exact same auto-placement without any MachineDefinition involved.
    // A machine that happens to sit on a multi-cell block finds out via Machine.is_multi_cell /
    // Machine.cell_count (see MachineType), not by reading anything on this class.

    public MachineDefinition(Key id, String recipeType, String title, int menuSize, int[] inputSlots, int[] outputSlots, int[] fuelSlots, UpgradeSpec upgrades, int infoSlot, List<TankSpec> fluidTanks, List<TankSpec> gasTanks, boolean fuelRequired, IOConfiguration io, List<ButtonSpec> buttons, PowerSpec power, List<BarRef> bars, InfoSpec info, PagingSpec paging, Map<String, VariableSpec> variables, List<RendererSpec> renderers, Map<Key, List<MachineAttributes.Mod>> upgradeDefs, String actionScript, int actionInterval) {
        this.id = id;
        this.recipeType = recipeType;
        this.title = title;
        this.menuSize = menuSize;
        this.inputSlots = (int[])inputSlots.clone();
        this.outputSlots = (int[])outputSlots.clone();
        this.fuelSlots = (int[])fuelSlots.clone();
        this.upgrades = upgrades;
        this.infoSlot = infoSlot;
        this.fluidTanks = List.copyOf(fluidTanks);
        this.gasTanks = List.copyOf(gasTanks);
        this.flags = MachineFlags.DEFAULT.withFuel(fuelRequired);
        this.io = io;
        this.buttons = List.copyOf(buttons);
        this.power = power == null ? PowerSpec.none() : power;
        this.bars = bars == null ? List.of() : List.copyOf(bars);
        this.info = info == null ? InfoSpec.none() : info;
        this.paging = paging == null ? PagingSpec.none() : paging;
        this.variables = variables == null ? Map.of() : Map.copyOf(variables);
        this.renderers = renderers == null ? List.of() : List.copyOf(renderers);
        this.upgradeDefs = upgradeDefs == null ? Map.of() : Map.copyOf(upgradeDefs);
        this.actionScript = actionScript;
        this.actionInterval = actionInterval <= 0 ? 20 : actionInterval;
    }

    public Key id() {
        return this.id;
    }

    public String recipeType() {
        return this.recipeType;
    }

    public String title() {
        return this.title;
    }

    public int menuSize() {
        return this.menuSize;
    }

    public int[] inputSlots() {
        return (int[])this.inputSlots.clone();
    }

    public int[] outputSlots() {
        return (int[])this.outputSlots.clone();
    }

    public int[] fuelSlots() {
        return (int[])this.fuelSlots.clone();
    }

    public UpgradeSpec upgrades() {
        return this.upgrades;
    }

    public PagingSpec paging() {
        return this.paging;
    }

    public InfoSpec info() {
        return this.info;
    }

    public int infoSlot() {
        return this.infoSlot;
    }

    public List<TankSpec> fluidTanks() {
        return this.fluidTanks;
    }

    public List<TankSpec> gasTanks() {
        return this.gasTanks;
    }

    /** All behavioural switches, one field per responsibility. */
    public MachineFlags flags() {
        return this.flags;
    }

    public void setFlags(MachineFlags flags) {
        this.flags = flags != null ? flags : MachineFlags.DEFAULT;
    }

    public boolean fuelRequired() {
        return this.flags.fuel();
    }

    public boolean continuousFuel() {
        return this.flags.continuousFuel();
    }

    public MachineDefinition withContinuousFuel(boolean v) {
        this.flags = this.flags.withContinuousFuel(v);
        return this;
    }

    public IOConfiguration io() {
        return this.io;
    }

    public List<ButtonSpec> buttons() {
        return this.buttons;
    }

    public PowerSpec power() {
        return this.power;
    }

    public List<BarRef> bars() {
        return this.bars;
    }

    public Map<String, VariableSpec> variables() {
        return this.variables;
    }

    public List<RendererSpec> renderers() {
        return this.renderers;
    }

    public Map<Key, List<MachineAttributes.Mod>> upgradeDefs() {
        return this.upgradeDefs;
    }

    public String actionScript() {
        return this.actionScript;
    }

    public int actionInterval() {
        return this.actionInterval;
    }

    public boolean openUi() {
        return this.flags.ui();
    }

    public void setOpenUi(boolean openUi) {
        this.flags = this.flags.withUi(openUi);
    }

    /** Refresh the open GUI each tick. Deliberately independent of {@link #runsRecipes()}. */
    public boolean tickUi() {
        return this.flags.uiTick();
    }

    /** Run the recipe/processing pipeline. */
    public boolean runsRecipes() {
        return this.flags.recipes();
    }

    /** Participate in the RPM/stress network. */
    public boolean kinetics() {
        return this.flags.kinetics();
    }

    /** Auto-pull fluid/gas through the faces declared in {@code io.input}. */
    public boolean ioPull() {
        return this.flags.ioPull();
    }

    /** Drive display entities / model animation / spec displays. Distinct from {@link #renderers()},
     *  which returns the renderer specs themselves. */
    public boolean tickRenderers() {
        return this.flags.renderers();
    }

    /** Run action, status and placement scripts. */
    public boolean scripts() {
        return this.flags.scripts();
    }

    /** Tick script-triggered animations. */
    public boolean animations() {
        return this.flags.animations();
    }

    /** May emit a redstone signal. */
    public boolean redstone() {
        return this.flags.redstone();
    }

    public Set<String> rpmInputFacesRaw() {
        return this.rpmInputFacesRaw;
    }

    public void setRpmInputFacesRaw(Set<String> f) {
        this.rpmInputFacesRaw = Set.copyOf(f);
    }

    public Map<String, Set<String>> rpmInputBlockFilter() {
        return this.rpmInputBlockFilter;
    }

    public void setRpmInputBlockFilter(Map<String, Set<String>> m) {
        this.rpmInputBlockFilter = Map.copyOf(m);
    }

    public Map<String, Set<String>> rpmOutputBlockFilter() {
        return this.rpmOutputBlockFilter;
    }

    public void setRpmOutputBlockFilter(Map<String, Set<String>> m) {
        this.rpmOutputBlockFilter = Map.copyOf(m);
    }

    public Set<String> rpmOutputFacesRaw() {
        return this.rpmOutputFacesRaw;
    }

    public void setRpmOutputFacesRaw(Set<String> f) {
        this.rpmOutputFacesRaw = Set.copyOf(f);
    }

    public boolean rpmOutputDeclared() {
        return this.rpmOutputDeclared;
    }

    public void setRpmOutputDeclared(boolean v) {
        this.rpmOutputDeclared = v;
    }

    public float rpmRatio() {
        return this.rpmRatio;
    }

    public void setRpmRatio(float r) {
        this.rpmRatio = r;
    }

    public boolean isSail() {
        return this.isSail;
    }

    public void setSail(boolean v) {
        this.isSail = v;
    }

    public float sailRpmBonus() {
        return this.sailRpmBonus;
    }

    public void setSailRpmBonus(float v) {
        this.sailRpmBonus = v;
    }



    public Set<String> rpmOutputInvertedRaw() {
        return this.rpmOutputInvertedRaw;
    }

    /**
     * When true the machine ignores {@code output_same}/{@code output_inverted} for sign purposes
     * and instead continues straight through the driven axis while reversing across it — the
     * physical behaviour of a gearbox, which a static face list cannot express.
     */
    public boolean rpmOutputRelative() {
        return this.rpmOutputRelative;
    }

    public void setRpmOutputRelative(boolean v) {
        this.rpmOutputRelative = v;
    }

    public void setRpmOutputInvertedRaw(Set<String> f) {
        this.rpmOutputInvertedRaw = Set.copyOf(f);
    }

    public String interactScript() {
        return this.interactScript;
    }

    public void setInteractScript(String s) {
        this.interactScript = s;
    }

    /**
     * Fires for EVERY machine and EVERY resource (item/fluid/gas/energy — not just item pipes,
     * despite the name, kept as the user asked for it) on every external transfer attempt: from
     * {@code canPlaceItemThroughFace}/{@code canTakeItemThroughFace} for items (the single choke
     * point hoppers, funnels, and the item pipe network already go through), and from the slotted
     * insert/extract methods for fluid, gas, and energy. Called with {@code type} ("item"/"fluid"/
     * "gas"/"energy"), {@code payload} (an {@code Item} for "item", a {@code Map{type, amount}} for
     * "fluid"/"gas", or a plain number for "energy"), {@code direction}, and {@code mode} ("input"/
     * "output") bound in its ScriptContext. A script vetoes the transfer with
     * {@code Machine.set_flag("_transfer_cancel", 1)}; anything else (including never running) leaves
     * the existing IOConfiguration decision untouched — this is an ADDITIONAL veto/observation layer,
     * never a replacement for it. This is also the item pipe network's per-face identity filter (see
     * {@code pipe.item.ItemEngine}) — one hook covers both uses.
     */
    private String onTransferScript = null;

    public String onTransferScript() {
        return this.onTransferScript;
    }

    public void setOnTransferScript(String s) {
        this.onTransferScript = s;
    }

    public String attackScript() {
        return this.attackScript;
    }

    public void setAttackScript(String s) { this.attackScript = s; }
    public String statusScript() { return this.statusScript; }
    public void setStatusScript(String s) { this.statusScript = s; }
    public String onPlaceScript() { return this.onPlaceScript; }
    public void setOnPlaceScript(String s) { this.onPlaceScript = s; }
    public String onBreakScript() { return this.onBreakScript; }
    public void setOnBreakScript(String s) { this.onBreakScript = s; }
    public String redstoneActuatorScript() { return this.redstoneActuatorScript; }
    public void setRedstoneActuatorScript(String s) { this.redstoneActuatorScript = s; }
    public String onStateChangeScript() { return this.onStateChangeScript; }
    public void setOnStateChangeScript(String s) { this.onStateChangeScript = s; }

    public static MachineDefinition byName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        return REGISTRY.get(trimmed.indexOf(58) >= 0 ? Key.of((String)trimmed) : Key.of((String)"polyfills", (String)trimmed));
    }

    public String toString() {
        return String.valueOf(this.id) + "(recipes=" + this.recipeType + ")";
    }

    public record UpgradeSpec(int count, int baseUnlocked, int[] explicitSlots) {
        public UpgradeSpec(int count, int baseUnlocked) {
            this(count, baseUnlocked, null);
        }

        public boolean isInline() {
            return this.explicitSlots != null;
        }

        public int[] slots() {
            if (this.explicitSlots != null) {
                return (int[])this.explicitSlots.clone();
            }
            int[] out = new int[this.count];
            for (int i = 0; i < this.count; ++i) {
                out[i] = i;
            }
            return out;
        }

        public int size() {
            return this.explicitSlots != null ? this.explicitSlots.length : this.count;
        }
    }

    public record PowerSpec(boolean consumesStress, double suExponent, int stressGraceTicks, float generatesRpm, int generatesSu, float baseOverclock) {
        public static PowerSpec none() {
            return new PowerSpec(false, 1.25, 20, 0.0f, 0, 2.0f);
        }
    }

    public record InfoSpec(int slot, String type, String source) {
        public static InfoSpec none() {
            return new InfoSpec(-1, "recipe", "");
        }

        public boolean isTank() {
            return "tank".equalsIgnoreCase(this.type);
        }
    }

    public record PagingSpec(int pages, int slots, int prevSlot, int nextSlot, int indicator) {
        public static PagingSpec none() {
            return new PagingSpec(1, 0, -1, -1, -1);
        }

        public boolean isPaged() {
            return this.pages > 1;
        }
    }

    public record TankSpec(String name, int capacity, Key filter) {
    }

    /**
     * A machine's CraftEnergy buffer. Push is effectively infinite but limited by network
     * capacity: {@code perTick} energy is added to this buffer every processing tick,
     * capped at {@code capacity} — whatever the buffer can't hold that tick is simply lost, same as
     * a real Forge-Energy generator overflowing a too-small internal buffer. What the network can
     * then actually move onward is separately capped by the cable tier's own capacity/conductance
     * (see {@code fluid.graph.EnergyEngine}), so a big generator behind small cables still bottlenecks.
     */
    public record EnergySpec(int capacity, int maxInput, int maxOutput, int perTick) {
        public static EnergySpec none() {
            return new EnergySpec(0, 0, 0, 0);
        }

        public boolean isEmpty() {
            return capacity <= 0;
        }
    }

    public record BarRef(Key bar, int[] slots, String source) {
    }

    public record ButtonSpec(int slot, String icon, String action, String name, List<String> lore, String lockedIcon, String lockedWhen) {
    }

    /**
     * A configurable page/menu in the machine UI.
     * Each page has its own title, inventory type/size, static layout items, slots, buttons, bars.
     */
    public record PageDef(
        String title,
        String sizeOrType,
        List<StaticSlot> layout,
        int[] inputSlots,
        int[] outputSlots,
        int[] fuelSlots,
        List<ButtonSpec> buttons,
        List<BarRef> bars,
        int infoSlot,
        String specialType,
        String guiImage,                        // optional: CraftEngine image id for background title overlay
        int guiImageShift,                      // pixel shift for the image (default -8)
        Map<String, ItemSpec> specialItems,     // generic special-page item overrides (locked, filler, increase, decrease, ...)
        List<GhostSlotSpec> ghostSlots,         // script-backed identity-marker slots (item filters, ...) — see MenuSlotType#GHOST
        int[] storageSlots,                     // free, unrestricted slots — see MenuSlotType#STORAGE
        StorageFilterSpec storageFilter,        // what a STORAGE slot on this page will accept
        // "buttons": "file.pf:func" / "layout": "file.pf:func" — instead of a static array, calls
        // this zero-arg function fresh EVERY menu open (see DataMachineBlockEntity#buildPageLayout)
        // with Machine bound, expecting an Array of Map (make_map(...)) button/layout descriptors —
        // for content whose SLOT COUNT itself varies (e.g. specialized_teleporter's destination
        // list), not just per-slot content, which the static array + a "locked_when" condition per
        // slot already covers without this. Null means "use the static buttons()/layout() list" —
        // the two are not mutually exclusive elsewhere but SHOULD be additive, so both still run.
        String buttonsGenerator,
        String layoutGenerator
    ) {
        // Compact constructors for backwards compat
        public PageDef(String title, String sizeOrType, List<StaticSlot> layout, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<ButtonSpec> buttons, List<BarRef> bars, int infoSlot, String specialType) {
            this(title, sizeOrType, layout, inputSlots, outputSlots, fuelSlots, buttons, bars, infoSlot, specialType, null, -8, Map.of(), List.of(), new int[0], StorageFilterSpec.none());
        }
        public PageDef(String title, String sizeOrType, List<StaticSlot> layout, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<ButtonSpec> buttons, List<BarRef> bars, int infoSlot, String specialType, String guiImage, int guiImageShift) {
            this(title, sizeOrType, layout, inputSlots, outputSlots, fuelSlots, buttons, bars, infoSlot, specialType, guiImage, guiImageShift, Map.of(), List.of(), new int[0], StorageFilterSpec.none());
        }
        public PageDef(String title, String sizeOrType, List<StaticSlot> layout, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<ButtonSpec> buttons, List<BarRef> bars, int infoSlot, String specialType, String guiImage, int guiImageShift, Map<String, ItemSpec> specialItems) {
            this(title, sizeOrType, layout, inputSlots, outputSlots, fuelSlots, buttons, bars, infoSlot, specialType, guiImage, guiImageShift, specialItems, List.of(), new int[0], StorageFilterSpec.none());
        }
        public PageDef(String title, String sizeOrType, List<StaticSlot> layout, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<ButtonSpec> buttons, List<BarRef> bars, int infoSlot, String specialType, String guiImage, int guiImageShift, Map<String, ItemSpec> specialItems, List<GhostSlotSpec> ghostSlots) {
            this(title, sizeOrType, layout, inputSlots, outputSlots, fuelSlots, buttons, bars, infoSlot, specialType, guiImage, guiImageShift, specialItems, ghostSlots, new int[0], StorageFilterSpec.none());
        }
        public PageDef(String title, String sizeOrType, List<StaticSlot> layout, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<ButtonSpec> buttons, List<BarRef> bars, int infoSlot, String specialType, String guiImage, int guiImageShift, Map<String, ItemSpec> specialItems, List<GhostSlotSpec> ghostSlots, int[] storageSlots) {
            this(title, sizeOrType, layout, inputSlots, outputSlots, fuelSlots, buttons, bars, infoSlot, specialType, guiImage, guiImageShift, specialItems, ghostSlots, storageSlots, StorageFilterSpec.none());
        }
        public PageDef(String title, String sizeOrType, List<StaticSlot> layout, int[] inputSlots, int[] outputSlots, int[] fuelSlots, List<ButtonSpec> buttons, List<BarRef> bars, int infoSlot, String specialType, String guiImage, int guiImageShift, Map<String, ItemSpec> specialItems, List<GhostSlotSpec> ghostSlots, int[] storageSlots, StorageFilterSpec storageFilter) {
            this(title, sizeOrType, layout, inputSlots, outputSlots, fuelSlots, buttons, bars, infoSlot, specialType, guiImage, guiImageShift, specialItems, ghostSlots, storageSlots, storageFilter, null, null);
        }

        /**
         * What a {@link MenuSlotType#STORAGE} slot on this page will accept. Both an id list and a
         * script may be given at once — the id lists are checked first (fast path, no script
         * engine involved), then the script (if any) gets the final say. Everything empty/null
         * (the default) means "accept anything," same as a plain chest.
         *
         * <p>{@code script} is a {@code "file.pf:function"} ref called with an {@code item} var
         * bound to the candidate stack (see {@code dev.arubik.craftengine.machine.menu.layout.StorageFilters});
         * its RETURN VALUE (not a mutated context var) is the verdict — {@code return false} denies.
         */
        public record StorageFilterSpec(List<String> allow, List<String> deny, String script, int maxAmount) {
            public static StorageFilterSpec none() { return new StorageFilterSpec(List.of(), List.of(), null, 0); }
            /** Whether identity-filtering (allow/deny/script) is configured at all — {@link #maxAmount}
             *  is a separate, independent concern and does NOT affect this. */
            public boolean isEmpty() { return allow.isEmpty() && deny.isEmpty() && (script == null || script.isBlank()); }
            /** {@code maxAmount <= 0} means "use the item's own max stack size" (a plain chest slot). */
            public boolean hasMaxAmount() { return maxAmount > 0; }
        }

        /**
         * One group of {@link MenuSlotType#GHOST} slots sharing a get/set script pair — {@code get}
         * is called per slot per render tick (bound var {@code slot}, must return the item id
         * string to display, or "" for empty) and {@code set} is called on click (bound vars
         * {@code slot} and {@code clicked_id} — the cursor item's id, or "" if the player clicked
         * with an empty hand). Neither script call ever touches a real ItemStack: the slot's
         * displayed item is purely a rendered stand-in, so there's nothing for a client disconnect
         * mid-edit to leave in an exploitable state, and no separate "Save" step is required at all
         * — every click already IS the save.
         */
        public record GhostSlotSpec(int[] slots, String getRef, String setRef, String emptyIcon) {
        }
        /** Look up a special item by name, returning null if not defined. */
        public ItemSpec specialItem(String name) {
            return specialItems == null ? null : specialItems.get(name);
        }
        /**
         * @param locked pin this slot so the player cannot move its contents. Independent of the
         *               slot's {@code MenuSlotType}, so a placeholder can block a real
         *               input/output/fuel/upgrade slot that the machine still tracks.
         */
        public record StaticSlot(int slot, String item, String name, List<String> lore, String action,
                                 boolean locked) {
            public StaticSlot(int slot, String item, String name, List<String> lore, String action) {
                this(slot, item, name, lore, action, false);
            }

            public StaticSlot(int slot, String item, String name, List<String> lore) {
                this(slot, item, name, lore, null, false);
            }
        }

        public org.bukkit.event.inventory.InventoryType inventoryType() {
            if (sizeOrType == null) return org.bukkit.event.inventory.InventoryType.CHEST;
            return switch (sizeOrType.toLowerCase().trim()) {
                case "hopper"     -> org.bukkit.event.inventory.InventoryType.HOPPER;
                case "dropper"    -> org.bukkit.event.inventory.InventoryType.DROPPER;
                case "dispenser"  -> org.bukkit.event.inventory.InventoryType.DISPENSER;
                case "crafting"   -> org.bukkit.event.inventory.InventoryType.CRAFTING;
                case "furnace"    -> org.bukkit.event.inventory.InventoryType.FURNACE;
                case "brewing"    -> org.bukkit.event.inventory.InventoryType.BREWING;
                // ANVIL (3 native slots: input1/input2/output) and SMITHING (4: template/base/
                // addition/output) work the same way as the other non-chest types above — a
                // custom InventoryHolder-backed inventory of these types never runs vanilla
                // repair/smithing logic (that's tied to a real anvil/smithing-table BlockEntity),
                // so the result slot is purely whatever the machine's own script/recipe puts
                // there, same as every other machine here. The vanilla rename text field on an
                // anvil GUI still works client-side; read the renamed text via the normal
                // click-event item name, there's nothing anvil-specific to wire up for that.
                case "anvil"      -> org.bukkit.event.inventory.InventoryType.ANVIL;
                case "smithing"   -> org.bukkit.event.inventory.InventoryType.SMITHING;
                default           -> org.bukkit.event.inventory.InventoryType.CHEST;
            };
        }

        public int resolvedSize() {
            if (sizeOrType == null) return 54;
            try { return Integer.parseInt(sizeOrType.trim()); } catch (NumberFormatException e) { return 54; }
        }

        public boolean isChestType() {
            return inventoryType() == org.bukkit.event.inventory.InventoryType.CHEST;
        }
    }

    /**
     * Reusable item specification — used in special-page param maps, buttons, and layout slots.
     * All fields are optional; null means "use the default".
     *
     * JSON (in machine pages[].items.key):
     *   { "icon": "cml:plus_icon", "name": "<lang:key>", "lore": ["<gray>text"],
     *     "components": { "minecraft:hide_tooltip": true, "minecraft:custom_model_data": 5 } }
     * Short form (icon only):  "cml:plus_icon"
     */
    public record ItemSpec(
        String icon,                    // CraftEngine item key, e.g. "cml:gui_empty"
        String name,                    // MiniMessage string, supports <lang:key>
        java.util.List<String> lore,    // MiniMessage lore lines
        Map<String, Object> components  // Minecraft data components
    ) {
        public static final ItemSpec EMPTY = new ItemSpec(null, null, java.util.List.of(), Map.of());

        public static ItemSpec ofIcon(String icon) {
            return new ItemSpec(icon, null, java.util.List.of(), Map.of());
        }

        /** Build an ItemStack from this spec. Uses cml:gui_empty fallback. */
        public org.bukkit.inventory.ItemStack build() {
            return build(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
        }

        /** Build an ItemStack from this spec with the given fallback material. */
        public org.bukkit.inventory.ItemStack build(org.bukkit.Material fallback) {
            net.momirealms.craftengine.core.util.Key iconKey = (icon != null && !icon.isBlank())
                ? net.momirealms.craftengine.core.util.Key.of(icon) : null;
            net.kyori.adventure.text.Component nameComp = (name != null && !name.isBlank())
                ? net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(name)
                    .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false)
                : net.kyori.adventure.text.Component.empty();

            java.util.List<net.kyori.adventure.text.Component> loreComps = new java.util.ArrayList<>();
            if (lore != null) {
                for (String line : lore) {
                    if (line == null) continue;
                    try {
                        loreComps.add(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                            .deserialize(line)
                            .decoration(net.kyori.adventure.text.format.TextDecoration.ITALIC, false));
                    } catch (Throwable ignored) {
                        loreComps.add(net.kyori.adventure.text.Component.text(line));
                    }
                }
            }
            net.kyori.adventure.text.Component[] loreArr = loreComps.toArray(new net.kyori.adventure.text.Component[0]);
            org.bukkit.inventory.ItemStack item = dev.arubik.craftengine.machine.menu.MenuText.iconItem(iconKey, fallback, nameComp, loreArr);
            if (item == null || item.getType() == org.bukkit.Material.AIR)
                item = new org.bukkit.inventory.ItemStack(fallback);

            if (components != null && !components.isEmpty()) {
                // Apply via NMS DataComponents (no Bukkit meta needed)
                try {
                    net.minecraft.world.item.ItemStack nmsItem = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(item);
                    nmsItem = dev.arubik.craftengine.script.types.primitive.DataComponentTypes.applyJsonComponents(nmsItem, components);
                    item = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nmsItem);
                } catch (Throwable ignored) {}
            }
            return item;
        }
    }
}

