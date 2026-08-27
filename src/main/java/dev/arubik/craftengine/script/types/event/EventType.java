package dev.arubik.craftengine.script.types.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import dev.arubik.craftengine.script.event.AsyncChatWrapper;
import dev.arubik.craftengine.script.event.BlockBreakWrapper;
import dev.arubik.craftengine.script.event.BlockBurnWrapper;
import dev.arubik.craftengine.script.event.BlockDamageWrapper;
import dev.arubik.craftengine.script.event.BlockDispenseWrapper;
import dev.arubik.craftengine.script.event.BlockExplodeWrapper;
import dev.arubik.craftengine.script.event.BlockFadeWrapper;
import dev.arubik.craftengine.script.event.BlockFormWrapper;
import dev.arubik.craftengine.script.event.BlockGrowWrapper;
import dev.arubik.craftengine.script.event.BlockIgniteWrapper;
import dev.arubik.craftengine.script.event.BlockMultiPlaceWrapper;
import dev.arubik.craftengine.script.event.BlockPhysicsWrapper;
import dev.arubik.craftengine.script.event.BlockPistonExtendWrapper;
import dev.arubik.craftengine.script.event.BlockPistonRetractWrapper;
import dev.arubik.craftengine.script.event.BlockPlaceWrapper;
import dev.arubik.craftengine.script.event.BlockRedstoneWrapper;
import dev.arubik.craftengine.script.event.BlockSpreadWrapper;
import dev.arubik.craftengine.script.event.BreakEvent;
import dev.arubik.craftengine.script.event.BrewWrapper;
import dev.arubik.craftengine.script.event.BukkitEventWrapper;
import dev.arubik.craftengine.script.event.ButtonEvent;
import dev.arubik.craftengine.script.event.CraftItemWrapper;
import dev.arubik.craftengine.script.event.CreatureSpawnWrapper;
import dev.arubik.craftengine.script.event.EntityBreedWrapper;
import dev.arubik.craftengine.script.event.EntityChangeBlockWrapper;
import dev.arubik.craftengine.script.event.EntityCombustWrapper;
import dev.arubik.craftengine.script.event.EntityDamageByEntityWrapper;
import dev.arubik.craftengine.script.event.EntityDamageWrapper;
import dev.arubik.craftengine.script.event.EntityDeathWrapper;
import dev.arubik.craftengine.script.event.EntityExplodeWrapper;
import dev.arubik.craftengine.script.event.EntityKnockbackWrapper;
import dev.arubik.craftengine.script.event.EntityPickupItemWrapper;
import dev.arubik.craftengine.script.event.EntityPotionEffectWrapper;
import dev.arubik.craftengine.script.event.EntityRegainHealthWrapper;
import dev.arubik.craftengine.script.event.EntityShootBowWrapper;
import dev.arubik.craftengine.script.event.EntitySpawnWrapper;
import dev.arubik.craftengine.script.event.EntityTameWrapper;
import dev.arubik.craftengine.script.event.EntityTargetWrapper;
import dev.arubik.craftengine.script.event.EntityTeleportWrapper;
import dev.arubik.craftengine.script.event.EntityToggleGlideWrapper;
import dev.arubik.craftengine.script.event.FormEvent;
import dev.arubik.craftengine.script.event.FurnaceBurnWrapper;
import dev.arubik.craftengine.script.event.FurnaceExtractWrapper;
import dev.arubik.craftengine.script.event.FurnaceSmeltWrapper;
import dev.arubik.craftengine.script.event.GhostSlotEvent;
import dev.arubik.craftengine.script.event.InteractEvent;
import dev.arubik.craftengine.script.event.InventoryClickWrapper;
import dev.arubik.craftengine.script.event.InventoryCloseWrapper;
import dev.arubik.craftengine.script.event.InventoryDragWrapper;
import dev.arubik.craftengine.script.event.InventoryMoveItemWrapper;
import dev.arubik.craftengine.script.event.InventoryOpenWrapper;
import dev.arubik.craftengine.script.event.ItemActionEvent;
import dev.arubik.craftengine.script.event.ItemDespawnWrapper;
import dev.arubik.craftengine.script.event.ItemMergeWrapper;
import dev.arubik.craftengine.script.event.ItemSpawnWrapper;
import dev.arubik.craftengine.script.event.PlayerArmorStandManipulateWrapper;
import dev.arubik.craftengine.script.event.PlayerBedEnterWrapper;
import dev.arubik.craftengine.script.event.PlayerBedLeaveWrapper;
import dev.arubik.craftengine.script.event.PlayerChangedWorldWrapper;
import dev.arubik.craftengine.script.event.PlayerCommandPreprocessWrapper;
import dev.arubik.craftengine.script.event.PlayerDeathWrapper;
import dev.arubik.craftengine.script.event.PlayerDropItemWrapper;
import dev.arubik.craftengine.script.event.PlayerExpChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerGameModeChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerInteractWrapper;
import dev.arubik.craftengine.script.event.PlayerItemBreakWrapper;
import dev.arubik.craftengine.script.event.PlayerItemConsumeWrapper;
import dev.arubik.craftengine.script.event.PlayerItemDamageWrapper;
import dev.arubik.craftengine.script.event.PlayerJoinWrapper;
import dev.arubik.craftengine.script.event.PlayerKickWrapper;
import dev.arubik.craftengine.script.event.PlayerLevelChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerMoveWrapper;
import dev.arubik.craftengine.script.event.PlayerPortalWrapper;
import dev.arubik.craftengine.script.event.PlayerQuitWrapper;
import dev.arubik.craftengine.script.event.PlayerRespawnWrapper;
import dev.arubik.craftengine.script.event.PlayerSwapHandItemsWrapper;
import dev.arubik.craftengine.script.event.PlayerTeleportWrapper;
import dev.arubik.craftengine.script.event.PlayerToggleFlightWrapper;
import dev.arubik.craftengine.script.event.PlayerToggleSneakWrapper;
import dev.arubik.craftengine.script.event.PlayerToggleSprintWrapper;
import dev.arubik.craftengine.script.event.PrepareItemCraftWrapper;
import dev.arubik.craftengine.script.event.RenderEvent;
import dev.arubik.craftengine.script.event.ScriptEvent;
import dev.arubik.craftengine.script.event.SignChangeWrapper;
import dev.arubik.craftengine.script.event.TransferEvent;
import dev.arubik.craftengine.script.event.VehicleBlockCollisionWrapper;
import dev.arubik.craftengine.script.event.VehicleCreateWrapper;
import dev.arubik.craftengine.script.event.VehicleDamageWrapper;
import dev.arubik.craftengine.script.event.VehicleDestroyWrapper;
import dev.arubik.craftengine.script.event.VehicleEnterWrapper;
import dev.arubik.craftengine.script.event.VehicleEntityCollisionWrapper;
import dev.arubik.craftengine.script.event.VehicleExitWrapper;
import dev.arubik.craftengine.script.event.VehicleMoveWrapper;
import dev.arubik.craftengine.script.event.WeatherChangeWrapper;
import dev.arubik.craftengine.script.event.ThunderChangeWrapper;
import dev.arubik.craftengine.script.event.LightningStrikeWrapper;
import dev.arubik.craftengine.script.event.WorldLoadWrapper;
import dev.arubik.craftengine.script.event.WorldUnloadWrapper;
import dev.arubik.craftengine.script.event.WorldSaveWrapper;
import dev.arubik.craftengine.script.event.ChunkLoadWrapper;
import dev.arubik.craftengine.script.event.ChunkUnloadWrapper;
import dev.arubik.craftengine.script.event.SpawnChangeWrapper;
import dev.arubik.craftengine.script.event.PortalCreateWrapper;
import dev.arubik.craftengine.script.event.StructureGrowWrapper;
import dev.arubik.craftengine.script.event.ServerCommandEventWrapper;
import dev.arubik.craftengine.script.event.PluginEnableEventWrapper;
import dev.arubik.craftengine.script.event.PluginDisableEventWrapper;
import dev.arubik.craftengine.script.event.TabCompleteEventWrapper;
import dev.arubik.craftengine.script.event.PlayerAdvancementDoneWrapper;
import dev.arubik.craftengine.script.event.PlayerStatisticIncrementWrapper;
import dev.arubik.craftengine.script.event.PlayerRiptideWrapper;
import dev.arubik.craftengine.script.event.PlayerVelocityWrapper;
import dev.arubik.craftengine.script.event.PlayerShearEntityWrapper;
import dev.arubik.craftengine.script.event.PlayerBucketFillWrapper;
import dev.arubik.craftengine.script.event.PlayerBucketEmptyWrapper;
import dev.arubik.craftengine.script.event.PlayerEggThrowWrapper;
import dev.arubik.craftengine.script.event.PlayerFishWrapper;
import dev.arubik.craftengine.script.event.BroadcastMessageEventWrapper;
import dev.arubik.craftengine.script.event.PlayerElytraBoostWrapper;
import dev.arubik.craftengine.script.event.PlayerAnimationWrapper;
import dev.arubik.craftengine.script.types.entity.PlayerType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * Registers the {@code Event} base PolyType and every concrete subtype (each declared as
 * {@code PolyTypeRegistry.define("XEvent", "Event")}, mirroring how {@code Player} extends
 * {@code LivingEntity}). Every {@code on_*} script hook — machines, multiblocks (which reuse the
 * exact same machine hooks, see {@code CelledDataMachineBehavior}), and items alike — and every
 * button/ghost-slot menu interaction binds one of these as the lowercase {@code event} variable,
 * giving every hook the SAME base shape ({@code event.type}, {@code event.cancelled},
 * {@code event.cancel()}) instead of each one inventing its own ad-hoc convention (the old
 * {@code Machine.set_flag("_transfer_cancel", 1)} veto, or — for most item hooks — no veto
 * mechanism at all). See each {@code ScriptEvent} subclass's javadoc for what it adds and, where
 * relevant, what older convention it deprecates (kept working alongside the new one, not removed).
 */
