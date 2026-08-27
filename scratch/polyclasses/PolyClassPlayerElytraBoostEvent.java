/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;
import java.util.List;

public class PolyClassPlayerElytraBoostEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerElytraBoostEvent", (String)"set_should_consume", (String)"Z:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerElytraBoostEvent", (String)"set_should_consume");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerElytraBoostEvent", (String)"item");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerElytraBoostEvent", (String)"should_consume");
    }

    public boolean tm$0_set_should_consume(boolean bl) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"PlayerElytraBoostEvent", (String)"set_should_consume", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$1_set_should_consume(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerElytraBoostEvent", (String)"set_should_consume", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_item() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerElytraBoostEvent", (String)"item", (Object)this.instance);
    }

    public ScriptValue pg$3_should_consume() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerElytraBoostEvent", (String)"should_consume", (Object)this.instance);
    }

    public PolyClassPlayerElytraBoostEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerElytraBoostEvent of(Object object) {
        return new PolyClassPlayerElytraBoostEvent(object);
    }

    public static PolyClassPlayerElytraBoostEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerElytraBoostEvent")) {
            return new PolyClassPlayerElytraBoostEvent(object);
        }
        return null;
    }
}
