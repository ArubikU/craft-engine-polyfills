package dev.arubik.craftengine.machine.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.util.Utils;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.block.entity.BlockEntity;
import net.momirealms.craftengine.core.block.entity.BlockEntityController;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;
import net.momirealms.craftengine.core.util.Key;

/**
 * EntityBlock behavior for the RPM-powered Crusher ({@code polyfills:crusher}). Accepts the same
 * {@code upgrades:} config form as the advanced vapor motor (item id -> list of attribute mods).
 */
public class CrusherBehavior extends MachineBlockBehavior {

    public static final Key POLYFILL_CRUSHER = Key.of("polyfills:crusher");
    public static final Factory FACTORY = new Factory();

    private final Map<Key, List<Mod>> upgradeDefs;

    public CrusherBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> h,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> v,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig, Map<Key, List<Mod>> upgradeDefs) {
        super(block, connectableFaces, h, v, ioConfig);
        this.upgradeDefs = upgradeDefs;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new CrusherBlockEntity(blockEntity, upgradeDefs);
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

            return new CrusherBehavior(block, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.getIOConfiguration(null, null), upgrades);
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
