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

public class PolyClassBeehiveMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BeehiveMetadata", (String)"bee_count");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BeehiveMetadata", (String)"bee_count", (String)"D");
    }

    public ScriptValue pg$0_bee_count() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BeehiveMetadata", (String)"bee_count", (Object)this.instance);
    }

    public double tg$1_bee_count() {
        if (tp$1 != null) {
            return (Double)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BeehiveMetadata", (String)"bee_count", (Object)this.instance).asNum();
    }

    public PolyClassBeehiveMetadata(Object object) {
        super(object);
    }

    public static PolyClassBeehiveMetadata of(Object object) {
        return new PolyClassBeehiveMetadata(object);
    }

    public static PolyClassBeehiveMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BeehiveMetadata")) {
            return new PolyClassBeehiveMetadata(object);
        }
        return null;
    }
}
