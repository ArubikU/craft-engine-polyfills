package dev.arubik.craftengine.block.behavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.arubik.craftengine.machine.attribute.MachineAttributes;
import dev.arubik.craftengine.machine.attribute.MachineAttributes.Mod;
import dev.arubik.craftengine.machine.block.MachineBlockBehavior;
import dev.arubik.craftengine.machine.block.entity.FanMachineBlockEntity;
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
 * FULL-MACHINE Copper Fan behavior (the {@code cml:copper_fan} block, factory key
 * {@code polyfills:fan_block}). Parses the same machine config form as the pump/crusher
 * ({@code upgrades:}, {@code bars:}, menu slots/buttons) plus fan-specific tuning knobs, and constructs
 * a {@link FanMachineBlockEntity}. Right-click opens its menu (inherited from {@link MachineBlockBehavior}).
 *
 * <p>IO orientation tracks the block's 6-direction {@code facing}; gas is pulled from a carrier on the
 * BACK face into the internal buffer.</p>
 */
public class FanMachineBehavior extends MachineBlockBehavior {
    public static final Key FACTORY_KEY = Key.of("polyfills:fan_block");
    public static final Factory FACTORY = new Factory();

    private final Map<Key, List<Mod>> upgradeDefs;
    private final List<MachineBar> bars;
    private final MachineMenuConfig menuConfig;
    private final int gasCapacity;
    private final int gasPerTick;
    private final java.util.Map<dev.arubik.craftengine.gas.GasType, dev.arubik.craftengine.machine.block.entity.FanGasConfig> gasConfigs;
    private final java.util.Map<dev.arubik.craftengine.machine.recipe.FanProcess, org.bukkit.Particle> processParticles;
    private final java.util.Set<Key> passableBlocks;
    private final int baseProcessingTicks;
    private final int tickDelay;

    public FanMachineBehavior(BlockDefinition block,
            java.util.List<net.minecraft.core.Direction> connectableFaces,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> horizontalDirectionProperty,
            net.momirealms.craftengine.core.block.property.EnumProperty<net.momirealms.craftengine.core.util.Direction> verticalDirectionProperty,
            dev.arubik.craftengine.multiblock.IOConfiguration ioConfig,
            Map<Key, List<Mod>> upgradeDefs, List<MachineBar> bars, MachineMenuConfig menuConfig,
            int gasCapacity, int gasPerTick,
            java.util.Map<dev.arubik.craftengine.gas.GasType, dev.arubik.craftengine.machine.block.entity.FanGasConfig> gasConfigs,
            java.util.Map<dev.arubik.craftengine.machine.recipe.FanProcess, org.bukkit.Particle> processParticles,
            java.util.Set<Key> passableBlocks,
            int baseProcessingTicks, int tickDelay) {
        super(block, connectableFaces, horizontalDirectionProperty, verticalDirectionProperty, ioConfig);
        this.upgradeDefs = upgradeDefs;
        this.bars = bars;
        this.menuConfig = menuConfig;
        this.gasCapacity = gasCapacity;
        this.gasPerTick = gasPerTick;
        this.gasConfigs = gasConfigs;
        this.processParticles = processParticles;
        this.passableBlocks = passableBlocks;
        this.baseProcessingTicks = baseProcessingTicks;
        this.tickDelay = tickDelay;
    }

