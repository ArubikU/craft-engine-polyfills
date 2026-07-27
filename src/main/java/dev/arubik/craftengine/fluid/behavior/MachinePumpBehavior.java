package dev.arubik.craftengine.fluid.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.MachinePumpBlockEntity;
import dev.arubik.craftengine.machine.menu.MachineMenuConfig;
import dev.arubik.craftengine.machine.menu.bar.MachineBar;
import dev.arubik.craftengine.machine.menu.bar.MachineBars;
import dev.arubik.craftengine.util.Utils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * MachinePumpBehavior: the FULL-MACHINE pump behavior (the {@code cml:iron_pump} block). Parses the
 * same config form as the Crusher/Vapor-Furnace ({@code upgrades:}, {@code bars:}, menu slots/buttons)
 * plus pump-specific tuning knobs ({@code capacity}, {@code extract_per_tick}, {@code push_per_tick},
 * {@code pressure}) and constructs a {@link MachinePumpBlockEntity}.
 *
 * <p>Connectable faces are forced to UP/DOWN so a pipe only links on the pump's IN/OUT faces.</p>
 */
public class MachinePumpBehavior extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of("polyfills:machine_pump");
    public static final Factory FACTORY = new Factory();

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int capacity;
    private final int extractPerTick;
    private final int pushPerTick;
    private final int pressure;
    private final int extractTickRate;

    public MachinePumpBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            Map<Key, List<Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig,
            int capacity, int extractPerTick, int pushPerTick, int pressure, int extractTickRate) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.upgradeDefs = upgradeDefs;
        this.bars = bars;
        this.menuConfig = menuConfig;
        this.capacity = capacity;
        this.extractPerTick = extractPerTick;
        this.pushPerTick = pushPerTick;
        this.pressure = pressure;
        this.extractTickRate = extractTickRate;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new MachinePumpBlockEntity(blockEntity, upgradeDefs, bars, menuConfig,
                capacity, extractPerTick, pushPerTick, pressure, extractTickRate);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);

            Map<Key, List<Mod>> upgrades = new HashMap<>();
            Object uObj = arguments.get("upgrades");
            if (uObj instanceof Map<?, ?> uMap) {
                for (Map.Entry<?, ?> e : uMap.entrySet()) {
                    List<Mod> mods = parseMods(e.getValue());
                    if (!mods.isEmpty())
                        upgrades.put(key(String.valueOf(e.getKey())), mods);
                }
            }

            List<MachineBar> bars = MachineBars.parse(arguments.get("bars"));
            MachineMenuConfig menuConfig = MachineMenuConfig.parse(arguments::get);

            // Pump tuning comes from pump_types/*.json, selected by `pump_type:` or by this
            // block's id; explicit YAML keys still win, so an existing pack keeps its values.
            dev.arubik.craftengine.pipe.PumpType pump = null;
            Object configuredPump = arguments.get("pump_type");
            if (configuredPump != null)
                pump = dev.arubik.craftengine.pipe.PumpType.byName(String.valueOf(configuredPump));
            if (pump == null)
                pump = dev.arubik.craftengine.pipe.PumpType.byBlockId(block.id());
            if (pump == null)
                pump = dev.arubik.craftengine.pipe.PumpType.MACHINE_PUMP;

            int capacity = intOr(arguments.get("capacity"), pump.capacity());
            int extractPerTick = intOr(arguments.get("extract_per_tick"), pump.extractPerTick());
            int pushPerTick = intOr(arguments.get("push_per_tick"), pump.pushPerTick());
            int pressure = intOr(arguments.get("pressure"), pump.pressure());
            // Ticks between pump operations (extract+push). Overclock shortens this interval.
            int extractTickRate = intOr(arguments.get("extract_tick_rate"), pump.extractTickRate());

            // A pump only connects on its UP (out) and DOWN (in) local faces.
            java.util.List<net.minecraft.core.Direction> faces = java.util.List.of(
                    net.minecraft.core.Direction.UP, net.minecraft.core.Direction.DOWN);

            // Restrictive DEFAULT IO (FLUID in = local DOWN, out = local UP) — NOT the base Open() config.
            // Open() made the OUTPUT face report acceptsInput=true whenever the block entity wasn't loaded
            // at the moment a pipe queried it, so a pipe would shove fluid back IN through the pump's
            // output face (the pump<->pipe recirculation). The entity sets the same config when loaded.
            dev.arubik.craftengine.multiblock.IOConfiguration.RelativeIO defIO =
                    new dev.arubik.craftengine.multiblock.IOConfiguration.RelativeIO();
            defIO.addInput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    dev.arubik.craftengine.multiblock.RelativeDirection.DOWN);
            defIO.addOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.FLUID,
                    dev.arubik.craftengine.multiblock.RelativeDirection.UP);

            return new MachinePumpBehavior(block, faces,
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    defIO, upgrades, bars, menuConfig,
                    capacity, extractPerTick, pushPerTick, pressure, extractTickRate);
        }

        private static int intOr(Object o, int def) {
            return (o instanceof Number n) ? n.intValue() : def;
        }

        private static List<Mod> parseMods(Object value) {
            List<Mod> out = new ArrayList<>();
            if (value instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Map<?, ?> m) {
                        Object attr = m.get("attribute");
                        if (attr == null)
                            continue;
                        Object opObj = m.get("operation");
                        MachineAttributes.Operation op = MachineAttributes.parseOperation(
                                opObj == null ? "add" : String.valueOf(opObj));
                        Object vObj = m.get("value");
                        double v = Utils.getAsDouble(vObj == null ? 0 : vObj, "value");
                        out.add(new Mod(key(String.valueOf(attr)), op, v));
                    }
                }
            }
            return out;
        }

        private static Key key(String s) {
            int i = s.indexOf(':');
            return (i < 0) ? Key.of("minecraft", s) : Key.of(s.substring(0, i), s.substring(i + 1));
        }
    }
}
