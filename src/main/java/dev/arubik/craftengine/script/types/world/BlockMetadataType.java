package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.types.primitive.VectorType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Block.get_metadata — reads the real block entity at a position into a type-appropriate script
 * object, so scripts can do e.g. {@code block.get_metadata.front.lines[0]} for a sign, or
 * {@code block.get_metadata.get_item(0)} for a chest, instead of only seeing raw block-state
 * properties (which is all {@code Block.get_state}/{@code Block.property} expose).
 *
 * "BlockMetadata" is the common base type; each block-entity kind that has real per-instance data
 * worth exposing gets its own child type (single instance object shape: {@link MetaRef}, cast to
 * the specific NMS block-entity class per property — same "wrap once, branch inside" convention
 * {@code RedstoneType}/{@code IoType} already use elsewhere in this script system).
 */
public final class BlockMetadataType {

    /** Shared instance object for every BlockMetadata (sub)type. */
    public record MetaRef(BlockEntity be, BlockPos pos) {}

    /** Instance object for SignSide — one of a sign's two independently-writable faces. */
    public record SignSideRef(SignText text) {}

    private BlockMetadataType() {}

    public static void register() {
        PolyTypeRegistry.define("BlockMetadata")
            .property("type", obj -> ScriptValue.of(kindOf(ref(obj).be())))
            .property("pos", obj -> {
                BlockPos p = ref(obj).pos();
                return VectorType.wrap(p.getX(), p.getY(), p.getZ());
            })
            .property("custom_name", obj -> {
                if (ref(obj).be() instanceof Nameable n && n.hasCustomName()) {
                    return ScriptValue.of(n.getName().getString());
                }
                return ScriptValue.NULL;
            });

        PolyTypeRegistry.define("SignSide")
            .property("lines", obj -> {
                SignText text = ((SignSideRef) obj).text();
                List<ScriptValue> lines = new ArrayList<>(4);
                for (int i = 0; i < 4; i++) {
                    lines.add(ScriptValue.of(text.getMessage(i, false).getString()));
                }
                return new ScriptValue.Array(lines);
            })
            .property("color", obj -> ScriptValue.of(((SignSideRef) obj).text().getColor().getName()))
            .property("glowing", obj -> ScriptValue.of(((SignSideRef) obj).text().hasGlowingText()));

        PolyTypeRegistry.define("SignMetadata", "BlockMetadata")
            .property("front", obj -> {
                if (ref(obj).be() instanceof SignBlockEntity sign) {
                    return ScriptValue.ofObj("SignSide", new SignSideRef(sign.getFrontText()));
                }
                return ScriptValue.NULL;
            })
            .property("back", obj -> {
                if (ref(obj).be() instanceof SignBlockEntity sign) {
                    return ScriptValue.ofObj("SignSide", new SignSideRef(sign.getBackText()));
                }
                return ScriptValue.NULL;
            })
            .property("is_waxed", obj -> ScriptValue.of(
                    ref(obj).be() instanceof SignBlockEntity sign && sign.isWaxed()));

        PolyTypeRegistry.define("ContainerMetadata", "BlockMetadata")
            .property("size", obj -> ScriptValue.of(container(obj) != null ? container(obj).getContainerSize() : 0))
            .property("is_empty", obj -> ScriptValue.of(container(obj) == null || container(obj).isEmpty()))
            .property("items", obj -> {
                Container c = container(obj);
                List<ScriptValue> items = new ArrayList<>(c != null ? c.getContainerSize() : 0);
                if (c != null) {
                    for (int i = 0; i < c.getContainerSize(); i++) items.add(ScriptValue.ofItem(c.getItem(i)));
                }
                return new ScriptValue.Array(items);
            })
            .method("get_item", (obj, args) -> {
                Container c = container(obj);
                if (c == null || args.isEmpty()) return ScriptValue.NULL;
                int i = (int) args.get(0).asNum();
                return (i >= 0 && i < c.getContainerSize()) ? ScriptValue.ofItem(c.getItem(i)) : ScriptValue.NULL;
            });

        PolyTypeRegistry.define("SkullMetadata", "BlockMetadata")
            .property("owner_name", obj -> {
                var profile = ownerProfile(obj);
                if (profile == null) return ScriptValue.NULL;
                String name = profile.name().orElse(null);
                return name != null ? ScriptValue.of(name) : ScriptValue.NULL;
            })
            .property("owner_uuid", obj -> {
                var profile = ownerProfile(obj);
                java.util.UUID id = profile != null ? reflectOptionalUuid(profile) : null;
                return id != null ? ScriptValue.of(id.toString()) : ScriptValue.NULL;
            })
            .property("has_owner", obj -> ScriptValue.of(ownerProfile(obj) != null));

        PolyTypeRegistry.define("BannerMetadata", "BlockMetadata")
            .property("base_color", obj -> ScriptValue.of(
                    ref(obj).be() instanceof BannerBlockEntity banner ? banner.getBaseColor().getName() : "white"))
            .property("pattern_count", obj -> ScriptValue.of(
                    ref(obj).be() instanceof BannerBlockEntity banner ? banner.getPatterns().layers().size() : 0));

        // LecternBlockEntity.getBook() is a long-stable public accessor (comparators have always
        // read it directly) — no reflection needed, unlike the others below.
        PolyTypeRegistry.define("LecternMetadata", "BlockMetadata")
            .property("book", obj -> ref(obj).be() instanceof net.minecraft.world.level.block.entity.LecternBlockEntity l
                    ? ScriptValue.ofItem(l.getBook()) : ScriptValue.NULL)
            .property("has_book", obj -> ScriptValue.of(
                    ref(obj).be() instanceof net.minecraft.world.level.block.entity.LecternBlockEntity l && !l.getBook().isEmpty()));

        // JukeboxBlockEntity's record-item accessor name has changed across MC versions
        // ("getRecord"/"getTheItem"/a Container-style getItem(0)) — reflect defensively, same
        // reasoning as the ResolvableProfile helpers above.
        PolyTypeRegistry.define("JukeboxMetadata", "BlockMetadata")
            .property("record", obj -> {
                var item = reflectItemStack(ref(obj).be(), "getRecord", "getTheItem", "getItem");
                return item != null ? ScriptValue.ofItem(item) : ScriptValue.NULL;
            })
            .property("is_playing", obj -> ScriptValue.of(reflectBool(ref(obj).be(), "isRecordPlaying")));

        // BeehiveBlockEntity's occupant-count accessor is likewise not a stable/simple name across
        // versions — reflect for it too.
        PolyTypeRegistry.define("BeehiveMetadata", "BlockMetadata")
            .property("bee_count", obj -> ScriptValue.of(reflectInt(ref(obj).be(), "getOccupantCount", "getBeeCount")));
    }

