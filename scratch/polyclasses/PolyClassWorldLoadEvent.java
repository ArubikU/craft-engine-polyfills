/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
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

public class PolyClassWorldLoadEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"WorldLoadEvent", (String)"world");
    }

    public ScriptValue pg$0_world() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"WorldLoadEvent", (String)"world", (Object)this.instance);
    }

    public PolyClassWorldLoadEvent(Object object) {
        super(object);
    }

    public static PolyClassWorldLoadEvent of(Object object) {
        return new PolyClassWorldLoadEvent(object);
    }

    public static PolyClassWorldLoadEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("WorldLoadEvent")) {
            return new PolyClassWorldLoadEvent(object);
        }
        return null;
    }

    public static PolyClassWorldLoadEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassWorldLoadEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
