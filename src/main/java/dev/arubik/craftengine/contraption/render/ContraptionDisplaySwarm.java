package dev.arubik.craftengine.contraption.render;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Quaternionf;

import dev.arubik.craftengine.contraption.ContraptionMath;
import dev.arubik.craftengine.contraption.level.ContraptionLevel;
import dev.arubik.craftengine.util.MNms;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;

/**
 * Packet-only {@code BLOCK_DISPLAY} swarm rendering a {@link ContraptionLevel}'s captured
 * blocks (CONTRAPTIONS.md §5 Phase 2) — one fake entity per captured cell, shaped exactly
 * like {@code ConveyorItemDisplay} (spawn/reposition/despawn via {@code MNms}-constructed
 * packets, per-viewer {@code shownTo} tracking) but rendering real block appearances via
 * {@link DisplayData.BlockDisplayData#BlockState} instead of an item.
 *
 * <p>Phase 2 scope: stationary. {@link #render} always recomputes each cell's position from
 * {@code bearingWorldPos}/{@code yawRadians}, so calling it every tick already supports
 * movement/rotation once Phase 3 drives those — this class doesn't need to change then.
 *
 * <p><b>Rotation (2026-07-02 fix — "los blocks no conservan su facing... talves el display
 * solo renderiza sin considerar la property actual").</b> Each {@link Cell} also sends a
 * {@code LeftRotation} transform equal to the bearing's own orientation
 * ({@code Quaternionf().rotateY(-yawRadians)}), same convention as
 * {@code ContraptionLevel#realOrientationOf} — see {@link Cell#metadata} javadoc for the full
 * root-cause writeup. The captured {@link BlockState}'s own {@code facing}/{@code axis}
 * property is untouched here and never needs to be: it only gets rotated once, at disassembly
 * time ({@code ContraptionCapture#restoreRotated}), which is correct for snapping to a real
 * cardinal placement — the CONTINUOUS visual rotation while still flying is entirely this
 * transform's job.
 *
 * <p><b>Ambient lighting (2026-07-02 fix — "la luz del ambiente no esta afectando el
 * contraption... el contraption brilla" reported testing at night).</b> Every cell's
 * {@link Cell#metadata} used to send a HARDCODED {@code DisplayData.BrightnessOverride} of
 * {@code (15<<4)|(15<<20)} (full block+sky light) unconditionally on every packet — a real
 * {@code minecraft:block_display}'s client renderer, when given an explicit
 * {@code Brightness} override, uses THAT flat value forever instead of computing real per-position
 * ambient lighting the way it does for an ACTUAL block occupying an ACTUAL lit position (confirmed
 * via {@code javap} against this project's own {@code mappedServerJar.jar}:
 * {@code net.minecraft.world.entity.Display.NO_BRIGHTNESS_OVERRIDE = -1} is the sentinel meaning
 * "no override, use real ambient light" — {@code Display#getBrightnessOverride}/
 * {@code setBrightnessOverride} only kick in when explicitly set away from that default). Since a
 * captured block here is a plain packet-only entity, not a real block in a real lit chunk
 * position, it never had real ambient light to fall back on in the first place — leaving the
 * field simply UNSET (as {@code FluidDisplayElement}'s own {@code blockLight >= 0 && skyLight >= 0}
 * guard already does elsewhere in this codebase) would make it default to some fixed renderer-side
 * value, not track real day/night — so this class now computes a REAL brightness value every
 * {@link #render} call from the bearing's actual current real-world block position
 * ({@code realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK/SKY).getLightValue(pos)},
 * confirmed via the same decompile) and feeds THAT into the override instead of a hardcoded
 * constant — same mechanism, now actually live-tracked so a contraption sitting out at night
 * genuinely darkens instead of staying permanently full-bright, and re-lights immediately if a
 * real torch/light source gets placed nearby.
 *
 * <p><b>Internal light sources (same session, follow-up ask).</b> A captured block that is
 * ITSELF a light emitter (torch, lantern, glowstone, etc. — {@code BlockState#getLightEmission()},
 * also confirmed via decompile) has no real chunk position to actually propagate light through —
 * packet-only cells never talk to each other via vanilla's light engine, so without extra work a
 * lit-up captured lantern would light only ITSELF, leaving every other captured cell around it as
 * dark as the ambient-only reading. {@link #render} approximates this cheaply (deliberately NOT a
 * full per-tick flood-fill light-propagation simulation — that's overkill for what's visually a
 * "glow nearby" effect and would need re-deriving vanilla's entire light BFS per contraption every
 * tick): each cell's final block-light contribution is
 * {@code max(realAmbientBlockLight, max over every light-emitting cell in this same swarm
 * (INCLUDING the cell itself, at distance 0) of (emitterLightEmission - chebyshevDistance))}, i.e.
 * a simple linear per-block-distance falloff, unobstructed (no line-of-sight/occlusion check
 * against other captured blocks) — a reasonable "good enough" approximation of vanilla block-light
 * spread's own per-block falloff-by-1 rule without the cost/complexity of simulating occlusion or a
 * real BFS queue. A light-emitting cell's own light emission counts as its own distance-0
 * contribution (see {@link #ambientBlockLightWithEmitters} javadoc for the 2026-07-02 correction —
 * an earlier version of this method wrongly excluded a cell from boosting itself). Sky light is NOT
 * boosted by internal emitters (real torches don't add sky light either).
 *
 * <p><b>Real {@code ClientboundLightUpdatePacket} — investigated, NOT implemented.</b> Sending
 * genuine light-section updates for the contraption's real-world footprint was considered as an
 * alternative to the per-entity {@code BrightnessOverride} approach (which would let the client
 * compute proper smooth/blended lighting for free, the same as a real block). Rejected: a
 * contraption's real-world footprint (in front of a moving/rotating structure) overlaps REAL
 * chunk sections that real blocks/other players also occupy and light — sending fake light data
 * for those same sections would corrupt lighting for everything else at that position for every
 * viewer, not just the contraption's own cells, and would need constant correction/cleanup as the
 * contraption moves through chunk boundaries (light sections are keyed by real chunk/section
 * position, not by "this specific set of packet entities"). The per-entity override this class
 * already uses has no such blast radius: it only ever affects the exact fake entities it's sent
 * for. Not worth the added architectural risk for a cosmetic improvement.
 *
 * <p><b>Smooth lighting — not feasible, no real approximation attempted.</b> Vanilla's smooth
 * lighting (ambient occlusion / per-vertex light blending between adjacent block faces) is a
 * client-side rendering feature that operates on REAL per-block-face light samples at REAL block
 * positions in the world's actual light grid — a {@code Display} entity's flat
 * {@code BrightnessOverride} is a single packed block+sky value applied uniformly across the
 * entire model, with no per-vertex/per-face granularity exposed over the protocol at all, so there
 * is no lever here to pull toward genuine smooth lighting. The internal-light-source falloff
 * above (cell-to-cell distance blending) is the closest practically-available approximation:
 * adjacent cells near an internal light source get progressively different flat brightness values
 * rather than a single jarring on/off boundary, which reads visually similar to a soft light
 * falloff even though it isn't real smooth lighting.
 */
