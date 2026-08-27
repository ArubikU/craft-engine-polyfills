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

public class PolyClassWeatherChangeEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"WeatherChangeEvent", (String)"world");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"WeatherChangeEvent", (String)"to_weather_state");
    }

    public ScriptValue pg$0_world() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"WeatherChangeEvent", (String)"world", (Object)this.instance);
    }

    public ScriptValue pg$1_to_weather_state() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"WeatherChangeEvent", (String)"to_weather_state", (Object)this.instance);
    }

    public PolyClassWeatherChangeEvent(Object object) {
        super(object);
    }

    public static PolyClassWeatherChangeEvent of(Object object) {
        return new PolyClassWeatherChangeEvent(object);
    }
}
