/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassEvent;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassVehicleEntityCollisionEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleEntityCollisionEvent", (String)"entity");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleEntityCollisionEvent", (String)"vehicle");
    }

    public ScriptValue pg$0_entity() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleEntityCollisionEvent", (String)"entity", (Object)this.instance);
    }

    public ScriptValue pg$1_vehicle() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleEntityCollisionEvent", (String)"vehicle", (Object)this.instance);
    }

    public PolyClassVehicleEntityCollisionEvent(Object object) {
        super(object);
    }

    public static PolyClassVehicleEntityCollisionEvent of(Object object) {
        return new PolyClassVehicleEntityCollisionEvent(object);
    }
}
