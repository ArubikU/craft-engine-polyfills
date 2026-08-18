package dev.arubik.craftengine.block.behavior;

import dev.arubik.craftengine.machine.render.RendererManager;
import dev.arubik.craftengine.machine.render.RendererSpec;
import dev.arubik.craftengine.machine.render.variable.MachineRenderContext;
import dev.arubik.craftengine.machine.render.variable.VariableSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.behavior.BlockBehavior;
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory;
import net.momirealms.craftengine.core.plugin.config.ConfigSection;

import java.util.*;

/**
 * Block behavior that drives the data-driven renderer system (particles, displays, models)
 * on any CE custom block without requiring a full machine infrastructure.
 *
 * Config example in block YAML:
 * <pre>
 *   behaviors:
 *     polyfill:renderer:
 *       renderers:
 *         - { type: particle, particle: FLAME, offset: [0.5, 0.8, 0.5], count: "redstone / 4", when: "powered" }
 *         - { type: text_display, text: "'Signal: ' + redstone", offset_y: "1.5", when: "powered", billboard: center }
 * </pre>
 */
public class RendererBehavior extends BukkitBlockBehavior {

    public static final Factory FACTORY = new Factory();

    private final List<RendererSpec> specs;
    private final Map<String, VariableSpec> varSpecs;

    private RendererBehavior(BlockDefinition block, List<RendererSpec> specs, Map<String, VariableSpec> varSpecs) {
        super(block);
        this.specs = specs;
        this.varSpecs = varSpecs;
    }

    @Override
    public void tick(Object thisBlock, Object[] args) {
        super.tick(thisBlock, args);
        if (specs.isEmpty()) return;
        try {
            BlockState nms = (BlockState) args[0];
            Level level = (Level) args[2];
            BlockPos pos = (BlockPos) args[1];
            if (!(level instanceof ServerLevel sl)) return;

            int redstone = sl.getBestNeighborSignal(pos);
            String facing = "north";
            for (var prop : nms.getProperties()) {
                if ("facing".equals(prop.getName())) {
                    facing = nms.getValue(prop).toString().toLowerCase();
                    break;
                }
            }
            float yaw = switch (facing) {
                case "south" -> 0f;
                case "west" -> 90f;
                case "north" -> 180f;
                case "east" -> 270f;
                default -> 0f;
            };

            // Resolve container (chest, barrel, hopper, dispenser, etc.) at the block
            org.bukkit.inventory.Inventory container = null;
            try {
                if (nms.hasBlockEntity()) {
                    net.minecraft.world.level.block.entity.BlockEntity be = sl.getBlockEntity(pos);
                    if (be instanceof net.minecraft.world.Container c) {
                        container = new org.bukkit.craftbukkit.inventory.CraftInventory(c);
                    }
                }
            } catch (Throwable ignored) {}

            MachineRenderContext ctx = new MachineRenderContext(
                    0, 0, 0, 0, 0, 0,
                    false, redstone > 0, false, false,
                    container, null, Map.of(), Map.of(), redstone);

            getOrCreateManager().tick(ctx, sl, pos.getX(), pos.getY(), pos.getZ(), yaw);
        } catch (Throwable ignored) {}
    }

    private RendererManager manager;

    private RendererManager getOrCreateManager() {
        if (manager == null) {
            manager = new RendererManager(specs, varSpecs);
        }
        return manager;
    }

