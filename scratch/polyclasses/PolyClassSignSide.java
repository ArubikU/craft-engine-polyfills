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
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassSignSide {
    protected final Object instance;
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SignSide", (String)"color");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"SignSide", (String)"color", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"SignSide", (String)"glowing");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"SignSide", (String)"glowing", (String)"Z");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"SignSide", (String)"lines");
    }

    public ScriptValue pg$0_color() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"color", (Object)this.instance);
    }

    public String tg$1_color() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"color", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_glowing() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"glowing", (Object)this.instance);
    }

    public boolean tg$3_glowing() {
        if (tp$3 != null) {
            return (Boolean)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"glowing", (Object)this.instance).asBool();
    }

    public ScriptValue pg$4_lines() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignSide", (String)"lines", (Object)this.instance);
    }

    public PolyClassSignSide(Object object) {
        this.instance = object;
    }

    public static PolyClassSignSide of(Object object) {
        return new PolyClassSignSide(object);
    }

    public static PolyClassSignSide ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("SignSide")) {
            return new PolyClassSignSide(object);
        }
        return null;
    }

    public static PolyClassSignSide ofVar(ScriptContext scriptContext, String string) {
        return PolyClassSignSide.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