public final class EventType {
    private EventType() {}

    public static void register() {
        PolyTypeRegistry.define("Event")
            .propertyTyped("type", TypeCodecs.STRING, (ScriptEvent e) -> e.type())
            .propertyTyped("cancelled", TypeCodecs.BOOL, (ScriptEvent e) -> e.isCancelled())
            .methodTyped0("cancel", TypeCodecs.BOOL, (ScriptEvent e) -> { e.setCancelled(true); return true; })
            .methodTypedOpt1("set_cancelled", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (ScriptEvent e, Boolean cancelled) -> { e.setCancelled(cancelled); return true; });

        PolyTypeRegistry.define("BreakEvent", "Event")
            // TypeCodecs.RAW, not TypeCodecs.listOf: the elements are ScriptValue.Item, not
            // Obj-wrapped PolyType instances, so no PolyType name exists to declare — and listOf
            // would re-box each element as an Obj, breaking every `instanceof ScriptValue.Item`
            // consumer. The array stays hand-built inside a RAW-returning typed handler.
            .propertyTyped("drops", TypeCodecs.RAW, (BreakEvent e) -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : e.drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // set_drops(item, item, ...) — replaces the DEFAULT drops (this event's own container
            // contents) with exactly the given items. Every arg is treated as one drop, so both
            // event.set_drops(one_item) and event.set_drops(a, b, c) work with no array-literal
            // syntax required.
            // Left untyped: IRREDUCIBLY variadic. Both documented shapes are reachable — the
            // one-array form set_drops([a, b]) AND the flat form set_drops(a, b, c) — and every
            // arg past the first contributes drops. A single methodTypedOpt1(RAW, ...) slot binds
            // only args[0], so the flat multi-arg form would silently lose b and c: not exact.
            .method("set_drops", (obj, args) -> {
                List<ItemStack> drops = new ArrayList<>();
                for (ScriptValue v : args) {
                    if (v instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
                        drops.add(i.stack());
                    } else if (v instanceof ScriptValue.Array arr) {
                        for (ScriptValue elem : arr.elements()) {
                            if (elem instanceof ScriptValue.Item ei && ei.stack() != null && !ei.stack().isEmpty()) {
                                drops.add(ei.stack());
                            }
                        }
                    }
                }
                ((BreakEvent) obj).setDrops(drops);
                return ScriptValue.of(true);
            });

        // Generic item-hook event (on_right_click, on_use, on_drop, on_pickup, ...) — see
        // ItemActionEvent's javadoc. event.type carries the hook name so a script that shares one
        // function across several hooks can branch on it; other_entity/amount are null/0 when the
        // firing hook doesn't have one.
        PolyTypeRegistry.define("ItemActionEvent", "Event")
            // RAW, not TypeCodecs.polyType("Entity", ...): EntityType.wrap picks the PolyType name
            // per entity (Player/Animal/Mob/ItemEntity/...), so pinning it to one name would strip
            // every subtype member.
            .propertyTyped("other_entity", TypeCodecs.RAW, (ItemActionEvent ev) -> {
                var e = ev.otherEntity();
                return e == null ? ScriptValue.NULL
                        : dev.arubik.craftengine.script.types.entity.EntityType.wrap(e);
            })
            .propertyTyped("amount", TypeCodecs.DOUBLE, (ItemActionEvent ev) -> {
                Double a = ev.amount();
                return a == null ? 0.0 : a;
            })
            // clicked_block — the Block right-clicked (on_right_click only, null off-block). Combine
            // with clicked_face + Block.place_custom for "place a different custom block depending
            // on which face was clicked" placement routing, entirely from a script.
            .propertyTyped("clicked_block", TypeCodecs.RAW, (ItemActionEvent ev) -> {
                var b = ev.clickedBlock();
                if (b == null) return ScriptValue.NULL;
                try {
                    net.minecraft.server.level.ServerLevel level =
                            ((org.bukkit.craftbukkit.CraftWorld) b.getWorld()).getHandle();
                    net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(b.getX(), b.getY(), b.getZ());
                    return dev.arubik.craftengine.script.types.world.BlockType.wrap(level, pos);
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            // clicked_face — which face of clicked_block was clicked ("north".."down"), null if none.
            // A null face still reaches a script as NULL, not the string "null": TypeCodecs.STRING
            // encodes via ScriptValue.of(String), which maps a Java null to ScriptValue.NULL.
            .propertyTyped("clicked_face", TypeCodecs.STRING, (ItemActionEvent ev) -> {
                var f = ev.clickedFace();
                return f == null ? null : f.name().toLowerCase(java.util.Locale.ROOT);
            });

        // Right/left-click interactions — machines' on_right_click/on_left_click and items' click
        // hooks alike.
        PolyTypeRegistry.define("InteractEvent", "Event")
            // The explicit null check is redundant under TypeCodecs.STRING — ScriptValue.of(null)
            // already yields ScriptValue.NULL — so a plain hand() is exactly equivalent.
            .propertyTyped("hand", TypeCodecs.STRING, (InteractEvent e) -> e.hand());

        // on_pipe_transfer — read-only mirror of the type/payload/direction/mode already bound as
        // separate classes (kept for back-compat), plus a real cancel().
        PolyTypeRegistry.define("TransferEvent", "Event")
            .propertyTyped("transfer_type", TypeCodecs.STRING, (TransferEvent w) -> w.transferType())
            .propertyTyped("payload", TypeCodecs.RAW, (TransferEvent w) -> w.payload())
            .propertyTyped("direction", TypeCodecs.STRING, (TransferEvent w) -> w.direction())
            .propertyTyped("mode", TypeCodecs.STRING, (TransferEvent w) -> w.mode());

        // A menu button's "file.pf:function" script action.
        PolyTypeRegistry.define("ButtonEvent", "Event")
            .propertyTyped("slot", TypeCodecs.DOUBLE, (ButtonEvent w) -> (double) w.slot())
            .propertyTyped("click_type", TypeCodecs.STRING, (ButtonEvent w) -> w.clickType());

        // A GHOST slot's set-script — cancel() rejects the click (see MenuSlotType#GHOST).
        PolyTypeRegistry.define("GhostSlotEvent", "Event")
            .propertyTyped("slot", TypeCodecs.DOUBLE, (GhostSlotEvent w) -> (double) w.slot())
            .propertyTyped("clicked_id", TypeCodecs.STRING, (GhostSlotEvent w) -> w.clickedId())
            .propertyTyped("click_type", TypeCodecs.STRING, (GhostSlotEvent w) -> w.clickType());

        // A multiblock's on_form/on_disassemble — event.type distinguishes the two.
        PolyTypeRegistry.define("FormEvent", "Event");

        // on_render — see RenderEvent's javadoc for why cancel() is a no-op here.
        PolyTypeRegistry.define("RenderEvent", "Event")
            // RAW, not polyType("Entity", ...) — EntityType.wrap is per-entity polymorphic, see the
            // other_entity note on ItemActionEvent.
            .propertyTyped("holder", TypeCodecs.RAW, (RenderEvent ev) -> {
                Entity e = ev.holder();
                return e == null ? ScriptValue.NULL : dev.arubik.craftengine.script.types.entity.EntityType.wrap(e);
            })
            .propertyTyped("slot", TypeCodecs.DOUBLE, (RenderEvent w) -> (double) w.slot());

        // ---- Dedicated wrappers for the common vanilla PLAYER events (see each ScriptEvent
        // subclass's own javadoc for what it's for) — richly typed alternatives to BukkitEvent's
        // reflective get/set for the events scripts are most likely to actually want. Picked
        // automatically over BukkitEventWrapper by GenericEventBridge#buildContext whenever the
        // runtime event type matches one of these; anything else still falls back to BukkitEvent.
        PolyTypeRegistry.define("PlayerJoinEvent", "Event")
            .propertyTyped("join_message", TypeCodecs.STRING, (PlayerJoinWrapper w) -> w.joinMessage())
            .methodTypedOpt1("set_join_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerJoinWrapper w, String msg) -> { w.setJoinMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerQuitEvent", "Event")
            .propertyTyped("quit_message", TypeCodecs.STRING, (PlayerQuitWrapper w) -> w.quitMessage())
            .methodTypedOpt1("set_quit_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerQuitWrapper w, String msg) -> { w.setQuitMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerDeathEvent", "Event")
            .propertyTyped("death_message", TypeCodecs.STRING, (PlayerDeathWrapper w) -> w.deathMessage())
            .methodTypedOpt1("set_death_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerDeathWrapper w, String msg) -> { w.setDeathMessage(msg); return true; })
            // RAW, not TypeCodecs.listOf — ScriptValue.Item elements, same as BreakEvent.drops above.
            .propertyTyped("drops", TypeCodecs.RAW, (PlayerDeathWrapper w) -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : w.drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // Left untyped: IRREDUCIBLY variadic — same "one array OR a flat series of items"
            // shape as BreakEvent.set_drops above. A single methodTypedOpt1(RAW, ...) slot binds
            // only args[0] and would silently drop every further item of the flat form.
            .method("set_drops", (obj, args) -> {
                List<ItemStack> drops = new ArrayList<>();
                for (ScriptValue v : args) {
                    if (v instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
                        drops.add(i.stack());
                    } else if (v instanceof ScriptValue.Array arr) {
                        for (ScriptValue elem : arr.elements()) {
                            if (elem instanceof ScriptValue.Item ei && ei.stack() != null && !ei.stack().isEmpty()) {
                                drops.add(ei.stack());
                            }
                        }
                    }
                }
                ((PlayerDeathWrapper) obj).setDrops(drops);
                return ScriptValue.of(true);
            })
            .propertyTyped("keep_inventory", TypeCodecs.BOOL, (PlayerDeathWrapper w) -> w.keepInventory())
            .methodTypedOpt1("set_keep_inventory", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (PlayerDeathWrapper w, Boolean keep) -> { w.setKeepInventory(keep); return true; })
            .propertyTyped("exp", TypeCodecs.DOUBLE, (PlayerDeathWrapper w) -> (double) w.exp())
            .methodTypedOpt1("set_exp", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerDeathWrapper w, Double exp) -> { w.setExp(exp.intValue()); return true; });

        PolyTypeRegistry.define("PlayerRespawnEvent", "Event")
            .propertyTyped("respawn_location", TypeCodecs.RAW, (PlayerRespawnWrapper w) -> w.respawnLocation())
            .methodTyped1("set_respawn_location", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerRespawnWrapper w, ScriptValue loc) -> { w.setRespawnLocation(loc); return true; });

        PolyTypeRegistry.define("PlayerMoveEvent", "Event")
            .propertyTyped("from", TypeCodecs.RAW, (PlayerMoveWrapper w) -> w.from())
            .propertyTyped("to", TypeCodecs.RAW, (PlayerMoveWrapper w) -> w.to())
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerMoveWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("PlayerTeleportEvent", "Event")
            .propertyTyped("from", TypeCodecs.RAW, (PlayerTeleportWrapper w) -> w.from())
            .propertyTyped("to", TypeCodecs.RAW, (PlayerTeleportWrapper w) -> w.to())
            .propertyTyped("cause", TypeCodecs.STRING, (PlayerTeleportWrapper w) -> w.cause())
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerTeleportWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("PlayerInteractEvent", "Event")
            .propertyTyped("action", TypeCodecs.STRING, (PlayerInteractWrapper w) -> w.action())
            // Null hand still encodes to NULL under TypeCodecs.STRING — see InteractEvent.hand.
            .propertyTyped("hand", TypeCodecs.STRING, (PlayerInteractWrapper w) -> w.hand())
            .propertyTyped("clicked_block", TypeCodecs.RAW, (PlayerInteractWrapper w) -> w.clickedBlock())
            .propertyTyped("item", TypeCodecs.RAW, (PlayerInteractWrapper w) -> w.item());

        PolyTypeRegistry.define("AsyncChatEvent", "Event")
            .propertyTyped("message", TypeCodecs.STRING, (AsyncChatWrapper w) -> w.message())
            .methodTypedOpt1("set_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (AsyncChatWrapper w, String msg) -> { w.setMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerCommandPreprocessEvent", "Event")
            .propertyTyped("message", TypeCodecs.STRING, (PlayerCommandPreprocessWrapper w) -> w.message())
            .methodTypedOpt1("set_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerCommandPreprocessWrapper w, String msg) -> { w.setMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerLevelChangeEvent", "Event")
            .propertyTyped("old_level", TypeCodecs.DOUBLE, (PlayerLevelChangeWrapper w) -> (double) w.oldLevel())
            .propertyTyped("new_level", TypeCodecs.DOUBLE, (PlayerLevelChangeWrapper w) -> (double) w.newLevel());

        PolyTypeRegistry.define("PlayerExpChangeEvent", "Event")
            .propertyTyped("amount", TypeCodecs.DOUBLE, (PlayerExpChangeWrapper w) -> (double) w.amount())
            .methodTypedOpt1("set_amount", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerExpChangeWrapper w, Double amount) -> { w.setAmount(amount.intValue()); return true; });

        PolyTypeRegistry.define("PlayerToggleSneakEvent", "Event")
            .propertyTyped("is_sneaking", TypeCodecs.BOOL, (PlayerToggleSneakWrapper w) -> w.isSneaking());

        PolyTypeRegistry.define("PlayerToggleSprintEvent", "Event")
            .propertyTyped("is_sprinting", TypeCodecs.BOOL, (PlayerToggleSprintWrapper w) -> w.isSprinting());

        PolyTypeRegistry.define("PlayerToggleFlightEvent", "Event")
            .propertyTyped("is_flying", TypeCodecs.BOOL, (PlayerToggleFlightWrapper w) -> w.isFlying());

        PolyTypeRegistry.define("PlayerBedEnterEvent", "Event")
            .propertyTyped("bed", TypeCodecs.RAW, (PlayerBedEnterWrapper w) -> w.bed())
            .propertyTyped("bed_enter_result", TypeCodecs.STRING, (PlayerBedEnterWrapper w) -> w.bedEnterResult());

        PolyTypeRegistry.define("PlayerBedLeaveEvent", "Event")
            .propertyTyped("bed", TypeCodecs.RAW, (PlayerBedLeaveWrapper w) -> w.bed());

        PolyTypeRegistry.define("PlayerGameModeChangeEvent", "Event")
            .propertyTyped("new_game_mode", TypeCodecs.STRING, (PlayerGameModeChangeWrapper w) -> w.newGameMode());

        PolyTypeRegistry.define("PlayerKickEvent", "Event")
            .propertyTyped("reason", TypeCodecs.STRING, (PlayerKickWrapper w) -> w.reason())
            .methodTypedOpt1("set_reason", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerKickWrapper w, String reason) -> { w.setReason(reason); return true; });

        PolyTypeRegistry.define("PlayerItemConsumeEvent", "Event")
            .propertyTyped("item", TypeCodecs.RAW, (PlayerItemConsumeWrapper w) -> w.item())
            .methodTyped1("set_item", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerItemConsumeWrapper w, ScriptValue item) -> { w.setItem(item); return true; });

        PolyTypeRegistry.define("PlayerDropItemEvent", "Event")
            .propertyTyped("item_drop", TypeCodecs.RAW, (PlayerDropItemWrapper w) -> w.itemDrop());

        PolyTypeRegistry.define("EntityPickupItemEvent", "Event")
            .propertyTyped("item", TypeCodecs.RAW, (EntityPickupItemWrapper w) -> w.item())
            .propertyTyped("remaining", TypeCodecs.DOUBLE, (EntityPickupItemWrapper w) -> (double) w.remaining());

        PolyTypeRegistry.define("PlayerPortalEvent", "Event")
            .propertyTyped("from", TypeCodecs.RAW, (PlayerPortalWrapper w) -> w.from())
            .propertyTyped("to", TypeCodecs.RAW, (PlayerPortalWrapper w) -> w.to())
            .propertyTyped("cause", TypeCodecs.STRING, (PlayerPortalWrapper w) -> w.cause())
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerPortalWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("PlayerChangedWorldEvent", "Event")
            .propertyTyped("from_world", TypeCodecs.RAW, (PlayerChangedWorldWrapper w) -> w.fromWorld());

        // ---- Dedicated wrappers for the common vanilla ENTITY events — same idea as the PLAYER
        // batch above, for events that fire on entities in general (mobs included) rather than only
        // on players.
        PolyTypeRegistry.define("EntityDeathEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (EntityDeathWrapper w) -> w.entity())
            // RAW, not TypeCodecs.listOf — ScriptValue.Item elements, same as BreakEvent.drops above.
            .propertyTyped("drops", TypeCodecs.RAW, (EntityDeathWrapper w) -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : w.drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // Left untyped: IRREDUCIBLY variadic — same "one array OR a flat series of items"
            // shape as BreakEvent.set_drops above. A single methodTypedOpt1(RAW, ...) slot binds
            // only args[0] and would silently drop every further item of the flat form.
            .method("set_drops", (obj, args) -> {
                List<ItemStack> drops = new ArrayList<>();
                for (ScriptValue v : args) {
                    if (v instanceof ScriptValue.Item i && i.stack() != null && !i.stack().isEmpty()) {
                        drops.add(i.stack());
                    } else if (v instanceof ScriptValue.Array arr) {
                        for (ScriptValue elem : arr.elements()) {
                            if (elem instanceof ScriptValue.Item ei && ei.stack() != null && !ei.stack().isEmpty()) {
                                drops.add(ei.stack());
                            }
                        }
                    }
                }
                ((EntityDeathWrapper) obj).setDrops(drops);
                return ScriptValue.of(true);
            })
            .propertyTyped("dropped_exp", TypeCodecs.DOUBLE, (EntityDeathWrapper w) -> (double) w.droppedExp())
            .methodTypedOpt1("set_dropped_exp", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityDeathWrapper w, Double exp) -> { w.setDroppedExp(exp.intValue()); return true; });

        PolyTypeRegistry.define("EntityDamageEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (EntityDamageWrapper w) -> w.entity())
            .propertyTyped("damage", TypeCodecs.DOUBLE, (EntityDamageWrapper w) -> (double) w.damage())
            .methodTypedOpt1("set_damage", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityDamageWrapper w, Double damage) -> { w.setDamage(damage); return true; })
            .propertyTyped("cause", TypeCodecs.STRING, (EntityDamageWrapper w) -> w.cause());

        PolyTypeRegistry.define("EntityDamageByEntityEvent", "EntityDamageEvent")
            .propertyTyped("damager", TypeCodecs.RAW, (EntityDamageByEntityWrapper w) -> w.damager());

        PolyTypeRegistry.define("EntityTargetEvent", "Event")
            .propertyTyped("target", TypeCodecs.RAW, (EntityTargetWrapper w) -> w.target())
            .methodTypedOpt1("set_target", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (EntityTargetWrapper w, ScriptValue target) -> { w.setTarget(target); return true; })
            .propertyTyped("reason", TypeCodecs.STRING, (EntityTargetWrapper w) -> w.reason());

        PolyTypeRegistry.define("EntityTameEvent", "Event")
            .propertyTyped("owner", TypeCodecs.RAW, (EntityTameWrapper w) -> w.owner());

        PolyTypeRegistry.define("EntityExplodeEvent", "Event")
            .propertyTyped("location", TypeCodecs.RAW, (EntityExplodeWrapper w) -> w.location())
            // RAW, not TypeCodecs.listOf: the elements ARE uniform "Block" Objs, but the wrapper
            // hands back an already-boxed List<ScriptValue> (BlockType.wrap does the boxing, and may
            // yield NULL for a block it can't resolve). listOf wants the real List<BlockRef> and
            // silently drops every element that isn't one, so it would not be behaviour-preserving
            // here. Typing this properly needs a raw-typed accessor on the wrapper first — same for
            // every other array-valued property below.
            .propertyTyped("block_list", TypeCodecs.RAW,
                (EntityExplodeWrapper w) -> new ScriptValue.Array(w.blockList()))
            .propertyTyped("yield", TypeCodecs.DOUBLE, (EntityExplodeWrapper w) -> (double) w.yield())
            .methodTypedOpt1("set_yield", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityExplodeWrapper w, Double y) -> { w.setYield(y.floatValue()); return true; });

        PolyTypeRegistry.define("EntityCombustEvent", "Event")
            .propertyTyped("duration", TypeCodecs.DOUBLE, (EntityCombustWrapper w) -> (double) w.duration())
            .methodTypedOpt1("set_duration", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityCombustWrapper w, Double duration) -> { w.setDuration(duration); return true; });

        PolyTypeRegistry.define("EntityRegainHealthEvent", "Event")
            .propertyTyped("amount", TypeCodecs.DOUBLE, (EntityRegainHealthWrapper w) -> (double) w.amount())
            .methodTypedOpt1("set_amount", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityRegainHealthWrapper w, Double amount) -> { w.setAmount(amount); return true; })
            .propertyTyped("reason", TypeCodecs.STRING, (EntityRegainHealthWrapper w) -> w.reason());

