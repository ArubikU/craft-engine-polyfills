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
import dev.arubik.craftengine.script.PolyClassBlockMetadata;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassSignMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SignMetadata", (String)"is_waxed");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"SignMetadata", (String)"back");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"SignMetadata", (String)"front");
    }

    public ScriptValue pg$0_is_waxed() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignMetadata", (String)"is_waxed", (Object)this.instance);
    }

    public ScriptValue pg$1_back() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignMetadata", (String)"back", (Object)this.instance);
    }

    public ScriptValue pg$2_front() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SignMetadata", (String)"front", (Object)this.instance);
    }

    public PolyClassSignMetadata(Object object) {
        super(object);
    }

    public static PolyClassSignMetadata of(Object object) {
        return new PolyClassSignMetadata(object);
    }

    public static PolyClassSignMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("SignMetadata")) {
            return new PolyClassSignMetadata(object);
        }
        return null;
    }
}
