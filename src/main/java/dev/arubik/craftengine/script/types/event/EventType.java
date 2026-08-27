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
            .property("type", obj -> ScriptValue.of(event(obj).type()))
            .property("cancelled", obj -> ScriptValue.of(event(obj).isCancelled()))
            .methodTyped0("cancel", TypeCodecs.BOOL, (ScriptEvent e) -> { e.setCancelled(true); return true; })
            .methodTypedOpt1("set_cancelled", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (ScriptEvent e, Boolean cancelled) -> { e.setCancelled(cancelled); return true; });

        PolyTypeRegistry.define("BreakEvent", "Event")
            .property("drops", obj -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : ((BreakEvent) obj).drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // set_drops(item, item, ...) — replaces the DEFAULT drops (this event's own container
            // contents) with exactly the given items. Every arg is treated as one drop, so both
            // event.set_drops(one_item) and event.set_drops(a, b, c) work with no array-literal
            // syntax required.
            // Left untyped: variadic — loops over the whole args list, so it has no fixed arity.
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
            .property("other_entity", obj -> {
                var e = ((ItemActionEvent) obj).otherEntity();
                return e == null ? ScriptValue.NULL
                        : dev.arubik.craftengine.script.types.entity.EntityType.wrap(e);
            })
            .property("amount", obj -> {
                Double a = ((ItemActionEvent) obj).amount();
                return ScriptValue.of(a == null ? 0.0 : a);
            })
            // clicked_block — the Block right-clicked (on_right_click only, null off-block). Combine
            // with clicked_face + Block.place_custom for "place a different custom block depending
            // on which face was clicked" placement routing, entirely from a script.
            .property("clicked_block", obj -> {
                var b = ((ItemActionEvent) obj).clickedBlock();
                if (b == null) return ScriptValue.NULL;
                try {
                    net.minecraft.server.level.ServerLevel level =
                            ((org.bukkit.craftbukkit.CraftWorld) b.getWorld()).getHandle();
                    net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(b.getX(), b.getY(), b.getZ());
                    return dev.arubik.craftengine.script.types.world.BlockType.wrap(level, pos);
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            })
            // clicked_face — which face of clicked_block was clicked ("north".."down"), null if none.
            .property("clicked_face", obj -> {
                var f = ((ItemActionEvent) obj).clickedFace();
                return f == null ? ScriptValue.NULL : ScriptValue.of(f.name().toLowerCase(java.util.Locale.ROOT));
            });

        // Right/left-click interactions — machines' on_right_click/on_left_click and items' click
        // hooks alike.
        PolyTypeRegistry.define("InteractEvent", "Event")
            .property("hand", obj -> {
                String h = ((InteractEvent) obj).hand();
                return h == null ? ScriptValue.NULL : ScriptValue.of(h);
            });

        // on_pipe_transfer — read-only mirror of the type/payload/direction/mode already bound as
        // separate classes (kept for back-compat), plus a real cancel().
        PolyTypeRegistry.define("TransferEvent", "Event")
            .property("transfer_type", obj -> ScriptValue.of(((TransferEvent) obj).transferType()))
            .property("payload", obj -> ((TransferEvent) obj).payload())
            .property("direction", obj -> ScriptValue.of(((TransferEvent) obj).direction()))
            .property("mode", obj -> ScriptValue.of(((TransferEvent) obj).mode()));

        // A menu button's "file.pf:function" script action.
        PolyTypeRegistry.define("ButtonEvent", "Event")
            .property("slot", obj -> ScriptValue.of(((ButtonEvent) obj).slot()))
            .property("click_type", obj -> ScriptValue.of(((ButtonEvent) obj).clickType()));

        // A GHOST slot's set-script — cancel() rejects the click (see MenuSlotType#GHOST).
        PolyTypeRegistry.define("GhostSlotEvent", "Event")
            .property("slot", obj -> ScriptValue.of(((GhostSlotEvent) obj).slot()))
            .property("clicked_id", obj -> ScriptValue.of(((GhostSlotEvent) obj).clickedId()))
            .property("click_type", obj -> ScriptValue.of(((GhostSlotEvent) obj).clickType()));

        // A multiblock's on_form/on_disassemble — event.type distinguishes the two.
        PolyTypeRegistry.define("FormEvent", "Event");

        // on_render — see RenderEvent's javadoc for why cancel() is a no-op here.
        PolyTypeRegistry.define("RenderEvent", "Event")
            .property("holder", obj -> {
                Entity e = ((RenderEvent) obj).holder();
                return e == null ? ScriptValue.NULL : dev.arubik.craftengine.script.types.entity.EntityType.wrap(e);
            })
            .property("slot", obj -> ScriptValue.of(((RenderEvent) obj).slot()));

        // ---- Dedicated wrappers for the common vanilla PLAYER events (see each ScriptEvent
        // subclass's own javadoc for what it's for) — richly typed alternatives to BukkitEvent's
        // reflective get/set for the events scripts are most likely to actually want. Picked
        // automatically over BukkitEventWrapper by GenericEventBridge#buildContext whenever the
        // runtime event type matches one of these; anything else still falls back to BukkitEvent.
        PolyTypeRegistry.define("PlayerJoinEvent", "Event")
            .property("join_message", obj -> ScriptValue.of(((PlayerJoinWrapper) obj).joinMessage()))
            .methodTypedOpt1("set_join_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerJoinWrapper w, String msg) -> { w.setJoinMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerQuitEvent", "Event")
            .property("quit_message", obj -> ScriptValue.of(((PlayerQuitWrapper) obj).quitMessage()))
            .methodTypedOpt1("set_quit_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerQuitWrapper w, String msg) -> { w.setQuitMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerDeathEvent", "Event")
            .property("death_message", obj -> ScriptValue.of(((PlayerDeathWrapper) obj).deathMessage()))
            .methodTypedOpt1("set_death_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerDeathWrapper w, String msg) -> { w.setDeathMessage(msg); return true; })
            .property("drops", obj -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : ((PlayerDeathWrapper) obj).drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // Left untyped: variadic — loops over the whole args list, so it has no fixed arity.
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
            .property("keep_inventory", obj -> ScriptValue.of(((PlayerDeathWrapper) obj).keepInventory()))
            .methodTypedOpt1("set_keep_inventory", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (PlayerDeathWrapper w, Boolean keep) -> { w.setKeepInventory(keep); return true; })
            .property("exp", obj -> ScriptValue.of(((PlayerDeathWrapper) obj).exp()))
            .methodTypedOpt1("set_exp", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerDeathWrapper w, Double exp) -> { w.setExp(exp.intValue()); return true; });

        PolyTypeRegistry.define("PlayerRespawnEvent", "Event")
            .property("respawn_location", obj -> ((PlayerRespawnWrapper) obj).respawnLocation())
            .methodTyped1("set_respawn_location", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerRespawnWrapper w, ScriptValue loc) -> { w.setRespawnLocation(loc); return true; });

        PolyTypeRegistry.define("PlayerMoveEvent", "Event")
            .property("from", obj -> ((PlayerMoveWrapper) obj).from())
            .property("to", obj -> ((PlayerMoveWrapper) obj).to())
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerMoveWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("PlayerTeleportEvent", "Event")
            .property("from", obj -> ((PlayerTeleportWrapper) obj).from())
            .property("to", obj -> ((PlayerTeleportWrapper) obj).to())
            .property("cause", obj -> ScriptValue.of(((PlayerTeleportWrapper) obj).cause()))
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerTeleportWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("PlayerInteractEvent", "Event")
            .property("action", obj -> ScriptValue.of(((PlayerInteractWrapper) obj).action()))
            .property("hand", obj -> {
                String h = ((PlayerInteractWrapper) obj).hand();
                return h == null ? ScriptValue.NULL : ScriptValue.of(h);
            })
            .property("clicked_block", obj -> ((PlayerInteractWrapper) obj).clickedBlock())
            .property("item", obj -> ((PlayerInteractWrapper) obj).item());

        PolyTypeRegistry.define("AsyncChatEvent", "Event")
            .property("message", obj -> ScriptValue.of(((AsyncChatWrapper) obj).message()))
            .methodTypedOpt1("set_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (AsyncChatWrapper w, String msg) -> { w.setMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerCommandPreprocessEvent", "Event")
            .property("message", obj -> ScriptValue.of(((PlayerCommandPreprocessWrapper) obj).message()))
            .methodTypedOpt1("set_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerCommandPreprocessWrapper w, String msg) -> { w.setMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerLevelChangeEvent", "Event")
            .property("old_level", obj -> ScriptValue.of(((PlayerLevelChangeWrapper) obj).oldLevel()))
            .property("new_level", obj -> ScriptValue.of(((PlayerLevelChangeWrapper) obj).newLevel()));

        PolyTypeRegistry.define("PlayerExpChangeEvent", "Event")
            .property("amount", obj -> ScriptValue.of(((PlayerExpChangeWrapper) obj).amount()))
            .methodTypedOpt1("set_amount", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerExpChangeWrapper w, Double amount) -> { w.setAmount(amount.intValue()); return true; });

        PolyTypeRegistry.define("PlayerToggleSneakEvent", "Event")
            .property("is_sneaking", obj -> ScriptValue.of(((PlayerToggleSneakWrapper) obj).isSneaking()));

        PolyTypeRegistry.define("PlayerToggleSprintEvent", "Event")
            .property("is_sprinting", obj -> ScriptValue.of(((PlayerToggleSprintWrapper) obj).isSprinting()));

        PolyTypeRegistry.define("PlayerToggleFlightEvent", "Event")
            .property("is_flying", obj -> ScriptValue.of(((PlayerToggleFlightWrapper) obj).isFlying()));

        PolyTypeRegistry.define("PlayerBedEnterEvent", "Event")
            .property("bed", obj -> ((PlayerBedEnterWrapper) obj).bed())
            .property("bed_enter_result", obj -> ScriptValue.of(((PlayerBedEnterWrapper) obj).bedEnterResult()));

        PolyTypeRegistry.define("PlayerBedLeaveEvent", "Event")
            .property("bed", obj -> ((PlayerBedLeaveWrapper) obj).bed());

        PolyTypeRegistry.define("PlayerGameModeChangeEvent", "Event")
            .property("new_game_mode", obj -> ScriptValue.of(((PlayerGameModeChangeWrapper) obj).newGameMode()));

        PolyTypeRegistry.define("PlayerKickEvent", "Event")
            .property("reason", obj -> ScriptValue.of(((PlayerKickWrapper) obj).reason()))
            .methodTypedOpt1("set_reason", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (PlayerKickWrapper w, String reason) -> { w.setReason(reason); return true; });

        PolyTypeRegistry.define("PlayerItemConsumeEvent", "Event")
            .property("item", obj -> ((PlayerItemConsumeWrapper) obj).item())
            .methodTyped1("set_item", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerItemConsumeWrapper w, ScriptValue item) -> { w.setItem(item); return true; });

        PolyTypeRegistry.define("PlayerDropItemEvent", "Event")
            .property("item_drop", obj -> ((PlayerDropItemWrapper) obj).itemDrop());

        PolyTypeRegistry.define("EntityPickupItemEvent", "Event")
            .property("item", obj -> ((EntityPickupItemWrapper) obj).item())
            .property("remaining", obj -> ScriptValue.of(((EntityPickupItemWrapper) obj).remaining()));

        PolyTypeRegistry.define("PlayerPortalEvent", "Event")
            .property("from", obj -> ((PlayerPortalWrapper) obj).from())
            .property("to", obj -> ((PlayerPortalWrapper) obj).to())
            .property("cause", obj -> ScriptValue.of(((PlayerPortalWrapper) obj).cause()))
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerPortalWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("PlayerChangedWorldEvent", "Event")
            .property("from_world", obj -> ((PlayerChangedWorldWrapper) obj).fromWorld());

        // ---- Dedicated wrappers for the common vanilla ENTITY events — same idea as the PLAYER
        // batch above, for events that fire on entities in general (mobs included) rather than only
        // on players.
        PolyTypeRegistry.define("EntityDeathEvent", "Event")
            .property("entity", obj -> ((EntityDeathWrapper) obj).entity())
            .property("drops", obj -> {
                List<ScriptValue> out = new ArrayList<>();
                for (ItemStack s : ((EntityDeathWrapper) obj).drops()) out.add(ScriptValue.ofItem(s));
                return new ScriptValue.Array(out);
            })
            // Left untyped: variadic — loops over the whole args list, so it has no fixed arity.
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
            .property("dropped_exp", obj -> ScriptValue.of(((EntityDeathWrapper) obj).droppedExp()))
            .methodTypedOpt1("set_dropped_exp", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityDeathWrapper w, Double exp) -> { w.setDroppedExp(exp.intValue()); return true; });

        PolyTypeRegistry.define("EntityDamageEvent", "Event")
            .property("entity", obj -> ((EntityDamageWrapper) obj).entity())
            .property("damage", obj -> ScriptValue.of(((EntityDamageWrapper) obj).damage()))
            .methodTypedOpt1("set_damage", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityDamageWrapper w, Double damage) -> { w.setDamage(damage); return true; })
            .property("cause", obj -> ScriptValue.of(((EntityDamageWrapper) obj).cause()));

        PolyTypeRegistry.define("EntityDamageByEntityEvent", "EntityDamageEvent")
            .property("damager", obj -> ((EntityDamageByEntityWrapper) obj).damager());

        PolyTypeRegistry.define("EntityTargetEvent", "Event")
            .property("target", obj -> ((EntityTargetWrapper) obj).target())
            .methodTypedOpt1("set_target", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (EntityTargetWrapper w, ScriptValue target) -> { w.setTarget(target); return true; })
            .property("reason", obj -> ScriptValue.of(((EntityTargetWrapper) obj).reason()));

        PolyTypeRegistry.define("EntityTameEvent", "Event")
            .property("owner", obj -> ((EntityTameWrapper) obj).owner());

        PolyTypeRegistry.define("EntityExplodeEvent", "Event")
            .property("location", obj -> ((EntityExplodeWrapper) obj).location())
            .property("block_list", obj -> new ScriptValue.Array(((EntityExplodeWrapper) obj).blockList()))
            .property("yield", obj -> ScriptValue.of(((EntityExplodeWrapper) obj).yield()))
            .methodTypedOpt1("set_yield", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityExplodeWrapper w, Double y) -> { w.setYield(y.floatValue()); return true; });

        PolyTypeRegistry.define("EntityCombustEvent", "Event")
            .property("duration", obj -> ScriptValue.of(((EntityCombustWrapper) obj).duration()))
            .methodTypedOpt1("set_duration", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityCombustWrapper w, Double duration) -> { w.setDuration(duration); return true; });

        PolyTypeRegistry.define("EntityRegainHealthEvent", "Event")
            .property("amount", obj -> ScriptValue.of(((EntityRegainHealthWrapper) obj).amount()))
            .methodTypedOpt1("set_amount", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (EntityRegainHealthWrapper w, Double amount) -> { w.setAmount(amount); return true; })
            .property("reason", obj -> ScriptValue.of(((EntityRegainHealthWrapper) obj).reason()));

        PolyTypeRegistry.define("EntityTeleportEvent", "Event")
            .property("from", obj -> ((EntityTeleportWrapper) obj).from())
            .property("to", obj -> ((EntityTeleportWrapper) obj).to())
            .methodTyped1("set_to", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (EntityTeleportWrapper w, ScriptValue to) -> { w.setTo(to); return true; });

        PolyTypeRegistry.define("EntitySpawnEvent", "Event")
            .property("entity", obj -> ((EntitySpawnWrapper) obj).entity())
            .property("location", obj -> ((EntitySpawnWrapper) obj).location());

        PolyTypeRegistry.define("EntityShootBowEvent", "Event")
            .property("projectile", obj -> ((EntityShootBowWrapper) obj).projectile())
            .property("force", obj -> ScriptValue.of(((EntityShootBowWrapper) obj).force()))
            .property("consume_item", obj -> ScriptValue.of(((EntityShootBowWrapper) obj).consumeItem()))
            .methodTypedOpt1("set_consume_item", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (EntityShootBowWrapper w, Boolean consume) -> { w.setConsumeItem(consume); return true; });

        PolyTypeRegistry.define("EntityChangeBlockEvent", "Event")
            .property("block", obj -> ((EntityChangeBlockWrapper) obj).block())
            .property("to", obj -> ScriptValue.of(((EntityChangeBlockWrapper) obj).to()));

        PolyTypeRegistry.define("EntityPotionEffectEvent", "Event")
            .property("entity", obj -> ((EntityPotionEffectWrapper) obj).entity())
            .property("cause", obj -> ScriptValue.of(((EntityPotionEffectWrapper) obj).cause()))
            .property("action", obj -> ScriptValue.of(((EntityPotionEffectWrapper) obj).action()));

        PolyTypeRegistry.define("EntityBreedEvent", "Event")
            .property("mother", obj -> ((EntityBreedWrapper) obj).mother())
            .property("father", obj -> ((EntityBreedWrapper) obj).father())
            .property("breeder", obj -> ((EntityBreedWrapper) obj).breeder())
            .property("child", obj -> ((EntityBreedWrapper) obj).child());

        PolyTypeRegistry.define("CreatureSpawnEvent", "Event")
            .property("entity", obj -> ((CreatureSpawnWrapper) obj).entity())
            .property("reason", obj -> ScriptValue.of(((CreatureSpawnWrapper) obj).reason()));

        PolyTypeRegistry.define("EntityToggleGlideEvent", "Event")
            .property("is_gliding", obj -> ScriptValue.of(((EntityToggleGlideWrapper) obj).isGliding()));

        PolyTypeRegistry.define("EntityKnockbackEvent", "Event")
            .property("cause", obj -> ScriptValue.of(((EntityKnockbackWrapper) obj).cause()))
            .property("knockback", obj -> ((EntityKnockbackWrapper) obj).knockback())
            .methodTyped1("set_knockback", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (EntityKnockbackWrapper w, ScriptValue kb) -> { w.setKnockback(kb); return true; });

        // ---- Dedicated wrappers for the common vanilla BLOCK events — same idea as the PLAYER and
        // ENTITY batches above, for events that fire on a world block rather than a player/entity.
        PolyTypeRegistry.define("BlockBreakEvent", "Event")
            .property("block", obj -> ((BlockBreakWrapper) obj).block())
            .property("player", obj -> ((BlockBreakWrapper) obj).player())
            .property("drop_items", obj -> ScriptValue.of(((BlockBreakWrapper) obj).dropItems()))
            .methodTypedOpt1("set_drop_items", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (BlockBreakWrapper w, Boolean drop) -> { w.setDropItems(drop); return true; })
            .property("exp_to_drop", obj -> ScriptValue.of(((BlockBreakWrapper) obj).expToDrop()))
            .methodTypedOpt1("set_exp_to_drop", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (BlockBreakWrapper w, Double exp) -> { w.setExpToDrop(exp.intValue()); return true; });

        PolyTypeRegistry.define("BlockPlaceEvent", "Event")
            .property("block", obj -> ((BlockPlaceWrapper) obj).block())
            .property("player", obj -> ((BlockPlaceWrapper) obj).player())
            .property("block_placed_against", obj -> ((BlockPlaceWrapper) obj).blockPlacedAgainst())
            .property("can_build", obj -> ScriptValue.of(((BlockPlaceWrapper) obj).canBuild()));

        // BlockMultiPlaceEvent IS-A BlockPlaceEvent (both the Bukkit event and this wrapper), so it
        // extends "BlockPlaceEvent" and inherits block/player/block_placed_against/can_build for free.
        PolyTypeRegistry.define("BlockMultiPlaceEvent", "BlockPlaceEvent");

        PolyTypeRegistry.define("BlockBurnEvent", "Event")
            .property("block", obj -> ((BlockBurnWrapper) obj).block())
            .property("ignition_source", obj -> ((BlockBurnWrapper) obj).ignitionSource());

        PolyTypeRegistry.define("BlockExplodeEvent", "Event")
            .property("block", obj -> ((BlockExplodeWrapper) obj).block())
            .property("block_list", obj -> new ScriptValue.Array(((BlockExplodeWrapper) obj).blockList()))
            .property("yield", obj -> ScriptValue.of(((BlockExplodeWrapper) obj).yield()))
            .methodTypedOpt1("set_yield", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (BlockExplodeWrapper w, Double y) -> { w.setYield(y.floatValue()); return true; });

        PolyTypeRegistry.define("BlockIgniteEvent", "Event")
            .property("block", obj -> ((BlockIgniteWrapper) obj).block())
            .property("cause", obj -> ScriptValue.of(((BlockIgniteWrapper) obj).cause()))
            .property("ignition_source_entity", obj -> ((BlockIgniteWrapper) obj).ignitionSourceEntity());

        PolyTypeRegistry.define("BlockFadeEvent", "Event")
            .property("block", obj -> ((BlockFadeWrapper) obj).block())
            .property("new_state", obj -> ScriptValue.of(((BlockFadeWrapper) obj).newStateMaterial()));

        // BlockGrowEvent -> BlockFormEvent -> BlockSpreadEvent form a real Java hierarchy on both
        // the Bukkit event side and this wrapper side, so each PolyType extends the previous one and
        // inherits block/new_state for free; BlockSpreadEvent adds "source".
        PolyTypeRegistry.define("BlockGrowEvent", "Event")
            .property("block", obj -> ((BlockGrowWrapper) obj).block())
            .property("new_state", obj -> ScriptValue.of(((BlockGrowWrapper) obj).newStateMaterial()));

        PolyTypeRegistry.define("BlockFormEvent", "BlockGrowEvent");

        PolyTypeRegistry.define("BlockSpreadEvent", "BlockFormEvent")
            .property("source", obj -> ((BlockSpreadWrapper) obj).source());

        PolyTypeRegistry.define("BlockRedstoneEvent", "Event")
            .property("block", obj -> ((BlockRedstoneWrapper) obj).block())
            .property("old_current", obj -> ScriptValue.of(((BlockRedstoneWrapper) obj).oldCurrent()))
            .property("new_current", obj -> ScriptValue.of(((BlockRedstoneWrapper) obj).newCurrent()))
            .methodTypedOpt1("set_new_current", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (BlockRedstoneWrapper w, Double current) -> { w.setNewCurrent(current.intValue()); return true; });

        PolyTypeRegistry.define("BlockPistonExtendEvent", "Event")
            .property("block", obj -> ((BlockPistonExtendWrapper) obj).block())
            .property("direction", obj -> ScriptValue.of(((BlockPistonExtendWrapper) obj).direction()))
            .property("length", obj -> ScriptValue.of(((BlockPistonExtendWrapper) obj).length()));

        PolyTypeRegistry.define("BlockPistonRetractEvent", "Event")
            .property("block", obj -> ((BlockPistonRetractWrapper) obj).block())
            .property("direction", obj -> ScriptValue.of(((BlockPistonRetractWrapper) obj).direction()));

        PolyTypeRegistry.define("BlockDispenseEvent", "Event")
            .property("block", obj -> ((BlockDispenseWrapper) obj).block())
            .property("item", obj -> ((BlockDispenseWrapper) obj).item())
            .property("velocity", obj -> ((BlockDispenseWrapper) obj).velocity())
            .methodTyped1("set_velocity", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (BlockDispenseWrapper w, ScriptValue v) -> { w.setVelocity(v); return true; });

        PolyTypeRegistry.define("BlockDamageEvent", "Event")
            .property("block", obj -> ((BlockDamageWrapper) obj).block())
            .property("player", obj -> ((BlockDamageWrapper) obj).player())
            .property("instabreak", obj -> ScriptValue.of(((BlockDamageWrapper) obj).instabreak()))
            .methodTypedOpt1("set_instabreak", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (BlockDamageWrapper w, Boolean instabreak) -> { w.setInstabreak(instabreak); return true; });

        PolyTypeRegistry.define("BlockPhysicsEvent", "Event")
            .property("block", obj -> ((BlockPhysicsWrapper) obj).block())
            .property("changed_type", obj -> ScriptValue.of(((BlockPhysicsWrapper) obj).changedTypeMaterial()));

        PolyTypeRegistry.define("SignChangeEvent", "Event")
            .property("block", obj -> ((SignChangeWrapper) obj).block())
            .property("player", obj -> ((SignChangeWrapper) obj).player())
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
            .property("slot", obj -> ScriptValue.of(((InventoryClickWrapper) obj).slot()))
            .property("raw_slot", obj -> ScriptValue.of(((InventoryClickWrapper) obj).rawSlot()))
            .property("current_item", obj -> ((InventoryClickWrapper) obj).currentItem())
            .methodTypedOpt1("set_current_item", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (InventoryClickWrapper w, ScriptValue item) -> { w.setCurrentItem(item); return true; })
            .property("cursor", obj -> ((InventoryClickWrapper) obj).cursor())
            .methodTypedOpt1("set_cursor", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (InventoryClickWrapper w, ScriptValue cursor) -> { w.setCursor(cursor); return true; })
            .property("click_type", obj -> ScriptValue.of(((InventoryClickWrapper) obj).clickType()))
            .property("action", obj -> ScriptValue.of(((InventoryClickWrapper) obj).action()))
            .property("who_clicked", obj -> ((InventoryClickWrapper) obj).whoClicked());

        // CraftItemEvent IS-A InventoryClickEvent (both the Bukkit event and this wrapper), so it
        // extends "InventoryClickEvent" and inherits slot/current_item/who_clicked/... for free.
        PolyTypeRegistry.define("CraftItemEvent", "InventoryClickEvent")
            .property("recipe_result", obj -> ((CraftItemWrapper) obj).recipeResult());

        PolyTypeRegistry.define("InventoryDragEvent", "Event")
            .property("cursor", obj -> ((InventoryDragWrapper) obj).cursor())
            .methodTyped1("set_cursor", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (InventoryDragWrapper w, ScriptValue c) -> { w.setCursor(c); return true; })
            .property("old_cursor", obj -> ((InventoryDragWrapper) obj).oldCursor())
            .property("who_clicked", obj -> ((InventoryDragWrapper) obj).whoClicked());

        PolyTypeRegistry.define("InventoryOpenEvent", "Event")
            .property("player", obj -> ((InventoryOpenWrapper) obj).player());

        PolyTypeRegistry.define("InventoryCloseEvent", "Event")
            .property("player", obj -> ((InventoryCloseWrapper) obj).player());

        PolyTypeRegistry.define("InventoryMoveItemEvent", "Event")
            .property("item", obj -> ((InventoryMoveItemWrapper) obj).item())
            .methodTyped1("set_item", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (InventoryMoveItemWrapper w, ScriptValue item) -> { w.setItem(item); return true; })
            .property("source", obj -> ScriptValue.of(((InventoryMoveItemWrapper) obj).source()))
            .property("destination", obj -> ScriptValue.of(((InventoryMoveItemWrapper) obj).destination()))
            .property("initiator", obj -> ScriptValue.of(((InventoryMoveItemWrapper) obj).initiator()));

        PolyTypeRegistry.define("FurnaceBurnEvent", "Event")
            .property("fuel", obj -> ((FurnaceBurnWrapper) obj).fuel())
            .property("burn_time", obj -> ScriptValue.of(((FurnaceBurnWrapper) obj).burnTime()))
            .methodTypedOpt1("set_burn_time", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (FurnaceBurnWrapper w, Double ticks) -> { w.setBurnTime(ticks.intValue()); return true; })
            .property("burning", obj -> ScriptValue.of(((FurnaceBurnWrapper) obj).burning()));

        PolyTypeRegistry.define("FurnaceSmeltEvent", "Event")
            .property("source", obj -> ((FurnaceSmeltWrapper) obj).source())
            .property("result", obj -> ((FurnaceSmeltWrapper) obj).result())
            .methodTyped1("set_result", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (FurnaceSmeltWrapper w, ScriptValue result) -> { w.setResult(result); return true; });

        PolyTypeRegistry.define("FurnaceExtractEvent", "Event")
            .property("player", obj -> ((FurnaceExtractWrapper) obj).player())
            .property("item_type", obj -> ScriptValue.of(((FurnaceExtractWrapper) obj).itemType()))
            .property("item_amount", obj -> ScriptValue.of(((FurnaceExtractWrapper) obj).itemAmount()));

        // BrewEvent — see BrewWrapper's javadoc for why "contents"/"source" from the task description
        // became contents_size/ingredient instead (no full BrewerInventory wrapper type, and no real
        // getSource() accessor on the raw event).
        PolyTypeRegistry.define("BrewEvent", "Event")
            .property("contents_size", obj -> ScriptValue.of(((BrewWrapper) obj).contentsSize()))
            .property("ingredient", obj -> ((BrewWrapper) obj).ingredient())
            .property("results_count", obj -> ScriptValue.of(((BrewWrapper) obj).resultsCount()))
            .property("fuel_level", obj -> ScriptValue.of(((BrewWrapper) obj).fuelLevel()));

        PolyTypeRegistry.define("PrepareItemCraftEvent", "Event")
            .property("result", obj -> ((PrepareItemCraftWrapper) obj).result())
            .methodTypedOpt1("set_result", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (PrepareItemCraftWrapper w, ScriptValue result) -> { w.setResult(result); return true; });

        PolyTypeRegistry.define("ItemSpawnEvent", "Event")
            .property("entity", obj -> ((ItemSpawnWrapper) obj).entity())
            .property("item", obj -> ((ItemSpawnWrapper) obj).item());

        PolyTypeRegistry.define("ItemDespawnEvent", "Event")
            .property("entity", obj -> ((ItemDespawnWrapper) obj).entity())
            .property("item", obj -> ((ItemDespawnWrapper) obj).item());

        PolyTypeRegistry.define("ItemMergeEvent", "Event")
            .property("entity", obj -> ((ItemMergeWrapper) obj).entity())
            .property("target", obj -> ((ItemMergeWrapper) obj).target());

        PolyTypeRegistry.define("PlayerSwapHandItemsEvent", "Event")
            .property("main_hand_item", obj -> ((PlayerSwapHandItemsWrapper) obj).mainHandItem())
            .methodTypedOpt1("set_main_hand_item", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (PlayerSwapHandItemsWrapper w, ScriptValue item) -> { w.setMainHandItem(item); return true; })
            .property("off_hand_item", obj -> ((PlayerSwapHandItemsWrapper) obj).offHandItem())
            .methodTypedOpt1("set_off_hand_item", TypeCodecs.RAW, ScriptValue.NULL, TypeCodecs.BOOL,
                (PlayerSwapHandItemsWrapper w, ScriptValue item) -> { w.setOffHandItem(item); return true; });

        PolyTypeRegistry.define("PlayerItemBreakEvent", "Event")
            .property("broken_item", obj -> ((PlayerItemBreakWrapper) obj).brokenItem());

        PolyTypeRegistry.define("PlayerItemDamageEvent", "Event")
            .property("item", obj -> ((PlayerItemDamageWrapper) obj).item())
            .property("damage", obj -> ScriptValue.of(((PlayerItemDamageWrapper) obj).damage()))
            .methodTypedOpt1("set_damage", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerItemDamageWrapper w, Double damage) -> { w.setDamage(damage.intValue()); return true; });

        PolyTypeRegistry.define("PlayerArmorStandManipulateEvent", "Event")
            .property("armor_stand", obj -> ((PlayerArmorStandManipulateWrapper) obj).armorStand())
            .property("player_item", obj -> ((PlayerArmorStandManipulateWrapper) obj).playerItem())
            .property("armor_stand_item", obj -> ((PlayerArmorStandManipulateWrapper) obj).armorStandItem());

        // ---- Dedicated wrappers for the common vanilla VEHICLE events — same idea as the batches
        // above, for minecarts/boats and anything else implementing Vehicle.
        PolyTypeRegistry.define("VehicleEnterEvent", "Event")
            .property("vehicle", obj -> ((VehicleEnterWrapper) obj).vehicle())
            .property("entered", obj -> ((VehicleEnterWrapper) obj).entered());

        PolyTypeRegistry.define("VehicleExitEvent", "Event")
            .property("vehicle", obj -> ((VehicleExitWrapper) obj).vehicle())
            .property("exited", obj -> ((VehicleExitWrapper) obj).exited());

        PolyTypeRegistry.define("VehicleDamageEvent", "Event")
            .property("vehicle", obj -> ((VehicleDamageWrapper) obj).vehicle())
            .property("attacker", obj -> ((VehicleDamageWrapper) obj).attacker())
            .property("damage", obj -> ScriptValue.of(((VehicleDamageWrapper) obj).damage()))
            .methodTypedOpt1("set_damage", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (VehicleDamageWrapper w, Double damage) -> { w.setDamage(damage); return true; });

        PolyTypeRegistry.define("VehicleDestroyEvent", "Event")
            .property("vehicle", obj -> ((VehicleDestroyWrapper) obj).vehicle())
            .property("attacker", obj -> ((VehicleDestroyWrapper) obj).attacker());

        // VehicleCollisionEvent itself is abstract and not instantiable, so there's no PolyType for
        // it directly — VehicleBlockCollisionEvent/VehicleEntityCollisionEvent each extend "Event"
        // directly instead (see VehicleBlockCollisionWrapper's javadoc for why the block variant
        // isn't Cancellable while the entity variant is).
        PolyTypeRegistry.define("VehicleBlockCollisionEvent", "Event")
            .property("vehicle", obj -> ((VehicleBlockCollisionWrapper) obj).vehicle())
            .property("block", obj -> ((VehicleBlockCollisionWrapper) obj).block());

        PolyTypeRegistry.define("VehicleEntityCollisionEvent", "Event")
            .property("vehicle", obj -> ((VehicleEntityCollisionWrapper) obj).vehicle())
            .property("entity", obj -> ((VehicleEntityCollisionWrapper) obj).entity());

        PolyTypeRegistry.define("VehicleMoveEvent", "Event")
            .property("vehicle", obj -> ((VehicleMoveWrapper) obj).vehicle())
            .property("from", obj -> ((VehicleMoveWrapper) obj).from())
            .property("to", obj -> ((VehicleMoveWrapper) obj).to());

        PolyTypeRegistry.define("VehicleCreateEvent", "Event")
            .property("vehicle", obj -> ((VehicleCreateWrapper) obj).vehicle());

        // ---- Dedicated wrappers for the common vanilla WEATHER/WORLD events — same idea as the
        // batches above, for rain/thunder/lightning and world/chunk lifecycle.
        PolyTypeRegistry.define("WeatherChangeEvent", "Event")
            .property("world", obj -> ((WeatherChangeWrapper) obj).world())
            .property("to_weather_state", obj -> ScriptValue.of(((WeatherChangeWrapper) obj).toWeatherState()));

        PolyTypeRegistry.define("ThunderChangeEvent", "Event")
            .property("world", obj -> ((ThunderChangeWrapper) obj).world())
            .property("to_thunder_state", obj -> ScriptValue.of(((ThunderChangeWrapper) obj).toThunderState()));

        PolyTypeRegistry.define("LightningStrikeEvent", "Event")
            .property("lightning", obj -> ((LightningStrikeWrapper) obj).lightning())
            .property("cause", obj -> ScriptValue.of(((LightningStrikeWrapper) obj).cause()));

        PolyTypeRegistry.define("WorldLoadEvent", "Event")
            .property("world", obj -> ((WorldLoadWrapper) obj).world());

        PolyTypeRegistry.define("WorldUnloadEvent", "Event")
            .property("world", obj -> ((WorldUnloadWrapper) obj).world());

        PolyTypeRegistry.define("WorldSaveEvent", "Event")
            .property("world", obj -> ((WorldSaveWrapper) obj).world());

        PolyTypeRegistry.define("ChunkLoadEvent", "Event")
            .property("world", obj -> ((ChunkLoadWrapper) obj).world())
            .property("chunk_x", obj -> ScriptValue.of(((ChunkLoadWrapper) obj).chunkX()))
            .property("chunk_z", obj -> ScriptValue.of(((ChunkLoadWrapper) obj).chunkZ()))
            .property("is_new", obj -> ScriptValue.of(((ChunkLoadWrapper) obj).isNew()));

        PolyTypeRegistry.define("ChunkUnloadEvent", "Event")
            .property("world", obj -> ((ChunkUnloadWrapper) obj).world())
            .property("chunk_x", obj -> ScriptValue.of(((ChunkUnloadWrapper) obj).chunkX()))
            .property("chunk_z", obj -> ScriptValue.of(((ChunkUnloadWrapper) obj).chunkZ()));

        PolyTypeRegistry.define("SpawnChangeEvent", "Event")
            .property("world", obj -> ((SpawnChangeWrapper) obj).world())
            .property("previous_location", obj -> ((SpawnChangeWrapper) obj).previousLocation());

        PolyTypeRegistry.define("PortalCreateEvent", "Event")
            .property("world", obj -> ((PortalCreateWrapper) obj).world())
            .property("reason", obj -> ScriptValue.of(((PortalCreateWrapper) obj).reason()))
            .property("blocks", obj -> new ScriptValue.Array(((PortalCreateWrapper) obj).blocks()));

        PolyTypeRegistry.define("StructureGrowEvent", "Event")
            .property("location", obj -> ((StructureGrowWrapper) obj).location())
            .property("player", obj -> ((StructureGrowWrapper) obj).player())
            .property("blocks", obj -> new ScriptValue.Array(((StructureGrowWrapper) obj).blocks()));

        // ---- SERVER events + remaining "other" player events.
        PolyTypeRegistry.define("ServerCommandEvent", "Event")
            .property("command", obj -> ScriptValue.of(((ServerCommandEventWrapper) obj).command()))
            .property("sender_name", obj -> ScriptValue.of(((ServerCommandEventWrapper) obj).senderName()))
            .methodTypedOpt1("set_command", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (ServerCommandEventWrapper w, String command) -> { w.setCommand(command); return true; });

        PolyTypeRegistry.define("PluginEnableEvent", "Event")
            .property("plugin_name", obj -> ScriptValue.of(((PluginEnableEventWrapper) obj).pluginName()));

        PolyTypeRegistry.define("PluginDisableEvent", "Event")
            .property("plugin_name", obj -> ScriptValue.of(((PluginDisableEventWrapper) obj).pluginName()));

        PolyTypeRegistry.define("TabCompleteEvent", "Event")
            .property("buffer", obj -> ScriptValue.of(((TabCompleteEventWrapper) obj).buffer()))
            .property("completions", obj -> ((TabCompleteEventWrapper) obj).completions())
            // Left untyped: variadic — the whole args list IS the completion list, no fixed arity.
            .method("set_completions", (obj, args) -> {
                ((TabCompleteEventWrapper) obj).setCompletions(args);
                return ScriptValue.of(true);
            });

        PolyTypeRegistry.define("PlayerAdvancementDoneEvent", "Event")
            .property("advancement_key", obj -> ScriptValue.of(((PlayerAdvancementDoneWrapper) obj).advancementKey()));

        PolyTypeRegistry.define("PlayerStatisticIncrementEvent", "Event")
            .property("statistic", obj -> ScriptValue.of(((PlayerStatisticIncrementWrapper) obj).statistic()))
            .property("previous_value", obj -> ScriptValue.of(((PlayerStatisticIncrementWrapper) obj).previousValue()))
            .property("new_value", obj -> ScriptValue.of(((PlayerStatisticIncrementWrapper) obj).newValue()));

        PolyTypeRegistry.define("PlayerRiptideEvent", "Event")
            .property("item", obj -> ((PlayerRiptideWrapper) obj).item())
            .property("velocity", obj -> ((PlayerRiptideWrapper) obj).velocity());

        PolyTypeRegistry.define("PlayerVelocityEvent", "Event")
            .property("velocity", obj -> ((PlayerVelocityWrapper) obj).velocity())
            .methodTyped1("set_velocity", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (PlayerVelocityWrapper w, ScriptValue v) -> { w.setVelocity(v); return true; });

        PolyTypeRegistry.define("PlayerShearEntityEvent", "Event")
            .property("entity", obj -> ((PlayerShearEntityWrapper) obj).entity())
            .property("item", obj -> ((PlayerShearEntityWrapper) obj).item());

        PolyTypeRegistry.define("PlayerBucketFillEvent", "Event")
            .property("block_clicked", obj -> ((PlayerBucketFillWrapper) obj).blockClicked())
            .property("item_stack", obj -> ((PlayerBucketFillWrapper) obj).itemStack());

        PolyTypeRegistry.define("PlayerBucketEmptyEvent", "Event")
            .property("block_clicked", obj -> ((PlayerBucketEmptyWrapper) obj).blockClicked())
            .property("item_stack", obj -> ((PlayerBucketEmptyWrapper) obj).itemStack());

        PolyTypeRegistry.define("PlayerEggThrowEvent", "Event")
            .property("egg", obj -> ((PlayerEggThrowWrapper) obj).egg())
            .property("hatching", obj -> ScriptValue.of(((PlayerEggThrowWrapper) obj).hatching()))
            .methodTypedOpt1("set_hatching", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (PlayerEggThrowWrapper w, Boolean hatching) -> { w.setHatching(hatching); return true; });

        PolyTypeRegistry.define("PlayerFishEvent", "Event")
            .property("state", obj -> ScriptValue.of(((PlayerFishWrapper) obj).state()))
            .property("caught", obj -> ((PlayerFishWrapper) obj).caught())
            .property("hook", obj -> ((PlayerFishWrapper) obj).hook())
            .property("exp", obj -> ScriptValue.of(((PlayerFishWrapper) obj).exp()))
            .methodTypedOpt1("set_exp", TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (PlayerFishWrapper w, Double exp) -> { w.setExp(exp.intValue()); return true; });

        PolyTypeRegistry.define("BroadcastMessageEvent", "Event")
            .property("message", obj -> ScriptValue.of(((BroadcastMessageEventWrapper) obj).message()))
            .methodTypedOpt1("set_message", TypeCodecs.STRING, "", TypeCodecs.BOOL,
                (BroadcastMessageEventWrapper w, String msg) -> { w.setMessage(msg); return true; });

        PolyTypeRegistry.define("PlayerElytraBoostEvent", "Event")
            .property("item", obj -> ((PlayerElytraBoostWrapper) obj).item())
            .property("should_consume", obj -> ScriptValue.of(((PlayerElytraBoostWrapper) obj).shouldConsume()))
            .methodTypedOpt1("set_should_consume", TypeCodecs.BOOL, false, TypeCodecs.BOOL,
                (PlayerElytraBoostWrapper w, Boolean consume) -> { w.setShouldConsume(consume); return true; });

        PolyTypeRegistry.define("PlayerAnimationEvent", "Event")
            .property("animation_type", obj -> ScriptValue.of(((PlayerAnimationWrapper) obj).animationType()));

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
            .property("class_name", obj -> ScriptValue.of(bukkit(obj).raw().getClass().getSimpleName()))
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

    private static BukkitEventWrapper bukkit(Object obj) { return (BukkitEventWrapper) obj; }

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

    private static ScriptEvent event(Object obj) { return (ScriptEvent) obj; }
}