    @Override
    public BlockEntityController createBlockEntityController(BlockEntity blockEntity) {
        return new FanMachineBlockEntity(blockEntity, upgradeDefs, bars, menuConfig,
                gasCapacity, gasPerTick,
                // fresh copies so each block-entity owns its (mutable, default-seeded) maps
                new java.util.HashMap<>(gasConfigs), new java.util.HashMap<>(processParticles),
                new java.util.HashSet<>(passableBlocks),
                baseProcessingTicks, tickDelay);
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

            int gasCapacity = intOr(arguments.get("gas_buffer"), 4000);
            int gasPerTick = intOr(arguments.get("gas_per_tick"), 50);
            int baseProcessingTicks = intOr(arguments.get("processing_interval"), 40);
            int tickDelay = intOr(arguments.get("tickDelay"), 2);

            // One config block per gas type (extensible; defaults seeded in the entity ctor):
            //   gases:
            //     steam:       { push_strength: 0.1,  push_limit: 5, particle: CLOUD }
            //     heavy_steam: { push_strength: 0.16, push_limit: 7, particle: SMOKE }
            java.util.Map<dev.arubik.craftengine.gas.GasType, dev.arubik.craftengine.machine.block.entity.FanGasConfig> gasConfigs =
                    parseGasConfigs(arguments.get("gases"));
            // Particle shown DOWNSTREAM of a process block, per family:
            //   process_particles: { smelting: FLAME, blasting: LAVA, washing: SPLASH }
            java.util.Map<dev.arubik.craftengine.machine.recipe.FanProcess, org.bukkit.Particle> processParticles =
                    parseProcessParticles(arguments.get("process_particles"));
            // Extra blocks the airflow passes through (besides air/fire/water/lava).
            java.util.Set<Key> passableBlocks = new java.util.HashSet<>();
            Object pb = arguments.get("passableBlocks");
            if (pb instanceof java.util.List<?> list)
                for (Object o : list)
                    if (o != null)
                        passableBlocks.add(key(String.valueOf(o)));

            return new FanMachineBehavior(block, base.getConnectableFaces(),
                    base.horizontalDirectionProperty, base.verticalDirectionProperty,
                    base.getIOConfiguration(null, null), upgrades, bars, menuConfig,
                    gasCapacity, gasPerTick,
                    gasConfigs, processParticles, passableBlocks,
                    baseProcessingTicks, tickDelay);
        }

        private static java.util.Map<dev.arubik.craftengine.machine.recipe.FanProcess, org.bukkit.Particle> parseProcessParticles(
                Object o) {
            java.util.Map<dev.arubik.craftengine.machine.recipe.FanProcess, org.bukkit.Particle> m =
                    new java.util.HashMap<>();
            if (o instanceof Map<?, ?> map)
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    dev.arubik.craftengine.machine.recipe.FanProcess fp;
                    try {
                        fp = dev.arubik.craftengine.machine.recipe.FanProcess.valueOf(
                                String.valueOf(e.getKey()).trim().toUpperCase(java.util.Locale.ROOT));
                    } catch (Exception ex) {
                        continue;
                    }
                    try {
                        m.put(fp, org.bukkit.Particle.valueOf(String.valueOf(e.getValue()).trim()
                                .toUpperCase(java.util.Locale.ROOT)));
                    } catch (Exception ignored) {
                    }
                }
            return m;
        }

        @SuppressWarnings("unchecked")
        private static java.util.Map<dev.arubik.craftengine.gas.GasType, dev.arubik.craftengine.machine.block.entity.FanGasConfig> parseGasConfigs(
                Object o) {
            java.util.Map<dev.arubik.craftengine.gas.GasType, dev.arubik.craftengine.machine.block.entity.FanGasConfig> m =
                    new java.util.HashMap<>();
            if (o instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    dev.arubik.craftengine.gas.GasType g = gasOf(String.valueOf(e.getKey()));
                    if (g == null || !(e.getValue() instanceof Map<?, ?> cfg))
                        continue;
                    java.util.Map<String, Object> c = (java.util.Map<String, Object>) cfg;
                    double ps = dblOr(c.get("push_strength"), 0.1);
                    int pl = intOr(c.get("push_limit"), 5);
                    org.bukkit.Particle part = org.bukkit.Particle.CLOUD;
                    try {
                        if (c.get("particle") != null)
                            part = org.bukkit.Particle.valueOf(String.valueOf(c.get("particle")).trim()
                                    .toUpperCase(java.util.Locale.ROOT));
                    } catch (Exception ignored) {
                    }
                    m.put(g, new dev.arubik.craftengine.machine.block.entity.FanGasConfig(ps, pl, part));
                }
            }
            return m;
        }

        private static dev.arubik.craftengine.gas.GasType gasOf(String s) {
            try {
                return dev.arubik.craftengine.gas.GasType.valueOf(s.trim().toUpperCase(java.util.Locale.ROOT));
            } catch (Exception e) {
                return null;
            }
        }

        private static int intOr(Object o, int def) {
            if (o instanceof Number n)
                return n.intValue();
            try {
                return o == null ? def : Integer.parseInt(o.toString());
            } catch (NumberFormatException e) {
                return def;
            }
        }

        private static double dblOr(Object o, double def) {
            if (o instanceof Number n)
                return n.doubleValue();
            try {
                return o == null ? def : Double.parseDouble(o.toString());
            } catch (NumberFormatException e) {
                return def;
            }
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
