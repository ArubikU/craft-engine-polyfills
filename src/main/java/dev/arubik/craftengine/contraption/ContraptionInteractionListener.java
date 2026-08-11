package dev.arubik.craftengine.contraption;

import java.util.Optional;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.assembly.ContraptionMath;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionLevel;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.core.ContraptionState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.momirealms.craftengine.core.util.Key;

/**
 * Task 2 (CONTRAPTIONS.md 2026-07-01 session) — forwards real player interaction into the
 * real block living inside a {@link ContraptionLevel} at whatever local cell the player is
 * VISUALLY looking at (the packet-only {@code ContraptionDisplaySwarm}/hitbox cells never
 * receive real client interaction, since they aren't real blocks/entities — this listener is
 * the bridge). Per the user's explicit instruction: right-click routes through, left-click is
 * an explicit no-op (captured blocks can't be broken directly).
 *
 * <p><b>Placement (2026-07-02 follow-up session).</b> A right-click that doesn't get consumed
 * as an "interact with the existing captured block" (either because the block has no special
 * use-hook, or — see below — because sneaking skipped that step entirely) falls through to a
 * real vanilla/CraftEngine block PLACEMENT attempt using whatever's in the player's hand, via
 * {@link #tryPlace} — see that method's javadoc for the exact vanilla/CraftEngine entry points
 * used (deliberately the real {@code BlockItem#place(BlockPlaceContext)}/CraftEngine
 * {@code BlockItemBehavior#useOnBlock} dispatch, NOT a hand-rolled reimplementation of
 * orientation/{@code getStateForPlacement} logic, so stairs/logs/anything directional orients
 * exactly like a real placement would). {@link #raycast} itself needed no changes for this —
 * it already resolves a {@link BlockHitResult}-equivalent {@link Hit} against the FACE of an
 * existing captured cell, and {@link BlockPlaceContext}'s own real constructor is what decides
 * (exactly like vanilla) whether the actual target is that cell (if replaceable, e.g. tall
 * grass) or {@code hit.local().relative(hit.face())} (the common case).
 *
 * <p><b>Sneak bypass.</b> Real vanilla ({@code ServerPlayerGameMode#useItemOn}, confirmed via
 * javap) skips the "interact with existing block" step ENTIRELY whenever
 * {@code player.isSecondaryUseActive() && (either hand holds an item)} — a blanket sneak+holding
 * check, not a per-item exemption — falling through straight to the held item's placement
 * attempt instead. Replicated here (see the {@code skipInteract} local in {@link #forward}) so a
 * sneaking player next to a captured chest, holding a block, places the block instead of opening
 * the chest — matching vanilla exactly, including for CraftEngine custom containers.
 *
 * <p><b>Arm swing.</b> Since every click this listener handles is a manually-cancelled-and-
 * redispatched {@link PlayerInteractEvent}, none of vanilla's own packet-handling code that
 * would normally trigger the arm-swing animation ever runs. {@link #forward} calls
 * {@code player.swing(hand, true)} itself (confirmed via javap: {@code LivingEntity#swing}
 * both flips local swing state AND broadcasts the animation packet to real tracking players in
 * one call — no separate broadcast step needed) whenever a dispatch — interact OR placement —
 * actually consumed the click, mirroring vanilla only swinging on a consuming result.
 *
 * <p><b>Double-fire guard</b>: same as every other wand/hammer listener in this package
 * ({@link GlueWandListener}, {@link BearingHammerListener}) — gate on
 * {@code event.getHand() == EquipmentSlot.HAND} so the well-known Bukkit
 * main-hand/off-hand double-fire of {@link PlayerInteractEvent} is only handled once.
 *
 * <p><b>Raycast shape</b>: performed in each contraption's LOCAL space, which is what makes it
 * yaw-correct (2026-07-03 session — "cuando el contraption esta rotado con yaw el interaction se
 * rompe ... no sigue la rotacion y caras correcta"). The player's eye position AND look-vector
 * endpoint are transformed INTO the contraption's local frame via
 * {@link ContraptionMath#realToLocal} (the exact inverse of {@code renderPosition} — pivot/yaw
 * aware, the same transform the render swarms use, so the raycast stays consistent with where the
 * blocks visually render), then the local-space ray is clipped against each cell's axis-aligned box
 * built at the integer local {@link BlockPos} and sized to the block's REAL collision-shape bounds
 * ({@code getCollisionShape().bounds()}, so slabs/stairs get their true smaller box; unit-cube
 * fallback for empty/degenerate shapes). Ray length is {@link ServerPlayer#blockInteractionRange()}
 * (the real per-player reach vanilla uses); {@code realToLocal} is a rigid rotation+translation so
 * it preserves that distance. The closest hit across every contraption wins. Because the ray is
 * already local, the resulting hit point AND face come out as the block's OWN correct local values
 * at any yaw — the {@link BlockHitResult} is built directly from them and dispatched against the
 * {@link ContraptionLevel} with NO {@code realToLocal} round-trip at the dispatch site.
 *
 * <p><b>Prior (buggy) shape, for context</b>: the raycast used to build each cell's box
 * axis-aligned in WORLD space at the yaw-rotated {@code renderPosition} center — so the boxes sat at
 * the right centers but were never themselves rotated to match the visually-rotated blocks, making
 * clicks on a rotated contraption miss or hit the wrong cell, and the returned face was a WORLD-axis
 * face (via {@code Direction.getApproximateNearest} of a world-space relative vector) rather than
 * the block's own post-rotation local face — so interaction/placement targeted the wrong
 * face/neighbor. Raycasting in local space fixes both at once.
 *
 * <p><b>Dispatch hook</b>: {@link BlockState#useWithoutItem(net.minecraft.world.level.Level,
 * net.minecraft.world.entity.player.Player, BlockHitResult)} / {@code useItemOn} — the real
 * vanilla {@code BlockBehaviour.BlockStateBase} methods (confirmed via {@code javap} against
 * this project's own mapped server jar: neither is exposed as a friendlier hook on
 * CraftEngine's own {@code BukkitBlockBehavior} without first constructing a CraftEngine
 * {@code UseOnContext} from scratch, which needs several internal-only fields this project has
 * no existing call site constructing). Calling the vanilla method directly is exactly the
 * "raw NMS, this project already compiles against Mojang-mapped NMS" fallback the task
 * description names, and works uniformly for both vanilla AND CraftEngine-custom blocks (CE's
 * custom blocks are vanilla {@code Block} instances under the hood whose
 * {@code useWithoutItem}/{@code useItemOn} delegate into the registered
 * {@code BlockBehavior#useWithoutItem(UseOnContext, ImmutableBlockState)} internally — calling
 * the vanilla entry point reaches the exact same CE dispatch a real right-click would, without
 * this listener needing to know how to build a {@code UseOnContext} itself). GUI-container
 * blocks (chests, the miner's own status menu from Task 3, etc.) work unmodified: opening an
 * inventory view is never tied to which dimension the underlying block/block-entity lives in.
 *
 * <p><b>Furniture — still not routed through THIS listener, but for a different reason now.</b>
 * A captured {@link ContraptionFurniture} cell DOES have a live CraftEngine {@code Furniture}
 * object now (see that class's javadoc — placed for real inside the contraption's hidden
 * {@code ContraptionLevel}), but this listener's own raycast only walks
 * {@code level.localPositions()} (captured BLOCKS), so it still never matches a furniture cell.
 * The real furniture's own right-click behavior works unmodified for anyone who could reach it
 * from INSIDE that hidden dimension (nobody, normally) — from the real world, a player only ever
 * sees {@code render.ContraptionFurnitureSwarm}'s packet-only mirror, which (like every other
 * packet-only mirror in this package) cannot receive a real client interaction at all. Routing a
 * real-world click through to the fake-world live furniture instance (the same "manually build a
 * hit result, dispatch directly" bridge this listener already does for blocks) is a reasonable
 * future extension but is out of scope for this pass — the fake-world furniture object existing
 * at all (rather than pure captured config) is what makes that extension possible later; it just
 * isn't wired up yet.
 */
public final class ContraptionInteractionListener implements Listener {

