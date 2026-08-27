package dev.arubik.craftengine.script.types.machine;

import dev.arubik.craftengine.machine.render.ScriptAnimation;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;

import java.util.*;

/**
 * "Animation" script type.
 *
 * Construct with Animation(), define keyframes, then hand to a machine:
 *
 *   anim = Animation()
 *   anim.keyframe(0, { "id": "gem", "type": "item_display", "item": "minecraft:diamond",
 *                       "pos": [0, 0.5, 0], "scale": 0.3 })
 *   anim.keyframe(20, { "id": "gem", "pos": [0, 1.5, 0], "scale": 0.5,
 *                        "rot": [0, 180, 0] }, "expo_out")
 *   anim.loop(true).speed(0.5).interpolation(3)
 *   anim.sound_at(0, "minecraft:block.note_block.harp", 1.0, 1.2)
 *   anim.on_end("finish.pf:done")
 *   Machine.play_animation(anim)
 *
 * SUPPORTED TYPES:
 *   item_display   — item, pos, rot, scale, scale_x/y/z, pivot, billboard, glow,
 *                    brightness, view_range, shadow_radius, shadow_strength
 *   text_display   — text (MiniMessage + {expr}), text_bg (ARGB), text_opacity,
 *                    text_shadow, text_see_through, text_alignment, text_line_width
 *   block_display  — block (block state string, supports {expr})
 *   armor_stand    — head/chest/legs/feet/mainhand/offhand (item IDs), small, invisible, marker, yaw
 *   bettermodel    — model_id, model_animation, model_speed, yaw
 *   modelengine    — model_id, model_animation, model_speed, yaw
 *   particle       — particle (type id), count, spread [x,y,z], particle_speed, dir [x,y,z], shape
 *
 * All string fields (item, text, block, model_id, etc.) support {expr} scripting
 * evaluated with the machine's ScriptContext each tick.
 * Text fields additionally parse the resolved string through MiniMessage.
 *
 * EASING (3rd arg to keyframe):
 *   linear, quad_in, quad_out, quad_in_out,
 *   cubic_in, cubic_out, cubic_in_out,
 *   quart_in, quart_out, quart_in_out,
 *   sine_in, sine_out, sine_in_out,
 *   expo_in, expo_out, expo_in_out,
 *   circ_in, circ_out, circ_in_out,
 *   back_in, back_out, back_in_out,
 *   elastic_in, elastic_out, elastic_in_out,
 *   bounce_in, bounce_out, bounce_in_out,
 *   step_start, step_end
 */
public final class AnimationType {

    private AnimationType() {}

    public static void register() {
        PolyTypeRegistry.define("Animation")

            // ---- Keyframe definition ----
            // keyframe(tick, displays, easing?) — short-circuits (return false) only below 2 args,
            // but the 3rd (easing) is separately optional with its own in-body default (LINEAR);
            // methodTyped2/3 can't express "2 required + 1 optional with a different missing-args
            // rule than the required ones" — a fixed arity decodes exactly N args or short-circuits
            // for all of them uniformly. Left untyped.
            .method("keyframe", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                int tick = (int) args.get(0).asNum();
                ScriptAnimation.Easing easing = args.size() >= 3
                    ? ScriptAnimation.Easing.parse(args.get(2).asStr())
                    : ScriptAnimation.Easing.LINEAR;
                Map<String, ScriptAnimation.DisplayState> displays = parseDisplays(args.get(1));
                if (!displays.isEmpty()) anim(obj).addKeyframe(tick, displays, easing);
                return ScriptValue.ofObj("Animation", obj);
            })

            // ---- Playback ----
            // play/stop/pause/resume/reset — migrated to the typed-registration API
            // (PolyType.methodTyped0): all zero-arg, no missing-args case exists at that arity, and
            // the `anim(obj)` helper is a plain (ScriptAnimation) cast so it maps directly to the
            // typed handler's first parameter type.
            .methodTyped0("play", TypeCodecs.RAW, (ScriptAnimation a) -> {
                a.play();
                return ScriptValue.ofObj("Animation", a);
            })
            // play_from/seek take an OPTIONAL arg that defaults in-body (args.isEmpty() ? 0 : ...)
            // rather than short-circuiting to a fixed return value when absent — methodTyped1's
            // onMissingArgs can only supply a static fallback RETURN value, not a default argument,
            // and the actual return here is a dynamic ScriptValue.ofObj(obj) that can't be
            // precomputed at registration time. Left untyped.
            .method("play_from", (obj, args) -> {
                anim(obj).playFrom(args.isEmpty() ? 0 : (int) args.get(0).asNum());
                return ScriptValue.ofObj("Animation", obj);
            })
            .methodTyped0("stop", TypeCodecs.BOOL, (ScriptAnimation a) -> { a.stop(); return false; })
            .methodTyped0("pause", TypeCodecs.RAW, (ScriptAnimation a) -> { a.pause(); return ScriptValue.ofObj("Animation", a); })
            .methodTyped0("resume", TypeCodecs.RAW, (ScriptAnimation a) -> { a.resume(); return ScriptValue.ofObj("Animation", a); })
            .method("seek",   (obj, args) -> {
                anim(obj).seek(args.isEmpty() ? 0 : (int) args.get(0).asNum());
                return ScriptValue.ofObj("Animation", obj);
            })
            .methodTyped0("reset", TypeCodecs.RAW, (ScriptAnimation a) -> { a.reset(); return ScriptValue.ofObj("Animation", a); })

