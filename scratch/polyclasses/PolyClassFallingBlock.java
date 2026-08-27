/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEntity_v2;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassFallingBlock
extends PolyClassEntity_v2 {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"FallingBlock", (String)"time");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"FallingBlock", (String)"time", (String)"D");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"FallingBlock", (String)"block_id");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"FallingBlock", (String)"block_id", (String)"S");
    }

    public ScriptValue pg$0_time() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FallingBlock", (String)"time", (Object)this.instance);
    }

    public double tg$1_time() {
        if (tp$1 != null) {
            return (Double)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FallingBlock", (String)"time", (Object)this.instance).asNum();
    }

    public ScriptValue pg$2_block_id() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FallingBlock", (String)"block_id", (Object)this.instance);
    }

    public String tg$3_block_id() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FallingBlock", (String)"block_id", (Object)this.instance).asStr();
    }

    public PolyClassFallingBlock(Object object) {
        super(object);
    }

    public static PolyClassFallingBlock of(Object object) {
        return new PolyClassFallingBlock(object);
    }

    public static PolyClassFallingBlock ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("FallingBlock")) {
            return new PolyClassFallingBlock(object);
        }
        return null;
    }
}
