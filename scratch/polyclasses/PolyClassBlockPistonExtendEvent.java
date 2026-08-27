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

public class PolyClassBlockPistonExtendEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.TypedPropertyHandler tp$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonExtendEvent", (String)"length");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockPistonExtendEvent", (String)"length", (String)"D");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonExtendEvent", (String)"block");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BlockPistonExtendEvent", (String)"direction");
        tp$4 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BlockPistonExtendEvent", (String)"direction", (String)"S");
    }

    public ScriptValue pg$0_length() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"length", (Object)this.instance);
    }

    public double tg$1_length() {
        if (tp$1 != null) {
            return (Double)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"length", (Object)this.instance).asNum();
    }

    public ScriptValue pg$2_block() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"block", (Object)this.instance);
    }

    public ScriptValue pg$3_direction() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"direction", (Object)this.instance);
    }

    public String tg$4_direction() {
        if (tp$4 != null) {
            return (String)tp$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BlockPistonExtendEvent", (String)"direction", (Object)this.instance).asStr();
    }

    public PolyClassBlockPistonExtendEvent(Object object) {
        super(object);
    }

    public static PolyClassBlockPistonExtendEvent of(Object object) {
        return new PolyClassBlockPistonExtendEvent(object);
    }

    public static PolyClassBlockPistonExtendEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BlockPistonExtendEvent")) {
            return new PolyClassBlockPistonExtendEvent(object);
        }
        return null;
    }

    public static PolyClassBlockPistonExtendEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBlockPistonExtendEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
