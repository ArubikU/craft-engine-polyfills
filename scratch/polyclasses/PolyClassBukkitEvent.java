/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler1
 *  dev.arubik.craftengine.script.PolyType$TypedMethodHandler2
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

public class PolyClassBukkitEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler2 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.TypedMethodHandler1 h$2;
    private static volatile PolyType.MethodHandler m$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler2)PolyClassRuntime.resolveTypedHandler((String)"BukkitEvent", (String)"set", (String)"SR:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"BukkitEvent", (String)"set");
        h$2 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"BukkitEvent", (String)"get", (String)"S:R");
        m$3 = PolyClassRuntime.resolveMethodHandler((String)"BukkitEvent", (String)"get");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"BukkitEvent", (String)"class_name");
    }

    public boolean tm$0_set(String string, ScriptValue scriptValue) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string, (Object)scriptValue);
        }
        return PolyClassRuntime.genericCall((String)"BukkitEvent", (String)"set", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string), scriptValue}).asBool();
    }

    public ScriptValue um$1_set(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BukkitEvent", (String)"set", (Object)this.instance, (List)list);
    }

    public ScriptValue tm$2_get(String string) {
        if (h$2 != null) {
            return (ScriptValue)h$2.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"BukkitEvent", (String)"get", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)});
    }

    public ScriptValue um$3_get(List list) {
        if (m$3 != null) {
            return m$3.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BukkitEvent", (String)"get", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$4_class_name() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BukkitEvent", (String)"class_name", (Object)this.instance);
    }

    public PolyClassBukkitEvent(Object object) {
        super(object);
    }

    public static PolyClassBukkitEvent of(Object object) {
        return new PolyClassBukkitEvent(object);
    }

    public static PolyClassBukkitEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BukkitEvent")) {
            return new PolyClassBukkitEvent(object);
        }
        return null;
    }
}
