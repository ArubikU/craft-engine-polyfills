package dev.arubik.craftengine.contraption.render;

import java.util.List;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import net.minecraft.network.syncher.SynchedEntityData;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;

/**
 * Applies a contraption's per-contraption uniform {@code scale} (roadmap item #9,
 * {@code ContraptionState#scale()}, clamped {@code [0.1, 10.0]}, default {@code 1.0}) and — since
 * 2026-07-16 — its live model ROTATION (see {@link #applyTo(List, double, Quaternionf)}) to an
 * ALREADY-BUILT {@code Display} metadata value list — i.e. to appearance metadata some OTHER
 * system authored and this project only mirrors, rather than to metadata this project builds from
 * scratch. Extracted (like {@link ContraptionLightEmitters}) so
 * {@link ContraptionBlockEntityElementMirror} (CraftEngine's own {@code entity-renderer} block
 * visuals) and {@link ContraptionFurnitureSwarm} (captured CraftEngine furniture) get byte-for-byte
 * the same sizing rule, without either having to re-derive it.
 *
 * <p><b>Why this exists (2026-07-16 fix — a scaled contraption's entity-renderer/furniture visuals
 * stayed at size 1).</b> {@link ContraptionDisplaySwarm} owns every byte of its own block-cell
 * metadata, so it can simply emit {@code DisplayData.Scale = (s,s,s)} and a {@code -0.5s}
 * recentring translation itself (see {@code ContraptionDisplaySwarm.Cell#metadata}). The two
 * classes above CANNOT: their metadata is whatever CraftEngine's own element/furniture config
 * authored ({@code element.config.metadataValues(player, ...)} /
 * {@code config.metadata.apply(player, ...)}), which may ALREADY carry an authored
 * {@code Scale}/{@code Translation} that must be PRESERVED and merely multiplied — a sofa's
 * item_display authored at {@code Scale=(1.6,1.6,1.6)} on a {@code scale=2} contraption must end up
 * at {@code (3.2,3.2,3.2)}, not at a blindly-overwritten {@code (2,2,2)}. So this multiplies in
 * place instead of appending a blanket override.
 *
 * <p><b>Why {@code Translation} is multiplied too.</b> Confirmed against this project's own
 * {@code mappedServerJar.jar} ({@code com.mojang.math.Transformation#compose} — the same decompile
 * {@code ContraptionDisplaySwarm.Cell#metadata}'s javadoc already cites): a {@code Display}'s 4
 * transform components combine so that a model vertex {@code v} lands at
 * {@code Translation + LeftRotation·(Scale·(RightRotation·v))}. {@code Translation} is therefore an
 * offset in the entity's own un-scaled local space — scaling only {@code Scale} would grow the
 * model correctly but leave its authored offset at 1x, shearing it off its intended spot within a
 * scaled block (e.g. a tank's shell element authored 0.5 blocks above its origin would stay 0.5
 * real blocks up on a {@code scale=3} contraption instead of the 1.5 it must be). Multiplying both
 * is exactly the {@code Translation + Rot·(s·v)} = {@code s·(Translation₁ + Rot·v)} similarity
 * transform, i.e. the whole authored element grows uniformly about the entity position — which is
 * itself already the scale-aware {@code ContraptionMath#renderPosition} projection of the element's
 * local offset about the bearing pivot. The two compose into one rigid, uniformly-scaled structure.
 *
 * <p><b>{@code scale == 1.0} is byte-identical to before this class existed (zero regression).</b>
 * {@link #applyTo} returns IMMEDIATELY at {@code scale == 1.0} without touching, rewriting, or
 * appending a single value — so every normal (never-scaled) contraption's mirrored elements emit the
 * exact same metadata list they always did, including emitting NO {@code Scale} entry at all when
 * the authored config didn't (matching {@code ContraptionDisplaySwarm}'s own scale-1 fast path,
 * which likewise sends nothing because {@code (1,1,1)} is the vanilla default).
 */
public final class ContraptionRenderScale {

