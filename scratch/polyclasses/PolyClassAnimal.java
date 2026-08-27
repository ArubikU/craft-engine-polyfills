/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.arubik.craftengine.script.PolyClassRuntime
 *  dev.arubik.craftengine.script.PolyType$PropertyHandler
 *  dev.arubik.craftengine.script.ScriptValue
 */
package dev.arubik.craftengine.script;

import dev.arubik.craftengine.script.PolyClassMob;
import dev.arubik.craftengine.script.PolyClassRuntime;
import dev.arubik.craftengine.script.PolyType;
import dev.arubik.craftengine.script.ScriptValue;

public class PolyClassAnimal
extends PolyClassMob {
    private static volatile PolyType.PropertyHandler p$0;
    private static volatile PolyType.PropertyHandler p$1;
    private static volatile PolyType.PropertyHandler p$2;
    private static volatile PolyType.PropertyHandler p$3;
    private static volatile PolyType.PropertyHandler p$4;

    public static void refresh() {
        p$0 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"is_in_love");
        p$1 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"in_love_time");
        p$2 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"age");
        p$3 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"is_animal");
        p$4 = PolyClassRuntime.resolvePropertyHandler((String)"Animal", (String)"is_baby");
    }

    public ScriptValue pg$0_is_in_love() {
        if (p$0 != null) {
            return p$0.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_in_love", (Object)this.instance);
    }

    public ScriptValue pg$1_in_love_time() {
        if (p$1 != null) {
            return p$1.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"in_love_time", (Object)this.instance);
    }

    public ScriptValue pg$2_age() {
        if (p$2 != null) {
            return p$2.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"age", (Object)this.instance);
    }

    public ScriptValue pg$3_is_animal() {
        if (p$3 != null) {
            return p$3.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_animal", (Object)this.instance);
    }

    public ScriptValue pg$4_is_baby() {
        if (p$4 != null) {
            return p$4.get(this.instance);
        }
        return PolyClassRuntime.genericProperty((String)"Animal", (String)"is_baby", (Object)this.instance);
    }

    public PolyClassAnimal(Object object) {
        super(object);
    }

    public static PolyClassAnimal of(Object object) {
        return new PolyClassAnimal(object);
    }
}
