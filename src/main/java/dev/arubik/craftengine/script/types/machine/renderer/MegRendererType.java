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
            // Typed with a null sentinel on the LAST required slot (target_z): no codec decodes a
            // PRESENT argument to Java null (asNum() is primitive-backed, boxed only on return) and
            // arguments are positional, so `tzArg == null` is exactly the old `args.size() < 4`
            // early return, taken before the renderer is touched. The 5th keeps its 0 default.
            // Return codec is RAW so the ScriptValue.of(...) encoding of both paths is unchanged.
            .methodTypedOpt5("play_ik", TypeCodecs.RAW, null, TypeCodecs.DOUBLE, null,
                TypeCodecs.DOUBLE, null, TypeCodecs.DOUBLE, null, TypeCodecs.DOUBLE, 0.0, TypeCodecs.RAW,
                (MegRenderer r, ScriptValue chainArg, Double txArg, Double tyArg, Double tzArg,
                 Double timeArg) -> {
                if (tzArg == null) return ScriptValue.of(0);
                List<MegRenderer.BoneRange> chain = parseChain(chainArg);
                float timeToArrive = (float) (double) timeArg;
                return ScriptValue.of(r.playIk(chain, txArg, tyArg, tzArg, timeToArrive));
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
}
