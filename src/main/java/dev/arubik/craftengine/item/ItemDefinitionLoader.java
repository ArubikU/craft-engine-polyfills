package dev.arubik.craftengine.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.data.DataFiles;
import dev.arubik.craftengine.data.JsonView;
import dev.arubik.craftengine.data.Registries;
import net.momirealms.craftengine.core.util.Key;

/**
 * Loads {@code items/*.json} into {@link ItemDefinition#REGISTRY}, exactly mirroring
 * {@code MachineDefinitionLoader}'s load-order contract: {@code load()} is called eagerly in
 * {@code CraftEnginePolyfills#onEnable} (before CraftEngine builds its item packs, since
 * {@code DataItemBehavior.Factory} resolves its definition at construction time), and
 * {@code bootstrap()} registers the same work again for later {@code /craftengine reload}s.
 */
public final class ItemDefinitionLoader {
    private ItemDefinitionLoader() {}

    public static void bootstrap() {
        Registries.addLoader("items", 100, ItemDefinitionLoader::load);
    }

    public static void load() {
        int count = DataFiles.loadDirectory("items", ItemDefinitionLoader::apply);
        if (count > 0) {
            CraftEnginePolyfills.instance().getLogger()
                    .info("Loaded " + ItemDefinition.REGISTRY.size() + " item definitions.");
        }
    }

    private static void apply(JsonView view, String fileName) {
        Key id = view.has("id") ? view.key("id", "polyfills")
                : Key.of("polyfills", stripExtension(fileName).replace('/', '_'));
        ItemDefinition.REGISTRY.register(id, parse(view, id));
    }

    public static ItemDefinition parse(JsonView view, Key id) {
        // "pages": [ ... ] — the EXACT same schema machines use (buttons, bars, paging, ghost
        // slots, free storage slots + a filter) — see MachineDefinitionLoader#parsePage, shared
        // verbatim rather than re-implemented, so one JSON dialect covers machines AND items.
        List<dev.arubik.craftengine.machine.MachineDefinition.PageDef> pages = new ArrayList<>();
        if (view.raw().has("pages") && view.raw().get("pages").isJsonArray()) {
            for (com.google.gson.JsonElement pageEl : view.raw().get("pages").getAsJsonArray()) {
                if (!pageEl.isJsonObject()) continue;
                pages.add(dev.arubik.craftengine.machine.MachineDefinitionLoader.parsePage(pageEl.getAsJsonObject(), view));
            }
        }
        boolean openOnRightClick = view.bool("open_on_right_click", !pages.isEmpty());
        boolean openOnShiftRightClick = view.bool("open_on_shift_right_click", false);

        List<ItemDefinition.TankSpec> tanks = new ArrayList<>();
        for (JsonView t : view.objectList("tanks")) {
            tanks.add(new ItemDefinition.TankSpec(t.string("name", "tank" + tanks.size()),
                    t.string("kind", "fluid"), t.rangedInt("capacity", 1000, 1, Integer.MAX_VALUE)));
        }

        Map<String, String> scripts = ItemDefinition.parseScripts(view);

        // "place_block": { "block": "polyfills:...", "fill_storage": bool|"script.pf:func[:args]",
        // "fill_flags": ..., "fill_typed": ... } — each fill_* switch is either a constant or a
        // script condition evaluated fresh at fill time (see ItemDefinition.ScriptToggle), so e.g.
        // a locked backpack can refuse to spill its storage into a freshly placed block. There is
        // deliberately no "pickup" switch here — whether breaking the placed block hands the item
        // back is entirely the block's own on_break script's call (e.g. backpack_storage.pf calling
        // Machine.to_item), not a policy this generic item behavior should gate on its behalf.
        Key placeBlock = null;
        ItemDefinition.ScriptToggle fillStorage = ItemDefinition.ScriptToggle.TRUE;
        ItemDefinition.ScriptToggle fillFlags = ItemDefinition.ScriptToggle.TRUE;
        ItemDefinition.ScriptToggle fillTyped = ItemDefinition.ScriptToggle.TRUE;
        if (view.has("place_block")) {
            JsonView pb = view.object("place_block");
            placeBlock = pb.key("block", "polyfills");
            fillStorage = readToggle(pb, "fill_storage", true);
            fillFlags = readToggle(pb, "fill_flags", true);
            fillTyped = readToggle(pb, "fill_typed", true);
        }
        int tickInterval = view.rangedInt("tick_interval", 1, 1, 6000);

        // "flags"/"str_flags" — Machine.get_flag/set_flag (and the string variant) names to carry
        // over automatically when this item becomes a machine or vice versa. See
        // ItemDefinition#bridgeFlags for why only explicitly-declared names can be bridged.
        List<String> bridgeFlags = view.stringList("flags");
        List<String> bridgeStrFlags = view.stringList("str_flags");

        // "typed": [{"name": "...", "type": "int"|"string"|...}] — generic TypedKey entries to
        // bridge, same "no enumeration mechanism" reasoning as flags/str_flags above.
        List<ItemDefinition.TypedBridgeSpec> bridgeTyped = new ArrayList<>();
        for (JsonView t : view.objectList("typed")) {
            bridgeTyped.add(new ItemDefinition.TypedBridgeSpec(t.string("name"), t.string("type", "string")));
        }

        // "name"/"lore" — dynamic display templates (plain MiniMessage, "${expr}" inline scripts,
        // or a bare "script.pf:func" call) re-evaluated on demand via Item.update(); see
        // ItemDefinition#nameTemplate/#loreTemplate and dev.arubik.craftengine.script.TextTemplate.
        String nameTemplate = view.has("name") ? view.string("name") : null;
        List<String> loreTemplate = view.stringList("lore");

        return new ItemDefinition(id, view.string("title", id.value()), pages,
                openOnRightClick, openOnShiftRightClick, tanks, scripts,
                placeBlock, fillStorage, fillFlags, fillTyped, tickInterval,
                bridgeFlags, bridgeStrFlags, bridgeTyped, nameTemplate, loreTemplate);
    }

    /** Reads a {@code place_block} switch that's either a JSON boolean or a
     *  {@code "script.pf:func[:args]"} string condition — see {@link ItemDefinition.ScriptToggle}. */
    private static ItemDefinition.ScriptToggle readToggle(JsonView view, String field, boolean fallback) {
        if (!view.has(field)) return ItemDefinition.ScriptToggle.of(fallback);
        com.google.gson.JsonElement el = view.raw().get(field);
        if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isBoolean()) {
            return ItemDefinition.ScriptToggle.of(el.getAsBoolean());
        }
        return ItemDefinition.ScriptToggle.script(el.getAsString());
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
