package dev.arubik.craftengine.contraption.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import dev.arubik.craftengine.contraption.api.ContraptionTypeRegistry;
import dev.arubik.craftengine.contraption.assembly.ContraptionAssembler;
import dev.arubik.craftengine.contraption.core.ContraptionEntity;
import dev.arubik.craftengine.contraption.core.ContraptionManager;
import dev.arubik.craftengine.contraption.api.ContraptionType;
import dev.arubik.craftengine.contraption.bearing.MinecartBearing;
import dev.arubik.craftengine.contraption.behavior.BearingBlockBehavior;
import dev.arubik.craftengine.multiblock.HammerItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Hammer-trigger assemble/disassemble for contraption bearings (CONTRAPTIONS.md §5 Phase 6)
 * — mirrors {@code multiblock.HammerAssembleListener}'s shape, kept as its own class in the
 * contraption package rather than modifying the multiblock one.
 *
 * <p><b>Detection</b> (this session's fix): which block is a bearing, and its
 * LINEAR/ROTATIONAL/MINECART type, comes from the real {@link BearingBlockBehavior#typeAt}
 * lookup — a registered CraftEngine block behavior, not a hardcoded id and not the deleted
 * {@code BearingAnchorRegistry} placeholder command
 * ({@code /cep contraption bearing-test-register}).
 *
 * <p><b>Still-open gap, unchanged by this session</b>: there is still no real persistent
 * {@code BlockEntity} for the two block-anchored bearing types (LINEAR/ROTATIONAL), so "is
 * the bearing at this position currently assembled, and into which live contraption" has to
 * be tracked SOMEWHERE — that's exactly the state a real block entity would own. Until one
 * exists, {@link #ASSEMBLED}/{@link #BY_CONTRAPTION} below are a small in-memory map (this
 * class's own bookkeeping, not a separate registry class) filling that gap: purely
 * transient, does NOT survive a server restart. {@link ContraptionChunkLifecycleListener}
 * reads it via {@link #assembledAnchorsSnapshot()} for the same reason it used to read
 * {@code BearingAnchorRegistry}. The MINECART bearing type does NOT need this — its assembled
 * state lives on the real minecart entity's own PersistentDataContainer (see
 * {@link MinecartBearing}), which survives restarts normally.
 *
 * <p>Two entry points, because the 3 bearing types split into block-anchored (LINEAR,
 * ROTATIONAL — always a real block position, before AND after assembly) and entity-anchored
 * (MINECART — the bearing block is consumed by assembly and replaced with a real minecart
 * entity, so disassembly has to be triggered by right-clicking the entity itself, not a
 * block):
 * <ul>
 *   <li>{@link #onInteractBlock} — right-click a bearing BLOCK (detected via
 *   {@link BearingBlockBehavior#typeAt}): assembles it if not yet assembled (LINEAR/ROTATIONAL
 *   directly; MINECART via {@link MinecartBearing}), or disassembles the LINEAR/ROTATIONAL
 *   contraption anchored there if it's already assembled.</li>
 *   <li>{@link #onInteractEntity} — right-click a minecart bearing ENTITY: always a
 *   disassemble (a MINECART bearing is never "not yet assembled" while the entity exists —
 *   the entity IS the assembled state).</li>
 * </ul>
 */
public class BearingHammerListener implements Listener {

    /** (worldId, block position) key for the assembled-anchor bookkeeping below. */
    public record AnchorKey(ResourceKey<Level> worldId, BlockPos pos) {
    }

    /** Bearing position -> currently-assembled contraption id (LINEAR/ROTATIONAL only). */
    private static final Map<AnchorKey, UUID> ASSEMBLED = new HashMap<>();
    /** Reverse lookup: contraption id -> its bearing anchor. */
    private static final Map<UUID, AnchorKey> BY_CONTRAPTION = new HashMap<>();

    /** The contraption id currently assembled at {@code pos} in {@code worldId}, or null. */
    public static UUID assembledContraptionAt(ResourceKey<Level> worldId, BlockPos pos) {
        return ASSEMBLED.get(new AnchorKey(worldId, pos));
    }

    /** Snapshot of every currently-assembled block-anchored bearing, for chunk-unload scanning. */
    public static Map<UUID, AnchorKey> assembledAnchorsSnapshot() {
        return Map.copyOf(BY_CONTRAPTION);
    }

    /** Drops the assembled bookkeeping for a contraption (chunk unload, disassemble, etc). */
    public static void forgetAssembled(UUID contraptionId) {
        AnchorKey anchor = BY_CONTRAPTION.remove(contraptionId);
        if (anchor != null) {
            ASSEMBLED.remove(anchor);
        }
    }

    /**
     * Re-registers the assembled-anchor bookkeeping (assemble time, rehydrate on chunk load, or — for a
     * PHYS body — every tick it moves to a new block; see {@code PhysicsWorld#writeBack}).
     *
     * <p>Safe to call repeatedly with a DIFFERENT position for the same contraption: the previous key is
     * dropped. That matters because {@link #ASSEMBLED} is keyed by position and drives chunk-unload
     * teardown — a lingering old key would let an unrelated chunk's unload believe it owned this
     * contraption and tear it down while it was still live somewhere else. Nothing re-anchored before
     * PHYS bodies did, which is why this only had to be a plain put until now.
     */
    public static void markAssembled(ResourceKey<Level> worldId, BlockPos pos, UUID contraptionId) {
        AnchorKey anchor = new AnchorKey(worldId, pos);
        AnchorKey previous = BY_CONTRAPTION.get(contraptionId);
        if (anchor.equals(previous)) {
            return; // already anchored exactly here — the common case for a PHYS body, which calls this
                    // every tick but only actually moves between blocks occasionally, and never once asleep
        }
        if (previous != null) {
            ASSEMBLED.remove(previous);
        }
        BY_CONTRAPTION.put(contraptionId, anchor);
        ASSEMBLED.put(anchor, contraptionId);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractBlock(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND)
            return;
        Key hammer = hammerHeld(e.getItem());
        if (hammer == null)
            return;

        Block clicked = e.getClickedBlock();
        if (clicked == null)
            return;

        Level level = ((org.bukkit.craftbukkit.CraftWorld) clicked.getWorld()).getHandle();
        BlockPos pos = new BlockPos(clicked.getX(), clicked.getY(), clicked.getZ());
        Key type = BearingBlockBehavior.typeAt(level, pos);
        if (type == null)
            return; // not a bearing block — cheap early-out
        if (type.equals(Key.of("polyfills", "linear")))
            return; // LINEAR piston is REDSTONE-driven now (2026-07-03 — "quita el hammer al bearing");
                    // a hammer does nothing on it; a non-hammer right-click opens its config menu.

        ResourceKey<Level> worldId = ((org.bukkit.craftbukkit.CraftWorld) clicked.getWorld()).getHandle().dimension();
        UUID assembledId = assembledContraptionAt(worldId, pos);
        if (assembledId != null) {
            // Only reachable for LINEAR/ROTATIONAL — a MINECART anchor is forgotten the
            // moment it assembles (see below), since its block no longer exists.
            e.setCancelled(true);
            ContraptionEntity entity = ContraptionManager.get(assembledId);
            if (entity != null) {
                ContraptionAssembler.disassemble(clicked.getWorld(), entity);
            }
            forgetAssembled(assembledId);
            clicked.getWorld().playSound(clicked.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            e.getPlayer().sendMessage("§7Contraption disassembled, blocks restored.");
            return;
        }

        e.setCancelled(true);
        if (type.equals(Key.of("polyfills", "minecart"))) {
            ContraptionEntity entity = MinecartBearing.assemble(clicked.getWorld(), pos);
            if (entity == null) {
                e.getPlayer().sendMessage("§cA minecart bearing needs a rail directly beneath it.");
                return;
            }
            // The block anchor is gone (consumed into the captured structure) — the real
            // minecart entity's own PersistentDataContainer is the anchor from here on.
            clicked.getWorld().playSound(clicked.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
            e.getPlayer().sendMessage("§bContraption assembled onto a minecart. §7Right-click the minecart with a hammer to disassemble.");
            return;
        }

        double rotationalRpm = BearingBlockBehavior.rpmAt(level, pos);
        double suPerBlock = BearingBlockBehavior.suPerBlockAt(level, pos);
        ContraptionEntity entity = ContraptionAssembler.assemble(clicked.getWorld(), pos, type, rotationalRpm, suPerBlock);
        if (entity == null) {
            e.getPlayer().sendMessage("§cNothing to assemble here.");
            return;
        }
        markAssembled(worldId, pos, entity.state().id());
        clicked.getWorld().playSound(clicked.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
        e.getPlayer().sendMessage("§bContraption assembled §7(" + type + " bearing).");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        handleInteractEntity(e);
    }

    /**
     * Real root cause of "hammer right-click on a minecart bearing does nothing" (2026-07-02
     * live-test fix). Vanilla's client sends {@code ServerboundInteractPacket} with sub-action
     * {@code INTERACT_AT} (carrying a precise hit vector), NOT {@code INTERACT}, for the
     * overwhelming majority of real-entity right-clicks — any entity with a normal bounding box,
     * including a plain minecart, resolves a hit position and the client uses {@code
     * interactAt}/{@code INTERACT_AT} rather than the plain {@code interact}/{@code INTERACT}
     * fallback. On the server, CraftBukkit fires a SEPARATE Bukkit event for that sub-action:
     * {@link PlayerInteractAtEntityEvent} (which extends {@link PlayerInteractEntityEvent} in
     * Java, but — critically — declares its OWN independent static {@code HandlerList} and
     * overrides {@code getHandlers()}). Bukkit's plugin manager dispatches purely by an event's
     * own {@code getHandlers()} result, not by listener parameter supertype, so a listener
     * registered only against {@code PlayerInteractEntityEvent} (as {@link #onInteractEntity}
     * above was) is NEVER invoked for an {@code INTERACT_AT} click — it sits on a completely
     * separate {@code HandlerList} that this class never registered a handler for. This is why
     * the hammer worked fine on bearing BLOCKS ({@link PlayerInteractEvent} has no such
     * INTERACT/INTERACT_AT split) but silently did nothing on the bearing ENTITY. Fix: also
     * listen for {@link PlayerInteractAtEntityEvent} directly and run the identical disassemble
     * logic — {@link #handleInteractEntity} is shared so both entry points stay in sync.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        handleInteractEntity(e);
    }

    private void handleInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND)
            return;

        Key hammer = hammerHeld(e.getPlayer().getInventory().getItemInMainHand());
        if (hammer == null)
            return; // not holding hammer

        e.setCancelled(true);

        net.minecraft.world.entity.Entity nmsEntity =
                ((org.bukkit.craftbukkit.entity.CraftEntity) e.getRightClicked()).getHandle();
        Level level = nmsEntity.level();

        // Identify which ContraptionType owns this entity
        ContraptionType owningType = null;
        for (Key typeKey : ContraptionTypeRegistry.getRegisteredTypes()) {
            ContraptionType typeImpl = ContraptionTypeRegistry.get(typeKey);
            if (typeImpl != null && typeImpl.isBearingEntity(nmsEntity)) {
                owningType = typeImpl;
                break;
            }
        }

        if (owningType == null) {
            return; // not a bearing entity
        }

        UUID contraptionId = owningType.getContraptionId(nmsEntity);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);

        if (entity == null) {
            // Orphaned bearing — remove entity
            e.getRightClicked().remove();
            return;
        }

        // Pack to item via ContraptionType.toItem
        net.minecraft.world.item.ItemStack packedNms = owningType.toItem(entity, level);
        if (packedNms == null) {
            e.getPlayer().sendMessage("§cCouldn't pack this contraption.");
            return;
        }

        // Drop packed item
        ItemStack packedBukkit = org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(packedNms);
        java.util.Map<Integer, ItemStack> overflow = e.getPlayer().getInventory().addItem(packedBukkit);
        for (ItemStack leftover : overflow.values()) {
            e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), leftover);
        }

        e.getRightClicked().getWorld().playSound(
                e.getRightClicked().getLocation(), org.bukkit.Sound.ENTITY_ITEM_PICKUP, 0.8f, 1.0f);
        e.getPlayer().sendMessage("§bContraption packed into an item.");
    }

    private static Key hammerHeld(ItemStack hand) {
        if (hand == null || hand.getType().isAir())
            return null;
        Key hammer = CraftEngineItems.getCustomItemId(hand);
        return HammerItems.isHammer(hammer) ? hammer : null;
    }
}
