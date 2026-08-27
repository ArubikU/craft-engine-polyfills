/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassPlayerStatisticIncrementEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"statistic");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"statistic", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"previous_value");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"previous_value", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"new_value");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"new_value", (String)"D");
    }

    public ScriptValue pg$0_statistic() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"statistic", (Object)this.instance);
    }

    public String tg$1_statistic() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"statistic", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_previous_value() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"previous_value", (Object)this.instance);
    }

    public double tg$3_previous_value() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"previous_value", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_new_value() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"new_value", (Object)this.instance);
    }

    public double tg$5_new_value() {
        if (tp$5 != null) {
            return (Double)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"new_value", (Object)this.instance).asNum();
    }

    public PolyClassPlayerStatisticIncrementEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerStatisticIncrementEvent of(Object object) {
        return new PolyClassPlayerStatisticIncrementEvent(object);
    }

    public static PolyClassPlayerStatisticIncrementEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerStatisticIncrementEvent")) {
            return new PolyClassPlayerStatisticIncrementEvent(object);
        }
        return null;
    }
}
