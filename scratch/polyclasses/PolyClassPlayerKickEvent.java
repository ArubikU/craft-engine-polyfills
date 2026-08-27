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

public class PolyClassPlayerKickEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerKickEvent", (String)"set_reason", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerKickEvent", (String)"set_reason");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerKickEvent", (String)"reason");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerKickEvent", (String)"reason", (String)"S");
    }

    public boolean tm$0_set_reason(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"PlayerKickEvent", (String)"set_reason", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_set_reason(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerKickEvent", (String)"set_reason", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_reason() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerKickEvent", (String)"reason", (Object)this.instance);
    }

    public String tg$3_reason() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerKickEvent", (String)"reason", (Object)this.instance).asStr();
    }

    public PolyClassPlayerKickEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerKickEvent of(Object object) {
        return new PolyClassPlayerKickEvent(object);
    }

    public static PolyClassPlayerKickEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerKickEvent")) {
            return new PolyClassPlayerKickEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerKickEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerKickEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
