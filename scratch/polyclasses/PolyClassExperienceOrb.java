/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClass
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.PolyType$TypedPropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 *  dev.arubik.craftengine.script.ScriptValue$Obj
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClass;
import dev.arubik.craftengine.script.PolyClassEntity;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassExperienceOrb
extends PolyClassEntity {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.TypedPropertyHandler tp$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.TypedPropertyHandler tp$3;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ExperienceOrb", (String)"xp_value");
        tp$1 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ExperienceOrb", (String)"xp_value", (String)"D");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"ExperienceOrb", (String)"value");
        tp$3 = (PolyType.TypedPropertyHandler)PolyClassRuntime.resolveTypedPropertyHandler((String)"ExperienceOrb", (String)"value", (String)"D");
    }

    public ScriptValue pg$0_xp_value() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ExperienceOrb", (String)"xp_value", (Object)this.instance);
    }

    public double tg$1_xp_value() {
        if (tp$1 != null) {
            return (Double)tp$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ExperienceOrb", (String)"xp_value", (Object)this.instance).asNum();
    }

    public ScriptValue pg$2_value() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ExperienceOrb", (String)"value", (Object)this.instance);
    }

    public double tg$3_value() {
        if (tp$3 != null) {
            return (Double)tp$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ExperienceOrb", (String)"value", (Object)this.instance).asNum();
    }

    public PolyClassExperienceOrb(Object object) {
        super(object);
    }

    public static PolyClassExperienceOrb of(Object object) {
        return new PolyClassExperienceOrb(object);
    }

    public static PolyClassExperienceOrb ofGuarded(ScriptValue scriptValue) {
        ScriptValue.Obj obj;
        Object object;
        if (scriptValue instanceof ScriptValue.Obj && (object = (obj = (ScriptValue.Obj)scriptValue).instance()) != null && !(object instanceof PolyClass) && obj.typeName().equals("ExperienceOrb")) {
            return new PolyClassExperienceOrb(object);
        }
        return null;
    }
}
