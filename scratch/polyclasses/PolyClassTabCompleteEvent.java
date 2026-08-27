/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
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
import java.util.List;

public class PolyClassTabCompleteEvent
extends PolyClassEvent {
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"TabCompleteEvent", (String)"set_completions");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"TabCompleteEvent", (String)"completions");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"TabCompleteEvent", (String)"buffer");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"TabCompleteEvent", (String)"buffer", (String)"S");
    }

    public ScriptValue um$0_set_completions(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"TabCompleteEvent", (String)"set_completions", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$1_completions() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TabCompleteEvent", (String)"completions", (Object)this.instance);
    }

    public ScriptValue pg$2_buffer() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TabCompleteEvent", (String)"buffer", (Object)this.instance);
    }

    public String tg$3_buffer() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"TabCompleteEvent", (String)"buffer", (Object)this.instance).asStr();
    }

    public PolyClassTabCompleteEvent(Object object) {
        super(object);
    }

    public static PolyClassTabCompleteEvent of(Object object) {
        return new PolyClassTabCompleteEvent(object);
    }

    public static PolyClassTabCompleteEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("TabCompleteEvent")) {
            return new PolyClassTabCompleteEvent(object);
        }
        return null;
    }
}
