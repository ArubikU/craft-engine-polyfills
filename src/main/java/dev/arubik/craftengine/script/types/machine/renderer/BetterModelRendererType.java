package dev.arubik.craftengine.script.types.machine.renderer;

import dev.arubik.craftengine.machine.render.renderer.BetterModelRenderer;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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
            .method("play_anim", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                r(obj).playLoop(args.get(0).asStr());
                return ScriptValue.of(true);
            })
            .method("stop_anim", (obj, args) -> {
                r(obj).stopAnim();
                return ScriptValue.of(true);
            })
            // bone_location(bone_name) -> Vector (absolute world position) or NULL — object-based
            // counterpart of the "{rendererid}:bm:{bone_name}" string shorthand (see RendererManager
            // #resolveBoneLocation), usable directly inside another renderer's own "location" script
            // expression, e.g. "Machine.get_renderer('arm').bone_location('hand')".
            .method("bone_location", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                double[] pos = r(obj).boneWorldPosition(args.get(0).asStr());
                return pos == null ? ScriptValue.NULL
                        : dev.arubik.craftengine.script.types.primitive.VectorType.wrap(pos[0], pos[1], pos[2]);
            })
            // set_bone_yaw_pitch(bone_name, yaw_degrees, pitch_degrees) -> bool.
            .method("set_bone_yaw_pitch", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                return ScriptValue.of(r(obj).setBoneYawPitch(
                    args.get(0).asStr(), (float) args.get(1).asNum(), (float) args.get(2).asNum()));
            })
            // set_bone_tint(bone_name, rgb) -> bool. rgb is a packed 0xRRGGBB int.
            .method("set_bone_tint", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                return ScriptValue.of(r(obj).setBoneTint(args.get(0).asStr(), (int) args.get(1).asNum()));
            })
            .method("clear_bone_tint", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                return ScriptValue.of(r(obj).clearBoneTint(args.get(0).asStr()));
            })
            // play_ik(chain, target_x, target_y, target_z, time_to_arrive_seconds) -> bones applied.
            // chain is an Array of [bone_name, min_yaw, max_yaw, min_pitch, max_pitch] Arrays, base
            // bone first. time_to_arrive_seconds <= 0 snaps instantly instead of easing.
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
