/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEntity;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassFallingBlock
extends PolyClassEntity {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"FallingBlock", (String)"time");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"FallingBlock", (String)"block_id");
    }

    public ScriptValue pg$0_time() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FallingBlock", (String)"time", (Object)this.instance);
    }

    public ScriptValue pg$1_block_id() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FallingBlock", (String)"block_id", (Object)this.instance);
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
