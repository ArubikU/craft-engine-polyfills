package dev.arubik.craftengine.script.types.machine.renderer;

import dev.arubik.craftengine.machine.render.renderer.MegRenderer;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;

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
            .method("play_anim", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                r(obj).playLoop(args.get(0).asStr());
                return ScriptValue.of(true);
            })
            .method("stop_anim", (obj, args) -> {
                r(obj).stopAnim();
                return ScriptValue.of(true);
            })
            // bone_location(bone_name) -> Vector (absolute world position) or NULL. Meant to be used
            // directly inside another renderer's own "location" script expression — e.g.
            // "Machine.get_renderer('arm').bone_location('hand')" — as the object-based counterpart
            // of the "{rendererid}:meg:{bone_name}" string shorthand (see RendererManager
            // #resolveBoneLocation): same live bone tracking, usable anywhere a script expression is
            // already evaluated instead of only in a bare location string.
            .method("bone_location", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.NULL;
                double[] pos = r(obj).boneWorldPosition(args.get(0).asStr());
                return pos == null ? ScriptValue.NULL
                        : dev.arubik.craftengine.script.types.primitive.VectorType.wrap(pos[0], pos[1], pos[2]);
            })
            // set_bone_yaw(bone_name, yaw_degrees) -> bool. MEG's public API has no setPitch
            // (confirmed via javap against the real ModelEngine-R4.0.7 jar), so only yaw applies.
            .method("set_bone_yaw", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                return ScriptValue.of(r(obj).setBoneYaw(args.get(0).asStr(), (float) args.get(1).asNum()));
            })
            // play_ik(chain, target_x, target_y, target_z, time_to_arrive_seconds) -> bones applied.
            // chain is an Array of [bone_name, min_yaw, max_yaw, min_pitch, max_pitch] Arrays, base
            // bone first. time_to_arrive_seconds <= 0 snaps instantly instead of easing.
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
