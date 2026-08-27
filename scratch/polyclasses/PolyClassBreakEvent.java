/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$MethodHandler
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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

public class PolyClassBreakEvent
extends PolyClassEvent {
    private static volatile PolyType.MethodHandler m$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        m$0 = PolyClassRuntime.resolveMethodHandler((String)"BreakEvent", (String)"set_drops");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BreakEvent", (String)"drops");
    }

    public ScriptValue um$0_set_drops(List list) {
        if (m$0 != null) {
            return m$0.call(this.instance, list);
        }
        return PolyClassRuntime.genericCallList((String)"BreakEvent", (String)"set_drops", (Object)this.instance, (List)list);
    }

    public ScriptValue pg$1_drops() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BreakEvent", (String)"drops", (Object)this.instance);
    }

    public PolyClassBreakEvent(Object object) {
        super(object);
    }

    public static PolyClassBreakEvent of(Object object) {
        return new PolyClassBreakEvent(object);
    }

    public static PolyClassBreakEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BreakEvent")) {
            return new PolyClassBreakEvent(object);
        }
        return null;
    }

    public static PolyClassBreakEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBreakEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
