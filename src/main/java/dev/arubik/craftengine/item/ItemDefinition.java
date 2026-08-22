package dev.arubik.craftengine.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.machine.MachineDefinition;
import net.momirealms.craftengine.core.util.Key;

/**
 * The item-side analog of {@code MachineDefinition}: a data-driven description of an
 * "item behavior" — a menu built from the SAME {@code pages[]} system machines use (buttons,
 * bars, paging, ghost slots, free storage slots), tanks (fluid/gas/energy), and event script
 * hooks — loaded from {@code items/*.json} and interpreted by {@code DataItemBehavior} at runtime.
 */
public final class ItemDefinition {
    public static final Registry<ItemDefinition> REGISTRY = Registries.create("item");

    private final Key id;
    private final String title;
    private final List<MachineDefinition.PageDef> pages;
    private final boolean openOnRightClick;
    private final boolean openOnShiftRightClick;
    private final List<TankSpec> tanks;
    private final Map<String, String> scripts;
    private final Key placeBlock;
    private final ScriptToggle fillStorage;
    private final ScriptToggle fillFlags;
    private final ScriptToggle fillTyped;
    private final int tickInterval;
    private final List<String> bridgeFlags;
    private final List<String> bridgeStrFlags;
    private final List<TypedBridgeSpec> bridgeTyped;
    private final String nameTemplate;
    private final List<String> loreTemplate;

    public ItemDefinition(Key id, String title, List<MachineDefinition.PageDef> pages,
            boolean openOnRightClick, boolean openOnShiftRightClick, List<TankSpec> tanks,
            Map<String, String> scripts, Key placeBlock, ScriptToggle fillStorage, ScriptToggle fillFlags,
            ScriptToggle fillTyped,
            int tickInterval, List<String> bridgeFlags, List<String> bridgeStrFlags, List<TypedBridgeSpec> bridgeTyped,
            String nameTemplate, List<String> loreTemplate) {
        this.id = id;
        this.title = title;
        this.pages = List.copyOf(pages);
        this.openOnRightClick = openOnRightClick;
        this.openOnShiftRightClick = openOnShiftRightClick;
        this.tanks = List.copyOf(tanks);
        this.scripts = Map.copyOf(scripts);
        this.placeBlock = placeBlock;
        this.fillStorage = fillStorage != null ? fillStorage : ScriptToggle.TRUE;
        this.fillFlags = fillFlags != null ? fillFlags : ScriptToggle.TRUE;
        this.fillTyped = fillTyped != null ? fillTyped : ScriptToggle.TRUE;
        this.tickInterval = Math.max(1, tickInterval);
        this.bridgeFlags = bridgeFlags == null ? List.of() : List.copyOf(bridgeFlags);
        this.bridgeStrFlags = bridgeStrFlags == null ? List.of() : List.copyOf(bridgeStrFlags);
        this.bridgeTyped = bridgeTyped == null ? List.of() : List.copyOf(bridgeTyped);
        this.nameTemplate = nameTemplate;
        this.loreTemplate = loreTemplate == null ? List.of() : List.copyOf(loreTemplate);
    }

