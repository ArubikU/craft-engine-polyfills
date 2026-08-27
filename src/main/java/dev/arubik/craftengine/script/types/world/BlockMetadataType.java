package dev.arubik.craftengine.script.types.world;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
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
        // ref(obj) here IS a plain `(MetaRef) obj` cast, so MetaRef can be the typed instance
        // parameter directly (unlike BlockType.ref, which also converts a MachineRef).
        PolyTypeRegistry.define("BlockMetadata")
            .propertyTyped("type", TypeCodecs.STRING, (MetaRef m) -> kindOf(m.be()))
            // polyType("Vector", Vector3d) — VectorType.wrap(x,y,z) is exactly
            // ScriptValue.ofObj("Vector", new Vector3d(x,y,z)).
            .propertyTyped("pos", TypeCodecs.polyType("Vector", org.joml.Vector3d.class), (MetaRef m) -> {
                BlockPos p = m.pos();
                return new org.joml.Vector3d(p.getX(), p.getY(), p.getZ());
            })
            // STRING, not RAW: TypeCodecs.STRING.encode delegates to ScriptValue.of(String), which
            // maps a null String to ScriptValue.NULL — exactly the old "not Nameable / no custom
            // name" branch.
            .propertyTyped("custom_name", TypeCodecs.STRING, (MetaRef m) -> {
                if (m.be() instanceof Nameable n && n.hasCustomName()) {
                    return n.getName().getString();
                }
                return null;
            });

        PolyTypeRegistry.define("SignSide")
            // RAW, not listOf: the elements are raw strings, not Objs of any PolyType — a list
            // codec would silently drop every one of them.
            .propertyTyped("lines", TypeCodecs.RAW, (SignSideRef ref) -> {
                SignText text = ref.text();
                List<ScriptValue> lines = new ArrayList<>(4);
                for (int i = 0; i < 4; i++) {
                    lines.add(ScriptValue.of(text.getMessage(i, false).getString()));
                }
                return new ScriptValue.Array(lines);
            })
            .propertyTyped("color", TypeCodecs.STRING, (SignSideRef ref) -> ref.text().getColor().getName())
            .propertyTyped("glowing", TypeCodecs.BOOL, (SignSideRef ref) -> ref.text().hasGlowingText());

        PolyTypeRegistry.define("SignMetadata", "BlockMetadata")
            // polyType("SignSide", SignSideRef) — PolyCodec.encode is ofObj("SignSide", v) for a
            // non-null value and NULL for null, matching the old non-sign branch exactly.
            .propertyTyped("front", TypeCodecs.polyType("SignSide", SignSideRef.class), (MetaRef m) -> {
                if (m.be() instanceof SignBlockEntity sign) {
                    return new SignSideRef(sign.getFrontText());
                }
                return null;
            })
            .propertyTyped("back", TypeCodecs.polyType("SignSide", SignSideRef.class), (MetaRef m) -> {
                if (m.be() instanceof SignBlockEntity sign) {
                    return new SignSideRef(sign.getBackText());
                }
                return null;
            })
            .propertyTyped("is_waxed", TypeCodecs.BOOL, (MetaRef m) ->
                    m.be() instanceof SignBlockEntity sign && sign.isWaxed());

        PolyTypeRegistry.define("ContainerMetadata", "BlockMetadata")
            // container(obj) does more than a plain cast (it re-derives the Container view from the
            // block entity each call, see below), so it is still called explicitly inside these
            // bodies — the typed instance parameter is just the MetaRef ref(obj) would have cast to.
            .propertyTyped("size", TypeCodecs.DOUBLE, (MetaRef m) ->
                    (double) (container(m) != null ? container(m).getContainerSize() : 0))
            .propertyTyped("is_empty", TypeCodecs.BOOL, (MetaRef m) -> container(m) == null || container(m).isEmpty())
            // RAW, not listOf: the elements are ScriptValue.Item (ScriptValue.ofItem), a distinct
            // ScriptValue variant rather than an Obj of any PolyType — listOf would re-box them.
            .propertyTyped("items", TypeCodecs.RAW, (MetaRef obj) -> {
                Container c = container(obj);
                List<ScriptValue> items = new ArrayList<>(c != null ? c.getContainerSize() : 0);
                if (c != null) {
                    for (int i = 0; i < c.getContainerSize(); i++) items.add(ScriptValue.ofItem(c.getItem(i)));
                }
                return new ScriptValue.Array(items);
            })
            // container(obj) does more than a plain cast (it re-derives the Container view from the
            // block entity each call, see below), so `obj` stays Object here and the helper is
            // called explicitly inside the body rather than becoming the typed handler's instance
            // parameter type.
            .methodTyped1("get_item", TypeCodecs.DOUBLE, TypeCodecs.RAW, ScriptValue.NULL,
                (Object obj, Double idxArg) -> {
                    Container c = container(obj);
                    if (c == null) return ScriptValue.NULL;
                    int i = idxArg.intValue();
                    return (i >= 0 && i < c.getContainerSize()) ? ScriptValue.ofItem(c.getItem(i)) : ScriptValue.NULL;
                });

        PolyTypeRegistry.define("SkullMetadata", "BlockMetadata")
            // STRING with a null return — encodes to NULL exactly like the old explicit
            // ScriptValue.NULL branches (see BlockMetadata.custom_name above).
            .propertyTyped("owner_name", TypeCodecs.STRING, (MetaRef obj) -> {
                var profile = ownerProfile(obj);
                if (profile == null) return null;
                return profile.name().orElse(null);
            })
            .propertyTyped("owner_uuid", TypeCodecs.STRING, (MetaRef obj) -> {
                var profile = ownerProfile(obj);
                java.util.UUID id = profile != null ? reflectOptionalUuid(profile) : null;
                return id != null ? id.toString() : null;
            })
            .propertyTyped("has_owner", TypeCodecs.BOOL, (MetaRef obj) -> ownerProfile(obj) != null);

        PolyTypeRegistry.define("BannerMetadata", "BlockMetadata")
            .propertyTyped("base_color", TypeCodecs.STRING, (MetaRef m) ->
                    m.be() instanceof BannerBlockEntity banner ? banner.getBaseColor().getName() : "white")
            .propertyTyped("pattern_count", TypeCodecs.DOUBLE, (MetaRef m) ->
                    (double) (m.be() instanceof BannerBlockEntity banner ? banner.getPatterns().layers().size() : 0));

        // LecternBlockEntity.getBook() is a long-stable public accessor (comparators have always
        // read it directly) — no reflection needed, unlike the others below.
        PolyTypeRegistry.define("LecternMetadata", "BlockMetadata")
            // RAW: ScriptValue.ofItem yields the distinct ScriptValue.Item variant (not an Obj of
            // any PolyType), so no polyType/list codec applies — RAW.encode is identity.
            .propertyTyped("book", TypeCodecs.RAW, (MetaRef m) ->
                    m.be() instanceof net.minecraft.world.level.block.entity.LecternBlockEntity l
                    ? ScriptValue.ofItem(l.getBook()) : ScriptValue.NULL)
            .propertyTyped("has_book", TypeCodecs.BOOL, (MetaRef m) ->
                    m.be() instanceof net.minecraft.world.level.block.entity.LecternBlockEntity l && !l.getBook().isEmpty());

        // JukeboxBlockEntity's record-item accessor name has changed across MC versions
        // ("getRecord"/"getTheItem"/a Container-style getItem(0)) — reflect defensively, same
        // reasoning as the ResolvableProfile helpers above.
        PolyTypeRegistry.define("JukeboxMetadata", "BlockMetadata")
            // RAW for the same reason as LecternMetadata.book above (ScriptValue.Item variant).
            .propertyTyped("record", TypeCodecs.RAW, (MetaRef m) -> {
                var item = reflectItemStack(m.be(), "getRecord", "getTheItem", "getItem");
                return item != null ? ScriptValue.ofItem(item) : ScriptValue.NULL;
            })
            .propertyTyped("is_playing", TypeCodecs.BOOL, (MetaRef m) -> reflectBool(m.be(), "isRecordPlaying"));

        // BeehiveBlockEntity's occupant-count accessor is likewise not a stable/simple name across
        // versions — reflect for it too.
        PolyTypeRegistry.define("BeehiveMetadata", "BlockMetadata")
            .propertyTyped("bee_count", TypeCodecs.DOUBLE, (MetaRef m) ->
                    (double) reflectInt(m.be(), "getOccupantCount", "getBeeCount"));
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
    // Backs Block.metadata_types, which is registered with TypeCodecs.RAW: its elements are plain
    // type-NAME strings, not Objs of any PolyType, so no list codec applies.
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
