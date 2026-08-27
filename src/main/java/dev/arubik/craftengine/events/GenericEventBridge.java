package dev.arubik.craftengine.events;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;

import dev.arubik.craftengine.CraftEnginePolyfills;
import dev.arubik.craftengine.script.ScriptCall;
import dev.arubik.craftengine.script.ScriptContext;
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
import dev.arubik.craftengine.script.event.BrewWrapper;
import dev.arubik.craftengine.script.event.BukkitEventWrapper;
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
import dev.arubik.craftengine.script.event.FurnaceBurnWrapper;
import dev.arubik.craftengine.script.event.FurnaceExtractWrapper;
import dev.arubik.craftengine.script.event.FurnaceSmeltWrapper;
import dev.arubik.craftengine.script.event.InventoryClickWrapper;
import dev.arubik.craftengine.script.event.InventoryCloseWrapper;
import dev.arubik.craftengine.script.event.InventoryDragWrapper;
import dev.arubik.craftengine.script.event.InventoryMoveItemWrapper;
import dev.arubik.craftengine.script.event.InventoryOpenWrapper;
import dev.arubik.craftengine.script.event.ItemDespawnWrapper;
import dev.arubik.craftengine.script.event.ItemMergeWrapper;
import dev.arubik.craftengine.script.event.ItemSpawnWrapper;
import dev.arubik.craftengine.script.event.PlayerAdvancementDoneWrapper;
import dev.arubik.craftengine.script.event.PlayerAnimationWrapper;
import dev.arubik.craftengine.script.event.PlayerArmorStandManipulateWrapper;
import dev.arubik.craftengine.script.event.PlayerBedEnterWrapper;
import dev.arubik.craftengine.script.event.PlayerBedLeaveWrapper;
import dev.arubik.craftengine.script.event.PlayerBucketEmptyWrapper;
import dev.arubik.craftengine.script.event.PlayerBucketFillWrapper;
import dev.arubik.craftengine.script.event.PlayerChangedWorldWrapper;
import dev.arubik.craftengine.script.event.PlayerCommandPreprocessWrapper;
import dev.arubik.craftengine.script.event.PlayerDeathWrapper;
import dev.arubik.craftengine.script.event.PlayerDropItemWrapper;
import dev.arubik.craftengine.script.event.PlayerEggThrowWrapper;
import dev.arubik.craftengine.script.event.PlayerElytraBoostWrapper;
import dev.arubik.craftengine.script.event.PlayerExpChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerFishWrapper;
import dev.arubik.craftengine.script.event.PlayerGameModeChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerInteractWrapper;
import dev.arubik.craftengine.script.event.PlayerItemConsumeWrapper;
import dev.arubik.craftengine.script.event.PlayerJoinWrapper;
import dev.arubik.craftengine.script.event.PlayerKickWrapper;
import dev.arubik.craftengine.script.event.PlayerLevelChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerMoveWrapper;
import dev.arubik.craftengine.script.event.PlayerPortalWrapper;
import dev.arubik.craftengine.script.event.PlayerQuitWrapper;
import dev.arubik.craftengine.script.event.PlayerItemBreakWrapper;
import dev.arubik.craftengine.script.event.PlayerItemDamageWrapper;
import dev.arubik.craftengine.script.event.PlayerRespawnWrapper;
import dev.arubik.craftengine.script.event.PlayerRiptideWrapper;
import dev.arubik.craftengine.script.event.PlayerShearEntityWrapper;
import dev.arubik.craftengine.script.event.PlayerStatisticIncrementWrapper;
import dev.arubik.craftengine.script.event.PlayerSwapHandItemsWrapper;
import dev.arubik.craftengine.script.event.PlayerVelocityWrapper;
import dev.arubik.craftengine.script.event.PluginDisableEventWrapper;
import dev.arubik.craftengine.script.event.PluginEnableEventWrapper;
import dev.arubik.craftengine.script.event.PrepareItemCraftWrapper;
import dev.arubik.craftengine.script.event.BroadcastMessageEventWrapper;
import dev.arubik.craftengine.script.event.ScriptEvent;
import dev.arubik.craftengine.script.event.ServerCommandEventWrapper;
import dev.arubik.craftengine.script.event.TabCompleteEventWrapper;
import dev.arubik.craftengine.script.event.SignChangeWrapper;
import dev.arubik.craftengine.script.event.PlayerTeleportWrapper;
import dev.arubik.craftengine.script.event.PlayerToggleFlightWrapper;
import dev.arubik.craftengine.script.event.PlayerToggleSneakWrapper;
import dev.arubik.craftengine.script.event.PlayerToggleSprintWrapper;
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
import dev.arubik.craftengine.script.types.world.ServerType;

