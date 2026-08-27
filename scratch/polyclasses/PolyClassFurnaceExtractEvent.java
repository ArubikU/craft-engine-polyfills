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
import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassFurnaceExtractEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceExtractEvent", (String)"item_type");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceExtractEvent", (String)"item_amount");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"FurnaceExtractEvent", (String)"player");
    }

    public ScriptValue pg$0_item_type() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"item_type", (Object)this.instance);
    }

    public ScriptValue pg$1_item_amount() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"FurnaceExtractEvent", (String)"item_amount", (Object)this.instance);
    }

    public ScriptValue pg$2_player() {
        if (p$2 != null) {
            return p$2.get(this.instance);
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
}