    /**
     * TEMPORARY diagnostic (2026-07-01 debugging session — "click does nothing, not even the
     * raycast-miss log fires"). Runs at {@link EventPriority#LOWEST}, {@code ignoreCancelled =
     * false} — i.e. before every other listener in this plugin gets a chance to touch/cancel the
     * event, and even sees ones that were ALREADY cancelled by something else (a core plugin, a
     * different mod). If this logs but {@link #onInteract} never does, something at a priority
     * between LOWEST and HIGH is cancelling the event first. If this ALSO never logs, the event
     * genuinely never reaches the server for that click at all.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void debugRawInteract(PlayerInteractEvent event) {
        org.bukkit.Bukkit.getLogger().info("[Contraption][RAW] action=" + event.getAction() + " hand=" + event.getHand()
                + " cancelled=" + event.isCancelled() + " clickedBlock=" + event.getClickedBlock()
                + " item=" + event.getItem() + " player=" + event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void debugRawInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent event) {
        org.bukkit.Bukkit.getLogger().info("[Contraption][RAW-ENTITY] entity=" + event.getRightClicked()
                + " hand=" + event.getHand() + " cancelled=" + event.isCancelled()
                + " player=" + event.getPlayer().getName());
    }

    /**
     * Right-clicking a position covered by one of this package's own fake {@code INTERACTION}
     * entities ({@code ContraptionHitboxSwarm}'s standing hitboxes, {@code ContraptionFurnitureSwarm
     * .HitboxCell}'s furniture footprints) never reaches {@link #onInteract}: the CLIENT sees a real,
     * collidable entity there (a genuine spawned Interaction entity's clientside hit-detection, even
     * though the entity is server-side packet-only bookkeeping) and reports the click as an ENTITY
     * interaction (a totally separate Bukkit event from a block click), not a block/air click. This
     * is exactly why "right-click does nothing, not even the raycast-miss log line" was reproducible
     * for a chest sitting under one of these hitboxes — {@code onInteract} was never invoked at all.
     * Redirect the SAME block-cell raycast/dispatch here too, keyed off the player's aim (not the
     * specific entity clicked — the fake entity's exact bounds don't need to line up with the actual
     * block cell; the existing raycast already resolves whichever real captured cell the player is
     * really looking at).
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // double-fire guard — see class javadoc
        }
        // Real-entity bypass (2026-07-02 live-test fix, minecart bearing) — same landmine as
        // ContraptionInteractPacketDebug's own "Real-entity bypass" javadoc and
        // ContraptionSeatListener#onInteractEntity's identical fix: this handler's raycast is
        // keyed off the player's AIM, not event.getRightClicked(), so it could resolve a captured
        // cell near a real minecart bearing and swallow the click meant for
        // BearingHammerListener#onInteractEntity. Skip entirely for a bearing entity — it has its
        // own dedicated handler.
        if (dev.arubik.craftengine.contraption.bearing.MinecartBearing.isBearing(event.getRightClicked())) {
            return;
        }
        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        Hit hit = raycast(player);
        if (hit == null) {
            return; // the clicked entity isn't one of ours / doesn't line up with a captured cell
        }
        event.setCancelled(true);
        forward(player, hit);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // double-fire guard — see class javadoc
        }
        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR;
        boolean left = action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR;
        if (!right && !left) {
            return;
        }

        ServerPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        Hit hit = raycast(player);
        if (hit == null) {
            if (PLACEMENT_DEBUG && !ContraptionManager.all().isEmpty()) {
                org.bukkit.Bukkit.getLogger().info("[Contraption] interact raycast: no hit (" + right + "/"
                        + left + ") for " + event.getPlayer().getName());
            }
            return; // not looking at any contraption cell — let vanilla/CE handle the real click normally
        }
        if (PLACEMENT_DEBUG) {
            org.bukkit.Bukkit.getLogger()
                    .info("[Contraption] interact raycast HIT local=" + hit.local() + " right=" + right);
        }

        // Left-click now does BOTH: vanilla's non-destructive attack hook (BlockStateBase#attack —
        // note-block-style "hit to toggle") is forwarded, AND a hold-to-mine dig is armed against the
        // cell (ContraptionMining), which drives progressive server-side breaking with drops/tool/
        // durability/animation — the client can't run its own mining loop against a packet-only cell.
        event.setCancelled(true);
        if (left) {
            forwardAttack(player, hit); // non-destructive attack hook (note-block toggle, etc.)
            ContraptionMining.armDig(player, hit); // and drive progressive mining — see ContraptionMining
            return;
        }

        forward(player, hit);
    }

    /**
     * Right-clicking ANY cell of a PHYS contraption with a hammer takes it apart, wherever the body is
     * ("un phys contraption con right click de un hammer se deshace, en cualquier posicion del phys").
     *
     * <h2>Why this lives here and not in {@code BearingHammerListener}</h2>
     * Every other bearing is disassembled by hammering its ANCHOR — a real block or a real minecart, both
     * of which produce ordinary Bukkit events. A phys contraption has neither: it captured its anchor block
     * and then fell away from where it stood, so there is nothing real left to click. Its cells are
     * packet-only fake entities the server never spawned, so clicking one produces no event for that cell
     * either. This listener's raycast is the only thing that knows a player is looking at a phys cell, which
     * makes it the only place the hammer can be noticed at all.
     *
     * <p>Runs BEFORE {@link #forward}: the hammer must take the structure apart rather than be forwarded
     * into the captured block as an ordinary right-click (which would, say, open the chest it landed on).
     *
     * <p>The disassemble restores the blocks at the body's current pose. A phys body is free to be rotated,
     * tipped and scaled, and real blocks are none of those things — so the restore snaps to the grid, and a
     * body caught mid-tumble lands on the nearest cells rather than at some fractional orientation. That is
     * the same snap every other disassemble performs; it is only visible here because this is the one
     * bearing whose body is routinely NOT grid-aligned.
     */
    private static boolean tryHammerDisassemblePhys(ServerPlayer player, Hit hit) {
        ContraptionState state = hit.state();
        // A VEHICLE is a PHYS body too (piloted), so the same hammer-any-cell disassemble applies to it.
        if (!net.momirealms.craftengine.core.util.Key.of("polyfills", "phys").equals(state.bearingType())
                && !net.momirealms.craftengine.core.util.Key.of("polyfills", "vehicle").equals(state.bearingType())) {
            return false;
        }
        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player.getBukkitEntity();
        org.bukkit.inventory.ItemStack held = bukkitPlayer.getInventory().getItemInMainHand();
        if (!dev.arubik.craftengine.multiblock.HammerItems
                .isHammer(net.momirealms.craftengine.bukkit.api.CraftEngineItems.getCustomItemId(held))) {
            return false;
        }
        ContraptionEntity entity = ContraptionManager.get(state.id());
        org.bukkit.World world = null;
        try {
            MinecraftServer server = ((CraftServer) org.bukkit.Bukkit.getServer()).getServer();
            ServerLevel level = server.getLevel(state.worldId());
            world = level != null ? level.getWorld() : null;
        } catch (Throwable ignored) {
        }
        if (entity == null || world == null) {
            // Consumed regardless: one right-click can arrive twice (interactAt then interact), and the
            // second must not fall through into a level the first just disposed.
            return true;
        }
        try {
            // Release the driver (if any) before the body is gone, so no dangling helm binding survives.
            java.util.UUID driver = VehicleDriverRegistry.driverOf(state.id());
            if (driver != null) {
                VehicleDriverRegistry.clearDriver(driver);
            }
            ContraptionAssembler.disassemble(world, entity);
            world.playSound(bukkitPlayer.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            bukkitPlayer.sendMessage("§7Contraption disassembled, blocks restored.");
        } catch (Throwable t) {
            org.bukkit.Bukkit.getLogger().warning("[Contraption] phys hammer disassemble failed: " + t);
        }
        return true;
    }

    /**
     * @param localClip the hit point already in the contraption's LOCAL coordinate space (see
     *        {@link #raycast} — the ray is un-rotated into local space BEFORE clipping, so both this
     *        point and {@code face} are already local; no {@code realToLocal} round-trip is needed at
     *        the dispatch sites).
     */
    public record Hit(ContraptionState state, BlockPos local, Vec3 localClip, Direction face) {
    }

    /**
     * See class javadoc "Raycast shape". Yaw-correct as of the 2026-07-03 session ("cuando el
     * contraption esta rotado con yaw el interaction se rompe ... no sigue la rotacion y caras
     * correcta"): instead of building each cell's AABB axis-aligned in WORLD space (which left the
     * boxes at yaw-rotated CENTERS but never rotated the boxes themselves, so a rotated contraption's
     * clicks missed / hit the wrong cell, and the returned face was a WORLD-axis face rather than the
     * block's own local face), the player's eye/look ray is transformed INTO the contraption's local
     * frame via {@link ContraptionMath#realToLocal} (the exact inverse of {@code renderPosition},
     * pivot/yaw aware — same transform the render swarms use, so the raycast stays consistent with
     * where blocks visually render). The ray is then clipped against each cell's axis-aligned box
     * built at the integer local {@link BlockPos} — sized to the block's REAL collision shape
     * ({@code BlockState#getCollisionShape(level, local).bounds()}) so slabs/stairs/etc. get their
     * true (smaller) box, falling back to a unit cube for empty/degenerate shapes. Because the ray is
     * already in local space, the resulting hit point AND face are naturally the block's own correct
     * local values at any yaw. {@code realToLocal} is a rigid rotation+translation, so it preserves
     * distances — {@code blockInteractionRange()} stays the correct ray length.
     */
    public static Hit raycast(ServerPlayer player) {
        double maxDistance = player.blockInteractionRange();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDistance));

