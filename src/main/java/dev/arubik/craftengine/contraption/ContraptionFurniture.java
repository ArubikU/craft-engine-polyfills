package dev.arubik.craftengine.contraption;

import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.util.Key;

/**
 * One CraftEngine furniture piece captured into a contraption's glued footprint at assembly
 * time — a real "cell", exactly like a captured block.
 *
 * <p><b>Architecture (this session — "el furniture sigue sin moverse... crea el furniture real
 * en el contraption. luego usar las cells para mover la repr visual").</b> An EARLIER version of
 * this class destroyed the real furniture entirely at capture time and kept only static
 * {@code definitionId}/{@code variantName} config, rebuilding a from-scratch packet mirror every
 * tick with no live backing object at all — see the (now superseded) design notes previously
 * here for why an even-earlier "reposition the real furniture every tick via
 * {@code Furniture#moveTo}" approach was abandoned ({@code moveTo} doesn't rotate smoothly under
 * continuous per-tick calls). That "destroy it, rebuild from config" approach traded away every
 * bit of LIVE state (behaviors, custom data, interaction-driven changes) and turned out to still
 * not fix the reported "furniture doesn't move" symptom — the real root cause was a missing
 * Display interpolation-duration metadata field on the packet mirror (see
 * {@code render.ContraptionFurnitureSwarm}), unrelated to whether a live object backs it.
 *
 * <p>This class now takes the SAME approach blocks already use: {@code liveFurniture} is a real,
 * live {@code BukkitFurniture} instance genuinely placed (via
 * {@code CraftEngineFurniture#place}) INSIDE the contraption's own hidden
 * {@code dev.arubik.craftengine.contraption.level.ContraptionLevel} — a real dimension nothing
 * in the real world ever sees directly (mirrors exactly how a captured {@code BlockState}
 * genuinely lives inside that same hidden level; see that class's own javadoc, "Real vs. fake
 * positions"). Its position inside that level is fixed at capture time (the level's own local
 * block-position address space, 1:1 with {@code localOffset} rounded/placed at capture) and
 * never itself moves — {@code render.ContraptionFurnitureSwarm} reads this LIVE object's current
 * {@code hitboxes()}/{@code elements()} every tick and redirects packet-only mirror entities in
 * the REAL world to match, through {@code ContraptionMath#renderPosition}, exactly the same
 * "real object stays put in a stable local space, only the packet mirror gets repositioned"
 * technique {@code render.ContraptionDisplaySwarm} already uses for blocks.
 *
 * <p>{@code localOffset} is the furniture's continuous position relative to the bearing's
 * origin AT CAPTURE TIME (yaw is always 0 at capture — a fresh {@code ContraptionState} always
 * starts at yaw 0), so no inverse-rotation is needed when computing it — this is ALSO the exact
 * local position {@code liveFurniture} was placed at inside the {@code ContraptionLevel} (local
 * offsets map 1:1 onto that level's own block positions, per its own javadoc).
 * {@code yawOffsetDegrees} is the furniture's own facing relative to the bearing at capture
 * time, added back to the bearing's live yaw every tick so the furniture visibly turns with a
 * rotating contraption. {@code definitionId}/{@code variantName} are kept too (not just derived
 * from {@code liveFurniture}) so a swarm rebuild/fallback path never needs to re-query the live
 * object just to know what it originally was, and so restore-on-disassemble still works even if
 * {@code liveFurniture} was already invalidated/destroyed for some other reason.
 */
public record ContraptionFurniture(Key definitionId, String variantName, Vec3 localOffset, float yawOffsetDegrees,
        BukkitFurniture liveFurniture) {

    /** True if {@link #liveFurniture} is still a valid, non-destroyed CraftEngine furniture instance. */
    public boolean hasLiveFurniture() {
        return liveFurniture != null && liveFurniture.isValid();
    }
}
