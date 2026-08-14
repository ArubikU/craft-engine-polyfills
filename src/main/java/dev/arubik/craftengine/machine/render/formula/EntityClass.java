package dev.arubik.craftengine.machine.render.formula;

import net.minecraft.world.entity.Entity;
import java.util.List;

/**
 * PolyClass wrapping an NMS Entity. Returned by World.entities(x,y,z,r) array elements.
 * PlayerClass extends this conceptually (a player IS an entity with extra properties).
 *
 * Properties: type, x, y, z, name, uuid, is_player, health, is_alive
 * Methods: distance(x,y,z)
 */
public final class EntityClass implements PolyClass {

    private final Entity entity;

    public EntityClass(Entity entity) { this.entity = entity; }

    @Override
    public PolyValue get(String property) {
        if (entity == null || entity.isRemoved()) return PolyValue.NULL;
        return switch (property) {
            case "type"      -> PolyValue.of(entity.getType().builtInRegistryHolder().key().toString());
            case "x"         -> PolyValue.of(entity.getX());
            case "y"         -> PolyValue.of(entity.getY());
            case "z"         -> PolyValue.of(entity.getZ());
            case "name"      -> PolyValue.of(entity.getName().getString());
            case "uuid"      -> PolyValue.of(entity.getStringUUID());
            case "is_player" -> PolyValue.of(entity instanceof net.minecraft.server.level.ServerPlayer);
            case "is_alive"  -> PolyValue.of(entity.isAlive());
            case "health" -> {
                if (entity instanceof net.minecraft.world.entity.LivingEntity le)
                    yield PolyValue.of(le.getHealth());
                yield PolyValue.of(0);
            }
            case "max_health" -> {
                if (entity instanceof net.minecraft.world.entity.LivingEntity le)
                    yield PolyValue.of(le.getMaxHealth());
                yield PolyValue.of(0);
            }
            case "yaw"   -> PolyValue.of(entity.getYRot());
            case "pitch" -> PolyValue.of(entity.getXRot());
            case "location" -> new PolyValue.Obj(LocationClass.forEntity(entity));
            case "world" -> {
                if (entity.level() instanceof net.minecraft.server.level.ServerLevel sl)
                    yield new PolyValue.Obj(WorldClass.forLevel(sl));
                yield PolyValue.NULL;
            }
            case "main_hand" -> {
                if (entity instanceof net.minecraft.world.entity.LivingEntity le)
                    yield PolyValue.ofItem(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(
                            le.getMainHandItem()));
                yield PolyValue.NULL;
            }
            case "is_living"    -> PolyValue.of(entity instanceof net.minecraft.world.entity.LivingEntity);
            case "on_ground"    -> PolyValue.of(entity.onGround());
            case "velocity_x"   -> PolyValue.of(entity.getDeltaMovement().x);
            case "velocity_y"   -> PolyValue.of(entity.getDeltaMovement().y);
            case "velocity_z"   -> PolyValue.of(entity.getDeltaMovement().z);
            default -> PolyValue.NULL;
        };
    }

    @Override
    public PolyValue call(String method, List<PolyValue> args) {
        if (entity == null) return PolyValue.NULL;
        return switch (method) {
            case "distance" -> {
                if (args.size() < 3) yield PolyValue.of(0);
                double dx = entity.getX() - args.get(0).asNum();
                double dy = entity.getY() - args.get(1).asNum();
                double dz = entity.getZ() - args.get(2).asNum();
                yield PolyValue.of(Math.sqrt(dx*dx + dy*dy + dz*dz));
            }
            case "as_player" -> {
                if (entity instanceof net.minecraft.server.level.ServerPlayer sp) {
                    yield new PolyValue.Obj(new PlayerClass(sp));
                }
                yield PolyValue.NULL;
            }
            case "location" -> new PolyValue.Obj(LocationClass.forEntity(entity));
            default -> get(method);
        };
    }
}
