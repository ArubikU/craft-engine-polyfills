package dev.arubik.craftengine.script;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import dev.arubik.craftengine.script.types.primitive.*;
import dev.arubik.craftengine.script.types.world.*;
import dev.arubik.craftengine.script.types.entity.*;
import dev.arubik.craftengine.script.types.machine.*;
import dev.arubik.craftengine.script.types.resource.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Evaluation context for script expressions. Holds named variables and
 * typed class instances. Completely NMS — no Bukkit types.
 */
public final class ScriptContext {

    private final Map<String, ScriptValue> vars;
    private final Map<String, ScriptValue> classInstances;

    private ScriptContext(Map<String, ScriptValue> vars, Map<String, ScriptValue> classInstances) {
        this.vars = vars;
        this.classInstances = classInstances;
    }

    public ScriptValue getVar(String name) {
        return vars.getOrDefault(name, ScriptValue.NULL);
    }

    public ScriptValue getClassInstance(String name) {
        return classInstances.getOrDefault(name, ScriptValue.NULL);
    }

    public boolean hasVar(String name) {
        return vars.containsKey(name);
    }

    public boolean hasClass(String name) {
        return classInstances.containsKey(name);
    }

    public Map<String, ScriptValue> vars() { return vars; }
    public Map<String, ScriptValue> classInstances() { return classInstances; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final LinkedHashMap<String, ScriptValue> vars = new LinkedHashMap<>();
        private final LinkedHashMap<String, ScriptValue> classes = new LinkedHashMap<>();

        public Builder num(String name, double v) {
            vars.put(name, ScriptValue.of(v));
            return this;
        }

        public Builder bool(String name, boolean v) {
            vars.put(name, ScriptValue.of(v));
            return this;
        }

        public Builder str(String name, String v) {
            vars.put(name, ScriptValue.of(v));
            return this;
        }

        public Builder item(String name, ItemStack s) {
            vars.put(name, s == null || s.isEmpty() ? ScriptValue.NULL : ScriptValue.ofItem(s));
            return this;
        }

        public Builder val(String name, ScriptValue v) {
            vars.put(name, v != null ? v : ScriptValue.NULL);
            return this;
        }

        public Builder typed(String className, Object instance) {
            if (instance != null) {
                classes.put(className, ScriptValue.ofObj(className, instance));
            }
            return this;
        }

        public Builder player(ServerPlayer p) {
            if (p != null) classes.put("Player", PlayerType.wrap(p));
            return this;
        }

        public Builder world(ServerLevel level) {
            if (level != null) classes.put("World", WorldType.wrap(level));
            return this;
        }

        public Builder block(ServerLevel level, BlockPos pos) {
            if (level != null && pos != null) classes.put("Block", BlockType.wrap(level, pos));
            return this;
        }

        public Builder entity(net.minecraft.world.entity.Entity e) {
            if (e != null) classes.put("Entity", EntityType.wrap(e));
            return this;
        }

        public Builder vector(String name, double x, double y, double z) {
            classes.put(name, VectorType.wrap(x, y, z));
            return this;
        }

        public Builder redstone(int power) {
            vars.put("redstone", ScriptValue.of(power));
            vars.put("powered", ScriptValue.of(power > 0));
            vars.put("redstone_power", ScriptValue.of(power));
            return this;
        }

        public Builder machine(double rpm, double overclock, double efficiency,
                              double progress, double maxProgress, double tier,
                              boolean processing, boolean powered, boolean overclocked, boolean hasFuel) {
            vars.put("rpm", ScriptValue.of(rpm));
            vars.put("overclock", ScriptValue.of(overclock));
            vars.put("efficiency", ScriptValue.of(efficiency));
            vars.put("progress", ScriptValue.of(progress));
            vars.put("max_progress", ScriptValue.of(maxProgress));
            vars.put("tier", ScriptValue.of(tier));
            vars.put("processing", ScriptValue.of(processing));
            vars.put("powered", ScriptValue.of(powered));
            vars.put("overclocked", ScriptValue.of(overclocked));
            vars.put("has_fuel", ScriptValue.of(hasFuel));
            return this;
        }

        public Builder facing(String facing, float facingYaw) {
            vars.put("facing", ScriptValue.of(facing != null ? facing : "north"));
            vars.put("facing_angle", ScriptValue.of(facingYaw));
            int[] off = facingOffset(facing);
            vars.put("facing_dx", ScriptValue.of(off[0]));
            vars.put("facing_dy", ScriptValue.of(off[1]));
            vars.put("facing_dz", ScriptValue.of(off[2]));
            return this;
        }

        public Builder copyFrom(ScriptContext other) {
            vars.putAll(other.vars);
            classes.putAll(other.classInstances);
            return this;
        }

        public ScriptContext build() {
            return new ScriptContext(Map.copyOf(vars), Map.copyOf(classes));
        }

        private static int[] facingOffset(String facing) {
            if (facing == null) return new int[]{0, 0, -1};
            return switch (facing.toLowerCase()) {
                case "south" -> new int[]{0, 0, 1};
                case "east" -> new int[]{1, 0, 0};
                case "west" -> new int[]{-1, 0, 0};
                case "up" -> new int[]{0, 1, 0};
                case "down" -> new int[]{0, -1, 0};
                default -> new int[]{0, 0, -1};
            };
        }
    }
}
