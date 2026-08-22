package dev.arubik.craftengine.machine.block.behavior;

import java.util.List;

import dev.arubik.craftengine.machine.MachineDefinition;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.DataMachineBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * The one block behavior behind every data-defined machine.
 *
 * <p>
 * A pack points a block at a {@link MachineDefinition} and gets a working
 * machine — no Java class:
 *
 * <pre>{@code
 * behavior:
 *   type: polyfills:data_machine
 *   machine: polyfills:grinder      # machines/grinder.json
 *   menu_size: 54                   # the existing menu/bar keys still apply
 *   bars: { ... }
 *   buttons: [ ... ]
 * }</pre>
 *
 * <p>
 * The split is deliberate: the JSON definition says what the machine <em>is</em>
 * (recipe namespace, tanks, slot roles, IO faces), while the block config keeps
 * saying what it <em>looks like</em> through the already-config-driven
 * {@link MachineMenuConfig} and {@link MachineBars}. Neither had to grow a second
 * way to express the same thing.
 */
public class DataMachineBehavior extends MachineBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:data_machine");
    public static final Factory FACTORY = new Factory();

    private final MachineDefinition definition;
    private final MachineMenuConfig menuConfig;
    private final List<MachineBar> bars;
    /** item id -> attribute modifiers, from the block config's {@code upgrades:} section. */
    private final java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs;

    public DataMachineBehavior(BlockDefinition block, MachineDefinition definition,
            MachineMenuConfig menuConfig, List<MachineBar> bars,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontal,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> vertical,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> upgradeDefs) {
        super(block, connectableFaces, horizontal, vertical, ioConfig);
        this.definition = definition;
        this.menuConfig = menuConfig;
        this.bars = bars;
        this.upgradeDefs = upgradeDefs;
    }

    public MachineDefinition definition() {
        return definition;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        // `definition` is captured ONCE, when CraftEngine parses this block's config (i.e. at
        // Factory.create() below) — it is never touched by `/cep reload machines`/`render`, which
        // only replace the entries in MachineDefinition.REGISTRY and re-point ALREADY-PLACED
        // DataMachineBlockEntity instances (see DataMachineBlockEntity#refreshDefinitions). A block
        // placed for the first time after such a reload (but before a full /craftengine reload
        // reconstructs this behavior) would otherwise be built from that stale reference — same
        // fresh-lookup-with-fallback pattern DataMachineBlockEntity#ensureRenderer already uses.
        MachineDefinition fresh = MachineDefinition.REGISTRY.get(definition.id());
        MachineDefinition eff = fresh != null ? fresh : definition;
        DataMachineBlockEntity machine = new DataMachineBlockEntity(blockEntity, eff);
        machine.setMenuConfig(menuConfig);
        machine.setBars(bars);
        // Prefer definitions from the machine JSON; fall back to block-behavior YAML
        if (!eff.upgradeDefs().isEmpty()) {
            machine.setUpgradeDefs(eff.upgradeDefs());
        } else {
            machine.setUpgradeDefs(upgradeDefs);
        }
        return machine;
    }

    /**
     * JSON-native variant of {@link #parseUpgrades}: parses a {@code "definitions"} block directly
     * from a {@link com.google.gson.JsonObject}. Keys are CE item IDs; values are arrays of
     * modifier objects with {@code attribute}, {@code operation}, and {@code value} fields.
     *
     * <p>Used by {@link dev.arubik.craftengine.machine.MachineDefinitionLoader} to embed upgrade
     * definitions in the machine JSON rather than in block-behavior YAML.
     */
    public static java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>>
            parseUpgradeDefsFromJson(com.google.gson.JsonObject obj) {
        java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> out =
                new java.util.HashMap<>();
        for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : obj.entrySet()) {
            if (!entry.getValue().isJsonArray()) continue;
            List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> mods =
                    parseModsFromJsonArray(entry.getValue().getAsJsonArray());
            if (!mods.isEmpty())
                out.put(parseKey(entry.getKey()), mods);
        }
        return out;
    }

    private static List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>
            parseModsFromJsonArray(com.google.gson.JsonArray arr) {
        List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> out = new java.util.ArrayList<>();
        for (com.google.gson.JsonElement el : arr) {
            if (!el.isJsonObject()) continue;
            com.google.gson.JsonObject m = el.getAsJsonObject();
            if (!m.has("attribute")) continue;
            String attribute = m.get("attribute").getAsString();
            String operation = m.has("operation") ? m.get("operation").getAsString() : "add";
            double value = m.has("value") ? m.get("value").getAsDouble() : 0.0;
            var op = dev.arubik.craftengine.machine.attribute.MachineAttributes.parseOperation(operation);
            out.add(new dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod(
                    parseKey(attribute), op, value));
        }
        return out;
    }

    /** Parses the block config's {@code upgrades:} section; shared with the multiblock variant. */
    public static java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>>
            parseUpgrades(Object raw) {
        java.util.Map<Key, List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod>> out =
                new java.util.HashMap<>();
        if (raw instanceof java.util.Map<?, ?> map)
            for (java.util.Map.Entry<?, ?> e : map.entrySet()) {
                List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> mods = parseMods(e.getValue());
                if (!mods.isEmpty())
                    out.put(parseKey(String.valueOf(e.getKey())), mods);
            }
        return out;
    }

    private static List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> parseMods(Object value) {
        List<dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod> out = new java.util.ArrayList<>();
        if (!(value instanceof List<?> list))
            return out;
        for (Object o : list) {
            if (!(o instanceof java.util.Map<?, ?> m))
                continue;
            Object attribute = m.get("attribute");
            if (attribute == null)
                continue;
            Object operation = m.get("operation");
            var op = dev.arubik.craftengine.machine.attribute.MachineAttributes.parseOperation(
                    operation == null ? "add" : String.valueOf(operation));
            Object amount = m.get("value");
            double v = dev.arubik.craftengine.util.Utils.getAsDouble(amount == null ? 0 : amount, "value");
            out.add(new dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod(
                    parseKey(String.valueOf(attribute)), op, v));
        }
        return out;
    }

    private static Key parseKey(String spec) {
        int i = spec.indexOf(':');
        return i < 0 ? Key.of("polyfill", spec) : Key.of(spec.substring(0, i), spec.substring(i + 1));
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);

            Object configured = arguments.get("machine");
            MachineDefinition definition = MachineDefinition.byName(String.valueOf(configured));
            if (definition == null)
                throw new IllegalArgumentException("Block " + block.id()
                        + " uses polyfills:data_machine but 'machine: " + configured
                        + "' matches no entry in machines/*.json. Known: "
                        + MachineDefinition.REGISTRY.keys());

            // The attribute upgrade map keeps living in the block config, same shape the
            // Java machines already use, so migrating one needs no rewrite of its upgrades.
            var upgradeDefs = parseUpgrades(arguments.get("upgrades"));

            return new DataMachineBehavior(block, definition,
                    MachineMenuConfig.parse(arguments::get),
                    MachineBars.parse(arguments.get("bars")),
                    base.getConnectableFaces(), base.horizontalDirectionProperty,
                    base.verticalDirectionProperty, base.defaultIOConfig, upgradeDefs);
        }
    }
}
