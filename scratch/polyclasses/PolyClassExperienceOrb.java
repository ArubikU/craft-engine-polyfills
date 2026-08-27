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
import dev.arubik.craftengine.script.PolyClassEntity;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassExperienceOrb
extends PolyClassEntity {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"ExperienceOrb", (String)"xp_value");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"ExperienceOrb", (String)"value");
    }

    public ScriptValue pg$0_xp_value() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ExperienceOrb", (String)"xp_value", (Object)this.instance);
    }

    public ScriptValue pg$1_value() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"ExperienceOrb", (String)"value", (Object)this.instance);
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