        PolyTypeRegistry.define("EntityTeleportEvent", "Event")
            .propertyTyped("from", TypeCodecs.RAW, (EntityTeleportWrapper w) -> w.from())
            .propertyTyped("to", TypeCodecs.RAW, (EntityTeleportWrapper w) -> w.to())
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (EntityTeleportWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("EntitySpawnEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (EntitySpawnWrapper w) -> w.entity())
            .propertyTyped("location", TypeCodecs.RAW, (EntitySpawnWrapper w) -> w.location());

        PolyTypeRegistry.define("EntityShootBowEvent", "Event")
            .propertyTyped("projectile", TypeCodecs.RAW, (EntityShootBowWrapper w) -> w.projectile())
            .propertyTyped("force", TypeCodecs.DOUBLE, (EntityShootBowWrapper w) -> (double) w.force())
            .propertyTyped("consume_item", TypeCodecs.BOOL, (EntityShootBowWrapper w) -> w.consumeItem())
            .methodTypedOpt1("set_consume_item", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (EntityShootBowWrapper w, Boolean consume) -> { w.setConsumeItem(consume); return true; });

        PolyTypeRegistry.define("EntityChangeBlockEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (EntityChangeBlockWrapper w) -> w.block())
            .propertyTyped("to", TypeCodecs.STRING, (EntityChangeBlockWrapper w) -> w.to());

        PolyTypeRegistry.define("EntityPotionEffectEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (EntityPotionEffectWrapper w) -> w.entity())
            .propertyTyped("cause", TypeCodecs.STRING, (EntityPotionEffectWrapper w) -> w.cause())
            .propertyTyped("action", TypeCodecs.STRING, (EntityPotionEffectWrapper w) -> w.action());

        PolyTypeRegistry.define("EntityBreedEvent", "Event")
            .propertyTyped("mother", TypeCodecs.RAW, (EntityBreedWrapper w) -> w.mother())
            .propertyTyped("father", TypeCodecs.RAW, (EntityBreedWrapper w) -> w.father())
            .propertyTyped("breeder", TypeCodecs.RAW, (EntityBreedWrapper w) -> w.breeder())
            .propertyTyped("child", TypeCodecs.RAW, (EntityBreedWrapper w) -> w.child());

        PolyTypeRegistry.define("CreatureSpawnEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (CreatureSpawnWrapper w) -> w.entity())
            .propertyTyped("reason", TypeCodecs.STRING, (CreatureSpawnWrapper w) -> w.reason());

        PolyTypeRegistry.define("EntityToggleGlideEvent", "Event")
            .propertyTyped("is_gliding", TypeCodecs.BOOL, (EntityToggleGlideWrapper w) -> w.isGliding());

        PolyTypeRegistry.define("EntityKnockbackEvent", "Event")
            .propertyTyped("cause", TypeCodecs.STRING, (EntityKnockbackWrapper w) -> w.cause())
            .propertyTyped("knockback", TypeCodecs.RAW, (EntityKnockbackWrapper w) -> w.knockback())
            .methodTyped1("set_knockback", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (EntityKnockbackWrapper w, ScriptValue kb) -> { w.setKnockback(kb); return true; });

        // ---- Dedicated wrappers for the common vanilla BLOCK events — same idea as the PLAYER and
        // ENTITY batches above, for events that fire on a world block rather than a player/entity.
        PolyTypeRegistry.define("BlockBreakEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockBreakWrapper w) -> w.block())
            .propertyTyped("player", TypeCodecs.RAW, (BlockBreakWrapper w) -> w.player())
            .propertyTyped("drop_items", TypeCodecs.BOOL, (BlockBreakWrapper w) -> w.dropItems())
            .methodTypedOpt1("set_drop_items", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (BlockBreakWrapper w, Boolean drop) -> { w.setDropItems(drop); return true; })
            .propertyTyped("exp_to_drop", TypeCodecs.DOUBLE, (BlockBreakWrapper w) -> (double) w.expToDrop())
            .methodTypedOpt1("set_exp_to_drop", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (BlockBreakWrapper w, Double exp) -> { w.setExpToDrop(exp.intValue()); return true; });

        PolyTypeRegistry.define("BlockPlaceEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockPlaceWrapper w) -> w.block())
            .propertyTyped("player", TypeCodecs.RAW, (BlockPlaceWrapper w) -> w.player())
            .propertyTyped("block_placed_against", TypeCodecs.RAW, (BlockPlaceWrapper w) -> w.blockPlacedAgainst())
            .propertyTyped("can_build", TypeCodecs.BOOL, (BlockPlaceWrapper w) -> w.canBuild());

        // BlockMultiPlaceEvent IS-A BlockPlaceEvent (both the Bukkit event and this wrapper), so it
        // extends "BlockPlaceEvent" and inherits block/player/block_placed_against/can_build for free.
        PolyTypeRegistry.define("BlockMultiPlaceEvent", "BlockPlaceEvent");

        PolyTypeRegistry.define("BlockBurnEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockBurnWrapper w) -> w.block())
            .propertyTyped("ignition_source", TypeCodecs.RAW, (BlockBurnWrapper w) -> w.ignitionSource());

        PolyTypeRegistry.define("BlockExplodeEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockExplodeWrapper w) -> w.block())
            // RAW rather than listOf — already-boxed Objs, see EntityExplodeEvent.block_list.
            .propertyTyped("block_list", TypeCodecs.RAW,
                (BlockExplodeWrapper w) -> new ScriptValue.Array(w.blockList()))
            .propertyTyped("yield", TypeCodecs.DOUBLE, (BlockExplodeWrapper w) -> (double) w.yield())
            .methodTypedOpt1("set_yield", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (BlockExplodeWrapper w, Double y) -> { w.setYield(y.floatValue()); return true; });

        PolyTypeRegistry.define("BlockIgniteEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockIgniteWrapper w) -> w.block())
            .propertyTyped("cause", TypeCodecs.STRING, (BlockIgniteWrapper w) -> w.cause())
            .propertyTyped("ignition_source_entity", TypeCodecs.RAW, (BlockIgniteWrapper w) -> w.ignitionSourceEntity());

        PolyTypeRegistry.define("BlockFadeEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockFadeWrapper w) -> w.block())
            .propertyTyped("new_state", TypeCodecs.STRING, (BlockFadeWrapper w) -> w.newStateMaterial());

        // BlockGrowEvent -> BlockFormEvent -> BlockSpreadEvent form a real Java hierarchy on both
        // the Bukkit event side and this wrapper side, so each PolyType extends the previous one and
        // inherits block/new_state for free; BlockSpreadEvent adds "source".
        PolyTypeRegistry.define("BlockGrowEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockGrowWrapper w) -> w.block())
            .propertyTyped("new_state", TypeCodecs.STRING, (BlockGrowWrapper w) -> w.newStateMaterial());

        PolyTypeRegistry.define("BlockFormEvent", "BlockGrowEvent");

        PolyTypeRegistry.define("BlockSpreadEvent", "BlockFormEvent")
            .propertyTyped("source", TypeCodecs.RAW, (BlockSpreadWrapper w) -> w.source());

        PolyTypeRegistry.define("BlockRedstoneEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockRedstoneWrapper w) -> w.block())
            .propertyTyped("old_current", TypeCodecs.DOUBLE, (BlockRedstoneWrapper w) -> (double) w.oldCurrent())
            .propertyTyped("new_current", TypeCodecs.DOUBLE, (BlockRedstoneWrapper w) -> (double) w.newCurrent())
            .methodTypedOpt1("set_new_current", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (BlockRedstoneWrapper w, Double current) -> { w.setNewCurrent(current.intValue()); return true; });

        PolyTypeRegistry.define("BlockPistonExtendEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockPistonExtendWrapper w) -> w.block())
            .propertyTyped("direction", TypeCodecs.STRING, (BlockPistonExtendWrapper w) -> w.direction())
            .propertyTyped("length", TypeCodecs.DOUBLE, (BlockPistonExtendWrapper w) -> (double) w.length());

        PolyTypeRegistry.define("BlockPistonRetractEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockPistonRetractWrapper w) -> w.block())
            .propertyTyped("direction", TypeCodecs.STRING, (BlockPistonRetractWrapper w) -> w.direction());

        PolyTypeRegistry.define("BlockDispenseEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockDispenseWrapper w) -> w.block())
            .propertyTyped("item", TypeCodecs.RAW, (BlockDispenseWrapper w) -> w.item())
            .propertyTyped("velocity", TypeCodecs.RAW, (BlockDispenseWrapper w) -> w.velocity())
            .methodTyped1("set_velocity", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (BlockDispenseWrapper w, ScriptValue v) -> { w.setVelocity(v); return true; });

        PolyTypeRegistry.define("BlockDamageEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockDamageWrapper w) -> w.block())
            .propertyTyped("player", TypeCodecs.RAW, (BlockDamageWrapper w) -> w.player())
            .propertyTyped("instabreak", TypeCodecs.BOOL, (BlockDamageWrapper w) -> w.instabreak())
            .methodTypedOpt1("set_instabreak", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (BlockDamageWrapper w, Boolean instabreak) -> { w.setInstabreak(instabreak); return true; });

        PolyTypeRegistry.define("BlockPhysicsEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (BlockPhysicsWrapper w) -> w.block())
            .propertyTyped("changed_type", TypeCodecs.STRING, (BlockPhysicsWrapper w) -> w.changedTypeMaterial());

        PolyTypeRegistry.define("SignChangeEvent", "Event")
            .propertyTyped("block", TypeCodecs.RAW, (SignChangeWrapper w) -> w.block())
            .propertyTyped("player", TypeCodecs.RAW, (SignChangeWrapper w) -> w.player())
            .methodTypedOpt1("get_line", TypeCodecs.DOUBLE, 0.0, TypeCodecs.STRING,
                (SignChangeWrapper w, Double idx) -> w.getLine(idx.intValue()))
            .methodTyped2("set_line", TypeCodecs.DOUBLE, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (SignChangeWrapper w, Double idxArg, String line) -> {
                    w.setLine(idxArg.intValue(), line);
                    return true;
                });

        // ---- Dedicated wrappers for the common vanilla INVENTORY/ITEM events — same idea as the
        // PLAYER/ENTITY/BLOCK batches above, for menu clicks, furnaces, brewing, and dropped-item
        // entities.
        PolyTypeRegistry.define("InventoryClickEvent", "Event")
            .propertyTyped("slot", TypeCodecs.DOUBLE, (InventoryClickWrapper w) -> (double) w.slot())
            .propertyTyped("raw_slot", TypeCodecs.DOUBLE, (InventoryClickWrapper w) -> (double) w.rawSlot())
            .propertyTyped("current_item", TypeCodecs.RAW, (InventoryClickWrapper w) -> w.currentItem())
            .methodTypedOpt1("set_current_item", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (InventoryClickWrapper w, ScriptValue item) -> { w.setCurrentItem(item); return true; })
            .propertyTyped("cursor", TypeCodecs.RAW, (InventoryClickWrapper w) -> w.cursor())
            .methodTypedOpt1("set_cursor", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (InventoryClickWrapper w, ScriptValue cursor) -> { w.setCursor(cursor); return true; })
            .propertyTyped("click_type", TypeCodecs.STRING, (InventoryClickWrapper w) -> w.clickType())
            .propertyTyped("action", TypeCodecs.STRING, (InventoryClickWrapper w) -> w.action())
            .propertyTyped("who_clicked", TypeCodecs.RAW, (InventoryClickWrapper w) -> w.whoClicked());

        // CraftItemEvent IS-A InventoryClickEvent (both the Bukkit event and this wrapper), so it
        // extends "InventoryClickEvent" and inherits slot/current_item/who_clicked/... for free.
        PolyTypeRegistry.define("CraftItemEvent", "InventoryClickEvent")
            .propertyTyped("recipe_result", TypeCodecs.RAW, (CraftItemWrapper w) -> w.recipeResult());

        PolyTypeRegistry.define("InventoryDragEvent", "Event")
            .propertyTyped("cursor", TypeCodecs.RAW, (InventoryDragWrapper w) -> w.cursor())
            .methodTyped1("set_cursor", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (InventoryDragWrapper w, ScriptValue c) -> { w.setCursor(c); return true; })
            .propertyTyped("old_cursor", TypeCodecs.RAW, (InventoryDragWrapper w) -> w.oldCursor())
            .propertyTyped("who_clicked", TypeCodecs.RAW, (InventoryDragWrapper w) -> w.whoClicked());

        PolyTypeRegistry.define("InventoryOpenEvent", "Event")
            .propertyTyped("player", TypeCodecs.RAW, (InventoryOpenWrapper w) -> w.player());

        PolyTypeRegistry.define("InventoryCloseEvent", "Event")
            .propertyTyped("player", TypeCodecs.RAW, (InventoryCloseWrapper w) -> w.player());

        PolyTypeRegistry.define("InventoryMoveItemEvent", "Event")
            .propertyTyped("item", TypeCodecs.RAW, (InventoryMoveItemWrapper w) -> w.item())
            .methodTyped1("set_item", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (InventoryMoveItemWrapper w, ScriptValue item) -> { w.setItem(item); return true; })
            .propertyTyped("source", TypeCodecs.STRING, (InventoryMoveItemWrapper w) -> w.source())
            .propertyTyped("destination", TypeCodecs.STRING, (InventoryMoveItemWrapper w) -> w.destination())
            .propertyTyped("initiator", TypeCodecs.STRING, (InventoryMoveItemWrapper w) -> w.initiator());

        PolyTypeRegistry.define("FurnaceBurnEvent", "Event")
            .propertyTyped("fuel", TypeCodecs.RAW, (FurnaceBurnWrapper w) -> w.fuel())
            .propertyTyped("burn_time", TypeCodecs.DOUBLE, (FurnaceBurnWrapper w) -> (double) w.burnTime())
            .methodTypedOpt1("set_burn_time", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (FurnaceBurnWrapper w, Double ticks) -> { w.setBurnTime(ticks.intValue()); return true; })
            .propertyTyped("burning", TypeCodecs.BOOL, (FurnaceBurnWrapper w) -> w.burning());

        PolyTypeRegistry.define("FurnaceSmeltEvent", "Event")
            .propertyTyped("source", TypeCodecs.RAW, (FurnaceSmeltWrapper w) -> w.source())
            .propertyTyped("result", TypeCodecs.RAW, (FurnaceSmeltWrapper w) -> w.result())
            .methodTyped1("set_result", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (FurnaceSmeltWrapper w, ScriptValue result) -> { w.setResult(result); return true; });

        PolyTypeRegistry.define("FurnaceExtractEvent", "Event")
            .propertyTyped("player", TypeCodecs.RAW, (FurnaceExtractWrapper w) -> w.player())
            .propertyTyped("item_type", TypeCodecs.STRING, (FurnaceExtractWrapper w) -> w.itemType())
            .propertyTyped("item_amount", TypeCodecs.DOUBLE, (FurnaceExtractWrapper w) -> (double) w.itemAmount());

        // BrewEvent — see BrewWrapper's javadoc for why "contents"/"source" from the task description
        // became contents_size/ingredient instead (no full BrewerInventory wrapper type, and no real
        // getSource() accessor on the raw event).
        PolyTypeRegistry.define("BrewEvent", "Event")
            .propertyTyped("contents_size", TypeCodecs.DOUBLE, (BrewWrapper w) -> (double) w.contentsSize())
            .propertyTyped("ingredient", TypeCodecs.RAW, (BrewWrapper w) -> w.ingredient())
            .propertyTyped("results_count", TypeCodecs.DOUBLE, (BrewWrapper w) -> (double) w.resultsCount())
            .propertyTyped("fuel_level", TypeCodecs.DOUBLE, (BrewWrapper w) -> (double) w.fuelLevel());

        PolyTypeRegistry.define("PrepareItemCraftEvent", "Event")
            .propertyTyped("result", TypeCodecs.RAW, (PrepareItemCraftWrapper w) -> w.result())
            .methodTypedOpt1("set_result", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (PrepareItemCraftWrapper w, ScriptValue result) -> { w.setResult(result); return true; });

        PolyTypeRegistry.define("ItemSpawnEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (ItemSpawnWrapper w) -> w.entity())
            .propertyTyped("item", TypeCodecs.RAW, (ItemSpawnWrapper w) -> w.item());

        PolyTypeRegistry.define("ItemDespawnEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (ItemDespawnWrapper w) -> w.entity())
            .propertyTyped("item", TypeCodecs.RAW, (ItemDespawnWrapper w) -> w.item());

        PolyTypeRegistry.define("ItemMergeEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (ItemMergeWrapper w) -> w.entity())
            .propertyTyped("target", TypeCodecs.RAW, (ItemMergeWrapper w) -> w.target());

        PolyTypeRegistry.define("PlayerSwapHandItemsEvent", "Event")
            .propertyTyped("main_hand_item", TypeCodecs.RAW, (PlayerSwapHandItemsWrapper w) -> w.mainHandItem())
            .methodTypedOpt1("set_main_hand_item", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (PlayerSwapHandItemsWrapper w, ScriptValue item) -> { w.setMainHandItem(item); return true; })
            .propertyTyped("off_hand_item", TypeCodecs.RAW, (PlayerSwapHandItemsWrapper w) -> w.offHandItem())
            .methodTypedOpt1("set_off_hand_item", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (PlayerSwapHandItemsWrapper w, ScriptValue item) -> { w.setOffHandItem(item); return true; });

        PolyTypeRegistry.define("PlayerItemBreakEvent", "Event")
            .propertyTyped("broken_item", TypeCodecs.RAW, (PlayerItemBreakWrapper w) -> w.brokenItem());

        PolyTypeRegistry.define("PlayerItemDamageEvent", "Event")
            .propertyTyped("item", TypeCodecs.RAW, (PlayerItemDamageWrapper w) -> w.item())
            .propertyTyped("damage", TypeCodecs.DOUBLE, (PlayerItemDamageWrapper w) -> (double) w.damage())
            .methodTypedOpt1("set_damage", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerItemDamageWrapper w, Double damage) -> { w.setDamage(damage.intValue()); return true; });

        PolyTypeRegistry.define("PlayerArmorStandManipulateEvent", "Event")
            .propertyTyped("armor_stand", TypeCodecs.RAW, (PlayerArmorStandManipulateWrapper w) -> w.armorStand())
            .propertyTyped("player_item", TypeCodecs.RAW, (PlayerArmorStandManipulateWrapper w) -> w.playerItem())
            .propertyTyped("armor_stand_item", TypeCodecs.RAW, (PlayerArmorStandManipulateWrapper w) -> w.armorStandItem());

        // ---- Dedicated wrappers for the common vanilla VEHICLE events — same idea as the batches
        // above, for minecarts/boats and anything else implementing Vehicle.
        PolyTypeRegistry.define("VehicleEnterEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleEnterWrapper w) -> w.vehicle())
            .propertyTyped("entered", TypeCodecs.RAW, (VehicleEnterWrapper w) -> w.entered());

        PolyTypeRegistry.define("VehicleExitEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleExitWrapper w) -> w.vehicle())
            .propertyTyped("exited", TypeCodecs.RAW, (VehicleExitWrapper w) -> w.exited());

        PolyTypeRegistry.define("VehicleDamageEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleDamageWrapper w) -> w.vehicle())
            .propertyTyped("attacker", TypeCodecs.RAW, (VehicleDamageWrapper w) -> w.attacker())
            .propertyTyped("damage", TypeCodecs.DOUBLE, (VehicleDamageWrapper w) -> (double) w.damage())
            .methodTypedOpt1("set_damage", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (VehicleDamageWrapper w, Double damage) -> { w.setDamage(damage); return true; });

        PolyTypeRegistry.define("VehicleDestroyEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleDestroyWrapper w) -> w.vehicle())
            .propertyTyped("attacker", TypeCodecs.RAW, (VehicleDestroyWrapper w) -> w.attacker());

        // VehicleCollisionEvent itself is abstract and not instantiable, so there's no PolyType for
        // it directly — VehicleBlockCollisionEvent/VehicleEntityCollisionEvent each extend "Event"
        // directly instead (see VehicleBlockCollisionWrapper's javadoc for why the block variant
        // isn't Cancellable while the entity variant is).
        PolyTypeRegistry.define("VehicleBlockCollisionEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleBlockCollisionWrapper w) -> w.vehicle())
            .propertyTyped("block", TypeCodecs.RAW, (VehicleBlockCollisionWrapper w) -> w.block());

        PolyTypeRegistry.define("VehicleEntityCollisionEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleEntityCollisionWrapper w) -> w.vehicle())
            .propertyTyped("entity", TypeCodecs.RAW, (VehicleEntityCollisionWrapper w) -> w.entity());

        PolyTypeRegistry.define("VehicleMoveEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleMoveWrapper w) -> w.vehicle())
            .propertyTyped("from", TypeCodecs.RAW, (VehicleMoveWrapper w) -> w.from())
            .propertyTyped("to", TypeCodecs.RAW, (VehicleMoveWrapper w) -> w.to());

        PolyTypeRegistry.define("VehicleCreateEvent", "Event")
            .propertyTyped("vehicle", TypeCodecs.RAW, (VehicleCreateWrapper w) -> w.vehicle());

        // ---- Dedicated wrappers for the common vanilla WEATHER/WORLD events — same idea as the
        // batches above, for rain/thunder/lightning and world/chunk lifecycle.
        PolyTypeRegistry.define("WeatherChangeEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (WeatherChangeWrapper w) -> w.world())
            .propertyTyped("to_weather_state", TypeCodecs.BOOL, (WeatherChangeWrapper w) -> w.toWeatherState());

        PolyTypeRegistry.define("ThunderChangeEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (ThunderChangeWrapper w) -> w.world())
            .propertyTyped("to_thunder_state", TypeCodecs.BOOL, (ThunderChangeWrapper w) -> w.toThunderState());

        PolyTypeRegistry.define("LightningStrikeEvent", "Event")
            .propertyTyped("lightning", TypeCodecs.RAW, (LightningStrikeWrapper w) -> w.lightning())
            .propertyTyped("cause", TypeCodecs.STRING, (LightningStrikeWrapper w) -> w.cause());

        PolyTypeRegistry.define("WorldLoadEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (WorldLoadWrapper w) -> w.world());

        PolyTypeRegistry.define("WorldUnloadEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (WorldUnloadWrapper w) -> w.world());

        PolyTypeRegistry.define("WorldSaveEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (WorldSaveWrapper w) -> w.world());

        PolyTypeRegistry.define("ChunkLoadEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (ChunkLoadWrapper w) -> w.world())
            .propertyTyped("chunk_x", TypeCodecs.DOUBLE, (ChunkLoadWrapper w) -> (double) w.chunkX())
            .propertyTyped("chunk_z", TypeCodecs.DOUBLE, (ChunkLoadWrapper w) -> (double) w.chunkZ())
            .propertyTyped("is_new", TypeCodecs.BOOL, (ChunkLoadWrapper w) -> w.isNew());

        PolyTypeRegistry.define("ChunkUnloadEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (ChunkUnloadWrapper w) -> w.world())
            .propertyTyped("chunk_x", TypeCodecs.DOUBLE, (ChunkUnloadWrapper w) -> (double) w.chunkX())
            .propertyTyped("chunk_z", TypeCodecs.DOUBLE, (ChunkUnloadWrapper w) -> (double) w.chunkZ());

        PolyTypeRegistry.define("SpawnChangeEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (SpawnChangeWrapper w) -> w.world())
            .propertyTyped("previous_location", TypeCodecs.RAW, (SpawnChangeWrapper w) -> w.previousLocation());

        PolyTypeRegistry.define("PortalCreateEvent", "Event")
            .propertyTyped("world", TypeCodecs.RAW, (PortalCreateWrapper w) -> w.world())
            .propertyTyped("reason", TypeCodecs.STRING, (PortalCreateWrapper w) -> w.reason())
            // RAW rather than listOf — already-boxed Objs, see EntityExplodeEvent.block_list.
            .propertyTyped("blocks", TypeCodecs.RAW,
                (PortalCreateWrapper w) -> new ScriptValue.Array(w.blocks()));

        PolyTypeRegistry.define("StructureGrowEvent", "Event")
            .propertyTyped("location", TypeCodecs.RAW, (StructureGrowWrapper w) -> w.location())
            .propertyTyped("player", TypeCodecs.RAW, (StructureGrowWrapper w) -> w.player())
            // RAW rather than listOf — already-boxed Objs, see EntityExplodeEvent.block_list.
            .propertyTyped("blocks", TypeCodecs.RAW,
                (StructureGrowWrapper w) -> new ScriptValue.Array(w.blocks()));

        // ---- SERVER events + remaining "other" player events.
        PolyTypeRegistry.define("ServerCommandEvent", "Event")
            .propertyTyped("command", TypeCodecs.STRING, (ServerCommandEventWrapper w) -> w.command())
            .propertyTyped("sender_name", TypeCodecs.STRING, (ServerCommandEventWrapper w) -> w.senderName())
            .methodTypedOpt1("set_command", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (ServerCommandEventWrapper w, String command) -> { w.setCommand(command); return true; });

        PolyTypeRegistry.define("PluginEnableEvent", "Event")
            .propertyTyped("plugin_name", TypeCodecs.STRING, (PluginEnableEventWrapper w) -> w.pluginName());

        PolyTypeRegistry.define("PluginDisableEvent", "Event")
            .propertyTyped("plugin_name", TypeCodecs.STRING, (PluginDisableEventWrapper w) -> w.pluginName());

        PolyTypeRegistry.define("TabCompleteEvent", "Event")
            .propertyTyped("buffer", TypeCodecs.STRING, (TabCompleteEventWrapper w) -> w.buffer())
            .propertyTyped("completions", TypeCodecs.RAW, (TabCompleteEventWrapper w) -> w.completions())
            // Left untyped: IRREDUCIBLY variadic — the whole args list IS the completion list, and
            // TabCompleteEventWrapper.setCompletions accepts both set_completions([a, b]) and the
            // flat set_completions(a, b, c). A single methodTypedOpt1(RAW, ...) slot binds only
            // args[0], so the flat multi-arg form would silently lose every suggestion but the
            // first: not exact. Handing setCompletions the raw List<ScriptValue> is the contract.
            .method("set_completions", (obj, args) -> {
                ((TabCompleteEventWrapper) obj).setCompletions(args);
                return ScriptValue.of(true);
            });

        PolyTypeRegistry.define("PlayerAdvancementDoneEvent", "Event")
            .propertyTyped("advancement_key", TypeCodecs.STRING, (PlayerAdvancementDoneWrapper w) -> w.advancementKey());

        PolyTypeRegistry.define("PlayerStatisticIncrementEvent", "Event")
            .propertyTyped("statistic", TypeCodecs.STRING, (PlayerStatisticIncrementWrapper w) -> w.statistic())
            .propertyTyped("previous_value", TypeCodecs.DOUBLE, (PlayerStatisticIncrementWrapper w) -> (double) w.previousValue())
            .propertyTyped("new_value", TypeCodecs.DOUBLE, (PlayerStatisticIncrementWrapper w) -> (double) w.newValue());

        PolyTypeRegistry.define("PlayerRiptideEvent", "Event")
            .propertyTyped("item", TypeCodecs.RAW, (PlayerRiptideWrapper w) -> w.item())
            .propertyTyped("velocity", TypeCodecs.RAW, (PlayerRiptideWrapper w) -> w.velocity());

        PolyTypeRegistry.define("PlayerVelocityEvent", "Event")
            .propertyTyped("velocity", TypeCodecs.RAW, (PlayerVelocityWrapper w) -> w.velocity())
            .methodTyped1("set_velocity", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerVelocityWrapper w, ScriptValue v) -> { w.setVelocity(v); return true; });

        PolyTypeRegistry.define("PlayerShearEntityEvent", "Event")
            .propertyTyped("entity", TypeCodecs.RAW, (PlayerShearEntityWrapper w) -> w.entity())
            .propertyTyped("item", TypeCodecs.RAW, (PlayerShearEntityWrapper w) -> w.item());

        PolyTypeRegistry.define("PlayerBucketFillEvent", "Event")
            .propertyTyped("block_clicked", TypeCodecs.RAW, (PlayerBucketFillWrapper w) -> w.blockClicked())
            .propertyTyped("item_stack", TypeCodecs.RAW, (PlayerBucketFillWrapper w) -> w.itemStack());

        PolyTypeRegistry.define("PlayerBucketEmptyEvent", "Event")
            .propertyTyped("block_clicked", TypeCodecs.RAW, (PlayerBucketEmptyWrapper w) -> w.blockClicked())
            .propertyTyped("item_stack", TypeCodecs.RAW, (PlayerBucketEmptyWrapper w) -> w.itemStack());

        PolyTypeRegistry.define("PlayerEggThrowEvent", "Event")
            .propertyTyped("egg", TypeCodecs.RAW, (PlayerEggThrowWrapper w) -> w.egg())
            .propertyTyped("hatching", TypeCodecs.BOOL, (PlayerEggThrowWrapper w) -> w.hatching())
            .methodTypedOpt1("set_hatching", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (PlayerEggThrowWrapper w, Boolean hatching) -> { w.setHatching(hatching); return true; });

        PolyTypeRegistry.define("PlayerFishEvent", "Event")
            .propertyTyped("state", TypeCodecs.STRING, (PlayerFishWrapper w) -> w.state())
            .propertyTyped("caught", TypeCodecs.RAW, (PlayerFishWrapper w) -> w.caught())
            .propertyTyped("hook", TypeCodecs.RAW, (PlayerFishWrapper w) -> w.hook())
            .propertyTyped("exp", TypeCodecs.DOUBLE, (PlayerFishWrapper w) -> (double) w.exp())
            .methodTypedOpt1("set_exp", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerFishWrapper w, Double exp) -> { w.setExp(exp.intValue()); return true; });

        PolyTypeRegistry.define("BroadcastMessageEvent", "Event")
            .propertyTyped("message", TypeCodecs.STRING, (BroadcastMessageEventWrapper w) -> w.message())
            .methodTypedOpt1("set_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (BroadcastMessageEventWrapper w, String msg) -> { w.setMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerElytraBoostEvent", "Event")
            .propertyTyped("item", TypeCodecs.RAW, (PlayerElytraBoostWrapper w) -> w.item())
            .propertyTyped("should_consume", TypeCodecs.BOOL, (PlayerElytraBoostWrapper w) -> w.shouldConsume())
            .methodTypedOpt1("set_should_consume", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (PlayerElytraBoostWrapper w, Boolean consume) -> { w.setShouldConsume(consume); return true; });

        PolyTypeRegistry.define("PlayerAnimationEvent", "Event")
            .propertyTyped("animation_type", TypeCodecs.STRING, (PlayerAnimationWrapper w) -> w.animationType());

        // Generic wrapper around ANY Bukkit/Paper event — the {@code /events} JSON-configured bridge
        // (see dev.arubik.craftengine.events.GenericEventBridge) binds this as {@code event} instead
        // of a purpose-built ScriptEvent subclass, so an arbitrary Bukkit event (one this plugin has
        // no dedicated ScriptEvent for) still gets the same base {@code event.type}/{@code
        // event.cancelled}/{@code event.cancel()} shape, plus {@code class_name} to identify which
        // concrete Bukkit event class fired and reflective get(name)/set(name, value) to read/write
        // its own fields without any Java code specific to that one event. cancel()/cancelled proxy
        // straight through to the real event when it's Cancellable (see BukkitEventWrapper's javadoc).
        //
        // get/set are called from inside a live Bukkit event dispatch, so neither may EVER let an
        // exception escape — a missing getter, a security exception, a type mismatch, anything —
        // it would otherwise propagate into and disrupt whatever else is listening to that event.
        PolyTypeRegistry.define("BukkitEvent", "Event")
            .propertyTyped("class_name", TypeCodecs.STRING,
                (BukkitEventWrapper w) -> w.raw().getClass().getSimpleName())
            .methodTyped1("get", TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (BukkitEventWrapper w, String prop) -> {
                    try {
                        return bukkitGet(w.raw(), prop);
                    } catch (Throwable t) {
                        return ScriptValue.NULL;
                    }
                })
            .methodTyped2("set", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (BukkitEventWrapper w, String prop, ScriptValue value) -> {
                    try {
                        return bukkitSet(w.raw(), prop, value);
                    } catch (Throwable t) {
                        return false;
                    }
                });
    }

    private static ScriptValue bukkitGet(org.bukkit.event.Event raw, String property) {
        String capitalized = capitalize(property);
        Method getter = zeroArgMethod(raw.getClass(), "get" + capitalized);
        if (getter == null) getter = zeroArgMethod(raw.getClass(), "is" + capitalized);
        if (getter == null) return ScriptValue.NULL;
        try {
            getter.setAccessible(true);
            return bukkitToScriptValue(getter.invoke(raw));
        } catch (Throwable t) {
            return ScriptValue.NULL;
        }
    }

    private static boolean bukkitSet(org.bukkit.event.Event raw, String property, ScriptValue value) {
        String setterName = "set" + capitalize(property);
        for (Method m : raw.getClass().getMethods()) {
            if (!m.getName().equals(setterName) || m.getParameterCount() != 1) continue;
            Object arg = bukkitConvertArg(m.getParameterTypes()[0], value);
            if (arg == NO_MATCH) continue;
            try {
                m.setAccessible(true);
                m.invoke(raw, arg);
                return true;
            } catch (Throwable ignored) {
                // Try the next overload (rare, but a class can expose more than one setFoo(...)).
            }
        }
        return false;
    }

    private static final Object NO_MATCH = new Object();

    private static Object bukkitConvertArg(Class<?> paramType, ScriptValue value) {
        if (paramType == String.class) return value.asStr();
        if (paramType == boolean.class || paramType == Boolean.class) return value.asBool();
        if (paramType == int.class || paramType == Integer.class) return (int) value.asNum();
        if (paramType == double.class || paramType == Double.class) return value.asNum();
        if (paramType == float.class || paramType == Float.class) return (float) value.asNum();
        if (paramType == Component.class) return MiniMessage.miniMessage().deserialize(value.asStr());
        return NO_MATCH;
    }

    private static Method zeroArgMethod(Class<?> type, String name) {
        try {
            return type.getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Converts an arbitrary Bukkit getter result into a {@link ScriptValue}, reusing this
     *  codebase's existing Bukkit-to-script conversion idioms ({@code CraftPlayer}/{@code
     *  CraftEntity} → NMS → {@code XType.wrap(...)}) wherever a clean one exists, falling back to a
     *  plain string for anything else (Location/Block/World included — those still reach a script
     *  as their {@code toString()}, one extra line short of a fully typed object). */
    private static ScriptValue bukkitToScriptValue(Object value) {
        if (value == null) return ScriptValue.NULL;
        if (value instanceof String s) return ScriptValue.of(s);
        if (value instanceof Boolean b) return ScriptValue.of(b);
        if (value instanceof Number n) return ScriptValue.of(n.doubleValue());
        if (value instanceof Enum<?> e) return ScriptValue.of(e.name().toLowerCase(Locale.ROOT));
        if (value instanceof org.bukkit.entity.Player p) {
            return PlayerType.wrap(((CraftPlayer) p).getHandle());
        }
        if (value instanceof org.bukkit.entity.Entity e) {
            return dev.arubik.craftengine.script.types.entity.EntityType.wrap(((CraftEntity) e).getHandle());
        }
        if (value instanceof org.bukkit.inventory.ItemStack item) {
            return ScriptValue.ofItem(CraftItemStack.asNMSCopy(item));
        }
        return ScriptValue.of(String.valueOf(value));
    }

    public static ScriptValue wrap(ScriptEvent e) {
        if (e == null) return ScriptValue.NULL;
        String typeName = switch (e) {
            case BreakEvent ignored -> "BreakEvent";
            case ItemActionEvent ignored -> "ItemActionEvent";
            case InteractEvent ignored -> "InteractEvent";
            case TransferEvent ignored -> "TransferEvent";
            case ButtonEvent ignored -> "ButtonEvent";
            case GhostSlotEvent ignored -> "GhostSlotEvent";
            case FormEvent ignored -> "FormEvent";
            case RenderEvent ignored -> "RenderEvent";
            case PlayerJoinWrapper ignored -> "PlayerJoinEvent";
            case PlayerQuitWrapper ignored -> "PlayerQuitEvent";
            case PlayerDeathWrapper ignored -> "PlayerDeathEvent";
            case PlayerRespawnWrapper ignored -> "PlayerRespawnEvent";
            case PlayerMoveWrapper ignored -> "PlayerMoveEvent";
            case PlayerTeleportWrapper ignored -> "PlayerTeleportEvent";
            case PlayerInteractWrapper ignored -> "PlayerInteractEvent";
            case AsyncChatWrapper ignored -> "AsyncChatEvent";
            case PlayerCommandPreprocessWrapper ignored -> "PlayerCommandPreprocessEvent";
            case PlayerLevelChangeWrapper ignored -> "PlayerLevelChangeEvent";
            case PlayerExpChangeWrapper ignored -> "PlayerExpChangeEvent";
            case PlayerToggleSneakWrapper ignored -> "PlayerToggleSneakEvent";
            case PlayerToggleSprintWrapper ignored -> "PlayerToggleSprintEvent";
            case PlayerToggleFlightWrapper ignored -> "PlayerToggleFlightEvent";
            case PlayerBedEnterWrapper ignored -> "PlayerBedEnterEvent";
            case PlayerBedLeaveWrapper ignored -> "PlayerBedLeaveEvent";
            case PlayerGameModeChangeWrapper ignored -> "PlayerGameModeChangeEvent";
            case PlayerKickWrapper ignored -> "PlayerKickEvent";
            case PlayerItemConsumeWrapper ignored -> "PlayerItemConsumeEvent";
            case PlayerDropItemWrapper ignored -> "PlayerDropItemEvent";
            case EntityPickupItemWrapper ignored -> "EntityPickupItemEvent";
            case PlayerPortalWrapper ignored -> "PlayerPortalEvent";
            case PlayerChangedWorldWrapper ignored -> "PlayerChangedWorldEvent";
            case EntityDamageByEntityWrapper ignored -> "EntityDamageByEntityEvent";
            case EntityDamageWrapper ignored -> "EntityDamageEvent";
            case EntityDeathWrapper ignored -> "EntityDeathEvent";
            case EntityTargetWrapper ignored -> "EntityTargetEvent";
            case EntityTameWrapper ignored -> "EntityTameEvent";
            case EntityExplodeWrapper ignored -> "EntityExplodeEvent";
            case EntityCombustWrapper ignored -> "EntityCombustEvent";
            case EntityRegainHealthWrapper ignored -> "EntityRegainHealthEvent";
            case EntityTeleportWrapper ignored -> "EntityTeleportEvent";
            case EntitySpawnWrapper ignored -> "EntitySpawnEvent";
            case EntityShootBowWrapper ignored -> "EntityShootBowEvent";
            case EntityChangeBlockWrapper ignored -> "EntityChangeBlockEvent";
            case EntityPotionEffectWrapper ignored -> "EntityPotionEffectEvent";
            case EntityBreedWrapper ignored -> "EntityBreedEvent";
            case CreatureSpawnWrapper ignored -> "CreatureSpawnEvent";
            case EntityToggleGlideWrapper ignored -> "EntityToggleGlideEvent";
            case EntityKnockbackWrapper ignored -> "EntityKnockbackEvent";
            // BlockMultiPlaceWrapper extends BlockPlaceWrapper, and BlockFormWrapper/
            // BlockSpreadWrapper extend BlockGrowWrapper — the more specific case MUST be matched
            // before its supertype's case, same reasoning as EntityDamageByEntityWrapper above.
            case BlockMultiPlaceWrapper ignored -> "BlockMultiPlaceEvent";
            case BlockBreakWrapper ignored -> "BlockBreakEvent";
            case BlockPlaceWrapper ignored -> "BlockPlaceEvent";
            case BlockBurnWrapper ignored -> "BlockBurnEvent";
            case BlockExplodeWrapper ignored -> "BlockExplodeEvent";
            case BlockIgniteWrapper ignored -> "BlockIgniteEvent";
            case BlockFadeWrapper ignored -> "BlockFadeEvent";
            case BlockSpreadWrapper ignored -> "BlockSpreadEvent";
            case BlockFormWrapper ignored -> "BlockFormEvent";
            case BlockGrowWrapper ignored -> "BlockGrowEvent";
            case BlockRedstoneWrapper ignored -> "BlockRedstoneEvent";
            case BlockPistonExtendWrapper ignored -> "BlockPistonExtendEvent";
            case BlockPistonRetractWrapper ignored -> "BlockPistonRetractEvent";
            case BlockDispenseWrapper ignored -> "BlockDispenseEvent";
            case BlockDamageWrapper ignored -> "BlockDamageEvent";
            case BlockPhysicsWrapper ignored -> "BlockPhysicsEvent";
            case SignChangeWrapper ignored -> "SignChangeEvent";
            // ---- INVENTORY/ITEM events. CraftItemWrapper extends InventoryClickWrapper (its raw
            // event IS-A InventoryClickEvent, same "IS-A" pattern as BlockMultiPlaceWrapper above),
            // so its case MUST come before InventoryClickWrapper's.
            case CraftItemWrapper ignored -> "CraftItemEvent";
            case InventoryClickWrapper ignored -> "InventoryClickEvent";
            case InventoryDragWrapper ignored -> "InventoryDragEvent";
            case InventoryOpenWrapper ignored -> "InventoryOpenEvent";
            case InventoryCloseWrapper ignored -> "InventoryCloseEvent";
            case InventoryMoveItemWrapper ignored -> "InventoryMoveItemEvent";
            case FurnaceBurnWrapper ignored -> "FurnaceBurnEvent";
            case FurnaceSmeltWrapper ignored -> "FurnaceSmeltEvent";
            case FurnaceExtractWrapper ignored -> "FurnaceExtractEvent";
            case BrewWrapper ignored -> "BrewEvent";
            case PrepareItemCraftWrapper ignored -> "PrepareItemCraftEvent";
            case ItemSpawnWrapper ignored -> "ItemSpawnEvent";
            case ItemDespawnWrapper ignored -> "ItemDespawnEvent";
            case ItemMergeWrapper ignored -> "ItemMergeEvent";
            case PlayerSwapHandItemsWrapper ignored -> "PlayerSwapHandItemsEvent";
            case PlayerItemBreakWrapper ignored -> "PlayerItemBreakEvent";
            case PlayerItemDamageWrapper ignored -> "PlayerItemDamageEvent";
            case PlayerArmorStandManipulateWrapper ignored -> "PlayerArmorStandManipulateEvent";
            // ---- VEHICLE events.
            case VehicleEnterWrapper ignored -> "VehicleEnterEvent";
            case VehicleExitWrapper ignored -> "VehicleExitEvent";
            case VehicleDamageWrapper ignored -> "VehicleDamageEvent";
            case VehicleDestroyWrapper ignored -> "VehicleDestroyEvent";
            case VehicleBlockCollisionWrapper ignored -> "VehicleBlockCollisionEvent";
            case VehicleEntityCollisionWrapper ignored -> "VehicleEntityCollisionEvent";
            case VehicleMoveWrapper ignored -> "VehicleMoveEvent";
            case VehicleCreateWrapper ignored -> "VehicleCreateEvent";
            // ---- WEATHER/WORLD events.
            case WeatherChangeWrapper ignored -> "WeatherChangeEvent";
            case ThunderChangeWrapper ignored -> "ThunderChangeEvent";
            case LightningStrikeWrapper ignored -> "LightningStrikeEvent";
            case WorldLoadWrapper ignored -> "WorldLoadEvent";
            case WorldUnloadWrapper ignored -> "WorldUnloadEvent";
            case WorldSaveWrapper ignored -> "WorldSaveEvent";
            case ChunkLoadWrapper ignored -> "ChunkLoadEvent";
            case ChunkUnloadWrapper ignored -> "ChunkUnloadEvent";
            case SpawnChangeWrapper ignored -> "SpawnChangeEvent";
            case PortalCreateWrapper ignored -> "PortalCreateEvent";
            case StructureGrowWrapper ignored -> "StructureGrowEvent";
            // ---- SERVER events + remaining "other" player events.
            case ServerCommandEventWrapper ignored -> "ServerCommandEvent";
            case PluginEnableEventWrapper ignored -> "PluginEnableEvent";
            case PluginDisableEventWrapper ignored -> "PluginDisableEvent";
            case TabCompleteEventWrapper ignored -> "TabCompleteEvent";
            case PlayerAdvancementDoneWrapper ignored -> "PlayerAdvancementDoneEvent";
            case PlayerStatisticIncrementWrapper ignored -> "PlayerStatisticIncrementEvent";
            case PlayerRiptideWrapper ignored -> "PlayerRiptideEvent";
            case PlayerVelocityWrapper ignored -> "PlayerVelocityEvent";
            case PlayerShearEntityWrapper ignored -> "PlayerShearEntityEvent";
            case PlayerBucketFillWrapper ignored -> "PlayerBucketFillEvent";
            case PlayerBucketEmptyWrapper ignored -> "PlayerBucketEmptyEvent";
            case PlayerEggThrowWrapper ignored -> "PlayerEggThrowEvent";
            case PlayerFishWrapper ignored -> "PlayerFishEvent";
            case BroadcastMessageEventWrapper ignored -> "BroadcastMessageEvent";
            case PlayerElytraBoostWrapper ignored -> "PlayerElytraBoostEvent";
            case PlayerAnimationWrapper ignored -> "PlayerAnimationEvent";
            case BukkitEventWrapper ignored -> "BukkitEvent";
            default -> "Event";
        };
        return ScriptValue.ofObj(typeName, e);
    }
}