            // ---- Configuration ----
            // loop/max_loops/speed/interpolation, and on_end/on_loop/add_child/remove_child below,
            // all share the same shape: a single OPTIONAL arg (missing -> skip the setter, or use
            // an in-body default) but ALWAYS return ScriptValue.ofObj("Animation", obj) — a value
            // that depends on the live `obj` instance and so can't be expressed as a static
            // methodTyped1 onMissingArgs fallback (that only substitutes a fixed return value, and
            // would also skip invoking the handler entirely rather than running it with a default
            // arg). Left untyped.
            .method("loop", (obj, args) -> {
                anim(obj).setLoop(args.isEmpty() || args.get(0).asBool());
                return ScriptValue.ofObj("Animation", obj);
            })
            .method("max_loops", (obj, args) -> {
                if (!args.isEmpty()) anim(obj).setMaxLoops((int) args.get(0).asNum());
                return ScriptValue.ofObj("Animation", obj);
            })
            .method("speed", (obj, args) -> {
                if (!args.isEmpty()) anim(obj).setSpeed((float) args.get(0).asNum());
                return ScriptValue.ofObj("Animation", obj);
            })
            // interpolation(ticks) — NMS client-side smooth interpolation
            .method("interpolation", (obj, args) -> {
                if (!args.isEmpty()) anim(obj).setInterpolationDuration((int) args.get(0).asNum());
                return ScriptValue.ofObj("Animation", obj);
            })