    public Key id() { return id; }
    public String title() { return title; }
    public List<MachineDefinition.PageDef> pages() { return pages; }
    public boolean hasMenu() { return !pages.isEmpty(); }
    public boolean openOnRightClick() { return openOnRightClick; }
    public boolean openOnShiftRightClick() { return openOnShiftRightClick; }
    public List<TankSpec> tanks() { return tanks; }
    public Map<String, String> scripts() { return scripts; }
    public String script(String event) { return scripts.get(event); }
    public Key placeBlock() { return placeBlock; }
    /** {@code place_block.fill_storage} — whether placing this item's block should copy its page
     *  storage into the new machine's container. A constant or a script condition (see {@link ScriptToggle}). */
    public ScriptToggle fillStorage() { return fillStorage; }
    /** {@code place_block.fill_flags} — whether placing should bridge {@link #bridgeFlags()}/
     *  {@link #bridgeStrFlags()} onto the new machine. */
    public ScriptToggle fillFlags() { return fillFlags; }
    /** {@code place_block.fill_typed} — whether placing should bridge this item's generic
     *  {@code TypedKey} store onto the new machine's (see {@code Item.with_typed}/{@code Machine.get_typed}). */
    public ScriptToggle fillTyped() { return fillTyped; }
    /** Tick spacing for {@code on_equipped_tick} — every N ticks, default 1 (every tick). */
    public int tickInterval() { return tickInterval; }
    /** Names of {@code Machine.get_flag}/{@code set_flag} (int) flags to carry over when this item
     *  becomes a machine ({@code place_block}) or a machine becomes this item ({@code Machine.to_item}).
     *  A machine's TypedKey flags have no enumeration mechanism, so only these explicitly declared
     *  names are bridged automatically — see {@code ItemStateData#getFlag}/{@code setFlag}. */
    public List<String> bridgeFlags() { return bridgeFlags; }
    /** Same as {@link #bridgeFlags()} but for {@code Machine.get_str_flag}/{@code set_str_flag}. */
    public List<String> bridgeStrFlags() { return bridgeStrFlags; }
    /** {@code "typed": [{"name":..., "type":...}, ...]} — generic {@code TypedKey} entries to carry
     *  over the same way {@link #bridgeFlags()} does for int/string flags, gated by {@link #fillTyped()}
     *  on the item→machine direction (machine→item via {@code Machine.to_item} is unconditional, like flags). */
    public List<TypedBridgeSpec> bridgeTyped() { return bridgeTyped; }
    /** Dynamic display-name template: plain MiniMessage, {@code ${expr}} inline scripts, or a bare
     *  {@code "script.pf:func"} call — re-evaluated by {@code Item.update()} (see {@code ItemType}). */
    public String nameTemplate() { return nameTemplate; }
    /** Dynamic lore template: MiniMessage lines, {@code ${expr}} inline scripts, or a
     *  {@code "script.pf:func"} line returning an Array of Str — re-evaluated by {@code Item.update()}. */
    public List<String> loreTemplate() { return loreTemplate; }

    public TankSpec tank(String name) {
        for (TankSpec t : tanks) if (t.name().equals(name)) return t;
        return null;
    }

    public static ItemDefinition byId(Key id) {
        return REGISTRY.get(id);
    }

    @Override
    public String toString() {
        return "ItemDefinition[" + id + "]";
    }

    /** One {@code "typed": [...]} entry — a generic {@code TypedKey} name/type pair bridged between
     *  this item's {@code "tkey_"+name} store and a machine's own (same key convention on both
     *  sides — see {@code Item.get_typed}/{@code with_typed} and {@code Machine.get_typed}/{@code
     *  set_typed}). {@code type} is a {@code TypedKeyBridge}-resolvable name ("int", "string", ...). */
    public record TypedBridgeSpec(String name, String type) {}

    /**
     * A {@code place_block} switch that's either a constant boolean or a
     * {@code "script.pf:func[:args]"} condition, evaluated fresh at fill/pickup time (see
     * {@code dev.arubik.craftengine.script.TextTemplate#evaluateCondition}) — lets e.g.
     * {@code fill_typed} depend on the item's own state instead of being all-or-nothing.
     */
    public record ScriptToggle(boolean constant, String scriptRef) {
        public static final ScriptToggle TRUE = new ScriptToggle(true, null);
        public static final ScriptToggle FALSE = new ScriptToggle(false, null);

        public static ScriptToggle of(boolean b) { return b ? TRUE : FALSE; }
        public static ScriptToggle script(String ref) { return new ScriptToggle(true, ref); }

        public boolean evaluate(dev.arubik.craftengine.script.ScriptContext ctx) {
            return scriptRef == null ? constant
                    : dev.arubik.craftengine.script.TextTemplate.evaluateCondition(scriptRef, ctx, constant);
        }
    }

    public record TankSpec(String name, String kind, int capacity) {
        public boolean isFluid() { return "fluid".equalsIgnoreCase(kind); }
        public boolean isGas() { return "gas".equalsIgnoreCase(kind); }
        public boolean isEnergy() { return "energy".equalsIgnoreCase(kind); }
    }

    public static Map<String, String> parseScripts(dev.arubik.craftengine.data.JsonView view) {
        LinkedHashMap<String, String> out = new LinkedHashMap<>();
        if (!view.has("scripts")) return out;
        dev.arubik.craftengine.data.JsonView scripts = view.object("scripts");
        for (String field : scripts.fields()) {
            out.put(field, scripts.string(field));
        }
        return out;
    }
}