public final class ContraptionDisplaySwarm {

    private final Map<BlockPos, Cell> cells = new HashMap<>();

    /** Last real ambient (block, sky) light pair read at the bearing's block position — see {@link #render}. */
    private int lastAmbientBlockLight = -1;
    private int lastAmbientSkyLight = -1;

    /** Monotonic per-swarm render counter driving the distance-based packet-rate LOD (see {@link #buildLod}). */
    private long renderTick = 0;

    /**
     * Distance-based packet-rate LOD (2026-07-03 — user: "rx skip for distance, near 24 is each 2
     * tick, 48 4 etc ... optimize packets incrementing the transition time, but reducing each what
     * ticks u send depending on player distance"). Per viewer, decides how OFTEN to resend this
     * contraption's block-display position packets and how long the client should interpolate each:
     * close viewers get every-tick updates (crisp), far viewers get one update every N ticks with an
     * N-tick interpolation window so the motion stays visually smooth on a fraction of the packets.
     * Returns {@code {sendThisTick(1/0), interpDurationTicks}} keyed by player UUID.
     */
    private java.util.Map<UUID, int[]> buildLod(List<Player> viewers, Vec3 bearingWorldPos, long tick) {
        java.util.Map<UUID, int[]> lod = new HashMap<>();
        for (Player p : viewers) {
            Object pp = p.platformPlayer();
            if (!(pp instanceof org.bukkit.entity.Player b)) {
                continue;
            }
            org.bukkit.Location loc = b.getLocation();
            double dx = loc.getX() - bearingWorldPos.x;
            double dy = loc.getY() - bearingWorldPos.y;
            double dz = loc.getZ() - bearingWorldPos.z;
            int interval = intervalForDistanceSq(dx * dx + dy * dy + dz * dz);
            int send = (tick % interval == 0) ? 1 : 0;
            int duration = Math.max(2, interval); // interpolate across the whole send gap
            lod.put(b.getUniqueId(), new int[] {send, duration});
        }
        return lod;
    }

