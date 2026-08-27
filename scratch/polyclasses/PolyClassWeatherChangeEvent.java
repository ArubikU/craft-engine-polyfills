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

public class PolyClassWeatherChangeEvent
extends PolyClassEvent {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.TypedPropertyHandler tp$2;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"WeatherChangeEvent", (String)"world");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"WeatherChangeEvent", (String)"to_weather_state");
        tp$2 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"WeatherChangeEvent", (String)"to_weather_state", (String)"Z");
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

    public boolean tg$2_to_weather_state() {
        if (tp$2 != null) {
            return (Boolean)tp$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"WeatherChangeEvent", (String)"to_weather_state", (Object)this.instance).asBool();
    }

    public PolyClassWeatherChangeEvent(Object object) {
        super(object);
    }

    public static PolyClassWeatherChangeEvent of(Object object) {
        return new PolyClassWeatherChangeEvent(object);
    }

    public static PolyClassWeatherChangeEvent ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("WeatherChangeEvent")) {
            return new PolyClassWeatherChangeEvent(object);
        }
        return null;
    }

    public static PolyClassWeatherChangeEvent ofVar(ScriptContext scriptContext, String string) {
        return PolyClassWeatherChangeEvent.ofGuarded(scriptContext.getClassOrVar(string));
    }
}
