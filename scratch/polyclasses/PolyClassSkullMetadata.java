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
import dev.arubik.craftengine.script.PolyClassBlockMetadata;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassSkullMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;
    private static volatile PolyType.PropertyHandler p$4;
    private static volatile PolyType.TypedPropertyHandler tp$5;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"SkullMetadata", (String)"has_owner");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"SkullMetadata", (String)"has_owner", (String)"Z");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"SkullMetadata", (String)"owner_name");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"SkullMetadata", (String)"owner_name", (String)"S");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"SkullMetadata", (String)"owner_uuid");
        tp$5 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"SkullMetadata", (String)"owner_uuid", (String)"S");
    }

    public ScriptValue pg$0_has_owner() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"has_owner", (Object)this.instance);
    }

    public boolean tg$1_has_owner() {
        if (tp$1 != null) {
            return (Boolean)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"has_owner", (Object)this.instance).asBool();
    }

    public ScriptValue pg$2_owner_name() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"owner_name", (Object)this.instance);
    }

    public String tg$3_owner_name() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"owner_name", (Object)this.instance).asStr();
    }

    public ScriptValue pg$4_owner_uuid() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"owner_uuid", (Object)this.instance);
    }

    public String tg$5_owner_uuid() {
        if (tp$5 != null) {
            return (String)tp$5.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"SkullMetadata", (String)"owner_uuid", (Object)this.instance).asStr();
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