        Hit best = null;
        double bestDistSq = Double.MAX_VALUE;
        int contraptionCount = 0;
        for (ContraptionEntity entity : ContraptionManager.all()) {
            contraptionCount++;
            ContraptionState state = entity.state();
            ContraptionLevel level = state.level();
            if (level == null) {
                continue;
            }
            Vec3 bearingPos = new Vec3(state.x(), state.y(), state.z());
            double yaw = state.yawRadians();
            double pitch = state.pitchRadians();
            double roll = state.rollRadians();
            double scale = state.scale();
            // Un-rotate the ray into this contraption's LOCAL frame (both endpoints) — now the ray is
            // in the same axis-aligned space the captured cells live in, so a plain axis-aligned clip
            // against each cell's UNIT local box is correct at any yaw/pitch/scale. realToLocal is the
            // exact inverse of the canonical renderPosition mapping (pivot/yaw/pitch aware, and it DIVIDES
            // by scale), so passing pitch+scale here — previously only yaw was passed, so clicks on a
            // tipped or resized contraption missed/hit the wrong cell — lands the ray in the un-scaled,
            // un-tilted local frame where each cell is exactly [local, local+1] (localCellBox below).
            Vec3 localEye = ContraptionMath.realToLocal(eye, bearingPos, yaw, pitch, roll, scale);
            Vec3 localEnd = ContraptionMath.realToLocal(end, bearingPos, yaw, pitch, roll, scale);
            org.bukkit.Bukkit.getLogger().info("[Contraption] raycast vs contraption bearing=" + bearingPos
                    + " yaw=" + yaw + " cells=" + level.localPositions().size() + " localEye=" + localEye
                    + " localEnd=" + localEnd + " maxDist=" + maxDistance);
            for (BlockPos local : level.localPositions()) {
                AABB box = localCellBox(level, local);
                Optional<Vec3> clip = box.clip(localEye, localEnd);
                if (clip.isEmpty()) {
                    continue;
                }
                // Distance measured in local space is 1/scale of real space (realToLocal divides by
                // scale), so rescale to WORLD distance (×scale²) before comparing across contraptions —
                // otherwise a larger-scaled contraption's hits would look spuriously "closer." At scale 1
                // this is exactly the previous local==world distance.
                double distSq = localEye.distanceToSqr(clip.get()) * scale * scale;
                if (distSq < bestDistSq) {
                    bestDistSq = distSq;
                    // Face is the box face nearest the local hit point — now a genuine LOCAL face
                    // (the block's own front/side/top regardless of the contraption's yaw), because
                    // both the box and the ray are in local space.
                    Vec3 center = box.getCenter();
                    Vec3 rel = clip.get().subtract(center);
                    Direction face = Direction.getApproximateNearest(rel.x, rel.y, rel.z);
                    best = new Hit(state, local, clip.get(), face);
                }
            }
        }
        if (best == null) {
            org.bukkit.Bukkit.getLogger().info("[Contraption] raycast: " + contraptionCount + " contraption(s) checked, no cell clip hit");
        }
        return best;
    }

    /**
     * The LOCAL-space AABB for one captured cell — the block's REAL collision-shape bounds shifted to
     * the cell's integer {@link BlockPos} (so slabs/stairs/etc. get their true smaller box, per the
     * user's "follow the block's AABB" ask), falling back to a full unit cube for an empty/degenerate
     * shape (e.g. a captured torch/plant, or anything whose {@code getCollisionShape} is empty) so a
     * clickable-but-non-colliding block is still hittable. {@code getCollisionShape} is available here
     * because {@link ContraptionLevel} genuinely IS a {@code ServerLevel}.
     */
    private static AABB localCellBox(ContraptionLevel level, BlockPos local) {
        AABB unit = new AABB(local.getX(), local.getY(), local.getZ(),
                local.getX() + 1, local.getY() + 1, local.getZ() + 1);
        try {
            BlockState state = level.getBlockState(local);
            if (state.isAir()) {
                return unit;
            }
            net.minecraft.world.phys.shapes.VoxelShape shape = state.getCollisionShape(level.serverLevel(), local);
            if (shape.isEmpty()) {
                return unit; // no real collision (torch/plant/etc.) — keep it clickable as a full cube
            }
            AABB b = shape.bounds();
            // shape.bounds() is cell-relative (0..1); offset to the cell's integer position.
            return b.move(local.getX(), local.getY(), local.getZ());
        } catch (Throwable t) {
            return unit; // best-effort — a shape-query failure just falls back to a unit cube
        }
    }

    /** See class javadoc "Dispatch hook". */
    public static void forward(ServerPlayer player, Hit hit) {
        // Mark this as a right-click USE so the arm-swing the client sends alongside it is not mistaken for a
        // mining attack (2026-07-18 — "right y left click se consideran igual"). Every right-click dispatch —
        // block interact, entity interact, and the phys-contraption packet path — funnels through here.
        ContraptionMining.noteUse(player.getUUID());
        // Checked HERE rather than at any one call site, because three separate paths reach this: the
        // PlayerInteractEvent raycast, the PlayerInteractEntityEvent raycast, and — the one that actually
        // matters for a phys contraption — ContraptionInteractPacketDebug's PacketEvents listener. A phys
        // contraption's cells are packet-only fake entities the server never spawned, so a click on one is
        // dropped by vanilla and only ever surfaces through that packet listener. Hooking the hammer into
        // a single event handler put it on the one path the click never takes.
        if (tryHammerDisassemblePhys(player, hit)) {
            return;
        }
        ContraptionLevel level = hit.state().level();
        if (level == null) {
            return;
        }
        BlockState blockState = level.getBlockState(hit.local());
        if (blockState.isAir()) {
            return;
        }

        // Public API veto (ContraptionInteractEvent) — fired BEFORE any dispatch/placement runs.
        // Cancelling makes this right-click a no-op, exactly as if the raycast had missed.
        if (fireInteractCancelled(player, hit, true)) {
            return;
        }

        // hit.localClip()/hit.face() are already in local space (raycast un-rotated the ray before
        // clipping — see #raycast), so the BlockHitResult is built directly against the cell, no
        // realToLocal round-trip needed.
        BlockHitResult hitResult = new BlockHitResult(hit.localClip(), hit.face(), hit.local(), false);

        AbstractContainerMenu menuBefore = player.containerMenu;
        org.bukkit.Bukkit.getLogger().info("[Contraption] pre-dispatch blockEntity@" + hit.local() + "="
                + level.getBlockEntity(hit.local()));
        boolean consumed = false;
        InteractionHand swungHand = InteractionHand.MAIN_HAND;

        // Sneak-bypasses-interact gate (2026-07-02 follow-up — "sigo podiendo abrir cofres cuando
        // shifteo"). Confirmed via javap against this project's own mapped server jar,
        // net.minecraft.server.level.ServerPlayerGameMode#useItemOn bytecode: vanilla computes
        // `skipInteract = player.isSecondaryUseActive() && (!mainHand.isEmpty() ||
        // !offHand.isEmpty())` -- i.e. sneaking AND holding something in at least one hand -- and
        // when true, skips straight past BlockState#useItemOn/useWithoutItem entirely (never
        // calls either) to the held item's own ItemStack#useOn (the placement path, see #tryPlace
        // below). This is a blanket hand/sneak-state check, NOT a per-item
        // "doesSneakBypassUse"-style exemption -- no such per-item override exists at this call
        // site (confirmed by reading the actual bytecode: the boolean only reads
        // isSecondaryUseActive() and the two isEmpty() checks, nothing item-specific). Replicated
        // exactly here so a sneaking player holding a block item next to a captured chest places
        // the block instead of opening the chest, matching real vanilla.
        boolean anyHandHeld = !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
                || !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
        boolean skipInteract = player.isSecondaryUseActive() && anyHandHeld;

        try {
            if (!skipInteract) {
                Optional<net.momirealms.craftengine.core.block.ImmutableBlockState> customState =
                        net.momirealms.craftengine.bukkit.util.BlockStateUtils.getOptionalCustomBlockState(blockState);
                if (customState.isPresent()) {
                    // CraftEngine custom block — bypass the vanilla useItemOn/useWithoutItem bridge
                    // entirely. Confirmed via live log evidence ("mainHand dispatch result=Pass[]
                    // consumes=false" every single time for a captured crate/storage block, whose own
                    // CraftEngine-level useWithoutItem ALWAYS looks up the block entity via
                    // `((CraftWorld)((BukkitWorld) context.getLevel()).platformWorld()).getHandle()`):
                    // whatever internal UseOnContext CraftEngine's own vanilla-Block-subclass bridge
                    // builds when we call the vanilla method does NOT correctly resolve
                    // `context.getLevel()` to THIS ContraptionLevel — it's not part of any real chunk
                    // tracking or normal player interaction path CraftEngine expects, so its lookup
                    // silently misses and every custom block behavior sees "no block entity here"
                    // regardless of what's actually captured. Building the UseOnContext OURSELVES
                    // (via the same public net.momirealms...BukkitAdaptor#adapt(World)/adapt(Player)
                    // this project already has access to) and calling BlockBehavior#useOnBlock/
                    // #useWithoutItem directly guarantees the context wraps the CORRECT world.
                    InteractionResult ceResult = dispatchCraftEngineCustomBlock(customState.get(), level, player, hit);
                    org.bukkit.Bukkit.getLogger().info("[Contraption] CE-direct dispatch result=" + ceResult
                            + " consumes=" + ceResult.consumesAction());
                    consumed = ceResult.consumesAction();
                } else {
                    // Real vanilla order (confirmed via javap against this project's own mapped
                    // server jar, net.minecraft.server.level.ServerPlayerGameMode#useItemOn): the
                    // held item's OWN block interaction -- BlockState#useItemOn(stack, level, player,
                    // hand, hit) -- is tried FIRST for whichever hand actually holds an item
                    // (bucket-fill, cauldron/composter/note-block-with-item interactions all live
                    // here), for BOTH MAIN_HAND and OFF_HAND, falling back to useWithoutItem on a
                    // non-consuming result for MAIN_HAND only. This vanilla-bridge path is only used
                    // for genuinely REAL vanilla blocks (chest/furnace/barrel/...) now — confirmed
                    // working via live testing; CraftEngine custom blocks use the direct path above.
                    InteractionResult mainResult = tryHand(blockState, level, player, hitResult, InteractionHand.MAIN_HAND, true);
                    org.bukkit.Bukkit.getLogger().info("[Contraption] mainHand dispatch result=" + mainResult
                            + " consumes=" + mainResult.consumesAction());
                    consumed = mainResult.consumesAction();
                    if (!consumed) {
                        ItemStack offHeld = player.getItemInHand(InteractionHand.OFF_HAND);
                        if (!offHeld.isEmpty()) {
                            InteractionResult offResult = tryHand(blockState, level, player, hitResult, InteractionHand.OFF_HAND, false);
                            org.bukkit.Bukkit.getLogger().info("[Contraption] offHand dispatch result=" + offResult
                                    + " consumes=" + offResult.consumesAction());
                            if (offResult.consumesAction()) {
                                consumed = true;
                                swungHand = InteractionHand.OFF_HAND;
                            }
                        }
                    }
                }
            } else {
                org.bukkit.Bukkit.getLogger().info("[Contraption] sneak-bypass: skipping interact-with-block, "
                        + "falling through to placement for " + player.getName().getString());
            }

            if (!consumed) {
                // Nothing above consumed the click as an "interact with the existing block" —
                // per the real vanilla dispatch order (ServerPlayerGameMode#useItemOn), the next
                // thing tried is the HELD ITEM's own useOn, which for a BlockItem/CraftEngine
                // custom block-item is a real PLACEMENT attempt. See #tryPlace's own javadoc for
                // exactly how this is wired to avoid re-deriving vanilla's orientation/context
                // math by hand.
                PlaceOutcome mainPlace = tryPlace(level, player, hitResult, InteractionHand.MAIN_HAND, hit);
                if (mainPlace.consumed()) {
                    consumed = true;
                } else {
                    ItemStack offHeld = player.getItemInHand(InteractionHand.OFF_HAND);
                    if (!offHeld.isEmpty()) {
                        PlaceOutcome offPlace = tryPlace(level, player, hitResult, InteractionHand.OFF_HAND, hit);
                        if (offPlace.consumed()) {
                            consumed = true;
                            swungHand = InteractionHand.OFF_HAND;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // A misbehaving custom block's interact hook shouldn't take down the listener for
            // every other contraption/player — same fail-open shape used throughout this
            // package (e.g. ContraptionFurnitureCapture's per-piece try/catch) — but LOG it
            // instead of silently swallowing, otherwise a real bug here is indistinguishable
            // from "nothing happened" (this is exactly what hid the interaction gap earlier).
            org.bukkit.Bukkit.getLogger().warning("[Contraption] interact dispatch threw: " + t);
            t.printStackTrace();
        }

        org.bukkit.Bukkit.getLogger().info("[Contraption] menuBefore=" + menuBefore + " menuAfter=" + player.containerMenu);
        if (player.containerMenu != menuBefore) {
            fixVanillaMenuStillValid(player.containerMenu, level, hit.local());
            // Menus with no block entity (crafting table, anvil, enchanting, ...) have no Container to
            // swap and validate through a ContainerLevelAccess instead — see the method's javadoc.
            fixVanillaMenuLevelAccess(player.containerMenu, level, hit.local());
        }

        // Force the display swarm to resend this cell's metadata right away (2026-07-02 session
        // — "TankBlockBehavior... el modelo en el render del contraption no se actualizaba").
        // ContraptionDisplaySwarm.render()'s own passive per-tick BlockState re-read/compare
        // SHOULD already catch this, but calling markDirty explicitly here guarantees the visual
        // updates the exact same tick as the interaction, regardless of any blockstate-identity
        // quirk (stale cached property instances, CE's shared-proxy-blockstate multi-appearance
        // system, etc.) that might otherwise cause the passive comparison to miss a real change.
        // A successful PLACEMENT lands on a cell that was air a moment ago, so it may not even be
        // in localPositions() yet -- markDisplayDirty is still safe to call (see that method/
        // ContraptionDisplaySwarm#markDirty javadoc), and the very next tick's rebuildSwarm ->
        // refreshLocalPositions (ContraptionEngine.tickAll) picks up the newly-occupied cell
        // through its own neighbor-of-tracked-cell scan regardless.
        ContraptionEntity entity = ContraptionManager.get(hit.state().id());
        if (entity != null) {
            entity.markDisplayDirty(hit.local());
            // A successful PLACEMENT may have landed at the face-relative neighbor cell rather
            // than hit.local() itself (BlockPlaceContext's own real "clicked block isn't
            // replaceable -> relative(face)" logic — see #tryPlace javadoc) — mark that cell
            // dirty too so its very first render happens the same tick, not next tick's passive
            // refreshLocalPositions/rebuildSwarm pass.
            entity.markDisplayDirty(hit.local().relative(hit.face()));
        }

        // Arm-swing animation fix (task 4): since this whole click was cancelled and dispatched
        // manually, none of vanilla's own ServerGamePacketListenerImpl#handleUseItemOn code ever
        // ran, so the swing that would normally happen automatically there never fired. Confirmed
        // via javap against this project's own mapped server jar: real vanilla calls
        // `player.swing(hand, true)` itself right after a successful dispatch (result is a
        // consuming "Success" whose swingSource is SERVER) — Player#swing both flips the local
        // swing animation state AND broadcasts a ClientboundAnimatePacket to every real tracking
        // player in one call, so a single call here is everything needed for every nearby real
        // player to see the swing, exactly like a normal right-click. Only swing on an actual
        // consuming result -- a PASS (nothing here to interact with/place) shouldn't animate,
        // matching vanilla (which only swings on Success).
        if (consumed) {
            player.swing(swungHand, true);
        }
    }

    /** Outcome of a single-hand placement attempt — see {@link #tryPlace}. */
    private record PlaceOutcome(boolean consumed) {
    }

    /**
     * Attempts real block PLACEMENT for one hand's held item against the cell/face this
     * listener's raycast resolved, going through the SAME real vanilla/CraftEngine entry points
     * a genuine right-click placement uses — per explicit project direction, this deliberately
     * does NOT hand-roll {@code BlockItem#getStateForPlacement}/orientation math itself, so
     * stairs/logs/CraftEngine directional blocks/anything else with placement-context-driven
     * state all orient exactly like a real placement would, "for free," and stay correct for any
     * future block type without this listener needing to know about it.
     *
     * <p><b>Vanilla path</b> (confirmed via {@code javap} against this project's own mapped
     * server jar): a real right-click placement is {@code BlockItem#useOn(UseOnContext)}
     * building {@code new BlockPlaceContext(context)} and calling {@code this.place(ctx)} — that
     * {@code place(BlockPlaceContext)} method is {@code public}, so it's called DIRECTLY here
     * rather than going through {@code useOn}/an outer {@code UseOnContext} wrapper (this
     * listener already owns a real, correctly-typed real vanilla {@link BlockHitResult} from
     * {@link #forward}, so there is no outer context to build). {@link BlockPlaceContext}'s own
     * constructor (the real one, {@code (Level, Player, InteractionHand, ItemStack,
     * BlockHitResult)}) computes the actual target position itself — {@code
     * hitResult.getBlockPos()} if that block {@code canBeReplaced}, else
     * {@code hitResult.getBlockPos().relative(hitResult.getDirection())} — exactly {@code
     * BlockPlaceContext}'s own real construction logic, not reimplemented here. {@code place()}
     * itself does the {@code getStateForPlacement}/{@code canPlace}/{@code level.setBlock(pos,
     * state, 11)} (real {@code Block.UPDATE_ALL_IMMEDIATE}, confirmed via javap)/sound/{@code
     * stack.consume(1, player)} (creative-exempt via {@code LivingEntity#hasInfiniteMaterials()},
     * confirmed the SAME {@code getAbilities().instabuild}-backed check this project's own
     * {@code TankBlockBehavior}/{@code FluidBlockTankBehavior} already use elsewhere) sequence —
     * all real vanilla behavior, called through {@link ContraptionLevel} exactly like {@link
     * ContraptionLevel#putBlock} calls the level's own {@code setBlock}, so a newly-placed
     * block's {@code BlockEntity} (if {@code EntityBlock}) is constructed by the SAME
     * chunk-level {@code setBlock} plumbing {@code putBlock} itself relies on — no separate
     * "register the new cell" step is needed here.
     *
     * <p><b>CraftEngine custom item path</b>: mirrors {@link #dispatchCraftEngineCustomBlock}'s
     * existing "the vanilla-bridge context resolves the wrong Level" fix, extended to placement
     * — CraftEngine's own {@code BlockItemBehavior#useOnBlock(UseOnContext)} wraps its incoming
     * context into its OWN real {@code BlockPlaceContext} and calls ITS real {@code place(...)}
     * internally (confirmed via decompile: same "derive everything from {@code
     * context.getLevel()}" shape the existing interact bridge already had to work around), so
     * building the {@code UseOnContext} ourselves (wrapping {@link ContraptionLevel}'s own Bukkit
     * world via {@code BukkitAdaptor.adapt}, exactly like {@link #dispatchCraftEngineCustomBlock}
     * already does) and calling {@code useOnBlock} directly reaches CraftEngine's real placement
     * logic (its own getStateForPlacement-equivalent, its own {@code CustomBlockPlaceEvent}, etc)
     * with the context pointed at the correct level — the same "real dispatch, correct Level"
     * shape as the vanilla path above, just through CraftEngine's own API instead of NMS's.
     */
    private static PlaceOutcome tryPlace(ContraptionLevel level, ServerPlayer player, BlockHitResult hitResult,
            InteractionHand hand, Hit hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return new PlaceOutcome(false);
        }

        // Public API veto (ContraptionBlockPlaceEvent) — fired BEFORE the real vanilla/CE placement
        // below. Cancelling returns a NON-consuming outcome so the item isn't consumed and nothing is
        // written into the contraption, exactly as if placement had failed. The target cell is
        // BlockPlaceContext's own resolved target (hitResult.getBlockPos()).
        if (fireBlockPlaceCancelled(player, hit, hitResult.getBlockPos().immutable(), held, hand)) {
            return new PlaceOutcome(false);
        }

        // Chunk/CEChunk activation fix (2026-07-02 follow-up — "el fluid_block_tank... sus block
        // states no se actualiza"). Every OTHER way a block lands in a ContraptionLevel goes
        // through ContraptionLevel#putBlock, which calls ensureChunkTicking/activateCeChunk before
        // setBlock (see that method's own javadoc "Ticking" section) — without a permanent chunk
        // ticket, vanilla never promotes the chunk to BLOCK_TICKING, so the newly-placed block's
        // own BlockEntity/Controller never gets a ticker registered at all; without activating the
        // CEChunk, CraftEngine's own controller (FluidBlockTankBehavior.Controller, whose tick()
        // method is the ONLY thing that recomputes bottom/top/facing on a neighbor topology change
        // — see that class's Controller#tick) never gets constructed/ticked either. This placement
        // path (blockItem.place / CE's useOnBlock below) calls setBlock DIRECTLY against this level,
        // bypassing putBlock entirely, so a tank placed via this listener could sit forever without
        // a ticking Controller — its own shape never recomputes, and depending on chunk boundaries
        // it can also leave a neighbor tank's chunk unactivated the first time anything is placed
        // there. Mirrors the exact same fix ContraptionFurnitureCapture#placeInFakeLevel already
        // needed for furniture (see its own javadoc) — ensureChunkReady is idempotent per chunk.
        BlockPos targetPos = hitResult.getBlockPos().immutable();
        level.ensureChunkReady(targetPos);
        level.ensureChunkReady(targetPos.relative(hitResult.getDirection()));

        // Place with the player's aim expressed in the CONTRAPTION's frame, restoring it afterwards.
        //
        // Every orientation decision vanilla and CraftEngine make — stairs, logs, furnaces, banners,
        // getHorizontalDirection, getNearestLookingDirection — is derived from the player's live yRot,
        // and both placement paths below read it off the player rather than taking it as an argument.
        // But the block lands in the contraption's LOCAL frame, which the renderer then rotates by the
        // contraption's yaw: a cell whose local facing is F is drawn facing F + yaw. Placing with the
        // raw world yaw therefore stored F = D and drew D + yaw — the block came out turned by exactly
        // the contraption's own rotation, which is what "las rotaciones salen raras" is.
        //
        // Rotating the player instead of the result is deliberate: it is the only way to reach every one
        // of those derived decisions at once without reimplementing any of them, which this listener is
        // careful never to do (see the class javadoc). The window is inside one synchronous call, so no
        // packet observes it.
        // BOTH yaws, because vanilla reads two different ones and a block's orientation depends on which.
        //
        // A furnace asks getHorizontalDirection() -> Entity#getDirection -> Direction.fromYRot(getYRot()):
        // the BODY yaw. A piston asks getNearestLookingDirection() -> Direction.orderedByNearest ->
        // getViewYRot(1.0F), and LivingEntity OVERRIDES that to return yHeadRot: the HEAD yaw. They are
        // separate fields. Rotating only the body left every look-vector-derived block — pistons,
        // observers, droppers — reading the player's real-world head yaw and orienting to the world while
        // the yaw-derived ones correctly oriented to the contraption. That is exactly the reported split:
        // "el furnace si se pone bien pero el piston es el que falla cuando el ghast rota".
        float originalYaw = player.getYRot();
        float originalHeadYaw = player.getYHeadRot();
        float delta = (float) Math.toDegrees(hit.state().yawRadians());
        float localYaw = originalYaw - delta;
        player.setYRot(localYaw);
        player.setYHeadRot(originalHeadYaw - delta);
        try {
            PlaceOutcome outcome = placeWithAim(level, player, hitResult, hand, held, targetPos);
            if (PLACEMENT_DEBUG) {
                org.bukkit.Bukkit.getLogger().info("[ContraptionRot] contraptionYaw="
                        + String.format("%.1f", Math.toDegrees(hit.state().yawRadians()))
                        + " playerYaw=" + String.format("%.1f", originalYaw)
                        + " placedWithYaw=" + String.format("%.1f", localYaw)
                        + " face=" + hitResult.getDirection()
                        + " cell=" + targetPos
                        + " -> " + level.getBlockState(targetPos));
            }
            return outcome;
        } finally {
            player.setYRot(originalYaw);
            player.setYHeadRot(originalHeadYaw);
        }
    }

    /**
     * Logs what each placement actually resolved to. Off by default — this is diagnostic scaffolding for
     * orientation reports, which cannot be reproduced without a client, and it is per-placement noise.
     * Flip with {@code /cep show placement}.
     */
    public static volatile boolean PLACEMENT_DEBUG = false;

    /**
     * The two real placement paths, run with the player's yaw already rotated into the contraption's
     * frame by {@link #tryPlace} — see there for why.
     */
    private static PlaceOutcome placeWithAim(ContraptionLevel level, ServerPlayer player, BlockHitResult hitResult,
            InteractionHand hand, ItemStack held, BlockPos targetPos) {
        net.momirealms.craftengine.core.util.Key customItemId = net.momirealms.craftengine.bukkit.api.CraftEngineItems
                .getCustomItemId(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(held));
        if (customItemId != null) {
            boolean placed = tryPlaceCraftEngineCustomItem(level, player, hitResult, hand, held);
            if (PLACEMENT_DEBUG) {
                org.bukkit.Bukkit.getLogger().info("[Contraption] CE-item placement id=" + customItemId + " hand="
                        + hand + " placed=" + placed);
            }
            if (placed) {
                playPlaceSound(level, targetPos, hitResult.getDirection());
            }
            return new PlaceOutcome(placed);
        }

        if (!(held.getItem() instanceof BlockItem blockItem)) {
            return new PlaceOutcome(false);
        }

        BlockPlaceContext context = new BlockPlaceContext(level.serverLevel(), player, hand, held, hitResult);
        InteractionResult result = blockItem.place(context);
        if (PLACEMENT_DEBUG) {
            org.bukkit.Bukkit.getLogger().info("[Contraption] vanilla placement item=" + held + " hand=" + hand
                    + " result=" + result + " consumes=" + result.consumesAction());
        }
        if (result.consumesAction()) {
            playPlaceSound(level, targetPos, hitResult.getDirection());
        }
        return new PlaceOutcome(result.consumesAction());
    }

    /**
     * Bug-1 fix (2026-07-02 session — "placing a block into a contraption plays no block-place
     * sound"). Real vanilla placement plays the placed block's {@code SoundType#getPlaceSound}
     * via {@code BlockItem#place -> Level#playSound}, but that placement lands inside the hidden
     * {@link ContraptionLevel} where there are no real players, so the sound never reaches the
     * real-world player. Re-emit it manually, reading the block's OWN {@code SoundType} (so any
     * block — vanilla or CraftEngine-custom, whose CE blocks are vanilla {@code Block} instances
     * under the hood — gets the correct sound for free) and playing it through
     * {@link ContraptionLevel}'s own {@code playSeededSound} override, which already redirects to
     * the REAL world at the bearing's live transform (exactly the same idiom
     * {@code TankBlockBehavior#playFillOrEmptySound} relies on for its bucket sounds).
     *
     * <p>Volume/pitch match vanilla's own placement formula (confirmed via javap against this
     * project's mapped server jar, {@code BlockItem} bytecode: {@code (getVolume()+1)/2} volume,
     * {@code getPitch()*0.8} pitch, {@code SoundSource.BLOCKS}). The placement may have landed at
     * {@code hitResult.getBlockPos()} itself (a replaceable target) or its face-relative neighbor
     * (a {@code BlockPlaceContext} decision — see {@link #tryPlace} javadoc); {@code targetPos} is
     * whichever cell {@code BlockPlaceContext} resolved, so we read the placed state from there,
     * falling back to the face-relative neighbor if that cell reads air.
     */
    private static void playPlaceSound(ContraptionLevel level, BlockPos targetPos, Direction face) {
        try {
            BlockState placed = level.getBlockState(targetPos);
            if (placed.isAir()) {
                placed = level.getBlockState(targetPos.relative(face));
            }
            if (placed.isAir()) {
                return;
            }
            net.minecraft.world.level.block.SoundType soundType = placed.getSoundType();
            net.minecraft.sounds.SoundEvent placeSound = soundType.getPlaceSound();
            if (placeSound == null) {
                return;
            }
            // playSeededSound(source, x, y, z, ...) — ContraptionLevel translates (x,y,z) to the
            // real world at the bearing transform; +0.5 centres it on the placed cell.
            level.playSeededSound(null, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                    net.minecraft.core.Holder.direct(placeSound), net.minecraft.sounds.SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, 0L);
        } catch (Throwable t) {
            // Best-effort — a missing/odd sound shouldn't fail the placement itself.
            org.bukkit.Bukkit.getLogger().warning("[Contraption] playPlaceSound threw: " + t);
        }
    }

    /**
     * CraftEngine custom-item placement — see {@link #tryPlace}'s "CraftEngine custom item
     * path" javadoc. Builds the same shape of manually-constructed {@code UseOnContext} {@link
     * #dispatchCraftEngineCustomBlock} already uses for existing-block interaction, wrapping
     * THIS {@link ContraptionLevel}'s own Bukkit world, then calls the held item's {@code
     * ItemBehavior#useOnBlock(UseOnContext)} directly — for a block-placing custom item this is
     * (confirmed via decompile) {@code BlockItemBehavior}, which internally builds its own real
     * {@code BlockPlaceContext} and runs CraftEngine's real placement logic against whatever
     * Level the context says, i.e. correctly against this one.
     */
    private static boolean tryPlaceCraftEngineCustomItem(ContraptionLevel level, ServerPlayer player,
            BlockHitResult hitResult, InteractionHand hand, ItemStack held) {
        org.bukkit.inventory.ItemStack bukkitStack = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(held);
        net.momirealms.craftengine.bukkit.item.BukkitItemDefinition def = net.momirealms.craftengine.bukkit.api.CraftEngineItems
                .byItemStack(bukkitStack);
        if (def == null) {
            return false;
        }
        net.momirealms.craftengine.core.item.behavior.ItemBehavior behavior = def.behavior();
        if (!(behavior instanceof net.momirealms.craftengine.core.item.behavior.BlockItem)) {
            return false; // this custom item doesn't place a block at all (not our concern here)
        }

        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player.getBukkitEntity();
        net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer = net.momirealms.craftengine.bukkit.api.BukkitAdaptor
                .adapt(bukkitPlayer);
        net.momirealms.craftengine.bukkit.world.BukkitWorld ceWorld = net.momirealms.craftengine.bukkit.api.BukkitAdaptor
                .adapt(level.getWorld());
        net.momirealms.craftengine.core.item.Item ceItem = net.momirealms.craftengine.bukkit.api.BukkitAdaptor.adapt(bukkitStack);

        net.minecraft.core.BlockPos clickedPos = hitResult.getBlockPos();
        net.momirealms.craftengine.core.world.BlockPos ceBlockPos = new net.momirealms.craftengine.core.world.BlockPos(
                clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());
        net.momirealms.craftengine.core.util.Direction ceFace = net.momirealms.craftengine.core.util.Direction
                .valueOf(hitResult.getDirection().name());
        Vec3 clip = hitResult.getLocation();
        net.momirealms.craftengine.core.world.Vec3d ceClip = new net.momirealms.craftengine.core.world.Vec3d(clip.x,
                clip.y, clip.z);
        net.momirealms.craftengine.core.world.BlockHitResult ceHit = new net.momirealms.craftengine.core.world.BlockHitResult(
                ceClip, ceFace, ceBlockPos, false);

        net.momirealms.craftengine.core.entity.player.InteractionHand ceHand = hand == InteractionHand.MAIN_HAND
                ? net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND
                : net.momirealms.craftengine.core.entity.player.InteractionHand.OFF_HAND;
        net.momirealms.craftengine.core.world.context.UseOnContext ctx = new net.momirealms.craftengine.core.world.context.UseOnContext(
                ceWorld, cePlayer, ceHand, ceItem, ceHit);

        net.momirealms.craftengine.core.entity.player.InteractionResult result = behavior.useOnBlock(ctx);
        return result != null && result.success();
    }

    /**
     * Manually builds a {@code UseOnContext} wrapping THIS {@link ContraptionLevel} (via
     * {@code BukkitAdaptor.adapt(World)}, a public CraftEngine API this project already depends
     * on for other bridging) and calls the block's own {@link net.momirealms.craftengine.core.block.behavior.BlockBehavior}
     * hooks DIRECTLY — see the "CE-direct dispatch" comment at the call site in {@link #forward}
     * for why the vanilla useItemOn/useWithoutItem bridge doesn't work for CraftEngine custom
     * blocks living inside a {@link ContraptionLevel}. Tries {@code useOnBlock} (the item-in-hand
     * hook) first if the main hand holds an item, falling back to {@code useWithoutItem} — same
     * shape as {@link #tryHand}, just dispatched through CraftEngine's own API instead of vanilla.
     */
    private static InteractionResult dispatchCraftEngineCustomBlock(
            net.momirealms.craftengine.core.block.ImmutableBlockState ceState, ContraptionLevel level,
            ServerPlayer player, Hit hit) {
        net.momirealms.craftengine.core.block.behavior.BlockBehavior behavior = ceState.behavior();
        if (behavior == null) {
            return InteractionResult.PASS;
        }

        org.bukkit.entity.Player bukkitPlayer = (org.bukkit.entity.Player) player.getBukkitEntity();
        net.momirealms.craftengine.bukkit.plugin.user.BukkitServerPlayer cePlayer = net.momirealms.craftengine.bukkit.api.BukkitAdaptor
                .adapt(bukkitPlayer);
        net.momirealms.craftengine.bukkit.world.BukkitWorld ceWorld = net.momirealms.craftengine.bukkit.api.BukkitAdaptor
                .adapt(level.getWorld());

        net.momirealms.craftengine.core.world.BlockPos ceBlockPos = new net.momirealms.craftengine.core.world.BlockPos(
                hit.local().getX(), hit.local().getY(), hit.local().getZ());
        net.momirealms.craftengine.core.util.Direction ceFace = net.momirealms.craftengine.core.util.Direction
                .valueOf(hit.face().name());
        // Already local (see #raycast / Hit#localClip) — no realToLocal round-trip needed.
        Vec3 localClip = hit.localClip();
        net.momirealms.craftengine.core.world.Vec3d ceClip = new net.momirealms.craftengine.core.world.Vec3d(
                localClip.x, localClip.y, localClip.z);
        net.momirealms.craftengine.core.world.BlockHitResult ceHit = new net.momirealms.craftengine.core.world.BlockHitResult(
                ceClip, ceFace, ceBlockPos, false);

        ItemStack mainHeld = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!mainHeld.isEmpty()) {
            net.momirealms.craftengine.core.item.Item ceItem = net.momirealms.craftengine.bukkit.api.BukkitAdaptor
                    .adapt(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(mainHeld));
            net.momirealms.craftengine.core.world.context.UseOnContext withItemCtx = new net.momirealms.craftengine.core.world.context.UseOnContext(
                    ceWorld, cePlayer, net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND,
                    ceItem, ceHit);
            net.momirealms.craftengine.core.entity.player.InteractionResult withItemResult = behavior
                    .useOnBlock(withItemCtx, ceState);
            if (withItemResult != null && withItemResult.success()) {
                return InteractionResult.SUCCESS;
            }
        }

        net.momirealms.craftengine.core.world.context.UseOnContext emptyCtx = new net.momirealms.craftengine.core.world.context.UseOnContext(
                ceWorld, cePlayer, net.momirealms.craftengine.core.entity.player.InteractionHand.MAIN_HAND,
                null, ceHit);
        net.momirealms.craftengine.core.entity.player.InteractionResult emptyResult = behavior.useWithoutItem(emptyCtx,
                ceState);
        return (emptyResult != null && emptyResult.success()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    /**
     * One hand's worth of real vanilla dispatch — see the "Real vanilla order" comment at the
     * {@code useItemOn}/{@code useWithoutItem} call site in {@link #forward}. {@code
     * allowEmptyHandFallback} mirrors vanilla only permitting the {@code useWithoutItem}
     * fallback for {@code MAIN_HAND} (confirmed via the same {@code javap} dump: the
     * off-hand branch in {@code ServerPlayerGameMode#useItemOn} has no equivalent
     * {@code useWithoutItem} call at all).
     *
     * <p><b>Broadened fallback (2026-07-01 session — "el tanque normal... no se llena nunca se
     * llama al evento")</b>: true vanilla only falls through to {@code useWithoutItem} on the
     * special {@code TryEmptyHandInteraction} marker, NOT a plain {@code PASS}. But a CraftEngine
     * custom block (e.g. {@code TankBlockBehavior}, whose entire bucket-fill logic lives inside
     * ITS OWN {@code useWithoutItem(UseOnContext, ...)} CE-level hook — matches how chest/furnace
     * work too) is reached through CraftEngine's OWN vanilla-{@code Block}-subclass bridge, and
     * there is no confirmed guarantee that bridge's {@code useItemOn} override actually emits the
     * exact {@code TryEmptyHandInteraction} marker vanilla's default implementation does — if it
     * returns a plain {@code PASS} instead, the old marker-only check silently skipped the
     * {@code useWithoutItem} fallback and CraftEngine's real fill hook was NEVER reached, matching
     * exactly the reported symptom. Falling back on ANY non-consuming result (not just the
     * marker) is a strict superset of the old behavior — it can only make MORE interactions reach
     * {@code useWithoutItem}, never fewer.
     */
    private static InteractionResult tryHand(BlockState blockState, ContraptionLevel level, ServerPlayer player,
            BlockHitResult hitResult, InteractionHand hand, boolean allowEmptyHandFallback) {
        ItemStack held = player.getItemInHand(hand);
        InteractionResult result = blockState.useItemOn(held, level.serverLevel(), player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }
        if (allowEmptyHandFallback) {
            return blockState.useWithoutItem(level.serverLevel(), player, hitResult);
        }
        return result;
    }

    /**
     * Left-click dispatch — {@link BlockState#attack(net.minecraft.world.level.Level,
     * BlockPos, net.minecraft.world.entity.player.Player)}, vanilla's non-destructive
     * left-click hook (confirmed via {@code javap} against this project's mapped server jar:
     * {@code void attack(Level, BlockPos, Player)} on {@code BlockStateBase}, distinct from
     * actually breaking the block — breaking still has no analog here, see the class javadoc).
     * No polyfill block currently overrides {@code BlockBehavior#attack}, but the hook is a
     * real, reachable vanilla dispatch point for a block living inside a
     * {@link ContraptionLevel}, so it is forwarded the same way {@code useWithoutItem}/
     * {@code useItemOn} are below, for parity with any future/third-party custom block that
     * relies on it.
     */
    public static void forwardAttack(ServerPlayer player, Hit hit) {
        ContraptionLevel level = hit.state().level();
        if (level == null) {
            return;
        }
        BlockState blockState = level.getBlockState(hit.local());
        if (blockState.isAir()) {
            return;
        }
        // Public API veto (ContraptionInteractEvent) — same hook as forward(), left-click variant.
        // Cancelling skips the non-destructive attack dispatch entirely.
        if (fireInteractCancelled(player, hit, false)) {
            return;
        }
        try {
            blockState.attack(level.serverLevel(), hit.local(), player);
        } catch (Throwable ignored) {
            // Fail-open — see forward()'s own try/catch for the same rationale.
        }
    }

    /**
     * Fires {@link dev.arubik.craftengine.contraption.event.ContraptionInteractEvent} and returns
     * whether it was cancelled. Fail-open (returns {@code false} = "proceed") if the owning facade
     * can't be resolved or no live server is present, so a click behaves byte-identically to before
     * this event existed whenever nobody is listening — same shape as {@code ContraptionAssembler}'s
     * own fire helpers. {@code right} distinguishes the right-click ({@link #forward}) from the
     * left-click ({@link #forwardAttack}) dispatch path.
     */
    private static boolean fireInteractCancelled(ServerPlayer player, Hit hit, boolean right) {
        try {
            ContraptionEntity entity = ContraptionManager.get(hit.state().id());
            if (entity == null) {
                return false;
            }
            dev.arubik.craftengine.contraption.element.ContraptionElement element =
                    hit.state().elementByLocalPos(hit.local());
            dev.arubik.craftengine.contraption.event.ContraptionInteractEvent event =
                    new dev.arubik.craftengine.contraption.event.ContraptionInteractEvent(
                            (org.bukkit.entity.Player) player.getBukkitEntity(), entity, hit.local(), hit.face(),
                            EquipmentSlot.HAND, right, element);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            return event.isCancelled();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Fires {@link dev.arubik.craftengine.contraption.event.ContraptionBlockPlaceEvent} and returns
     * whether it was cancelled. Fail-open under the same conditions as {@link #fireInteractCancelled}
     * (unresolvable facade / no live server ⇒ proceed), so placement is byte-identical to before this
     * event existed whenever nobody listens.
     */
    private static boolean fireBlockPlaceCancelled(ServerPlayer player, Hit hit, BlockPos targetLocal,
            ItemStack held, InteractionHand hand) {
        try {
            ContraptionEntity entity = ContraptionManager.get(hit.state().id());
            if (entity == null) {
                return false;
            }
            EquipmentSlot slot = hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
            dev.arubik.craftengine.contraption.event.ContraptionBlockPlaceEvent event =
                    new dev.arubik.craftengine.contraption.event.ContraptionBlockPlaceEvent(
                            (org.bukkit.entity.Player) player.getBukkitEntity(), entity, targetLocal,
                            org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(held), slot);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            return event.isCancelled();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Vanilla-container auto-close fix (CONTRAPTIONS.md 2026-07-01 session, "menu opens then
     * immediately closes"). {@link BlockState#useWithoutItem} on a vanilla container block
     * (chest, barrel, furnace, ...) opens a menu whose {@code stillValid(Player)} ultimately
     * delegates to the underlying vanilla {@code BlockEntity}'s own {@code stillValid}, which
     * bottoms out in {@link Container#stillValidBlockEntity} — raw {@code player.distanceToSqr}
     * against the {@code BlockEntity}'s {@link BlockPos}. Since that pos is a
     * {@link ContraptionLevel} LOCAL position (small numbers), not the real-world position the
     * contraption visually occupies, this distance is always enormous and vanilla closes the
     * menu on the very next server tick.
     *
     * <p>Confirmed via {@code javap} against this project's own mapped server jar
     * ({@code remapMinecraft} output, paperweight-userdev cache):
     * <pre>
     * ChestMenu.stillValid(Player):
     *   aload_0; getfield container:Lnet/minecraft/world/Container;
     *   aload_1; invokeinterface Container.stillValid(Player)Z; ireturn
     * </pre>
     * i.e. {@code ChestMenu} (also used for barrels) holds a {@code private final Container
     * container} field and simply forwards to it — {@code AbstractFurnaceMenu} has the
     * equivalent {@code final Container container} field. Neither {@code AbstractContainerMenu}
     * nor its concrete subclasses expose a public way to override just the validity check, so
     * this reflectively swaps that one field (found by type, not by a name that could rot across
     * versions) to a delegating {@link Container} wrapper that forwards every method to the
     * original EXCEPT {@code stillValid}, which redoes the same distance check against
     * {@link ContraptionLevel#realWorldPositionOf(BlockPos)} instead of the raw local pos — item
     * slots, {@code getItem}/{@code setItem}/{@code getContainerSize} etc. all still hit the real
     * underlying container unmodified, so nothing about the inventory itself changes.
     *
     * <p>Best-effort: if a future/modified menu type doesn't expose a {@code Container}-typed
     * field (e.g. a fully custom third-party menu with no such field), this silently no-ops and
     * that menu keeps vanilla's original (broken, for a contraption) behavior — see this
     * listener's own class javadoc scoping note.
     */
    /**
     * The auto-close fix for menus with NO block entity — a crafting table, anvil, enchanting table,
     * grindstone, loom, stonecutter, cartography table.
     *
     * <h2>Why {@link #fixVanillaMenuStillValid} cannot reach these</h2>
     * That fix swaps a menu's {@code Container} field. A crafting table has no block entity and no
     * container to swap: {@code CraftingMenu} validates through a {@link ContainerLevelAccess} instead,
     * so the menu opened and vanilla closed it on the very next tick.
     *
     * <h2>Why the level/pos pair cannot be fixed, and the default can</h2>
     * Verified against the mapped jar, {@code AbstractContainerMenu#stillValid} is:
     * <pre>
     * access.evaluate((level, pos) -&gt;
     *     !level.getBlockState(pos).is(targetBlock) ? false : player.isWithinBlockInteractionRange(pos, 4.0),
     *     true);
     * </pre>
     * Those two checks want DIFFERENT frames. The block only exists at the LOCAL pos inside the hidden
     * {@link ContraptionLevel}, but the player's reach is measured in the REAL world — so handing the
     * lambda the contraption's frame passes the block test and fails the range test, and handing it the
     * real world's frame fails the block test (a contraption's cells are display entities; there is no
     * real block there). No pair of arguments satisfies both, which is why this cannot be fixed by
     * pointing the access somewhere better.
     *
     * <p>But that call ends in {@code , true)} — the default when {@code evaluate} yields nothing. An
     * access that returns {@link Optional#empty()} therefore makes {@code stillValid} answer {@code true}
     * without the lambda ever running. That is not a trick: it is exactly what
     * {@link ContainerLevelAccess#NULL} is for, and what vanilla itself uses for a menu with no block
     * behind it.
     *
     * <h2>Why {@code execute} must be overridden rather than inherited</h2>
     * {@code execute}'s default implementation routes through {@code evaluate}, so an empty-returning
     * access silently swallows it — and {@code CraftingMenu#removed} uses {@code execute} to run
     * {@code clearContainer}, which is what hands the 3x3 grid back when the menu closes. Using
     * {@code NULL} directly would therefore have made the crafting table work and <b>eaten every item
     * left in the grid</b>. Overriding {@code execute} to run against the contraption's own level and
     * local pos keeps that working, and is correct: for anything that ACTS on the block, the block
     * genuinely is there.
     *
     * <p><b>Known limit, stated rather than hidden:</b> other {@code evaluate} callers now take their
     * default too — an enchanting table in a contraption will read zero bookshelves, and an anvil will
     * not damage itself. Those are worth a working menu, and a menu that closes instantly is worth
     * nothing.
     */
    private static void fixVanillaMenuLevelAccess(AbstractContainerMenu menu, ContraptionLevel level, BlockPos local) {
        if (menu == null) {
            return;
        }
        ContainerLevelAccess patched = new ContainerLevelAccess() {
            @Override
            public <T> Optional<T> evaluate(java.util.function.BiFunction<net.minecraft.world.level.Level, BlockPos, T> function) {
                return Optional.empty(); // stillValid falls back to its `true` default — see the javadoc
            }

            @Override
            public void execute(java.util.function.BiConsumer<net.minecraft.world.level.Level, BlockPos> consumer) {
                // NOT inherited: the default routes through evaluate above and would swallow this,
                // taking CraftingMenu#removed's clearContainer with it — i.e. the player's grid.
                consumer.accept(level.serverLevel(), local);
            }
        };
        try {
            for (java.lang.reflect.Field field : allFields(menu.getClass())) {
                if (field.getType() != ContainerLevelAccess.class) {
                    continue;
                }
                field.setAccessible(true);
                if (field.get(menu) == patched) {
                    continue;
                }
                field.set(menu, patched);
            }
        } catch (Throwable t) {
            // Best-effort, same rationale as fixVanillaMenuStillValid: a failure just leaves vanilla's
            // original (instantly-closing, for a contraption) behaviour for this one menu.
            org.bukkit.Bukkit.getLogger().warning("[Contraption] fixVanillaMenuLevelAccess threw: " + t);
        }
    }

    private static void fixVanillaMenuStillValid(AbstractContainerMenu menu, ContraptionLevel level, BlockPos local) {
        if (menu == null) {
            return;
        }
        try {
            for (java.lang.reflect.Field field : allFields(menu.getClass())) {
                // EXACT type match, not isAssignableFrom: vanilla's own
                // net.minecraft.world.entity.player.Inventory ALSO implements Container (so
                // items can be accessed generically) -- isAssignableFrom matched it too, and a
                // menu's `inventory` field (the PLAYER's real inventory, completely unrelated to
                // this fix) is declared as the concrete `Inventory` type, not `Container`. Trying
                // to write a RealWorldAwareContainer (implements Container, not Inventory) into
                // that field threw IllegalArgumentException every time and aborted the whole
                // scan BEFORE any field after it in declaration order got a chance -- for
                // ChestMenu specifically `container` happens to come first so it still got
                // patched, but for any menu where the ordering differs this silently skipped the
                // real fix entirely. Matching the field's declared type EXACTLY against the
                // `Container` interface only touches fields actually declared as `Container`
                // (every menu's own target-container reference), never `Inventory`.
                if (field.getType() != Container.class) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(menu);
                if (!(value instanceof Container original) || value instanceof RealWorldAwareContainer) {
                    continue;
                }
                field.set(menu, new RealWorldAwareContainer(original, level, local));
                org.bukkit.Bukkit.getLogger().info("[Contraption] stillValid-wrapped field=" + field);
            }
        } catch (Throwable t) {
            // Best-effort — see method javadoc. A reflection failure just leaves vanilla's
            // original (buggy-for-contraptions) stillValid in place for this one menu — but log
            // it instead of hiding it, same rationale as forward()'s own catch.
            org.bukkit.Bukkit.getLogger().warning("[Contraption] fixVanillaMenuStillValid threw: " + t);
            t.printStackTrace();
        }
    }

    private static java.util.List<java.lang.reflect.Field> allFields(Class<?> type) {
        java.util.List<java.lang.reflect.Field> fields = new java.util.ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            fields.addAll(java.util.Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    /**
     * Delegating {@link Container} wrapper — see {@link #fixVanillaMenuStillValid}. Every method
     * except {@code stillValid} forwards straight to {@code delegate}, so the menu keeps reading
     * and writing the SAME real slots (this is not a copy). {@code stillValid} alone is
     * recomputed against the {@link ContraptionLevel}'s real-world position for the captured
     * block.
     */
    private static final class RealWorldAwareContainer implements Container {
        private final Container delegate;
        private final ContraptionLevel level;
        private final BlockPos local;

        RealWorldAwareContainer(Container delegate, ContraptionLevel level, BlockPos local) {
            this.delegate = delegate;
            this.level = level;
            this.local = local;
        }

        @Override
        public boolean stillValid(Player player) {
            if (player.isRemoved()) {
                return false;
            }
            Vec3 localCenter = new Vec3(local.getX() + 0.5, local.getY() + 0.5, local.getZ() + 0.5);
            Vec3 realPos = level.realWorldPositionOf(localCenter);
            return player.distanceToSqr(realPos.x, realPos.y, realPos.z) <= 64.0;
        }

        @Override
        public int getContainerSize() {
            return delegate.getContainerSize();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return delegate.getItem(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return delegate.removeItem(slot, amount);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return delegate.removeItemNoUpdate(slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            delegate.setItem(slot, stack);
        }

        @Override
        public void setChanged() {
            delegate.setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return delegate.getMaxStackSize();
        }

        @Override
        public int getMaxStackSize(ItemStack stack) {
            return delegate.getMaxStackSize(stack);
        }

        @Override
        public void startOpen(net.minecraft.world.entity.ContainerUser user) {
            delegate.startOpen(user);
        }

        @Override
        public void stopOpen(net.minecraft.world.entity.ContainerUser user) {
            delegate.stopOpen(user);
        }

        @Override
        public java.util.List<net.minecraft.world.entity.ContainerUser> getEntitiesWithContainerOpen() {
            return delegate.getEntitiesWithContainerOpen();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return delegate.canPlaceItem(slot, stack);
        }

        @Override
        public boolean canTakeItem(Container target, int slot, ItemStack stack) {
            return delegate.canTakeItem(target, slot, stack);
        }

        @Override
        public void clearContent() {
            delegate.clearContent();
        }

        // --- Paper's own CraftBukkit-bridge additions to Container (not vanilla) — all pure
        // pass-through, unrelated to the stillValid real-world-distance fix above. ---

        @Override
        public java.util.List<ItemStack> getContents() {
            return delegate.getContents();
        }

        @Override
        public void onOpen(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {
            delegate.onOpen(who);
        }

        @Override
        public void onClose(org.bukkit.craftbukkit.entity.CraftHumanEntity who) {
            delegate.onClose(who);
        }

        @Override
        public java.util.List<org.bukkit.entity.HumanEntity> getViewers() {
            return delegate.getViewers();
        }

        @Override
        public org.bukkit.inventory.InventoryHolder getOwner() {
            return delegate.getOwner();
        }

        @Override
        public void setMaxStackSize(int size) {
            delegate.setMaxStackSize(size);
        }

        @Override
        public org.bukkit.Location getLocation() {
            return delegate.getLocation();
        }
    }
}