/**
 * Registers every loaded {@link EventDefinition} directly against Bukkit's plugin manager via
 * {@code registerEvent(...)}, with no per-event-type Java listener class needed — the whole point
 * of {@code /events} being a JSON-configured bridge from arbitrary Bukkit events to {@code .pf}
 * scripts. See {@link EventDefinition}'s javadoc for the JSON shape and {@link
 * dev.arubik.craftengine.script.types.event.BukkitEventType} for what a script can do with the
 * bound {@code event} object.
 *
 * <p><b>Reload:</b> {@code /cep reload events} calls {@link #reload} — unlike {@code /cmds}'s
 * Brigadier tree (which genuinely can't be rebuilt without a restart), a listener registered via
 * {@code registerEvent(...)} CAN be cleanly torn down and rebuilt at runtime: every static-JSON
 * listener shares the ONE {@link #DUMMY_LISTENER} instance (never used by {@link
 * DynamicEventRegistry}'s own script-driven, per-registration listeners), so {@code
 * HandlerList.unregisterAll(DUMMY_LISTENER)} removes exactly the static set and nothing else
 * before re-reading {@code events/*.json} and re-registering — including a BRAND NEW file dropped
 * in after startup, which {@link #registerAll} alone could never pick up before.
 */
public final class GenericEventBridge {

    private static final Listener DUMMY_LISTENER = new Listener() {};

    private static volatile List<EventDefinition> DEFINITIONS = List.of();

    private GenericEventBridge() {}

    public static void setDefinitions(List<EventDefinition> defs) {
        DEFINITIONS = List.copyOf(defs);
    }

    public static List<EventDefinition> all() {
        return DEFINITIONS;
    }

    /** Re-reads {@code events/*.json} and re-registers every listener from scratch — see the
     *  class javadoc for why this is safe (unlike {@code /cmds}'s Brigadier tree). Returns how
     *  many definitions were loaded. */
    public static int reload(Plugin plugin) {
        org.bukkit.event.HandlerList.unregisterAll(DUMMY_LISTENER);
        EventDefinitionLoader.load();
        registerAll(plugin);
        return DEFINITIONS.size();
    }