            // ---- Sound events ----
            // sound_at(tick, sound, volume?, pitch?) — 2 required + 2 further optional args beyond
            // them (each independently defaulting to 1.0f in-body); methodTyped2 would decode only
            // the first 2 and has no way to conditionally read args 2/3. Left untyped.
            .method("sound_at", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                anim(obj).addSoundAt((int) args.get(0).asNum(), new ScriptAnimation.SoundSpec(
                    args.get(1).asStr(),
                    args.size() >= 3 ? (float) args.get(2).asNum() : 1.0f,
                    args.size() >= 4 ? (float) args.get(3).asNum() : 1.0f));
                return ScriptValue.ofObj("Animation", obj);
            })

            // ---- Event callbacks ----
            // Same optional-arg-with-obj-dependent-return shape as loop/max_loops/speed above.
            .method("on_end", (obj, args) -> {
                if (!args.isEmpty()) anim(obj).setOnEndCall(ScriptCall.parse(args.get(0).asStr()));
                return ScriptValue.ofObj("Animation", obj);
            })
            .method("on_loop", (obj, args) -> {
                if (!args.isEmpty()) anim(obj).setOnLoopCall(ScriptCall.parse(args.get(0).asStr()));
                return ScriptValue.ofObj("Animation", obj);
            })

            // ---- Child animations ----
            // Same optional-arg-with-obj-dependent-return shape, plus the arg is only acted on
            // after an `instanceof ScriptValue.Obj` + typeName check (not a plain codec decode).
            .method("add_child", (obj, args) -> {
                if (!args.isEmpty() && args.get(0) instanceof ScriptValue.Obj o
                        && o.typeName().equals("Animation"))
                    anim(obj).addChild((ScriptAnimation) o.instance());
                return ScriptValue.ofObj("Animation", obj);
            })
            .method("remove_child", (obj, args) -> {
                if (!args.isEmpty() && args.get(0) instanceof ScriptValue.Obj o
                        && o.typeName().equals("Animation"))
                    anim(obj).removeChild((ScriptAnimation) o.instance());
                return ScriptValue.ofObj("Animation", obj);
            })

            // ---- Properties ----
            .property("playing",  obj -> ScriptValue.of(anim(obj).isPlaying()))
            .property("paused",   obj -> ScriptValue.of(anim(obj).isPaused()))
            .property("looping",  obj -> ScriptValue.of(anim(obj).isLooping()))
            .property("tick",     obj -> ScriptValue.of((double) anim(obj).currentTick()))
            .property("duration", obj -> ScriptValue.of((double) anim(obj).duration()))
            .property("progress", obj -> ScriptValue.of((double) anim(obj).progress()))
            .property("speed",    obj -> ScriptValue.of((double) anim(obj).speed()));
    }

    // ===================================================================
    // FACTORY
    // ===================================================================

    public static ScriptValue create() { return ScriptValue.ofObj("Animation", new ScriptAnimation()); }

    // ===================================================================
    // INTERNAL HELPERS
    // ===================================================================

    private static ScriptAnimation anim(Object obj) { return (ScriptAnimation) obj; }

    private static Map<String, ScriptAnimation.DisplayState> parseDisplays(ScriptValue val) {
        Map<String, ScriptAnimation.DisplayState> out = new LinkedHashMap<>();
        if (val instanceof ScriptValue.Array arr) {
            for (ScriptValue elem : arr.elements()) {
                ScriptAnimation.DisplayState s = parseOne(elem);
                if (s != null) out.put(idOf(elem, out.size()), s);
            }
        } else {
            ScriptAnimation.DisplayState s = parseOne(val);
            if (s != null) out.put(idOf(val, 0), s);
        }
        return out;
    }

    private static String idOf(ScriptValue val, int fallbackIdx) {
        String id = val.getProperty("id").asStr();
        return (id != null && !id.isBlank()) ? id : "display_" + fallbackIdx;
    }

    private static ScriptAnimation.DisplayState parseOne(ScriptValue val) {
        if (!(val instanceof ScriptValue.Obj)) return null;
        ScriptAnimation.DisplayState.Builder b = new ScriptAnimation.DisplayState.Builder();

        // Type
        String type = str(val, "type", null);
        if (type != null) b.type(type);

        // item_display
        String item = str(val, "item", null);
        if (item != null) b.item(item);

        // text_display
        String text = str(val, "text", null);
        if (text != null) b.text(text);

        String blockId = firstStr(val, "block", "block_id", "block_state");
        if (blockId != null) b.blockId(blockId);

        // armor_stand fields
        String asHead = str(val, "head", str(val, "head_item", null));
        if (asHead != null) b.asHead(asHead);
        String asChest = firstStr(val, "chest", "chest_item", "chestplate");
        if (asChest != null) b.asChest(asChest);
        String asLegs = firstStr(val, "legs", "leggings", "legs_item");
        if (asLegs != null) b.asLegs(asLegs);
        String asFeet = firstStr(val, "feet", "boots", "feet_item");
        if (asFeet != null) b.asFeet(asFeet);
        String asMain = firstStr(val, "main_hand", "mainhand", "hand");
        if (asMain != null) b.asMainHand(asMain);
        String asOff = firstStr(val, "off_hand", "offhand");
        if (asOff != null) b.asOffHand(asOff);
        ScriptValue smallV = val.getProperty("small");
        if (!(smallV instanceof ScriptValue.Null)) b.asSmall(smallV.asBool());
        ScriptValue invV = val.getProperty("invisible");
        if (!(invV instanceof ScriptValue.Null)) b.asInvisible(invV.asBool());
        ScriptValue markV = val.getProperty("marker");
        if (!(markV instanceof ScriptValue.Null)) b.asMarker(markV.asBool());

        // model (bettermodel / modelengine)
        String modelId = firstStr(val, "model_id", "model", "modelid");
        if (modelId != null) b.modelId(modelId);
        String modelAnim = firstStr(val, "model_animation", "animation", "anim");
        if (modelAnim != null) b.modelAnimation(modelAnim);
        float modelSpeed = num(val, "model_speed", 0);
        if (modelSpeed > 0) b.modelSpeed(modelSpeed);

        // particle fields
        String ptType = firstStr(val, "particle", "particle_type");
        if (ptType != null) b.particleType(ptType);
        int ptCount = (int) val.getProperty("count").asNum();
        if (ptCount > 0) b.particleCount(ptCount);
        float[] ptSpread = vec3Of(val, "spread", null, null);
        if (ptSpread[0] != 0 || ptSpread[1] != 0 || ptSpread[2] != 0)
            b.particleSpread(ptSpread[0], ptSpread[1], ptSpread[2]);
        float ptSpeed = num(val, "particle_speed", 0);
        if (ptSpeed > 0) b.particleSpeed(ptSpeed);
        float[] ptDir = vec3Of(val, "dir", "direction", null);
        if (ptDir[0] != 0 || ptDir[1] != 0 || ptDir[2] != 0)
            b.particleDir(ptDir[0], ptDir[1], ptDir[2]);
        String ptShape = str(val, "shape", null);
        if (ptShape != null) b.particleShape(ptShape);

        // Transform — position
        float[] pos = vec3Of(val, "pos", "position", "location");
        b.pos(pos[0], pos[1], pos[2]);

        // Rotation
        float[] rot = vec3Of(val, "rot", "rotation", "euler");
        b.rot(rot[0], rot[1], rot[2]);

        // Scale — uniform or per-axis or array
        ScriptValue scaleVal = val.getProperty("scale");
        if (scaleVal instanceof ScriptValue.Array scaleArr) {
            List<ScriptValue> se = scaleArr.elements();
            float sx = se.size() > 0 ? (float) se.get(0).asNum() : 1;
            float sy = se.size() > 1 ? (float) se.get(1).asNum() : sx;
            float sz = se.size() > 2 ? (float) se.get(2).asNum() : sx;
            b.scale(sx, sy, sz);
        } else {
            float su = (float) scaleVal.asNum();
            float sx = num(val, "scale_x", su <= 0 ? 1 : su);
            float sy = num(val, "scale_y", su <= 0 ? 1 : su);
            float sz = num(val, "scale_z", su <= 0 ? 1 : su);
            b.scale(sx, sy, sz);
        }

        // Yaw (for armor_stand, bettermodel, modelengine)
        float yaw = num(val, "yaw", 0);
        b.yaw(yaw);

        // Pivot
        float[] pivot = vec3Of(val, "pivot", null, null);
        b.pivot(pivot[0], pivot[1], pivot[2]);

        // Billboard / glow / brightness
        String billboard = str(val, "billboard", null);
        if (billboard != null) b.billboard(billboard);

        ScriptValue glowV = val.getProperty("glow");
        if (!(glowV instanceof ScriptValue.Null)) b.glow(glowV.asBool());

        ScriptValue brightV = val.getProperty("brightness");
        if (brightV instanceof ScriptValue.Array ba) {
            List<ScriptValue> be = ba.elements();
            b.brightness(be.size() > 0 ? (int) be.get(0).asNum() : -1,
                         be.size() > 1 ? (int) be.get(1).asNum() : -1);
        } else {
            int bBlock = (int) val.getProperty("brightness_block").asNum();
            int bSky   = (int) val.getProperty("brightness_sky").asNum();
            if (bBlock > 0 || bSky > 0) b.brightness(bBlock, bSky);
        }

        float vr = num(val, "view_range", 0);
        if (vr > 0) b.viewRange(vr);
        float sr = num(val, "shadow_radius", -1);
        if (sr >= 0) b.shadowRadius(sr);
        float ss = num(val, "shadow_strength", -1);
        if (ss >= 0) b.shadowStrength(ss);

        // text_display specific
        ScriptValue tbg = val.getProperty("text_bg");
        if (!(tbg instanceof ScriptValue.Null)) b.textBg((int) tbg.asNum());
        float to = num(val, "text_opacity", -1);
        if (to >= 0) b.textOpacity(to);
        ScriptValue tshadow = val.getProperty("text_shadow");
        if (!(tshadow instanceof ScriptValue.Null)) b.textShadow(tshadow.asBool());
        ScriptValue tst = val.getProperty("text_see_through");
        if (!(tst instanceof ScriptValue.Null)) b.textSeeThrough(tst.asBool());
        String talign = firstStr(val, "text_alignment", "text_align", "alignment");
        if (talign != null) b.textAlignment(talign);
        int tlw = (int) val.getProperty("text_line_width").asNum();
        if (tlw > 0) b.textLineWidth(tlw);

        return b.build();
    }

    // ---- Parsing utils ----

    private static String str(ScriptValue obj, String key, String def) {
        ScriptValue v = obj.getProperty(key);
        if (v instanceof ScriptValue.Null) return def;
        String s = v.asStr();
        return (s == null || s.isBlank()) ? def : s;
    }

    private static String firstStr(ScriptValue obj, String... keys) {
        for (String k : keys) {
            String s = str(obj, k, null);
            if (s != null) return s;
        }
        return null;
    }

    private static float num(ScriptValue obj, String key, float def) {
        ScriptValue v = obj.getProperty(key);
        if (v instanceof ScriptValue.Null) return def;
        if (!(v instanceof ScriptValue.Num)) return def;
        return (float) v.asNum();
    }

    private static float[] vec3Of(ScriptValue obj, String key1, String key2, String key3) {
        for (String key : new String[]{ key1, key2, key3 }) {
            if (key == null) continue;
            ScriptValue v = obj.getProperty(key);
            if (v instanceof ScriptValue.Array arr) {
                List<ScriptValue> e = arr.elements();
                return new float[]{
                    e.size() > 0 ? (float) e.get(0).asNum() : 0,
                    e.size() > 1 ? (float) e.get(1).asNum() : 0,
                    e.size() > 2 ? (float) e.get(2).asNum() : 0
                };
            }
        }
        return new float[]{ 0, 0, 0 };
    }
}
