package dev.arubik.craftengine.script.types.machine.renderer;

import dev.arubik.craftengine.machine.render.renderer.MegRenderer;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

import java.util.ArrayList;
import java.util.List;

/**
 * "MegRenderer" script type — wraps a {@link MegRenderer} entry resolved via
 * {@code Machine.get_renderer(id)} (see {@link MachineType}). Exposes ModelEngine animation
 * playback plus the pseudo bone-rotation/IK support added this session (MEG has no real IK solver
 * on its public API — see {@link MegRenderer#playIk}'s own doc for why this is a simulated
 * successive-clamp distribution, not a true chain solver).
 */
public final class MegRendererType {

    private MegRendererType() {}

    public static void register() {
        PolyTypeRegistry.define("MegRenderer")
            .methodTyped1("play_anim", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (MegRenderer r, String name) -> {
                    r.playLoop(name);
                    return true;
                })
            .methodTyped0("stop_anim", TypeCodecs.BOOL,
                (MegRenderer r) -> {
                    r.stopAnim();
                    return true;
                })
            // bone_location(bone_name) -> Vector (absolute world position) or NULL. Meant to be used
            // directly inside another renderer's own "location" script expression — e.g.
            // "Machine.get_renderer('arm').bone_location('hand')" — as the object-based counterpart
            // of the "{rendererid}:meg:{bone_name}" string shorthand (see RendererManager
            // #resolveBoneLocation): same live bone tracking, usable anywhere a script expression is
            // already evaluated instead of only in a bare location string.
            // Return is dynamic (Vector or NULL) -> TypeCodecs.RAW.
            .methodTyped1("bone_location", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (MegRenderer r, String bone) -> {
                    double[] pos = r.boneWorldPosition(bone);
                    return pos == null ? ScriptValue.NULL
                            : dev.arubik.craftengine.script.types.primitive.VectorType.wrap(pos[0], pos[1], pos[2]);
                })
            // set_bone_yaw(bone_name, yaw_degrees) -> bool. MEG's public API has no setPitch
            // (confirmed via javap against the real ModelEngine-R4.0.7 jar), so only yaw applies.
            .methodTyped2("set_bone_yaw", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (MegRenderer r, String bone, Double yawDeg) -> r.setBoneYaw(bone, yawDeg.floatValue()))
            // play_ik(chain, target_x, target_y, target_z, time_to_arrive_seconds) -> bones applied.
            // chain is an Array of [bone_name, min_yaw, max_yaw, min_pitch, max_pitch] Arrays, base
            // bone first. time_to_arrive_seconds <= 0 snaps instantly instead of easing.
            // NOT migrated: mixed required/optional arity — the first 4 args are REQUIRED (a shorter
            // call returns 0 without touching the renderer) while the 5th (time_to_arrive_seconds)
            // is optional, read via args.size() > 4 with a 0f default. methodTyped5's onMissingArgs
            // can't supply that per-argument default, and methodTypedOpt5 would make all five
            // optional and still run the body — firing a real playIk() with an empty chain at
            // (0,0,0) on a call that today does nothing at all. Left untyped.
            .method("play_ik", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(0);
                List<MegRenderer.BoneRange> chain = parseChain(args.get(0));
                double tx = args.get(1).asNum();
                double ty = args.get(2).asNum();
                double tz = args.get(3).asNum();
                float timeToArrive = args.size() > 4 ? (float) args.get(4).asNum() : 0f;
                return ScriptValue.of(r(obj).playIk(chain, tx, ty, tz, timeToArrive));
            });
    }

    static List<MegRenderer.BoneRange> parseChain(ScriptValue arg) {
        List<MegRenderer.BoneRange> chain = new ArrayList<>();
        if (!(arg instanceof ScriptValue.Array outer)) return chain;
        for (ScriptValue el : outer.elements()) {
            if (!(el instanceof ScriptValue.Array inner) || inner.elements().size() < 5) continue;
            List<ScriptValue> f = inner.elements();
            chain.add(new MegRenderer.BoneRange(
                f.get(0).asStr(),
                (float) f.get(1).asNum(), (float) f.get(2).asNum(),
                (float) f.get(3).asNum(), (float) f.get(4).asNum()));
        }
        return chain;
    }

    public static ScriptValue wrap(MegRenderer renderer) {
        return renderer == null ? ScriptValue.NULL : ScriptValue.ofObj("MegRenderer", renderer);
    }

    private static MegRenderer r(Object obj) { return (MegRenderer) obj; }
}
