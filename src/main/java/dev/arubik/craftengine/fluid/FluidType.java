/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.ExperienceOrb
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.fluid;

import com.mojang.datafixers.util.Pair;
import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import dev.arubik.craftengine.fluid.FluidCollector;
import dev.arubik.craftengine.fluid.FluidItemConverter;
import dev.arubik.craftengine.fluid.FluidPlacer;
import dev.arubik.craftengine.fluid.FluidReactions;
import dev.arubik.craftengine.fluid.FluidStack;
import dev.arubik.craftengine.fluid.behavior.FluidCarrier;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.momirealms.craftengine.core.util.Key;

public final class FluidType {
    public static final String NAMESPACE = "polyfills";
    public static final Registry<FluidType> REGISTRY = Registries.create("fluid_type", false);
    public static final int MB_PER_BUCKET = 1000;
    private final Key id;
    private final String legacyName;
    private volatile FluidProperties properties;
    public static final FluidType WATER = FluidType.builtin("water", 1000, 1000, 4, 2, "water", -12618012, 1.0, 1.0, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", Key.of((String)"minecraft", (String)"water"), "water");
    public static final FluidType LAVA = FluidType.builtin("lava", 1000, 1000, 12, 8, "lava", -3188202, 3.0, 12.0, "minecraft:item.bucket.empty_lava", "minecraft:item.bucket.fill_lava", Key.of((String)"minecraft", (String)"lava"), "lava");
    public static final FluidType SLIME = FluidType.builtin("slime", 111, 999, 10, 8, "lava", -8604325, 1.4, 8.0, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "slime");
    public static final FluidType EXPERIENCE = FluidType.builtin("experience", 1, 1, 1, 1, "xp", -8396757, 0.2, 0.5, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "experience");
    public static final FluidType POWDER_SNOW = FluidType.builtin("powder_snow", 1000, 1000, 6, 4, "water", -984065, 0.6, 3.0, "minecraft:item.bucket.empty_powder_snow", "minecraft:item.bucket.fill_powder_snow", null, "powder_snow");
    public static final FluidType MILK = FluidType.builtin("milk", 1000, 1000, 2, 2, "water", -1, 1.0, 1.2, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "milk");
    public static final FluidType EMPTY = FluidType.builtin("empty", 0, 0, 1, 1, "water", 0, 0.0, 1.0, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "empty");
    public static final FluidType HONEY = FluidType.builtin("honey", 250, 1000, 8, 8, "lava", -411877, 1.5, 10.0, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "honey");
    public static final Random RANDOM = new Random();

    private FluidType(Key id, String legacyName, FluidProperties properties) {
        this.id = id;
        this.legacyName = legacyName;
        this.properties = properties;
    }

    private static FluidType builtin(String path, int unitMb, int mbPerFullBlock, int blockCollectDelay, int carrierIoDelay, String renderFamily, int color, double density, double viscosity, String fillSound, String drainSound, Key vanillaFluid, String tankVariant) {
        Key id = Key.of((String)NAMESPACE, (String)path);
        FluidType type = new FluidType(id, path.toUpperCase(Locale.ROOT), new FluidProperties(unitMb, mbPerFullBlock, blockCollectDelay, carrierIoDelay, renderFamily, color, density, viscosity, fillSound, drainSound, vanillaFluid, tankVariant, "polyfill.liquid." + path));
        return REGISTRY.register(id, type);
    }

    public static FluidType getOrCreate(Key id) {
        FluidType existing = REGISTRY.get(id);
        if (existing != null) {
            return existing;
        }
        if (REGISTRY.isFrozen()) {
            return null;
        }
        FluidType created = new FluidType(id, id.value().toUpperCase(Locale.ROOT), FluidProperties.defaults(id.value()));
        return REGISTRY.register(id, created);
    }

    public void applyProperties(FluidProperties properties) {
        if (REGISTRY.isFrozen()) {
            throw new IllegalStateException("Cannot retune fluid '" + String.valueOf(this.id) + "' after the load phase");
        }
        this.properties = properties;
    }

    public Key id() {
        return this.id;
    }

    public String name() {
        return this.legacyName;
    }

    public String toString() {
        return this.legacyName;
    }

    public static FluidType valueOf(String name) {
        FluidType type = FluidType.byName(name);
        if (type == null) {
            throw new IllegalArgumentException("No fluid type '" + name + "'; known: " + String.valueOf(REGISTRY.keys()));
        }
        return type;
    }

    public static FluidType byName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        if (trimmed.indexOf(58) >= 0) {
            return REGISTRY.get(Key.of((String)trimmed));
        }
        FluidType byId = REGISTRY.get(Key.of((String)NAMESPACE, (String)trimmed.toLowerCase(Locale.ROOT)));
        if (byId != null) {
            return byId;
        }
        for (FluidType type : REGISTRY.values()) {
            if (!type.legacyName.equalsIgnoreCase(trimmed)) continue;
            return type;
        }
        return null;
    }

