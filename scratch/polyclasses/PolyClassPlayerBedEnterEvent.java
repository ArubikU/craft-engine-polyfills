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

public class PolyClassPlayerBedEnterEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerBedEnterEvent", (String)"bed");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"PlayerBedEnterEvent", (String)"bed_enter_result");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"PlayerBedEnterEvent", (String)"bed_enter_result", (String)"S");
    }

    public ScriptValue pg$0_bed() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBedEnterEvent", (String)"bed", (Object)this.instance);
    }

    public ScriptValue pg$1_bed_enter_result() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBedEnterEvent", (String)"bed_enter_result", (Object)this.instance);
    }

    public String tg$2_bed_enter_result() {
        if (tp$2 != null) {
            return (String)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"PlayerBedEnterEvent", (String)"bed_enter_result", (Object)this.instance).asStr();
    }

    public PolyClassPlayerBedEnterEvent(Object object) {
        super(object);
    }

    public static PolyClassPlayerBedEnterEvent of(Object object) {
        return new PolyClassPlayerBedEnterEvent(object);
    }

    public static PolyClassPlayerBedEnterEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("PlayerBedEnterEvent")) {
            return new PolyClassPlayerBedEnterEvent(object);
        }
        return null;
    }

    public static PolyClassPlayerBedEnterEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassPlayerBedEnterEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
