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
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.bukkit.entity.data.DisplayData;
import net.momirealms.craftengine.core.entity.player.Player;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.core.world.CEWorld;

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
 * <p><b>Phantom second half of a 2-part carrier block (2026-07-02 → 2026-07-16 fix — "el bed
 * aparece magicamente su otra mitad").</b> A CraftEngine block whose vanilla CARRIER state belongs to
 * the client's hardcoded special-block-model-renderer family (bed, chest, banner, shulker box, skull,
 * conduit, decorated pot) is drawn IN FULL by a {@code block_display} — a bed carrier renders head AND
 * foot — even though the same state renders correctly as a real block. See
 * {@link #displayStateOf}'s javadoc for the full root-cause writeup (it is the client's ITEM-shaped
 * special renderer, keyed by {@code Block} identity and blind to the {@code part} property, standing in
 * for the {@code BlockEntityRenderer} a {@code block_display} cannot use) and for why the fix — hand the
 * display {@code minecraft:air} and let {@link ContraptionBlockEntityElementMirror}'s
 * {@code entity_renderer} element supply the visual — is exactly what the real world already does for
 * these blocks.
 *
 * <p><b>DOUBLE bed on a REAL vanilla bed (2026-07-16 follow-up — "el render de las partes de la cama
 * sigue bugueado, se renderiza doble — completo por cada parte de la cama; debe renderizarse solo la
 * parte exacta de la cama").</b> The blanking rule above only fires for a CE block WITH an
 * {@code entity_renderer}; a REAL captured {@code minecraft:*_bed} has none, so BOTH of its cells kept
 * running {@code BedSpecialRenderer} and each drew a COMPLETE bed — two overlapping full beds. Since
 * that renderer physically cannot draw a half bed, the fix draws the complete bed EXACTLY ONCE for the
 * pair, from the HEAD cell, and blanks the FOOT cell to {@code minecraft:air} — see
 * {@link #bedDisplayStateOf} for the anchor/orientation derivation (verified against real Mojang-mapped
 * client source) and {@link #modelYawOffsetOf} for the facing correction the anchor needs.
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
 * {@link #render} call from EACH CELL's own real-world position
 * ({@code realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK/SKY).getLightValue(pos)},
 * confirmed via the same decompile) and feeds THAT into the override instead of a hardcoded
 * constant — so a contraption sitting out at night genuinely darkens instead of staying
 * permanently full-bright, re-lights when a real torch is placed nearby, and shades per cell so a
 * cell under an overhang is darker than one in the open. Sampling per cell rather than once at the
 * bearing also removes a failure that made the whole structure black: see {@link #ambientAt}.
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

    /**
     * Beyond this many blocks from a CELL, that cell is not projected to that viewer at all.
     *
     * <h2>Why a cull and not just a slower packet rate</h2>
     * The distance bands below only decide how OFTEN a cell's position is resent — every cell was still
     * SPAWNED for every viewer in the list, however far away. So a contraption's cost scaled as
     * cells x viewers with no upper bound: 200 cells and 20 players meant 4000 fake entities alive on
     * clients, and 4000 iterations of this loop every tick, for a structure most of them could not see.
     *
     * <p>Culling is per CELL rather than per contraption deliberately: a large structure's near face
     * stays projected while its far side does not, which is both cheaper and more correct than an
     * all-or-nothing test against the bearing — a 100-block bridge is not "far away" just because its
     * anchor is.
     *
     * <p>Chosen at 96 (six chunks) — comfortably past any distance a block-sized visual is
     * distinguishable, and past vanilla's own 64-block entity tracking, so a cell winks out well after
     * the client would have stopped tracking a real entity there anyway. The
     * {@link #RENDER_DISTANCE_HYSTERESIS} band keeps a viewer hovering at the boundary from
     * respawning it every tick.
     */
    private static final double RENDER_DISTANCE = 96.0;

    /** Extra distance a cell stays projected once shown, so a viewer on the boundary does not strobe it. */
    private static final double RENDER_DISTANCE_HYSTERESIS = 8.0;

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
            cells.computeIfAbsent(offset, o -> {
                BlockState raw = level.getBlockState(o);
                return new Cell(displayStateOf(level, o, raw), raw);
            });
        }
    }

    /**
     * The {@link BlockState} this cell's {@code block_display} should actually be handed — normally the
     * captured state verbatim, but {@code minecraft:air} for a CraftEngine block whose vanilla CARRIER state
     * belongs to the client's "special block model renderer" family (see
     * {@link #isSpecialModelRendererBlock}) AND that supplies its real visual through an
     * {@code entity_renderer} (see {@link #hasConstantEntityRenderer}).
     *
     * <p><b>ROOT CAUSE (2026-07-16 — user report: a captured bed-carrier block renders a PHANTOM second
     * half; {@code default:sleeper_sofa}'s head "magically appears" next to the authored foot).</b> The
     * phantom half is NOT a capture bug: the captured cell set contains exactly ONE cell (verified — the
     * head half is a different {@code BlockPos} and simply is not in {@code level.localPositions()}), and it
     * is not this class deriving anything either. It is the CLIENT drawing the whole vanilla bed from the
     * single {@code part=foot} state, and the reason is a real asymmetry between how a bed renders as a
     * BLOCK and how it renders inside a {@code block_display}:
     * <ul>
     * <li><b>As a real block</b>, a bed's visual comes from {@code BedRenderer}, a
     * {@code BlockEntityRenderer} bound to {@code BlockEntityType.BED} — it renders ONE half per block
     * entity, choosing head vs. foot from that block entity's own {@code part} property. CraftEngine's
     * carrier states are unused vanilla state ids with no bed block entity behind them at all, so in the
     * real world that renderer never runs and the sofa's only visual is its {@code entity_renderer}
     * item_display. That is exactly why the real world looks correct today.</li>
     * <li><b>Inside a {@code block_display}</b>, there is no block entity and no block position, so the
     * client cannot use {@code BedRenderer}. Its renderer instead goes through
     * {@code BlockRenderDispatcher#renderSingleBlock}, which — since the 1.21.4 "special model renderer"
     * migration — pairs the state's baked model with a {@code SpecialBlockModelRenderer} lookup keyed by
     * {@code Block} IDENTITY ({@code SpecialModelRenderers}' static block mapping: beds, chests, banners,
     * shulker boxes, skulls, conduit, decorated pots). {@code BedSpecialRenderer} is the ITEM-shaped
     * renderer — the one that draws the bed you see in an inventory slot — so it draws the COMPLETE bed,
     * head and foot both. Being keyed by Block, it never sees the {@code part} property, and being a
     * hardcoded renderer rather than a model, no resource pack (CraftEngine's included) can override or
     * blank it.</li>
     * </ul>
     * Corroborated server-side against this project's own {@code mappedServerJar.jar}: {@code RenderShape}
     * now has only {@code INVISIBLE} and {@code MODEL} — the old {@code ENTITYBLOCK_ANIMATED} constant that
     * used to mark exactly this family of blocks is gone, which is precisely the bookkeeping that moved into
     * the client-side special-model-renderer mapping described above.
     *
     * <p><b>FIX.</b> Hand the {@code block_display} {@code minecraft:air} instead, so the client has nothing
     * to run its special renderer against, and let the {@code entity_renderer} element supply the visual via
     * {@link ContraptionBlockEntityElementMirror} — which is EXACTLY the arrangement the real world already
     * uses for these blocks (invisible carrier + item_display visual), not a new one. Detection of "this CE
     * block has an entity_renderer" goes through the same {@code CEChunk#getConstantBlockEntityRenderer(pos)}
     * path that mirror's own {@code rebuild} uses, so the two can never disagree about which cells the
     * mirror is covering.
     *
     * <p><b>Deliberately narrow — why NOT "blank every entity_renderer block".</b> For a NON-special carrier
     * (a note block, tripwire, etc. — the overwhelming majority of CE blocks) {@code renderSingleBlock}
     * renders only the state's baked model, which the CraftEngine resource pack has overridden to the
     * block's real custom model. Those cells render CORRECTLY today, and blanking them would delete a real
     * visual for any CE block that has both a pack model and an entity_renderer. The bug is unique to the
     * special-renderer family, so the fix is scoped to it: every other block, and every special-carrier block
     * WITHOUT an entity_renderer (which has no mirrored visual to fall back on, so blanking could only make
     * it worse), is left byte-for-byte untouched.
     *
     * <p><b>2026-07-16 follow-up — a REAL vanilla bed (no entity_renderer) still doubled.</b> The
     * entity_renderer rule above is checked FIRST and is unchanged (a CE sofa on a bed carrier still blanks
     * both halves and keeps its mirrored item_display visual). Only when it does NOT apply does a real
     * captured {@code BedBlock} fall through to {@link #bedDisplayStateOf}, which picks the single anchor
     * half. Ordering matters: the CE rule is the stricter one (blanks BOTH halves because the mirror already
     * owns the visual), so it must win.
     */
    private static BlockState displayStateOf(ContraptionLevel level, BlockPos local, BlockState raw) {
        if (level == null || !isSpecialModelRendererBlock(raw)) {
            return raw; // fast path — the overwhelming majority of cells never even do the CE lookup
        }
        if (hasConstantEntityRenderer(level, local)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (raw.getBlock() instanceof BedBlock) {
            return bedDisplayStateOf(level, local, raw);
        }
        return raw;
    }

    /**
     * The render state for a REAL captured vanilla bed cell (no CraftEngine {@code entity_renderer} — that
     * case is already handled and returned before this is ever reached; see {@link #displayStateOf}): the
     * captured state verbatim for the ONE cell that draws the whole bed, {@code minecraft:air} for every
     * other bed cell.
     *
     * <p><b>ROOT CAUSE (2026-07-16 — user: "el render de las partes de la cama sigue bugueado, se renderiza
     * doble — completo por cada parte de la cama; debe renderizarse solo la parte exacta de la cama").</b>
     * A real bed occupies TWO blocks ({@code part=head} + {@code part=foot}), and both cells run
     * {@code BedSpecialRenderer} — the part-blind, item-shaped renderer described at length in
     * {@link #displayStateOf} — so each cell independently draws a COMPLETE two-block bed, giving two
     * overlapping full beds. The {@code entity_renderer} blanking rule cannot help here: a vanilla bed is not
     * a CraftEngine block and has no mirrored visual to fall back on, so blanking both halves would simply
     * delete the bed.
     *
     * <p><b>FIX — draw the complete bed exactly once, from the HEAD cell.</b>
     * {@code BedSpecialRenderer} has exactly one drawing primitive ("a complete bed"), so the only lever left
     * is WHICH cell carries it and HOW it is oriented. Both were derived from the REAL Mojang-mapped client
     * source ({@code BedSpecialRenderer#render} → {@code BedRenderer#renderInHand} → {@code BedRenderer
     * #preparePose}), read from a decompile of 1.21.8 (the nearest published decompile to this project's
     * 1.21.11 — the class is client-side so it is NOT in this project's {@code mappedServerJar.jar}; the
     * bed renderer has been byte-identical across 1.21.4→1.21.8, and its server-side companions
     * {@code BedBlock#getConnectedDirection}/{@code BedBlock.PART}/{@code Direction#toYRot} were
     * re-verified against 1.21.11's own {@code mappedServerJar.jar} via {@code javap}):
     * <ul>
     * <li>{@code renderInHand} calls {@code preparePose(isFoot=false, Direction.SOUTH)} for the head model and
     * {@code preparePose(isFoot=true, Direction.SOUTH)} for the foot model. {@code preparePose} is
     * {@code translate(0, 0.5625, isFoot ? -1 : 0) · rotX(90°) · translate(0.5,0.5,0.5) ·
     * rotZ(180° + dir.toYRot()) · translate(-0.5,-0.5,-0.5)}, and the bed's {@code main} cube is
     * {@code addBox(0,0,0, 16,16,6)} (i.e. the unit cell {@code [0,1]²} in the horizontal plane once
     * {@code ModelPart} divides by 16). Working that through: the HEAD slab lands in model cell
     * {@code x∈[0,1], z∈[0,1]} (the ORIGIN cell) and the FOOT slab lands in {@code z∈[-1,0]} — one block
     * toward {@code -Z}. <b>So the anchor is the HEAD cell</b>, and the bed is drawn with a HARDCODED
     * {@code facing = SOUTH} (head at the origin, foot at origin{@code -Z}; cross-checked against
     * {@code BedBlock#getConnectedDirection}, which puts a bed's foot at {@code head - facing}).</li>
     * <li>The hardcoded SOUTH is why the anchor needs a facing correction on top of the bearing's own yaw —
     * see {@link #modelYawOffsetOf}.</li>
     * </ul>
     * Anchoring at the HEAD costs no extra positioning work: this class already places each cell's entity at
     * the cell CENTRE with a {@code (-0.5,-0.5,-0.5)} recentring translation, so entity yaw spins the model
     * about the HEAD cell's own centre — the head stays put in its own cell and the foot orbits into the
     * adjacent one, exactly tracking where the real foot cell's own {@code renderPosition} puts it (both are
     * {@code headCentre + scale · rotateYaw(-facing, bearingYaw)}). Uniform {@code scale} rides along for
     * free through the existing {@code DisplayData.Scale} + scaled recentring translation.
     *
     * <p><b>HALF-CAPTURED BED — both halves blank, deliberately.</b> When only one half of a bed made it into
     * the contraption (the player assembled across the bed), this returns air for that half too, so a
     * half-captured bed renders NOTHING. Why that is the least-surprising choice: the client can only draw a
     * COMPLETE bed, so the only alternative is to put solid bed geometry into a cell the contraption does not
     * own — protruding out of the structure, clipping through whatever real block is actually there, and
     * re-creating the exact "phantom half" symptom this whole fix exists to remove. Rendering nothing keeps
     * the failure contained inside cells we do own. A lone bed half is also not a legal vanilla configuration
     * in the first place (breaking either half destroys both), i.e. a degenerate capture rather than a state
     * worth rendering faithfully. Pair detection deliberately requires BOTH that the partner cell is in
     * {@code localPositions()} (the same authority {@link #rebuild} uses to decide a cell exists at all — a
     * bed half sitting in the mini-dimension but outside the captured set has no {@link Cell} and must not be
     * covered) AND that its state is genuinely this bed's other half.
     */
    private static BlockState bedDisplayStateOf(ContraptionLevel level, BlockPos local, BlockState raw) {
        if (raw.getValue(BedBlock.PART) != BedPart.HEAD) {
            return Blocks.AIR.defaultBlockState(); // the foot half is drawn BY the head's full-bed model
        }
        // getConnectedDirection points at the partner: facing.getOpposite() for a HEAD (verified against
        // 1.21.11's mappedServerJar.jar), i.e. the foot sits at head - facing.
        BlockPos partner = local.relative(BedBlock.getConnectedDirection(raw));
        if (!level.localPositions().contains(partner)) {
            return Blocks.AIR.defaultBlockState(); // half-captured — see javadoc
        }
        BlockState partnerState = level.getBlockState(partner);
        boolean paired = partnerState.getBlock() == raw.getBlock()
                && partnerState.getValue(BedBlock.PART) == BedPart.FOOT
                && partnerState.getValue(BedBlock.FACING) == raw.getValue(BedBlock.FACING);
        return paired ? raw : Blocks.AIR.defaultBlockState();
    }

    /**
     * Extra body-yaw (DEGREES) a cell's {@code block_display} needs ON TOP of the bearing's own yaw so the
     * client's hardcoded special renderer draws the captured block at its captured facing — {@code 0} for
     * every cell that isn't a surviving bed anchor, which is all but one cell per captured bed.
     *
     * <p><b>Why a bed anchor needs one.</b> {@code BedRenderer#renderInHand} — the method
     * {@code BedSpecialRenderer} delegates to (see {@link #bedDisplayStateOf} for the full derivation and
     * source provenance) — passes a HARDCODED {@code Direction.SOUTH} for both halves and ignores the
     * state's own {@code facing} entirely, so an untouched bed anchor would always draw a SOUTH-facing bed no
     * matter what was captured. Body yaw is the one rotation lever a {@code block_display} actually honours
     * in this codebase (see {@link Cell#metadata}'s "ROTATION VIA ENTITY YAW" javadoc — an extensive earlier
     * attempt proved a {@code block_display} ignores the transformation {@code LeftRotation}), and it already
     * pivots about the block's own centre, which for the HEAD anchor is exactly the pivot that swings the
     * foot half into the adjacent cell.
     *
     * <p><b>Why {@code facing.toYRot()} is the exact correction.</b> Entity yaw {@code Y} rotates a
     * {@code block_display}'s model by {@code Display#calculateOrientation}'s
     * {@code rotationYXZ(-radians(Y), ...)}, which expands to {@code x' = x·cosY − z·sinY, z' = x·sinY +
     * z·cosY} — i.e. bit-for-bit {@link ContraptionMath#rotateYaw}{@code (v, radians(Y))}, which is also why
     * this class can pass {@code toDegrees(yawRadians)} straight through and have the model track the
     * {@code rotateYaw(+yawRadians)} orbit its position already follows. The model puts the foot centre at
     * {@code +(0,0,-1)} from the head centre, so under yaw {@code θ} the foot lands at
     * {@code (sinθ, 0, -cosθ)}; solving that against {@code facing.getOpposite()} (where the real foot is)
     * gives {@code θ = SOUTH→0°, WEST→90°, NORTH→180°, EAST→270°} — precisely {@code Direction#toYRot()}.
     * Yaw rotations about the same axis commute and compose additively, so the anchor's total body yaw is
     * simply {@code bearingYawDegrees + facing.toYRot()} and the bearing's continuous spin is unaffected.
     *
     * <p><b>Not extended to the rest of the special-renderer family — deliberate.</b> Every other member is
     * either single-block (banner, skull, conduit, decorated pot, shulker box) or already renders acceptably
     * (a double chest's two halves each draw a SINGLE chest model, so they tile rather than overlap — the
     * seam is cosmetic, not a doubled block). Beds are the only member whose ONE renderer call spans two
     * cells, which is why they are the only member that needs an anchor and a facing correction.
     */
    private static float modelYawOffsetOf(BlockState renderState) {
        // Reads the RENDER state, so a blanked (air) bed half is not a BedBlock and correctly gets 0 —
        // only the single surviving anchor per bed ever carries a correction.
        return renderState.getBlock() instanceof BedBlock ? renderState.getValue(BedBlock.FACING).toYRot() : 0f;
    }

    /**
     * Whether {@code state}'s block is one the client draws through a hardcoded
     * {@code SpecialBlockModelRenderer} (keyed by {@code Block} identity, part/state-blind, unoverridable by
     * a resource pack) whenever it is rendered outside a real block position — i.e. exactly the family whose
     * {@code block_display} shows a full vanilla object regardless of the authored state. See
     * {@link #displayStateOf}'s root-cause javadoc.
     *
     * <p>Mirrors the client's own static block mapping: beds ({@code BedSpecialRenderer} — the reported bug),
     * chests/trapped/ender ({@code AbstractChestBlock} covers all three), banners, shulker boxes, skulls,
     * conduit, decorated pots. Matched by CLASS rather than by a hardcoded {@code Blocks.WHITE_BED}-style id
     * list so every dye colour / wood type / mob variant is covered automatically, and so a CE block carrying
     * any member of the family gets the fix without this list needing to know about it.
     */
    private static boolean isSpecialModelRendererBlock(BlockState state) {
        Block block = state.getBlock();
        return block instanceof BedBlock
                || block instanceof AbstractChestBlock<?>
                || block instanceof AbstractBannerBlock
                || block instanceof ShulkerBoxBlock
                || block instanceof AbstractSkullBlock
                || block instanceof ConduitBlock
                || block instanceof DecoratedPotBlock;
    }

    /**
     * Whether the CraftEngine block captured at {@code local} declares an {@code entity_renderer} — i.e.
     * whether {@link ContraptionBlockEntityElementMirror} is already rendering this cell's real visual, and
     * this swarm's {@code block_display} is therefore free to be blanked (see {@link #displayStateOf}).
     * Deliberately the SAME {@code CEChunk#getConstantBlockEntityRenderer(cePos)} lookup that mirror's
     * {@code rebuild} uses to decide it owns the cell.
     *
     * <p>Only ever called for the rare special-renderer family (see {@link #displayStateOf}'s fast path), so
     * the per-tick cost of the world/chunk resolution is not paid by normal cells. Any failure (CraftEngine
     * absent, world unresolvable, chunk not loaded) answers {@code false} — the conservative direction: keep
     * rendering the captured state exactly as this class always did rather than risk blanking a cell whose
     * mirror coverage we could not confirm.
     */
    private static boolean hasConstantEntityRenderer(ContraptionLevel level, BlockPos local) {
        try {
            org.bukkit.World bukkitWorld = level.getWorld();
            CEWorld ceWorld = CraftEngine.instance().worldManager().getWorld(bukkitWorld.getUID());
            if (ceWorld == null) {
                return false;
            }
            net.momirealms.craftengine.core.world.BlockPos cePos =
                    new net.momirealms.craftengine.core.world.BlockPos(local.getX(), local.getY(), local.getZ());
            net.momirealms.craftengine.core.world.chunk.CEChunk chunk = ceWorld.getChunkAtIfLoaded(cePos);
            return chunk != null && chunk.getConstantBlockEntityRenderer(cePos) != null;
        } catch (Throwable t) {
            return false;
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
     * javadoc, "Ambient lighting", for the full root-cause writeup. Read per CELL at its own real
     * block position, throttled by {@link #LIGHT_REFRESH_TICKS} (a per-cell ambient reading is a
     * fine approximation for an entire contraption-sized structure — real per-cell-position
     * readings would be more accurate but re-querying the light engine once per cell per tick for
     * a purely cosmetic value isn't worth the cost), then combined per-cell with any OTHER
     * captured light-emitting cell's own falloff contribution (see javadoc). Only actually
     * resends a cell's metadata when ITS OWN final computed brightness changed since last render
     * — ambient light changes are already infrequent (day/night transition, a real torch placed
     * /broken nearby) so this doesn't add meaningful packet volume on top of the existing
     * blockstate-dirty/moved-dirty resend gating.
     */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double rollRadians, double scale, ContraptionLevel level, boolean moved, ServerLevel realLevel) {
        // Light is re-read per CELL below (see #ambientAt); this only decides WHEN, since a few
        // ticks of staleness in a light level is invisible but re-sampling every cell every tick is not free.
        boolean refreshLight = renderTick % LIGHT_REFRESH_TICKS == 0;
        if (refreshLight) {
            ambientCache.keySet().retainAll(cells.keySet()); // a removed cell must not keep its reading alive
        }
        java.util.Map<UUID, int[]> lod = buildLod(viewers, bearingWorldPos, renderTick++);
        for (Map.Entry<BlockPos, Cell> e : cells.entrySet()) {
            if (level != null) {
                // displayStateOf, not the raw state: keeps the live re-read on the SAME blanking rule
                // rebuild applied, so a special-renderer carrier (bed, ...) whose CE entity_renderer
                // appears/disappears at runtime flips its block_display in lock-step instead of the two
                // paths disagreeing. Non-special blocks take its fast path and are unaffected — see
                // #displayStateOf's root-cause javadoc (2026-07-16 phantom bed head).
                BlockState raw = level.getBlockState(e.getKey());
                e.getValue().updateIfChanged(displayStateOf(level, e.getKey(), raw), raw);
            }
            // Position the BLOCK_DISPLAY entity at the cell's CENTER (not its corner): the model is
            // recentred onto the entity via a static translation=(-0.5,-0.5,-0.5) so the entity's own
            // body yaw spins it around the block's centre (see ContraptionDisplaySwarm.Cell#metadata's
            // ROTATION VIA ENTITY YAW javadoc). renderPosition orbits this centre around the bearing —
            // now by BOTH yaw AND pitch (roadmap item #9 phase 5 — TIPPING) AND uniform scale (roadmap
            // item #9 — per-contraption size), so a scaled/tipping body's cells ALWAYS orbit to their
            // scaled tilted rigid positions (position-scale/pitch is guaranteed, independent of whether
            // the per-block model Scale/tilt below is honoured by the client). Cell CENTERS are spaced by
            // `scale` here, and the model itself is sized by the same `scale` in Cell#metadata, so scaled
            // blocks tile seamlessly. At scale 1 / pitch 0 this is byte-for-byte the pre-scale/pre-pitch
            // orbit (ContraptionMath#renderPosition's fast paths).
            BlockPos local = e.getKey();
            // An OffsetType.XZ block (bamboo, grass, ...) is drawn shifted by a per-position pseudo-random
            // horizontal offset. Vanilla applies it to BOTH the model AND the collision shape, so they align.
            // Our shulker collider already gets it for free (it comes from BlockState#getCollisionShape(level,
            // local), and bamboo's getCollisionShape does SHAPE.move(getOffset(pos))) — but the packet
            // block_display does NOT (the client never applies getOffset to a display entity), so the model sat
            // at the plain cell centre while the collider sat at centre+offset: "el bambú no coincide con su
            // collision box". Fix: shift the display by the SAME offset the collider uses. It is keyed off the
            // LOCAL cell pos (fixed per cell, exactly as getCollisionShape computes it — NOT the moving world
            // pos), added in the LOCAL frame BEFORE renderPosition so it rotates/scales with the body just like
            // the collider does. No-op for non-offset blocks.
            double offX = 0.0, offZ = 0.0;
            BlockState offState = e.getValue().displayState();
            if (offState != null) {
                net.minecraft.world.phys.Vec3 boff = offState.getOffset(local);
                offX = boff.x;
                offZ = boff.z;
            }
            Vec3 center = new Vec3(local.getX() + 0.5 + offX, local.getY() + 0.5, local.getZ() + 0.5 + offZ);
            Vec3 pos = ContraptionMath.renderPosition(center, bearingWorldPos, yawRadians, pitchRadians, rollRadians,
                    scale);
            int[] ambient = ambientAt(realLevel, local, pos, refreshLight);
            int blockLight = ambientBlockLightWithEmitters(local, ambient[0]);
            e.getValue().render(viewers, pos.x, pos.y, pos.z, (float) Math.toDegrees(yawRadians), yawRadians,
                    pitchRadians, rollRadians, scale, moved, blockLight, ambient[1], lod);
        }
    }

    /** Back-compat roll-0 overload — see the full {@link #render(List, Vec3, double, double, double, double, ContraptionLevel, boolean, ServerLevel)}. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            double scale, ContraptionLevel level, boolean moved, ServerLevel realLevel) {
        render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, scale, level, moved, realLevel);
    }

    /** Back-compat scale-1/roll-0 overload — see the full {@link #render(List, Vec3, double, double, double, double, ContraptionLevel, boolean, ServerLevel)}. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, double pitchRadians,
            ContraptionLevel level, boolean moved, ServerLevel realLevel) {
        render(viewers, bearingWorldPos, yawRadians, pitchRadians, 0.0, 1.0, level, moved, realLevel);
    }

    /** Back-compat pitch-0/roll-0/scale-1 overload (yaw only) — see the full {@link #render(List, Vec3, double, double, double, double, ContraptionLevel, boolean, ServerLevel)}. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ContraptionLevel level,
            boolean moved, ServerLevel realLevel) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 0.0, 1.0, level, moved, realLevel);
    }

    /** Back-compat overload for any caller without a real-world light reference — falls back to full-bright. */
    public void render(List<Player> viewers, Vec3 bearingWorldPos, double yawRadians, ContraptionLevel level,
            boolean moved) {
        render(viewers, bearingWorldPos, yawRadians, 0.0, 0.0, 1.0, level, moved, null);
    }

    /** Cached real-world ambient {@code {block, sky}} light per cell — see {@link #ambientAt}. */
    private final Map<BlockPos, int[]> ambientCache = new HashMap<>();

    /**
     * Ticks between real-light re-reads. Light changes slowly (a torch placed, dusk falling) and a cell
     * being a few ticks stale is invisible, so re-sampling every cell every tick buys nothing and costs
     * two light-engine reads per cell per tick.
     */
    private static final int LIGHT_REFRESH_TICKS = 4;

    /**
     * The real world's ambient light AT THIS CELL's own position.
     *
     * <h2>Why per cell, and what was wrong before</h2>
     * This used to be sampled ONCE, at the bearing's block position, and that single value was handed
     * to every cell of the contraption. The bearing origin is not a meaningful place to measure light:
     * it is the structure's origin CELL — a corner — and for an entity-anchored contraption it is
     * wherever the anchor happens to put it. When that one spot landed inside solid terrain (or any
     * unlit pocket) the reading was 0 and <b>the entire contraption rendered pitch black</b>, however
     * bright the world around it actually was.
     *
     * <p>Sampling at each cell's own projected position is both correct and better: a cell in shadow is
     * darker than one in the sun, which is what a real structure looks like. The reading is of the REAL
     * world at that point — the contraption's own blocks live in a hidden level and are not there to
     * occlude it — so an airborne contraption reads open sky, as it should.
     *
     * <p>{@code realLevel} null (no live Bukkit world, e.g. unit tests) falls back to full-bright,
     * which is what this class did unconditionally before it read light at all.
     */
    private int[] ambientAt(ServerLevel realLevel, BlockPos local, Vec3 worldPos, boolean refresh) {
        if (realLevel == null) {
            return FULL_BRIGHT;
        }
        int[] cached = ambientCache.get(local);
        if (cached != null && !refresh) {
            return cached;
        }
        int[] sampled;
        try {
            BlockPos at = BlockPos.containing(worldPos.x, worldPos.y, worldPos.z);
            sampled = new int[] {
                    realLevel.getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(at),
                    realLevel.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(at) };
        } catch (Throwable ignored) {
            // Defensive: never let a light-engine read failure blank a cell's rendering.
            sampled = FULL_BRIGHT;
        }
        ambientCache.put(local.immutable(), sampled);
        return sampled;
    }

    private static final int[] FULL_BRIGHT = { 15, 15 };

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
    private int ambientBlockLightWithEmitters(BlockPos offset, int ambientBlockLight) {
        int best = ambientBlockLight;
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

        /**
         * The cell's RAW captured state, kept alongside the possibly-blanked {@link #blockState} this cell
         * actually renders (see {@link ContraptionDisplaySwarm#displayStateOf} — 2026-07-16 phantom bed fix).
         * Identical to {@link #blockState} for every cell that isn't blanked, i.e. all but a CE
         * special-renderer carrier with an entity_renderer.
         *
         * <p>Exists so blanking stays a PURELY VISUAL change: {@link #lightEmission} must keep answering
         * from the real captured block, never from the {@code minecraft:air} stand-in. Beds (the reported
         * case) emit 0 either way, but the special-renderer family also contains {@code ConduitBlock}, which
         * emits 15 — reading emission off the blanked state would silently switch such a cell's own glow, and
         * every neighbouring cell's falloff contribution, from 15 to 0 (see
         * {@link ContraptionDisplaySwarm#ambientBlockLightWithEmitters}). Cheap insurance against the fix
         * leaking out of the rendering layer it belongs to.
         */
        private BlockState rawState;

        /**
         * Extra body yaw (DEGREES) this cell adds to the bearing's own yaw before it goes into the
         * spawn/position-sync packets — see {@link ContraptionDisplaySwarm#modelYawOffsetOf} for the full
         * derivation. {@code 0} for every cell but a captured bed's single anchor half, in which case every
         * yaw this class sends is byte-for-byte the pre-fix value.
         */
        private float modelYawOffsetDegrees;

        /**
         * Set when {@link #modelYawOffsetDegrees} actually changed, to force ONE position-sync resend even on
         * an idle ({@code moved == false}) contraption — the offset rides in the position packet, not in the
         * metadata, so the existing {@code metaDirty} resend cannot carry it. Only reachable if a live
         * blockstate change flips a cell's bed anchor/facing mid-flight (a {@code setblock} inside the
         * mini-dimension); costs nothing otherwise.
         */
        private volatile boolean yawOffsetDirty = false;
        private volatile boolean metaDirty = false;
        private final Object despawnPacket;
        private final Set<UUID> shownTo = ConcurrentHashMap.newKeySet();

        // Live-tracked brightness (see ContraptionDisplaySwarm's own "Ambient lighting" javadoc) —
        // -1 sentinel so the very first #render call always counts as a change and sends real
        // metadata instead of silently reusing whatever spawn() happened to send.
        private int blockLight = -1;
        private int skyLight = -1;

        /**
         * This cell's live PITCH (radians, roadmap item #9 phase 5 — TIPPING) — drives the model-tilt
         * LeftRotation in {@link #metadata}. 0 for any never-tipped body, in which case {@link #metadata} takes
         * the pitch-0 fast path and emits byte-for-byte the pre-pitch packet. A change flags a metadata resend
         * exactly like a brightness/blockstate change (see {@link #updatePitch}).
         */
        private double pitchRadians = 0.0;

        /**
         * This cell's live ROLL (radians, roadmap item #9 phase 6 — the horizontal twin of {@link #pitchRadians})
         * — drives the model-tilt LeftRotation in {@link #metadata} together with pitch. 0 for any never-rolled
         * body, in which case (with pitch also 0) {@link #metadata} takes the tilt-0 fast path and emits
         * byte-for-byte the pre-tilt packet. A change flags a metadata resend exactly like a pitch change (see
         * {@link #updateRoll}).
         */
        private double rollRadians = 0.0;

        /**
         * This cell's live uniform SCALE (roadmap item #9 — per-contraption {@code scale}) — drives the
         * model {@code DisplayData.Scale} + recentring translation in {@link #metadata}. {@code 1.0} for any
         * never-scaled body, in which case {@link #metadata} takes the scale-1 fast path and emits the
         * byte-for-byte pre-scale packet (NO {@code Scale} metadata — {@code (1,1,1)} is the vanilla default).
         * A change flags a metadata resend exactly like a brightness/pitch/blockstate change (see
         * {@link #updateScale}).
         */
        private double scale = 1.0;

        Cell(BlockState blockState, BlockState rawState) {
            this.blockState = blockState;
            this.rawState = rawState;
            this.modelYawOffsetDegrees = modelYawOffsetOf(blockState);
            this.despawnPacket = MNms.INSTANCE.constructor$ClientboundRemoveEntitiesPacket(IntList.of(entityId));
        }

        /**
         * Swaps in a freshly-read live {@link BlockState}, flagging a metadata resend if it actually changed.
         * {@code current} is the RENDER state (already through
         * {@link ContraptionDisplaySwarm#displayStateOf}); {@code rawCurrent} is the unblanked captured state
         * kept only for {@link #lightEmission} (see {@link #rawState}). Only a RENDER-state change dirties
         * metadata — the raw state is not in any packet, so a change there alone must not cost a resend.
         */
        /** The render {@link BlockState} this cell's block_display shows — used to compute its client XZ offset. */
        BlockState displayState() {
            return blockState;
        }

        void updateIfChanged(BlockState current, BlockState rawCurrent) {
            this.rawState = rawCurrent;
            if (!current.equals(this.blockState)) {
                this.blockState = current;
                this.metaDirty = true;
                // The bed-anchor facing correction is a pure function of the RENDER state, so it can only
                // ever move when the render state does — recompute it here rather than per-viewer per-tick.
                float offset = modelYawOffsetOf(current);
                if (offset != this.modelYawOffsetDegrees) {
                    this.modelYawOffsetDegrees = offset;
                    this.yawOffsetDirty = true;
                }
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
            return rawState.getLightEmission(); // raw, never the blanked render state — see #rawState
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

        /** Updates this cell's live pitch (roadmap item #9 phase 5), flagging a metadata resend only when it actually changed. */
        void updatePitch(double pitch) {
            if (pitch != this.pitchRadians) {
                this.pitchRadians = pitch;
                this.metaDirty = true;
            }
        }

        /** Updates this cell's live roll (roadmap item #9 phase 6), flagging a metadata resend only when it actually changed. */
        void updateRoll(double roll) {
            if (roll != this.rollRadians) {
                this.rollRadians = roll;
                this.metaDirty = true;
            }
        }

        /** Updates this cell's live scale (roadmap item #9), flagging a metadata resend only when it actually changed. */
        void updateScale(double scale) {
            if (scale != this.scale) {
                this.scale = scale;
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
            // Uniform model scale (roadmap item #9 — per-contraption `scale`). `s` sizes the block MODEL
            // itself so it matches the scaled cell SPACING that ContraptionDisplaySwarm#render already applies
            // to this entity's POSITION. The recentring translation is scaled by `s` too so the model still
            // pivots/recentres about the block's own centre at any size (compose = Translation + Rot·(Scale·v),
            // so translation = Rot·(-0.5s,-0.5s,-0.5s) makes the effective transform Rot·(s·(v−0.5)) — a clean
            // centre-anchored scale). The Scale metadata is emitted ONLY when scaled: at s == 1 the vanilla
            // default is exactly (1,1,1), so sending nothing keeps the scale-1 packet byte-for-byte identical
            // to the pre-scale one (verified against the pitch-0 and pitch!=0 branches below).
            float s = (float) scale;
            boolean scaled = scale != 1.0;
            if (pitchRadians == 0.0 && rollRadians == 0.0) {
                // TILT-0 FAST PATH: at scale 1 this is EXACTLY the pre-tilt/pre-scale metadata — the
                // recentring translation only, NO LeftRotation, NO Scale. Recentres the corner-anchored [0,1]
                // block model onto the entity position so entity-yaw rotation pivots around the block's own
                // centre (vanilla /tp-yaw block_display technique). Every never-tilted, never-scaled
                // contraption emits this identical packet.
                DisplayData.Translation.addEntityData(new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s), values);
                if (scaled) {
                    DisplayData.Scale.addEntityData(new org.joml.Vector3f(s, s, s), values);
                }
            } else {
                // TILTING (roadmap item #9 phase 5 PITCH + phase 6 ROLL): tilt the block MODEL about its own
                // centre by the combined pitch+roll orientation via the transformation LeftRotation quaternion —
                // the one lever a block_display exposes for tilt, since the entity body can only carry yaw. The
                // quaternion is rotateX(pitch).rotateZ(roll), matching the pitch∘roll part of
                // ContraptionLevel#realOrientationOf and ContraptionMath#rotateYawPitchRoll exactly (the entity
                // body yaw, sent in the position packet, supplies the outer yaw spin). LeftRotation pivots the
                // model about its corner (0,0,0), so to tilt about the block CENTRE it is paired with
                // translation = LeftRotation·(-0.5,-0.5,-0.5): the compose (Translation + LeftRotation·v) then
                // equals LeftRotation·(v−0.5), a clean centre-pivot tilt that still reduces to the (-0.5,-0.5,-0.5)
                // recentre at pitch 0 && roll 0.
                //
                // HONEST CAVEAT — see this class's own "ROTATION VIA ENTITY YAW" javadoc: an earlier extensive
                // attempt found a block_display IGNORED LeftRotation entirely (server logs proved the quaternion
                // was sent every tick, yet the model never rotated), which is why continuous YAW is driven by
                // entity body yaw here rather than by LeftRotation. This project's ContraptionPistonShaftSwarm
                // DOES rotate an ITEM_DISPLAY via LeftRotation, proving LeftRotation works at the protocol level
                // for item displays — but whether a BLOCK_DISPLAY honours it could NOT be re-verified for this
                // change (no live server was available). This pitch+roll LeftRotation is therefore BEST-EFFORT
                // model tilt: if the client honours it the blocks visibly lean their models; if it is the
                // documented no-op for block_display the model tilt is simply absent — but the whole structure
                // STILL leans, because every cell's POSITION already orbits to its tilted rigid coordinate
                // (position-tilt, GUARANTEED, in ContraptionDisplaySwarm#render). Nothing regresses either way.
                Quaternionf tiltQ = new Quaternionf().rotateX((float) pitchRadians).rotateZ((float) rollRadians);
                // Recentring translation scaled by `s` (see the tilt-0 branch's Scale note): the compose
                // becomes Translation + tiltQ·(Scale·v) = tiltQ·(s·(v−0.5)), a centre-pivot tilt at any size.
                // At s == 1 this reduces to tiltQ·(-0.5,-0.5,-0.5) with no Scale — byte-for-byte the pre-scale
                // tilt packet.
                org.joml.Vector3f t = tiltQ.transform(new org.joml.Vector3f(-0.5f * s, -0.5f * s, -0.5f * s));
                DisplayData.Translation.addEntityData(t, values);
                DisplayData.LeftRotation.addEntityData(tiltQ, values);
                if (scaled) {
                    DisplayData.Scale.addEntityData(new org.joml.Vector3f(s, s, s), values);
                }
            }
            // Live real-world brightness (see class javadoc, "Ambient lighting") — NOT a hardcoded
            // full-bright constant anymore. blockLight/skyLight are clamped 0-15 by the swarm
            // before ever reaching here (see #ambientBlockLightWithEmitters/#ambientAt).
            DisplayData.BrightnessOverride.addEntityData((blockLight << 4) | (skyLight << 20), values);
            // Smooth the TRANSFORM too (the LeftRotation pitch/roll tilt lives here, NOT in the position packet).
            // Without this, a tilt change resent via metadata SNAPS — the abrupt jump the user saw when an
            // already-asleep body (whose displays already exist) recomputes: "cuando un body ya está dormido igual
            // debes suavizar eso para que el tilt no sea brusco". Delay 0 = start now; duration = the same
            // distance-LOD window the position uses, so the tilt eases in over N ticks instead of popping.
            DisplayData.TransformationInterpolationDelay.addEntityData(0, values);
            DisplayData.TransformationInterpolationDuration.addEntityData(Math.max(2, interpDuration), values);
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

        /**
         * Whether {@code p} is too far from this cell's world position to be worth projecting to.
         *
         * <p>Asymmetric by design: an already-shown cell holds until {@link #RENDER_DISTANCE} +
         * {@link #RENDER_DISTANCE_HYSTERESIS}, while a hidden one only appears inside
         * {@link #RENDER_DISTANCE}. A player walking the boundary would otherwise spawn and despawn every
         * cell of the contraption on alternate ticks — far more expensive than simply rendering it.
         */
        private boolean outOfRange(Player p, double x, double y, double z) {
            Object pp = p.platformPlayer();
            if (!(pp instanceof org.bukkit.entity.Player b)) {
                return false;
            }
            UUID id = b.getUniqueId();
            double limit = shownTo.contains(id) ? RENDER_DISTANCE + RENDER_DISTANCE_HYSTERESIS : RENDER_DISTANCE;
            org.bukkit.Location loc = b.getLocation();
            double dx = loc.getX() - x;
            double dy = loc.getY() - y;
            double dz = loc.getZ() - z;
            return dx * dx + dy * dy + dz * dz > limit * limit;
        }

        void render(List<Player> viewers, double x, double y, double z, float yawDegrees, double yawRadians,
                double pitchRadians, double rollRadians, double scale, boolean moved, int blockLight, int skyLight,
                Map<UUID, int[]> lod) {
            updateLight(blockLight, skyLight); // flags metaDirty itself if this cell's brightness actually changed
            updatePitch(pitchRadians); // flags metaDirty itself if this cell's tip angle actually changed
            updateRoll(rollRadians); // flags metaDirty itself if this cell's roll angle actually changed
            updateScale(scale); // flags metaDirty itself if this cell's size actually changed
            boolean metaChanged = metaDirty; // blockstate/brightness actually changed this tick
            metaDirty = false;
            // Bed anchors need their captured facing added to the bearing's yaw, because the client's
            // BedSpecialRenderer hardcodes SOUTH — see ContraptionDisplaySwarm#modelYawOffsetOf. This is
            // +0 for every other cell, so their packets stay byte-for-byte identical.
            float bodyYawDegrees = yawDegrees + modelYawOffsetDegrees;
            boolean yawOffsetChanged = yawOffsetDirty;
            yawOffsetDirty = false;
            Set<UUID> current = new java.util.HashSet<>();
            for (Player p : viewers) {
                UUID id = uuidOf(p);
                if (id == null) {
                    continue;
                }
                if (outOfRange(p, x, y, z)) {
                    // Despawn EXPLICITLY rather than just leaving them out of `current`: the
                    // retainAll below forgets a viewer without telling their client, which would
                    // strand this cell on their screen forever as an un-tracked fake entity.
                    if (shownTo.remove(id)) {
                        despawn(p);
                        lastInterp.remove(id);
                    }
                    continue;
                }
                current.add(id);
                int[] v = lod.get(id);
                boolean sendThisTick = v == null || v[0] != 0;
                int duration = v != null ? v[1] : 2;
                if (shownTo.add(id)) {
                    spawn(p, x, y, z, bodyYawDegrees, duration);
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
                    if ((moved || yawOffsetChanged) && sendThisTick) {
                        updatePosition(p, x, y, z, bodyYawDegrees);
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
