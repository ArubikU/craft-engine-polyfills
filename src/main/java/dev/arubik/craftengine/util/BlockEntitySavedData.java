package dev.arubik.craftengine.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class BlockEntitySavedData extends SavedData {

    private static final String DATA_NAME = "craftengine_block_data";
    private final Map<UUID, BlockEntityBehaviorController.BlockData> dataMap = new HashMap<>();

    public static BlockEntitySavedData get(ServerLevel level) {
        Object storage = level.getDataStorage();
        // Try: computeIfAbsent(Function<CompoundTag,T>, Supplier<T>, String)
        try {
            var m = storage.getClass().getMethod(
                    "computeIfAbsent",
                    java.util.function.Function.class,
                    java.util.function.Supplier.class,
                    String.class
            );
            Object result = m.invoke(
                    storage,
                    (java.util.function.Function<CompoundTag, BlockEntitySavedData>) BlockEntitySavedData::load,
                    (java.util.function.Supplier<BlockEntitySavedData>) BlockEntitySavedData::new,
                    DATA_NAME
            );
            return (BlockEntitySavedData) result;
        } catch (Throwable ignored) {}

        // Try: computeIfAbsent(SavedData$Factory, String)
        try {
            Class<?> factoryCls = Class.forName("net.minecraft.world.level.saveddata.SavedData$Factory");
            var ctor = factoryCls.getConstructor(java.util.function.Function.class, java.util.function.Supplier.class);
            Object factory = ctor.newInstance(
                    (java.util.function.Function<CompoundTag, BlockEntitySavedData>) BlockEntitySavedData::load,
                    (java.util.function.Supplier<BlockEntitySavedData>) BlockEntitySavedData::new
            );
            var m = storage.getClass().getMethod("computeIfAbsent", factoryCls, String.class);
            Object result = m.invoke(storage, factory, DATA_NAME);
            return (BlockEntitySavedData) result;
        } catch (Throwable ignored) {}

        // Fallback: get(name) then set(name, data)
        try {
            // Try get(String)
            var getM = storage.getClass().getMethod("get", String.class);
            Object val = getM.invoke(storage, DATA_NAME);
            if (val instanceof BlockEntitySavedData got) return got;
        } catch (Throwable ignored) {}
        BlockEntitySavedData created = new BlockEntitySavedData();
        // Try set(String, SavedData)
        try {
            for (var m : storage.getClass().getMethods()) {
                if (!m.getName().equals("set")) continue;
                var params = m.getParameterTypes();
                if (params.length == 2 && params[0] == String.class) {
                    try {
                        m.invoke(storage, DATA_NAME, created);
                        break;
                    } catch (Throwable ignored2) {}
                }
            }
        } catch (Throwable ignored) {}
        return created;
    }

    public BlockEntitySavedData() {}

    public static BlockEntitySavedData load(CompoundTag tag) {
        BlockEntitySavedData data = new BlockEntitySavedData();
        ListTag list = getList0(tag, "blocks");
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = getListCompound0(list, i);
            BlockEntityBehaviorController.BlockData bd = BlockEntityBehaviorController.BlockData.load(entry);
            data.dataMap.put(bd.id, bd);
        }
    // Hydrate runtime map so in-memory data matches persisted snapshot
    BlockEntityBehaviorController.hydrateFromSaved(data.dataMap.values());
        return data;
    }

    public CompoundTag save(CompoundTag tag) {
    ListTag list = new ListTag();
    // Always flush current runtime state instead of stale internal map
    for (BlockEntityBehaviorController.BlockData bd : BlockEntityBehaviorController.getAll()) {
            list.add(bd.save());
        }
        tag.put("blocks", list);
        return tag;
    }

    public Map<UUID, BlockEntityBehaviorController.BlockData> getData() {
        return dataMap;
    }

    // --- NBT helpers for compatibility ---
    private static ListTag getList0(CompoundTag tag, String key) {
        try {
            // 2-arg variant: getList(String, int)
            var m = tag.getClass().getMethod("getList", String.class, int.class);
            Object val = m.invoke(tag, key, 10); // 10 = CompoundTag id
            if (val instanceof ListTag lt) return lt;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                if (o instanceof ListTag lt) return lt;
            }
        } catch (Throwable ignored) {}
        try {
            // 1-arg variant: getList(String)
            var m = tag.getClass().getMethod("getList", String.class);
            Object val = m.invoke(tag, key);
            if (val instanceof ListTag lt) return lt;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                if (o instanceof ListTag lt) return lt;
            }
        } catch (Throwable ignored) {}
        return new ListTag();
    }

    private static CompoundTag getListCompound0(ListTag list, int index) {
        try {
            var m = list.getClass().getMethod("getCompound", int.class);
            Object val = m.invoke(list, index);
            if (val instanceof CompoundTag ct) return ct;
            if (val instanceof java.util.Optional<?> opt) {
                Object o = opt.orElse(null);
                if (o instanceof CompoundTag ct) return ct;
            }
        } catch (Throwable ignored) {}
        try {
            var m = list.getClass().getMethod("get", int.class);
            Object val = m.invoke(list, index);
            if (val instanceof CompoundTag ct) return ct;
        } catch (Throwable ignored) {}
        return new CompoundTag();
    }
}
