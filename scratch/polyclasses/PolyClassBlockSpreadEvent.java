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
import dev.arubik.craftengine.script.PolyClassBlockFormEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassBlockSpreadEvent
extends PolyClassBlockFormEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockSpreadEvent", (String)"source");
    }

    public ScriptValue pg$0_source() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockSpreadEvent", (String)"source", (Object)this.instance);
    }

    public PolyClassBlockSpreadEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockSpreadEvent of(Object object) {
        return new PolyClassBlockSpreadEvent(object);
    }

    public static PolyClassBlockSpreadEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockSpreadEvent")) {
            return new PolyClassBlockSpreadEvent(object);
        }
        return null;
    }

    public static PolyClassBlockSpreadEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBlockSpreadEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