    public static FluidType[] values() {
        return REGISTRY.values().toArray(new FluidType[0]);
    }

    public FluidProperties properties() {
        return this.properties;
    }

    public int unitMb() {
        return this.properties.unitMb();
    }

    public int mbPerFullBlock() {
        return this.properties.mbPerFullBlock();
    }

    public String renderFamily() {
        return this.properties.renderFamily();
    }

    public int color() {
        return this.properties.color();
    }

    public double density() {
        return this.properties.density();
    }

    public double viscosity() {
        return this.properties.viscosity();
    }

    public String fillSound() {
        return this.properties.fillSound();
    }

    public String drainSound() {
        return this.properties.drainSound();
    }

    public Key vanillaFluid() {
        return this.properties.vanillaFluid();
    }

    public String tankVariant() {
        return this.properties.tankVariant();
    }

    public static FluidType byTankVariant(String variant) {
        if (variant == null) {
            return EMPTY;
        }
        for (FluidType type : REGISTRY.values()) {
            if (!variant.equalsIgnoreCase(type.properties.tankVariant())) continue;
            return type;
        }
        return EMPTY;
    }

    public String translationKey() {
        return this.properties.translationKey();
    }

    public boolean isEmpty() {
        return this == EMPTY || this.properties.mbPerFullBlock() <= 0 && this.properties.unitMb() <= 0;
    }

    public static int blockCollectDelay(FluidType t) {
        return t == null ? 1 : t.properties.blockCollectDelay();
    }

    public static int carrierIODelay(FluidType t) {
        return t == null ? 1 : t.properties.carrierIoDelay();
    }

    public int unit() {
        return this.properties.unitMb();
    }

    private static boolean isVanilla(FluidType type, FluidState state, Fluid source, Fluid flowing) {
        return type != null && type.properties.vanillaFluid() != null && (state.is(source) || state.is(flowing));
    }

    public boolean isSourceBlock(FluidState state) {
        Key vanilla = this.properties.vanillaFluid();
        if (vanilla == null || state == null || state.isEmpty()) {
            return false;
        }
        if (this == WATER) {
            return FluidType.isVanilla(this, state, (Fluid)Fluids.WATER, (Fluid)Fluids.FLOWING_WATER);
        }
        if (this == LAVA) {
            return FluidType.isVanilla(this, state, (Fluid)Fluids.LAVA, (Fluid)Fluids.FLOWING_LAVA);
        }
        return false;
    }

    public static boolean matches(FluidType t, FluidState state) {
        if (t == null || state == null || state.isEmpty()) {
            return false;
        }
        return t.isSourceBlock(state);
    }

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
        return FluidType.depositToCarrier(carrier, level, pos, stack, null);
    }

    public static int depositToCarrier(FluidCarrier carrier, Level level, BlockPos pos, FluidStack stack, Direction direction) {
        if (carrier == null || stack == null || stack.isEmpty()) {
            return 0;
        }
        return carrier.insertFluid(level, pos, stack, direction);
    }

    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb, Consumer<FluidStack> drained) {
        return FluidType.extractFromCarrier(carrier, level, pos, mb, drained, null);
    }

    public static int extractFromCarrier(FluidCarrier carrier, Level level, BlockPos pos, int mb, Consumer<FluidStack> drained, Direction direction) {
        if (carrier == null || mb <= 0) {
            return 0;
        }
        return carrier.extractFluid(level, pos, mb, drained, direction);
    }

    public static Pair<FluidStack, ItemStack> collectFromStack(ItemStack stack) {
        return FluidItemConverter.collectFromStack(stack);
    }

    public static Pair<ItemStack, FluidStack> collectToStack(ItemStack container, FluidStack fluid, int requestedAmount) {
        return FluidItemConverter.collectToStack(container, fluid, requestedAmount);
    }

    public static Pair<FluidStack, List<ItemStack>> reaction(FluidStack fluid, ItemStack playerItem) {
        return FluidReactions.reaction(fluid, playerItem);
    }

    public static int disperseIntoAir(FluidStack stack, BlockPos pos, Level level) {
        return FluidPlacer.disperseIntoAir(stack, pos, level);
    }

    public static void spawnXpOrb(Level level, BlockPos pos, int xp) {
        ExperienceOrb orb = new ExperienceOrb(level, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, xp);
        level.addFreshEntity((Entity)orb);
    }

    public record FluidProperties(int unitMb, int mbPerFullBlock, int blockCollectDelay, int carrierIoDelay, String renderFamily, int color, double density, double viscosity, String fillSound, String drainSound, Key vanillaFluid, String tankVariant, String translationKey) {
        public static FluidProperties defaults(String path) {
            return new FluidProperties(1000, 1000, 4, 2, "water", -1, 1.0, 1.0, "minecraft:item.bucket.empty", "minecraft:item.bucket.fill", null, "water", "polyfill.liquid." + path);
        }
    }
}

