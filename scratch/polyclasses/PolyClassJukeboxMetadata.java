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

public class PolyClassJukeboxMetadata
extends PolyClassBlockMetadata {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"JukeboxMetadata", (String)"record");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"JukeboxMetadata", (String)"is_playing");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"JukeboxMetadata", (String)"is_playing", (String)"Z");
    }

    public ScriptValue pg$0_record() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"JukeboxMetadata", (String)"record", (Object)this.instance);
    }

    public ScriptValue pg$1_is_playing() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"JukeboxMetadata", (String)"is_playing", (Object)this.instance);
    }

    public boolean tg$2_is_playing() {
        if (tp$2 != null) {
            return (Boolean)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"JukeboxMetadata", (String)"is_playing", (Object)this.instance).asBool();
    }

    public PolyClassJukeboxMetadata(Object object) {
        super(object);
    }

    public static PolyClassJukeboxMetadata of(Object object) {
        return new PolyClassJukeboxMetadata(object);
    }

    public static PolyClassJukeboxMetadata ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("JukeboxMetadata")) {
            return new PolyClassJukeboxMetadata(object);
        }
        return null;
    }

    public static PolyClassJukeboxMetadata ofVar(ScriptContext scriptContext, String string) {
        return PolyClassJukeboxMetadata.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
