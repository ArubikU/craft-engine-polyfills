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

public class PolyClassFurnaceExtractEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceExtractEvent", (String)"item_type");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"FurnaceExtractEvent", (String)"item_type", (String)"S");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceExtractEvent", (String)"item_amount");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"FurnaceExtractEvent", (String)"item_amount", (String)"D");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceExtractEvent", (String)"player");
    }

    public ScriptValue pg$0_item_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"item_type", (Object)this.instance);
    }

    public String tg$1_item_type() {
        if (tp$1 != null) {
            return (String)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"item_type", (Object)this.instance).asStr();
    }

    public ScriptValue pg$2_item_amount() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"item_amount", (Object)this.instance);
    }

    public double tg$3_item_amount() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"item_amount", (Object)this.instance).asNum();
    }

    public ScriptValue pg$4_player() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"player", (Object)this.instance);
    }

    public PolyClassFurnaceExtractEvent(Object object) {
        super(object);
    }

    public static PolyClassFurnaceExtractEvent of(Object object) {
        return new PolyClassFurnaceExtractEvent(object);
    }

    public static PolyClassFurnaceExtractEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("FurnaceExtractEvent")) {
            return new PolyClassFurnaceExtractEvent(object);
        }
        return null;
    }

    public static PolyClassFurnaceExtractEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassFurnaceExtractEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
