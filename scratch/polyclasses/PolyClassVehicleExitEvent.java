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

public class PolyClassVehicleExitEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleExitEvent", (String)"exited");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleExitEvent", (String)"vehicle");
    }

    public ScriptValue pg$0_exited() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleExitEvent", (String)"exited", (Object)this.instance);
    }

    public ScriptValue pg$1_vehicle() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleExitEvent", (String)"vehicle", (Object)this.instance);
    }

    public PolyClassVehicleExitEvent(Object object) {
        super(object);
    }

    public static PolyClassVehicleExitEvent of(Object object) {
        return new PolyClassVehicleExitEvent(object);
    }

    public static PolyClassVehicleExitEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("VehicleExitEvent")) {
            return new PolyClassVehicleExitEvent(object);
        }
        return null;
    }
}
