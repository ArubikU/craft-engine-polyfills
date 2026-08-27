/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"statistic");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"previous_value");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerStatisticIncrementEvent", (String)"new_value");
    }

    public ScriptValue pg$0_statistic() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"statistic", (Object)this.instance);
    }

    public ScriptValue pg$1_previous_value() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"previous_value", (Object)this.instance);
    }

    public ScriptValue pg$2_new_value() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerStatisticIncrementEvent", (String)"new_value", (Object)this.instance);
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
