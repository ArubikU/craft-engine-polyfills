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

public class PolyClassBrewEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"BrewEvent", (String)"ingredient");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"BrewEvent", (String)"fuel_level");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"BrewEvent", (String)"results_count");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"BrewEvent", (String)"contents_size");
    }

    public ScriptValue pg$0_ingredient() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BrewEvent", (String)"ingredient", (Object)this.instance);
    }

    public ScriptValue pg$1_fuel_level() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BrewEvent", (String)"fuel_level", (Object)this.instance);
    }

    public ScriptValue pg$2_results_count() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BrewEvent", (String)"results_count", (Object)this.instance);
    }

    public ScriptValue pg$3_contents_size() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"BrewEvent", (String)"contents_size", (Object)this.instance);
    }

    public PolyClassBrewEvent(Object object) {
        super(object);
    }

    public static PolyClassBrewEvent of(Object object) {
        return new PolyClassBrewEvent(object);
    }

    public static PolyClassBrewEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("BrewEvent")) {
            return new PolyClassBrewEvent(object);
        }
        return null;
    }
}
