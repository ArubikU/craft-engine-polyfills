package dev.arubik.craftengine.fluid;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.mojang.datafixers.util.Pair;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.momirealms.craftengine.core.util.Key;

/**
 * A kind of liquid.
 *
 * <p>
 * This used to be a closed {@code enum}, which meant every numeric property
 * (mB per block, transfer delays, which pack model family renders it) lived in
 * a {@code switch} that had to be edited and recompiled in lockstep. It is now
 * an identity in {@link #REGISTRY} carrying a swappable
 * {@link FluidProperties} block loaded from {@code fluid_types/*.json}, so
 * server owners can retune the built-ins and add liquids of their own.
 *
 * <p>
 * The built-in constants below still exist and are still compared with
 * {@code ==}: their <em>identity</em> is fixed at class-load so live block
 * entities and save data keep resolving, while their <em>properties</em> are
 * replaced in place on every reload. That is also why the registry is declared
 * identity-stable — see {@link Registry#clearsOnReload()}.
 *
 * <p>
 * The legacy {@link #name()} (e.g. {@code "WATER"}) is what
 * {@link FluidKeys} writes to disk, so it must stay stable for the built-ins.
 */
public final class FluidType {

    /** Default namespace for fluid ids, so data files may write a bare {@code "water"}. */
    public static final String NAMESPACE = "polyfills";

    /**
     * Every known liquid. Identity-stable: entries survive a reload and only their
     * properties are swapped, because loaded chunks hold references to them.
     */
    public static final Registry<FluidType> REGISTRY = Registries.create("fluid_type", false);

    public static final int MB_PER_BUCKET = 1000;

    /**
     * The tunable half of a liquid. Everything here is overridable per fluid from
     * {@code fluid_types/<id>.json}.
     *
     * @param unitMb            mB in one "item unit" of this fluid (a slime ball, a
     *                          honey bottle, one XP point)
     * @param mbPerFullBlock    mB needed to place one full block of it in the world
     * @param blockCollectDelay ticks between pulls when harvesting from world blocks
     * @param carrierIoDelay    ticks between pushes/pulls between carriers, which is
     *                          cheaper than harvesting
     * @param renderFamily      which pack model family draws it — the item id
     *                          template is {@code fluidlvl_<family>_<0..15>}
     * @param color             ARGB tint for tooltips and particles
     * @param density           relative to water; the network solver uses it for
     *                          gravity head
     * @param viscosity         relative to water; scales pipe conductance
     * @param fillSound         sound id played when pouring INTO a tank
     * @param drainSound        sound id played when drawing OUT of a tank
     * @param vanillaFluid      the {@code minecraft:} fluid this maps onto in the
     *                          world, or {@code null} if it has no world form
     * @param tankVariant       the value written to a tank's {@code fluidtype}
     *                          blockstate to pick its appearance. Plain text, because
     *                          the tank declares that property as CraftEngine's
     *                          built-in {@code string} type whose value set lives in
     *                          the block config — so a pack adds a look by listing one
     *                          more value, with no Java change
     * @param translationKey    client-side i18n key
     */
    public record FluidProperties(
            int unitMb,
            int mbPerFullBlock,
            int blockCollectDelay,
            int carrierIoDelay,
            String renderFamily,
            int color,
            double density,
            double viscosity,
            String fillSound,
            String drainSound,
            Key vanillaFluid,
            String tankVariant,
            String translationKey,
            String barItemTemplate,
            int barLevels) {

        /** Neutral defaults, used as the base a data file overrides field by field. */
        public static FluidProperties defaults(String path) {
            return new FluidProperties(MB_PER_BUCKET, MB_PER_BUCKET, 4, 2, "water", 0xFFFFFFFF, 1.0, 1.0,
                    "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "water",
                    "polyfill.liquid." + path, "cml:fluidlvl_%s_%d", 16);
        }
    }

    private final Key id;
    private final String legacyName;
    private volatile FluidProperties properties;

    private FluidType(Key id, String legacyName, FluidProperties properties) {
        this.id = id;
        this.legacyName = legacyName;
        this.properties = properties;
    }

