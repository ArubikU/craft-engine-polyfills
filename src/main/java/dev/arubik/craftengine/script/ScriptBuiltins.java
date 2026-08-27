package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.types.primitive.MapType;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global built-in functions available in all script expressions.
 * External code can register additional builtins via {@link #register(String, BuiltinFunction)}.
 */
public final class ScriptBuiltins {

    @FunctionalInterface
    public interface BuiltinFunction {
        ScriptValue call(List<ScriptValue> args, ScriptContext ctx);
    }

    private static final Map<String, BuiltinFunction> BUILTINS = new ConcurrentHashMap<>();

    private ScriptBuiltins() {}

    public static void register(String name, BuiltinFunction fn) {
        BUILTINS.put(name, fn);
    }

    public static BuiltinFunction get(String name) {
        return BUILTINS.get(name);
    }

    public static boolean has(String name) {
        return BUILTINS.containsKey(name);
    }

    public static void init() {
        BUILTINS.clear();

        // ---- Type checks / coercions -------------------------------------------
        // instanceof(entity, "Player") → true if entity's type inherits from "Player"
        // TODO: ScriptFormula parser should also support infix syntax: `entity instanceof Player`
        //       by treating `instanceof` as a binary operator (lhs=Obj, rhs=Str type name).
        register("instanceof", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(false);
            return ScriptValue.of(args.get(0).isInstanceOf(args.get(1).asStr()));
        });
        register("type_of", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.of("null");
            ScriptValue v = args.get(0);
            if (v instanceof ScriptValue.Obj o) return ScriptValue.of(o.typeName());
            if (v instanceof ScriptValue.Num) return ScriptValue.of("num");
            if (v instanceof ScriptValue.Bool) return ScriptValue.of("bool");
            if (v instanceof ScriptValue.Str) return ScriptValue.of("str");
            if (v instanceof ScriptValue.Item) return ScriptValue.of("item");
            if (v instanceof ScriptValue.Array) return ScriptValue.of("array");
            return ScriptValue.of("null");
        });
        register("is_empty", (args, ctx) -> ScriptValue.of(args.isEmpty() || isNullOrEmpty(args.get(0))));
        register("is_not_empty", (args, ctx) -> ScriptValue.of(!args.isEmpty() && !isNullOrEmpty(args.get(0))));
        register("not", (args, ctx) -> ScriptValue.of(args.isEmpty() || !args.get(0).asBool()));
        register("bool", (args, ctx) -> ScriptValue.of(!args.isEmpty() && args.get(0).asBool()));
        register("num", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum()));
        register("str", (args, ctx) -> ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr()));

        // ---- Item functions (NMS only) -----------------------------------------
        register("item_id", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            ScriptValue v = args.get(0);
            if (!(v instanceof ScriptValue.Item i)) return ScriptValue.NULL;
            if (i.stack().isEmpty()) return ScriptValue.of("minecraft:air");
            return ScriptValue.of(BuiltInRegistries.ITEM.getKey(i.stack().getItem()).toString());
        });

        register("item_count", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum()));

        register("matches", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(false);
            return ScriptValue.of(itemMatchesId(args.get(0), args.get(1).asStr()));
        });

        register("has_item", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(false);
            return ScriptValue.of(itemMatchesId(args.get(0), args.get(1).asStr()));
        });

        // ---- Item creation (NMS only) ------------------------------------------
        register("MinecraftItem", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            String id = args.get(0).asStr();
            Item item = (Item) BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
            if (item == null) return ScriptValue.NULL;
            int count = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
            return ScriptValue.ofItem(new ItemStack(item, count));
        });
        register("VanillaItem", BUILTINS.get("MinecraftItem"));

        // create_item — alias for MinecraftItem, preferred name in scripts
        register("create_item", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            String id = args.get(0).asStr();
            Item item = (Item) BuiltInRegistries.ITEM.getValue(Identifier.parse(id.contains(":") ? id : "minecraft:" + id));
            if (item == null) return ScriptValue.NULL;
            int count = args.size() >= 2 ? (int) args.get(1).asNum() : 1;
            return ScriptValue.ofItem(new ItemStack(item, count));
        });

        // item_nbt(item) → NbtData of custom data tag
        register("item_nbt", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            if (!(args.get(0) instanceof ScriptValue.Item i) || i.stack() == null || i.stack().isEmpty()) return ScriptValue.NULL;
            try {
                var cd = i.stack().get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                if (cd == null) return ScriptValue.NULL;
                return dev.arubik.craftengine.script.types.primitive.NbtDataType.wrap(cd.copyTag());
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // ---- Map / collection --------------------------------------------------
        register("make_map", (args, ctx) -> MapType.makeMap(args));

        register("range", (args, ctx) -> {
            int start = 0, end = 0;
            if (args.size() == 1) { end = (int) args.get(0).asNum(); }
            else if (args.size() >= 2) { start = (int) args.get(0).asNum(); end = (int) args.get(1).asNum(); }
            List<ScriptValue> list = new ArrayList<>(Math.max(0, end - start));
            for (int i = start; i < end; i++) list.add(ScriptValue.of(i));
            return new ScriptValue.Array(list);
        });

        register("len", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.of(0);
            ScriptValue v = args.get(0);
            if (v instanceof ScriptValue.Array a) return ScriptValue.of(a.elements().size());
            if (v instanceof ScriptValue.Str s) return ScriptValue.of(s.value().length());
            return ScriptValue.of(0);
        });

        // ---- Vector construction -----------------------------------------------
        register("vec", (args, ctx) -> {
            double x = args.size() > 0 ? args.get(0).asNum() : 0;
            double y = args.size() > 1 ? args.get(1).asNum() : 0;
            double z = args.size() > 2 ? args.get(2).asNum() : 0;
            return VectorType.wrap(x, y, z);
        });

        // ---- Trig (radians) ----------------------------------------------------
        register("sin", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.sin(args.get(0).asNum())));
        register("cos", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.cos(args.get(0).asNum())));
        register("tan", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.tan(args.get(0).asNum())));
        register("asin", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.asin(args.get(0).asNum())));
        register("acos", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.acos(args.get(0).asNum())));
        register("atan", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.atan(args.get(0).asNum())));
        register("atan2", (args, ctx) -> ScriptValue.of(args.size() < 2 ? 0 : Math.atan2(args.get(0).asNum(), args.get(1).asNum())));
        register("deg", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.toDegrees(args.get(0).asNum())));
        register("rad", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.toRadians(args.get(0).asNum())));

        // ---- Math --------------------------------------------------------------
        register("abs", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.abs(args.get(0).asNum())));
        register("sqrt", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.sqrt(args.get(0).asNum())));
        register("pow", (args, ctx) -> ScriptValue.of(args.size() < 2 ? 0 : Math.pow(args.get(0).asNum(), args.get(1).asNum())));
        register("log", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.log(args.get(0).asNum())));
        register("log10", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.log10(args.get(0).asNum())));
        register("exp", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.exp(args.get(0).asNum())));
        register("sign", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.signum(args.get(0).asNum())));

        // ---- Rounding ----------------------------------------------------------
        register("floor", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.floor(args.get(0).asNum())));
        register("ceil", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.ceil(args.get(0).asNum())));
        register("round", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : Math.round(args.get(0).asNum())));
        register("trunc", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : (long) args.get(0).asNum()));

        // ---- Min / max / clamp / lerp ------------------------------------------
        register("min", (args, ctx) -> ScriptValue.of(args.size() < 2 ? 0 : Math.min(args.get(0).asNum(), args.get(1).asNum())));
        register("max", (args, ctx) -> ScriptValue.of(args.size() < 2 ? 0 : Math.max(args.get(0).asNum(), args.get(1).asNum())));
        register("clamp", (args, ctx) -> {
            if (args.size() < 3) return ScriptValue.of(0);
            double v = args.get(0).asNum(), lo = args.get(1).asNum(), hi = args.get(2).asNum();
            return ScriptValue.of(Math.max(lo, Math.min(hi, v)));
        });
        register("lerp", (args, ctx) -> {
            if (args.size() < 3) return ScriptValue.of(0);
            double a = args.get(0).asNum(), b = args.get(1).asNum(), t = args.get(2).asNum();
            return ScriptValue.of(a + (b - a) * t);
        });

        // ---- String functions --------------------------------------------------
        register("concat", (args, ctx) -> {
            StringBuilder sb = new StringBuilder();
            for (ScriptValue a : args) sb.append(a.asStr());
            return ScriptValue.of(sb.toString());
        });
        register("contains", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(false);
            return ScriptValue.of(args.get(0).asStr().contains(args.get(1).asStr()));
        });
        register("starts_with", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(false);
            return ScriptValue.of(args.get(0).asStr().startsWith(args.get(1).asStr()));
        });
        register("ends_with", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(false);
            return ScriptValue.of(args.get(0).asStr().endsWith(args.get(1).asStr()));
        });

        // ---- String extras -------------------------------------------------------
        register("split", (args, ctx) -> {
            if (args.size() < 2) return new ScriptValue.Array(java.util.List.of());
            String delim = args.get(1).asStr();
            String[] parts = args.get(0).asStr().split(java.util.regex.Pattern.quote(delim), -1);
            java.util.List<ScriptValue> list = new java.util.ArrayList<>(parts.length);
            for (String p : parts) list.add(ScriptValue.of(p));
            return new ScriptValue.Array(list);
        });
        register("trim", (args, ctx) -> ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr().trim()));
        register("upper", (args, ctx) -> ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr().toUpperCase(java.util.Locale.ROOT)));
        register("lower", (args, ctx) -> ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr().toLowerCase(java.util.Locale.ROOT)));
        register("replace", (args, ctx) -> {
            if (args.size() < 3) return args.isEmpty() ? ScriptValue.of("") : args.get(0);
            return ScriptValue.of(args.get(0).asStr().replace(args.get(1).asStr(), args.get(2).asStr()));
        });
        register("index_of", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(-1);
            return ScriptValue.of(args.get(0).asStr().indexOf(args.get(1).asStr()));
        });
        register("char_at", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of("");
            String s = args.get(0).asStr();
            int i = (int) args.get(1).asNum();
            return (i >= 0 && i < s.length()) ? ScriptValue.of(String.valueOf(s.charAt(i))) : ScriptValue.of("");
        });
        register("to_str", (args, ctx) -> ScriptValue.of(args.isEmpty() ? "" : args.get(0).asStr()));
        register("to_num", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum()));
        register("format_num", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.of("0");
            double val = args.get(0).asNum();
            int decimals = args.size() >= 2 ? (int) args.get(1).asNum() : 2;
            return ScriptValue.of(String.format("%." + Math.max(0, decimals) + "f", val));
        });

        // ---- Random & constants -------------------------------------------------
        register("random", (args, ctx) -> {
            var rng = java.util.concurrent.ThreadLocalRandom.current();
            if (args.isEmpty()) return ScriptValue.of(rng.nextDouble());
            if (args.size() == 1) return ScriptValue.of(rng.nextDouble() * args.get(0).asNum());
            double lo = args.get(0).asNum(), hi = args.get(1).asNum();
            return ScriptValue.of(lo + rng.nextDouble() * (hi - lo));
        });
        register("random_int", (args, ctx) -> {
            var rng = java.util.concurrent.ThreadLocalRandom.current();
            if (args.isEmpty()) return ScriptValue.of(0);
            if (args.size() == 1) {
                int hi = (int) args.get(0).asNum();
                return hi <= 0 ? ScriptValue.of(0) : ScriptValue.of(rng.nextInt(hi));
            }
            int lo = (int) args.get(0).asNum(), hi = (int) args.get(1).asNum();
            return hi <= lo ? ScriptValue.of(lo) : ScriptValue.of(lo + rng.nextInt(hi - lo));
        });
        register("pi", (args, ctx) -> ScriptValue.of(Math.PI));
        register("tau", (args, ctx) -> ScriptValue.of(Math.PI * 2));
        register("infinity", (args, ctx) -> ScriptValue.of(Double.MAX_VALUE));

        // ---- Array extras -------------------------------------------------------
        register("first", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            if (args.get(0) instanceof ScriptValue.Array a)
                return a.elements().isEmpty() ? ScriptValue.NULL : a.elements().get(0);
            return args.get(0);
        });
        register("last", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            if (args.get(0) instanceof ScriptValue.Array a) {
                java.util.List<ScriptValue> elems = a.elements();
                return elems.isEmpty() ? ScriptValue.NULL : elems.get(elems.size() - 1);
            }
            return args.get(0);
        });
        register("slice", (args, ctx) -> {
            if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Array a)) return new ScriptValue.Array(java.util.List.of());
            java.util.List<ScriptValue> elems = a.elements();
            int start = args.size() >= 2 ? (int) args.get(1).asNum() : 0;
            int end   = args.size() >= 3 ? (int) args.get(2).asNum() : elems.size();
            start = Math.max(0, start < 0 ? elems.size() + start : start);
            end   = Math.min(elems.size(), end < 0 ? elems.size() + end : end);
            return new ScriptValue.Array(new java.util.ArrayList<>(elems.subList(Math.min(start, end), Math.max(start, end))));
        });
        register("push", (args, ctx) -> {
            if (args.size() < 2 || !(args.get(0) instanceof ScriptValue.Array a))
                return args.isEmpty() ? new ScriptValue.Array(java.util.List.of()) : args.get(0);
            java.util.List<ScriptValue> newList = new java.util.ArrayList<>(a.elements());
            newList.add(args.get(1));
            return new ScriptValue.Array(newList);
        });
        register("concat_arrays", (args, ctx) -> {
            java.util.List<ScriptValue> result = new java.util.ArrayList<>();
            for (ScriptValue arg : args) {
                if (arg instanceof ScriptValue.Array a) result.addAll(a.elements());
                else result.add(arg);
            }
            return new ScriptValue.Array(result);
        });
        register("sort_nums", (args, ctx) -> {
            if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Array a)) return new ScriptValue.Array(java.util.List.of());
            java.util.List<ScriptValue> sorted = new java.util.ArrayList<>(a.elements());
            sorted.sort(java.util.Comparator.comparingDouble(ScriptValue::asNum));
            return new ScriptValue.Array(sorted);
        });
        register("sort_strs", (args, ctx) -> {
            if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Array a)) return new ScriptValue.Array(java.util.List.of());
            java.util.List<ScriptValue> sorted = new java.util.ArrayList<>(a.elements());
            sorted.sort(java.util.Comparator.comparing(ScriptValue::asStr));
            return new ScriptValue.Array(sorted);
        });
        register("len", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.of(0);
            if (args.get(0) instanceof ScriptValue.Array a) return ScriptValue.of(a.elements().size());
            if (args.get(0) instanceof ScriptValue.Str s) return ScriptValue.of(s.value().length());
            return ScriptValue.of(0);
        });
        register("make_map", (args, ctx) -> {
            java.util.LinkedHashMap<String, ScriptValue> map = new java.util.LinkedHashMap<>();
            for (int i = 0; i + 1 < args.size(); i += 2) map.put(args.get(i).asStr(), args.get(i + 1));
            return ScriptValue.ofObj("Map", map);
        });
        register("range", (args, ctx) -> {
            int start = 0, end = 0;
            if (args.size() == 1) { end = (int) args.get(0).asNum(); }
            else if (args.size() >= 2) { start = (int) args.get(0).asNum(); end = (int) args.get(1).asNum(); }
            java.util.List<ScriptValue> list = new java.util.ArrayList<>(Math.max(0, end - start));
            for (int i = start; i < end; i++) list.add(ScriptValue.of(i));
            return new ScriptValue.Array(list);
        });

        // ---- Control / utility --------------------------------------------------
        register("switch_val", (args, ctx) -> {
            if (args.size() < 3) return ScriptValue.NULL;
            String key = args.get(0).asStr();
            for (int i = 1; i + 1 < args.size(); i += 2) {
                if (args.get(i).asStr().equals(key)) return args.get(i + 1);
            }
            return (args.size() % 2 == 0) ? args.get(args.size() - 1) : ScriptValue.NULL;
        });
        register("print", (args, ctx) -> {
            String msg = args.stream().map(ScriptValue::asStr).collect(java.util.stream.Collectors.joining(", "));
            java.util.logging.Logger.getLogger("CraftEnginePolyfills").info("[Script] " + msg);
            return ScriptValue.NULL;
        });
        register("vec", (args, ctx) -> {
            double x = args.size() > 0 ? args.get(0).asNum() : 0;
            double y = args.size() > 1 ? args.get(1).asNum() : 0;
            double z = args.size() > 2 ? args.get(2).asNum() : 0;
            return dev.arubik.craftengine.script.types.primitive.VectorType.wrap(x, y, z);
        });

        // ---- Interpolation / easing -------------------------------------------
        register("smoothstep", (args, ctx) -> {
            if (args.size() < 3) return ScriptValue.of(0);
            double e0 = args.get(0).asNum(), e1 = args.get(1).asNum(), x = args.get(2).asNum();
            double t = Math.max(0, Math.min(1, (x - e0) / (e1 - e0 == 0 ? 1 : e1 - e0)));
            return ScriptValue.of(t * t * (3 - 2 * t));
        });
        register("ease_in", (args, ctx) -> {
            double t = args.isEmpty() ? 0 : Math.max(0, Math.min(1, args.get(0).asNum()));
            return ScriptValue.of(t * t);
        });
        register("ease_out", (args, ctx) -> {
            double t = args.isEmpty() ? 0 : Math.max(0, Math.min(1, args.get(0).asNum()));
            return ScriptValue.of(1 - (1 - t) * (1 - t));
        });
        register("ease_in_out", (args, ctx) -> {
            double t = args.isEmpty() ? 0 : Math.max(0, Math.min(1, args.get(0).asNum()));
            return ScriptValue.of(t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2);
        });
        register("cubic_bezier", (args, ctx) -> {
            // cubic_bezier(t, p0, p1, p2, p3) — 1D cubic bezier
            if (args.size() < 5) return ScriptValue.of(0);
            double t = args.get(0).asNum(), p0 = args.get(1).asNum(), p1 = args.get(2).asNum();
            double p2 = args.get(3).asNum(), p3 = args.get(4).asNum();
            double u = 1 - t;
            return ScriptValue.of(u*u*u*p0 + 3*u*u*t*p1 + 3*u*t*t*p2 + t*t*t*p3);
        });

        // ---- Geometric / vector math ------------------------------------------
        register("hypot", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.of(0);
            double x = args.get(0).asNum(), y = args.get(1).asNum();
            double z = args.size() >= 3 ? args.get(2).asNum() : 0;
            return ScriptValue.of(Math.sqrt(x*x + y*y + z*z));
        });
        register("angle_between", (args, ctx) -> {
            // angle_between(x1,y1,z1, x2,y2,z2) in degrees
            if (args.size() < 6) return ScriptValue.of(0);
            double ax = args.get(0).asNum(), ay = args.get(1).asNum(), az = args.get(2).asNum();
            double bx = args.get(3).asNum(), by = args.get(4).asNum(), bz = args.get(5).asNum();
            double dot = ax*bx + ay*by + az*bz;
            double la = Math.sqrt(ax*ax + ay*ay + az*az), lb = Math.sqrt(bx*bx + by*by + bz*bz);
            if (la == 0 || lb == 0) return ScriptValue.of(0);
            return ScriptValue.of(Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, dot / (la * lb))))));
        });
        register("yaw_to_vec", (args, ctx) -> {
            double yaw = args.isEmpty() ? 0 : Math.toRadians(args.get(0).asNum());
            return VectorType.wrap(-Math.sin(yaw), 0, Math.cos(yaw));
        });
        register("pitch_yaw_to_vec", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.NULL;
            double pitch = Math.toRadians(args.get(0).asNum()), yaw = Math.toRadians(args.get(1).asNum());
            return VectorType.wrap(
                -Math.cos(pitch) * Math.sin(yaw),
                -Math.sin(pitch),
                Math.cos(pitch) * Math.cos(yaw));
        });
        register("look_at", (args, ctx) -> {
            // look_at(fromX, fromY, fromZ, toX, toY, toZ) → vec normalized direction
            if (args.size() < 6) return ScriptValue.NULL;
            double dx = args.get(3).asNum()-args.get(0).asNum();
            double dy = args.get(4).asNum()-args.get(1).asNum();
            double dz = args.get(5).asNum()-args.get(2).asNum();
            double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (len == 0) return VectorType.wrap(0, 0, 1);
            return VectorType.wrap(dx/len, dy/len, dz/len);
        });

        // ---- Logic utilities --------------------------------------------------
        register("if_val", (args, ctx) -> {
            if (args.size() < 2) return ScriptValue.NULL;
            return args.get(0).asBool() ? args.get(1) : (args.size() >= 3 ? args.get(2) : ScriptValue.NULL);
        });
        register("between", (args, ctx) -> {
            if (args.size() < 3) return ScriptValue.of(false);
            double v = args.get(0).asNum(), lo = args.get(1).asNum(), hi = args.get(2).asNum();
            return ScriptValue.of(v >= lo && v <= hi);
        });
        register("coalesce", (args, ctx) -> {
            for (ScriptValue v : args)
                if (!(v instanceof ScriptValue.Null) && !(v instanceof ScriptValue.Str s2 && s2.value().isEmpty()))
                    return v;
            return ScriptValue.NULL;
        });
        register("toggle", (args, ctx) -> {
            // toggle(val) — flips 0→1, non-zero→0; useful for boolean flags
            return args.isEmpty() ? ScriptValue.of(1) : ScriptValue.of(args.get(0).asNum() == 0 ? 1.0 : 0.0);
        });

        // ---- Time utilities ---------------------------------------------------
        register("time", (args, ctx) -> {
            try { return ScriptValue.of(net.minecraft.server.MinecraftServer.getServer().getTickCount()); }
            catch (Throwable ignored) { return ScriptValue.of(0); }
        });
        register("tick",   (args, ctx) -> { // alias for time() — used in renderer expressions
            try { return ScriptValue.of(net.minecraft.server.MinecraftServer.getServer().getTickCount()); }
            catch (Throwable ignored) { return ScriptValue.of(0); }
        });
        register("millis", (args, ctx) -> ScriptValue.of((double) System.currentTimeMillis()));

        // ---- Title component builtins (for machine page titles) -------------------
        // Images.from(id)        → TitleImage with default shift -8
        // Images.from(id, shift) → TitleImage with custom shift
        register("Images.from", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            String id = args.get(0).asStr();
            int shift = args.size() >= 2 ? (int) args.get(1).asNum() : -8;
            return ScriptValue.ofObj("_TitleImage", new String[]{id, String.valueOf(shift)});
        });
        // Shift(n) → wraps a TitleImage value with a new shift (used as Shift(Images.from(id), n))
        register("Shift", (args, ctx) -> {
            if (args.size() < 2) return args.isEmpty() ? ScriptValue.NULL : args.get(0);
            ScriptValue base = args.get(0);
            int shift = (int) args.get(1).asNum();
            if (base instanceof ScriptValue.Obj o && "_TitleImage".equals(o.typeName())) {
                String[] data = (String[]) o.instance();
                return ScriptValue.ofObj("_TitleImage", new String[]{data[0], String.valueOf(shift)});
            }
            return base; // if not an image, pass through
        });
        register("seconds_to_ticks", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum() * 20));
        register("ticks_to_seconds", (args, ctx) -> ScriptValue.of(args.isEmpty() ? 0 : args.get(0).asNum() / 20.0));

        // ---- Bitwise helpers (also available as operators ** // & | ~ << >>) ---
        register("bit_and",  (args, ctx) -> args.size() < 2 ? ScriptValue.of(0) : ScriptValue.of((double)((long)args.get(0).asNum() & (long)args.get(1).asNum())));
        register("bit_or",   (args, ctx) -> args.size() < 2 ? ScriptValue.of(0) : ScriptValue.of((double)((long)args.get(0).asNum() | (long)args.get(1).asNum())));
        register("bit_xor",  (args, ctx) -> args.size() < 2 ? ScriptValue.of(0) : ScriptValue.of((double)((long)args.get(0).asNum() ^ (long)args.get(1).asNum())));
        register("bit_not",  (args, ctx) -> args.isEmpty() ? ScriptValue.of(0) : ScriptValue.of((double)(~(long)args.get(0).asNum())));
        register("bit_shl",  (args, ctx) -> args.size() < 2 ? ScriptValue.of(0) : ScriptValue.of((double)((long)args.get(0).asNum() << (long)args.get(1).asNum())));
        register("bit_shr",  (args, ctx) -> args.size() < 2 ? ScriptValue.of(0) : ScriptValue.of((double)((long)args.get(0).asNum() >> (long)args.get(1).asNum())));
        register("int_div",  (args, ctx) -> args.size() < 2 ? ScriptValue.of(0) : ScriptValue.of(Math.floor(args.get(0).asNum() / (args.get(1).asNum() == 0 ? 1 : args.get(1).asNum()))));
        register("Animation", (args, ctx) -> dev.arubik.craftengine.script.types.machine.AnimationType.create());
    }

    private static boolean isNullOrEmpty(ScriptValue v) {
        if (v == null || v instanceof ScriptValue.Null) return true;
        if (v instanceof ScriptValue.Item i) return i.stack() == null || i.stack().isEmpty();
        if (v instanceof ScriptValue.Array a) return a.elements().isEmpty();
        if (v instanceof ScriptValue.Str s) return s.value().isEmpty();
        return false;
    }

    // Accepts a plain id or a "#"-prefixed tag (vanilla or CraftEngine custom item) — see
    // dev.arubik.craftengine.script.types.util.ItemMatch, the shared matcher used across the
    // script engine (Container.pull_item, Item.matches, here).
    private static boolean itemMatchesId(ScriptValue v, String id) {
        return dev.arubik.craftengine.script.types.util.ItemMatch.matches(v, id);
    }
}