    @SuppressWarnings("unchecked")
    public static class Factory implements BlockBehaviorFactory<BlockBehavior> {
        @Override
        public BlockBehavior create(BlockDefinition block, ConfigSection arguments) {
            Map<String, VariableSpec> vars = new LinkedHashMap<>();
            List<RendererSpec> renderers = new ArrayList<>();

            // Parse variables: Map<String, String> (inline formula strings)
            Object rawVars = arguments.get("variables");
            if (rawVars instanceof Map<?, ?> varMap) {
                for (var entry : varMap.entrySet()) {
                    vars.put(String.valueOf(entry.getKey()), new VariableSpec.Formula(String.valueOf(entry.getValue())));
                }
            }

            // Parse renderers: List<Map<String, Object>>
            Object rawRenderers = arguments.get("renderers");
            if (rawRenderers instanceof List<?> list) {
                for (Object item : list) {
                    if (!(item instanceof Map<?, ?> m)) continue;
                    Map<String, Object> r = (Map<String, Object>) m;
                    String type = str(r, "type", "particle");
                    String when = str(r, "when", "always");
                    String updateWhen = str(r, "update_when", "always");
                    String run = str(r, "run", null);

                    // Build locationExpr from offset_x/y/z or location key
                    String locExpr = null;
                    if (r.containsKey("location")) {
                        Object locObj = r.get("location");
                        if (locObj instanceof String ls) locExpr = ls;
                        else if (locObj instanceof List<?> la && la.size() >= 3)
                            locExpr = "[" + la.get(0) + ", " + la.get(1) + ", " + la.get(2) + "]";
                    }
                    if (locExpr == null && (r.containsKey("offset_x") || r.containsKey("offset_y") || r.containsKey("offset_z"))) {
                        locExpr = "[" + str(r, "offset_x", "0.5") + ", " + str(r, "offset_y", "0.5") + ", " + str(r, "offset_z", "0.5") + "]";
                    }
                    RendererSpec spec = switch (type) {
                        case "particle" -> new RendererSpec.ParticleSpec(
                                str(r, "particle", "FLAME"),
                                str(r, "count", "1"),
                                locExpr,
                                str(r, "spread_x", "0"), str(r, "spread_y", "0"), str(r, "spread_z", "0"),
                                str(r, "speed", "0.05"),
                                str(r, "dir_x", "0"), str(r, "dir_y", "0"), str(r, "dir_z", "0"),
                                str(r, "shape", "point"), str(r, "direction_mode", "random"),
                                intV(r, "interval", 1),
                                when, updateWhen, run);
                        case "text_display" -> new RendererSpec.TextDisplaySpec(
                                str(r, "text", ""),
                                locExpr != null ? locExpr : "[0, 1.5, 0]",
                                str(r, "scale", "0.1"),
                                str(r, "rot_x", "0"), str(r, "rot_y", "0"), str(r, "rot_z", "0"),
                                str(r, "billboard", "center"),
                                intV(r, "line_width", 200),
                                str(r, "background", "0"),
                                boolV(r, "shadow", false), boolV(r, "see_through", false),
                                str(r, "alignment", "center"), intV(r, "opacity", 255),
                                when, updateWhen, boolV(r, "global", false), run);
                        case "item_display" -> new RendererSpec.ItemDisplaySpec(
                                str(r, "item", "minecraft:air"),
                                locExpr,
                                str(r, "scale", "1.0"),
                                str(r, "rot_x", "0"), str(r, "rot_y", "0"), str(r, "rot_z", "0"),
                                str(r, "billboard", "none"),
                                when, updateWhen, boolV(r, "global", false), run);
                        case "bettermodel" -> new RendererSpec.BetterModelSpec(
                                str(r, "model_id", ""),
                                str(r, "animation", null),
                                str(r, "speed", null),
                                when, updateWhen, run, intV(r, "linger_ticks", 0));
                        case "modelengine" -> new RendererSpec.ModelEngineSpec(
                                str(r, "model_id", ""),
                                str(r, "animation", null),
                                str(r, "speed", null),
                                when, updateWhen, run);
                        default -> null;
                    };
                    if (spec != null) renderers.add(spec);
                }
            }

            return new RendererBehavior(block, renderers, vars);
        }

        private static String str(Map<String, Object> m, String key, String def) {
            Object v = m.get(key);
            return v != null ? v.toString() : def;
        }

        private static float flt(Map<String, Object> m, String key, float def) {
            Object v = m.get(key);
            if (v instanceof Number n) return n.floatValue();
            if (v instanceof String s) { try { return Float.parseFloat(s); } catch (Exception ignored) {} }
            return def;
        }

        private static int intV(Map<String, Object> m, String key, int def) {
            Object v = m.get(key);
            if (v instanceof Number n) return n.intValue();
            if (v instanceof String s) { try { return Integer.parseInt(s); } catch (Exception ignored) {} }
            return def;
        }

        private static boolean boolV(Map<String, Object> m, String key, boolean def) {
            Object v = m.get(key);
            if (v instanceof Boolean b) return b;
            if (v instanceof String s) return Boolean.parseBoolean(s);
            return def;
        }
    }
}