    private ContraptionRenderScale() {
    }

    /**
     * Multiplies {@code values}' {@code DisplayData.Scale} and {@code DisplayData.Translation}
     * entries by {@code scale}, IN PLACE. Entries are matched by their real synched-data id
     * ({@code EntityData#id()}, the same id CraftEngine's own {@code addEntityData} stamps onto each
     * {@code SynchedEntityData.DataValue}) rather than by list position, since the authored list's
     * ordering/contents are entirely CraftEngine's business and must not be assumed.
     *
     * <p>An authored entry is REPLACED with a freshly-constructed {@code DataValue} carrying a NEW
     * {@link Vector3f} — never mutated in place: the {@code Vector3f} inside an authored value is
     * frequently the config object's OWN long-lived field (CraftEngine caches element configs across
     * every player and every render), so scaling it destructively would permanently corrupt the real
     * block/furniture's appearance server-wide, for every non-contraption instance of that block too.
     *
     * <p>When no {@code Scale} entry was authored (the common case — {@code (1,1,1)} is the vanilla
     * default, so most configs simply omit it) a {@code (s,s,s)} entry is APPENDED. A missing
     * {@code Translation} needs no such treatment: its default is {@code (0,0,0)} and
     * {@code 0·s == 0}, so the absent entry is already correct at any scale.
     *
     * <p>Non-{@code DataValue} / non-{@code Vector3f} entries are skipped defensively — this walks a
     * list CraftEngine built, and a future CraftEngine could put something unexpected in it; a
     * surprise here must degrade to "this element isn't resized", never to a thrown exception that
     * blanks the whole swarm's rendering.
     *
     * <p>Scale-only shorthand for {@link #applyTo(List, double, Quaternionf)} — every word above still
     * applies; see there for the model-ROTATION half and why the two share one pass.
     */
    public static void applyTo(List<Object> values, double scale) {
        applyTo(values, scale, null);
    }

