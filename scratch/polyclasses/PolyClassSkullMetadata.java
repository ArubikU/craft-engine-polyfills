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

public class PolyClassSkullMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SkullMetadata", (String)"has_owner");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"SkullMetadata", (String)"owner_name");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"SkullMetadata", (String)"owner_uuid");
    }

    public ScriptValue pg$0_has_owner() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"has_owner", (Object)this.instance);
    }

    public ScriptValue pg$1_owner_name() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"owner_name", (Object)this.instance);
    }

    public ScriptValue pg$2_owner_uuid() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"owner_uuid", (Object)this.instance);
    }

    public PolyClassSkullMetadata(Object object) {
        super(object);
    }

    public static PolyClassSkullMetadata of(Object object) {
        return new PolyClassSkullMetadata(object);
    }

    public static PolyClassSkullMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("SkullMetadata")) {
            return new PolyClassSkullMetadata(object);
        }
        return null;
    }
}
