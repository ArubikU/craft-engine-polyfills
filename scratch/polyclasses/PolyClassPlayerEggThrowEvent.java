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

public class PolyClassPlayerEggThrowEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerEggThrowEvent", (String)"set_hatching", (String)"Z:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerEggThrowEvent", (String)"set_hatching");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerEggThrowEvent", (String)"egg");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerEggThrowEvent", (String)"hatching");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerEggThrowEvent", (String)"hatching", (String)"Z");
    }

    public boolean tm$0_set_hatching(boolean bl) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)bl);
        }
        return PolyClassRuntime.genericCall((String)"PlayerEggThrowEvent", (String)"set_hatching", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((boolean)bl)}).asBool();
    }

    public ScriptValue um$1_set_hatching(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerEggThrowEvent", (String)"set_hatching", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_egg() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerEggThrowEvent", (String)"egg", (Object)this.instance);
    }

    public ScriptValue pg$3_hatching() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerEggThrowEvent", (String)"hatching", (Object)this.instance);
    }

    public boolean tg$4_hatching() {
        if (tp$4 != null) {
            return (Boolean)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerEggThrowEvent", (String)"hatching", (Object)this.instance).asBool();
    }

    public PolyClassPlayerEggThrowEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerEggThrowEvent of(Object object) {
        return new PolyClassPlayerEggThrowEvent(object);
    }

    public static PolyClassPlayerEggThrowEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerEggThrowEvent")) {
            return new PolyClassPlayerEggThrowEvent(object);
        }
        return null;
    }
}
