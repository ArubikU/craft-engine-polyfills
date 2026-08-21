package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import org.joml.Vector3d;

public final class VectorType {

    private VectorType() {}

    public static void register() {
        PolyTypeRegistry.define("Vector")
            .property("x", obj -> ScriptValue.of(vec(obj).x))
            .property("y", obj -> ScriptValue.of(vec(obj).y))
            .property("z", obj -> ScriptValue.of(vec(obj).z))
            .property("length", obj -> ScriptValue.of(vec(obj).length()))
            .property("length_sq", obj -> ScriptValue.of(vec(obj).lengthSquared()))
            .method("add", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() >= 3) return wrap(new Vector3d(v.x + args.get(0).asNum(), v.y + args.get(1).asNum(), v.z + args.get(2).asNum()));
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return wrap(new Vector3d(v.x + other.x, v.y + other.y, v.z + other.z));
                }
                return ScriptValue.NULL;
            })
            .method("sub", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() >= 3) return wrap(new Vector3d(v.x - args.get(0).asNum(), v.y - args.get(1).asNum(), v.z - args.get(2).asNum()));
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return wrap(new Vector3d(v.x - other.x, v.y - other.y, v.z - other.z));
                }
                return ScriptValue.NULL;
            })
            .method("scale", (obj, args) -> {
                Vector3d v = vec(obj);
                double s = args.isEmpty() ? 1 : args.get(0).asNum();
                return wrap(new Vector3d(v.x * s, v.y * s, v.z * s));
            })
            .method("normalize", (obj, args) -> {
                Vector3d v = vec(obj);
                double len = v.length();
                if (len == 0) return wrap(new Vector3d(0, 0, 0));
                return wrap(new Vector3d(v.x / len, v.y / len, v.z / len));
            })
            .method("distance", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return ScriptValue.of(v.distance(other));
                }
                return ScriptValue.of(0.0);
            })
            .method("distance_sq", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return ScriptValue.of(v.distanceSquared(other));
                }
                return ScriptValue.of(0.0);
            })
            .method("dot", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return ScriptValue.of(v.dot(other));
                }
                return ScriptValue.of(0.0);
            })
            .method("cross", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return wrap(new Vector3d(v).cross(other));
                }
                return ScriptValue.NULL;
            });
    }

    public static ScriptValue wrap(Vector3d v) {
        return ScriptValue.ofObj("Vector", v);
    }

    public static ScriptValue wrap(double x, double y, double z) {
        return ScriptValue.ofObj("Vector", new Vector3d(x, y, z));
    }

    private static Vector3d vec(Object obj) {
        return (Vector3d) obj;
    }
}