    // ------------------------------------------------------------- built-ins

    public static final FluidType WATER = builtin("water", 1000, 1000, 4, 2, "water", 0xFF3F76E4, 1.0, 1.0,
            "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", Key.of("minecraft", "water"),
            "water");
    public static final FluidType LAVA = builtin("lava", 1000, 1000, 12, 8, "lava", 0xFFCF5A16, 3.0, 12.0,
            "minecraft:item.bucket.empty_lava", "minecraft:item.bucket.fill_lava", Key.of("minecraft", "lava"),
            "lava");
    public static final FluidType SLIME = builtin("slime", 111, 999, 10, 8, "lava", 0xFF7CB55B, 1.4, 8.0,
            "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "slime");
    public static final FluidType EXPERIENCE = builtin("experience", 1, 1, 1, 1, "xp", 0xFF7FE02B, 0.2, 0.5,
            "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "experience");
    public static final FluidType POWDER_SNOW = builtin("powder_snow", 1000, 1000, 6, 4, "water", 0xFFF0FBFF, 0.6, 3.0,
            "minecraft:item.bucket.empty_powder_snow", "minecraft:item.bucket.fill_powder_snow", null,
            "powder_snow");
    public static final FluidType MILK = builtin("milk", 1000, 1000, 2, 2, "water", 0xFFFFFFFF, 1.0, 1.2,
            "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "milk");
    public static final FluidType EMPTY = builtin("empty", 0, 0, 1, 1, "water", 0x00000000, 0.0, 1.0,
            "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "empty");
    public static final FluidType HONEY = builtin("honey", 250, 1000, 8, 8, "lava", 0xFFF9B71B, 1.5, 10.0,
            "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "honey");

    private static FluidType builtin(String path, int unitMb, int mbPerFullBlock, int blockCollectDelay,
            int carrierIoDelay, String renderFamily, int color, double density, double viscosity,
            String fillSound, String drainSound, Key vanillaFluid, String tankVariant) {
        Key id = Key.of(NAMESPACE, path);
        FluidType type = new FluidType(id, path.toUpperCase(Locale.ROOT),
                new FluidProperties(unitMb, mbPerFullBlock, blockCollectDelay, carrierIoDelay, renderFamily, color,
                        density, viscosity, fillSound, drainSound, vanillaFluid, tankVariant,
                        "polyfill.liquid." + path, "cml:fluidlvl_%s_%d", 16));
        return REGISTRY.register(id, type);
    }

    /**
     * Looks a fluid up, creating it if the load phase is still open.
     *
     * <p>
     * This is how {@code fluid_types/*.json} introduces liquids the plugin does not
     * know about. Outside the load phase the registry is frozen and an unknown id
     * simply yields {@code null}.
     */
    public static FluidType getOrCreate(Key id) {
        FluidType existing = REGISTRY.get(id);
        if (existing != null)
            return existing;
        if (REGISTRY.isFrozen())
            return null;
        FluidType created = new FluidType(id, id.value().toUpperCase(Locale.ROOT),
                FluidProperties.defaults(id.value()));
        return REGISTRY.register(id, created);
    }

    /** Replaces this fluid's tunables. Load-phase only, called by the loader. */
    public void applyProperties(FluidProperties properties) {
        if (REGISTRY.isFrozen())
            throw new IllegalStateException("Cannot retune fluid '" + id + "' after the load phase");
        this.properties = properties;
    }

    // -------------------------------------------------------------- identity

    public Key id() {
        return id;
    }

    /**
     * The legacy uppercase name ({@code "WATER"}). Kept because {@link FluidKeys}
     * persists it and because it reads naturally in logs; prefer {@link #id()} in
     * new code.
     */
    public String name() {
        return legacyName;
    }

    @Override
    public String toString() {
        return legacyName;
    }

    /**
     * Resolves a fluid by legacy name ({@code "WATER"}) or namespaced id.
     * Drop-in for the old {@code enum} method, including its
     * {@link IllegalArgumentException} on an unknown value.
     */
    public static FluidType valueOf(String name) {
        FluidType type = byName(name);
        if (type == null)
            throw new IllegalArgumentException("No fluid type '" + name + "'; known: " + REGISTRY.keys());
        return type;
    }

    /** Like {@link #valueOf(String)} but returns {@code null} instead of throwing. */
    public static FluidType byName(String name) {
        if (name == null || name.isBlank())
            return null;
        String trimmed = name.trim();
        if (trimmed.indexOf(':') >= 0)
            return REGISTRY.get(Key.of(trimmed));
        FluidType byId = REGISTRY.get(Key.of(NAMESPACE, trimmed.toLowerCase(Locale.ROOT)));
        if (byId != null)
            return byId;
        for (FluidType type : REGISTRY.values())
            if (type.legacyName.equalsIgnoreCase(trimmed))
                return type;
        return null;
    }

    /** All registered fluids. Drop-in for the old {@code enum} method. */
    public static FluidType[] values() {
        return REGISTRY.values().toArray(new FluidType[0]);
    }

    // ------------------------------------------------------------ properties

    public FluidProperties properties() {
        return properties;
    }

    /** mB in one item unit of this fluid. Kept as a method-shaped field accessor. */
    public int unitMb() {
        return properties.unitMb();
    }

    /** mB needed to place one full block of this fluid in the world. */
    public int mbPerFullBlock() {
        return properties.mbPerFullBlock();
    }

    /** Which pack model family draws it: {@code water}, {@code lava} or {@code xp}. */
    public String renderFamily() {
        return properties.renderFamily();
    }

    public int color() {
        return properties.color();
    }

    public double density() {
        return properties.density();
    }

    public double viscosity() {
        return properties.viscosity();
    }

    public String fillSound() {
        return properties.fillSound();
    }

    public String drainSound() {
        return properties.drainSound();
    }

    /** The {@code minecraft:} fluid this maps onto, or {@code null} if it has none. */
    public Key vanillaFluid() {
        return properties.vanillaFluid();
    }

    /**
     * Which of the pack's fixed tank appearances draws this liquid. A data-defined
     * fluid picks one of the existing looks rather than adding a new blockstate.
     */
    public String tankVariant() {
        return properties.tankVariant();
    }

    /**
     * The fluid a stored tank appearance maps back to.
     *
     * <p>
     * The mapping is one-way in general — several data-defined fluids may share a
     * variant — so this returns the first registered fluid claiming it, which for
     * the built-ins is the original 1:1 partner. Only used when reading a tank's
     * contents back out of a blockstate, where the block entity's own stored stack
     * is the authoritative source anyway.
     */
    public static FluidType byTankVariant(String variant) {
        if (variant == null)
            return EMPTY;
        for (FluidType type : REGISTRY.values())
            if (variant.equalsIgnoreCase(type.properties.tankVariant()))
                return type;
        return EMPTY;
    }

    /**
     * The item id for one fill level of this liquid's gauge, e.g. level 7 of water.
     *
     * <p>
     * Declared once per fluid rather than repeated in every machine's bar config —
     * a gauge showing water looks the same in a furnace and in a refinery, so the
     * machine only says where the bar goes and the fluid says how it looks.
     */
    public String barItem(int level) {
        int clamped = Math.max(0, Math.min(properties.barLevels() - 1, level));
        return String.format(properties.barItemTemplate(), properties.renderFamily(), clamped);
    }

    /** How many fill levels this liquid's gauge has. */
    public int barLevels() {
        return properties.barLevels();
    }

    /** Minecraft i18n key for this liquid, resolved client-side from the pack lang. */
    public String translationKey() {
        return properties.translationKey();
    }

    public boolean isEmpty() {
        return this == EMPTY || (properties.mbPerFullBlock() <= 0 && properties.unitMb() <= 0);
    }

    /** Ticks between pulls when harvesting from world blocks. */
    public static int blockCollectDelay(FluidType t) {
        return t == null ? 1 : t.properties.blockCollectDelay();
    }

    /** Ticks between pushes/pulls between carriers — cheaper than harvesting. */
    public static int carrierIODelay(FluidType t) {
        return t == null ? 1 : t.properties.carrierIoDelay();
    }

    /** Legacy field-style accessor, equivalent to {@link #unitMb()}. */
    public int unit() {
        return properties.unitMb();
    }

    // ---------------------------------------------------------- world lookup

    private static boolean isVanilla(FluidType type, FluidState state, net.minecraft.world.level.material.Fluid source,
            net.minecraft.world.level.material.Fluid flowing) {
        return type != null && type.properties.vanillaFluid() != null
                && (state.is(source) || state.is(flowing));
    }

    public boolean isSourceBlock(FluidState state) {
        Key vanilla = properties.vanillaFluid();
        if (vanilla == null || state == null || state.isEmpty())
            return false;
        if (this == WATER)
            return isVanilla(this, state, net.minecraft.world.level.material.Fluids.WATER,
                    net.minecraft.world.level.material.Fluids.FLOWING_WATER);
        if (this == LAVA)
            return isVanilla(this, state, net.minecraft.world.level.material.Fluids.LAVA,
                    net.minecraft.world.level.material.Fluids.FLOWING_LAVA);
        return false;
    }

    public static boolean matches(FluidType t, FluidState state) {
        if (t == null || state == null || state.isEmpty())
            return false;
        return t.isSourceBlock(state);
    }

    // ------------------------------------------------- delegating helpers

    public static FluidStack collectAt(BlockPos pos, Level level, int maxMb, FluidType preferred) {
        return FluidCollector.collectAt(pos, level, maxMb, preferred);
    }

    public static FluidType getFluidTypeAt(BlockPos pos, Level level) {
        return FluidCollector.getFluidTypeAt(pos, level);
    }

    public static FluidStack collectArea(BlockPos pos, Level level, int radius, int maxMb, FluidType preferred) {
        return FluidCollector.collectArea(pos, level, radius, maxMb, preferred);
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level) {
        return FluidPlacer.place(stack, pos, level);
    }

    public static boolean place(FluidStack stack, BlockPos pos, Level level, int radius) {
        return FluidPlacer.place(stack, pos, level, radius);
    }

    public static int depositToCarrier(FluidCarrier carrier, Level level, BlockPos pos, FluidStack stack) {
        return depositToCarrier(carrier, level, pos, stack, null);
    }

    public static int depositToCarrier(FluidCarrier carrier, Level level, BlockPos pos, FluidStack stack,
            net.minecraft.core.Direction direction) {
        if (carrier == null || stack == null || stack.isEmpty())
            return 0;
        return carrier.insertFluid(level, pos, stack, direction);
    }

    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb,
            java.util.function.Consumer<FluidStack> drained) {
        return extractFromCarrier(carrier, level, pos, mb, drained, null);
    }

    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb,
            java.util.function.Consumer<FluidStack> drained, net.minecraft.core.Direction direction) {
        if (carrier == null || mb <= 0)
            return 0;
        return carrier.extractFluid(level, pos, mb, drained, direction);
    }

    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack) {
        return FluidItemConverter.collectFromStack(stack);
    }

    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid,
            int requestedAmount) {
        return FluidItemConverter.collectToStack(container, fluid, requestedAmount);
    }

    /**
     * Reaction of a fluid with the player's held item: consumes part of the fluid
     * and transforms the item. Returns the remaining fluid plus the items produced
     * (buckets, bottles...).
     */
    public static Pair<FluidStack, List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem) {
        return FluidReactions.reaction(fluid, playerItem);
    }

    /**
     * Disperses a fluid into an air block: XP spawns orbs, placeable liquids try to
     * place a block. Returns how much fluid was consumed.
     */
    public static int disperseIntoAir(FluidStack stack, BlockPos pos, Level level) {
        return FluidPlacer.disperseIntoAir(stack, pos, level);
    }

    public static final Random RANDOM = new Random();

    @SuppressWarnings("deprecation")
    public static void spawnXpOrb(Level level, BlockPos pos, int xp) {
        ExperienceOrb orb = new ExperienceOrb(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xp);
        level.addFreshEntity(orb);
    }
}
