package dev.arubik.craftengine.item;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;

/**
 * Periodic {@code on_equipped_tick} dispatcher — the piece {@link ExtendedItemBehavior}'s
 * discrete Bukkit events can't cover, since an item like a jetpack needs to act EVERY tick while
 * worn, not just on equip/unequip. Every online player's 6 equipment slots (NMS
 * {@link EquipmentSlot}: HEAD/CHEST/LEGS/FEET/MAINHAND/OFFHAND) are checked each server tick; any
 * slot holding an item whose {@link ItemDefinition} declares {@code scripts.on_equipped_tick}
 * gets that script run (throttled to the definition's own {@link ItemDefinition#tickInterval()}),
 * with {@code item}/{@code player} bound plus a {@code jumped} flag set true on the tick(s) right
 * after this engine detects a jump (an {@code onGround: true -> false} transition with upward
 * velocity) for that player. Everything the script needs to implement real behaviour — granting
 * flight, draining a tank, spawning particles, applying potion effects — is exposed as generic
 * {@code Player}/{@code Item}/{@code World} script methods; this class only ever dispatches, it
 * never hardcodes what any particular item does.
 *
 * <p>Also grants a short fall-damage grace window after any tick where {@code jumped} was true for
 * a player wearing/holding a script-bearing item, since that damage event is Bukkit-only surface
 * (there is no clean NMS hook for it from a plugin) — the one deliberate Bukkit edge in this class.
 * Everything else (enumerating players, reading/writing equipment, ground/velocity checks) stays
 * on NMS types throughout, converting to Bukkit only for the single instant CraftEngine's own
 * (Bukkit-based) item-identity lookup requires it.
 */
public final class ItemTickEngine implements Listener {

    /** Ticks a "jumped" flag stays true after a real jump — long enough for a 1-tick-interval
     *  script to see it, short enough that a plain jump (no thrust available) doesn't read as
     *  sustained flight. */
    private static final int JUMP_WINDOW_TICKS = 8;
    /** Fall-damage grace window after any tick that reported {@code jumped} while a script-bearing
     *  item was equipped — covers the landing after a burst of item-driven thrust. */
    private static final int FALL_GRACE_TICKS = 40;

    private static final Map<UUID, Integer> jumpTicksLeft = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> fallGraceTicksLeft = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> wasOnGround = new ConcurrentHashMap<>();
    private static int globalTick = 0;

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
        EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
    };

    public static void register(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ItemTickEngine(), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, ItemTickEngine::tickAll, 1L, 1L);
    }

    /** The one genuine Bukkit-only edge in this class: fall damage is a Bukkit event with no
     *  clean NMS hook available from a plugin. */
    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (event.getEntity().getType() != org.bukkit.entity.EntityType.PLAYER) return;
        Integer grace = fallGraceTicksLeft.get(event.getEntity().getUniqueId());
        if (grace != null && grace > 0) event.setCancelled(true);
    }

    private static void tickAll() {
        globalTick++;
        for (UUID id : jumpTicksLeft.keySet()) decay(jumpTicksLeft, id);
        for (UUID id : fallGraceTicksLeft.keySet()) decay(fallGraceTicksLeft, id);

        for (org.bukkit.World bukkitWorld : org.bukkit.Bukkit.getServer().getWorlds()) {
            ServerLevel level = (ServerLevel) ((CraftWorld) bukkitWorld).getHandle();
            for (ServerPlayer sp : level.players()) {
                UUID id = sp.getUUID();
                boolean onGround = sp.onGround();
                boolean wasGround = wasOnGround.getOrDefault(id, true);
                if (wasGround && !onGround && sp.getDeltaMovement().y > 0.1) {
                    jumpTicksLeft.put(id, JUMP_WINDOW_TICKS);
                }
                wasOnGround.put(id, onGround);

                boolean jumped = jumpTicksLeft.getOrDefault(id, 0) > 0;
                tickEquipment(sp, jumped, id);
            }
        }
    }

    private static void decay(Map<UUID, Integer> map, UUID id) {
        Integer v = map.get(id);
        if (v == null) return;
        if (v <= 1) map.remove(id); else map.put(id, v - 1);
    }

    private static void tickEquipment(ServerPlayer sp, boolean jumped, UUID id) {
        for (EquipmentSlot slot : SLOTS) {
            checkSlot(sp, slot, jumped, id);
        }
    }

    private static void checkSlot(ServerPlayer sp, EquipmentSlot slot, boolean jumped, UUID id) {
        ItemStack nms = sp.getItemBySlot(slot);
        if (nms == null || nms.isEmpty()) return;

        // CraftEngine's own item-identity API is Bukkit-based — this is the one unavoidable
        // library-boundary conversion, immediately discarded once the id is resolved.
        Key ceId = CraftEngineItems.getCustomItemId(nms.asBukkitMirror());
        if (ceId == null) return;
        ItemDefinition def = ItemDefinition.byId(ceId);
        if (def == null) return;
        String ref = def.script("on_equipped_tick");
        if (ref == null) return;
        if (globalTick % def.tickInterval() != 0) return;

        ScriptCall call = ScriptCall.parse(ref);
        if (call == null) return;

        try {
            ScriptContext ctx = ScriptContext.builder()
                    .item("item", nms)
                    .player(sp)
                    .world((ServerLevel) sp.level())
                    .bool("jumped", jumped)
                    // A per-tick poll, not a discrete action — nothing to cancel, but still bound
                    // for a consistent event shape across every hook.
                    .event(new dev.arubik.craftengine.script.event.ItemActionEvent("on_equipped_tick"))
                    .build();
            ScriptContext result = call.execute(ctx);
            ScriptValue iv = result.getVar("item");
            if (iv instanceof ScriptValue.Item itemVal && itemVal.stack() != null) {
                sp.setItemSlot(slot, itemVal.stack());
            }
        } catch (Throwable ignored) {
        }

        if (jumped) fallGraceTicksLeft.put(id, FALL_GRACE_TICKS);
    }
}
