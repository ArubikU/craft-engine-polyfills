/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassRegistry {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.PropertyHandler p$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.PropertyHandler p$10;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"recipes");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"sounds");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"biomes");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"blocks");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"potions");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"entity_types");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"particles");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"enchantments");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"damage_types");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"items");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"Registry", (String)"fluids");
    }

    public ScriptValue pg$0_recipes() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"recipes", (Object)this.instance);
    }

    public ScriptValue pg$1_sounds() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"sounds", (Object)this.instance);
    }

    public ScriptValue pg$2_biomes() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"biomes", (Object)this.instance);
    }

    public ScriptValue pg$3_blocks() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"blocks", (Object)this.instance);
    }

    public ScriptValue pg$4_potions() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"potions", (Object)this.instance);
    }

    public ScriptValue pg$5_entity_types() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"entity_types", (Object)this.instance);
    }

    public ScriptValue pg$6_particles() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"particles", (Object)this.instance);
    }

    public ScriptValue pg$7_enchantments() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"enchantments", (Object)this.instance);
    }

    public ScriptValue pg$8_damage_types() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"damage_types", (Object)this.instance);
    }

    public ScriptValue pg$9_items() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"items", (Object)this.instance);
    }

    public ScriptValue pg$10_fluids() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Registry", (String)"fluids", (Object)this.instance);
    }

    public PolyClassRegistry(Object object) {
        this.instance = object;
    }

    public static PolyClassRegistry of(Object object) {
        return new PolyClassRegistry(object);
    }
}
