/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassRecipe {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler0 h$0;
    private static volatile PolyType.MethodHandler m$1;
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
        h$0 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Recipe", (String)"outputs", (String)":R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Recipe", (String)"outputs");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"energy_cost");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"outputs");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"processing_time");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"inputs");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"output_chances");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"su_cost");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"fuel_required");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"id");
        p$10 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"min_rpm");
    }

    public ScriptValue tm$0_outputs() {
        if (h$0 != null) {
            return (ScriptValue)h$0.call(this.instance);
        }
        return PolyClassRuntime.genericCall((String)"Recipe", (String)"outputs", (Object)this.instance, (ScriptValue[])new ScriptValue[0]);
    }

    public ScriptValue um$1_outputs(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"Recipe", (String)"outputs", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_energy_cost() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"energy_cost", (Object)this.instance);
    }

    public ScriptValue pg$3_outputs() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"outputs", (Object)this.instance);
    }

    public ScriptValue pg$4_processing_time() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"processing_time", (Object)this.instance);
    }

    public ScriptValue pg$5_inputs() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"inputs", (Object)this.instance);
    }

    public ScriptValue pg$6_output_chances() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"output_chances", (Object)this.instance);
    }

    public ScriptValue pg$7_su_cost() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"su_cost", (Object)this.instance);
    }

    public ScriptValue pg$8_fuel_required() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"fuel_required", (Object)this.instance);
    }

    public ScriptValue pg$9_id() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"id", (Object)this.instance);
    }

    public ScriptValue pg$10_min_rpm() {
        if (p$10 != null) {
            return p$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"min_rpm", (Object)this.instance);
    }

    public PolyClassRecipe(Object object) {
        this.instance = object;
    }

    public static PolyClassRecipe of(Object object) {
        return new PolyClassRecipe(object);
    }
}
