package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import org.joml.Vector3d;

public final class VectorType {

    private VectorType() {}

    public static void register() {
        PolyTypeRegistry.define("Vector")
            .propertyTyped("x", TypeCodecs.DOUBLE, (Vector3d v) -> v.x)
            .propertyTyped("y", TypeCodecs.DOUBLE, (Vector3d v) -> v.y)
            .propertyTyped("z", TypeCodecs.DOUBLE, (Vector3d v) -> v.z)
            .propertyTyped("length", TypeCodecs.DOUBLE, (Vector3d v) -> v.length())
            .propertyTyped("length_sq", TypeCodecs.DOUBLE, (Vector3d v) -> v.lengthSquared())
            // add(x,y,z) OR add(otherVector) — NOT migrated to methodTyped: two genuinely different
            // arg-count shapes dispatched dynamically off args.size() (3 numbers vs. 1 object), which
            // methodTypedN's single fixed arity/codec list per registration can't express. Left untyped.
            .method("add", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() >= 3) return wrap(new Vector3d(v.x + args.get(0).asNum(), v.y + args.get(1).asNum(), v.z + args.get(2).asNum()));
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return wrap(new Vector3d(v.x + other.x, v.y + other.y, v.z + other.z));
                }
                return ScriptValue.NULL;
            })
            // sub(x,y,z) OR sub(otherVector) — same multi-shape dispatch as add() above. Left untyped.
            .method("sub", (obj, args) -> {
                Vector3d v = vec(obj);
                if (args.size() >= 3) return wrap(new Vector3d(v.x - args.get(0).asNum(), v.y - args.get(1).asNum(), v.z - args.get(2).asNum()));
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o) {
                    Vector3d other = vec(o.instance());
                    return wrap(new Vector3d(v.x - other.x, v.y - other.y, v.z - other.z));
                }
                return ScriptValue.NULL;
            })
            // scale(factor?) — a missing factor defaults to 1 and the body still runs, which is
            // precisely the methodTypedOpt1 shape. Return slot stays RAW: it hands back a wrapped
            // Vector object, not one of the native scalar codecs.
            .methodTypedOpt1("scale", TypeCodecs.DOUBLE, 1.0, TypeCodecs.RAW,
                (Vector3d v, Double s) -> wrap(new Vector3d(v.x * s, v.y * s, v.z * s)))
            .methodTyped0("normalize", TypeCodecs.RAW, (Vector3d v) -> {
                double len = v.length();
                if (len == 0) return wrap(new Vector3d(0, 0, 0));
                return wrap(new Vector3d(v.x / len, v.y / len, v.z / len));
            })
            // distance/distance_sq/dot/cross(otherVector) — NOT migrated to methodTyped1: each checks
            // args.size() == 1 EXACTLY (not just "at least 1") before accepting the Vector argument,
            // falling back to a default (0.0/NULL) for any other arg count including MORE than one.
            // methodTypedN's onMissingArgs only expresses a "fewer than N args" floor, not an exact-
            // arity match, so a call with extra args would behave differently (compute a real result
            // instead of the original's default) — a real behavior change. methodTypedOptN is no
            // help either: it explicitly IGNORES extra trailing args. Left untyped.
            //   (A methodTypedOpt2 registration whose SECOND slot is a RAW null-sentinel used purely
            //   as an "extra argument was supplied" flag would in fact be exact — but it buys
            //   nothing: the JIT's typed fast path only fires when the CALL SITE's argument count
            //   equals the declared arity (see ScriptBytecodeCompiler's `argKinds().length ==
            //   arity` test), so every real 1-arg call would fall back to generic dispatch anyway,
            //   exactly as it does today — while the descriptor would advertise a phantom second
            //   parameter this method does not have. Not worth the dishonest signature.)
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
