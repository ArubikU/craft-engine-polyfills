package dev.arubik.craftengine.contraption;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import dev.arubik.craftengine.multiblock.HammerItems;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * The player-facing half of the happy-ghast harness contraption (2026-07-16 goal, as redesigned):
 * right-clicking a harnessed ghast with a hammer assembles the glued structure it sits among into a
 * contraption ("just when right click a ghast with a harness equiped and with a hammer, if the ghast is
 * between some glued block ... it should make a entire contraption"), and shearing the harness back off
 * hands that structure to the player inside the harness item, the way {@link MinecartBearing}'s
 * chest-minecart item does.
 *
 * <h2>Why the hammer, and why on the entity</h2>
 * The hammer is already every other bearing's assemble/disassemble trigger — see
 * {@link BearingHammerListener}, whose entity branch this mirrors for a ghast instead of a minecart.
 * Merely EQUIPPING a harness deliberately does nothing: a ghast is a vehicle first, and a player
 * harnessing one to fly it must not have the scenery around it silently torn out of the world.
 *
 * <p>A ghast is an {@code INTERACT_AT}-sized entity, so a right-click on it fires
 * {@link PlayerInteractAtEntityEvent} — which sits on a completely separate {@code HandlerList} that a
 * handler registered only against {@link PlayerInteractEntityEvent} never sees. Both entry points are
 * registered for the identical logic; see {@link BearingHammerListener#onInteractAtEntity}'s javadoc,
 * which documents the same split after it silently broke the hammer on bearing entities.
 *
 * <h2>Why the harness still has to be stamped from the interact event</h2>
 * A harness leaves a ghast through {@code Entity#attemptToShearEquipment} (verified against the mapped
 * jar), which is not a Bukkit event, and vanilla's shear path drops the equipped stack ITSELF
 * ({@code spawnAtLocation(serverLevel, itemBySlot, average)}) during packet handling — i.e. the item
 * entity already exists in the world by the time any post-hoc equipment diff runs on the ghast's own
 * tick. {@link EntityEquipmentChangedEvent}, the one hook every removal path funnels through, therefore
 * cannot carry the structure OUT: it fires AFTER the change and its {@code EquipmentChange#oldItem()} is
 * annotated {@code @Contract(pure = true, value = "-> new")} — a copy, so stamping it writes to nothing.
 *
 * <p>So {@link #onInteractEntity} runs FIRST (right-click with shears is what triggers the shear) and
 * writes the current structure into the still-equipped harness via {@link GhastHarnessBearing#packInto},
 * which vanilla then drops verbatim. It deliberately does NOT cancel and does NOT re-implement shearing:
 * vanilla keeps full control of the damage, sound, advancement and drop position, and every one of its own
 * preconditions ({@code !isVehicle()}, {@code !isSecondaryUseActive()}, leash-cutting taking priority)
 * still decides whether the shear actually happens. The only cost of that choice is that a pre-stamp can
 * land on a harness that then ISN'T sheared (a leashed ghast cuts the leash instead) — see
 * {@link #armed} for how the teardown avoids trusting those stale bytes, and {@link #onDeath} for the one
 * case where they could otherwise duplicate the structure.
 *
 * <p><b>Residual gap, documented rather than papered over.</b> Stale bytes are only dangerous if a harness
 * carrying them reaches a player's hands through a path that also restores the blocks into the world. The
 * two realistic ones are covered ({@link #onDeath} strips the drops; {@code /item replace} destroys the
 * harness outright, so there is nothing to re-equip). A third-party plugin that manually lifts the harness
 * out of the BODY slot and hands the same stack to a player would still duplicate the structure on
 * re-assemble. Closing it would need a per-tick sweep to un-stamp expired arms, which is not worth the cost
 * for a path nothing in this plugin takes.
 */
public final class GhastHarnessListener implements Listener {

    /**
     * Ghasts whose equipped harness was pre-stamped by {@link #onInteractEntity} this tick, to the tick it
     * happened on. This — not introspecting {@code oldItem} for our marker — is what tells
     * {@link #onUnequip} that the structure genuinely rode out on a dropped item and the blocks must NOT
     * also be restored into the world.
     *
     * <p>Introspection is unusable precisely because a pre-stamp can outlive a shear that never happened
     * (see the class javadoc): a harness carrying stale bytes that is later destroyed outright by
     * {@code /item replace} would then be read as "the structure is safe on an item" and the whole
     * structure would be silently deleted. An explicit arm, only honoured within
     * {@link #ARM_WINDOW_TICKS}, cannot make that mistake — vanilla's shear runs during packet handling
     * and the equipment diff runs on the ghast's tick immediately after, so a real removal is always
     * within the window, and anything else falls through to the restore-into-the-world path.
     *
     * <p>Values are {@link org.bukkit.Bukkit#getCurrentTick()}, NOT {@code World#getFullTime()}: the world
     * clock is arbitrarily reassignable ({@code /time set}, a daylight-cycle plugin), and a backwards jump
     * would leave an arm looking permanently fresh — precisely the "trusted stale bytes" failure this map
     * exists to prevent. The server tick counter only advances.
     */
    private final Map<UUID, Integer> armed = new HashMap<>();

    /** Player -> the tick their last hammer click was handled. See {@link #firstHammerThisClick}. */
    private final Map<UUID, Integer> lastHammerTick = new HashMap<>();

    /** Ghast -> the tick it was last assembled or disassembled. See {@link #toggleCooldownTicks}. */
    private final Map<UUID, Integer> lastToggleTick = new HashMap<>();

    /**
     * How long a ghast must settle between an assemble and a disassemble.
     *
     * <p>Distinct from {@link #firstHammerThisClick}, which swallows the SECOND event of a single click
     * — a protocol artifact the player never intended. This is about the player's own intent: taking a
     * structure apart and putting it back is a heavy operation (a full capture or a full restore of every
     * captured block, plus its block entities), and hammering it back and forth does real work each time.
     * Four seconds is long enough that a toggle is always deliberate and short enough not to be felt when
     * it is.
     */
    private static final int TOGGLE_COOLDOWN_TICKS = 80;

    /** Ticks still remaining on this ghast's assemble/disassemble cooldown, or {@code 0} if it is free. */
    private int toggleCooldownTicks(HappyGhast ghast) {
        Integer last = lastToggleTick.get(ghast.getUniqueId());
        if (last == null) {
            return 0;
        }
        int elapsed = org.bukkit.Bukkit.getCurrentTick() - last;
        return elapsed >= TOGGLE_COOLDOWN_TICKS ? 0 : TOGGLE_COOLDOWN_TICKS - elapsed;
    }

    /** Starts this ghast's cooldown, and drops entries for ghasts whose own cooldown has long expired. */
    private void markToggled(HappyGhast ghast) {
        int now = org.bukkit.Bukkit.getCurrentTick();
        lastToggleTick.values().removeIf(tick -> now - tick > TOGGLE_COOLDOWN_TICKS);
        lastToggleTick.put(ghast.getUniqueId(), now);
    }

    /**
     * Whether this is the FIRST hammer event of a single right-click, swallowing the second.
     *
     * <h2>Why one click arrives twice</h2>
     * Right-clicking an entity makes the vanilla client call {@code interactAt} and then, if that did
     * not consume the click, {@code interact} — sending TWO {@code ServerboundInteractPacket}s for one
     * press. A Happy Ghast does not consume a hammer click, so both arrive, and CraftBukkit fires them
     * as two events on two independent HandlerLists ({@link PlayerInteractAtEntityEvent} declares its
     * own). Both are genuinely needed — a listener on only one of them misses half the clicks, which is
     * why {@link BearingHammerListener} listens to both.
     *
     * <p>But this handler is a TOGGLE, so being called twice for one press assembled the contraption
     * and then immediately tore it back down, leaving no trace except a pair of chat lines. Cancelling
     * the event does not help: the second packet was already sent by the client before the server
     * replied to the first.
     *
     * <p>So the second call is dropped by tick: the two packets are read from the same connection in
     * the same tick, and no human can legitimately re-click a hammer within one tick.
     */
    private boolean firstHammerThisClick(Player player) {
        int now = org.bukkit.Bukkit.getCurrentTick();
        Integer last = lastHammerTick.get(player.getUniqueId());
        if (last != null && last == now) {
            return false;
        }
        lastHammerTick.values().removeIf(tick -> now - tick > 1);
        lastHammerTick.put(player.getUniqueId(), now);
        return true;
    }

    /** How long a pre-stamp stays trusted. Packet handling and the ghast's own tick are the same server tick; 1 covers the boundary. */
    private static final int ARM_WINDOW_TICKS = 1;

    /** See the class javadoc — an {@code INTERACT_AT} click reaches a different HandlerList entirely. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        onInteractEntity(e);
    }

    /** Routes a right-click on a ghast to the hammer assemble/disassemble or the shear pre-stamp. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof HappyGhast ghast)) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack hand = e.getHand() == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        if (hammerHeld(hand) != null) {
            if (e.getHand() != EquipmentSlot.HAND) {
                return; // main-hand only, exactly as BearingHammerListener gates its own hammer paths
            }
            onHammer(e, ghast, player);
            return;
        }
        preStampForShear(ghast, player, hand);
    }

    /**
     * Assembles the glued structure around a harnessed ghast, or disassembles the one it already carries
     * — the same toggle {@link BearingHammerListener} gives every other bearing.
     */
    private void onHammer(PlayerInteractEntityEvent e, HappyGhast ghast, Player player) {
        e.setCancelled(true);
        if (!firstHammerThisClick(player)) {
            return;
        }
        int cooldown = toggleCooldownTicks(ghast);
        if (cooldown > 0) {
            player.sendMessage(String.format("§eThis ghast is still settling — %.1fs.", cooldown / 20.0));
            return;
        }
        if (GhastHarnessBearing.isBearing(ghast) && GhastHarnessBearing.isAssembled(ghast)) {
            UUID contraptionId = GhastHarnessBearing.contraptionId(ghast);
            ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
            if (entity == null) {
                GhastHarnessBearing.forget(ghast); // tagged, but its contraption is already gone
                return;
            }
            GhastHarnessBearing.disassembleInPlace(ghast.getWorld(), ghast, entity);
            markToggled(ghast);
            ghast.getWorld().playSound(ghast.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.4f);
            player.sendMessage("§7Contraption disassembled, blocks restored.");
            return;
        }
        ItemStack harness = ghast.getEquipment().getItem(EquipmentSlot.BODY);
        if (!GhastHarness.isHarness(harness.getType())) {
            player.sendMessage("§cThis ghast needs a harness before it can carry a contraption.");
            return;
        }
        boolean fromItem = GhastHarnessBearing.isContraptionItem(harness);
        if (GhastHarnessBearing.assemble(ghast.getWorld(), ghast, harness) == null) {
            player.sendMessage(fromItem ? "§cCouldn't restore this packed harness."
                    : "§cNothing glued around this ghast to assemble.");
            return;
        }
        if (fromItem) {
            // The structure is live again, so the bytes still on the equipped harness are a duplicate the
            // next removal would restore a second time. CraftEntityEquipment#getItem handed out a copy
            // (asBukkitCopy), so clearing them means writing the slot back — silently: this is bookkeeping,
            // not a re-equip, and the player must not hear a second equip sound for it.
            ghast.getEquipment().setItem(EquipmentSlot.BODY, GhastHarnessBearing.unpack(harness), true);
        }
        markToggled(ghast);
        ghast.getWorld().playSound(ghast.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.0f);
        player.sendMessage("§bContraption assembled onto the ghast. §7Shear the harness off to pocket it.");
    }

    /** Pre-stamps the equipped harness so vanilla's own shear drops a packed one. See the class javadoc. */
    private void preStampForShear(HappyGhast ghast, Player player, ItemStack hand) {
        if (!GhastHarnessBearing.isBearing(ghast) || !GhastHarnessBearing.isAssembled(ghast)) {
            return;
        }
        // Mirrors vanilla's own gate in Entity#interact: shears in the hand that clicked, and not sneaking
        // (isSecondaryUseActive). We do NOT check !isVehicle() / leash priority — vanilla still decides;
        // a pre-stamp that doesn't end in a shear is harmless (see #armed).
        if (hand == null || hand.getType() != Material.SHEARS || player.isSneaking()) {
            return;
        }
        UUID contraptionId = GhastHarnessBearing.contraptionId(ghast);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        if (entity == null) {
            return;
        }
        ItemStack harness = ghast.getEquipment().getItem(EquipmentSlot.BODY);
        if (!GhastHarness.isHarness(harness.getType())) {
            return;
        }
        if (!GhastHarnessBearing.packInto(harness, entity.state())) {
            return;
        }
        // CraftEntityEquipment#getItem hands out a copy (asBukkitCopy), so the stamp only reaches the real
        // slot by writing it back. Silent: this is our own bookkeeping write, not a re-equip, and the
        // player must not hear a second equip sound for it.
        ghast.getEquipment().setItem(EquipmentSlot.BODY, harness, true);
        int now = org.bukkit.Bukkit.getCurrentTick();
        // An arm is only ever consumed by #onUnequip/#onDeath, so a shear that vanilla declined (a leashed
        // ghast cuts the leash instead) would otherwise leave its entry behind forever. Every entry older
        // than the window is already dead weight — dropping them here bounds the map to the ghasts actually
        // being sheared this tick, without needing a scheduled task to sweep it.
        armed.values().removeIf(armedAt -> now - armedAt > ARM_WINDOW_TICKS);
        armed.put(ghast.getUniqueId(), now);
    }

    /**
     * Tears the contraption down when the harness leaves the BODY slot.
     *
     * <p>{@link EntityEquipmentChangedEvent} is the hook because it is Paper's post-hoc diff of a
     * {@code LivingEntity}'s equipment and therefore reports the BODY slot changing regardless of WHO
     * changed it — the shear path, {@code canDispenserEquipIntoSlot(BODY)} (which is {@code true} for a
     * ghast), {@code /item replace entity}, a plugin, death. A harness cannot leave a ghast without
     * passing through here, and a contraption must not outlive the harness carrying it.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquipmentChanged(EntityEquipmentChangedEvent e) {
        if (!(e.getEntity() instanceof HappyGhast ghast)) {
            return;
        }
        EntityEquipmentChangedEvent.EquipmentChange change = e.getEquipmentChanges().get(EquipmentSlot.BODY);
        if (change == null) {
            return;
        }
        if (GhastHarness.isHarness(change.oldItem().getType()) && !GhastHarness.isHarness(change.newItem().getType())) {
            onUnequip(ghast);
            return;
        }
        if (!GhastHarness.isHarness(change.oldItem().getType()) && GhastHarness.isHarness(change.newItem().getType())) {
            onEquip(ghast, change.newItem());
        }
    }

    /**
     * Restores a PACKED harness's structure the moment it is put on a ghast.
     *
     * <h2>Why equipping restores but does not assemble</h2>
     * Assembling from glue is deliberately hammer-only — putting a plain harness on a ghast must not
     * silently rip whatever happens to be glued nearby out of the world. But a harness that is already
     * carrying a structure is a different thing entirely: it is the packed contraption, exactly like a
     * chest minecart holding one, and the whole point of shearing it off was to put it back on later.
     * Requiring a second tool to unpack what the harness already contains would make the round trip
     * asymmetric for no reason.
     *
     * <p>So: an EMPTY harness equips as an ordinary harness and does nothing, while a PACKED one
     * restores itself and comes back un-packed — leaving stale bytes on a harness whose structure is now
     * live would let the next equip duplicate it.
     */
    private void onEquip(HappyGhast ghast, ItemStack harness) {
        if (GhastHarnessBearing.structureBytesOf(harness) == null) {
            return; // a plain harness — assembling from glue stays behind the hammer
        }
        if (GhastHarnessBearing.isBearing(ghast) && GhastHarnessBearing.isAssembled(ghast)) {
            return; // already carrying one; restoring a second would stack two structures on one ghast
        }
        ContraptionEntity restored = GhastHarnessBearing.assemble(ghast.getWorld(), ghast, harness);
        if (restored == null) {
            return;
        }
        // The structure is live now, so the copy on the item is stale. It has to be cleared through the
        // equipment slot rather than the caller's stack: the event's items are post-hoc copies.
        ghast.getEquipment().setItem(EquipmentSlot.BODY, GhastHarnessBearing.unpack(harness), true);
        ghast.getWorld().playSound(ghast.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.7f, 1.2f);
    }

    /**
     * Tears down after the harness left the BODY slot: into the dropped item if
     * {@link #preStampForShear} stamped it moments ago, otherwise back into the world so nothing the
     * player built is lost to a path that had no item to carry it ({@code /item replace}, a plugin
     * clearing the slot).
     */
    private void onUnequip(HappyGhast ghast) {
        UUID contraptionId = GhastHarnessBearing.contraptionId(ghast);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        Integer armedAt = armed.remove(ghast.getUniqueId());
        if (entity == null) {
            return;
        }
        boolean carriedOutOnItem = armedAt != null
                && org.bukkit.Bukkit.getCurrentTick() - armedAt <= ARM_WINDOW_TICKS;
        if (carriedOutOnItem) {
            GhastHarnessBearing.tearDownIntoItem(ghast.getWorld(), ghast, entity);
        } else {
            GhastHarnessBearing.disassembleInPlace(ghast.getWorld(), ghast, entity);
        }
    }

    /**
     * A dying ghast must not leak its contraption, and must not duplicate it either.
     *
     * <p>Death drops the harness through {@code setItemSlotAndDropWhenKilled}'s guaranteed drop rather than
     * through the shear path, so the structure has no way out on that item and the blocks are restored into
     * the world instead. That makes any {@link GhastHarnessBearing#packInto} bytes still sitting on the
     * dropped harness a DUPLICATE of what is about to be restored — reachable whenever a pre-stamp didn't
     * end in a shear (see the class javadoc). Stripping them from the drops first is what keeps the restore
     * the single copy.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity dead = e.getEntity();
        if (!(dead instanceof HappyGhast ghast)) {
            return;
        }
        armed.remove(ghast.getUniqueId());
        List<ItemStack> drops = e.getDrops();
        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (GhastHarnessBearing.isContraptionItem(drop)) {
                drops.set(i, GhastHarnessBearing.unpack(drop));
            }
        }
        if (!GhastHarnessBearing.isBearing(ghast) || !GhastHarnessBearing.isAssembled(ghast)) {
            return;
        }
        UUID contraptionId = GhastHarnessBearing.contraptionId(ghast);
        ContraptionEntity entity = contraptionId == null ? null : ContraptionManager.get(contraptionId);
        if (entity != null) {
            GhastHarnessBearing.disassembleInPlace(ghast.getWorld(), ghast, entity);
        }
    }

    private static Key hammerHeld(ItemStack hand) {
        if (hand == null || hand.getType().isAir()) {
            return null;
        }
        Key hammer = CraftEngineItems.getCustomItemId(hand);
        return HammerItems.isHammer(hammer) ? hammer : null;
    }
}
