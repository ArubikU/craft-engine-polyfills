package dev.arubik.craftengine.script.types.primitive;

import dev.arubik.craftengine.script.PolyTypeRegistry;
import dev.arubik.craftengine.script.ScriptBuiltins;
import dev.arubik.craftengine.script.ScriptValue;
import dev.arubik.craftengine.script.TypeCodecs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Registers every Minecraft data component as a {@code {Name}Component} PolyType.
 * Provides NMS-only read/write/JSON-apply helpers used by ItemType and ItemSpec.
 *
 * Component types registered (example usage in scripts):
 *   cmd = item.component("custom_model_data")   → CustomModelDataComponent
 *   cmd.value  → number
 *   item2 = item.with_component(CustomModelDataComponent(42))
 */
public final class DataComponentTypes {

    private DataComponentTypes() {}

    // =========================================================================
    // REGISTRATION
    // =========================================================================

    public static void register() {
        registerCustomModelData();
        registerLore();
        registerCustomData();
        registerCustomName();
        registerItemName();
        registerItemModel();
        registerRarity();
        registerDamage();
        registerMaxDamage();
        registerMaxStackSize();
        registerRepairCost();
        registerOminousBottleAmplifier();
        registerEnchantmentGlintOverride();
        registerEnchantments();
        registerStoredEnchantments();
        registerFood();
        registerTool();
        registerEnchantable();
        registerDyedColor();
        registerMapColor();
        registerTooltipDisplay();
        registerUnbreakable();
        registerDamageResistant();
        registerAttributeModifiers();
        registerPotionContents();
        registerPotionDurationScale();
        registerBundleContents();
        registerContainer();
        registerChargedProjectiles();
        registerWrittenBookContent();
        registerWritableBookContent();
        registerProfile();
        registerNoteBlockSound();
        registerBaseColor();
        registerBannerPatterns();
        registerMapId();
        registerFireworkExplosion();
        registerFireworks();
        registerTrim();
        registerLodestoneTracker();
        registerSuspiciousStewEffects();
        registerBees();
        registerBlockState();
        registerInstrument();
        registerJukeboxPlayable();
        registerDeathProtection();
        registerPotDecorations();
        registerMapDecorations();
        registerTropicalFishPattern();
        registerRecipes();
        // Marker components (no data, presence = true)
        registerMarker("GliderComponent",              DataComponents.GLIDER);
        registerMarker("IntangibleProjectileComponent",DataComponents.INTANGIBLE_PROJECTILE);
        registerMarker("DamageTypeComponent",          DataComponents.DAMAGE_TYPE);
        // Complex components — wrap as opaque for read access
        registerOpaque("EntityDataComponent",          DataComponents.ENTITY_DATA);
        registerOpaque("BucketEntityDataComponent",    DataComponents.BUCKET_ENTITY_DATA);
        registerOpaque("BlockEntityDataComponent",     DataComponents.BLOCK_ENTITY_DATA);
        registerOpaque("ContainerLootComponent",       DataComponents.CONTAINER_LOOT);
        registerOpaque("LockComponent",                DataComponents.LOCK);
        registerOpaque("DebugStickStateComponent",     DataComponents.DEBUG_STICK_STATE);
        registerOpaque("CanBreakComponent",            DataComponents.CAN_BREAK);
        registerOpaque("CanPlaceOnComponent",          DataComponents.CAN_PLACE_ON);
        registerOpaque("MapDecorationsComponent",      DataComponents.MAP_DECORATIONS);
        // 1.21.4+ components — try/catch at registration time
        tryRegisterOpaque("TooltipStyleComponent",     "TOOLTIP_STYLE");
        tryRegisterOpaque("ProvidesTrimMaterialComponent","PROVIDES_TRIM_MATERIAL");
        tryRegisterOpaque("UseRemainderComponent",     "USE_REMAINDER");
        tryRegisterOpaque("UseCooldownComponent",      "USE_COOLDOWN");
        tryRegisterOpaque("WeaponComponent",           "WEAPON");
        tryRegisterOpaque("ConsumableComponent",       "CONSUMABLE");
        tryRegisterOpaque("EquippableComponent",       "EQUIPPABLE");
        tryRegisterOpaque("BreakSoundComponent",       "BREAK_SOUND");
        tryRegisterOpaque("RepairableComponent",       "REPAIRABLE");
        tryRegisterOpaque("MapPostProcessingComponent","MAP_POST_PROCESSING");
        tryRegisterOpaque("DamageTypeTagComponent",    "DAMAGE_TYPE");

        // Register script constructors
        registerBuiltins();
    }

    // =========================================================================
    // INDIVIDUAL TYPE REGISTRATIONS
    // =========================================================================
    //
    // Scalar properties below use propertyTyped with DOUBLE/BOOL/STRING — behaviour-identical to the
    // old untyped form (ScriptValue.of(int) already widened to of(double), and STRING encodes a Java
    // null back to ScriptValue.NULL exactly like the old catch branches returned it).
    //
    // The lambdas deliberately keep `Object obj` plus the ORIGINAL cast inside the try block: moving
    // the cast into the handler's declared parameter type would make a wrong-typed instance throw a
    // ClassCastException outside the try, losing the fallback value every one of these relies on.
    //
    // None of the ScriptValue.Array sites below can declare an element type with
    // TypeCodecs.listOf: every element is a raw scalar (double, bool, string) rather than an
    // Obj-wrapped PolyType instance, which is the only shape listOf can encode or decode — so those
    // stay on the untyped .property API.

