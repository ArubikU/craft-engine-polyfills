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

public class PolyClassPlayerLevelChangeEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerLevelChangeEvent", (String)"old_level");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerLevelChangeEvent", (String)"old_level", (String)"D");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerLevelChangeEvent", (String)"new_level");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerLevelChangeEvent", (String)"new_level", (String)"D");
    }

    public ScriptValue pg$0_old_level() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerLevelChangeEvent", (String)"old_level", (Object)this.instance);
    }

    public double tg$1_old_level() {
        if (tp$1 != null) {
            return (Double)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerLevelChangeEvent", (String)"old_level", (Object)this.instance).asNum();
    }

    public ScriptValue pg$2_new_level() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerLevelChangeEvent", (String)"new_level", (Object)this.instance);
    }

    public double tg$3_new_level() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerLevelChangeEvent", (String)"new_level", (Object)this.instance).asNum();
    }

    public PolyClassPlayerLevelChangeEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerLevelChangeEvent of(Object object) {
        return new PolyClassPlayerLevelChangeEvent(object);
    }

    public static PolyClassPlayerLevelChangeEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerLevelChangeEvent")) {
            return new PolyClassPlayerLevelChangeEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerLevelChangeEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerLevelChangeEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
