package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;

public final class WorldType {

    private WorldType() {}

    public static void register() {
        PolyTypeRegistry.define("World")
            .property("time",         obj -> ScriptValue.of(level(obj).getDayTime()))
            .property("day_time",     obj -> ScriptValue.of(level(obj).getDayTime() % 24000))
            .property("is_day",       obj -> ScriptValue.of(level(obj).getDayTime() % 24000 < 12000))
            .property("is_night",     obj -> ScriptValue.of(level(obj).getDayTime() % 24000 >= 12000))
            .property("is_raining",   obj -> ScriptValue.of(level(obj).isRaining()))
            .property("is_thundering",obj -> ScriptValue.of(level(obj).isThundering()))
            .property("name",         obj -> ScriptValue.of(level(obj).dimension().identifier().toString()))
            .property("seed",         obj -> ScriptValue.of((double) level(obj).getSeed()))
            .method("get_block", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.NULL;
                int x = (int) args.get(0).asNum(), y = (int) args.get(1).asNum(), z = (int) args.get(2).asNum();
                return BlockType.wrap(level(obj), new net.minecraft.core.BlockPos(x, y, z));
            })
            .method("get_light", (obj, args) -> {
                if (args.size() < 3) return ScriptValue.of(0);
                int x = (int) args.get(0).asNum(), y = (int) args.get(1).asNum(), z = (int) args.get(2).asNum();
                return ScriptValue.of(level(obj).getMaxLocalRawBrightness(new net.minecraft.core.BlockPos(x, y, z)));
            })
            // spawn_entity(type, x, y, z) → Entity
            .method("spawn_entity", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.NULL;
                ServerLevel level = level(obj);
                String typeId = args.get(0).asStr();
                double x = args.get(1).asNum(), y = args.get(2).asNum(), z = args.get(3).asNum();
                try {
                    var optType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(typeId.contains(":") ? typeId : "minecraft:" + typeId));
                    if (optType.isEmpty()) return ScriptValue.NULL;
                    net.minecraft.world.entity.Entity spawned = optType.get().create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                    if (spawned == null) return ScriptValue.NULL;
                    spawned.setPos(x, y, z);
                    level.addFreshEntity(spawned);
                    return EntityType.wrap(spawned);
                } catch (Throwable e) { return ScriptValue.NULL; }
            })
            // set_block(x, y, z, blockId)
            .method("set_block", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                ServerLevel level = level(obj);
                int x = (int) args.get(0).asNum(), y = (int) args.get(1).asNum(), z = (int) args.get(2).asNum();
                String id = args.get(3).asStr();
                try {
                    var block = (net.minecraft.world.level.block.Block) BuiltInRegistries.BLOCK.getValue(
                        Identifier.parse(id.contains(":") ? id : "minecraft:" + id));
                    if (block == null) return ScriptValue.of(false);
                    level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), 3);
                    return ScriptValue.of(true);
                } catch (Throwable e) { return ScriptValue.of(false); }
            })
            // entities_in_range(x, y, z, radius) → Array<Entity>
            .method("entities_in_range", (obj, args) -> {
                if (args.size() < 4) return new ScriptValue.Array(java.util.List.of());
                ServerLevel level = level(obj);
                double x = args.get(0).asNum(), y = args.get(1).asNum(), z = args.get(2).asNum(), r = args.get(3).asNum();
                var entities = level.getEntities((net.minecraft.world.entity.Entity) null, AABB.ofSize(new net.minecraft.world.phys.Vec3(x, y, z), r*2, r*2, r*2), e -> true);
                java.util.List<ScriptValue> result = new java.util.ArrayList<>(entities.size());
                for (var e : entities) result.add(EntityType.wrap(e));
                return new ScriptValue.Array(result);
            })
            // play_sound(x, y, z, soundId, volume?, pitch?)
            .method("play_sound", (obj, args) -> {
                if (args.size() < 4) return ScriptValue.of(false);
                try {
                    double x = args.get(0).asNum(), y = args.get(1).asNum(), z = args.get(2).asNum();
                    String sound = args.get(3).asStr();
                    float vol   = args.size() >= 5 ? (float) args.get(4).asNum() : 1.0f;
                    float pitch = args.size() >= 6 ? (float) args.get(5).asNum() : 1.0f;
                    Identifier id = Identifier.tryParse(sound.contains(":") ? sound : "minecraft:" + sound);
                    if (id == null) return ScriptValue.of(false);
                    SoundEvent event = SoundEvent.createVariableRangeEvent(id);
                    level(obj).playSeededSound(null, x, y, z, Holder.direct(event),
                        SoundSource.BLOCKS, vol, pitch, 0L);
                    return ScriptValue.of(true);
                } catch (Throwable ignored) { return ScriptValue.of(false); }
            });
    }

    public static ScriptValue wrap(ServerLevel level) {
        if (level == null) return ScriptValue.NULL;
        return ScriptValue.ofObj("World", level);
    }

    private static ServerLevel level(Object obj) { return (ServerLevel) obj; }
}
