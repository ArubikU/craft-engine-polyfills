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

public class PolyClassVehicleCreateEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"VehicleCreateEvent", (String)"vehicle");
    }

    public ScriptValue pg$0_vehicle() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"VehicleCreateEvent", (String)"vehicle", (Object)this.instance);
    }

    public PolyClassVehicleCreateEvent(Object object) {
        super(object);
    }

    public static PolyClassVehicleCreateEvent of(Object object) {
        return new PolyClassVehicleCreateEvent(object);
    }
}
