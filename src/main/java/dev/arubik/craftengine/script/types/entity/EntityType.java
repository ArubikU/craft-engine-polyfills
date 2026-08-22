package dev.arubik.craftengine.script.types.entity;

import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.world.LocationType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class EntityType {

    private EntityType() {}

    public static void register() {
        // ---- Base Entity — only what ALL entities share -------------------------
        PolyTypeRegistry.define("Entity")
            .property("type",     obj -> ScriptValue.of(BuiltInRegistries.ENTITY_TYPE.getKey(entity(obj).getType()).toString()))
            .property("pos",      obj -> { Vec3 p = entity(obj).position(); return VectorType.wrap(p.x, p.y, p.z); })
            .property("x",        obj -> ScriptValue.of(entity(obj).getX()))
            .property("y",        obj -> ScriptValue.of(entity(obj).getY()))
            .property("z",        obj -> ScriptValue.of(entity(obj).getZ()))
            .property("yaw",      obj -> ScriptValue.of(entity(obj).getYRot()))
            .property("pitch",    obj -> ScriptValue.of(entity(obj).getXRot()))
            .property("is_alive",   obj -> ScriptValue.of(entity(obj).isAlive()))
            .property("is_living",  obj -> ScriptValue.of(entity(obj) instanceof LivingEntity))
            .property("is_animal",  obj -> ScriptValue.of(entity(obj) instanceof Animal))
            .property("world", obj -> {
                if (entity(obj).level() instanceof net.minecraft.server.level.ServerLevel sl)
                    return dev.arubik.craftengine.script.types.world.WorldType.wrap(sl);
                return ScriptValue.NULL;
            })
            .property("is_on_ground", obj -> ScriptValue.of(entity(obj).onGround()))
            .property("is_in_water",  obj -> ScriptValue.of(entity(obj).isInWater()))
            .property("is_sneaking",  obj -> ScriptValue.of(entity(obj).isShiftKeyDown()))
            .property("is_sprinting", obj -> ScriptValue.of(entity(obj).isSprinting()))
            .property("is_silent",    obj -> ScriptValue.of(entity(obj).isSilent()))
            .property("is_invisible", obj -> ScriptValue.of(entity(obj).isInvisible()))
            .property("velocity",   obj -> { Vec3 v = entity(obj).getDeltaMovement(); return VectorType.wrap(v.x, v.y, v.z); })
            .property("velocity_x", obj -> ScriptValue.of(entity(obj).getDeltaMovement().x))
            .property("velocity_y", obj -> ScriptValue.of(entity(obj).getDeltaMovement().y))
            .property("velocity_z", obj -> ScriptValue.of(entity(obj).getDeltaMovement().z))
            .property("uuid",     obj -> ScriptValue.of(entity(obj).getUUID().toString()))
            .property("name",     obj -> ScriptValue.of(entity(obj).getName().getString()))
            .property("tick_age", obj -> ScriptValue.of(entity(obj).tickCount))
            .property("location", obj -> LocationType.wrapEntity(entity(obj)))
            .method("distance_to", (obj, args) -> {
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o && o.instance() instanceof Entity other)
                    return ScriptValue.of(entity(obj).distanceTo(other));
                return ScriptValue.of(0.0);
            })
            .method("set_velocity", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                entity(obj).setDeltaMovement(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum());
                entity(obj).hurtMarked = true;
                return ScriptValue.of(true);
            })
            .method("push", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                Entity e = entity(obj);
                ScriptValue v = args.get(0);
                if (v instanceof ScriptValue.Obj o && o.instance() instanceof org.joml.Vector3d vec) {
                    e.setDeltaMovement(e.getDeltaMovement().add(vec.x, vec.y, vec.z));
                    e.hurtMarked = true;
                    return ScriptValue.of(true);
                }
                if (args.size() >= 3) {
                    e.setDeltaMovement(e.getDeltaMovement().add(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum()));
                    e.hurtMarked = true;
                    return ScriptValue.of(true);
                }
                return ScriptValue.of(false);
            })
            .method("teleport", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(false);
                entity(obj).setPos(args.get(0).asNum(), args.get(1).asNum(), args.get(2).asNum());
                return ScriptValue.of(true);
            })
            // teleport_to(Location) — unlike teleport(x,y,z) above (setPos, SAME level only), this
            // goes through Bukkit's real Entity#teleport(Location), which properly moves an entity
            // ACROSS dimensions/worlds. Needed for anything building a cross-dimension link (a
            // teleporter network, ...) — setPos silently does nothing useful if the target Location
            // is in a different world.
            .method("teleport_to", (obj, args) -> {
                if (args.isEmpty() || !(args.get(0) instanceof ScriptValue.Obj o)
                        || !(o.instance() instanceof dev.arubik.craftengine.script.types.world.LocationType.LocationRef loc)
                        || loc.level() == null)
                    return ScriptValue.of(false);
                try {
                    org.bukkit.World world = loc.level().getWorld();
                    if (world == null) return ScriptValue.of(false);
                    org.bukkit.Location bukkitLoc = new org.bukkit.Location(world, loc.x(), loc.y(), loc.z());
                    return ScriptValue.of(entity(obj).getBukkitEntity().teleport(bukkitLoc));
                } catch (Throwable ignored) {
                    return ScriptValue.of(false);
                }
            })
            // Fall-distance control — the Bukkit-mirror side is stable across NMS internals, unlike
            // the raw `fallDistance` field's visibility/type, which has moved around between
            // versions. Used by e.g. a jetpack's thrust logic to keep the ensuing landing damage-free.
            .property("fall_distance", obj -> {
                try { return ScriptValue.of(entity(obj).getBukkitEntity().getFallDistance()); }
                catch (Throwable ignored) { return ScriptValue.of(0.0); }
            })
            .method("reset_fall_distance", (obj, args) -> {
                try { entity(obj).getBukkitEntity().setFallDistance(0f); return ScriptValue.of(true); }
                catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            // Generic potion-effect application — usable by any script (a jetpack softening its
            // own landing with slow-falling, a trap item poisoning whoever picks it up, etc).
            // add_potion_effect(name, duration_ticks, amplifier?)
            .method("add_potion_effect", (obj, args) -> {
                if (args.size() < 2 || !(entity(obj) instanceof LivingEntity living)) return ScriptValue.of(false);
                try {
                    var holder = mobEffectHolder(args.get(0).asStr());
                    if (holder == null) return ScriptValue.of(false);
                    int duration = (int) args.get(1).asNum();
                    int amplifier = args.size() >= 3 ? (int) args.get(2).asNum() : 0;
                    living.addEffect(new net.minecraft.world.effect.MobEffectInstance(holder, duration, amplifier));
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .method("remove_potion_effect", (obj, args) -> {
                if (args.isEmpty() || !(entity(obj) instanceof LivingEntity living)) return ScriptValue.of(false);
                try {
                    var holder = mobEffectHolder(args.get(0).asStr());
                    if (holder == null) return ScriptValue.of(false);
                    living.removeEffect(holder);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .method("has_potion_effect", (obj, args) -> {
                if (args.isEmpty() || !(entity(obj) instanceof LivingEntity living)) return ScriptValue.of(false);
                try {
                    var holder = mobEffectHolder(args.get(0).asStr());
                    return holder == null ? ScriptValue.of(false) : ScriptValue.of(living.hasEffect(holder));
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .method("kill", (obj, args) -> { entity(obj).kill(null); return ScriptValue.of(true); })
            .method("remove", (obj, args) -> {
                entity(obj).discard();
                return ScriptValue.of(true);
            })
            // --- Persistent per-entity flags (int/string) — the Entity/Player-side counterpart
            // of Machine.get_flag/set_flag/get_str_flag/set_str_flag, same naming convention, same
            // "0"/"" absent-default semantics. Backed by Bukkit's PersistentDataContainer: modern
            // (1.20.5+) NMS Entity no longer exposes its raw custom-data CompoundTag for live
            // mutation the way it used to (it's a private CustomData component now, no public
            // getter) — PDC is the actually-supported, version-stable way to attach arbitrary
            // per-entity data today, unlike Server-scope state (see ServerFlags), which has no
            // Bukkit-native equivalent at all and genuinely needs its own file.
            .method("get_flag", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(0);
                org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
                if (pdc == null) return ScriptValue.of(0);
                Integer v = pdc.get(flagKey(args.get(0).asStr()), org.bukkit.persistence.PersistentDataType.INTEGER);
                return ScriptValue.of(v != null ? v : 0);
            })
            .method("set_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
                if (pdc == null) return ScriptValue.of(false);
                pdc.set(flagKey(args.get(0).asStr()), org.bukkit.persistence.PersistentDataType.INTEGER,
                        (int) args.get(1).asNum());
                return ScriptValue.of(true);
            })
            .method("get_str_flag", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of("");
                org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
                if (pdc == null) return ScriptValue.of("");
                String v = pdc.get(flagKey(args.get(0).asStr()), org.bukkit.persistence.PersistentDataType.STRING);
                return ScriptValue.of(v != null ? v : "");
            })
            .method("set_str_flag", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
                if (pdc == null) return ScriptValue.of(false);
                pdc.set(flagKey(args.get(0).asStr()), org.bukkit.persistence.PersistentDataType.STRING,
                        args.get(1).asStr());
                return ScriptValue.of(true);
            });

        // ---- LivingEntity extends Entity ----------------------------------------
        PolyTypeRegistry.define("LivingEntity", "Entity")
            .property("health",       obj -> ScriptValue.of(living(obj).getHealth()))
            .property("max_health",   obj -> ScriptValue.of(living(obj).getMaxHealth()))
            .property("is_on_fire",   obj -> ScriptValue.of(living(obj).isOnFire()))
            .property("fire_ticks",   obj -> ScriptValue.of(living(obj).getRemainingFireTicks()))
            .property("frozen_ticks", obj -> ScriptValue.of(living(obj).getTicksFrozen()))
            .property("armor",        obj -> ScriptValue.of(living(obj).getArmorValue()))
            .property("main_hand",    obj -> ScriptValue.ofItem(living(obj).getMainHandItem()))
            .property("off_hand",     obj -> ScriptValue.ofItem(living(obj).getOffhandItem()))
            .property("is_dead",      obj -> ScriptValue.of(!living(obj).isAlive()))
            .property("last_damage",  obj -> ScriptValue.of(living(obj).getLastDamageSource() != null ?
                living(obj).getLastDamageSource().typeHolder().getRegisteredName() : ""))
            .method("fire", (obj, args) -> {
                int ticks = args.isEmpty() ? 60 : (int) args.get(0).asNum();
                living(obj).setRemainingFireTicks(Math.max(living(obj).getRemainingFireTicks(), ticks));
                return ScriptValue.of(true);
            })
            .method("freeze", (obj, args) -> {
                int ticks = args.isEmpty() ? 140 : (int) args.get(0).asNum();
                living(obj).setTicksFrozen(Math.max(living(obj).getTicksFrozen(), ticks));
                return ScriptValue.of(true);
            })
            .method("damage", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                float amount = (float) args.get(0).asNum();
                LivingEntity le = living(obj);
                if (le.level() instanceof ServerLevel sl)
                    return ScriptValue.of(le.hurtOrSimulate(sl.damageSources().generic(), amount));
                return ScriptValue.of(false);
            })
            .method("kill",       (obj, args) -> { living(obj).kill(null); return ScriptValue.of(true); })
            .method("set_health", (obj, args) -> { if (!args.isEmpty()) living(obj).setHealth((float) args.get(0).asNum()); return ScriptValue.of(true); })
            .method("give_item", (obj, args) -> {
                if (args.isEmpty()) return ScriptValue.of(false);
                net.minecraft.world.item.ItemStack stack = null;
                ScriptValue v = args.get(0);
                if (v instanceof ScriptValue.Item i) stack = i.stack().copy();
                if (stack == null || stack.isEmpty()) return ScriptValue.of(false);
                LivingEntity le = living(obj);
                if (le instanceof net.minecraft.world.entity.player.Player p) {
                    p.getInventory().add(stack);
                } else {
                    // Drop at entity position for non-players
                    if (le.level() instanceof ServerLevel sl) le.spawnAtLocation(sl, stack);
                }
                return ScriptValue.of(true);
            })
            .property("equipment", obj -> {
                LivingEntity le = living(obj);
                java.util.LinkedHashMap<String, ScriptValue> eq = new java.util.LinkedHashMap<>();
                eq.put("main_hand", ScriptValue.ofItem(le.getMainHandItem()));
                eq.put("off_hand",  ScriptValue.ofItem(le.getOffhandItem()));
                eq.put("head",  ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)));
                eq.put("chest", ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)));
                eq.put("legs",  ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)));
                eq.put("feet",  ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET)));
                return dev.arubik.craftengine.script.types.primitive.MapType.wrap(eq);
            })
            .method("set_equipment", (obj, args) -> {
                if (args.size() < 2) return ScriptValue.of(false);
                LivingEntity le = living(obj);
                String slotName = args.get(0).asStr();
                net.minecraft.world.item.ItemStack stack = args.get(1) instanceof ScriptValue.Item i ? i.stack() : net.minecraft.world.item.ItemStack.EMPTY;
                net.minecraft.world.entity.EquipmentSlot slot = switch (slotName.toLowerCase()) {
                    case "main_hand" -> net.minecraft.world.entity.EquipmentSlot.MAINHAND;
                    case "off_hand"  -> net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                    case "head"  -> net.minecraft.world.entity.EquipmentSlot.HEAD;
                    case "chest" -> net.minecraft.world.entity.EquipmentSlot.CHEST;
                    case "legs"  -> net.minecraft.world.entity.EquipmentSlot.LEGS;
                    case "feet"  -> net.minecraft.world.entity.EquipmentSlot.FEET;
                    default -> null;
                };
                if (slot == null) return ScriptValue.of(false);
                le.setItemSlot(slot, stack);
                return ScriptValue.of(true);
            });

        // ---- Mob extends LivingEntity -------------------------------------------
        PolyTypeRegistry.define("Mob", "LivingEntity")
            .property("can_pickup_loot", obj -> ScriptValue.of(mob(obj).canPickUpLoot()))
            .property("is_aggressive",   obj -> {
                try { return ScriptValue.of(mob(obj).isAggressive()); } catch (Throwable ignored) { return ScriptValue.of(false); }
            })
            .property("target_uuid", obj -> {
                LivingEntity tgt = mob(obj).getTarget();
                return tgt != null ? ScriptValue.of(tgt.getUUID().toString()) : ScriptValue.NULL;
            })
            .property("has_target", obj -> ScriptValue.of(mob(obj).getTarget() != null));

        // ---- Animal extends Mob -------------------------------------------------
        PolyTypeRegistry.define("Animal", "Mob")
            .property("is_animal",    obj -> ScriptValue.of(true))
            .property("age",          obj -> ScriptValue.of(animal(obj).getAge()))
            .property("in_love_time", obj -> ScriptValue.of(animal(obj).getInLoveTime()))
            .property("is_baby",      obj -> ScriptValue.of(animal(obj).isBaby()))
            .property("is_in_love",   obj -> ScriptValue.of(animal(obj).isInLove()));

        // ---- ItemEntity extends Entity ------------------------------------------
        PolyTypeRegistry.define("ItemEntity", "Entity")
            .property("item",         obj -> ScriptValue.ofItem(itemEntity(obj).getItem()))
            .property("pickup_delay", obj -> ScriptValue.of(itemEntity(obj).pickupDelay))
            .method("set_item", (obj, args) -> {
                if (!args.isEmpty() && args.get(0) instanceof ScriptValue.Item i && i.stack() != null)
                    itemEntity(obj).setItem(i.stack());
                return ScriptValue.of(true);
            });

        // ---- ExperienceOrb extends Entity ----------------------------------------
        PolyTypeRegistry.define("ExperienceOrb", "Entity")
            .property("value",    obj -> ScriptValue.of(expOrb(obj).getValue()))
            .property("xp_value", obj -> ScriptValue.of(expOrb(obj).getValue()));

        // ---- Projectile extends Entity ------------------------------------------
        PolyTypeRegistry.define("Projectile", "Entity")
            .property("owner_uuid", obj -> {
                try {
                    var owner = ((Projectile) obj).getOwner();
                    return owner != null ? ScriptValue.of(owner.getUUID().toString()) : ScriptValue.NULL;
                } catch (Throwable ignored) { return ScriptValue.NULL; }
            });

        // ---- FallingBlock extends Entity ----------------------------------------
        PolyTypeRegistry.define("FallingBlock", "Entity")
            .property("block_id", obj -> {
                try {
                    return ScriptValue.of(BuiltInRegistries.BLOCK.getKey(((FallingBlockEntity) obj).getBlockState().getBlock()).toString());
                } catch (Throwable ignored) { return ScriptValue.of("minecraft:air"); }
            })
            .property("time", obj -> ScriptValue.of(((FallingBlockEntity) obj).time));
    }

    /** Wrap to the most specific registered type. */
    public static ScriptValue wrap(Entity entity) {
        if (entity == null) return ScriptValue.NULL;
        if (entity instanceof net.minecraft.server.level.ServerPlayer) return ScriptValue.ofObj("Player", entity);
        if (entity instanceof Animal)                                   return ScriptValue.ofObj("Animal", entity);
        if (entity instanceof Mob)                                      return ScriptValue.ofObj("Mob", entity);
        if (entity instanceof LivingEntity)                             return ScriptValue.ofObj("LivingEntity", entity);
        if (entity instanceof ItemEntity)                               return ScriptValue.ofObj("ItemEntity", entity);
        if (entity instanceof ExperienceOrb)                            return ScriptValue.ofObj("ExperienceOrb", entity);
        if (entity instanceof Projectile)                               return ScriptValue.ofObj("Projectile", entity);
        if (entity instanceof FallingBlockEntity)                       return ScriptValue.ofObj("FallingBlock", entity);
        return ScriptValue.ofObj("Entity", entity);
    }

    public static ScriptValue wrapList(List<? extends Entity> entities) {
        if (entities == null || entities.isEmpty()) return new ScriptValue.Array(List.of());
        List<ScriptValue> list = new ArrayList<>(entities.size());
        for (Entity e : entities) list.add(wrap(e));
        return new ScriptValue.Array(list);
    }

    /** The Bukkit PersistentDataContainer backing get_flag/set_flag/get_str_flag/set_str_flag —
     * null only if the entity has already been discarded/has no Bukkit mirror. */
    private static org.bukkit.persistence.PersistentDataContainer entityPdc(Object obj) {
        try {
            org.bukkit.entity.Entity bukkit = entity(obj).getBukkitEntity();
            return bukkit != null ? bukkit.getPersistentDataContainer() : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static org.bukkit.NamespacedKey flagKey(String name) {
        return new org.bukkit.NamespacedKey(dev.arubik.craftengine.CraftEnginePolyfills.instance(), "flag_" + name);
    }

    private static Entity        entity(Object obj)     { return (Entity) obj; }
    private static LivingEntity  living(Object obj)     { return (LivingEntity) obj; }
    private static Mob           mob(Object obj)        { return (Mob) obj; }
    private static Animal        animal(Object obj)     { return (Animal) obj; }
    private static ItemEntity    itemEntity(Object obj) { return (ItemEntity) obj; }
    private static ExperienceOrb expOrb(Object obj)     { return (ExperienceOrb) obj; }

    /** Resolves a bare or namespaced potion-effect id ("slow_falling" / "minecraft:slow_falling")
     *  to its registry {@link net.minecraft.core.Holder}, or null if unknown. */
    private static net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> mobEffectHolder(String name) {
        net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.parse(
                name.contains(":") ? name : "minecraft:" + name);
        var mobEffect = BuiltInRegistries.MOB_EFFECT.getValue(id);
        return mobEffect == null ? null : BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
    }
}
