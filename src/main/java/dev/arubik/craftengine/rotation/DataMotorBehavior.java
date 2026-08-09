package dev.arubik.craftengine.rotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.rotation.DataMotorBlockEntity.GasSpec;
import dev.arubik.craftengine.util.Utils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * EntityBlock behavior for the advanced (upgradeable, multi-gas) vapor motor.
 * Config (all under {@code behavior:}):
 * <pre>
 *   vapor-capacity: 10000
 *   gases:
 *     steam:       { rpm: 32, su: 64, gas-per-tick: 20 }
 *     heavy_steam: { rpm: 48, su: 96, gas-per-tick: 28 }   # 0/absent rpm|su = unsupported
 *   upgrades:                       # item id -> list of attribute modifiers (Mojang-style)
 *     minecraft:redstone:
 *       - { attribute: polyfill:overclock_limit, operation: add, value: 0.5 }
 *     demo:scrapped_upgrade:        # buff one attribute, debuff another
 *       - { attribute: polyfill:generation,     operation: add, value: 0.6 }
 *       - { attribute: polyfill:gas_efficiency, operation: add, value: -0.3 }
 * </pre>
 * operation = add | multiply_base | multiply_total.
 */
public class DataMotorBehavior extends MachineBlockBehavior {

    public static final Key FACTORY_KEY = Key.of("polyfills:data_motor");
    public static final Factory FACTORY = new Factory();

    private final MotorDefinition motor;
    private final int vaporCapacity;
    private final Map<GasType, GasSpec> gases;
    private final Map<Key, List<Mod>> upgradeDefs;

    public DataMotorBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            MotorDefinition motor, int vaporCapacity, Map<GasType, GasSpec> gases, Map<Key, List<Mod>> upgradeDefs) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.motor = motor;
        this.vaporCapacity = vaporCapacity;
        this.gases = gases;
        this.upgradeDefs = upgradeDefs;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new DataMotorBlockEntity(blockEntity, motor, vaporCapacity, gases, upgradeDefs);
    }

    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior) MachineBlockBehavior.FACTORY.create(block, arguments);

            // A motors/*.json definition supplies the fuel table, tank and grid; the
            // block config still wins where it names something, so existing packs keep
            // their values.
            // The block config names its motor, the same way a machine block names its
            // machine — a motor does not claim a block.
            Object configured = arguments.get("motor");
            MotorDefinition motor = configured != null
                    ? MotorDefinition.byName(String.valueOf(configured))
                    : null;

            Map<GasType, GasSpec> gases = new HashMap<>();
            if (motor != null)
                for (Map.Entry<Key, MotorDefinition.FuelOutput> e
                        : motor.fuelsOfKind(MotorDefinition.FuelKind.GAS).entrySet()) {
                    GasType gt = GasType.byName(e.getKey().toString());
                    if (gt != null)
                        gases.put(gt, new GasSpec(e.getValue().rpm(), e.getValue().su(),
                                e.getValue().perTick()));
                }
            int capacity = Utils.getAsInt(
                    arguments.getOrDefault("vapor-capacity", defaultVaporCapacity(motor)),
                    "vapor-capacity");

            Object gObj = arguments.get("gases");
            if (gObj instanceof Map<?, ?> gMap) {
                for (Map.Entry<?, ?> e : gMap.entrySet()) {
                    GasType gt = parseGas(String.valueOf(e.getKey()));
                    if (gt == null || !(e.getValue() instanceof Map<?, ?> spec))
                        continue;
                    float rpm = Utils.getAsFloat(get(spec, "rpm", 0), "rpm");
                    float su = Utils.getAsFloat(get(spec, "su", 0), "su");
                    int gpt = Utils.getAsInt(get(spec, "gas-per-tick", 20), "gas-per-tick");
                    if (rpm > 0 && su > 0)
                        gases.put(gt, new GasSpec(rpm, su, gpt));
                }
            }

            Map<Key, List<Mod>> upgrades = new HashMap<>();
            Object uObj = arguments.get("upgrades");
            if (uObj instanceof Map<?, ?> uMap) {
                for (Map.Entry<?, ?> e : uMap.entrySet()) {
                    List<Mod> mods = parseMods(e.getValue());
                    if (!mods.isEmpty())
                        upgrades.put(key(String.valueOf(e.getKey())), mods);
                }
            }

            return new DataMotorBehavior(block, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.getIOConfiguration(null, null), motor, capacity, gases, upgrades);
        }

        /** A modifier list is a YAML list of {attribute, operation, value} maps. */
        private static List<Mod> parseMods(Object value) {
            List<Mod> out = new ArrayList<>();
            if (value instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Map<?, ?> m) {
                        Object attr = get(m, "attribute", null);
                        if (attr == null)
                            continue;
                        MachineAttributes.Operation op = MachineAttributes.parseOperation(
                                String.valueOf(get(m, "operation", "add")));
                        double v = Utils.getAsDouble(get(m, "value", 0), "value");
                        out.add(new Mod(key(String.valueOf(attr)), op, v));
                    }
                }
            }
            return out;
        }

        private static Object get(Map<?, ?> m, String k, Object def) {
            Object v = m.get(k);
            return v != null ? v : def;
        }

        private static int defaultVaporCapacity(MotorDefinition motor) {
            if (motor == null || motor.machine() == null || motor.machine().gasTanks().isEmpty())
                return 10000;
            return motor.machine().gasTanks().get(0).capacity();
        }

        private static GasType parseGas(String s) {
            try {
                return GasType.valueOf(s.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        private static Key key(String s) {
            int i = s.indexOf(':');
            return (i < 0) ? Key.of("minecraft", s) : Key.of(s.substring(0, i), s.substring(i + 1));
        }
    }
}
