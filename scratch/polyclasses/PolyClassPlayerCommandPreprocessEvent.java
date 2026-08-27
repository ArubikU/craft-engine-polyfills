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

public class PolyClassPlayerCommandPreprocessEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerCommandPreprocessEvent", (String)"set_message", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerCommandPreprocessEvent", (String)"set_message");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerCommandPreprocessEvent", (String)"message");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerCommandPreprocessEvent", (String)"message", (String)"S");
    }

    public boolean tm$0_set_message(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"PlayerCommandPreprocessEvent", (String)"set_message", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_set_message(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerCommandPreprocessEvent", (String)"set_message", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_message() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerCommandPreprocessEvent", (String)"message", (Object)this.instance);
    }

    public String tg$3_message() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerCommandPreprocessEvent", (String)"message", (Object)this.instance).asStr();
    }

    public PolyClassPlayerCommandPreprocessEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerCommandPreprocessEvent of(Object object) {
        return new PolyClassPlayerCommandPreprocessEvent(object);
    }

    public static PolyClassPlayerCommandPreprocessEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerCommandPreprocessEvent")) {
            return new PolyClassPlayerCommandPreprocessEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerCommandPreprocessEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerCommandPreprocessEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
