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

public class PolyClassVehicleMoveEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleMoveEvent", (String)"from");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleMoveEvent", (String)"to");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleMoveEvent", (String)"vehicle");
    }

    public ScriptValue pg$0_from() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleMoveEvent", (String)"from", (Object)this.instance);
    }

    public ScriptValue pg$1_to() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleMoveEvent", (String)"to", (Object)this.instance);
    }

    public ScriptValue pg$2_vehicle() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleMoveEvent", (String)"vehicle", (Object)this.instance);
    }

    public PolyClassVehicleMoveEvent(Object object) {
        super(object);
    }

    public static PolyClassVehicleMoveEvent of(Object object) {
        return new PolyClassVehicleMoveEvent(object);
    }

    public static PolyClassVehicleMoveEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("VehicleMoveEvent")) {
            return new PolyClassVehicleMoveEvent(object);
        }
        return null;
    }
}
