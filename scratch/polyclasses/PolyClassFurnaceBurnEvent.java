/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptContext
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassFurnaceBurnEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;
    private static volatile PolyType.PropertyHandler p$6;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"FurnaceBurnEvent", (String)"set_burn_time", (String)"D:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"FurnaceBurnEvent", (String)"set_burn_time");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceBurnEvent", (String)"burning");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"FurnaceBurnEvent", (String)"burning", (String)"Z");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceBurnEvent", (String)"burn_time");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"FurnaceBurnEvent", (String)"burn_time", (String)"D");
        p$6 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceBurnEvent", (String)"fuel");
    }

    public boolean tm$0_set_burn_time(double d) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)d);
        }
        return PolyClassRuntime.genericCall((String)"FurnaceBurnEvent", (String)"set_burn_time", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((double)d)}).asBool();
    }

    public ScriptValue um$1_set_burn_time(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"FurnaceBurnEvent", (String)"set_burn_time", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_burning() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceBurnEvent", (String)"burning", (Object)this.instance);
    }

    public boolean tg$3_burning() {
        if (tp$3 != null) {
            return (Boolean)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceBurnEvent", (String)"burning", (Object)this.instance).asBool();
    }

    public ScriptValue pg$4_burn_time() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceBurnEvent", (String)"burn_time", (Object)this.instance);
    }

    public double tg$5_burn_time() {
        if (tp$5 != null) {
            return (Double)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceBurnEvent", (String)"burn_time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$6_fuel() {
        if (p$6 != null) {
            return p$6.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceBurnEvent", (String)"fuel", (Object)this.instance);
    }

    public PolyClassFurnaceBurnEvent(Object object) {
        super(object);
    }

    public static PolyClassFurnaceBurnEvent of(Object object) {
        return new PolyClassFurnaceBurnEvent(object);
    }

    public static PolyClassFurnaceBurnEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("FurnaceBurnEvent")) {
            return new PolyClassFurnaceBurnEvent(object);
        }
        return null;
    }

    public static PolyClassFurnaceBurnEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassFurnaceBurnEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
