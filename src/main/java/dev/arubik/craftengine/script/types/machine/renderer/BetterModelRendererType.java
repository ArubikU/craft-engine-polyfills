package dev.arubik.craftengine.script.types.machine.renderer;

import dev.arubik.craftengine.machine.render.renderer.BetterModelRenderer;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

import java.util.ArrayList;
import java.util.List;

/**
 * "BetterModelRenderer" script type — wraps a {@link BetterModelRenderer} entry resolved via
 * {@code Machine.get_renderer(id)} (see {@link MachineType}). Exposes animation playback, bone
 * tinting, and the pseudo bone-rotation/IK support added this session, all backed by BetterModel's
 * real public API (no reflection — {@code kr.toxicity.model.api.*} is a compile-time dependency).
 */
public final class BetterModelRendererType {

    private BetterModelRendererType() {}

    public static void register() {
        PolyTypeRegistry.define("BetterModelRenderer")
            .methodTyped1("play_anim", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (BetterModelRenderer obj, String anim) -> {
                    obj.playLoop(anim);
                    return true;
                })
            .methodTyped0("stop_anim", TypeCodecs.BOOL,
                (BetterModelRenderer obj) -> {
                    obj.stopAnim();
                    return true;
                })
            // bone_location(bone_name) -> Vector (absolute world position) or NULL — object-based
            // counterpart of the "{rendererid}:bm:{bone_name}" string shorthand (see RendererManager
            // #resolveBoneLocation), usable directly inside another renderer's own "location" script
            // expression, e.g. "Machine.get_renderer('arm').bone_location('hand')".
            .methodTyped1("bone_location", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (BetterModelRenderer obj, String bone) -> {
                    double[] pos = obj.boneWorldPosition(bone);
                    return pos == null ? ScriptValue.NULL
                            : dev.arubik.craftengine.script.types.primitive.VectorType.wrap(pos[0], pos[1], pos[2]);
                })
            // set_bone_yaw_pitch(bone_name, yaw_degrees, pitch_degrees) -> bool.
            .methodTyped3("set_bone_yaw_pitch", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE,
                TypeCodecs.BOOL, false,
                (BetterModelRenderer obj, String bone, Double yaw, Double pitch) ->
                    obj.setBoneYawPitch(bone, (float) (double) yaw, (float) (double) pitch))
            // set_bone_tint(bone_name, rgb) -> bool. rgb is a packed 0xRRGGBB int.
            .methodTyped2("set_bone_tint", TypeCodecs.STRING, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (BetterModelRenderer obj, String bone, Double rgb) ->
                    obj.setBoneTint(bone, (int) (double) rgb))
            .methodTyped1("clear_bone_tint", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (BetterModelRenderer obj, String bone) -> obj.clearBoneTint(bone))
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
                List<BetterModelRenderer.BoneRange> chain = parseChain(args.get(0));
                double tx = args.get(1).asNum();
                double ty = args.get(2).asNum();
                double tz = args.get(3).asNum();
                float timeToArrive = args.size() > 4 ? (float) args.get(4).asNum() : 0f;
                return ScriptValue.of(r(obj).playIk(chain, tx, ty, tz, timeToArrive));
            });
    }

    static List<BetterModelRenderer.BoneRange> parseChain(ScriptValue arg) {
        List<BetterModelRenderer.BoneRange> chain = new ArrayList<>();
        if (!(arg instanceof ScriptValue.Array outer)) return chain;
        for (ScriptValue el : outer.elements()) {
            if (!(el instanceof ScriptValue.Array inner) || inner.elements().size() < 5) continue;
            List<ScriptValue> f = inner.elements();
            chain.add(new BetterModelRenderer.BoneRange(
                f.get(0).asStr(),
                (float) f.get(1).asNum(), (float) f.get(2).asNum(),
                (float) f.get(3).asNum(), (float) f.get(4).asNum()));
        }
        return chain;
    }

    public static ScriptValue wrap(BetterModelRenderer renderer) {
        return renderer == null ? ScriptValue.NULL : ScriptValue.ofObj("BetterModelRenderer", renderer);
    }

    private static BetterModelRenderer r(Object obj) { return (BetterModelRenderer) obj; }
}
