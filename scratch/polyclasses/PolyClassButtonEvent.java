/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
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

public class PolyClassButtonEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ButtonEvent", (String)"click_type");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ButtonEvent", (String)"click_type", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ButtonEvent", (String)"slot");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ButtonEvent", (String)"slot", (String)"D");
    }

    public ScriptValue pg$0_click_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ButtonEvent", (String)"click_type", (Object)this.instance);
    }

    public String tg$1_click_type() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ButtonEvent", (String)"click_type", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_slot() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ButtonEvent", (String)"slot", (Object)this.instance);
    }

    public double tg$3_slot() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ButtonEvent", (String)"slot", (Object)this.instance).asNum();
    }

    public PolyClassButtonEvent(Object object) {
        super(object);
    }

    public static PolyClassButtonEvent of(Object object) {
        return new PolyClassButtonEvent(object);
    }

    public static PolyClassButtonEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ButtonEvent")) {
            return new PolyClassButtonEvent(object);
        }
        return null;
    }

    public static PolyClassButtonEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassButtonEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
