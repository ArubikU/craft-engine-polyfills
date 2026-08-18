/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.momirealms.craftengine.core.util.Key
 */
package dev.arubik.craftengine.gas;

import dev.arubik.craftengine.data.Registries;
import dev.arubik.craftengine.data.Registry;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.momirealms.craftengine.core.util.Key;

public final class GasType {
    public static final String NAMESPACE = "polyfills";
    public static final Registry<GasType> REGISTRY = Registries.create("gas_type", false);
    private final Key id;
    private final String legacyName;
    private volatile GasProperties properties;
    public static final GasType EMPTY = GasType.builtin("empty", "Empty", 0, 0.0, "empty");
    public static final GasType STEAM = GasType.builtin("steam", "Steam", -1510152, 1.0, "steam");
    public static final GasType HEAVY_STEAM = GasType.builtin("heavy_steam", "Heavy Steam", -4205348, 2.0, "heavy_steam");
    public static final GasType NITROGEN = GasType.builtin("nitrogen", "Nitrogen", -5715736, 0.97, "nitrogen");

    private GasType(Key id, String legacyName, GasProperties properties) {
        this.id = id;
        this.legacyName = legacyName;
        this.properties = properties;
    }

    private static GasType builtin(String path, String displayName, int color, double density, String variant) {
        Key id = Key.of((String)NAMESPACE, (String)path);
        GasType type = new GasType(id, path.toUpperCase(Locale.ROOT), new GasProperties(displayName, "polyfill.gas." + path, color, density, variant, Key.of((String)"minecraft", (String)"cloud"), 50, 0.2, 0.05));
        return REGISTRY.register(id, type);
    }

    public static GasType getOrCreate(Key id) {
        GasType existing = REGISTRY.get(id);
        if (existing != null) {
            return existing;
        }
        if (REGISTRY.isFrozen()) {
            return null;
        }
        GasType created = new GasType(id, id.value().toUpperCase(Locale.ROOT), GasProperties.defaults(id.value()));
        return REGISTRY.register(id, created);
    }

    public void applyProperties(GasProperties properties) {
        if (REGISTRY.isFrozen()) {
            throw new IllegalStateException("Cannot retune gas '" + String.valueOf(this.id) + "' after the load phase");
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

    public static GasType valueOf(String name) {
        GasType type = GasType.byName(name);
        if (type == null) {
            throw new IllegalArgumentException("No gas type '" + name + "'; known: " + String.valueOf(REGISTRY.keys()));
        }
        return type;
    }

    public static GasType byName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        if (trimmed.indexOf(58) >= 0) {
            return REGISTRY.get(Key.of((String)trimmed));
        }
        GasType byId = REGISTRY.get(Key.of((String)NAMESPACE, (String)trimmed.toLowerCase(Locale.ROOT)));
        if (byId != null) {
            return byId;
        }
        for (GasType type : REGISTRY.values()) {
            if (!type.legacyName.equalsIgnoreCase(trimmed)) continue;
            return type;
        }
        return null;
    }

    public static GasType fromName(String s) {
        GasType type = GasType.byName(s);
        return type != null ? type : EMPTY;
    }

    public static GasType[] values() {
        return REGISTRY.values().toArray(new GasType[0]);
    }

    public GasProperties properties() {
        return this.properties;
    }

    @Deprecated
    public String getDisplayName() {
        return this.properties.displayName();
    }

    public String translationKey() {
        return this.properties.translationKey();
    }

    public int color() {
        return this.properties.color();
    }

    public double density() {
        return this.properties.density();
    }

    public String tankVariant() {
        return this.properties.tankVariant();
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public static GasType byTankVariant(String variant) {
        if (variant == null) {
            return EMPTY;
        }
        for (GasType type : REGISTRY.values()) {
            if (!variant.equalsIgnoreCase(type.properties.tankVariant())) continue;
            return type;
        }
        return EMPTY;
    }

    public boolean vent(Level level, BlockPos pos, int amount) {
        if (this.isEmpty()) {
            return false;
        }
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            GasProperties p = this.properties;
            ParticleType particle = (ParticleType)BuiltInRegistries.PARTICLE_TYPE.getOptional(Identifier.parse((String)p.ventParticle().toString())).orElse(ParticleTypes.CLOUD);
            if (particle instanceof SimpleParticleType) {
                SimpleParticleType simple = (SimpleParticleType)particle;
                serverLevel.sendParticles((ParticleOptions)simple, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, Math.max(1, amount / Math.max(1, p.ventPerParticle())), p.ventSpread(), p.ventSpread(), p.ventSpread(), p.ventSpeed());
            }
        }
        return true;
    }

    public record GasProperties(String displayName, String translationKey, int color, double density, String tankVariant, Key ventParticle, int ventPerParticle, double ventSpread, double ventSpeed) {
        public static GasProperties defaults(String path) {
            return new GasProperties(path, "polyfill.gas." + path, -1, 1.0, "empty", Key.of((String)"minecraft", (String)"cloud"), 50, 0.2, 0.05);
        }
    }
}

