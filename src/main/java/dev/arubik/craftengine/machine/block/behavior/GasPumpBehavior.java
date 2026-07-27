package dev.arubik.craftengine.machine.block.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.GasPumpBlockEntity;
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
 * GasPumpBehavior: the FULL-MACHINE gas pump (the {@code cml:gas_pump} block). Same config form as the
 * iron pump / crusher ({@code upgrades:}, {@code bars:}, menu slots/buttons) plus gas-pump knobs
 * ({@code capacity}, {@code mb_per_point}, {@code pressure}, {@code extract_tick_rate}). Builds a
 * {@link GasPumpBlockEntity} that draws nitrogen from the cal vein below into its buffer and pushes it
 * out the local UP face.
 */
public class GasPumpBehavior extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of("polyfills:gas_pump");
    public static final Factory FACTORY = new Factory();

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int capacity;
    private final int mbPerPoint;
    private final int pressure;
    private final int extractTickRate;

    public GasPumpBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            Map<Key, List<Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig,
            int capacity, int mbPerPoint, int pressure, int extractTickRate) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.upgradeDefs = upgradeDefs;
        this.bars = bars;
        this.menuConfig = menuConfig;
        this.capacity = capacity;
        this.mbPerPoint = mbPerPoint;
        this.pressure = pressure;
        this.extractTickRate = extractTickRate;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new GasPumpBlockEntity(blockEntity, upgradeDefs, bars, menuConfig,
                capacity, mbPerPoint, pressure, extractTickRate);
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

            // Same as the fluid pump: defaults come from pump_types/*.json, explicit YAML wins.
            dev.arubik.craftengine.pipe.PumpType pump = null;
            Object configuredPump = arguments.get("pump_type");
            if (configuredPump != null)
                pump = dev.arubik.craftengine.pipe.PumpType.byName(String.valueOf(configuredPump));
            if (pump == null)
                pump = dev.arubik.craftengine.pipe.PumpType.byBlockId(block.id());
            if (pump == null)
                pump = dev.arubik.craftengine.pipe.PumpType.GAS_PUMP;

            int capacity = intOr(arguments.get("capacity"), pump.capacity());
            int mbPerPoint = intOr(arguments.get("mb_per_point"), pump.mbPerPoint());
            int pressure = intOr(arguments.get("pressure"), pump.pressure());
            int extractTickRate = intOr(arguments.get("extract_tick_rate"), pump.extractTickRate());

            // Pipe links only on the pump's IN/OUT local faces (like the iron pump).
            java.util.List<net.minecraft.core.Direction> faces = java.util.List.of(
                    net.minecraft.core.Direction.UP, net.minecraft.core.Direction.DOWN);

            // Restrictive DEFAULT IO (gas OUT on local UP) so a pipe connecting BEFORE the block entity
            // loads still resolves to UP only — NOT the base Open() config, which let pipes attach on
            // every face when the entity wasn't loaded yet.
            dev.arubik.craftengine.multiblock.IOConfiguration.RelativeIO defIO =
                    new dev.arubik.craftengine.multiblock.IOConfiguration.RelativeIO();
            defIO.addOutput(dev.arubik.craftengine.multiblock.IOConfiguration.IOType.GAS,
                    dev.arubik.craftengine.multiblock.RelativeDirection.UP);

            return new GasPumpBehavior(block, faces,
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    defIO, upgrades, bars, menuConfig,
                    capacity, mbPerPoint, pressure, extractTickRate);
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
