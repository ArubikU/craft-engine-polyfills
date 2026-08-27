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
import dev.arubik.craftengine.script.PolyClassBlockMetadata;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptContext;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassBannerMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BannerMetadata", (String)"pattern_count");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BannerMetadata", (String)"pattern_count", (String)"D");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BannerMetadata", (String)"base_color");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"BannerMetadata", (String)"base_color", (String)"S");
    }

    public ScriptValue pg$0_pattern_count() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BannerMetadata", (String)"pattern_count", (Object)this.instance);
    }

    public double tg$1_pattern_count() {
        if (tp$1 != null) {
            return (Double)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BannerMetadata", (String)"pattern_count", (Object)this.instance).asNum();
    }

    public ScriptValue pg$2_base_color() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BannerMetadata", (String)"base_color", (Object)this.instance);
    }

    public String tg$3_base_color() {
        if (tp$3 != null) {
            return (String)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BannerMetadata", (String)"base_color", (Object)this.instance).asStr();
    }

    public PolyClassBannerMetadata(Object object) {
        super(object);
    }

    public static PolyClassBannerMetadata of(Object object) {
        return new PolyClassBannerMetadata(object);
    }

    public static PolyClassBannerMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BannerMetadata")) {
            return new PolyClassBannerMetadata(object);
        }
        return null;
    }

    public static PolyClassBannerMetadata ofVar(ScriptContext scriptContext, String string) {
        return PolyClassBannerMetadata.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
