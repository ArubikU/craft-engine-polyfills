package dev.arubik.craftengine.script.types.entity;

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
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public final class EntityType {

    private EntityType() {}

    /** Storage-key prefix for every TypedKeyBridge-backed accessor below — mirrors Machine/Item's
     *  identical "tkey_" prefix (just over a PDC namespaced key instead of an NBT tag) so a typed
     *  value bridged between an entity and either of those round-trips under the same key either
     *  side reads. */
    private static final String TYPED_PREFIX = "tkey_";

    /** The PolyType names below are FIXED — unlike {@link #wrap}, which picks a different name per
     *  entity subclass, {@code VectorType.wrap}/{@code LocationType.wrap}/{@code MapType.wrap} each
     *  box under one constant name, so a PolyCodec reproduces them exactly. */
    private static final dev.arubik.craftengine.script.PolyType.TypeCodec<Vector3d> VECTOR_CODEC =
            TypeCodecs.polyType("Vector", Vector3d.class);
    private static final dev.arubik.craftengine.script.PolyType.TypeCodec<LocationType.LocationRef> LOCATION_CODEC =
            TypeCodecs.polyType("Location", LocationType.LocationRef.class);
    /** Erasure gives no {@code Map<String, ScriptValue>.class}; the unavoidable cast lives here once
     *  (same pattern as SQLDriverType.MAP_ELEMENT). */
    @SuppressWarnings("unchecked")
    private static final dev.arubik.craftengine.script.PolyType.TypeCodec<java.util.Map<String, ScriptValue>> MAP_CODEC =
            TypeCodecs.polyType("Map", (Class<java.util.Map<String, ScriptValue>>) (Class<?>) java.util.Map.class);

    public static void register() {
        // ---- Base Entity — only what ALL entities share -------------------------
        PolyTypeRegistry.define("Entity")
            .propertyTyped("type", TypeCodecs.STRING,
                (Entity e) -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString())
            // VECTOR_CODEC / LOCATION_CODEC re-box under exactly the name VectorType.wrap /
            // LocationType.wrap already used ("Vector" / "Location"), so the produced ScriptValue is
            // byte-for-byte the old one — see those constants below.
            .propertyTyped("pos", VECTOR_CODEC,
                (Entity e) -> { Vec3 p = e.position(); return new Vector3d(p.x, p.y, p.z); })
            .propertyTyped("x", TypeCodecs.DOUBLE, (Entity e) -> e.getX())
            .propertyTyped("y", TypeCodecs.DOUBLE, (Entity e) -> e.getY())
            .propertyTyped("z", TypeCodecs.DOUBLE, (Entity e) -> e.getZ())
            .propertyTyped("yaw", TypeCodecs.DOUBLE, (Entity e) -> (double) e.getYRot())
            .propertyTyped("pitch", TypeCodecs.DOUBLE, (Entity e) -> (double) e.getXRot())
            .propertyTyped("is_alive", TypeCodecs.BOOL, (Entity e) -> e.isAlive())
            .propertyTyped("is_living", TypeCodecs.BOOL, (Entity e) -> e instanceof LivingEntity)
            .propertyTyped("is_animal", TypeCodecs.BOOL, (Entity e) -> e instanceof Animal)
            // WorldType.wrap(level) is ofObj("World", level) with a null guard; PolyCodec.encode is
            // the same null guard, so returning null here reproduces the old ScriptValue.NULL branch.
            .propertyTyped("world", TypeCodecs.polyType("World", ServerLevel.class),
                (Entity e) -> e.level() instanceof ServerLevel sl ? sl : null)
            .propertyTyped("is_on_ground", TypeCodecs.BOOL, (Entity e) -> e.onGround())
            .propertyTyped("is_in_water", TypeCodecs.BOOL, (Entity e) -> e.isInWater())
            .propertyTyped("is_sneaking", TypeCodecs.BOOL, (Entity e) -> e.isShiftKeyDown())
            .propertyTyped("is_sprinting", TypeCodecs.BOOL, (Entity e) -> e.isSprinting())
            .propertyTyped("is_silent", TypeCodecs.BOOL, (Entity e) -> e.isSilent())
            .propertyTyped("is_invisible", TypeCodecs.BOOL, (Entity e) -> e.isInvisible())
            .propertyTyped("velocity", VECTOR_CODEC,
                (Entity e) -> { Vec3 v = e.getDeltaMovement(); return new Vector3d(v.x, v.y, v.z); })
            .propertyTyped("velocity_x", TypeCodecs.DOUBLE, (Entity e) -> e.getDeltaMovement().x)
            .propertyTyped("velocity_y", TypeCodecs.DOUBLE, (Entity e) -> e.getDeltaMovement().y)
            .propertyTyped("velocity_z", TypeCodecs.DOUBLE, (Entity e) -> e.getDeltaMovement().z)
            .propertyTyped("uuid", TypeCodecs.STRING, (Entity e) -> e.getUUID().toString())
            .propertyTyped("name", TypeCodecs.STRING, (Entity e) -> e.getName().getString())
            .propertyTyped("tick_age", TypeCodecs.DOUBLE, (Entity e) -> (double) e.tickCount)
            .propertyTyped("location", LOCATION_CODEC, (Entity e) -> locationRef(e))
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
            // Instance stays `Object` with the cast INSIDE the try: declaring (Entity e) would move
            // the cast outside it, so a wrong-typed instance would throw instead of falling back to 0.
            .propertyTyped("fall_distance", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) entity(obj).getBukkitEntity().getFallDistance(); }
                catch (Throwable ignored) { return 0.0; }
            })
            .methodTyped0("reset_fall_distance", TypeCodecs.BOOL,
                (Entity e) -> {
                    try { e.getBukkitEntity().setFallDistance(0f); return true; }
                    catch (Throwable ignored) { return false; }
                })
            // Generic potion-effect application — usable by any script (a jetpack softening its
            // own landing with slow-falling, a trap item poisoning whoever picks it up, etc).
            // add_potion_effect(name, duration_ticks, amplifier?)
            // Typed with a null sentinel on the LAST required slot (duration): no codec decodes a
            // PRESENT argument to Java null (asNum() is primitive-backed, boxed only on return), and
            // arguments are positional, so `durationArg == null` is exactly the old `args.size() < 2`
            // early return — checked before the entity is touched. amplifier keeps its 0 default.
            .methodTypedOpt3("add_potion_effect", TypeCodecs.STRING, null, TypeCodecs.DOUBLE, null,
                TypeCodecs.DOUBLE, 0.0, TypeCodecs.BOOL,
                (Entity e, String effectName, Double durationArg, Double amplifierArg) -> {
                if (durationArg == null || !(e instanceof LivingEntity living)) return false;
                try {
                    var holder = mobEffectHolder(effectName);
                    if (holder == null) return false;
                    int duration = (int) (double) durationArg;
                    int amplifier = (int) (double) amplifierArg;
                    living.addEffect(new net.minecraft.world.effect.MobEffectInstance(holder, duration, amplifier));
                    return true;
                } catch (Throwable ignored) { return false; }
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
            .propertyTyped("health", TypeCodecs.DOUBLE, (LivingEntity le) -> (double) le.getHealth())
            .propertyTyped("max_health", TypeCodecs.DOUBLE, (LivingEntity le) -> (double) le.getMaxHealth())
            .propertyTyped("is_on_fire", TypeCodecs.BOOL, (LivingEntity le) -> le.isOnFire())
            .propertyTyped("fire_ticks", TypeCodecs.DOUBLE, (LivingEntity le) -> (double) le.getRemainingFireTicks())
            .propertyTyped("frozen_ticks", TypeCodecs.DOUBLE, (LivingEntity le) -> (double) le.getTicksFrozen())
            .propertyTyped("armor", TypeCodecs.DOUBLE, (LivingEntity le) -> (double) le.getArmorValue())
            // main_hand/off_hand stay untyped: ScriptValue.ofItem produces the distinct ScriptValue
            // .Item variant, not an Obj — no codec here encodes to it, and switching to one would
            // break every `instanceof ScriptValue.Item` consumer downstream.
            .property("main_hand",    obj -> ScriptValue.ofItem(living(obj).getMainHandItem()))
            .property("off_hand",     obj -> ScriptValue.ofItem(living(obj).getOffhandItem()))
            .propertyTyped("is_dead", TypeCodecs.BOOL, (LivingEntity le) -> !le.isAlive())
            .propertyTyped("last_damage", TypeCodecs.STRING, (LivingEntity le) ->
                le.getLastDamageSource() != null ? le.getLastDamageSource().typeHolder().getRegisteredName() : "")
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
            // The MAP's own encoding is typed ("Map" is fixed); its VALUES stay ScriptValue.Item —
            // MapType holds a Map<String, ScriptValue>, so the Item variant is preserved as-is.
            .propertyTyped("equipment", MAP_CODEC, (LivingEntity le) -> {
                java.util.LinkedHashMap<String, ScriptValue> eq = new java.util.LinkedHashMap<>();
                eq.put("main_hand", ScriptValue.ofItem(le.getMainHandItem()));
                eq.put("off_hand",  ScriptValue.ofItem(le.getOffhandItem()));
                eq.put("head",  ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)));
                eq.put("chest", ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST)));
                eq.put("legs",  ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS)));
                eq.put("feet",  ScriptValue.ofItem(le.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET)));
                return eq;
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
            .propertyTyped("can_pickup_loot", TypeCodecs.BOOL, (Mob m) -> m.canPickUpLoot())
            // Object instance + cast inside the try — see fall_distance above.
            .propertyTyped("is_aggressive", TypeCodecs.BOOL, (Object obj) -> {
                try { return mob(obj).isAggressive(); } catch (Throwable ignored) { return false; }
            })
            // STRING encodes a Java null back to ScriptValue.NULL (ScriptValue.of(String) null-guards),
            // reproducing the old explicit NULL branch exactly.
            .propertyTyped("target_uuid", TypeCodecs.STRING, (Mob m) -> {
                LivingEntity tgt = m.getTarget();
                return tgt != null ? tgt.getUUID().toString() : null;
            })
            .propertyTyped("has_target", TypeCodecs.BOOL, (Mob m) -> m.getTarget() != null);

        // ---- Animal extends Mob -------------------------------------------------
        PolyTypeRegistry.define("Animal", "Mob")
            // Constant true — the instance is never touched, so it stays Object (no cast at all).
            .propertyTyped("is_animal", TypeCodecs.BOOL, (Object obj) -> true)
            .propertyTyped("age", TypeCodecs.DOUBLE, (Animal a) -> (double) a.getAge())
            .propertyTyped("in_love_time", TypeCodecs.DOUBLE, (Animal a) -> (double) a.getInLoveTime())
            .propertyTyped("is_baby", TypeCodecs.BOOL, (Animal a) -> a.isBaby())
            .propertyTyped("is_in_love", TypeCodecs.BOOL, (Animal a) -> a.isInLove());

        // ---- ItemEntity extends Entity ------------------------------------------
        PolyTypeRegistry.define("ItemEntity", "Entity")
            // `item` stays untyped — ScriptValue.Item variant, see main_hand above.
            .property("item",         obj -> ScriptValue.ofItem(itemEntity(obj).getItem()))
            .propertyTyped("pickup_delay", TypeCodecs.DOUBLE, (ItemEntity ie) -> (double) ie.pickupDelay)
            // onMissingArgs=true matches the original's "empty args -> no-op, still return true".
            .methodTyped1("set_item", TypeCodecs.RAW, TypeCodecs.BOOL, true,
                (ItemEntity ie, ScriptValue v) -> {
                    if (v instanceof ScriptValue.Item i && i.stack() != null) ie.setItem(i.stack());
                    return true;
                });

        // ---- ExperienceOrb extends Entity ----------------------------------------
        PolyTypeRegistry.define("ExperienceOrb", "Entity")
            .propertyTyped("value", TypeCodecs.DOUBLE, (ExperienceOrb o) -> (double) o.getValue())
            .propertyTyped("xp_value", TypeCodecs.DOUBLE, (ExperienceOrb o) -> (double) o.getValue());

        // ---- Projectile extends Entity ------------------------------------------
        PolyTypeRegistry.define("Projectile", "Entity")
            // Object instance + cast inside the try (see fall_distance); a null string encodes back
            // to ScriptValue.NULL, matching both old NULL branches.
            .propertyTyped("owner_uuid", TypeCodecs.STRING, (Object obj) -> {
                try {
                    var owner = ((Projectile) obj).getOwner();
                    return owner != null ? owner.getUUID().toString() : null;
                } catch (Throwable ignored) { return null; }
            });

        // ---- FallingBlock extends Entity ----------------------------------------
        PolyTypeRegistry.define("FallingBlock", "Entity")
            .propertyTyped("block_id", TypeCodecs.STRING, (Object obj) -> {
                try {
                    return BuiltInRegistries.BLOCK.getKey(((FallingBlockEntity) obj).getBlockState().getBlock()).toString();
                } catch (Throwable ignored) { return "minecraft:air"; }
            })
            .propertyTyped("time", TypeCodecs.DOUBLE, (FallingBlockEntity fb) -> (double) fb.time);
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

    /** Deliberately NOT replaceable by {@code TypeCodecs.listOf("Entity", Entity.class)} at its call
     *  sites: {@link #wrap} is POLYMORPHIC — it picks "Player"/"Animal"/"Mob"/"ItemEntity"/... per
     *  element, so a script gets each entity's most specific PolyType and its subtype-only members.
     *  A single listOf codec re-boxes every element under ONE fixed name, which would flatten all of
     *  those to plain "Entity" and silently strip those members. */
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

    /** The {@code location} property's typed body. Goes through {@link LocationType#wrapEntity} on
     *  purpose — it carries the contraption sub-level -> real-world projection — and then unwraps the
     *  LocationRef the LOCATION_CODEC re-boxes under the very same "Location" name. A NULL from
     *  wrapEntity (non-ServerLevel) becomes null here and encodes back to NULL. */
    private static LocationType.LocationRef locationRef(Entity e) {
        return LocationType.wrapEntity(e) instanceof ScriptValue.Obj o
                && o.instance() instanceof LocationType.LocationRef ref ? ref : null;
    }

    private static Entity        entity(Object obj)     { return (Entity) obj; }
    private static LivingEntity  living(Object obj)     { return (LivingEntity) obj; }
    private static Mob           mob(Object obj)        { return (Mob) obj; }
    private static ItemEntity    itemEntity(Object obj) { return (ItemEntity) obj; }

    /** Resolves a bare or namespaced potion-effect id ("slow_falling" / "minecraft:slow_falling")
     *  to its registry {@link net.minecraft.core.Holder}, or null if unknown. */
    private static net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> mobEffectHolder(String name) {
        net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.parse(
                name.contains(":") ? name : "minecraft:" + name);
        var mobEffect = BuiltInRegistries.MOB_EFFECT.getValue(id);
        return mobEffect == null ? null : BuiltInRegistries.MOB_EFFECT.wrapAsHolder(mobEffect);
    }
}
