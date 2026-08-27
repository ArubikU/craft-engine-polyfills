/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler0
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassRecipe {
    protected final Object instance;
    private static volatile PolyType.TypedMethodHandler0 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.PropertyHandler p$5;
    private static volatile PolyType.TypedPropertyHandler tp$6;
    private static volatile PolyType.PropertyHandler p$7;
    private static volatile PolyType.PropertyHandler p$8;
    private static volatile PolyType.PropertyHandler p$9;
    private static volatile PolyType.TypedPropertyHandler tp$10;
    private static volatile PolyType.PropertyHandler p$11;
    private static volatile PolyType.TypedPropertyHandler tp$12;
    private static volatile PolyType.PropertyHandler p$13;
    private static volatile PolyType.TypedPropertyHandler tp$14;
    private static volatile PolyType.PropertyHandler p$15;
    private static volatile PolyType.TypedPropertyHandler tp$16;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler0)PolyClassRuntime.resolveTypedHandler((String)"Recipe", (String)"outputs", (String)":R");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"Recipe", (String)"outputs");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"energy_cost");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Recipe", (String)"energy_cost", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"outputs");
        p$5 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"processing_time");
        tp$6 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Recipe", (String)"processing_time", (String)"D");
        p$7 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"inputs");
        p$8 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"output_chances");
        p$9 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"su_cost");
        tp$10 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Recipe", (String)"su_cost", (String)"D");
        p$11 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"fuel_required");
        tp$12 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Recipe", (String)"fuel_required", (String)"Z");
        p$13 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"id");
        tp$14 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Recipe", (String)"id", (String)"S");
        p$15 = PolyClassRuntime.resolvePropertyHandler((String)"Recipe", (String)"min_rpm");
        tp$16 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"Recipe", (String)"min_rpm", (String)"D");
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

    public double tg$3_energy_cost() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"energy_cost", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_outputs() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"outputs", (Object)this.instance);
    }

    public ScriptValue pg$5_processing_time() {
        if (p$5 != null) {
            return p$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"processing_time", (Object)this.instance);
    }

    public double tg$6_processing_time() {
        if (tp$6 != null) {
            return (Double)tp$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"processing_time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$7_inputs() {
        if (p$7 != null) {
            return p$7.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"inputs", (Object)this.instance);
    }

    public ScriptValue pg$8_output_chances() {
        if (p$8 != null) {
            return p$8.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"output_chances", (Object)this.instance);
    }

    public ScriptValue pg$9_su_cost() {
        if (p$9 != null) {
            return p$9.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"su_cost", (Object)this.instance);
    }

    public double tg$10_su_cost() {
        if (tp$10 != null) {
            return (Double)tp$10.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"su_cost", (Object)this.instance).asNum();
    }

    public ScriptValue pg$11_fuel_required() {
        if (p$11 != null) {
            return p$11.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"fuel_required", (Object)this.instance);
    }

    public boolean tg$12_fuel_required() {
        if (tp$12 != null) {
            return (Boolean)tp$12.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"fuel_required", (Object)this.instance).asBool();
    }

    public ScriptValue pg$13_id() {
        if (p$13 != null) {
            return p$13.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"id", (Object)this.instance);
    }

    public String tg$14_id() {
        if (tp$14 != null) {
            return (String)tp$14.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"id", (Object)this.instance).asStr();
    }

    public ScriptValue pg$15_min_rpm() {
        if (p$15 != null) {
            return p$15.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"min_rpm", (Object)this.instance);
    }

    public double tg$16_min_rpm() {
        if (tp$16 != null) {
            return (Double)tp$16.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Recipe", (String)"min_rpm", (Object)this.instance).asNum();
    }

    public PolyClassRecipe(Object object) {
        this.instance = object;
    }

    public static PolyClassRecipe of(Object object) {
        return new PolyClassRecipe(object);
    }

    public static PolyClassRecipe ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("Recipe")) {
            return new PolyClassRecipe(object);
        }
        return null;
    }

    public static PolyClassRecipe ofVar(ScriptContext scriptContext, String string) {
        return PolyClassRecipe.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
