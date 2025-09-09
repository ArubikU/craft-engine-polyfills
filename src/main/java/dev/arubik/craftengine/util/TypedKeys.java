package dev.arubik.craftengine.util;

import java.util.List;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.minecraft.world.ItemStackWithSlot;

public class TypedKeys {
    public static final String NAMESPACE = "craftengine";
    public static final TypedKey<Integer> MAX_STACK_SIZE = TypedKey.of(NAMESPACE,"max_stack_size", PersistentDataType.INTEGER);
    public static final TypedKey<List<ItemStackWithSlot>> CONTENTS = TypedKey.of(NAMESPACE,"contents", CustomDataType.ITEM_STACK_WITH_SLOT_LIST_TYPE);
    public static final TypedKey<PersistentDataContainer> PERSISTENT_DATA = TypedKey.of(NAMESPACE,"persistent_data", PersistentDataType.TAG_CONTAINER);
}
