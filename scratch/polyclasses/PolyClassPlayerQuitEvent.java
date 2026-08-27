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

public class PolyClassPlayerQuitEvent
extends PolyClassEvent {
    private static volatile PolyType.TypedMethodHandler1 h$0;
    private static volatile PolyType.MethodHandler m$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        h$0 = (PolyType.TypedMethodHandler1)PolyClassRuntime.resolveTypedHandler((String)"PlayerQuitEvent", (String)"set_quit_message", (String)"S:Z");
        m$1 = PolyClassRuntime.resolveMethodHandler((String)"PlayerQuitEvent", (String)"set_quit_message");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerQuitEvent", (String)"quit_message");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerQuitEvent", (String)"quit_message", (String)"S");
    }

    public boolean tm$0_set_quit_message(String string) {
        if (h$0 != null) {
            return (Boolean)h$0.call(this.instance, (Object)string);
        }
        return PolyClassRuntime.genericCall((String)"PlayerQuitEvent", (String)"set_quit_message", (Object)this.instance, (ScriptValue[])new ScriptValue[]{ScriptValue.of((String)string)}).asBool();
    }

    public ScriptValue um$1_set_quit_message(List list) {
        if (m$1 != null) {
            return m$1.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"PlayerQuitEvent", (String)"set_quit_message", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$2_quit_message() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerQuitEvent", (String)"quit_message", (Object)this.instance);
    }

    public String tg$3_quit_message() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerQuitEvent", (String)"quit_message", (Object)this.instance).asStr();
    }

    public PolyClassPlayerQuitEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerQuitEvent of(Object object) {
        return new PolyClassPlayerQuitEvent(object);
    }

    public static PolyClassPlayerQuitEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerQuitEvent")) {
            return new PolyClassPlayerQuitEvent(object);
        }
        return null;
    }
}