    /**
     * {@link #applyTo(List, double)} plus a model ROTATION: composes {@code rotation} INTO the authored
     * transform, so a mirrored element's model tips/turns with the contraption's live pose instead of
     * standing stubbornly upright inside a tilted body (2026-07-16 fix — reported as "el furniture render
     * no se mueve con el pitch/yaw etc o sea siempre queda recto"). {@code rotation} {@code null} means
     * "no rotation to compose" and skips the rotation half entirely — see {@link #applyTo(List, double)}
     * and the identity-pose note below.
     *
     * <p><b>The composition, and WHY this order.</b> Confirmed against this project's own
     * {@code mappedServerJar.jar} and already cited by {@code ContraptionDisplaySwarm.Cell#metadata}'s
     * javadoc ({@code com.mojang.math.Transformation#compose}), a {@code Display}'s 4 transform components
     * place a model vertex {@code v} at {@code Translation + LeftRotation·(Scale·(RightRotation·v))}.
     * Rotating that ENTIRE authored result by {@code R} — i.e. rotating the finished element rigidly about
     * the entity's own position, which is exactly what "this element sits inside a body that leaned by
     * {@code R}" means — is:
     *
     * <pre>R·(Translation + LeftRotation·(Scale·(RightRotation·v)))
     *   = (R·Translation) + (R·LeftRotation)·(Scale·(RightRotation·v))</pre>
     *
     * <p>So {@code R} is LEFT-multiplied onto {@code LeftRotation} ({@code R} applied AFTER/OUTSIDE the
     * authored rotation, never {@code LeftRotation·R}, which would instead re-interpret the body lean in
     * the element's own already-rotated model frame and shear a rotated element off its lean), and
     * {@code Translation} — an offset in the entity's own space, per this class's own "Why
     * {@code Translation} is multiplied too" javadoc — is rotated by the same {@code R}. {@code Scale} and
     * {@code RightRotation} are deliberately NOT touched: they sit INSIDE {@code LeftRotation} in the
     * compose chain, so the identity above already carries them along untouched. {@code Scale} is uniform,
     * so the scale and rotation halves commute and may share this one pass ({@code R·(s·Tr) == s·(R·Tr)}).
     *
     * <p><b>A missing {@code Translation} needs no work</b> (default {@code (0,0,0)}, and
     * {@code R·0 == 0}). A missing {@code LeftRotation} — the common case, since CraftEngine emits it via
     * {@code addEntityDataIfNotDefaultValue} and most elements author no {@code rotation:} at all — is
     * APPENDED as plain {@code R}, which is exactly {@code R·identity}.
     *
     * <p><b>ITEM/TEXT displays only — verified, not assumed.</b> {@code LeftRotation} is honoured by
     * {@code ITEM_DISPLAY}: proven in this codebase by {@link ContraptionPistonShaftSwarm}, which rotates
     * its shaft head purely through it. A {@code BLOCK_DISPLAY} appears to IGNORE it (see
     * {@code ContraptionDisplaySwarm}'s own "ROTATION VIA ENTITY YAW" javadoc for that investigation) —
     * which is not a problem here, because the only callers of this method are the two mirrors' item/text
     * display cells; captured BLOCKS never route through this class.
     *
     * <p><b>Identity pose stays byte-identical.</b> {@code scale == 1.0 && rotation == null} returns
     * immediately without touching, rewriting, or appending a single value. Callers are responsible for
     * passing {@code null} rather than an identity quaternion at an un-tilted pose (they all early-return
     * on the exact-zero pose — see {@code ContraptionFurnitureSwarm.Cell#modelRotation}); that keeps the
     * fast path a structural guarantee instead of one resting on {@code Q⁻¹·Q} coming back
     * bit-exactly identity in {@code float} math, which it need not.
     */
    public static void applyTo(List<Object> values, double scale, Quaternionf rotation) {
        if (scale == 1.0 && rotation == null) {
            return; // fast path — byte-for-byte the pre-scale/pre-rotation metadata (see class javadoc)
        }
        boolean scaled = scale != 1.0;
        float s = (float) scale;
        int scaleId = DisplayData.Scale.id();
        int translationId = DisplayData.Translation.id();
        int leftRotationId = DisplayData.LeftRotation.id();
        boolean sawScale = false;
        boolean sawLeftRotation = false;
        for (int i = 0; i < values.size(); i++) {
            if (!(values.get(i) instanceof SynchedEntityData.DataValue<?> dv)) {
                continue;
            }
            // LeftRotation's serializer is QUATERNION (its NMS accessor is typed Quaternionfc), so match
            // the read-only interface rather than the concrete Quaternionf CraftEngine happens to author.
            if (dv.id() == leftRotationId && dv.value() instanceof Quaternionfc authored) {
                sawLeftRotation = true;
                if (rotation != null) {
                    values.set(i, DisplayData.LeftRotation.createEntityData(
                            new Quaternionf(rotation).mul(authored)));
                }
                continue;
            }
            if (!(dv.value() instanceof Vector3f v)) {
                continue;
            }
            if (dv.id() == scaleId) {
                if (scaled) {
                    values.set(i, DisplayData.Scale.createEntityData(new Vector3f(v.x * s, v.y * s, v.z * s)));
                }
                sawScale = true;
            } else if (dv.id() == translationId) {
                Vector3f t = scaled ? new Vector3f(v.x * s, v.y * s, v.z * s) : new Vector3f(v);
                if (rotation != null) {
                    rotation.transform(t); // R·(s·Translation) — see javadoc; uniform scale commutes
                }
                values.set(i, DisplayData.Translation.createEntityData(t));
            }
        }
        if (scaled && !sawScale) {
            DisplayData.Scale.addEntityData(new Vector3f(s, s, s), values);
        }
        if (rotation != null && !sawLeftRotation) {
            // No authored rotation (the common case — CraftEngine omits the default identity), so the
            // composition R·identity is just R itself. Copied, never aliased: `rotation` is the caller's.
            DisplayData.LeftRotation.addEntityData(new Quaternionf(rotation), values);
        }
    }
}
