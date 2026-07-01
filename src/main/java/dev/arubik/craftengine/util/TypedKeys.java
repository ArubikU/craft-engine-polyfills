package dev.arubik.craftengine.util;

public class TypedKeys {
        public static final String NAMESPACE = "craftengine";
        public static final TypedKey<Integer> MAX_STACK_SIZE = TypedKey.of(NAMESPACE, "max_stack_size",
                        NbtType.INTEGER);
        public static final TypedKey<java.util.UUID> OWNER = TypedKey.of(NAMESPACE, "owner", CustomDataType.UUID_TYPE);
        public static final TypedKey<Long> LAST_ATTACK_TIME = TypedKey.of(NAMESPACE, "last_attack_time",
                        NbtType.LONG);

        /** Single NMS ItemStack, codec-serialized (see {@link CustomDataType#ITEM_CODEC_TYPE}). */
        public static final TypedKey<net.minecraft.world.item.ItemStack> NMS_ITEM = TypedKey.of(NAMESPACE, "nms_item",
                        CustomDataType.ITEM_CODEC_TYPE);
        /** Fixed-size NMS ItemStack[] (index = slot), codec-serialized (see {@link CustomDataType#ITEM_ARRAY_CODEC_TYPE}). */
        public static final TypedKey<net.minecraft.world.item.ItemStack[]> NMS_ITEMS = TypedKey.of(NAMESPACE, "nms_items",
                        CustomDataType.ITEM_ARRAY_CODEC_TYPE);
}