    public static void registerAll(Plugin plugin) {
        for (EventDefinition def : DEFINITIONS) {
            try {
                Class<?> raw = Class.forName(def.eventClass());
                if (!Event.class.isAssignableFrom(raw)) {
                    plugin.getLogger().warning("[Events] " + def.eventClass() + " is not a Bukkit Event — skipping " + def.id());
                    continue;
                }
                @SuppressWarnings("unchecked")
                Class<? extends Event> eventClass = (Class<? extends Event>) raw;
                EventPriority priority = EventPriority.valueOf(def.priority().toUpperCase(Locale.ROOT));
                Bukkit.getPluginManager().registerEvent(
                    eventClass, DUMMY_LISTENER, priority,
                    (listener, event) -> handle(def, event),
                    plugin, def.ignoreCancelled()
                );
            } catch (ClassNotFoundException e) {
                plugin.getLogger().warning("[Events] unknown event class '" + def.eventClass() + "' for " + def.id()
                        + " — skipping (maybe a plugin providing it isn't installed?)");
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "[Events] failed to register " + def.id(), t);
            }
        }
    }

    private static void handle(EventDefinition def, Event event) {
        try {
            ScriptCall call = ScriptCall.parse(def.script());
            if (call != null) call.execute(buildContext(event));
        } catch (Throwable t) {
            CraftEnginePolyfills.instance().getLogger()
                .log(Level.WARNING, "[Events] " + def.eventClass() + " -> " + def.script() + " threw", t);
        }
    }

    /** Builds the standard script context for a raw Bukkit event — {@code Server} + the wrapped
     *  {@code event} object, plus best-effort {@code Player}/{@code Entity} auto-binding for any
     *  event implementing the obvious marker interfaces. Shared by the static {@code events/*.json}
     *  bridge above and {@link DynamicEventRegistry}'s one-shot, script-registered handlers, so
     *  both give a script the exact same shape to work with regardless of which one fired it. */
    public static ScriptContext buildContext(Event event) {
        ScriptEvent wrapper = dedicatedWrapper(event);
        var b = ScriptContext.builder();
        b.typedAll(dev.arubik.craftengine.script.ScriptBootstrap.globalSingletons());
        b.event(wrapper);
        if (event instanceof PlayerEvent pe) {
            b.player(((CraftPlayer) pe.getPlayer()).getHandle());
        } else if (event instanceof EntityEvent ee) {
            b.entity(((CraftEntity) ee.getEntity()).getHandle());
            if (ee.getEntity() instanceof org.bukkit.entity.Player p) {
                b.player(((CraftPlayer) p).getHandle());
            }
        }
        return b.build();
    }

    /** Picks the richly-typed {@link ScriptEvent} subclass matching the runtime type of {@code
     *  event}, falling back to the generic reflective {@link BukkitEventWrapper} for anything this
     *  plugin has no dedicated wrapper for. This is the ONE place that decides which wrapper a
     *  script sees — both the static {@code events/*.json} bridge and {@link
     *  DynamicEventRegistry#register} funnel through {@link #buildContext}, so both automatically
     *  gain a new dedicated wrapper the moment it's added here, no changes needed on their end. */
    private static ScriptEvent dedicatedWrapper(Event event) {
        if (event instanceof org.bukkit.event.player.PlayerJoinEvent e) return new PlayerJoinWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerQuitEvent e) return new PlayerQuitWrapper(e);
        if (event instanceof org.bukkit.event.entity.PlayerDeathEvent e) return new PlayerDeathWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerRespawnEvent e) return new PlayerRespawnWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerMoveEvent e) return new PlayerMoveWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerTeleportEvent e) return new PlayerTeleportWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerInteractEvent e) return new PlayerInteractWrapper(e);
        if (event instanceof io.papermc.paper.event.player.AsyncChatEvent e) return new AsyncChatWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerCommandPreprocessEvent e) return new PlayerCommandPreprocessWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerLevelChangeEvent e) return new PlayerLevelChangeWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerExpChangeEvent e) return new PlayerExpChangeWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerToggleSneakEvent e) return new PlayerToggleSneakWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerToggleSprintEvent e) return new PlayerToggleSprintWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerToggleFlightEvent e) return new PlayerToggleFlightWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerBedEnterEvent e) return new PlayerBedEnterWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerBedLeaveEvent e) return new PlayerBedLeaveWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerGameModeChangeEvent e) return new PlayerGameModeChangeWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerKickEvent e) return new PlayerKickWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerItemConsumeEvent e) return new PlayerItemConsumeWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerDropItemEvent e) return new PlayerDropItemWrapper(e);
        // Only the modern PlayerPickupItemEvent replacement — and only when a player actually did
        // the picking up (see EntityPickupItemWrapper's javadoc for why).
        if (event instanceof org.bukkit.event.entity.EntityPickupItemEvent e && e.getEntity() instanceof org.bukkit.entity.Player) {
            return new EntityPickupItemWrapper(e);
        }
        if (event instanceof org.bukkit.event.player.PlayerPortalEvent e) return new PlayerPortalWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerChangedWorldEvent e) return new PlayerChangedWorldWrapper(e);
        // Entity events — subclass checks (EntityDamageByEntityEvent extends EntityDamageEvent,
        // CreatureSpawnEvent extends EntitySpawnEvent) MUST come before their superclass's check,
        // otherwise the more specific wrapper never gets picked.
        if (event instanceof org.bukkit.event.entity.EntityDeathEvent e) return new EntityDeathWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityDamageByEntityEvent e) return new EntityDamageByEntityWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityDamageEvent e) return new EntityDamageWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityTargetEvent e) return new EntityTargetWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityTameEvent e) return new EntityTameWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityExplodeEvent e) return new EntityExplodeWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityCombustEvent e) return new EntityCombustWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityRegainHealthEvent e) return new EntityRegainHealthWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityTeleportEvent e) return new EntityTeleportWrapper(e);
        if (event instanceof org.bukkit.event.entity.CreatureSpawnEvent e) return new CreatureSpawnWrapper(e);
        // ItemSpawnEvent IS-A EntitySpawnEvent, so its check MUST come before EntitySpawnEvent's,
        // same reasoning as CreatureSpawnEvent above.
        if (event instanceof org.bukkit.event.entity.ItemSpawnEvent e) return new ItemSpawnWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntitySpawnEvent e) return new EntitySpawnWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityShootBowEvent e) return new EntityShootBowWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityChangeBlockEvent e) return new EntityChangeBlockWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityPotionEffectEvent e) return new EntityPotionEffectWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityBreedEvent e) return new EntityBreedWrapper(e);
        if (event instanceof org.bukkit.event.entity.EntityToggleGlideEvent e) return new EntityToggleGlideWrapper(e);
        if (event instanceof io.papermc.paper.event.entity.EntityKnockbackEvent e) return new EntityKnockbackWrapper(e);
        if (event instanceof org.bukkit.event.entity.ItemDespawnEvent e) return new ItemDespawnWrapper(e);
        if (event instanceof org.bukkit.event.entity.ItemMergeEvent e) return new ItemMergeWrapper(e);
        // Block events — BlockMultiPlaceEvent extends BlockPlaceEvent, and BlockSpreadEvent extends
        // BlockFormEvent extends BlockGrowEvent, so each subclass check MUST come before its
        // superclass's check, same reasoning as the entity events above.
        if (event instanceof org.bukkit.event.block.BlockMultiPlaceEvent e) return new BlockMultiPlaceWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockPlaceEvent e) return new BlockPlaceWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockBreakEvent e) return new BlockBreakWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockBurnEvent e) return new BlockBurnWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockExplodeEvent e) return new BlockExplodeWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockIgniteEvent e) return new BlockIgniteWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockFadeEvent e) return new BlockFadeWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockSpreadEvent e) return new BlockSpreadWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockFormEvent e) return new BlockFormWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockGrowEvent e) return new BlockGrowWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockRedstoneEvent e) return new BlockRedstoneWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockPistonExtendEvent e) return new BlockPistonExtendWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockPistonRetractEvent e) return new BlockPistonRetractWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockDispenseEvent e) return new BlockDispenseWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockDamageEvent e) return new BlockDamageWrapper(e);
        if (event instanceof org.bukkit.event.block.BlockPhysicsEvent e) return new BlockPhysicsWrapper(e);
        if (event instanceof org.bukkit.event.block.SignChangeEvent e) return new SignChangeWrapper(e);
        // Inventory/item events — CraftItemEvent IS-A InventoryClickEvent, so its check MUST come
        // before InventoryClickEvent's, same reasoning as BlockMultiPlaceEvent above.
        if (event instanceof org.bukkit.event.inventory.CraftItemEvent e) return new CraftItemWrapper(e);
        if (event instanceof org.bukkit.event.inventory.InventoryClickEvent e) return new InventoryClickWrapper(e);
        if (event instanceof org.bukkit.event.inventory.InventoryDragEvent e) return new InventoryDragWrapper(e);
        if (event instanceof org.bukkit.event.inventory.InventoryOpenEvent e) return new InventoryOpenWrapper(e);
        if (event instanceof org.bukkit.event.inventory.InventoryCloseEvent e) return new InventoryCloseWrapper(e);
        if (event instanceof org.bukkit.event.inventory.InventoryMoveItemEvent e) return new InventoryMoveItemWrapper(e);
        if (event instanceof org.bukkit.event.inventory.FurnaceBurnEvent e) return new FurnaceBurnWrapper(e);
        if (event instanceof org.bukkit.event.inventory.FurnaceSmeltEvent e) return new FurnaceSmeltWrapper(e);
        if (event instanceof org.bukkit.event.inventory.FurnaceExtractEvent e) return new FurnaceExtractWrapper(e);
        if (event instanceof org.bukkit.event.inventory.BrewEvent e) return new BrewWrapper(e);
        if (event instanceof org.bukkit.event.inventory.PrepareItemCraftEvent e) return new PrepareItemCraftWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerSwapHandItemsEvent e) return new PlayerSwapHandItemsWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerItemBreakEvent e) return new PlayerItemBreakWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerItemDamageEvent e) return new PlayerItemDamageWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerArmorStandManipulateEvent e) return new PlayerArmorStandManipulateWrapper(e);
        // Vehicle events — VehicleBlockCollisionEvent/VehicleEntityCollisionEvent both extend the
        // abstract (non-instantiable) VehicleCollisionEvent, so there's no separate "supertype"
        // check to worry about ordering against; each is matched directly.
        if (event instanceof org.bukkit.event.vehicle.VehicleEnterEvent e) return new VehicleEnterWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleExitEvent e) return new VehicleExitWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleDamageEvent e) return new VehicleDamageWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleDestroyEvent e) return new VehicleDestroyWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleBlockCollisionEvent e) return new VehicleBlockCollisionWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleEntityCollisionEvent e) return new VehicleEntityCollisionWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleMoveEvent e) return new VehicleMoveWrapper(e);
        if (event instanceof org.bukkit.event.vehicle.VehicleCreateEvent e) return new VehicleCreateWrapper(e);
        // Weather/world events.
        if (event instanceof org.bukkit.event.weather.WeatherChangeEvent e) return new WeatherChangeWrapper(e);
        if (event instanceof org.bukkit.event.weather.ThunderChangeEvent e) return new ThunderChangeWrapper(e);
        if (event instanceof org.bukkit.event.weather.LightningStrikeEvent e) return new LightningStrikeWrapper(e);
        if (event instanceof org.bukkit.event.world.WorldLoadEvent e) return new WorldLoadWrapper(e);
        if (event instanceof org.bukkit.event.world.WorldUnloadEvent e) return new WorldUnloadWrapper(e);
        if (event instanceof org.bukkit.event.world.WorldSaveEvent e) return new WorldSaveWrapper(e);
        if (event instanceof org.bukkit.event.world.ChunkLoadEvent e) return new ChunkLoadWrapper(e);
        if (event instanceof org.bukkit.event.world.ChunkUnloadEvent e) return new ChunkUnloadWrapper(e);
        if (event instanceof org.bukkit.event.world.SpawnChangeEvent e) return new SpawnChangeWrapper(e);
        if (event instanceof org.bukkit.event.world.PortalCreateEvent e) return new PortalCreateWrapper(e);
        if (event instanceof org.bukkit.event.world.StructureGrowEvent e) return new StructureGrowWrapper(e);
        // Server events + remaining "other" player events.
        if (event instanceof org.bukkit.event.server.ServerCommandEvent e) return new ServerCommandEventWrapper(e);
        if (event instanceof org.bukkit.event.server.PluginEnableEvent e) return new PluginEnableEventWrapper(e);
        if (event instanceof org.bukkit.event.server.PluginDisableEvent e) return new PluginDisableEventWrapper(e);
        if (event instanceof org.bukkit.event.server.TabCompleteEvent e) return new TabCompleteEventWrapper(e);
        if (event instanceof org.bukkit.event.server.BroadcastMessageEvent e) return new BroadcastMessageEventWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerAdvancementDoneEvent e) return new PlayerAdvancementDoneWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerStatisticIncrementEvent e) return new PlayerStatisticIncrementWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerRiptideEvent e) return new PlayerRiptideWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerVelocityEvent e) return new PlayerVelocityWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerShearEntityEvent e) return new PlayerShearEntityWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerBucketFillEvent e) return new PlayerBucketFillWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerBucketEmptyEvent e) return new PlayerBucketEmptyWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerEggThrowEvent e) return new PlayerEggThrowWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerFishEvent e) return new PlayerFishWrapper(e);
        if (event instanceof com.destroystokyo.paper.event.player.PlayerElytraBoostEvent e) return new PlayerElytraBoostWrapper(e);
        if (event instanceof org.bukkit.event.player.PlayerAnimationEvent e) return new PlayerAnimationWrapper(e);
        return new BukkitEventWrapper(event);
    }
}