    /** Ticks-between-position-updates for a viewer at squared distance {@code distSq} — 1/2/4/8 by band. */
    private static int intervalForDistanceSq(double distSq) {
        if (distSq < 24.0 * 24.0) {
            return 1; // close (and within collision range) — every tick
        }
        if (distSq < 48.0 * 48.0) {
            return 2;
        }
        if (distSq < 96.0 * 96.0) {
            return 4;
        }
        return 8;
    }

    /**
     * (Re)builds the swarm's cell set from a captured level. Existing cells for unchanged
     * offsets are REUSED as-is (never despawned/recreated) — only offsets that actually
     * dropped out of {@code wanted} (a block that stopped existing there, e.g. a piston
     * retracting its head) get their {@link Cell} despawned for {@code viewers} and removed;
     * a moving/rotating contraption never touches this map at all (that's handled per-tick by
     * {@link #render}'s position sync), and a blockstate-only change (furnace lit toggling
     * etc.) is handled by {@link Cell#updateIfChanged} inside {@link #render} too — neither
     * case should ever hit a despawn/respawn here.
     */
    public void rebuild(ContraptionLevel level, List<Player> viewers) {
        Set<BlockPos> wanted = level.localPositions();
        java.util.Iterator<Map.Entry<BlockPos, Cell>> it = cells.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Cell> e = it.next();
            if (!wanted.contains(e.getKey())) {
                for (Player p : viewers) {
                    e.getValue().despawn(p);
                }
                it.remove();
            }
        }
        for (BlockPos offset : wanted) {
            cells.computeIfAbsent(offset, o -> new Cell(level.getBlockState(o)));
        }
    }

    /**
     * Render every cell for the given viewers at the bearing's current CONTINUOUS
     * world-space position (a real {@code BLOCK_DISPLAY}'s position is the block model's
     * min corner, same as a real block's origin — no centering offset needed, unlike an
     * item/entity display). {@code bearingWorldPos} is the bearing cell's own origin
     * corner, e.g. {@code new Vec3(bearingBlockPos.getX(), ...)} with no +0.5.
     *
     * <p>{@code level} lets each cell re-check its LIVE {@link BlockState} every call (e.g. a
     * furnace's {@code lit} property toggling, a crafter's face, any block whose appearance is
     * driven by a blockstate property that changes mid-game inside the {@link ContraptionLevel})
     * and re-send its metadata packet when it changed — {@link #rebuild} only ever bakes each
     * cell's state once, at (re)build time, so without this the external {@code BLOCK_DISPLAY}
     * would silently freeze at whatever appearance the block had at capture/last-rebuild.
     *
     * <p>{@code moved} (packet-volume optimization — see {@code ContraptionEntity#render}):
     * false for a stalled/idle contraption whose bearing transform didn't change since the last
     * call — skips resending every cell's position-sync packet to every viewer for nothing;
     * blockstate/metadata dirty-resends are unaffected (tracked independently per cell).
     *
     * <p>{@code realLevel} is the contraption's REAL {@code ServerLevel} (null-tolerant — the
     * pure-kinematics/registry unit test path has no live Bukkit world) — see this class's own
     * javadoc, "Ambient lighting", for the full root-cause writeup. Read ONCE per call at the
     * bearing's own real block position and shared by every cell (a single ambient reading is a
     * fine approximation for an entire contraption-sized structure — real per-cell-position
     * readings would be more accurate but re-querying the light engine once per cell per tick for
     * a purely cosmetic value isn't worth the cost), then combined per-cell with any OTHER
     * captured light-emitting cell's own falloff contribution (see javadoc). Only actually
     * resends a cell's metadata when ITS OWN final computed brightness changed since last render
     * — ambient light changes are already infrequent (day/night transition, a real torch placed
     * /broken nearby) so this doesn't add meaningful packet volume on top of the existing
     * blockstate-dirty/moved-dirty resend gating.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ContraptionLevel level,
            boolean moved, ServerLevel realLevel) {
        updateAmbientLight(bearingWorldPos, realLevel);
        java.util.Map<UUID, int[]> lod = buildLod(viewers, bearingWorldPos, renderTick++);
        for (Map.Entry<BlockPos, Cell> e : cells.entrySet()) {
            if (level != null) {
                e.getValue().updateIfChanged(level.getBlockState(e.getKey()));
            }
            int blockLight = ambientBlockLightWithEmitters(e.getKey());
            // Position the BLOCK_DISPLAY entity at the cell's CENTER (not its corner): the model is
            // recentred onto the entity via a static translation=(-0.5,-0.5,-0.5) so the entity's own
            // body yaw spins it around the block's centre (see ContraptionDisplaySwarm.Cell#metadata's
            // ROTATION VIA ENTITY YAW javadoc). renderPosition orbits this centre around the bearing.
            BlockPos local = e.getKey();
            Vec3 center = new Vec3(local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5);
            Vec3 pos = ContraptionMath.renderPosition(center, bearingWorldPos, yawRadians);
            e.getValue().render(viewers, pos.x, pos.y, pos.z, (float) Math.toDegrees(yawRadians), yawRadians, moved,
                    blockLight, lastAmbientSkyLight, lod);
        }
    }

    /** Back-compat overload for any caller without a real-world light reference — falls back to full-bright. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ContraptionLevel level,
            boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, level, moved, null);
    }

    /**
     * Re-reads real block/sky light at the bearing's current real-world block position (see
     * class javadoc, "Ambient lighting"). {@code realLevel} null (no live Bukkit world, e.g. unit
     * tests) falls back to full-bright (15/15) — same visual as this class's old hardcoded
     * behavior, just now scoped to ONLY the null-world case instead of always.
     */
    private void updateAmbientLight(Vec3 bearingWorldPos, ServerLevel realLevel) {
        if (realLevel == null) {
            lastAmbientBlockLight = 15;
            lastAmbientSkyLight = 15;
            return;
        }
        BlockPos pos = BlockPos.containing(bearingWorldPos.x, bearingWorldPos.y, bearingWorldPos.z);
        try {
            lastAmbientBlockLight = realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(pos);
            lastAmbientSkyLight = realLevel.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos);
        } catch (Throwable ignored) {
            // Defensive: never let a light-engine read failure blank the whole swarm's rendering.
            lastAmbientBlockLight = 15;
            lastAmbientSkyLight = 15;
        }
    }

    /**
     * The real ambient block-light reading, boosted by every captured cell in this same swarm
     * that is itself a light emitter (see class javadoc, "Internal light sources") —
     * {@code max(ambient, emitterLightEmission - chebyshevDistance)} over every emitting cell,
     * INCLUDING {@code offset} itself at {@code distance = 0} (i.e. its own final brightness is
     * at least {@code max(ambient, its own getLightEmission())}).
     *
     * <p><b>2026-07-02 correction — live-test report: "la antorcha si ilumina pero no a si misma,
     * su propio block entity sigue del sky light."</b> This method used to {@code continue} past
     * {@code offset} itself under the theory that "a block doesn't need to light itself — its own
     * emission already implies it renders fine regardless of ambient darkness in vanilla too."
     * That assumption was wrong, and the live test proves it: a real placed torch's own
     * {@code getLightEmission()} DOES factor into the light level AT ITS OWN position (vanilla's
     * light engine computes every block's light level, including a light-emitting block's own
     * position, as the max of every contribution reaching that position — the emitter's own
     * light source counts as a distance-0 contributor to itself, same as it would to a neighbor
     * one block away). Skipping self here left a captured torch's OWN {@code BLOCK_DISPLAY} with
     * only the real ambient reading (dark at night) while correctly brightening every OTHER
     * nearby cell — exactly backwards from a real torch, which is always at least as bright as
     * its own emission. Sky light is never boosted this way (matches real torches: block light
     * only).
     */
    private int ambientBlockLightWithEmitters(BlockPos offset) {
        int best = lastAmbientBlockLight;
        for (Map.Entry<BlockPos, Cell> e : cells.entrySet()) {
            int emission = e.getValue().lightEmission();
            if (emission <= 0) {
                continue;
            }
            int dist = chebyshevDistance(offset, e.getKey());
            int contribution = emission - dist;
            if (contribution > best) {
                best = contribution;
            }
        }
        return Math.min(15, Math.max(0, best));
    }

    private static int chebyshevDistance(BlockPos a, BlockPos b) {
        return Math.max(Math.abs(a.getX() - b.getX()),
                Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
    }

    /**
     * Force a metadata resend for the cell at {@code offset} on the NEXT {@link #render} call,
     * regardless of whether a plain {@code BlockState} re-read would have detected a change on
     * its own (2026-07-02 session — "TankBlockBehavior renderiza el nivel de fluido via
     * blockstate... el modelo en el render del contraption no se actualizaba"). {@link #render}'s
     * own {@code updateIfChanged(level.getBlockState(offset))} SHOULD already catch a real
     * vanilla blockstate change on its own next-tick re-read — this exists as a guaranteed,
     * unconditional fallback for {@link dev.arubik.craftengine.contraption.ContraptionInteractionListener}
     * to call right after a successful CE-block interaction it dispatched, so a live fill/drain
     * visually updates immediately no matter what subtle blockstate-identity/equality quirk (CE's
     * own shared-proxy-blockstate multi-appearance system, stale cached property instances after
     * a `/craftengine reload`, etc.) might otherwise cause the passive re-read comparison to miss
     * it. A no-op if there's no tracked cell at {@code offset} (nothing to refresh).
     */
    public void markDirty(BlockPos offset) {
        Cell cell = cells.get(offset);
        if (cell != null) {
            cell.forceMetaDirty();
        }
    }

    /** Despawn every cell for one viewer (e.g. contraption torn down, or they logged off). */
    public void despawnAll(List<Player> viewers) {
        for (Cell cell : cells.values()) {
            for (Player p : viewers) {
                cell.despawn(p);
            }
        }
        cells.clear();
    }

    public int cellCount() {
        return cells.size();
    }

    /** One fake {@code BLOCK_DISPLAY} entity for a single captured cell. */
    private static final class Cell {
        private final int entityId = nextEntityId();
        private final UUID uuid = UUID.randomUUID();
        private BlockState blockState;
        private volatile boolean metaDirty = false;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        // Live-tracked brightness (see ContraptionDisplaySwarm's own "Ambient lighting" javadoc) —
        // -1 sentinel so the very first #render call always counts as a change and sends real
        // metadata instead of silently reusing whatever spawn() happened to send.
        private int blockLight = -1;
        private int skyLight = -1;

        Cell(BlockState blockState) {
            this.blockState = blockState;
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        /** Swaps in a freshly-read live {@link BlockState}, flagging a metadata resend if it actually changed. */
        void updateIfChanged(BlockState current) {
            if (!current.equals(this.blockState)) {
                this.blockState = current;
                this.metaDirty = true;
            }
        }

        /**
         * This cell's own captured block's light emission ({@code BlockState#getLightEmission()},
         * confirmed via decompile against {@code net.minecraft.world.level.block.state
         * .BlockBehaviour$BlockStateBase}) — 0 for a non-emitting block, up to 15 for something
         * like glowstone/sea lantern. Feeds {@link ContraptionDisplaySwarm}'s own
         * "Internal light sources" contribution to every OTHER cell in the same swarm.
         */
        int lightEmission() {
            return blockState.getLightEmission();
        }

        /**
         * Updates this cell's live brightness reading, flagging a metadata resend only if it
         * actually changed since the last call — ambient light changes infrequently (day/night
         * transition, a real/internal light source placed or broken nearby), so this doesn't add
         * meaningful packet volume beyond the existing blockstate/moved-dirty resend gating.
         */
        void updateLight(int blockLight, int skyLight) {
            if (blockLight != this.blockLight || skyLight != this.skyLight) {
                this.blockLight = blockLight;
                this.skyLight = skyLight;
                this.metaDirty = true;
            }
        }

        /** Unconditional metadata-resend flag — see {@link ContraptionDisplaySwarm#markDirty}. */
        void forceMetaDirty() {
            this.metaDirty = true;
        }

        /**
         * {@code yawRadians} is the bearing's CURRENT continuous yaw — NOT baked into the block's
         * own captured {@link BlockState} (that keeps whatever cardinal {@code facing}/{@code axis}
         * property it had at capture time; see {@code ContraptionCapture#restoreRotated} for the
         * only place a block's OWN facing property ever gets rotated, at disassembly). A real
         * {@code BLOCK_DISPLAY}'s client-side renderer does NOT rotate its baked block model from
         * the entity's own yaw/pitch (unlike a normal mob) — it is oriented purely by the
         * {@code transformation} metadata's rotation quaternions, applied ON TOP of the (already
         * facing-aware) block model. Without this, a captured block's model stays frozen at
         * whatever cardinal orientation it was captured in while only its POSITION orbits the
         * bearing (this class's own {@code render}'s {@code ContraptionMath.renderPosition} call) —
         * exactly the "no conserva su facing / no rota con el contraption" symptom reported
         * 2026-07-02: the chest never actually lost its facing, it simply never rotated WITH the
         * structure the way the position always correctly did. Fixed the same way every other
         * Display-backed swarm in this codebase already does it (see
         * {@code ContraptionLevel#realOrientationOf}, {@code FluidDisplay#setRotation},
         * {@code ConveyorItemDisplay#setRotation}): send a {@code LeftRotation} equal to the
         * bearing's own orientation quaternion, {@code rotateY(-yawRadians)} to match
         * {@link ContraptionMath#rotateYaw}'s {@code +yawRadians} position-rotation convention.
         *
         * <p><b>2026-07-02 follow-up fix — live-test report: "el rotation sigue mal pero solo el
         * rotation de los display de bloques, el rotation de las hitbox y interaction estan
         * bien."</b> The {@code LeftRotation} above rotated the model, but around the WRONG pivot.
         * A vanilla {@code minecraft:block_display}'s block model is rendered as a raw unit cube in
         * {@code [0,0,0]}-{@code [1,1,1]} MODEL space — corner-anchored, exactly like a real block's
         * own {@code BlockPos} origin, NOT centered on the entity's own position. Confirmed via
         * decompile of this project's own {@code mappedServerJar.jar}
         * ({@code com.mojang.math.Transformation#compose}): the 4 transform components combine as
         * a single {@code Matrix4f} built via, in order, {@code .translation(Translation)},
         * {@code .rotate(LeftRotation)}, {@code .scale(Scale)}, {@code .rotate(RightRotation)} —
         * each call right-multiplies the running matrix, so applied to a model vertex {@code v} the
         * effective math is {@code Translation + LeftRotation * (Scale * (RightRotation * v))}.
         * With {@code Scale}/{@code RightRotation} left at identity (as here) this reduces to
         * {@code Translation + LeftRotation * v} — sending ONLY a {@code LeftRotation} with no
         * accompanying {@code Translation} (this class's previous behavior; {@code Translation}
         * defaults to {@code (0,0,0)} per {@code Display#defineSynchedData}) rotates every model
         * vertex {@code v} around LOCAL {@code (0,0,0)}, i.e. around the block's own CORNER — same
         * class of bug as the just-fixed group-orbit pivot in {@code ContraptionMath}, but for each
         * individual block's own in-place spin instead of the group's orbit. That's exactly why the
         * hitboxes (plain {@code INTERACTION} entities — position/size only, no model, unaffected by
         * this) looked correct while every block's own texture/facing visibly rotated around the
         * wrong point.
         *
         * <p><b>2026-07-02 CORRECTION — "no debes rotar los bloques, toma todo el contraption como
         * si fuera 1 y rotalo desde el pivot point."</b> The center-recentering {@code Translation}
         * added by the paragraph above was WRONG — it re-broke rotation (model layer visibly
         * offset a whole block from its own hitboxes on a rotated structure). The correct model for
         * "rotate the whole contraption rigidly around the pivot" is: rotate the model around its
         * own CORNER (i.e. {@code LeftRotation} only, NO {@code Translation}), NOT its center.
         * Why: a {@code block_display}'s model is corner-anchored at the entity's own position, and
         * the entity's position is {@code ContraptionMath.renderPosition(localOffset)} = the block's
         * CORNER rotated around the bearing's pivot. Composing "rotate the corner around the global
         * pivot" (position) with "rotate the model around that same corner" (a plain
         * {@code LeftRotation}, pivoting at the model origin {@code (0,0,0)}) is exactly a single
         * rigid rotation of the whole block around the global pivot — verified algebraically and by
         * a worked 2-block/90-degree example: world vertex
         * {@code = E + R*v = renderPosition(L) + R*v = bearingWorldPos + pivot + R*(L - pivot) + R*v
         * = bearingWorldPos + pivot + R*((L + v) - pivot)}, i.e. every point {@code L+v} of the
         * block rotated rigidly about the global pivot. Any {@code Translation != 0} here adds an
         * extra {@code (I - R)*Translation} offset that shears the model off its own hitbox
         * (the recentering attempt's {@code center - R*center} became exactly a +1-block X shift at
         * 90 degrees). So: send only the {@code LeftRotation}, leave {@code Translation} at its
         * {@code (0,0,0)} default. The INTERACTION/hitbox layers already looked correct precisely
         * because they only ever used {@code renderPosition} (no model to spin) — corner-spin makes
         * the block model compose into the same rigid transform they already followed.
         */
        /**
         * ROTATION VIA ENTITY YAW (2026-07-02 — the LeftRotation-transformation approach never
         * rotated the block model no matter what we tried: top-level {@code DisplayData.LeftRotation}
         * and the {@code ItemDisplayData.LeftRotation} subtype accessor, {@code
         * TransformationInterpolationDuration} 0 and 2, {@code TransformationInterpolationDelay} 0,
         * splitting the blockstate off the update packet — server-side logging PROVED the exact
         * continuous quaternion was sent every tick, yet the model stayed at its cardinal facing).
         * Switched to the vanilla-idiomatic technique the user demonstrated with real
         * {@code /summon block_display} + {@code /tp @s ~ ~ ~ ~5 ~} commands: a {@code block_display}'s
         * model rotates with the ENTITY'S OWN body yaw, and a static
         * {@code transformation.translation = (-0.5,-0.5,-0.5)} re-centres the model on the entity's
         * position so that yaw spins it around the block's CENTER instead of its corner. So this
         * metadata now carries NO LeftRotation at all — only the recentring translation (static,
         * never changes) — and the rotation is driven entirely by the entity yaw we send in the
         * spawn {@link #spawn} + {@link #updatePosition} packets. The bearing's orbit is handled by
         * positioning the entity at the block's CENTER (see {@code ContraptionDisplaySwarm#render}),
         * and {@code PosRotInterpolationDuration} smooths both the position AND the yaw rotation.
         */
        /** Per-viewer interpolation-duration last sent (distance-LOD — see {@link ContraptionDisplaySwarm#buildLod}). */
        private final Map<UUID, Integer> lastInterp = new ConcurrentHashMap<>();

        private List<Object> metadata(int interpDuration) {
            List<Object> values = new ArrayList<>();
            DisplayData.BlockDisplayData.BlockState.addEntityData(blockState, values);
            // Recentre the corner-anchored [0,1] block model onto the entity position so entity-yaw
            // rotation pivots around the block's own centre (vanilla /tp-yaw block_display technique).
            DisplayData.Translation.addEntityData(new org.joml.Vector3f(-0.5f, -0.5f, -0.5f), values);
            // Live real-world brightness (see class javadoc, "Ambient lighting") — NOT a hardcoded
            // full-bright constant anymore. blockLight/skyLight are clamped 0-15 by the swarm
            // before ever reaching here (see #ambientBlockLightWithEmitters/#updateAmbientLight).
            DisplayData.BrightnessOverride.addEntityData((blockLight << 4) | (skyLight << 20), values);
            // Smooths BOTH the per-tick position AND the entity-yaw rotation. Now distance-scaled
            // (2026-07-03 LOD): a far viewer that only receives an update every N ticks gets an
            // N-tick interpolation window so the motion still looks continuous on 1/N the packets.
            DisplayData.PosRotInterpolationDuration.addEntityData(interpDuration, values);
            return values;
        }

        void spawn(Player player, double x, double y, double z, float yawDegrees, int interpDuration) {
            // Entity spawned already facing the bearing's current yaw — the block model rotates
            // with the entity body yaw (see #metadata's ROTATION VIA ENTITY YAW javadoc).
            Object addPacket = MNms.INSTANCE.constructor$ClientboundAddEntityPacket(
                    entityId, uuid, x, y, z, 0f, yawDegrees, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0);
            Object dataPacket = MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(interpDuration));
            player.sendPackets(List.of(addPacket, dataPacket), false);
        }

        void updatePosition(Player player, double x, double y, double z, float yawDegrees) {
            player.sendPacket(MNms.INSTANCE
                    .constructor$ClientboundEntityPositionSyncPacket(entityId, x, y, z, yawDegrees, 0f, false), false);
        }

        void despawn(Player player) {
            player.sendPacket(despawnPacket, false);
        }

        void render(List<Player> viewers, double x, double y, double z, float yawDegrees, double yawRadians,
                boolean moved, int blockLight, int skyLight, Map<UUID, int[]> lod) {
            updateLight(blockLight, skyLight); // flags metaDirty itself if this cell's brightness actually changed
            boolean metaChanged = metaDirty; // blockstate/brightness actually changed this tick
            metaDirty = false;
            Set<UUID> current = new java.util.HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                current.add(id);
                int[] v = lod.get(id);
                boolean sendThisTick = v == null || v[0] != 0;
                int duration = v != null ? v[1] : 2;
                if (shownTo.add(id)) {
                    spawn(p, x, y, z, yawDegrees, duration);
                    lastInterp.put(id, duration);
                } else {
                    // Resend metadata when this viewer's interpolation window changed (they crossed a
                    // distance band) OR the block's own appearance/brightness changed. Rotation and
                    // position travel together in the position-sync packet (via body yaw), gated below
                    // by this viewer's distance-LOD send interval.
                    Integer prev = lastInterp.get(id);
                    if (prev == null || prev != duration || metaChanged) {
                        sendMetadata(p, duration);
                        lastInterp.put(id, duration);
                    }
                    if (moved && sendThisTick) {
                        updatePosition(p, x, y, z, yawDegrees);
                    }
                }
            }
            shownTo.retainAll(current);
            lastInterp.keySet().retainAll(current);
        }

        private void sendMetadata(Player player, int interpDuration) {
            player.sendPacket(MNms.INSTANCE.constructor$ClientboundSetEntityDataPacket(entityId, metadata(interpDuration)),
                    false);
        }

        private static UUID uuidOf(Player player) {
            Object pp = player.platformPlayer();
            return pp instanceof org.bukkit.entity.Player b ? b.getUniqueId() : null;
        }
    }

    // ---- fresh server-unique fake entity id (Entity.ENTITY_COUNTER is private) ----
    private static final AtomicInteger ENTITY_COUNTER;
    static {
        AtomicInteger counter;
        try {
            Field f = net.minecraft.world.entity.Entity.class.getDeclaredField("ENTITY_COUNTER");
            f.setAccessible(true);
            counter = (AtomicInteger) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
        ENTITY_COUNTER = counter;
    }

    private static int nextEntityId() {
        return ENTITY_COUNTER.incrementAndGet();
    }
}
