package dev.arubik.craftengine.script.types.entity;

import dev.arubik.craftengine.script.types.primitive.VectorType;
import dev.arubik.craftengine.script.types.world.LocationType;
import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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

    /** Storage-key prefix for every TypedKeyBridge-backed accessor below — mirrors Machine/Item's
     *  identical "tkey_" prefix (just over a PDC namespaced key instead of an NBT tag) so a typed
     *  value bridged between an entity and either of those round-trips under the same key either
     *  side reads. */
    private static final String TYPED_PREFIX = "tkey_";

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
            // Left untyped: original requires args.size() == 1 EXACTLY (an extra arg falls through
            // to the 0.0 default) — methodTypedN's onMissingArgs only guards a MINIMUM arg count,
            // so it can't reproduce the "too many args" branch of this exact check.
            .method("distance_to", (obj, args) -> {
                if (args.size() == 1 && args.get(0) instanceof ScriptValue.Obj o && o.instance() instanceof Entity other)
                    return ScriptValue.of(entity(obj).distanceTo(other));
                return ScriptValue.of(0.0);
            })
            .methodTyped3("set_velocity", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Entity e, Double x, Double y, Double z) -> {
                    e.setDeltaMovement(x, y, z);
                    e.hurtMarked = true;
                    return true;
                })
            // Left untyped: dynamically branches between a Vector-object first arg and a 3-double
            // form — no single methodTypedN arity/codec combination represents both call shapes.
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
            .methodTyped3("teleport", TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (Entity e, Double x, Double y, Double z) -> {
                    e.setPos(x, y, z);
                    return true;
                })
            // teleport_to(Location) — unlike teleport(x,y,z) above (setPos, SAME level only), this
            // goes through Bukkit's real Entity#teleport(Location), which properly moves an entity
            // ACROSS dimensions/worlds. Needed for anything building a cross-dimension link (a
            // teleporter network, ...) — setPos silently does nothing useful if the target Location
            // is in a different world.
            // Arg stays TypeCodecs.RAW: it's a dynamic ScriptValue.Obj/instanceof check, not a
            // fixed native type — same reasoning as MachineType's get_typed/set_typed value slot.
            .methodTyped1("teleport_to", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Entity e, ScriptValue arg0) -> {
                    if (!(arg0 instanceof ScriptValue.Obj o)
                            || !(o.instance() instanceof dev.arubik.craftengine.script.types.world.LocationType.LocationRef loc)
                            || loc.level() == null)
                        return false;
                    try {
                        org.bukkit.World world = loc.level().getWorld();
                        if (world == null) return false;
                        org.bukkit.Location bukkitLoc = new org.bukkit.Location(world, loc.x(), loc.y(), loc.z());
                        return e.getBukkitEntity().teleport(bukkitLoc);
                    } catch (Throwable ignored) {
                        return false;
                    }
                })
            // Fall-distance control — the Bukkit-mirror side is stable across NMS internals, unlike
            // the raw `fallDistance` field's visibility/type, which has moved around between
            // versions. Used by e.g. a jetpack's thrust logic to keep the ensuing landing damage-free.
            .property("fall_distance", obj -> {
                try { return ScriptValue.of(entity(obj).getBukkitEntity().getFallDistance()); }
                catch (Throwable ignored) { return ScriptValue.of(0.0); }
            })
            .methodTyped0("reset_fall_distance", TypeCodecs.BOOL,
                (Entity e) -> {
                    try { e.getBukkitEntity().setFallDistance(0f); return true; }
                    catch (Throwable ignored) { return false; }
                })
            // Generic potion-effect application — usable by any script (a jetpack softening its
            // own landing with slow-falling, a trap item poisoning whoever picks it up, etc).
            // add_potion_effect(name, duration_ticks, amplifier?)
            // Left untyped: MIXED arity — args 0-1 are REQUIRED (fewer than 2 returns false without
            // touching the entity) while arg 2 (amplifier) is optional with a default of 0. Neither
            // typed form covers that: methodTyped3's onMissingArgs short-circuits the whole call
            // below size 3 (wrongly rejecting the valid 2-arg form), and methodTypedOpt3 makes ALL
            // three optional, so a 0- or 1-arg call would apply a real effect the original refused.
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
            .methodTyped1("remove_potion_effect", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Entity e, String effectName) -> {
                    if (!(e instanceof LivingEntity living)) return false;
                    try {
                        var holder = mobEffectHolder(effectName);
                        if (holder == null) return false;
                        living.removeEffect(holder);
                        return true;
                    } catch (Throwable ignored) { return false; }
                })
            .methodTyped1("has_potion_effect", TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Entity e, String effectName) -> {
                    if (!(e instanceof LivingEntity living)) return false;
                    try {
                        var holder = mobEffectHolder(effectName);
                        return holder != null && living.hasEffect(holder);
                    } catch (Throwable ignored) { return false; }
                })
            .methodTyped0("kill", TypeCodecs.BOOL, (Entity e) -> { e.kill(null); return true; })
            .methodTyped0("remove", TypeCodecs.BOOL, (Entity e) -> {
                e.discard();
                return true;
            })
            // --- Generic TypedKey storage (see dev.arubik.craftengine.script.TypedKeyBridge) ----
            // Entity/Player-side counterpart of Machine.get_typed/set_typed/has_typed — backed by
            // Bukkit's PersistentDataContainer instead of a raw NBT CompoundTag: modern (1.20.5+)
            // NMS Entity no longer exposes its custom-data CompoundTag for live mutation (it's a
            // private CustomData component now, no public getter) — PDC is the actually-supported,
            // version-stable way to attach arbitrary per-entity data today.
            .methodTyped2("get_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, ScriptValue.NULL,
                (Entity e, String key, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return ScriptValue.NULL;
                    return readTyped(e, TYPED_PREFIX + key, codec);
                })
            .methodTyped3("set_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (Entity e, String key, String typeName, ScriptValue value) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return false;
                    return writeTyped(e, TYPED_PREFIX + key, codec, value);
                })
            .methodTyped2("has_typed", TypeCodecs.STRING, TypeCodecs.STRING, TypeCodecs.BOOL, false,
                (Entity e, String key, String typeName) -> {
                    dev.arubik.craftengine.script.TypedKeyBridge.Codec codec = dev.arubik.craftengine.script.TypedKeyBridge.resolve(typeName);
                    if (codec == null) return false;
                    return hasTyped(e, TYPED_PREFIX + key, codec);
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
            // fire/freeze take an OPTIONAL tick count substituted with a default (60 / 140) when
            // omitted, after which execution continues unconditionally and the side effect still
            // happens — the methodTypedOptN shape, NOT methodTypedN's onMissingArgs short-circuit
            // (which would silently drop the mutation on the no-arg call).
            .methodTypedOpt1("fire", TypeCodecs.DOUBLE, 60.0, TypeCodecs.BOOL,
                (LivingEntity le, Double ticksArg) -> {
                    int ticks = (int) (double) ticksArg;
                    le.setRemainingFireTicks(Math.max(le.getRemainingFireTicks(), ticks));
                    return true;
                })
            .methodTypedOpt1("freeze", TypeCodecs.DOUBLE, 140.0, TypeCodecs.BOOL,
                (LivingEntity le, Double ticksArg) -> {
                    int ticks = (int) (double) ticksArg;
                    le.setTicksFrozen(Math.max(le.getTicksFrozen(), ticks));
                    return true;
                })
            .methodTyped1("damage", TypeCodecs.DOUBLE, TypeCodecs.BOOL, false,
                (LivingEntity le, Double amountArg) -> {
                    float amount = amountArg.floatValue();
                    if (le.level() instanceof ServerLevel sl)
                        return le.hurtOrSimulate(sl.damageSources().generic(), amount);
                    return false;
                })
            .methodTyped0("kill", TypeCodecs.BOOL, (LivingEntity le) -> { le.kill(null); return true; })
            // set_health's onMissingArgs=true matches the original's "if empty, no-op, still return
            // true" fallthrough exactly — no health mutation happens in either the missing-arg path
            // above (methodTyped1 never invokes the handler) or the original's skipped `if` body.
            .methodTyped1("set_health", TypeCodecs.DOUBLE, TypeCodecs.BOOL, true,
                (LivingEntity le, Double h) -> { le.setHealth(h.floatValue()); return true; })
            .methodTyped1("give_item", TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (LivingEntity le, ScriptValue v) -> {
                    net.minecraft.world.item.ItemStack stack = null;
                    if (v instanceof ScriptValue.Item i) stack = i.stack().copy();
                    if (stack == null || stack.isEmpty()) return false;
                    if (le instanceof net.minecraft.world.entity.player.Player p) {
                        p.getInventory().add(stack);
                    } else {
                        // Drop at entity position for non-players
                        if (le.level() instanceof ServerLevel sl) le.spawnAtLocation(sl, stack);
                    }
                    return true;
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
            .methodTyped2("set_equipment", TypeCodecs.STRING, TypeCodecs.RAW, TypeCodecs.BOOL, false,
                (LivingEntity le, String slotName, ScriptValue itemArg) -> {
                    net.minecraft.world.item.ItemStack stack = itemArg instanceof ScriptValue.Item i ? i.stack() : net.minecraft.world.item.ItemStack.EMPTY;
                    net.minecraft.world.entity.EquipmentSlot slot = switch (slotName.toLowerCase()) {
                        case "main_hand" -> net.minecraft.world.entity.EquipmentSlot.MAINHAND;
                        case "off_hand"  -> net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                        case "head"  -> net.minecraft.world.entity.EquipmentSlot.HEAD;
                        case "chest" -> net.minecraft.world.entity.EquipmentSlot.CHEST;
                        case "legs"  -> net.minecraft.world.entity.EquipmentSlot.LEGS;
                        case "feet"  -> net.minecraft.world.entity.EquipmentSlot.FEET;
                        default -> null;
                    };
                    if (slot == null) return false;
                    le.setItemSlot(slot, stack);
                    return true;
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
            // onMissingArgs=true matches the original's "empty args -> no-op, still return true".
            .methodTyped1("set_item", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (ItemEntity ie, ScriptValue v) -> {
                    if (v instanceof ScriptValue.Item i && i.stack() != null) ie.setItem(i.stack());
                    return true;
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

    /** The Bukkit PersistentDataContainer backing every TypedKeyBridge-based accessor below —
     * null only if the entity has already been discarded/has no Bukkit mirror. */
    private static org.bukkit.persistence.PersistentDataContainer entityPdc(Object obj) {
        try {
            org.bukkit.entity.Entity bukkit = entity(obj).getBukkitEntity();
            return bukkit != null ? bukkit.getPersistentDataContainer() : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** {@code storageKey} is the FULL prefixed key ("tkey_foo", ...) — callers add their own
     *  prefix, this just turns it into a real NamespacedKey. */
    private static org.bukkit.NamespacedKey pdcKey(String storageKey) {
        return new org.bukkit.NamespacedKey(dev.arubik.craftengine.CraftEnginePolyfills.instance(), storageKey);
    }

    /** Maps a {@link dev.arubik.craftengine.util.NbtType} to the matching Paper
     *  {@link org.bukkit.persistence.PersistentDataType} — every TypedKeyBridge codec's storage()
     *  shape has a 1:1 PDC counterpart, so this is the only place that needs to know both. */
    private static void setPdc(org.bukkit.persistence.PersistentDataContainer pdc, org.bukkit.NamespacedKey key,
            dev.arubik.craftengine.util.NbtType type, Object value) {
        switch (type) {
            case BYTE -> pdc.set(key, org.bukkit.persistence.PersistentDataType.BYTE, (Byte) value);
            case SHORT -> pdc.set(key, org.bukkit.persistence.PersistentDataType.SHORT, (Short) value);
            case INTEGER -> pdc.set(key, org.bukkit.persistence.PersistentDataType.INTEGER, (Integer) value);
            case LONG -> pdc.set(key, org.bukkit.persistence.PersistentDataType.LONG, (Long) value);
            case FLOAT -> pdc.set(key, org.bukkit.persistence.PersistentDataType.FLOAT, (Float) value);
            case DOUBLE -> pdc.set(key, org.bukkit.persistence.PersistentDataType.DOUBLE, (Double) value);
            case STRING -> pdc.set(key, org.bukkit.persistence.PersistentDataType.STRING, (String) value);
            case BOOLEAN -> pdc.set(key, org.bukkit.persistence.PersistentDataType.BOOLEAN, (Boolean) value);
            case BYTE_ARRAY -> pdc.set(key, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY, (byte[]) value);
            case INTEGER_ARRAY -> pdc.set(key, org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY, (int[]) value);
            case LONG_ARRAY -> pdc.set(key, org.bukkit.persistence.PersistentDataType.LONG_ARRAY, (long[]) value);
        }
    }

    private static Object getPdc(org.bukkit.persistence.PersistentDataContainer pdc, org.bukkit.NamespacedKey key,
            dev.arubik.craftengine.util.NbtType type) {
        return switch (type) {
            case BYTE -> pdc.get(key, org.bukkit.persistence.PersistentDataType.BYTE);
            case SHORT -> pdc.get(key, org.bukkit.persistence.PersistentDataType.SHORT);
            case INTEGER -> pdc.get(key, org.bukkit.persistence.PersistentDataType.INTEGER);
            case LONG -> pdc.get(key, org.bukkit.persistence.PersistentDataType.LONG);
            case FLOAT -> pdc.get(key, org.bukkit.persistence.PersistentDataType.FLOAT);
            case DOUBLE -> pdc.get(key, org.bukkit.persistence.PersistentDataType.DOUBLE);
            case STRING -> pdc.get(key, org.bukkit.persistence.PersistentDataType.STRING);
            case BOOLEAN -> pdc.get(key, org.bukkit.persistence.PersistentDataType.BOOLEAN);
            case BYTE_ARRAY -> pdc.get(key, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY);
            case INTEGER_ARRAY -> pdc.get(key, org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY);
            case LONG_ARRAY -> pdc.get(key, org.bukkit.persistence.PersistentDataType.LONG_ARRAY);
        };
    }

    private static boolean hasPdc(org.bukkit.persistence.PersistentDataContainer pdc, org.bukkit.NamespacedKey key,
            dev.arubik.craftengine.util.NbtType type) {
        return switch (type) {
            case BYTE -> pdc.has(key, org.bukkit.persistence.PersistentDataType.BYTE);
            case SHORT -> pdc.has(key, org.bukkit.persistence.PersistentDataType.SHORT);
            case INTEGER -> pdc.has(key, org.bukkit.persistence.PersistentDataType.INTEGER);
            case LONG -> pdc.has(key, org.bukkit.persistence.PersistentDataType.LONG);
            case FLOAT -> pdc.has(key, org.bukkit.persistence.PersistentDataType.FLOAT);
            case DOUBLE -> pdc.has(key, org.bukkit.persistence.PersistentDataType.DOUBLE);
            case STRING -> pdc.has(key, org.bukkit.persistence.PersistentDataType.STRING);
            case BOOLEAN -> pdc.has(key, org.bukkit.persistence.PersistentDataType.BOOLEAN);
            case BYTE_ARRAY -> pdc.has(key, org.bukkit.persistence.PersistentDataType.BYTE_ARRAY);
            case INTEGER_ARRAY -> pdc.has(key, org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY);
            case LONG_ARRAY -> pdc.has(key, org.bukkit.persistence.PersistentDataType.LONG_ARRAY);
        };
    }

    /** Shared read path for every TypedKeyBridge-backed accessor. */
    private static ScriptValue readTyped(Object obj, String storageKey, dev.arubik.craftengine.script.TypedKeyBridge.Codec codec) {
        org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
        if (pdc == null) return codec.fromStorage(null);
        try {
            return codec.fromStorage(getPdc(pdc, pdcKey(storageKey), codec.storage()));
        } catch (Throwable ignored) { return codec.fromStorage(null); }
    }

    private static boolean hasTyped(Object obj, String storageKey, dev.arubik.craftengine.script.TypedKeyBridge.Codec codec) {
        org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
        if (pdc == null) return false;
        try { return hasPdc(pdc, pdcKey(storageKey), codec.storage()); } catch (Throwable ignored) { return false; }
    }

    /** Shared write path. */
    private static boolean writeTyped(Object obj, String storageKey, dev.arubik.craftengine.script.TypedKeyBridge.Codec codec, ScriptValue value) {
        org.bukkit.persistence.PersistentDataContainer pdc = entityPdc(obj);
        if (pdc == null) return false;
        try {
            setPdc(pdc, pdcKey(storageKey), codec.storage(), codec.toStorage(value));
            return true;
        } catch (Throwable ignored) { return false; }
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
