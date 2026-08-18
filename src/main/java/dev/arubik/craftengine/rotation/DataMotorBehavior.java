/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.momirealms.craftengine.core.block.BlockDefinition
 *  net.momirealms.craftengine.core.block.behavior.BlockBehavior
 *  net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
 *  net.momirealms.craftengine.core.block.entity.BlockEntity
 *  net.momirealms.craftengine.core.block.entity.BlockEntityController
 *  net.momirealms.craftengine.core.block.property.EnumProperty
 *  net.momirealms.craftengine.core.plugin.config.ConfigSection
 *  net.momirealms.craftengine.core.util.Direction
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.rotation;

import dev.arubik.craftengine.gas.GasType;
import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.multiblock.IOConfiguration;
import dev.arubik.craftengine.rotation.DataMotorBlockEntity;
import dev.arubik.craftengine.rotation.MotorDefinition;
import dev.arubik.craftengine.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Direction;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.block.property.EnumProperty;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

public class DataMotorBehavior
extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of((String)"polyfills:data_motor");
    public static final Factory FACTORY = new Factory();
    private final MotorDefinition motor;
    private final int vaporCapacity;
    private final Map<GasType, DataMotorBlockEntity.GasSpec> gases;
    private final Map<Key, List<MachineAttributes.Mod>> upgradeDefs;

    public DataMotorBehavior(BlockDefinition block, List<Direction> connectableFaces, EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty, EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty, IOConfiguration ioConfig, MotorDefinition motor, int vaporCapacity, Map<GasType, DataMotorBlockEntity.GasSpec> gases, Map<Key, List<MachineAttributes.Mod>> upgradeDefs) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.motor = motor;
        this.vaporCapacity = vaporCapacity;
        this.gases = gases;
        this.upgradeDefs = upgradeDefs;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new DataMotorBlockEntity(blockEntity, this.motor, this.vaporCapacity, this.gases, this.upgradeDefs);
    }

    public static class Factory
    implements BlockBehaviorFactory<BlockBehavior> {
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            MachineBlockBehavior base = (MachineBlockBehavior)MachineBlockBehavior.FACTORY.create(block, arguments);
            Object configured = arguments.get("motor");
            MotorDefinition motor = configured != null ? MotorDefinition.byName(String.valueOf(configured)) : null;
            HashMap<GasType, DataMotorBlockEntity.GasSpec> gases = new HashMap<GasType, DataMotorBlockEntity.GasSpec>();
            if (motor != null) {
                for (Map.Entry<Key, MotorDefinition.FuelOutput> e : motor.fuelsOfKind(MotorDefinition.FuelKind.GAS).entrySet()) {
                    GasType gt = GasType.byName(e.getKey().toString());
                    if (gt == null) continue;
                    gases.put(gt, new DataMotorBlockEntity.GasSpec(e.getValue().rpm(), e.getValue().su(), e.getValue().perTick()));
                }
            }
            int capacity = Utils.getAsInt(arguments.getOrDefault("vapor-capacity", Factory.defaultVaporCapacity(motor)), "vapor-capacity");
            Object gObj = arguments.get("gases");
            if (gObj instanceof Map) {
                Map gMap = (Map)gObj;
                for (Map.Entry e : gMap.entrySet()) {
                    Object v;
                    GasType gt = Factory.parseGas(String.valueOf(e.getKey()));
                    if (gt == null || !((v = e.getValue()) instanceof Map)) continue;
                    Map spec = (Map)v;
                    float rpm = Utils.getAsFloat(Factory.get(spec, "rpm", 0), "rpm");
                    float su = Utils.getAsFloat(Factory.get(spec, "su", 0), "su");
                    int gpt = Utils.getAsInt(Factory.get(spec, "gas-per-tick", 20), "gas-per-tick");
                    if (!(rpm > 0.0f) || !(su > 0.0f)) continue;
                    gases.put(gt, new DataMotorBlockEntity.GasSpec(rpm, su, gpt));
                }
            }
            HashMap<Key, List<MachineAttributes.Mod>> upgrades = new HashMap<Key, List<MachineAttributes.Mod>>();
            Object uObj = arguments.get("upgrades");
            if (uObj instanceof Map) {
                Map uMap = (Map)uObj;
                for (Map.Entry e : uMap.entrySet()) {
                    List<MachineAttributes.Mod> mods = Factory.parseMods(e.getValue());
                    if (mods.isEmpty()) continue;
                    upgrades.put(Factory.key(String.valueOf(e.getKey())), mods);
                }
            }
            return new DataMotorBehavior(block, base.getConnectableFaces(), (EnumProperty<net.momirealms.craftengine.core.util.Direction>)base.horizontalDirectionProperty, (EnumProperty<net.momirealms.craftengine.core.util.Direction>)base.verticalDirectionProperty, base.getIOConfiguration(null, null), motor, capacity, gases, upgrades);
        }

        private static List<MachineAttributes.Mod> parseMods(Object value) {
            ArrayList<MachineAttributes.Mod> out = new ArrayList<MachineAttributes.Mod>();
            if (value instanceof List) {
                List list = (List)value;
                for (Object o : list) {
                    Map m;
                    Object attr;
                    if (!(o instanceof Map) || (attr = Factory.get(m = (Map)o, "attribute", null)) == null) continue;
                    MachineAttributes.Operation op = MachineAttributes.parseOperation(String.valueOf(Factory.get(m, "operation", "add")));
                    double v = Utils.getAsDouble(Factory.get(m, "value", 0), "value");
                    out.add(new MachineAttributes.Mod(Factory.key(String.valueOf(attr)), op, v));
                }
            }
            return out;
        }

        private static Object get(Map<?, ?> m, String k, Object def) {
            Object v = m.get(k);
            return v != null ? v : def;
        }

        private static int defaultVaporCapacity(MotorDefinition motor) {
            if (motor == null || motor.machine() == null || motor.machine().gasTanks().isEmpty()) {
                return 10000;
            }
            return motor.machine().gasTanks().get(0).capacity();
        }

        private static GasType parseGas(String s) {
            try {
                return GasType.valueOf(s.trim().toUpperCase());
            }
            catch (IllegalArgumentException ex) {
                return null;
            }
        }

        private static Key key(String s) {
            int i = s.indexOf(58);
            return i < 0 ? Key.of((String)"minecraft", (String)s) : Key.of((String)s.substring(0, i), (String)s.substring(i + 1));
        }
    }
}