    private static void registerCustomModelData() {
        PolyTypeRegistry.define("CustomModelDataComponent")
            .property("floats",  obj -> {
                try { var cmd = (net.minecraft.world.item.component.CustomModelData) obj;
                    return new ScriptValue.Array(cmd.floats().stream().map(f -> (ScriptValue) ScriptValue.of(f.doubleValue())).toList()); }
                catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            })
            .property("flags",   obj -> {
                try { var cmd = (net.minecraft.world.item.component.CustomModelData) obj;
                    return new ScriptValue.Array(cmd.flags().stream().map(b -> (ScriptValue) ScriptValue.of(b)).toList()); }
                catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            })
            .property("strings", obj -> {
                try { var cmd = (net.minecraft.world.item.component.CustomModelData) obj;
                    return new ScriptValue.Array(cmd.strings().stream().map(s -> (ScriptValue) ScriptValue.of(s)).toList()); }
                catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            })
            .property("colors",  obj -> {
                try { var cmd = (net.minecraft.world.item.component.CustomModelData) obj;
                    return new ScriptValue.Array(cmd.colors().stream().map(c -> (ScriptValue) ScriptValue.of(c.intValue())).toList()); }
                catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            })
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> {
                try { var cmd = (net.minecraft.world.item.component.CustomModelData) obj;
                    return cmd.floats().isEmpty() ? 0.0 : cmd.floats().get(0).doubleValue(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerLore() {
        PolyTypeRegistry.define("LoreComponent")
            .property("lines", obj -> {
                try {
                    var lore = (net.minecraft.world.item.component.ItemLore) obj;
                    return new ScriptValue.Array(lore.lines().stream()
                        .map(c -> (ScriptValue) ScriptValue.of(c.getString())).toList());
                } catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            })
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.item.component.ItemLore) obj).lines().size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerCustomData() {
        PolyTypeRegistry.define("CustomDataComponent")
            // Stays untyped: NbtDataType.wrap returns NULL for a null tag, and TypeCodecs.polyType
            // would need the handler to hand back a CompoundTag — doable, but the null-tag branch is
            // exactly what wrap() already encodes, so there is nothing to gain and a shape to lose.
            .property("nbt", obj -> {
                try { return NbtDataType.wrap(((net.minecraft.world.item.component.CustomData) obj).copyTag()); }
                catch (Throwable e) { return ScriptValue.NULL; }
            })
            .propertyTyped("is_empty", TypeCodecs.BOOL, (Object obj) -> {
                try { return ((net.minecraft.world.item.component.CustomData) obj).isEmpty(); }
                catch (Throwable e) { return true; }
            });
    }

    private static void registerCustomName() {
        PolyTypeRegistry.define("CustomNameComponent")
            .propertyTyped("text",  TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.network.chat.Component) obj).getString(); } catch (Throwable e) { return null; } })
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.network.chat.Component) obj).getString(); } catch (Throwable e) { return null; } });
    }

    private static void registerItemName() {
        PolyTypeRegistry.define("ItemNameComponent")
            .propertyTyped("text",  TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.network.chat.Component) obj).getString(); } catch (Throwable e) { return null; } })
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.network.chat.Component) obj).getString(); } catch (Throwable e) { return null; } });
    }

    private static void registerItemModel() {
        PolyTypeRegistry.define("ItemModelComponent")
            .propertyTyped("id",    TypeCodecs.STRING, (Object obj) -> { try { return obj.toString(); } catch (Throwable e) { return null; } })
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return obj.toString(); } catch (Throwable e) { return null; } });
    }

    private static void registerRarity() {
        PolyTypeRegistry.define("RarityComponent")
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return ((Rarity) obj).name().toLowerCase(Locale.ROOT); } catch (Throwable e) { return "common"; } });
    }

    private static void registerDamage() {
        PolyTypeRegistry.define("DamageComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((Integer) obj).doubleValue(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerMaxDamage() {
        PolyTypeRegistry.define("MaxDamageComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((Integer) obj).doubleValue(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerMaxStackSize() {
        PolyTypeRegistry.define("MaxStackSizeComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((Integer) obj).doubleValue(); } catch (Throwable e) { return 64.0; } });
    }

    private static void registerRepairCost() {
        PolyTypeRegistry.define("RepairCostComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((Integer) obj).doubleValue(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerOminousBottleAmplifier() {
        PolyTypeRegistry.define("OminousBottleAmplifierComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((Integer) obj).doubleValue(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerEnchantmentGlintOverride() {
        PolyTypeRegistry.define("EnchantmentGlintOverrideComponent")
            // `b != null && b` rather than a bare cast: the old form unboxed inside the try, so a null
            // instance produced `false` via the catch. Returning a null Boolean here would instead NPE
            // in the codec's encode, outside the try.
            .propertyTyped("value", TypeCodecs.BOOL, (Object obj) -> { try { Boolean b = (Boolean) obj; return b != null && b; } catch (Throwable e) { return false; } });
    }

    private static void registerEnchantments() {
        PolyTypeRegistry.define("EnchantmentsComponent")
            .propertyTyped("show_in_tooltip", TypeCodecs.BOOL, (Object obj) -> true)
            .property("entries", obj -> {
                try {
                    var enc = (net.minecraft.world.item.enchantment.ItemEnchantments) obj;
                    var ls = new java.util.ArrayList<ScriptValue>();
                    enc.entrySet().forEach(e -> ls.add(ScriptValue.of(
                        e.getKey().unwrapKey().map(Object::toString).orElse("?") + ":" + e.getIntValue())));
                    return new ScriptValue.Array(ls);
                } catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            })
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.item.enchantment.ItemEnchantments) obj).size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerStoredEnchantments() {
        // Same structure as EnchantmentsComponent but different type name
        PolyTypeRegistry.define("StoredEnchantmentsComponent")
            .propertyTyped("show_in_tooltip", TypeCodecs.BOOL, (Object obj) -> true)
            .property("entries", obj -> {
                try {
                    var enc = (net.minecraft.world.item.enchantment.ItemEnchantments) obj;
                    var ls = new java.util.ArrayList<ScriptValue>();
                    enc.entrySet().forEach(e -> ls.add(ScriptValue.of(
                        e.getKey().unwrapKey().map(Object::toString).orElse("?") + ":" + e.getIntValue())));
                    return new ScriptValue.Array(ls);
                } catch (Throwable e) { return new ScriptValue.Array(List.of()); }
            });
    }

    private static void registerFood() {
        PolyTypeRegistry.define("FoodComponent")
            .propertyTyped("nutrition",      TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.food.FoodProperties) obj).nutrition(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("saturation",     TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.food.FoodProperties) obj).saturation(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("can_always_eat", TypeCodecs.BOOL,   (Object obj) -> { try { return ((net.minecraft.world.food.FoodProperties) obj).canAlwaysEat(); } catch (Throwable e) { return false; } });
    }

    private static void registerTool() {
        PolyTypeRegistry.define("ToolComponent")
            .propertyTyped("default_mining_speed", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.Tool) obj).defaultMiningSpeed(); } catch (Throwable e) { return 1.0; } })
            .propertyTyped("damage_per_block",     TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.Tool) obj).damagePerBlock(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("rules_count",          TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.Tool) obj).rules().size(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerEnchantable() {
        PolyTypeRegistry.define("EnchantableComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.enchantment.Enchantable) obj).value(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerDyedColor() {
        PolyTypeRegistry.define("DyedColorComponent")
            .propertyTyped("rgb",              TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.DyedItemColor) obj).rgb(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerMapColor() {
        PolyTypeRegistry.define("MapColorComponent")
            .propertyTyped("rgb",   TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.MapItemColor) obj).rgb(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.MapItemColor) obj).rgb(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerTooltipDisplay() {
        PolyTypeRegistry.define("TooltipDisplayComponent")
            .propertyTyped("hide_tooltip", TypeCodecs.BOOL, (Object obj) -> { try { return ((net.minecraft.world.item.component.TooltipDisplay) obj).hideTooltip(); } catch (Throwable e) { return false; } });
    }

    private static void registerUnbreakable() {
        PolyTypeRegistry.define("UnbreakableComponent")
            .propertyTyped("show_in_tooltip", TypeCodecs.BOOL, (Object obj) -> true);
    }

    private static void registerDamageResistant() {
        PolyTypeRegistry.define("DamageResistantComponent")
            .propertyTyped("types", TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.world.item.component.DamageResistant) obj).types().location().toString(); } catch (Throwable e) { return null; } });
    }

    private static void registerAttributeModifiers() {
        PolyTypeRegistry.define("AttributeModifiersComponent")
            .propertyTyped("show_in_tooltip", TypeCodecs.BOOL, (Object obj) -> true)
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.ItemAttributeModifiers) obj).modifiers().size(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerPotionContents() {
        PolyTypeRegistry.define("PotionContentsComponent")
            // Left untyped: this one is genuinely two shapes — a number when the color is present,
            // NULL when it isn't. No scalar codec can express that (rule: mixed-shape stays untyped).
            .property("custom_color", obj -> {
                try {
                    var pc = (net.minecraft.world.item.alchemy.PotionContents) obj;
                    return pc.customColor().isPresent() ? ScriptValue.of(pc.customColor().orElse(-1)) : ScriptValue.NULL;
                } catch (Throwable e) { return ScriptValue.NULL; }
            })
            // Typed despite the absent case: STRING encodes a Java null straight back to
            // ScriptValue.NULL, which is exactly what the old branch returned.
            .propertyTyped("potion", TypeCodecs.STRING, (Object obj) -> {
                try {
                    var pc = (net.minecraft.world.item.alchemy.PotionContents) obj;
                    return pc.potion().isPresent()
                        ? pc.potion().get().unwrapKey().map(Object::toString).orElse("?")
                        : null;
                } catch (Throwable e) { return null; }
            });
    }

    private static void registerPotionDurationScale() {
        PolyTypeRegistry.define("PotionDurationScaleComponent")
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((Float) obj).doubleValue(); } catch (Throwable e) { return 1.0; } });
    }

    private static void registerBundleContents() {
        PolyTypeRegistry.define("BundleContentsComponent")
            .propertyTyped("size",   TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.BundleContents) obj).size(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("weight", TypeCodecs.DOUBLE, (Object obj) -> { try { return ((net.minecraft.world.item.component.BundleContents) obj).weight().doubleValue(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerContainer() {
        PolyTypeRegistry.define("ContainerComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.item.component.ItemContainerContents) obj).items.stream().filter(i -> !i.isEmpty()).count(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerChargedProjectiles() {
        PolyTypeRegistry.define("ChargedProjectilesComponent")
            .propertyTyped("size",     TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.ChargedProjectiles) obj).getItems().size(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("is_empty", TypeCodecs.BOOL,   (Object obj) -> { try { return ((net.minecraft.world.item.component.ChargedProjectiles) obj).getItems().isEmpty(); } catch (Throwable e) { return true; } });
    }

    private static void registerWrittenBookContent() {
        PolyTypeRegistry.define("WrittenBookContentComponent")
            .propertyTyped("title",      TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.world.item.component.WrittenBookContent) obj).title().raw(); } catch (Throwable e) { return null; } })
            .propertyTyped("author",     TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.world.item.component.WrittenBookContent) obj).author(); } catch (Throwable e) { return null; } })
            .propertyTyped("generation", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.WrittenBookContent) obj).generation(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("page_count", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.WrittenBookContent) obj).pages().size(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerWritableBookContent() {
        PolyTypeRegistry.define("WritableBookContentComponent")
            .propertyTyped("page_count", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.WritableBookContent) obj).pages().size(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerProfile() {
        PolyTypeRegistry.define("ProfileComponent")
            .propertyTyped("name", TypeCodecs.STRING, (Object obj) -> {
                try {
                    var p = (net.minecraft.world.item.component.ResolvableProfile) obj;
                    return p.name().isPresent() ? p.name().get() : null;
                } catch (Throwable e) { return null; }
            });
    }

    private static void registerNoteBlockSound() {
        PolyTypeRegistry.define("NoteBlockSoundComponent")
            .propertyTyped("id",    TypeCodecs.STRING, (Object obj) -> { try { return obj.toString(); } catch (Throwable e) { return null; } })
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return obj.toString(); } catch (Throwable e) { return null; } });
    }

    private static void registerBaseColor() {
        PolyTypeRegistry.define("BaseColorComponent")
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return obj.toString().toLowerCase(Locale.ROOT); } catch (Throwable e) { return null; } });
    }

    private static void registerBannerPatterns() {
        PolyTypeRegistry.define("BannerPatternsComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.level.block.entity.BannerPatternLayers) obj).layers().size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerMapId() {
        PolyTypeRegistry.define("MapIdComponent")
            .propertyTyped("id",    TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.level.saveddata.maps.MapId) obj).id(); } catch (Throwable e) { return 0.0; } })
            .propertyTyped("value", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.level.saveddata.maps.MapId) obj).id(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerFireworkExplosion() {
        PolyTypeRegistry.define("FireworkExplosionComponent")
            .propertyTyped("has_trail",   TypeCodecs.BOOL,   (Object obj) -> { try { return ((net.minecraft.world.item.component.FireworkExplosion) obj).hasTrail(); } catch (Throwable e) { return false; } })
            .propertyTyped("has_twinkle", TypeCodecs.BOOL,   (Object obj) -> { try { return ((net.minecraft.world.item.component.FireworkExplosion) obj).hasTwinkle(); } catch (Throwable e) { return false; } })
            .propertyTyped("shape",       TypeCodecs.STRING, (Object obj) -> { try { return ((net.minecraft.world.item.component.FireworkExplosion) obj).shape().getSerializedName(); } catch (Throwable e) { return null; } });
    }

    private static void registerFireworks() {
        PolyTypeRegistry.define("FireworksComponent")
            .propertyTyped("flight_duration", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.Fireworks) obj).flightDuration(); } catch (Throwable e) { return 1.0; } })
            .propertyTyped("explosion_count", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.Fireworks) obj).explosions().size(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerTrim() {
        PolyTypeRegistry.define("TrimComponent")
            .propertyTyped("material", TypeCodecs.STRING, (Object obj) -> {
                try {
                    var t = (net.minecraft.world.item.equipment.trim.ArmorTrim) obj;
                    return t.material().unwrapKey().map(Object::toString).orElse("?");
                } catch (Throwable e) { return null; }
            })
            .propertyTyped("pattern", TypeCodecs.STRING, (Object obj) -> {
                try {
                    var t = (net.minecraft.world.item.equipment.trim.ArmorTrim) obj;
                    return t.pattern().unwrapKey().map(Object::toString).orElse("?");
                } catch (Throwable e) { return null; }
            });
    }

    private static void registerLodestoneTracker() {
        PolyTypeRegistry.define("LodestoneTrackerComponent")
            .propertyTyped("tracked",    TypeCodecs.BOOL, (Object obj) -> { try { return ((net.minecraft.world.item.component.LodestoneTracker) obj).tracked(); } catch (Throwable e) { return false; } })
            .propertyTyped("has_target", TypeCodecs.BOOL, (Object obj) -> { try { return ((net.minecraft.world.item.component.LodestoneTracker) obj).target().isPresent(); } catch (Throwable e) { return false; } });
    }

    private static void registerSuspiciousStewEffects() {
        PolyTypeRegistry.define("SuspiciousStewEffectsComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> { try { return (double) ((net.minecraft.world.item.component.SuspiciousStewEffects) obj).effects().size(); } catch (Throwable e) { return 0.0; } });
    }

    private static void registerBees() {
        PolyTypeRegistry.define("BeesComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((java.util.List<?>) obj).size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerBlockState() {
        PolyTypeRegistry.define("BlockStateComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.item.component.BlockItemStateProperties) obj).properties().size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerInstrument() {
        PolyTypeRegistry.define("InstrumentComponent")
            .propertyTyped("id", TypeCodecs.STRING, (Object obj) -> {
                try {
                    var h = (net.minecraft.core.Holder<?>) obj;
                    return h.unwrapKey().map(Object::toString).orElse("?");
                } catch (Throwable e) { return null; }
            });
    }

    private static void registerJukeboxPlayable() {
        PolyTypeRegistry.define("JukeboxPlayableComponent")
            .propertyTyped("show_in_tooltip", TypeCodecs.BOOL, (Object obj) -> true);
    }

    private static void registerDeathProtection() {
        PolyTypeRegistry.define("DeathProtectionComponent")
            .propertyTyped("effect_count", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.item.component.DeathProtection) obj).deathEffects().size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerPotDecorations() {
        PolyTypeRegistry.define("PotDecorationsComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> 4.0);
    }

    private static void registerMapDecorations() {
        PolyTypeRegistry.define("MapDecorationsComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((net.minecraft.world.item.component.MapDecorations) obj).decorations().size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    private static void registerTropicalFishPattern() {
        PolyTypeRegistry.define("TropicalFishPatternComponent")
            .propertyTyped("value", TypeCodecs.STRING, (Object obj) -> { try { return obj.toString(); } catch (Throwable e) { return null; } });
    }

    private static void registerRecipes() {
        PolyTypeRegistry.define("RecipesComponent")
            .propertyTyped("size", TypeCodecs.DOUBLE, (Object obj) -> {
                try { return (double) ((java.util.List<?>) obj).size(); }
                catch (Throwable e) { return 0.0; }
            });
    }

    /** Register a marker component that has no data (presence = true). */
    @SuppressWarnings("rawtypes")
    private static void registerMarker(String typeName, net.minecraft.core.component.DataComponentType type) {
        PolyTypeRegistry.define(typeName)
            .propertyTyped("value", TypeCodecs.BOOL, (Object obj) -> true);
    }

    /** Register any component as opaque (just wraps the NMS value, limited property access). */
    @SuppressWarnings("rawtypes")
    private static void registerOpaque(String typeName, net.minecraft.core.component.DataComponentType type) {
        PolyTypeRegistry.define(typeName)
            .propertyTyped("value", TypeCodecs.BOOL, (Object obj) -> obj != null);
    }

    /** Try to register an opaque component by field name (handles missing 1.21.4+ components). */
    private static void tryRegisterOpaque(String typeName, String fieldName) {
        try {
            var field = DataComponents.class.getDeclaredField(fieldName);
            @SuppressWarnings("unchecked")
            var type = (net.minecraft.core.component.DataComponentType<?>) field.get(null);
            registerOpaque(typeName, type);
        } catch (Throwable ignored) {}
    }

    // =========================================================================
    // SCRIPT BUILTINS (constructors)
    // =========================================================================

    private static void registerBuiltins() {
        // CustomModelDataComponent(value_or_list)
        ScriptBuiltins.register("CustomModelDataComponent", (args, ctx) -> {
            try {
                if (args.isEmpty()) return ScriptValue.ofObj("CustomModelDataComponent",
                    new net.minecraft.world.item.component.CustomModelData(List.of(), List.of(), List.of(), List.of()));
                ScriptValue a = args.get(0);
                if (a instanceof ScriptValue.Num) {
                    float f = (float) a.asNum();
                    return ScriptValue.ofObj("CustomModelDataComponent",
                        new net.minecraft.world.item.component.CustomModelData(List.of(f), List.of(), List.of(), List.of()));
                }
                if (a instanceof ScriptValue.Array arr) {
                    var floats = arr.elements().stream().map(v -> (float) v.asNum()).toList();
                    return ScriptValue.ofObj("CustomModelDataComponent",
                        new net.minecraft.world.item.component.CustomModelData(floats, List.of(), List.of(), List.of()));
                }
            } catch (Throwable ignored) {}
            return ScriptValue.NULL;
        });

        // LoreComponent(line1, line2, ...)
        ScriptBuiltins.register("LoreComponent", (args, ctx) -> {
            try {
                var lines = new java.util.ArrayList<net.minecraft.network.chat.Component>();
                for (var arg : args) {
                    String text = arg.asStr();
                    try {
                        net.kyori.adventure.text.Component adv = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
                        lines.add((net.minecraft.network.chat.Component) io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
                    } catch (Throwable e2) {
                        lines.add(net.minecraft.network.chat.Component.literal(text));
                    }
                }
                return ScriptValue.ofObj("LoreComponent", new net.minecraft.world.item.component.ItemLore(lines));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // ItemNameComponent(text)
        ScriptBuiltins.register("ItemNameComponent", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            try {
                net.kyori.adventure.text.Component adv = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(args.get(0).asStr());
                return ScriptValue.ofObj("ItemNameComponent", io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // CustomNameComponent(text)
        ScriptBuiltins.register("CustomNameComponent", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            try {
                net.kyori.adventure.text.Component adv = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(args.get(0).asStr());
                return ScriptValue.ofObj("CustomNameComponent", io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // RarityComponent(name)  e.g. RarityComponent("rare")
        ScriptBuiltins.register("RarityComponent", (args, ctx) -> {
            try {
                String name = args.isEmpty() ? "common" : args.get(0).asStr().toUpperCase(Locale.ROOT);
                return ScriptValue.ofObj("RarityComponent", Rarity.valueOf(name));
            } catch (Throwable ignored) { return ScriptValue.ofObj("RarityComponent", Rarity.COMMON); }
        });

        // MaxStackSizeComponent(n)
        ScriptBuiltins.register("MaxStackSizeComponent", (args, ctx) -> {
            int n = args.isEmpty() ? 64 : (int) args.get(0).asNum();
            return ScriptValue.ofObj("MaxStackSizeComponent", Integer.valueOf(n));
        });
        // DamageComponent(n), MaxDamageComponent(n), RepairCostComponent(n), OminousBottleAmplifierComponent(n)
        ScriptBuiltins.register("DamageComponent",               (args, ctx) -> ScriptValue.ofObj("DamageComponent", (int)(args.isEmpty()?0:args.get(0).asNum())));
        ScriptBuiltins.register("MaxDamageComponent",            (args, ctx) -> ScriptValue.ofObj("MaxDamageComponent", (int)(args.isEmpty()?0:args.get(0).asNum())));
        ScriptBuiltins.register("RepairCostComponent",           (args, ctx) -> ScriptValue.ofObj("RepairCostComponent", (int)(args.isEmpty()?0:args.get(0).asNum())));
        ScriptBuiltins.register("OminousBottleAmplifierComponent",(args, ctx) -> ScriptValue.ofObj("OminousBottleAmplifierComponent", (int)(args.isEmpty()?0:args.get(0).asNum())));
        ScriptBuiltins.register("PotionDurationScaleComponent",  (args, ctx) -> ScriptValue.ofObj("PotionDurationScaleComponent", (float)(args.isEmpty()?1.0:args.get(0).asNum())));
        ScriptBuiltins.register("EnchantmentGlintOverrideComponent",(args,ctx)-> ScriptValue.ofObj("EnchantmentGlintOverrideComponent", args.isEmpty()||args.get(0).asBool()));

        // UnbreakableComponent(show_in_tooltip?)
        ScriptBuiltins.register("UnbreakableComponent", (args, ctx) -> {
            boolean show = args.isEmpty() || args.get(0).asBool();
            try { return ScriptValue.ofObj("UnbreakableComponent", net.minecraft.util.Unit.INSTANCE); }
            catch (Throwable e) { return ScriptValue.NULL; }
        });

        // DyedColorComponent(rgb, show_in_tooltip?)
        ScriptBuiltins.register("DyedColorComponent", (args, ctx) -> {
            try {
                int rgb = args.isEmpty() ? 0xFFFFFF : (int) args.get(0).asNum();
                boolean show = args.size() < 2 || args.get(1).asBool();
                return ScriptValue.ofObj("DyedColorComponent", new net.minecraft.world.item.component.DyedItemColor(rgb));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // MapColorComponent(rgb)
        ScriptBuiltins.register("MapColorComponent", (args, ctx) -> {
            try {
                int rgb = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                return ScriptValue.ofObj("MapColorComponent", new net.minecraft.world.item.component.MapItemColor(rgb));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // TooltipDisplayComponent(hide_tooltip)
        ScriptBuiltins.register("TooltipDisplayComponent", (args, ctx) -> {
            try {
                boolean hide = !args.isEmpty() && args.get(0).asBool();
                return ScriptValue.ofObj("TooltipDisplayComponent",
                    new net.minecraft.world.item.component.TooltipDisplay(hide, new java.util.LinkedHashSet<>()));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // FoodComponent(nutrition, saturation, can_always_eat?)
        ScriptBuiltins.register("FoodComponent", (args, ctx) -> {
            try {
                int nutrition = args.isEmpty() ? 0 : (int) args.get(0).asNum();
                float saturation = args.size() < 2 ? 0f : (float) args.get(1).asNum();
                boolean canAlways = args.size() >= 3 && args.get(2).asBool();
                return ScriptValue.ofObj("FoodComponent",
                    new net.minecraft.world.food.FoodProperties(nutrition, saturation, canAlways));
            } catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // ItemModelComponent(id)
        ScriptBuiltins.register("ItemModelComponent", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            try { return ScriptValue.ofObj("ItemModelComponent", net.minecraft.resources.Identifier.parse(args.get(0).asStr())); }
            catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // NoteBlockSoundComponent(sound_id)
        ScriptBuiltins.register("NoteBlockSoundComponent", (args, ctx) -> {
            if (args.isEmpty()) return ScriptValue.NULL;
            try { return ScriptValue.ofObj("NoteBlockSoundComponent", net.minecraft.resources.Identifier.parse(args.get(0).asStr())); }
            catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // MapIdComponent(id)
        ScriptBuiltins.register("MapIdComponent", (args, ctx) -> {
            try { return ScriptValue.ofObj("MapIdComponent", new net.minecraft.world.level.saveddata.maps.MapId((int)(args.isEmpty()?0:args.get(0).asNum()))); }
            catch (Throwable ignored) { return ScriptValue.NULL; }
        });

        // Marker constructors
        ScriptBuiltins.register("GliderComponent",               (args, ctx) -> getMarker("GliderComponent", DataComponents.GLIDER));
        ScriptBuiltins.register("IntangibleProjectileComponent",  (args, ctx) -> getMarker("IntangibleProjectileComponent", DataComponents.INTANGIBLE_PROJECTILE));
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    private static ScriptValue getMarker(String name, net.minecraft.core.component.DataComponentType type) {
        try {
            var field = type.getClass().getDeclaredField("defaultValue");
            field.setAccessible(true);
            return ScriptValue.ofObj(name, field.get(type));
        } catch (Throwable e) {
            return ScriptValue.ofObj(name, Boolean.TRUE); // fallback
        }
    }

    // =========================================================================
    // READ / WRITE / JSON HELPERS
    // =========================================================================

    /** Get a component from an NMS ItemStack as a typed ScriptValue.Obj, or NULL if absent. */
    public static ScriptValue getComponent(ItemStack stack, String rawName) {
        String name = rawName.replace("minecraft:", "").replace("-", "_").toLowerCase(Locale.ROOT);
        try { return switch (name) {
            case "custom_model_data"         -> wrap(stack, DataComponents.CUSTOM_MODEL_DATA, "CustomModelDataComponent");
            case "lore"                      -> wrap(stack, DataComponents.LORE,                    "LoreComponent");
            case "custom_data"               -> wrap(stack, DataComponents.CUSTOM_DATA,             "CustomDataComponent");
            case "custom_name"               -> wrap(stack, DataComponents.CUSTOM_NAME,             "CustomNameComponent");
            case "item_name"                 -> wrap(stack, DataComponents.ITEM_NAME,               "ItemNameComponent");
            case "item_model"                -> wrap(stack, DataComponents.ITEM_MODEL,              "ItemModelComponent");
            case "rarity"                    -> wrap(stack, DataComponents.RARITY,                  "RarityComponent");
            case "damage"                    -> wrap(stack, DataComponents.DAMAGE,                  "DamageComponent");
            case "max_damage"                -> wrap(stack, DataComponents.MAX_DAMAGE,              "MaxDamageComponent");
            case "max_stack_size"            -> wrap(stack, DataComponents.MAX_STACK_SIZE,          "MaxStackSizeComponent");
            case "repair_cost"               -> wrap(stack, DataComponents.REPAIR_COST,             "RepairCostComponent");
            case "ominous_bottle_amplifier"  -> wrap(stack, DataComponents.OMINOUS_BOTTLE_AMPLIFIER,"OminousBottleAmplifierComponent");
            case "enchantment_glint_override"-> wrap(stack, DataComponents.ENCHANTMENT_GLINT_OVERRIDE,"EnchantmentGlintOverrideComponent");
            case "enchantments"              -> wrap(stack, DataComponents.ENCHANTMENTS,            "EnchantmentsComponent");
            case "stored_enchantments"       -> wrap(stack, DataComponents.STORED_ENCHANTMENTS,     "StoredEnchantmentsComponent");
            case "food"                      -> wrap(stack, DataComponents.FOOD,                    "FoodComponent");
            case "tool"                      -> wrap(stack, DataComponents.TOOL,                    "ToolComponent");
            case "enchantable"               -> wrap(stack, DataComponents.ENCHANTABLE,             "EnchantableComponent");
            case "dyed_color"                -> wrap(stack, DataComponents.DYED_COLOR,              "DyedColorComponent");
            case "map_color"                 -> wrap(stack, DataComponents.MAP_COLOR,               "MapColorComponent");
            case "tooltip_display"           -> wrap(stack, DataComponents.TOOLTIP_DISPLAY,         "TooltipDisplayComponent");
            case "unbreakable"               -> wrap(stack, DataComponents.UNBREAKABLE,             "UnbreakableComponent");
            case "damage_resistant"          -> wrap(stack, DataComponents.DAMAGE_RESISTANT,        "DamageResistantComponent");
            case "attribute_modifiers"       -> wrap(stack, DataComponents.ATTRIBUTE_MODIFIERS,     "AttributeModifiersComponent");
            case "potion_contents"           -> wrap(stack, DataComponents.POTION_CONTENTS,         "PotionContentsComponent");
            case "potion_duration_scale"     -> wrap(stack, DataComponents.POTION_DURATION_SCALE,   "PotionDurationScaleComponent");
            case "bundle_contents"           -> wrap(stack, DataComponents.BUNDLE_CONTENTS,         "BundleContentsComponent");
            case "container"                 -> wrap(stack, DataComponents.CONTAINER,               "ContainerComponent");
            case "charged_projectiles"       -> wrap(stack, DataComponents.CHARGED_PROJECTILES,     "ChargedProjectilesComponent");
            case "written_book_content"      -> wrap(stack, DataComponents.WRITTEN_BOOK_CONTENT,    "WrittenBookContentComponent");
            case "writable_book_content"     -> wrap(stack, DataComponents.WRITABLE_BOOK_CONTENT,   "WritableBookContentComponent");
            case "profile"                   -> wrap(stack, DataComponents.PROFILE,                 "ProfileComponent");
            case "note_block_sound"          -> wrap(stack, DataComponents.NOTE_BLOCK_SOUND,        "NoteBlockSoundComponent");
            case "base_color"                -> wrap(stack, DataComponents.BASE_COLOR,              "BaseColorComponent");
            case "banner_patterns"           -> wrap(stack, DataComponents.BANNER_PATTERNS,         "BannerPatternsComponent");
            case "map_id"                    -> wrap(stack, DataComponents.MAP_ID,                  "MapIdComponent");
            case "firework_explosion"        -> wrap(stack, DataComponents.FIREWORK_EXPLOSION,      "FireworkExplosionComponent");
            case "fireworks"                 -> wrap(stack, DataComponents.FIREWORKS,               "FireworksComponent");
            case "trim"                      -> wrap(stack, DataComponents.TRIM,                    "TrimComponent");
            case "lodestone_tracker"         -> wrap(stack, DataComponents.LODESTONE_TRACKER,       "LodestoneTrackerComponent");
            case "suspicious_stew_effects"   -> wrap(stack, DataComponents.SUSPICIOUS_STEW_EFFECTS, "SuspiciousStewEffectsComponent");
            case "bees"                      -> wrap(stack, DataComponents.BEES,                    "BeesComponent");
            case "block_state"               -> wrap(stack, DataComponents.BLOCK_STATE,             "BlockStateComponent");
            case "instrument"                -> wrap(stack, DataComponents.INSTRUMENT,              "InstrumentComponent");
            case "jukebox_playable"          -> wrap(stack, DataComponents.JUKEBOX_PLAYABLE,        "JukeboxPlayableComponent");
            case "death_protection"          -> wrap(stack, DataComponents.DEATH_PROTECTION,        "DeathProtectionComponent");
            case "pot_decorations"           -> wrap(stack, DataComponents.POT_DECORATIONS,         "PotDecorationsComponent");
            case "map_decorations"           -> wrap(stack, DataComponents.MAP_DECORATIONS,         "MapDecorationsComponent");
            case "tropical_fish_pattern"     -> wrap(stack, DataComponents.TROPICAL_FISH_PATTERN,   "TropicalFishPatternComponent");
            case "recipes"                   -> wrap(stack, DataComponents.RECIPES,                 "RecipesComponent");
            case "entity_data"               -> wrap(stack, DataComponents.ENTITY_DATA,             "EntityDataComponent");
            case "block_entity_data"         -> wrap(stack, DataComponents.BLOCK_ENTITY_DATA,       "BlockEntityDataComponent");
            case "bucket_entity_data"        -> wrap(stack, DataComponents.BUCKET_ENTITY_DATA,      "BucketEntityDataComponent");
            case "container_loot"            -> wrap(stack, DataComponents.CONTAINER_LOOT,          "ContainerLootComponent");
            case "lock"                      -> wrap(stack, DataComponents.LOCK,                    "LockComponent");
            case "can_break"                 -> wrap(stack, DataComponents.CAN_BREAK,               "CanBreakComponent");
            case "can_place_on"              -> wrap(stack, DataComponents.CAN_PLACE_ON,            "CanPlaceOnComponent");
            case "glider"                    -> hasMarker(stack, DataComponents.GLIDER,             "GliderComponent");
            case "intangible_projectile"     -> hasMarker(stack, DataComponents.INTANGIBLE_PROJECTILE,"IntangibleProjectileComponent");
            // 1.21.4+ — try via reflection
            default                          -> tryGetByFieldName(stack, name);
        };
        } catch (Throwable e) { return ScriptValue.NULL; }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static <T> ScriptValue wrap(ItemStack stack, net.minecraft.core.component.DataComponentType<T> type, String typeName) {
        T v = stack.get(type);
        return v != null ? ScriptValue.ofObj(typeName, v) : ScriptValue.NULL;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static ScriptValue hasMarker(ItemStack stack, net.minecraft.core.component.DataComponentType<?> type, String typeName) {
        return stack.has(type) ? ScriptValue.ofObj(typeName, Boolean.TRUE) : ScriptValue.NULL;
    }

    private static ScriptValue tryGetByFieldName(ItemStack stack, String snakeName) {
        try {
            String fieldName = snakeName.toUpperCase(Locale.ROOT);
            var field = DataComponents.class.getDeclaredField(fieldName);
            @SuppressWarnings("unchecked")
            var type = (net.minecraft.core.component.DataComponentType<Object>) field.get(null);
            var v = stack.get(type);
            if (v == null) return ScriptValue.NULL;
            String typeName = toPascalCase(snakeName) + "Component";
            return ScriptValue.ofObj(typeName, v);
        } catch (Throwable ignored) { return ScriptValue.NULL; }
    }

    private static String toPascalCase(String snake) {
        var sb = new StringBuilder();
        for (String part : snake.split("_")) {
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase(Locale.ROOT));
        }
        return sb.toString();
    }

    /** Apply a typed component ScriptValue.Obj to a copy of the NMS stack. */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static ItemStack setComponent(ItemStack stack, ScriptValue comp) {
        if (!(comp instanceof ScriptValue.Obj o)) return stack;
        ItemStack copy = stack.copy();
        try { switch (o.typeName()) {
            case "CustomModelDataComponent"         -> copy.set(DataComponents.CUSTOM_MODEL_DATA,           (net.minecraft.world.item.component.CustomModelData) o.instance());
            case "LoreComponent"                    -> copy.set(DataComponents.LORE,                        (net.minecraft.world.item.component.ItemLore) o.instance());
            case "CustomDataComponent"              -> copy.set(DataComponents.CUSTOM_DATA,                  (net.minecraft.world.item.component.CustomData) o.instance());
            case "CustomNameComponent"              -> copy.set(DataComponents.CUSTOM_NAME,                  (net.minecraft.network.chat.Component) o.instance());
            case "ItemNameComponent"                -> copy.set(DataComponents.ITEM_NAME,                    (net.minecraft.network.chat.Component) o.instance());
            case "ItemModelComponent"               -> copy.set(DataComponents.ITEM_MODEL,                   (net.minecraft.resources.Identifier) o.instance());
            case "RarityComponent"                  -> copy.set(DataComponents.RARITY,                       (Rarity) o.instance());
            case "DamageComponent"                  -> copy.set(DataComponents.DAMAGE,                       (Integer) o.instance());
            case "MaxDamageComponent"               -> copy.set(DataComponents.MAX_DAMAGE,                   (Integer) o.instance());
            case "MaxStackSizeComponent"            -> copy.set(DataComponents.MAX_STACK_SIZE,               (Integer) o.instance());
            case "RepairCostComponent"              -> copy.set(DataComponents.REPAIR_COST,                  (Integer) o.instance());
            case "OminousBottleAmplifierComponent"  -> copy.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new net.minecraft.world.item.component.OminousBottleAmplifier((Integer) o.instance()));
            case "EnchantmentGlintOverrideComponent"-> copy.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,   (Boolean) o.instance());
            case "FoodComponent"                    -> copy.set(DataComponents.FOOD,                         (net.minecraft.world.food.FoodProperties) o.instance());
            case "ToolComponent"                    -> copy.set(DataComponents.TOOL,                         (net.minecraft.world.item.component.Tool) o.instance());
            case "EnchantableComponent"             -> copy.set(DataComponents.ENCHANTABLE,                  (net.minecraft.world.item.enchantment.Enchantable) o.instance());
            case "DyedColorComponent"               -> copy.set(DataComponents.DYED_COLOR,                   (net.minecraft.world.item.component.DyedItemColor) o.instance());
            case "MapColorComponent"                -> copy.set(DataComponents.MAP_COLOR,                    (net.minecraft.world.item.component.MapItemColor) o.instance());
            case "TooltipDisplayComponent"          -> copy.set(DataComponents.TOOLTIP_DISPLAY,              (net.minecraft.world.item.component.TooltipDisplay) o.instance());
            case "UnbreakableComponent"             -> copy.set(DataComponents.UNBREAKABLE,                  (net.minecraft.util.Unit) o.instance());
            case "DamageResistantComponent"         -> copy.set(DataComponents.DAMAGE_RESISTANT,             (net.minecraft.world.item.component.DamageResistant) o.instance());
            case "AttributeModifiersComponent"      -> copy.set(DataComponents.ATTRIBUTE_MODIFIERS,          (net.minecraft.world.item.component.ItemAttributeModifiers) o.instance());
            case "PotionContentsComponent"          -> copy.set(DataComponents.POTION_CONTENTS,              (net.minecraft.world.item.alchemy.PotionContents) o.instance());
            case "PotionDurationScaleComponent"     -> copy.set(DataComponents.POTION_DURATION_SCALE,        (Float) o.instance());
            case "BundleContentsComponent"          -> copy.set(DataComponents.BUNDLE_CONTENTS,              (net.minecraft.world.item.component.BundleContents) o.instance());
            case "ContainerComponent"               -> copy.set(DataComponents.CONTAINER,                    (net.minecraft.world.item.component.ItemContainerContents) o.instance());
            case "ChargedProjectilesComponent"      -> copy.set(DataComponents.CHARGED_PROJECTILES,          (net.minecraft.world.item.component.ChargedProjectiles) o.instance());
            case "WrittenBookContentComponent"      -> copy.set(DataComponents.WRITTEN_BOOK_CONTENT,         (net.minecraft.world.item.component.WrittenBookContent) o.instance());
            case "WritableBookContentComponent"     -> copy.set(DataComponents.WRITABLE_BOOK_CONTENT,        (net.minecraft.world.item.component.WritableBookContent) o.instance());
            case "ProfileComponent"                 -> copy.set(DataComponents.PROFILE,                      (net.minecraft.world.item.component.ResolvableProfile) o.instance());
            case "NoteBlockSoundComponent"          -> copy.set(DataComponents.NOTE_BLOCK_SOUND,             (net.minecraft.resources.Identifier) o.instance());
            case "MapIdComponent"                   -> copy.set(DataComponents.MAP_ID,                       (net.minecraft.world.level.saveddata.maps.MapId) o.instance());
            case "FireworkExplosionComponent"       -> copy.set(DataComponents.FIREWORK_EXPLOSION,           (net.minecraft.world.item.component.FireworkExplosion) o.instance());
            case "FireworksComponent"               -> copy.set(DataComponents.FIREWORKS,                    (net.minecraft.world.item.component.Fireworks) o.instance());
            case "TrimComponent"                    -> copy.set(DataComponents.TRIM,                         (net.minecraft.world.item.equipment.trim.ArmorTrim) o.instance());
            case "LodestoneTrackerComponent"        -> copy.set(DataComponents.LODESTONE_TRACKER,            (net.minecraft.world.item.component.LodestoneTracker) o.instance());
            case "SuspiciousStewEffectsComponent"   -> copy.set(DataComponents.SUSPICIOUS_STEW_EFFECTS,      (net.minecraft.world.item.component.SuspiciousStewEffects) o.instance());
            case "InstrumentComponent"              -> copy.set(DataComponents.INSTRUMENT, new net.minecraft.world.item.component.InstrumentComponent((net.minecraft.world.item.EitherHolder<net.minecraft.world.item.Instrument>) null));
            case "JukeboxPlayableComponent"         -> copy.set(DataComponents.JUKEBOX_PLAYABLE,             (net.minecraft.world.item.JukeboxPlayable) o.instance());
            case "DeathProtectionComponent"         -> copy.set(DataComponents.DEATH_PROTECTION,             (net.minecraft.world.item.component.DeathProtection) o.instance());
            default                                 -> trySetByTypeName(copy, o.typeName(), o.instance());
        }} catch (Throwable ignored) {}
        return copy;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private static void trySetByTypeName(ItemStack stack, String typeName, Object value) {
        try {
            String snakeName = toSnakeCase(typeName.replace("Component", ""));
            var field = DataComponents.class.getDeclaredField(snakeName.toUpperCase(Locale.ROOT));
            var type = (net.minecraft.core.component.DataComponentType) field.get(null);
            stack.set(type, value);
        } catch (Throwable ignored) {}
    }

    private static String toSnakeCase(String pascal) {
        return pascal.replaceAll("([A-Z])", "_$1").toLowerCase(Locale.ROOT).replaceFirst("^_", "");
    }

    /** Remove a component from a copy of the stack by name. */
    public static ItemStack removeComponent(ItemStack stack, String rawName) {
        String name = rawName.replace("minecraft:", "").replace("-", "_").toLowerCase(Locale.ROOT);
        ItemStack copy = stack.copy();
        try {
            var field = DataComponents.class.getDeclaredField(name.toUpperCase(Locale.ROOT));
            @SuppressWarnings("unchecked")
            var type = (net.minecraft.core.component.DataComponentType<Object>) field.get(null);
            copy.remove(type);
        } catch (Throwable ignored) {}
        return copy;
    }

    /** Check if a stack has a component by name. */
    public static boolean hasComponent(ItemStack stack, String rawName) {
        String name = rawName.replace("minecraft:", "").replace("-", "_").toLowerCase(Locale.ROOT);
        try {
            var field = DataComponents.class.getDeclaredField(name.toUpperCase(Locale.ROOT));
            @SuppressWarnings("unchecked")
            var type = (net.minecraft.core.component.DataComponentType<Object>) field.get(null);
            return stack.has(type);
        } catch (Throwable ignored) { return false; }
    }

    /**
     * Apply a JSON components map (from ItemSpec) to a copy of the NMS stack.
     * Keys are Minecraft component names (with or without "minecraft:" prefix).
     * Values are JSON-decoded primitives: Number, Boolean, String, Map, or null.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static ItemStack applyJsonComponents(ItemStack stack, Map<String, Object> comps) {
        if (comps == null || comps.isEmpty()) return stack;
        ItemStack copy = stack.copy();
        for (var entry : comps.entrySet()) {
            String key = entry.getKey().replace("minecraft:", "").replace("-", "_").toLowerCase(Locale.ROOT);
            Object val = entry.getValue();
            try { switch (key) {
                case "custom_model_data" -> {
                    float f = val instanceof Number n ? n.floatValue() : 0f;
                    copy.set(DataComponents.CUSTOM_MODEL_DATA,
                        new net.minecraft.world.item.component.CustomModelData(List.of(f), List.of(), List.of(), List.of()));
                }
                case "max_stack_size"             -> copy.set(DataComponents.MAX_STACK_SIZE,               val instanceof Number n ? n.intValue() : 64);
                case "damage"                     -> copy.set(DataComponents.DAMAGE,                       val instanceof Number n ? n.intValue() : 0);
                case "max_damage"                 -> copy.set(DataComponents.MAX_DAMAGE,                   val instanceof Number n ? n.intValue() : 0);
                case "repair_cost"                -> copy.set(DataComponents.REPAIR_COST,                  val instanceof Number n ? n.intValue() : 0);
                case "ominous_bottle_amplifier"   -> copy.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new net.minecraft.world.item.component.OminousBottleAmplifier(val instanceof Number n ? n.intValue() : 0));
                case "enchantment_glint_override" -> copy.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE,   val instanceof Boolean b ? b : Boolean.TRUE);
                case "unbreakable"                -> {
                    boolean show = !(val instanceof Map) || ((Map<?,?>) val).isEmpty();
                    copy.set(DataComponents.UNBREAKABLE, net.minecraft.util.Unit.INSTANCE);
                }
                case "hide_tooltip", "tooltip_display" -> {
                    boolean hide = val instanceof Boolean b ? b : Boolean.TRUE;
                    copy.set(DataComponents.TOOLTIP_DISPLAY, new net.minecraft.world.item.component.TooltipDisplay(hide, new java.util.LinkedHashSet<>()));
                }
                case "item_name" -> {
                    String text = String.valueOf(val);
                    try {
                        net.kyori.adventure.text.Component adv = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
                        copy.set(DataComponents.ITEM_NAME, (net.minecraft.network.chat.Component) io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
                    } catch (Throwable e2) { copy.set(DataComponents.ITEM_NAME, net.minecraft.network.chat.Component.literal(text)); }
                }
                case "custom_name" -> {
                    String text = String.valueOf(val);
                    try {
                        net.kyori.adventure.text.Component adv = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
                        copy.set(DataComponents.CUSTOM_NAME, (net.minecraft.network.chat.Component) io.papermc.paper.adventure.PaperAdventure.asVanilla(adv));
                    } catch (Throwable e2) { copy.set(DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal(text)); }
                }
                case "rarity" -> {
                    try { copy.set(DataComponents.RARITY, Rarity.valueOf(String.valueOf(val).toUpperCase(Locale.ROOT))); }
                    catch (Throwable ignored2) {}
                }
                case "dyed_color" -> {
                    int rgb = val instanceof Number n ? n.intValue() : val instanceof Map m && m.containsKey("rgb") ? ((Number) m.get("rgb")).intValue() : 0xFFFFFF;
                    copy.set(DataComponents.DYED_COLOR, new net.minecraft.world.item.component.DyedItemColor(rgb));
                }
                case "map_color" -> {
                    int rgb = val instanceof Number n ? n.intValue() : 0;
                    copy.set(DataComponents.MAP_COLOR, new net.minecraft.world.item.component.MapItemColor(rgb));
                }
                case "item_model" -> {
                    copy.set(DataComponents.ITEM_MODEL, net.minecraft.resources.Identifier.parse(String.valueOf(val)));
                }
                case "note_block_sound" -> {
                    copy.set(DataComponents.NOTE_BLOCK_SOUND, net.minecraft.resources.Identifier.parse(String.valueOf(val)));
                }
                case "glider" -> {
                    if (val instanceof Boolean b && b || !(val instanceof Boolean)) {
                        try { copy.set(DataComponents.GLIDER, net.minecraft.util.Unit.INSTANCE); } catch (Throwable ignored2) {}
                    }
                }
                case "intangible_projectile" -> {
                    try { copy.set(DataComponents.INTANGIBLE_PROJECTILE, net.minecraft.util.Unit.INSTANCE); } catch (Throwable ignored2) {}
                }
                default -> {} // Unknown — skip
            }} catch (Throwable ignored) {}
        }
        return copy;
    }
}