    /**
     * ResolvableProfile's UUID accessor name has shifted across Minecraft versions (it's an
     * {@code Optional<UUID>} field, sometimes named {@code id}, sometimes reached only via the
     * profile's {@code GameProfile}) — reflect for it instead of hard-coding one signature that
     * might not match this exact NMS build, mirroring how ContraptionSignElement's setSignText
     * already reflects around a similarly version-shifted sign API in this same codebase.
     */
    private static java.util.UUID reflectOptionalUuid(net.minecraft.world.item.component.ResolvableProfile profile) {
        for (String name : new String[]{"id", "uuid", "gameProfile"}) {
            try {
                var m = profile.getClass().getMethod(name);
                Object result = m.invoke(profile);
                if (result instanceof java.util.Optional<?> opt && opt.isPresent()) {
                    Object v = opt.get();
                    if (v instanceof java.util.UUID u) return u;
                    if (v instanceof com.mojang.authlib.GameProfile) return gameProfileId(v);
                }
                if (result instanceof java.util.UUID u) return u;
                if (result instanceof com.mojang.authlib.GameProfile) return gameProfileId(result);
            } catch (Throwable ignored) {}
        }
        return null;
    }

    /** GameProfile's UUID accessor is "id" in this mapped version, but reflect defensively. */
    private static java.util.UUID gameProfileId(Object gameProfile) {
        for (String name : new String[]{"id", "getId", "uuid"}) {
            try {
                Object v = gameProfile.getClass().getMethod(name).invoke(gameProfile);
                if (v instanceof java.util.UUID u) return u;
            } catch (Throwable ignored) {}
        }
        return null;
    }

    /** Block.get_metadata — NULL when there's no block entity here, or nothing modeled for it. */
    public static ScriptValue metadataOf(ServerLevel level, BlockPos pos) {
        BlockEntity be = beAt(level, pos);
        if (be == null) return ScriptValue.NULL;
        String type = typeNameOf(be);
        return type != null ? ScriptValue.ofObj(type, new MetaRef(be, pos)) : ScriptValue.NULL;
    }

