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

public class PolyClassPlayerAnimationEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerAnimationEvent", (String)"animation_type");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerAnimationEvent", (String)"animation_type", (String)"S");
    }

    public ScriptValue pg$0_animation_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerAnimationEvent", (String)"animation_type", (Object)this.instance);
    }

    public String tg$1_animation_type() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerAnimationEvent", (String)"animation_type", (Object)this.instance).asStr();
    }

    public PolyClassPlayerAnimationEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerAnimationEvent of(Object object) {
        return new PolyClassPlayerAnimationEvent(object);
    }

    public static PolyClassPlayerAnimationEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerAnimationEvent")) {
            return new PolyClassPlayerAnimationEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerAnimationEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerAnimationEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