    /**
     * Block.metadata_type — the PolyType name {@code get_metadata} would return for this block
     * right now (e.g. "SignMetadata"), or NULL if there's no block entity here / nothing modeled
     * for it — lets a script branch on what a block IS without fetching (and paying for) the full
     * metadata object first.
     */
    public static ScriptValue metadataTypeOf(ServerLevel level, BlockPos pos) {
        BlockEntity be = beAt(level, pos);
        if (be == null) return ScriptValue.NULL;
        String type = typeNameOf(be);
        return type != null ? ScriptValue.of(type) : ScriptValue.NULL;
    }

    /**
     * Block.metadata_types — every modeled type name that currently applies to this block, most
     * specific first (e.g. a hopper is both "ContainerMetadata" and, generically, "BlockMetadata")
     * — plural because a block entity can genuinely satisfy more than one shape (a container that
     * is also Nameable is still just "container" here since custom_name already lives on the base
     * type, but the array shape is future-proof for a block-entity kind modeled under two child
     * types at once).
     */
    public static ScriptValue metadataTypesOf(ServerLevel level, BlockPos pos) {
        BlockEntity be = beAt(level, pos);
        if (be == null) return new ScriptValue.Array(List.of());
        List<ScriptValue> types = new ArrayList<>(2);
        String specific = typeNameOf(be);
        if (specific != null) {
            types.add(ScriptValue.of(specific));
            if (!specific.equals("BlockMetadata")) types.add(ScriptValue.of("BlockMetadata"));
        }
        return new ScriptValue.Array(types);
    }

    private static BlockEntity beAt(ServerLevel level, BlockPos pos) {
        return level == null || pos == null ? null : level.getBlockEntity(pos);
    }

    /** The specific PolyType name for a block entity, or null when nothing is modeled for it. */
    private static String typeNameOf(BlockEntity be) {
        return switch (be) {
            case SignBlockEntity ignored -> "SignMetadata";
            case SkullBlockEntity ignored -> "SkullMetadata";
            case BannerBlockEntity ignored -> "BannerMetadata";
            case net.minecraft.world.level.block.entity.LecternBlockEntity ignored -> "LecternMetadata";
            default -> {
                if (be.getClass().getSimpleName().equals("JukeboxBlockEntity")) yield "JukeboxMetadata";
                if (be.getClass().getSimpleName().equals("BeehiveBlockEntity")) yield "BeehiveMetadata";
                yield be instanceof Container ? "ContainerMetadata" : null;
            }
        };
    }

    private static String kindOf(BlockEntity be) {
        String type = typeNameOf(be);
        if (type == null) return "unknown";
        // "SignMetadata" -> "sign", "ContainerMetadata" -> "container", etc.
        String stripped = type.endsWith("Metadata") ? type.substring(0, type.length() - "Metadata".length()) : type;
        return stripped.toLowerCase(java.util.Locale.ROOT);
    }

    /** Reflect a no-arg ItemStack-returning accessor, trying each candidate name in order. */
    private static net.minecraft.world.item.ItemStack reflectItemStack(Object target, String... names) {
        for (String name : names) {
            try {
                Object v = target.getClass().getMethod(name).invoke(target);
                if (v instanceof net.minecraft.world.item.ItemStack stack) return stack;
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private static boolean reflectBool(Object target, String... names) {
        for (String name : names) {
            try {
                Object v = target.getClass().getMethod(name).invoke(target);
                if (v instanceof Boolean b) return b;
            } catch (Throwable ignored) {}
        }
        return false;
    }

    private static int reflectInt(Object target, String... names) {
        for (String name : names) {
            try {
                Object v = target.getClass().getMethod(name).invoke(target);
                if (v instanceof Integer i) return i;
            } catch (Throwable ignored) {}
        }
        return 0;
    }

    private static Container container(Object obj) {
        return ref(obj).be() instanceof Container c ? c : null;
    }

    private static net.minecraft.world.item.component.ResolvableProfile ownerProfile(Object obj) {
        return ref(obj).be() instanceof SkullBlockEntity skull ? skull.getOwnerProfile() : null;
    }

    private static MetaRef ref(Object obj) { return (MetaRef) obj; }
}
